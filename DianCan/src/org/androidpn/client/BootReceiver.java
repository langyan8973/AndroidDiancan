package org.androidpn.client;


import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import com.diancan.R;

public class BootReceiver extends BroadcastReceiver {

	@Override
	public void onReceive(Context context, Intent intent) {
		// TODO Auto-generated method stub
//		Intent intents = new Intent(context,MyService.class);
//        context.startService(intents);
		// Start the service
        ServiceManager serviceManager = new ServiceManager(context);
        serviceManager.setNotificationIcon(R.drawable.notification);
        serviceManager.startService();
	}

}
