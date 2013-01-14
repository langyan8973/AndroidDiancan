package com.diancan;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import cn.jpush.android.api.BasicPushNotificationBuilder;
import cn.jpush.android.api.JPushInterface;

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

import android.R.string;
import android.app.Activity;
import android.app.Notification;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
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
                break;  
            case 2:
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
		
		//屏幕尺寸容器测试
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
        //初始化必要的全局变量
        declare.menuListDataObj=new MenuListDataObj();
        
        FileUtils.cacheDir  = new File("/sdcard/ChiHuoPro/MenuImg/");
        if (!FileUtils.cacheDir.exists()) {
			FileUtils.cacheDir.mkdirs();
		}
        MenuUtils.initUrl="http://"+getResources().getString(R.string.url_service);
        MenuUtils.updateUrl="http://"+getResources().getString(R.string.url_service);
        MenuUtils.imageUrl="http://"+getResources().getString(R.string.image_service);
        HttpDownloader.enableHttpResponseCache();
        
        SharedPreferences deviceInfo = getSharedPreferences("X-device", 0);
        String deviceString = deviceInfo.getString("udid", "");
        if(TextUtils.isEmpty(deviceString))
        {
        	deviceString =  JPushInterface.getUdid(getApplicationContext());
        	deviceInfo.edit().putString("udid",deviceString).commit();
        	RegisterUdid(deviceString);
        }
        declare.udidString=deviceString;
     // 设置通知样式
      	BasicPushNotificationBuilder builder = new BasicPushNotificationBuilder(InitPage.this);
      	builder.statusBarDrawable = R.drawable.notification_icon;
      	builder.notificationFlags = Notification.FLAG_AUTO_CANCEL;  //设置为自动消失
      	builder.notificationDefaults = Notification.DEFAULT_SOUND|Notification.DEFAULT_VIBRATE;  // 设置为铃声与震动都要
      	JPushInterface.setPushNotificationBuilder(1, builder);
        //判断机型
        printDeviceInf();
        int wifi=getWifiRssi();//获取wifi信号强度
        if(wifi<=-70)
        {
        	ShowError("当前网络信号强度非常差。");
        }
        Sleep();
	}
	
	private void RegisterUdid(final String udid){
		new Thread(new Runnable() {
			
			@Override
			public void run() {
				// TODO Auto-generated method stub
				try {
					String resultString = HttpDownloader.RegisterUdid(udid,MenuUtils.initUrl+ "device");
					
				} catch (Throwable e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}).start();
	}
	
	public void Sleep(){
		Thread th=new Thread(new Runnable() {
			
			@Override
			public void run() {
				// TODO Auto-generated method stub
				
				try {
					Thread.sleep(1000);
					httpHandler.obtainMessage(3).sendToTarget();
				} catch (Exception e) {
					// TODO Auto-generated catch block
					httpHandler.obtainMessage(0,e.getMessage()).sendToTarget();
				}
				
			}
		});
		th.start();
	}
	
  	
  	/**
  	 * 跳转
  	 */
  	public void ToMain()
  	{
  		Intent intent=new Intent(this,RestaurantActivity.class);
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
