package com.diancan;

import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import com.diancan.Utils.MenuUtils;
import com.diancan.Utils.MyDateUtils;
import com.diancan.custom.adapter.HistoriesAdapter;
import com.diancan.custom.view.PinnedHeaderListView;
import com.diancan.diancanapp.AppDiancan;
import com.diancan.http.HttpCallback;
import com.diancan.http.HttpHandler;
import com.diancan.model.Category;
import com.diancan.model.History;
import com.diancan.model.MyRestaurant;
import com.diancan.sectionlistview.OrderSectionListAdapter;
import com.diancan.sectionlistview.SectionListItem;

import android.app.Activity;
import android.app.LocalActivityManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Message;
import android.text.format.DateUtils;
import android.util.SparseArray;
import android.view.View;
import android.view.Window;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Toast;

public class HistoryList extends Activity implements OnClickListener,OnItemClickListener,
							HttpCallback{
	AppDiancan appDiancan;
	Button btnBack;
	PinnedHeaderListView historyListView;
	HttpHandler mHandler;
	List<History> mHistories;
	
	public class MyStandardArrayAdapter extends ArrayAdapter<SectionListItem> {

    	public List<SectionListItem> items;
        public MyStandardArrayAdapter(Context context, int textViewResourceId,
				List<SectionListItem> objects) {
			super(context, textViewResourceId, objects);
			// TODO Auto-generated constructor stub
			this.items = objects;
		}
        
    }
    
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.historylist);
		appDiancan = (AppDiancan)getApplicationContext();
		btnBack = (Button)findViewById(R.id.bt_back);
		btnBack.setOnClickListener(this);
		historyListView = (PinnedHeaderListView)findViewById(R.id.historyList);
		historyListView.setOnItemClickListener(this);
		
		mHandler = new HttpHandler(this);
		RequestAllHistorys();
	}
	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
	}
	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		if(v.getId()==R.id.bt_back){
			ToMainFirstPage();
		}
	}
	@Override
	public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
		// TODO Auto-generated method stub
		
	}
	@Override
	public void RequestComplete(Message msg) {
		// TODO Auto-generated method stub
		switch(msg.what) {  
        case HttpHandler.REQUEST_ALLHISTORY:
        	mHistories = (List<History>)msg.obj;
        	DisplayHistories();
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
		Toast toast = Toast.makeText(HistoryList.this, strMess, Toast.LENGTH_SHORT); 
        toast.show();
	}
    
    /**
     * 发送请求获取所有历史订单
     */
    private void RequestAllHistorys(){
    	new Thread(new Runnable() {
			
			@Override
			public void run() {
				// TODO Auto-generated method stub
				
				try {
					List<History> histories=MenuUtils.getAllHistories(appDiancan.udidString,
							appDiancan.accessToken.getAuthorization());
					mHandler.obtainMessage(HttpHandler.REQUEST_ALLHISTORY,histories).sendToTarget();
				} catch (Exception e) {
					// TODO Auto-generated catch block
					mHandler.obtainMessage(HttpHandler.REQUEST_ERROR,e.getMessage()).sendToTarget();
				}
				
			}
		}).start();
    }
    
    private void DisplayHistories(){
    	
    	MyStandardArrayAdapter arrayAdapter;
    	HistoriesAdapter sectionAdapter;
        List<SectionListItem> exampleArray =new ArrayList<SectionListItem>();
        Date curDate = new Date(System.currentTimeMillis());
        Iterator<History> iterator;
        for(iterator = mHistories.iterator();iterator.hasNext();){
        	History history = iterator.next();
        	String section = MyDateUtils.getWeekString(curDate, history.getTime());
        	exampleArray.add(new SectionListItem(history, section));
        }
        
        arrayAdapter = new MyStandardArrayAdapter(this,R.id.his_r_name,exampleArray);
		sectionAdapter = new HistoriesAdapter(getLayoutInflater(),arrayAdapter);
		sectionAdapter.setViewClickListener(this);
    	
    	historyListView.setAdapter(sectionAdapter);
    	historyListView.setOnScrollListener(sectionAdapter);
    	historyListView.setPinnedHeaderView(getLayoutInflater().inflate(R.layout.his_list_section, historyListView, false));
    }
    
    /**
  	 * 跳回导航页
  	 */
  	private void ToMainFirstPage(){
  		MenuGroup parent = (MenuGroup)this.getParent();
		LocalActivityManager manager = parent.getLocalActivityManager();
		Activity activity=manager.getCurrentActivity();
		Window w1=activity.getWindow();
		View v1=w1.getDecorView();
		Animation sAnimation=AnimationUtils.loadAnimation(this, R.anim.push_right_out);
		v1.startAnimation(sAnimation);
	    final LinearLayout contain = (LinearLayout) parent.findViewById(R.id.group_Layout);
		contain.removeAllViews();
		
		Animation animation = AnimationUtils.loadAnimation(this, R.anim.push_right_in);
		Intent in = new Intent(this.getParent(), MainFirstPage.class);
		in.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
		Window window = manager.startActivity(MenuGroup.ID_MAINFIRST, in);
		View view=window.getDecorView();		
		contain.addView(view);
		LayoutParams params=(LayoutParams) view.getLayoutParams();
        params.width=LayoutParams.FILL_PARENT;
        params.height=LayoutParams.FILL_PARENT;
        view.setLayoutParams(params);
        view.startAnimation(animation);
	}
}
