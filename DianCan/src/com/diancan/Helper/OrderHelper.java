package com.diancan.Helper;

import java.util.Iterator;

import com.diancan.diancanapp.AppDiancan;
import com.diancan.model.OrderItem;

public class OrderHelper {
	AppDiancan mAppDiancan;
	public OrderHelper(AppDiancan appDiancan){
		mAppDiancan=appDiancan;
	}
	public void AddToOrderForm(OrderItem orderItem){
		if(mAppDiancan.curOrder.getOrderItems().isEmpty())
		{
			orderItem.setOrder(mAppDiancan.curOrder);
			mAppDiancan.curOrder.getOrderItems().add(orderItem);
			mAppDiancan.totalCount=orderItem.getCount();
			mAppDiancan.totalPrice=orderItem.getCount()*orderItem.getRecipe().getPrice();
			return;
		}
		
		Iterator<OrderItem> iterator;
		boolean isContains=false;
		for(iterator=mAppDiancan.curOrder.getOrderItems().iterator();iterator.hasNext();)
		{
			OrderItem oItem=iterator.next();
			if(oItem.getRecipe().getId()==orderItem.getRecipe().getId())
			{
				mAppDiancan.totalCount+=1;
				mAppDiancan.totalPrice+=orderItem.getRecipe().getPrice();
				isContains=true;
				break;
			}
		}
		if(!isContains)
		{
			orderItem.setOrder(mAppDiancan.curOrder);
			orderItem.setCount(1);
			mAppDiancan.curOrder.getOrderItems().add(orderItem);
			mAppDiancan.totalCount+=1;
			mAppDiancan.totalPrice+=orderItem.getRecipe().getPrice();
		}
	}
	
	public void deleteFromOrderForm(OrderItem orderItem){
		mAppDiancan.totalCount-=orderItem.getCount();
		mAppDiancan.totalPrice-=orderItem.getCount()*orderItem.getRecipe().getPrice();
		mAppDiancan.curOrder.getOrderItems().remove(orderItem);
	}

	public void Subtraction(OrderItem orderItem){
		mAppDiancan.totalCount-=1;
		mAppDiancan.totalPrice-=orderItem.getRecipe().getPrice();
	}
}
