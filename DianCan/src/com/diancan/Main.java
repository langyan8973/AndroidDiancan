package com.diancan;

import com.diancan.diancanapp.AppDiancan;
import com.diancan.model.MyRestaurant;
import com.diancan.model.Order;

import android.app.ActivityGroup;
import android.app.TabActivity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.RelativeLayout;
import android.widget.TabHost;
import android.widget.TabWidget;
import android.widget.TextView;
import android.widget.Toast;

public class Main extends TabActivity {
    
	/** Called when the activity is first created. */
	private TabHost m_tabHost;
	private TabWidget m_tabWidget;
	private long exitTime = 0;
	Resources rsResources;
	BroadcastReceiver receiver;	
	 
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE); 
        //输入法弹出时压盖页面
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
        setContentView(R.layout.main);
        //获取应用全局变量   
        final AppDiancan appDiancan=(AppDiancan)getApplicationContext();       
        rsResources=getResources();

        m_tabHost = getTabHost();  
        m_tabHost.setup();
        m_tabHost.bringToFront();
        m_tabHost.clearAllTabs();
        addFirstPageTab();
        addMyTableTab();
        addMyServiceTab();
        
        SharedPreferences deviceInfo = getSharedPreferences("StartInfo", 0);
        int ctab = deviceInfo.getInt("ctab", 0);
        Log.d("Main", "ctab====================="+ctab);
        m_tabHost.setCurrentTab(ctab);
        m_tabWidget = (TabWidget)findViewById(android.R.id.tabs);
        m_tabWidget.setStripEnabled(false);
        //注册一个广播接收器，启动餐桌抖动动画  
        receiver = new BroadcastReceiver() {
	    	@Override
	        public void onReceive(Context ctx, Intent intent) {
	    		if (intent.getAction().equals("animation")) {
	 
	   			   View v=m_tabHost.getTabWidget().getChildAt(1);
	   			   TextView txtcount=(TextView)v.findViewById(R.id.txtcount);
	   			   txtcount.setVisibility(View.VISIBLE);
	   			   txtcount.setText(appDiancan.myOrderHelper.getTotalCount()+"");
	    		}
	    		else if(intent.getAction().equals("selectedtable"))
	    		{
	    			View v=m_tabHost.getTabWidget().getChildAt(1);
//	    			String strNo=(String)intent.getSerializableExtra("tablename");
//	    			if(!strNo.equals(""))
//	    			{
//	    				((TextView) v.findViewById(R.id.tab_item_textview)).setText(strNo);
//	    			}
//	    			else {
//	    				((TextView) v.findViewById(R.id.tab_item_textview)).setText(rsResources.getString(R.string.title_Tab2));
//					}
	    			TextView txtcount=(TextView)v.findViewById(R.id.txtcount);
	    			if(appDiancan.myOrderHelper.getTotalCount()>0){
	    				txtcount.setVisibility(View.VISIBLE);
	    				txtcount.setText(appDiancan.myOrderHelper.getTotalCount()+"");
	    			}
	    			else{
	    				txtcount.setVisibility(View.INVISIBLE);
	    			}
	    		}
	    		else if(intent.getAction().equals("setcount"))
	    		{
	    			View v=m_tabHost.getTabWidget().getChildAt(1);
	    			TextView txtcount=(TextView)v.findViewById(R.id.txtcount);
	    			if(appDiancan.myOrder==null||appDiancan.myOrderHelper.getTotalCount()==0)
	    			{
	    				txtcount.setVisibility(View.INVISIBLE);
	    			}
	    			else {
						txtcount.setVisibility(View.VISIBLE);
						txtcount.setText(appDiancan.myOrderHelper.getTotalCount()+"");
					}
	    		}
	    	}
	    };
	    IntentFilter filter = new IntentFilter();
	    filter.addAction("animation");
	    filter.addAction("selectedtable");
	    filter.addAction("setcount");
	    filter.addCategory(Intent.CATEGORY_DEFAULT);
	    registerReceiver(receiver, filter);
    
    }
    
    /***
     * 创建标签的view
     * @param imageResourceSelector
     * @param text
     * @return
     */
    private View CreateTabItem(Drawable imagedrawable, String text) {  
  
        View view = View.inflate(this, R.layout.tab_item, null); 
        RelativeLayout rLayout=(RelativeLayout)view.findViewById(R.id.itemlayout);
        rLayout.setBackgroundDrawable(imagedrawable);
        ((TextView) view.findViewById(R.id.tab_item_textview)).setText(text);  
        return view;
    }
    
    /***
     * 创建标签的view
     * @param imageResourceSelector
     * @param text
     * @param count
     * @return
     */
    private View CreateOrderTab(Drawable imagedrawable, String text,int count) {
    	View view = View.inflate(this, R.layout.tab_item, null); 
    	RelativeLayout rLayout=(RelativeLayout)view.findViewById(R.id.itemlayout);
        rLayout.setBackgroundDrawable(imagedrawable);
        ((TextView) view.findViewById(R.id.tab_item_textview)).setText(text);  
        TextView txtcount=(TextView)view.findViewById(R.id.txtcount);
        if(count==0)
        {
        	txtcount.setVisibility(View.INVISIBLE);
        }
        else {
			txtcount.setVisibility(View.VISIBLE);
			txtcount.setText(count+"");
		}
        return view;
	}
    
    /***
     * 增加“首页”标签
     */
    public void addFirstPageTab(){  
        Intent intent = new Intent();  
        intent.setClass(Main.this, MenuGroup.class);
        Drawable drawable=rsResources.getDrawable(R.drawable.foodlist_tab_back);
        View v=CreateTabItem(drawable, rsResources.getString(R.string.title_Tab1));
	    m_tabHost.addTab(m_tabHost.newTabSpec("menu1").setIndicator(v).setContent(intent));
    } 
    
    /***
     * 增加订单标签
     */
    public void addMyTableTab(){  
        Intent intent = new Intent();  
        intent.setClass(Main.this, MyTable.class);  
        String tabnameString="";
        AppDiancan declare=(AppDiancan)getApplicationContext(); 
        int count=0;
        tabnameString=rsResources.getString(R.string.title_Tab2);
        if(declare.myOrder!=null&&declare.myOrderHelper!=null)
        {
        	count=declare.myOrderHelper.getTotalCount();
        	
        }
        Drawable drawable=rsResources.getDrawable(R.drawable.orderlist_tab_back);
        View v=CreateOrderTab(drawable, tabnameString,count);
	    m_tabHost.addTab(m_tabHost.newTabSpec("menu2").setIndicator(v).setContent(intent));
    } 
    
    /***
     * 增加服务标签
     */
    public void addMyServiceTab(){  
        Intent intent = new Intent();  
        intent.setClass(Main.this, MyService.class);   
        Drawable drawable=rsResources.getDrawable(R.drawable.waiter_tab_back);
        View v=CreateTabItem(drawable, rsResources.getString(R.string.title_Tab3));
	    m_tabHost.addTab(m_tabHost.newTabSpec("menu3").setIndicator(v).setContent(intent));
    }  
    
    /***
     * 增加结账标签
     */
    public void addCheckTab(){  
        Intent intent = new Intent();  
        intent.setClass(Main.this, HistoryGroup.class);
        Drawable drawable=rsResources.getDrawable(R.drawable.pay_tab_back);
        View v=CreateTabItem(drawable, rsResources.getString(R.string.title_Tab4));
	    m_tabHost.addTab(m_tabHost.newTabSpec("menu4").setIndicator(v).setContent(intent));
    } 

	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		unregisterReceiver(receiver);
  		
  		WriteRestaurantAndOrder();
	}
  	
  	/***
  	 * 重写onTouchEvent,这里的目的是把touch事件传给子视图
  	 */
  	@Override
	public boolean onTouchEvent(MotionEvent event) {
		// TODO Auto-generated method stub
    	return this.getCurrentActivity().onTouchEvent(event);
	}

	@Override
	public boolean dispatchKeyEvent(KeyEvent event) {
		// TODO Auto-generated method stub
//		int keyCode = event.getKeyCode();
//		if(m_tabHost.getCurrentTab()>0){
//			if(keyCode == KeyEvent.KEYCODE_BACK && event.getAction() == KeyEvent.ACTION_DOWN){                    
//			    if((System.currentTimeMillis()-exitTime) > 2000){  
//			        Toast.makeText(getApplicationContext(), "再按一次退出程序", Toast.LENGTH_SHORT).show();
//			        exitTime = System.currentTimeMillis();  
//			    }  
//			    else{  
//			    	Destroy();
//			        finish();
//			        System.exit(0);  
//			    }  
//	                  
//			    return true;  
//		    }  
//		    return super.onKeyDown(keyCode, event);
//		}
//		else{
//			ActivityGroup menuGroup = (ActivityGroup)this.getCurrentActivity();
//			if(menuGroup.getLocalActivityManager().getCurrentId().equals(MenuGroup.ID_MAINFIRST)){
//				if(keyCode == KeyEvent.KEYCODE_BACK && event.getAction() == KeyEvent.ACTION_DOWN){                    
//				    if((System.currentTimeMillis()-exitTime) > 2000){  
//				        Toast.makeText(getApplicationContext(), "再按一次退出程序", Toast.LENGTH_SHORT).show();
//				        exitTime = System.currentTimeMillis();  
//				    }  
//				    else{  
//				    	Destroy();
//				        finish();
//				        System.exit(0);  
//				    }  
//		                  
//				    return true;  
//			    }  
//			    return super.onKeyDown(keyCode, event);
//			}
//			else{
//				return super.dispatchKeyEvent(event);
//			}
//		}
		return super.dispatchKeyEvent(event);
	}
