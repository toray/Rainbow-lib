package com.toraysoft.rainbow.generator;

import com.toraysoft.rainbow.util.LogUtil;
import com.toraysoft.rainbow.Rainbow;

public class MessageIDGenerator {

	private static final String TAG = MessageIDGenerator.class.getSimpleName();
	private Rainbow mRainbow;
	private int msgId = 0;
	private int MAX_ID = 0xFFFF;

	public MessageIDGenerator(Rainbow rainbow) {
		this.mRainbow = rainbow;
	}

	private int getMsgId() {
		msgId++;
		if (msgId > MAX_ID) {
			msgId = 0;
		}
		LogUtil.d(TAG, "getMsgId : " + msgId);
		return msgId;
	}

	public synchronized byte[] getMsgIdByte() {
		return getBytes(getMsgId());
	}

	private byte[] getBytes(int data) {
		byte[] bytes = new byte[2];
		bytes[0] = (byte) ((data & 0xff00) >> 8);
		bytes[1] = (byte) (data & 0xff);
		return bytes;
	}

}
