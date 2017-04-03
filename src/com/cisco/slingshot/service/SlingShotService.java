package com.cisco.slingshot.service;

import android.app.Service;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.sip.SipManager;
import android.os.IBinder;
import android.util.Log;

import com.cisco.slingshot.call.AccountManager;
import com.cisco.slingshot.call.AccountManager.LoginAccount;
import com.cisco.slingshot.call.CallManager;
import com.cisco.slingshot.call.ConnectStateData;
import com.cisco.slingshot.call.ConnectionStateListener;
import com.cisco.slingshot.receiver.DataNetworkInfoReceiver;
import com.cisco.slingshot.receiver.EthenetHotPlugEventReceiver;
import com.cisco.slingshot.receiver.EthenetHotPlugEventReceiver.EthenetHotPlugEventHandler;
import com.cisco.slingshot.receiver.IncomingCallReceiver;
import com.cisco.slingshot.receiver.IncomingcallListener;
import com.cisco.slingshot.utils.AsyncCallTask;
import com.cisco.slingshot.utils.ServiceToast;
import com.cisco.slingshot.utils.Util;

/**
 * Call service starting with the system and  running in the background, to listen the incoming call.
 * @author yuancui
 *
 */
public class SlingShotService extends Service implements IncomingcallListener,
														AccountManager.UpdateAccountCallback,
														ConnectionStateListener,
														EthenetHotPlugEventHandler{
	
	public final static String LOG_TAG = "SlingShotService";
	
	private IncomingCallReceiver mCallReceiver = null;
	private CallManager	mCallManager = null;
	
	@Override
	public IBinder onBind(Intent intent) {
		
		return null;
	}
	
	@Override
	public void onCreate(){
		Log.v(LOG_TAG, "Slingshot service created!");
		mCallReceiver = IncomingCallReceiver.getInstance();
		mCallManager = CallManager.getInstance(SlingShotService.this);
		mCallManager.addConnectionStateListener(this);
		
		Intent i = new Intent(this, SocketListenerService.class);
		startService(i);
	}
	
	@Override
	public int onStartCommand(Intent intent, int flags,int startId){
		Log.v(LOG_TAG, "Slingshot service start, startId is " + startId + ":" + intent );

		//CallManager.getInstance(this).initSip();
		startAllServices();
		
    	if(mCallReceiver != null){
            IntentFilter filter = new IntentFilter();  
            filter.addAction(IncomingCallReceiver.ACTION_RECEIVER_INCOMING_CALL);
    		this.registerReceiver(mCallReceiver, filter);
    	}
		mCallReceiver.registerFundaMentalListener(this);
		return START_STICKY;
	}
	
	
	@Override
	public void onDestroy(){
		Log.v(LOG_TAG, "slingshot  Service is destroyed");
		this.unregisterReceiver(mCallReceiver);	
		//CallManager.getInstance(this).releaseSip();
	}

	@Override
	public void onAnswer(Intent intent) {
		
		mCallManager.saveIncomingCallIntent(intent);
		//dealWithIntent(intent);
		try{
			AsyncCallTask.newTask(this, 
								  AsyncCallTask.ASYNC_INCOMING, 
								  null).execute();
		}catch(Exception e){
			e.printStackTrace();
		}
	}
	/**
	 * Try to parse the incoming call info  , for call-reminder.
	 * @param i incoming intent
	 */
	private void dealWithIntent(Intent i){
		String call_id = i.getStringExtra(SipManager.EXTRA_CALL_ID);
		String call_sd = i.getStringExtra(SipManager.EXTRA_OFFER_SD);
		Util.S_Log.d(LOG_TAG, "id:" + call_id + ",sd:" + call_sd);
		
	}

	@Override
	public void onDeny(Intent intent) {
		mCallManager.denyCall(intent);
	}

	@Override
	public void onNewIncoming(Intent intent) {
		/*This call back is just used as a notification, operations such as show a incoming dialog would be created by IncomingCallReceiver */	
	}
	
	@Override
	public void onReceive(LoginAccount account) {
		//ServiceToast.showMassage(SlingShotService.this,"Receive account!");
		//ServiceToast.showMassage(SlingShotService.this,"Account:" + account.username + "@" + account.domain);
		//ServiceToast.showMassage(SlingShotService.this,"Password:" + account.password);
		mCallManager.initSip(account);
        
	}
	

	@Override
	public void onStatusChanged(ConnectStateData state) {
		if(state == null){
			return;
		}
		/*
		if(state.state == ConnectStateData.CONN_STATE_READY){
			ServiceToast.showMassage( SlingShotService.this,state.account.username 
															+ " "
															+ SlingShotService.this.getString(R.string.main_status_bar_text_connect)
															+" "
															+ state.account.domain);

		}else if(state.state == ConnectStateData.CONN_STATE_FAIL){
			ServiceToast.showMassage( SlingShotService.this,state.account.username 
															+ " " 
															+ SlingShotService.this.getString(R.string.main_status_bar_text_disconnect)
															+" "
															+  state.account.domain);

		}else if(state.state == ConnectStateData.CONN_STATE_REGISTERING){
			ServiceToast.showMassage( SlingShotService.this,SlingShotService.this.getString(R.string.main_status_bar_text_connecting)
															+" "
															+ state.account.domain);

		}
		*/
	}
	

	

	
	private void startAllServices(){
		
		/*Network info service*/
		DataNetworkInfoReceiver.getInstance(this).startDataNetworkInfoService();
	
		
		//Read login account, login in call back
        AccountManager.getInstance(SlingShotService.this).updateAccount(this);
        
		/*Network hot plug listener*/
		EthenetHotPlugEventReceiver.getInstance(this).startHotPlugEventService();
		EthenetHotPlugEventReceiver.getInstance(this).setEthenetHotPlugEventHandler(this);
        
	}

	@Override
	public void onAdd() {
		//reconnect to SIP server
		Util.S_Log.d(LOG_TAG, "Network on =======");
		AccountManager.getInstance(SlingShotService.this).updateAccount(this);
	}

	@Override
	public void onPlug() {
		//ignore
	}
	
	
	


	
}