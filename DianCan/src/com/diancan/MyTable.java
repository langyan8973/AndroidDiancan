package com.diancan;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.http.client.ClientProtocolException;
import org.json.JSONException;
import org.json.JSONObject;
import com.Utils.CustomViewBinder;
import com.Utils.DisplayUtil;
import com.Utils.JsonUtils;
import com.Utils.MenuUtils;
import com.custom.ClassListViewWidget;
import com.declare.Declare;
import com.download.HttpDownloader;
import com.mode.CategoryObj;
import com.mode.DeskObj;
import com.mode.SelectedMenuObj;
import com.mode.SelectedProduct;
import com.model.Order;
import com.model.OrderItem;
import com.model.Recipe;
import android.app.Activity;
import android.app.LocalActivityManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

public class MyTable extends Activity {
	LinearLayout rootLayout;
	LinearLayout rootLyt;
	ListView selectListView;
	Hashtable<String, ClassListViewWidget> dicWidgets;
	HashMap<String, List<OrderItem>> hashOrderItems;
	Button overButton;
	Button flashButton;
	ProgressBar mProgressBar;
	TextView sumTextView;
	BroadcastReceiver receiver;
	boolean isSelf=false;
	String sumString;	
	int sendId,sendCount;
	Declare declare;
	
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
            }  
        }  
    }; 
		
    @Override
    protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.mytable);
		sumString=getResources().getString(R.string.infostr_sum);
		rootLayout=(LinearLayout)findViewById(R.id.rootLayout);
		rootLyt=(LinearLayout)findViewById(R.id.rootLyt);
		sumTextView=(TextView)findViewById(R.id.sumText);
		sumTextView.setTextColor(Color.WHITE);
		sumTextView.setTextSize(DisplayUtil.dip2px(16));
		
		mProgressBar=(ProgressBar)findViewById(R.id.httppro);
		
		overButton=(Button)findViewById(R.id.overbtn);
		overButton.setText(getResources().getString(R.string.btnstr_over));
		overButton.setOnClickListener(new OverBtnOnclick());
		
		flashButton=(Button)findViewById(R.id.BtnFlash);
		flashButton.setOnClickListener(new FlashOnClick());
		
		dicWidgets=new Hashtable<String, ClassListViewWidget>();
		hashOrderItems=new HashMap<String, List<OrderItem>>();
		
		declare=(Declare)getApplicationContext();
    }
    @Override
	protected void onResume() {
		// TODO Auto-generated method stub
    	mProgressBar.setVisibility(View.INVISIBLE);
    	dicWidgets.clear();
    	hashOrderItems.clear();
    	rootLayout.removeAllViews();
    	if(declare.curOrder!=null)
    	{
    		InitHashOrderItems();
    		CreateElements();
    	}
    	else {
    		overButton.setVisibility(View.GONE);
		} 	
		super.onResume();
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
    		if(!hashOrderItems.containsKey(orderItem.getRecipe().getCname()))
    		{
    			List<OrderItem> orderItems=new ArrayList<OrderItem>();
    			hashOrderItems.put(orderItem.getRecipe().getCname(), orderItems);
    		}
    		List<OrderItem> oItems=hashOrderItems.get(orderItem.getRecipe().getCname());
    		oItems.add(orderItem);
    		declare.totalCount+=orderItem.getCount();
    		declare.totalPrice+=orderItem.getCount()*orderItem.getRecipe().getPrice();
    	}
    }
	
	private void UpdateHashList(List<OrderItem> orderItems,ArrayList<HashMap<String, Object>> hashList)
	{
		hashList.clear();
		for (Iterator iterator = orderItems.iterator(); iterator.hasNext();) {
			OrderItem orderItem = (OrderItem) iterator.next();
			HashMap<String, Object> map = new HashMap<String, Object>();
			map.put("title", orderItem.getRecipe().getName());
			map.put("price", "¥ "+orderItem.getRecipe().getPrice());
			String strCount=orderItem.getCount()+"";
			map.put("count", strCount);
			Integer kk=orderItem.getStatus();
			map.put("status", kk);
			Bitmap imgBitmap=HttpDownloader.getStream(MenuUtils.imageUrl+orderItem.getRecipe().getImage());
			map.put("img", imgBitmap);
			
			hashList.add(map);
		}
	}
    private void CreateElements()
    {
    	String strKey;
    	Set<String> meenum=hashOrderItems.keySet();
    	
    	Iterator iterator;
		for (iterator = meenum.iterator(); iterator.hasNext();) {
			strKey = (String) iterator.next();
			List<OrderItem> orderItemList=hashOrderItems.get(strKey);
			ClassListViewWidget clvw=CreateListWidget(orderItemList, strKey);
			clvw.setLayoutParams(new LayoutParams(LayoutParams.FILL_PARENT,LayoutParams.WRAP_CONTENT));
			clvw.setScrollContainer(true);
			rootLayout.addView(clvw);
			dicWidgets.put(strKey, clvw);			
		}
    	sumTextView.setText(sumString+declare.getTotalPrice());
    	
    	if(meenum.size()<=0)
    	{
    		overButton.setVisibility(View.GONE);
    	}
    	else {
			overButton.setVisibility(View.VISIBLE);
		}
    	SendSetCountMessage();
    }
    private void  UpdateElement() {
    	if(declare.curOrder==null)
    	{
			dicWidgets.clear();
			hashOrderItems.clear();
			rootLayout.removeAllViews();
			sumTextView.setText("");
    		return;
    	}
    	
    	String strKey;
    	Set<String> meenum;
    	if(hashOrderItems.size()>=dicWidgets.size())
    	{
    		meenum= hashOrderItems.keySet();
    		Iterator iterator;
    		for (iterator = meenum.iterator(); iterator.hasNext();) {
    			strKey = (String) iterator.next();
    			if(dicWidgets.containsKey(strKey))
        		{
        			ClassListViewWidget classListViewWidget=dicWidgets.get(strKey);
        			TableListAdapter sListAdapter=(TableListAdapter)classListViewWidget.listView.getAdapter();
        			ArrayList<HashMap<String, Object>> hashList=classListViewWidget.getHashList();
        			List<OrderItem> orderItems=hashOrderItems.get(strKey);
        			UpdateHashList(orderItems, hashList);
        			sListAdapter.setmItemList(hashList);
        			sListAdapter.notifyDataSetChanged();
        			sListAdapter.setOrderItemList(orderItems);
        			classListViewWidget.setListViewHeight(classListViewWidget.getListView());
        		}
        		else {
        			List<OrderItem> orderItems=hashOrderItems.get(strKey);
        			ClassListViewWidget clvw=CreateListWidget(orderItems, strKey);
        			clvw.setLayoutParams(new LayoutParams(LayoutParams.FILL_PARENT,LayoutParams.WRAP_CONTENT));
        			clvw.setScrollContainer(true);
        			rootLayout.addView(clvw);
        			dicWidgets.put(strKey, clvw);
        			registerForContextMenu(clvw.getListView());
				}
    		}
        	
    	}
    	else {
    		meenum= dicWidgets.keySet();
    		Iterator iterator;
    		for (iterator = meenum.iterator(); iterator.hasNext();) {
    			strKey = (String) iterator.next();
    			if(hashOrderItems.containsKey(strKey))
        		{
        			ClassListViewWidget classListViewWidget=dicWidgets.get(strKey);
        			TableListAdapter sListAdapter=(TableListAdapter)classListViewWidget.listView.getAdapter();
        			ArrayList<HashMap<String, Object>> hashList=classListViewWidget.getHashList();
        			List<OrderItem> orderItems=hashOrderItems.get(strKey);
        			UpdateHashList(orderItems, hashList);
        			sListAdapter.setmItemList(hashList);
        			sListAdapter.notifyDataSetChanged();
        			sListAdapter.setOrderItemList(orderItems);
        			classListViewWidget.setListViewHeight(classListViewWidget.getListView());
        		}
        		else {
        			ClassListViewWidget classListViewWidget1=dicWidgets.get(strKey);
        			rootLayout.removeView(classListViewWidget1);
        			dicWidgets.remove(strKey);
				}
    		}
		}
    	
    	sumTextView.setText(sumString+declare.getTotalPrice());
    	if(hashOrderItems.size()<=0)
    	{
    		overButton.setVisibility(View.GONE);
    	}
    	else {
			overButton.setVisibility(View.VISIBLE);
		}
	}
    private ClassListViewWidget CreateListWidget(List<OrderItem> orderItemlist,String strtitle)
    {
    	ArrayList<HashMap<String, Object>> hashList=new ArrayList<HashMap<String, Object>>();
    	UpdateHashList(orderItemlist, hashList);
    	TableListAdapter simpleAdapter1=new TableListAdapter(this, hashList,
				R.layout.select_list_item, new String[] { "title", "price","count","img"},
				new int[] { R.id.mtitle, R.id.mprice,R.id.mcount,R.id.imgctrl});
    	simpleAdapter1.setOrderItemList(orderItemlist);
    	ClassListViewWidget cLvWidget=new ClassListViewWidget(this, orderItemlist, hashList, strtitle);
    	simpleAdapter1.setViewBinder(new CustomViewBinder());
    	cLvWidget.listView.setAdapter(simpleAdapter1);
    	setListViewHeight(cLvWidget.listView);
    	return cLvWidget;
    }


    public void setListViewHeight(ListView lv) {
        ListAdapter la = lv.getAdapter();
        if(null == la) {
            return;
        }
        // calculate height of all items.
        int h = 0;
        final int cnt = la.getCount();
        for(int i=0; i<cnt; i++) {
            View item = la.getView(i, null, lv);
            item.measure(0, 0);
            h += item.getMeasuredHeight();
        }
        // reset ListView height
        ViewGroup.LayoutParams lp = lv.getLayoutParams();
        lp.height = h + (lv.getDividerHeight() * (cnt - 1));
        lp.width=android.view.ViewGroup.LayoutParams.FILL_PARENT;
        lv.setLayoutParams(lp);
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
					String resultString = HttpDownloader.alterRecipeCount(MenuUtils.initUrl, declare.curOrder.getId(), object);
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
			try {
				String jsString = HttpDownloader.RequestFinally(MenuUtils.initUrl, declare.curOrder.getId().toString());
				Order order=JsonUtils.ParseJsonToOrder(jsString);
			} catch (ClientProtocolException e) {
				// TODO Auto-generated catch block
				ShowError(e.getMessage());
			} catch (IOException e) {
				// TODO Auto-generated catch block
				ShowError(e.getMessage());
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				ShowError(e.getMessage());
			} catch (Throwable e) {
				// TODO Auto-generated catch block
				ShowError(e.getMessage());
			}
		}    	
    }
    
    class FlashOnClick implements OnClickListener{

		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			mProgressBar.setVisibility(View.VISIBLE);
			new Thread(new Runnable() {
				
				@Override
				public void run() {
					// TODO Auto-generated method stub
					try {
						String jsonString = HttpDownloader.getString(MenuUtils.initUrl+ "orders/" +declare.curOrder.getId() );
						httpHandler.obtainMessage(1,jsonString).sendToTarget();
					} catch (Throwable e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
						httpHandler.obtainMessage(0,e.getMessage()).sendToTarget();
					}
					
				}
			}).start();
			
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
		dicWidgets.clear();
		hashOrderItems.clear();
    	rootLayout.removeAllViews();
		InitHashOrderItems();
		CreateElements();
		new Thread(){
			public void run(){
				declare.getMenuListDataObj().ChangeRecipeMapByOrder(order);
			}
		}.start();
	}
    
    
    public void ToTableList()
    {
    	TableGroup parent = (TableGroup) getParent();
	    final LinearLayout contain = (LinearLayout) parent.findViewById(R.id.table_continer);
		contain.removeAllViews();
		Intent in = new Intent(getParent(), TableListPage.class);
		in.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
		LocalActivityManager manager = parent.getLocalActivityManager();
		Window window = manager.startActivity("TableListPage", in);
		
		View view=window.getDecorView();		
		contain.addView(view);
		LayoutParams params=(LayoutParams) view.getLayoutParams();
        params.width=LayoutParams.FILL_PARENT;
        params.height=LayoutParams.FILL_PARENT;
        view.setLayoutParams(params);
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
    
    public class TableListAdapter extends SimpleAdapter {

		public ArrayList<HashMap<String, Object>> getmItemList() {
			return mItemList;
		}
		public void setmItemList(ArrayList<HashMap<String, Object>> hashList) {
			this.mItemList = hashList;
		}
		int count = 0;
		int[] idArray;
		List<OrderItem> orderItemList;
		Context thisContext;
	    private ArrayList<HashMap<String, Object>> mItemList;
	    public TableListAdapter(Context context, List<? extends Map<String, Object>> data,
	            int resource, String[] from, int[] to) {
	        super(context, data, resource, from, to);
	        thisContext=context;
	        mItemList = (ArrayList<HashMap<String, Object>>) data;
	        if(data == null){
	            count = 0;
	        }else{
	            count = data.size();
	        }
	        idArray=to;
	    }
	    public int getCount() {
	        return mItemList.size();
	    }

	    public Object getItem(int pos) {
	        return pos;
	    }

	    public long getItemId(int pos) {
	        return pos;
	    }
	    

		public List<OrderItem> getOrderItemList() {
			return orderItemList;
		}
		public void setOrderItemList(List<OrderItem> orderItemList) {
			this.orderItemList = orderItemList;
		}
		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			// TODO Auto-generated method stub
			final View localView = super.getView(position, convertView, parent);
			final ListView listView=(ListView)parent;
			final int index=position;
	        
			ImageView imgdelete=(ImageView)localView.findViewById(R.id.imgdelete);
			ImageView imgadd=(ImageView)localView.findViewById(R.id.imgadd);
	        
	        OrderItem orderItem=TableListAdapter.this.orderItemList.get(position);
	        Map<String, Object> mMap=TableListAdapter.this.mItemList.get(position);
            int mCount=Integer.parseInt(mMap.get("count").toString());
            
            if(mMap.get("status")!=null)
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
		             final String cNameString=oItem.getRecipe().getCname().toString();
		             Map<String, Object> map=TableListAdapter.this.mItemList.get(position);
		             int count=Integer.parseInt(map.get("count").toString());
		             
		             sendId=oItem.getRecipe().getId();
		             sendCount=-1;
		             
//		             count--;
//		             
//		             if(count<=0)
//		             {
//		            	 //因为删除操作要把个数传出去所以要删除后再设置个数以便后面的同步操作
//		            	 declare.RemoveItemFromOrder(oItem);
//		            	 oItem.setCount(0);
//		             }
//		             else {
//		            	 oItem.setCount(count);
//		            	 declare.SubtractionItemCount(oItem);
//					}
//		            //同步
//		            declare.getMenuListDataObj().ChangeRecipeMapByObj(oItem);
//		            
//		            MenuUtils.bUpdating=true;
//		            sumTextView.setText(sumString+declare.getTotalPrice());
//		            if(count<=0)
//		             {
//		            	 TableListAdapter.this.orderItemList.remove(position);
//		            	 TableListAdapter.this.mItemList.remove(position); 
//		            	 Animation aAnimation=new AlphaAnimation(1, 0);
//		            	 aAnimation.setDuration(300);
//		            	 aAnimation.setAnimationListener(new AnimationListener() {
//							
//							@Override
//							public void onAnimationStart(Animation animation) {}
//							
//							@Override
//							public void onAnimationRepeat(Animation animation) {}
//							
//							@Override
//							public void onAnimationEnd(Animation animation) {
//								// TODO Auto-generated method stub								
//								TableListAdapter.this.notifyDataSetChanged();
//								if(TableListAdapter.this.mItemList.size()<=0)
//					            {
//					            	try {
//					            		ClassListViewWidget clWidget=dicWidgets.get(cNameString);
//					            		rootLayout.removeView(clWidget);
//					            		dicWidgets.remove(cNameString);
//									} catch (Exception e) {
//										// TODO Auto-generated catch block
//										e.printStackTrace();
//									}
//					            }
//					            else {
//					            	setListViewHeight(listView);
//								}
//							}
//						});
//		            	localView.startAnimation(aAnimation);
//		             }
//		             else {
//						map.put("count", count+"");
//						TableListAdapter.this.notifyDataSetChanged();
//					 }
//		            
//		            SendSetCountMessage();
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
					
//		             Map<String, Object> map=TableListAdapter.this.mItemList.get(position);
//		             int count=Integer.parseInt(map.get("count").toString());
//		             count++;
//		             map.put("count", count+"");
//		             
//		             TableListAdapter.this.notifyDataSetChanged();
//		             oItem.setCount(count);
//		             declare.AddItemToOrder(oItem); 
//		             declare.getMenuListDataObj().ChangeRecipeMapByObj(oItem);
//			         MenuUtils.bUpdating=true;
//			         sumTextView.setText(sumString+declare.getTotalPrice());	
//			         setListViewHeight(listView);
//			         SendSetCountMessage();
			         PostToServer();
				}
			});
	        
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
