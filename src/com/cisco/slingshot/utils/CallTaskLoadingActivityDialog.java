package com.cisco.slingshot.utils;

import android.app.Activity;
import android.os.Bundle;

import com.cisco.slingshot.R;

public class CallTaskLoadingActivityDialog extends Activity{
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
        // Be sure to call the super class.
        super.onCreate(savedInstanceState);

        setContentView(R.layout.dialog_activity);
 
    }
	
	@Override
	public void onPause(){
		super.onPause();
		
		//Finish when it was closed
		finish();
	}
	
}