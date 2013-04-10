package com.diancan;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import com.diancan.Helper.OrderHelper;
import com.diancan.Helper.RestaurantHttpHelper;
import com.diancan.Helper.SearchAdapterHelper;
import com.diancan.Utils.DisplayUtil;
import com.diancan.custom.adapter.CityArrayAdapter;
import com.diancan.custom.adapter.RestaurantArrayAdapter;
import com.diancan.diancanapp.AppDiancan;
import com.diancan.http.HttpCallback;
import com.diancan.http.HttpHandler;
import com.diancan.http.ImageDownloader;
import com.diancan.model.MyRestaurant;
import com.diancan.model.Restaurant;
import com.diancan.model.city;
import com.diancan.model.favorite;
import android.app.Activity;
import android.app.LocalActivityManager;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Message;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.SparseIntArray;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.TranslateAnimation;
import android.view.animation.Animation.AnimationListener;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.SimpleAdapter;
import android.widget.TextView.OnEditorActionListener;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.TextView;

public class SearchPage extends Activity implements HttpCallback,OnClickListener,
OnItemClickListener,TextWatcher,SearchAdapterHelper,OnEditorActionListener {

	ListView restaurantListView;
	Button backButton;
	EditText mEditText;
	ImageView mClearImageView;
	ProgressBar mProgressBar;
	List<Restaurant> mRestaurants;
	AppDiancan appDiancan;
	ImageDownloader imgDownloader;
	RestaurantArrayAdapter<Restaurant> restaurantAdapter;
	int sWidth,sHeight,topHeight,tabHeight;
	SparseIntArray favoriteIntArray = new SparseIntArray();
	RestaurantHttpHelper restaurantHttpHelper;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.searchpage);
		topHeight = (int)getResources().getDimension(R.dimen.topbar_height);
		tabHeight = (int)getResources().getDimension(R.dimen.tabbar_height);
		sWidth = DisplayUtil.PIXWIDTH;
		sHeight = DisplayUtil.PIXHEIGHT-topHeight-tabHeight;
		
		restaurantListView=(ListView)findViewById(R.id.rList);
		restaurantListView.setOnItemClickListener(this);
		backButton=(Button)findViewById(R.id.bt_back);
		backButton.setOnClickListener(this);
		mEditText = (EditText)findViewById(R.id.searchExt);
		mEditText.addTextChangedListener(this);
		mEditText.setOnEditorActionListener(this);
		mClearImageView = (ImageView)findViewById(R.id.ImgClear);
		mClearImageView.setOnClickListener(this);
		mProgressBar = (ProgressBar)findViewById(R.id.httppro);
		appDiancan=(AppDiancan)getApplicationContext();
		restaurantHttpHelper = new RestaurantHttpHelper(this, appDiancan);
		mRestaurants = new ArrayList<Restaurant>();
		Drawable[] layers={getResources().getDrawable(R.drawable.imagewaiting)};
		imgDownloader=new ImageDownloader(layers);
		
		//请求收藏列表
		restaurantHttpHelper.RequestFavorites();
		
		if(appDiancan.selectedCity!=null){
			mProgressBar.setVisibility(View.VISIBLE);
			restaurantHttpHelper.RequestRestaurants();
		}
	}

	
	
	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		restaurantAdapter = null;
		mRestaurants = null;
		imgDownloader.clearCache();
		imgDownloader = null;
		restaurantHttpHelper = null;
		System.gc();
	}



	@Override
	public void SetListViewHeight(int count) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void afterTextChanged(Editable s) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void beforeTextChanged(CharSequence s, int start, int count,
			int after) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onTextChanged(CharSequence s, int start, int before, int count) {
		// TODO Auto-generated method stub
		restaurantAdapter.getFilter().filter(s);
		if(count<=0){
			mClearImageView.setVisibility(View.GONE);
		}
		else{
			mClearImageView.setVisibility(View.VISIBLE);
		}
	}

	@Override
	public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
		// TODO Auto-generated method stub
		if(arg0.getId()==R.id.rList){
			InputMethodManager imm = (InputMethodManager)getSystemService(SearchPage.this.INPUT_METHOD_SERVICE);
			imm.hideSoftInputFromWindow(mEditText.getWindowToken(), 0);
			Restaurant restaurant = mRestaurants.get(arg2);
			MyRestaurant myRestaurant=new MyRestaurant(restaurant);
			appDiancan.myRestaurant=myRestaurant;
			if(appDiancan.myOrder!=null){
				if(appDiancan.myOrderHelper==null){
					appDiancan.myOrderHelper = new OrderHelper(appDiancan.myOrder,getString(R.string.strportion));
				}
				else{
					appDiancan.myOrderHelper.SetOrderAndItemDic(appDiancan.myOrder);
				}
			}
			ToRecipeListPage();
		}
		
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.bt_back:
			ToMainFirstPage();
			break;
		case R.id.ImgClear:
			mEditText.setText("");
			mProgressBar.setVisibility(View.VISIBLE);
			restaurantHttpHelper.RequestRestaurants();
			break;
		case R.id.favoriteBtn:
			int rid = Integer.parseInt(v.getTag().toString());
			mProgressBar.setVisibility(View.VISIBLE);
			if(favoriteIntArray.get(rid)==0){
				restaurantHttpHelper.favoriteRestaurant(rid);
			}
			else{
				restaurantHttpHelper.deleteFavorite(rid);
			}
			break;
		default:
			break;
		}
	}
	
	@Override
	public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
		// TODO Auto-generated method stub
		 if (actionId == EditorInfo.IME_ACTION_SEARCH)     
         {     
			 String strkey = mEditText.getText().toString();
			 mProgressBar.setVisibility(View.VISIBLE);
			 restaurantHttpHelper.searchRestaurants(strkey);
         }
		return false;
	}
  	

	@Override
	public void RequestComplete(Message msg) {
		// TODO Auto-generated method stub
		mProgressBar.setVisibility(View.GONE);
		switch(msg.what) {  
        case HttpHandler.REQUEST_RESTAURANTS: 
        	mRestaurants = (List<Restaurant>)msg.obj;
        	DisplayRestaurants();
            break; 
        case HttpHandler.RESULT_NULL:
        	if(mRestaurants!=null){
        		mRestaurants.clear();
        	}
        	DisplayRestaurants();
        	break;
    	case HttpHandler.REQUEST_FAVORITES:
        	List<favorite> favorites = (List<favorite>)msg.obj;
        	if(favorites==null||favorites.size()==0){
				return;
			}
			Iterator<favorite> iterator;
			for(iterator=favorites.iterator();iterator.hasNext();){
				favorite f=iterator.next();
				favoriteIntArray.put(f.getRid(), f.getId());
			}
			if(restaurantAdapter!=null){
				restaurantAdapter.setfSparseIntArray(favoriteIntArray);
				restaurantAdapter.notifyDataSetChanged();
			}
			break;
        case HttpHandler.POST_FAVORITE:
        	favorite f = (favorite)msg.obj;
        	if(favoriteIntArray==null){
        		favoriteIntArray = new SparseIntArray();
        	}
        	favoriteIntArray.put(f.getRid(), f.getId());
        	restaurantAdapter.notifyDataSetChanged();
        	break;
        case HttpHandler.DELETE_FAVORITE:
        	int rid = Integer.parseInt(msg.obj.toString());
        	favoriteIntArray.delete(rid);
        	restaurantAdapter.notifyDataSetChanged();
        	break;
        default:
            break;
        }
	}

	@Override
	public void RequestError(String errString) {
		// TODO Auto-generated method stub
		mProgressBar.setVisibility(View.GONE);
		showError(errString);
	}
	
	private void showError(String errString){
		Toast.makeText(this, errString, Toast.LENGTH_SHORT).show();
	}
	
	
	/**
	 * 显示餐厅列表
	 */
	private void DisplayRestaurants(){
		restaurantAdapter = new RestaurantArrayAdapter<Restaurant>(this,
				R.layout.list_item_restaurant, mRestaurants,favoriteIntArray);
		restaurantAdapter.setImageDownloader(imgDownloader);
		restaurantAdapter.setMyClickListener(this);
		restaurantAdapter.setmAdapterHelper(this);
		restaurantListView.setAdapter(restaurantAdapter);
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
		MenuGroup.back_id = MenuGroup.ID_SEARCHPAGE;
		intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
		Window window = manager.startActivity(MenuGroup.ID_RECIPLIST, intent);
		View view=window.getDecorView();		
		contain.addView(view);
		LayoutParams params=(LayoutParams) view.getLayoutParams();
        params.width=LayoutParams.FILL_PARENT;
        params.height=LayoutParams.FILL_PARENT;
        view.setLayoutParams(params);
        view.startAnimation(animation);
  	}
  	
  	
  	
