package com.diancan.sectionlistview;

import java.util.HashMap;
import java.util.Map;

import android.R.integer;
import android.content.res.ColorStateList;
import android.database.DataSetObserver;
import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.SectionIndexer;
import android.widget.TextView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.AdapterView.OnItemClickListener;

import com.diancan.MyTable.MyStandardArrayAdapter;
import com.diancan.R;
import com.diancan.Utils.MenuUtils;
import com.diancan.custom.adapter.PinnedHeaderAdapter;
import com.diancan.custom.view.PinnedHeaderListView;
import com.diancan.http.ImageDownloader;
import com.diancan.model.OrderItem;
import com.diancan.sectionlistview.SectionListAdapter.AdapterViewHolder;

public class OrderSectionListAdapter implements ListAdapter, OnItemClickListener, 
							PinnedHeaderAdapter, SectionIndexer, OnScrollListener{
	

	
	private SectionIndexer mIndexer;
	private String[] mSections;
	private int[] mCounts;
	private int mSectionCounts = 0; 
	
	/**
	 * 异步加载图片对象
	 */
	ImageDownloader imageDownloader;
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
    private String[] messageStrings;
    
    public OrderSectionListAdapter(final LayoutInflater inflater,
            final MyStandardArrayAdapter linkedAdapter,ImageDownloader imgDownloader,String[] strmessages) {
        this.linkedAdapter = linkedAdapter;
        this.inflater = inflater;
        messageStrings = strmessages;
        linkedAdapter.registerDataSetObserver(dataSetObserver);
        updateTotalCount();
        imageDownloader = imgDownloader;
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
//        Log.d("OrderSectionListAdapter", "count********************"+count);
        for (int i = 0; i < count; i++) {
            final SectionListItem item = (SectionListItem) linkedAdapter.getItem(i);
            if (!isTheSame(currentSection, item.section)) {
            	mSectionCounts++;
            	currentSection = item.section;
            }
        }
//        Log.d("OrderSectionListAdapter", "mSectionCounts********************"+mSectionCounts);
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
    public View getView(final int position, final View convertView,
            final ViewGroup parent) {
        View view = convertView;
        AdapterViewHolder viewHolder;
        final SectionListItem currentItem = linkedAdapter.items.get(position);
        if (view == null) {
        	viewHolder = new AdapterViewHolder();
            view = inflater.inflate(R.layout.order_section_list_item, null);
            TextView header = (TextView) view.findViewById(R.id.header);
            viewHolder.header=header;
            ImageView imgrecipe=(ImageView)view.findViewById(R.id.img);
            viewHolder.imgrecipe=imgrecipe;
            TextView tvTitle=(TextView)view.findViewById(R.id.title);
            viewHolder.tvTitle=tvTitle;
            TextView tvPrice=(TextView)view.findViewById(R.id.price);
            viewHolder.tvPrice=tvPrice;
            TextView tvCount=(TextView)view.findViewById(R.id.count);
            viewHolder.tvCount=tvCount;
            TextView tvCountDes = (TextView)view.findViewById(R.id.txtDescription);
            viewHolder.tvCountDeposit = tvCountDes;
            TextView tvCountConfirm = (TextView)view.findViewById(R.id.txtConfirm);
            viewHolder.tvCountConfirm = tvCountConfirm;
            ImageView imgdelete=(ImageView)view.findViewById(R.id.jianhao);
            viewHolder.imgdelete=imgdelete;
            ImageView imgadd=(ImageView)view.findViewById(R.id.jiahao);
            viewHolder.imgadd=imgadd;
            View headerView=view.findViewById(R.id.header_parent);
			viewHolder.headerView = headerView;
			view.setTag(viewHolder);
        }
        else {
			viewHolder=(AdapterViewHolder)view.getTag();
		}
        
        if (currentItem != null) {//to set every item's text
        	viewHolder.header.setText(currentItem.section);
			OrderItem orderItem=(OrderItem)currentItem.item;
			String strUrl;
			if(orderItem.getRecipe().getImage()==null){
				strUrl=null;
			}
			else{
				strUrl=MenuUtils.imageUrl+MenuUtils.IMAGE_SMALL+orderItem.getRecipe().getImage();
			}
	        imageDownloader.download(strUrl, viewHolder.imgrecipe);
	        viewHolder.tvCount.setText(orderItem.GetCount()+ "");
	        viewHolder.tvCountConfirm.setTextColor(Color.DKGRAY);
			if(orderItem.getCountConfirm()>0){
				viewHolder.tvCountConfirm.setText(orderItem.getCountConfirm()+messageStrings[0]);
			}
			else{
				viewHolder.tvCountConfirm.setText("");
			}
	        viewHolder.tvTitle.setText(orderItem.getRecipe().getName());
	        viewHolder.tvPrice.setText(messageStrings[1]+orderItem.getRecipe().getPrice().toString());
	        viewHolder.imgdelete.setTag(position);
	        viewHolder.imgadd.setTag(position);
	        viewHolder.imgadd.setOnClickListener(viewClickListener);
	        viewHolder.imgdelete.setOnClickListener(viewClickListener);
	        
	        if(orderItem.getCountNew()<=0){
	        	viewHolder.imgdelete.setVisibility(View.INVISIBLE);
	        	viewHolder.tvCountDeposit.setTextColor(Color.DKGRAY);
	        	viewHolder.tvCountDeposit.setText(messageStrings[2]);
	        }
	        else{
	        	viewHolder.imgdelete.setVisibility(View.VISIBLE);
	        	viewHolder.tvCountDeposit.setTextColor(Color.RED);
	        	viewHolder.tvCountDeposit.setText(orderItem.getCountNew()+messageStrings[3]);
	        }
	        
			int section = getSectionForPosition(position);
			int s2=getPositionForSection(section);
            if (s2 == position){
            	viewHolder.headerView.setVisibility(View.VISIBLE);
            	viewHolder.header.setVisibility(View.VISIBLE);
        	} else {
        		viewHolder.headerView.setVisibility(View.GONE);
        		viewHolder.header.setVisibility(View.GONE);
        	}
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
