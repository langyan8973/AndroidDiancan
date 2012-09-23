package com.custom;

import android.R.string;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.Paint.Align;
import android.util.AttributeSet;
import android.view.View;

public class RecipeListView extends View {
	String name;
	String price;
	Bitmap imgBitmap;
	Paint  mPaint;
	int index;
	Bitmap duigouBitmap;
	public RecipeListView(Context context, AttributeSet attrs) {
		super(context, attrs);
		// TODO Auto-generated constructor stub
		mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
		mPaint.setTextAlign(Align.LEFT);
		mPaint.setTextSize(24);
		mPaint.setColor(Color.BLACK);
	}
	
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
		postInvalidate();
	}
	public String getPrice() {
		return price;
	}
	public void setPrice(String price) {
		this.price = price;
		postInvalidate();
	}
	public Bitmap getImgBitmap() {
		return imgBitmap;
	}
	public void setImgBitmap(Bitmap imgBitmap) {
		this.imgBitmap = imgBitmap;
		postInvalidate();
	}
	
	public int getIndex() {
		return index;
	}

	public void setIndex(int index) {
		this.index = index;
	}

	public Bitmap getDuigouBitmap() {
		return duigouBitmap;
	}

	public void setDuigouBitmap(Bitmap duigouBitmap) {
		this.duigouBitmap = duigouBitmap;
		postInvalidate();
	}

	@Override
	protected void onDraw(Canvas canvas) {
		// TODO Auto-generated method stub
//		if(name!=null&&!name.isEmpty())
//		{
//			mPaint.setTextSize(24);
//			mPaint.setColor(Color.BLACK);
//			canvas.drawText(name, 105, 30, mPaint);
//		}
//		if(price!=null&&!price.isEmpty())
//		{
//			mPaint.setTextSize(18);
//			mPaint.setColor(Color.RED);
//			canvas.drawText("¥ "+price, 105, 50, mPaint);
//		}
		if(imgBitmap!=null)
		{
			Rect sRect=new Rect(0, 0, imgBitmap.getWidth(), imgBitmap.getHeight());
			Rect tRect=new Rect(5, 5, 95, 95);
			canvas.drawBitmap(imgBitmap, sRect, tRect, null);
		}
		if(duigouBitmap!=null)
		{
			Rect sRect=new Rect(0, 0, duigouBitmap.getWidth(), duigouBitmap.getHeight());
			Rect tRect=new Rect(105, 70, 135, 100);
			canvas.drawBitmap(duigouBitmap, sRect, tRect, null);
		}
	}
	
}
