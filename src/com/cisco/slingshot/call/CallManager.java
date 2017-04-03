package com.cisco.slingshot.call;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Iterator;

import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.net.sip.SipException;
import android.net.sip.SipManager;
import android.net.sip.SipProfile;
import android.net.sip.SipRegistrationListener;
import android.preference.PreferenceManager;
import android.util.Log;

import com.cisco.slingshot.R;
import com.cisco.slingshot.call.AccountManager.LoginAccount;
import com.cisco.slingshot.net.sip.SipConfCall;
import com.cisco.slingshot.net.sip.SipManagerExtd;
import com.cisco.slingshot.receiver.IncomingCallReceiver;
import com.cisco.slingshot.utils.Util;
/**
 * Manage the call
 * @author yuancui
 *
 */
public class CallManager implements CallStatusObserver,SipRegistrationListener{
	
	private static final String TAG = "CallManager";
	
	//private LoginAccount 	mAccount = null;
	private SipManagerExtd 	mSipManager = null;
	private SipProfile 		mMyProfile = null;

	//private SipConfCall 		mCurrentCall = null;
	private  boolean isOnline = false;
	private  boolean isInCall = false;
	
	private Context mContext = null;	
	
	private Intent mIncomingIntent = null;
	

	private ConnectStateData mState;
	
	
	private static CallManager mInstance = null;
	/**
	 * Get a global CallManager instance
	 * @param context
	 * @return
	 */
	public static synchronized CallManager getInstance(Context context){
		if(mInstance == null){
			if(context == null){
	            throw new IllegalStateException("Creating CallManager need a valid Context.");
			}
			mInstance = new CallManager(context);
		}
		return mInstance;
	}

	private CallManager(Context context){
		mContext = context;
		mSipManager = new SipManagerExtd(mContext);
		mState = new ConnectStateData();
	}
	
	
	
	/**
	 * Initialize sip. Registration 
	 */
	public void initSip(LoginAccount account)
	{    	
		Util.S_Log.d(TAG, "init sip, LoginAccount: " + account.username + "@" + account.domain );

		if(!SipManager.isApiSupported(mContext)){
			Log.e(TAG, "Devices do not support SIP API!");
			return;
		}
		
		
		try{
			
			SipProfile newProfile = createSipProfile(account);
			/*Check if the same account*/
			if(mMyProfile != null &&  mSipManager.isOpened(mMyProfile.getUriString())){
				
				/*Account remain unchanged, quit*/
				/*
				if(mMyProfile.getUriString().equals(newProfile.getUriString())){
					Util.S_Log.d(TAG, "Account don't change, Ignore ================== ");
					return;
				}
				*/
				
				/*Different account, disconnect first*/
				if(mSipManager.isRegistered(mMyProfile.getUriString())){
					mSipManager.unregister(mMyProfile, this);
				}
				mSipManager.close(mMyProfile.getUriString());
			}
			
			/*Connect new account*/
			mState.account  = account;
            mMyProfile = newProfile;
            Intent i = new Intent();
            i.setAction(IncomingCallReceiver.ACTION_RECEIVER_INCOMING_CALL);
            PendingIntent pi = PendingIntent.getBroadcast(mContext, 0, i, Intent.FILL_IN_DATA);
            mSipManager.open(mMyProfile, pi, null);   
            mSipManager.setRegistrationListener(mMyProfile.getUriString(), this);
		} catch (ParseException pe) {
        	Log.e(TAG, "Connection Error.");
        } catch (SipException se) {
        	Log.e(TAG, "Connection Error.");
        }
	}
	
	public void onRegistering(String localProfileUri) {
        Util.S_Log.d(TAG, "Registering with SIP Server...");
        mState.state = ConnectStateData.CONN_STATE_REGISTERING;
        postStatusChanged(mState);
        //isOnline = false;
    }

    public void onRegistrationDone(String localProfileUri, long expiryTime) {
    	Util.S_Log.d(TAG, "Ready");
    	mState.state = ConnectStateData.CONN_STATE_READY;
    	postStatusChanged(mState);
    	isOnline = true;
    }

