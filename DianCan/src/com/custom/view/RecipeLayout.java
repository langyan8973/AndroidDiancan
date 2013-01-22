package com.custom.view;

import com.Utils.DisplayUtil;

import android.content.Context;
import android.graphics.Color;
import android.view.View;
import android.view.ViewGroup;

public class RecipeLayout extends ViewGroup {

	public RecipeLayout(Context context) {
		super(context);
		// TODO Auto-generated constructor stub
		LayoutParams layoutParams=new LayoutParams(LayoutParams.FILL_PARENT,LayoutParams.FILL_PARENT);
		this.setLayoutParams(layoutParams);
		this.setBackgroundColor(Color.parseColor("#FFCCCCCC"));
	}

	@Override
	protected void onLayout(boolean changed, int l, int t, int r, int b) {
		// TODO Auto-generated method stub
		System.out.println("重新定位调用");
		int count=getChildCount();
		for(int i=0;i<count;i++){
			View child=getChildAt(i);
			child.measure(r-l, b-t);
			if(i==0){
				child.layout(l,t,DisplayUtil.dip2px(80), b);
			}
			if(i==1){
				MyViewGroup myViewGroup=(MyViewGroup)child;
				myViewGroup.layout(myViewGroup.getmLeft(), t, myViewGroup.getmLeft()+r, b);
			}
		}
	}

}
