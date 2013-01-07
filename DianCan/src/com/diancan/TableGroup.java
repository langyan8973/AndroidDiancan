package com.diancan;

import com.declare.Declare;

import android.app.ActivityGroup;
import android.app.LocalActivityManager;
import android.content.Intent;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.Window;
import android.view.ViewGroup.LayoutParams;
import android.widget.LinearLayout;

public class TableGroup extends ActivityGroup {
	public LinearLayout rootLayout;
	public LocalActivityManager activityManager;
	
	Declare declare;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.tablegroup);
		rootLayout=(LinearLayout)findViewById(R.id.table_continer);
		rootLayout.removeAllViews();
		
		activityManager = getLocalActivityManager();
		declare=(Declare)getApplicationContext();
		
		if(declare.curOrder!=null)
		{
			Intent intent=new Intent(TableGroup .this,MyTable.class);
	        Window subActivity=getLocalActivityManager().startActivity("MyTable",intent);
	        View view=subActivity.getDecorView();
	        rootLayout.addView(view);  

	        LayoutParams params=(LayoutParams) view.getLayoutParams();
	        params.width=LayoutParams.FILL_PARENT;
	        params.height=LayoutParams.FILL_PARENT;
	        view.setLayoutParams(params);
		}
		else {
			Intent intent=new Intent(TableGroup .this,TableCodePage.class);
			intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
	        Window subActivity=getLocalActivityManager().startActivity("TableCodePage",intent);
	        View view=subActivity.getDecorView();
	        rootLayout.addView(view);  

	        LayoutParams params=(LayoutParams) view.getLayoutParams();
	        params.width=LayoutParams.FILL_PARENT;
	        params.height=LayoutParams.FILL_PARENT;
	        view.setLayoutParams(params);
		}
	}

}
