package com.cisco.slingshot.ui;

import java.io.FileDescriptor;
import java.io.IOException;
import java.net.InetAddress;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.hardware.Camera;
import android.media.MediaPlayer;
import android.net.Uri;
import android.net.sip.SipErrorCode;
import android.net.sip.SipException;
import android.net.sip.SipManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;
import android.os.Message;
import android.preference.PreferenceManager;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.widget.AbsoluteLayout;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.cisco.slingshot.R;
import com.cisco.slingshot.activity.InCallActivity;
import com.cisco.slingshot.call.CallManager;
import com.cisco.slingshot.call.CallStatusObserver;
import com.cisco.slingshot.camera.CameraDisabledException;
import com.cisco.slingshot.camera.CameraHardwareException;
import com.cisco.slingshot.camera.CameraManager;
import com.cisco.slingshot.camera.CameraSettings;
import com.cisco.slingshot.contact.Contact;
import com.cisco.slingshot.contact.ContactDatabase;
import com.cisco.slingshot.net.rtp.test.RtpVideo;
import com.cisco.slingshot.net.sip.SipConfCall;
import com.cisco.slingshot.receiver.IncomingcallListener;
import com.cisco.slingshot.ui.statistic.StatisticAdapter;
import com.cisco.slingshot.ui.statistic.StatisticView;
import com.cisco.slingshot.ui.widget.ControlPanel;
import com.cisco.slingshot.ui.widget.VolumeBar;
import com.cisco.slingshot.utils.Ringtone;
import com.cisco.slingshot.utils.ServiceToast;
import com.cisco.slingshot.utils.TimeoutTimer;
import com.cisco.slingshot.utils.Util;



