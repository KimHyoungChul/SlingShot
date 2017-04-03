package com.cisco.slingshot.call;

import android.app.ProgressDialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.IBinder;
import android.os.RemoteException;
import android.preference.PreferenceManager;
import android.view.WindowManager;

import com.CTC_ChinaNet.android.tm.aidl.CTCCISCOIMSResult;
import com.CTC_ChinaNet.android.tm.aidl.TMServiceCISCOIMSAidl;
import com.cisco.slingshot.R;
import com.cisco.slingshot.utils.AESCrypto;
import com.cisco.slingshot.utils.NotificationDialog;
import com.cisco.slingshot.utils.ServiceToast;
import com.cisco.slingshot.utils.TimeoutTimer;
import com.cisco.slingshot.utils.Util;

public class AccountManager{
	private static final String LOG_TAG = "AccountManager";
	
	public static boolean DISABLE_LOCAL = true;

	
	private static AccountManager mInstance = null;
	private Context mContext;
	 
	
	/*TM IMS Serivce*/
	private final String ACTION_BIND_REMOTESERVER = "com.CTC_ChinaNet.android.tm.TMServiceCISCOIMS";
	//private final String ACTION_BIND_REMOTESERVER = "cisco.action.IMS_SERBVICE_STUB";
	private TMServiceCISCOIMSAidl mIMSService;
	private boolean isServiceReady = false;
	
	private UpdateAccountCallback mUpdateAccountCallback = null;
	
	public static synchronized AccountManager getInstance(Context context){
		if(mInstance == null){
			if(context == null){
	            throw new IllegalStateException("Creating CallManager need a valid Context.");
			}
			mInstance = new AccountManager(context);
		}
		return mInstance;
	}
	
	private AccountManager(Context context){
		mContext = context;
	} 
	
    
	private  final ServiceConnection mRemoteConnection = new ServiceConnection(){
		public void onServiceConnected(ComponentName name, IBinder service){
			Util.S_Log.d(LOG_TAG, "Remote Service Connected!!");
			mIMSService = TMServiceCISCOIMSAidl.Stub.asInterface(service);
			new UpdateAccountTask(mUpdateAccountCallback).start();
		}
		
		public void onServiceDisconnected(ComponentName name){
			Util.S_Log.d(LOG_TAG, "Remote service Disconnected!!");
			mIMSService = null;
		}
	};
	
	
	public void updateAccount(UpdateAccountCallback callback){
		mUpdateAccountCallback = callback;
		
		SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(mContext);
		final String ACCOUNT_SOURCE_KEY = mContext.getString(R.string.key_settings_user_account_source);
		
		final String SOURCE_IMS = mContext.getString(R.string.settings_user_account_source_ims);
		final String SOURCE_LOCAL = mContext.getString(R.string.settings_user_account_source_local);
		
		String account_source_value = prefs.getString(ACCOUNT_SOURCE_KEY,
				mContext.getString(R.string.settings_user_account_source_default));

		
		if(account_source_value.equals(SOURCE_IMS)){
			if(BindRemoteServer()){
				isServiceReady = true;
			}else{
		    	NotificationDialog.showError(	mContext,
												mContext.getString(R.string.ims_error_title), 
												mContext.getString(R.string.ims_error_content), 
												null);
			}
		}else if(account_source_value.equals(SOURCE_LOCAL)){
			mUpdateAccountCallback.onReceive(loadLocalAccount());
		}
	}
	
	
	private  boolean BindRemoteServer(){
    	try{
    		return mContext.bindService(new Intent(ACTION_BIND_REMOTESERVER), mRemoteConnection, Context.BIND_AUTO_CREATE);
    	}catch( SecurityException ex){
    		Util.S_Log.d(LOG_TAG,"BindRemoteServer error!!");
    		return false;
    	}
    	
    }
    
    private  void UnBindRemoteServer(){
    	if(isServiceReady){
    		mContext.unbindService(mRemoteConnection);
    		isServiceReady = false;
    	}
    }    
    
	private LoginAccount loadLocalAccount(){
		
		LoginAccount account = new LoginAccount();
		
		//load the account setting
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(mContext);
        account.username	= prefs.getString(mContext.getString(R.string.str_pref_username), "");
        account.domain 	= prefs.getString(mContext.getString(R.string.str_pref_domain), "");
        account.password 	= prefs.getString(mContext.getString(R.string.str_pref_password), "");
        account.proxy 		= prefs.getString(mContext.getString(R.string.str_pref_proxy), "");
        account.port 		= prefs.getString(mContext.getString(R.string.str_pref_port), "5060");
        account.protocol 	= prefs.getString(mContext.getString(R.string.str_pref_protocol), 
        									  mContext.getString(R.string.settings_connection_protocol_default));
        
        if (account.username.length() == 0 || account.domain.length() == 0 || account.password.length() == 0) {
        	//if no settings, load the default
        	
        	//return null;
        	
			 Editor prefsEditor = prefs.edit();
			 prefsEditor.putString(mContext.getString(R.string.str_pref_username), "default");
			 prefsEditor.putString(mContext.getString(R.string.str_pref_domain), "default");
			 prefsEditor.putString(mContext.getString(R.string.str_pref_password), "default");
			 prefsEditor.putString(mContext.getString(R.string.str_pref_proxy), "default");
			 prefsEditor.putString(mContext.getString(R.string.str_pref_port), "5060");
			 prefsEditor.putString(mContext.getString(R.string.str_pref_protocol), "UDP");
			 prefsEditor.apply();
			 
        	account.username 	= prefs.getString(mContext.getString(R.string.str_pref_username), "");
        	account.domain 	= prefs.getString(mContext.getString(R.string.str_pref_domain), "");
        	account.password 	= prefs.getString(mContext.getString(R.string.str_pref_password), "");
        	account.proxy 		= prefs.getString(mContext.getString(R.string.str_pref_proxy), "");
        	account.port 		= prefs.getString(mContext.getString(R.string.str_pref_port), "5060");
        	account.protocol 	= prefs.getString(mContext.getString(R.string.str_pref_protocol), 
					  							  mContext.getString(R.string.settings_connection_protocol_default));
        	
        }
        
        return account;
	} 
	
