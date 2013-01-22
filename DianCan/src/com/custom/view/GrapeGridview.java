package com.custom.view;

import java.util.List;

import android.content.Context;
import android.graphics.Color;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.GridView;

public class GrapeGridview extends GridView {
		public GrapeGridview(Context context) {
		super(context);
		// TODO Auto-generated constructor stub
		}
		public GrapeGridview(Context context, AttributeSet attrs) {
		super(context, attrs);
		// TODO Auto-generated constructor stub
		}

		public GrapeGridview(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		// TODO Auto-generated constructor stub
		}
		     
		@Override
		public boolean dispatchTouchEvent(MotionEvent ev) {
		// TODO Auto-generated method stub
		if(ev.getAction() == MotionEvent.ACTION_MOVE){
		           return true;//��ֹGridview���л���
		       }
		return super.dispatchTouchEvent(ev);
		}
		
}
