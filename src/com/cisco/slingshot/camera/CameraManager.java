package com.cisco.slingshot.camera;

import java.io.FileDescriptor;
import java.io.IOException;
import java.lang.ref.WeakReference;

import android.app.Activity;
import android.app.admin.DevicePolicyManager;
import android.content.Context;
import android.hardware.Camera;
import android.media.CamcorderProfile;
import android.media.MediaRecorder;
import android.util.Log;
import android.view.SurfaceHolder;

import com.cisco.slingshot.utils.Util;



public class CameraManager implements MediaRecorder.OnErrorListener{
	
	public final static String LOG_TAG = "CameraManager";
	
	private Context mContext = null;

	private Camera mCameraDevice;    //Camera hardware device
	private Camera.ErrorCallback mCameraErrorCallback = null;
	private MediaRecorder mCameraRecorder = null; 
    private boolean isRecording = false;
    private int mEncoderProfile = MediaRecorder.EncoderH264.ENCODER_H264ProfileBaseline;
    private int mEncoderLevel = MediaRecorder.EncoderH264.Encoder_H264Level31;
    
	private SurfaceHolder mHolder = null;
	private CameraSettings mSettings;
	
	private static CameraManager mInstance = null;
	
	public static synchronized CameraManager getInstance(Context context){
		if(mInstance == null){
			if(context == null){
	            throw new IllegalStateException("Creating CameraManager need a valid Context.");
			}
			mInstance = new CameraManager(context);
		}
		return mInstance;
	}
	
	private CameraManager(Context context){
		mContext = context;	
		mSettings = new CameraSettings(context);
	}
	
	public CameraSettings getSettings(){
		return mSettings;
	}
	
    @Override
    public void	 onError(MediaRecorder mr, int what, int extra) {
    	Log.e(LOG_TAG, "MediaRecorder::onError  error occur! what:" + what + "extra:" + extra);
    	stopRecordingInternal();
    	finishAndShowMessages((Activity)mContext,"Warning!! Camera Recorder crashed!!");
    }
    
	private void finishAndShowMessages(final Activity activiy, String message){
		activiy.finish();
		
	}
	
	public void setErrorCallback(Camera.ErrorCallback cb){
		mCameraErrorCallback = cb;
	}
		
	/**
	 * Associate a surface
	 * @param holder
	 */
    public void attachSurface(SurfaceHolder holder){
    	mHolder = holder;
    }
    
	/**
     * Open camera and start preview.
     * @throws RuntimeException throw when can't open camera
     * @throws IOException throw when can't start preview
     */
    public void openCamera()throws CameraDisabledException,CameraHardwareException{
    	openCameraInternal();	
    }
    
    /**
     * Close camera
     */
    public void closeCamera(){
    	if(mCameraDevice!=null){
    		mCameraDevice.stopPreview();
    		closeCameraInternal();
    	}
    }
    
    public void setEncoderProfileLevel(int profile , int level){
    	
    	mEncoderProfile = profile;
    	mEncoderLevel = level;
    	Util.S_Log.d(LOG_TAG, "THe Encoder profile is " + profile + " level is " + level);
    	return;
    }
    /**
     * Start recording to a FD
     * @param fd target FileDescriptor
     * @param needRecordAudio whether need record audio
     */
    public void startRecording(final FileDescriptor fd, final boolean needRecordAudio){    	
    	startRecordingInternal(fd,needRecordAudio);
    }
    
    /**
     * Stop recording
     */
    public void stopRecording(){
    	stopRecordingInternal();
    }
	

