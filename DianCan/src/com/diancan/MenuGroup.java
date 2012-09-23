package com.diancan;

import com.declare.Declare;
import com.download.HttpDownloader;
import com.model.AllDomain;
import android.app.Activity;
import android.app.ActivityGroup;
import android.app.LocalActivityManager;
import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.Window;
import android.widget.LinearLayout;
/***
 * 
 * @author liuyan
 *
 */
public class MenuGroup extends ActivityGroup {

	public LinearLayout rootLayout;
	public LocalActivityManager activityManager;
	AllDomain infos;
	Declare declare;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.menugroup);
		declare=(Declare)getApplicationContext();
		rootLayout=(LinearLayout)findViewById(R.id.group_Layout);
		rootLayout.removeAllViews();
		activityManager = getLocalActivityManager();
		//启用图片缓存
		HttpDownloader.enableHttpResponseCache();
		Intent intent=new Intent(MenuGroup .this,RecipeList.class);
//        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        Window subActivity=getLocalActivityManager().startActivity("RecipeList",intent);
        View view=subActivity.getDecorView();
        rootLayout.addView(view);  
        LayoutParams params=(LayoutParams) view.getLayoutParams();
        params.width=LayoutParams.FILL_PARENT;
        params.height=LayoutParams.FILL_PARENT;
        view.setLayoutParams(params);
        
		
	}
	
	/***
	 * 转到菜单列表页面
	 */
	public void ToRecipeList()
	{
		Activity activity=activityManager.getCurrentActivity();
		Window w1=activity.getWindow();
		View v1=w1.getDecorView();
		Animation sAnimation=AnimationUtils.loadAnimation(MenuGroup.this, R.anim.close_out);
		v1.startAnimation(sAnimation);
		rootLayout.removeAllViews();				
		
		Intent intent=new Intent(MenuGroup .this,RecipeList.class);
        Window subActivity=getLocalActivityManager().startActivity("RecipeList",intent);
        View view=subActivity.getDecorView();
        rootLayout.addView(view);  
        LayoutParams params=(LayoutParams) view.getLayoutParams();
        params.width=LayoutParams.FILL_PARENT;
        params.height=LayoutParams.FILL_PARENT;
        view.setLayoutParams(params);
		return;
		
	}
	/***
	 * 监听返回按键
	 */
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
	    		else if(strid.equals("MenuBook")||strid.equals("SearchList"))
	    		{
	    			ToRecipeList();
	    			return true;
	    		}
	    		else {
	    			return super.dispatchKeyEvent(event);
				}
	        } 
	    } 
		return super.dispatchKeyEvent(event);

	}
	@Override
	public boolean onTouchEvent(MotionEvent event) {
		// TODO Auto-generated method stub
    	return this.getCurrentActivity().onTouchEvent(event);
	}
}
