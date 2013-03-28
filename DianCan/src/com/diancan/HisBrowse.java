package com.diancan;

import java.text.ParseException;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import com.diancan.Helper.OrderHelper;
import com.diancan.Helper.RestaurantHttpHelper;
import com.diancan.Utils.MenuUtils;
import com.diancan.custom.adapter.HisBrowseAdapter;
import com.diancan.diancanapp.AppDiancan;
import com.diancan.http.HttpCallback;
import com.diancan.http.HttpHandler;
import com.diancan.http.ImageDownloader;
import com.diancan.model.HisRestaurant;
import com.diancan.model.MyRestaurant;
import com.diancan.model.favorite;

import android.app.Activity;
import android.app.LocalActivityManager;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Message;
import android.util.SparseIntArray;
import android.view.View;
import android.view.Window;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;

public class HisBrowse extends Activity implements OnClickListener,
	OnItemClickListener,HttpCallback{
	AppDiancan appDiancan;
	Button btnBack;
	ListView browseListView;
	ProgressBar mProgressBar;
	HttpHandler mHandler;
	List<HisRestaurant> mHisRestaurants;
	HisBrowseAdapter browseAdapter;
	SparseIntArray favoriteSparseIntArray = new SparseIntArray();
	RestaurantHttpHelper restaurantHttpHelper;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.hisbrowse);
		
		appDiancan = (AppDiancan)getApplicationContext();
		restaurantHttpHelper = new RestaurantHttpHelper(this, appDiancan);
		btnBack = (Button)findViewById(R.id.bt_back);
		btnBack.setOnClickListener(this);
		browseListView = (ListView)findViewById(R.id.historyList);
		browseListView.setOnItemClickListener(this);
		mProgressBar = (ProgressBar)findViewById(R.id.httppro);
		restaurantHttpHelper.RequestFavorites();
	}

	
	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		QueryHisRestaurants();
		displayListView();
	}
	
	@Override
	public void RequestComplete(Message msg) {
		// TODO Auto-generated method stub
		mProgressBar.setVisibility(View.GONE);
		switch (msg.what) {
		case HttpHandler.REQUEST_FAVORITES:
        	List<favorite> favorites = (List<favorite>)msg.obj;
        	if(favorites==null||favorites.size()==0){
				return;
			}
			Iterator<favorite> iterator;
			for(iterator=favorites.iterator();iterator.hasNext();){
				favorite f=iterator.next();
				favoriteSparseIntArray.put(f.getRid(), f.getId());
			}
			if(browseAdapter!=null){
				browseAdapter.notifyDataSetChanged();
			}
			break;
        case HttpHandler.POST_FAVORITE:
        	favorite f = (favorite)msg.obj;
        	if(favoriteSparseIntArray==null){
        		favoriteSparseIntArray = new SparseIntArray();
        	}
        	favoriteSparseIntArray.put(f.getRid(), f.getId());
        	browseAdapter.notifyDataSetChanged();
        	break;
        case HttpHandler.DELETE_FAVORITE:
        	int rid = Integer.parseInt(msg.obj.toString());
        	favoriteSparseIntArray.delete(rid);
        	browseAdapter.notifyDataSetChanged();
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


	@Override
	public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
		// TODO Auto-generated method stub
		HisRestaurant hisRestaurant = mHisRestaurants.get(arg2);
		MyRestaurant myRestaurant=new MyRestaurant();
		myRestaurant.setId(hisRestaurant.getRid());
		myRestaurant.setName(hisRestaurant.getRname());
		myRestaurant.setImage(hisRestaurant.getImage());
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
		if(v.getId()==R.id.bt_back){
			ToMainFirstPage();
		}
		else if(v.getId()==R.id.favoriteBtn){
			int rid = Integer.parseInt(v.getTag().toString());
			mProgressBar.setVisibility(View.VISIBLE);
			if(favoriteSparseIntArray.get(rid)==0){
				restaurantHttpHelper.favoriteRestaurant(rid);
			}
			else{
				restaurantHttpHelper.deleteFavorite(rid);
			}
		}
	}
	
	private void  QueryHisRestaurants(){
		mProgressBar.setVisibility(View.VISIBLE);
		try {
			mHisRestaurants = MenuUtils.getHisRestaurants();
			mProgressBar.setVisibility(View.GONE);
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			ShowError(e.getMessage());
		}
	}
	
	private void displayListView(){
		if(mHisRestaurants==null||mHisRestaurants.size()<=0){
			return;
		}
		Date curDate = new Date(System.currentTimeMillis());
        if(browseAdapter==null){
        	Drawable[] layers={getResources().getDrawable(R.drawable.imagewaiting)};
        	ImageDownloader imgDownloader=new ImageDownloader(layers);
    		browseAdapter = new HisBrowseAdapter(this,mHisRestaurants,getLayoutInflater(),imgDownloader,favoriteSparseIntArray);
        	browseAdapter.setMyClickListener(this);
        	browseListView.setAdapter(browseAdapter);
        }
        else{
        	browseAdapter.notifyDataSetChanged();
        }
		
	}

	private void ShowError(String errString){
		mProgressBar.setVisibility(View.GONE);
		Toast.makeText(this, errString, Toast.LENGTH_SHORT);
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
		MenuGroup.back_id = MenuGroup.ID_HISBROWSE;
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

}
