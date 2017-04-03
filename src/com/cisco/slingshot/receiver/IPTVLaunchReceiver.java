package com.cisco.slingshot.receiver;

import android.app.ActivityManager;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.cisco.slingshot.R;
import com.cisco.slingshot.call.CallManager;
import com.cisco.slingshot.utils.ServiceToast;
import com.cisco.slingshot.utils.Util;

public class IPTVLaunchReceiver extends BroadcastReceiver{
	public final static  String LOG_TAG = "IPTVLaunchReceiver";
	public static final String ACTION_RECEIVER_IPTV = "cisco.slingshot.action.IPTV";
	
	public static final String IPTV_PACKAGE  = "com.android.smart.terminal.iptv";
	public static final String IPTV_MAIN_ACTIVITY  = "com.SyMedia.SyIptv.SyIptv";
	@Override
	public void onReceive(Context context, Intent intent) {
		if(intent.getAction().equals(ACTION_RECEIVER_IPTV)){
			Util.S_Log.d(LOG_TAG, "Start up");
			
			if(CallManager.getInstance(context).isInCall()){
				ServiceToast.showMassage(context, (String)context.getResources().getText(R.string.toast_text_deny_launcher_iptv));
				return;
			}
			
			launchIPTV(context);
		}
		 
	}
	
	private void launchIPTV(Context ctx){
		
		ActivityManager am = (ActivityManager) ctx.getSystemService(Context.ACTIVITY_SERVICE);  
		ComponentName cn = am.getRunningTasks(1).get(0).topActivity;  
		if(cn.getClassName().equals(IPTV_MAIN_ACTIVITY) && cn.getPackageName().equals(IPTV_PACKAGE))
			return;
		
		Intent intent = new Intent(Intent.ACTION_MAIN);
		intent.setClassName(IPTV_PACKAGE, IPTV_MAIN_ACTIVITY);
		intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
		intent.addFlags(Intent.FLAG_ACTIVITY_BROUGHT_TO_FRONT);
		Util.startActivitySafely(ctx,intent);
	}
	    
	
	
}