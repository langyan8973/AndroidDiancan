package com.mode;

import com.Utils.MenuUtils;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.widget.TextView;

public class TabViewWidget extends TextView{
	private String strTitle;
	private Bitmap backgroundBitmap;
	private Bitmap bmpLogo;
	private Animation scaleAnimation;
	private boolean isStart;

	public TabViewWidget(Context context) {
		super(context);
		// TODO Auto-generated constructor stub
	}
	public void init(String str,Bitmap backBmp,Bitmap bmp,Animation animation)
	{
		strTitle=str;
		bmpLogo=bmp;
		backgroundBitmap=backBmp;
		scaleAnimation=animation;
		isStart=false;
		scaleAnimation.setAnimationListener(new RemoveAnimationListener());
	}
	@Override
	protected void onDraw(Canvas canvas) {
		// TODO Auto-generated method stub
//		canvas.setBitmap(backgroundBitmap);
		if(!isStart)
		{
			Bitmap bmpBitmap=Bitmap.createScaledBitmap(bmpLogo, 40, 40, false);
			canvas.drawBitmap(bmpBitmap, 30, 0, null);
			MenuUtils.Recycled(bmpBitmap);
		}
		else {
			Bitmap bmpBitmap=Bitmap.createScaledBitmap(bmpLogo, 50, 50, false);
			canvas.drawBitmap(bmpBitmap, 25, 0, null);
			MenuUtils.Recycled(bmpBitmap);
		}
	}
	public void StartAnimation()
	{
		isStart=true;
		startAnimation(scaleAnimation);
	}
	//���������ڲ���
		private class RemoveAnimationListener implements AnimationListener{
			//�÷����ڵ���Ч��ִ�н���֮�󱻵��� 
			 @Override
			 public void onAnimationEnd(Animation animation) {
				 isStart=false;
			 }  
			 //�÷����ڶ����ظ�ִ�е�ʱ�����
			 @Override
			 public void onAnimationRepeat(Animation animation) {
			 }  
			 //�÷����ڶ�����ʼִ�е�ʱ�򣬵��� 
			 @Override
			 public void onAnimationStart(Animation animation) {
			 }  
			        
		 }  
}
