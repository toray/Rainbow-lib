package com.toraysoft.rainbow.util;

public class LogUtil {

	static final String TAG = "Rainbow";
	public static final boolean DEBUG = true;

	public static void d(String msg) {
		if (DEBUG) {
			d(TAG, msg);
		}
	}

	public static void d(String tag, String msg) {
		if (DEBUG) {
			System.out.println(tag + " " + msg);
		}
	}

	public static void v(String msg) {
		if (DEBUG) {
			v(TAG, msg);
		}
	}

	public static void v(String tag, String msg) {
		if (DEBUG) {
			System.out.println(tag + "  " + msg);
		}
	}

	public static void e(String msg) {
		if (DEBUG) {
			e(TAG, msg);
		}
	}

	public static void e(String tag, String msg) {
		if (DEBUG) {
			System.out.println(tag + "  " + msg);
		}
	}

	public static void i(String msg) {
		if (DEBUG) {
			i(TAG, msg);
		}
	}

	public static void i(String tag, String msg) {
		if (DEBUG) {
			System.out.println(tag + "  " + msg);
		}
	}

	public static void w(String msg) {
		if (DEBUG) {
			w(TAG, msg);
		}
	}

	public static void w(String tag, String msg) {
		if (DEBUG) {
			System.out.println(tag + "  " + msg);
		}
	}

}
