package com.toraysoft.rainbow.controller;

import com.toraysoft.rainbow.common.RainbowFrame;
import com.toraysoft.rainbow.generator.RainbowGenerator;
import com.toraysoft.rainbow.listener.OnRainbowRequestListener;
import com.toraysoft.rainbow.Rainbow;

public class FrameController {

	public static RainbowFrame getRainbowSendNormal(Rainbow rainbow,
			byte[] msgType, byte[] data, OnRainbowRequestListener l) {
		return RainbowGenerator.getSendFrameNormal(rainbow, false, null,
				msgType, data, l);
	}

	public static RainbowFrame getRainbowSendLeastOnes(Rainbow rainbow,
			byte[] msgType, byte[] data, OnRainbowRequestListener l) {
		return RainbowGenerator.getSendFrameLeastOnes(rainbow, false, rainbow
				.getMessageIDGenerator().getMsgIdByte(), msgType, data, l);
	}

	public static RainbowFrame getRainbowSendLeastOnes(Rainbow rainbow,
			boolean isResend, byte[] msgId, byte[] msgType, byte[] data, OnRainbowRequestListener l) {
		return RainbowGenerator.getSendFrameLeastOnes(rainbow, isResend, msgId,
				msgType, data, l);
	}

	public static RainbowFrame getRainbowSendOnlyOnes(Rainbow rainbow,
			byte[] msgType, byte[] data, OnRainbowRequestListener l) {
		return RainbowGenerator.getSendFrameOnlyOnes(rainbow, false, rainbow
				.getMessageIDGenerator().getMsgIdByte(), msgType, data, l);
	}

	public static RainbowFrame getRainbowSendOnlyOnes(Rainbow rainbow,
			boolean isResend, byte[] msgId, byte[] msgType, byte[] data, OnRainbowRequestListener l) {
		return RainbowGenerator.getSendFrameOnlyOnes(rainbow, isResend, msgId,
				msgType, data, l);
	}

	public static RainbowFrame getRainbowAck(Rainbow rainbow, byte[] msgId,
			byte[] data) {
		return RainbowGenerator.getAckFrameNormal(rainbow, false, msgId, null,
				data);
	}

	public static RainbowFrame getRainbowRec(Rainbow rainbow, byte[] msgId,
			byte[] data) {
		return RainbowGenerator.getRecFrameNormal(rainbow, false, msgId, null,
				data);
	}

	public static RainbowFrame getRainbowRel(Rainbow rainbow, byte[] msgId) {
		return RainbowGenerator.getRelFrameNormal(rainbow, false, msgId, null,
				null);
	}

	public static RainbowFrame getRainbowCom(Rainbow rainbow, byte[] msgId) {
		return RainbowGenerator.getComFrameNormal(rainbow, false, msgId, null,
				null);
	}

}
