package com.cisco.slingshot.activity;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.view.Window;
import android.view.WindowManager;

import com.cisco.slingshot.R;
import com.cisco.slingshot.service.SlingShotService;
import com.cisco.slingshot.utils.Util;


public class Splashscreen extends SlingshotBaseActivity {
    /** Called when the activity is first created. */
	
	public static final boolean Aquarius = false;;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        
		// Full screen mode
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, 
                             WindowManager.LayoutParams.FLAG_FULLSCREEN);		
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        
        setContentView(R.layout.splash);
        
        splashThread.start();

    }
    
    private Thread splashThread = new Thread()
    {
    	public void run()
    	{
    		try
    		{
    			int waited = 0;
    			while(_active && (waited < _spalshTime))
    			{
    				sleep(100);
    				if(_active)
    					waited +=100;
    			}
    			//Start Service
    			
    			startService(new Intent(Splashscreen.this, SlingShotService.class));
    			
    			//Start Activity
    			if(Aquarius)
    				startActivity(new Intent(Splashscreen.this, AquariusActivity.class));
    			else
    				startActivity(new Intent(Splashscreen.this, FragmentMainActivity.class));
    		}
    		catch(InterruptedException e)
    		{
    			e.printStackTrace();
    		}
    		finally
    		{
    			
       			Util.S_Log.d(TAG,"finish Splashscreen, start contact list ...");
    			
    			
    			finish();
    		}
    	}
    };
    
    private final static String TAG = "Splashscreen";
    private Boolean _active = true;
    private int _spalshTime = 3000;
}