//  	private void startTransAnimation(){
//  		int duration = 500;
//		Animation animation1; 
//		Animation animation2; 
//		if(cityLayout.getVisibility()==View.GONE){
//			cityLayout.setVisibility(View.VISIBLE);
//			animation1 = new TranslateAnimation(0, 0, 0, sHeight);
//			animation2 = new TranslateAnimation(0,0,-sHeight,0);
//			
//			animation2.setAnimationListener(new AnimationListener() {
//				
//				@Override
//				public void onAnimationStart(Animation animation) {
//					// TODO Auto-generated method stub
//					
//				}
//				
//				@Override
//				public void onAnimationRepeat(Animation animation) {
//					// TODO Auto-generated method stub
//					
//				}
//				
//				@Override
//				public void onAnimationEnd(Animation animation) {
//					// TODO Auto-generated method stub
//					restaurantLayout.clearAnimation();
//					restaurantLayout.setVisibility(View.GONE);
//					cityLayout.clearAnimation();
//				}
//			});
//		}
//		else{
//			restaurantLayout.setVisibility(View.VISIBLE);
//			animation2 = new TranslateAnimation(0, 0, 0, -sHeight);
//			animation1 = new TranslateAnimation(0,0,sHeight,0);
//			animation2.setAnimationListener(new AnimationListener() {
//				
//				@Override
//				public void onAnimationStart(Animation animation) {
//					// TODO Auto-generated method stub
//					
//				}
//				
//				@Override
//				public void onAnimationRepeat(Animation animation) {
//					// TODO Auto-generated method stub
//					
//				}
//				
//				@Override
//				public void onAnimationEnd(Animation animation) {
//					// TODO Auto-generated method stub
//					restaurantLayout.clearAnimation();
//					cityLayout.clearAnimation();
//					cityLayout.setVisibility(View.GONE);
//				}
//			});
//		}
//		animation1.setDuration(duration);
//		animation2.setDuration(duration);
//		
//		restaurantLayout.startAnimation(animation1);
//		cityLayout.startAnimation(animation2);
//  	}
}
