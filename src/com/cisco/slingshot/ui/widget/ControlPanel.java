package com.cisco.slingshot.ui.widget;

import android.app.Activity;
import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.TextSwitcher;
import android.widget.TextView;
import android.widget.ViewSwitcher;

import com.cisco.slingshot.R;
import com.cisco.slingshot.ui.CallViewProxy;
import com.cisco.slingshot.utils.TimeoutTimer;


public class ControlPanel extends FrameLayout{
	
	public final static String LOG_TAG = "ControlPanel";
	
	private Context mContext;
	private View    _rootView;
	
	private TextSwitcher mHint;
	private TimeoutTimer mAutoHideTimer;
	final int HINT_DURATION_TIME = 3; //seconds
	
    private ImageButton bnMicroPhone= null;
    private ImageButton bnLocalVideo = null;
    private ImageButton bnCamera = null;
    private ImageButton bnStartEnd = null;
    //private ImageButton bnPause = null;
    private ImageButton bnStatistic = null;
    
    private CallViewProxy 				mCallViewProxy = null;
    
    private UiHandler mUiHandler = new UiHandler();
    public static final int HIDE_HINT = 1; //finish call
    
   
	public ControlPanel(Context context){
		super(context);
		mContext = context;
		initView();
	}

	public ControlPanel(Context context, AttributeSet attr) {
		super(context, attr);
		mContext = context ;
		initView();
	}
	
	public void setCallViewProxy(CallViewProxy proxy){
		mCallViewProxy = proxy;	
		updateControlPanelStatus();
		
	}
	
	private CallViewProxy createDefaultProxy(){
		
		return new CallViewProxy(){

			@Override
			public CallStatus requestCallStatus() {
				CallViewProxy.CallStatus status = new CallViewProxy.CallStatus();
				status.isIdle = false;
				//status.isAudioMuted = false;
				//status.isVideoMuted = false;
				status.isSpeekerOn  = true;
				status.isRecorderOn = true;
				status.isPause = false;
				status.isLocalHiden = false;
				status.isStatisticOn = false;
				return status;
			}
		};
	}
	private void initView(){
		_rootView = LayoutInflater.from(mContext).inflate(R.layout.control_panel, null);
		this.addView(_rootView);
		if(mCallViewProxy == null){
			mCallViewProxy = createDefaultProxy();
		}
		
		mHint = (TextSwitcher)_rootView.findViewById(R.id.control_indicator);
		mHint.setFactory(new ViewSwitcher.ViewFactory(){

			@Override
			public View makeView() {
				
		        TextView t = new TextView(mContext);
		        t.setTextSize(16);
		        t.setTextColor(mContext.getResources().getColor(R.color.white));
		        t.setGravity(Gravity.TOP | Gravity.CENTER_HORIZONTAL);
		        t.setBackgroundDrawable(mContext.getResources().getDrawable(R.drawable.background_gray_corner_rect));
		        return t;
			}
			
		});

        Animation in = AnimationUtils.loadAnimation(mContext,
                android.R.anim.fade_in);
        Animation out = AnimationUtils.loadAnimation(mContext,
                android.R.anim.fade_out);
        mHint.setInAnimation(in);
        mHint.setOutAnimation(out);
     // mIncomingcallDiag.getWindow().getAttributes().windowAnimations = R.style.DialogAnimation;
       
        mAutoHideTimer = new TimeoutTimer("ControlBarHintAutoHideTimer",new TimeoutTimer.TimeoutCallback() {
			
			@Override
			public void onTimeout() {
				hideHintInUI();
			}
		});
		
    	
		bnLocalVideo = (ImageButton)findViewById(R.id.control_local_video);
		//bnLocalVideo.setClickable(false);
		//bnLocalVideo.setFocusable(false);
		bnLocalVideo.setOnClickListener(
        		new View.OnClickListener() {
					@Override
					public void onClick(View v) {	
						mCallViewProxy.hideLocal(!mCallViewProxy.requestCallStatus().isLocalHiden);
						mCallViewProxy.requestCallStatus().isLocalHiden = !mCallViewProxy.requestCallStatus().isLocalHiden;
						updateControlPanelStatus();                    	
					}
				}
        );
		bnLocalVideo.setOnFocusChangeListener(new View.OnFocusChangeListener(){

			@Override
			public void onFocusChange(View v, boolean hasFocus) {
				if(hasFocus){
					
					mHint.setText(mContext.getString(R.string.incallview_control_panel_indicator_pip));
					showHintWithTimeOut();

				}
				
			}
			
		});
        
    	bnMicroPhone = (ImageButton)findViewById(R.id.control_microphone);
    	bnMicroPhone.setOnClickListener(
        		new View.OnClickListener() {
					@Override
					public void onClick(View v) {
						mCallViewProxy.muteVoice(mCallViewProxy.requestCallStatus().isSpeekerOn);
						mCallViewProxy.requestCallStatus().isSpeekerOn = !mCallViewProxy.requestCallStatus().isSpeekerOn;
						updateControlPanelStatus();
					}
				}
        );   
    	
    	bnMicroPhone.setOnFocusChangeListener(new View.OnFocusChangeListener(){

			@Override
			public void onFocusChange(View v, boolean hasFocus) {
				if(hasFocus){
					mHint.setText(mContext.getString(R.string.incallview_control_panel_indicator_speaker));
					showHintWithTimeOut();
				}
				
			}
			
		});

         bnCamera = (ImageButton)findViewById(R.id.control_camera);
         //bnCamera.setClickable(false);
         //bnCamera.setFocusable(false);
         bnCamera.setOnClickListener(
        		new View.OnClickListener() {
					@Override
					public void onClick(View v) {
						mCallViewProxy.muteVideo(mCallViewProxy.requestCallStatus().isRecorderOn);
						mCallViewProxy.requestCallStatus().isRecorderOn = !mCallViewProxy.requestCallStatus().isRecorderOn;
						updateControlPanelStatus();
					}
				}
        );
         
         bnCamera.setOnFocusChangeListener(new View.OnFocusChangeListener(){

			@Override
			public void onFocusChange(View v, boolean hasFocus) {
				if(hasFocus){
					mHint.setText(mContext.getString(R.string.incallview_control_panel_indicator_camera));
					showHintWithTimeOut();
				}
				
			}
			
		});

        
        bnStartEnd = (ImageButton)findViewById(R.id.control_startend);
        bnStartEnd.setOnClickListener(
        		new View.OnClickListener() {
					@Override
					public void onClick(View v) {			
						mCallViewProxy.endCall();
						mCallViewProxy.requestCallStatus().isIdle = !mCallViewProxy.requestCallStatus().isIdle ;
						updateControlPanelStatus();
						
					}
				}
        );  
        bnStartEnd.setOnFocusChangeListener(new View.OnFocusChangeListener(){

			@Override
			public void onFocusChange(View v, boolean hasFocus) {
				if(hasFocus){
					mHint.setText(mContext.getString(R.string.incallview_control_panel_indicator_call_end));
					showHintWithTimeOut();
				}
				
			}
			
		});
        /*
        bnPause = (ImageButton)findViewById(R.id.control_pause);
        bnPause.setClickable(false);
        bnPause.setFocusable(false);
        bnPause.setOnClickListener(
        		new View.OnClickListener() {
					@Override
					public void onClick(View v) {		
						mCallViewProxy.pauseCall(!mCallViewProxy.requestCallStatus().isPause);
						mCallViewProxy.requestCallStatus().isPause = !mCallViewProxy.requestCallStatus().isPause ;
						updateControlPanelStatus();
					}
				}
        );  
        */
  
        
        bnStatistic = (ImageButton)findViewById(R.id.control_stat);
        //bnStatistic.setClickable(false);
        //bnStatistic.setFocusable(false);
        bnStatistic.setOnClickListener(
        		new View.OnClickListener() {
					@Override
					public void onClick(View v) {		
						mCallViewProxy.showStatistic((!mCallViewProxy.requestCallStatus().isStatisticOn));
						mCallViewProxy.requestCallStatus().isStatisticOn = !mCallViewProxy.requestCallStatus().isStatisticOn ;
						updateControlPanelStatus();
					}
				}
        );  
        
        bnStatistic.setOnFocusChangeListener(new View.OnFocusChangeListener(){

			@Override
			public void onFocusChange(View v, boolean hasFocus) {
				if(hasFocus){
					mHint.setText(mContext.getString(R.string.incallview_control_panel_indicator_statistic));
					showHintWithTimeOut();
				}
				
			}
			
		});
        
        updateControlPanelStatus();
		
	}
	