    public void onRegistrationFailed(String localProfileUri, int errorCode,
            String errorMessage) {
    	Util.S_Log.d(TAG, "Registration failed.  Please check settings.");
    	mState.state = ConnectStateData.CONN_STATE_FAIL;
    	postStatusChanged(mState);
    	isOnline = false;
    }
	
	
	private SipProfile createSipProfile(LoginAccount account) throws ParseException{
		SipProfile.Builder builder = new SipProfile.Builder(account.username, account.domain);
		//SipProfile.Builder builder = new SipProfile.Builder("sip:@" +  account.domain);
		//builder.setAuthUserName(account.username);
		builder.setPassword(account.password);
        builder.setOutboundProxy(account.proxy);
        builder.setProtocol(account.protocol);   
        
        if(account.port == null || account.port.equals("")){
        	builder.setPort(5060);
        }else{
        	Util.S_Log.d(TAG, account.port + "," + Integer.parseInt( account.port));
        	builder.setPort(Integer.parseInt( account.port));
        }
       
        builder.setSendKeepAlive(true);
        return builder.build();
	}
	/** 
	 * Add ConnectionStateListener
	 * @param listener Connection State Listener
	 */
	public void addConnectionStateListener(ConnectionStateListener listener){
		mConnSteListener.add(listener);
		listener.onStatusChanged(mState);
	}
	
	public void removeConnectionStateListener(ConnectionStateListener listener){
		mConnSteListener.remove(listener);
	}
	
	private ArrayList<ConnectionStateListener> mConnSteListener = new ArrayList<ConnectionStateListener>();
	
    private void postStatusChanged(ConnectStateData state){
    	Iterator<ConnectionStateListener> itor = mConnSteListener.iterator();
		while(itor.hasNext()){
			ConnectionStateListener listener = itor.next();
			listener.onStatusChanged(state);
		}	
    }
	
	/**
	 * Log out sip server	
	 */
	public void releaseSip(){
		if(mSipManager != null && mMyProfile != null){
			try {
				mSipManager.close(mMyProfile.getUriString());
			} catch (SipException e) {
				e.printStackTrace();
			}
		}
	}
	/**
	 * Save current incoming call intent 
	 * @param i
	 */
	public void saveIncomingCallIntent(Intent i){
		mIncomingIntent = i;
	}
	/**
	 * Get current incoming call intent if there is
	 * @return
	 */
	public Intent getIncomingCallIntent(){
		return mIncomingIntent;
	}
	
	private SipConfCall.Listener createDefaultListener(){
		return new SipConfCall.Listener(){
            @Override
    		public void onCallEstablished(SipConfCall call) {
    			Util.S_Log.d(TAG, "Default listener, onCallEstablished");
    		}
            @Override
    		public void onCallEnded(SipConfCall call) {
            	Util.S_Log.d(TAG, "Default listener,onCallEnded");
    		}
            @Override
    		public void onError(SipConfCall call, int errorCode, String errorMessage) {
    			Log.e(TAG, "Default listener,onError: " + errorMessage);
    		}			
		};
	}
	
	/**
	 * Wrapper for SipManagerExt.makeConfCall
	 * @param address
	 * @param listener
	 * @return
	 */
	public SipConfCall makeConfCall(String address, SipConfCall.Listener listener){
		SipConfCall.Listener callListener = listener;
		if(callListener == null){
			//Set default listener
			callListener = createDefaultListener();
		}		
		try {
			return mSipManager.makeConfCall(mMyProfile.getUriString(), address, listener, 10);
		} catch (SipException e) {
			Log.e(TAG, e.getMessage());
			return null;
		}

	}
	
	/**
	 * Wrapper for SipManagerExt.takeConfCall
	 * @param incomingIntent
	 * @param listener
	 * @return
	 */
	public SipConfCall takeConfCall( Intent incomingIntent,SipConfCall.Listener listener){
		SipConfCall.Listener callListener = listener;
		if(callListener == null){
			//Set default listener
			callListener = createDefaultListener();
		}
		
		try {
			return mSipManager.takeConfCall(incomingIntent, listener);
		} catch (SipException e) {
			Log.e(TAG, e.getMessage());
			return null;
		}
		
	}
	
	
	/**
	 * Deny a incoming call.
	 * @param incomingIntent incoming call intent
	 */
	public void denyCall(Intent incomingIntent){
		SipConfCall incomingCall = null;
		try{
			incomingCall = mSipManager.takeConfCall(incomingIntent, null);
            incomingCall.endCall();
            
        } catch (SipException e) {
        	Log.e(TAG, e.getMessage());
            if (incomingCall != null) {
                incomingCall.close();
            }
        }			
	}
	

	
	public boolean isInCall(){
		return isInCall;
	}
	
	public boolean isOnline(){
		return isOnline;
	}
	
	
	

	@Override
	public void onCallStart() {
		isInCall = true;
	}
	@Override
	public void onCallEnd() {
		isInCall = false;
	}
	@Override
	public void onCallError() {
		isInCall = false;
	}
	


}
