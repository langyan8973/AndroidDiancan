package com.diancan.custom.view;
import java.io.IOException;
import java.util.Calendar;
import java.util.List;  

import org.apache.http.client.ClientProtocolException;
import org.json.JSONException;
import org.json.JSONObject;

import com.diancan.Main;
import com.diancan.MenuBook;
import com.diancan.MenuGroup;
import com.diancan.R;
import com.diancan.RecipeList;
import com.diancan.Helper.OrderHelper;
import com.diancan.Helper.RecipeListHttpHelper;
import com.diancan.Utils.DisplayUtil;
import com.diancan.Utils.MenuUtils;
import com.diancan.custom.animation.GroupMoveAnimation;
import com.diancan.custom.animation.ListPopImgAnimation;
import com.diancan.diancanapp.AppDiancan;
import com.diancan.http.HttpCallback;
import com.diancan.http.HttpDownloader;
import com.diancan.http.ImageDownloader;
import com.diancan.model.OrderItem;

import android.R.integer;
import android.app.Activity;
import android.app.LocalActivityManager;
import android.content.Context;  
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.util.Log;  
import android.view.GestureDetector;  
import android.view.LayoutInflater;
import android.view.MotionEvent;  
import android.view.View;  
import android.view.ViewConfiguration;  
import android.view.ViewGroup;  
import android.view.ViewParent;
import android.view.Window;
import android.view.GestureDetector.OnGestureListener;  
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.AnimationUtils;
import android.view.animation.TranslateAnimation;
import android.widget.Button;  
import android.widget.ImageView;  
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.Scroller;  
import android.widget.TextView;
import android.widget.Toast;  
  
public class MyViewGroup extends ViewGroup implements OnGestureListener,HttpCallback{  
  
	RecipeListHttpHelper recipeListHttpHelper;
	OrderHelper orderHelper;
    private float mLastMotionY;// 最后点击的点  
    private GestureDetector detector;  
    int move = 0;// 移动距离  
    int MAXMOVE;// 最大允许的移动距离  
    private Scroller mScroller;  
    int up_excess_move = 0;// 往上多移的距离  
    int down_excess_move = 0;// 往下多移的距离  
    private final static int TOUCH_STATE_REST = 0;  
    private final static int TOUCH_STATE_SCROLLING = 1;  
    private final static int DURATION=200;
    private int mTouchSlop;  
    private int mTouchState = TOUCH_STATE_REST;  
    private int curview=-1;
    private int sWidth,sHeight;
    private int count,cIndex,rIndex;
    private long clicktime=0;  
    Context mContext; 
    List<OrderItem> selectedOrderItems;
    ImageDownloader mImageDownloader;
    private int startIndex,mLength;
    Button txtView;
    int sendId,sendCount;  
    boolean ispop=false;
    boolean isRight=false;
    boolean lrAction=false;
    int mLeft;
    public int itemTopHeight;
    public int itemBotmHeight;
    
  
    public MyViewGroup(Context context,int width,int height,int left,int cindex,List<OrderItem> orderItems,
    		ImageDownloader imgDownloader) {  
        super(context);  
        // TODO Auto-generated constructor stub  
        AppDiancan app=(AppDiancan)context.getApplicationContext();
        recipeListHttpHelper=new RecipeListHttpHelper(this, app);
        orderHelper=new OrderHelper(app);
        mContext = context; 
        mImageDownloader=imgDownloader;
        selectedOrderItems=orderItems;
        cIndex=cindex;
        sWidth=DisplayUtil.dip2px(width);
        sHeight=DisplayUtil.dip2px(height);
        mLeft=left;
        itemTopHeight=DisplayUtil.dip2px(76);
        itemBotmHeight=DisplayUtil.dip2px(66);
        mScroller = new Scroller(mContext); 
        detector = new GestureDetector(this);
        ViewConfiguration configuration = ViewConfiguration.get(mContext);  
        // 获得可以认为是滚动的距离  
        mTouchSlop = configuration.getScaledTouchSlop(); 
    	startIndex=0;
        mLength=selectedOrderItems.size();
        
        AddRecipes(startIndex, mLength);
        count=getChildCount();       
        MAXMOVE=(count*itemTopHeight)-sHeight;
    }  


