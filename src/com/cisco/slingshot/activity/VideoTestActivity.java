package com.cisco.slingshot.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import com.cisco.slingshot.R;
import com.cisco.slingshot.contact.Contact;
import com.cisco.slingshot.ui.widget.VideoWidget;
import com.cisco.slingshot.utils.Util;



public class VideoTestActivity extends Activity {
	@Override
    protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		
		Intent i =  this.getIntent();
		
		Log.d("MainActivity", "data: "+ this.getIntent());
		// Full screen mode
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, 
                             WindowManager.LayoutParams.FLAG_FULLSCREEN);		
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        
		
		init();
	}
	
	@Override
	public void onResume(){
		super.onResume();
		Log.d("SMARTHOME", "===========onResume===========");
		new Thread(new Runnable(){
    		@Override
    		public void  run(){
	    			try {
	    				java.lang.Thread.sleep(1000);
	    			} catch (InterruptedException e) {
	    				e.printStackTrace();
	    			}
	    			
	    			mVideoWidget.playUri(VIDEO_PATH);
    			
    		}}).start();
	}
	
	@Override
	public void onPause(){
		super.onPause();
		mVideoWidget.stopPlayBack();
	}
	
	
	@Override
	public void onBackPressed(){
		//super.onBackPressed();
		showQuitDialog();
	}
	
	private void showQuitDialog(){
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		
		builder.setTitle("Quit ?")
		.setPositiveButton("OK",new DialogInterface.OnClickListener() {
	           public void onClick(DialogInterface dialog, int id) {
	        	   VideoTestActivity.this.finish();
	           }
	       })
	    .setNegativeButton("Cancel",new DialogInterface.OnClickListener() {
	           public void onClick(DialogInterface dialog, int id) {
	        	   dialog.cancel();
	           }
	       })
		.create()
		.show();
	}
	
	private void init(){
		
		_rootView = LayoutInflater.from(this).inflate(R.layout.video_test_layout, null);
		this.setContentView(_rootView);
		
		mVideoWidget = (VideoWidget)_rootView.findViewById(R.id.portal_layout_video);

	}
	
	private final static String VIDEO_PATH = "/sdcard/stb_slingshot_video_test.ts";
	
	private View _rootView;
	private VideoWidget mVideoWidget;
}