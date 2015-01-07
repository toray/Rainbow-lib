package com.toraysoft.rainbow.ws;

import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.http.message.BasicNameValuePair;

import com.toraysoft.rainbow.controller.FrameController;
import com.toraysoft.rainbow.controller.RequestController;
import com.toraysoft.rainbow.generator.ProtocolGenerator;
import com.toraysoft.rainbow.generator.ProtocolGenerator.PROTOCOL_TYPE;
import com.toraysoft.rainbow.generator.ProtocolGenerator.QOS_TYPE;
import com.toraysoft.rainbow.listener.WebSocketClientListener;
import com.toraysoft.rainbow.util.LogUtil;
import com.toraysoft.rainbow.Rainbow;

public class WSHelper {

	private static final String TAG = WSHelper.class.getSimpleName();
	private Rainbow mRainbow;
	private WebSocketClient client;
	private boolean isConnected = false;
	private boolean autoReconnect = true;
	private List<BasicNameValuePair> headers;
	private static final String CHARSET = "UTF-8";

	public WSHelper(Rainbow rainbow) {
		this.mRainbow = rainbow;
	}

	public void wsClient() {
		if (headers != null) {
			headers.clear();
		}
		headers = getExtraHeaders(mRainbow.getRainbowFactory().getHeaders());
		client = new WebSocketClient(URI.create(mRainbow.getRainbowFactory()
				.getHost()), mListener, headers);
		autoReconnect = mRainbow.getRainbowFactory().isAutoReconnect();
		client.connect();
	}

	private List<BasicNameValuePair> getExtraHeaders(Map<String, String> map) {
		List<BasicNameValuePair> lists = new ArrayList<BasicNameValuePair>();
		Iterator<Entry<String, String>> iterator = map.entrySet().iterator();
		while (iterator.hasNext()) {
			Entry<String, String> entry = iterator.next();
			String key = entry.getKey();
			String value = entry.getValue();
			lists.add(new BasicNameValuePair(key, value));
		}
		return lists;
	}

	public void connect() {
		LogUtil.e(TAG, "client connect : " + client);
		if (client != null) {
			if (headers != null)
				headers.clear();
			headers = getExtraHeaders(mRainbow.getRainbowFactory().getHeaders());
			client.setExtraHeaders(headers);
			client.connect();
		}
	}

	public void disconnect() {
		if (client != null) {
			autoReconnect = false;
			mRainbow.getRainbowFactory().setAutoReconnect(false);
			if (mRainbow.getRainbowFactory().isHeartBeatActive()) {
				try {
					mRainbow.getHeartBeatService().stop();
				} catch (Throwable t) {
					LogUtil.e(
							TAG,
							"Disconnect heartbeat service error : "
									+ t.getMessage());
				}
			}
			client.disconnect();
			client = null;
		}
	}

	public void ping() {
		if (client != null) {
			client.ping();
		}
	}

	public boolean isConnected() {
		return isConnected;
	}

	public void send(String text) {
		if (client != null) {
			client.send(text);
		}
	}

	public void send(byte[] data) {
		if (client != null) {
			try {
				LogUtil.d(TAG, "------->>>send:" + new String(data, "UTF-8"));
			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();
			}
			client.send(data);
		}
	}

	WebSocketClientListener mListener = new WebSocketClientListener() {

		@Override
		public void onMessage(byte[] data) {
			wsParseRainbowFrame(data);
		}

		@Override
		public void onMessage(String message) {
			LogUtil.e(TAG, "WebSocketClient.Listener onMessage : " + message);
		}

		@Override
		public void onError(Exception error) {
			LogUtil.e(TAG,
					"WebSocketClient.Listener onError : " + error.getMessage());
			isConnected = false;
			if (mRainbow.getRainbowListener() != null) {
				mRainbow.getRainbowListener().onRainbowConnectionError(
						error.getMessage());
			}
			if (autoReconnect)
				connect();
		}

		@Override
		public void onDisconnect(int code, String reason) {
			LogUtil.e(TAG, "WebSocketClient.Listener onDisconnect : code="
					+ code + " -- reason=" + reason);
			isConnected = false;
			if (mRainbow.getRainbowListener() != null) {
				mRainbow.getRainbowListener().onRainbowDisconnect(code, reason);
			}
			if (autoReconnect)
				connect();
		}

		@Override
		public void onConnect() {
			LogUtil.e(TAG, "WebSocketClient.Listener onConnect");
			isConnected = true;
			if (mRainbow.getRainbowListener() != null) {
				mRainbow.getRainbowListener().onRainbowConnect();
			}
			if (!mRainbow.getRainbowFactory().isHeartBeatActive()) {
				try {
					mRainbow.getHeartBeatService().doInBackground();
					LogUtil.d(TAG, "Start send heart beat now!!!!!!!!!");
				} catch (Throwable t) {
					LogUtil.e(TAG,
							"Start heartbeat service error : " + t.getMessage());
				}
			}
		}
	};

