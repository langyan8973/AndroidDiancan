package com.diancan.Utils;

import java.io.InputStream;

import android.R;
import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.NinePatchDrawable;
import android.graphics.drawable.StateListDrawable;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

public class BitmapUtil {
	
	private static final String LOGTAG = "AndroidUtil";
	
	/**
	 * 把View绘制到Bitmap上
	 * @param view 需要绘制的View
	 * @param width 该View的宽度
	 * @param height 该View的高度
	 * @return 返回Bitmap对象
	 */
	public static Bitmap getBitmapFromView(View view,int width,int height) {
		int widthSpec = View.MeasureSpec.makeMeasureSpec(width, View.MeasureSpec.EXACTLY);
		int heightSpec = View.MeasureSpec.makeMeasureSpec(height, View.MeasureSpec.EXACTLY);
		view.measure(widthSpec, heightSpec);
		view.layout(0, 0, width, height);
		Bitmap bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.RGB_565);
		Canvas canvas = new Canvas(bitmap);
		view.draw(canvas);
		
		return bitmap;
	}
	
	/**
	 * 去除标题
	 * @param Activity act
	 */
	public static void setFullNoTitleScreen(Activity act) {
		act.setTheme(R.style.Theme_Black_NoTitleBar_Fullscreen);
		act.requestWindowFeature(Window.FEATURE_NO_TITLE);
		act.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
				WindowManager.LayoutParams.FLAG_FULLSCREEN);
	}
	
	/**
	 * 释放bmp资源
	 * @param bmp
	 */
	public static void recycleBmp(Bitmap bmp){
		if(null != bmp && !bmp.isRecycled()){
			bmp.recycle();
			Log.d(LOGTAG, "=======recycleBmp ======");
		}
	}
	
	/**
	 * 
	 * @param context
	 * @return
	 */
	public static DisplayMetrics getDisplayMetrics(Context context) {
		return context.getApplicationContext().getResources().getDisplayMetrics();
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
	
	public static void destroyDrawable(View view){
		Drawable drawable  = view.getBackground();
		drawable.setCallback(null);
//		if(drawable instanceof StateListDrawable){
//			StateListDrawable sDrawable = (StateListDrawable)drawable;
//			Drawable cDrawable = sDrawable.getCurrent();
//			if(cDrawable instanceof BitmapDrawable){
//				BitmapDrawable bitmapDrawable = (BitmapDrawable)cDrawable;
//				bitmapDrawable.setCallback(null);
//			}
//			sDrawable.setCallback(null);
//		}
//		else if(drawable instanceof BitmapDrawable){
//			BitmapDrawable bDrawable = (BitmapDrawable)drawable;
//			bDrawable.setCallback(null);
//			bDrawable.getBitmap().recycle();
//		}
//		else if(drawable instanceof NinePatchDrawable){
//			NinePatchDrawable nDrawable = (NinePatchDrawable)drawable;
//			Drawable cDrawable = nDrawable.getCurrent();
//			if(cDrawable instanceof BitmapDrawable){
//				BitmapDrawable bitmapDrawable = (BitmapDrawable)cDrawable;
//				bitmapDrawable.setCallback(null);
//				bitmapDrawable.getBitmap().recycle();
//			}
//			nDrawable.setCallback(null);
//		}
		System.gc();
	}
	
}
