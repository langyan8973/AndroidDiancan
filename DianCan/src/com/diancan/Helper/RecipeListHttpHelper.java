package com.diancan.Helper;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import org.apache.http.client.ClientProtocolException;
import org.json.JSONException;
import org.json.JSONObject;

import android.R.integer;
import android.util.SparseArray;
import android.widget.Toast;

import com.diancan.Utils.JsonUtils;
import com.diancan.Utils.MenuUtils;
import com.diancan.diancanapp.AppDiancan;
import com.diancan.http.HttpCallback;
import com.diancan.http.HttpDownloader;
import com.diancan.http.HttpHandler;
import com.diancan.model.Category;
import com.diancan.model.Order;
import com.diancan.model.OrderItem;
import com.diancan.model.Recipe;

public class RecipeListHttpHelper{
	HttpHandler mHandler;
	AppDiancan appDiancan;
	public RecipeListHttpHelper(HttpCallback callback,AppDiancan app){
		appDiancan=app;
		mHandler=new HttpHandler(callback);
	}
	public void RequestAllTypes(){
		new Thread(new Runnable() {
			
			@Override
			public void run() {
				// TODO Auto-generated method stub
				
				try {
					appDiancan.getMenuListDataObj().categories=MenuUtils.getAllCategory(appDiancan.restaurantId,appDiancan.udidString);
					appDiancan.hashTypes=new HashMap<String, String>();
					Iterator<Category> iterator;
					for(iterator=appDiancan.getMenuListDataObj().categories.iterator();iterator.hasNext();){
						Category category=iterator.next();
						appDiancan.hashTypes.put(category.getId()+"", category.getName());
					}
					mHandler.obtainMessage(HttpHandler.REQUEST_ALLCATEGORY).sendToTarget();
				} catch (Exception e) {
					// TODO Auto-generated catch block
					mHandler.obtainMessage(HttpHandler.REQUEST_ERROR,e.getMessage()).sendToTarget();
				}
				
			}
		}).start();
	}
	
	public void RequestRecipes(){
		new Thread(new Runnable() {
			
			@Override
			public void run() {
				// TODO Auto-generated method stub
				
				try {
					List<Recipe> recipes=MenuUtils.getAllRecipes(appDiancan.restaurantId,appDiancan.udidString);
					if(recipes==null){
						String strnull="获取菜品失败！";
						mHandler.obtainMessage(HttpHandler.REQUEST_ERROR,strnull).sendToTarget();
						return;
					}
					SparseArray<List<OrderItem>> recipeHashMap=appDiancan.getMenuListDataObj().getRecipeMap();
					
					Iterator<Recipe> iterator;
					for(iterator=recipes.iterator();iterator.hasNext();)
					{
						Recipe recipe=iterator.next();
						int cid=recipe.getCid().intValue();
						if(recipeHashMap.indexOfKey(cid)>=0)
						{
							List<OrderItem> orderItems=recipeHashMap.get(cid);
							OrderItem oItem=new OrderItem();
							oItem.setRecipe(recipe);
							oItem.setCount(0);
							orderItems.add(oItem);
						}
						else {
							List<OrderItem> orderItems=new ArrayList<OrderItem>();
							recipeHashMap.put(cid, orderItems);
							OrderItem oItem=new OrderItem();
							oItem.setRecipe(recipe);
							oItem.setCount(0);
							orderItems.add(oItem);
						}
							
					}
					mHandler.obtainMessage(HttpHandler.REQUEST_ALLRECIPES).sendToTarget();
				} catch (Exception e) {
					// TODO Auto-generated catch block
					mHandler.obtainMessage(HttpHandler.REQUEST_ERROR,e.getMessage()).sendToTarget();
				}
				
			}
		}).start();
	}
	
	public void RequestOrderById(){
		new Thread(new Runnable() {
			
			@Override
			public void run() {
				// TODO Auto-generated method stub
				
				try {
					String resultString = HttpDownloader.getString(MenuUtils.initUrl+ "restaurants/"+appDiancan.restaurantId+"/orders/" +appDiancan.curOrder.getId(),
							appDiancan.udidString);
					if(resultString==null)
					{
						mHandler.obtainMessage(HttpHandler.REQUEST_ERROR,"获取订单失败！").sendToTarget();
						return;
					}
					else {
					Order order=JsonUtils.ParseJsonToOrder(resultString);
					appDiancan.curOrder=order;
					mHandler.obtainMessage(HttpHandler.REQUEST_ORDER_BY_ID).sendToTarget();
				}
				} catch (Exception e) {
					// TODO Auto-generated catch block
					mHandler.obtainMessage(HttpHandler.REQUEST_ERROR,e.getMessage()).sendToTarget();
				}
				
			}
		}).start();
	}
	
	public void Diancai(final int iid,final int count){
		new Thread(){
            public void run(){
            	//加减菜
        		JSONObject object = new JSONObject();
        		try {
        			object.put("rid", iid);
        			object.put("count", count);
        		} catch (JSONException e) {
        		}		
        		try {
        			String resultString = HttpDownloader.alterRecipeCount(MenuUtils.initUrl, appDiancan.curOrder.getId(),
        					appDiancan.restaurantId, object,appDiancan.udidString);
        			mHandler.obtainMessage(HttpHandler.POST_RECIPE_COUNT,resultString).sendToTarget();
        		} catch (ClientProtocolException e) {
        		} catch (JSONException e) {
        		} catch (IOException e) {
        		} catch (Throwable e) {
        			e.printStackTrace();
        			mHandler.obtainMessage(HttpHandler.REQUEST_ERROR,e.getMessage()).sendToTarget();
        		}
            }
        }.start();
	}
	
	public void RefreshOrder(){
		new Thread(new Runnable() {
			
			@Override
			public void run() {
				// TODO Auto-generated method stub
				try {
					String jsonString = HttpDownloader.getString(MenuUtils.initUrl+ "restaurants/"+appDiancan.restaurantId+"/orders/" +appDiancan.curOrder.getId(),
							appDiancan.udidString);
					mHandler.obtainMessage(HttpHandler.REFRESH_ORDER,jsonString).sendToTarget();
				} catch (Throwable e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
					mHandler.obtainMessage(HttpHandler.REQUEST_ERROR,e.getMessage()).sendToTarget();
				}
				
			}
		}).start();
	}
	
	public void CheckOrder(){
		new Thread(new Runnable() {
			
			@Override
			public void run() {
				// TODO Auto-generated method stub
				try {
					String urlString=MenuUtils.initUrl+"restaurants/"+appDiancan.restaurantId+"/orders/"+appDiancan.curOrder.getId()+"/tocheck";
					String jsString = HttpDownloader.RequestFinally(urlString,appDiancan.udidString);
					mHandler.obtainMessage(HttpHandler.CHECK_ORDER,jsString).sendToTarget();
				} catch (ClientProtocolException e) {
					// TODO Auto-generated catch block
					mHandler.obtainMessage(HttpHandler.REQUEST_ERROR,e.getMessage()).sendToTarget();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					mHandler.obtainMessage(HttpHandler.REQUEST_ERROR,e.getMessage()).sendToTarget();
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					mHandler.obtainMessage(HttpHandler.REQUEST_ERROR,e.getMessage()).sendToTarget();
				} catch (Throwable e) {
					// TODO Auto-generated catch block
					mHandler.obtainMessage(HttpHandler.REQUEST_ERROR,e.getMessage()).sendToTarget();
				}
			}
		}).start();
	}
}
