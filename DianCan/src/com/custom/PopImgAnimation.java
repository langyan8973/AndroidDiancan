package com.custom;

import android.R.integer;
import android.graphics.Camera;
import android.graphics.Matrix;
import android.provider.ContactsContract.CommonDataKinds.Im;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.Animation;
import android.view.animation.Transformation;

public class PopImgAnimation extends Animation {
	int halfWidth;
	int halfHeight;
	int sWidth;
	int sHeight;
	int duration;
	double a,b,c;
	int x1,y1,x2,y2,x3,y3;
	
	public PopImgAnimation(int dur,int width,int height)
	{
		duration=dur;
		sWidth=width;
		sHeight=height;
	}
	@Override   
    public void initialize(int width, int height, int parentWidth,   
            int parentHeight) {   
        super.initialize(width, height, parentWidth, parentHeight);  
        this.halfWidth=width/2;
        this.halfHeight=height/2;
        this.setDuration(duration);
        this.setInterpolator(new AccelerateDecelerateInterpolator());
        x3=sWidth/4+20;
        x2=0;
        x1=-(x3/3);
        y3=0;
        y2=-sHeight/2;
        y1=y3;
        getFC(x1, y1, x2, y2, x3, y3);
    }
	@Override
	protected void applyTransformation(float interpolatedTime, Transformation t) {
		// TODO Auto-generated method stub
		super.applyTransformation(interpolatedTime, t);
		Matrix matrix=t.getMatrix();
		if (interpolatedTime>=0&&interpolatedTime<0.5) {
			matrix.preScale(interpolatedTime * 80, interpolatedTime * 80,
					halfWidth, halfHeight);
			float x = x3 * interpolatedTime;
			float y=(float)((a*x*x)+(b*x)+c);
			matrix.postTranslate(x, y);
		}
		else {
			if(interpolatedTime<0.95)
			{
				matrix.preScale(40, 40,
						halfWidth, halfHeight);
				float x = x3 * interpolatedTime;
				float y=(float)((a*x*x)+(b*x)+c);
				matrix.postTranslate(x, y);
			}
			else {
				matrix.preScale(0, 0,
						halfWidth, halfHeight);
				float x = x3 * interpolatedTime;
				float y=(float)((a*x*x)+(b*x)+c);
				matrix.postTranslate(x, y);
			}
		}
	}
	public void getFC(double x1,double y1,double x2,double y2,double x3,double y3)
	{
		b = ((y1-y3)*(x1*x1-x2*x2)-(y1-y2)*(x1*x1-x3*x3))/((x1-x3)*(x1*x1-x2*x2)-(x1-x2)*(x1*x1-x3*x3));
		a = ((y1-y2)-b*(x1-x2))/(x1*x1-x2*x2);
		c = y1-a*x1*x1-b*x1;
	}
}
