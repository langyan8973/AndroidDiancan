package com.diancan;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import com.Utils.DisplayUtil;
import com.Utils.FileUtils;
import com.Utils.JsonUtils;
import com.Utils.MenuUtils;
import com.declare.Declare;
import com.download.HttpDownloader;
import com.mode.History;
import com.mode.MenuListDataObj;
import com.model.Order;
import com.model.OrderItem;
import com.model.Recipe;

import android.app.Activity;
import android.content.Intent;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.Window;
import android.widget.Toast;

public class InitPage extends Activity {

	
	private Handler httpHandler = new Handler() {  
        public void handleMessage (Message msg) {//此方法在ui线程运行   
            switch(msg.what) {  
            case 0: 
            	String errString=msg.obj.toString();
            	ShowError(errString);
                break;   
            case 1: 
            	RequestRecipes();
                break;  
            case 2:
            	UpdateOrder();
            	break;
            case 3:
            	ToMain();
            	break;
            }  
        }  
    };
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.initpage);
		
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
        
        FileUtils.cacheDir  = new File("/sdcard/ChiHuoPro/MenuImg/");
        if (!FileUtils.cacheDir.exists()) {
			FileUtils.cacheDir.mkdirs();
		}
        MenuUtils.initUrl="http://"+getResources().getString(R.string.url_service);
        MenuUtils.updateUrl="http://"+getResources().getString(R.string.url_service);
        MenuUtils.imageUrl="http://"+getResources().getString(R.string.image_service);
        
        RequestAllTypes();
        //判断机型
        printDeviceInf();
        // Start the service
//      ServiceManager serviceManager = new ServiceManager(this);
//      serviceManager.setNotificationIcon(R.drawable.notification);
//      serviceManager.startService();
        int wifi=getWifiRssi();//获取wifi信号强度
        if(wifi<=-70)
        {
        	ShowError("当前网络信号强度非常差。");
        }
	}
	
	/**
	 * 请求所有的菜的类型
	 */
	public void RequestAllTypes()
	{
		new Thread(new Runnable() {
			
			@Override
			public void run() {
				// TODO Auto-generated method stub
				
				try {
					Declare declare=(Declare)getApplicationContext();
					declare.getMenuListDataObj().categories=MenuUtils.getAllCategory();
					httpHandler.obtainMessage(1).sendToTarget();
				} catch (Exception e) {
					// TODO Auto-generated catch block
					httpHandler.obtainMessage(0,e.getMessage()).sendToTarget();
				}
				
			}
		}).start();
		
	}
	
	/**
  	 * 请求所有的菜
  	 */
  	public void RequestRecipes()
	{	
  		new Thread(new Runnable() {
			
			@Override
			public void run() {
				// TODO Auto-generated method stub
				
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
					httpHandler.obtainMessage(2).sendToTarget();
				} catch (Exception e) {
					// TODO Auto-generated catch block
					httpHandler.obtainMessage(0,e.getMessage()).sendToTarget();
				}
				
			}
		}).start();
  			
	}
  	
  	public void UpdateOrder()
  	{
  		final Declare declare=(Declare)getApplicationContext();
  		if(declare.curOrder==null)
  		{
  			ToMain();
  		}
  		else {
  			new Thread(new Runnable() {
  				
  				@Override
  				public void run() {
  					// TODO Auto-generated method stub
  					
  					try {
  						String resultString = HttpDownloader.getString(MenuUtils.initUrl+ "orders/" +declare.curOrder.getId());
  						if(resultString==null)
  						{
  							httpHandler.obtainMessage(0,"编码错误！").sendToTarget();
  							return;
  						}
  						else {
							Order order=JsonUtils.ParseJsonToOrder(resultString);
							declare.curOrder=order;
							httpHandler.obtainMessage(3).sendToTarget();
						}
  					} catch (Exception e) {
  						// TODO Auto-generated catch block
  						httpHandler.obtainMessage(0,e.getMessage()).sendToTarget();
  					}
  					
  				}
  			}).start();
		}
  	}
  	
  	/**
  	 * 跳转
  	 */
  	public void ToMain()
  	{
  		Intent intent=new Intent(this,Main.class);
        startActivity(intent);
        this.finish();
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
    
    /**
  	 * 显示错误信息
  	 * @param strMess
  	 */
  	public void ShowError(String strMess) {
		Toast toast = Toast.makeText(InitPage.this, strMess, Toast.LENGTH_SHORT); 
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
	
}
