package com.cisco.slingshot.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.cisco.slingshot.service.SlingShotService;

/**
 * Handle Boot-Completed signal and start the service running in the background to listen the incoming call  
 * @author yuancui
 *
 */
public class BootCompletedReceiver extends BroadcastReceiver {
	
	private final String ACTION_SERVICE_SLINGSHOT = "cisco.action.service.slingshot";

	@Override
	public void onReceive(Context context, Intent intent) {
		if(intent.getAction().equals(Intent.ACTION_BOOT_COMPLETED)){
			Intent i = new Intent();
			i.setAction(ACTION_SERVICE_SLINGSHOT);
			context.startService(i);
		
		}
	}
}