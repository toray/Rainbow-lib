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

	public Rainbow() {
		mRainbowMeta = new RainbowMeta(Rainbow.this);
		mWSHelper = new WSHelper(Rainbow.this);
		mHeartBeatService = new HeartBeatService(Rainbow.this);
		mMessageIDGenerator = new MessageIDGenerator(Rainbow.this);
		mRainbowController = new RainbowController(Rainbow.this);
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

	public void connect() {
		if (mWSHelper != null) {
			mWSHelper.connect();
		}
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

	public static class Builder {

		private RainbowListener listener;
		private String host;
		private int timeout;
		private boolean autoReconnect;
		private Map<String, byte[]> msgtypemap;

		public Builder setAutoReconnect(boolean value) {
			this.autoReconnect = value;
			return this;
		}

		public boolean getAutoReconnect() {
			return autoReconnect;
		}

		public Builder setRainbowListener(RainbowListener l) {
			this.listener = l;
			return this;
		}

		public RainbowListener getRainbowListener() {
			return listener;
		}

		public Builder setRainbowHost(String host) {
			this.host = host;
			return this;
		}

		public String getRainbowHost() {
			return host;
		}

		public Builder setRainbowTimeout(int sec) {
			this.timeout = sec;
			return this;
		}

		public int getRainbowTimeout() {
			return timeout;
		}

		public Builder setRainbowMsgType(Map<String, byte[]> msgtypemap) {
			this.msgtypemap = msgtypemap;
			return this;
		}

		public Map<String, byte[]> getRainbowMsgType() {
			return msgtypemap;
		}

		public Rainbow create() {
			Rainbow rainbow = new Rainbow();
			rainbow.setRainbowListener(getRainbowListener());
			rainbow.getRainbowMeta().setAutoReconnect(getAutoReconnect())
					.setHost(getRainbowHost())
					.setRainbowTimeout(getRainbowTimeout())
					.setMsgType(getRainbowMsgType());
			return rainbow;
		}

	}

}
