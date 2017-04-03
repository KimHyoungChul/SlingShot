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
import com.cisco.slingshot.ui.FragmentListView;
import com.cisco.slingshot.utils.Util;




public class SelectionFragment extends Fragment implements OnItemClickListener{
	
        private int mCurCheckPosition = 0;
        
    	private static final String TAG = "SelectionFragment";
    	
    	private ContactListFragment mContactListFragment = null;
    	private SettingMainFragment mSettingMainFragment = null;
    	private DevVideoFragment    mDevVideoFragment    = null;
    	
    	private static final int INDEX_CONTACTLIST 	= 0;
    	private static final int INDEX_SETTINGS	  	= 1;
		private static final int INDEX_DEV 			= 2;
    	
    	private View 		mFragmentView;
    	//private ListView	mListView;
    	private FragmentListView	mListView;
    	private Context		mContext;

    	private SelectionMenuAdapter mSelectionMenuAdapter;
    	
    	private boolean _enableDevMode = false;
    	
    	
    	public void addMenuItem(SelectionMenuItem item){
    		mSelectionMenuAdapter.addItem(item);
    	}        
    	
    	
        @Override 
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                Bundle savedInstanceState){
        	
        	  mFragmentView = inflater.inflate(R.layout.selection, container, false);
        	  return mFragmentView;

        }

        @Override
        public void onActivityCreated(Bundle savedInstanceState) {
            super.onActivityCreated(savedInstanceState);
            mContext = this.getActivity();
            
            if (savedInstanceState != null) {
                // Restore last state for checked position.
                mCurCheckPosition = savedInstanceState.getInt("curChoice", 0);
                Util.S_Log.d(TAG, "============Current choice is " + mCurCheckPosition);
            }
            
            //mListView = (ListView)mFragmentView.findViewById(R.id.selection_list);
            mListView = (FragmentListView)mFragmentView.findViewById(R.id.selection_list);
            mListView.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
        	if (mListView != null)
    		{	
    			mSelectionMenuAdapter = new SelectionMenuAdapter(mContext);
    			
    			addMenuItem(new SelectionMenuItem((String)mContext.getResources().getText(R.string.main_menu_item_contact), 
    					                           R.drawable.ic_contact_list2){
    				@Override
    				public void handleMenuIntent(){
    					showContact();
    					Util.S_Log.d(TAG, "item 1 selected");
    				}
    			});
    			addMenuItem(new SelectionMenuItem((String)mContext.getResources().getText(R.string.main_menu_item_settings) ,
    					                           R.drawable.ic_settings2){
    				@Override
    				public void handleMenuIntent(){
    					showSettings();
    					Util.S_Log.d(TAG, "item 2 selected");
    				}
    			});

				if(_enableDevMode){
	    			addMenuItem(new SelectionMenuItem("Dev" ,R.drawable.ic_settings2){
	    				@Override
	    				public void handleMenuIntent(){
	    					
	    					showDev();
	    					Util.S_Log.d(TAG, "item 3 selected");
	    				}
	    			});
				}
    			
    			mListView.setAdapter(mSelectionMenuAdapter);
    			mListView.setOnItemClickListener(this);
    			Util.S_Log.d(TAG, "setChoiceMode");
    			
    		}
    		else
    		{
    			Util.S_Log.d(TAG, "ListView is null, nothing to display");
    		}
        	
        	
        	if(mCurCheckPosition == INDEX_CONTACTLIST){
        		showContact();
        	}else if(mCurCheckPosition == INDEX_SETTINGS){
        		showSettings();
        	}else if(mCurCheckPosition == INDEX_DEV){
				showDev();
			}
			else{
        		Log.e(TAG, "Index saved can't be found in the current version");
        		showContact();
        	}
        	
        }
        


        @Override
        public void onSaveInstanceState(Bundle outState) {
            super.onSaveInstanceState(outState);
            outState.putInt("curChoice", mCurCheckPosition);
        }

		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position, long id) 
		{		
			SelectionMenuItem handler = (SelectionMenuItem)mSelectionMenuAdapter.getItem(position);
			mCurCheckPosition = position;
			handler.handleMenuIntent();
		}
		
		void showContact(){
			Util.S_Log.d(TAG,"showContact!!!");
			mListView.setItemChecked(mCurCheckPosition,true);
			
			if(mContactListFragment == null){
				mContactListFragment = new ContactListFragment();
			}
			
			if(!mContactListFragment.isVisible()){
                FragmentTransaction ft = getFragmentManager().beginTransaction();
                ft.replace(R.id.selectionsDetails, mContactListFragment);
                ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
                ft.commit();
			}
			
		}
		
		void showSettings(){
			Util.S_Log.d(TAG,"showSettings!!!");
			mListView.setItemChecked(mCurCheckPosition,true);
			
			if(mSettingMainFragment == null){
				mSettingMainFragment = new SettingMainFragment();
			}
			
			if(!mSettingMainFragment.isVisible()){
                FragmentTransaction ft = getFragmentManager().beginTransaction();
                ft.replace(R.id.selectionsDetails, mSettingMainFragment);
                ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
                ft.commit();
			}
		}

		void showDev(){
			Util.S_Log.d(TAG,"showDev!!!");
			mListView.setItemChecked(mCurCheckPosition,true);
			
			if(mDevVideoFragment == null){
				mDevVideoFragment = new DevVideoFragment();
			}
			
			if(!mDevVideoFragment.isVisible()){
                FragmentTransaction ft = getFragmentManager().beginTransaction();
                ft.replace(R.id.selectionsDetails, mDevVideoFragment);
                ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
                ft.commit();
			}
		}    
		
		
        class SelectionMenuAdapter extends BaseAdapter{
            private Context mContext;
        	private ArrayList<SelectionMenuItem> mMenuItemList = new ArrayList<SelectionMenuItem>();

    		public SelectionMenuAdapter(Context ctx){
    			mContext = ctx;
    		}
            public void addItem(SelectionMenuItem item){
            	mMenuItemList.add(item);
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
        	   if( convertView == null){
        		   	convertView = LayoutInflater.from(mContext).inflate(R.layout.selection_menu_item, null);
        		   	
        		   	SelectionMenuItem item= mMenuItemList.get(position);
        		   	
        		   	ImageView image = (ImageView)convertView.findViewById(R.id.selecton_menu_item_icon);
        		   	image.setImageResource(item.mIconId);
        		   	
    				TextView menuItem = (TextView)convertView.findViewById(R.id.selecton_menu_item_name);
    				menuItem.setText(item.mName);
    				
        	   }
        	   return convertView;
            }	

    	}
    	
    	private abstract class SelectionMenuItem {
    		public String mName;
    		public int mIconId;
    		public SelectionMenuItem(String name,int iconId){
    			mName = name;
    			mIconId = iconId;
    		}
    		abstract public void handleMenuIntent();
    	}


    }