package com.diancan.Helper;

import java.io.IOException;
import java.util.Iterator;
import java.util.List;

import android.view.View;

import com.diancan.R;
import com.diancan.SearchPage;
import com.diancan.Utils.FileUtils;
import com.diancan.Utils.JsonUtils;
import com.diancan.Utils.MenuUtils;
import com.diancan.diancanapp.AppDiancan;
import com.diancan.http.HttpCallback;
import com.diancan.http.HttpDownloader;
import com.diancan.http.HttpHandler;
import com.diancan.model.Restaurant;
import com.diancan.model.city;
import com.diancan.model.favorite;

public class RestaurantHttpHelper {
	HttpHandler mHandler;
	AppDiancan appDiancan;
	
	public RestaurantHttpHelper(HttpCallback callback,AppDiancan app){
		appDiancan=app;
		mHandler=new HttpHandler(callback);
	}
	
	/**
	 * 请求餐厅数据
	 */
	public void RequestRestaurants(){
		new Thread(new Runnable() {
			
			@Override
			public void run() {
				// TODO Auto-generated method stub
				
				try {
					List<Restaurant> mRestaurants=MenuUtils.getAllRestaurants(appDiancan.udidString,
							appDiancan.accessToken.getAuthorization(),appDiancan.selectedCity.getId());
					if(mRestaurants==null||mRestaurants.size()==0){
						mHandler.obtainMessage(HttpHandler.RESULT_NULL,appDiancan.getResources().getString(R.string.message_norestaurants)).sendToTarget();
						return;
					}
					mHandler.obtainMessage(HttpHandler.REQUEST_RESTAURANTS,mRestaurants).sendToTarget();
				} catch (Exception e) {
					// TODO Auto-generated catch block
					mHandler.obtainMessage(HttpHandler.REQUEST_ERROR,e.getMessage()).sendToTarget();
				}
				
			}
		}).start();
	}
	
	/**
	 * 请求餐厅数据
	 */
	public void RequestRestaurants(final double x,final double y,final double distance){
		new Thread(new Runnable() {
			@Override
			public void run() {
				// TODO Auto-generated method stub
				try {
					List<Restaurant> mRestaurants=MenuUtils.getAround(appDiancan.udidString,x,y,distance,
							appDiancan.accessToken.getAuthorization(),appDiancan.selectedCity.getId());
					if(mRestaurants==null||mRestaurants.size()==0){
						mHandler.obtainMessage(HttpHandler.REQUEST_ERROR,appDiancan.getResources().getString(R.string.message_norestaurants)).sendToTarget();
						return;
					}
					Iterator<Restaurant> iterator;
					for(iterator=mRestaurants.iterator();iterator.hasNext();){
						Restaurant restaurant=iterator.next();
						if(restaurant.getX()==0){
							mRestaurants.remove(restaurant);
						}
					}
					mHandler.obtainMessage(HttpHandler.REQUEST_RESTAURANTS,mRestaurants).sendToTarget();
				} catch (Exception e) {
					// TODO Auto-generated catch block
					mHandler.obtainMessage(HttpHandler.REQUEST_ERROR,e.getMessage()).sendToTarget();
				}
				
			}
		}).start();
	}
	
	/**
	 * 搜索餐馆
	 * @param keyString
	 */
	public void searchRestaurants(final String keyString){
		new Thread(new Runnable() {
			
			@Override
			public void run() {
				// TODO Auto-generated method stub
				
				try {
					List<Restaurant> mRestaurants=MenuUtils.searchRestaurants(appDiancan.udidString,keyString,
							appDiancan.accessToken.getAuthorization(),appDiancan.selectedCity.getId());
					if(mRestaurants==null||mRestaurants.size()==0){
						mHandler.obtainMessage(HttpHandler.RESULT_NULL,appDiancan.getResources().getString(R.string.message_norestaurants)).sendToTarget();
						return;
					}
					mHandler.obtainMessage(HttpHandler.REQUEST_RESTAURANTS,mRestaurants).sendToTarget();
				} catch (Exception e) {
					// TODO Auto-generated catch block
					mHandler.obtainMessage(HttpHandler.REQUEST_ERROR,e.getMessage()).sendToTarget();
				}
				
			}
		}).start();
	}
	
	/**
	 * 获取城市列表
	 */
	public void getCities(){
  		new Thread(new Runnable() {
			
			@Override
			public void run() {
				// TODO Auto-generated method stub
				String jsonString;
				try {
					jsonString = FileUtils.ReadCity(FileUtils._cityFile.getAbsolutePath());
					List<city> mCities = JsonUtils.parseJsonTocities(jsonString);
					mHandler.obtainMessage(HttpHandler.GET_CITIES,mCities).sendToTarget();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
					mHandler.obtainMessage(HttpHandler.REQUEST_ERROR,e.getMessage()).sendToTarget();
				}
			}
		}).start();
  	}
	
	/**
	 * 请求收藏列表
	 */
	public void RequestFavorites(){
		new Thread(new Runnable() {
			@Override
			public void run() {
				// TODO Auto-generated method stub
				try {
					List<favorite> favorites=MenuUtils.getFavorites(appDiancan.udidString,appDiancan.accessToken.getAuthorization());
					mHandler.obtainMessage(HttpHandler.REQUEST_FAVORITES, favorites).sendToTarget();
					
				} catch (Exception e) {
					// TODO Auto-generated catch block
					mHandler.obtainMessage(HttpHandler.REQUEST_ERROR,e.getMessage()).sendToTarget();
				}
				
			}
		}).start();
	}
	
	/**
	 * 收藏餐馆
	 * @param rid
	 */
	public void favoriteRestaurant(final int rid){
		if(appDiancan.accessToken.getAuthorization().isEmpty()){
			return;
		}
		new Thread(new Runnable() {
			@Override
			public void run() {
				// TODO Auto-generated method stub
				try {
					String rootUrlString = MenuUtils.initUrl;
					String jsonString = HttpDownloader.favoriteRestaurant(rootUrlString, rid, appDiancan.udidString, 
							appDiancan.accessToken.getAuthorization());
					favorite f = JsonUtils.parseJsonToFavorite(jsonString);
					mHandler.obtainMessage(HttpHandler.POST_FAVORITE,f).sendToTarget();
				} catch (Throwable e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
					mHandler.obtainMessage(HttpHandler.REQUEST_ERROR,e.getMessage()).sendToTarget();
				}
				
			}
		}).start();
	}
	
	/**
	 * 取消收藏
	 * @param rid
	 */
	public void deleteFavorite(final int rid){
		new Thread(new Runnable() {
			@Override
			public void run() {
				// TODO Auto-generated method stub
				try {
					String rootUrlString = MenuUtils.initUrl;
					HttpDownloader.deleteFavorite(rootUrlString, rid, appDiancan.udidString, 
							appDiancan.accessToken.getAuthorization());
					mHandler.obtainMessage(HttpHandler.DELETE_FAVORITE,rid).sendToTarget();
				} catch (Throwable e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
					mHandler.obtainMessage(HttpHandler.REQUEST_ERROR,e.getMessage()).sendToTarget();
				}
				
			}
		}).start();
	}
	
}
