package com.custom;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import com.Utils.CustomViewBinder;
import com.diancan.R;
import com.mode.SelectedMenuObj;
import com.model.OrderItem;

public class ClassListViewWidget extends LinearLayout {

	int mWidth,mHeight;
	List<OrderItem> orderItemList;
	ArrayList<HashMap<String, Object>> hashList;
	TextView titleTextView;
	public ListView listView;
	
	public ClassListViewWidget(Context context,List<OrderItem> orderItems,ArrayList<HashMap<String, Object>> hList,
			String strtitle) {
		super(context);
		// TODO Auto-generated constructor stub
		this.setOrientation(LinearLayout.VERTICAL);
		this.setPadding(10, 10, 10, 10);
		orderItemList=orderItems;
		hashList=hList;
		
		titleTextView=new TextView(context);
		titleTextView.setLayoutParams(new LayoutParams(120,60));
		titleTextView.setGravity(Gravity.CENTER);
		titleTextView.setTextColor(Color.WHITE);
		titleTextView.setTextSize(16);
		titleTextView.setText(strtitle);
		titleTextView.setBackgroundDrawable(context.getResources().getDrawable(R.drawable.categoryname));
		this.addView(titleTextView);
		
		LinearLayout line=new LinearLayout(context);
		line.setLayoutParams(new LayoutParams(LayoutParams.FILL_PARENT,3));
		this.addView(line);
		
		listView=new ListView(context);
		listView.setDivider(new ColorDrawable(Color.rgb(204, 204, 204)));
		listView.setSelector(new ColorDrawable(Color.argb(0, 255, 255, 255)));
		listView.setCacheColorHint(Color.TRANSPARENT);
		listView.setBackgroundDrawable(context.getResources().getDrawable(R.drawable.defaultcolor));
		this.addView(listView);

	}
	public void setListViewHeight(ListView lv) {
        ListAdapter la = lv.getAdapter();
        if(null == la) {
            return;
        }
        // calculate height of all items.
        int h = 0;
        final int cnt = la.getCount();
        for(int i=0; i<cnt; i++) {
            View item = la.getView(i, null, lv);
            item.measure(0, 0);
            h += item.getMeasuredHeight();
        }
        // reset ListView height
        ViewGroup.LayoutParams lp = lv.getLayoutParams();
        lp.height = h + (lv.getDividerHeight() * (cnt - 1));
        lp.width=android.view.ViewGroup.LayoutParams.FILL_PARENT;
        lv.setLayoutParams(lp);
    }
	public boolean isEmpty()
	{
		if(hashList.size()<=0)
			return true;
		else {
			return false;
		}
	}
	
	public int getmWidth() {
		return mWidth;
	}
	public void setmWidth(int mWidth) {
		this.mWidth = mWidth;
	}
	public int getmHeight() {
		return mHeight;
	}
	public void setmHeight(int mHeight) {
		this.mHeight = mHeight;
	}
	
	public List<OrderItem> getOrderItemList() {
		return orderItemList;
	}
	public void setOrderItemList(List<OrderItem> orderItemList) {
		this.orderItemList = orderItemList;
	}
	public ArrayList<HashMap<String, Object>> getHashList() {
		return hashList;
	}
	public void setHashList(ArrayList<HashMap<String, Object>> hashList) {
		this.hashList = hashList;
	}
	public TextView getTitleTextView() {
		return titleTextView;
	}
	public void setTitleTextView(TextView titleTextView) {
		this.titleTextView = titleTextView;
	}
	public ListView getListView() {
		return listView;
	}
	public void setListView(ListView listView) {
		this.listView = listView;
	}

}
