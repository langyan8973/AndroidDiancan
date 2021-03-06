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
	public static final int REQUEST_RESTAURANTS=8;
	public static final int POST_RECIPE_COUNT_FROMBOOK=9;
	public static final int POST_RECIPE_COUNT_FROMORDER=10;
	public static final int DEPOSIT_ORDER = 11;
	public static final int REQUEST_ALLHISTORY = 12;
	public static final int REQUEST_CITYANDCODE = 13;
	public static final int GET_CITIES = 14;
	public static final int RESULT_NULL = 15;
	public static final int REQUEST_FAVORITES = 16;
	public static final int POST_FAVORITE = 17;
	public static final int DELETE_FAVORITE = 18;
	public static final int LOCATION_CITY = 19;
	HttpCallback mCallback;
	public HttpHandler(HttpCallback callback){
		mCallback=callback;
	}
	@Override
	public void handleMessage(Message msg) {
		// TODO Auto-generated method stub
		if(msg.what==HttpHandler.REQUEST_ERROR){
			if(msg.obj!=null)
			{
				mCallback.RequestError(msg.obj.toString());
			}
			else {
				mCallback.RequestError("");
			}
		}
		else{
			mCallback.RequestComplete(msg);
		}
	}

}
