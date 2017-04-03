package com.cisco.slingshot.ui.fragment;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.os.Bundle;
import android.preference.ListPreference;
import android.preference.PreferenceFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.cisco.slingshot.R;

public class VideoSettingsFragment extends PreferenceFragment implements OnSharedPreferenceChangeListener{

	@Override
	public void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		mContext = this.getActivity();
		KEY_CODEC = mContext.getString(R.string.key_video_codec);
		KEY_RESOLUTION = mContext.getString(R.string.key_video_resolution);
		KEY_BIT_RATE = mContext.getString(R.string.key_video_bit_rate);
		KEY_FRAME_RATE = mContext.getString(R.string.key_video_frame_rate);
		

		new Thread(new Runnable(){
    		@Override
    		public void  run(){
    			addPreferencesFromResource(R.xml.slingshot_preferences_video);
    			init();
    		}}).start();
	}
	
	private void init(){
		  //mPrefListCodec = (ListPreference)this.findPreference(KEY_CODEC);
		  mPrefListBitRate = (ListPreference)this.findPreference(KEY_BIT_RATE);
		  mPrefListResolution = (ListPreference)this.findPreference(KEY_RESOLUTION);
		  mPrefListFrameRate = (ListPreference)this.findPreference(KEY_FRAME_RATE);	
		  /*Update Summary immediately*/
		  SharedPreferences sharedPreferences = getPreferenceScreen().getSharedPreferences();
		  //mPrefListCodec.setSummary(sharedPreferences.getString(KEY_CODEC, 
			//	  mContext.getString(R.string.settings_video_codec_default)));
		  
		  mPrefListBitRate.setSummary(bitRateValueToEntry(sharedPreferences.getString(KEY_BIT_RATE, 
				  mContext.getString(R.string.settings_video_bitrate_default))));
		  
		  mPrefListFrameRate.setSummary(sharedPreferences.getString(KEY_FRAME_RATE, 
				  mContext.getString(R.string.settings_video_framerate_default)));
		  
		  mPrefListResolution.setSummary(sharedPreferences.getString(KEY_RESOLUTION, 
				  mContext.getString(R.string.settings_video_resolution_default)));
		  
		  sharedPreferences.registerOnSharedPreferenceChangeListener(this);
	}
	
	private String bitRateValueToEntry(String value){
		if(value.equals(mContext.getString(R.string.settings_video_bitrate_low))){
			return mContext.getString(R.string.settings_video_bitrate_low_title);
		}else if(value.equals(mContext.getString(R.string.settings_video_bitrate_middle))){
			return mContext.getString(R.string.settings_video_bitrate_middle_title);
		}else if(value.equals(mContext.getString(R.string.settings_video_bitrate_high))){
			return mContext.getString(R.string.settings_video_bitrate_high_title);
		}else if(value.equals(mContext.getString(R.string.settings_video_bitrate_super))){
			return mContext.getString(R.string.settings_video_bitrate_super_title);
		}else{
			return value;
		}
	}
    @Override 
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState){
    	
    	  return inflater.inflate(R.layout.slingshot_layout_preferences_video, container, false);
    }
    

	private static final String TAG = "VideoSettingsFragment";


	@Override
	public void onSharedPreferenceChanged(SharedPreferences sharedPreferences,
			String key) {
		if(key.equals(KEY_CODEC)){
			String video_codec 	= sharedPreferences.getString(KEY_CODEC, 
					mContext.getString(R.string.settings_video_codec_default));
			
			//mPrefListCodec.setSummary(video_codec);
			
		}else if(key.equals(KEY_BIT_RATE)){
			String bit_rate = sharedPreferences.getString(KEY_BIT_RATE, 
					mContext.getString(R.string.settings_video_bitrate_default));
			
			mPrefListBitRate.setSummary(bitRateValueToEntry(bit_rate));
		}else if(key.equals(KEY_RESOLUTION)){
			String video_resolution = sharedPreferences.getString(KEY_RESOLUTION, 
					mContext.getString(R.string.settings_video_resolution_default));
			
			mPrefListResolution.setSummary(video_resolution);
		}else if(key.equals(KEY_FRAME_RATE)){
			String video_frameRate 	= sharedPreferences.getString(KEY_FRAME_RATE, 
					mContext.getString(R.string.settings_video_framerate_default));
			
			mPrefListFrameRate.setSummary(video_frameRate);
		}
			
		
	}
	
	private  String KEY_CODEC ;
	private  String KEY_RESOLUTION ;
	private  String KEY_BIT_RATE ;
	private  String KEY_FRAME_RATE ;
	
	private Context mContext;
	
	//private ListPreference mPrefListCodec;
	private ListPreference mPrefListBitRate;
	private ListPreference mPrefListResolution;
	private ListPreference mPrefListFrameRate;
}