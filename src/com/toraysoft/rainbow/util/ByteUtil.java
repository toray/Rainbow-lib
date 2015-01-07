package com.toraysoft.rainbow.util;

public class ByteUtil {
	/**
	 * Byte转Bit
	 */
	public static String byteToBit(byte b) {
		return "" + (byte) ((b >> 7) & 0x1) + (byte) ((b >> 6) & 0x1)
				+ (byte) ((b >> 5) & 0x1) + (byte) ((b >> 4) & 0x1)
				+ (byte) ((b >> 3) & 0x1) + (byte) ((b >> 2) & 0x1)
				+ (byte) ((b >> 1) & 0x1) + (byte) ((b >> 0) & 0x1);
	}

	/**
	 * Bit转Byte
	 */
	public static byte BitToByte(String byteStr) {
		int re, len;
		if (null == byteStr) {
			return 0;
		}
		len = byteStr.length();
		if (len != 4 && len != 8) {
			return 0;
		}
		if (len == 8) {// 8 bit处理
			if (byteStr.charAt(0) == '0') {// 正数
				re = Integer.parseInt(byteStr, 2);
			} else {// 负数
				re = Integer.parseInt(byteStr, 2) - 256;
			}
		} else {// 4 bit处理
			re = Integer.parseInt(byteStr, 2);
		}
		return (byte) re;
	}

	/**
	 * 将data字节型数据转换为0~255 (0xFF 即BYTE)
	 * 
	 * @param data
	 * @return
	 */
	public int getUnsignedByte(byte data) {
		return data & 0x0FF;
	}

	/**
	 * 将data字节型数据转换为0~65535 (0xFFFF 即WORD)
	 * 
	 * @param data
	 * @return
	 */
	public int getUnsignedByte(short data) {
		return data & 0x0FFFF;
	}

	/**
	 * 将int数据转换为0~4294967295(0xFFFFFFFF即DWORD)
	 * 
	 * @param data
	 * @return
	 */
	public long getUnsignedIntt(int data) {
		return data & 0x0FFFFFFFF;
	}

	public static int getInt(byte[] bytes) {
		return (0xff & bytes[0]) | (0xff00 & (bytes[1] << 8))
				| (0xff0000 & (bytes[2] << 16))
				| (0xff000000 & (bytes[3] << 24));
	}
	
	public static int getIntShort(byte[] bytes) {
		byte[] mBytes = new byte[4];
		mBytes[0] = bytes[1];
		mBytes[1] = bytes[0];
		mBytes[2] = 0;
		mBytes[3] = 0;
		int value = getInt(mBytes);
		return value;
	}

}
