package com.diancan;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.apache.http.client.ClientProtocolException;
import org.json.JSONException;
import org.json.JSONObject;
import com.Utils.DisplayUtil;
import com.Utils.JsonUtils;
import com.Utils.MenuUtils;
import com.custom.ImageDownloader;
import com.declare.Declare;
import com.download.HttpDownloader;
import com.model.Order;
import com.model.OrderItem;
import com.model.Recipe;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.LocalActivityManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

public class MyTable extends Activity {
	ListView orderListView;
	HashMap<String, List<OrderItem>> hashOrderItems;
	Button overButton;
	Button flashButton;
	ProgressBar mProgressBar;
	TextView sumTextView;
	String sumString;	
	int sendId,sendCount;
	Declare declare;
	NotifiReceiver receiver;
	
	private List<OrderItem> mOrderItems=new ArrayList<OrderItem>();
	private List<HashMap<String, Object>> itemlist = new ArrayList<HashMap<String, Object>>();  
	private List<HashMap<String, Object>> tagList = new ArrayList<HashMap<String, Object>>();
	
	@SuppressLint("HandlerLeak")
	private Handler httpHandler = new Handler() {  
        public void handleMessage (Message msg) {//此方法在ui线程运行   
            switch(msg.what) {  
            case 0: 
            	String errString=msg.obj.toString();
            	ShowError(errString);
                break;   
            case 1: 
            	String jsString=msg.obj.toString();
            	ParseOrder(jsString);
                break; 
            case 2:
            	String jsString2=msg.obj.toString();
//            	ParseItems(jsString2);
            	break;
            }  
        }  
    }; 
		
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
		overButton.setOnClickListener(new OverBtnOnclick());
		flashButton=(Button)findViewById(R.id.BtnFlash);
		flashButton.setOnClickListener(new FlashOnClick());
		declare=(Declare)getApplicationContext();
		receiver = new NotifiReceiver();
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
//        filter.addAction(Constants.ACTION_NOTIFICATION_CLICKED);
//        filter.addAction(Constants.ACTION_NOTIFICATION_CLEARED);
	    registerReceiver(receiver, filter);
	}
    
    @Override
	protected void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
		unregisterReceiver(receiver);
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
    	Order order=declare.curOrder;
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
		new Thread(new Runnable() {
			
			@Override
			public void run() {
				// TODO Auto-generated method stub
				try {
					String jsonString = HttpDownloader.getString(MenuUtils.initUrl+ "restaurants/"+declare.restaurantId+"/orders/" +declare.curOrder.getId(),
							declare.udidString);
					httpHandler.obtainMessage(1,jsonString).sendToTarget();
				} catch (Throwable e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
					httpHandler.obtainMessage(0,e.getMessage()).sendToTarget();
				}
				
			}
		}).start();
	}
    
    public void PostToServer()
    {
    	mProgressBar.setVisibility(View.VISIBLE);
    	//加减菜
		final JSONObject object = new JSONObject();
		try {
			object.put("rid", sendId);
			object.put("count", sendCount);
		} catch (JSONException e) {
		}	
		
		new Thread(new Runnable() {
			
			@Override
			public void run() {
				// TODO Auto-generated method stub
				try {
					String resultString = HttpDownloader.alterRecipeCount(MenuUtils.initUrl, declare.curOrder.getId(),
							declare.restaurantId, object,declare.udidString);
					httpHandler.obtainMessage(1,resultString).sendToTarget();
				} catch (Throwable e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
					httpHandler.obtainMessage(0,e.getMessage()).sendToTarget();
				}
				
			}
		}).start();
		
    }

    public class OverBtnOnclick implements View.OnClickListener{

		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			mProgressBar.setVisibility(View.VISIBLE);
			try {
				String urlString=MenuUtils.initUrl+"restaurants/"+declare.restaurantId+"/orders/"+declare.curOrder.getId()+"/tocheck";
				String jsString = HttpDownloader.RequestFinally(urlString,declare.udidString);
				httpHandler.obtainMessage(1,jsString).sendToTarget();
			} catch (ClientProtocolException e) {
				// TODO Auto-generated catch block
				httpHandler.obtainMessage(0,e.getMessage()).sendToTarget();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				httpHandler.obtainMessage(0,e.getMessage()).sendToTarget();
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				httpHandler.obtainMessage(0,e.getMessage()).sendToTarget();
			} catch (Throwable e) {
				// TODO Auto-generated catch block
				httpHandler.obtainMessage(0,e.getMessage()).sendToTarget();
			}
		}    	
    }
    
    class FlashOnClick implements OnClickListener{

		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			FlashOrder();
		}
    	
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
		new Thread(){
			public void run(){
				declare.getMenuListDataObj().ChangeRecipeMapByOrder(order);
			}
		}.start();
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
//    		if (Constants.ACTION_SHOW_NOTIFICATION.equals(action)) {
//                String notificationId = intent
//                        .getStringExtra(Constants.NOTIFICATION_ID);
//                String notificationApiKey = intent
//                        .getStringExtra(Constants.NOTIFICATION_API_KEY);
//                String notificationTitle = intent
//                        .getStringExtra(Constants.NOTIFICATION_TITLE);
//                String notificationMessage = intent
//                        .getStringExtra(Constants.NOTIFICATION_MESSAGE);
//                String notificationUri = intent
//                        .getStringExtra(Constants.NOTIFICATION_URI);
//
//                if(notificationTitle.equals("11")&&notificationMessage.equals(mOrder.getId().toString()))
//                {
//                	String jsonString = HttpDownloader.getString(MenuUtils.initUrl+ "orders/" +mOrder.getId() ,null);
//        			final Order order=JsonUtils.ParseJsonToOrder(jsonString);
//        			mOrder=order;
////        			dicWidgets.clear();
////        			hashOrderItems.clear();
////        	    	rootLayout.removeAllViews();
////        			InitHashOrderItems();
////        			CreateElements();
//                }
//    		}
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
				final ListView listView=(ListView)parent;
				final int index=position;
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
		        
		        OrderItem orderItem=TableListAdapter.this.orderItemList.get(position);
	            int mCount=Integer.parseInt(map.get("count").toString());
	            
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
			             Map<String, Object> map=TableListAdapter.this.mItemList.get(position);
			             int count=Integer.parseInt(map.get("count").toString());
			             
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
						
				         PostToServer();
					}
				});
			}
			
	        
