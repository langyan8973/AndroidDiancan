package com.diancan;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import com.diancan.Helper.OrderHelper;
import com.diancan.Helper.RestaurantHttpHelper;
import com.diancan.Helper.SearchAdapterHelper;
import com.diancan.Utils.DisplayUtil;
import com.diancan.custom.adapter.RestaurantArrayAdapter;
import com.diancan.custom.adapter.RestaurantArrayAdapter.ViewHolder;
import com.diancan.diancanapp.AppDiancan;
import com.diancan.http.HttpCallback;
import com.diancan.http.HttpHandler;
import com.diancan.http.ImageDownloader;
import com.diancan.model.MyRestaurant;
import com.diancan.model.Restaurant;
import com.diancan.model.favorite;
import android.app.Activity;
import android.app.LocalActivityManager;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Message;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.Window;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Toast;

public class FavoriteListPage extends Activity implements HttpCallback,OnClickListener,OnItemClickListener,
	TextWatcher,SearchAdapterHelper{
	
	ListView favoritesListView;
	Button backButton;
	EditText mEditText;
	ImageView mClearImageView;
	ProgressBar mProgressBar;
	List<favorite> mFavorites;
	AppDiancan appDiancan;
	ImageDownloader imgDownloader;
	RestaurantArrayAdapter<favorite> restaurantAdapter;
	int sWidth,sHeight,topHeight,tabHeight;
	RestaurantHttpHelper restaurantHttpHelper;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.favoritelistpage);
		
		topHeight = (int)getResources().getDimension(R.dimen.topbar_height);
		tabHeight = (int)getResources().getDimension(R.dimen.tabbar_height);
		sWidth = DisplayUtil.PIXWIDTH;
		sHeight = DisplayUtil.PIXHEIGHT-topHeight-tabHeight;
		
		favoritesListView=(ListView)findViewById(R.id.rList);
		favoritesListView.setOnItemClickListener(this);
		backButton=(Button)findViewById(R.id.bt_back);
		backButton.setOnClickListener(this);
		mEditText = (EditText)findViewById(R.id.searchExt);
		mEditText.addTextChangedListener(this);
		mClearImageView = (ImageView)findViewById(R.id.ImgClear);
		mClearImageView.setOnClickListener(this);
		mProgressBar = (ProgressBar)findViewById(R.id.httppro);
		appDiancan=(AppDiancan)getApplicationContext();
		restaurantHttpHelper = new RestaurantHttpHelper(this, appDiancan);
		mFavorites = new ArrayList<favorite>();
		Drawable[] layers={getResources().getDrawable(R.drawable.imagewaiting)};
		imgDownloader=new ImageDownloader(layers);
		
		//请求收藏列表
		mProgressBar.setVisibility(View.VISIBLE);
		restaurantHttpHelper.RequestFavorites();
	}
	
	

	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		imgDownloader.clearCache();
		imgDownloader = null;
		restaurantAdapter = null;
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
			InputMethodManager imm = (InputMethodManager)getSystemService(FavoriteListPage.this.INPUT_METHOD_SERVICE);
			imm.hideSoftInputFromWindow(mEditText.getWindowToken(), 0);
			ViewHolder viewHolder = (ViewHolder)arg1.getTag();
			String rnameString = viewHolder.titleTextView.getText().toString();
			int rid = Integer.parseInt(viewHolder.titleTextView.getTag().toString());
			MyRestaurant myRestaurant=new MyRestaurant();
			myRestaurant.setId(rid);
			myRestaurant.setName(rnameString);
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
			ToUserInfoPage();
			break;
		case R.id.ImgClear:
			mEditText.setText("");
			break;
		case R.id.favoriteBtn:
			int rid = Integer.parseInt(v.getTag().toString());
			mProgressBar.setVisibility(View.VISIBLE);
			restaurantHttpHelper.deleteFavorite(rid);
			break;
		default:
			break;
		}
	}

	@Override
	public void RequestComplete(Message msg) {
		// TODO Auto-generated method stub
		mProgressBar.setVisibility(View.GONE);
		switch(msg.what) {  
    	case HttpHandler.REQUEST_FAVORITES:
        	List<favorite> favorites = (List<favorite>)msg.obj;
        	if(favorites==null||favorites.size()==0){
				mFavorites.clear();
			}
        	mFavorites = favorites;
        	DisplayFavorites();
			break;
        case HttpHandler.DELETE_FAVORITE:
        	int rid = Integer.parseInt(msg.obj.toString());
        	parseDeleteFavorite(rid);
        	break;
        default:
            break;
        }
	}

	@Override
	public void RequestError(String errString) {
		// TODO Auto-generated method stub
		mProgressBar.setVisibility(View.GONE);
		ShowError(errString);
	}

	private void ShowError(String errString){
		Toast.makeText(this, errString, Toast.LENGTH_SHORT).show();
	}
	
	/**
	 * 显示收藏列表
	 */
	private void DisplayFavorites(){
		restaurantAdapter = new RestaurantArrayAdapter<favorite>(this,
				R.layout.list_item_restaurant, mFavorites,null);
		restaurantAdapter.setImageDownloader(imgDownloader);
		restaurantAdapter.setMyClickListener(this);
		restaurantAdapter.setmAdapterHelper(this);
		favoritesListView.setAdapter(restaurantAdapter);
	}
	
	private void parseDeleteFavorite(int rid){
		Iterator<favorite> iterator;
		for(iterator = mFavorites.iterator();iterator.hasNext();){
			favorite f = iterator.next();
			if(f.getRid()==rid){
				mFavorites.remove(f);
				restaurantAdapter.notifyDataSetChanged();
				break;
			}
		}
	}
	
	/**
  	 * 跳回用户页
  	 */
  	private void ToUserInfoPage(){
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
		Intent in = new Intent(this.getParent(), UserInfoActivity.class);
		in.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
		Window window = manager.startActivity(MenuGroup.ID_USERINFO, in);
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
		MenuGroup.back_id = MenuGroup.ID_FAVORITELISTPAGE;
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
}
