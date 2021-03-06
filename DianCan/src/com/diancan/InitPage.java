package com.diancan;

import java.io.File;
import java.io.IOException;
import java.net.ResponseCache;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import cn.jpush.android.api.BasicPushNotificationBuilder;
import cn.jpush.android.api.JPushInterface;

import com.SqlLiteDB.MenuDataHelper;
import com.baidu.mapapi.BMapManager;
import com.baidu.mapapi.GeoPoint;
import com.baidu.mapapi.LocationListener;
import com.baidu.mapapi.MKAddrInfo;
import com.baidu.mapapi.MKBusLineResult;
import com.baidu.mapapi.MKDrivingRouteResult;
import com.baidu.mapapi.MKPoiResult;
import com.baidu.mapapi.MKSearch;
import com.baidu.mapapi.MKSearchListener;
import com.baidu.mapapi.MKSuggestionResult;
import com.baidu.mapapi.MKTransitRouteResult;
import com.baidu.mapapi.MKWalkingRouteResult;
import com.diancan.Helper.OrderHelper;
import com.diancan.Helper.RecipeListHttpHelper;
import com.diancan.Utils.BitmapUtil;
import com.diancan.Utils.DisplayUtil;
import com.diancan.Utils.FileUtils;
import com.diancan.Utils.JsonUtils;
import com.diancan.Utils.MenuUtils;
import com.diancan.diancanapp.AppDiancan;
import com.diancan.http.HttpCallback;
import com.diancan.http.HttpDownloader;
import com.diancan.http.HttpHandler;
import com.diancan.http.ImageFileCache;
import com.diancan.http.MyResponseCache2;
import com.diancan.model.Category;
import com.diancan.model.History;
import com.diancan.model.MyRestaurant;
import com.diancan.model.Order;
import com.diancan.model.OrderItem;
import com.diancan.model.Recipe;
import com.diancan.model.Restaurant;
import com.diancan.model.city;
import com.weibo.sdk.android.keep.AccessTokenKeeper;

import android.R.integer;
import android.R.string;
import android.app.Activity;
import android.app.Notification;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.BitmapDrawable;
import android.location.Location;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.SparseArray;
import android.view.View;
import android.view.Window;
import android.widget.Toast;

