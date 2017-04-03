package com.cisco.slingshot.ui.widget;

import android.content.Context;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.widget.FrameLayout;

import com.cisco.slingshot.R;

public class VideoWidget extends FrameLayout{
	public VideoWidget(Context context){
		super(context);
		mContext = context;
		initView();
	}

	public VideoWidget(Context context, AttributeSet attr) {
		super(context, attr);
		mContext = context;
		initView();
	}
	
	
	private void initView(){
		_rootView = LayoutInflater.from(mContext).inflate(R.layout.widget_video, null);
		this.addView(_rootView);
		mVideoFrame = (SurfaceView)_rootView.findViewById(R.id.widget_video_video);
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
		
	}
	
	public void playUri(final String uri){
    	
		if(mVideoFrame!=null){
			if(mVideoFrame.getHolder().isCreating())
				return;
				
			new Thread(new Runnable(){
	    		@Override
	    		public void  run(){
	    			try{
	    				if(mMediaPlayer == null){
	    					mMediaPlayer = new MediaPlayer();
	    				}else{
	    					mMediaPlayer.reset();
	    				}
	    				
	    	    		mPreparedListener = new MediaPlayer.OnPreparedListener(){
	    					@Override
	    					public void onPrepared(MediaPlayer mp) {
	    						Log.d(LOG_TAG, "Start video...");
	    						mp.start();
	    					}
	    	    		};
	    	    		mMediaPlayer.setDisplay(mVideoFrame.getHolder());
	        			mMediaPlayer.setDataSource(uri);
	        			mMediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
	            		mMediaPlayer.setOnPreparedListener(mPreparedListener);
	            		mMediaPlayer.prepareAsync();
	        		}catch(Exception e){
	        			Log.e(LOG_TAG, "initVideoAsync(String sdpUri),Failed: " + e.getMessage());
	        		}
	    		}
	    		}).start();
	    		
			
	    	//mVideoFrame.requestFocus();
			/*
			try{
				if(mMediaPlayer == null){
					mMediaPlayer = new MediaPlayer();

				}else{
					mMediaPlayer.reset();
				}
				
				mMediaPlayer.setDataSource(uri);
				mMediaPlayer.setDisplay(mVideoHolder);
	    		mPreparedListener = new MediaPlayer.OnPreparedListener(){
					@Override
					public void onPrepared(MediaPlayer mp) {
						Log.d(LOG_TAG, "Start video...");
						mp.start();
					}
	    		};
	    		mMediaPlayer.setOnPreparedListener(mPreparedListener);
	    		mMediaPlayer.prepareAsync();
			}catch(Exception e){
				Log.e(LOG_TAG, "initVideoAsync(String sdpUri),Failed: " + e.getMessage());
			}
			*/
		}
	}
	
	public void stopPlayBack(){
		if(mMediaPlayer != null && mMediaPlayer.isPlaying()){
			mMediaPlayer.stop();
			mMediaPlayer.reset();
			mMediaPlayer.release();
			mMediaPlayer = null;
		}
	}
	
	public void pausePlayBack(){
		if(mMediaPlayer.isPlaying()){
			mMediaPlayer.pause();
		}
	}
	
	public static String LOG_TAG = "DesktopVideoWidget";
	private View _rootView;
	
	private SurfaceView  					mVideoFrame = null;
	private SurfaceHolder 					mVideoHolder = null;
	private MediaPlayer 					mMediaPlayer;
	private MediaPlayer.OnPreparedListener 	mPreparedListener;
	
	
	private Context mContext;
	
}