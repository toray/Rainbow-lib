package com.toraysoft.rainbow;

import com.toraysoft.rainbow.common.RainbowFactory;
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

	private RainbowFactory mRainbowFactory;
	private WSHelper mWSHelper;
	private HeartBeatService mHeartBeatService;
	private RainbowListener mRainbowListener;
	private MessageIDGenerator mMessageIDGenerator;
	private RainbowController mRainbowController;

	public Rainbow() {
		mRainbowFactory = new RainbowFactory(this);
		mWSHelper = new WSHelper(this);
		mHeartBeatService = new HeartBeatService(this);
		mMessageIDGenerator = new MessageIDGenerator(this);
		mRainbowController = new RainbowController(this);
	}

	public RainbowFactory getRainbowFactory() {
		return mRainbowFactory;
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

}
