package com.diancan;

import java.io.File;
import java.io.IOException;
import java.util.Iterator;
import java.util.List;

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
import com.diancan.Utils.FileUtils;
import com.diancan.Utils.JsonUtils;
import com.diancan.Utils.MenuUtils;
import com.diancan.diancanapp.AppDiancan;
import com.diancan.http.HttpCallback;
import com.diancan.http.HttpHandler;
import com.diancan.model.city;
import com.weibo.sdk.android.Oauth2AccessToken;

import android.app.Activity;
import android.app.Dialog;
import android.app.LocalActivityManager;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.os.Environment;
import android.os.Message;
import android.text.method.ScrollingMovementMethod;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.OvershootInterpolator;
import android.view.LayoutInflater;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

public class MainFirstPage extends Activity implements OnClickListener,HttpCallback {

	Button restaurantsBtn;
	Button captrueBtn;
	Button historyBtn;
	Button userBtn;
	Button browseBtn;
	Button searchBtn;
	TextView cityTextView;
	AppDiancan appDiancan;
	LocationListener mLocationListener=null;
	/** 定义搜索服务类 */
    private MKSearch mMKSearch;
    HttpHandler mHandler;
    public static Oauth2AccessToken accessToken;
    public static final String TAG = "sinasdk";
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
				WindowManager.LayoutParams.FLAG_FULLSCREEN);
		setContentView(R.layout.mainfirst);
		appDiancan = (AppDiancan)getApplicationContext();
		mHandler = new HttpHandler(this);
		cityTextView = (TextView)findViewById(R.id.selectcity_btn);
		cityTextView.setOnClickListener(this);
		
		restaurantsBtn=(Button)findViewById(R.id.toRestaurants);
		restaurantsBtn.setOnClickListener(this);
		captrueBtn=(Button)findViewById(R.id.toCapture);
		captrueBtn.setOnClickListener(this);
		userBtn = (Button)findViewById(R.id.user_btn);
		userBtn.setOnClickListener(this);
		browseBtn = (Button)findViewById(R.id.toBrowse);
		browseBtn.setOnClickListener(this);
		searchBtn = (Button)findViewById(R.id.search_btn);
		searchBtn.setOnClickListener(this);
		//历史订单
		historyBtn = (Button)findViewById(R.id.toHistory);
		historyBtn.setOnClickListener(this);
		

        if(appDiancan.mBMapMan==null)
		{
			appDiancan.mBMapMan=new BMapManager(getApplicationContext());
			appDiancan.mBMapMan.init(appDiancan.BMapKey, new AppDiancan.MyGeneralListener());
		}
  		/** 初始化MKSearch */
        mMKSearch = new MKSearch();
        mMKSearch.init(appDiancan.mBMapMan, new MySearchListener());
        
        if(appDiancan.locationCity==null){
        	//定位监听器
            mLocationListener=new LocationListener() {
    			
    			@Override
    			public void onLocationChanged(Location location) {
    				// TODO Auto-generated method stub
    				if (location != null){
    					GeoPoint pt = new GeoPoint((int)(location.getLatitude()*1e6),
    							(int)(location.getLongitude()*1e6));
    					mMKSearch.reverseGeocode(pt);
    					
    				}
    			}
    		};
    		
    		appDiancan.mBMapMan.getLocationManager().requestLocationUpdates(mLocationListener);
    		appDiancan.mBMapMan.start();
        }
	}

	
	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		if(appDiancan.selectedCity!=null){
			cityTextView.setText(appDiancan.selectedCity.getName());
		}
	}
	

	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
		if(appDiancan.mBMapMan!=null){
			appDiancan.mBMapMan.getLocationManager().removeUpdates(mLocationListener);
			appDiancan.mBMapMan.stop();
		}
	}


	@Override
	public void RequestComplete(Message msg) {
		// TODO Auto-generated method stub
		if(msg.what == HttpHandler.LOCATION_CITY){
			city c = (city)msg.obj;
			setCityName(c);
		}
	}


	@Override
	public void RequestError(String errString) {
		// TODO Auto-generated method stub
		ShowError(errString);
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		if(v.getId()==R.id.toRestaurants){
			if(appDiancan.selectedCity == null){
				ShowError("请选择城市");
				return;
			}
			ToRestaurantsPage();
		}
		else if(v.getId()==R.id.toCapture){
			ToCapturePage();
		}
		else if(v.getId()==R.id.toHistory){
			ToHistoriesListPage();
		}else if(v.getId()==R.id.user_btn){
			Intent intent = new Intent(this,UserInfoActivity.class);
			startActivity(intent);
		}
		else if(v.getId()==R.id.toBrowse){
			ToHisBrowse();
		}
		else if(v.getId()==R.id.search_btn){
			if(appDiancan.selectedCity == null){
				ShowError("请选择城市");
				return;
			}
			ToSearchPage();
		}
		else if(v.getId()==R.id.selectcity_btn){
			Intent intent = new Intent(this,CityPage.class);
			startActivity(intent);
		}
		
	}
	
	
	/**
  	 * 显示错误信息
  	 * @param strMess
  	 */
  	public void ShowError(String strMess) {
		Toast toast = Toast.makeText(MainFirstPage.this, strMess, Toast.LENGTH_SHORT); 
        toast.show();
	}
	
	private void setCityName(final city c){
		if(appDiancan.selectedCity==null){
			appDiancan.selectedCity = c;
			cityTextView.setText(appDiancan.selectedCity.getName());
		}
		else{
			if(!appDiancan.selectedCity.getId().equals(c.getId())){
				final Dialog dialog = new Dialog(getParent(), R.style.MyDialog);
		        //设置它的ContentView
				LayoutInflater inflater = (LayoutInflater) this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		        View layout = inflater.inflate(R.layout.dialog, null);
		        dialog.setContentView(layout);
		        String contentString = "定位的城市是"+c.getName()+"是否切换？";       
		        TextView contentView = (TextView)layout.findViewById(R.id.contentTxt);
		        TextView titleView = (TextView)layout.findViewById(R.id.dialog_title);
		        Button okBtn = (Button)layout.findViewById(R.id.dialog_button_ok);
		        okBtn.setOnClickListener(new OnClickListener() {
					
					@Override
					public void onClick(View v) {
						// TODO Auto-generated method stub
						dialog.dismiss();
						appDiancan.selectedCity = c;
						cityTextView.setText(appDiancan.selectedCity.getName());
					}
				});
		        Button cancelButton = (Button)layout.findViewById(R.id.dialog_button_cancel);
		        cancelButton.setOnClickListener(new OnClickListener() {
					
					@Override
					public void onClick(View v) {
						// TODO Auto-generated method stub
						dialog.dismiss();
					}
				});
		        titleView.setText("定位提示");
		        contentView.setText(contentString);
		        contentView.setMovementMethod(ScrollingMovementMethod.getInstance());
		        dialog.show();
		        Animation animation = AnimationUtils.loadAnimation(MainFirstPage.this, R.anim.activity_in);
		        animation.setInterpolator(new OvershootInterpolator());
		        layout.startAnimation(animation);
			}
		}
	}
	
	private void ToRestaurantsPage(){
		MenuGroup parent = (MenuGroup)this.getParent();
		LocalActivityManager manager = parent.getLocalActivityManager();
		Activity activity=manager.getCurrentActivity();
		Window w1=activity.getWindow();
		View v1=w1.getDecorView();
		Animation sAnimation=AnimationUtils.loadAnimation(this, R.anim.push_left_out);
		v1.startAnimation(sAnimation);
	    final LinearLayout contain = (LinearLayout) parent.findViewById(R.id.group_Layout);
		contain.removeAllViews();
		
		Animation animation = AnimationUtils.loadAnimation(this, R.anim.push_left_in);
		Intent in = new Intent(this.getParent(), RestaurantActivity.class);
		in.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
		Window window = manager.startActivity(MenuGroup.ID_RESTAURANTACTIVITY, in);
		View view=window.getDecorView();		
		contain.addView(view);
		LayoutParams params=(LayoutParams) view.getLayoutParams();
        params.width=LayoutParams.FILL_PARENT;
        params.height=LayoutParams.FILL_PARENT;
        view.setLayoutParams(params);
        view.startAnimation(animation);
	}

	private void ToCapturePage(){
		MenuGroup parent = (MenuGroup)this.getParent();
		LocalActivityManager manager = parent.getLocalActivityManager();
		Activity activity=manager.getCurrentActivity();
		Window w1=activity.getWindow();
		View v1=w1.getDecorView();
		Animation sAnimation=AnimationUtils.loadAnimation(this, R.anim.push_left_out);
		v1.startAnimation(sAnimation);
	    final LinearLayout contain = (LinearLayout) parent.findViewById(R.id.group_Layout);
		contain.removeAllViews();
		
		Animation animation = AnimationUtils.loadAnimation(this, R.anim.push_left_in);
		Intent in = new Intent(this.getParent(), CaptureActivity.class);
		in.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
		Window window = manager.startActivity(MenuGroup.ID_CAPTUREACTIVITY, in);
		View view=window.getDecorView();		
		contain.addView(view);
		LayoutParams params=(LayoutParams) view.getLayoutParams();
        params.width=LayoutParams.FILL_PARENT;
        params.height=LayoutParams.FILL_PARENT;
        view.setLayoutParams(params);
        view.startAnimation(animation);
	}
	
	private void ToHistoriesListPage(){
		MenuGroup parent = (MenuGroup)this.getParent();
		LocalActivityManager manager = parent.getLocalActivityManager();
		Activity activity=manager.getCurrentActivity();
		Window w1=activity.getWindow();
		View v1=w1.getDecorView();
		Animation sAnimation=AnimationUtils.loadAnimation(this, R.anim.push_left_out);
		v1.startAnimation(sAnimation);
	    final LinearLayout contain = (LinearLayout) parent.findViewById(R.id.group_Layout);
		contain.removeAllViews();
		
		Animation animation = AnimationUtils.loadAnimation(this, R.anim.push_left_in);
		Intent in = new Intent(this.getParent(), HistoryList.class);
		in.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
		Window window = manager.startActivity(MenuGroup.ID_HISTORYLIST, in);
		View view=window.getDecorView();		
		contain.addView(view);
		LayoutParams params=(LayoutParams) view.getLayoutParams();
        params.width=LayoutParams.FILL_PARENT;
        params.height=LayoutParams.FILL_PARENT;
        view.setLayoutParams(params);
        view.startAnimation(animation);
	}
	
	private void ToHisBrowse(){
		MenuGroup parent = (MenuGroup)this.getParent();
		LocalActivityManager manager = parent.getLocalActivityManager();
		Activity activity=manager.getCurrentActivity();
		Window w1=activity.getWindow();
		View v1=w1.getDecorView();
		Animation sAnimation=AnimationUtils.loadAnimation(this, R.anim.push_left_out);
		v1.startAnimation(sAnimation);
	    final LinearLayout contain = (LinearLayout) parent.findViewById(R.id.group_Layout);
		contain.removeAllViews();
		
		Animation animation = AnimationUtils.loadAnimation(this, R.anim.push_left_in);
		Intent in = new Intent(this.getParent(), HisBrowse.class);
		in.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
		Window window = manager.startActivity(MenuGroup.ID_HISBROWSE, in);
		View view=window.getDecorView();		
		contain.addView(view);
		LayoutParams params=(LayoutParams) view.getLayoutParams();
        params.width=LayoutParams.FILL_PARENT;
        params.height=LayoutParams.FILL_PARENT;
        view.setLayoutParams(params);
        view.startAnimation(animation);
	}
	
	private void ToSearchPage(){
		MenuGroup parent = (MenuGroup)this.getParent();
		LocalActivityManager manager = parent.getLocalActivityManager();
		Activity activity=manager.getCurrentActivity();
		Window w1=activity.getWindow();
		View v1=w1.getDecorView();
		Animation sAnimation=AnimationUtils.loadAnimation(this, R.anim.push_left_out);
		v1.startAnimation(sAnimation);
	    final LinearLayout contain = (LinearLayout) parent.findViewById(R.id.group_Layout);
		contain.removeAllViews();
		
		Animation animation = AnimationUtils.loadAnimation(this, R.anim.push_left_in);
		Intent in = new Intent(this.getParent(), SearchPage.class);
		in.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
		Window window = manager.startActivity(MenuGroup.ID_SEARCHPAGE, in);
		View view=window.getDecorView();		
		contain.addView(view);
		LayoutParams params=(LayoutParams) view.getLayoutParams();
        params.width=LayoutParams.FILL_PARENT;
        params.height=LayoutParams.FILL_PARENT;
        view.setLayoutParams(params);
        view.startAnimation(animation);
	}
	

	public void RequestAllCities(final String cityName){
		new Thread(new Runnable() {
			@Override
			public void run() {
				// TODO Auto-generated method stub
				
				try {
					String jsonString = MenuUtils.getAllCities();
					if(!jsonString.isEmpty()){
						List<city> cities = JsonUtils.parseJsonTocities(jsonString);
						Iterator<city> iterator;
						for(iterator=cities.iterator();iterator.hasNext();){
							city c = iterator.next();
							if(cityName.contains(c.getName())){
								appDiancan.locationCity = c;
								mHandler.obtainMessage(HttpHandler.LOCATION_CITY, c).sendToTarget();
							}
						}
						
						FileUtils._cityFile=new File(Environment.getExternalStorageDirectory().getPath()+"/ChiHuoPro/city.txt");
						FileUtils.SaveCity(FileUtils._cityFile.getAbsolutePath(), jsonString);
						
					}
				} catch (Exception e) {
					// TODO Auto-generated catch block
				}
				
			}
		}).start();
	}
	
	
	private class MySearchListener implements MKSearchListener{

		@Override
		public void onGetAddrResult(MKAddrInfo result, int iError) {
			// TODO Auto-generated method stub
			if( iError != 0 || result == null){
                Toast.makeText(MainFirstPage.this, "获取地理信息失败", Toast.LENGTH_LONG).show();
            }else {
                String cityName =result.addressComponents.city;
                FileUtils._cityFile=new File(Environment.getExternalStorageDirectory().getPath()+"/ChiHuoPro/city.txt");
                if(FileUtils._cityFile.exists()){
                	try {
						String jsonString = FileUtils.ReadCity(FileUtils._cityFile.getAbsolutePath());
						List<city> cities = JsonUtils.parseJsonTocities(jsonString);
						
						Iterator<city> iterator;
						for(iterator=cities.iterator();iterator.hasNext();){
							city c = iterator.next();
							if(cityName.contains(c.getName())){
								appDiancan.locationCity = c;
								mHandler.obtainMessage(HttpHandler.LOCATION_CITY, c).sendToTarget();
							}
						}
						
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
						ShowError(e.getMessage());
					}
                }
                else{
                	RequestAllCities(cityName);
                }
            }
			
		}

		@Override
		public void onGetBusDetailResult(MKBusLineResult arg0, int arg1) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void onGetDrivingRouteResult(MKDrivingRouteResult arg0, int arg1) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void onGetPoiDetailSearchResult(int arg0, int arg1) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void onGetPoiResult(MKPoiResult arg0, int arg1, int arg2) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void onGetRGCShareUrlResult(String arg0, int arg1) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void onGetSuggestionResult(MKSuggestionResult arg0, int arg1) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void onGetTransitRouteResult(MKTransitRouteResult arg0, int arg1) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void onGetWalkingRouteResult(MKWalkingRouteResult arg0, int arg1) {
			// TODO Auto-generated method stub
			
		}
  		
  	}
}
