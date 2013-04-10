package com.diancan.custom.adapter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.diancan.R;
import com.diancan.Utils.MenuUtils;
import com.diancan.http.ImageDownloader;
import com.diancan.model.Restaurant;
import com.diancan.model.favorite;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.util.SparseIntArray;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

public class RestaurantArrayAdapter<T> extends AllMatchArrayAdapter<T> {

	public class ViewHolder{
  		public ImageView imageView;
  		public TextView titleTextView;
  		public TextView addressTextView;
  		public TextView phoneTextView;
  		public ImageView fovariteImageView;
  	}
	
	private ImageDownloader imageDownloader;
	private OnClickListener myClickListener;
	private Context mContext;
	private SparseIntArray fSparseIntArray;
	
	public ImageDownloader getImageDownloader() {
		return imageDownloader;
	}

	public void setImageDownloader(ImageDownloader imageDownloader) {
		this.imageDownloader = imageDownloader;
	}

	public OnClickListener getMyClickListener() {
		return myClickListener;
	}

	public void setMyClickListener(OnClickListener myClickListener) {
		this.myClickListener = myClickListener;
	}

	public SparseIntArray getfSparseIntArray() {
		return fSparseIntArray;
	}

	public void setfSparseIntArray(SparseIntArray fSparseIntArray) {
		this.fSparseIntArray = fSparseIntArray;
	}

	public RestaurantArrayAdapter(Context context, int textViewResourceId) {
		super(context, textViewResourceId);
		// TODO Auto-generated constructor stub
		mContext = context;
	}

	/**
     * Constructor
     *
     * @param context The current context.
     * @param resource The resource ID for a layout file containing a layout to use when
     *                 instantiating views.
     * @param textViewResourceId The id of the TextView within the layout resource to be populated
     */
    public RestaurantArrayAdapter(Context context, int resource, int textViewResourceId) {
        super(context, resource, textViewResourceId, new ArrayList<T>());
        mContext = context;
    }

    /**
     * Constructor
     *
     * @param context The current context.
     * @param textViewResourceId The resource ID for a layout file containing a TextView to use when
     *                 instantiating views.
     * @param objects The objects to represent in the ListView.
     */
    public RestaurantArrayAdapter(Context context, int textViewResourceId, T[] objects) {
        super(context, textViewResourceId, 0, Arrays.asList(objects));
        mContext = context;
    }

    /**
     * Constructor
     *
     * @param context The current context.
     * @param resource The resource ID for a layout file containing a layout to use when
     *                 instantiating views.
     * @param textViewResourceId The id of the TextView within the layout resource to be populated
     * @param objects The objects to represent in the ListView.
     */
    public RestaurantArrayAdapter(Context context, int resource, int textViewResourceId, T[] objects) {
        super(context, resource, textViewResourceId, Arrays.asList(objects));
        mContext = context;
    }

    /**
     * Constructor
     *
     * @param context The current context.
     * @param textViewResourceId The resource ID for a layout file containing a TextView to use when
     *                 instantiating views.
     * @param objects The objects to represent in the ListView.
     */
    public RestaurantArrayAdapter(Context context, int textViewResourceId, List<T> objects,SparseIntArray sparseIntArray) {
        super(context, textViewResourceId, 0, objects);
        mContext = context;
        fSparseIntArray = sparseIntArray;
    }

    /**
     * Constructor
     *
     * @param context The current context.
     * @param resource The resource ID for a layout file containing a layout to use when
     *                 instantiating views.
     * @param textViewResourceId The id of the TextView within the layout resource to be populated
     * @param objects The objects to represent in the ListView.
     */
    public RestaurantArrayAdapter(Context context, int resource, int textViewResourceId, List<T> objects) {
        super(context, resource, textViewResourceId, objects);
        mContext = context;
    }

	@Override
	public View createViewFromResource(int position, View convertView,
			ViewGroup parent, int resource) {
		// TODO Auto-generated method stub
		View view;
		ViewHolder viewHolder;
        if (convertView == null) {
            view = mInflater.inflate(resource, parent, false);
            viewHolder = new ViewHolder();
            TextView titleView=(TextView)view.findViewById(R.id.tv_name);
		    TextView addressView=(TextView)view.findViewById(R.id.tv_address);
		    TextView phoneView=(TextView)view.findViewById(R.id.tv_telephone);
		    ImageView recipeImg=(ImageView)view.findViewById(R.id.restaurant_image);
		    ImageView favoriteImageView = (ImageView)view.findViewById(R.id.favoriteBtn);
		    viewHolder.titleTextView=titleView;
		    viewHolder.addressTextView=addressView;
		    viewHolder.phoneTextView=phoneView;
		    viewHolder.imageView=recipeImg;
		    viewHolder.fovariteImageView = favoriteImageView;
		    view.setTag(viewHolder);
            
        } else {
            view = convertView;
            viewHolder = (ViewHolder)view.getTag();
        }
        Object o = getItem(position);
        if(o instanceof Restaurant){
        	Restaurant restaurant = (Restaurant)o;
        	viewHolder.titleTextView.setTag(restaurant.getId());
            viewHolder.titleTextView.setText(restaurant.getName());
    	    viewHolder.addressTextView.setText(restaurant.getAddress());
    	    viewHolder.phoneTextView.setText(restaurant.getTelephone());
    	    String strUrl;
    		if(restaurant.getImage()==null){
    			strUrl=null;
    		}
    		else{
    			strUrl=MenuUtils.imageUrl+MenuUtils.IMAGE_SMALL+restaurant.getImage();
    		}
    	    imageDownloader.download(strUrl, viewHolder.imageView);
    	    Drawable drawable;
    	    if(fSparseIntArray!=null && fSparseIntArray.get(restaurant.getId())!=0){
    	    	drawable = mContext.getResources().getDrawable(R.drawable.favorite_btn_select);
    	    }
    	    else{
    	    	drawable = mContext.getResources().getDrawable(R.drawable.favorite_btn);
    	    }
    	    viewHolder.fovariteImageView.setImageDrawable(drawable);
    	    viewHolder.fovariteImageView.setTag(restaurant.getId());
    	    viewHolder.fovariteImageView.setOnClickListener(myClickListener);
        }
        else if(o instanceof favorite){
        	favorite favorite = (favorite)o;
        	viewHolder.titleTextView.setTag(favorite.getRid());
            viewHolder.titleTextView.setText(favorite.getRestaurantName());
    	    String strUrl;
    		if(favorite.getRestaurantImage()==null){
    			strUrl=null;
    		}
    		else{
    			strUrl=MenuUtils.imageUrl+MenuUtils.IMAGE_SMALL+favorite.getRestaurantImage();
    		}
    	    imageDownloader.download(strUrl, viewHolder.imageView);
    	    Drawable drawable;
    	    drawable = mContext.getResources().getDrawable(R.drawable.favorite_btn_select);
    	    viewHolder.fovariteImageView.setImageDrawable(drawable);
    	    viewHolder.fovariteImageView.setTag(favorite.getRid());
    	    viewHolder.fovariteImageView.setOnClickListener(myClickListener);
        }
        
        return view;
	}

    
}
