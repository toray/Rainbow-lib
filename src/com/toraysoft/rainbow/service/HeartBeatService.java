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
		mTimerTask = new TimerTask() {

			@Override
			public void run() {
				sendHeartBeat();
			}
		};
	}

	public void doInBackground() {
		LogUtil.d(TAG, "HeartBeatService started!!!!!");
		mRainbow.getRainbowMeta().setHeartBeatActive(true);
		mTimer = new Timer(true);
		mTimer.schedule(mTimerTask, TIME, TIME);
	}

	public void stop() {
		LogUtil.d(TAG, "HeartBeatService stoped!!!!!");
		mTimer.cancel();
		mRainbow.getRainbowMeta().setHeartBeatActive(false);
	}

	private void sendHeartBeat() {
		LogUtil.d(TAG, "sendHeartBeat ping!!!");
		mRainbow.getWsHelper().ping();
	}

}
