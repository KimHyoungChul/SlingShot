package com.cisco.slingshot.call;

import com.cisco.slingshot.net.sip.SipConfCall;

import android.content.Context;
import android.util.Log;

public class CallStatusMachine {
	
	private final static String TAG = "CallStatusMachine";
	
	private SipConfCall mCall 	=  null;
	private Context mContext;
	
	private boolean mIsInCall 	= false;
	private boolean mIsMuted 	= false;
	private boolean mIsVisible	= false;
	
	public CallStatusMachine(Context ctx){
		mContext = ctx;
	}
	
	public static CallStatusMachine newInstance(Context ctx) {
		if(ctx == null){
            throw new IllegalStateException(
            "Creating CallStatusMachine need a valid Context.");
		}
		return  new CallStatusMachine(ctx);
	}
	//get
	public boolean isInCall(){
		
		if(mCall == null){
			Log.e(TAG, "Call status machine has not attched to a call");
			return false;
		}
		return mCall.isInCall();
		
	}
	public boolean isMuted(){
		if(mCall == null){
			Log.e(TAG, "Call status machine has not attched to a call");
			return false;
		}
		return mCall.isMuted();
	}
	
	
	public boolean isVisible(){return mIsVisible;}
	
	//set
	public void setInCall(boolean value){mIsInCall = value;}	
	public void setMuted(boolean value){mIsMuted = value;}
	public void setVisible(boolean value){mIsVisible = value;}
	

	public void attachCall(SipConfCall call)throws IllegalStateException{
		if(mCall != null){
            throw new IllegalStateException(
            "Current status machine has attached to another call");
		}
		mCall = call;
	}
	

	

	
}
