package com.diancan;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.apache.http.client.ClientProtocolException;
import org.json.JSONException;
import org.json.JSONObject;

import com.diancan.Helper.OrderHelper;
import com.diancan.Helper.RecipeListHttpHelper;
import com.diancan.Utils.CustomViewBinder;
import com.diancan.Utils.DisplayUtil;
import com.diancan.Utils.MenuUtils;
import com.diancan.custom.adapter.CategoryListAdapter;
import com.diancan.custom.animation.PopImgAnimation;
import com.diancan.custom.view.BookPageFactory;
import com.diancan.custom.view.PageWidget;
import com.diancan.diancanapp.AppDiancan;
import com.diancan.http.HttpCallback;
import com.diancan.http.HttpDownloader;
import com.diancan.http.ImageDownloader;
import com.diancan.model.Category;
import com.diancan.model.OrderItem;
import com.diancan.model.Recipe;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.view.View.OnTouchListener;
import android.view.ViewGroup.LayoutParams;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.Animation;
import android.view.animation.OvershootInterpolator;
import android.view.animation.TranslateAnimation;
import android.view.animation.Animation.AnimationListener;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;

public class MenuBook extends Activity implements HttpCallback,OnClickListener,OnItemClickListener,OnTouchListener {
	RecipeListHttpHelper recipeListHttpHelper;
	OrderHelper orderHelper;
	private PageWidget mPageWidget;
	Bitmap mCurPageBitmap, mNextPageBitmap;
	Canvas mCurPageCanvas, mNextPageCanvas;
	BookPageFactory pagefactory;
	RelativeLayout rootLayout;
	LinearLayout categoryLayout;
	Button mButton;
	TextView mCatBtn;
	ListView categoryList;
	List<Category> m_arr;
	ArrayList<HashMap<String, Object>> hashlist;
	CategoryListAdapter simpleAdapter;
	AppDiancan declare;
	ImageView littleImageView;
	BroadcastReceiver receiver;
	Animation popAnimation;
	Animation descendAnimation;
	boolean isInit=true;
	public int sWidth;
	public int sHeight;
	public int cIndex,rIndex;
	public boolean isSelf=false;
	public ImageDownloader imgDownloader;
	int sendId,sendCount;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
				WindowManager.LayoutParams.FLAG_FULLSCREEN);
		setContentView(R.layout.menubook);
		
		sWidth = DisplayUtil.dip2px(DisplayUtil.DPWIDTH);
		sHeight = DisplayUtil.dip2px(DisplayUtil.DPHEIGHT-108);	
		imgDownloader=new ImageDownloader();
		Intent intent =getIntent();
		cIndex=Integer.parseInt((String)intent.getSerializableExtra("index"));
  		rIndex=Integer.parseInt((String)intent.getSerializableExtra("rindex"));

		mPageWidget = new PageWidget(this,sWidth,sHeight);
		rootLayout=(RelativeLayout)findViewById(R.id.pageroot);
		mPageWidget.setLayoutParams(new LayoutParams(LayoutParams.FILL_PARENT,LayoutParams.FILL_PARENT));
		rootLayout.addView(mPageWidget);		
		LayoutInflater inflater = (LayoutInflater)this.getSystemService( Context.LAYOUT_INFLATER_SERVICE);
    	categoryLayout = (LinearLayout)inflater.inflate(R.layout.categorylayout, null);
		RelativeLayout.LayoutParams lParams=new RelativeLayout.LayoutParams(DisplayUtil.dip2px(133),DisplayUtil.dip2px(200));
		lParams.addRule(RelativeLayout.ALIGN_PARENT_TOP);
		lParams.addRule(RelativeLayout.CENTER_HORIZONTAL);
		categoryLayout.setLayoutParams(lParams);
		rootLayout.addView(categoryLayout);
		categoryLayout.setVisibility(View.GONE);
		categoryList=(ListView)categoryLayout.findViewById(R.id.ListCategory);	
		mButton=(Button)findViewById(R.id.BtnDian);
		mButton.setOnClickListener(this);
		mCatBtn=(TextView)findViewById(R.id.BtnCategory);
		mCatBtn.setOnClickListener(this);		
		littleImageView=(ImageView)findViewById(R.id.imglittle);
		
		declare=(AppDiancan)getApplicationContext();
		recipeListHttpHelper=new RecipeListHttpHelper(this, declare);
		orderHelper=new OrderHelper(declare);
		m_arr=declare.getMenuListDataObj().getCategories();
		hashlist=new ArrayList<HashMap<String,Object>>();
		
		mCurPageBitmap = Bitmap.createBitmap(sWidth, sHeight, Bitmap.Config.ARGB_8888);
		mNextPageBitmap = Bitmap.createBitmap(sWidth, sHeight, Bitmap.Config.ARGB_8888);
		mCurPageCanvas = new Canvas(mCurPageBitmap);
		mNextPageCanvas = new Canvas(mNextPageBitmap);
		
		pagefactory = new BookPageFactory(sWidth, sHeight,this);
		Bitmap bgBitmap=MenuUtils.readBitMap(this, R.drawable.waitback, 1);
		bgBitmap=Bitmap.createScaledBitmap(bgBitmap, sWidth, sHeight, false);
		pagefactory.setBgBitmap(bgBitmap);	
		pagefactory.setM_curPage(rIndex);
		InitCategoryList();
		
		mPageWidget.setOnTouchListener(this);		
	}	
	
	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		HashMap<String, Object> map=hashlist.get(cIndex);
		String nameString=map.get("name").toString();
		simpleAdapter.setSelectedName(nameString);
		simpleAdapter.notifyDataSetChanged();
		mCatBtn.setText(map.get("name").toString());
		DisplayRecipeList(cIndex);

	}
	
	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		if(!mCurPageBitmap.isRecycled())
		{
			mCurPageBitmap.recycle();
		}
		if(!mNextPageBitmap.isRecycled())
		{
			mNextPageBitmap.recycle();
		}
	}
	
	@Override
	public void RequestComplete(Message msg) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void RequestError(String errString) {
		// TODO Auto-generated method stub
		//自定义的错误，在界面上显示
		Toast toast = Toast.makeText(MenuBook.this, errString, Toast.LENGTH_SHORT); 
        toast.show();
	} 
	
	@Override
	public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
		// TODO Auto-generated method stub
		HashMap<String, Object> map=hashlist.get(arg2);
		simpleAdapter.setSelectedName(map.get("name").toString());
		cIndex=arg2;
		rIndex=0;
		pagefactory.setM_curPage(rIndex);
		mCatBtn.setText(map.get("name").toString());
		DisplayRecipeList(cIndex);
		
		HideList();
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.BtnCategory:
			if(categoryLayout.getVisibility()==View.GONE)
			{
				ExpandList();
			}
			else {
				HideList();
			}
			break;
		case R.id.BtnDian:
			if(categoryLayout.getVisibility()!=View.GONE)
			{
				HideList();
			}
			if(declare.curOrder==null)
			{
				Toast toast = Toast.makeText(MenuBook.this, "请选择餐桌", Toast.LENGTH_SHORT); 
	            toast.show(); 
	            return;
			}
			PopImgAnimation animation=new PopImgAnimation(500,sWidth,sHeight);
			animation.setAnimationListener(new PopImgAnimationListener(pagefactory.GetCurMenu()));
			littleImageView.startAnimation(animation);
			break;
		default:
			break;
		}
	}
	
	@Override
	public boolean onTouch(View v, MotionEvent event) {
		// TODO Auto-generated method stub
		boolean ret=false;
		if (v == mPageWidget) {
			if (event.getAction() == MotionEvent.ACTION_DOWN) {
				if(categoryLayout.getVisibility()!=View.GONE)
				{
					HideList();
				}
				isInit=false;
				mPageWidget.abortAnimation();
				mPageWidget.calcCornerXY(event.getX(), event.getY());
				pagefactory.onDraw(mCurPageCanvas);
				if (mPageWidget.toRight) {
					try {
						pagefactory.prePage();
					} catch (IOException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}						
					if(pagefactory.isfirstPage())return false;
					
					pagefactory.onDraw(mNextPageCanvas);
					OrderItem orderItem=pagefactory.GetCurMenu();
					imgDownloader.download(MenuUtils.imageUrl+orderItem.getRecipe().getImage(), littleImageView);
				} else {
					try {
						pagefactory.nextPage();
					} catch (IOException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}
					if(pagefactory.islastPage())return false;
					
					pagefactory.onDraw(mNextPageCanvas);
					OrderItem orderItem=pagefactory.GetCurMenu();
					imgDownloader.download(MenuUtils.imageUrl+orderItem.getRecipe().getImage(), littleImageView);
				}
				mPageWidget.setBitmaps(mCurPageBitmap, mNextPageBitmap);
				
			}                
			ret = mPageWidget.doTouchEvent(event);					
			return ret;
		}
		return false;
	}


	public void InitCategoryList()
	{
		for(final Category category:m_arr)
		{		
			HashMap<String, Object> map=new HashMap<String, Object>();
			map.put("name", category.getName()); 
			map.put("id", category.getId());
			hashlist.add(map);
		}
		simpleAdapter=new CategoryListAdapter(this, hashlist,
				R.layout.categorylist_item, new String[] { "name","id" },
				new int[] { R.id.category_name,R.id.category_id});
		simpleAdapter.setViewBinder(new CustomViewBinder());
		simpleAdapter.setSelectedName("");
		categoryList.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
		categoryList.setAdapter(simpleAdapter);
		categoryList.setOnItemClickListener(this);	 
		
	}
	
	public void ExpandList()
	{
		categoryLayout.clearAnimation();
		categoryLayout.setVisibility(View.VISIBLE);
		Animation animation=new TranslateAnimation(0, 0, -DisplayUtil.dip2px(247), 0);
		animation.setDuration(300);
		animation.setInterpolator(new OvershootInterpolator());
		categoryLayout.startAnimation(animation);
	}
	public void HideList()
	{
		categoryLayout.clearAnimation();
		Animation animation=new TranslateAnimation(0, 0, 0, -DisplayUtil.dip2px(247));
		animation.setDuration(300);
		animation.setInterpolator(new AccelerateInterpolator());
		categoryLayout.startAnimation(animation);
		categoryLayout.setVisibility(View.GONE);
	}
	
	public void DisplayRecipeList(int position)
	{
		Category category=m_arr.get(position);
		if(declare.getMenuListDataObj().getRecipeMap().indexOfKey(category.getId())<0)
		{
			try {
				List<OrderItem> orderItems=new ArrayList<OrderItem>();
				List<Recipe> recipes = MenuUtils
						.getRecipesByCategory(category.getId(),declare.udidString);
				Iterator<Recipe> iterator;
				for(iterator=recipes.iterator();iterator.hasNext();)
				{
					OrderItem orderItem=new OrderItem();
					orderItem.setRecipe(iterator.next());
					orderItem.setCount(0);
					orderItems.add(orderItem);
				}
				declare.getMenuListDataObj().getRecipeMap().put(category.getId(), orderItems);
			} catch (Exception e) {
				// TODO: handle exception
				System.out.println(e.getMessage());
				return;
			}
		}
		
		pagefactory.m_OrderItems=declare.getMenuListDataObj().getRecipeMap().get(category.getId());
		if(pagefactory.m_OrderItems==null||pagefactory.m_OrderItems.size()==0)
		{
			return;
		}
		if(declare.curOrder!=null)
		{
			//同步数据
			declare.getMenuListDataObj().SyncMenuListByCategory(category, declare.curOrder);
		}
		pagefactory.onDraw(mCurPageCanvas);
		
		OrderItem orderItem=pagefactory.GetCurMenu();
		imgDownloader.download(MenuUtils.imageUrl+orderItem.getRecipe().getImage(), littleImageView);	
		mPageWidget.setBitmaps(mCurPageBitmap, mCurPageBitmap);

	}
	
	public void setCountValue(OrderItem orderItem,int count) {
		if(count>0)
		{
			orderItem.setCount(count);
		}
		else {
			orderItem.setCount(0);
		}	
		
		pagefactory.onDraw(mCurPageCanvas);
		pagefactory.onDraw(mNextPageCanvas);
		mPageWidget.postInvalidate();
	}
	
	public void PostToServer()
    {
		recipeListHttpHelper.Diancai(sendId, sendCount);
    }
	
	class PopImgAnimationListener implements AnimationListener{
		private OrderItem mOrderItem;
		public PopImgAnimationListener(OrderItem orderItem)
		{
			mOrderItem=orderItem;
		}
		 @Override
		 public void onAnimationEnd(Animation animation) {
			littleImageView.clearAnimation();
			sendId=mOrderItem.getRecipe().getId();
			sendCount=1;
			

			Intent in = new Intent();
            in.setAction("animation");
            in.addCategory(Intent.CATEGORY_DEFAULT);
            MenuBook.this.sendBroadcast(in);
            int i=mOrderItem.getCount()+1;
            setCountValue(mOrderItem,i);
            //加入订单
            orderHelper.AddToOrderForm(mOrderItem);
			PostToServer();
		 }  
		 //该方法在动画重复执行的时候调用
		 @Override
		 public void onAnimationRepeat(Animation animation) {
		 }  
		 //该方法在动画开始执行的时候，调用 
		 @Override
		 public void onAnimationStart(Animation animation) {
		 }  
		        
	 }
	
}
