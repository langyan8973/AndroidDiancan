package com.diancan;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import com.baidu.mapapi.BMapManager;
import com.baidu.mapapi.GeoPoint;
import com.baidu.mapapi.LocationListener;
import com.diancan.Helper.OrderHelper;
import com.diancan.Helper.RestaurantHttpHelper;
import com.diancan.Helper.SearchAdapterHelper;
import com.diancan.Utils.JsonUtils;
import com.diancan.Utils.MenuUtils;
import com.diancan.custom.adapter.RestaurantArrayAdapter;
import com.diancan.custom.adapter.RestaurantArrayAdapter.ViewHolder;
import com.diancan.diancanapp.AppDiancan;
import com.diancan.http.HttpCallback;
import com.diancan.http.HttpDownloader;
import com.diancan.http.HttpHandler;
import com.diancan.http.ImageDownloader;
import com.diancan.model.MyRestaurant;
import com.diancan.model.Restaurant;
import com.diancan.model.favorite;
import com.google.zxing.common.StringUtils;

import android.app.Activity;
import android.app.LocalActivityManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.util.SparseIntArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.InputMethodManager;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;

public class RestaurantActivity extends Activity implements HttpCallback,OnClickListener,
									OnItemClickListener,TextWatcher,SearchAdapterHelper {
	ListView mListView;
	Button backButton;
	Button mapButton;
	EditText mEditText;
	ImageView mClearImageView;
	ProgressBar mProgressBar;
	List<Restaurant> mRestaurants;
	RestaurantHttpHelper restaurantHttpHelper;
	SparseIntArray favoriteIntArray = new SparseIntArray();;
	AppDiancan appDiancan;
	ImageDownloader imgDownloader;
	RestaurantArrayAdapter<Restaurant> restaurantAdapter;
	LocationListener mLocationListener=null;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.restaurant);
		
		mListView=(ListView)findViewById(R.id.rList);
		mListView.setOnItemClickListener(this);
		backButton=(Button)findViewById(R.id.bt_back);
		backButton.setOnClickListener(this);
		mapButton=(Button)findViewById(R.id.bt_map);
		mapButton.setOnClickListener(this);
		mEditText = (EditText)findViewById(R.id.searchExt);
		mEditText.addTextChangedListener(this);
		mClearImageView = (ImageView)findViewById(R.id.ImgClear);
		mClearImageView.setOnClickListener(this);
		mProgressBar = (ProgressBar)findViewById(R.id.httppro);
		appDiancan=(AppDiancan)getApplicationContext();
		
		restaurantHttpHelper = new RestaurantHttpHelper(this, appDiancan);
		
		Drawable[] layers={getResources().getDrawable(R.drawable.imagewaiting)};
		imgDownloader=new ImageDownloader(layers);
		
		if(appDiancan.mBMapMan==null)
		{
			appDiancan.mBMapMan=new BMapManager(getApplicationContext());
			appDiancan.mBMapMan.init(appDiancan.BMapKey, new AppDiancan.MyGeneralListener());
		}
		appDiancan.mBMapMan.start();
		//请求收藏列表
		restaurantHttpHelper.RequestFavorites();
		//定位监听器
        mLocationListener=new LocationListener() {
			
			@Override
			public void onLocationChanged(Location location) {
				// TODO Auto-generated method stub
				if (location != null){
					GeoPoint pt = new GeoPoint((int)(location.getLatitude()*1e6),
							(int)(location.getLongitude()*1e6));
					double x=location.getLongitude();
					double y=location.getLatitude();
					Log.d("onLocationChanged", "x===="+x+"    y===="+y);
					mProgressBar.setVisibility(View.VISIBLE);
					restaurantHttpHelper.RequestRestaurants(x,y,1000);
					
				}
			}
		};
		
	}
	
	
	
	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		if(appDiancan.mBMapMan!=null){
			appDiancan.mBMapMan.getLocationManager().removeUpdates(mLocationListener);
			appDiancan.mBMapMan.stop();
		}
		super.onPause();
	}



	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		appDiancan.mBMapMan.getLocationManager().requestLocationUpdates(mLocationListener);
		appDiancan.mBMapMan.start();
		super.onResume();
	}



	@Override
	public void RequestComplete(Message msg) {
		// TODO Auto-generated method stub
		mProgressBar.setVisibility(View.GONE);
		switch(msg.what) {  
        case HttpHandler.REQUEST_RESTAURANTS: 
        	mRestaurants = (List<Restaurant>)msg.obj;
        	DisplayRestaurants();
            break; 
        case HttpHandler.REQUEST_FAVORITES:
        	List<favorite> favorites = (List<favorite>)msg.obj;
        	if(favorites==null||favorites.size()==0){
				return;
			}
			Iterator<favorite> iterator;
			for(iterator=favorites.iterator();iterator.hasNext();){
				favorite f=iterator.next();
				favoriteIntArray.put(f.getRid(), f.getId());
			}
			if(restaurantAdapter!=null){
				restaurantAdapter.setfSparseIntArray(favoriteIntArray);
				restaurantAdapter.notifyDataSetChanged();
			}
			break;
        case HttpHandler.POST_FAVORITE:
        	favorite f = (favorite)msg.obj;
        	if(favoriteIntArray==null){
        		favoriteIntArray = new SparseIntArray();
        	}
        	favoriteIntArray.put(f.getRid(), f.getId());
        	restaurantAdapter.notifyDataSetChanged();
        	break;
        case HttpHandler.DELETE_FAVORITE:
        	int rid = Integer.parseInt(msg.obj.toString());
        	favoriteIntArray.delete(rid);
        	restaurantAdapter.notifyDataSetChanged();
        	break;
        default:
            break;
        }
	}

	@Override
	public void RequestError(String errString) {
		// TODO Auto-generated method stub
		mProgressBar.setVisibility(View.GONE);
		ShowError(errString);
	}

	@Override
	public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
		// TODO Auto-generated method stub
		InputMethodManager imm = (InputMethodManager)getSystemService(RestaurantActivity.this.INPUT_METHOD_SERVICE);
		imm.hideSoftInputFromWindow(mEditText.getWindowToken(), 0);
		
		Restaurant restaurant = mRestaurants.get(arg2);
		MyRestaurant myRestaurant=new MyRestaurant(restaurant);
		appDiancan.myRestaurant=myRestaurant;
		if(appDiancan.myOrder!=null){
			if(appDiancan.myOrderHelper==null){
				appDiancan.myOrderHelper = new OrderHelper(appDiancan.myOrder);
			}
			else{
				appDiancan.myOrderHelper.SetOrderAndItemDic(appDiancan.myOrder);
			}
		}
		ToRecipeListPage();
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.bt_back:
			ToMainFirstPage();
			break;
		case R.id.bt_map:
			ToMapPage();
			break;
		case R.id.ImgClear:
			mEditText.setText("");
			break;
		case R.id.favoriteBtn:
			int rid = Integer.parseInt(v.getTag().toString());
			mProgressBar.setVisibility(View.VISIBLE);
			if(favoriteIntArray.get(rid)==0){
				restaurantHttpHelper.favoriteRestaurant(rid);
			}
			else{
				restaurantHttpHelper.deleteFavorite(rid);
			}
			break;
		default:
			break;
		}
	}

	@Override
	public void afterTextChanged(Editable s) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void beforeTextChanged(CharSequence s, int start, int count,
			int after) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onTextChanged(CharSequence s, int start, int before, int count) {
		// TODO Auto-generated method stub
		restaurantAdapter.getFilter().filter(s);
		if(count<=0){
			mClearImageView.setVisibility(View.GONE);
		}
		else{
			mClearImageView.setVisibility(View.VISIBLE);
		}
	}
	
	@Override
	public void SetListViewHeight(int count) {
		// TODO Auto-generated method stub
		
	}
	
	
	/**
	 * 显示餐厅列表
	 */
	private void DisplayRestaurants(){
		restaurantAdapter = 
				new RestaurantArrayAdapter<Restaurant>(this,R.layout.list_item_restaurant,mRestaurants,favoriteIntArray);
		restaurantAdapter.setImageDownloader(imgDownloader);
		restaurantAdapter.setMyClickListener(this);
		restaurantAdapter.setmAdapterHelper(this);
		mListView.setAdapter(restaurantAdapter);
	}
	
	/**
  	 * 显示错误信息
  	 * @param strMess
  	 */
  	public void ShowError(String strMess) {
		Toast toast = Toast.makeText(RestaurantActivity.this, strMess, Toast.LENGTH_SHORT); 
        toast.show();
	}
  	
  	/**
  	 * 跳回导航页
  	 */
  	private void ToMainFirstPage(){
  		MenuGroup parent = (MenuGroup)this.getParent();
		LocalActivityManager manager = parent.getLocalActivityManager();
		Activity activity=manager.getCurrentActivity();
		Window w1=activity.getWindow();
		View v1=w1.getDecorView();
		Animation sAnimation=AnimationUtils.loadAnimation(this, R.anim.push_right_out);
		v1.startAnimation(sAnimation);
	    final LinearLayout contain = (LinearLayout) parent.findViewById(R.id.group_Layout);
		contain.removeAllViews();
		
		Animation animation = AnimationUtils.loadAnimation(this, R.anim.push_right_in);
		Intent in = new Intent(this.getParent(), MainFirstPage.class);
		in.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
		Window window = manager.startActivity(MenuGroup.ID_MAINFIRST, in);
		View view=window.getDecorView();		
		contain.addView(view);
		LayoutParams params=(LayoutParams) view.getLayoutParams();
        params.width=LayoutParams.FILL_PARENT;
        params.height=LayoutParams.FILL_PARENT;
        view.setLayoutParams(params);
        view.startAnimation(animation);
	}
  	
  	private void ToMapPage(){
  		MenuGroup parent = (MenuGroup)this.getParent();
  		LocalActivityManager manager = parent.getLocalActivityManager();
  		Activity activity=manager.getCurrentActivity();
		Window w1=activity.getWindow();
		View v1=w1.getDecorView();
		Animation sAnimation=AnimationUtils.loadAnimation(this, R.anim.restaurantlist_out);
		v1.startAnimation(sAnimation);
	    final LinearLayout contain = (LinearLayout) parent.findViewById(R.id.group_Layout);
		contain.removeAllViews();
		
		Intent in = new Intent(this.getParent(), MapViewActivity.class);
		in.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
		Window window = manager.startActivity("MapViewActivity", in);
		
		View view=window.getDecorView();		
		contain.addView(view);
		LayoutParams params=(LayoutParams) view.getLayoutParams();
        params.width=LayoutParams.FILL_PARENT;
        params.height=LayoutParams.FILL_PARENT;
        view.setLayoutParams(params);
  	}
  	
  	private void ToRecipeListPage(){
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
		Intent intent = new Intent(this.getParent(), RecipeList.class);
		MenuGroup.back_id = MenuGroup.ID_RESTAURANTACTIVITY;
//		in.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
		Window window = manager.startActivity(MenuGroup.ID_RECIPLIST, intent);
		View view=window.getDecorView();		
		contain.addView(view);
		LayoutParams params=(LayoutParams) view.getLayoutParams();
        params.width=LayoutParams.FILL_PARENT;
        params.height=LayoutParams.FILL_PARENT;
        view.setLayoutParams(params);
        view.startAnimation(animation);
  	}

}
