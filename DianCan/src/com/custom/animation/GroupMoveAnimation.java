package com.custom.animation;

import com.custom.view.MyViewGroup;

import android.view.View;
import android.view.animation.Animation;
import android.view.animation.Transformation;

public class GroupMoveAnimation extends Animation {
	int mDuration;
	int mFromX;
	int mToX;
	View mView;
	int mWidth;
	int mHeight;
	int mDeltaX;
	public GroupMoveAnimation(int duration,int fromx,int tox,View view,int width,int height){
		mDuration=duration;
		mFromX=fromx;
		mToX=tox;
		mView=view;
		mWidth=width;
		mHeight=height;
	}
	@Override
	protected void applyTransformation(float interpolatedTime, Transformation t) {
		// TODO Auto-generated method stub
		super.applyTransformation(interpolatedTime, t);
		float dx;
		if(mDeltaX>0)
		{
			dx=mDeltaX*interpolatedTime;
			
		}
		else{
			dx=mFromX+mDeltaX*interpolatedTime;
		}
		mView.layout((int)dx, 0, mWidth, mHeight);
		MyViewGroup myViewGroup=(MyViewGroup)mView;
		myViewGroup.setmLeft((int)dx);
	}
	@Override
	public void initialize(int width, int height, int parentWidth,
			int parentHeight) {
		// TODO Auto-generated method stub
		super.initialize(width, height, parentWidth, parentHeight);
		this.setDuration(mDuration);
		mDeltaX=mToX-mFromX;
	}
	
	
	
}
