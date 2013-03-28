package com.diancan.custom.adapter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import android.content.Context;
import android.util.SparseIntArray;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import com.diancan.R;
import com.diancan.model.city;

public class CityArrayAdapter<T> extends AllMatchArrayAdapter<T> {
	public class CityViewHolder{
  		public TextView titleTextView;
  	}

	public CityArrayAdapter(Context context, int textViewResourceId) {
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
    public CityArrayAdapter(Context context, int resource, int textViewResourceId) {
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
    public CityArrayAdapter(Context context, int textViewResourceId, T[] objects) {
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
    public CityArrayAdapter(Context context, int resource, int textViewResourceId, T[] objects) {
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
    public CityArrayAdapter(Context context, int textViewResourceId, List<T> objects) {
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
    public CityArrayAdapter(Context context, int resource, int textViewResourceId, List<T> objects) {
        super(context, resource, textViewResourceId, objects);
    }

	@Override
	public View createViewFromResource(int position, View convertView,
			ViewGroup parent, int resource) {
		// TODO Auto-generated method stub
		View view;
		CityViewHolder viewHolder;
		city c = (city)getItem(position);
        if (convertView == null) {
            view = mInflater.inflate(resource, parent, false);
            viewHolder = new CityViewHolder();
            TextView titleView=(TextView)view.findViewById(R.id.cityname);
		    viewHolder.titleTextView=titleView;
		    view.setTag(viewHolder);
            
        } else {
            view = convertView;
            viewHolder = (CityViewHolder)view.getTag();
        }
        viewHolder.titleTextView.setTag(c.getId());
        viewHolder.titleTextView.setText(c.getName());
        return view;
	}

    
}
