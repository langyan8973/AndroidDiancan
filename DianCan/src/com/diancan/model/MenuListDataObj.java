package com.diancan.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Set;


import android.R.integer;
import android.util.SparseArray;

public class MenuListDataObj {
	
	public MenuListDataObj()
	{
		categories=new ArrayList<Category>();
		recipeMap=new SparseArray<List<OrderItem>>();
	}
	
	public List<Category> categories;		
	public List<Category> getCategories() {
		return categories;
	}
	public void setCategories(List<Category> categories) {
		this.categories = categories;
	}
	
	public SparseArray<List<OrderItem>> recipeMap;			
	public SparseArray<List<OrderItem>> getRecipeMap() {
		return recipeMap;
	}
	public void setRecipeMap(SparseArray<List<OrderItem>> recipeMap) {
		this.recipeMap = recipeMap;
	}
	
	public void ChangeRecipeMapByObj(OrderItem orderItem)
	{
		int cid=orderItem.getRecipe().getCid();
		int id=orderItem.getRecipe().getId();
		int count=recipeMap.size();
		for(int i=0;i<count;i++){
			int categoryId=recipeMap.keyAt(i);
			if(categoryId==cid)
			{
				List<OrderItem> items=recipeMap.get(cid);
				Iterator<OrderItem> iterator2;
				for(iterator2=items.iterator();iterator2.hasNext();)
				{
					OrderItem orderItem2=iterator2.next();
					int id1=orderItem2.getRecipe().getId();
					if(id1==id)
					{
						orderItem2.setCount(orderItem.getCount());
						break;
					}
				}
				break;
			}
		}
	}
	
	
	
	public void ChangeRecipeMapByOrder(Order order)
	{
		List<OrderItem> orderitems=order.getOrderItems();
		Iterator<OrderItem> iterator;
		for(iterator=orderitems.iterator();iterator.hasNext();)
		{
			OrderItem oItem=iterator.next();
			int id=oItem.getRecipe().getId();
			int cid=oItem.getRecipe().getCid();
			List<OrderItem> oItems1=recipeMap.get(cid);
			Iterator<OrderItem> iterator2;
			for(iterator2=oItems1.iterator();iterator2.hasNext();)
			{
				OrderItem oItem1=iterator2.next();
				int id1=oItem1.getRecipe().getId();
				if(id==id1)
				{
					oItem1.setCount(oItem.getCount());
				}
			}
			
		}
	}
	
	public void ChangeRecipeMapByItem(OrderItem item,Order order){
		List<OrderItem> orderitems=order.getOrderItems();
		Iterator<OrderItem> iterator;
		OrderItem sItem=null;
		for(iterator=orderitems.iterator();iterator.hasNext();)
		{
			OrderItem oItem=iterator.next();
			if(oItem.getId()==item.getId()){
				sItem=oItem;
				break;
			}
			
		}
		
		if(sItem!=null){
			int id=sItem.getRecipe().getId();
			int cid=sItem.getRecipe().getCid();
			List<OrderItem> oItems1=recipeMap.get(cid);
			Iterator<OrderItem> iterator2;
			for(iterator2=oItems1.iterator();iterator2.hasNext();)
			{
				OrderItem oItem1=iterator2.next();
				int id1=oItem1.getRecipe().getId();
				if(id==id1)
				{
					oItem1.setCount(sItem.getCount());
					break;
				}
			}
		}
		else{
			int id=item.getRecipe().getId();
			int cid=item.getRecipe().getCid();
			List<OrderItem> oItems1=recipeMap.get(cid);
			Iterator<OrderItem> iterator2;
			for(iterator2=oItems1.iterator();iterator2.hasNext();)
			{
				OrderItem oItem1=iterator2.next();
				int id1=oItem1.getRecipe().getId();
				if(id==id1)
				{
					oItem1.setCount(0);
					break;
				}
			}
		}
	}
	
	
	public void SyncMenuListByCategory(Category category,Order order)
	{
		List<OrderItem> cOrderItems=recipeMap.get(category.getId().intValue());
		List<OrderItem> orderItems=order.getOrderItems();
		Iterator<OrderItem> iterator;
		for(iterator=orderItems.iterator();iterator.hasNext();)
		{
			OrderItem oItem=iterator.next();
			int id=oItem.getRecipe().getId();
			if(oItem.getRecipe().getCid()==category.getId())
			{
				Iterator<OrderItem> iterator2;
				for(iterator2=cOrderItems.iterator();iterator2.hasNext();)
				{
					OrderItem oItem2=iterator2.next();
					int id1=oItem2.getRecipe().getId();
					if(id==id1)
					{
						oItem2.setCount(oItem.getCount());
						break;
					}
				}
			}
		}
	}

	public void RestoreCategoryMenuList()
	{
		int count=recipeMap.size();
		for(int i=0;i<count;i++){
			int key=recipeMap.keyAt(i);
			List<OrderItem> orderItems=recipeMap.get(key);
			Iterator<OrderItem> iterator2;
			for(iterator2=orderItems.iterator();iterator2.hasNext();)
			{
				OrderItem orderItem=iterator2.next();
				orderItem.setCount(0);
				orderItem.setCount(null);
			}
		}
	}
}
