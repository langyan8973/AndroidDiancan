package com.diancan.http;

import android.R.integer;
import android.os.Handler;
import android.os.Message;

public class HttpHandler extends Handler {
	public static final int REQUEST_ERROR=0;
	public static final int START_MAIN=1;
	public static final int REQUEST_ALLCATEGORY=2;
	public static final int REQUEST_ALLRECIPES=3;
	public static final int REQUEST_ORDER_BY_ID=4;
	public static final int POST_RECIPE_COUNT=5;
	public static final int REFRESH_ORDER=6;
	public static final int CHECK_ORDER=7;
	HttpCallback mCallback;
	public HttpHandler(HttpCallback callback){
		mCallback=callback;
	}
	@Override
	public void handleMessage(Message msg) {
		// TODO Auto-generated method stub
		if(msg.what==HttpHandler.REQUEST_ERROR){
			mCallback.RequestError(msg.obj.toString());
		}
		else{
			mCallback.RequestComplete(msg);
		}
	}

}
