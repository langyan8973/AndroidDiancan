package com.diancan;

import java.util.Timer;
import java.util.TimerTask;
import com.diancan.Utils.JsonUtils;
import com.diancan.Utils.MenuUtils;
import com.diancan.diancanapp.AppDiancan;
import com.diancan.http.HttpDownloader;
import com.diancan.model.Order;

import android.app.Activity;
import android.app.LocalActivityManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

public class TableCodePage extends Activity implements OnClickListener {
	EditText inpuText;
	Button okBtn;
	AppDiancan declare;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.tablecodepage);
		inpuText=(EditText)findViewById(R.id.inputCode);
		okBtn=(Button)findViewById(R.id.btnyes);
		okBtn.setOnClickListener(this);
		declare=(AppDiancan)getApplicationContext();
		PopInputMethod();
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
			String resultString = HttpDownloader.GetOrderByCode(MenuUtils.initUrl+"restaurants/"+declare.restaurantId+"/orders/code/"+codeString,
					declare.udidString);
			System.out.println("resultString:"+resultString);
			final Order order=JsonUtils.ParseJsonToOrder(resultString);
			declare.curOrder=order;
			 
            //发广播更新餐桌tab标题
            Intent in = new Intent();
            in.setAction("selectedtable");
            in.putExtra("tablename", order.getDesk().getName());
            in.addCategory(Intent.CATEGORY_DEFAULT);
            TableCodePage.this.sendBroadcast(in);
            new Thread(){
				public void run(){
					declare.getMenuListDataObj().ChangeRecipeMapByOrder(order);
				}
			}.start();
            
		} catch (Throwable e) {
			e.printStackTrace();
			//自定义的错误，在界面上显示
			Toast toast = Toast.makeText(TableCodePage.this, e.getMessage(), Toast.LENGTH_SHORT); 
            toast.show();
            return;
		}
		ToMyTable();
	}
	
	/***
	 * 跳转到订单页面
	 */
	public void ToMyTable()
	{
		TableGroup parent = (TableGroup) getParent();
		LocalActivityManager manager = parent.getLocalActivityManager();
	    final LinearLayout contain = (LinearLayout) parent.findViewById(R.id.table_continer);
	    
		contain.removeAllViews();
		Intent in = new Intent(getParent(), MyTable.class);
		Window window = manager.startActivity("MyTable", in);
		
		View view=window.getDecorView();		
		contain.addView(view);
		LayoutParams params=(LayoutParams) view.getLayoutParams();
        params.width=LayoutParams.FILL_PARENT;
        params.height=LayoutParams.FILL_PARENT;
        view.setLayoutParams(params);
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
	}
}
