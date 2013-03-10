package com.diancan.Utils;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.lang.reflect.Type;
import java.util.List;

import android.R.integer;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import com.SqlLiteDB.MenuDataHelper;
import com.diancan.http.HttpDownloader;
import com.diancan.model.AllDomain;
import com.diancan.model.Category;
import com.diancan.model.Desk;
import com.diancan.model.DeskType;
import com.diancan.model.Recipe;
import com.diancan.model.Restaurant;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

public class MenuUtils {
	public static final int CMD_STOP_SERVICE = 0;
	public static final String UPDATE_COMPLETE = "updatecomplete";
	public static final String DOWNLOAD_COMPLETE = "downloadcomplete";
	public static final String UPDATE_SERVICE = "com.chihuo.UpdateService";
	public static final String IMAGE_SMALL = "small/";
	public static final String IMAGE_MEDIUM = "medium/";
	public static final String IMAGE_BIG = "big/";
	public static String initUrl;
	public static String imageUrl;
	public static boolean bUpdating = false;
	public static String XIAOMI="Xiaomi";
	public static String MIONE="MI-ONE Plus";
	public static boolean ISXIAOMI;
	
	public static String Service_1="加餐具";
	public static String Service_2="加水";
	public static String Service_3="服务员";
	
	/**
	 * 获取所有餐厅
	 * @return
	 */
	public static List<Restaurant> getAllRestaurants(String udid){
		String urlString=initUrl+"restaurants";
		String jsonString=HttpDownloader.getString(urlString,udid);
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
	public static List<Restaurant> getAround(String udid,double x,double y,double distance){
		String urlString=initUrl+"restaurants/around?x="+x+"&y="+y+"&distance="+distance;
		String jsonString=HttpDownloader.getString(urlString,udid);
		Type objType=new TypeToken<List<Restaurant>>() {
		}.getType();
		Gson sGson=new GsonBuilder().setDateFormat("yyyy-MM-dd HH:mm:SS")
				.create();
		List<Restaurant> restaurants=sGson.fromJson(jsonString, objType);
		return restaurants;
	}

	// 获取所有种类
	public static List<Category> getAllCategory(int rid,String udid) {
		String urlString = initUrl + "restaurants/"+rid+"/categories";
		String jsonStr = HttpDownloader.getString(urlString,udid);
		System.out.println(jsonStr);

		Type objType = new TypeToken<List<Category>>() {
		}.getType();
		Gson sGson = new GsonBuilder().setDateFormat("yyyy-MM-dd HH:mm:SS")
				.create();
		List<Category> infos = sGson.fromJson(jsonStr, objType);
		return infos;
	}

	// 获取某个种类的所有菜
	public static List<Recipe> getRecipesByCategory(Integer id,String udid) {
		String urlString = initUrl + "categories/" + id;

		String jsonStr = HttpDownloader.getString(urlString,udid);

		Type objType = new TypeToken<List<Recipe>>() {
		}.getType();
		Gson sGson = new GsonBuilder().setDateFormat("yyyy-MM-dd HH:mm:SS")
				.create();
		List<Recipe> infos = sGson.fromJson(jsonStr, objType);
		return infos;
	}
	
	public static List<Recipe> getAllRecipes(int rid,String udid)
	{
		String urlString = initUrl + "restaurants/"+rid+"/recipes";

		String jsonStr = HttpDownloader.getString(urlString,udid);

		Type objType = new TypeToken<List<Recipe>>() {
		}.getType();
		Gson sGson = new GsonBuilder().setDateFormat("yyyy-MM-dd HH:mm:SS")
				.create();
		List<Recipe> infos = sGson.fromJson(jsonStr, objType);
		return infos;
	}

	// 获取所有桌子
	public static List<Desk> getAllDesks(String udid) {
		String urlString = initUrl + "desks";

		String jsonStr = HttpDownloader.getString(urlString,udid);

		Type objType = new TypeToken<List<Desk>>() {
		}.getType();
		Gson sGson = new GsonBuilder().setDateFormat("yyyy-MM-dd HH:mm:SS")
				.create();
		List<Desk> infos = sGson.fromJson(jsonStr, objType);
		return infos;
	}
	//获取桌子分类
	public static List<DeskType> getDeskTypes(String udid)
	{
		String urlString = initUrl + "desktypes";

		String jsonStr = HttpDownloader.getString(urlString,udid);

		Type objType = new TypeToken<List<DeskType>>() {
		}.getType();
		Gson sGson = new GsonBuilder().setDateFormat("yyyy-MM-dd HH:mm:SS")
				.create();
		List<DeskType> infos = sGson.fromJson(jsonStr, objType);
		return infos;
	}
	//获取分类桌子
	public static List<Desk> getDesksByTid(int id,String udid)
	{
		String urlString = initUrl + "desktypes/"+id;

		String jsonStr = HttpDownloader.getString(urlString,udid);

		Type objType = new TypeToken<List<Desk>>() {
		}.getType();
		Gson sGson = new GsonBuilder().setDateFormat("yyyy-MM-dd HH:mm:SS")
				.create();
		List<Desk> infos = sGson.fromJson(jsonStr, objType);
		return infos;
	}

	public static AllDomain DownloadMenusData(String strdate,String udid) {
		String urlString = initUrl + "all";
		if (strdate != "") {
			urlString += "/" + strdate;
		}
		String jsonStr = HttpDownloader.getString(urlString,udid);
		// System.out.println(jsonStr);

		Type objType = new TypeToken<AllDomain>() {
		}.getType();
		Gson sGson = new GsonBuilder().setDateFormat("yyyy-MM-dd HH:mm:SS")
				.create();
		AllDomain infos = sGson.fromJson(jsonStr, objType);
		return infos;
	}

	public static void SaveCatagoryData(List<Category> list) {
		for (Category category : list) {
			MenuDataHelper.InsertCategory(category);
		}
	}

	public static void SaveDeskData(List<Desk> list) {
		for (Desk category : list) {
			MenuDataHelper.InsertDesk(category);
		}
	}

	public static void SaveMenuDatas(List<Recipe> infos) {
		for (final Recipe recipe : infos) {
			// executorService.submit(new Runnable() {
			// public void run() {
			// handler.post(new Runnable() {
			// public void run() {
			SaveMenuObj(recipe);
			// }
			// });
			// }
			// });
		}
	}

	public static void SaveMenuObj(Recipe recipe) {
		String filename = FileUtils._imgPathFile.getPath() + "/"
				+ recipe.getImage();
		File imgfile = new File(filename);
		if (imgfile.exists()) {
			imgfile.delete();
		}
		if (!imgfile.exists()) {
			Bitmap bmp = HttpDownloader.getStream(initUrl + "recipes/"
					+ recipe.getId());

			try {
				FileOutputStream out = new FileOutputStream(filename);
				bmp.compress(Bitmap.CompressFormat.PNG, 90, out);
			} catch (FileNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

		// recipe.setImffage(filename);
		MenuDataHelper.InsertMenu(recipe);
	}

	public static void DeleteMenuObj(int id) {
		Cursor cursor = MenuDataHelper.QueryMenusById(id);
		String filename = "";
		if (cursor.moveToNext()) {
			String imgString = cursor.getString(cursor.getColumnIndex("image"));
			filename = FileUtils._imgPathFile.getPath() + "/" + imgString;
			File imgfile = new File(filename);
			if (imgfile.exists()) {
				imgfile.delete();
			}
		}
		cursor.close();
		MenuDataHelper.deletefromrecipe(id + "");
	}

	public static void UpdateMenuObj(Recipe recipe) {
		Cursor cursor = MenuDataHelper.QueryMenusById(recipe.getId());
		String filename = "";
		if (cursor.moveToNext()) {
			String imgString = cursor.getString(cursor.getColumnIndex("image"));
			filename = FileUtils._imgPathFile.getPath() + "/" + imgString;
			File imgfile = new File(filename);
			if (imgfile.exists()) {
				imgfile.delete();
			}
		}
		cursor.close();
		filename = FileUtils._imgPathFile.getPath() + "/" + recipe.getImage();
		File imgfile = new File(filename);
		if (imgfile.exists()) {
			imgfile.delete();
		}
		if (!imgfile.exists()) {
			Bitmap bmp = HttpDownloader.getStream(initUrl + "recipes/"
					+ recipe.getId());

			try {
				FileOutputStream out = new FileOutputStream(filename);
				bmp.compress(Bitmap.CompressFormat.PNG, 90, out);
			} catch (FileNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

		MenuDataHelper.updaterecipe(recipe);
	}




	/**
	   * 以最省内存的方式读取本地资源的图片
	   * @param context
	   * @param resId
	   * @return
	   */  
	public static Bitmap readBitMap(Context context, int resId,int size){  
	   BitmapFactory.Options opt = new BitmapFactory.Options();  
	   opt.inPreferredConfig = Bitmap.Config.RGB_565;   
	   opt.inPurgeable = true;  
	   opt.inInputShareable = true;  
	   opt.inSampleSize=size;
	   //获取资源图片  
	   InputStream is = context.getResources().openRawResource(resId);  
	   return BitmapFactory.decodeStream(is,null,opt);  
	 }
	public static void Recycled(Bitmap bmp)
	{
		if(!bmp.isRecycled() ){
			bmp.recycle();   //回收图片所占的内存
	         System.gc();    //提醒系统及时回收
		}
	}
}
