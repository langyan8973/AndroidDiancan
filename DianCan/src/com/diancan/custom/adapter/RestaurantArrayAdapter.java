package com.diancan.custom.adapter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.diancan.R;
import com.diancan.Utils.MenuUtils;
import com.diancan.http.ImageDownloader;
import com.diancan.model.Restaurant;
import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

public class RestaurantArrayAdapter<T> extends AllMatchArrayAdapter<T> {

	public class ViewHolder{
  		public ImageView imageView;
  		public TextView titleTextView;
  		public TextView addressTextView;
  		public TextView phoneTextView;
  	}
	
	private ImageDownloader imageDownloader;
	
	public ImageDownloader getImageDownloader() {
		return imageDownloader;
	}

	public void setImageDownloader(ImageDownloader imageDownloader) {
		this.imageDownloader = imageDownloader;
	}

	public RestaurantArrayAdapter(Context context, int textViewResourceId) {
		super(context, textViewResourceId);
		// TODO Auto-generated constructor stub
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
    }

    /**
     * Constructor
     *
     * @param context The current context.
     * @param textViewResourceId The resource ID for a layout file containing a TextView to use when
     *                 instantiating views.
     * @param objects The objects to represent in the ListView.
     */
    public RestaurantArrayAdapter(Context context, int textViewResourceId, List<T> objects) {
        super(context, textViewResourceId, 0, objects);
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
    }

	@Override
	public View createViewFromResource(int position, View convertView,
			ViewGroup parent, int resource) {
		// TODO Auto-generated method stub
		View view;
		ViewHolder viewHolder;
		Restaurant restaurant = (Restaurant)getItem(position);
        if (convertView == null) {
            view = mInflater.inflate(resource, parent, false);
            viewHolder = new ViewHolder();
            TextView titleView=(TextView)view.findViewById(R.id.tv_name);
		    TextView addressView=(TextView)view.findViewById(R.id.tv_address);
		    TextView phoneView=(TextView)view.findViewById(R.id.tv_telephone);
		    ImageView recipeImg=(ImageView)view.findViewById(R.id.restaurant_image);
		    viewHolder.titleTextView=titleView;
		    viewHolder.addressTextView=addressView;
		    viewHolder.phoneTextView=phoneView;
		    viewHolder.imageView=recipeImg;
		    view.setTag(viewHolder);
            
        } else {
            view = convertView;
            viewHolder = (ViewHolder)view.getTag();
        }
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


        return view;
	}

    
}
