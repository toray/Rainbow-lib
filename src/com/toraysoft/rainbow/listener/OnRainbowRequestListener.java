package com.toraysoft.rainbow.listener;


public interface OnRainbowRequestListener {
	void onSuccess(String data);
	
	void onTimeout();
}
