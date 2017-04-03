package com.cisco.slingshot.ui.fragment;

import java.util.ArrayList;

import android.app.Fragment;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
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
import com.cisco.slingshot.history.HistroyManager;
import com.cisco.slingshot.preference.SlingshotPreference;
import com.cisco.slingshot.utils.NotificationDialog;
import com.cisco.slingshot.utils.Util;


public class OtherSettingsFragment extends Fragment implements OnItemClickListener{
	
		private static final String TAG = "OtherSettingsFragment";
		private static final int INDEX_RESET 	= 0;

		private Context 	mContext;
		
		//view
		private View 		mFragmentView;
		private ListView 	mListView;
		//Adapter for list view
		private SettingsMenuAdapter mSettingsMenuAdapter;
		
		//Save the operation
		private int mCurCheckPosition = 0;
		
		
		
	    @Override 
	    public View onCreateView(LayoutInflater inflater, ViewGroup container,
	            Bundle savedInstanceState){
	    	  mFragmentView = inflater.inflate(R.layout.settings_other, container, false);
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
			mSettingsMenuAdapter.addItem(new MenuItem((String)mContext.getResources().getText(R.string.settings_others_reset), 
														(String)mContext.getResources().getText(R.string.settings_others_reset_des), 
														R.drawable.ic_backup_reset
													  ){
				@Override
				public void handleMenuIntent(){
					doFactoryReset();
				}
			});
			
			
			mListView.setAdapter(mSettingsMenuAdapter);
			
			mListView.setOnItemClickListener(this);
			mListView.setChoiceMode(ListView.CHOICE_MODE_SINGLE);

		}
		
		/**
		 * Show Account Settings
		 */
	    private void doFactoryReset(){
			mListView.setItemChecked(mCurCheckPosition,true);
			
			showFactoryResetWarningDialog();
	    }

	    
	    private void showFactoryResetWarningDialog(){
	    	NotificationDialog.showWarning(	mContext,
	    									(String)mContext.getText( R.string.settings_others_reset_dialog_title), 
	    									(String)mContext.getText( R.string.settings_others_reset_dialog_warning), 
	    									new NotificationDialog.NotificationHandler() {
												
												@Override
												public void handleNotification() {
													// TODO Auto-generated method stub
													 new AsyncClearTask(mContext).execute();
												}
											});
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
	    	   
			   	convertView = LayoutInflater.from(mContext).inflate(R.layout.settings_other_menu_item, null);
			   	
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
		
		
		private class AsyncClearTask extends AsyncTask<Void,Void,Boolean>{
		
			 public AsyncClearTask(Context context){
				 _context = context;
			 }
			
			 @Override
			 protected void onPreExecute(){
				 showResetProcDialog();
			 }

			@Override
			protected Boolean doInBackground(Void... params) {
				try {
					SlingshotPreference.getInstance(mContext).reset();
					HistroyManager.getInstance(mContext).clearHistory();
					java.lang.Thread.sleep(1000);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				return true;
			}
			
			 @Override
			 protected void onPostExecute(Boolean result) {
				 closeResetProcDialog();
	
			 }
			 
			 
			 private void showResetProcDialog(){
				 try{
					 mProgressDialog = new ProgressDialog(_context);
					 mProgressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
					 mProgressDialog.setMessage(_context.getString(R.string.settings_others_reset_process_title));
					 mProgressDialog.setCancelable(false);
					 mProgressDialog.show();	
				 }catch(Exception e){
					 e.printStackTrace();
				 }
			 }
			 private void closeResetProcDialog(){
				 if(mProgressDialog!=null)
					mProgressDialog.cancel();
				 	mProgressDialog = null;
			 }
			private ProgressDialog mProgressDialog = null;
		  	private Context _context;

		}
	
}