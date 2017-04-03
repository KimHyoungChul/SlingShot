package com.cisco.slingshot.receiver;

import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.cisco.slingshot.R;
import com.cisco.slingshot.call.CallManager;
import com.cisco.slingshot.ui.quickcall.QuickCallLauncherFlipperDialog;
import com.cisco.slingshot.utils.ServiceToast;
import com.cisco.slingshot.utils.Util;

public class QuickCallReceiver extends BroadcastReceiver{
	public final static  String LOG_TAG = "StartUpReceiver";
	public static final String ACTION_RECEIVER_QUICK_CALL = "cisco.slingshot.action.QUICK_CALL";
	private Dialog mOutgoingcallDiag = null;
	
	@Override
	public void onReceive(Context context, Intent intent) {
		if(intent.getAction().equals(ACTION_RECEIVER_QUICK_CALL)){
			Util.S_Log.d(LOG_TAG, "Start up");
			
			if(CallManager.getInstance(context).isInCall()){
				ServiceToast.showMassage(context, (String)context.getResources().getText(R.string.toast_text_deny_new_call));
			}else{
				QuickCallLauncherFlipperDialog.getInstance(context).show();
				
			}

		}
		
	}
	    
	
	
}