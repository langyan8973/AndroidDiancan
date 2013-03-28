package com.diancan;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import android.R.integer;
import android.app.Activity;
import android.app.LocalActivityManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.Paint.Style;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.ViewGroup.LayoutParams;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.OvershootInterpolator;
import android.view.animation.ScaleAnimation;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.baidu.mapapi.BMapManager;
import com.baidu.mapapi.GeoPoint;
import com.baidu.mapapi.ItemizedOverlay;
import com.baidu.mapapi.LocationListener;
import com.baidu.mapapi.MKMapViewListener;
import com.baidu.mapapi.MapActivity;
import com.baidu.mapapi.MapView;
import com.baidu.mapapi.MyLocationOverlay;
import com.baidu.mapapi.OverlayItem;
import com.baidu.mapapi.Projection;
import com.diancan.Utils.MenuUtils;
import com.diancan.diancanapp.AppDiancan;
import com.diancan.http.ImageDownloader;
import com.diancan.model.MyRestaurant;
import com.diancan.model.Restaurant;

public class MapViewActivity extends MapActivity implements OnClickListener {
	
	AppDiancan declare;
	MapView mapView=null;
	View mPopView = null;
	LocationListener mLocationListener=null;
	MyLocationOverlay myLocationOverlay=null;
	MKMapViewListener mapViewListener = null;
	Button mBtnList;
	Button mBtnBack;
	RestaurantsOverItems overitem=null;
	int iZoom = 0;
	int selectId=0;
	String selectName;
	String selectImage;
	List<Restaurant> mRestaurants;
	ImageDownloader imgDownloader;
	//记录范围变化次数
	int locationCount = 0;
	private Handler httpHandler = new Handler() {  
        public void handleMessage (Message msg) {
        	//此方法在ui线程运行   
            switch(msg.what) {  
            case 0: 
            	String errString=msg.obj.toString();
            	ShowError(errString);
                break;   
            case 1: 
            	DisplayRestaurants();
                break;  
            }  
        }  
    };
	@Override
	protected void onCreate(Bundle arg0) {
		// TODO Auto-generated method stub
		super.onCreate(arg0);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.mapview);
		
		mBtnList=(Button)findViewById(R.id.bt_list);
		mBtnList.setOnClickListener(this);
		mBtnBack=(Button)findViewById(R.id.bt_back);
		mBtnBack.setOnClickListener(this);
		Drawable[] layers={getResources().getDrawable(R.drawable.imagewaiting)};
		imgDownloader=new ImageDownloader(layers);
		declare=(AppDiancan)this.getApplicationContext();
		if(declare.mBMapMan==null)
		{
			declare.mBMapMan=new BMapManager(getApplicationContext());
			declare.mBMapMan.init(declare.BMapKey, new AppDiancan.MyGeneralListener());
		}
		declare.mBMapMan.start();
		super.initMapActivity(declare.mBMapMan);
		mapView = (MapView)findViewById(R.id.bmapView);
        mapView.setBuiltInZoomControls(true);
        mapView.setDrawOverlayWhenZooming(true);
        
        myLocationOverlay=new MyLocationOverlay(this, mapView);
        mapView.getOverlays().add(myLocationOverlay);
        
        //定位监听器
        mLocationListener=new LocationListener() {
			
			@Override
			public void onLocationChanged(Location location) {
				// TODO Auto-generated method stub
				if (location != null){
					GeoPoint pt = new GeoPoint((int)(location.getLatitude()*1e6),
							(int)(location.getLongitude()*1e6));
					mapView.getController().animateTo(pt);
					double x=location.getLongitude();
					double y=location.getLatitude();
					Log.d("onLocationChanged", "x===="+x+"    y===="+y);
					if(locationCount==1){
						RequestRestaurants(x,y,1000);
					}
					
				}
			}
		};
		
