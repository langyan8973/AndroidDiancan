package com.diancan.Utils;

import java.lang.reflect.Type;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import android.database.Cursor;
import com.SqlLiteDB.MenuDataHelper;
import com.diancan.http.HttpDownloader;
import com.diancan.model.Category;
import com.diancan.model.HisRestaurant;
import com.diancan.model.History;
import com.diancan.model.Recipe;
import com.diancan.model.Restaurant;
import com.diancan.model.favorite;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

public class MenuUtils {
	public static final String IMAGE_SMALL = "small/";
	public static final String IMAGE_MEDIUM = "medium/";
	public static final String IMAGE_BIG = "big/";
	public static String initUrl;
	public static String imageUrl;
	public static boolean bUpdating = false;
	
	public static String Service_1="加餐具";
	public static String Service_2="加水";
	public static String Service_3="服务员";
	
	/**
	 * 获取所有餐厅
	 * @return
	 */
	public static List<Restaurant> getAllRestaurants(String udid,String Authorization,String citycode){
		String urlString=initUrl+"restaurants?city="+citycode;
		String jsonString=HttpDownloader.getString(urlString,udid,Authorization);
		Type objType=new TypeToken<List<Restaurant>>() {
		}.getType();
		Gson sGson=new GsonBuilder().setDateFormat("yyyy-MM-dd HH:mm:SS")
				.create();
		List<Restaurant> restaurants=sGson.fromJson(jsonString, objType);
		return restaurants;
	}
	/**
	 * 获取周边餐厅
	 * @param udid
	 * @param x
	 * @param y
	 * @return
	 */
	public static List<Restaurant> getAround(String udid,double x,double y,double distance,
			String Authorization,String citycode){
		String urlString=initUrl+"restaurants/around?x="+x+"&y="+y+"&distance="+distance+"&city="+citycode;
		String jsonString=HttpDownloader.getString(urlString,udid,Authorization);
		Type objType=new TypeToken<List<Restaurant>>() {
		}.getType();
		Gson sGson=new GsonBuilder().setDateFormat("yyyy-MM-dd HH:mm:SS")
				.create();
		List<Restaurant> restaurants=sGson.fromJson(jsonString, objType);
		return restaurants;
	}
	
	/**
	 * 搜索餐厅
	 * @param udid
	 * @param strkey
	 * @param Authorization
	 * @param citycode
	 * @return
	 */
	public static List<Restaurant> searchRestaurants(String udid,String strkey,String Authorization,String citycode){
		String urlString=initUrl+"restaurants?city="+citycode+"&name="+strkey;
		String jsonString=HttpDownloader.getString(urlString,udid,Authorization);
		Type objType=new TypeToken<List<Restaurant>>() {
		}.getType();
		Gson sGson=new GsonBuilder().setDateFormat("yyyy-MM-dd HH:mm:SS")
				.create();
		List<Restaurant> restaurants=sGson.fromJson(jsonString, objType);
		return restaurants;
	}

	// 获取所有种类
	public static List<Category> getAllCategory(int rid,String udid,String Authorization) {
		String urlString = initUrl + "restaurants/"+rid+"/categories";
		String jsonStr = HttpDownloader.getString(urlString,udid,Authorization);
		System.out.println(jsonStr);

		Type objType = new TypeToken<List<Category>>() {
		}.getType();
		Gson sGson = new GsonBuilder().setDateFormat("yyyy-MM-dd HH:mm:SS")
				.create();
		List<Category> infos = sGson.fromJson(jsonStr, objType);
		return infos;
	}
	
	public static List<favorite> getFavorites(String udid,String Authorization){
		String urlString = initUrl + "user/favorites";
		String jsonStr = HttpDownloader.getString(urlString,udid,Authorization);
		System.out.println(jsonStr);

		Type objType = new TypeToken<List<favorite>>() {
		}.getType();
		Gson sGson = new GsonBuilder().setDateFormat("yyyy-MM-dd HH:mm:SS")
				.create();
		List<favorite> infos = sGson.fromJson(jsonStr, objType);
		return infos;
	}
	
	// 获取所有城市
	public static String  getAllCities() {
		String urlString = initUrl + "city";
		String jsonStr = HttpDownloader.getString(urlString,"","");

		return jsonStr;
	}

	// 获取某个种类的所有菜
	public static List<Recipe> getRecipesByCategory(Integer id,String udid,String Authorization) {
		String urlString = initUrl + "categories/" + id;

		String jsonStr = HttpDownloader.getString(urlString,udid,Authorization);

		Type objType = new TypeToken<List<Recipe>>() {
		}.getType();
		Gson sGson = new GsonBuilder().setDateFormat("yyyy-MM-dd HH:mm:SS")
				.create();
		List<Recipe> infos = sGson.fromJson(jsonStr, objType);
		return infos;
	}
	
	public static List<Recipe> getAllRecipes(int rid,String udid,String Authorization)
	{
		String urlString = initUrl + "restaurants/"+rid+"/recipes";

		String jsonStr = HttpDownloader.getString(urlString,udid,Authorization);

		Type objType = new TypeToken<List<Recipe>>() {
		}.getType();
		Gson sGson = new GsonBuilder().setDateFormat("yyyy-MM-dd HH:mm:SS")
				.create();
		List<Recipe> infos = sGson.fromJson(jsonStr, objType);
		return infos;
	}
	
	public static List<History> getAllHistories(String udid,String Authorization){
		String urlString = initUrl + "user/history";

		String jsonStr = HttpDownloader.getString(urlString,udid,Authorization);

		Type objType = new TypeToken<List<History>>() {
		}.getType();
		Gson sGson = new GsonBuilder().setDateFormat("yyyy-MM-dd HH:mm:SS")
				.create();
		List<History> infos = sGson.fromJson(jsonStr, objType);
		return infos;
	}

	/**
	 * 获取浏览记录
	 * @return
	 * @throws ParseException 
	 */
	public static List<HisRestaurant> getHisRestaurants() throws ParseException{
		List<HisRestaurant> hisRestaurants = new ArrayList<HisRestaurant>();
		MenuDataHelper.OpenDatabase();
		Cursor cursor=MenuDataHelper.QueryHisRestaurants();
		while(cursor.moveToNext()){
			int rid = cursor.getInt(cursor.getColumnIndex("rid"));
			String rnameString = cursor.getString(cursor.getColumnIndex("rname"));
			String myDate =cursor.getString(cursor.getColumnIndex("time")); 
			String imageString = cursor.getString(cursor.getColumnIndex("image"));
			SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:SS"); 
			Date date = format.parse(myDate);
			HisRestaurant hisRestaurant = new HisRestaurant();
			hisRestaurant.setRid(rid);
			hisRestaurant.setRname(rnameString);
			hisRestaurant.setImage(imageString);
			hisRestaurant.setTime(date);
			hisRestaurants.add(hisRestaurant);
		}
		MenuDataHelper.CloseDatabase();
		return hisRestaurants;
	}
}
