package com.diancan;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import com.diancan.Helper.RecipeListHttpHelper;
import com.diancan.Utils.DisplayUtil;
import com.diancan.Utils.JsonUtils;
import com.diancan.Utils.MenuUtils;
import com.diancan.diancanapp.AppDiancan;
import com.diancan.http.HttpCallback;
import com.diancan.http.HttpHandler;
import com.diancan.http.ImageDownloader;
import com.diancan.model.Order;
import com.diancan.model.OrderItem;
import com.diancan.model.Recipe;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

public class MyTable extends Activity implements HttpCallback,OnClickListener{
	ListView orderListView;
	Button overButton;
	Button flashButton;
	ProgressBar mProgressBar;
	TextView sumTextView;
	String sumString;	
	int sendId,sendCount;
	OrderItem changedItem;
	AppDiancan declare;
	NotifiReceiver receiver;
	RecipeListHttpHelper recipeListHttpHelper;
	HashMap<String, List<OrderItem>> hashOrderItems;
	
	private List<OrderItem> mOrderItems=new ArrayList<OrderItem>();
	private List<HashMap<String, Object>> itemlist = new ArrayList<HashMap<String, Object>>();  
	private List<HashMap<String, Object>> tagList = new ArrayList<HashMap<String, Object>>();
	
		
    @Override
    protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.mytable);
		sumString=getResources().getString(R.string.infostr_sum);
		sumTextView=(TextView)findViewById(R.id.sumText);
		sumTextView.setTextColor(Color.WHITE);
		sumTextView.setTextSize(DisplayUtil.dip2px(16));
		orderListView=(ListView)findViewById(R.id.orderList);
		mProgressBar=(ProgressBar)findViewById(R.id.httppro);
		overButton=(Button)findViewById(R.id.overBtn);
		overButton.setText(getResources().getString(R.string.btnstr_over));
		overButton.setOnClickListener(this);
		flashButton=(Button)findViewById(R.id.BtnFlash);
		flashButton.setOnClickListener(this);
		declare=(AppDiancan)getApplicationContext();
		receiver = new NotifiReceiver();
		
		recipeListHttpHelper=new RecipeListHttpHelper(this, declare);
    }
    @Override
	protected void onResume() {
		// TODO Auto-generated method stub
    	mProgressBar.setVisibility(View.INVISIBLE);
    	if(hashOrderItems==null){
    		hashOrderItems=new HashMap<String, List<OrderItem>>();
    		InitHashOrderItems();
    		InitListDatasource();
    		TableListAdapter listAdapter=new TableListAdapter(this, itemlist, tagList,mOrderItems);
    		orderListView.setAdapter(listAdapter);
    		sumTextView.setText(sumString+declare.totalPrice);
    		if(declare.curOrder.getStatus()>=3){
    			overButton.setEnabled(false);
    		}
    		else{
    			overButton.setEnabled(true);
    		}
    	}
    	else{
    		UpdateElement();	
    	}
    	
		super.onResume();
		
	    IntentFilter filter = new IntentFilter();
	    filter.addAction("diancan");
	    filter.addCategory(Intent.CATEGORY_DEFAULT);
	    registerReceiver(receiver, filter);
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
    	switch(msg.what) {  
        case HttpHandler.REFRESH_ORDER: 
        	String jsString=msg.obj.toString();
        	ParseOrderRefresh(jsString);
            break;
        case HttpHandler.POST_RECIPE_COUNT:
        	String jsonString=msg.obj.toString();
        	ParseOrder(jsonString);
        	break;
        case HttpHandler.CHECK_ORDER:
        	String json=msg.obj.toString();
        	ParseOrderRefresh(json);
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
		case R.id.overBtn:
			mProgressBar.setVisibility(View.VISIBLE);
			recipeListHttpHelper.CheckOrder();
			break;
		case R.id.BtnFlash:
			FlashOrder();
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
		mProgressBar.setVisibility(View.INVISIBLE);
		Toast toast = Toast.makeText(MyTable.this, strMess, Toast.LENGTH_SHORT); 
        toast.show();
	}
    
    /**
     * 初始化订单
     */
    public void InitHashOrderItems()
    {
    	hashOrderItems.clear();
    	declare.totalCount=0;
    	declare.totalPrice=0;
    	Iterator<OrderItem> iterator;
    	for(iterator=declare.curOrder.getOrderItems().iterator();iterator.hasNext();)
    	{
    		OrderItem orderItem=iterator.next();
    		String cnameString=declare.hashTypes.get(orderItem.getRecipe().getCid()+"");
    		if(!hashOrderItems.containsKey(cnameString))
    		{
    			List<OrderItem> orderItems=new ArrayList<OrderItem>();
    			hashOrderItems.put(cnameString, orderItems);
    		}
    		List<OrderItem> oItems=hashOrderItems.get(cnameString);
    		oItems.add(orderItem);
    		declare.totalCount+=orderItem.getCount();
    		declare.totalPrice+=orderItem.getCount()*orderItem.getRecipe().getPrice();
    	}
    }
    
    /**
     * 初始化列表数据源
     */
    public void InitListDatasource(){
    	String strKey;
    	Set<String> meenum=hashOrderItems.keySet();
    	itemlist.clear();
    	tagList.clear();
    	mOrderItems.clear();
    	Iterator iterator;
		for (iterator = meenum.iterator(); iterator.hasNext();) {
			strKey = (String) iterator.next();
			List<OrderItem> orderItemList=hashOrderItems.get(strKey);
			HashMap<String, Object> tagMap=new HashMap<String, Object>();
			tagMap.put("tagTitle", strKey);
			tagList.add(tagMap);
			itemlist.add(tagMap);
			OrderItem oItem=new OrderItem();
			mOrderItems.add(oItem);
			Iterator<OrderItem> iterator2;
			for(iterator2=orderItemList.iterator();iterator2.hasNext();){
				OrderItem orderItem=iterator2.next();
				mOrderItems.add(orderItem);
				HashMap<String, Object> map = new HashMap<String, Object>();
				map.put("title", orderItem.getRecipe().getName());
				map.put("price", "¥ "+orderItem.getRecipe().getPrice());
				String strCount=orderItem.getCount()+"";
				map.put("count", strCount);
				Integer kk=orderItem.getStatus();
				map.put("status", kk);
				String urlString=MenuUtils.imageUrl+orderItem.getRecipe().getImage();
				map.put("img", urlString);
				itemlist.add(map);
			}
		}
    }
	
	private void UpdateElement(){
		hashOrderItems.clear();
    	if(declare.curOrder!=null)
    	{
    		InitHashOrderItems();
    		InitListDatasource();
    		TableListAdapter listAdapter=(TableListAdapter)orderListView.getAdapter();
    		listAdapter.setOrderItemList(mOrderItems);
    		listAdapter.notifyDataSetChanged();
    		sumTextView.setText(sumString+declare.totalPrice);
    		if(declare.curOrder.getStatus()>=3){
    			overButton.setEnabled(false);
    		}
    		else{
    			overButton.setEnabled(true);
    		}
    	}
    	else {
    		overButton.setVisibility(View.GONE);
    		sumTextView.setText(sumString+"0.00");
		}
	}
	public void FlashOrder(){
		mProgressBar.setVisibility(View.VISIBLE);
		recipeListHttpHelper.RefreshOrder();
	}
    
    public void PostToServer()
    {
    	mProgressBar.setVisibility(View.VISIBLE);
    	recipeListHttpHelper.Diancai(sendId, sendCount);
    }
    private void ParseOrderRefresh(String jsString){
    	mProgressBar.setVisibility(View.INVISIBLE);
    	if(jsString.equals(""))
    	{
    		ShowError("操作失败！");
    	}
    	final Order order=JsonUtils.ParseJsonToOrder(jsString);
		declare.curOrder=order;
		UpdateElement();
		SendSetCountMessage();
    }
    private void ParseOrder(String jsString) {
    	mProgressBar.setVisibility(View.INVISIBLE);
    	if(jsString.equals(""))
    	{
    		ShowError("操作失败！");
    	}
    	final Order order=JsonUtils.ParseJsonToOrder(jsString);
		declare.curOrder=order;
		UpdateElement();
		SendSetCountMessage();
		new Thread(){
			public void run(){
				declare.getMenuListDataObj().ChangeRecipeMapByItem(changedItem, order);
			}
		}.start();
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
		for(iterator=order.getOrderItems().iterator();iterator.hasNext();)
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
				if(id==declare.curOrder.getId())
				{
					FlashOrder();
				}
			}
		}
		
	}
    
    public class TableListAdapter extends BaseAdapter {

		int count = 0;
		int[] idArray;
		ImageDownloader imageDownloader;
		LayoutInflater mInflater;
		List<OrderItem> orderItemList;
		Context thisContext;
	    List<HashMap<String, Object>> mItemList;
	    List<HashMap<String, Object>> mTagList;
	    
	    public TableListAdapter(Context context, List<HashMap<String, Object>> mlist,List<HashMap<String, Object>> tags,List<OrderItem> orderItems) {
	        super();
	        thisContext=context;
	        mItemList = mlist;
	        mTagList=tags;
	        orderItemList=orderItems;
	        mInflater = LayoutInflater.from(context);
	        if(mlist == null){
	            count = 0;
	        }else{
	            count = mlist.size();
	        }
	        imageDownloader=new ImageDownloader();
	    }
	    public int getCount() {
	        return mItemList.size();
	    }

	    public Object getItem(int pos) {
	        return mItemList.get(pos);
	    }

	    public long getItemId(int pos) {
	        return pos;
	    }
	    
	    @Override  
		public boolean isEnabled(int position) {  
		   if (mTagList.contains(mItemList.get(position))) {  
		     return false;  
		   }  
		   return super.isEnabled(position);  
		}
	    

		public List<OrderItem> getOrderItemList() {
			return orderItemList;
		}
		public void setOrderItemList(List<OrderItem> orderItemList) {
			this.orderItemList = orderItemList;
		}
		
		public List<HashMap<String, Object>> getmItemList() {
			return mItemList;
		}
		public void setmItemList(ArrayList<HashMap<String, Object>> hashList) {
			this.mItemList = hashList;
		}
		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			// TODO Auto-generated method stub
			final View localView;
			if(mTagList.contains(mItemList.get(position))){
				localView = mInflater.inflate(R.layout.listtagitem, null);
				TextView titleTextView=(TextView)localView.findViewById(R.id.tagTitle);
			    titleTextView.setText(mItemList.get(position).get("tagTitle").toString());
			}
			else{
				localView = mInflater.inflate(R.layout.select_list_item, null);
				HashMap<String, Object> map=mItemList.get(position);
		        ImageView imgrecipe=(ImageView)localView.findViewById(R.id.imgctrl);
		        String strUrl=map.get("img").toString();
		        imageDownloader.download(strUrl, imgrecipe);
		        TextView tvTitle=(TextView)localView.findViewById(R.id.mtitle);
		        tvTitle.setText(map.get("title").toString());
		        TextView tvPrice=(TextView)localView.findViewById(R.id.mprice);
		        tvPrice.setText(map.get("price").toString());
		        TextView tvCount=(TextView)localView.findViewById(R.id.mcount);
		        tvCount.setText(map.get("count").toString());
				ImageView imgdelete=(ImageView)localView.findViewById(R.id.imgdelete);
				ImageView imgadd=(ImageView)localView.findViewById(R.id.imgadd);
	            
	            if(map.get("status")!=null&&map.get("status").toString().equals("1"))
	            {
	            	imgadd.setVisibility(View.INVISIBLE);
	            	imgdelete.setVisibility(View.INVISIBLE);
	            }
	            else {
	            	imgadd.setVisibility(View.VISIBLE);
	            	imgdelete.setVisibility(View.VISIBLE);
				}
	            
		        imgdelete.setTag(position);
		        imgadd.setTag(position);
		        
		        imgdelete.setOnClickListener(new View.OnClickListener() {
					
					@Override
					public void onClick(View v) {
						// TODO Auto-generated method stub
			             int position = Integer.parseInt(v.getTag().toString());
			             OrderItem oItem=TableListAdapter.this.orderItemList.get(position);
			             changedItem=oItem;
			             sendId=oItem.getRecipe().getId();
			             sendCount=-1;
			             
			            PostToServer();
					}
				});
		        
		        imgadd.setOnClickListener(new View.OnClickListener() {
					
					@Override
					public void onClick(View v) {
						// TODO Auto-generated method stub
						int position = Integer.parseInt(v.getTag().toString());
						OrderItem oItem=TableListAdapter.this.orderItemList.get(position);
						sendId=oItem.getRecipe().getId();
			            sendCount=1;
						changedItem=oItem;
				        PostToServer();
					}
				});
			}
			
	        
			return localView;
		}
	}
}
