package com.cisco.slingshot.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.cisco.slingshot.ui.CallViewProxy;
import com.cisco.slingshot.utils.Util;

public class StandbyReceiver extends BroadcastReceiver{
	public final static  String LOG_TAG = "StandbyReceiver";
	public static final String ACTION_RECEIVER_STANDBY = "cisco.action.powerkey";

	@Override
	public void onReceive(Context context, Intent intent) {
		if(intent.getAction().equals(ACTION_RECEIVER_STANDBY)){
			Util.S_Log.d(LOG_TAG, "Receive standby signal,Stop call if necessary");
			if(mCallViewProxy != null){
				mCallViewProxy.endCall();
			}

		}
		 
	}
	
	 private CallViewProxy 				mCallViewProxy = null;
	 
	 public void setCallViewProxy(CallViewProxy proxy){
			mCallViewProxy = proxy;		
	 }
	
	    
	
	
}