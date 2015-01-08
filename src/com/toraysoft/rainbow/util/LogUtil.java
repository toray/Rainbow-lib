package com.toraysoft.rainbow.util;

public class LogUtil {

	static final String TAG = "Rainbow";

	public static void d(String msg) {
		d(TAG, msg);
	}

	public static void d(String tag, String msg) {
		System.out.println(tag + " " + msg);
	}

}