//	        localView.setOnTouchListener(new View.OnTouchListener() {
//        		float sx,sy,ex,ey;
//        		boolean action;
//				@Override
//				public boolean onTouch(View v, MotionEvent event) {
//					// TODO Auto-generated method stub
//					if(event.getAction()==MotionEvent.ACTION_DOWN)
//					{
//						sx=event.getX();
//						sy=event.getY();
//						action=true;
//					}
//					else if(event.getAction()==MotionEvent.ACTION_MOVE)
//					{
//						ex=event.getX();
//						ey=event.getY();
//						if((ex-sx)>120&&action)
//						{
//							action=false;
//							final OrderItem orderItem=TableListAdapter.this.orderItemList.get(index);
//							Animation animation=AnimationUtils.loadAnimation(MyTable.this, R.anim.delete_anim);
//				            animation.setAnimationListener(new AnimationListener() {
//								
//								@Override
//								public void onAnimationStart(Animation animation) {}
//								
//								@Override
//								public void onAnimationRepeat(Animation animation) {}
//								
//								@Override
//								public void onAnimationEnd(Animation animation) {
//									// TODO Auto-generated method stub
//									sendId=orderItem.getRecipe().getId();
//									sendCount=-orderItem.getCount();
//						 			
//									delete(orderItem);
//									SendSetCountMessage();
//									PostToServer();
//								}
//							});
//				            localView.startAnimation(animation);
//						}
//						
//					}
//					return true;
//				}
//				public void delete(OrderItem orderItem)
//				{
//					declare.RemoveItemFromOrder(orderItem);	
//					orderItem.setCount(0);
//		            String cNameString=orderItem.getRecipe().getCname().toString();
//		            TableListAdapter.this.orderItemList.remove(index);
//		            TableListAdapter.this.mItemList.remove(index);
//		            TableListAdapter.this.notifyDataSetChanged();
//		            declare.getMenuListDataObj().ChangeRecipeMapByObj(orderItem);
//		            MenuUtils.bUpdating=true;
//		            sumTextView.setText(sumString+declare.getTotalPrice());
//		            if(TableListAdapter.this.mItemList.size()<=0)
//		            {
//		            	try {
//		            		ClassListViewWidget clWidget=dicWidgets.get(cNameString);
//		            		rootLayout.removeView(clWidget);
//		            		dicWidgets.remove(cNameString);
//						} catch (Exception e) {
//							// TODO Auto-generated catch block
//							e.printStackTrace();
//						}
//		            }
//		            else {
//		            	setListViewHeight(listView);
//					} 
//				}
//			});
	        
			return localView;
		}
	}
    public void SendSetCountMessage()
    {
    	Intent in = new Intent();
        in.setAction("setcount");
        in.addCategory(Intent.CATEGORY_DEFAULT);
        MyTable.this.sendBroadcast(in);
    }
}
