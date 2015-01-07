package com.toraysoft.rainbow.listener;

public interface RainbowListener {
	void onRainbowMessage(String msgType, String responseData);

	void onRainbowDisconnect(int code, String reason);

	void onRainbowConnect();

	void onRainbowConnectionError(String message);
	
}
