package com.cisco.slingshot.ui.fragment;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.os.Bundle;
import android.preference.EditTextPreference;
import android.preference.ListPreference;
import android.preference.PreferenceFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.cisco.slingshot.R;
import com.cisco.slingshot.call.AccountManager;

public class AccountSettingsFragment extends PreferenceFragment implements OnSharedPreferenceChangeListener{
	
	private static final String TAG = "AccountSettingsFragment";

	@Override
	public void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		mContext = this.getActivity();
		
		KEY_ACCOUNT_SOURCE = mContext.getString(R.string.key_settings_user_account_source);
		KEY_USERNAME = mContext.getString(R.string.str_pref_username);
		KEY_DOMAIN = mContext.getString(R.string.str_pref_domain);
		KEY_PASSWORD = mContext.getString(R.string.str_pref_password);
		KEY_PROXY = mContext.getString(R.string.str_pref_proxy);
		KEY_PORT = mContext.getString(R.string.str_pref_port);
		KEY_PROTOCOL = mContext.getString(R.string.str_pref_protocol);
		
		new Thread(new Runnable(){
    		@Override
    		public void  run(){
    				addPreferencesFromResource(AccountManager.DISABLE_LOCAL? R.xml.slingshot_preferences_user2:R.xml.slingshot_preferences_user);
    				init();
    		}}).start();
		
		
	}
	
    @Override 
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState){
    	
    	  return inflater.inflate(R.layout.slingshot_layout_preferences_user, container, false);
    }

	@Override
	public void onSharedPreferenceChanged(SharedPreferences sharedPreferences,
			String key) {
		// TODO Auto-generated method stub
		if(AccountManager.DISABLE_LOCAL == false){
			if(key.equals(KEY_ACCOUNT_SOURCE)){
				String keyValue = sharedPreferences.getString(KEY_ACCOUNT_SOURCE,
						mContext.getString(R.string.settings_user_account_source_default));
				
				mPrefAccountSource.setSummary(accountSourceValueToEntry(sharedPreferences.getString(KEY_ACCOUNT_SOURCE, "")));
						
				if(keyValue.equals(mContext.getString(R.string.settings_user_account_source_ims))){
					enableLocalSetting(false);
				}else{
					enableLocalSetting(true);
				}
				
			}
		}else{
			enableLocalSetting(false);
		}
		
		if(key.equals(KEY_USERNAME)){
			  mPrefUserName.setSummary(sharedPreferences.getString(KEY_USERNAME, 
			  ""));
			
		}else if(key.equals(KEY_DOMAIN)){
			  mPrefDomain.setSummary(sharedPreferences.getString(KEY_DOMAIN, 
			  ""));
		}else if(key.equals(KEY_PASSWORD)){
			mPrefPassword.setSummary("******");
		}else if(key.equals(KEY_PROXY)){
			  mPrefProxy.setSummary(sharedPreferences.getString(KEY_PROXY, 
			  ""));
		}else if(key.equals(KEY_PORT)){
			mPrefPort.setSummary(sharedPreferences.getString(KEY_PORT, 
			  ""));
			
		}else if(key.equals(KEY_PROTOCOL)){
			  mPrefProtocol.setSummary(sharedPreferences.getString(KEY_PROTOCOL, 
			  ""));
		}
					
	}
	
	
	private void init(){
		mPrefAccountSource = (ListPreference)this.findPreference(KEY_ACCOUNT_SOURCE);
		mPrefUserName = (EditTextPreference)this.findPreference(KEY_USERNAME);
		mPrefDomain = (EditTextPreference)this.findPreference(KEY_DOMAIN);
		mPrefPassword = (EditTextPreference)this.findPreference(KEY_PASSWORD);
		mPrefProxy = (EditTextPreference)this.findPreference(KEY_PROXY);
		mPrefPort = (EditTextPreference)this.findPreference(KEY_PORT);
		mPrefProtocol = (ListPreference)this.findPreference(KEY_PROTOCOL);	
		
		/*
		mPrefUserName.getEditText().setInputType(InputType.TYPE_CLASS_TEXT);
		mPrefDomain.getEditText().setInputType(InputType.TYPE_CLASS_TEXT);
		mPrefPassword.getEditText().setInputType(InputType.TYPE_CLASS_TEXT);
		mPrefProxy.getEditText().setInputType(InputType.TYPE_CLASS_TEXT);
		*/
		  /*Update Summary immediately*/
		  SharedPreferences sharedPreferences = getPreferenceScreen().getSharedPreferences();
		  if(AccountManager.DISABLE_LOCAL == false){
			String keyValue = sharedPreferences.getString(KEY_ACCOUNT_SOURCE,
					mContext.getString(R.string.settings_user_account_source_default));
			
			mPrefAccountSource.setSummary(accountSourceValueToEntry(sharedPreferences.getString(KEY_ACCOUNT_SOURCE, "")));
					
			if(keyValue.equals(mContext.getString(R.string.settings_user_account_source_ims))){
				enableLocalSetting(false);
			}else{
				enableLocalSetting(true);
			}
		  }else{
			  enableLocalSetting(false);
		  }
		  
		  mPrefUserName.setSummary(sharedPreferences.getString(KEY_USERNAME, 
				  ""));
		  
		  mPrefDomain.setSummary(sharedPreferences.getString(KEY_DOMAIN, 
				  ""));
		  
		  mPrefPassword.setSummary("******");
		  
		  mPrefProxy.setSummary(sharedPreferences.getString(KEY_PROXY, 
				  ""));
		  mPrefPort.setSummary(sharedPreferences.getString(KEY_PORT, 
				  ""));
		  mPrefProtocol.setSummary(sharedPreferences.getString(KEY_PROTOCOL, 
		  ""));
		  
		  sharedPreferences.registerOnSharedPreferenceChangeListener(this);		
	}
	
	private void enableLocalSetting(boolean operation){
			  mPrefUserName.setEnabled(operation);
			  mPrefDomain.setEnabled(operation);
			  mPrefPassword.setEnabled(operation);;
			  mPrefProxy.setEnabled(operation);;
			  mPrefPort.setEnabled(operation);;
			  mPrefProtocol.setEnabled(operation);;
	}
	
	private String accountSourceValueToEntry(String value){
		if(value.equals(mContext.getString(R.string.settings_user_account_source_ims))){
			return mContext.getString(R.string.settings_user_account_source_ims_title);
		}else if(value.equals(mContext.getString(R.string.settings_user_account_source_local))){
			return mContext.getString(R.string.settings_user_account_source_local_title);
		}else{
			return value;
		}
	}
	
	
	private Context mContext;
	
	private  String KEY_ACCOUNT_SOURCE ;
	private  String KEY_USERNAME ;
	private  String KEY_DOMAIN ;
	private  String KEY_PASSWORD ;
	private  String KEY_PROXY ;
	private  String KEY_PORT;
	private  String KEY_PROTOCOL;
	
	private ListPreference mPrefAccountSource;
	private EditTextPreference mPrefUserName;
	private EditTextPreference mPrefDomain;
	private EditTextPreference mPrefPassword;
	private EditTextPreference mPrefProxy;
	private EditTextPreference mPrefPort;
	private ListPreference mPrefProtocol;

	
}