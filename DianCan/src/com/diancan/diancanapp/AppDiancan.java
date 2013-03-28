package com.diancan.diancanapp;

import java.util.HashMap;
import java.util.Iterator;
import cn.jpush.android.api.JPushInterface;
import com.baidu.mapapi.BMapManager;
import com.baidu.mapapi.MKEvent;
import com.baidu.mapapi.MKGeneralListener;
import com.diancan.Helper.OrderHelper;
import com.diancan.http.HttpCallback;
import com.diancan.http.HttpHandler;
import com.diancan.http.ImageFileCache;
import com.diancan.model.History;
import com.diancan.model.MyRestaurant;
import com.diancan.model.Order;
import com.diancan.model.OrderItem;
import com.diancan.model.city;
import com.weibo.sdk.android.Oauth2AccessToken;

import android.app.Application;
import android.os.Message;
import android.util.Log;
import android.util.SparseArray;
import android.widget.Toast;

public class AppDiancan extends Application {
	public String udidString;
	public Oauth2AccessToken accessToken;
	public MyRestaurant myRestaurant;
	public Order myOrder;
	public OrderHelper myOrderHelper;
	//百度地图相关
	public String BMapKey="79D53C4E2FE1E8F907D3087A68958DFDB8CE1E6C";
	public BMapManager mBMapMan;
	static AppDiancan mDemoApp;
	boolean m_bKeyRight=true;
	
	public city locationCity;
	public city selectedCity;
	
	
	@Override
	public void onCreate() {
		// TODO Auto-generated method stub
		super.onCreate();
//		StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder(); 
//	    builder.detectLeakedSqlLiteObjects(); 
//	    if (VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) { 
//	    builder.detectActivityLeaks().detectLeakedClosableObjects(); 
//	    } 
//	    // or you could simply call builder.detectAll() 
//	    // penalty 
//	    builder.penaltyLog(); // other penalties exist (e.g. penaltyDeath()) and can be 
//	    StrictMode.VmPolicy vmp = builder.build(); 
//	    StrictMode.setVmPolicy(vmp); 
		
//		StrictMode.setThreadPolicy(new StrictMode.ThreadPolicy.Builder()
//        .detectDiskReads()
//        .detectDiskWrites()
//        .detectNetwork()   // or .detectAll() for all detectable problems
//        .penaltyLog()
//        .build());
//		StrictMode.setVmPolicy(new StrictMode.VmPolicy.Builder()
//        .detectLeakedSqlLiteObjects()
//        .detectLeakedClosableObjects()
//        .detectActivityLeaks()
//        .penaltyLog()
//        .penaltyDeath()
//        .build());
		JPushInterface.setDebugMode(true); 	//设置开启日志,发布时请关闭日志
        JPushInterface.init(this); 
	}
	public static class MyGeneralListener implements MKGeneralListener {
		@Override
		public void onGetNetworkState(int iError) {
			Log.d("MyGeneralListener", "onGetNetworkState error is "+ iError);
			Toast.makeText(AppDiancan.mDemoApp.getApplicationContext(), "onGetNetworkState error is "+ iError,
					Toast.LENGTH_LONG).show();
		}

		@Override
		public void onGetPermissionState(int iError) {
			Log.d("MyGeneralListener", "onGetPermissionState error is "+ iError);
			if (iError ==  MKEvent.ERROR_PERMISSION_DENIED) {
				Toast.makeText(AppDiancan.mDemoApp.getApplicationContext(), 
						"onGetPermissionState error is "+ iError,
						Toast.LENGTH_LONG).show();
				AppDiancan.mDemoApp.m_bKeyRight = false;
			}
		}
	}
}
