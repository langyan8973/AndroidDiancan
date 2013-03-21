package com.SqlLiteDB;

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
			_menusdb.execSQL("create table histories(id number,rid number,rname varchar(40),time varchar(40))");
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
	
	public static void InsertCategory(Category category){
		ContentValues values = new ContentValues();

		values.put("id", category.getId());
		values.put("name",category.getName());
		values.put("image", category.getImage());
		values.put("description", category.getDescription());

		_menusdb.insert("category", null, values);
	}
	
	public static void insertHisRestaurant(HisRestaurant hisRestaurant){
		ContentValues values = new ContentValues();

		values.put("rid", hisRestaurant.getRid());
		values.put("rname",hisRestaurant.getRname());
		values.put("time", hisRestaurant.getTime());

		_menusdb.insert("histories", null, values);
	}
	
	public static void DeleteCategory(){
		try {
			_menusdb.execSQL("delete from category");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public static void InsertDesk(Desk desk){
		ContentValues values = new ContentValues();

		values.put("id", desk.getId());
		values.put("name",desk.getName());
		values.put("capacity",desk.getCapacity());
		values.put("description", desk.getDescription());

		_menusdb.insert("desk", null, values);
	}
	
	public static void InsertUpdateTime(String strtime)
	{
		ContentValues values = new ContentValues();

		values.put("time", strtime);

		_menusdb.insert("updatetime", null, values);
	}
	
	public static void DeleteDesk(){
		try {
			_menusdb.execSQL("delete from desk");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public static Cursor QueryMenus()
	{
		Cursor cursor =_menusdb.rawQuery("SELECT * FROM recipe", null);
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
	public static Cursor QueryCategorysById(int id)
	{
		Cursor cursor =_menusdb.rawQuery("SELECT * FROM category where id='"+id+"'", null);
		return cursor;
	}
	public static Cursor QueryCategory()
	{
		Cursor cursor =_menusdb.rawQuery("SELECT * FROM category", null);
		return cursor;
	}
	public static Cursor QueryDesk()
	{
		Cursor cursor =_menusdb.rawQuery("SELECT * FROM desk", null);
		return cursor;
	}
	public static Cursor QuetyUpdateTime()
	{
		Cursor cursor =_menusdb.rawQuery("SELECT * FROM updatetime", null);
		return cursor;
	}
	
	public static void deletefromrecipe(String id)
    {
        String where="id='?'";
        String[] whereValue={id};
        _menusdb.delete("recipe", where, whereValue);
    }
	public static void deletefromdesk(String id)
    {
        String where="id='?'";
        String[] whereValue={id};
        _menusdb.delete("desk", where, whereValue);
    }
	public static void deletefromcategory(String id)
    {
        String where="id='?'";
        String[] whereValue={id};
        _menusdb.delete("category", where, whereValue);
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
    public static void updatecateDesk(Desk desk)
    {
        String where="id='?'";
        String[] whereValue={desk.getId().toString()};
        ContentValues cv=new ContentValues(); 
        cv.put("name",desk.getName());
        cv.put("capacity",desk.getCapacity());
        cv.put("description", desk.getDescription());
        _menusdb.update("desk", cv, where, whereValue);
    }
    public static void updatecategory(Category category)
    {
        String where="id='?'";
        String[] whereValue={category.getId().toString()};
        ContentValues cv=new ContentValues(); 
        cv.put("name",category.getName());
        cv.put("image", category.getImage());
        cv.put("description", category.getDescription());
        _menusdb.update("category", cv, where, whereValue);
    }
}