//	private void Destroy(){
//		unregisterReceiver(receiver);
//  		AppDiancan declare=(AppDiancan)getApplicationContext();
//  		WriteRestaurantAndOrder(declare.myRestaurant,declare.myOrder);
//	}
	private void  WriteRestaurantAndOrder(){
		AppDiancan declare=(AppDiancan)getApplicationContext();
		SharedPreferences deviceInfo = getSharedPreferences("StartInfo", 0);
		int rid;
		int oid;
		int orid;
		String rnameString;
		int currentTab;
		
		if(declare.myRestaurant!=null)
		{
			rid = declare.myRestaurant.getId();
			rnameString = declare.myRestaurant.getName();
		}
		else{
			rid = -1;
			rnameString = "-1";
		}
		if(declare.myOrder!=null){
			oid = declare.myOrder.getId();
			orid = declare.myOrder.getRestaurant().getId();
		}
		else{
			oid = -1;
			orid = -1;
		}
		
		
		currentTab = m_tabHost.getCurrentTab();
		deviceInfo.edit().putInt("rid", rid).commit();
		deviceInfo.edit().putString("rname",rnameString).commit();
		deviceInfo.edit().putInt("oid", oid).commit();
		deviceInfo.edit().putInt("orid", orid).commit();
		deviceInfo.edit().putInt("ctab", currentTab).commit();
	}
}