package com.toraysoft.rainbow.controller;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import com.toraysoft.rainbow.Rainbow;
import com.toraysoft.rainbow.util.LogUtil;

public class RainbowController {

	private static final String TAG = RainbowController.class.getSimpleName();
	private Rainbow mRainbow;
	private Map<Integer, RequestController> localRequest;
	private Map<Integer, byte[]> serverRequest;

	public RainbowController(Rainbow rainbow) {
		this.mRainbow = rainbow;
		localRequest = new HashMap<Integer, RequestController>();
		serverRequest = new HashMap<Integer, byte[]>();
	}

	public boolean putRequestControllerLocal(int msgId, RequestController r) {
		if (localRequest != null) {
			localRequest.put(msgId, r);
			if (mRainbow.isDebug())
				LogUtil.d(TAG, "putRequestControllerLocal msgId:" + msgId);
			return true;
		}
		return false;
	}

	public void removeRequestControllerLocal(int msgId) {
		if (localRequest != null) {
			if (mRainbow.isDebug())
				LogUtil.d(TAG, "removeRequestControllerLocal msgId:" + msgId);
			localRequest.remove(msgId);
		}
	}

	public RequestController getRequestControllerLocal(int msgId) {
		if (localRequest != null) {
			return localRequest.get(msgId);
		}
		return null;
	}

	public boolean isRequestControllerLocalExist(int msgId) {
		if (localRequest != null) {
			return localRequest.containsKey(msgId);
		}
		return false;
	}

	public boolean putRequestServer(int msgId, byte[] data) {
		if (serverRequest != null) {
			serverRequest.put(msgId, data);
			if (mRainbow.isDebug())
				LogUtil.d(TAG, "putRequestServer msgId:" + msgId);
			return true;
		}
		return false;
	}

	public void removeRequestServer(int msgId) {
		if (serverRequest != null) {
			if (mRainbow.isDebug())
				LogUtil.d(TAG, "removeRequestServer msgId:" + msgId);
			serverRequest.remove(msgId);
		}
	}

	public byte[] getRequestServer(int msgId) {
		if (serverRequest != null) {
			return serverRequest.get(msgId);
		}
		return null;
	}

	public boolean isRequestServerExist(int msgId) {
		if (serverRequest != null) {
			return serverRequest.containsKey(msgId);
		}
		return false;
	}

	public void cleanAllRequestTimeout() {
		if (localRequest != null) {
			Iterator iter = localRequest.entrySet().iterator();
			while (iter.hasNext()) {
				Map.Entry entry = (Map.Entry) iter.next();
				RequestController request = (RequestController) entry
						.getValue();
				if (request != null) {
					request.finish();
					if (request.getOnRainbowRequestListener() != null) {
						request.getOnRainbowRequestListener().onTimeout();
					}
				}
			}
			localRequest.clear();
		}
	}
}
