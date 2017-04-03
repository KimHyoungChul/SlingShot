package com.cisco.slingshot;

import java.util.Locale;

import android.app.Application;

import com.cisco.slingshot.preference.SlingshotPreference;
import com.cisco.slingshot.utils.Util;

public class SlingshotApplication extends Application{
	
	
	private final static String LOG_TAG = "SlingshotApplication";
	
    @Override
    public void onCreate(){
    
        // Force the locale to US because it is not set on the STB.
    	Util.S_Log.d(LOG_TAG, "Slingshot application start...");
        Locale.setDefault(Locale.SIMPLIFIED_CHINESE);
        
        /*Get default setting values when launching app the first time*/
        SlingshotPreference.getInstance(this).initPrefs();
    }
}