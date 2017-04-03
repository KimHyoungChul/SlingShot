package com.cisco.slingshot.ui;

import android.util.Log;


public abstract class CallViewProxy{
	
	final static String LOG_TAG = "CallViewProxy";
	
	static public class CallStatus{
    	public boolean isIdle;
    	public boolean isPause;
    	//public boolean isAudioMuted;
    	//public boolean isVideoMuted; 
    	public boolean isLocalHiden;
    	public boolean isSpeekerOn;
    	public boolean isRecorderOn; 
    	public boolean isStatisticOn;
    	public int 	   videoFps;
	}
	//ControlPanel
	public  void muteVoice(boolean option){Log.i(LOG_TAG,"ignore");};
	public  void muteVideo(boolean option){Log.i(LOG_TAG,"ignore");};
	public  void pauseCall(boolean option){Log.i(LOG_TAG,"ignore");}
	public  void hideLocal(boolean option){Log.i(LOG_TAG,"ignore");}
	public 	void showStatistic(boolean option){Log.i(LOG_TAG,"ignore");}
	public  void endCall  (){Log.i(LOG_TAG,"ignore");}
	
	
	public abstract CallStatus requestCallStatus();
	
	
	
}