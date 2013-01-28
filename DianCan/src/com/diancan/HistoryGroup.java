package com.diancan;

import com.diancan.custom.animation.HistoryRotateAnim;

import android.app.Activity;
import android.app.ActivityGroup;
import android.app.LocalActivityManager;
import android.content.Intent;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.view.ViewGroup.LayoutParams;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.LinearLayout;

public class HistoryGroup extends ActivityGroup {
	public LinearLayout rootLayout;
	public LocalActivityManager activityManager;
	int sWidth;
	int sHeight;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.historygroup);
		rootLayout=(LinearLayout)findViewById(R.id.group_continer);
		rootLayout.removeAllViews();
		//屏幕尺寸容器
		DisplayMetrics dm;
		dm = new DisplayMetrics();
		this.getWindowManager().getDefaultDisplay().getMetrics(dm);
		sWidth = dm.widthPixels;
		sHeight=dm.heightPixels;
		activityManager = getLocalActivityManager();
		
		Intent intent=new Intent(HistoryGroup .this,HistoryList.class);
//      intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        Window subActivity=getLocalActivityManager().startActivity("HistoryList",intent);
        View view=subActivity.getDecorView();
        rootLayout.addView(view);  

        LayoutParams params=(LayoutParams) view.getLayoutParams();
        params.width=LayoutParams.FILL_PARENT;
        params.height=LayoutParams.FILL_PARENT;
        view.setLayoutParams(params);
	}
	
	public void ToHistoryList()
	{
		Activity activity=activityManager.getCurrentActivity();
		Window w1=activity.getWindow();
		View v1=w1.getDecorView();
		
		Intent intent=new Intent(HistoryGroup .this,HistoryList.class);
        Window subActivity=getLocalActivityManager().startActivity("HistoryList",intent);
        final View view=subActivity.getDecorView();
         
		
		Animation sAnimation=new HistoryRotateAnim(0, 90, 0.0f, 0.0f,sWidth/2 , sHeight/2,true);
		final Animation sAnimation1=new HistoryRotateAnim(-90, 0, 0.0f, 0.0f,sWidth/2 , sHeight/2,false);
		sAnimation.setAnimationListener(new Animation.AnimationListener() {
			
			@Override
			public void onAnimationStart(Animation animation) {}
			
			@Override
			public void onAnimationRepeat(Animation animation) {}
			
			@Override
			public void onAnimationEnd(Animation animation) {
				// TODO Auto-generated method stub
				rootLayout.addView(view); 
				 LayoutParams params=(LayoutParams) view.getLayoutParams();
		        params.width=LayoutParams.FILL_PARENT;
		        params.height=LayoutParams.FILL_PARENT;
		        view.setLayoutParams(params);
		        view.startAnimation(sAnimation1);
			}
		});
		v1.startAnimation(sAnimation);
		rootLayout.removeAllViews();
	}
	
	@Override
	public boolean dispatchKeyEvent(KeyEvent event) {
		// TODO Auto-generated method stub
		if (event.getKeyCode() == KeyEvent.KEYCODE_BACK) { 
	        if (event.getAction() == KeyEvent.ACTION_DOWN 
	                && event.getRepeatCount() == 0) { 
	        	String strid=activityManager.getCurrentId();
	    		if(strid==null)
	    		{
	    			return super.dispatchKeyEvent(event);
	    		}
	    		else if(strid.equals("HistoryPage"))
	    		{
	    			ToHistoryList();
	    			return true;
	    		}
	    		else {
	    			return super.dispatchKeyEvent(event);
				}
	        } 
	    } 
		return super.dispatchKeyEvent(event);
	}
	
}
