package com.declare;

import java.util.HashMap;
import java.util.Iterator;
import cn.jpush.android.api.JPushInterface;
import com.baidu.mapapi.BMapManager;
import com.baidu.mapapi.MKEvent;
import com.baidu.mapapi.MKGeneralListener;
import com.mode.History;
import com.mode.MenuListDataObj;
import com.model.Order;
import com.model.OrderItem;
import android.app.Application;
import android.util.Log;
import android.widget.Toast;

public class Declare extends Application {
	public String udidString;
	public MenuListDataObj menuListDataObj;
	public HashMap<String, String> hashTypes;
	public History history;
	public int restaurantId;
	//百度地图相关
	public String BMapKey="79D53C4E2FE1E8F907D3087A68958DFDB8CE1E6C";
	public BMapManager mBMapMan;
	static Declare mDemoApp;
	boolean m_bKeyRight=true;
	
	//2012.5.28
	public Order curOrder;
	public int totalCount=0;
	public double totalPrice=0;
	
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
	
	public MenuListDataObj getMenuListDataObj() {
		return menuListDataObj;
	}

	public void setMenuListDataObj(MenuListDataObj menuListDataObj) {
		this.menuListDataObj = menuListDataObj;
	}
	public History getHistory() {
		return history;
	}

	public void setHistory(History history) {
		this.history = history;
	}
	
	public int getTotalCount() {
		return totalCount;
	}

	public void setTotalCount(int totalCount) {
		this.totalCount = totalCount;
	}

	public double getTotalPrice() {
		return totalPrice;
	}

	public void setTotalPrice(double totalPrice) {
		this.totalPrice = totalPrice;
	}
	
	public void AddItemToOrder(OrderItem orderItem)
	{
		if(curOrder.getOrderItems().isEmpty())
		{
			orderItem.setOrder(curOrder);
			curOrder.getOrderItems().add(orderItem);
			totalCount=orderItem.getCount();
			totalPrice=orderItem.getCount()*orderItem.getRecipe().getPrice();
			return;
		}
		
		Iterator<OrderItem> iterator;
		boolean isContains=false;
		for(iterator=curOrder.getOrderItems().iterator();iterator.hasNext();)
		{
			OrderItem oItem=iterator.next();
			if(oItem.getRecipe().getId()==orderItem.getRecipe().getId())
			{
				totalCount+=1;
				totalPrice+=orderItem.getRecipe().getPrice();
				isContains=true;
				break;
			}
		}
		if(!isContains)
		{
			orderItem.setOrder(curOrder);
			orderItem.setCount(1);
			curOrder.getOrderItems().add(orderItem);
			totalCount+=1;
			totalPrice+=orderItem.getRecipe().getPrice();
		}
	}
	
	public void RemoveItemFromOrder(OrderItem orderItem)
	{
		System.out.println("count:"+orderItem.getCount());
		System.out.println("Remove1");
		totalCount-=orderItem.getCount();
		totalPrice-=orderItem.getCount()*orderItem.getRecipe().getPrice();
		curOrder.getOrderItems().remove(orderItem);
		System.out.println("Remove2");
	}
	
	public void SubtractionItemCount(OrderItem orderItem)
	{
		System.out.println("Delete1");
		totalCount-=1;
		totalPrice-=orderItem.getRecipe().getPrice();
		System.out.println("Delete2");
	}
	
	public static class MyGeneralListener implements MKGeneralListener {
		@Override
		public void onGetNetworkState(int iError) {
			Log.d("MyGeneralListener", "onGetNetworkState error is "+ iError);
			Toast.makeText(Declare.mDemoApp.getApplicationContext(), "onGetNetworkState error is "+ iError,
					Toast.LENGTH_LONG).show();
		}

		@Override
		public void onGetPermissionState(int iError) {
			Log.d("MyGeneralListener", "onGetPermissionState error is "+ iError);
			if (iError ==  MKEvent.ERROR_PERMISSION_DENIED) {
				Toast.makeText(Declare.mDemoApp.getApplicationContext(), 
						"onGetPermissionState error is "+ iError,
						Toast.LENGTH_LONG).show();
				Declare.mDemoApp.m_bKeyRight = false;
			}
		}
	}
}
