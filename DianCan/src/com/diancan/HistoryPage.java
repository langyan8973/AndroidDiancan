package com.diancan;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import com.diancan.Helper.RecipeListHttpHelper;
import com.diancan.Utils.BitmapUtil;
import com.diancan.Utils.DisplayUtil;
import com.diancan.Utils.MyDateUtils;
import com.diancan.custom.view.DropDownListView;
import com.diancan.custom.view.DropDownListView.OnRefreshListener;
import com.diancan.diancanapp.AppDiancan;
import com.diancan.http.HttpCallback;
import com.diancan.http.HttpHandler;
import com.diancan.model.History;
import com.diancan.model.Order;
import com.diancan.model.OrderItem;

import android.R.integer;
import android.app.Activity;
import android.app.LocalActivityManager;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Message;
import android.view.View;
import android.view.Window;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.TranslateAnimation;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

public class HistoryPage extends Activity implements HttpCallback,OnRefreshListener,OnClickListener  {
	FrameLayout containerLayout;
	TextView timeTextView;
	TextView titleTextView;
	DropDownListView ddListView;
	ImageView animateImageView;
	SimpleAdapter dataAdapter;
	List<HashMap<String, String>> hashMaps;
	HttpHandler mHttpHandler;
	List<String> mIdStrings;
	int selectedIndex;
	RecipeListHttpHelper recipeListHttpHelper;
	AppDiancan appDiancan;
	int sWidth,sHeight,topHeight,tabHeight;
	int req_Count = 0;
	boolean downAnimation;
	ProgressBar mProgressBar;
	Button backButton;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.historypage);
		appDiancan = (AppDiancan)getApplicationContext();
		topHeight = (int)getResources().getDimension(R.dimen.topbar_height);
		tabHeight = (int)getResources().getDimension(R.dimen.tabbar_height);
		sWidth = DisplayUtil.PIXWIDTH;
		sHeight = DisplayUtil.PIXHEIGHT-topHeight-tabHeight;
		
		containerLayout = (FrameLayout)findViewById(R.id.animateContainer);
		timeTextView = (TextView)findViewById(R.id.timeText);
		titleTextView = (TextView)findViewById(R.id.tv_title);
		ddListView = (DropDownListView)findViewById(R.id.itemList);
//		ddListView.setonRefreshListener(this);
//		ddListView.setMyClickListener(this);
		
		backButton = (Button)findViewById(R.id.bt_back);
		backButton.setOnClickListener(this);
		mProgressBar = (ProgressBar)findViewById(R.id.httppro);
		animateImageView = (ImageView)findViewById(R.id.animateImg);
		recipeListHttpHelper = new RecipeListHttpHelper(this, appDiancan);
		
		Intent  intent = getIntent();
		selectedIndex = intent.getIntExtra("index", 0);
		mIdStrings = intent.getStringArrayListExtra("ids");
		
		RequestOrder();
	}
	@Override
	public void RequestComplete(Message msg) {
		// TODO Auto-generated method stub
		mProgressBar.setVisibility(View.GONE);
		if(msg.what==HttpHandler.REQUEST_ORDER_BY_ID){
			Order order = (Order)msg.obj;
			if(req_Count>0){
				Bitmap bmp = BitmapUtil.getBitmapFromView(ddListView, sWidth, sHeight);
				animateImageView.setImageBitmap(bmp);
				animateImageView.setVisibility(View.VISIBLE);
			}
			DisplayItems(order);
			req_Count++;
		}
	}
	@Override
	public void RequestError(String errString) {
		// TODO Auto-generated method stub
		mProgressBar.setVisibility(View.GONE);
		Toast.makeText(this, errString, Toast.LENGTH_SHORT).show();
	}
	
	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
//		downAnimation=false;
//		selectedIndex++;
//		RequestOrder();
		if(v.getId()==R.id.bt_back){
			toHistoryList();
		}
	}
	
	@Override
	public void onRefresh() {
		// TODO Auto-generated method stub
		if(selectedIndex==0){
			ddListView.onRefreshComplete();
		}
		else{
			downAnimation=true;
			selectedIndex--;
			RequestOrder();
		}
	}
	
	private void RequestOrder(){
		mProgressBar.setVisibility(View.VISIBLE);
		String idString = mIdStrings.get(selectedIndex);
		String[] ids = idString.split("-");
		recipeListHttpHelper.RequestOrderByOid(Integer.parseInt(ids[0]),Integer.parseInt(ids[1]));
	}
	
	private void DisplayItems(Order order){
		List<OrderItem> orderItems = order.getClientItems();
		if(hashMaps==null){
			hashMaps = new ArrayList<HashMap<String,String>>();
		}
		hashMaps.clear();
		
		Iterator<OrderItem> iterator;
		for(iterator=orderItems.iterator();iterator.hasNext();){
			OrderItem orderItem = iterator.next();
			HashMap<String, String> map = new HashMap<String, String>();
			map.put("title", orderItem.getRecipe().getName()+"----"+orderItem.GetCount()+"份");
			map.put("price", "单价：￥ "+orderItem.getRecipe().getPrice());
			hashMaps.add(map);
		}
		
		if(dataAdapter==null){
			dataAdapter = new SimpleAdapter(this, hashMaps, R.layout.history_orderitem,
					new String[] {"title","price"},new int[] {R.id.his_recipe_name,R.id.his_recipe_price});
			ddListView.setAdapter(dataAdapter);
		}
		else{
			dataAdapter.notifyDataSetChanged();
//			ddListView.onRefreshComplete();
//			startDownAnimation();
			
		}
//		if(selectedIndex==mIdStrings.size()-1){
//			ddListView.changeFooterVisibility(false);
//		}
//		else{
//			ddListView.changeFooterVisibility(true);
//		}
		titleTextView.setText(order.getRestaurant().getName()+"(￥"+order.getPriceAll()+")");
		Date curDate = new Date(System.currentTimeMillis());
		Date timeDate = order.getStarttime();
		timeTextView.setText(MyDateUtils.getStringFormDate(curDate, timeDate));
	}
	
	public void startDownAnimation(){
		int duration = 500;
		Animation animation1; 
		Animation animation2; 
		if(downAnimation){
			animation1 = new TranslateAnimation(0, 0, 0, sHeight);
			animation2 = new TranslateAnimation(0,0,-sHeight,0);
		}
		else{
			animation1 = new TranslateAnimation(0, 0, 0, -sHeight);
			animation2 = new TranslateAnimation(0,0,sHeight,0);
		}
		animation1.setDuration(duration);
		animation2.setDuration(duration);
		animation1.setAnimationListener(new AnimationListener() {
			
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
				animateImageView.setVisibility(View.GONE);
				animateImageView.clearAnimation();
			}
		});
		animateImageView.startAnimation(animation1);
		ddListView.startAnimation(animation2);
	}
	
	private void toHistoryList(){
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
		Intent in = new Intent(this.getParent(), HistoryList.class);
		in.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
		Window window = manager.startActivity(MenuGroup.ID_HISTORYLIST, in);
		View view=window.getDecorView();		
		contain.addView(view);
		LayoutParams params=(LayoutParams) view.getLayoutParams();
        params.width=LayoutParams.FILL_PARENT;
        params.height=LayoutParams.FILL_PARENT;
        view.setLayoutParams(params);
        view.startAnimation(animation);
	}
}
