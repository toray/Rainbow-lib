package com.toraysoft.rainbow.generator;

import com.toraysoft.rainbow.common.RainbowFrame;
import com.toraysoft.rainbow.generator.ProtocolGenerator.PROTOCOL_TYPE;
import com.toraysoft.rainbow.generator.ProtocolGenerator.QOS_TYPE;
import com.toraysoft.rainbow.listener.OnRainbowRequestListener;
import com.toraysoft.rainbow.Rainbow;

public class RainbowGenerator {

	public static RainbowFrame getSendFrameNormal(Rainbow rainbow,
			boolean isResend, byte[] data, OnRainbowRequestListener l) {
		return getRainbowFrame(rainbow, isResend, data,
				PROTOCOL_TYPE.PROTOCOL_SEND, QOS_TYPE.QOS_NORMAL, l);
	}

	public static RainbowFrame getSendFrameLeastOnes(Rainbow rainbow,
			boolean isResend, byte[] data, OnRainbowRequestListener l) {
		return getRainbowFrame(rainbow, isResend, data,
				PROTOCOL_TYPE.PROTOCOL_SEND, QOS_TYPE.QOS_LEAST_ONES, l);
	}

	public static RainbowFrame getSendFrameOnlyOnes(Rainbow rainbow,
			boolean isResend, byte[] data, OnRainbowRequestListener l) {
		return getRainbowFrame(rainbow, isResend, data,
				PROTOCOL_TYPE.PROTOCOL_SEND, QOS_TYPE.QOS_ONLY_ONES, l);
	}

	public static RainbowFrame getAckFrameNormal(Rainbow rainbow,
			boolean isResend, byte[] data) {
		return getRainbowFrame(rainbow, isResend, data,
				PROTOCOL_TYPE.PROTOCOL_ACK, QOS_TYPE.QOS_NORMAL, null);
	}

	public static RainbowFrame getRecFrameNormal(Rainbow rainbow,
			boolean isResend, byte[] data) {
		return getRainbowFrame(rainbow, isResend, data,
				PROTOCOL_TYPE.PROTOCOL_REC, QOS_TYPE.QOS_NORMAL, null);
	}

	public static RainbowFrame getRelFrameNormal(Rainbow rainbow,
			boolean isResend, byte[] data) {
		return getRainbowFrame(rainbow, isResend, data,
				PROTOCOL_TYPE.PROTOCOL_REL, QOS_TYPE.QOS_NORMAL, null);
	}

	public static RainbowFrame getComFrameNormal(Rainbow rainbow,
			boolean isResend, byte[] data) {
		return getRainbowFrame(rainbow, isResend, data,
				PROTOCOL_TYPE.PROTOCOL_COM, QOS_TYPE.QOS_NORMAL, null);
	}

	public static RainbowFrame getSendFrameNormal(Rainbow rainbow,
			boolean isResend, byte[] msgId, String msgTypeKey, byte[] data,
			OnRainbowRequestListener l) {
		return getRainbowFrame(rainbow, isResend, msgId, msgTypeKey, data,
				PROTOCOL_TYPE.PROTOCOL_SEND, QOS_TYPE.QOS_NORMAL, l);
	}

	public static RainbowFrame getSendFrameLeastOne(Rainbow rainbow,
			boolean isResend, byte[] msgId, String msgTypeKey, byte[] data,
			OnRainbowRequestListener l) {
		return getRainbowFrame(rainbow, isResend, msgId, msgTypeKey, data,
				PROTOCOL_TYPE.PROTOCOL_SEND, QOS_TYPE.QOS_LEAST_ONES, l);
	}

	public static RainbowFrame getSendFrameOnlyOne(Rainbow rainbow,
			boolean isResend, byte[] msgId, String msgTypeKey, byte[] data,
			OnRainbowRequestListener l) {
		return getRainbowFrame(rainbow, isResend, msgId, msgTypeKey, data,
				PROTOCOL_TYPE.PROTOCOL_SEND, QOS_TYPE.QOS_ONLY_ONES, l);
	}

	public static RainbowFrame getAckFrameNormal(Rainbow rainbow,
			boolean isResend, byte[] msgId, String msgTypeKey, byte[] data) {
		return getRainbowFrame(rainbow, isResend, msgId, msgTypeKey, data,
				PROTOCOL_TYPE.PROTOCOL_ACK, QOS_TYPE.QOS_NORMAL, null);
	}

	public static RainbowFrame getRecFrameNormal(Rainbow rainbow,
			boolean isResend, byte[] msgId, String msgTypeKey, byte[] data) {
		return getRainbowFrame(rainbow, isResend, msgId, msgTypeKey, data,
				PROTOCOL_TYPE.PROTOCOL_REC, QOS_TYPE.QOS_NORMAL, null);
	}

	public static RainbowFrame getRelFrameNormal(Rainbow rainbow,
			boolean isResend, byte[] msgId, String msgTypeKey, byte[] data) {
		return getRainbowFrame(rainbow, isResend, msgId, msgTypeKey, data,
				PROTOCOL_TYPE.PROTOCOL_REL, QOS_TYPE.QOS_NORMAL, null);
	}

	public static RainbowFrame getComFrameNormal(Rainbow rainbow,
			boolean isResend, byte[] msgId, String msgTypeKey, byte[] data) {
		return getRainbowFrame(rainbow, isResend, msgId, msgTypeKey, data,
				PROTOCOL_TYPE.PROTOCOL_COM, QOS_TYPE.QOS_NORMAL, null);
	}

	private static RainbowFrame getRainbowFrame(Rainbow rainbow,
			boolean isResend, byte[] data, PROTOCOL_TYPE typeProtocol,
			QOS_TYPE typeQOS, OnRainbowRequestListener l) {
		return new RainbowFrame(rainbow, isResend, data, typeProtocol, typeQOS,
				l);
	}

	private static RainbowFrame getRainbowFrame(Rainbow rainbow,
			boolean isResend, byte[] msgId, String msgTypeKey, byte[] data,
			PROTOCOL_TYPE typeProtocol, QOS_TYPE typeQOS,
			OnRainbowRequestListener l) {
		return new RainbowFrame(rainbow, isResend, msgId, msgTypeKey, data,
				typeProtocol, typeQOS, l);
	}

}
