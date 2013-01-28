package com.diancan;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import com.diancan.Helper.RecipeListHttpHelper;
import com.diancan.Utils.CustomViewBinder;
import com.diancan.Utils.DisplayUtil;
import com.diancan.Utils.JsonUtils;
import com.diancan.Utils.MenuUtils;
import com.diancan.custom.adapter.AdapterCategoryList;
import com.diancan.custom.view.MyViewGroup;
import com.diancan.custom.view.RecipeLayout;
import com.diancan.diancanapp.AppDiancan;
import com.diancan.http.HttpCallback;
import com.diancan.http.HttpDownloader;
import com.diancan.http.HttpHandler;
import com.diancan.http.ImageDownloader;
import com.diancan.model.Category;
import com.diancan.model.Order;
import com.diancan.model.OrderItem;
import com.diancan.model.Recipe;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.SparseArray;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

public class RecipeList extends Activity implements HttpCallback,OnItemClickListener {
	RecipeLayout mRecipeLayout;
	ListView categoryList;
	MyViewGroup mRecipeGroup;
	AppDiancan appDiancan;	
	List<Category> m_arr;
	List<OrderItem> mOrderItemarr;
	ArrayList<HashMap<String, Object>> hashlist;
	AdapterCategoryList simpleAdapter;
	int sWidth,sHeight,cIndex,rIndex;
	ImageDownloader imgDownloader;
	
