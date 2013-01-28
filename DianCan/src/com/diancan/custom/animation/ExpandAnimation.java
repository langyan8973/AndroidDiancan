package com.diancan.custom.animation;

import com.diancan.custom.view.RecipeView;

import android.R.integer;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.Transformation;

public class ExpandAnimation extends Animation {

	int mTop;
	int mWidth;
	int mHeight;
	View mView;
	int mChangeHeight;
	int mChangeTop;
	public ExpandAnimation(View view,int top,int width,int height,int changeheight,int changetop){
		mView=view;
		mTop=top;
		mWidth=width;
		mHeight=height;
		mChangeHeight=changeheight;
		mChangeTop=changetop;
	}
	@Override
	protected void applyTransformation(float interpolatedTime, Transformation t) {
		// TODO Auto-generated method stub
		super.applyTransformation(interpolatedTime, t);
		int deltay=(int)(interpolatedTime*mChangeHeight);
		int deltatop=(int)(interpolatedTime*mChangeTop);
		mView.layout(0, mTop+deltatop, mWidth, mTop+deltatop+mHeight+deltay);
	}

}
