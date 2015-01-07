package com.toraysoft.rainbow.common;

import java.util.Map;
import java.util.Map.Entry;

import com.toraysoft.rainbow.Rainbow;
import com.toraysoft.rainbow.util.ByteUtil;

public class RainbowFactory {

	private RainbowFactory instance;
	private Rainbow mRainbow;

	private String HOST;
	private boolean autoReconnect = true;
	private Map<String, String> headers;
	private boolean isHeartBeatActive = false;
	private int rainbowTimeout;
	public static final int WEBSOCKE_TTIMEOUT = 5000;

	public Map<String, byte[]> msgTypeMap;

	public RainbowFactory(Rainbow rainbow) {
		instance = this;
		this.mRainbow = mRainbow;
	}

	public boolean isAutoReconnect() {
		return autoReconnect;
	}

	public RainbowFactory setAutoReconnect(boolean value) {
		this.autoReconnect = value;
		return instance;
	}

	public String getHost() {
		return HOST;
	}

	public RainbowFactory setHost(String host) {
		HOST = host;
		return instance;
	}

	public Map<String, String> getHeaders() {
		return headers;
	}

	public RainbowFactory setHeaders(Map<String, String> headers) {
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

	public RainbowFactory setRainbowTimeout(int time) {
		this.rainbowTimeout = time;
		return instance;
	}

	public Map<String, byte[]> getMsgType() {
		return msgTypeMap;
	}

	public RainbowFactory setMsgType(Map<String, byte[]> msgtypemap) {
		this.msgTypeMap = msgtypemap;
		return instance;
	}

	public byte[] getMsgTypeByte(String msgType) {
		return msgTypeMap.get(msgType);
	}

	public String getMsgTypeKey(byte[] value) {
		if (msgTypeMap != null) {
			for (Entry<String, byte[]> e : msgTypeMap.entrySet()) {
				if (ByteUtil.getIntShort(e.getValue()) == ByteUtil.getIntShort(value)) {
					return e.getKey();
				}
			}
		}
		return null;
	}
}
