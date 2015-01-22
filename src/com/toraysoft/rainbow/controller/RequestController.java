package com.toraysoft.rainbow.controller;

import java.util.Timer;
import java.util.TimerTask;

import com.toraysoft.rainbow.Rainbow;
import com.toraysoft.rainbow.common.RainbowFrame;
import com.toraysoft.rainbow.common.RainbowMeta;
import com.toraysoft.rainbow.generator.ProtocolGenerator;
import com.toraysoft.rainbow.listener.OnRainbowRequestListener;
import com.toraysoft.rainbow.listener.OnRainbowRequestListener.RAINBOW_ERR;
import com.toraysoft.rainbow.util.ByteUtil;
import com.toraysoft.rainbow.util.LogUtil;

public class RequestController {

	private Rainbow mRainbow;
	private RainbowFrame mRainbowFrame;
	private Timer mTimer;
	private boolean isSending = false;

	public RequestController(Rainbow rainbow, RainbowFrame mRainbowFrame) {
		this.mRainbow = rainbow;
		this.mRainbowFrame = mRainbowFrame;
		isSending = true;
		rainbow.getRainbowController().putRequestControllerLocal(
				getRequestID(), this);
		if(send()) {
			mTimer = new Timer(true);
			mTimer.schedule(mTimerTask, RainbowMeta.WEBSOCKE_TTIMEOUT,
					RainbowMeta.WEBSOCKE_TTIMEOUT);	
		} else {
			isSending = false;			
		}
	}

	TimerTask mTimerTask = new TimerTask() {

		@Override
		public void run() {
			if (isSending) {
				if (System.currentTimeMillis() - mRainbowFrame.getRequestTime() < mRainbow
						.getRainbowMeta().getRainbowTimeout()) {
					resend();
				} else {
					finish();
					mRainbow.getRainbowController()
							.removeRequestControllerLocal(getRequestID());
					if (mRainbowFrame.getOnRainbowRequestListener() != null) {
						try {
							mRainbowFrame.getOnRainbowRequestListener().onTimeout();	
						} catch (Throwable e) {
							e.printStackTrace();
						}
					} else {
						LogUtil.d("=======OnRainbowRequestListener is null=======");
					}
				}
			}
		}
	};

	public boolean isSending() {
		return isSending;
	}

	public void setSending(boolean isSending) {
		this.isSending = isSending;
	}

	public void finish() {
		isSending = false;
		mTimer.cancel();
	}

	public int getRequestID() {
		return ProtocolGenerator.getMessageId(mRainbowFrame.getMsgId());
	}

	public byte[] getData() {
		return mRainbowFrame.getData();
	}

	public void setResponseData(byte[] data) {
		mRainbowFrame.setResponseData(data);
	}
	
	public byte[] getResponseData() {
		return mRainbowFrame.getResponseData();
	}
	
	public OnRainbowRequestListener getOnRainbowRequestListener() {
		return mRainbowFrame.getOnRainbowRequestListener();
	}

	private boolean send() {
		if(checkWebSocketAlive()) {
			mRainbow.getWsHelper().send(mRainbowFrame.getFrames());
			return true;
		}
		return false;
	}

	private void resend() {
		mRainbowFrame.setResend(true);
		mRainbow.getWsHelper().send(mRainbowFrame.getFrames());
	}
	
	public int getMsgType() {
		return ByteUtil.getIntShort(mRainbowFrame.getMsgType());
	}
	
	private boolean checkWebSocketAlive() {
		if(!mRainbow.getWsHelper().isConnected()){
			LogUtil.d("RequestController", "rainbow may not connect now!!!");
			mRainbow.getRainbowController()
					.removeRequestControllerLocal(getRequestID());
			if (mRainbowFrame.getOnRainbowRequestListener() != null) {
				try {
					mRainbowFrame.getOnRainbowRequestListener().onRainbowError(RAINBOW_ERR.NOT_CONNECT);
				} catch (Throwable e) {
					e.printStackTrace();
				}
			}
			return false;
		}
		return true;
	}

}