	public int getcIndex() {
		return cIndex;
	}


	public void setcIndex(int cIndex) {
		this.cIndex = cIndex;
	}


	public List<OrderItem> getSelectedOrderItems() {
		return selectedOrderItems;
	}


	public void setSelectedOrderItems(List<OrderItem> selectedOrderItems) {
		this.selectedOrderItems = selectedOrderItems;
	}
	

	public int getmLeft() {
		return mLeft;
	}

	public void setmLeft(int mLeft) {
		this.mLeft = mLeft;
	}

	/***
     * 增加菜谱列表项
     * @param startIndex
     * @param length
     */
    public void AddRecipes(int startindex,int length)
    {
    	for (int i = startindex; i < startindex+length; i++) {  
        	LayoutInflater inflater = (LayoutInflater)getContext().getSystemService( Context.LAYOUT_INFLATER_SERVICE);
        	RecipeView view = (RecipeView)inflater.inflate(R.layout.menulistitem, null);
        	addView(view);
        	view.setmTopHeight(itemTopHeight);
        	view.setmBotHeight(itemBotmHeight);
        	view.setmWidth(sWidth);
        	TextView titleView=(TextView)view.findViewById(R.id.title);
        	titleView.setText(selectedOrderItems.get(i).getRecipe().getName());
        	TextView priceView=(TextView)view.findViewById(R.id.price);
        	priceView.setText("¥ "+selectedOrderItems.get(i).getRecipe().getPrice());
        	ImageView img=(ImageView)view.findViewById(R.id.img);
        	mImageDownloader.download(MenuUtils.imageUrl+selectedOrderItems.get(i).getRecipe().getImage(), img);
        	ImageView littleImg=(ImageView)view.findViewById(R.id.littleImg);
        	TextView countView=(TextView)view.findViewById(R.id.count);
        	ImageView duihaoView=(ImageView)view.findViewById(R.id.duihao);
        	int count=selectedOrderItems.get(i).getCount();
        	if(count<=0)
        	{
        		countView.setVisibility(View.INVISIBLE);
        		duihaoView.setVisibility(View.INVISIBLE);
        	}
        	else {
				countView.setVisibility(View.VISIBLE);
				countView.setText(""+count);
				duihaoView.setVisibility(View.VISIBLE);
			}

        	view.setOnClickListener(new ItemOnclick(i));
        	//加菜
        	ImageView jiaImg=(ImageView)view.findViewById(R.id.jiahao);
        	jiaImg.setOnClickListener(new JiaOnclick(img,littleImg, countView, duihaoView,selectedOrderItems.get(i)));
        	//减菜
        	ImageView jianImg=(ImageView)view.findViewById(R.id.jianhao);
        	jianImg.setOnClickListener(new jianOnclick(countView, duihaoView, selectedOrderItems.get(i)));
        	//点击图片跳转
        	img.setOnClickListener(new ImgOnclick(i));
        }  
    	
    }
    
    @Override  
    public void computeScroll() {  
        if (mScroller.computeScrollOffset()) {  
            // 返回当前滚动X方向的偏移  
            scrollTo(0, mScroller.getCurrY());  
            postInvalidate();  
        }  
    }  
  
