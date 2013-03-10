package com.diancan;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;

import com.diancan.Helper.OrderHelper;
import com.diancan.Helper.RecipeListHttpHelper;
import com.diancan.Utils.CustomViewBinder;
import com.diancan.Utils.DisplayUtil;
import com.diancan.Utils.JsonUtils;
import com.diancan.Utils.MenuUtils;
import com.diancan.custom.adapter.AdapterCategoryList;
import com.diancan.custom.animation.ListPopImgAnimation;
import com.diancan.custom.view.PinnedHeaderListView;
import com.diancan.custom.view.RecipeFrameLayout;
import com.diancan.diancanapp.AppDiancan;
import com.diancan.http.HttpCallback;
import com.diancan.http.HttpHandler;
import com.diancan.http.ImageDownloader;
import com.diancan.model.Category;
import com.diancan.model.MyRestaurant;
import com.diancan.model.Order;
import com.diancan.model.OrderItem;
import com.diancan.model.Recipe;
import com.diancan.sectionlistview.SectionListAdapter;
import com.diancan.sectionlistview.SectionListAdapter.AdapterViewHolder;
import com.diancan.sectionlistview.SectionListItem;

import android.R.integer;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Message;
import android.util.Log;
import android.util.SparseArray;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

public class RecipeList extends Activity implements HttpCallback,OnItemClickListener,OnClickListener {
	
	ListView categoryList;
	PinnedHeaderListView recipeListView;
	Button mSelectDeskBtn;
	Button mRefreshBtn;
	Button mCategoriesBtn;
	ImageView mPopImageView;
	TextView titleView;
	ProgressBar mProgressBar;
	
	AppDiancan appDiancan;	
	SparseArray<Category> m_arr;
	SparseArray<OrderItem> mItemDic;
	List<Recipe> allRecipes;
	ArrayList<HashMap<String, Object>> hashlist;
	AdapterCategoryList simpleAdapter;
	int sWidth,sHeight,cIndex,rIndex;
	ImageDownloader imgDownloader;
	MyRestaurant myRestaurant;
	RecipeListHttpHelper recipeListHttpHelper;
	RecipeFrameLayout recipeLayout;
	boolean isRefresh;
	static int TABCOUNT = 3;
	static int TIME_MEASURE = 500;
	int topbarHeight,tabbarHeight;
	private long clicktime=0;
	
	public class StandardArrayAdapter extends ArrayAdapter<SectionListItem> {

        public  SectionListItem[] items;

        public StandardArrayAdapter(final Context context,
                final int textViewResourceId, final SectionListItem[] items) {
            super(context, textViewResourceId, items);
            this.items = items;
        }

    }
	
	private StandardArrayAdapter arrayAdapter;

    private SectionListAdapter sectionAdapter;

