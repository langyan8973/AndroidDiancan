package com.diancan;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.androidpn.client.Constants;
import org.androidpn.client.Notifier;
import org.apache.http.client.ClientProtocolException;
import org.json.JSONException;
import org.json.JSONObject;

import android.R.bool;
import android.R.integer;
import android.app.Activity;
import android.app.ListActivity;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.SqlLiteDB.MenuDataHelper;
import com.Utils.CustomViewBinder;
import com.Utils.FileUtils;
import com.Utils.MenuUtils;
import com.declare.Declare;
import com.diancan.RecipeList.RecipeListAdapter;
import com.download.HttpDownloader;
import com.mode.ServiceMess;
import com.model.AllDomain;
import com.model.Category;
import com.model.Desk;
import com.model.OrderItem;
import com.model.Recipe;

public class MyService extends Activity {
	ListView serviceList;
	ListView messListView;
	ArrayList<ServiceMess> messages=new ArrayList<ServiceMess>();
	ArrayList<HashMap<String, String>> hashlist;
	ArrayList<HashMap<String, Object>> messHashMaps=new ArrayList<HashMap<String,Object>>();
	boolean isNew=false;
	Declare declare;
	MessageListAdapter messageListAdapter;
	NotifiReceiver receiver;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.myservice);
		declare=(Declare)getApplicationContext();
		serviceList=(ListView)findViewById(R.id.ServiceList);
		serviceList.setOnItemClickListener(new listitemClick());
		
		messListView=(ListView)findViewById(R.id.MessList);
		InitServiceList();
		
	}
	
	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		//注册一个广播接收器，启动餐桌抖动动画  
	    receiver = new NotifiReceiver();
	    IntentFilter filter = new IntentFilter();
	    filter.addAction(Constants.ACTION_SHOW_NOTIFICATION);
        filter.addAction(Constants.ACTION_NOTIFICATION_CLICKED);
        filter.addAction(Constants.ACTION_NOTIFICATION_CLEARED);
	    registerReceiver(receiver, filter);
	}

	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
		unregisterReceiver(receiver);
	}

	private void InitServiceList() {
		List<String> serNames=new ArrayList<String>();
		hashlist=new ArrayList<HashMap<String,String>>();
		serNames.add(MenuUtils.Service_1);
		serNames.add(MenuUtils.Service_2);
		serNames.add(MenuUtils.Service_3);
		
		for(String sername:serNames)
		{		
			HashMap<String, String> map=new HashMap<String, String>();
			map.put("name", sername); 
			map.put("id", sername);
			hashlist.add(map);
		}
		
		ServiceListAdapter simpleAdapter=new ServiceListAdapter(this, hashlist,
				R.layout.categorylist_item, new String[] { "name","id" },
				new int[] { R.id.category_name,R.id.category_id});
		simpleAdapter.selectedName="";
		serviceList.setAdapter(simpleAdapter);
	}
	private void InitMessageList() {
		for(int i=0;i<messages.size();i++)
		{
			ServiceMess sem=messages.get(i);
			HashMap<String, Object> map=new HashMap<String, Object>();
			map.put("id", sem.getId());
			map.put("content", sem.getsText());
			map.put("returnmess", sem.getMessReturned());
			String strCom="";
			if(sem.isComplete())
			{
				strCom="true";
			}
			else {
				strCom="false";
			}
			map.put("iscomplete", strCom);
			messHashMaps.add(map);
		}
		messageListAdapter=new MessageListAdapter(this, messHashMaps, 
				R.layout.messagelist_item, new String[]{"id","content","returnmess","iscomplete"}, 
				new int[]{R.id.messid,R.id.messtext,R.id.messreturn,R.id.isover});
		messListView.setAdapter(messageListAdapter);
	}
	class listitemClick implements OnItemClickListener{

		@Override
		public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
				long arg3) {
			// TODO Auto-generated method stub
			HashMap<String, String> map=hashlist.get(arg2);
			String nameString=map.get("name").toString();
			isNew=true;
			UUID uuid = UUID.randomUUID();
			ServiceMess sMess=new ServiceMess();
			sMess.setId(uuid.toString());
			sMess.setsText(nameString);
			sMess.setComplete(false);
			sMess.setMessReturned("已发出");
			messages.add(sMess);
			if(messages.size()<=1)
			{
				InitMessageList();
			}
			else {
				HashMap<String, Object> mapnew=new HashMap<String, Object>();
				mapnew.put("id", sMess.getId());
				mapnew.put("content", sMess.getsText());
				mapnew.put("returnmess", sMess.getMessReturned());
				String strCom="";
				if(sMess.isComplete())
				{
					strCom="true";
				}
				else {
					strCom="false";
				}
				mapnew.put("iscomplete", strCom);
				messHashMaps.add(mapnew);
				messageListAdapter.notifyDataSetChanged();
			}
			messListView.setSelectionFromTop(messages.size()-1, 300);
			
			try {
				HttpDownloader.RequestServices(MenuUtils.initUrl, sMess.getsText(), declare.curOrder.getId().toString());
			} catch (ClientProtocolException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
	}
	class ServiceListAdapter extends SimpleAdapter{

		int[] ids;
		String selectedName;
		private ArrayList<HashMap<String, Object>> mItemList;
		public String getSelectedName() {
			return selectedName;
		}

		public void setSelectedName(String selectedName) {
			this.selectedName = selectedName;
		}
		
		public ArrayList<HashMap<String, Object>> getmItemList() {
			return mItemList;
		}
		public void setmItemList(ArrayList<HashMap<String, Object>> hashList) {
			this.mItemList = hashList;
		}

		public ServiceListAdapter(Context context,
				List<? extends Map<String, ?>> data, int resource,
				String[] from, int[] to) {
			super(context, data, resource, from, to);
			// TODO Auto-generated constructor stub
			ids=to;
			mItemList = (ArrayList<HashMap<String, Object>>) data;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			// TODO Auto-generated method stub
			View localView = super.getView(position, convertView, parent);
			HashMap<String, Object> map=mItemList.get(position);

			TextView nameView=(TextView)localView.findViewById(R.id.category_name);
			if(!selectedName.equals("")&&selectedName.equals(map.get("name").toString()))
			{
				localView.setBackgroundDrawable(MyService.this.getResources().getDrawable(R.drawable.co));
				nameView.setTextColor(Color.WHITE);
			}
			else {
				localView.setBackgroundDrawable(MyService.this.getResources().getDrawable(R.drawable.c));
				nameView.setTextColor(Color.BLACK);
			}
			
	        return localView;
		}
		
	}
	
	class MessageListAdapter extends SimpleAdapter{

		private ArrayList<HashMap<String, Object>> mItemList;
		
		public ArrayList<HashMap<String, Object>> getmItemList() {
			return mItemList;
		}
		public void setmItemList(ArrayList<HashMap<String, Object>> hashList) {
			this.mItemList = hashList;
		}

		public MessageListAdapter(Context context,
				List<? extends Map<String, ?>> data, int resource,
				String[] from, int[] to) {
			super(context, data, resource, from, to);
			// TODO Auto-generated constructor stub
			mItemList = (ArrayList<HashMap<String, Object>>) data;
		}
		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			// TODO Auto-generated method stub
			return super.getView(position, convertView, parent);
		}

		
		
	}
	class NotifiReceiver extends BroadcastReceiver{

		@Override
		public void onReceive(Context context, Intent intent) {
			// TODO Auto-generated method stub
			String action = intent.getAction();
    		if (Constants.ACTION_SHOW_NOTIFICATION.equals(action)) {
                String notificationId = intent
                        .getStringExtra(Constants.NOTIFICATION_ID);
                String notificationApiKey = intent
                        .getStringExtra(Constants.NOTIFICATION_API_KEY);
                String notificationTitle = intent
                        .getStringExtra(Constants.NOTIFICATION_TITLE);
                String notificationMessage = intent
                        .getStringExtra(Constants.NOTIFICATION_MESSAGE);
                String notificationUri = intent
                        .getStringExtra(Constants.NOTIFICATION_URI);

                Toast toast = Toast.makeText(MyService.this, notificationMessage, Toast.LENGTH_SHORT); 
	            toast.show();
    		}
		}
		
	}
}
