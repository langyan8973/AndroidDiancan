package com.diancan;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import org.androidpn.client.Constants;
import org.androidpn.client.ServiceManager;

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
        setContentView(R.layout.main);
        //屏幕尺寸容器
  		DisplayMetrics dm;
  		dm = new DisplayMetrics();
  		this.getWindowManager().getDefaultDisplay().getMetrics(dm);
  		DisplayUtil.SCALE=dm.density;
  		DisplayUtil.DPWIDTH=DisplayUtil.px2dip(dm.widthPixels);
  		DisplayUtil.DPHEIGHT=DisplayUtil.px2dip(dm.heightPixels)-25;
  		System.out.println("屏幕密度："+dm.density);
  		System.out.println("dp宽度："+DisplayUtil.DPWIDTH);
  		System.out.println("dp高度："+DisplayUtil.DPHEIGHT);
        //获取应用全局变量   
        final Declare declare=(Declare)getApplicationContext();       
//        DeskObj deskObj=null;
//        String jsonString="";
//        try {
//			jsonString=FileUtils.ReadDingDan(this);
//			deskObj=JsonUtils.ParseJsonToDeskObj(jsonString);
//		} catch (IOException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}catch (Exception e) {
//			e.printStackTrace();
//			// TODO: handle exception
//		}
//        
        History history=null;
//        jsonString="";
//        try {
//			jsonString=FileUtils.ReadHistory(this);
//			history=JsonUtils.ParseJsonToHistory(jsonString);
//		} catch (IOException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
        
