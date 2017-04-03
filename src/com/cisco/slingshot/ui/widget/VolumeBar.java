package com.cisco.slingshot.ui.widget;

import java.util.ArrayList;
import java.util.Iterator;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.media.AudioManager;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.cisco.slingshot.R;
import com.cisco.slingshot.utils.TimeoutTimer;

public class VolumeBar extends LinearLayout implements VolumeChangedListener{
	
	public final static String LOG_TAG = "VolumeBar";
	private final static int VOLUME_BAR_ITEM_DEFAULT_WIDGH_BASE = 2;//2dip 
	private final static int VOLUME_BAR_ITEM_DEFAULT_HEIGHT = 4;//4dip 
	private final static int VOLUME_TOTAL_LEVEL = 10;
	
	private ImageView mVolumeImage = null;
	
	private int[]  mItemWidthGroup = null;
 
	private Context mContext ;
	
	private VolumeAdapter mVolumeAdapter;
	

	
 	
	public VolumeBar(Context context){
		super(context);
		mContext = context;
		initView();
	}
	
	public VolumeBar(Context context, AttributeSet attrs) {
		super(context, attrs);
		mContext = context;
		initView();
	}

	private void initView(){

		mItemWidthGroup = createWidthGroup(VOLUME_TOTAL_LEVEL);
		
		//set properties
		this.setOrientation(VERTICAL);
		this.setGravity(Gravity.RIGHT);
		this.setBackgroundColor(mContext.getResources().getColor(android.R.color.transparent));
		
		//set adapter
		mVolumeAdapter = new VolumeAdapter(mContext){

			@Override
			public int getItemCount() {
				return VOLUME_TOTAL_LEVEL;
			}

			@Override
			public View getItemView(int pos) {
				View bar_item = new View(mContext);
				bar_item.setLayoutParams(new LayoutParams(mItemWidthGroup[VOLUME_TOTAL_LEVEL - 1 - pos],VOLUME_BAR_ITEM_DEFAULT_HEIGHT));
				return bar_item;
			}
			
		};
		
		setVolumeAdapter(mVolumeAdapter);
		
		mVolumeImage = new ImageView(mContext);
		mVolumeImage.setLayoutParams(new LayoutParams(24,24));
		mVolumeImage.setImageResource(R.drawable.ic_sound_on);
		this.addView(mVolumeImage);
		
		mVolumeAdapter.addVolmueChangedListener(this);
		
	}
	
	public void setVolumeAdapter(VolumeAdapter adapter){
		adapter.setAnchorVolumeBar(this);
	}
	
	public void addItem(View v){
		
	}
	
	private int[] createWidthGroup(final int count){
		int[] widthGroup = new int[count]; 
		
		for(int i =0 ; i < count; i++){
			widthGroup[i] = VOLUME_BAR_ITEM_DEFAULT_WIDGH_BASE + i*5;
		}
		return widthGroup;
	}
	

	@Override
	public void onVolumeChanged(int volume) {
		//TODO
		if(volume == 0){
			mVolumeImage.setImageResource(R.drawable.ic_sound_off);
		}else{
			mVolumeImage.setImageResource(R.drawable.ic_sound_on);
		}
		
	}
	
	public void VolumeUp(){
		mVolumeAdapter.VolumeUp();
	}
	
	public void VolumeDown(){
		mVolumeAdapter.VolumeDown();
	}
	
	public void mute(){	
		mVolumeAdapter.mute();
	}
	
	public boolean isMuted(){
		return mVolumeAdapter.isMuted();
	}
	
	public void restoreAudioMode(){
		mVolumeAdapter.restoreAudioMode();
	}
	

	
	private abstract class VolumeAdapter{
		
		private Context mContext;
		
		
		private int _originVolume = 0;
		private int _curVolume = 0;
		private int _maxVolume = 0;
		private AudioManager mAudioManager;
		
		private VolumeBar mAnchorVolumeBar = null;
		private ArrayList<VolumeChangedListener> mListeners = null;
		
		private int mItemCount = 0;
		private View[] mItemViews = null;
		private int[]  mVoiceGroup = null;
		
		private Drawable mUnhighlightBackground = null;
		private Drawable mHighlightBackground = null;
		
		private final int AUDIO_COMMUNICATION_MODE = AudioManager.MODE_IN_COMMUNICATION;
		
		private boolean isManualMuted = false;
		private int _reserveVolume;
		
	    private UiHandler mUiHandler = new UiHandler();
	    public static final int UPDATE_VIEW = 1;
	    
		private class UiHandler extends Handler{

	        @Override
	        public void handleMessage(Message msg) {
	            switch (msg.what) {

	                case UPDATE_VIEW:
	                	updateVolumeView();
	    				break;
	                default:
	                    Log.v(LOG_TAG, "Unhandled message: " + msg.what);
	                    break;
	            }
	        }    	
	    }
		
		public VolumeAdapter(Context ctx){
			mContext = ctx;
		}
		
		public void addVolmueChangedListener(VolumeChangedListener listener){
			mListeners.add(listener);
			if(isManualMuted){
				listener.onVolumeChanged(0);
			}else{
				listener.onVolumeChanged(_curVolume);				
			}
		}
		
