package com.diancan.custom.animation;

import android.R.integer;
import android.graphics.Matrix;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.Animation;
import android.view.animation.Transformation;

public class ListPopImgAnimation extends Animation {
	int halfWidth;
	int halfHeight;
	double a,b,c;
	int duration;
	int x1,y1,x2,y2,x3,y3;
	private float mFromAlpha;
	private float mToAlpha;
	public ListPopImgAnimation(int duration,int x1,int y1, int x2, int y2, int x3, int y3)
	{
		this.duration=duration;
		this.x1=x1;
		this.y1=y1;
		this.x2=x2;
		this.y2=y2;
		this.x3=x3;
		this.y3=y3;
		mFromAlpha=1.0f;
		mToAlpha=0;
	}
	@Override
	protected void applyTransformation(float interpolatedTime, Transformation t) {
		// TODO Auto-generated method stub
		super.applyTransformation(interpolatedTime, t);
//		final float alpha = mFromAlpha;
		
		Matrix matrix=t.getMatrix();
		if (interpolatedTime>=0&&interpolatedTime<0.5) {
			matrix.preScale(interpolatedTime * 60, interpolatedTime * 60,halfWidth, halfHeight);
			float x = x3 * interpolatedTime;
			float y=(float)((a*x*x)+(b*x)+c);
			matrix.postTranslate(x, y);
		}
		else {
			if(interpolatedTime<0.95)
			{
				matrix.preScale(30, 30,halfWidth, halfHeight);
				float x = x3 * interpolatedTime;
				float y=(float)((a*x*x)+(b*x)+c);
				matrix.postTranslate(x, y);
			}
			else {
				matrix.preScale(0, 0,halfWidth, halfHeight);
				float x = x3 * interpolatedTime;
				float y=(float)((a*x*x)+(b*x)+c);
				matrix.postTranslate(x, y);
			}
			
		}
	}

	@Override
	public void initialize(int width, int height, int parentWidth,
			int parentHeight) {
		// TODO Auto-generated method stub
		super.initialize(width, height, parentWidth, parentHeight);
		this.halfWidth=width/2;
        this.halfHeight=height/2;
		this.setDuration(duration);
        this.setInterpolator(new AccelerateDecelerateInterpolator());
        getFC(x1, y1, x2, y2, x3, y3);
	}
	public void getFC(double x1,double y1,double x2,double y2,double x3,double y3)
	{
		b = ((y1-y3)*(x1*x1-x2*x2)-(y1-y2)*(x1*x1-x3*x3))/((x1-x3)*(x1*x1-x2*x2)-(x1-x2)*(x1*x1-x3*x3));
		a = ((y1-y2)-b*(x1-x2))/(x1*x1-x2*x2);
		c = y1-a*x1*x1-b*x1;
	}
}