	private void wsParseRainbowFrame(byte[] frame) {
		if (frame == null || frame.length == 0) {
			return;
		}
		byte headerByte = frame[0];
		PROTOCOL_TYPE typePROTOCOL = ProtocolGenerator
				.getProtocolType(headerByte);
		boolean isResend = ProtocolGenerator.getIsResend(headerByte);
		QOS_TYPE typeQOS = ProtocolGenerator.getQOSType(headerByte);
		switch (typePROTOCOL) {
		case PROTOCOL_SEND:
			wsParseRainbowSend(isResend, typeQOS, frame);
			break;
		case PROTOCOL_ACK:
			wsParseRainbowAck(frame);
			break;
		case PROTOCOL_REC:
			wsParseRainbowRec(frame);
			break;
		case PROTOCOL_REL:
			wsParseRainbowRel(frame);
			break;
		case PROTOCOL_COM:
			wsParseRainbowCom(frame);
			break;
		}
	}

	private void wsParseRainbowSend(boolean isResend, QOS_TYPE type,
			byte[] frame) {
		if (type == QOS_TYPE.QOS_NORMAL) {
			byte[] msgType = new byte[ProtocolGenerator.COMMON_BYTE_LEGTH];
			System.arraycopy(frame, ProtocolGenerator.SECOND_BYTE_OFFSET,
					msgType, 0, ProtocolGenerator.COMMON_BYTE_LEGTH);
			byte[] data = new byte[frame.length - msgType.length - 1];
			if (data.length > 0) {
				System.arraycopy(frame, ProtocolGenerator.THIRD_BYTE_OFFSET,
						data, 0, data.length);
			}
			wsParseRainbowSendNormal(msgType, data);
		} else {
			byte[] msgType = new byte[ProtocolGenerator.COMMON_BYTE_LEGTH];
			System.arraycopy(frame, ProtocolGenerator.SECOND_BYTE_OFFSET,
					msgType, 0, ProtocolGenerator.COMMON_BYTE_LEGTH);
			byte[] msgId = new byte[ProtocolGenerator.COMMON_BYTE_LEGTH];
			System.arraycopy(frame, ProtocolGenerator.THIRD_BYTE_OFFSET, msgId,
					0, ProtocolGenerator.COMMON_BYTE_LEGTH);
			byte[] data = new byte[frame.length - msgType.length - msgId.length
					- 1];
			if (data.length > 0) {
				System.arraycopy(frame, ProtocolGenerator.FOUR_BYTE_OFFSET,
						data, 0, data.length);
			}
			if (type == QOS_TYPE.QOS_LEAST_ONES) {
				wsParseRainbowSendLeastOnes(msgType, msgId, data);
			} else if (type == QOS_TYPE.QOS_ONLY_ONES) {
				wsParseRainbowSendOnlyOnes(msgType, msgId, data);
			}
		}
	}