		public void removeVolmueChangedListener(VolumeChangedListener listener){
			mListeners.remove(listener);
		}
		
		private void postVolumeChanged(int volume){
	    	Iterator<VolumeChangedListener> itor = mListeners.iterator();
			while(itor.hasNext()){
				VolumeChangedListener listener = itor.next();
				listener.onVolumeChanged(volume);
			}		
		}
		
		
		
		public void setAnchorVolumeBar(VolumeBar bar){
			mAnchorVolumeBar = bar;
			init();
		}
		
		protected boolean isMuted(){
			return isManualMuted || _curVolume == 0;
		}
		
		public abstract int getItemCount();
		public abstract View getItemView(int pos);
		
		private void init(){
			mAudioManager = (AudioManager)mContext.getSystemService(Context.AUDIO_SERVICE);
			mAudioManager.setMode(AUDIO_COMMUNICATION_MODE);
			_maxVolume = mAudioManager.getStreamMaxVolume(AudioManager.STREAM_VOICE_CALL);
			_originVolume = _curVolume = mAudioManager.getStreamVolume(AudioManager.STREAM_VOICE_CALL);
			
			mListeners = new ArrayList<VolumeChangedListener>();
			
			mItemCount = getItemCount();
			mItemViews = new View[mItemCount];
			
			mUnhighlightBackground = mContext.getResources().getDrawable(R.color.transparent_gray);
			mHighlightBackground = mContext.getResources().getDrawable(R.color.highlight_yellow);
			
			for(int i=0;i<mItemCount;i++){
				mItemViews[i] = getItemView(i);
				mItemViews[i].setBackgroundDrawable(mUnhighlightBackground);
				mAnchorVolumeBar.addView(mItemViews[i]);
				//add divider
				View divider = new View(mContext);
				divider.setLayoutParams(new LayoutParams(4,4));
				divider.setBackgroundColor(mContext.getResources().getColor(android.R.color.transparent));
				mAnchorVolumeBar.addView(divider);
				
			}
			
			mVoiceGroup = createVolumeGroup();
			
			updateVolumeView();

			
		}
		
		private int[] createVolumeGroup(){
			
			int[] voiceGroup = new int[mItemCount];
				
			int remainder = 0,delta = 0;
			float delta_f = 0.0f;

			delta     = _maxVolume / mItemCount;

			if(delta == 0){
				delta_f  = (float)_maxVolume / (float)mItemCount;
				mAdjustDelta = 1;
				
				int volumeAtPositonFirst =  1;
				
				for(int i=0;i<mItemCount;i++){
					voiceGroup[i] = volumeAtPositonFirst+(int)( i * delta_f);
				}
			}else{
				remainder = _maxVolume % mItemCount;
				mAdjustDelta = delta;
				int volumeAtPositonFirst =  delta + remainder;
				
				for(int i=0;i<mItemCount;i++){
					voiceGroup[i] = (i == 0)?volumeAtPositonFirst:(voiceGroup[i - 1] + delta);
				}
			}
		
			return voiceGroup;
		}
		
		private void updateVolumeView(){
			for(int i=0;i<mItemCount;i++){
				if(mVoiceGroup[i] <= _curVolume){
					mItemViews[mItemCount - 1 - i].setBackgroundDrawable(mContext.getResources().getDrawable(R.color.highlight_yellow));
				}else{
					mItemViews[mItemCount - 1 - i].setBackgroundDrawable(mContext.getResources().getDrawable(R.color.transparent_gray));
				}
			}
		}
		
		
		private int mAdjustDelta = 0;
		protected void VolumeUp(){
			_curVolume = mAdjustDelta + _curVolume;
			
			if(_curVolume > _maxVolume){
				_curVolume = _maxVolume;
			}
			
			setVolume(_curVolume);
			postVolumeChanged(_curVolume);
		}
		
		protected void VolumeDown(){
			_curVolume =_curVolume - mAdjustDelta ;
			
			if(_curVolume < 0){
				_curVolume = 0;
			}
			
			setVolume(_curVolume);
			postVolumeChanged(_curVolume);
		}
		
		protected void mute(){	
			if(isMuted()){
				//restore volume
				_curVolume = _reserveVolume;
				//isManualMuted = false;
			}else{
				//save volume
				_reserveVolume = _curVolume;
				_curVolume = 0;
				//isManualMuted = true;
			}
			setVolume(_curVolume);
			postVolumeChanged(_curVolume);
		}
		
		public void restoreAudioMode(){
			mAudioManager.setMode(AudioManager.MODE_NORMAL);
			mAudioManager.setStreamVolume(AudioManager.STREAM_VOICE_CALL, _originVolume, 0);
			
		}
		
		private void setVolume(int volume){
			mAudioManager.setStreamVolume(AudioManager.STREAM_VOICE_CALL, volume, 0);
			//updateVolumeView();
			mUiHandler.sendEmptyMessage(UPDATE_VIEW);
		}
	}



	
	
	
	
	
	
	
}