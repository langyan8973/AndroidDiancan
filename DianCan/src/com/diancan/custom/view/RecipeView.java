package com.diancan.custom.view;

import com.diancan.custom.animation.ExpandAnimation;

import android.R.integer;
import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.Transformation;
import android.widget.LinearLayout;

public class RecipeView extends LinearLayout {

	int mBotHeight;
	int mTopHeight;
	int mDuration;
	int mTop;
	int mWidth;
	
	public int getmTop() {
		return mTop;
	}

	public void setmTop(int mTop) {
		this.mTop = mTop;
	}

	public int getmWidth() {
		return mWidth;
	}

	public void setmWidth(int mWidth) {
		this.mWidth = mWidth;
	}

	public int getmBotHeight() {
		return mBotHeight;
	}

	public void setmBotHeight(int mBotHeight) {
		this.mBotHeight = mBotHeight;
	}

	public int getmTopHeight() {
		return mTopHeight;
	}

	public void setmTopHeight(int mTopHeight) {
		this.mTopHeight = mTopHeight;
	}

	public int getmDuration() {
		return mDuration;
	}

	public void setmDuration(int mDuration) {
		this.mDuration = mDuration;
	}
	
	/**
	 * 构造函数
	 * @param context
	 * @param attrs
	 */

	public RecipeView(Context context, AttributeSet attrs) {
		super(context, attrs);
		// TODO Auto-generated constructor stub
		mDuration=200;
	}
	
	/**
	 * 开启单纯的伸展动画
	 */
	public void StartExpandAnimation(){
		ExpandAnimation animation=new ExpandAnimation(this, mTop, mWidth, mTopHeight, mBotHeight, 0);
		animation.setDuration(mDuration);
		animation.setAnimationListener(new AnimationListener() {
			
			@Override
			public void onAnimationStart(Animation animation) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void onAnimationRepeat(Animation animation) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void onAnimationEnd(Animation animation) {
				// TODO Auto-generated method stub
				RecipeView.this.clearAnimation();
			}
		});
		this.startAnimation(animation);
	}
	
	/**
	 * 开启单纯的收缩动画
	 */
	public void StartContractAnimation(){
		ExpandAnimation animation=new ExpandAnimation(this, mTop, mWidth, mTopHeight+mBotHeight, -mBotHeight, 0);
		animation.setDuration(mDuration);
		animation.setAnimationListener(new AnimationListener() {
			
			@Override
			public void onAnimationStart(Animation animation) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void onAnimationRepeat(Animation animation) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void onAnimationEnd(Animation animation) {
				// TODO Auto-generated method stub
				RecipeView.this.clearAnimation();
			}
		});
		this.startAnimation(animation);
	}
	
	/**
	 * 开启单纯的上移动画
	 */
	public void StartUpAnimation(){
		ExpandAnimation animation=new ExpandAnimation(this, mTop, mWidth, mTopHeight, 0, -mBotHeight);
		animation.setDuration(mDuration);
		animation.setAnimationListener(new AnimationListener() {
			
			@Override
			public void onAnimationStart(Animation animation) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void onAnimationRepeat(Animation animation) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void onAnimationEnd(Animation animation) {
				// TODO Auto-generated method stub
				RecipeView.this.clearAnimation();
			}
		});
		this.startAnimation(animation);
	}
	
	/**
	 * 开启单纯的下移动画
	 */
	public void StartDownAnimation(){
		ExpandAnimation animation=new ExpandAnimation(this, mTop, mWidth, mTopHeight, 0, mBotHeight);
		animation.setDuration(mDuration);
		animation.setAnimationListener(new AnimationListener() {
			
			@Override
			public void onAnimationStart(Animation animation) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void onAnimationRepeat(Animation animation) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void onAnimationEnd(Animation animation) {
				// TODO Auto-generated method stub
				RecipeView.this.clearAnimation();
			}
		});
		this.startAnimation(animation);
	}
	
	/**
	 * 开启上移并且展开动画
	 */
	public void StartUpAndExpandAnimation(){
		ExpandAnimation animation=new ExpandAnimation(this, mTop, mWidth, mTopHeight, mBotHeight, -mBotHeight);
		animation.setDuration(mDuration);
		animation.setAnimationListener(new AnimationListener() {
			
			@Override
			public void onAnimationStart(Animation animation) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void onAnimationRepeat(Animation animation) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void onAnimationEnd(Animation animation) {
				// TODO Auto-generated method stub
				RecipeView.this.clearAnimation();
			}
		});
		this.startAnimation(animation);
	}
	
	public void StartDownAndContractAnimation(){
		ExpandAnimation animation=new ExpandAnimation(this, mTop, mWidth, mTopHeight+mBotHeight, -mBotHeight, mBotHeight);
		animation.setDuration(mDuration);
		animation.setAnimationListener(new AnimationListener() {
			
			@Override
			public void onAnimationStart(Animation animation) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void onAnimationRepeat(Animation animation) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void onAnimationEnd(Animation animation) {
				// TODO Auto-generated method stub
				RecipeView.this.clearAnimation();
			}
		});
		this.startAnimation(animation);
	}
}