	private void wsParseRainbowSendNormal(byte[] msgType, byte[] data) {
		try {
			String dataStr = new String(data, CHARSET);
			String msgTypeStr = mRainbow.getRainbowFactory().getMsgTypeKey(
					msgType);
			LogUtil.d(TAG, "wsParseRainbowSendNormal: msgType:" + msgTypeStr
					+ " --- data:" + dataStr);
			if (mRainbow.getRainbowListener() != null) {
				mRainbow.getRainbowListener().onRainbowMessage(msgTypeStr,
						dataStr);
			}
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
	}

	private void wsParseRainbowSendLeastOnes(byte[] msgType, byte[] msgId,
			byte[] data) {
		try {
			String dataStr = new String(data, CHARSET);
			String msgTypeStr = mRainbow.getRainbowFactory().getMsgTypeKey(
					msgType);
			LogUtil.d(TAG, "wsParseRainbowSendLeastOne: msgType:" + msgTypeStr
					+ " --- msgId:" + ProtocolGenerator.getMessageId(msgId)
					+ " --- data:" + dataStr);
			if (mRainbow.getRainbowListener() != null) {
				mRainbow.getRainbowListener().onRainbowMessage(msgTypeStr,
						dataStr);
			}
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		if (client != null)
			client.send(FrameController.getRainbowAck(mRainbow, msgId, null)
					.getFrames());
	}

	private void wsParseRainbowSendOnlyOnes(byte[] msgType, byte[] msgId,
			byte[] data) {
		try {
			String dataStr = new String(data, CHARSET);
			String msgTypeStr = mRainbow.getRainbowFactory().getMsgTypeKey(
					msgType);
			LogUtil.d(TAG, "wsParseRainbowSendOnlyOne: msgType:" + msgTypeStr
					+ " --- msgId:" + ProtocolGenerator.getMessageId(msgId)
					+ " --- data:" + dataStr);
			if (mRainbow.getRainbowListener() != null) {
				mRainbow.getRainbowListener().onRainbowMessage(msgTypeStr,
						dataStr);
			}
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		if (client != null)
			client.send(FrameController.getRainbowRec(mRainbow, msgId, null)
					.getFrames());
	}

	private void wsParseRainbowAck(byte[] frame) {
		byte[] msgId = new byte[ProtocolGenerator.COMMON_BYTE_LEGTH];
		System.arraycopy(frame, ProtocolGenerator.SECOND_BYTE_OFFSET, msgId, 0,
				ProtocolGenerator.COMMON_BYTE_LEGTH);
		byte[] data = new byte[frame.length - msgId.length - 1];
		if (data.length > 0) {
			System.arraycopy(frame, ProtocolGenerator.THIRD_BYTE_OFFSET, data,
					0, data.length);
		}
		try {
			if (LogUtil.DEBUG) {
				String log = "wsParseRainbowAck: msgId:"
						+ ProtocolGenerator.getMessageId(msgId) + " --- data:"
						+ new String(data, CHARSET);
				LogUtil.d(TAG, log);
			}
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		int msgIdInt = ProtocolGenerator.getMessageId(msgId);
		if (mRainbow.getRainbowController().isRequestControllerLocalExist(
				msgIdInt)) {
			RequestController request = mRainbow.getRainbowController()
					.getRequestControllerLocal(msgIdInt);
			request.finish();
			mRainbow.getRainbowController().removeRequestControllerLocal(
					msgIdInt);
			if (request.getOnRainbowRequestListener() != null) {
				try {
					String dataStr = new String(data, CHARSET);
					request.getOnRainbowRequestListener().onSuccess(dataStr);
				} catch (UnsupportedEncodingException e) {
					e.printStackTrace();
				}
			}
		}
	}

	private void wsParseRainbowRec(byte[] frame) {
		byte[] msgId = new byte[ProtocolGenerator.COMMON_BYTE_LEGTH];
		System.arraycopy(frame, ProtocolGenerator.SECOND_BYTE_OFFSET, msgId, 0,
				ProtocolGenerator.COMMON_BYTE_LEGTH);
		byte[] data = new byte[frame.length - msgId.length - 1];
		if (data.length > 0) {
			System.arraycopy(frame, ProtocolGenerator.THIRD_BYTE_OFFSET, data,
					0, data.length);
		}
		int msgIdInt = ProtocolGenerator.getMessageId(msgId);
		try {
			if (LogUtil.DEBUG) {
				String log = "wsParseRainbowRec: msgId:" + msgIdInt
						+ " --- data:" + new String(data, CHARSET);
				LogUtil.d(TAG, log);
			}
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		if (mRainbow.getRainbowController().isRequestControllerLocalExist(
				msgIdInt)) {
			RequestController request = mRainbow.getRainbowController()
					.getRequestControllerLocal(msgIdInt);
			request.setResponseData(data);
		}
		if (client != null)
			client.send(FrameController.getRainbowRel(mRainbow, msgId)
					.getFrames());
	}

	private void wsParseRainbowRel(byte[] frame) {
		byte[] msgId = new byte[ProtocolGenerator.COMMON_BYTE_LEGTH];
		System.arraycopy(frame, ProtocolGenerator.SECOND_BYTE_OFFSET, msgId, 0,
				ProtocolGenerator.COMMON_BYTE_LEGTH);
		if (LogUtil.DEBUG) {
			String log = "wsParseRainbowRel: msgId:"
					+ ProtocolGenerator.getMessageId(msgId);
			LogUtil.d(TAG, log);
		}
		if (client != null)
			client.send(FrameController.getRainbowCom(mRainbow, msgId)
					.getFrames());
	}

	private void wsParseRainbowCom(byte[] frame) {
		byte[] msgId = new byte[ProtocolGenerator.COMMON_BYTE_LEGTH];
		System.arraycopy(frame, ProtocolGenerator.SECOND_BYTE_OFFSET, msgId, 0,
				ProtocolGenerator.COMMON_BYTE_LEGTH);
		int msgIdInt = ProtocolGenerator.getMessageId(msgId);
		String log = "wsParseRainbowCom: msgId:" + msgIdInt;
		LogUtil.d(TAG, log);

		if (mRainbow.getRainbowController().isRequestControllerLocalExist(
				msgIdInt)) {
			RequestController request = mRainbow.getRainbowController()
					.getRequestControllerLocal(msgIdInt);
			request.finish();
			mRainbow.getRainbowController().removeRequestControllerLocal(
					msgIdInt);
			if (request.getOnRainbowRequestListener() != null) {
				try {
					String dataStr = new String(request.getResponseData(),
							CHARSET);
					request.getOnRainbowRequestListener().onSuccess(dataStr);
				} catch (UnsupportedEncodingException e) {
					e.printStackTrace();
				}
			}
		}
	}

}
