package com.diancan;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import com.diancan.custom.animation.HistoryRotateAnim;
import com.diancan.diancanapp.AppDiancan;

import android.R.integer;
import android.app.Activity;
import android.app.ListActivity;
import android.app.LocalActivityManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.Window;
import android.view.ViewGroup.LayoutParams;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.AnimationUtils;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;

public class HistoryList extends ListActivity {
	AppDiancan declare;
	ArrayList<HashMap<String, Object>> hashList;
	HistoryListAdapter hisAdapter;
	int sWidth;
	int sHeight;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.historylist);
		//屏幕尺寸容器
		DisplayMetrics dm;
		dm = new DisplayMetrics();
		this.getWindowManager().getDefaultDisplay().getMetrics(dm);
		sWidth = dm.widthPixels;
		sHeight=dm.heightPixels;
		declare=(AppDiancan)getApplicationContext();
//		selectedProducts=declare.getHistory().getHisSelectedProducts();
//		if(selectedProducts.size()>0)
//		{
//			CreateList();
//		}
	}
	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
//		if(selectedProducts.size()>0)
//		{
//			if(hisAdapter==null)
//			{
//				CreateList();
//			}
//			else {
//				UpdateHashList();
//				hisAdapter.setmItemList(hashList);
//				hisAdapter.notifyDataSetChanged();
//			}
//		}
	}
	public void CreateList()
	{
		UpdateHashList();
		hisAdapter=new HistoryListAdapter(this, hashList,R.layout.historylistitem, 
				new String[] { "date", "sum"},
				new int[] { R.id.txtDate, R.id.txtSum});
		setListAdapter(hisAdapter);
	}
	public void UpdateHashList()
	{
	}
	class HistoryListAdapter extends SimpleAdapter implements ListAdapter{
		private ArrayList<HashMap<String, Object>> mItemList;
		public ArrayList<HashMap<String, Object>> getmItemList() {
			return mItemList;
		}
		public void setmItemList(ArrayList<HashMap<String, Object>> hashList) {
			this.mItemList = hashList;
		}
		public HistoryListAdapter(Context context,
				List<? extends Map<String, ?>> data, int resource,
				String[] from, int[] to) {
			super(context, data, resource, from, to);
			// TODO Auto-generated constructor stub
			mItemList = (ArrayList<HashMap<String, Object>>) data;
		}
		
	}
	@Override
	protected void onListItemClick(ListView l, View v, int position, long id) {
		HistoryGroup parent = (HistoryGroup) getParent();
		LocalActivityManager manager = parent.getLocalActivityManager();
		final LinearLayout contain = (LinearLayout) parent.findViewById(R.id.group_continer);
		Activity activity=manager.getCurrentActivity();
		Window w1=activity.getWindow();
		View v1=w1.getDecorView();
	    
		Intent in = new Intent(getParent(), HistoryPage.class);
		in.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
		in.putExtra("index", position+"");		
		Window window = manager.startActivity("HistoryPage", in);	
		final View view=window.getDecorView();
		
		Animation sAnimation=new HistoryRotateAnim(0, -90, 0.0f, 0.0f,sWidth/2 , sHeight/2,true);
		final Animation sAnimation1=new HistoryRotateAnim(90, 0, 0.0f, 0.0f,sWidth/2, sHeight/2,false);
		sAnimation.setAnimationListener(new AnimationListener() {
			
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
				contain.addView(view);
				LayoutParams params=(LayoutParams) view.getLayoutParams();
		        params.width=LayoutParams.FILL_PARENT;
		        params.height=LayoutParams.FILL_PARENT;
		        view.setLayoutParams(params); 
				view.startAnimation(sAnimation1);
			}
		});
		v1.startAnimation(sAnimation);
		contain.removeAllViews();			
				
		super.onListItemClick(l, v, position, id);
	}
}
