package com.cisco.slingshot.ui.fragment;

import java.io.IOException;

import android.app.Fragment;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;

import com.cisco.slingshot.R;
import com.cisco.slingshot.test.TsPlayer;



public class DevVideoFragment extends Fragment {
	public static final String LOG_TAG = "DevVideoFragment";
	private View 		mFragmentView;	
	
	private SurfaceView  					mVideoFrame  = null;
	private SurfaceHolder 					mVideoHolder = null;
	
	private TsPlayer 						mTsPlayer    = null;
	private boolean  						isPlaying    = false;
	
	 
	@Override 
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState){
    	
    	mFragmentView = inflater.inflate(R.layout.dev_video_fragment, container,false);
    	initFragmentView(mFragmentView);
    	
    	return mFragmentView;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }
    
    
    public void onPause (){
    	super.onPause();
		
			mTsPlayer.Stop();
		 	mTsPlayer.Release();
			isPlaying = false;
			
    	
    }
    
    public void onResume(){
    	super.onResume();
    	mTsPlayer.Init();
		mTsPlayer.StartPlay();
		isPlaying = true;    	
    }
    
	private void initFragmentView(View v){
		if(v == null)
			return;
		
		
		mVideoFrame = (SurfaceView)v.findViewById(R.id.video_frame);
		
		mVideoFrame.setOnClickListener(new OnClickListener(){
 
			@Override
			public void onClick(View arg0) {
				if(isPlaying){
					mTsPlayer.Stop();
					isPlaying = false;
				}else{
					mTsPlayer.StartPlay();
					isPlaying = true;
				}
				
			}
			  
		});
		
        SurfaceHolder videoHolder = mVideoFrame.getHolder();
        videoHolder.addCallback(new SurfaceHolder.Callback(){
			@Override
			public void surfaceCreated(SurfaceHolder holder) {mVideoHolder = holder;}

			@Override
			public void surfaceChanged(SurfaceHolder holder, int format,
					int width, int height) {
			}

			@Override
			public void surfaceDestroyed(SurfaceHolder holder) {}
        	
        });
        videoHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);	
        
        mTsPlayer = TsPlayer.newInstance();
        
        
	}
}
	