    @Override 
    /***
     * 分发触摸事件
     */
    public boolean onInterceptTouchEvent(MotionEvent ev) {  
        final float y = ev.getY();  
        switch (ev.getAction())  
        {  
        case MotionEvent.ACTION_DOWN:  
  
            mLastMotionY = y;  
            mTouchState = mScroller.isFinished() ? TOUCH_STATE_REST  
                    : TOUCH_STATE_SCROLLING;  
            break;  
        case MotionEvent.ACTION_MOVE:  
            final int yDiff = (int) Math.abs(y - mLastMotionY);  
            boolean yMoved = yDiff > mTouchSlop;  
            // 判断是否是移动  
            if (yMoved) {  
                mTouchState = TOUCH_STATE_SCROLLING;  
            }  
            break;  
        case MotionEvent.ACTION_UP:  
            mTouchState = TOUCH_STATE_REST;  
            break;  
        }  
        return mTouchState != TOUCH_STATE_REST;  
    }  

	@Override  
	/***
	 * 触摸事件
	 */
    public boolean onTouchEvent(MotionEvent ev) {  
  
        // final int action = ev.getAction();
        final float y = ev.getY(); 
        final float x = ev.getX();
        switch (ev.getAction())  
        {  
        case MotionEvent.ACTION_DOWN:  
            if (!mScroller.isFinished()) {  
                mScroller.forceFinished(true);  
                move = mScroller.getFinalY();  
            }  
            mLastMotionY = y;  
            break;  
        case MotionEvent.ACTION_MOVE:  
            if (ev.getPointerCount() == 1) {  
                  
                // 随手指 拖动的代码  
                int deltaY = 0;  
                deltaY = (int) (mLastMotionY - y);  
                
                mLastMotionY = y;  
                if (deltaY < 0) {  
                    // 下移  
                    // 判断上移 是否滑过头  
                    if (up_excess_move == 0) {  
                        if (move > 0) {  
                            int move_this = Math.max(-move, deltaY);  
                            move = move + move_this;  
                            scrollBy(0, move_this);  
                        } else if (move == 0) {// 如果已经是最顶端 继续往下拉  
                            down_excess_move = down_excess_move - deltaY / 2;// 记录下多往下拉的值  
                            scrollBy(0, deltaY / 2);  
                        }  
                    } else if (up_excess_move > 0)// 之前有上移过头  
                    {                     
                        if (up_excess_move >= (-deltaY)) {  
                            up_excess_move = up_excess_move + deltaY;  
                            scrollBy(0, deltaY);  
                        } else {                          
                            up_excess_move = 0;  
                            scrollBy(0, -up_excess_move);                 
                        }  
                    }  
                } else if (deltaY > 0) {  
                    // 上移  
                    if (down_excess_move == 0) {  
                        if (MAXMOVE - move > 0) {  
                            int move_this = Math.min(MAXMOVE - move, deltaY);  
                            move = move + move_this;  
                            scrollBy(0, move_this);  
                        } else if (MAXMOVE - move == 0) {  
                            if (up_excess_move <= 100) {  
                                up_excess_move = up_excess_move + deltaY / 2;  
                                scrollBy(0, deltaY / 2);  
                            }  
                        }  
                    } else if (down_excess_move > 0) {  
                        if (down_excess_move >= deltaY) {  
                            down_excess_move = down_excess_move - deltaY;  
                            scrollBy(0, deltaY);  
                        } else {  
                            down_excess_move = 0;  
                            scrollBy(0, down_excess_move);  
                        }  
                    }  
                }         
            }   
            break;  
        case MotionEvent.ACTION_UP:           
            // 多滚是负数 记录到move里  
            if (up_excess_move > 0) {  
                // 多滚了 要弹回去  
                scrollBy(0, -up_excess_move);  
                invalidate();  
                up_excess_move = 0;  
            }  
            if (down_excess_move > 0) {  
                // 多滚了 要弹回去  
                scrollBy(0, down_excess_move);  
                invalidate();  
                down_excess_move = 0;  
            }  
            mTouchState = TOUCH_STATE_REST;  
            break;  
        }  
        return this.detector.onTouchEvent(ev);  
    }  
  
    int Fling_move = 0;  
  
