package com.diancan.custom.view;

import com.diancan.R;
import com.diancan.Utils.DisplayUtil;
import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.TranslateAnimation;
import android.widget.FrameLayout;
import android.widget.LinearLayout;

public class RecipeFrameLayout extends FrameLayout {
	LinearLayout listLayout;
	PinnedHeaderListView mListView;
	float mDownY;
    float mDownX;
    float mUpX;
    float mParentX;
    float mStartX;
    float mEndX;
    static int STARTX;
    Boolean isFirst;
    Boolean isLR;
    Context mContext;
    int sWidth,sHeight;
    int mDuration;
	public RecipeFrameLayout(Context context, AttributeSet attrs) {
		super(context, attrs);
		// TODO Auto-generated constructor stub
		mContext = context;
		mParentX = 0;
		STARTX = -(int)mContext.getResources().getDimension(R.dimen.left_shadow_width);
		mEndX = mContext.getResources().getDimension(R.dimen.recipelist_move_end);
		mDuration = 200;
		mStartX = STARTX;
		int topheight = (int)mContext.getResources().getDimension(R.dimen.topbar_height);
		int bottomheight = (int)mContext.getResources().getDimension(R.dimen.tabbar_height);
		sWidth = DisplayUtil.PIXWIDTH;
		sHeight = DisplayUtil.PIXHEIGHT - topheight - bottomheight;
	}

	
	
	@Override
	protected void onLayout(boolean changed, int l, int t, int r, int b) {
		// TODO Auto-generated method stub
//		super.onLayout(changed, l, t, r, b);
		View c1=this.getChildAt(0);
		float dd=mContext.getResources().getDimension(R.dimen.categorylist_width);
		int cr=(int)dd;
		c1.layout(0, 0, cr, sHeight);
		
		View c2=this.getChildAt(1);
		if(mStartX<0){
			c2.layout((int)mStartX, 0, sWidth, sHeight);
		}
		else {
			c2.layout((int)mStartX, 0, (int)Math.abs(mStartX)+sWidth, sHeight);
		}
		
		if(mListView==null){
			LinearLayout layout = (LinearLayout)c2;
			mListView = (PinnedHeaderListView)layout.getChildAt(0);
		}
		
		View c3 = this.getChildAt(2);
		if(c3.getVisibility()!=View.GONE){
			c3.layout(0, 0, 1, 1);
		}
	}



	@Override
	public boolean onInterceptTouchEvent(MotionEvent ev) {
		// TODO Auto-generated method stub
		final float x = ev.getX();
		final float y = ev.getY(); 
		switch (ev.getAction())  
        {  
        case MotionEvent.ACTION_DOWN: 
        	mDownX = x;
        	mDownY = y; 
        	isFirst = true;
        	isLR = false;
            break;  
        case MotionEvent.ACTION_MOVE:
        	float deltaX = x-mDownX;
        	float deltaY = y-mDownY;
        	if(isFirst){
        		
            	if(Math.abs(deltaX) > Math.abs(deltaY)){
            		if(x>=mParentX){
            			isLR = true;
                		isFirst = false;
                		MoveLayout(deltaX);
            		}
            		else{
            			isFirst = false;
            			isLR = false;
            		}
            	}
            	else if(deltaX==0 && deltaY==0){
            		isFirst = true;
            		isLR = false;
            	}
            	else{
            		isFirst = false;
            		isLR = false;
            	}
        	}
        	
        	break;
        case MotionEvent.ACTION_UP:
        	break;
        }
		return isLR;
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		// TODO Auto-generated method stub
		float x = event.getX();
		float deltaX = x-mDownX;
		switch (event.getAction())  
        { 
		case MotionEvent.ACTION_MOVE:
			MoveLayout(deltaX);
			break;
		case MotionEvent.ACTION_UP:
			MoveLayout(deltaX);
			AnimateLayout();
			break;
        }
		return super.onTouchEvent(event);
	}