	RecipeListHttpHelper recipeListHttpHelper;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		mRecipeLayout=new RecipeLayout(this);
		setContentView(mRecipeLayout);
		appDiancan=(AppDiancan)getApplicationContext();
		recipeListHttpHelper=new RecipeListHttpHelper(this,appDiancan);
		cIndex=-1;
		sWidth = DisplayUtil.DPWIDTH;
		sHeight=DisplayUtil.DPHEIGHT-63;
		hashlist=new ArrayList<HashMap<String,Object>>();
		InitElement();
		RequestAllTypes();
	}
	
	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		if(m_arr==null){
			return;
		}
		categoryList.postDelayed(new Runnable() {
	        @Override
	        public void run() {	
	        	HashMap<String, Object> map=hashlist.get(cIndex);
        		String nameString=map.get("name").toString();
        		simpleAdapter.setSelectedName(nameString);
        		simpleAdapter.notifyDataSetChanged();
        		DisplayRecipeList(cIndex);
	        }
	    }, 100);
	}
	

	@Override
	public void RequestComplete(Message msg) {
		// TODO Auto-generated method stub
		switch(msg.what) {  
        case HttpHandler.REQUEST_ALLCATEGORY: 
        	RequestRecipes();
            break;  
        case HttpHandler.REQUEST_ALLRECIPES:
        	UpdateOrder();
        	break;
        case HttpHandler.REQUEST_ORDER_BY_ID:
        	InitCategoryList();
        	break;
        } 
	}

	@Override
	public void RequestError(String errString) {
		// TODO Auto-generated method stub
		ShowError(errString);
	}
	
	@Override
	public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
		// TODO Auto-generated method stub
		HashMap<String, Object> map=hashlist.get(arg2);
		simpleAdapter.setSelectedName(map.get("name").toString());
		cIndex=arg2;
		DisplayRecipeList(cIndex);
	}
	
	private void InitElement(){
		imgDownloader=new ImageDownloader();
		categoryList=new ListView(this);
	    categoryList.setDivider(new ColorDrawable(Color.parseColor("#FFCCCCCC")));
	    categoryList.setDividerHeight(DisplayUtil.dip2px(20));
	    categoryList.setSelector(this.getResources().getDrawable(R.drawable.co));
	    categoryList.setCacheColorHint(Color.parseColor("#00000000"));
	    mRecipeLayout.addView(categoryList);
	    
	}
	
	/**
  	 * 显示错误信息
  	 * @param strMess
  	 */
  	public void ShowError(String strMess) {
		Toast toast = Toast.makeText(RecipeList.this, strMess, Toast.LENGTH_SHORT); 
        toast.show();
	}
  	
  	/**
	 * 请求所有的菜的类型
	 */
	public void RequestAllTypes()
	{
		recipeListHttpHelper.RequestAllTypes();
	}
	
	/**
  	 * 请求所有的菜
  	 */
  	public void RequestRecipes()
	{	
  		recipeListHttpHelper.RequestRecipes();
	}
  	
  	public void UpdateOrder()
	{
		if(appDiancan.curOrder==null||appDiancan.curOrder.getRestaurant().getId()!=appDiancan.restaurantId)
		{
			InitCategoryList();
		}
		else {
			recipeListHttpHelper.RequestOrderById();
		}
	}
	
	/***
	 * 显示分类列表
	 */
	public void InitCategoryList()
	{
		try {
			m_arr = appDiancan.getMenuListDataObj().categories;
			
		} catch (Exception e) {
			// TODO Auto-generated catch block			 
			e.printStackTrace();
			ShowError(e.getMessage());
        	return;
		}	

		for(final Category category:m_arr)
		{		
			HashMap<String, Object> map=new HashMap<String, Object>();
			map.put("name", category.getName()); 
			map.put("id", category.getId());
			hashlist.add(map);
		}
		simpleAdapter=new AdapterCategoryList(this, hashlist,
				R.layout.categorylist_item, new String[] { "name","id" },
				new int[] { R.id.category_name,R.id.category_id});
		simpleAdapter.setViewBinder(new CustomViewBinder());
		simpleAdapter.setSelectedName("");
		categoryList.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
		categoryList.setAdapter(simpleAdapter);
		categoryList.setOnItemClickListener(this);
		
		categoryList.postDelayed(new Runnable() {
	        @Override
	        public void run() {	
	        	HashMap<String, Object> map=hashlist.get(0);
        		String nameString=map.get("name").toString();
        		simpleAdapter.setSelectedName(nameString);
        		simpleAdapter.notifyDataSetChanged();
        		cIndex=0;
        		DisplayRecipeList(cIndex);
	        }
	    }, 100);
		
	}
	
	
	public void DisplayRecipeList(int position)
	{
		Category category=m_arr.get(position);
		SparseArray<List<OrderItem>> recipeMap=appDiancan.getMenuListDataObj().getRecipeMap();
		if(recipeMap.indexOfKey(category.getId())<0)
		{
			try {
				List<Recipe> recipes = MenuUtils
						.getRecipesByCategory(category.getId(),appDiancan.udidString);
				List<OrderItem> orderItems=new ArrayList<OrderItem>();
				Iterator<Recipe> iterator;
				for(iterator=recipes.iterator();iterator.hasNext();)
				{
					OrderItem oItem=new OrderItem();
					oItem.setRecipe(iterator.next());
					oItem.setCount(0);
					orderItems.add(oItem);
				}
				recipeMap.put(category.getId(), orderItems);
			} catch (Exception e) {
				// TODO: handle exception
				System.out.println(e.getMessage());
				return;
			}
		}
		mOrderItemarr=appDiancan.getMenuListDataObj().getRecipeMap().get(category.getId());
		if(mOrderItemarr==null||mOrderItemarr.size()==0)
		{
			return;
		}
		if(appDiancan.curOrder!=null)
		{
			//同步数据
			appDiancan.getMenuListDataObj().SyncMenuListByCategory(category, appDiancan.curOrder);
		}
		int left=0;
		if(mRecipeGroup!=null)
		{
			left=mRecipeGroup.getmLeft();
			mRecipeLayout.removeView(mRecipeGroup);
		}
		mRecipeGroup=null;
		mRecipeGroup=new MyViewGroup(this, sWidth, sHeight,left,cIndex,mOrderItemarr, imgDownloader);
	    mRecipeLayout.addView(mRecipeGroup);
	}
	
	
}
