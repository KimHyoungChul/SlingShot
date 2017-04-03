package com.cisco.slingshot.preference;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.cisco.slingshot.R;


public class SlingshotPreference{
	
	public static synchronized SlingshotPreference getInstance(Context context){
		if(mInstance == null){
			if(context == null){
	            throw new IllegalStateException("Creating CallManager need a valid Context.");
			}
			mInstance = new SlingshotPreference(context);
		}
		return mInstance;
	}
	
	private SlingshotPreference(Context context){
		mContext = context;
		_slingshotPrefs = PreferenceManager.getDefaultSharedPreferences(mContext);
	} 
	
	public void initPrefs(){
		PreferenceManager.setDefaultValues(mContext, R.xml.slingshot_preferences_user, true);
		PreferenceManager.setDefaultValues(mContext, R.xml.slingshot_preferences_video, true);
		PreferenceManager.setDefaultValues(mContext, R.xml.slingshot_preferences_call, true);
	}
	
	public void reset(){
		SharedPreferences.Editor prefEdit = _slingshotPrefs.edit();
        prefEdit.clear();
        prefEdit.commit();
        
		//PreferenceManager.setDefaultValues(mContext, R.xml.slingshot_preferences_user, true);
		PreferenceManager.setDefaultValues(mContext, R.xml.slingshot_preferences_video, true);
		PreferenceManager.setDefaultValues(mContext, R.xml.slingshot_preferences_call, true);
	}
	
	private static SlingshotPreference mInstance = null;
    protected SharedPreferences _slingshotPrefs = null;
    private  final Context mContext;
    private final static  String LOG_TAG = "SlingshotPreference";
}