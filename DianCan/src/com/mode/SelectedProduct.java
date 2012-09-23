package com.mode;

import java.io.Serializable;
import java.sql.Date;
import java.util.ArrayList;
import java.util.Dictionary;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import com.model.Order;
import com.model.OrderItem;

import android.R.bool;

public class SelectedProduct  implements Serializable {
	/**
	 * 
	 */
	private double sum=0;
	private int count=0;
	public boolean bState=false;
	public HashMap<String, List<SelectedMenuObj>> dicMenusHashtable=new HashMap<String, List<SelectedMenuObj>>();
	public String strdate;
	public int id;
 	
	public SelectedProduct()
	{
		
	}
	
	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getStrdate() {
		return strdate;
	}
	public void setStrdate(String strdate) {
		this.strdate = strdate;
	}
	public double getSum() {
		return sum;
	}

	public void setSum(double sum) {
		this.sum = sum;
	}
	
	public boolean isbState() {
		return bState;
	}

	public void setbState(boolean bState) {
		this.bState = bState;
	}
	
	public HashMap<String, List<SelectedMenuObj>> getDicMenusHashtable() {
		return dicMenusHashtable;
	}

	public void setDicMenusHashtable(
			HashMap<String, List<SelectedMenuObj>> dicMenusHashtable) {
		this.dicMenusHashtable = dicMenusHashtable;
	}
	public int getCount() {
		return count;
	}
	public void setCount(int count) {
		this.count = count;
	}
	@Override
	public String toString() {
		// TODO Auto-generated method stub
		return super.toString();
	}
	public void AddMenu(SelectedMenuObj menuinfo)
	{
		String cidString=menuinfo.getCid();
		if(!dicMenusHashtable.containsKey(cidString))
		{
			List<SelectedMenuObj> listmenu=new ArrayList<SelectedMenuObj>();
			dicMenusHashtable.put(cidString, listmenu);
		}
		List<SelectedMenuObj> list=dicMenusHashtable.get(cidString);
		boolean b=false;
		for (SelectedMenuObj selectedMenuObj : list) {
			if(selectedMenuObj.getId().equals(menuinfo.getId()))
			{
				selectedMenuObj.setCount(menuinfo.getCount());
				selectedMenuObj.setCommit(false);
				selectedMenuObj.totalPrice=selectedMenuObj.getCount()*selectedMenuObj.getPrice();
				b=true;
				break;
			}
		}
		if(!b)
		{
			menuinfo.totalPrice=menuinfo.getCount()*menuinfo.getPrice();
			list.add(menuinfo);
		}
		sum+=menuinfo.getPrice();
		count+=1;
		bState=false;
	}
	public void RemoveMenu(SelectedMenuObj menuinfo)
	{
		String cidString=menuinfo.getCid();
		List<SelectedMenuObj> list=dicMenusHashtable.get(cidString);
		for (SelectedMenuObj selectedMenuObj : list) {
			if(selectedMenuObj.getId().equals(menuinfo.getId()))
			{
				if(menuinfo.getCount()==0)
				{
					list.remove(selectedMenuObj);
				}
				else {
					selectedMenuObj=menuinfo;
					selectedMenuObj.totalPrice=selectedMenuObj.getCount()*selectedMenuObj.getPrice();
				}
				break;
			}
		}
		sum-=menuinfo.getPrice();
		count-=1;
		if(sum<=0)
		{
			sum=0;
		}
		if(count<=0)
		{
			count=0;
		}
		bState=false;
	}
	public void DeleteMenu(SelectedMenuObj menuObj)
	{
		String cidString=menuObj.getCid();
		List<SelectedMenuObj> list=dicMenusHashtable.get(cidString);
		list.remove(menuObj);
		if(list.size()<=0)
		{
			dicMenusHashtable.remove(cidString);
		}
		sum-=menuObj.getTotalPrice();
		count-=menuObj.getCount();
		if(sum<=0)
		{
			sum=0;
		}
		if(count<=0)
		{
			count=0;
		}
		bState=false;
	}
	
	public void ChangeOrder(Order order)
	{
		sum=0;
		count=0;
		bState=false;
		strdate="";
		dicMenusHashtable.clear();
		Set<OrderItem> orderSet=order.getOrderItems();
		Iterator<OrderItem> iterator;
		for(iterator=orderSet.iterator();iterator.hasNext();)
		{
			OrderItem oItem=iterator.next();
			String cidString=oItem.getRecipe().getCid().toString();
			if(!dicMenusHashtable.containsKey(cidString))
			{
				List<SelectedMenuObj> listmenu=new ArrayList<SelectedMenuObj>();
				dicMenusHashtable.put(cidString, listmenu);
			}
			List<SelectedMenuObj> list=dicMenusHashtable.get(cidString);
			SelectedMenuObj menuinfo=new SelectedMenuObj(oItem.getRecipe());
			menuinfo.count=oItem.getCount();
			menuinfo.totalPrice=oItem.getCount()*menuinfo.getPrice();
			list.add(menuinfo);
			
			sum+=menuinfo.getTotalPrice();
			count+=menuinfo.getCount();
			bState=false;
		}
		
		
		
	}
}
