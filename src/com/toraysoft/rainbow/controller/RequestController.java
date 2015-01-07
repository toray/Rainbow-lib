package com.toraysoft.rainbow.controller;

import java.util.Timer;
import java.util.TimerTask;

import com.toraysoft.rainbow.common.RainbowFactory;
import com.toraysoft.rainbow.common.RainbowFrame;
import com.toraysoft.rainbow.generator.ProtocolGenerator;
import com.toraysoft.rainbow.listener.OnRainbowRequestListener;
import com.toraysoft.rainbow.Rainbow;

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
		send();
		mTimer = new Timer(true);
		mTimer.schedule(mTimerTask, RainbowFactory.WEBSOCKE_TTIMEOUT,
				RainbowFactory.WEBSOCKE_TTIMEOUT);
	}

	TimerTask mTimerTask = new TimerTask() {

		@Override
		public void run() {
			if (isSending) {
				if (System.currentTimeMillis() - mRainbowFrame.getRequestTime() < mRainbow
						.getRainbowFactory().getRainbowTimeout()) {
					resend();
				} else {
					finish();
					mRainbow.getRainbowController()
							.removeRequestControllerLocal(getRequestID());
					if (mRainbowFrame.getOnRainbowRequestListener() != null) {
						 mRainbowFrame.getOnRainbowRequestListener().onTimeout();
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

	public String getMsgTypeKey() {
		return mRainbowFrame.getMsgTypeKey();
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

	private void send() {
		mRainbow.getWsHelper().send(mRainbowFrame.getFrames());
	}

	private void resend() {
		mRainbowFrame.setResend(true);
		mRainbow.getWsHelper().send(mRainbowFrame.getFrames());
	}

}
