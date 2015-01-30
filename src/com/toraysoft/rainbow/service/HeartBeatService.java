package com.toraysoft.rainbow.service;

import java.util.Timer;
import java.util.TimerTask;

import com.toraysoft.rainbow.util.LogUtil;
import com.toraysoft.rainbow.Rainbow;

public class HeartBeatService {

	private static final String TAG = HeartBeatService.class.getSimpleName();
	private Rainbow mRainbow;
	private int TIME = 20000;
	private Timer mTimer;
	private TimerTask mTimerTask;

	public HeartBeatService(Rainbow rainbow) {
		this.mRainbow = rainbow;
	}

	public void doInBackground() {
		LogUtil.d(TAG, "HeartBeatService started!!!!!");
		mRainbow.getRainbowMeta().setHeartBeatActive(true);
		mTimer = new Timer(true);
		if (mTimerTask != null) {
			mTimerTask.cancel();
			mTimerTask = null;
		}
		mTimerTask = new TimerTask() {

			@Override
			public void run() {
				sendHeartBeat();
			}
		};
		mTimer.schedule(mTimerTask, TIME, TIME);
	}

	public void stop() {
		LogUtil.d(TAG, "HeartBeatService stoped!!!!!");
		if (mTimerTask != null)
			mTimerTask.cancel();
		if (mTimer != null)
			mTimer.cancel();
		mTimer = null;
		mRainbow.getRainbowMeta().setHeartBeatActive(false);
	}

	private void sendHeartBeat() {
		if(mRainbow.getWsHelper().isConnected()) {
			LogUtil.d(TAG, "sendHeartBeat ping!!!");
			mRainbow.getWsHelper().ping();			
		}
	}

}
