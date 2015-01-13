package com.toraysoft.rainbow.common;

import java.util.Map;

import com.toraysoft.rainbow.Rainbow;

public class RainbowMeta {

	private RainbowMeta instance;
	private Rainbow mRainbow;

	private String HOST;
	private boolean autoReconnect = true;
	private Map<String, String> headers;
	private boolean isHeartBeatActive = false;
	private int rainbowTimeout;
	public static int WEBSOCKE_TTIMEOUT = 5000;

	public Map<String, byte[]> msgTypeMap;

	public RainbowMeta(Rainbow rainbow) {
		instance = this;
		this.mRainbow = mRainbow;
	}

	public boolean isAutoReconnect() {
		return autoReconnect;
	}

	public RainbowMeta setAutoReconnect(boolean value) {
		this.autoReconnect = value;
		return instance;
	}

	public String getHost() {
		return HOST;
	}

	public RainbowMeta setHost(String host) {
		HOST = host;
		return instance;
	}

	public Map<String, String> getHeaders() {
		return headers;
	}

	public RainbowMeta setHeaders(Map<String, String> headers) {
		this.headers = headers;
		return instance;
	}

	public boolean isHeartBeatActive() {
		return isHeartBeatActive;
	}

	public void setHeartBeatActive(boolean isHeartBeatActive) {
		this.isHeartBeatActive = isHeartBeatActive;
	}

	public int getRainbowTimeout() {
		return rainbowTimeout;
	}

	public RainbowMeta setRainbowTimeout(int time) {
		this.rainbowTimeout = time;
		return instance;
	}

}
