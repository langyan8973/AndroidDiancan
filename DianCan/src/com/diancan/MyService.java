package com.diancan;

import android.app.Activity;
import android.app.Dialog;
import android.app.TabActivity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.method.ScrollingMovementMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.OvershootInterpolator;
import android.view.Window;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.diancan.Helper.OrderHelper;
import com.diancan.Helper.RecipeListHttpHelper;
import com.diancan.Utils.JsonUtils;
import com.diancan.diancanapp.AppDiancan;
import com.diancan.http.HttpCallback;
import com.diancan.http.HttpHandler;
import com.diancan.model.Order;

public class MyService extends Activity implements OnClickListener,HttpCallback {
	AppDiancan declare;
	Button mCheckButton;
	ProgressBar mProgressBar;
	Button mCallButton;
	Button mWankuaiButton;
	Button mTangshaoButton;
	Button mKaishuiButton;
	RecipeListHttpHelper recipeListHttpHelper;
//	NotifiReceiver receiver;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.myservice);
		declare=(AppDiancan)getApplicationContext();
		recipeListHttpHelper=new RecipeListHttpHelper(this, declare);
		mCheckButton = (Button)findViewById(R.id.checkBtn);
		mCheckButton.setOnClickListener(this);
		mProgressBar = (ProgressBar)findViewById(R.id.httppro);
		mProgressBar.setVisibility(View.GONE);
		mCallButton = (Button)findViewById(R.id.callwaiter_btn);
		mCallButton.setOnClickListener(this);
		mWankuaiButton = (Button)findViewById(R.id.addwankuai_btn);
		mWankuaiButton.setOnClickListener(this);
		mTangshaoButton = (Button)findViewById(R.id.addtangshao_btn);
		mTangshaoButton.setOnClickListener(this);
		mKaishuiButton = (Button)findViewById(R.id.addkaishui_btn);
		mKaishuiButton.setOnClickListener(this);
	}
	
	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		if(declare.myOrder==null){
			mCheckButton.setVisibility(View.GONE);
		}
		else{
			mCheckButton.setVisibility(View.VISIBLE);
		}
		//注册一个广播接收器，启动餐桌抖动动画  
//	    receiver = new NotifiReceiver();
//	    IntentFilter filter = new IntentFilter();
//	    filter.addAction(Constants.ACTION_SHOW_NOTIFICATION);
//        filter.addAction(Constants.ACTION_NOTIFICATION_CLICKED);
//        filter.addAction(Constants.ACTION_NOTIFICATION_CLEARED);
//	    registerReceiver(receiver, filter);
	}

	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
//		unregisterReceiver(receiver);
	}

	class NotifiReceiver extends BroadcastReceiver{

		@Override
		public void onReceive(Context context, Intent intent) {
			// TODO Auto-generated method stub
			String action = intent.getAction();
//    		if (Constants.ACTION_SHOW_NOTIFICATION.equals(action)) {
//                String notificationId = intent
//                        .getStringExtra(Constants.NOTIFICATION_ID);
//                String notificationApiKey = intent
//                        .getStringExtra(Constants.NOTIFICATION_API_KEY);
//                String notificationTitle = intent
//                        .getStringExtra(Constants.NOTIFICATION_TITLE);
//                String notificationMessage = intent
//                        .getStringExtra(Constants.NOTIFICATION_MESSAGE);
//                String notificationUri = intent
//                        .getStringExtra(Constants.NOTIFICATION_URI);

//                Toast toast = Toast.makeText(MyService.this, notificationMessage, Toast.LENGTH_SHORT); 
//	            toast.show();
//    		}
		}
		
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		if(v.getId()==R.id.checkBtn){
			ClickCheckBtn();
		}
		else if(v.getId()==R.id.callwaiter_btn){
			mCallButton.setEnabled(false);
			new Handler().postDelayed(new Runnable() {
				
				@Override
				public void run() {
					// TODO Auto-generated method stub
					mCallButton.setEnabled(true);
				}
			}, 1000*60*3);
		}
	}

	@Override
	public void RequestComplete(Message msg) {
		// TODO Auto-generated method stub
		mProgressBar.setVisibility(View.GONE);
		switch (msg.what) {
		case HttpHandler.CHECK_ORDER:
			String json=msg.obj.toString();
        	ParseOrderRefresh(json);
			break;

		default:
			break;
		}
	}

	@Override
	public void RequestError(String errString) {
		// TODO Auto-generated method stub
		ShowError(errString);
	}
	
	/**
  	 * 显示错误信息
  	 * @param strMess
  	 */
  	public void ShowError(String strMess) {
		Toast toast = Toast.makeText(MyService.this, strMess, Toast.LENGTH_SHORT); 
        toast.show();
	}
  	
  	private void ClickCheckBtn(){
  		if(declare.myOrder==null){
  			return;
  		}
		final Dialog dialog = new Dialog(this, R.style.MyDialog);
        //设置它的ContentView
		LayoutInflater inflater = (LayoutInflater) this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View layout = inflater.inflate(R.layout.dialog, null);
        dialog.setContentView(layout);
        String contentString = declare.myOrderHelper.GetCheckOrderString();       
        TextView contentView = (TextView)layout.findViewById(R.id.contentTxt);
        TextView titleView = (TextView)layout.findViewById(R.id.dialog_title);
        Button okBtn = (Button)layout.findViewById(R.id.dialog_button_ok);
        okBtn.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				dialog.dismiss();
				mProgressBar.setVisibility(View.VISIBLE);
				recipeListHttpHelper.CheckOrder();
			}
		});
        Button cancelButton = (Button)layout.findViewById(R.id.dialog_button_cancel);
        cancelButton.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				dialog.dismiss();
			}
		});
        double totalprice = declare.myOrder.getPriceDeposit()+declare.myOrder.getPriceConfirm();
        titleView.setText(getString(R.string.str_check_totalprice)+totalprice+getString(R.string.str_yuan));
        contentView.setText(contentString);
        contentView.setMovementMethod(ScrollingMovementMethod.getInstance());
        dialog.show();
		Animation animation = AnimationUtils.loadAnimation(MyService.this, R.anim.activity_in);
		animation.setInterpolator(new OvershootInterpolator());
		layout.startAnimation(animation);
	}
  	
  	private void ParseOrderRefresh(String jsString){
    	if(jsString.equals(""))
    	{
    		ShowError(getString(R.string.message_option_fail));
    	}
    	final Order order=JsonUtils.ParseJsonToOrder(jsString);
    	declare.myOrder = order;
		declare.myOrderHelper.SetOrderAndItemDic(order);
		if(order.getStatus()==3 || order.getStatus()==4){
			declare.myOrder = null;
			declare.myOrderHelper = null;
			MenuGroup.isChecked = true;
			SendSetCountMessage();
			TabActivity main = (TabActivity)this.getParent();
			main.getTabHost().setCurrentTab(0);
			return;
		}
		
    }
  	
  	public void SendSetCountMessage()
    {
    	Intent in = new Intent();
        in.setAction("setcount");
        in.addCategory(Intent.CATEGORY_DEFAULT);
        MyService.this.sendBroadcast(in);
    }
}