public class InitPage extends Activity implements HttpCallback {

	
	private HttpHandler httpHandler;
	private AppDiancan appDiancan;
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.initpage);
		httpHandler=new HttpHandler(this);
		
		//屏幕尺寸容器测试
  		DisplayMetrics dm;
  		dm = new DisplayMetrics();
  		this.getWindowManager().getDefaultDisplay().getMetrics(dm);
  		DisplayUtil.SCALE=dm.density;
  		DisplayUtil.PIXWIDTH = dm.widthPixels;
  		float notifyBarHeight = this.getResources().getDimension(R.dimen.notifybar_height);
  		DisplayUtil.DPWIDTH=DisplayUtil.px2dip(dm.widthPixels);
  		DisplayUtil.DPHEIGHT=DisplayUtil.px2dip(dm.heightPixels - notifyBarHeight);
  		DisplayUtil.PIXHEIGHT = (int)(dm.heightPixels- notifyBarHeight) ;
  		
  		//初始化应用全局变量   
  		appDiancan=(AppDiancan)getApplicationContext();   
       
		//初始化缓存路径
        FileUtils.cacheDir  = new File(Environment.getExternalStorageDirectory().getPath()+"/ChiHuoPro/MenuImg/");
        if (!FileUtils.cacheDir.exists()) {
			FileUtils.cacheDir.mkdirs();
		}
        FileUtils._cityFile=new File(Environment.getExternalStorageDirectory().getPath()+"/ChiHuoPro/city.txt");
        //创建数据库
        createDatabase();
        //启用缓存
        HttpDownloader.mImageFileCache = new ImageFileCache();
        HttpDownloader.enableHttpResponseCache();
        //初始化服务地址
        MenuUtils.initUrl="http://"+getResources().getString(R.string.url_service);
        MenuUtils.imageUrl="http://"+getResources().getString(R.string.image_service);
        // 设置通知样式
        BasicPushNotificationBuilder builder = new BasicPushNotificationBuilder(InitPage.this);
        builder.statusBarDrawable = R.drawable.notification_icon;
        builder.notificationFlags = Notification.FLAG_AUTO_CANCEL;  //设置为自动消失
        builder.notificationDefaults = Notification.DEFAULT_SOUND|Notification.DEFAULT_VIBRATE;  // 设置为铃声与震动都要
        JPushInterface.setPushNotificationBuilder(1, builder);
        //读取udid
        SharedPreferences deviceInfo = getSharedPreferences("StartInfo", 0);
        String deviceString = deviceInfo.getString("udid", "");
        if(TextUtils.isEmpty(deviceString))
        {
        	
        	deviceString =  JPushInterface.getUdid(getApplicationContext());
        	deviceInfo.edit().putString("udid",deviceString).commit();
        	RegisterUdid(deviceString);
        }
        appDiancan.udidString=deviceString;
        appDiancan.accessToken = AccessTokenKeeper.readAccessToken(this);
        
        String citynameString = deviceInfo.getString("cityname", "-1");
        if(!citynameString.equals("-1")){
        	city c = new city();
        	c.setName(citynameString);
        	c.setId(deviceInfo.getString("cityid", ""));
        	appDiancan.selectedCity = c;
        }
        else{
        	appDiancan.selectedCity=null;
        }
      	
      	int rid = deviceInfo.getInt("rid", -1);
        String rname = deviceInfo.getString("rname", "-1");
        String rimage = deviceInfo.getString("rimage", "-1");
        int oid = deviceInfo.getInt("oid", -1);
        int orid = deviceInfo.getInt("orid", -1);
        
        //如果有选中的饭店先要把饭店的菜类请求到
        if(rid!=-1){
        	MyRestaurant myRestaurant = new MyRestaurant();
        	myRestaurant.setId(rid);
        	if(!rname.equals("-1")){
        		myRestaurant.setName(rname);
        	}
        	if(!rimage.equals("-1")){
        		myRestaurant.setImage(rimage);
        	}
        	appDiancan.myRestaurant = myRestaurant;
        }
        
    	if(oid!=-1&&orid!=-1){
    		RequestOrderById(oid, orid, appDiancan.udidString);
    	}
        else{
        	Sleep();
        }
        
	}
	
	
	@Override
	public void RequestComplete(Message msg) {
		// TODO Auto-generated method stub
		switch (msg.what) {
		case HttpHandler.START_MAIN:
			ToMain();
			break;
		case HttpHandler.REQUEST_ORDER_BY_ID:
			Order order = (Order)msg.obj;
			InitOrder(order);
			break;
		case HttpHandler.REQUEST_ALLCATEGORY:
			SparseArray<Category> caArray = (SparseArray<Category>)msg.obj;
			InitCategoryDic(caArray);
			break;
		default:
			ToMain();
			break;
		}
	}

	@Override
	public void RequestError(String errString) {
		// TODO Auto-generated method stub
		ShowError(errString);
	}
	
	
	
	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		httpHandler = null;
		System.gc();
		View view = findViewById(R.id.rootlayout);
		BitmapUtil.destroyDrawable(view);
	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		
		super.onResume();
	}

	private void RegisterUdid(final String udid){
		new Thread(new Runnable() {
			
			@Override
			public void run() {
				// TODO Auto-generated method stub
				try {
					HttpDownloader.RegisterUdid(udid,MenuUtils.initUrl+ "device");
					
				} catch (Throwable e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
					httpHandler.obtainMessage(HttpHandler.REQUEST_ERROR,e.getMessage()).sendToTarget();
				}
			}
		}).start();
	}
	
	public void Sleep(){
		Thread th=new Thread(new Runnable() {
			
			@Override
			public void run() {
				// TODO Auto-generated method stub
				
				try {
					Thread.sleep(2000);
					httpHandler.obtainMessage(HttpHandler.START_MAIN).sendToTarget();
				} catch (Exception e) {
					// TODO Auto-generated catch block
					httpHandler.obtainMessage(HttpHandler.REQUEST_ERROR,e.getMessage()).sendToTarget();
				}
				
			}
		});
		th.start();
	}
	
	public void RequestOrderById(final int oid,final int rid,final String udidString){
		new Thread(new Runnable() {
			
			@Override
			public void run() {
				// TODO Auto-generated method stub
				
				try {
					String resultString = HttpDownloader.getString(MenuUtils.initUrl+ "restaurants/"+rid+"/orders/" +oid,
							udidString,appDiancan.accessToken.getAuthorization());
					if(resultString==null)
					{
						httpHandler.obtainMessage(HttpHandler.REQUEST_ERROR,getResources().getString(R.string.message_net_error)).sendToTarget();
						return;
					}
					else {
					Order order=JsonUtils.ParseJsonToOrder(resultString);
					httpHandler.obtainMessage(HttpHandler.REQUEST_ORDER_BY_ID,order).sendToTarget();
				}
				} catch (Exception e) {
					// TODO Auto-generated catch block
					httpHandler.obtainMessage(HttpHandler.REQUEST_ERROR,e.getMessage()).sendToTarget();
				}
				
			}
		}).start();
	}
	
	public void RequestAllTypes(final int rid){
		new Thread(new Runnable() {
			@Override
			public void run() {
				// TODO Auto-generated method stub
				
				try {
					List<Category> categories=MenuUtils.getAllCategory(rid,appDiancan.udidString,
							appDiancan.accessToken.getAuthorization());
					SparseArray<Category> caArray=new SparseArray<Category>();
					Iterator<Category> iterator;
					for(iterator=categories.iterator();iterator.hasNext();){
						Category category=iterator.next();
						caArray.put(category.getId(), category);
					}
					httpHandler.obtainMessage(HttpHandler.REQUEST_ALLCATEGORY,caArray).sendToTarget();
				} catch (Exception e) {
					// TODO Auto-generated catch block
					httpHandler.obtainMessage(HttpHandler.REQUEST_ERROR,e.getMessage()).sendToTarget();
				}
				
			}
		}).start();
	}
	
	
	public void createDatabase(){
		new Thread(new Runnable() {
			
			@Override
			public void run() {
				// TODO Auto-generated method stub
				FileUtils._dbPathFile=new File(Environment.getExternalStorageDirectory().getPath()+"/ChiHuoPro/Database");
			    FileUtils._dbFileFile=new File(Environment.getExternalStorageDirectory().getPath()+"/ChiHuoPro/Database/taochike.db");
			    if(!FileUtils._dbFileFile.exists())
				{
		        	FileUtils.Createdbfile();
		        	MenuDataHelper.OpenDatabase();
		        	MenuDataHelper.CreateMenusTable();
		        	MenuDataHelper.CloseDatabase();
				}
			}
		}).start();
	}
	
	public void InitOrder(Order order){
		appDiancan.myOrder = order;
		appDiancan.myOrderHelper = new OrderHelper(order,getString(R.string.strportion));
		RequestAllTypes(order.getRestaurant().getId());
	}
	
	public void InitCategoryDic(SparseArray<Category> sArray){
		appDiancan.myOrderHelper.setCategoryDic(sArray);
		ToMain();
	}
  	
  	/**
  	 * 跳转
  	 */
  	public void ToMain()
  	{
  		Intent intent=new Intent(this,Main.class);
        startActivity(intent);
        this.finish();
  	}
    /**
  	 * 显示错误信息
  	 * @param strMess
  	 */
  	public void ShowError(String strMess) {
		Toast toast = Toast.makeText(InitPage.this, strMess, Toast.LENGTH_SHORT); 
        toast.show();
	}
  	 
}
