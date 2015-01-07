package com.toraysoft.rainbow.controller;

import com.toraysoft.rainbow.common.RainbowFrame;
import com.toraysoft.rainbow.generator.RainbowGenerator;
import com.toraysoft.rainbow.listener.OnRainbowRequestListener;
import com.toraysoft.rainbow.Rainbow;

public class FrameController {

	public static RainbowFrame getRainbowSendNormal(Rainbow rainbow,
			String msgTypeKey, byte[] data, OnRainbowRequestListener l) {
		return RainbowGenerator.getSendFrameNormal(rainbow, false, null,
				msgTypeKey, data, l);
	}

	public static RainbowFrame getRainbowSendLeastOne(Rainbow rainbow,
			String msgTypeKey, byte[] data, OnRainbowRequestListener l) {
		return RainbowGenerator.getSendFrameLeastOne(rainbow, false, rainbow
				.getMessageIDGenerator().getMsgIdByte(), msgTypeKey, data, l);
	}

	public static RainbowFrame getRainbowSendLeastOne(Rainbow rainbow,
			boolean isResend, byte[] msgId, String msgTypeKey, byte[] data, OnRainbowRequestListener l) {
		return RainbowGenerator.getSendFrameLeastOne(rainbow, isResend, msgId,
				msgTypeKey, data, l);
	}

	public static RainbowFrame getRainbowSendOnlyOne(Rainbow rainbow,
			String msgTypeKey, byte[] data, OnRainbowRequestListener l) {
		return RainbowGenerator.getSendFrameOnlyOne(rainbow, false, rainbow
				.getMessageIDGenerator().getMsgIdByte(), msgTypeKey, data, l);
	}

	public static RainbowFrame getRainbowSendOnlyOne(Rainbow rainbow,
			boolean isResend, byte[] msgId, String msgTypeKey, byte[] data, OnRainbowRequestListener l) {
		return RainbowGenerator.getSendFrameOnlyOne(rainbow, isResend, msgId,
				msgTypeKey, data, l);
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
