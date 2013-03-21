package com.diancan;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import com.diancan.Helper.RecipeListHttpHelper;
import com.diancan.Utils.DisplayUtil;
import com.diancan.Utils.JsonUtils;
import com.diancan.custom.animation.ListPopImgAnimation;
import com.diancan.custom.view.PinnedHeaderListView;
import com.diancan.diancanapp.AppDiancan;
import com.diancan.http.HttpCallback;
import com.diancan.http.HttpHandler;
import com.diancan.http.ImageDownloader;
import com.diancan.model.Category;
import com.diancan.model.Order;
import com.diancan.model.OrderItem;
import com.diancan.model.Recipe;
import com.diancan.sectionlistview.OrderSectionListAdapter;
import com.diancan.sectionlistview.SectionListItem;
import com.diancan.sectionlistview.SectionListAdapter.AdapterViewHolder;
import android.app.Activity;
import android.app.Dialog;
import android.app.TabActivity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.ColorStateList;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Message;
import android.text.method.ScrollingMovementMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.AnimationUtils;
import android.view.animation.OvershootInterpolator;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

public class MyTable extends Activity implements HttpCallback,OnClickListener{
	
	PinnedHeaderListView orderListView;
	Button commitButton;
	Button flashButton;
	TextView sumTextView;
	TextView countNewView;
	ImageView mPopImageView;
	ProgressBar mProgressBar;
	String sumString;	
	String countNewString;
	AppDiancan declare;
	NotifiReceiver receiver;
	RecipeListHttpHelper recipeListHttpHelper;
	ImageDownloader imageDownloader;
	static int TABCOUNT = 3;
	static int TIME_MEASURE = 500;
	private long clicktime=0;
	int sWidth,sHeight;
	int topbarHeight,tabbarHeight;
	
	HashMap<String, List<OrderItem>> hashOrderItems;
	private MyStandardArrayAdapter arrayAdapter;

    private OrderSectionListAdapter sectionAdapter;

    List<SectionListItem> exampleArray;
    
    public class MyStandardArrayAdapter extends ArrayAdapter<SectionListItem> {

    	public List<SectionListItem> items;
        public MyStandardArrayAdapter(Context context, int textViewResourceId,
				List<SectionListItem> objects) {
			super(context, textViewResourceId, objects);
			// TODO Auto-generated constructor stub
			this.items = objects;
		}
        
    }
		
