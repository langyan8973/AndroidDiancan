package com.declare;

import java.io.File;
import java.net.ResponseCache;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import com.Utils.FileUtils;
import com.download.MyResponseCache2;
import com.mode.CategoryObj;
import com.mode.DeskObj;
import com.mode.History;
import com.mode.MenuListDataObj;
import com.mode.SelectedMenuObj;
import com.mode.SelectedProduct;
import com.model.AllDomain;
import com.model.Category;
import com.model.Desk;
import com.model.Order;
import com.model.OrderItem;

import android.R.integer;
import android.app.Application;
import android.os.Build;
import android.os.Build.VERSION;
import android.os.StrictMode;

public class Declare extends Application {
	public MenuListDataObj menuListDataObj;
	public History history;
	
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
//				oItem.setCount(oItem.getCount()+1);
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
//		Iterator<OrderItem> iterator;
//		for(iterator=curOrder.getOrderItems().iterator();iterator.hasNext();)
//		{
//			OrderItem oItem=iterator.next();
//			if(oItem.getRecipe().getId()==orderItem.getRecipe().getId())
//			{
//				totalCount-=oItem.getCount();
//				totalPrice-=oItem.getCount()*oItem.getRecipe().getPrice();
//				curOrder.getOrderItems().remove(oItem);
//				System.out.println("Remove2");
//				break;
//			}
//		}
	}
	
	public void SubtractionItemCount(OrderItem orderItem)
	{
		System.out.println("Delete1");
		totalCount-=1;
		totalPrice-=orderItem.getRecipe().getPrice();
		System.out.println("Delete2");
//		Iterator<OrderItem> iterator;
//		for(iterator=curOrder.getOrderItems().iterator();iterator.hasNext();)
//		{
//			OrderItem oItem=iterator.next();
//			if(oItem.getRecipe().getId()==orderItem.getRecipe().getId())
//			{
//				oItem.setCount(oItem.getCount()-1);
//				totalCount-=1;
//				totalPrice-=oItem.getRecipe().getPrice();
//				break;
//			}
//		}
	}
}
