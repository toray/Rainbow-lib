package com.toraysoft.rainbow;

import java.util.Map;

import com.toraysoft.rainbow.common.RainbowMeta;
import com.toraysoft.rainbow.controller.FrameController;
import com.toraysoft.rainbow.controller.RainbowController;
import com.toraysoft.rainbow.controller.RequestController;
import com.toraysoft.rainbow.generator.MessageIDGenerator;
import com.toraysoft.rainbow.generator.ProtocolGenerator.QOS_TYPE;
import com.toraysoft.rainbow.listener.OnRainbowRequestListener;
import com.toraysoft.rainbow.listener.RainbowListener;
import com.toraysoft.rainbow.service.HeartBeatService;
import com.toraysoft.rainbow.ws.WSHelper;

public class Rainbow {

	private RainbowMeta mRainbowMeta;
	private WSHelper mWSHelper;
	private HeartBeatService mHeartBeatService;
	private RainbowListener mRainbowListener;
	private MessageIDGenerator mMessageIDGenerator;
	private RainbowController mRainbowController;
	private boolean DEBUG = false;

	public Rainbow(RainbowBuilder builder) {
		mRainbowMeta = new RainbowMeta(Rainbow.this);
		mWSHelper = new WSHelper(Rainbow.this);
		mHeartBeatService = new HeartBeatService(Rainbow.this);
		mMessageIDGenerator = new MessageIDGenerator(Rainbow.this);
		mRainbowController = new RainbowController(Rainbow.this);
		setRainbowListener(builder.getRainbowListener());
		getRainbowMeta().setAutoReconnect(builder.getAutoReconnect())
				.setHost(builder.getRainbowHost())
				.setRainbowTimeout(builder.getRainbowTimeout())
				.setMsgType(builder.getRainbowMsgType());
	}

	public RainbowMeta getRainbowMeta() {
		return mRainbowMeta;
	}

	public WSHelper getWsHelper() {
		return mWSHelper;
	}

	public HeartBeatService getHeartBeatService() {
		return mHeartBeatService;
	}

	public RainbowListener getRainbowListener() {
		return mRainbowListener;
	}

	public void setRainbowListener(RainbowListener l) {
		this.mRainbowListener = l;
	}

	public MessageIDGenerator getMessageIDGenerator() {
		return mMessageIDGenerator;
	}

	public void setRainbowHeaders(Map<String, String> headers) {
		mRainbowMeta.setHeaders(headers);
	}

	public void initClient() {
		getWsHelper().wsClient();
	}

	public void send(String msgType, byte[] data, QOS_TYPE type,
			OnRainbowRequestListener l) {
		if (type == QOS_TYPE.QOS_NORMAL) {
			sendRainbowNromal(msgType, data, l);
		} else if (type == QOS_TYPE.QOS_LEAST_ONES) {
			sendRainbowLeastOnes(msgType, data, l);
		} else if (type == QOS_TYPE.QOS_ONLY_ONES) {
			sendRainbowOnlyOnes(msgType, data, l);
		}
	}

	private void sendRainbowNromal(String msgType, byte[] data,
			OnRainbowRequestListener l) {
		if (mWSHelper != null) {
			mWSHelper.send(FrameController.getRainbowSendNormal(this, msgType,
					data, l).getFrames());
		}
	}

	private void sendRainbowLeastOnes(String msgType, byte[] data,
			OnRainbowRequestListener l) {
		new RequestController(this, FrameController.getRainbowSendLeastOne(
				this, msgType, data, l));
	}

	private void sendRainbowOnlyOnes(String msgType, byte[] data,
			OnRainbowRequestListener l) {
		new RequestController(this, FrameController.getRainbowSendOnlyOne(this,
				msgType, data, l));
	}

	public void disconnect() {
		if (mWSHelper != null) {
			mWSHelper.disconnect();
		}
	}

	public boolean getRainbowStatus() {
		if (mWSHelper != null) {
			return mWSHelper.isConnected();
		}
		return false;
	}

	public RainbowController getRainbowController() {
		return mRainbowController;
	}

	public boolean isDebug() {
		return DEBUG;
	}

	public void setDebug(boolean debug) {
		DEBUG = debug;
	}

}