public class InCallView extends FrameLayout 	
	implements  Camera.ErrorCallback,
				MediaPlayer.OnErrorListener, 
				MediaPlayer.OnCompletionListener ,
				MediaPlayer.OnBufferingUpdateListener,
				View.OnClickListener,
				IncomingcallListener
	{
	
	private final static String LOG_TAG = "InCallView";
	
	public final static int INCOMING_CALL = 1;
	public final static int OUT_GOING = 2;
	
	
	/*for local video test*/
	public  boolean ENABLE_TEST_MODE_VIDEOTOFD;
	public  RtpVideo mLocalVideo;
	
	private Context mContext;
	private InCallActivity mActivityContainer ;
	private RelativeLayout _rootView;
	
    /*Main Frame*/
    //private RelativeLayout mMainFrame = null;
	
	/*Camera*/
	private AbsoluteLayout  mCameraLayer = null;
    private CameraFrame     mCameraFrame = null;
	private SurfaceView 	mCameraView = null;
	private SurfaceHolder 	mCameraHolder = null;
	private CameraManager 	mCamaraManager;
	private boolean  isErrorHappened = false;
	
	/*Video*/
	private SurfaceView  					mVideoFrame = null;
	private SurfaceHolder 					mVideoHolder = null;
	private MediaPlayer 					mMediaPlayer;
	private MediaPlayer.OnPreparedListener 	mPreparedListener;
	private LinearLayout 					mVideoReplacement = null;
	private boolean 						isPlaying = false;
	
	/*Control panel*/
    private ControlPanel 	mControlPanel = null;
    private boolean 		mIsControlPanelVisible = true;
    
    /*Volume Bar*/
    private VolumeBar       mVolumeBar = null;
	private TimeoutTimer mAutoHideTimer;
	final int VOLUMEBAR_DURATION_TIME = 3; //seconds
    
    /*Statistic View*/
    private StatisticView   mStatisticView = null;

    /*Call object*/
    private CallManager 		mCallManager = null;
    private ArrayList<CallStatusObserver> mCallStatusObservers = null;
    
    private CallViewProxy        		mCallViewProxy = null;    
    private CallViewProxy.CallStatus 	mCallStatus= null;
    
    private SipConfCall 			mCall = null;
    private SipConfCall.Listener 	mMainCallListener = null;
    
    
    private SipConfCall				mReplacedCall = null;
    private SipConfCall.Listener    mReplacedCallListener = null;
    
    
    
    //private Dialog 				mOutgoingDialog = null;
    
     
    /*Handle ui thread to run some UI tasks*/
    private UiHandler mUiHandler = new UiHandler();
    public static final int FINISH = 0; //finish call
    public static final int VIDEO_CHANGED = 1; 
    public static final int UPDATE_STATUS = 2; //update status bar text
    public static final int PIP_WINDOW_CHANGED = 3;
    public static final int CAMERA_ERROR  = 4;
    public static final int NETWORK_ERROR = 5;
    public static final int HIDE_VOLUMEBAR 	  = 6;
    
    /*Handle call in worker thread*/
    private HandlerThread mCallThread; 
    private CallHandler   mCallHandler;
    public static final int SATRT_CALL = 0; //finish call
    public static final int END_CALL = 1; //finish call
	public static final int EXJABBER_END = 100;
	
	/*sync media pipline*/
    private Object mMediaLock = new Object();
    private boolean isStoppingCall = false;
    
    private class UiHandler extends Handler{

        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {

                case FINISH:
    				try{
    					int reason = msg.arg1;
    					new EndVideoAndFinishTask(false,reason).execute();
    				}catch(Exception e){
    					e.printStackTrace();
    				}
                    break;
                    
                case CAMERA_ERROR:
                	showCameraErrorAndFinish((Activity)mContext,R.string.camera_error_dialog_connection);
                	//Log.e(LOG_TAG, "Cam error!!");
                	break;
                	
                case NETWORK_ERROR:
                	showNetworkErrorAndFinish((Activity)mContext,R.string.network_error_dialog_message);
                	break;
                                 	
                case UPDATE_STATUS: {
                	String status = (String)msg.obj;
                    TextView labelView = (TextView) findViewById(R.id.sipLabel);
                    labelView.setText(status);
                    labelView.invalidate();
                    labelView.requestLayout();
                    break;
                }
                case EXJABBER_END:
                {
                	//btnEndCall.performClick();
                	mControlPanel.extEndCall();
                }
                	break; 
                
                case VIDEO_CHANGED:
                	hideVideoReplacement(isPlaying);
                	break;
                case PIP_WINDOW_CHANGED:
                	break;
                	
                case HIDE_VOLUMEBAR:
                	mVolumeBar.setAnimation(AnimationUtils.loadAnimation(mContext,
                android.R.anim.fade_out));
                	mVolumeBar.setVisibility(View.INVISIBLE);
    				break;
    				
                default:
                    Log.v(LOG_TAG, "Unhandled message: " + msg.what);
                    break;
            }
        }    	
    }
    
    private class CallHandler extends Handler{
    	
    	public CallHandler(Looper looper){
    		super(looper);
    	}
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                    
                case SATRT_CALL:
                	
                	dealWithCallInternal();
                	
                	break;
                    	
                default:
                    Log.v(LOG_TAG, "Unhandled message: " + msg.what);
                    break;
            }
        }    	
    }
	

	public InCallView(Context context) {
		super(context);
		mContext = context;
		initView();
	}
	
	public InCallView(Context context, AttributeSet attrs) {
		super(context, attrs);
		mContext = context;
		initView();
	}


	public InCallView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		mContext = context;
		initView();
	}

		
	private void initView(){
		exHandler = mUiHandler;
    	Util.S_Log.d(LOG_TAG, "exHandler = " + exHandler );
    	
 
		mActivityContainer = (InCallActivity)mContext;
		/*Read running mode first, common or test*/
        setupRunningMode();		
        
        /*init worker thread*/
        initCallThread();
        
        mCallStatusObservers = new ArrayList<CallStatusObserver>();
        /*Load child view as root */
		_rootView = (RelativeLayout)LayoutInflater.from(mContext).inflate(R.layout.incallview, null);
        this.addView(_rootView);

        
        /*CallView Proxy*/
        initCallViewProxy();
        
        
        /* Main Frame*/
        _rootView = (RelativeLayout)findViewById(R.id.mainFrame);
        _rootView.setOnClickListener(this);
        
        /* Camera */
        mCameraLayer = (AbsoluteLayout)findViewById(R.id.camera_layer);
        initCameraLayer();


        /* Video */
        mVideoFrame = (SurfaceView)findViewById(R.id.video_frame);
        initVideoFrame();
       
        /* Controller */
    	mControlPanel = (ControlPanel)findViewById(R.id.control_panel);
    	mControlPanel.setCallViewProxy(mCallViewProxy);
        
        /* Volume Bar */
        mVolumeBar =  (VolumeBar)findViewById(R.id.volume_bar);
        mAutoHideTimer = new TimeoutTimer("VolumeBarHintAutoHideTimer",new TimeoutTimer.TimeoutCallback() {
			
			@Override
			public void onTimeout() {
				if(!mVolumeBar.isMuted())
					hideVolumeBarInUI();
			}
		});
        if(mVolumeBar.isMuted())
        	mVolumeBar.setVisibility(View.VISIBLE);
        /* Statistic View*/
        mStatisticView = (StatisticView)findViewById(R.id.stat_view);
        initStatisticView();
        
        /*Call object*/
        mCallManager = CallManager.getInstance(mContext);
        addCallStatusObserver(mCallManager);
        this.postCallStartEvent();
        
        
        /*Sip Error Code*/
        initSipErrInfoMap();

        
	}
	
	private void initCallThread(){
        //mCallThread = new HandlerThread("CallThread");
       // mCallThread.start();
        //mCallHandler = new CallHandler(mCallThread.getLooper());
	}
	
	private void initCallViewProxy(){
		
		//Create status object
        mCallStatus = new CallViewProxy.CallStatus();
        mCallStatus.isIdle = false;
        //mCallStatus.isAudioMuted = false;
        //mCallStatus.isVideoMuted = false;
        mCallStatus.isSpeekerOn  = true;
        mCallStatus.isRecorderOn = true;
        mCallStatus.isPause = false;
        mCallStatus.isLocalHiden = false;
        
        //create proxy object
        mCallViewProxy = new CallViewProxy(){

			@Override
			public CallStatus requestCallStatus() {
				return mCallStatus;
			}
			
			@Override 
			public  void muteVoice(boolean option){
				//InCallView.this.mCall.setSpeakerMode(option);
				if(InCallView.this.mCall != null)
				InCallView.this.mCall.toggleMute();
			};
			@Override 
			public  void muteVideo(boolean option){
				//super.muteVideo(option);
				//TODO mute video
				mCamaraManager.muteRecordingByChangingCameraDevices(option);
				
			};
			@Override
			public  void pauseCall(boolean option){
				if(option)InCallView.this.holdCall(mCall);
				else InCallView.this.continueCall(mCall);
			}
			@Override
			public void hideLocal(boolean option){
				hidePipWindow(option);
			}
			
			@Override
			public void showStatistic(boolean option){
				showStatisticView(option);
			}
			@Override
			public  void endCall(){
				InCallView.this.endCall();
			}
			

			
        	
        };
        
	}
	
	private void initCameraLayer(){
		mCameraFrame = (CameraFrame)LayoutInflater.from(mContext).inflate(R.layout.camera_frame, null);
		mCameraView = (SurfaceView)mCameraFrame.findViewById(R.id.camera_preview);
		
		SurfaceHolder cameraHolder = mCameraView.getHolder();
        cameraHolder.addCallback(new SurfaceHolder.Callback(){
			@Override
			public void surfaceCreated(SurfaceHolder holder) {
				Util.S_Log.d(LOG_TAG, "CameraView:surfaceCreated");
				mCameraHolder = holder;
				mCamaraManager = CameraManager.getInstance(mContext);
				mCamaraManager.setErrorCallback(InCallView.this);
				mCamaraManager.attachSurface(mCameraHolder);
				
				/*Start camera preview in worker thread*/
				
				try{	
					mCamaraManager.openCamera();
				}catch (CameraDisabledException e){
					e.printStackTrace();	
					postCameraError();
					//showErrorAndFinish((Activity)mContext,R.string.camera_error_dialog_connection);
					return;
				} catch (CameraHardwareException e) {
					e.printStackTrace();
					postCameraError();
					//showErrorAndFinish((Activity)mContext,R.string.camera_error_dialog_connection);
					return;
				}
		            
				
				//we start to deal with the call after surface holder was created
				dealWithCallAfterCameraSurfaceCreated();
			}

			@Override
			public void surfaceChanged(SurfaceHolder holder, int format,
					int width, int height) {}

			@Override
			public void surfaceDestroyed(SurfaceHolder holder) {
				Util.S_Log.d(LOG_TAG, "CameraView:surfaceDestroyed");
				if(mCamaraManager!=null){
					mCamaraManager.closeCamera();
				}		     
			}
        });       
        
        //read saved camera layout info
		CameraSettings cameara_settings = CameraManager.getInstance(mContext).getSettings();
		cameara_settings.read();
		
		int width = 320,height = 240;
    	if(cameara_settings.getVideoResolution()==CameraSettings.VIDEO_RESOLUTION_480P){
    		width = 320;
    		height = 240;
    	}else if(cameara_settings.getVideoResolution()==CameraSettings.VIDEO_RESOLUTION_720P){
    		width = 320;
    		height = 180;
    	}
		Util.S_Log.d(LOG_TAG, width+ "," + height+"," + cameara_settings.getLeft() +","+ cameara_settings.getTop());
		mCameraLayer.addView(
        		mCameraFrame, 
        		new AbsoluteLayout.LayoutParams(width, 
        										height, 
        										cameara_settings.getLeft(), 
        										cameara_settings.getTop())
        );
	}
	
	
    
	
	private void initVideoFrame(){
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
        
        mVideoReplacement = (LinearLayout)findViewById(R.id.video_replacement);
        mVideoReplacement.setAnimation(AnimationUtils.loadAnimation(mContext, R.anim.anim_alpha));
	}
	
	private void initStatisticView(){
		mStatisticView.setStatisticAdapter(new StatisticAdapter(mContext){
			@Override
			public String getDataXMLPath() {
				return "/tmp/stat.xml";
			}

			@Override
			public int getVideoHeight() {
				if(mMediaPlayer != null)
					return mMediaPlayer.getVideoHeight();
				else
					return 0;
			}

			@Override
			public int getVideoWidth() {
				if(mMediaPlayer != null)
					return mMediaPlayer.getVideoWidth();
				else
					return 0;
			}
			
		});
	}
	
	
	/*We don't start a call until  Camera Surface is created */
	private void dealWithCallAfterCameraSurfaceCreated(){
		/*Check network first*/
		
		if(!Util.Network.isConnect(mContext) || !mCallManager.isOnline()){
			postNetworkError();
			return;
		}
		
		if(Util.MathineStatus.isStandby()){
			InCallView.this.endCall();
			return;
		}
		
		
		
		dealWithCallInternal();
		//mCallHandler.removeMessages(SATRT_CALL);
		//mCallHandler.sendMessage(mCallHandler.obtainMessage(SATRT_CALL));
	}
	
	
	private void dealWithCallInternal(){
		
		mMainCallListener = createCallListener();
	
		Activity activity = (Activity)mContext;
        Intent intent = activity.getIntent();
        if(intent.getAction().equals(InCallActivity.ACTION_CALL_OUTGOING)){
        	Bundle bundle = intent.getBundleExtra("outgoing_call");
        	String name = bundle.getString(mContext.getString(R.string.str_contact_name));
        	String addr = bundle.getString(mContext.getString(R.string.str_contact_addr));
        	mCall = initiateCall(addr,mMainCallListener);
        }else if(intent.getAction().equals(InCallActivity.ACTION_CALL_ANDROID)){
        	Uri uri= intent.getData();
        	Util.S_Log.d(LOG_TAG, "Scheme:" + uri.getScheme() + ",content:" + uri.getSchemeSpecificPart() );
        	String addr = uri.getSchemeSpecificPart();
        	mCall = initiateCall(addr,mMainCallListener);
        }
        else if(intent.getAction().equals(InCallActivity.ACTION_CALL_INCOMING)){
        	/*Read the incoming call intent saved in the singleton CallManager */
        	Intent incomingcallIntent = mCallManager.getIncomingCallIntent();
        	mCall = answerCall(incomingcallIntent,mMainCallListener);
        }    
	}		
	
	private SipConfCall.Listener createCallListener(){
		//return new SipConfCall.Listener(){
		return new MySipCallListener(){
        	@Override
    		public void onCalling(SipConfCall call) {
        		Util.S_Log.d(LOG_TAG, "onCalling...");
        		updateStatus(mContext.getString(R.string.call_status_calling));
        		 
        		Ringtone.play(mContext, Ringtone.RING_OUT);
				mCallStatus.isIdle = false;
				//updateControlPanelStatus();
    		}
            @Override
            public void onCallEstablished(SipConfCall call) {
            	Util.S_Log.d(LOG_TAG, "onCallEstablished...");
            	Ringtone.stop(mContext);
            	//TODO Need add something to deal with Holding the call
            	if(isEstablished)
            		return;
            	isEstablished = true;
            	updateStatus(mContext.getString(R.string.call_status_established));
            	//Start  Audio
                call.startAudio();
                call.setSpeakerMode(true);
                
                //Start Video
    			if(startBidiVideoTransfer(call)){
    				updateStatus(call);
    			}else{
    				updateStatus(mContext.getString(R.string.call_status_audio_only));
    			}
    			
    			//mStatisticView.startUpdate();
    			
            }

            @Override
            public void onCallEnded(SipConfCall call) {
            	Util.S_Log.d(LOG_TAG, "onCallEnded...");
            	Ringtone.stop(mContext);
            	isEstablished = false;
            	updateStatus(mContext.getString(R.string.call_status_end));
            	tryFinishing(EndVideoAndFinishTask.REASON_END);
            }
            @Override
    		public void onCallBusy(SipConfCall call) {
            	Util.S_Log.d(LOG_TAG, "onCallBusy...");
            	Ringtone.stop(mContext);
            	updateStatus(mContext.getString(R.string.call_status_busy));
            	tryFinishing(EndVideoAndFinishTask.REASON_BUSY);
    		}
            
            @Override
    		public void onError(SipConfCall call, int errorCode, String errorMessage) {
            	Util.S_Log.d(LOG_TAG, "onError---errorCode:" + errorCode + ",errorMessage:" + errorMessage );
            	isEstablished = false;
            	Ringtone.stop(mContext);
            	String errorInfo = mSipErrInfoMap.get(errorCode);
            	if(errorInfo != null)ServiceToast.showMassage(mContext, mSipErrInfoMap.get(errorCode));
    			tryFinishing(EndVideoAndFinishTask.REASON_ERROR);
    		}
            
            @Override
    		public void onCallHeld(SipConfCall call) {
            	Util.S_Log.d(LOG_TAG, "onCallHeld...");
            	mCallStatus.isPause = true;
            	updateStatus("Pause");
            	//updateControlPanelStatus();
    		}
    	};
	}
	
	private Map<Integer, String> mSipErrInfoMap = new HashMap<Integer, String>();
	
	private void initSipErrInfoMap(){
		mSipErrInfoMap.put(SipErrorCode.CLIENT_ERROR, (String)mContext.getText(R.string.sip_error_des_CLIENT_ERROR));
		mSipErrInfoMap.put(SipErrorCode.CROSS_DOMAIN_AUTHENTICATION,(String)mContext.getText(R.string.sip_error_des_CROSS_DOMAIN_AUTHENTICATION));
		mSipErrInfoMap.put(SipErrorCode.DATA_CONNECTION_LOST, (String)mContext.getText(R.string.sip_error_des_DATA_CONNECTION_LOST));
		mSipErrInfoMap.put(SipErrorCode.INVALID_CREDENTIALS, (String)mContext.getText(R.string.sip_error_des_INVALID_CREDENTIALS));
		mSipErrInfoMap.put(SipErrorCode.INVALID_REMOTE_URI, (String)mContext.getText(R.string.sip_error_des_INVALID_REMOTE_URI));
		mSipErrInfoMap.put(SipErrorCode.IN_PROGRESS, (String)mContext.getText(R.string.sip_error_des_IN_PROGRESS));
		mSipErrInfoMap.put(SipErrorCode.NO_ERROR, (String)mContext.getText(R.string.sip_error_des_NO_ERROR));
		mSipErrInfoMap.put(SipErrorCode.PEER_NOT_REACHABLE, (String)mContext.getText(R.string.sip_error_des_PEER_NOT_REACHABLE));
		mSipErrInfoMap.put(SipErrorCode.SERVER_ERROR, (String)mContext.getText(R.string.sip_error_des_SERVER_ERROR));
		mSipErrInfoMap.put(SipErrorCode.SERVER_UNREACHABLE, (String)mContext.getText(R.string.sip_error_des_SERVER_UNREACHABLE));
		mSipErrInfoMap.put(SipErrorCode.SOCKET_ERROR, (String)mContext.getText(R.string.sip_error_des_SOCKET_ERROR));
		mSipErrInfoMap.put(SipErrorCode.TIME_OUT, (String)mContext.getText(R.string.sip_error_des_TIME_OUT));
		mSipErrInfoMap.put(SipErrorCode.TRANSACTION_TERMINTED, (String)mContext.getText(R.string.sip_error_des_TRANSACTION_TERMINTED));
	}
	
	
	
	
	

    private void setupRunningMode(){
    	ENABLE_TEST_MODE_VIDEOTOFD = PreferenceManager.getDefaultSharedPreferences(mContext)
		.getBoolean(
				mContext.getString(R.string.key_local_video_test), 
				false
		);
    }
    
	private void postCameraError(){
		mUiHandler.sendEmptyMessage(CAMERA_ERROR);
	}
	
	private void postNetworkError(){
		mUiHandler.sendEmptyMessage(NETWORK_ERROR);
	}
	
    public void addCallStatusObserver(CallStatusObserver observer){
    	mCallStatusObservers.add(observer);
    	Util.S_Log.d(LOG_TAG, "Adding new observer; Total:" + mCallStatusObservers.size());
    }
    
    public void removeCallStatusObserver(CallStatusObserver observer){
    	mCallStatusObservers.remove(observer);
    	Util.S_Log.d(LOG_TAG, "Removing observer; Total:" + mCallStatusObservers.size());
    }
    

	@Override
	public void onClick(View v) {
    	mControlPanel.setVisibility(mIsControlPanelVisible?View.INVISIBLE:View.VISIBLE);
    	mIsControlPanelVisible = !mIsControlPanelVisible;
	}

	@Override
	public void onCompletion(MediaPlayer mp) {
    	Log.e(LOG_TAG, "=========MediaPlayer: end!=========");
    	isPlaying  = false;
    	postVideoChanged();
	}

	@Override
	public boolean onError(MediaPlayer mp, int what, int extra) {
    	Log.e(LOG_TAG, "=========MediaPlayer: error occur!what:" + what +"extra:" + extra + "=============");
    	return true;
	}	
	
	@Override
	public void onError(int error, Camera camera) {
		Util.S_Log.d(LOG_TAG, "========= Camera error: " + error + " ===============");
		if(error == Camera.CAMERA_ERROR_SERVER_DIED )
			return;
		//Only deal with camera hot plug error here
		if(isErrorHappened){
			Log.i(LOG_TAG, "Error is happened, in proccesing");
			return;
		}
		isErrorHappened = true;
		postCameraError();
	}
	
	@Override
	public void onBufferingUpdate(MediaPlayer mp, int percent) {
		Util.S_Log.d(LOG_TAG, "percent");
		//ServiceToast.showMassage(mContext, "Percent:"+percent);
		
	}

	
	/**
     *  Make an outgoing call.
     * @param address Outgoing call address
     */
    public SipConfCall initiateCall(final String address, SipConfCall.Listener listener) {

        updateStatus(address);
    	Util.S_Log.d(LOG_TAG, "Making a outgoing call...");
    	
    	
		if(!SipManager.isApiSupported(mContext)){
			Log.e(LOG_TAG, "Device don't surpport sip, call failed!");
			return null;
		}
		
        try {
        	
            /*check the video resolution setting firstly*/
			SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(mContext);
			String video_resolution = sharedPrefs.getString("video_resolution", mContext.getString(R.string.settings_video_resolution_default));

            if(video_resolution.equals(mContext.getString(R.string.settings_video_resolution_480p))){
            	SipConfCall.setVideoResolution(SipConfCall.V_480P);
            }else if(video_resolution.equals(mContext.getString(R.string.settings_video_resolution_720p))){
            	SipConfCall.setVideoResolution(SipConfCall.V_720P);
            }else{
            	SipConfCall.setVideoResolution(SipConfCall.V_480P);
            }
            //Make a call 
            return mCallManager.makeConfCall(address, listener);
            
        }
        catch (Exception e) {
            e.printStackTrace();
            return null;
        }
        
        
    }    
    
    /**
	 * Answer a incoming call
	 * @param intent incoming call receiver intent
	 */
    public SipConfCall answerCall(Intent intent, SipConfCall.Listener listener){
    	Util.S_Log.d(LOG_TAG, "Answer a incoming call!!!");
    	
    	
        try { 
            
            /*check the video resolution setting*/
			SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(mContext);
			String video_resolution = sharedPrefs.getString(
					mContext.getString(R.string.key_video_resolution),
					mContext.getString(R.string.settings_video_resolution_default)
			);

            if(video_resolution.equals(mContext.getString(R.string.settings_video_resolution_480p))){
            	SipConfCall.setVideoResolution(SipConfCall.V_480P);
            }else if(video_resolution.equals(mContext.getString(R.string.settings_video_resolution_720p))){
            	SipConfCall.setVideoResolution(SipConfCall.V_720P);
            }else{
            	SipConfCall.setVideoResolution(SipConfCall.V_480P);
            }
            
            SipConfCall incomingCall = mCallManager.takeConfCall(intent, listener);
            incomingCall.answerConfCall(30);
            return incomingCall;

        } catch (SipException e) {
        	Log.e(LOG_TAG, "answerCall(),SipException:" + e.getMessage());
            e.printStackTrace();
            return null;
        } 		
    }
    /**
     * Deny a incoming call
     * @param intent  Incoming call receiver intent
     */
    private void denyCall(Intent intent){
    	mCallManager.denyCall(intent);
    }  
    
    private boolean startBidiVideoTransfer(SipConfCall call){
    	//if(true)return false;
    	Util.S_Log.e(LOG_TAG, "startBidiVideoTransfer... ");
    	if(isStoppingCall){
    		Util.S_Log.e(LOG_TAG, "Media is being stopped by other thread, cancel starting media immdiately");
    		return true;
    	}
            
    	try {
        	//Setup video record and start it 

    		if(ENABLE_TEST_MODE_VIDEOTOFD){
    			Util.S_Log.d(LOG_TAG, "Local video test!!");
    			InetAddress localAddr = InetAddress.getLocalHost();
    			mLocalVideo = new RtpVideo(localAddr);
    			FileDescriptor fd = mLocalVideo.getVideoFileDescriptor();
    			if(fd.valid()){
    					CameraManager.getInstance(mContext).startRecording(mLocalVideo.getVideoFileDescriptor(),false);
    			}else{
    				//TODO exception
    			}	
    		}else{
        		FileDescriptor fd = call.getLocalVideoSocketFileDescripter();
        		if(fd.valid()){
				    int profile = mCall.getRemoteVideoCodecProfile();
        			int level = mCall.getRemoteVideoCodecLevel();
        			mCamaraManager.setEncoderProfileLevel(profile , level);
        				synchronized(mMediaLock){
        				Util.S_Log.i(LOG_TAG, "Camera startup start!");
        				CameraManager.getInstance(mContext).startRecording(fd,false);
        				Util.S_Log.i(LOG_TAG, "Camera startup end!");
        				}
        		}else{
        			//TODO exception
        		}
    		} 		
			//Video
			if(ENABLE_TEST_MODE_VIDEOTOFD){
				
				String videoUrl=null;
				SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(mContext);
				String video_codec = sharedPrefs.getString(
						mContext.getString(R.string.key_video_codec), 
						mContext.getString(R.string.settings_video_codec_default)
				);
				String video_resolution = sharedPrefs.getString(
						mContext.getString(R.string.key_video_resolution), 
						mContext.getString(R.string.settings_video_resolution_default)
				);
		    	
		    	if(video_resolution.equals(mContext.getString(R.string.settings_video_resolution_480p))){
		    		if(video_codec.equals(mContext.getString(R.string.settings_video_codec_h264))){
			    		videoUrl = new String( "sdp://v=0\n" +
								"c=IN IP4 127.0.0.1\n" +
								"t=0 0\n" +
								"m=video 5004 RTP/AVP 97\n" +
								"a=rtpmap:97 H264/90000\n" +
								"a=framesize:97 640-480\n"
								);
		    		}else if(video_codec.equals(mContext.getString(R.string.settings_video_codec_mpeg4))){
			    		videoUrl = new String( "sdp://v=0\n" +
								"c=IN IP4 127.0.0.1\n" +
								"t=0 0\n" +
								"m=video 5004 RTP/AVP 97\n" +
								"a=rtpmap:97 mpeg4-generic/90000\n" +
								"a=framesize:97 640-480\n"
								);
		    		}
		    	}else if(video_resolution.equals(mContext.getString(R.string.settings_video_resolution_720p))){
		    		if(video_codec.equals(mContext.getString(R.string.settings_video_codec_h264))){
			    		videoUrl = new String( "sdp://v=0\n" +
								"c=IN IP4 127.0.0.1\n" +
								"t=0 0\n" +
								"m=video 5004 RTP/AVP 97\n" +
								"a=rtpmap:97 H264/90000\n" +
								"a=framesize:97 1280-720\n"
						 		);
		    		}else if(video_codec.equals(mContext.getString(R.string.settings_video_codec_mpeg4))){
			    		videoUrl = new String( "sdp://v=0\n" +
								"c=IN IP4 127.0.0.1\n" +
								"t=0 0\n" +
								"m=video 5004 RTP/AVP 97\n" +
								"a=rtpmap:97 mpeg4-generic/90000\n" +
								"a=framesize:97 1280-720\n"
								);
		    		}
		    	}
		    	
		    	initVideoAsync(videoUrl);
				
			}else{
				initVideoAsync(call.getPeerSDP());
			}
        	
		} catch (IOException e) {

			e.printStackTrace();
			return false;
		}
		return true;
    }
    
    
    private void stopBidiVideoTransfer(){
    	//if(true)return;
    	synchronized(mMediaLock){
    		Util.S_Log.i(LOG_TAG, "Media close start!");
    		//isStoppingMedia = true;
	        stopPlayVideo();
	        releasePlayer();	
	        CameraManager.getInstance(mContext).stopRecording();
	        Util.S_Log.i(LOG_TAG, "Media close done!");
    	}
        
    }
    /**
     * End a call if necessary
     */
    public boolean  endCall(/*SipConfCall call*/){
    	
    	if(mCall == null){
    		Util.S_Log.d(LOG_TAG, "endCall(),call == null");
    		tryFinishing(EndVideoAndFinishTask.REASON_ERROR);
    		return false;
    	}
		try{
			//end call, need wait after media start-up
			synchronized(mMediaLock){
				Util.S_Log.i(LOG_TAG, "Call end start!");
				isStoppingCall = true;
				mCall.endCall();
				Util.S_Log.i(LOG_TAG, "Call end done!");
			}
			return true;
			/*
			if(call.isInCall()){
				call.endCall();
				return true;
			}else{
				tryFinishing(EndVideoAndFinishTask.REASON_USER);
				return false;
			}
			*/
			
		}catch(SipException e){
			Log.e(LOG_TAG, "End Call error:" + e.getMessage());
			return false;
		}
    }
    
    public boolean holdCall(SipConfCall call){
    	if(call == null || mCallStatus.isIdle){
    		Log.e(LOG_TAG, "holdCall(),is idle!");
    		return false;
    	}
    	
    	if(mCallStatus.isPause){
    		Log.e(LOG_TAG, "Already pause...");
    		return false;
    	}
    	
		try{
			//end call
			call.holdCall(0);
			return true;
			
		}catch(SipException e){
			Log.e(LOG_TAG, "End Call error:" + e.getMessage());
			return false;
		}   	
    }
    
    public boolean continueCall(SipConfCall call){
    	if(call == null || mCallStatus.isIdle){
    		Log.e(LOG_TAG, "continueCall(),is idle!");
    		return false;
    	}
    	
    	if(!mCallStatus.isPause){
    		Log.e(LOG_TAG, "Already continue...");
    		return false;
    	}
    	
		try{
			//end call
			call.continueCall(0);
			return true;
			
		}catch(SipException e){
			Log.e(LOG_TAG, "End Call error:" + e.getMessage());
			return false;
		}     	
    	
    }
    

    /**
     * Initialize remote rtsp video
     * @param sdpUri SDP Uri for incoming video
     */
	private void initVideoAsync(final String sdpUri){
	    	
		if(mVideoFrame!=null){
			if(mVideoFrame.getHolder().isCreating())
				return;
			  		
			new Thread(new Runnable(){
	    		@Override
	    		public void  run(){
	    			
	    			synchronized(mMediaLock){
	    				
	    				if(isStoppingCall){
	    					Util.S_Log.e(LOG_TAG, "Media is being stopped by other thread, cancel starting video immdiately");
	    					return;
	    				}
	    				Util.S_Log.i(LOG_TAG, "Media startup start!");
		    			try{
		    				if(mMediaPlayer == null){
		    					mMediaPlayer = new MediaPlayer();
		    				}else{
		    					mMediaPlayer.reset();
		    				}
		    				Util.S_Log.d(LOG_TAG, "setDisplay start");
		            		mMediaPlayer.setDisplay(mVideoFrame.getHolder());
		            		mMediaPlayer.setOnErrorListener(InCallView.this);
		            		mMediaPlayer.setOnCompletionListener(InCallView.this);
		            		mMediaPlayer.setOnBufferingUpdateListener(InCallView.this);
		            		
		    	    		mPreparedListener = new MediaPlayer.OnPreparedListener(){
		    					@Override
		    					public void onPrepared(MediaPlayer mp) {
		    						Util.S_Log.d(LOG_TAG, "Start play data...");
		    						if(mMediaPlayer == null)
		    							return;
		    						mMediaPlayer.start();
		    						isPlaying = true;
		    						postVideoChanged();
		    					}
		    	    		};
		    	    		Util.S_Log.d(LOG_TAG, "setDataSource start");
		        			mMediaPlayer.setDataSource(sdpUri);
		        			Util.S_Log.d(LOG_TAG, "setDataSource end");
		            		mMediaPlayer.setOnPreparedListener(mPreparedListener);
		            		mMediaPlayer.prepareAsync();
		            		Util.S_Log.d(LOG_TAG, "prepareAsync end");
		        		}catch(Exception e){
		        			Log.e(LOG_TAG, "initVideoAsync(String sdpUri),Failed: " + e.getMessage());
		        			tryFinishing(EndVideoAndFinishTask.REASON_ERROR);
		        		}
		        		
		        		Util.S_Log.i(LOG_TAG, "Media startup done!");
	    			}
	    		
	    		}
			}).start();
			
		}
	}
	private void postVideoChanged(){
		mUiHandler.sendEmptyMessage(VIDEO_CHANGED);
	}
	
	private void hideVideoReplacement(boolean option){
		if(option){
			mVideoReplacement.setAnimation(null);
			mVideoReplacement.setVisibility(View.INVISIBLE);
		}else{
			mVideoReplacement.setAnimation(AnimationUtils.loadAnimation(mContext, R.anim.anim_alpha));
			mVideoReplacement.setVisibility(View.VISIBLE);
		}
	}
	
	private void hidePipWindow(boolean option){
		if(option){
			InCallView.this.mCameraFrame.hide();
		}else{
			InCallView.this.mCameraFrame.show();
		}
	}
	
	private void showStatisticView(boolean option){
		if(option){

			mStatisticView.startUpdate();
			mStatisticView.setAnimation(AnimationUtils.loadAnimation(mContext, R.anim.statistic_right_in));
			mStatisticView.setVisibility(View.VISIBLE);
			
		}else{
			
			mStatisticView.stopUpdate();
			mStatisticView.setAnimation(AnimationUtils.loadAnimation(mContext, R.anim.statistic_right_out));
			mStatisticView.setVisibility(View.INVISIBLE);
			
		}
	}
	
	
    private void stopPlayVideo(){
    	if(mMediaPlayer!=null /*&& isPlaying*/ ){
    		Util.S_Log.d(LOG_TAG, "stopPlayVideo()");
    		try{
    			mMediaPlayer.stop();
    			isPlaying = false;
    			//postVideoChanged();
    		}catch(Exception e){
    			Log.e(LOG_TAG, "stopPlayVideo(), Failed: " + e.getMessage());
    		}
    	}
    }
    private void releasePlayer(){
    	if(mMediaPlayer!=null){
    		Log.e(LOG_TAG, "Now, release media player!!!");
    		mMediaPlayer.reset();
    		mMediaPlayer.release();
    		mMediaPlayer = null;
    	}
    	
    	if(ENABLE_TEST_MODE_VIDEOTOFD){
    		if(mLocalVideo != null){
    			mLocalVideo.release();
				mLocalVideo = null;
    		}
		}
    }    
	
	private boolean isFinishing = false;
	
	private void tryFinishing( int  reason){
		if(isFinishing){
			return;
		}
		mUiHandler.removeMessages(FINISH);
		mUiHandler.sendMessage(mUiHandler.obtainMessage(FINISH, reason, 0));
	}

    public void updateStatus(final String status) {
        // Be a good citizen.  Make sure UI changes fire on the UI thread.
    	((Activity)mContext).runOnUiThread(new Runnable() {
            public void run() {
                TextView labelView = (TextView) findViewById(R.id.sipLabel);
                labelView.setText(status);
                labelView.invalidate();
                labelView.requestLayout();
            }
        });
        
    }
    
    public void updateStatus(SipConfCall call) {

    	String address = call.getPeerProfile().getUserName() + "@" + call.getPeerProfile().getSipDomain();
    	Contact user = Contact.findContactByAddress(mContext, address);//ContactDatabase.getInstance(mContext).queryUserByAddress(address);
    	if(user!=null){
    		updateStatus(user.get_username());
    	}else{
    		updateStatus(address);
    	}
    }
    
	public void VolumeUp(){
		mVolumeBar.VolumeUp();
		showVolumeBarWithTimeOut();
	}
	
	public void VolumeDown(){
		mVolumeBar.VolumeDown();
		showVolumeBarWithTimeOut();
	}
	
	public void mute(){	
		mVolumeBar.mute();
		showVolumeBarWithTimeOut();
	}
	public void restoreAudioMode(){
		mVolumeBar.restoreAudioMode();
	}
	
	private void hideVolumeBarInUI(){
		mUiHandler.sendEmptyMessage(HIDE_VOLUMEBAR);
	}
	private void showVolumeBarWithTimeOut(){
		mAutoHideTimer.cancel();
		mVolumeBar.setVisibility(View.VISIBLE);
		mAutoHideTimer.start(VOLUMEBAR_DURATION_TIME);
		
	}
	
	
	
    
    private void postCallEndEvent(){
    	Iterator<CallStatusObserver> itor = mCallStatusObservers.iterator();
		while(itor.hasNext()){
			CallStatusObserver observer = itor.next();
			observer.onCallEnd();
		}
    }
    
    private void postCallErrorEvent(){
    	Iterator<CallStatusObserver> itor = mCallStatusObservers.iterator();
		while(itor.hasNext()){
			CallStatusObserver observer = itor.next();
			observer.onCallError();
		}
    }
    
    private void postCallStartEvent(){
    	Iterator<CallStatusObserver> itor = mCallStatusObservers.iterator();
		while(itor.hasNext()){
			CallStatusObserver observer = itor.next();
			observer.onCallStart();
		}
    	
    }
    
    
    /*Network error dialog*/
    
    public  void showNetworkErrorAndFinish(final Activity activity, int msgId) {
    	
        DialogInterface.OnClickListener buttonListener =
                new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
            	dialog.dismiss();
                //activity.finish();
            	InCallView.this.endCall();
            }
        };
        new AlertDialog.Builder(activity)
                .setCancelable(false)
                //.setIconAttribute(android.R.attr.alertDialogIcon)
                .setTitle(R.string.network_error_dialog_title)
                .setMessage(msgId)
                .setNeutralButton(R.string.network_error_dialog_ok, buttonListener)
                .show();
                
    }   
    
    
    
    /*Camera error dialog*/
    public  void showCameraErrorAndFinish(final Activity activity, int msgId) {
    	
        /*DialogInterface.OnClickListener buttonListener =
                new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
            	dialog.dismiss();
            	InCallView.this.endCall(mCall);
            }
        };*/

        /*Toast.makeText(
                mContext.getApplicationContext(), 
                getResources().getString(msgId),
                Toast.LENGTH_LONG).show();*/
        Intent intent = new Intent("cisco.action.camera.remove");
        mContext.sendBroadcast(intent);

        InCallView.this.endCall();
        
       /*new AlertDialog.Builder(activity)
                .setCancelable(false)
                //.setIconAttribute(android.R.attr.alertDialogIcon)
                .setTitle(R.string.camera_error_dialog_title)
                .setMessage(msgId)
                .setNeutralButton(R.string.camera_error_dialog_ok, buttonListener)
                .show();*/
    }
    
    private class EndVideoAndFinishTask extends AsyncTask<Void,Void,Boolean>{

   	 private ProgressDialog mProgressDialog = null;	
   	 private boolean mShowDialog;
   	 private int mReason;
   	 
   	 public final static int REASON_ERROR = 0;
   	 public final static int REASON_BUSY = 1;
     public final static int REASON_END = 2;
     public final static int REASON_USER = 3;
   	 
   	 public EndVideoAndFinishTask(boolean showDialog,int reason){
   		 mShowDialog = showDialog;
   		 mReason = reason;
   	 }
   	 
		 @Override
		 protected void onPreExecute(){
			 Util.S_Log.d(LOG_TAG, "onPreExecute");
			 InCallView.this.isFinishing = true;
			 if(mShowDialog)showFinishCallDialog(mReason);
		 }
 	
		@Override
		protected Boolean doInBackground(Void... params) {
			/*Stop update statistic*/
			mStatisticView.stopUpdate();
			/*Stop video transfer,after 1 second's delay*/
			
			try {
				java.lang.Thread.sleep(1000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			InCallView.this.stopBidiVideoTransfer();

			return true;
		}
		
		 @Override
		 protected void onPostExecute(Boolean result) {
			 Util.S_Log.d(LOG_TAG, "Call Ending Task return result: " + result.toString());
			 InCallView.this.isFinishing = false;
			 closeFinishCallDialog();
			 postCallEndEvent();
			
		 }
		 
		 
		 private void showFinishCallDialog(int reason){
			 try{
				 mProgressDialog = new ProgressDialog(mContext);
				 mProgressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
				 mProgressDialog.setCancelable(false);
				 switch (reason){
				 case REASON_ERROR:
					 mProgressDialog.setMessage("Error happened!!");
					 break;
				 case REASON_BUSY:
					 mProgressDialog.setMessage("User busy!!");
					 break;
				 case REASON_END:
					 mProgressDialog.setMessage("Call finished!!");
					 break;
				 case REASON_USER:
					 mProgressDialog.setMessage("Finish call...");
					 break;
				 default:
					 mProgressDialog.setMessage("Finish call...");
					 break;
					 
				 }
				 
				 mProgressDialog.show();				 
			 }catch(Exception e){
				 e.printStackTrace();
			 }
		 }
		 private void closeFinishCallDialog(){
			 if(mProgressDialog!=null)
				mProgressDialog.cancel();
			 	mProgressDialog = null;
		 }
 	
    }

    /*This is used to deal with a replaced call*/
	@Override
	public void onAnswer(final Intent intent) {
		Log.e(LOG_TAG, "InCallView deal with the Answer");
		replaceCall(intent);
	}

	@Override
	public void onDeny(Intent intent) {
		Log.e(LOG_TAG, "InCallView deal with the Deny");
		denyCall(intent);
		mActivityContainer.setCanAutoFinishingActivity(true);
	}
	
	@Override
	public void onNewIncoming(Intent intent) {
		//Disable auto-close activity when replace call
		mActivityContainer.setCanAutoFinishingActivity(false);
	}
	
	
	private void replaceCall(final Intent intent){
		
		/*Create a temporary observer to get the notification of CALL END */
		CallStatusObserver tmpObserver = new CallStatusObserver(){
			@Override
			public void onCallStart() {}

			@Override
			public void onCallEnd() {
				if(mCall != null){
					mCall.close();
					mCall = null;
				}
				//Enable auto-close
				mActivityContainer.setCanAutoFinishingActivity(true);
				mReplacedCallListener = InCallView.this.createCallListener();
				mCall = answerCall(intent,mReplacedCallListener);
				//Remove and stop listening the status. 
				removeCallStatusObserver(this);
			}
			@Override
			public void onCallError() {
				removeCallStatusObserver(this);
			}
			
		};
		addCallStatusObserver(tmpObserver);
		
		if(mCall != null && mCall.isInCall()){
			endCall();
		}else{
			mReplacedCallListener = InCallView.this.createCallListener();
			mCall = answerCall(intent,mReplacedCallListener);
		}
		
	}
	
	private class MySipCallListener extends SipConfCall.Listener{
		public boolean isEstablished = false;
		
	}

    // Jabber <--> STB
    
    public static void exEndCall()
    {
    	Util.S_Log.d(LOG_TAG, "exEndCall...");
    	exHandler.sendMessage(exHandler.obtainMessage(EXJABBER_END, 0, 0));
    	
    }
    
    private static UiHandler exHandler = null;
    //private ImageButton btnEndCall = null;;



}