    @Override
    protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.mytable);
		
		sWidth = DisplayUtil.PIXWIDTH;
		sHeight = DisplayUtil.PIXHEIGHT;
		topbarHeight = (int)getResources().getDimension(R.dimen.topbar_height);
		tabbarHeight = (int)getResources().getDimension(R.dimen.tabbar_height);
		
		sumString=getResources().getString(R.string.infostr_sum);
		countNewString = getResources().getString(R.string.countnewinfo);
		sumTextView=(TextView)findViewById(R.id.sumText);
		countNewView = (TextView)findViewById(R.id.countNew);
		mPopImageView = (ImageView)findViewById(R.id.img_popup);
		mProgressBar = (ProgressBar)findViewById(R.id.httppro);
		
		orderListView=(PinnedHeaderListView)findViewById(R.id.orderList);
		commitButton=(Button)findViewById(R.id.commitBtn);
		commitButton.setOnClickListener(this);
		flashButton=(Button)findViewById(R.id.BtnFlash);
		flashButton.setOnClickListener(this);
		declare=(AppDiancan)getApplicationContext();
		receiver = new NotifiReceiver();
		recipeListHttpHelper=new RecipeListHttpHelper(this, declare);
    }
    @Override
	protected void onResume() {
		// TODO Auto-generated method stub
    	super.onResume();
	    IntentFilter filter = new IntentFilter();
	    filter.addAction("diancan");
	    filter.addCategory(Intent.CATEGORY_DEFAULT);
	    registerReceiver(receiver, filter);
	    mProgressBar.setVisibility(View.GONE);
    	if(declare.myOrder==null){
    		if(exampleArray!=null){
    			exampleArray.clear();
    			hashOrderItems.clear();
    			arrayAdapter.notifyDataSetChanged();
    			sumTextView.setText(sumString+"0.00");
    		}
    		return;
    	}
    	
    	if(hashOrderItems==null){
    		
    		hashOrderItems=new HashMap<String, List<OrderItem>>();
    		InitHashOrderItems();
    		InitListDatasource();
    		DisplayOrderList();
    		
    	}
    	else{
    		UpdateElement();	
    	}
    	
		
	}
    
    @Override
	protected void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
		unregisterReceiver(receiver);
	}
    
    @Override
	public void RequestComplete(Message msg) {
		// TODO Auto-generated method stub
    	mProgressBar.setVisibility(View.GONE);
    	switch(msg.what) {  
        case HttpHandler.REFRESH_ORDER:
        	String jsString=msg.obj.toString();
        	ParseOrderRefresh(jsString);
            break;
        case HttpHandler.POST_RECIPE_COUNT_FROMORDER:
        	String jsonString=msg.obj.toString();
        	ParseOrder(jsonString);
        	break;
        case HttpHandler.CHECK_ORDER:
        	String json=msg.obj.toString();
        	ParseOrderRefresh(json);
        	break;
        case HttpHandler.DEPOSIT_ORDER:
        	String jString = msg.obj.toString();
        	ParseOrderRefresh(jString);
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
		case R.id.commitBtn:
			ClickCommitBtn();
			break;
		case R.id.BtnFlash:
			FlashOrder();
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
    
	/**
     * 显示错误信息
     * @param strMess
     */
    public void ShowError(String strMess) {
		Toast toast = Toast.makeText(MyTable.this, strMess, Toast.LENGTH_SHORT); 
        toast.show();
	}
    
    /**
     * 初始化订单
     */
    public void InitHashOrderItems()
    {
    	hashOrderItems.clear();
    	if(exampleArray==null){
    		exampleArray = new ArrayList<SectionListItem>();
    	}
    	else{
    		exampleArray.clear();
    	}
    	Iterator<OrderItem> iterator;
    	for(iterator=declare.myOrder.getClientItems().iterator();iterator.hasNext();)
    	{
    		OrderItem orderItem=iterator.next();
    		Category category=declare.myOrderHelper.getCategoryDic().get(orderItem.getRecipe().getCid());
    		String cnameString=category.getName();
    		if(!hashOrderItems.containsKey(cnameString))
    		{
    			List<OrderItem> orderItems=new ArrayList<OrderItem>();
    			hashOrderItems.put(cnameString, orderItems);
    		}
    		
    		List<OrderItem> oItems=hashOrderItems.get(cnameString);
    		oItems.add(orderItem);
    	}
    }
    
    /**
     * 初始化列表数据源
     */
    public void InitListDatasource(){
    	String strKey;
    	Set<String> meenum=hashOrderItems.keySet();
    	Iterator iterator;
		for (iterator = meenum.iterator(); iterator.hasNext();) {
			strKey = (String) iterator.next();
			List<OrderItem> orderItemList=hashOrderItems.get(strKey);
			Iterator<OrderItem> iterator2;
			int count = orderItemList.size();
			for(iterator2=orderItemList.iterator();iterator2.hasNext();){
				OrderItem orderItem=iterator2.next();
				exampleArray.add(new SectionListItem(orderItem, strKey+"("+count+")"));
			}
		}
    }
    
    private void DisplayOrderList(){
    	Drawable[] layers={getResources().getDrawable(R.drawable.imagewaiting)};
    	imageDownloader = new ImageDownloader(layers);
    	arrayAdapter = new MyStandardArrayAdapter(this,R.id.title,exampleArray);
		sectionAdapter = new OrderSectionListAdapter(getLayoutInflater(),
                arrayAdapter,imageDownloader);
		sectionAdapter.setViewClickListener(this);
		orderListView.setAdapter(sectionAdapter);
		orderListView.setOnScrollListener(sectionAdapter);
		orderListView.setPinnedHeaderView(getLayoutInflater().inflate(R.layout.list_section, orderListView, false));

		sumTextView.setText(sumString+declare.myOrder.getPriceAll());
		if(declare.myOrderHelper.getCountNew()==0){
    		countNewView.setText("");
    	}
    	else{
    		countNewView.setText(declare.myOrderHelper.getCountNew()+countNewString);
    	}
		if(declare.myOrder.getPriceAll()==(declare.myOrder.getPriceDeposit()+declare.myOrder.getPriceConfirm())){
			commitButton.setVisibility(View.GONE);
		}
		else{
			commitButton.setVisibility(View.VISIBLE);
		}
    }
	
	private void UpdateElement(){
		hashOrderItems.clear();
    	if(declare.myOrder!=null)
    	{
    		InitHashOrderItems();
    		InitListDatasource();
    		arrayAdapter.notifyDataSetChanged();
    		sumTextView.setText(sumString+declare.myOrder.getPriceAll());
    		if(declare.myOrderHelper.getCountNew()==0){
	    		countNewView.setText("");
	    	}
	    	else{
	    		countNewView.setText(declare.myOrderHelper.getCountNew()+countNewString);
	    	}
    		if(declare.myOrder.getPriceAll()==(declare.myOrder.getPriceDeposit()+declare.myOrder.getPriceConfirm())){
    			commitButton.setVisibility(View.GONE);
    		}
    		else{
    			commitButton.setVisibility(View.VISIBLE);
    		}
    	}
    	else {
    		commitButton.setVisibility(View.GONE);
    		sumTextView.setText(sumString+"0.00");
    		countNewView.setText("");
		}
	}
	public void FlashOrder(){
		mProgressBar.setVisibility(View.VISIBLE);
		recipeListHttpHelper.RefreshOrder();
	}
    
    public void PostToServer(int recipeId,int count)
    {
    	mProgressBar.setVisibility(View.VISIBLE);
    	recipeListHttpHelper.DiancaiFormOrder(recipeId, count);
    }
    private void ParseOrderRefresh(String jsString){
    	if(jsString.equals(""))
    	{
    		ShowError("操作失败！");
    	}
    	final Order order=JsonUtils.ParseJsonToOrder(jsString);
    	declare.myOrder = order;
    	declare.myOrderHelper.SetOrderAndItemDic(order);
		if(order.getStatus()==3 || order.getStatus()==4){
			countNewView.setText("");
			SendSetCountMessage();
			TabActivity main = (TabActivity)this.getParent();
			main.getTabHost().setCurrentTab(0);
			return;
		}
		else{
			UpdateElement();
			if(declare.myOrderHelper.getCountNew()==0){
	    		countNewView.setText("");
	    	}
	    	else{
	    		countNewView.setText(declare.myOrderHelper.getCountNew()+countNewString);
	    	}
			SendSetCountMessage();
		}
		
    }
    
    private void ParseOrder(String jsString) {
    	if(jsString.equals(""))
    	{
    		ShowError("操作失败！");
    	}
    	final Order order=JsonUtils.ParseJsonToOrder(jsString);
    	declare.myOrder = order;
    	declare.myOrderHelper.SetOrderAndItemDic(order);
    	if(declare.myOrderHelper.getCountNew()==0){
    		countNewView.setText("");
    	}
    	else{
    		countNewView.setText(declare.myOrderHelper.getCountNew()+countNewString);
    	}
		SendSetCountMessage();
	}
    
    public void SendSetCountMessage()
    {
    	Intent in = new Intent();
        in.setAction("setcount");
        in.addCategory(Intent.CATEGORY_DEFAULT);
        MyTable.this.sendBroadcast(in);
    }
    
	public OrderItem GetItemById(int id,Order order)
	{
		OrderItem orderItem=null;
		
		Iterator<OrderItem> iterator;
		for(iterator=order.getClientItems().iterator();iterator.hasNext();)
		{
			OrderItem oItem=iterator.next();
			Recipe recipe=oItem.getRecipe();
			if(id==recipe.getId())
			{
				orderItem=oItem;
				break;
			}
		}
		
		return orderItem;
	}
	
	/**
	 * 根据位置获取被点击的view
	 * @param position
	 * @return
	 */
	public View GetClickItemView(int position){
		int first = orderListView.getFirstVisiblePosition();
		View clickView = orderListView.getChildAt(position - first);
		return clickView;
	}
	
	private void ClickIncreaseImg(int position){
		Calendar cld = Calendar.getInstance();
		if(cld.getTimeInMillis()-clicktime<TIME_MEASURE)
		{
			return;
		}
		clicktime=cld.getTimeInMillis();
		
		SectionListItem sectionListItem = exampleArray.get(position);
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
		
		int bottomNum = orderListView.getChildCount() - 1;
		int currentNum = position - orderListView.getFirstVisiblePosition();
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
				orderItem.setCountNew(orderItem.getCountNew()+1);
				viewHolder.tvCount.setText(orderItem.GetCount()+"");
				viewHolder.imgdelete.setVisibility(View.VISIBLE);
				viewHolder.tvCountDeposit.setTextColor(Color.RED);
				viewHolder.tvCountDeposit.setText(orderItem.getCountNew()+"例未下单");
				declare.myOrder.setPriceAll(declare.myOrder.getPriceAll()+orderItem.getRecipe().getPrice());
				sumTextView.setText(sumString+declare.myOrder.getPriceAll());
				//加减菜请求
				PostToServer(orderItem.getRecipe().getId(),1);
			}
		});
		mPopImageView.startAnimation(tAnimation);
	}
	
	private void ClickDecreaseImg(int position,View v){
		Calendar cld = Calendar.getInstance();
		if(cld.getTimeInMillis()-clicktime<TIME_MEASURE)
		{
			return;
		}
		clicktime=cld.getTimeInMillis();
		View clickView = GetClickItemView(position);
		AdapterViewHolder viewHolder = (AdapterViewHolder)clickView.getTag();
		SectionListItem sectionListItem = exampleArray.get(position);
		OrderItem orderItem = (OrderItem)sectionListItem.item;
		if(orderItem.getCountNew()<=0){
			return;
		}
		if(orderItem.getCountDeposit()==0&&orderItem.getCountNew()==1&&orderItem.getCountConfirm()==0){
			declare.myOrder.getClientItems().remove(position);
			declare.myOrderHelper.getOrderItemDic().remove(orderItem.getRecipe().getId());
			exampleArray.remove(position);
			arrayAdapter.notifyDataSetChanged();
			
		}
		else{
			orderItem.setCountNew(orderItem.getCountNew()-1);
			viewHolder.tvCount.setText(orderItem.GetCount()+"");
			if(orderItem.getCountNew()>0){
				viewHolder.imgdelete.setVisibility(View.VISIBLE);
				viewHolder.tvCountDeposit.setTextColor(Color.RED);
				viewHolder.tvCountDeposit.setText(orderItem.getCountNew()+"例未下单");
			}
			else{
				viewHolder.imgdelete.setVisibility(View.GONE);
				viewHolder.tvCountDeposit.setTextColor(Color.DKGRAY);
				viewHolder.tvCountDeposit.setText("已下单");
			}
		}
		declare.myOrder.setPriceAll(declare.myOrder.getPriceAll()-orderItem.getRecipe().getPrice());
		if(declare.myOrder.getPriceAll()==0){
			declare.myOrder.setPriceAll(0);
		}
		sumTextView.setText(sumString+declare.myOrder.getPriceAll());
		//加减菜请求
		PostToServer(orderItem.getRecipe().getId(),-1);
		
	}
	
	private void ClickCommitBtn(){
		if(declare.myOrder==null){
			return;
		}
		final Dialog dialog = new Dialog(this, R.style.MyDialog);
        //设置它的ContentView
		LayoutInflater inflater = (LayoutInflater) this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View layout = inflater.inflate(R.layout.dialog, null);
        dialog.setContentView(layout);
        String contentString = declare.myOrderHelper.GetCommitOrderString();       
        TextView contentView = (TextView)layout.findViewById(R.id.contentTxt);
        TextView titleView = (TextView)layout.findViewById(R.id.dialog_title);
        Button okBtn = (Button)layout.findViewById(R.id.dialog_button_ok);
        okBtn.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				dialog.dismiss();
				mProgressBar.setVisibility(View.VISIBLE);
				recipeListHttpHelper.DepositOrder();
			}
		});
        Button cancelButton = (Button)layout.findViewById(R.id.dialog_button_cancel);
        cancelButton.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				dialog.dismiss();
			}
		});
        titleView.setText("本次提交");
        contentView.setText(contentString);
        contentView.setMovementMethod(ScrollingMovementMethod.getInstance());
        dialog.show();
        Animation animation = AnimationUtils.loadAnimation(MyTable.this, R.anim.activity_in);
        animation.setInterpolator(new OvershootInterpolator());
        layout.startAnimation(animation);
	}
	
	
	/**
     * 通知的接收器，只接收点菜消息
     */
    class NotifiReceiver extends BroadcastReceiver{

		@Override
		public void onReceive(Context context, Intent intent) {
			// TODO Auto-generated method stub
			String action = intent.getAction();
			if(action.equals("diancan")){
				String idString=intent.getSerializableExtra("message").toString();
				int id=Integer.parseInt(idString);
				if(id==declare.myOrder.getId())
				{
					FlashOrder();
				}
			}
		}
		
	}
}
