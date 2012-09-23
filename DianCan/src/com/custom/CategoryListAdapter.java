package com.custom;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import com.diancan.R;

public class CategoryListAdapter extends SimpleAdapter{
	int[] ids;
	String selectedName;
	private ArrayList<HashMap<String, Object>> mItemList;
	public String getSelectedName() {
		return selectedName;
	}

	public void setSelectedName(String selectedName) {
		this.selectedName = selectedName;
	}
	
	public ArrayList<HashMap<String, Object>> getmItemList() {
		return mItemList;
	}
	public void setmItemList(ArrayList<HashMap<String, Object>> hashList) {
		this.mItemList = hashList;
	}

	public CategoryListAdapter(Context context,
			List<? extends Map<String, ?>> data, int resource,
			String[] from, int[] to) {
		super(context, data, resource, from, to);
		// TODO Auto-generated constructor stub
		ids=to;
		mItemList = (ArrayList<HashMap<String, Object>>) data;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		// TODO Auto-generated method stub
		View localView = super.getView(position, convertView, parent);
		HashMap<String, Object> map=mItemList.get(position);

		TextView nameView=(TextView)localView.findViewById(R.id.category_name);
		nameView.setTextColor(Color.WHITE);
		if(!selectedName.equals("")&&selectedName.equals(map.get("name").toString()))
		{
			localView.setBackgroundDrawable(new ColorDrawable(Color.parseColor("#FFF28931")));
		}
		else {
			localView.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
		}
		
        return localView;
	}
}
