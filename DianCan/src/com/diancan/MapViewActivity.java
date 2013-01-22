package com.diancan;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import android.R.integer;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.Window;
import android.view.ViewGroup.LayoutParams;
import android.widget.TextView;
import android.widget.Toast;

import com.Utils.MenuUtils;
import com.baidu.mapapi.BMapManager;
import com.baidu.mapapi.GeoPoint;
import com.baidu.mapapi.ItemizedOverlay;
import com.baidu.mapapi.LocationListener;
import com.baidu.mapapi.MapActivity;
import com.baidu.mapapi.MapView;
import com.baidu.mapapi.MyLocationOverlay;
import com.baidu.mapapi.OverlayItem;
import com.baidu.mapapi.Projection;
import com.declare.Declare;
import com.diancan.RestaurantActivity.RestaurantAdapter;
import com.model.Restaurant;

public class MapViewActivity extends MapActivity {
	
	Declare declare;
	MapView mapView=null;
	View mPopView = null;
	LocationListener mLocationListener=null;
	MyLocationOverlay myLocationOverlay=null;
	RestaurantsOverItems overitem=null;
	int iZoom = 0;
	int selectId=0;
	List<Restaurant> mRestaurants;
	@SuppressLint("HandlerLeak")
	private Handler httpHandler = new Handler() {  
        public void handleMessage (Message msg) {//此方法在ui线程运行   
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
		
		declare=(Declare)this.getApplicationContext();
		if(declare.mBMapMan==null)
		{
			declare.mBMapMan=new BMapManager(getApplicationContext());
			declare.mBMapMan.init(declare.BMapKey, new Declare.MyGeneralListener());
		}
		declare.mBMapMan.start();
		super.initMapActivity(declare.mBMapMan);
		mapView = (MapView)findViewById(R.id.bmapView);
        mapView.setBuiltInZoomControls(true);
        mapView.setDrawOverlayWhenZooming(true);
        
        myLocationOverlay=new MyLocationOverlay(this, mapView);
        mapView.getOverlays().add(myLocationOverlay);
        mLocationListener=new LocationListener() {
			
			@Override
			public void onLocationChanged(Location location) {
				// TODO Auto-generated method stub
				if (location != null){
					GeoPoint pt = new GeoPoint((int)(location.getLatitude()*1e6),
							(int)(location.getLongitude()*1e6));
					mapView.getController().animateTo(pt);
					double y=location.getLatitude();
					double x=location.getLongitude();
					RequestRestaurants(x,y,1000);
				}
			}
		};
		
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
					mRestaurants=MenuUtils.getAround(declare.udidString,x,y,distance);
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
		Drawable marker = getResources().getDrawable(R.drawable.iconmarka); 
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
				declare.restaurantId=selectId;
				Intent intent=new Intent(MapViewActivity.this, Main.class);
			    startActivity(intent);
			    MapViewActivity.this.finish();
			}
		});
	}
	
	class RestaurantsOverItems extends ItemizedOverlay<OverlayItem>{
		private List<Restaurant> mRestaurants;
		private Drawable mDrawable;
		private Context mContext;
		List<OverlayItem> pointsList=new ArrayList<OverlayItem>();
		public RestaurantsOverItems(Drawable marker,Context context,List<Restaurant> restaurants) {
			
			super(boundCenterBottom(marker));
			// TODO Auto-generated constructor stub
			mDrawable=marker;
			mContext=context;
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
				String title = overLayItem.getTitle();
				Point point = projection.toPixels(overLayItem.getPoint(), null); 
				Paint paintText = new Paint();
				paintText.setColor(Color.BLUE);
				paintText.setTextSize(15);
				canvas.drawText(title, point.x-30, point.y, paintText);
			}

			super.draw(canvas, mapView, shadow);
			boundCenterBottom(mDrawable);
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
			//设置popview显示
			Restaurant restaurant=mRestaurants.get(i);
			TextView titleView=(TextView)mPopView.findViewById(R.id.r_title);
			titleView.setText(restaurant.getName());
			TextView addressView=(TextView)mPopView.findViewById(R.id.r_address);
			addressView.setText(restaurant.getAddress());
			TextView phoneView=(TextView)mPopView.findViewById(R.id.r_phone);
			phoneView.setText(restaurant.getTelephone());
			String idString=pointsList.get(i).getSnippet();
			selectId=Integer.parseInt(idString);
			return true;
		}

		@Override
		public boolean onTap(GeoPoint arg0, MapView arg1) {
			// TODO Auto-generated method stub
			mPopView.setVisibility(View.GONE);
			return super.onTap(arg0, arg1);
		}
		
	}
}
