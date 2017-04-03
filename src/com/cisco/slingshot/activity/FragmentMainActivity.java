package com.cisco.slingshot.activity;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

import com.cisco.slingshot.R;
import com.cisco.slingshot.call.CallManager;
import com.cisco.slingshot.call.ConnectStateData;
import com.cisco.slingshot.call.ConnectionStateListener;
import com.cisco.slingshot.test.TsPlayer;
import com.cisco.slingshot.utils.Util;
import com.cisco.slingshot.utils.Util.MathineStatus;

/**
 * Main activity for slingshot
 * @author yuancui
 *
 */
public class FragmentMainActivity extends Activity implements ConnectionStateListener{
	@Override
    protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		Intent i =  this.getIntent();
		
		Util.S_Log.d("FragmentMainActivity", "data: "+ this.getIntent());
		// Full screen mode
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, 
                             WindowManager.LayoutParams.FLAG_FULLSCREEN);		
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        
		this.setContentView(R.layout.fragment_layout);

	}
	
	private void updateStatusBar(final ConnectStateData state){
		Util.S_Log.d("FragmentMainActivity", "updateStatusBar");
		this.runOnUiThread(new Runnable(){
			@Override
			public void run() {
				ImageView statusIcon = (ImageView)FragmentMainActivity.this.findViewById(R.id.statusbar_image);
				TextView statusText = (TextView)FragmentMainActivity.this.findViewById(R.id.statusbar_text);
				
				if(state.state == ConnectStateData.CONN_STATE_READY){
					statusText.setText(state.account.username + " " + FragmentMainActivity.this.getString(R.string.main_status_bar_text_connect)/*+" "+ state.account.domain*/);
					statusIcon.setImageDrawable(FragmentMainActivity.this.getResources().getDrawable(R.drawable.ic_pass));
					statusIcon.setVisibility(View.VISIBLE);
				}else if(state.state == ConnectStateData.CONN_STATE_FAIL){
					statusText.setText(state.account.username + " " + FragmentMainActivity.this.getString(R.string.main_status_bar_text_disconnect)/*+" "+  state.account.domain*/);
					statusIcon.setImageDrawable(FragmentMainActivity.this.getResources().getDrawable(R.drawable.ic_error));
					statusIcon.setVisibility(View.VISIBLE);
				}else if(state.state == ConnectStateData.CONN_STATE_REGISTERING){
					statusText.setText(FragmentMainActivity.this.getString(R.string.main_status_bar_text_connecting)/*+" "+ state.account.domain*/);
					statusIcon.setVisibility(View.INVISIBLE);
				}
			}
		});
		 
	}
	
	@Override 
	public void onResume(){
		Util.S_Log.d("FragmentMainActivity", "onResume");
		super.onResume();
		CallManager.getInstance(this).addConnectionStateListener(this);
		/*Get system version*/
		TextView system_ver = (TextView)FragmentMainActivity.this.findViewById(R.id.system_version_text);
		String system_version = MathineStatus.getSystemVeriosn();
		system_ver.setText(system_version);
		
		  
		//Util.S_Log.d("FragmentMainActivity", "Test shell:" + TsPlayer.newInstance().TestShell());
		
	}
	
	@Override
	public void onPause(){
		Util.S_Log.d("FragmentMainActivity", "onPause");
		super.onPause();
		CallManager.getInstance(this).removeConnectionStateListener(this);
	}

	@Override
	public void onStatusChanged(ConnectStateData state) {
		Util.S_Log.d("FragmentMainActivity", "onStatusChanged");
		if(state != null){
			updateStatusBar(state);
		}
	}
	
}