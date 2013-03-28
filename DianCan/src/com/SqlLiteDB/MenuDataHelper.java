package com.SqlLiteDB;

import java.text.SimpleDateFormat;
import java.util.Date;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.diancan.Utils.FileUtils;
import com.diancan.model.Category;
import com.diancan.model.Desk;
import com.diancan.model.HisRestaurant;
import com.diancan.model.Recipe;
public class MenuDataHelper {
	
	public static SQLiteDatabase _menusdb;

	public static void OpenDatabase()
	{
		_menusdb = SQLiteDatabase.openOrCreateDatabase(FileUtils._dbFileFile, null);
	}
	
	public static void CloseDatabase()
	{
		_menusdb.close();
	}
	public static boolean IsOpened()
	{
		return _menusdb.isOpen();
	}
	public static boolean CreateMenusTable()
	{
		try{
			_menusdb.execSQL("create table histories(id INTEGER PRIMARY KEY,rid number,rname varchar(40),image varchar(60),time DATETIME DEFAULT CURRENT_TIMESTAMP)");
			return true;
		}catch(Exception e)
		{
			e.printStackTrace();
			return false;
		}
	}
	
	public static void InsertMenu(Recipe menuInfo)
	{
		ContentValues values = new ContentValues();

		values.put("id", menuInfo.getId());
		values.put("cid", menuInfo.getCid());
		values.put("name",menuInfo.getName());
		values.put("description", menuInfo.getDescription());
		values.put("price", menuInfo.getPrice());
		values.put("image", menuInfo.getImage());

		_menusdb.insert("recipe", null, values);
	}
	
	public static void DeleteMenu(){
		_menusdb.execSQL("delete from recipe");
	}
	public static void insertHisRestaurant(HisRestaurant hisRestaurant){
		Cursor cursor =_menusdb.rawQuery("SELECT * FROM histories where rid="+hisRestaurant.getRid(), null);
		boolean isFind = false;
		if(cursor.moveToNext()){
			isFind = true;
		}
		cursor.close();
		
		if(isFind){
			String where="rid=?";
	        String[] whereValue={String.valueOf(hisRestaurant.getRid())};
	        ContentValues cv=new ContentValues(); 
	        cv.put("rid", hisRestaurant.getRid());
			cv.put("rname",hisRestaurant.getRname());
			cv.put("image", hisRestaurant.getImage());
			SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			String s = dateFormat.format(hisRestaurant.getTime());
			cv.put("time",s );
	        _menusdb.update("histories", cv, where, whereValue);
		}
		else{
			ContentValues values = new ContentValues();

			values.put("rid", hisRestaurant.getRid());
			values.put("rname",hisRestaurant.getRname());
			values.put("image", hisRestaurant.getImage());
			SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			String s = dateFormat.format(hisRestaurant.getTime());
			values.put("time",s );
			_menusdb.insert("histories", null, values);
		}
		
	}
	
	public static Cursor QueryMenus()
	{
		Cursor cursor =_menusdb.rawQuery("SELECT * FROM recipe", null);
		return cursor;
	}
	
	public static Cursor QueryHisRestaurants(){
		Cursor cursor =_menusdb.rawQuery("SELECT * FROM histories order by time desc", null);
		return cursor;
	}
	
	
	public static Cursor QueryMenusByCid(int cid)
	{
		Cursor cursor =_menusdb.rawQuery("SELECT * FROM recipe where cid='"+cid+"'", null);
		return cursor;
	}
	public static Cursor QueryMenusById(int id)
	{
		Cursor cursor =_menusdb.rawQuery("SELECT * FROM recipe where id='"+id+"'", null);
		return cursor;
	}
    public static void updaterecipe(Recipe menuInfo)
    {
        String where="id='?'";
        String[] whereValue={menuInfo.getId().toString()};
        ContentValues cv=new ContentValues(); 
        cv.put("cid", menuInfo.getCid());
        cv.put("name",menuInfo.getName());
        cv.put("description", menuInfo.getDescription());
        cv.put("price", menuInfo.getPrice());
        cv.put("image", menuInfo.getImage());
        _menusdb.update("recipe", cv, where, whereValue);
    }
}
