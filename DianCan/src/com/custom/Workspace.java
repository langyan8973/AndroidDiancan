package com.custom;
import java.util.List;

import com.Utils.DisplayUtil;
import com.mode.SelectedMenuObj;
import com.model.OrderItem;

import android.R.integer;
import android.content.Context;  
import android.view.View;  
import android.view.ViewGroup;  
  
public class Workspace extends ViewGroup {  
  
	int sWidth,sHeight;
    public Workspace(Context context,int width,int height,List<OrderItem> orderItems,ImageDownloader imgDownloader,int cindex) {  
        super(context);  
        // TODO Auto-generated constructor stub  
        sWidth=width;
        sHeight=height;
        addView(new MyViewGroup(getContext(),sWidth,sHeight,orderItems,imgDownloader,cindex));
    }  
  
    @Override  
    protected void onLayout(boolean changed, int l, int t, int r, int b) {  
        // TODO Auto-generated method stub  
    	int pxWidth=DisplayUtil.dip2px(sWidth);
    	int pxHeight=DisplayUtil.dip2px(sHeight);
        final int count = getChildCount();  
        for (int i = 0; i < count; i++) {  
            final View child = getChildAt(i);  
            child.measure(r - l, b - t);  
            child.layout(0, 0, pxWidth, pxHeight);  
        }  
    } 
    
//    public void InitListView(List<SelectedMenuObj> menuObjs,ImageDownloader imgDownloader)
//    {
//    	removeAllViews();
//    	addView(new MyViewGroup(getContext(),sWidth,sHeight,menuObjs,imgDownloader,cindex));
//    }
  
}  
