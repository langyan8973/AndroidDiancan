package com.diancan;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.Utils.CustomViewBinder;
import com.Utils.MenuUtils;
import com.declare.Declare;
import com.diancan.MyTable.OverBtnOnclick;
import com.diancan.MyTable.TableListAdapter;
import com.mode.CategoryObj;
import com.mode.SelectedMenuObj;
import com.mode.SelectedProduct;
import com.model.Category;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;

public class HistoryPage extends Activity {
	LinearLayout rootLayout;
	LinearLayout rootLyt;
	SelectedProduct selectedProduct;
	Button useButton;
	TextView sumTextView;
	TextView titleTextView;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.historypage);
		
		rootLayout=(LinearLayout)findViewById(R.id.rootLayoutHis);
		rootLyt=(LinearLayout)findViewById(R.id.rootLytHis);
		sumTextView=(TextView)findViewById(R.id.sumTextHis);
		sumTextView.setTextColor(Color.DKGRAY);
		sumTextView.setTextSize(30);
		
		titleTextView=(TextView)findViewById(R.id.historytitle);
		titleTextView.setTextColor(Color.BLUE);
		titleTextView.setTextSize(22);
		
		useButton=(Button)findViewById(R.id.usebtn);
		
		Declare declare=(Declare)getApplicationContext();
		Intent intent =getIntent();
		int index=Integer.parseInt((String)intent.getSerializableExtra("index"));
		selectedProduct=declare.getHistory().getHisSelectedProducts().get(index);
		if(selectedProduct!=null)
		{
			titleTextView.setText(selectedProduct.getStrdate()+"的订单");
			CreateElements();
		}

	}
	private void CreateElements()
    {
    	
    }
	private void UpdateHashList(List<SelectedMenuObj> menuInfos,ArrayList<HashMap<String, Object>> hashList)
	{
		hashList.clear();
		for (Iterator iterator = menuInfos.iterator(); iterator.hasNext();) {
			SelectedMenuObj menuInfo = (SelectedMenuObj) iterator.next();
			HashMap<String, Object> map = new HashMap<String, Object>();
			map.put("title", menuInfo.getName());
			map.put("price", "¥ "+menuInfo.getPrice());
			String strCount=menuInfo.getCount()+"";
			map.put("count", strCount);
			
			hashList.add(map);
		}
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
	public class HistoryListAdapter extends SimpleAdapter {

		public ArrayList<HashMap<String, Object>> getmItemList() {
			return mItemList;
		}
		public void setmItemList(ArrayList<HashMap<String, Object>> hashList) {
			this.mItemList = hashList;
		}
		int count = 0;
		int[] idArray;
		List<SelectedMenuObj> menuList;
		Context thisContext;
	    private ArrayList<HashMap<String, Object>> mItemList;
	    public HistoryListAdapter(Context context, List<? extends Map<String, Object>> data,
	            int resource, String[] from, int[] to) {
	        super(context, data, resource, from, to);
	        thisContext=context;
	        mItemList = (ArrayList<HashMap<String, Object>>) data;
	        if(data == null){
	            count = 0;
	        }else{
	            count = data.size();
	        }
	        idArray=to;
	    }
	    public int getCount() {
	        return mItemList.size();
	    }

	    public Object getItem(int pos) {
	        return pos;
	    }

	    public long getItemId(int pos) {
	        return pos;
	    }
	    
	    public List<SelectedMenuObj> getMenuList() {
			return menuList;
		}
		public void setMenuList(List<SelectedMenuObj> menuList) {
			this.menuList = menuList;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			// TODO Auto-generated method stub
			View localView = super.getView(position, convertView, parent);
			ImageView addview=(ImageView)localView.findViewById(R.id.imgadd);
			addview.setVisibility(View.INVISIBLE);
			ImageView delview=(ImageView)localView.findViewById(R.id.imgdelete);
			delview.setVisibility(View.INVISIBLE);
			return localView;
		}

	}
}
