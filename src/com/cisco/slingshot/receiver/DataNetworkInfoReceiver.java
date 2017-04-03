package com.cisco.slingshot.receiver;

import java.util.ArrayList;
import java.util.Iterator;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import com.cisco.slingshot.call.AccountManager;
import com.cisco.slingshot.call.CallManager;
import com.cisco.slingshot.call.AccountManager.LoginAccount;
import com.cisco.slingshot.service.SlingShotService;
import com.cisco.slingshot.utils.ServiceToast;
import com.cisco.slingshot.utils.Util;

//uses-permission android:name="android.permission.ACCESS_NETWORK_STATE"></uses-permission>
	public class DataNetworkInfoReceiver {

	private boolean mIsConnected = false;
	private NetworkInfo mActiveNetworkInfo = null;
	private NetworkInfo[] mAllNetworkInfo = null;
	
	private ArrayList<DataNetworkConnectListener> mListeners = new ArrayList<DataNetworkConnectListener>();

	private Context mContext;
	
	private static DataNetworkInfoReceiver mInstance = null;
	
	private DataNetworkInfoReceiver(Context context){
		mContext = context; 	
	}
	
	public synchronized static DataNetworkInfoReceiver getInstance(Context context){
		if(mInstance == null){
			mInstance = new DataNetworkInfoReceiver(context);
		}
		return mInstance;
	}
	 
	private BroadcastReceiver mConntectStateReceiver = new BroadcastReceiver(){
		@Override
		public void onReceive(Context context, Intent intent){
			String action = intent.getAction();
			if(!action.equals(ConnectivityManager.CONNECTIVITY_ACTION))
				return;
			
			mIsConnected = !intent.getBooleanExtra(ConnectivityManager.EXTRA_NO_CONNECTIVITY,false);

			
			if(mIsConnected){

		
				ConnectivityManager connectivityManager = (ConnectivityManager)mContext.getSystemService(Context.CONNECTIVITY_SERVICE);
				mActiveNetworkInfo = connectivityManager.getActiveNetworkInfo();
				mAllNetworkInfo = connectivityManager.getAllNetworkInfo();
				
				//postStatusChanged(mActiveNetworkInfo);
								
				AccountManager.getInstance(mContext).updateAccount(new AccountManager.UpdateAccountCallback(){

					@Override
					public void onReceive(LoginAccount account) {
						//ServiceToast.showMassage(mContext, "Network changed, Try to login ");
						Util.S_Log.d("DataNetworkInfoReceiver", "Network changed, Try to login ");
						CallManager.getInstance(mContext).initSip(account);
						
					}
					
				});
				
			}else{
				
				//postStatusChanged(null);
				//ServiceToast.showMassage(context, "No network connected!");
				mActiveNetworkInfo = null;
				mAllNetworkInfo = null;
			}
			 				
		}
	};
	
	public void startDataNetworkInfoService(){
		IntentFilter filter = new IntentFilter();
		filter.addAction(ConnectivityManager.CONNECTIVITY_ACTION);
		mContext.registerReceiver(mConntectStateReceiver, filter);
	}
	
	public void stopDataNetworkInfoService(){
		mContext.unregisterReceiver(mConntectStateReceiver);
		
		mIsConnected = false;
		mActiveNetworkInfo = null;
		mAllNetworkInfo = null;
	}
	
	
	public void addDataNetworkConnectListener(DataNetworkConnectListener listener){
		mListeners.add(listener);
		listener.onDataNetworkChanged(mActiveNetworkInfo);
	}
	
	public void removeDataNetworkConnectListener(DataNetworkConnectListener listener){
		mListeners.remove(listener);
	}
	
    private void postStatusChanged(NetworkInfo info){
    	Iterator<DataNetworkConnectListener> itor = mListeners.iterator();
		while(itor.hasNext()){
			DataNetworkConnectListener listener = itor.next();
			listener.onDataNetworkChanged(info);
		}	
    }
	
	
	
	public boolean isDataNetworkConnected(){
		return mIsConnected;
	}
	
	public  String[] networkConnectionType(){
		
		final String[] noneType = new String[]{"none"};

		if(!isDataNetworkConnected())
			return noneType;
		
		String[] type = new String[mAllNetworkInfo.length];
		
		for(int i =0; i< mAllNetworkInfo.length; i++){
			type[i] = mAllNetworkInfo[i].getTypeName();
		}

		return type;
	}
	
	public String activeConnectionType(){
		final String noneType = "none";

		if(!isDataNetworkConnected())
			return noneType;
		

		return mActiveNetworkInfo.getTypeName(); 
	}
	

	public String getNetworkConnectionName(String networkConnecionType){
		if(!isDataNetworkConnected())
			return new String("none");
		
		for(int i = 0; i < mAllNetworkInfo.length; i++){
			if(networkConnecionType.equals(mAllNetworkInfo[i].getTypeName()))
				return mAllNetworkInfo[i].getSubtypeName();
		}
		
		return new String("unvalid type");
		
	}
	
	public interface DataNetworkConnectListener{
		abstract void onDataNetworkChanged(NetworkInfo info);
	}

	

}