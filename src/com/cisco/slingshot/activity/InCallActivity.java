package com.cisco.slingshot.activity;

import android.app.Activity;
import android.content.IntentFilter;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;

import com.cisco.slingshot.R;
import com.cisco.slingshot.call.CallStatusObserver;
import com.cisco.slingshot.receiver.EthenetHotPlugEventReceiver;
import com.cisco.slingshot.receiver.StandbyReceiver;
import com.cisco.slingshot.ui.CallViewProxy;
import com.cisco.slingshot.ui.InCallView;
import com.cisco.slingshot.utils.Util;

/**
 * In call activity
 * @author yuancui
 *
 */
public class InCallActivity extends Activity implements CallStatusObserver{
	
	private final static String LOG_TAG = "InCallActivity2";
	public final static String ACTION_CALL_INCOMING = "slingshot.intent.action.INCOMING_CALL"; 
	public final static String ACTION_CALL_OUTGOING = "slingshot.intent.action.OUTGOING_CALL";
	public final static String ACTION_CALL_ANDROID  = "android.intent.action.CALL_PRIVILEGED";
	
	private boolean _canAutoFinishingActivity = true;
	
	private StandbyReceiver _StandbyReceiver =null;

	public boolean canAutoFinishActivity(){
		return _canAutoFinishingActivity;
	}
	
	public void setCanAutoFinishingActivity(boolean canAutoFinishActivity){
		_canAutoFinishingActivity = canAutoFinishActivity;
	}
	
	private InCallView mInCallView = null;
	private View mRootView;
	
    
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
		// Full screen mode
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        /*
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, 
                             WindowManager.LayoutParams.FLAG_FULLSCREEN);	
                             */	
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        
        mRootView = this.getLayoutInflater().inflate(R.layout.incall, null);
        setContentView(mRootView);
        
        mInCallView = (InCallView)this.findViewById(R.id.main);
        mInCallView.addCallStatusObserver(this);
        
        _StandbyReceiver = new StandbyReceiver();
        
        
    }	
    
    @Override
    public void onResume(){
        Util.S_Log.d(LOG_TAG, "IncallActivity========onResume=======");
        /*register in coming call listener*/
        //IncomingCallReceiver.getInstance().registerReplacedListener(mInCallView);
        
		/*Network hot plug listener*/
		//EthenetHotPlugEventReceiver.getInstance(this).startHotPlugEventService();
		
		/*Standby signal*/
        IntentFilter filter = new IntentFilter();  
        filter.addAction(StandbyReceiver.ACTION_RECEIVER_STANDBY);
		this.registerReceiver(_StandbyReceiver, filter);
    	
		_StandbyReceiver.setCallViewProxy(new CallViewProxy(){

			@Override
			public CallStatus requestCallStatus() {
				// TODO Auto-generated method stub
				return null;
			}
			
			@Override
			public  void endCall  (){
				mInCallView.endCall();
			}
        	
        });
		
    	super.onResume();
    }
    @Override
    public void onBackPressed() {
    	Util.S_Log.d(LOG_TAG, "IncallActivity========onBackPressed========");
    	super.onBackPressed();
    	/*
    	boolean success = mInCallView.endCall();
    	if(!success){
    		super.onBackPressed();
    	}
    	*/
    }
    @Override
    public void onPause(){
        Util.S_Log.d(LOG_TAG, "IncallActivity========onPause========");
        /*register in coming call listener*/
        //IncomingCallReceiver.getInstance().registerReplacedListener(null);
        
		/*Network hot plug listener*/
		//EthenetHotPlugEventReceiver.getInstance(this).stopHotPlugEventService();
		
		/*standby*/
		this.unregisterReceiver(_StandbyReceiver);	
		_StandbyReceiver.setCallViewProxy(null);
		
        /*Make sure the audio been restored back to origin*/
        mInCallView.restoreAudioMode();
    	super.onPause();
    }
    @Override 
    public void onStop(){
    	Util.S_Log.d(LOG_TAG, "IncallActivity========onStop=========");
    	super.onStop();
    }
    @Override
    public void onDestroy(){
    	Util.S_Log.d(LOG_TAG, "IncallActivity========onDestroy======");
    	super.onDestroy();
    }
    
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event){
    	Util.S_Log.d(LOG_TAG, "IncallActivity========onKeyDown======,keycode:" + keyCode);
    	switch(keyCode){
    	case KeyEvent.KEYCODE_VOLUME_UP:
    		mInCallView.VolumeUp();
    		return true;
    	case KeyEvent.KEYCODE_VOLUME_DOWN:
    		mInCallView.VolumeDown();
    		return true;    		
    	case KeyEvent.KEYCODE_MUTE:
    		mInCallView.mute();
    		return true;    
    	}
    	return false;
    }
    
    
    /*Call Backs to do operations when call status changed*/
	@Override
	public void onCallStart() {
	}
	@Override
	public void onCallEnd() {
		if(canAutoFinishActivity())this.finish();
	}
	@Override
	public void onCallError() {
		if(canAutoFinishActivity())this.finish();
	}

}
