package com.toraysoft.rainbow.listener;

public interface WebSocketClientListener {
	public void onConnect();

	public void onMessage(String message);

	public void onMessage(byte[] data);

	public void onDisconnect(int code, String reason);

	public void onError(Exception error);
}
