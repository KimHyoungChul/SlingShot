package com.cisco.slingshot.camera;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.preference.PreferenceManager;

import com.cisco.slingshot.R;
import com.cisco.slingshot.preference.IPreferenceUtil;
import com.cisco.slingshot.utils.Util;


public class CameraSettings implements IPreferenceUtil{
	
	public final static String LOG_TAG = "CameraSettings";
	
	
	
	//Video resolution
	public final static int VIDEO_RESOLUTION_480P = 1;
	public final static int VIDEO_RESOLUTION_720P = 2;
	
	//Video codec
	public final static int VIDEO_CODEC_H264 = 1;
	public final static int VIDEO_CODEC_MPEG4 = 2;
	
	//video bit rate
	public final static int VIDEO_BITRATE_LOW = 500*1000;
	public final static int VIDEO_BITRATE_MIDDLE = 1000*1000;
	public final static int VIDEO_BITRATE_HIGH = 1500*1000;
	public final static int VIDEO_BITRATE_SUPER = 2000*1000;
	
	//video frame rate
	public final static int VIDEO_FRAME_RATE_15 = 15;
	public final static int VIDEO_FRAME_RATE_30 = 30;
	
	
	private int 	mVideoResolution;
	private int 	mVideoCodec;
	private int 	mFrameRate;
	private int 	mVideoBitRate;
	private boolean isAutoFocus;
	
	//position
	private int mLeft;
	private int mTop;
	//private int mWidth;
	//private int mHeight;

	
	private Context mContext;
	private SharedPreferences sharedPrefs ;
	
	public CameraSettings(Context ctx){
		mContext = ctx;
		sharedPrefs = PreferenceManager.getDefaultSharedPreferences(ctx);
	}
	public int getVideoResolution(){
		return mVideoResolution;
	}
	
	public int getVideoCodec(){
		return mVideoCodec;
	}
	
	public int getFrameRate(){
		return mFrameRate;
	}
	
	public int getVideoBitsRate(){
		return mVideoBitRate;
	}
	
	public boolean isAutoFocus(){
		return isAutoFocus;
	}
	
	public int getLeft(){
		return mLeft;
	}
	public int getTop(){
		return mTop;
	}
	/*
	public int getWidth(){
		return mWidth;
	}
	public int getHeight(){
		return mHeight;
	}*/
	
	
	private void setVideoResolution(int vr){
		 mVideoResolution = vr;
	}
	
	private void setVideoCodec(int vc){
		 mVideoCodec = vc;
	}
	
	private void setVideoBitRate(int br){
		mVideoBitRate = br;
	}
	
	private void setFrameRateint (int fr){
		 mFrameRate = fr;
	}
	
	public void setLeft(int l){
		 mLeft = l;
	}
	public void setTop(int t){
		mTop = t;
	}
	/*
	public void setWidth(int w){
		mWidth = w;
	}
	public void setHeight(int h){
		mHeight = h;
	}*/

