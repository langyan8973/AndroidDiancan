package com.diancan;

import com.diancan.diancanapp.AppDiancan;
import com.diancan.http.HttpDownloader;
import android.R.integer;
import android.app.Activity;
import android.app.ActivityGroup;
import android.app.LocalActivityManager;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.Window;
import android.widget.LinearLayout;
import android.widget.Toast;
/***
 * 
 * @author liuyan
 *
 */
public class MenuGroup extends ActivityGroup {

	public LinearLayout rootLayout;
	public LocalActivityManager activityManager;
	AppDiancan declare;
	
	public static String ID_MAINFIRST = "MainFirstPage";
	public static String ID_RESTAURANTACTIVITY = "RestaurantActivity";
	public static String ID_MAPVIEWACTIVITY = "MapViewActivity";
	public static String ID_CAPTUREACTIVITY = "CaptureActivity";
	public static String ID_RECIPLIST = "RecipeList";
	public static String ID_HISTORYLIST = "HistoryList";
	public static String ID_HISTORYPAGE = "HistoryPage";
	public static String ID_HISBROWSE = "HisBrowse";
	public static String ID_SEARCHPAGE = "SearchPage";
	public static String ID_USERINFO = "UserInfoActivity";
	public static String ID_FAVORITELISTPAGE = "FavoriteListPage";
	
