package com.toraysoft.rainbow.common;

import com.toraysoft.rainbow.generator.ProtocolGenerator;
import com.toraysoft.rainbow.generator.ProtocolGenerator.PROTOCOL_TYPE;
import com.toraysoft.rainbow.generator.ProtocolGenerator.QOS_TYPE;
import com.toraysoft.rainbow.listener.OnRainbowRequestListener;
import com.toraysoft.rainbow.util.LogUtil;
import com.toraysoft.rainbow.Rainbow;

public class RainbowFrame {

	private static final String TAG = RainbowFrame.class.getSimpleName();
	private PROTOCOL_TYPE requestType; // 数据包类型
	private boolean isResend; // 是否重发
	private QOS_TYPE qos; // 数据包质量控制
	private byte[] msgId; // 数据包消息ID
	private byte[] msgType; // 数据包消息类型
	private byte[] data; // 数据包内容
	private byte[] responseData; // 返回数据
	private long requestTime; // 时间
	private byte[] frames; // 封装后报文
	private OnRainbowRequestListener mOnRainbowRequestListener;

	public RainbowFrame() {

	}

	public RainbowFrame(Rainbow rainbow, boolean isResend, byte[] data,
			PROTOCOL_TYPE typeProtocol, QOS_TYPE typeQOS, OnRainbowRequestListener l) {
		this.requestType = typeProtocol;
		this.isResend = isResend;
		this.qos = typeQOS;
		this.msgId = rainbow.getMessageIDGenerator().getMsgIdByte();
		this.msgType = new byte[2];
		this.data = data;
		this.requestTime = System.currentTimeMillis();
		this.frames = createRainbowFrameBytes();
		this.mOnRainbowRequestListener = l;
	}

	public RainbowFrame(Rainbow rainbow, boolean isResend, byte[] msgId,
			byte[] msgType, byte[] data, PROTOCOL_TYPE typeProtocol,
			QOS_TYPE typeQOS, OnRainbowRequestListener l) {
		this.requestType = typeProtocol;
		this.isResend = isResend;
		this.qos = typeQOS;
		this.msgId = msgId;
		this.msgType = msgType;
		this.data = data;
		this.requestTime = System.currentTimeMillis();
		this.frames = createRainbowFrameBytes();
		this.mOnRainbowRequestListener = l;
	}

	public byte[] getFrames() {
		return frames;
	}

	public void setFrames(byte[] frames) {
		this.frames = frames;
	}

	public long getRequestTime() {
		return requestTime;
	}

	public void setRequestTime(long requestTime) {
		this.requestTime = requestTime;
	}

	public PROTOCOL_TYPE getRequestType() {
		return requestType;
	}

	public void setRequestType(PROTOCOL_TYPE requestType) {
		this.requestType = requestType;
	}

	public boolean isResend() {
		return isResend;
	}

	public void setResend(boolean isResend) {
		this.isResend = isResend;
		byte headerByte = ProtocolGenerator.encodeHeaderByte(requestType,
				isResend, qos);
		frames[0] = headerByte;
	}

	public QOS_TYPE getQos() {
		return qos;
	}

	public void setQos(QOS_TYPE qos) {
		this.qos = qos;
	}

	public byte[] getMsgId() {
		return msgId;
	}

	public void setMsgId(byte[] msgId) {
		this.msgId = msgId;
	}

	public byte[] getMsgType() {
		return msgType;
	}

	public void setMsgType(byte[] msgType) {
		this.msgType = msgType;
	}

	public byte[] getData() {
		return data;
	}

	public void setData(byte[] data) {
		this.data = data;
	}
	
	public byte[] getResponseData() {
		return responseData;
	}

	public void setResponseData(byte[] responseData) {
		this.responseData = responseData;
	}
	
	public OnRainbowRequestListener getOnRainbowRequestListener() {
		return mOnRainbowRequestListener;
	}

	public void setOnRainbowRequestListener(OnRainbowRequestListener l) {
		this.mOnRainbowRequestListener = l;
	}

	private byte[] createRainbowFrameBytes() {
		byte headerByte = ProtocolGenerator.encodeHeaderByte(requestType,
				isResend, qos);
		byte[] bytes = new byte[1];
		bytes[0] = headerByte;
		if (msgType != null && msgType.length > 0) {
			LogUtil.d(TAG, "createRainbowFrameBytes hava msgType!!!");
			byte[] mBytes = new byte[bytes.length + msgType.length];
			System.arraycopy(bytes, 0, mBytes, 0, bytes.length);
			System.arraycopy(msgType, 0, mBytes, bytes.length, msgType.length);
			bytes = mBytes;
		}
		if (msgId != null && msgId.length > 0) {
			LogUtil.d(TAG, "createRainbowFrameBytes hava msgId!!!");
			byte[] mBytes = new byte[bytes.length + msgId.length];
			System.arraycopy(bytes, 0, mBytes, 0, bytes.length);
			System.arraycopy(msgId, 0, mBytes, bytes.length, msgId.length);
			bytes = mBytes;
		}
		if (data != null && data.length > 0) {
			LogUtil.d(TAG, "createRainbowFrameBytes hava data!!!");
			byte[] mBytes = new byte[bytes.length + data.length];
			System.arraycopy(bytes, 0, mBytes, 0, bytes.length);
			System.arraycopy(data, 0, mBytes, bytes.length, data.length);
			bytes = mBytes;
		}
		return bytes;
	}

}
