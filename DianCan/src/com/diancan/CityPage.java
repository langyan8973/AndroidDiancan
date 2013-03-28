package com.diancan;

import java.util.List;

import com.diancan.Helper.RestaurantHttpHelper;
import com.diancan.Helper.SearchAdapterHelper;
import com.diancan.Utils.DisplayUtil;
import com.diancan.custom.adapter.CityArrayAdapter;
import com.diancan.custom.adapter.CityArrayAdapter.CityViewHolder;
import com.diancan.diancanapp.AppDiancan;
import com.diancan.http.HttpCallback;
import com.diancan.http.HttpHandler;
import com.diancan.model.city;
import android.app.Activity;
import android.os.Bundle;
import android.os.Message;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.TranslateAnimation;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;

public class CityPage extends Activity implements HttpCallback,OnClickListener,
	OnItemClickListener,TextWatcher,SearchAdapterHelper{
	LinearLayout rootLayout;
	ListView cityListView;
	Button backButton;
	EditText mEditText;
	ImageView mClearImageView;
	ProgressBar mProgressBar;
	AppDiancan appDiancan;
	List<city> mCities;
	RestaurantHttpHelper restaurantHttpHelper;
	CityArrayAdapter<city> listAdapter;
	int sWidth,sHeight;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.citypage);
		sWidth = DisplayUtil.PIXWIDTH;
		sHeight = DisplayUtil.PIXHEIGHT;
		rootLayout = (LinearLayout)findViewById(R.id.rootLayout);
		cityListView = (ListView)findViewById(R.id.cityList);
		cityListView.setOnItemClickListener(this);
		backButton = (Button)findViewById(R.id.bt_back);
		backButton.setOnClickListener(this);
		mEditText = (EditText)findViewById(R.id.searchExt);
		mEditText.addTextChangedListener(this);
		mClearImageView = (ImageView)findViewById(R.id.ImgClear);
		mClearImageView.setOnClickListener(this);
		mProgressBar = (ProgressBar)findViewById(R.id.httppro);
		
		appDiancan = (AppDiancan)getApplicationContext();
		restaurantHttpHelper = new RestaurantHttpHelper(this, appDiancan);
		mProgressBar.setVisibility(View.VISIBLE);
		restaurantHttpHelper.getCities();
		
		startOpenAnimation();
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
		listAdapter.getFilter().filter(s);
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
		if(arg0.getId()==R.id.cityList){
			CityViewHolder viewHolder = (CityViewHolder)arg1.getTag();
			String cnameString = viewHolder.titleTextView.getText().toString();
			String idString = viewHolder.titleTextView.getTag().toString();
			
			if(!idString.equals(appDiancan.selectedCity.getId())){
				city c = new city();
				c.setId(idString);
				c.setName(cnameString);
				appDiancan.selectedCity = c;
			}
			startCloseAnimation();
		}
	}

	@Override
	public void RequestComplete(Message msg) {
		// TODO Auto-generated method stub
		mProgressBar.setVisibility(View.GONE);
		switch (msg.what) {
		case HttpHandler.GET_CITIES:
        	mCities = (List<city>)msg.obj;
        	displayCities();
			break;

		default:
			break;
		}
	}

	@Override
	public void RequestError(String errString) {
		// TODO Auto-generated method stub
		mProgressBar.setVisibility(View.GONE);
		Toast.makeText(this, errString, Toast.LENGTH_SHORT).show();
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.ImgClear:
			mEditText.setText("");
			mProgressBar.setVisibility(View.VISIBLE);
			break;
		case R.id.bt_back:
			startCloseAnimation();
			break;
		default:
			break;
		}
	}
	
	/**
	 * 显示城市列表
	 */
	private void displayCities(){
  		if(mCities==null || mCities.size()==0){
  			return;
  		}
  		listAdapter = new CityArrayAdapter<city>(this, R.layout.citylistitem,mCities);
  		listAdapter.setmAdapterHelper(this);
  		cityListView.setAdapter(listAdapter);
  	}
	
	private void startOpenAnimation(){
		Animation animation = new TranslateAnimation(0, 0, sHeight,0);
		animation.setDuration(500);
		rootLayout.startAnimation(animation);
	}
	
	private void startCloseAnimation(){
		Animation animation = new TranslateAnimation(0, 0, 0,sHeight);
		animation.setDuration(500);
		animation.setAnimationListener(new AnimationListener() {
			
			@Override
			public void onAnimationStart(Animation animation) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void onAnimationRepeat(Animation animation) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void onAnimationEnd(Animation animation) {
				// TODO Auto-generated method stub
				CityPage.this.finish();
			}
		});
		rootLayout.startAnimation(animation);
	}
}
