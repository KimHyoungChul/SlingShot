package com.cisco.slingshot.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;

public class EthenetHotPlugEventReceiver{
	
	private Context mContext;
	private static EthenetHotPlugEventReceiver mInstance = null;
	
	private EthenetHotPlugEventReceiver(Context context){
		mContext = context; 	
	}
	
	public synchronized static EthenetHotPlugEventReceiver getInstance(Context context){
		if(mInstance == null){
			mInstance = new EthenetHotPlugEventReceiver(context);
		}
		return mInstance;
	}	
	
	
	private BroadcastReceiver mInternalReceiver = new BroadcastReceiver(){
		
		@Override
		public void onReceive(Context context, Intent intent) {
			if(intent.getAction().equals(ACTION_ETHENET_ADD)){
				//ServiceToast.showMassage(context, "Cable pluged");
				notifyHotAdd();
			}else if(intent.getAction().equals(ACTION_ETHENET_REMOVE)){
				//ServiceToast.showMassage(context, "Cable unpluged");
				notifyHotPlug();
			}
			
		}
	};
	
	public void startHotPlugEventService(){
		IntentFilter filter = new IntentFilter();
		filter.addAction(ACTION_ETHENET_ADD);
		filter.addAction(ACTION_ETHENET_REMOVE);
		mContext.registerReceiver(mInternalReceiver, filter);
	}
	
	public void stopHotPlugEventService(){
		mContext.unregisterReceiver(mInternalReceiver);

	}	
	
	public void setEthenetHotPlugEventHandler(EthenetHotPlugEventHandler handler){
		mHandler = handler;
	}
	
	private void notifyHotAdd(){
		
		if(mHandler != null){
			mHandler.onAdd();
		}
	}
	
	private void notifyHotPlug(){
		/*
		NotificationDialog.showWarning(mContext, 
										(String)mContext.getText(R.string.network_error_dialog_hotplug_title), 
										(String)mContext.getText(R.string.network_error_dialog_hotplug_message), 
										null);
		*/								
		if(mHandler != null){
			mHandler.onPlug();
		}
	}
	
	public static interface EthenetHotPlugEventHandler{
		abstract void onAdd();
		abstract void onPlug();
	}
	
	private static final String ACTION_ETHENET_ADD = "cisco.action.net.eth0.add";
	private static final String ACTION_ETHENET_REMOVE = "cisco.action.net.eth0.remove";
	
	private  EthenetHotPlugEventHandler mHandler = null;
	
	
}