    private boolean openCameraInternal()throws CameraDisabledException,CameraHardwareException{
    	
    	Util.S_Log.d(LOG_TAG, "=============Start initializing Camera================");
    	if(mHolder == null){
    		Log.e(LOG_TAG, "Error:Surface holder for Camera is null!!");
    		return false;
    	}
    	
        DevicePolicyManager dpm = (DevicePolicyManager) mContext.getSystemService(
                Context.DEVICE_POLICY_SERVICE);
        if (dpm.getCameraDisabled(null)) {
            throw new CameraDisabledException();
        }
        
        
    	
    	//readSettings();
    	mSettings.read();
    	
    	//reset camera first
    	if(mCameraDevice!=null){
    		mCameraDevice.release();
    		mCameraDevice = null;
    	}
    	
    	int cameraCount = Camera.getNumberOfCameras();
    	
    	Util.S_Log.d(LOG_TAG, "Camera count: " + cameraCount);
    	try{
    		//mCameraDevice = Camera.open();
    		mCameraDevice = Camera.open(0);
    	}catch(RuntimeException e){
    		Log.e(LOG_TAG,e.getMessage());
    		throw new CameraHardwareException(e);
    	}
    	
    	if(mCameraErrorCallback!=null){
    		mCameraDevice.setErrorCallback(mCameraErrorCallback);
    	}
  
    	try{
    		mCameraDevice.setPreviewDisplay(mHolder);
		}catch(IOException e){
			Log.e(LOG_TAG, e.getMessage());
			throw new CameraHardwareException(e);
		}
    	
    	Camera.Parameters parameters = mCameraDevice.getParameters();

    	if(mSettings.getVideoResolution()==CameraSettings.VIDEO_RESOLUTION_480P){
    		parameters.setPreviewSize(640, 480);
    	}else if(mSettings.getVideoResolution()==CameraSettings.VIDEO_RESOLUTION_720P){
    		parameters.setPreviewSize(1280, 720);
    	}
    	
    	if(mSettings.isAutoFocus()){
    		parameters.setFocusMode(Camera.Parameters.FOCUS_MODE_CONTINUOUS_VIDEO);
    	}else{
    		parameters.setFocusMode(Camera.Parameters.FOCUS_MODE_INFINITY);
    	}
    	 
    	
    	parameters.setRecordingHint(true);
    	parameters.setPreviewFrameRate(mSettings.getFrameRate());
    	mCameraDevice.setParameters(parameters);
    	mCameraDevice.startPreview();
		
    	return true;
    }	
    
    /**
     * Set up Media recorder
     * @param fd
     * @param needRecordAudio
     */
    
