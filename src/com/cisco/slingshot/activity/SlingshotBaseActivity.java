package com.cisco.slingshot.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.sip.*;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.cisco.slingshot.net.sip.*; 
import com.cisco.slingshot.receiver.*;

public  class SlingshotBaseActivity extends Activity implements IncomingcallListener{
	private final static String LOG_TAG = "SlingshotActivity";
    //receiver
    public IncomingCallReceiver callReceiver = null;
    
    @Override
    public void onCreate(Bundle savedInstanceState){
    	super.onCreate(savedInstanceState);
    	//callReceiver = new IncomingCallReceiver();
    	//callReceiver.registerIncomingcallListener(this);
    }
    protected void onPause(){
    	//this.unregisterReceiver(callReceiver);
    	super.onPause();
    }    
    protected void onResume(){
    	if(callReceiver != null){
            IntentFilter filter = new IntentFilter();  
            filter.addAction(IncomingCallReceiver.ACTION_RECEIVER_INCOMING_CALL);
    		//this.registerReceiver(callReceiver, filter);
    	}
    	super.onResume();
    }
    
    
	public  void onAnswer(Intent intent){};
	public  void onDeny(Intent intent) {}
	@Override
	public void onNewIncoming(Intent intent) {};
    
    

}
