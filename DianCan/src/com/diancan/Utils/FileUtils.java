package com.diancan.Utils;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import org.apache.http.util.EncodingUtils;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Environment;

public class FileUtils {
	public static File _dbPathFile;
	public static File _dbFileFile;
	public static File _imgPathFile;
	public static File cacheDir;
	public static File _cityFile;
	
	private String SDCardRoot;

	public FileUtils() {
		SDCardRoot = Environment.getExternalStorageDirectory()
				.getAbsolutePath()
				+ File.separator;
	}
	public static void Createdbfile() {
		try{
			_dbPathFile.mkdirs();  
		 } catch (Exception e) {
			 // TODO Auto-generated catch block
			 e.printStackTrace();
		 }
		try {
			_dbFileFile.createNewFile();
		 } catch (IOException e) {
			 // TODO Auto-generated catch block
			 e.printStackTrace();
		 }
	}

	
	public static void SaveCity(String fileName,String str) throws IOException
	{
		try{ 
	        FileOutputStream fout = new FileOutputStream(fileName);
	        byte [] bytes = str.getBytes(); 
	        fout.write(bytes); 
	        fout.close(); 
	        } 
	       catch(Exception e){ 
	        e.printStackTrace(); 
	       } 
	}
	public static String ReadCity(String fileName) throws IOException
	{
		String jsonString=""; 
        try{ 
         FileInputStream fin = new FileInputStream(fileName); 
         int length = fin.available(); 
         byte [] buffer = new byte[length]; 
         fin.read(buffer);     
         jsonString = EncodingUtils.getString(buffer, "UTF-8"); 
         fin.close();     
        } 
        catch(Exception e){ 
         e.printStackTrace(); 
        } 
        return jsonString; 
	}

}