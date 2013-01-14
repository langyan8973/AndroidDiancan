package com.diancan;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;


import com.Utils.DisplayUtil;
import com.Utils.JsonUtils;
import com.Utils.MenuUtils;
import com.Utils.FileUtils;
import com.declare.Declare;
import com.download.HttpDownloader;
import com.model.Desk;
import com.model.OrderItem;
import com.model.Recipe;
import com.mode.DeskObj;
import com.mode.History;
import com.mode.MenuListDataObj;

import android.R.integer;
import android.app.TabActivity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Resources;
import android.graphics.Rect;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.DisplayMetrics;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TabHost;
import android.widget.TabWidget;
import android.widget.TextView;
import android.widget.Toast;

public class Main extends TabActivity {
    
	/** Called when the activity is first created. */
	private TabHost m_tabHost;
	private TabWidget m_tabWidget;
	Resources rsResources;
	BroadcastReceiver receiver;	
	 
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE); 
        //输入法弹出时整个页面上移以免压盖输入框
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
        setContentView(R.layout.main);
        //获取应用全局变量   
        final Declare declare=(Declare)getApplicationContext();       
        rsResources=getResources();

        m_tabHost = getTabHost();  
        m_tabHost.setup();
        m_tabHost.bringToFront();
        m_tabHost.clearAllTabs();
        
        addMenuListTab();
        addMyTableTab();
        addMyServiceTab();
        addHistoryTab();
        m_tabHost.setCurrentTab(0);
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
   			   txtcount.setText(declare.getTotalCount()+"");
    		}
    		else if(intent.getAction().equals("selectedtable"))
    		{
    			View v=m_tabHost.getTabWidget().getChildAt(1);
    			String strNo=(String)intent.getSerializableExtra("tablename");
    			if(!strNo.equals(""))
    			{
    				((TextView) v.findViewById(R.id.tab_item_textview)).setText(strNo);
    			}
    			else {
    				((TextView) v.findViewById(R.id.tab_item_textview)).setText(rsResources.getString(R.string.title_Tab2));
				}
    		}
    		else if(intent.getAction().equals("setcount"))
    		{
    			View v=m_tabHost.getTabWidget().getChildAt(1);
    			TextView txtcount=(TextView)v.findViewById(R.id.txtcount);
    			if(declare.curOrder==null||declare.getTotalCount()==0)
    			{
    				txtcount.setVisibility(View.INVISIBLE);
    			}
    			else {
					txtcount.setVisibility(View.VISIBLE);
					txtcount.setText(declare.getTotalCount()+"");
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
    private View populateTabItem(int imageResourceSelector, String text) {  
  
        View view = View.inflate(this, R.layout.tab_item, null); 
        ((ImageView) view.findViewById(R.id.tab_item_imageview))  
                .setImageResource(imageResourceSelector);  
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
    private View CreateTableTab(int imageResourceSelector, String text,int count) {
    	View view = View.inflate(this, R.layout.tab_item, null); 
        ((ImageView) view.findViewById(R.id.tab_item_imageview))  
                .setImageResource(imageResourceSelector);  
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
     * 增加“菜单”标签
     */
    public void addMenuListTab(){  
        Intent intent = new Intent();  
        intent.setClass(Main.this, MenuGroup.class); 
        View v=populateTabItem(R.drawable.drinks, rsResources.getString(R.string.title_Tab1));
	    m_tabHost.addTab(m_tabHost.newTabSpec("menu1").setIndicator(v).setContent(intent));
    } 
    
    /***
     * 增加我的餐桌标签
     */
    public void addMyTableTab(){  
        Intent intent = new Intent();  
        intent.setClass(Main.this, TableGroup.class);  
        String tabnameString="";
        Declare declare=(Declare)getApplicationContext();  
        int count=0;
        if(declare.curOrder!=null)
        {
        	if(declare.curOrder.getStatus()==4||declare.curOrder.getStatus()==5)
        	{
        		tabnameString=rsResources.getString(R.string.title_Tab2);
        		count=0;
        		declare.curOrder=null;
        	}
        	else {
        		tabnameString=declare.curOrder.getDesk().getName();
            	count=declare.getTotalCount();
			}
        	
        }
        else {
        	tabnameString=rsResources.getString(R.string.title_Tab2);
		}
        View v=CreateTableTab(R.drawable.mytable, tabnameString,count);
	    m_tabHost.addTab(m_tabHost.newTabSpec("menu2").setIndicator(v).setContent(intent));
    } 
    
    /***
     * 增加服务标签
     */
    public void addMyServiceTab(){  
        Intent intent = new Intent();  
        intent.setClass(Main.this, MyService.class);     
        View v=populateTabItem(R.drawable.myservice, rsResources.getString(R.string.title_Tab3));
	    m_tabHost.addTab(m_tabHost.newTabSpec("menu3").setIndicator(v).setContent(intent));
    }  
    
    /***
     * 增加历史订单标签
     */
    public void addHistoryTab(){  
        Intent intent = new Intent();  
        intent.setClass(Main.this, HistoryGroup.class);     
        View v=populateTabItem(R.drawable.ic_launcher, rsResources.getString(R.string.title_Tab4));
	    m_tabHost.addTab(m_tabHost.newTabSpec("menu4").setIndicator(v).setContent(intent));
    } 
    
  	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
  		Declare declare=(Declare)getApplicationContext();
 		
//  		String deskstr=JsonUtils.ConvertDeskObjToJson(declare.curDeskObj);
//  		String hisStr=JsonUtils.ConvertHistoryToJson(declare.getHistory());
//    	try {
//			FileUtils.SaveDingDan(this, deskstr);
//			FileUtils.SaveHistory(this, hisStr);
//		} catch (IOException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
//    	
//    	declare.curDeskObj=null;
    	declare.history=null;
    	declare.menuListDataObj.categories.clear();
    	declare.menuListDataObj.recipeMap.clear();
    	declare.hashTypes.clear();
    	declare.restaurantId=0;

    	unregisterReceiver(receiver);
		super.onDestroy();
	}
  	
  	/***
  	 * 重写onTouchEvent,这里的目的是把touch事件传给子视图
  	 */
  	@Override
	public boolean onTouchEvent(MotionEvent event) {
		// TODO Auto-generated method stub
    	return this.getCurrentActivity().onTouchEvent(event);
	}

}