    private void setupMediaRecoder(final FileDescriptor fd, boolean needRecordAudio)throws IllegalStateException,IOException{
    	
    	Util.S_Log.d(LOG_TAG, "==============Start initializing Camera recorder================");
    	Util.S_Log.d(LOG_TAG, "Enter >>>> fd = " + fd);
    	
    	
    	//WeakReference<FileDescriptor> tmpFd = new WeakReference<FileDescriptor>(fd);
    	
    	
    	if(mCameraDevice == null){
    		Util.S_Log.d(LOG_TAG, "Camera device has not been opened successfully");
    		throw new IOException("Camera device not available!");
    	}
    	
    	if(mCameraRecorder == null){
    		mCameraRecorder = new MediaRecorder();
    	}else{
    		mCameraRecorder.reset();
    	}
    	
    	
    	// Step 1: Unlock and set camera to MediaRecorder
    	Util.S_Log.d(LOG_TAG, "step 1 >>>> fd = " + fd);
    	mCameraDevice.unlock();
    	mCameraRecorder.setCamera(mCameraDevice); 	
    	
    	// Step 2: Set sources
    	Util.S_Log.d(LOG_TAG, "step 2 >>>> fd = " + fd);
    	if(needRecordAudio){
    		mCameraRecorder.setAudioSource(MediaRecorder.AudioSource.CAMCORDER);
    	}
    	mCameraRecorder.setVideoSource(MediaRecorder.VideoSource.CAMERA);
  
        // Step 3: Set a CamcorderProfile (requires API Level 8 or higher)
    	Util.S_Log.d(LOG_TAG, "step 3 >>>> fd = " + fd);
    	CamcorderProfile profile = null;

    	if(mSettings.getVideoCodec() == CameraSettings.VIDEO_CODEC_H264){
        	if(mSettings.getVideoResolution() == CameraSettings.VIDEO_RESOLUTION_480P){
        		profile = CamcorderProfile.get(0,CamcorderProfile.QUALITY_TIME_LAPSE_480P);
            	profile.videoFrameWidth = 640;
            	profile.videoFrameHeight = 480;
        		
            	/*
        		if(mEncoderLevel <= MediaRecorder.EncoderH264.Encoder_H264Level21){
                	profile.videoFrameWidth = 352;
                	profile.videoFrameHeight = 288;
                } else {
                	profile.videoFrameWidth = 640;
                	profile.videoFrameHeight = 480;
                }
                */
                
        	}else if(mSettings.getVideoResolution() == CameraSettings.VIDEO_RESOLUTION_720P){
        		profile = CamcorderProfile.get(0,CamcorderProfile.QUALITY_TIME_LAPSE_480P);
            	profile.videoFrameWidth = 1280;
            	profile.videoFrameHeight = 720;
        		
            	/*
        		if(mEncoderLevel <= MediaRecorder.EncoderH264.Encoder_H264Level21){
                	profile.videoFrameWidth = 352;
                	profile.videoFrameHeight = 288;
                } else if(mEncoderLevel <= MediaRecorder.EncoderH264.Encoder_H264Level3){
                	profile.videoFrameWidth = 640;
                	profile.videoFrameHeight = 480;
                } else {
                	profile.videoFrameWidth = 1280;
                	profile.videoFrameHeight = 720;
                }
                */
                
        	}
        	profile.videoCodec = MediaRecorder.VideoEncoder.H264;  
    	}else if(mSettings.getVideoCodec() == CameraSettings.VIDEO_CODEC_MPEG4){
    		if(mSettings.getVideoResolution() == CameraSettings.VIDEO_RESOLUTION_480P){
        		profile = CamcorderProfile.get(0,CamcorderProfile.QUALITY_TIME_LAPSE_480P);
        	}else if(mSettings.getVideoResolution() == CameraSettings.VIDEO_RESOLUTION_720P){
        		profile = CamcorderProfile.get(0,CamcorderProfile.QUALITY_TIME_LAPSE_720P);
        	}
    		profile.videoCodec = MediaRecorder.VideoEncoder.MPEG_4_SP; 
    	}
    	//updatePreviewSize(profile.videoFrameWidth,profile.videoFrameHeight);
    	
    	/* frame rate and bit rate */
    	profile.videoBitRate = mSettings.getVideoBitsRate();
        profile.videoFrameRate = mSettings.getFrameRate();
        
		/*File format*/
		profile.fileFormat = MediaRecorder.OutputFormat.OUTPUT_FORMAT_RTP_AVP;
    	mCameraRecorder.setProfile(profile);

    	/*set Video Encoder Profile and Level*/
    	//Util.S_Log.d(LOG_TAG,"===The Profile is "+ mEncoderProfile + "Level is "+mEncoderLevel);
    	//mCameraRecorder.setVideoEncoderProfile(mEncoderProfile);
    	//mCameraRecorder.setVideoEncoderLevel(mEncoderLevel);

        // Step 4: Set output file
    	Util.S_Log.d(LOG_TAG, "step 4 >>>> fd = " + fd);

    	mCameraRecorder.setOutputFile(fd);
    	
        // Step 5: Set the preview output
    	mCameraRecorder.setPreviewDisplay(mHolder.getSurface());
        
    	// Step 6: Prepare configured MediaRecorder
        try {
        	mCameraRecorder.prepare();
        } catch (IllegalStateException e) {
            Util.S_Log.d(LOG_TAG, "IllegalStateException preparing MediaRecorder: " + e.getMessage());
            releaseCameraRecorder();
            mCameraDevice.lock();
            throw e;

            
        } catch (IOException e) {
            Util.S_Log.d(LOG_TAG, "IOException preparing MediaRecorder: " + e.getMessage());
            releaseCameraRecorder();
            mCameraDevice.lock();
            throw e;
        }    	
        
		mCameraRecorder.setOnErrorListener(CameraManager.this);
 	
    }
    
    private void updatePreviewSize(int width, int height){
    	/*Stop first*/
		mCameraDevice.stopPreview();
		
    	Camera.Parameters parameters = mCameraDevice.getParameters();
		parameters.setPreviewSize(width, height);
		mCameraDevice.setParameters(parameters);
		/*start preview*/
    	mCameraDevice.startPreview();
    }
    
	
    