    SectionListItem[] exampleArray;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.recipelist);
		
		appDiancan=(AppDiancan)getApplicationContext();
		recipeListHttpHelper=new RecipeListHttpHelper(this,appDiancan);
		myRestaurant=appDiancan.myRestaurant;
		cIndex=-1;
		
		sWidth = DisplayUtil.PIXWIDTH;
		sHeight = DisplayUtil.PIXHEIGHT;
		topbarHeight = (int)getResources().getDimension(R.dimen.topbar_height);
		tabbarHeight = (int)getResources().getDimension(R.dimen.tabbar_height);
		
		hashlist=new ArrayList<HashMap<String,Object>>();
		InitElement();
	}
	
	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();

		if(appDiancan.myOrder!=null){
			mSelectDeskBtn.setVisibility(View.GONE);
			mRefreshBtn.setVisibility(View.VISIBLE);
		}
		else{
			mSelectDeskBtn.setVisibility(View.VISIBLE);
			mRefreshBtn.setVisibility(View.GONE);
		}
		
		if(m_arr==null){
			isRefresh = false;
			RequestAllTypes();
		}
		else{
			if(appDiancan.myOrder!=null){
				isRefresh = true;
				recipeListHttpHelper.RequestOrderById();
			}
			else{
				RefreshRecipeList(null);
			}
		}
		
	}
	
	@Override
	public void RequestComplete(Message msg) {
		// TODO Auto-generated method stub
		mProgressBar.setVisibility(View.GONE);
		switch(msg.what) {  
        case HttpHandler.REQUEST_ALLCATEGORY: 
        	RequestAllRecipes();
            break;  
        case HttpHandler.REQUEST_ALLRECIPES:
        	allRecipes = (List<Recipe>)msg.obj;
        	SetCategories();
        	break;
        case HttpHandler.REQUEST_ORDER_BY_ID:
        	Order order=(Order)msg.obj;
        	
        	if(!isRefresh){
        		InitCategoryList(order);
        	}
        	else{
        		RefreshRecipeList(order);
        	}
        	SendSetCountMessage();
        	break;
        case HttpHandler.POST_RECIPE_COUNT:
			String jsString=msg.obj.toString();
			Order o=JsonUtils.ParseJsonToOrder(jsString);
			appDiancan.myOrder = o;
	    	appDiancan.myOrderHelper.SetOrderAndItemDic(o);
	    	SendSetCountMessage();
			break;
        } 
	}

	@Override
	public void RequestError(String errString) {
		// TODO Auto-generated method stub
		ShowError(errString);
	}
	
	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.bt_SelectDesk:
			Intent intent=new Intent(this,TableCodePage.class);
	        startActivity(intent);
			break;
		case R.id.bt_Refresh:
			isRefresh = true;
			mProgressBar.setVisibility(View.VISIBLE);
			recipeListHttpHelper.RequestOrderById();
			break;
		case R.id.bt_Categories:
			ClickCategories();
			break;
		case R.id.img:
			ImageView img = (ImageView)v;
			int position = Integer.parseInt(img.getTag().toString());
			ClickRecipeImg(position);
			break;
		case R.id.jiahao:
			int p = Integer.parseInt(v.getTag().toString());
			ClickIncreaseImg(p);
			break;
		case R.id.jianhao:
			int p1 = Integer.parseInt(v.getTag().toString());
			ClickDecreaseImg(p1,v);
			break;
		default:
			
			break;
		}
	}
	
	@Override
	public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
		// TODO Auto-generated method stub
		
		HashMap<String, Object> map=hashlist.get(arg2);
		cIndex=arg2;
		final int position = sectionAdapter.getPositionForSection(cIndex);
		
		recipeLayout.ToleftStart(0);
		recipeListView.postDelayed(new Runnable() {
			
			@Override
			public void run() {
				// TODO Auto-generated method stub
				recipeListView.smoothScrollToPositionFromTop(position,0,400);
				
			}
		}, 200);
	}
	

	private void InitElement(){
		Drawable[] layers={getResources().getDrawable(R.drawable.imagewaiting)};
		imgDownloader=new ImageDownloader(layers);
		recipeLayout = (RecipeFrameLayout)findViewById(R.id.myFrameLayout);
		categoryList=(ListView)findViewById(R.id.CategoryList);
		categoryList.setOnItemClickListener(this);
		recipeListView=(PinnedHeaderListView)findViewById(R.id.RecipeList);
		mSelectDeskBtn=(Button)findViewById(R.id.bt_SelectDesk);
		mSelectDeskBtn.setOnClickListener(this);
		mRefreshBtn = (Button)findViewById(R.id.bt_Refresh);
		mRefreshBtn.setOnClickListener(this);
		mCategoriesBtn = (Button)findViewById(R.id.bt_Categories);
		mCategoriesBtn.setOnClickListener(this);
		mPopImageView = (ImageView)findViewById(R.id.img_pop);
		titleView = (TextView)findViewById(R.id.tv_title);
		mProgressBar = (ProgressBar)findViewById(R.id.httppro);
		isRefresh = false;
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
		mProgressBar.setVisibility(View.VISIBLE);
		recipeListHttpHelper.RequestAllTypes();
	}
	
	public void RequestAllRecipes(){
		mProgressBar.setVisibility(View.VISIBLE);
		recipeListHttpHelper.RequestRecipes();
	}
	
	/**
  	 * 设置类别数据
  	 */
  	public void SetCategories()
	{	
  		m_arr=appDiancan.myRestaurant.getCategoryDic();
  		UpdateOrder();
	}
  	
  	public void UpdateOrder()
	{
		if(appDiancan.myOrder==null
				||appDiancan.myOrder.getRestaurant().getId()!=appDiancan.myRestaurant.getId())
		{
			InitCategoryList(null);
		}
		else {
			appDiancan.myOrderHelper.setCategoryDic(appDiancan.myRestaurant.getCategoryDic());
			mProgressBar.setVisibility(View.VISIBLE);
			recipeListHttpHelper.RequestOrderById();
		}
	}
	
	/***
	 * 显示分类列表
	 */
	public void InitCategoryList(Order order)
	{
		if(order!=null){
			appDiancan.myOrder = order;
			if(appDiancan.myOrderHelper==null){
				appDiancan.myOrderHelper = new OrderHelper(appDiancan.myOrder);
			}
			else{
				appDiancan.myOrderHelper.SetOrderAndItemDic(order);
			}
			titleView.setText(myRestaurant.getName()+"-"+order.getDesk().getName());
		}
		else{
			titleView.setText(myRestaurant.getName());
		}
		
		for(int i=0;i<m_arr.size();i++)
		{	
			int key=m_arr.keyAt(i);
			Category category=m_arr.get(key);
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
		
		cIndex=0;
		DisplayRecipeList();
	}
	
	
	public void DisplayRecipeList()
	{
		if(appDiancan.myOrder!=null&&appDiancan.myOrder.getRestaurant().getId()==myRestaurant.getId()){
			mItemDic = appDiancan.myOrderHelper.getOrderItemDic();
		}
		else{
			mItemDic = null;
		}
		exampleArray=new SectionListItem[allRecipes.size()];
		for(int i=0;i<allRecipes.size();i++){
			Recipe recipe=allRecipes.get(i);
    		Category category=m_arr.get(recipe.getCid());
    		OrderItem orderItem;
        	if(mItemDic!=null&&
        			mItemDic.indexOfKey(recipe.getId())>-1){
        		orderItem = mItemDic.get(recipe.getId());
        	}
        	else{
        		orderItem = new OrderItem(recipe);
        	}
        	
        	exampleArray[i]=new SectionListItem(orderItem, category.getName());
    	}
		
		arrayAdapter = new StandardArrayAdapter(this,R.id.title,exampleArray);
		
		sectionAdapter = new SectionListAdapter(getLayoutInflater(),
                arrayAdapter,imgDownloader);
		sectionAdapter.setCategoryAdapter(simpleAdapter);
		sectionAdapter.setViewClickListener(this);
        
		recipeListView.setAdapter(sectionAdapter);
		recipeListView.setOnScrollListener(sectionAdapter);
		recipeListView.setPinnedHeaderView(getLayoutInflater().inflate(R.layout.list_section, recipeListView, false));
	}
	
	public void RefreshRecipeList(Order order){
		
		if(order!=null){
			appDiancan.myOrder = order;
			if(appDiancan.myOrderHelper==null){
				appDiancan.myOrderHelper = new OrderHelper(appDiancan.myOrder);
			}
			else{
				appDiancan.myOrderHelper.SetOrderAndItemDic(order);
			}
			
			titleView.setText(myRestaurant.getName()+"-"+order.getDesk().getName());
		}
		else{
			titleView.setText(myRestaurant.getName());
		}
		
		if(appDiancan.myOrder!=null&&appDiancan.myOrder.getRestaurant().getId()==myRestaurant.getId()){
			mItemDic = appDiancan.myOrderHelper.getOrderItemDic();
		}
		else{
			mItemDic = null;
		}
		
		for(int i=0;i<exampleArray.length;i++){
			
			SectionListItem sectionListItem = exampleArray[i];
			OrderItem orderItem = (OrderItem)sectionListItem.item;
			int recipeId = orderItem.getRecipe().getId();
			
        	if(mItemDic!=null&&
        			mItemDic.indexOfKey(recipeId)>-1){
        		OrderItem oItem = mItemDic.get(recipeId);
        		sectionListItem.item = oItem;  
        	}
        	else{
        		orderItem.setCountConfirm(0);
        		orderItem.setCountDeposit(0);
        		orderItem.setCountNew(0);
        	}
		}
		
		arrayAdapter.notifyDataSetChanged();
	}
	
	/**
	 * 点击分类按钮展开或者关闭分类
	 */
	private void ClickCategories(){
		if(recipeListView.ismEnableTouch()){
			recipeLayout.Torightend();
		}
		else{
			recipeLayout.ToleftStart(0);
		}
	}
	
	/**
	 * 根据位置获取被点击的view
	 * @param position
	 * @return
	 */
	public View GetClickItemView(int position){
		int first = recipeListView.getFirstVisiblePosition();
		View clickView = recipeListView.getChildAt(position - first);
		return clickView;
	}
	
	/**
	 * 点击了菜的图片
	 * @param position
	 */
	private void ClickRecipeImg(int position){
		
		if(!recipeListView.ismEnableTouch()){
			return;
		}
		
		SectionListItem sectionListItem = exampleArray[position];
		OrderItem orderItem = (OrderItem)sectionListItem.item;
		String urlString = MenuUtils.imageUrl+MenuUtils.IMAGE_BIG+orderItem.getRecipe().getImage();
		
		View clickView = GetClickItemView(position);
		AdapterViewHolder viewHolder = (AdapterViewHolder)clickView.getTag();
		ImageView img = viewHolder.imgrecipe;
		img.setDrawingCacheEnabled(true);
		Bitmap obmp = Bitmap.createBitmap(img.getDrawingCache());
		img.setDrawingCacheEnabled(false);
		
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		obmp.compress(Bitmap.CompressFormat.PNG, 100, baos);
		byte[] by = baos.toByteArray();
		
		int X1 = img.getLeft();
		int Y1 = clickView.getTop() + img.getTop();
		int X2 = img.getRight();
		int Y2 = clickView.getTop() + img.getBottom();
		Y1 += topbarHeight;
		Y2 += topbarHeight;
		int centerX = (X2+X1)/2;
		int centerY = (Y2+Y1)/2;
		Intent intent = new Intent();
		intent.putExtra("url", urlString);
		intent.putExtra("centerx", centerX);
		intent.putExtra("centery", centerY);
		intent.putExtra("byte", by);
		
		intent.setClass(this, RecipeImgActivity.class);
		startActivity(intent);
		
	}
	
	private void ClickIncreaseImg(int position){
		
		if(!recipeListView.ismEnableTouch()){
			return;
		}
		
		Calendar cld = Calendar.getInstance();
		
//		if(cld.getTimeInMillis()-clicktime<TIME_MEASURE)
//		{
//			return;
//		}
		clicktime=cld.getTimeInMillis();
		
		if(appDiancan.myOrder==null){
			ShowError("点菜前请您先开台！");
			return;
		}
		
		SectionListItem sectionListItem = exampleArray[position];
		final OrderItem orderItem = (OrderItem)sectionListItem.item;
		
		View clickView = GetClickItemView(position);
		final AdapterViewHolder viewHolder = (AdapterViewHolder)clickView.getTag();
		viewHolder.imgrecipe.setDrawingCacheEnabled(true);
		Bitmap obmp = Bitmap.createBitmap(viewHolder.imgrecipe.getDrawingCache());
		viewHolder.imgrecipe.setDrawingCacheEnabled(false);
		mPopImageView.setImageBitmap(obmp);
		mPopImageView.setVisibility(View.VISIBLE);
		
		int listHeight = sHeight - tabbarHeight - topbarHeight;
		int tabWidth = sWidth/TABCOUNT;
		int leftX = viewHolder.imgrecipe.getLeft();
		int leftY = clickView.getTop() + viewHolder.imgrecipe.getBottom();
		int centerX = (tabWidth*4/3 - leftX)/2;
		int centerY = clickView.getTop();
		int rightX = tabWidth*4/3;
		int rightY = listHeight;
		
		int bottomNum = recipeListView.getChildCount() - 1;
		int currentNum = position - recipeListView.getFirstVisiblePosition();
		int animationDuration = 400 + (bottomNum - currentNum)*50;
		
		ListPopImgAnimation tAnimation=new ListPopImgAnimation(animationDuration, leftX,leftY,centerX,centerY,rightX,rightY);
		tAnimation.setAnimationListener(new AnimationListener() {
			
			@Override
			public void onAnimationStart(Animation animation) {}
			
			@Override
			public void onAnimationRepeat(Animation animation) {}
			
			@Override
			public void onAnimationEnd(Animation animation) {
				// TODO Auto-generated method stub
				mPopImageView.clearAnimation();
				mPopImageView.setVisibility(View.GONE);
				
				//加减菜请求
				PostToServer(orderItem.getRecipe().getId(),1);
				orderItem.setCountNew(orderItem.getCountNew()+1);
				if(orderItem.GetCount()>0){
					viewHolder.tvCount.setText(orderItem.GetCount()+"");
					if(orderItem.getCountNew()>0){
						viewHolder.imgdelete.setVisibility(View.VISIBLE);
					}
					else{
						viewHolder.imgdelete.setVisibility(View.GONE);
					}
				}
				else{
					viewHolder.tvCount.setText("");
					viewHolder.imgdelete.setVisibility(View.GONE);
				}
			}
		});
		mPopImageView.startAnimation(tAnimation);
	}
	
	private void ClickDecreaseImg(int position,View v){
		
		if(!recipeListView.ismEnableTouch()){
			return;
		}
		
		Calendar cld = Calendar.getInstance();
		if(cld.getTimeInMillis()-clicktime<TIME_MEASURE)
		{
			return;
		}
		clicktime=cld.getTimeInMillis();
		
		SectionListItem sectionListItem = exampleArray[position];
		OrderItem orderItem = (OrderItem)sectionListItem.item;
		
		View clickView = GetClickItemView(position);
		AdapterViewHolder viewHolder = (AdapterViewHolder)clickView.getTag();
		
//		if(myRestaurant.getOrderItemDic().indexOfKey(recipeId)<=-1){
//			viewHolder.tvCount.setText("");
//			viewHolder.imgdelete.setVisibility(View.GONE);
//			return;
//		}
		if(orderItem.getCountNew()<=0){
			viewHolder.tvCount.setText("");
			viewHolder.imgdelete.setVisibility(View.GONE);
			return;
		}
		else{
			//加减菜请求
			PostToServer(orderItem.getRecipe().getId(),-1);
			orderItem.setCountNew(orderItem.getCountNew()-1);
			
			if(orderItem.GetCount()>0){
				viewHolder.tvCount.setText(orderItem.GetCount()+"");
				if(orderItem.getCountNew()>0){
					viewHolder.imgdelete.setVisibility(View.VISIBLE);
				}
				else{
					viewHolder.imgdelete.setVisibility(View.GONE);
				}
			}
			else{
				viewHolder.tvCount.setText("");
				viewHolder.imgdelete.setVisibility(View.GONE);
			}
		}
		
	}
	
	public void PostToServer(int recipeId,int count)
    {
		mProgressBar.setVisibility(View.VISIBLE);
    	recipeListHttpHelper.Diancai(recipeId, count);
    }
	
	//加入订单
  	public void SendSetCountMessage()
    {
    	Intent in = new Intent();
        in.setAction("setcount");
        in.addCategory(Intent.CATEGORY_DEFAULT);
        this.sendBroadcast(in);      
    }
}
