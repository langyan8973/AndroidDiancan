package com.diancan;

import java.util.Timer;
import java.util.TimerTask;

import com.diancan.Helper.OrderHelper;
import com.diancan.Utils.JsonUtils;
import com.diancan.Utils.MenuUtils;
import com.diancan.diancanapp.AppDiancan;
import com.diancan.http.HttpDownloader;
import com.diancan.model.MyRestaurant;
import com.diancan.model.Order;

import android.app.Activity;
import android.app.LocalActivityManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.SparseIntArray;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.AnimationUtils;
import android.view.animation.OvershootInterpolator;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

public class TableCodePage extends Activity implements OnClickListener {
	EditText inpuText;
	Button okBtn;
	Button cancelBtn;
	AppDiancan declare;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		//输入法弹出时整个页面上移以免压盖输入框
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
		setContentView(R.layout.tablecodepage);
		inpuText=(EditText)findViewById(R.id.inputCode);
		okBtn=(Button)findViewById(R.id.btnyes);
		okBtn.setOnClickListener(this);
		cancelBtn = (Button)findViewById(R.id.dialog_button_cancel);
		cancelBtn.setOnClickListener(this);
		declare=(AppDiancan)getApplicationContext();
		
		final LinearLayout dialogLayout = (LinearLayout)findViewById(R.id.dialogLayout);
		Animation animation = AnimationUtils.loadAnimation(this, R.anim.activity_in);
		animation.setInterpolator(new OvershootInterpolator());
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
				dialogLayout.clearAnimation();
				PopInputMethod();
			}
		});
		dialogLayout.startAnimation(animation);
	}
	
	/**
	 * 弹出输入法
	 */
	public void PopInputMethod()
	{
		//弹出软键盘
		inpuText.requestFocus();
        Timer timer = new Timer(); //设置定时器
        timer.schedule(new TimerTask() {
        @Override
        	public void run() { //弹出软键盘的代码
        		InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
        		imm.showSoftInput(inpuText, InputMethodManager.RESULT_SHOWN);
        	}
        }, 100);
	}
	
	/***
	 * 根据邀请码获取订单
	 * @param codeString
	 */
	public void RequestTable(String codeString)
	{
		try {
			String resultString = HttpDownloader.GetOrderByCode(MenuUtils.initUrl+"restaurants/"+declare.myRestaurant.getId()+"/orders/code/"+codeString,
					declare.udidString,declare.accessToken.getAuthorization());
			
			final Order order=JsonUtils.ParseJsonToOrder(resultString);
			declare.myOrder=order;
			declare.myOrderHelper = new OrderHelper(declare.myOrder);
			declare.myOrderHelper.setCategoryDic(declare.myRestaurant.getCategoryDic());
			
            //发广播更新餐桌tab标题
            Intent in = new Intent();
            in.setAction("selectedtable");
            in.putExtra("tablename", order.getDesk().getName());
            in.addCategory(Intent.CATEGORY_DEFAULT);
            TableCodePage.this.sendBroadcast(in);
            
		} catch (Throwable e) {
			e.printStackTrace();
			//自定义的错误，在界面上显示
			Toast toast = Toast.makeText(TableCodePage.this, e.getMessage(), Toast.LENGTH_SHORT); 
            toast.show();
            return;
		}
		this.finish();
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		if(v.getId()==R.id.btnyes){
			//隐藏软键盘
			InputMethodManager imm = (InputMethodManager)getSystemService(TableCodePage.this.INPUT_METHOD_SERVICE);
			imm.hideSoftInputFromWindow(inpuText.getWindowToken(), 0);
			String codeString=inpuText.getText().toString();
			if(codeString=="")
			{
				Toast toast = Toast.makeText(TableCodePage.this, "请输入餐桌邀请码！", Toast.LENGTH_SHORT); 
	            toast.show();
				return;
			}
			RequestTable(codeString);
		}
		else if(v.getId()==R.id.dialog_button_cancel){
			this.finish();
		}
	}
}