    public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX,  
            float velocityY) {  
         //随手指 快速拨动的代码  
        Log.d("onFling", "onFling");  
        boolean isLR=false;
        if(Math.abs(velocityX)>Math.abs(velocityY)){
        	if(velocityX>=0){
            	StartToRight();
            	postInvalidate();
            }else{
            	StartToLeft();
            	postInvalidate();
            }
        	isLR=true;
        }
        else{
        	if (up_excess_move == 0 && down_excess_move == 0) {  
        		  
                int slow = -(int) velocityY ; //*3/4;
                mScroller.fling(0, move, 0, slow, 0, 0, 0, MAXMOVE);  
                move = mScroller.getFinalY();  
                computeScroll();  
            }
        	isLR=false;
        }
          
        return isLR;  
    }  
  
    public boolean onDown(MotionEvent e) {  
        // TODO Auto-generated method stub  
        return true;  
    }  
  
    public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX,  
            float distanceY) {  
        return false;  
    }  
  
    public void onShowPress(MotionEvent e) {  
        // // TODO Auto-generated method stub  
    }  
  
    public boolean onSingleTapUp(MotionEvent e) {  
        // TODO Auto-generated method stub  
        return false;  
    }  
  
    public void onLongPress(MotionEvent e) {  
        // TODO Auto-generated method stub  
    }  
  
    @Override  
    protected void onLayout(boolean changed, int l, int t, int r, int b) {  
        // TODO Auto-generated method stub 
    	if(ispop)
    		return;
    	System.out.println("onlayout===============");
        int childTop = 0;  
        int childLeft = 0;  
        
        final int count = getChildCount();  
        for (int i = 0; i < count; i++) {  
            final View child = getChildAt(i);  
            if (child.getVisibility() != View.GONE) {  
                child.setVisibility(View.VISIBLE);  
                child.measure(r - l, b - t);
                child.layout(childLeft, childTop, childLeft + sWidth,  
                        childTop + itemTopHeight);
                childTop+=itemTopHeight;
            }  
        } 
        System.out.println("onlayout====end===========");
    }  
    
    @Override
	public void RequestComplete(Message msg) {
		// TODO Auto-generated method stub
		
	}


	@Override
	public void RequestError(String errString) {
		// TODO Auto-generated method stub
		Toast toast = Toast.makeText(mContext, errString, Toast.LENGTH_SHORT); 
        toast.show();
	}
    
    public void StartToRight(){
    	this.clearAnimation();
    	Animation animation=new GroupMoveAnimation(200, 0, 200, this, sWidth, sHeight);
        this.startAnimation(animation);
    }
    
    public void StartToLeft(){
    	this.clearAnimation();
    	Animation animation=new GroupMoveAnimation(200, 200, 0, this, sWidth, sHeight);
        this.startAnimation(animation);
    }
   
    public void StartAnimation(int clicknum,int times)
    {
    	//如果没有展开项
    	if(curview==-1)
    	{
    		int count = getChildCount();
    		//被点击的view下面所有view向下移动
        	for(int i=clicknum;i<count;i++)
        	{
        		final RecipeView view=(RecipeView)getChildAt(i);
        		view.setmTop(view.getTop());
        		if(i==clicknum){
        			view.StartExpandAnimation();
        		}
        		else{
        			view.StartDownAnimation();
        		}
        	}
        	curview=clicknum;
        	MAXMOVE=(count*itemTopHeight)-sHeight+itemBotmHeight;
    	}
    	//如果点击的是已经展开的则关闭
    	else if(clicknum==curview)
    	{
    		//被关闭项下面所有项向上移动
        	for(int i=curview;i<count;i++)
        	{
        		final RecipeView view=(RecipeView)getChildAt(i);
        		view.setmTop(view.getTop());
        		if(i==curview){
        			view.StartContractAnimation();
        		}
        		else{
        			view.StartUpAnimation();
        		}
        	}
        	curview=-1;
        	//如果滚动条在最底部，需要缩小空间并依然使滚动条在最底部
        	if(move==MAXMOVE)
        	{
        		mScroller.startScroll(0, mScroller.getFinalY(), 0, -itemBotmHeight, DURATION);
    			MAXMOVE=count*itemTopHeight-sHeight;
    			move=MAXMOVE;
        	}
        	//否则只缩小空间
        	else {
        		MAXMOVE=(count*itemTopHeight)-sHeight;
        	}
    	}
    	//如果点击的项在展开项的前面，让点击项的下一项至展开项向下移动
    	else if(clicknum<curview)
    	{
        	for(int i=clicknum;i<=curview;i++)
        	{
        		final RecipeView view=(RecipeView)getChildAt(i);
        		view.setmTop(view.getTop());
        		if(i==clicknum){
        			view.StartExpandAnimation();
        		}
        		else if(i==curview){
        			view.StartDownAndContractAnimation();
        		}
        		else{
        			view.StartDownAnimation();
        		}
        	}
        	curview=clicknum;
        	MAXMOVE=(count*itemTopHeight)-sHeight+itemBotmHeight;
    	}
    	//如果点击的项在展开项的后面，让点击项至展开项的下一项向上移动
    	else if(clicknum>curview)
    	{
        	for(int i=curview;i<=clicknum;i++)
        	{
        		final RecipeView view=(RecipeView)getChildAt(i);
        		view.setmTop(view.getTop());
        		if(i==curview){
        			view.StartContractAnimation();
        		}
        		else if(i==clicknum){
        			view.StartUpAndExpandAnimation();
        		}
        		else{
        			view.StartUpAnimation();
        		}
        	}
        	curview=clicknum;
        	MAXMOVE=(count*itemTopHeight)-sHeight+itemBotmHeight;
    	}

    }
    public void PostToServer()
    {
    	recipeListHttpHelper.Diancai(sendId, sendCount);
    }
   //加入订单
  	public void SendSetCountMessage()
    {
    	Intent in = new Intent();
        in.setAction("setcount");
        in.addCategory(Intent.CATEGORY_DEFAULT);
        mContext.sendBroadcast(in);      
    }
  	private int getWifiRssi()
  	{
  		WifiManager mWifiManager=(WifiManager) mContext.getSystemService(mContext.WIFI_SERVICE);
  	    WifiInfo mWifiInfo=mWifiManager.getConnectionInfo();
  	    int wifi=mWifiInfo.getRssi();//获取wifi信号强度
  	    return wifi;
  	}
  	//点击item展开或者关闭
  	class ItemOnclick implements OnClickListener{

  		public int index;
  		public ItemOnclick(int i)
  		{
  			index=i;
  		}
		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			ispop=true;
			Calendar cld = Calendar.getInstance();
			if(clicktime!=0&&(cld.getTimeInMillis()-clicktime<500))
			{
				return;
			}
			clicktime=cld.getTimeInMillis();
			int[] location = new int[2];
    		v.getLocationInWindow(location);
    		int y = location[1]-DisplayUtil.dip2px(25);
    		//如果点击的view在屏幕的底部展开后不能完全显示，需要向上移动
    		int movedis=itemTopHeight+itemBotmHeight;
    		if((sHeight-y)<movedis)
    		{
    			int count = getChildCount();
    			//如果当前展开的是最后一项则关闭最后一项操作（）
        		if(curview==count-1&&MAXMOVE-move<movedis)
        		{
        			mScroller.startScroll(0, getScrollY(), 0, -itemBotmHeight, DURATION);
        			postInvalidate();
        			MAXMOVE=count*itemTopHeight-sHeight;
        			move=MAXMOVE;
        			curview=-1;
        		}
        		else {
        			//如果还有足够的滚动空间
        			if(MAXMOVE-move>=movedis-(sHeight-y))
	    			{   	
	    				mScroller.startScroll(0, getScrollY(), 0, movedis-(sHeight-y), DURATION);
	    				postInvalidate();
	    				move+=movedis-(sHeight-y);
	    				
	    				StartAnimation(index,DURATION);
	    			}
        			//如果没有足够的滚动空间则剩多少滚动多少
	    			else if(MAXMOVE-move<movedis&&MAXMOVE-move>0){
	    				if(MAXMOVE==count*itemTopHeight-sHeight)
	    				{
	    					MAXMOVE+=itemBotmHeight;
	    				}
	    				mScroller.startScroll(0, getScrollY(), 0, MAXMOVE-move, DURATION);
	    				postInvalidate();
	    				move=MAXMOVE;
	    				StartAnimation(index,DURATION);
					}
        			//如果滚动条已经到达底部并且目前没有展开的项则增加一个展开距离
	    			else if(MAXMOVE-move==0&&MAXMOVE==count*itemTopHeight-sHeight)
					{
						MAXMOVE+=itemBotmHeight;
						mScroller.startScroll(0, getScrollY(), 0, MAXMOVE-move, DURATION);
						postInvalidate();
	    				move=MAXMOVE;
	    				StartAnimation(index,DURATION);
					}
	    			else {
	    				postInvalidate();
	    				StartAnimation(index,0);
					}
				}
    			
    		}
    		//如果点击的view在屏幕顶部且没有完全显示需要向下移动
    		else if(y<0)
    		{
    			//如果还有大于y的滚动空间则滚动y
    			if(move>=-y)
    			{
    				mScroller.startScroll(0, getScrollY(), 0, y, DURATION);
    				postInvalidate();
    				move+=y;
    			}
    			//如果不够y则有多少滚动多少
    			else {
    				mScroller.startScroll(0, getScrollY(), 0, -move, DURATION);
    				postInvalidate();
    				move=0;
				}
    			StartAnimation(index,DURATION);
    		}
    		//如果点击的view不在底部也不在顶部
    		else {
    			postInvalidate();
    			StartAnimation(index,0);
			}
		}
  		
  	}
  	//点击加号加菜
  	class JiaOnclick implements OnClickListener{

  		ImageView img;
  		ImageView bImg;
  		TextView couTextView;
  		ImageView duihaoImg;
  		OrderItem mOrderItem;
  		
  		public JiaOnclick(ImageView bImageView,ImageView lImageView,TextView countTextView,ImageView duihao,OrderItem orderItem)
  		{
  			bImg=bImageView;
  			img=lImageView;
  			couTextView=countTextView;
  			duihaoImg=duihao;
  			mOrderItem=orderItem;
  		}
		@Override
		public void onClick(final View v) {
			// TODO Auto-generated method stub
			ispop=true;
			Calendar cld = Calendar.getInstance();
			if((cld.getTimeInMillis()-clicktime)<400)
			{
				return;
			}
			clicktime=cld.getTimeInMillis();
			AppDiancan declare=(AppDiancan)mContext.getApplicationContext();
			if(declare.curOrder==null)
			{
				Toast toast = Toast.makeText(mContext, "请选择餐桌", Toast.LENGTH_SHORT); 
	            toast.show();
	            return;
			}
			bImg.setDrawingCacheEnabled(true);
			Bitmap bmp=Bitmap.createBitmap(bImg.getDrawingCache());
			img.setImageBitmap(bmp);
			bImg.setDrawingCacheEnabled(false);
			img.setVisibility(View.VISIBLE);
			img.setAlpha(255);
			int x1=0;
			int y1=0;
			int x2=DisplayUtil.dip2px(183);
			int y2=-DisplayUtil.dip2px(33);
			int x3=DisplayUtil.dip2px(223);
			int y3=DisplayUtil.dip2px(7);
			
			ListPopImgAnimation tAnimation=new ListPopImgAnimation(400, x1,y1,x2,y2,x3,y3);
			tAnimation.setAnimationListener(new AnimationListener() {
				@Override
				public void onAnimationStart(Animation animation) {}
				@Override
				public void onAnimationRepeat(Animation animation) {}
				
				@Override
				public void onAnimationEnd(Animation animation) {
					// TODO Auto-generated method stub
					img.clearAnimation();
					img.setImageBitmap(null);
					//加减菜请求
					sendId=mOrderItem.getRecipe().getId();
					sendCount=1;
					
					
					mOrderItem.setCount(mOrderItem.getCount()+1);
					couTextView.setText(mOrderItem.getCount()+"");
					if(couTextView.getVisibility()==View.INVISIBLE)
					{
						couTextView.setVisibility(View.VISIBLE);
					}
					
					if(duihaoImg.getVisibility()==View.INVISIBLE)
					{
						duihaoImg.setVisibility(View.VISIBLE);
					}
					orderHelper.AddToOrderForm(mOrderItem);
					SendSetCountMessage();
					
					PostToServer();
				}
				
			});
			img.startAnimation(tAnimation);

		}
  		
  	}
  	//点击减号减菜
  	class jianOnclick implements OnClickListener{
  		TextView couTextView;
  		ImageView duihaoImg;
  		OrderItem mOrderItem;
  		public jianOnclick(TextView countTextView,ImageView duihao,OrderItem orderItem)
  		{
  			couTextView=countTextView;
  			duihaoImg=duihao;
  			mOrderItem=orderItem;
  		}
		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			Calendar cld = Calendar.getInstance();
			if((cld.getTimeInMillis()-clicktime)<400)
			{
				return;
			}
			clicktime=cld.getTimeInMillis();
			int rCount=mOrderItem.getCount();
			if(rCount==0)
			{
				return;
			}
			//加减菜请求
			sendId=mOrderItem.getRecipe().getId();
			sendCount=-1;

			rCount-=1;
			couTextView.setText(rCount+"");
			if(rCount==0)
			{
				couTextView.setVisibility(View.INVISIBLE);
				duihaoImg.setVisibility(View.INVISIBLE);
			}				
			if(rCount<0)
			{
				rCount=0;
				orderHelper.deleteFromOrderForm(mOrderItem);
				SendSetCountMessage();
			}
			else {						
				mOrderItem.setCount(rCount);
				orderHelper.Subtraction(mOrderItem);
				SendSetCountMessage();
			}
			PostToServer();
		}
  		
  	}
  	//点击图片页面跳转
  	class ImgOnclick  implements OnClickListener{
  		int index;
  		public ImgOnclick(int i){
  			index=i;
  		}
		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			int wifi=getWifiRssi();
			if(wifi<=-70)
			{
				Toast toast = Toast.makeText(mContext, "当前网络信号强度太差，不能浏览大图。", Toast.LENGTH_SHORT); 
		    	toast.show();
		    	return;
			}
			Calendar cld = Calendar.getInstance();
			if((cld.getTimeInMillis()-clicktime)<400)
			{
				return;
			}
			clicktime=cld.getTimeInMillis();
			rIndex=index;
			
			Activity recipeActivity=(Activity)mContext;
			MenuGroup parent = (MenuGroup)recipeActivity.getParent();
		    final LinearLayout contain = (LinearLayout) parent.findViewById(R.id.group_Layout);
			contain.removeAllViews();
			Intent in = new Intent(recipeActivity.getParent(), MenuBook.class);
			in.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
			in.putExtra("rindex", rIndex+"");
			in.putExtra("index", cIndex+"");
			LocalActivityManager manager = parent.getLocalActivityManager();
			String currentId = manager.getCurrentId();
			Window window = manager.startActivity("MenuBook", in);
			
			View view=window.getDecorView();		
			contain.addView(view);
			LayoutParams params=(LayoutParams) view.getLayoutParams();
	        params.width=LayoutParams.FILL_PARENT;
	        params.height=LayoutParams.FILL_PARENT;
	        view.setLayoutParams(params);
	        
	        Animation sAnimation=AnimationUtils.loadAnimation(mContext, R.anim.open_in);
	        view.startAnimation(sAnimation);
		}
  		
  	}
}
