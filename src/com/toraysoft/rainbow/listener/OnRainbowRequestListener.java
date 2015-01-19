package com.toraysoft.rainbow.listener;


public interface OnRainbowRequestListener {
	
	public enum RAINBOW_ERR {
		NOT_CONNECT,
	}
	
	void onSuccess(String data);
	
	void onTimeout();
	
	void onRainbowError(RAINBOW_ERR err);
}