	private void saveAccount(LoginAccount account){
		SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(mContext);
		
		 Editor prefsEditor = prefs.edit();
		 prefsEditor.putString(mContext.getString(R.string.str_pref_username), account.username);
		 prefsEditor.putString(mContext.getString(R.string.str_pref_domain), account.domain);
		 prefsEditor.putString(mContext.getString(R.string.str_pref_password), account.password);
		 prefsEditor.putString(mContext.getString(R.string.str_pref_proxy), account.proxy);
		 prefsEditor.putString(mContext.getString(R.string.str_pref_port), account.port);
		 prefsEditor.putString(mContext.getString(R.string.str_pref_protocol), account.protocol);
		 prefsEditor.apply();
	}
    

	
	 
	 
    
	public static class LoginAccount{
	 	public String username ;
	 	public String domain ;
	 	public String password;
	 	public String proxy;
	 	public String port;
	 	public String protocol;
	}
	
	
	
	public static interface UpdateAccountCallback{
		abstract void onReceive(LoginAccount account);
	}
	
	
	private class UpdateAccountTask{
		
		public UpdateAccountTask(UpdateAccountCallback uacb){
			mUpdateAccountCallback = uacb;
			mTimeoutCallback = new TimeoutTimer.TimeoutCallback(){
				@Override
				public void onTimeout() {
					mConnectServerCount ++;
					if(mConnectServerCount < 5){
						/*Create a new timeout timer*/
						mTimeoutTimer = new TimeoutTimer("UpdateAccountTimeoutTimer" + mConnectServerCount,this);
						mTimeoutTimer.start(mLoginTimeOutArray[mConnectServerCount]);
						return;
					}
					//closeStartCallDialog();
					ServiceToast.showMassage(mContext, "Get account from IMS failed!");
						
				}};
		}
		 
		public void start(){
			 //startTimeoutTimer();
			 new Thread(new Runnable() {
		            public void run() {
		            	if(mIMSService != null){
							CTCCISCOIMSResult result;
							try {
								
								 
								result = mIMSService.getIMSParameter();
								//stopTimeoutTimer();
															 
								if(result != null){
									LoginAccount account = new LoginAccount();
									account.username = result.getUserName();
									account.domain = result.getDomain();
									//account.password = new String(AESCrypto.dencrypt(Base64.decode(result.getPassword(),Base64.DEFAULT), result.getUserName().getBytes()));//result.getPassword();
									//account.password = new String(AESCrypto.dencrypt(result.getPassword().getBytes("ISO-8859-1"), result.getUserName().getBytes()));
									account.password = AESCrypto.dencryptAES(result.getPassword(), result.getUserName());
									account.proxy = result.getProxy();
									account.port = result.getPort();
									account.protocol = result.getProtocol();
									
									Util.S_Log.d(LOG_TAG, "IMS: username: " + account.username);
									Util.S_Log.d(LOG_TAG, "IMS: domain: " + account.domain);
									Util.S_Log.d(LOG_TAG, "IMS: password: " + result.getPassword());
									Util.S_Log.d(LOG_TAG, "IMS: proxy: " + account.proxy);
									Util.S_Log.d(LOG_TAG, "IMS: port: " + account.port);
									Util.S_Log.d(LOG_TAG, "IMS: protocol: " + account.protocol);
									saveAccount(account);
									mUpdateAccountCallback.onReceive(account);
								}
							} catch (RemoteException e) {
								e.printStackTrace();
							}
						}
		            }
			 }, "UpdateAccountTask").start();		
			 
		}
		
		private void startTimeoutTimer(){
			//showStartCallDialog(); 
			mTimeoutTimer = new TimeoutTimer("UpdateAccountTimeoutTimer"+mConnectServerCount,mTimeoutCallback);
			mTimeoutTimer.start(mLoginTimeOutArray[mConnectServerCount]);
		}
		
		private void stopTimeoutTimer(){
			//closeStartCallDialog();
			if(mTimeoutTimer != null){
				mTimeoutTimer.cancel();
				mTimeoutTimer = null;
			}
			
		}
		
		private void showStartCallDialog(){
			 try{
				 mProgressDialog = new ProgressDialog(mContext);
				 mProgressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
				 mProgressDialog.setMessage("Update user acount info ...");
				 mProgressDialog.setCancelable(false);
				 mProgressDialog.getWindow().setType(WindowManager.LayoutParams.TYPE_SYSTEM_ALERT);
				 mProgressDialog.show();	
			 }catch(Exception e){
				 e.printStackTrace();
			 }
		 }
		 private void closeStartCallDialog(){
			 if(mProgressDialog!=null)
				mProgressDialog.cancel();
			 	mProgressDialog = null;
		 } 
		 
		private ProgressDialog mProgressDialog = null;
		//private final  int[] mLoginTimeOutArray = {30,5,5,5*60,5};
		private final  int[] mLoginTimeOutArray = {5,5,5,5,5};
		private int mConnectServerCount = 0;
		private UpdateAccountCallback mUpdateAccountCallback;
		
		private TimeoutTimer mTimeoutTimer = null;
		private TimeoutTimer.TimeoutCallback mTimeoutCallback;

	}
	
}