	public static String back_id;
	public static boolean isChecked = false;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.menugroup);
		declare=(AppDiancan)getApplicationContext();
		
		SharedPreferences deviceInfo = getSharedPreferences("StartInfo", 0);
		String childString = deviceInfo.getString("childId", "-1");
		int rid = deviceInfo.getInt("rid", -1);
		int orid = deviceInfo.getInt("orid", -1);
		if(rid==-1&&orid!=-1){
			rid=orid;
		}
		InitChildPage(childString,rid);
        
	}
	
	
	
	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		//结账过后回到首页
		if(isChecked){
			ToMainFirstPage();
			isChecked = false;
		}
		
	}
	
	private void InitChildPage(String childString,int rid){

		rootLayout=(LinearLayout)findViewById(R.id.group_Layout);
		rootLayout.removeAllViews();
		activityManager = getLocalActivityManager();
		Intent intent;
		Window subActivity;
		if(childString.equals(MenuGroup.ID_MAINFIRST)){
			if(rid!=-1){
				intent=new Intent(MenuGroup .this,RecipeList.class);
				intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
		        subActivity=getLocalActivityManager().startActivity(MenuGroup.ID_RECIPLIST,intent);
			}
			else{
				intent=new Intent(MenuGroup .this,MainFirstPage.class);
				intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
		        subActivity=getLocalActivityManager().startActivity(MenuGroup.ID_MAINFIRST,intent);
			}
			
		}else if(childString.equals(MenuGroup.ID_RESTAURANTACTIVITY)){
			intent=new Intent(MenuGroup .this,RestaurantActivity.class);
			intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
	        subActivity=getLocalActivityManager().startActivity(MenuGroup.ID_RESTAURANTACTIVITY,intent);
			
		}else if(childString.equals(MenuGroup.ID_MAPVIEWACTIVITY)){
			intent=new Intent(MenuGroup .this,MapViewActivity.class);
			intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
	        subActivity=getLocalActivityManager().startActivity(MenuGroup.ID_MAPVIEWACTIVITY,intent);
			
		}else if(childString.equals(MenuGroup.ID_CAPTUREACTIVITY)){
			intent=new Intent(MenuGroup .this,CaptureActivity.class);
			intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
	        subActivity=getLocalActivityManager().startActivity(MenuGroup.ID_CAPTUREACTIVITY,intent);
			
		}else if(childString.equals(MenuGroup.ID_HISTORYLIST)){
			intent=new Intent(MenuGroup .this,HistoryList.class);
			intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
	        subActivity=getLocalActivityManager().startActivity(MenuGroup.ID_HISTORYLIST,intent);
			
		}else if(childString.equals(MenuGroup.ID_RECIPLIST)){
			if(rid!=-1){
				intent=new Intent(MenuGroup .this,RecipeList.class);
				intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
		        subActivity=getLocalActivityManager().startActivity(MenuGroup.ID_RECIPLIST,intent);
			}
			else{
				intent=new Intent(MenuGroup .this,RestaurantActivity.class);
				intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
		        subActivity=getLocalActivityManager().startActivity(MenuGroup.ID_RESTAURANTACTIVITY,intent);
			}
		}else{
			intent=new Intent(MenuGroup .this,MainFirstPage.class);
			intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
	        subActivity=getLocalActivityManager().startActivity(MenuGroup.ID_MAINFIRST,intent);
		}
		
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
	public void ToMainFirstPage()
	{
		Activity activity=activityManager.getCurrentActivity();
		Window w1=activity.getWindow();
		View v1=w1.getDecorView();
		Animation sAnimation=AnimationUtils.loadAnimation(MenuGroup.this, R.anim.push_right_out);
		v1.startAnimation(sAnimation);
		rootLayout.removeAllViews();				
		
		Animation animation = AnimationUtils.loadAnimation(MenuGroup.this, R.anim.push_right_in);
		Intent intent=new Intent(MenuGroup .this,MainFirstPage.class);
		intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        Window subActivity=getLocalActivityManager().startActivity(MenuGroup.ID_MAINFIRST,intent);
        View view=subActivity.getDecorView();
        rootLayout.addView(view);  
        
        LayoutParams params=(LayoutParams) view.getLayoutParams();
        params.width=LayoutParams.FILL_PARENT;
        params.height=LayoutParams.FILL_PARENT;
        view.setLayoutParams(params);
        view.startAnimation(animation);
        declare.myRestaurant = null;
		return;
		
	}
	
	public void ToRestaurantPage(){
		Activity activity=activityManager.getCurrentActivity();
		Window w1=activity.getWindow();
		View v1=w1.getDecorView();
		Animation sAnimation=AnimationUtils.loadAnimation(MenuGroup.this, R.anim.push_right_out);
		v1.startAnimation(sAnimation);
		rootLayout.removeAllViews();
		
		Animation animation = AnimationUtils.loadAnimation(MenuGroup.this, R.anim.push_right_in);
		Intent intent=new Intent(MenuGroup.this,RestaurantActivity.class);
		intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        Window subActivity=getLocalActivityManager().startActivity(MenuGroup.ID_RESTAURANTACTIVITY,intent);
        View view=subActivity.getDecorView();
        rootLayout.addView(view);  
        
        LayoutParams params=(LayoutParams) view.getLayoutParams();
        params.width=LayoutParams.FILL_PARENT;
        params.height=LayoutParams.FILL_PARENT;
        view.setLayoutParams(params);
        view.startAnimation(animation);
		return;
	}
	
	public void toMapViewPage(){
		Activity activity=activityManager.getCurrentActivity();
		Window w1=activity.getWindow();
		View v1=w1.getDecorView();
		Animation sAnimation=AnimationUtils.loadAnimation(MenuGroup.this, R.anim.push_right_out);
		v1.startAnimation(sAnimation);
		rootLayout.removeAllViews();
		
		Animation animation = AnimationUtils.loadAnimation(MenuGroup.this, R.anim.push_right_in);
		Intent intent=new Intent(MenuGroup.this,MapViewActivity.class);
		intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        Window subActivity=getLocalActivityManager().startActivity(MenuGroup.ID_MAPVIEWACTIVITY,intent);
        View view=subActivity.getDecorView();
        rootLayout.addView(view);  
        
        LayoutParams params=(LayoutParams) view.getLayoutParams();
        params.width=LayoutParams.FILL_PARENT;
        params.height=LayoutParams.FILL_PARENT;
        view.setLayoutParams(params);
        view.startAnimation(animation);
		return;
	}
	
	public void toHisBrowsePage(){
		Activity activity=activityManager.getCurrentActivity();
		Window w1=activity.getWindow();
		View v1=w1.getDecorView();
		Animation sAnimation=AnimationUtils.loadAnimation(MenuGroup.this, R.anim.push_right_out);
		v1.startAnimation(sAnimation);
		rootLayout.removeAllViews();
		
		Animation animation = AnimationUtils.loadAnimation(MenuGroup.this, R.anim.push_right_in);
		Intent intent=new Intent(MenuGroup.this,HisBrowse.class);
		intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        Window subActivity=getLocalActivityManager().startActivity(MenuGroup.ID_HISBROWSE,intent);
        View view=subActivity.getDecorView();
        rootLayout.addView(view);  
        
        LayoutParams params=(LayoutParams) view.getLayoutParams();
        params.width=LayoutParams.FILL_PARENT;
        params.height=LayoutParams.FILL_PARENT;
        view.setLayoutParams(params);
        view.startAnimation(animation);
		return;
	}
	
	public void toSearchPage(){
		Activity activity=activityManager.getCurrentActivity();
		Window w1=activity.getWindow();
		View v1=w1.getDecorView();
		Animation sAnimation=AnimationUtils.loadAnimation(MenuGroup.this, R.anim.push_right_out);
		v1.startAnimation(sAnimation);
		rootLayout.removeAllViews();
		
		Animation animation = AnimationUtils.loadAnimation(MenuGroup.this, R.anim.push_right_in);
		Intent intent=new Intent(MenuGroup.this,SearchPage.class);
		intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        Window subActivity=getLocalActivityManager().startActivity(MenuGroup.ID_SEARCHPAGE,intent);
        View view=subActivity.getDecorView();
        rootLayout.addView(view);  
        
        LayoutParams params=(LayoutParams) view.getLayoutParams();
        params.width=LayoutParams.FILL_PARENT;
        params.height=LayoutParams.FILL_PARENT;
        view.setLayoutParams(params);
        view.startAnimation(animation);
		return;
	}
	
	public void ToHistoryListPage(){
		Activity activity=activityManager.getCurrentActivity();
		Window w1=activity.getWindow();
		View v1=w1.getDecorView();
		Animation sAnimation=AnimationUtils.loadAnimation(MenuGroup.this, R.anim.push_right_out);
		v1.startAnimation(sAnimation);
		rootLayout.removeAllViews();
		
		Animation animation = AnimationUtils.loadAnimation(MenuGroup.this, R.anim.push_right_in);
		Intent intent=new Intent(MenuGroup.this,HistoryList.class);
		intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        Window subActivity=getLocalActivityManager().startActivity(MenuGroup.ID_HISTORYLIST,intent);
        View view=subActivity.getDecorView();
        rootLayout.addView(view);  
        
        LayoutParams params=(LayoutParams) view.getLayoutParams();
        params.width=LayoutParams.FILL_PARENT;
        params.height=LayoutParams.FILL_PARENT;
        view.setLayoutParams(params);
        view.startAnimation(animation);
		return;
	}
	
	private void ToUserInfoPage(){
		Activity activity=activityManager.getCurrentActivity();
		Window w1=activity.getWindow();
		View v1=w1.getDecorView();
		Animation sAnimation=AnimationUtils.loadAnimation(this, R.anim.push_right_out);
		v1.startAnimation(sAnimation);
		rootLayout.removeAllViews();
		
		Animation animation = AnimationUtils.loadAnimation(this, R.anim.push_right_in);
		Intent in = new Intent(this.getParent(), UserInfoActivity.class);
		in.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
		Window window = getLocalActivityManager().startActivity(MenuGroup.ID_USERINFO, in);
		View view=window.getDecorView();		
		rootLayout.addView(view);
		LayoutParams params=(LayoutParams) view.getLayoutParams();
        params.width=LayoutParams.FILL_PARENT;
        params.height=LayoutParams.FILL_PARENT;
        view.setLayoutParams(params);
        view.startAnimation(animation);
	}
	
	private void ToFavoriteListPage(){
		Activity activity=activityManager.getCurrentActivity();
		Window w1=activity.getWindow();
		View v1=w1.getDecorView();
		Animation sAnimation=AnimationUtils.loadAnimation(this, R.anim.push_right_out);
		v1.startAnimation(sAnimation);
		rootLayout.removeAllViews();
		
		Animation animation = AnimationUtils.loadAnimation(this, R.anim.push_right_in);
		Intent in = new Intent(this.getParent(), FavoriteListPage.class);
		in.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
		Window window = getLocalActivityManager().startActivity(MenuGroup.ID_FAVORITELISTPAGE, in);
		View view=window.getDecorView();		
		rootLayout.addView(view);
		LayoutParams params=(LayoutParams) view.getLayoutParams();
        params.width=LayoutParams.FILL_PARENT;
        params.height=LayoutParams.FILL_PARENT;
        view.setLayoutParams(params);
        view.startAnimation(animation);
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
	    		if(strid==null || strid.equals(MenuGroup.ID_MAINFIRST)){
	    			return super.dispatchKeyEvent(event);
	    		}
	    		else if(strid.equals(MenuGroup.ID_RESTAURANTACTIVITY)
	    				||strid.equals(MenuGroup.ID_CAPTUREACTIVITY)
	    				||strid.equals(MenuGroup.ID_MAPVIEWACTIVITY)
	    				||strid.equals(MenuGroup.ID_HISTORYLIST)
	    				||strid.equals(MenuGroup.ID_HISBROWSE)
	    				||strid.equals(MenuGroup.ID_SEARCHPAGE)
	    				||strid.equals(MenuGroup.ID_USERINFO)){
	    			ToMainFirstPage();
	    			return true;
	    		}
	    		else if(strid.equals(MenuGroup.ID_RECIPLIST)){
	    			if(MenuGroup.back_id == MenuGroup.ID_RESTAURANTACTIVITY){
	    				
	    				ToRestaurantPage();
	    			}else if(MenuGroup.back_id == MenuGroup.ID_MAPVIEWACTIVITY){
	    				toMapViewPage();
	    			}
	    			else if(MenuGroup.back_id == MenuGroup.ID_HISBROWSE){
	    				toHisBrowsePage();
	    			}
	    			else if(MenuGroup.back_id == MenuGroup.ID_SEARCHPAGE){
	    				toSearchPage();
	    			}
	    			else if(MenuGroup.back_id == MenuGroup.ID_FAVORITELISTPAGE){
	    				ToFavoriteListPage();
	    			}
	    			else{
	    				ToMainFirstPage();
	    			}
	    			return true;
	    		}
	    		else if(strid.equals(MenuGroup.ID_HISTORYPAGE)){
	    			ToHistoryListPage();
	    			return true;
	    		}
	    		else if(strid.equals(MenuGroup.ID_FAVORITELISTPAGE)){
	    			ToUserInfoPage();
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



	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		String childIdString = activityManager.getCurrentId();
		SharedPreferences deviceInfo = getSharedPreferences("StartInfo", 0);
		deviceInfo.edit().putString("childId", childIdString).commit();
		Activity activity=activityManager.getCurrentActivity();
		activity.finish();
		super.onDestroy();
		
	}
	
}
