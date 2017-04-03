package com.cisco.slingshot.ui.fragment;

import android.os.Bundle;
import android.preference.PreferenceFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.cisco.slingshot.R;

public class CallSettingsFragment extends PreferenceFragment{
	
	private static final String TAG = "CallSettingsFragment";

	@Override
	public void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		new Thread(new Runnable(){
    		@Override
    		public void  run(){
    			addPreferencesFromResource(R.xml.slingshot_preferences_call);
    		}}).start();
	}
	
    @Override 
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState){
    	
    	  return inflater.inflate(R.layout.slingshot_layout_preferences_call, container, false);
    }

	
}