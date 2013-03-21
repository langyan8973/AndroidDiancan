package com.diancan.custom.view;

import com.diancan.R;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.LinearLayout;

public class LeftShadowLayout extends LinearLayout {
	int shadowWidth;
	public LeftShadowLayout(Context context, AttributeSet attrs) {
		super(context, attrs);
		// TODO Auto-generated constructor stub
		shadowWidth = (int)context.getResources().getDimension(R.dimen.left_shadow_width);
	}

	@Override
	protected void onLayout(boolean changed, int l, int t, int r, int b) {
		// TODO Auto-generated method stub
		View listView = getChildAt(0);
		if(listView.getVisibility()==View.VISIBLE){
			listView.layout(shadowWidth, t, r+shadowWidth, b);
		}
	}

}
