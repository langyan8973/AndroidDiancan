package com.Utils;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Environment;

public class FileUtils {
	public static File _dbPathFile;
	public static File _dbFileFile;
	public static File _imgPathFile;
	public static File cacheDir;
	
	private String SDCardRoot;

	public FileUtils() {
		// �õ���ǰ�ⲿ�洢�豸��Ŀ¼
		SDCardRoot = Environment.getExternalStorageDirectory()
				.getAbsolutePath()
				+ File.separator;
	}
//	public static void Createdbfile() {
//		try{
//			_dbPathFile.mkdirs();  
//		 } catch (Exception e) {
//			 // TODO Auto-generated catch block
//			 e.printStackTrace();
//		 }
//		try {
//			_dbFileFile.createNewFile();
//		 } catch (IOException e) {
//			 // TODO Auto-generated catch block
//			 e.printStackTrace();
//		 }
////		 File imgpath=new File(_imgPath);
//		if (!_imgPathFile.exists()) { 
//			 try{
//				 _imgPathFile.mkdirs();  
//			 } catch (Exception e) {
//				 // TODO Auto-generated catch block
//				 e.printStackTrace();
//			 }
//		}
//	}

	/**
	 * ��SD���ϴ����ļ�
	 * 
	 * @throws IOException
	 */
//	public File createFileInSDCard(String fileName, String dir)
//			throws IOException {
//		File file = new File(SDCardRoot + dir + File.separator + fileName);
//		System.out.println("file---->" + file);
//		file.createNewFile();
//		return file;
//	}

	/**
	 * ��SD���ϴ���Ŀ¼
	 * 
	 * @param dirName
	 */
//	public File creatSDDir(String dir) {
//		File dirFile = new File(SDCardRoot + dir + File.separator);
//		System.out.println(dirFile.mkdirs());
//		return dirFile;
//	}

	/**
	 * �ж�SD���ϵ��ļ����Ƿ����?
	 */
//	public boolean isFileExist(String fileName, String path) {
//		File file = new File(SDCardRoot + path + File.separator + fileName);
//		return file.exists();
//	}

	/**
	 * ��һ��InputStream��������д�뵽SD����
	 */
//	public File write2SDFromInput(String path, String fileName,
//			InputStream input) {
//
//		File file = null;
//		OutputStream output = null;
//		try {
//			creatSDDir(path);
//			file = createFileInSDCard(fileName, path);
//			output = new FileOutputStream(file);
//			byte buffer[] = new byte[4 * 1024];
//			int temp;
//			while ((temp = input.read(buffer)) != -1) {
//				output.write(buffer, 0, temp);
//			}
//			output.flush();
//		} catch (Exception e) {
//			e.printStackTrace();
//		} finally {
//			try {
//				output.close();
//			} catch (Exception e) {
//				e.printStackTrace();
//			}
//		}
//		return file;
//	}
	public static void SaveDingDan(Context context,String str) throws IOException
	{
		context.deleteFile("dingdan.txt");
		FileOutputStream outputStream=context.openFileOutput("dingdan.txt", Context.MODE_APPEND);
        outputStream.write(str.getBytes());  
        outputStream.close();
	}
	public static String ReadDingDan(Context context) throws IOException
	{
		FileInputStream inputStream=context.openFileInput("dingdan.txt");  
        ByteArrayOutputStream outStream=new ByteArrayOutputStream();  
        byte[] buffer=new byte[1024];  
        int len=0;  
        while ((len=inputStream.read(buffer))!=-1){  
            outStream.write(buffer, 0, len);  
        }  
        outStream.close();  
        byte[] data=outStream.toByteArray();  
        String jsonstr=new String(data);  
        return jsonstr; 
	}
	public static void SaveHistory(Context context,String str) throws IOException
	{
		context.deleteFile("history.txt");
		FileOutputStream outputStream=context.openFileOutput("history.txt", Context.MODE_APPEND);
        outputStream.write(str.getBytes());  
        outputStream.close();
	}
	public static String ReadHistory(Context context) throws IOException
	{
		FileInputStream inputStream=context.openFileInput("history.txt");  
        ByteArrayOutputStream outStream=new ByteArrayOutputStream();  
        byte[] buffer=new byte[1024];  
        int len=0;  
        while ((len=inputStream.read(buffer))!=-1){  
            outStream.write(buffer, 0, len);  
        }  
        outStream.close();  
        byte[] data=outStream.toByteArray();  
        String jsonstr=new String(data);  
        return jsonstr; 
	}
	
//	public static Bitmap readBitMap(Context context, int resId) throws IOException{  
//		   BitmapFactory.Options opt = new BitmapFactory.Options();  
//		   opt.inPreferredConfig = Bitmap.Config.RGB_565;   
//		   opt.inPurgeable = true;  
//		   opt.inInputShareable = true;  
//		   //获取资源图片  
//		   InputStream is = context.getResources().openRawResource(resId);
//		   Bitmap bmp=BitmapFactory.decodeStream(is,null,opt);
//		   is.close();
//		   return bmp;  
//	}

	/**
	 * ��ȡĿ¼�е�Mp3�ļ������ֺʹ�С
	 */
//	public List<ChiHuoMenuInfo> getMp3Files(String path) {
//		List<ChiHuoMenuInfo> menuInfos = new ArrayList<ChiHuoMenuInfo>();
//		File file = new File(SDCardRoot + File.separator + path);
//		File[] files = file.listFiles();
//		FileUtils fileUtils = new FileUtils();
//		for (int i = 0; i < files.length; i++) {
//			if (files[i].getName().endsWith("mp3")) {
//				Mp3Info mp3Info = new Mp3Info();
//				mp3Info.setMp3Name(files[i].getName());
//				mp3Info.setMp3Size(files[i].length() + "");
//				String temp [] = mp3Info.getMp3Name().split("\\.");
//				String eLrcName = temp[0] + ".lrc";
//				if(fileUtils.isFileExist(eLrcName, "/mp3")){
//					mp3Info.setLrcName(eLrcName);
//				}
//				mp3Infos.add(mp3Info);
//			}
//		}
//		return mp3Infos;
//	}

}