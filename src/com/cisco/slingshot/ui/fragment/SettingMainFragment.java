package com.cisco.slingshot.ui.fragment;

import java.util.ArrayList;

import android.app.Fragment;
import android.app.FragmentTransaction;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.cisco.slingshot.R;
import com.cisco.slingshot.utils.Util;

/**
 * 
 * Setting Fragment: Video settings & Account settings, etc
 * @author yuancui
 *
 */  
class SettingMainFragment extends Fragment implements OnItemClickListener{
	private static final String TAG = "SettingMainFragment";
	private static final int INDEX_ACCOUNTSETTING 	= 0;
	private static final int INDEX_VIDEOSETTING	  	= 1;
	private static final int INDEX_CALLSETTING      = 2;
	private static final int INDEX_OTHERETTING      = 3;
	
	private Context 	mContext;
	
	//view
	private View 		mFragmentView;
	private ListView 	mListView;
	//Adapter for list view
	private SettingsMenuAdapter mSettingsMenuAdapter;
	
	//Sub-fragment
	//private AccountSettingsFragment mAccountSettingsFragment = null;
	//private VideoSettingsFragment 	mVideoSettingsFragment = null;
	
	//Save the operation
	private int mCurCheckPosition = 0;
	
	
	
    @Override 
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState){
    	  mFragmentView = inflater.inflate(R.layout.settings, container, false);
    	  return mFragmentView;
    }
    
    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mContext = this.getActivity();
        
        initView();
        
        //Show pre-selected item
        
        if (savedInstanceState != null) {
            // Restore last state for checked position.
            mCurCheckPosition = savedInstanceState.getInt("curChoice", 0);
            Util.S_Log.d(TAG, "============Current choice is " + mCurCheckPosition);
        }
        
    	if(mCurCheckPosition == INDEX_ACCOUNTSETTING){
    		showAccountSettings();
    	}else if(mCurCheckPosition == INDEX_VIDEOSETTING){
    		showVideoSettings();
    	}else if(mCurCheckPosition == INDEX_CALLSETTING){
    		showCallSettings();
    	}else if(mCurCheckPosition ==INDEX_OTHERETTING){
    		showOtherSettings();
    	}
    	else{
    		Log.e(TAG, "Index saved can't be found in the current version");
    		showAccountSettings();
    	}
    }
    
	/**
	 * initialize the Selection List
	 * 
	 */
	private void initView(){
		mListView = (ListView)mFragmentView.findViewById(R.id.settingsListView);

		/*Adapter*/
		mSettingsMenuAdapter = new SettingsMenuAdapter(mContext);
		
		//Add "User Account"
		mSettingsMenuAdapter.addItem(new MenuItem((String)mContext.getResources().getText(R.string.settings_main_item_user_account), 
													(String)mContext.getResources().getText(R.string.settings_main_item_user_account_des), 
													R.drawable.ic_menu_account
												  ){
			@Override
			public void handleMenuIntent(){
				showAccountSettings();
			}
		});
		//Add "Video Profile"
		mSettingsMenuAdapter.addItem(new MenuItem((String)mContext.getResources().getText(R.string.settings_main_item_video_profile),
													(String)mContext.getResources().getText(R.string.settings_main_item_video_profile_des),
													android.R.drawable.ic_menu_camera){
			@Override
			public void handleMenuIntent(){
				showVideoSettings();
			}
		});
		// Call
		mSettingsMenuAdapter.addItem(new MenuItem((String)mContext.getResources().getText(R.string.settings_main_item_call), 
													(String)mContext.getResources().getText(R.string.settings_main_item_call_des),
													android.R.drawable.ic_menu_call){
			@Override
			public void handleMenuIntent(){
				showCallSettings();
			}
		});
		
		//others
		mSettingsMenuAdapter.addItem(new MenuItem((String)mContext.getResources().getText(R.string.settings_main_item_others), 
				(String)mContext.getResources().getText(R.string.settings_main_item_others_des),
				android.R.drawable.ic_menu_preferences){
			@Override
				public void handleMenuIntent(){
				showOtherSettings();
			}
		});		
		
		mListView.setAdapter(mSettingsMenuAdapter);
		
		mListView.setOnItemClickListener(this);
		mListView.setChoiceMode(ListView.CHOICE_MODE_SINGLE);

	}
	
	/**
	 * Show Account Settings
	 */
    private void showAccountSettings(){
		mListView.setItemChecked(mCurCheckPosition,true);
	
		AccountSettingsFragment fragment = new AccountSettingsFragment();
        FragmentTransaction ft = getFragmentManager().beginTransaction();
        ft.replace(R.id.settingsDetails, fragment);
        ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
        ft.commit();
        
	
    }
	/**
	 * Show Video Settings
	 */
    private void showVideoSettings(){
		mListView.setItemChecked(mCurCheckPosition,true);
        
		VideoSettingsFragment fragment = new VideoSettingsFragment();
        FragmentTransaction ft = getFragmentManager().beginTransaction();
        ft.replace(R.id.settingsDetails, fragment);
        ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
        ft.commit();
    }
    
    
	/**
	 * Show Call Settings
	 */
    private void showCallSettings(){
		mListView.setItemChecked(mCurCheckPosition,true);
        
		CallSettingsFragment fragment = new CallSettingsFragment();
        FragmentTransaction ft = getFragmentManager().beginTransaction();
        ft.replace(R.id.settingsDetails, fragment);
        ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
        ft.commit();
    }
    
    
	/**
	 * Show Phter Settings
	 */
    private void showOtherSettings(){
		mListView.setItemChecked(mCurCheckPosition,true);
        
		OtherSettingsFragment fragment = new OtherSettingsFragment();
        FragmentTransaction ft = getFragmentManager().beginTransaction();
        ft.replace(R.id.settingsDetails, fragment);
        ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
        ft.commit();
    }
    
    
    
    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt("curChoice", mCurCheckPosition);
    }
    
	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position, long id) 
	{		
		mCurCheckPosition = position;
		MenuItem handler = (MenuItem)mSettingsMenuAdapter.getItem(position);
		if (handler != null)
		{
			handler.handleMenuIntent();
		}
		else
		{
			Util.S_Log.d(TAG, "No handler for item " );
		}
	
	}

	/*Private class*/
	
	class SettingsMenuAdapter extends BaseAdapter{
		
		public SettingsMenuAdapter(Context ctx){
			mContext = ctx;
		}
		
		@Override
		public int getCount() {
            return mMenuItemList.size();
        }
		@Override
        public Object getItem(int pos) {
            return mMenuItemList.get(pos);
        }
		@Override
        public long getItemId(int pos) {
            return pos;
        }
 
       @Override
        public View getView(int position, View convertView, ViewGroup parent){
    	   
		   	convertView = LayoutInflater.from(mContext).inflate(R.layout.settings_menu_item, null);
		   	
		   	MenuItem item= mMenuItemList.get(position);
		   	
		   	ImageView image = (ImageView)convertView.findViewById(R.id.menu_item_icon);
		   	image.setImageResource(item.mIconId);
		   	
			TextView menuItem = (TextView)convertView.findViewById(R.id.menu_item_name);
			menuItem.setText(item.mName);
			
			TextView menuDesc = (TextView)convertView.findViewById(R.id.menu_item_description);
			menuDesc.setText(item.mDescription);
    	    return convertView;
        }	
       
        public void addItem(MenuItem item){
        	mMenuItemList.add(item);
        }
        
        
        private Context mContext;
    	private ArrayList<MenuItem> mMenuItemList = new ArrayList<MenuItem>();
    	
	}
	
	private abstract class MenuItem {
		
		public MenuItem(String name, String description,int iconId){
			mName = name;
			mDescription = description;
			mIconId = iconId;
		}
		 
		abstract public void handleMenuIntent();
		
		public String mName;
		public String mDescription;
		public int mIconId;
	}
}