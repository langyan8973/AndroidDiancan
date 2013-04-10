package com.diancan.custom.adapter;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.diancan.HistoryList.MyStandardArrayAdapter;
import com.diancan.R;
import com.diancan.Utils.MenuUtils;
import com.diancan.Utils.MyDateUtils;
import com.diancan.custom.view.PinnedHeaderListView;
import com.diancan.http.ImageDownloader;
import com.diancan.model.History;
import com.diancan.model.OrderItem;
import com.diancan.sectionlistview.MySectionIndexer;
import com.diancan.sectionlistview.SectionListItem;
import com.diancan.sectionlistview.SectionListAdapter.AdapterViewHolder;

import android.content.Context;
import android.database.DataSetObserver;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.SectionIndexer;
import android.widget.TextView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.AdapterView.OnItemClickListener;

public class HistoriesAdapter implements ListAdapter, OnItemClickListener, 
						PinnedHeaderAdapter, SectionIndexer, OnScrollListener {
	public  class HistoryItemViewHolder{
		public View head;
		public TextView headTextView;
		public TextView restaurantView;
		public TextView moneyTextView;
		public TextView numberTextView;
		public TextView timeTextView;
	}
	private SectionIndexer mIndexer;
	private String[] mSections;
	private int[] mCounts;
	private int mSectionCounts = 0; 
	private Date currentDate;
	private String markString;
	
	/**
	 * 按钮、图片点击监听；
	 */
	OnClickListener viewClickListener;
	
	public OnClickListener getViewClickListener() {
		return viewClickListener;
	}

	public void setViewClickListener(OnClickListener viewClickListener) {
		this.viewClickListener = viewClickListener;
	}
	
	private final DataSetObserver dataSetObserver = new DataSetObserver() {
        @Override
        public void onChanged() {
            super.onChanged();
            updateTotalCount();
        }

        @Override
        public void onInvalidated() {
            super.onInvalidated();
            updateTotalCount();
        };
    };
    
    private final MyStandardArrayAdapter linkedAdapter;
    
    private final Map<String, View> currentViewSections = new HashMap<String, View>();
    private int viewTypeCount;
    protected final LayoutInflater inflater;

    private View transparentSectionView;

    private OnItemClickListener linkedListener;
    
    public HistoriesAdapter(final LayoutInflater inflater,
            final MyStandardArrayAdapter linkedAdapter,String strmark) {
        this.linkedAdapter = linkedAdapter;
        this.inflater = inflater;
        this.currentDate = new Date(System.currentTimeMillis());
        markString = strmark;
        linkedAdapter.registerDataSetObserver(dataSetObserver);
        updateTotalCount();
    }

    private boolean isTheSame(final String previousSection,
            final String newSection) {
        if (previousSection == null) {
            return newSection == null;
        } else {
            return previousSection.equals(newSection);
        }
    }

    private void fillSections() {
    	mSections = new String[mSectionCounts];
    	mCounts = new int[mSectionCounts];
    	final int count = linkedAdapter.getCount();
    	String currentSection = null;
    	int newSectionIndex = 0;
    	int newSectionCounts = 0;
    	String previousSection = null;
    	for (int i = 0; i < count; i++) {
    		newSectionCounts++;
    		currentSection = linkedAdapter.items.get(i).section;
    		if (!isTheSame(previousSection, currentSection)) {
    			mSections[newSectionIndex] = currentSection;
    			previousSection = currentSection;
    			if (newSectionIndex == 1) {
    				mCounts[0] = newSectionCounts-1;
    			} else if(newSectionIndex != 0){
    				mCounts[newSectionIndex-1] = newSectionCounts;
    			}
    			if(i != 0) {
    				newSectionCounts = 0;
    			}
    			newSectionIndex++;
    		} else if(i == count-1){
            	mCounts[newSectionIndex-1] = newSectionCounts+1;
            }
    		
    	}
    	mIndexer = new MySectionIndexer(mSections, mCounts);
    }
    
    private synchronized void updateTotalCount() {
        String currentSection = null;
        mSectionCounts = 0;
        viewTypeCount = linkedAdapter.getViewTypeCount() + 1;
        final int count = linkedAdapter.getCount();
        for (int i = 0; i < count; i++) {
            final SectionListItem item = (SectionListItem) linkedAdapter.getItem(i);
            if (!isTheSame(currentSection, item.section)) {
            	mSectionCounts++;
            	currentSection = item.section;
            }
        }
        fillSections();
    }

    @Override
    public synchronized int getCount() {
        return linkedAdapter.getCount();
    }

    @Override
    public synchronized Object getItem(final int position) {
    	final int linkedItemPosition = getLinkedPosition(position);
        return linkedAdapter.getItem(linkedItemPosition);
    }

    public synchronized String getSectionName(final int position) {
        return null;
    }
    

    @Override
    public long getItemId(final int position) {
    	return linkedAdapter.getItemId(getLinkedPosition(position));
    }

    protected Integer getLinkedPosition(final int position) {
        return position;
    }

    @Override
    public int getItemViewType(final int position) {
        return linkedAdapter.getItemViewType(getLinkedPosition(position));
    }
    
    private View getSectionView(final View convertView, final String section) {
        View theView = convertView;
        if (theView == null) {
//            theView = createNewSectionView();
        }
        setSectionText(section, theView);
        replaceSectionViewsInMaps(section, theView);
        return theView;
    }

    protected void setSectionText(final String section, final View sectionView) {
        final TextView textView = (TextView) sectionView.findViewById(R.id.header);
        textView.setText(section);
    }

    protected synchronized void replaceSectionViewsInMaps(final String section,
    		final View theView) {
        if (currentViewSections.containsKey(theView)) {
            currentViewSections.remove(theView);
        }
        currentViewSections.put(section, theView );
    }

    
    
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		// TODO Auto-generated method stub
		View view = convertView;
		HistoryItemViewHolder viewHolder;
        final SectionListItem currentItem = linkedAdapter.items.get(position);
        if (view == null) {
        	viewHolder = new HistoryItemViewHolder();
            view = inflater.inflate(R.layout.his_section_list_item, null);
            TextView header = (TextView) view.findViewById(R.id.header);
            viewHolder.headTextView=header;
            viewHolder.head = view.findViewById(R.id.header_parent);
            viewHolder.restaurantView = (TextView)view.findViewById(R.id.his_r_name);
			viewHolder.moneyTextView = (TextView)view.findViewById(R.id.tv_money);
			viewHolder.timeTextView = (TextView)view.findViewById(R.id.tv_time);
			view.setTag(viewHolder);
        }
        else {
			viewHolder=(HistoryItemViewHolder)view.getTag();
		}
        
        History history = (History)currentItem.item;
        viewHolder.headTextView.setText(currentItem.section);
        viewHolder.restaurantView.setText(history.getRestaurant());
		viewHolder.moneyTextView.setText(markString+history.getMoney());
		Date timeDate = history.getTime();
		viewHolder.timeTextView.setText(MyDateUtils.getStringFormDate(currentDate, timeDate));
		
		int section = getSectionForPosition(position);
		int s2=getPositionForSection(section);
        if (s2 == position){
        	viewHolder.head.setVisibility(View.VISIBLE);
        	viewHolder.headTextView.setVisibility(View.VISIBLE);
    	} else {
    		viewHolder.head.setVisibility(View.GONE);
    		viewHolder.headTextView.setVisibility(View.GONE);
    	}
        
        return view;
		
	}

	@Override
    public int getViewTypeCount() {
        return viewTypeCount;
    }

    @Override
    public boolean hasStableIds() {
        return linkedAdapter.hasStableIds();
    }

    @Override
    public boolean isEmpty() {
        return linkedAdapter.isEmpty();
    }

    @Override
    public void registerDataSetObserver(final DataSetObserver observer) {
        linkedAdapter.registerDataSetObserver(observer);
    }

    @Override
    public void unregisterDataSetObserver(final DataSetObserver observer) {
        linkedAdapter.unregisterDataSetObserver(observer);
    }

    @Override
    public boolean areAllItemsEnabled() {
        return linkedAdapter.areAllItemsEnabled();
    }

    @Override
    public boolean isEnabled(final int position) {
        return linkedAdapter.isEnabled(getLinkedPosition(position));
    }


    public int getRealPosition(int pos) {
    	return pos-1;
    }
    
    public synchronized View getTransparentSectionView() {
        if (transparentSectionView == null) {
        }
        return transparentSectionView;
    }

    protected void sectionClicked(final String section) {
    }

    @Override
    public void onItemClick(final AdapterView< ? > parent, final View view,
            final int position, final long id) {
         if (linkedListener != null) {
            linkedListener.onItemClick(parent, view,getLinkedPosition(position), id);
         }
        
    }

    public void setOnItemClickListener(final OnItemClickListener linkedListener) {
        this.linkedListener = linkedListener;
    }
    
	@Override
	public int getPinnedHeaderState(int position) {
		int realPosition = position;
		if (mIndexer == null) {
			return PINNED_HEADER_GONE;
		}
		if (realPosition < 0) {
			return PINNED_HEADER_GONE;
		}
		int section = getSectionForPosition(realPosition);
		int nextSectionPosition = getPositionForSection(section + 1);
		if (nextSectionPosition != -1
				&& realPosition == nextSectionPosition - 1) {
			return PINNED_HEADER_PUSHED_UP;
		}
		return PINNED_HEADER_VISIBLE;
	}

	@Override
	public void configurePinnedHeader(View header, int position, int alpha) {
        int realPosition = position;
        int section = getSectionForPosition(realPosition);
        if(mSectionCounts==0){
        	return;
        }
        else if(mSectionCounts==1){
        	String title = (String)mIndexer.getSections()[0];
        	((TextView)header.findViewById(R.id.header_text)).setText(title);
        }
        else{
        	
            String title = (String)mIndexer.getSections()[section];
            
            ((TextView)header.findViewById(R.id.header_text)).setText(title);
        }
	}

	@Override
	public Object[] getSections() {
		if (mIndexer == null) {
			return new String[] { "" };
		} else {
			return mIndexer.getSections();
		}
	}

	@Override
	public int getPositionForSection(int section) {
		if (mIndexer == null) {
			return -1;
		}
		return mIndexer.getPositionForSection(section);
	}

	@Override
	public int getSectionForPosition(int position) {
		if (mIndexer == null) {
			return -1;
		}
		return mIndexer.getSectionForPosition(position);
	}

	@Override
	public void onScrollStateChanged(AbsListView view, int scrollState) {
    
	}

	@Override
	public void onScroll(AbsListView view, int firstVisibleItem,
			int visibleItemCount, int totalItemCount) {
            if(view instanceof PinnedHeaderListView) {
            	((PinnedHeaderListView)view).configureHeaderView(firstVisibleItem);
            }
		
	}

}