    private void startRecordingInternal(final FileDescriptor fd, final boolean needRecordAudio){
    	Util.S_Log.d(LOG_TAG, "=============Start recording to FileDescriptor:" + fd + "=============");
  	
		//new Thread(new Runnable(){
    	//	@Override
    	//	public void  run(){
				
				try{
					setupMediaRecoder(fd,needRecordAudio);
					Util.S_Log.d(LOG_TAG, "Start recording...");
					mCameraRecorder.start();
					isRecording = true;
				}catch(Exception e){
					Log.e(LOG_TAG, "startRecording(),Failed: " + e.getMessage());
					stopRecordingInternal();
					isRecording = false;
				}
    	//	}	
		//}).start();
		
	}
   
    
    /**
     * Record video to local file(For test)
     * @param path
     */
    private void startRecordingInternal(final String path) {
    	Util.S_Log.d(LOG_TAG, "start initializing Camera recorder...");
    	
		if(mCameraRecorder == null){
    		mCameraRecorder = new MediaRecorder();
    	}else{
    		mCameraRecorder.reset();
    	}

    	// Step 1: Unlock and set camera to MediaRecorder
		mCameraDevice.unlock();
    	mCameraRecorder.setCamera(mCameraDevice); 	

    	// Step 2: Set sources
    	//mCameraRecorder.setAudioSource(MediaRecorder.AudioSource.CAMCORDER);
    	mCameraRecorder.setVideoSource(MediaRecorder.VideoSource.CAMERA);
  
    	// Step 3: Set a CamcorderProfile (requires API Level 8 or higher)
    	CamcorderProfile profile = null;
    	if(mSettings.getVideoResolution() == CameraSettings.VIDEO_RESOLUTION_480P){
    		profile = CamcorderProfile.get(0,CamcorderProfile.QUALITY_TIME_LAPSE_480P);
            profile.videoFrameWidth = 640;
            profile.videoFrameHeight = 480;
    	}else if(mSettings.getVideoResolution() == CameraSettings.VIDEO_RESOLUTION_720P){
    		profile = CamcorderProfile.get(0,CamcorderProfile.QUALITY_TIME_LAPSE_480P);
            profile.videoFrameWidth = 1280;
            profile.videoFrameHeight = 720;
    	}
    	if(mSettings.getVideoCodec() == CameraSettings.VIDEO_CODEC_H264){
    		profile.videoCodec = MediaRecorder.VideoEncoder.H264;   
    	}else if(mSettings.getVideoCodec() == CameraSettings.VIDEO_RESOLUTION_720P){
    		profile.videoCodec = MediaRecorder.VideoEncoder.MPEG_4_SP; 
    	}
        
    	
        profile.videoFrameRate = mSettings.getFrameRate();
        profile.fileFormat = 7;
    	mCameraRecorder.setProfile(profile);

    	/*set Video Encoder Profile and Level*/
    	Util.S_Log.d(LOG_TAG,"The Profile is "+ mEncoderProfile + "Level is "+mEncoderLevel);
    	mCameraRecorder.setVideoEncoderProfile(mEncoderProfile);
    	mCameraRecorder.setVideoEncoderLevel(mEncoderLevel);

        // Step 4: Set output file
    	mCameraRecorder.setOutputFile(path);
    	
        // Step 5: Set the preview output
    	mCameraRecorder.setPreviewDisplay(mHolder.getSurface());
        
    	// Step 6: Prepare configured MediaRecorder
        try {
        	mCameraRecorder.prepare();
        } catch (IllegalStateException e) {
            Util.S_Log.d(LOG_TAG, "IllegalStateException preparing MediaRecorder: " + e.getMessage());
            releaseCameraRecorder();
        } catch (IOException e) {
            Util.S_Log.d(LOG_TAG, "IOException preparing MediaRecorder: " + e.getMessage());
            releaseCameraRecorder();
        }    	
		
		mCameraRecorder.setOnErrorListener(CameraManager.this);
		Util.S_Log.d(LOG_TAG, "Start recording...");
		mCameraRecorder.start();
		isRecording = true;
    }
            
    private void stopRecordingInternal(){
    	Util.S_Log.d(LOG_TAG, "=====================Stop recording! =====================");
    	if(isRecording  && mCameraRecorder!=null){
        	mCameraRecorder.setOnErrorListener(null);
        	mCameraRecorder.stop();
    		isRecording = false;
    	}
    	releaseCameraRecorder();
    }
    
    private void releaseCameraRecorder(){
    	Util.S_Log.d(LOG_TAG, "=====================Release camera recorder!=============");
    	if(mCameraRecorder!=null){
    		mCameraRecorder.reset();
    		mCameraRecorder.release();
    		mCameraRecorder = null;
    	}

    }    
    
    
    private void closeCameraInternal(){
    	Util.S_Log.d(LOG_TAG, "=====================Close camera!========================");
        if (mCameraDevice != null){
        	mCameraDevice.release();        // release the camera for other applications
        	mCameraDevice = null;
        }
    }
    
    public void muteRecordingByChangingCameraDevices(boolean toMute){
    	if(mCameraDevice!=null){
    		if(toMute)
    			mCameraDevice.sendBackGroudPictureCmd(Camera.CAMERA_CMD_START_BACKGROUND_PICTURE);
    		else
    			mCameraDevice.sendBackGroudPictureCmd(Camera.CAMERA_CMD_STOP_BACKGROUND_PICTURE);
    	}
    }


}