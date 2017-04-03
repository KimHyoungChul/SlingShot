package com.cisco.slingshot.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.cisco.slingshot.call.AccountManager;
import com.cisco.slingshot.call.CallManager;
import com.cisco.slingshot.call.AccountManager.LoginAccount;
import com.cisco.slingshot.service.SlingShotService;
import com.cisco.slingshot.utils.ServiceToast;
import com.cisco.slingshot.utils.Util;

public class TMCompletedReceiver extends BroadcastReceiver implements AccountManager.UpdateAccountCallback{
	public final static  String LOG_TAG = "TMCompletedReceiver";
	public static final String ACTION_RECEIVER_TM = "com.CTC.android.TM.ServiceAuthorized_ACTION";
	
	private Context mContext;
	@Override
	public void onReceive(Context context, Intent intent) {
		if(intent.getAction().equals(ACTION_RECEIVER_TM)){
			Util.S_Log.d(LOG_TAG, "TM Completed !");
			//AccountManager.getInstance(context).updateAccount(this);
		}
		 
	}

	@Override
	public void onReceive(LoginAccount account) {
		//ServiceToast.showMassage(mContext,"Receive account!");
		//ServiceToast.showMassage(mContext,"Account:" + account.username + "@" + account.domain);
		//ServiceToast.showMassage(mContext,"Password:" + account.password);
		
		CallManager.getInstance(mContext).initSip(account);
		
	}

	    
	
	
}