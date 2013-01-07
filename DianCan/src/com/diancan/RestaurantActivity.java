package com.diancan;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import com.Utils.MenuUtils;
import com.custom.ImageDownloader;
import com.declare.Declare;
import com.model.Restaurant;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;

public class RestaurantActivity extends Activity {
	ListView mListView;
	List<Restaurant> mRestaurants;
	private Handler httpHandler = new Handler() {  
        public void handleMessage (Message msg) {//此方法在ui线程运行   
            switch(msg.what) {  
            case 0: 
            	String errString=msg.obj.toString();
            	ShowError(errString);
                break;   
            case 1: 
            	DisplayRestaurants();
                break;  
            case 2:
//            	UpdateOrder();
            	break;
            case 3:
            	break;
            }  
        }  
    };
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.restaurant);
		
		mListView=(ListView)findViewById(R.id.rList);
		mListView.setOnItemClickListener(new ListItemClick());
		
		RequestRestaurants();
	}
	
	/**
	 * 请求餐厅数据
	 */
	private void RequestRestaurants(){
		new Thread(new Runnable() {
			
			@Override
			public void run() {
				// TODO Auto-generated method stub
				
				try {
					Declare declare=(Declare)RestaurantActivity.this.getApplicationContext();
					mRestaurants=MenuUtils.getAllRestaurants(declare.udidString);
					if(mRestaurants==null||mRestaurants.size()==0){
						httpHandler.obtainMessage(0,"没有餐厅！").sendToTarget();
						return;
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
	 * 显示餐厅列表
	 */
	private void DisplayRestaurants(){
		List<HashMap<String, Object>> hashiList=new ArrayList<HashMap<String,Object>>();
		Iterator<Restaurant> iterator;
		for(iterator=mRestaurants.iterator();iterator.hasNext();){
			Restaurant restaurant=iterator.next();
			HashMap<String, Object> map=new HashMap<String, Object>();
			map.put("name", restaurant.getName());
			map.put("id", restaurant.getId());
			map.put("address", restaurant.getAddress());
			map.put("telephone", restaurant.getTelephone());
			map.put("img", MenuUtils.imageUrl+"2e751fc2-62be-4bec-9196-e338010ce05c.png");
			hashiList.add(map);
		}
		
		RestaurantAdapter listAdapter=new RestaurantAdapter(this, hashiList);
		mListView.setAdapter(listAdapter);
	}
	
	/**
  	 * 显示错误信息
  	 * @param strMess
  	 */
  	public void ShowError(String strMess) {
		Toast toast = Toast.makeText(RestaurantActivity.this, strMess, Toast.LENGTH_SHORT); 
        toast.show();
	}
  	
  	/***
  	 * 列表点击
  	 * @author liuyan
  	 *
  	 */
	class ListItemClick implements OnItemClickListener{

		@Override
		public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
				long arg3) {
			// TODO Auto-generated method stub
			RestaurantAdapter listAdapter=(RestaurantAdapter)mListView.getAdapter();
			HashMap<String, Object> map=(HashMap<String, Object>)listAdapter.getItem(arg2);
			String idString=map.get("id").toString();
			int id=Integer.parseInt(idString);
			Declare declare=(Declare)RestaurantActivity.this.getApplicationContext();
			declare.restaurantId=id;
			Intent intent=new Intent(RestaurantActivity.this, Main.class);
		    startActivity(intent);
			
		}
		
	}
	
	class RestaurantAdapter extends BaseAdapter{
		private ImageDownloader imageDownloader;
    	private LayoutInflater mInflater;  
    	private List<HashMap<String, Object>> mListData;
    	
    	public RestaurantAdapter(Context context,List<HashMap<String, Object>> listData){
    		mInflater = LayoutInflater.from(context);  
    		mListData = listData;  
    		imageDownloader=new ImageDownloader();
    	}
		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			return mListData.size();
		}

		@Override
		public Object getItem(int position) {
			// TODO Auto-generated method stub
			return mListData.get(position);
		}

		@Override
		public long getItemId(int position) {
			// TODO Auto-generated method stub
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			// TODO Auto-generated method stub
			 convertView = mInflater.inflate(R.layout.list_item_restaurant, null); 
		     HashMap<String, Object> map=mListData.get(position);
		     TextView titleView=(TextView)convertView.findViewById(R.id.tv_name);
		     titleView.setText(map.get("name").toString());
		     TextView addressView=(TextView)convertView.findViewById(R.id.tv_address);
		     addressView.setText(map.get("address").toString());
		     TextView phoneView=(TextView)convertView.findViewById(R.id.tv_telephone);
		     phoneView.setText(map.get("telephone").toString());
		     ImageView recipeImg=(ImageView)convertView.findViewById(R.id.restaurant_image);
		     String strUrl=map.get("img").toString();
		     imageDownloader.download(strUrl, recipeImg);
		     
			return convertView;
		}
		
	}

}
