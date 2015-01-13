package com.toraysoft.rainbow.listener;

public interface RainbowListener {
	
	String onRainbowMessage(int msgType, String responseData);

	void onRainbowDisconnect(int code, String reason);

	void onRainbowConnect();

	void onRainbowConnectionError(String message);
	
}