	private void hideHintInUI(){
		mUiHandler.sendEmptyMessage(HIDE_HINT);
	}
	private void showHintWithTimeOut(){
		mAutoHideTimer.cancel();
		mHint.setVisibility(View.VISIBLE);
		mAutoHideTimer.start(HINT_DURATION_TIME);
		
	}
	
	
	private void updateControlPanelStatus(){
		/*
    	bnStartEnd.setImageDrawable(mCallViewProxy.requestCallStatus().isIdle?
    			mContext.getResources().getDrawable(R.drawable.ic_call_start)
    			:mContext.getResources().getDrawable(R.drawable.ic_call_stop));
    			*/
    	/*
    	bnPause.setImageDrawable(mCallViewProxy.requestCallStatus().isPause? 
    			mContext.getResources().getDrawable(android.R.drawable.stat_sys_phone_call)
    			:mContext.getResources().getDrawable(android.R.drawable.stat_sys_phone_call_on_hold));   	
    	*/
    	bnMicroPhone.setImageDrawable(mCallViewProxy.requestCallStatus().isSpeekerOn? 
    			mContext.getResources().getDrawable(R.drawable.ic_microphone_on)
    			:mContext.getResources().getDrawable(R.drawable.ic_microphone_off));  	
    	
    	bnLocalVideo.setImageDrawable(mCallViewProxy.requestCallStatus().isLocalHiden?  
    			mContext.getResources().getDrawable(R.drawable.local_video_hiden)
    			:mContext.getResources().getDrawable(R.drawable.local_video_shown));
    	
    	bnCamera.setImageDrawable(mCallViewProxy.requestCallStatus().isRecorderOn?
    			mContext.getResources().getDrawable(R.drawable.ic_camera_on)
    			:mContext.getResources().getDrawable(R.drawable.ic_camera_off));
    	
    	bnStatistic.setImageDrawable(mCallViewProxy.requestCallStatus().isStatisticOn?
    			mContext.getResources().getDrawable(R.drawable.ic_stats_on)
    			:mContext.getResources().getDrawable(R.drawable.ic_stats_off));
    }
	
	public void extEndCall(){
		bnStartEnd.performClick();
	}
	
	private class UiHandler extends Handler{

        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {

                case HIDE_HINT:
                	mHint.setAnimation(AnimationUtils.loadAnimation(mContext,
                android.R.anim.fade_out));
                	mHint.setVisibility(View.INVISIBLE);
    				break;
                default:
                    Log.v(LOG_TAG, "Unhandled message: " + msg.what);
                    break;
            }
        }    	
    }
	

	
	
	
	
}