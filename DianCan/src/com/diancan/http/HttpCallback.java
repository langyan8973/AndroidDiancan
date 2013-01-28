package com.diancan.http;

import android.os.Message;

public interface HttpCallback {
	public void RequestComplete(Message msg);
	public void RequestError(String errString);
}