		//地图监听器
		mapViewListener = new MKMapViewListener() {
			
			@Override
			public void onMapMoveFinish() {
				// TODO Auto-generated method stub
				GeoPoint gPoint = mapView.getMapCenter();
				double x = gPoint.getLongitudeE6()/1000000.0f;
				double y = gPoint.getLatitudeE6()/1000000.0f;
				Log.d("onMapMoveFinish", "x===="+x+"    y===="+y);
				if(locationCount>0){
					RequestRestaurants(x,y, 10000);
				}
				locationCount++;
			}
		};
		mapView.regMapViewListener(declare.mBMapMan, mapViewListener);
	}

	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
	}

	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		if(declare.mBMapMan!=null){
			declare.mBMapMan.getLocationManager().removeUpdates(mLocationListener);
			myLocationOverlay.disableMyLocation();
			myLocationOverlay.disableCompass();
			declare.mBMapMan.stop();
		}
		super.onPause();
	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		locationCount = 0;
		declare.mBMapMan.getLocationManager().requestLocationUpdates(mLocationListener);
		myLocationOverlay.enableMyLocation();
		myLocationOverlay.enableCompass();
		declare.mBMapMan.start();
		super.onResume();
	}

	@Override
	protected boolean isRouteDisplayed() {
		// TODO Auto-generated method stub
		return false;
	}
	
	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		if(v.getId()==R.id.bt_list){
			ToRestaurantPage();
		}else if(v.getId()==R.id.bt_back){
			ToMainFirstPage();
		}
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
	
	/**
	 * 回到列表页
	 */
	private void ToRestaurantPage(){
		MenuGroup parent = (MenuGroup)this.getParent();
		LocalActivityManager manager = parent.getLocalActivityManager();
		Activity activity=manager.getCurrentActivity();
		Window w1=activity.getWindow();
		View v1=w1.getDecorView();
		Animation sAnimation=AnimationUtils.loadAnimation(this, R.anim.restaurantlist_out);
		v1.startAnimation(sAnimation);
	    final LinearLayout contain = (LinearLayout) parent.findViewById(R.id.group_Layout);
		contain.removeAllViews();
		Animation animation = AnimationUtils.loadAnimation(this, R.anim.restaurantlist_in);
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
		MenuGroup.back_id = MenuGroup.ID_MAPVIEWACTIVITY;
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
	
	/**
	 * 显示错误信息
	 * @param strMess
	 */
	public void ShowError(String strMess) {
		Toast toast = Toast.makeText(MapViewActivity.this, strMess, Toast.LENGTH_SHORT); 
	    toast.show();
	}
	
	
	/**
	 * 请求餐厅数据
	 */
	private void RequestRestaurants(final double x,final double y,final double distance){
		new Thread(new Runnable() {
			@Override
			public void run() {
				// TODO Auto-generated method stub
				try {
					mRestaurants=MenuUtils.getAround(declare.udidString,x,y,distance,
							declare.accessToken.getAuthorization(),declare.selectedCity.getId());
					if(mRestaurants==null||mRestaurants.size()==0){
						httpHandler.obtainMessage(0,"没有餐厅！").sendToTarget();
						return;
					}
					Iterator<Restaurant> iterator;
					for(iterator=mRestaurants.iterator();iterator.hasNext();){
						Restaurant restaurant=iterator.next();
						if(restaurant.getX()==0){
							mRestaurants.remove(restaurant);
						}
					}
					httpHandler.obtainMessage(1).sendToTarget();
				} catch (Exception e) {
					// TODO Auto-generated catch block
					httpHandler.obtainMessage(0,e.getMessage()).sendToTarget();
				}
				
			}
		}).start();
	}
	/**
	 * 显示餐厅
	 */
	private void DisplayRestaurants(){
		Drawable marker = getResources().getDrawable(R.drawable.mapmarker); 
		marker.setBounds(0, 0, marker.getIntrinsicWidth(), marker
				.getIntrinsicHeight());
		overitem = new RestaurantsOverItems(marker, this, mRestaurants);
		mapView.getOverlays().add(overitem); 
		mPopView=super.getLayoutInflater().inflate(R.layout.popview, null);
		mapView.addView( mPopView,
                new MapView.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT,
                		null, MapView.LayoutParams.TOP_LEFT));
		mPopView.setVisibility(View.GONE);
		iZoom = mapView.getZoomLevel();
		mPopView.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				MyRestaurant myRestaurant=new MyRestaurant();
				myRestaurant.setId(selectId);
				myRestaurant.setName(selectName);
				myRestaurant.setImage(selectImage);
				declare.myRestaurant=myRestaurant;
//				Intent intent=new Intent(MapViewActivity.this, Main.class);
//			    startActivity(intent);
//			    MapViewActivity.this.finish();
				ToRecipeListPage();
			}
		});
	}
	
	class RestaurantsOverItems extends ItemizedOverlay<OverlayItem>{
		private List<Restaurant> mRestaurants;
		private Drawable mDrawable;
		List<OverlayItem> pointsList=new ArrayList<OverlayItem>();
		public RestaurantsOverItems(Drawable marker,Context context,List<Restaurant> restaurants) {
			
			super(boundCenterBottom(marker));
			// TODO Auto-generated constructor stub
			mDrawable=marker;
			mRestaurants=restaurants;
			
			Iterator<Restaurant> iterator;
			for(iterator=mRestaurants.iterator();iterator.hasNext();){
				Restaurant restaurant=iterator.next();
				if(restaurant.getX()!=0){
					GeoPoint point = new GeoPoint((int) (restaurant.getY() * 1E6), (int) (restaurant.getX() * 1E6));
					pointsList.add(new OverlayItem(point, restaurant.getName(), restaurant.getId()+""));
				}
			}
			
			populate();
		}
		
		public void updateOverlay()
		{
			populate();
		}

		@Override
		public void draw(Canvas canvas, MapView mapView, boolean shadow) {
			// TODO Auto-generated method stub
			Projection projection = mapView.getProjection(); 
			for (int index = size() - 1; index >= 0; index--) { 
				OverlayItem overLayItem = getItem(index);
				overLayItem.setMarker(mDrawable);
				String title = overLayItem.getTitle();
				Point point = projection.toPixels(overLayItem.getPoint(), null); 
				Paint paintText = new Paint();
				paintText.setColor(Color.DKGRAY);
				paintText.setTextSize(15);
				paintText.setStyle(Style.STROKE);
				canvas.drawText(title, point.x, point.y, paintText);
			}
			
			super.draw(canvas, mapView, shadow);
//			boundCenterBottom(mDrawable);
		}

		@Override
		protected OverlayItem createItem(int arg0) {
			// TODO Auto-generated method stub
			return pointsList.get(arg0);
		}

		@Override
		public int size() {
			// TODO Auto-generated method stub
			return pointsList.size();
		}

		@Override
		protected boolean onTap(int i) {
			// TODO Auto-generated method stub
			setFocus(pointsList.get(i));
			GeoPoint pt = pointsList.get(i).getPoint();
			mapView.updateViewLayout(mPopView,
	                new MapView.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT,
	                		pt, MapView.LayoutParams.BOTTOM_CENTER));
			mPopView.setVisibility(View.VISIBLE);
			StartAnimation();
			
			
			//设置popview显示
			Restaurant restaurant=mRestaurants.get(i);
			TextView titleView=(TextView)mPopView.findViewById(R.id.r_title);
			titleView.setText(restaurant.getName());
			TextView addressView=(TextView)mPopView.findViewById(R.id.r_address);
			addressView.setText(restaurant.getAddress());
			ImageView rImageView = (ImageView)mPopView.findViewById(R.id.restaurantImg);
			String strUrl;
			if(restaurant.getImage()==null){
				strUrl=null;
			}else{
				strUrl=MenuUtils.imageUrl+MenuUtils.IMAGE_SMALL+restaurant.getImage();
			}
			imgDownloader.download(strUrl, rImageView);
			String idString=pointsList.get(i).getSnippet();
			selectId=Integer.parseInt(idString);
			selectName = restaurant.getName();
			selectImage = restaurant.getImage();
			return true;
		}

		@Override
		public boolean onTap(GeoPoint arg0, MapView arg1) {
			// TODO Auto-generated method stub
			mPopView.setVisibility(View.GONE);
			return super.onTap(arg0, arg1);
		}
		
		private void StartAnimation(){
			mPopView.clearAnimation();
			Animation animation = new ScaleAnimation(0f, 1f, 0f, 1f,
					Animation.RELATIVE_TO_SELF, 0.5f,Animation.RELATIVE_TO_SELF, 1f);
			animation.setDuration(300);
			animation.setInterpolator(new OvershootInterpolator());
			mPopView.startAnimation(animation);
		}
		
	}
}
