package com.toraysoft.rainbow.generator;

import com.toraysoft.rainbow.util.ByteUtil;

public class ProtocolGenerator {

	public enum PROTOCOL_TYPE {
		PROTOCOL_SEND, PROTOCOL_ACK, PROTOCOL_REC, PROTOCOL_REL, PROTOCOL_COM
	}

	public enum QOS_TYPE {
		QOS_NORMAL, QOS_LEAST_ONES, QOS_ONLY_ONES,
	}

	private static final byte send = 0x10;
	private static final byte ack = 0x20;
	private static final byte rec = 0x30;
	private static final byte rel = 0x40;
	private static final byte com = 0x50;
	private static final byte resend = 0x8;

	private static final byte qosNormal = 0x0;
	private static final byte qosLeastOnes = 0x2;
	private static final byte qosOnlyOnes = 0x4;

	private static final byte response_send = 0x1;
	private static final byte response_ack = 0x2;
	private static final byte response_rec = 0x3;
	private static final byte response_rel = 0x4;
	private static final byte response_com = 0x5;

	private static final byte response_not_resend = 0x77;
	private static final byte response_is_resend = 0x7f;

	private static final byte response_qos_normal = 0x79;
	private static final byte response_qos_leastOnes = 0x7b;
	private static final byte response_qos_onlyOnes = 0x7d;

	public static final int HEADER_BYTE_OFFSET = 0;
	public static final int SECOND_BYTE_OFFSET = 1;
	public static final int THIRD_BYTE_OFFSET = 3;
	public static final int FOUR_BYTE_OFFSET = 5;

	public static final int COMMON_BYTE_LEGTH = 2;

	private static byte setDatagramHeaderByte(PROTOCOL_TYPE protocolType,
			boolean isResend, QOS_TYPE qosType) {
		byte protocolHeader = 0;
		switch (protocolType) {
		case PROTOCOL_SEND:
			protocolHeader = send;
			break;
		case PROTOCOL_ACK:
			protocolHeader = ack;
			break;
		case PROTOCOL_REC:
			protocolHeader = rec;
			break;
		case PROTOCOL_REL:
			protocolHeader = rel;
			break;
		case PROTOCOL_COM:
			protocolHeader = com;
			break;
		}
		if (isResend)
			protocolHeader = (byte) (protocolHeader | resend);
		switch (qosType) {
		case QOS_NORMAL:
			protocolHeader = (byte) (protocolHeader | qosNormal);
			break;
		case QOS_LEAST_ONES:
			protocolHeader = (byte) (protocolHeader | qosLeastOnes);
			break;
		case QOS_ONLY_ONES:
			protocolHeader = (byte) (protocolHeader | qosOnlyOnes);
			break;
		}
		return protocolHeader;
	}

	public static byte encodeHeaderByte(PROTOCOL_TYPE typeProtocol,
			boolean isResend, QOS_TYPE typeQOS) {
		return setDatagramHeaderByte(typeProtocol, isResend, typeQOS);
	}

	public static PROTOCOL_TYPE getProtocolType(byte b) {
		byte mByte = (byte) (b >> 4);
		if (mByte == response_send) {
			return PROTOCOL_TYPE.PROTOCOL_SEND;
		} else if (mByte == response_ack) {
			return PROTOCOL_TYPE.PROTOCOL_ACK;
		} else if (mByte == response_rec) {
			return PROTOCOL_TYPE.PROTOCOL_REC;
		} else if (mByte == response_rel) {
			return PROTOCOL_TYPE.PROTOCOL_REL;
		} else if (mByte == response_com) {
			return PROTOCOL_TYPE.PROTOCOL_COM;
		}
		return null;
	}

	public static boolean getIsResend(byte b) {
		byte mByte = (byte) (b | response_not_resend);
		if (mByte == response_is_resend) {
			return true;
		}
		return false;
	}

	public static QOS_TYPE getQOSType(byte b) {
		byte mByte = (byte) (b | response_qos_normal);
		if (mByte == response_qos_normal) {
			return QOS_TYPE.QOS_NORMAL;
		} else if (mByte == response_qos_leastOnes) {
			return QOS_TYPE.QOS_LEAST_ONES;
		} else if (mByte == response_qos_onlyOnes) {
			return QOS_TYPE.QOS_ONLY_ONES;
		}
		return null;
	}

	public static int getMessageId(byte[] bytes) {
		byte[] mBytes = new byte[4];
		mBytes[0] = bytes[1];
		mBytes[1] = bytes[0];
		mBytes[2] = 0;
		mBytes[3] = 0;
		int msgId = ByteUtil.getInt(mBytes);
		return msgId;
	}

}