	@Override
	public void read() {
		Util.S_Log.d(LOG_TAG, "Start to read camera and recorder settings....");
    	
    	
    	//String video_codec 		= sharedPrefs.getString(mContext.getString(R.string.key_video_codec), 
    	//		mContext.getString(R.string.settings_video_codec_default));
    	String video_resolution = sharedPrefs.getString(mContext.getString(R.string.key_video_resolution), 
    			mContext.getString(R.string.settings_video_resolution_default));
    	String video_frameRate	= sharedPrefs.getString(mContext.getString(R.string.key_video_frame_rate), 
    			mContext.getString(R.string.settings_video_framerate_default));
    	
    	String video_bitRate 	= sharedPrefs.getString(mContext.getString(R.string.key_video_bit_rate), 
    			mContext.getString(R.string.settings_video_bitrate_default));

    	
    	Util.S_Log.d(LOG_TAG, "video_codec@h.264"  + ",video_resolution@" + video_resolution + ",Frame Rate@" + video_frameRate);
    	
    	isAutoFocus = sharedPrefs.getBoolean(mContext.getString(R.string.key_camera_auto_focus), 
    			true);    	
    	
    	mLeft = sharedPrefs.getInt(mContext.getString(R.string.key_camera_position_left), 
    			0);
    	if(mLeft < 0)
    		mLeft = 0;
    	
    	mTop = sharedPrefs.getInt(mContext.getString(R.string.key_camera_position_top), 
    			0);
    	if(mTop < 0)
    		mTop = 0;
    	
    	/*
    	mWidth = sharedPrefs.getInt(mContext.getString(R.string.key_camera_size_width), 
    			320);
    	
    	mHeight = sharedPrefs.getInt(mContext.getString(R.string.key_camera_size_height), 
    			240);
    			*/

    	//resolution
    	if(video_resolution.equals(mContext.getString(R.string.settings_video_resolution_480p))){
    		setVideoResolution(CameraSettings.VIDEO_RESOLUTION_480P);
    	}else if(video_resolution.equals(mContext.getString(R.string.settings_video_resolution_720p))){
    		setVideoResolution(CameraSettings.VIDEO_RESOLUTION_720P);
    	}
    	//codec
    	setVideoCodec(CameraSettings.VIDEO_CODEC_H264);
    	
    	//if(video_codec.equals(mContext.getString(R.string.settings_video_codec_h264))){ 
    	//	setVideoCodec(CameraSettings.VIDEO_CODEC_H264);
    	//}else if(video_codec.equals(mContext.getString(R.string.settings_video_codec_mpeg4))){
    	//	setVideoCodec(CameraSettings.VIDEO_CODEC_MPEG4);
    	//}
    	
    	//Bit rate
    	if(video_bitRate.equals(mContext.getString(R.string.settings_video_bitrate_low))){ 
    		setVideoBitRate(VIDEO_BITRATE_LOW);
    	}else if(video_bitRate.equals(mContext.getString(R.string.settings_video_bitrate_middle))){
    		setVideoBitRate(VIDEO_BITRATE_MIDDLE);
    	}else if(video_bitRate.equals(mContext.getString(R.string.settings_video_bitrate_high))){
    		setVideoBitRate(VIDEO_BITRATE_HIGH);
    	}else if(video_bitRate.equals(mContext.getString(R.string.settings_video_bitrate_super))){
    		setVideoBitRate(VIDEO_BITRATE_SUPER);
    	}
    	
    	//frame rate
    	if(video_frameRate.equals(mContext.getString(R.string.settings_video_framerate_15))){ 
    		setFrameRateint(VIDEO_FRAME_RATE_15);
    	}else if(video_frameRate.equals(mContext.getString(R.string.settings_video_framerate_30))){
    		setFrameRateint(VIDEO_FRAME_RATE_30);
    	}
    	
    	/*
    	try{
    		int frameRate;
    		frameRate = Integer.parseInt(video_frameRate);
    		if(frameRate<0){
    			frameRate = 0;
    		}
    		if(frameRate>60){
    			frameRate = 60;
    		}
    		setFrameRateint(frameRate);
    	}catch(NumberFormatException e){
    		Log.e(LOG_TAG, e.getMessage());
    		setFrameRateint(15);
    	}*/
    	

    	
    	Util.S_Log.d(LOG_TAG, "Reading settings done!");
		
	}

	@Override
	public void flush() {
		Editor prefsEditor = sharedPrefs.edit();
		prefsEditor.putInt(mContext.getString(R.string.key_camera_position_left), mLeft);
		prefsEditor.putInt(mContext.getString(R.string.key_camera_position_top), mTop);
		/*
		prefsEditor.putInt(mContext.getString(R.string.key_camera_size_width), mWidth);
		prefsEditor.putInt(mContext.getString(R.string.key_camera_size_height), mHeight);
		*/
		prefsEditor.apply();
		
	}
	

	
	
	
	
	
}