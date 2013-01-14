package com.diancan;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import com.Utils.CustomViewBinder;
import com.Utils.DisplayUtil;
import com.Utils.JsonUtils;
import com.Utils.MenuUtils;
import com.custom.ImageDownloader;
import com.custom.MyViewGroup;
import com.custom.RecipeLayout;
import com.declare.Declare;
import com.download.HttpDownloader;
import com.model.Category;
import com.model.Order;
import com.model.OrderItem;
import com.model.Recipe;
import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

public class RecipeList extends Activity {
	RecipeLayout mRecipeLayout;
	ListView categoryList;
	MyViewGroup mRecipeGroup;
	Declare declare;	
	List<Category> m_arr;
	List<OrderItem> mOrderItemarr;
	ArrayList<HashMap<String, Object>> hashlist;
	RecipeListAdapter simpleAdapter;
	int sWidth,sHeight,cIndex,rIndex;
	ImageDownloader imgDownloader;
	
	
	private Handler httpHandler = new Handler() {  
        public void handleMessage (Message msg) {//此方法在ui线程运行   
            switch(msg.what) {  
            case 0: 
            	String errString=msg.obj.toString();
            	ShowError(errString);
                break;   
            case 1: 
            	RequestRecipes();
                break;  
            case 2:
            	UpdateOrder();
            	break;
            case 3:
            	InitCategoryList();
            	break;
            }  
        }  
    };
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		mRecipeLayout=new RecipeLayout(this);
		setContentView(mRecipeLayout);
		declare=(Declare)getApplicationContext();
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
		new Thread(new Runnable() {
			
			@Override
			public void run() {
				// TODO Auto-generated method stub
				
				try {
					declare.getMenuListDataObj().categories=MenuUtils.getAllCategory(declare.restaurantId,declare.udidString);
					declare.hashTypes=new HashMap<String, String>();
					Iterator<Category> iterator;
					for(iterator=declare.getMenuListDataObj().categories.iterator();iterator.hasNext();){
						Category category=iterator.next();
						declare.hashTypes.put(category.getId()+"", category.getName());
					}
					httpHandler.obtainMessage(1).sendToTarget();
				} catch (Exception e) {
					// TODO Auto-generated catch block
					httpHandler.obtainMessage(0,e.getMessage()).sendToTarget();
				}
				
			}
		}).start();
		
	}
	
	/**
  	 * 请求所有的菜
  	 */
  	public void RequestRecipes()
	{	
  		new Thread(new Runnable() {
			
			@Override
			public void run() {
				// TODO Auto-generated method stub
				
				try {
					List<Recipe> recipes=MenuUtils.getAllRecipes(declare.restaurantId,declare.udidString);
					if(recipes==null){
						String strnull="获取菜品失败！";
						httpHandler.obtainMessage(0,strnull).sendToTarget();
						return;
					}
					HashMap<Integer, List<OrderItem>> recipeHashMap=declare.getMenuListDataObj().getRecipeMap();
					
					Iterator<Recipe> iterator;
					for(iterator=recipes.iterator();iterator.hasNext();)
					{
						Recipe recipe=iterator.next();
						if(recipeHashMap.containsKey(recipe.getCid()))
						{
							List<OrderItem> orderItems=recipeHashMap.get(recipe.getCid());
							OrderItem oItem=new OrderItem();
							oItem.setRecipe(recipe);
							oItem.setCount(0);
							orderItems.add(oItem);
						}
						else {
							List<OrderItem> orderItems=new ArrayList<OrderItem>();
							recipeHashMap.put(recipe.getCid(), orderItems);
							OrderItem oItem=new OrderItem();
							oItem.setRecipe(recipe);
							oItem.setCount(0);
							orderItems.add(oItem);
						}
							
					}
					httpHandler.obtainMessage(2).sendToTarget();
				} catch (Exception e) {
					// TODO Auto-generated catch block
					httpHandler.obtainMessage(0,e.getMessage()).sendToTarget();
				}
				
			}
		}).start();
  			
	}
  	
  	public void UpdateOrder()
	{
		if(declare.curOrder==null)
		{
			InitCategoryList();
		}
		else {
			new Thread(new Runnable() {
				
				@Override
				public void run() {
					// TODO Auto-generated method stub
					
					try {
						String resultString = HttpDownloader.getString(MenuUtils.initUrl+ "restaurants/"+declare.restaurantId+"/orders/" +declare.curOrder.getId(),
								declare.udidString);
						if(resultString==null)
						{
							httpHandler.obtainMessage(0,"编码错误！").sendToTarget();
							return;
						}
						else {
						Order order=JsonUtils.ParseJsonToOrder(resultString);
						declare.curOrder=order;
						httpHandler.obtainMessage(3).sendToTarget();
					}
					} catch (Exception e) {
						// TODO Auto-generated catch block
						httpHandler.obtainMessage(0,e.getMessage()).sendToTarget();
					}
					
				}
			}).start();
	}
	}
	
	/***
	 * 显示分类列表
	 */
	public void InitCategoryList()
	{
		try {
			m_arr = declare.getMenuListDataObj().categories;
			
		} catch (Exception e) {
			// TODO Auto-generated catch block			 
			e.printStackTrace();
			Toast toast = Toast.makeText(RecipeList.this, "网络异常", Toast.LENGTH_SHORT); 
        	toast.show();
        	return;
		}	

		for(final Category category:m_arr)
		{		
			HashMap<String, Object> map=new HashMap<String, Object>();
			map.put("name", category.getName()); 
			map.put("id", category.getId());
			hashlist.add(map);
		}
		simpleAdapter=new RecipeListAdapter(this, hashlist,
				R.layout.categorylist_item, new String[] { "name","id" },
				new int[] { R.id.category_name,R.id.category_id});
		simpleAdapter.setViewBinder(new CustomViewBinder());
		simpleAdapter.setSelectedName("");
		categoryList.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
		categoryList.setAdapter(simpleAdapter);
		categoryList.setOnItemClickListener(new ListItemClick());
		
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
	
	/***
	 * 显示点击分类的所有菜
	 * @author liuyan
	 *
	 */
	public class ListItemClick implements OnItemClickListener{

		@Override
		public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
				long arg3) {
			// TODO Auto-generated method stub
			HashMap<String, Object> map=hashlist.get(arg2);
			simpleAdapter.setSelectedName(map.get("name").toString());
			cIndex=arg2;
			DisplayRecipeList(cIndex);
		}		
	}
	
	
	public void DisplayRecipeList(int position)
	{
		Category category=m_arr.get(position);
		HashMap<Integer, List<OrderItem>> recipeMap=declare.getMenuListDataObj().getRecipeMap();
		if(!recipeMap.containsKey(category.getId()))
		{
			try {
				List<Recipe> recipes = MenuUtils
						.getRecipesByCategory(category.getId(),declare.udidString);
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
		mOrderItemarr=declare.getMenuListDataObj().getRecipeMap().get(category.getId());
		if(mOrderItemarr==null||mOrderItemarr.size()==0)
		{
			return;
		}
		if(declare.curOrder!=null)
		{
			//同步数据
			declare.getMenuListDataObj().SyncMenuListByCategory(category, declare.curOrder);
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
	
	
	public class RecipeListAdapter extends SimpleAdapter{

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

		public RecipeListAdapter(Context context,
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
			if(!selectedName.equals("")&&selectedName.equals(map.get("name").toString()))
			{
				localView.setBackgroundDrawable(RecipeList.this.getResources().getDrawable(R.drawable.co));
				nameView.setTextColor(Color.WHITE);
			}
			else {
				localView.setBackgroundDrawable(RecipeList.this.getResources().getDrawable(R.drawable.c));
				nameView.setTextColor(Color.BLACK);
			}
			
	        return localView;
		}
		
	}
}
