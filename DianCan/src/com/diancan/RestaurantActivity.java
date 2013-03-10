package com.diancan;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import com.diancan.Helper.OrderHelper;
import com.diancan.Utils.MenuUtils;
import com.diancan.diancanapp.AppDiancan;
import com.diancan.http.HttpCallback;
import com.diancan.http.HttpHandler;
import com.diancan.http.ImageDownloader;
import com.diancan.model.MyRestaurant;
import com.diancan.model.Restaurant;
import com.google.zxing.common.StringUtils;

import android.app.Activity;
import android.app.LocalActivityManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;

public class RestaurantActivity extends Activity implements HttpCallback,OnClickListener,OnItemClickListener {
	ListView mListView;
	Button backButton;
	Button mapButton;
	List<Restaurant> mRestaurants;
	HttpHandler httpHandler;
	AppDiancan appDiancan;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.restaurant);
		httpHandler = new HttpHandler(this);
		mListView=(ListView)findViewById(R.id.rList);
		mListView.setOnItemClickListener(this);
		backButton=(Button)findViewById(R.id.bt_back);
		backButton.setOnClickListener(this);
		mapButton=(Button)findViewById(R.id.bt_map);
		mapButton.setOnClickListener(this);
		appDiancan=(AppDiancan)getApplicationContext();
		RequestRestaurants();
	}
	
	@Override
	public void RequestComplete(Message msg) {
		// TODO Auto-generated method stub
		switch(msg.what) {  
        case HttpHandler.REQUEST_RESTAURANTS: 
        	DisplayRestaurants();
            break; 
        default:
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
		RestaurantAdapter listAdapter=(RestaurantAdapter)mListView.getAdapter();
		HashMap<String, Object> map=(HashMap<String, Object>)listAdapter.getItem(arg2);
		String idString=map.get("id").toString();
		int id=Integer.parseInt(idString);
		
		MyRestaurant myRestaurant=new MyRestaurant();
		myRestaurant.setId(id);
		myRestaurant.setName(map.get("name").toString());
		appDiancan.myRestaurant=myRestaurant;
		if(appDiancan.myOrder!=null){
			if(appDiancan.myOrderHelper==null){
				appDiancan.myOrderHelper = new OrderHelper(appDiancan.myOrder);
			}
			else{
				appDiancan.myOrderHelper.SetOrderAndItemDic(appDiancan.myOrder);
			}
		}
		ToRecipeListPage();
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.bt_back:
			ToMainFirstPage();
			break;
		case R.id.bt_map:
			ToMapPage();
			break;
		default:
			break;
		}
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
					AppDiancan declare=(AppDiancan)RestaurantActivity.this.getApplicationContext();
					mRestaurants=MenuUtils.getAllRestaurants(declare.udidString);
					if(mRestaurants==null||mRestaurants.size()==0){
						httpHandler.obtainMessage(HttpHandler.REQUEST_ERROR,"没有餐厅！").sendToTarget();
						return;
					}
					httpHandler.obtainMessage(HttpHandler.REQUEST_RESTAURANTS).sendToTarget();
				} catch (Exception e) {
					// TODO Auto-generated catch block
					httpHandler.obtainMessage(HttpHandler.REQUEST_ERROR,e.getMessage()).sendToTarget();
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
			String imgString=restaurant.getImage();
			if(imgString==null||imgString=="")
			{
				map.put("img", MenuUtils.imageUrl+"2e751fc2-62be-4bec-9196-e338010ce05c.png");
			}
			else {
				map.put("img", MenuUtils.imageUrl+MenuUtils.IMAGE_SMALL+imgString);
			}
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
  	
  	/**
  	 * 跳回导航页
  	 */
  	private void ToMainFirstPage(){
  		MenuGroup parent = (MenuGroup)this.getParent();
		LocalActivityManager manager = parent.getLocalActivityManager();
		Activity activity=manager.getCurrentActivity();
		Window w1=activity.getWindow();
		View v1=w1.getDecorView();
		Animation sAnimation=AnimationUtils.loadAnimation(this, R.anim.push_right_out);
		v1.startAnimation(sAnimation);
	    final LinearLayout contain = (LinearLayout) parent.findViewById(R.id.group_Layout);
		contain.removeAllViews();
		
		Animation animation = AnimationUtils.loadAnimation(this, R.anim.push_right_in);
		Intent in = new Intent(this.getParent(), MainFirstPage.class);
		in.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
		Window window = manager.startActivity(MenuGroup.ID_MAINFIRST, in);
		View view=window.getDecorView();		
		contain.addView(view);
		LayoutParams params=(LayoutParams) view.getLayoutParams();
        params.width=LayoutParams.FILL_PARENT;
        params.height=LayoutParams.FILL_PARENT;
        view.setLayoutParams(params);
        view.startAnimation(animation);
	}
  	
  	private void ToMapPage(){
  		MenuGroup parent = (MenuGroup)this.getParent();
	    final LinearLayout contain = (LinearLayout) parent.findViewById(R.id.group_Layout);
		contain.removeAllViews();
		
		Intent in = new Intent(this.getParent(), MapViewActivity.class);
		in.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
		LocalActivityManager manager = parent.getLocalActivityManager();
		Window window = manager.startActivity("MapViewActivity", in);
		
		View view=window.getDecorView();		
		contain.addView(view);
		LayoutParams params=(LayoutParams) view.getLayoutParams();
        params.width=LayoutParams.FILL_PARENT;
        params.height=LayoutParams.FILL_PARENT;
        view.setLayoutParams(params);
  	}
  	
  	private void ToRecipeListPage(){
  		MenuGroup parent = (MenuGroup)this.getParent();
		LocalActivityManager manager = parent.getLocalActivityManager();
		Activity activity=manager.getCurrentActivity();
		Window w1=activity.getWindow();
		View v1=w1.getDecorView();
		Animation sAnimation=AnimationUtils.loadAnimation(this, R.anim.push_left_out);
		v1.startAnimation(sAnimation);
	    final LinearLayout contain = (LinearLayout) parent.findViewById(R.id.group_Layout);
		contain.removeAllViews();
		
		Animation animation = AnimationUtils.loadAnimation(this, R.anim.push_left_in);
		Intent intent = new Intent(this.getParent(), RecipeList.class);
//		in.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
		Window window = manager.startActivity(MenuGroup.ID_RECIPLIST, intent);
		View view=window.getDecorView();		
		contain.addView(view);
		LayoutParams params=(LayoutParams) view.getLayoutParams();
        params.width=LayoutParams.FILL_PARENT;
        params.height=LayoutParams.FILL_PARENT;
        view.setLayoutParams(params);
        view.startAnimation(animation);
  	}
  	
  	class ViewHolder{
  		public ImageView imageView;
  		public TextView titleTextView;
  		public TextView addressTextView;
  		public TextView phoneTextView;
  	}
  	
	class RestaurantAdapter extends BaseAdapter{
		private ImageDownloader imageDownloader;
    	private LayoutInflater mInflater;  
    	private List<HashMap<String, Object>> mListData;
    	
    	public RestaurantAdapter(Context context,List<HashMap<String, Object>> listData){
    		mInflater = LayoutInflater.from(context);  
    		mListData = listData;  
    		Drawable[] layers={RestaurantActivity.this.getResources().getDrawable(R.drawable.imagewaiting)};
    		imageDownloader=new ImageDownloader(layers);
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
			ViewHolder viewHolder;
			if(convertView==null){
				viewHolder=new ViewHolder();
				convertView = mInflater.inflate(R.layout.list_item_restaurant, null);
				HashMap<String, Object> map=mListData.get(position);
			    TextView titleView=(TextView)convertView.findViewById(R.id.tv_name);
			    TextView addressView=(TextView)convertView.findViewById(R.id.tv_address);
			    TextView phoneView=(TextView)convertView.findViewById(R.id.tv_telephone);
			    ImageView recipeImg=(ImageView)convertView.findViewById(R.id.restaurant_image);
			    viewHolder.titleTextView=titleView;
			    viewHolder.addressTextView=addressView;
			    viewHolder.phoneTextView=phoneView;
			    viewHolder.imageView=recipeImg;
			    titleView.setText(map.get("name").toString());
			    addressView.setText(map.get("address").toString());
			    phoneView.setText(map.get("telephone").toString());
			    String strUrl=map.get("img").toString();
			    imageDownloader.download(strUrl, recipeImg);
			    convertView.setTag(viewHolder);
			}
			else{
				HashMap<String, Object> map=mListData.get(position);
				viewHolder=(ViewHolder)convertView.getTag();
				viewHolder.titleTextView.setText(map.get("name").toString());
			    viewHolder.addressTextView.setText(map.get("address").toString());
			    viewHolder.phoneTextView.setText(map.get("telephone").toString());
			    String strUrl=map.get("img").toString();
			    imageDownloader.download(strUrl, viewHolder.imageView);
			}
		     
			return convertView;
		}
		
	}
}
