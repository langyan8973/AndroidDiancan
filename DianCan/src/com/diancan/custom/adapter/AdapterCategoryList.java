package com.diancan.custom.adapter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.diancan.R;
import com.diancan.RecipeList;

import android.content.Context;
import android.graphics.Color;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SimpleAdapter;
import android.widget.TextView;

public class AdapterCategoryList extends SimpleAdapter {

	int[] ids;
	String selectedName;
	Context mContext;
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

	public AdapterCategoryList(Context context,
			List<? extends Map<String, ?>> data, int resource,
			String[] from, int[] to) {
		super(context, data, resource, from, to);
		// TODO Auto-generated constructor stub
		mContext=context;
		ids=to;
		mItemList = (ArrayList<HashMap<String, Object>>) data;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		// TODO Auto-generated method stub
		View localView = super.getView(position, convertView, parent);
		HashMap<String, Object> map=mItemList.get(position);

		TextView nameView=(TextView)localView.findViewById(R.id.category_name);
		if(!selectedName.equals("")&&selectedName.equals(map.get("name").toString()))
		{
			localView.setBackgroundDrawable(mContext.getResources().getDrawable(R.drawable.co));
			nameView.setTextColor(Color.WHITE);
		}
		else {
			localView.setBackgroundDrawable(mContext.getResources().getDrawable(R.drawable.c));
			nameView.setTextColor(Color.BLACK);
		}
		
        return localView;
	}

}
