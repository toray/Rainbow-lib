package com.toraysoft.rainbow;

import java.util.Map;

import com.toraysoft.rainbow.listener.RainbowListener;

public class RainbowBuilder {

	private RainbowListener listener;
	private String host;
	private int timeout;
	private boolean autoReconnect;
	private Map<String, byte[]> msgtypemap;

	public RainbowBuilder() {

	}

	public RainbowBuilder setAutoReconnect(boolean value) {
		this.autoReconnect = value;
		return this;
	}

	public boolean getAutoReconnect() {
		return autoReconnect;
	}

	public RainbowBuilder setRainbowListener(RainbowListener l) {
		this.listener = l;
		return this;
	}

	public RainbowListener getRainbowListener() {
		return listener;
	}

	public RainbowBuilder setRainbowHost(String host) {
		this.host = host;
		return this;
	}

	public String getRainbowHost() {
		return host;
	}

	public RainbowBuilder setRainbowTimeout(int sec) {
		this.timeout = sec;
		return this;
	}

	public int getRainbowTimeout() {
		return timeout;
	}

	public RainbowBuilder setRainbowMsgType(Map<String, byte[]> msgtypemap) {
		this.msgtypemap = msgtypemap;
		return this;
	}

	public Map<String, byte[]> getRainbowMsgType() {
		return msgtypemap;
	}

}