//        if(deskObj!=null)
//        {
//        	declare.curDeskObj=deskObj;
//        }
        if(history!=null)
        {
        	declare.history=history;
        }
        else {
			declare.history=new History();			
		}
        //初始化必要的全局变量
        declare.menuListDataObj=new MenuListDataObj();
        
        rsResources=getResources();
        FileUtils.cacheDir  = new File("/sdcard/ChiHuoPro/MenuImg/");
        if (!FileUtils.cacheDir.exists()) {
			FileUtils.cacheDir.mkdirs();
		}
        MenuUtils.initUrl="http://"+rsResources.getString(R.string.url_service);
        MenuUtils.updateUrl="http://"+rsResources.getString(R.string.url_service);
        MenuUtils.imageUrl="http://"+rsResources.getString(R.string.image_service);
        
        RequestRecipes();

        m_tabHost = getTabHost();  
        m_tabHost.setup();
        m_tabHost.bringToFront();
        
        addMenuListTab();
        addMyTableTab();
        addMyServiceTab();
        addHistoryTab();
        m_tabHost.setCurrentTab(0);
        m_tabWidget = (TabWidget)findViewById(android.R.id.tabs);
        m_tabWidget.setStripEnabled(false);
        //判断机型
        printDeviceInf();
      //注册一个广播接收器，启动餐桌抖动动画  
      receiver = new BroadcastReceiver() {
    	@Override
        public void onReceive(Context ctx, Intent intent) {
    		if (intent.getAction().equals("animation")) {
//   			 Animation animation2=AnimationUtils.loadAnimation(Main.this, R.anim.txtviewanim);
//   			 v.startAnimation(animation2);  
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
    
    // Start the service
    ServiceManager serviceManager = new ServiceManager(this);
    serviceManager.setNotificationIcon(R.drawable.notification);
    serviceManager.startService();
    
    
    int wifi=getWifiRssi();//获取wifi信号强度
    if(wifi<=-70)
    {
    	Toast toast = Toast.makeText(Main.this, "当前网络信号强度非常差。", Toast.LENGTH_SHORT); 
    	toast.show();
    }    
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
        	tabnameString=declare.curOrder.getDesk().getName();
        	count=declare.getTotalCount();
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
    
    /***
     * 获取设备信息,判断是不是小米
     */
    public  void printDeviceInf(){
    	String MANUFACTURER=android.os.Build.MANUFACTURER+"";
    	String MODEL=android.os.Build.MODEL+"";
    	if(MANUFACTURER.equals(MenuUtils.XIAOMI)&&MODEL.equals(MenuUtils.MIONE))
    	{
    		MenuUtils.ISXIAOMI=true;
    	}
    	else {
    		MenuUtils.ISXIAOMI=false;
		}
//		StringBuilder sb = new StringBuilder();
//		sb.append("PRODUCT ").append(android.os.Build.PRODUCT).append("\n");
//		sb.append("BOARD ").append(android.os.Build.BOARD).append("\n");
//		sb.append("BOOTLOADER ").append(android.os.Build.BOOTLOADER).append("\n");
//		sb.append("BRAND ").append(android.os.Build.BRAND).append("\n");
//		sb.append("CPU_ABI ").append(android.os.Build.CPU_ABI).append("\n");
//		sb.append("CPU_ABI2 ").append(android.os.Build.CPU_ABI2).append("\n");
//		sb.append("DEVICE ").append(android.os.Build.DEVICE).append("\n");
//		sb.append("DISPLAY ").append(android.os.Build.DISPLAY).append("\n");
//		sb.append("FINGERPRINT ").append(android.os.Build.FINGERPRINT).append("\n");
//		sb.append("HARDWARE ").append(android.os.Build.HARDWARE).append("\n");
//		sb.append("HOST ").append(android.os.Build.HOST).append("\n");
//		sb.append("ID ").append(android.os.Build.ID).append("\n");
//		sb.append("MANUFACTURER ").append(android.os.Build.MANUFACTURER).append("\n");
//		sb.append("MODEL ").append(android.os.Build.MODEL).append("\n");
//		sb.append("PRODUCT ").append(android.os.Build.PRODUCT).append("\n");
//		sb.append("RADIO ").append(android.os.Build.RADIO).append("\n");
//		sb.append("SERIAL ").append(android.os.Build.SERIAL).append("\n");
//		sb.append("TAGS ").append(android.os.Build.TAGS).append("\n");
//		sb.append("TIME ").append(android.os.Build.TIME).append("\n");
//		sb.append("TYPE ").append(android.os.Build.TYPE).append("\n");
//		sb.append("USER ").append(android.os.Build.USER).append("\n");
//		Log.i(tag,sb.toString());
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
    	declare.menuListDataObj=null;

    	unregisterReceiver(receiver);
		super.onDestroy();
	}
  	
  	/**
  	 * 请求所有的菜
  	 */
  	public void RequestRecipes()
	{	
  		try {
			Declare declare=(Declare)getApplicationContext(); 
			List<Recipe> recipes=MenuUtils.getAllRecipes();
			HashMap<Integer, List<OrderItem>> recipeHashMap=declare.getMenuListDataObj().getRecipeMap();
			
			Iterator<Recipe> iterator;
			for(iterator=recipes.iterator();iterator.hasNext();)
			{
				Recipe recipe=iterator.next();
				if(recipeHashMap.containsKey(recipe.getCid()))
				{
					List<OrderItem> orderItems=recipeHashMap.get(recipe.getCid());
					OrderItem oItem=new OrderItem();
					oItem.setRecipe(recipe);
					oItem.setCount(0);
					orderItems.add(oItem);
				}
				else {
					List<OrderItem> orderItems=new ArrayList<OrderItem>();
					recipeHashMap.put(recipe.getCid(), orderItems);
					OrderItem oItem=new OrderItem();
					oItem.setRecipe(recipe);
					oItem.setCount(0);
					orderItems.add(oItem);
				}
					
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			ShowError(e.getMessage());
		}	
	}
  	/**
  	 * 显示错误信息
  	 * @param strMess
  	 */
  	public void ShowError(String strMess) {
		Toast toast = Toast.makeText(Main.this, strMess, Toast.LENGTH_SHORT); 
        toast.show();
	}
  	
  	/***
  	 * 获取wifi信息
  	 * @return
  	 */
  	private int getWifiRssi()
  	{
  		WifiManager mWifiManager=(WifiManager) getSystemService(WIFI_SERVICE);
  	    WifiInfo mWifiInfo=mWifiManager.getConnectionInfo();
  	    int wifi=mWifiInfo.getRssi();//获取wifi信号强度
  	    return wifi;
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