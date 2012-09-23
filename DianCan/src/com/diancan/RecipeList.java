package com.diancan;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import com.Utils.CustomViewBinder;
import com.Utils.DisplayUtil;
import com.Utils.MenuUtils;
import com.custom.ImageDownloader;
import com.custom.ListImgDownloader;
import com.custom.Workspace;
import com.declare.Declare;
import com.download.HttpDownloader;
import com.mode.CategoryObj;
import com.mode.SelectedMenuObj;
import com.model.Category;
import com.model.OrderItem;
import com.model.Recipe;
import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

public class RecipeList extends Activity {
	ListView categoryList;
	Declare declare;	
	List<Category> m_arr;
	List<OrderItem> mOrderItemarr;
	ArrayList<HashMap<String, Object>> hashlist;
	RecipeListAdapter simpleAdapter;
	int sWidth,sHeight,cIndex,rIndex;
	LinearLayout groupLayout;
	ImageDownloader imgDownloader;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.recipelist);
		categoryList=(ListView)findViewById(R.id.CategoryList);
		groupLayout=(LinearLayout)findViewById(R.id.group_root);
		imgDownloader=new ImageDownloader();
		declare=(Declare)getApplicationContext();
		cIndex=-1;
		
		sWidth = DisplayUtil.DPWIDTH;
		sHeight=DisplayUtil.DPHEIGHT-63;
		
		hashlist=new ArrayList<HashMap<String,Object>>();	
		InitCategoryList();
	}
	
	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		categoryList.postDelayed(new Runnable() {
	        @Override
	        public void run() {	
	        	if(cIndex==-1)
	        	{
	        		HashMap<String, Object> map=hashlist.get(1);
	        		String nameString=map.get("name").toString();
	        		simpleAdapter.setSelectedName(nameString);
	        		simpleAdapter.notifyDataSetChanged();
	        		cIndex=1;
	        		DisplayRecipeList(cIndex);
	        	}
	        	else {
	        		HashMap<String, Object> map=hashlist.get(cIndex);
	        		String nameString=map.get("name").toString();
	        		simpleAdapter.setSelectedName(nameString);
	        		simpleAdapter.notifyDataSetChanged();
	        		DisplayRecipeList(cIndex);
				}
	        }
	    }, 100);
	}
	
	/***
	 * 显示分类列表
	 */
	public void InitCategoryList()
	{
		try {
			declare.getMenuListDataObj().categories=MenuUtils.getAllCategory();
			m_arr = declare.getMenuListDataObj().categories;
			
		} catch (Exception e) {
			// TODO Auto-generated catch block			 
			e.printStackTrace();
			Toast toast = Toast.makeText(RecipeList.this, "网络异常", Toast.LENGTH_SHORT); 
        	toast.show();
        	return;
		}	
//		if(categories!=null&&categories.size()>0)
//		{
//			for(Category category:categories)
//			{
//				declare.getMenuListDataObj().categoryObjs.add(new CategoryObj(category));
//			}
//		}
//		else {
//			return;
//		}
//		m_arr=declare.getMenuListDataObj().getCategoryObjs();
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
						.getRecipesByCategory(category.getId());
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
//		if(categoryObj.getSelectedMenuObjs().size()==0)
//		{
//			try {
//				List<Recipe> recipes = MenuUtils
//						.getRecipesByCategory(categoryObj.getId());
//				Iterator<Recipe> iterator;
//				for(iterator=recipes.iterator();iterator.hasNext();)
//				{
//					categoryObj.getSelectedMenuObjs().add(new SelectedMenuObj(iterator.next()));
//				}
//			} catch (Exception e) {
//				// TODO: handle exception
//				System.out.println(e.getMessage());
//				return;
//			}
//		}
		mOrderItemarr=declare.getMenuListDataObj().getRecipeMap().get(category.getId());
		if(mOrderItemarr==null||mOrderItemarr.size()==0)
		{
			return;
		}
		if(declare.curOrder!=null)
		{
			//同步数据
//			declare.SyncMenuListByCategory(categoryObj);
			declare.getMenuListDataObj().SyncMenuListByCategory(category, declare.curOrder);
		}
		groupLayout.removeAllViews();
		Workspace groupWorkspace=new Workspace(this, sWidth-80, sHeight,mOrderItemarr,imgDownloader,cIndex);
		groupLayout.addView(groupWorkspace);

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