	public void MoveLayout(float deltax){
		if(listLayout==null){
			listLayout=(LinearLayout)findViewById(R.id.group_root);
		}
		if(mParentX<=STARTX&&deltax<=0){
			listLayout.layout(STARTX, 0, sWidth, sHeight);
			mParentX=STARTX;
			mListView.setmEnableTouch(true);
			return;
		}
		int dx = (int)deltax;
		int px = (int)mStartX;
		listLayout.layout(px+dx, 0, px+dx+sWidth-STARTX, sHeight);
		this.invalidate();
		mParentX = px+dx;
		mListView.setmEnableTouch(false);
	}
	
	public void AnimateLayout(){
		if(listLayout==null){
			listLayout=(LinearLayout)findViewById(R.id.group_root);
		}
		listLayout.clearAnimation();
		Animation animation;
		if(mParentX>=STARTX&&mParentX<=mEndX){
			if(Math.abs(mParentX-STARTX)>Math.abs(mEndX-mParentX)){
				animation=new TranslateAnimation(0, mEndX-mParentX, 0, 0);
				animation.setDuration(mDuration);
				animation.setFillEnabled(true);
				animation.setFillAfter(true);
				animation.setInterpolator(new DecelerateInterpolator());
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
						listLayout.clearAnimation();
						listLayout.layout((int)mEndX, 0, (int)(mEndX+sWidth)-STARTX, sHeight);
						mStartX=mEndX;
						mParentX=mEndX;
						mListView.setmEnableTouch(false);
					}
				});
				listLayout.startAnimation(animation);
			}
			else{
				animation=new TranslateAnimation(0, STARTX-mParentX, 0, 0);
				animation.setDuration(mDuration);
				animation.setFillEnabled(true);
				animation.setFillAfter(true);
				animation.setInterpolator(new DecelerateInterpolator());
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
						listLayout.clearAnimation();
						listLayout.layout(STARTX, 0, sWidth, sHeight);
						mStartX=STARTX;
						mParentX=STARTX;
						mListView.setmEnableTouch(true);
					}
				});
				listLayout.startAnimation(animation);
			}
		}
		else{
			animation=new TranslateAnimation(0, mEndX-mParentX, 0, 0);
			animation.setDuration(mDuration);
			animation.setFillEnabled(true);
			animation.setFillAfter(true);
			animation.setInterpolator(new DecelerateInterpolator());
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
					listLayout.clearAnimation();
					listLayout.layout((int)mEndX, 0, (int)(mEndX+sWidth)-STARTX, sHeight);
					mStartX=mEndX;
					mParentX=mEndX;
					mListView.setmEnableTouch(false);
				}
			});
			listLayout.startAnimation(animation);
		}
	}
	
	public void ToleftStart(long startoffset){
		if(listLayout==null){
			listLayout=(LinearLayout)findViewById(R.id.group_root);
		}
		Animation animation=new TranslateAnimation(0, STARTX-mParentX, 0, 0);
		animation.setDuration(mDuration);
		animation.setStartOffset(startoffset);
		animation.setFillEnabled(true);
		animation.setFillAfter(true);
		animation.setInterpolator(new DecelerateInterpolator());
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
				listLayout.clearAnimation();
				listLayout.layout(STARTX, 0, sWidth, sHeight);
				mStartX=STARTX;
				mParentX=STARTX;
				mListView.setmEnableTouch(true);
			}
		});
		listLayout.startAnimation(animation);
	}
	
	public void Torightend(){
		if(listLayout==null){
			listLayout=(LinearLayout)findViewById(R.id.group_root);
		}
		Animation animation=new TranslateAnimation(0, mEndX-STARTX, 0, 0);
		animation.setDuration(mDuration);
		animation.setFillEnabled(true);
		animation.setFillAfter(true);
		animation.setInterpolator(new DecelerateInterpolator());
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
				listLayout.clearAnimation();
				listLayout.layout((int)mEndX, 0, (int)(mEndX+sWidth)-STARTX, sHeight);
				mStartX=mEndX;
				mParentX=mEndX;
				mListView.setmEnableTouch(false);
			}
		});
		listLayout.startAnimation(animation);
	}
	
}
