package com.cisco.slingshot.activity;

import android.app.Activity;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import com.cisco.slingshot.R;
import com.cisco.slingshot.utils.Util;


public class AquariusActivity extends Activity{
	public static final String LOG_TAG = "Aquarius";
	
	private View _rootView;
	
	@Override
    protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		Util.S_Log.d(LOG_TAG, "Create Activity... ");
		// Full screen mode
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, 
                             WindowManager.LayoutParams.FLAG_FULLSCREEN);		
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        
		init();
	}
	
	
	
	private void init(){
			
			_rootView = this.getLayoutInflater().inflate(R.layout.aquarius_container, null);
	        setContentView(_rootView);
	}
	
	
}