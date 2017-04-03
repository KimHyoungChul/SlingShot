package com.cisco.slingshot.utils;

import android.content.Context;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.ToneGenerator;

import com.cisco.slingshot.R;


public class Ringtone{
	
	private final static String LOG_TAG = "Ringtone";
	
	public final static int RING_OUT = 1;
	public final static int RING_IN = 2;
	
	
	private static MediaPlayer _mp = null;
	private static ToneGenerator _mToneGenerator = null;
	
	/*
	public static void play(final Context ctx ,final int type){

		
		Thread playthread = new Thread(new Runnable(){

			@Override
			public void run() {
				
				Util.S_Log.d(LOG_TAG, "Ringtone start");
				stop();
				try{
				switch(type){
				case RING_OUT:
					_mp = MediaPlayer.create(ctx, R.raw.ring_out);
					_mp.setLooping(true);
					_mp.start();
					break;
				case RING_IN:
					_mp = MediaPlayer.create(ctx, R.raw.ring_in2);
					_mp.setLooping(true);
					_mp.start();
					
					break;
				}
				}catch(Exception e){
					Util.S_Log.e(LOG_TAG, "error happend: " + e.getMessage() );
				}
			}
			
		},"RingtoneThread");
		
		playthread.start();

	}
	*/
public static void play(final Context ctx ,final int type){

		
		Thread playthread = new Thread(new Runnable(){

			@Override
			public void run() {
				
				Util.S_Log.d(LOG_TAG, "Ringtone start");
				stop(ctx);
				
				try{
				switch(type){
				case RING_OUT:
					_mToneGenerator = new ToneGenerator(AudioManager.STREAM_VOICE_CALL,(int)(ToneGenerator.MAX_VOLUME));
					_mToneGenerator.startTone(ToneGenerator.TONE_SUP_RINGTONE);
					
					break;
				case RING_IN:
					_mToneGenerator = new ToneGenerator(AudioManager.STREAM_RING,(int)(ToneGenerator.MAX_VOLUME));
					_mToneGenerator.startTone(ToneGenerator.TONE_SUP_RINGTONE);
					break;
				}
				}catch(Exception e){
					Util.S_Log.e(LOG_TAG, "error happend: " + e.getMessage() );
				}
			}
			
		},"RingtoneThread");
		
		playthread.start();

	}
	public static void stop(final Context context){
		
		AudioManager am = (AudioManager)context.getSystemService(Context.AUDIO_SERVICE);
		
		if(_mp != null){
			_mp.stop();
			_mp.reset();
			_mp.release();
			_mp = null;
		}
		
		if(_mToneGenerator != null){
			_mToneGenerator.stopTone();
			_mToneGenerator.release();
			_mToneGenerator = null;
		}

	}
	
	
	
	
	
}