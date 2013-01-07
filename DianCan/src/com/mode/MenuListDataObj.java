package com.mode;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import com.model.Category;
import com.model.Order;
import com.model.OrderItem;
import com.model.Recipe;

import android.R.integer;

public class MenuListDataObj {
	
	public MenuListDataObj()
	{
		categories=new ArrayList<Category>();
		recipeMap=new HashMap<Integer, List<OrderItem>>();
	}
	
	public List<Category> categories;		
	public List<Category> getCategories() {
		return categories;
	}
	public void setCategories(List<Category> categories) {
		this.categories = categories;
	}
	
	public HashMap<Integer, List<OrderItem>> recipeMap;			
	public HashMap<Integer, List<OrderItem>> getRecipeMap() {
		return recipeMap;
	}
	public void setRecipeMap(HashMap<Integer, List<OrderItem>> recipeMap) {
		this.recipeMap = recipeMap;
	}
	
	public void ChangeRecipeMapByObj(OrderItem orderItem)
	{
		int cid=orderItem.getRecipe().getCid();
		int id=orderItem.getRecipe().getId();
		Iterator<Integer> iterator;
		Set<Integer> kSet=recipeMap.keySet();
		for(iterator=kSet.iterator();iterator.hasNext();)
		{
			int categoryId=iterator.next();
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
	
	
//	public void ChangeMenuListByObj(SelectedMenuObj menu)
//	{
//		String cidString=menu.getCid();
//		for(CategoryObj categoryObj:categoryObjs)
//		{
//			if(cidString.equals(categoryObj.getId()+""))
//			{
//				List<SelectedMenuObj> cList=categoryObj.getSelectedMenuObjs();
//				Iterator<SelectedMenuObj> iterator;
//				for (iterator = cList.iterator(); iterator.hasNext();) {
//					SelectedMenuObj menuinfo =iterator.next();
//					if(menuinfo.getId().equals(menu.getId()))
//					{
//						menuinfo.setCount(menu.getCount());
//						menuinfo.setTotalPrice(menu.getTotalPrice());
//						break;
//					}
//				}
//			}
//		}		
//	}
	
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
					break;
				}
			}
			
		}
	}
	
//	public void ChangeMenuListByOrder(Order order)
//	{
//		Set<OrderItem> orderitems=order.getOrderItems();
//		Iterator<OrderItem> iterator;
//		for(iterator=orderitems.iterator();iterator.hasNext();)
//		{
//			OrderItem oItem=iterator.next();
//			String cidString=oItem.getRecipe().getCid().toString();
//			for(CategoryObj categoryObj:categoryObjs)
//			{
//				if(cidString.equals(categoryObj.getId()+""))
//				{
//					List<SelectedMenuObj> cList=categoryObj.getSelectedMenuObjs();
//					Iterator<SelectedMenuObj> iterator1;
//					for (iterator1 = cList.iterator(); iterator1.hasNext();) {
//						SelectedMenuObj menuinfo =iterator1.next();
//						if(menuinfo.getId().equals(oItem.getRecipe().getId().toString()))
//						{
//							menuinfo.setCount(oItem.getCount());
//							menuinfo.setTotalPrice(oItem.getCount()*oItem.getRecipe().getPrice());
//							break;
//						}
//					}
//				}
//			}
//		}
//	}
	
	public void SyncMenuListByCategory(Category category,Order order)
	{
		List<OrderItem> cOrderItems=recipeMap.get(category.getId());
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
		Set<Integer> keySet=recipeMap.keySet();
		Iterator<Integer> iterator;
		for(iterator=keySet.iterator();iterator.hasNext();)
		{
			int key=iterator.next();
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
