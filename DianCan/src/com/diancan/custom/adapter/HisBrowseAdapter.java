package com.diancan.custom.adapter;

import java.util.Date;
import java.util.List;

import com.diancan.R;
import com.diancan.Utils.MenuUtils;
import com.diancan.Utils.MyDateUtils;
import com.diancan.http.ImageDownloader;
import com.diancan.model.HisRestaurant;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.util.SparseIntArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class HisBrowseAdapter extends BaseAdapter {

	public  class HisBrowseItemViewHolder{
		public TextView restaurantView;
		public TextView timeTextView;
		public ImageView rImageView;
		public ImageView fovariteImageView;
	}
	private ImageDownloader imageDownloader;
	private List<HisRestaurant> hisRestaurants;
	protected LayoutInflater inflater;
	private Date currentDate;
	private SparseIntArray fSparseIntArray;
	private OnClickListener myClickListener;
	private Context mContext;
	
	public HisBrowseAdapter(Context context,List<HisRestaurant> restaurants,LayoutInflater flater,
			ImageDownloader downloader,SparseIntArray sparseIntArray){
		hisRestaurants = restaurants;
		inflater = flater;
		imageDownloader = downloader;
		currentDate = new Date(System.currentTimeMillis());
		fSparseIntArray = sparseIntArray;
		mContext = context;
	}
	
	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return hisRestaurants.size();
	}

	@Override
	public Object getItem(int position) {
		// TODO Auto-generated method stub
		return hisRestaurants.get(position);
	}

	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return hisRestaurants.get(position).getRid();
	}

	public OnClickListener getMyClickListener() {
		return myClickListener;
	}

	public void setMyClickListener(OnClickListener myClickListener) {
		this.myClickListener = myClickListener;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		// TODO Auto-generated method stub
		View view = convertView;
		HisBrowseItemViewHolder viewHolder;
		if(view==null){
			view = inflater.inflate(R.layout.browse_list_item, null);
			viewHolder = new HisBrowseItemViewHolder();
			viewHolder.restaurantView = (TextView)view.findViewById(R.id.tv_name);
			viewHolder.timeTextView = (TextView)view.findViewById(R.id.tv_time);
			viewHolder.rImageView = (ImageView)view.findViewById(R.id.restaurant_image);
			viewHolder.fovariteImageView = (ImageView)view.findViewById(R.id.favoriteBtn);
			view.setTag(viewHolder);
		}
		else{
			viewHolder = (HisBrowseItemViewHolder)view.getTag();
		}
		
		HisRestaurant hisRestaurant = hisRestaurants.get(position);
		viewHolder.restaurantView.setText(hisRestaurant.getRname());
		Date timeDate = hisRestaurant.getTime();
		viewHolder.timeTextView.setText(MyDateUtils.getStringFormDate(currentDate, timeDate));
		String strUrl;
		if(hisRestaurant.getImage()==null){
			strUrl=null;
		}
		else{
			strUrl=MenuUtils.imageUrl+MenuUtils.IMAGE_SMALL+hisRestaurant.getImage();
		}
		imageDownloader.download(strUrl, viewHolder.rImageView);
		
		Drawable drawable;
	    if(fSparseIntArray!=null && fSparseIntArray.get(hisRestaurant.getRid())!=0){
	    	drawable = mContext.getResources().getDrawable(R.drawable.favorite_btn_select);
	    }
	    else{
	    	drawable = mContext.getResources().getDrawable(R.drawable.favorite_btn);
	    }
	    viewHolder.fovariteImageView.setImageDrawable(drawable);
	    viewHolder.fovariteImageView.setTag(hisRestaurant.getRid());
	    viewHolder.fovariteImageView.setOnClickListener(myClickListener);
		
		return view;
	}

}
