package com.cisco.slingshot.ui.quickcall;

import java.util.ArrayList;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.util.AttributeSet;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import com.cisco.slingshot.R;
import com.cisco.slingshot.contact.Contact;
import com.cisco.slingshot.contact.ContactDatabase;
import com.cisco.slingshot.history.HistoryItem;
import com.cisco.slingshot.history.HistroyManager;
import com.cisco.slingshot.utils.Util;

public class QuickCallHistoryListView extends  QuickCallViewBase implements OnItemClickListener{

	public final static String LOG_TAG = "QuickCallContactListView"; 
	
	private ListView mHistoryList = null;
	private Button mClearBtn = null;

	//data
	private ArrayList<HistoryItem> mAllItems = null;
	private int			mTotalUser = 0;
	
	private HistroyManager mHistroyManager;

	
	private QuickCallHistoryListAdapter mHistoryAdapter;
	
	private Handler mainHandler;
	
	private int _currentContactListPosition = 0;
	
	private View 		mRootView;
	private Context		mContext;

	
	public QuickCallHistoryListView(Context context) {
		super(context);
		mContext = context;
		initView();
	}
	
	public QuickCallHistoryListView(Context context, AttributeSet attrs) {
		super(context, attrs);
		mContext = context;
		initView();
	}


	public QuickCallHistoryListView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		mContext = context;
		initView();
	}
	
	public void update(){
		mHistroyManager.queryAllHistory(new HistroyManager.QueryCallback() {
			@Override
			public void onDone(ArrayList<HistoryItem> items) {
				mAllItems 	= items;
				mTotalUser 	= mAllItems.size();
				updateViewInUI();
				
			}
		});
	}
	
	private void updateViewInUI(){
		
		mainHandler.post(new Runnable(){

			@Override
			public void run() {
				mHistoryAdapter.changeData(mAllItems);
			}
			
		});
	}
	
	private void initView(){
		mainHandler = new Handler();
		mHistroyManager = HistroyManager.getInstance(mContext);
		
		mRootView = (LinearLayout)LayoutInflater.from(mContext).inflate(R.layout.diag_outgoing_call_launcher_history_list, null);
		this.addView(mRootView);
		mClearBtn = (Button)mRootView.findViewById(R.id.diag_outgoing_call_launcher_history_list_clear);
		mClearBtn.setOnKeyListener(new View.OnKeyListener() {
			
			@Override
			public boolean onKey(View v, int keyCode, KeyEvent event) {
				// TODO Auto-generated method stub
				Util.S_Log.d(LOG_TAG, "Key Code: " + keyCode);
				if(event.isDown()){
					switch (keyCode) {
						case KeyEvent.KEYCODE_ESCAPE:
						case KeyEvent.KEYCODE_BACK:
						case KeyEvent.KEYCODE_DPAD_LEFT:
						{
							getLauncherEventLister().onMenuBack(QuickCallLauncherDialog.SEL_CONTACT, null);
							return true;			
						}		
					}
				}
				
				
				return false;
			}
		});
		mClearBtn.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				
				showClearHistoryDialog();
				
			}
		});
		
		
		mHistoryList = (ListView)mRootView.findViewById(R.id.diag_outgoing_call_launcher_history_list_list);

		mHistoryAdapter = new QuickCallHistoryListAdapter(mContext,mAllItems);
		
		mHistoryList.setAdapter(mHistoryAdapter);
		mHistoryList.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
		mHistoryList.setOnItemClickListener(this);
		mHistoryList.setDividerHeight(0);
		mHistoryList.setNextFocusUpId(R.id.diag_outgoing_call_launcher_history_list_clear);
		mHistoryList.setOnFocusChangeListener(new OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean focused) {
                if( focused ) {
                	mHistoryList.setSelection(_currentContactListPosition);
                }
            }
        });
		
		
		mHistoryList.setOnItemSelectedListener(new OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> parent, View view,
                    int position, long id) {
            	 _currentContactListPosition = position;
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
            
        });     
		
		mHistoryList.setOnKeyListener(new View.OnKeyListener() {
			
			@Override
			public boolean onKey(View v, int keyCode, KeyEvent event) {
				// TODO Auto-generated method stub
				Util.S_Log.d(LOG_TAG, "Key Code: " + keyCode);
				if(event.isDown()){
					switch (keyCode) {
						case KeyEvent.KEYCODE_ESCAPE:
						case KeyEvent.KEYCODE_BACK:
						case KeyEvent.KEYCODE_DPAD_LEFT:
						{
							getLauncherEventLister().onMenuBack(QuickCallLauncherDialog.SEL_CONTACT, null);
							return true;			
						}
						
						case KeyEvent.KEYCODE_DPAD_RIGHT:
						{
							Contact user = getContactByPosition(_currentContactListPosition);
							if(user == null){
								return false;
							}
							getLauncherEventLister().onMenuForward(QuickCallLauncherDialog.SEL_CONTACT, user);
							return true;
						}
						
					}
				}
				
				return false;
			}
		});
		update();
		
	}
	


	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
		Contact user = getContactByPosition(position);
		if(user == null){
			return;
		}
		getLauncherEventLister().onMenuForward(QuickCallLauncherDialog.SEL_CONTACT, user);
	}
	
	private Contact getContactByPosition(int pos){

		if(mTotalUser == 0){
			return null;
		}
		HistoryItem current = mAllItems.get(pos);
		String address = current._address;
		
    	Contact userInDb =  Contact.findContactByAddress(mContext, address);//ContactDatabase.getInstance(mContext).queryUserByAddress(address);
    	if(userInDb != null){
    		//reserve @ in history contact,Append @domain to user in database
    		String historyAddr = userInDb.get_address();
    		if(!historyAddr.contains("@")){
    			Util.S_Log.d(LOG_TAG, "Append domain to address....");
    			SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(mContext);
    			String domain 	= prefs.getString(mContext.getString(R.string.str_pref_domain), "");
    			historyAddr += "@"+domain;
    		}
    		return new Contact(userInDb.get_username(),historyAddr);
    	}else{
    		return new Contact(address,address);
    	}
	}
	
	
	private void showClearHistoryDialog(){
		AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
		
		AlertDialog dialog = builder.setTitle(mContext.getString(R.string.diag_clear_histroy_title))
		.setPositiveButton((String)mContext.getString(R.string.dialog_nav_ok),new DialogInterface.OnClickListener() {
	           public void onClick(DialogInterface dialog, int id) {
	        	   mHistroyManager.clearHistory();
	        	   update();
	           }
	       })
	    .setNegativeButton((String)mContext.getString(R.string.dialog_nav_cancel),new DialogInterface.OnClickListener() {
	           public void onClick(DialogInterface dialog, int id) {
	        	   dialog.cancel();
	           }
	       })
		.create();
		
		dialog.getWindow().setType(WindowManager.LayoutParams.TYPE_SYSTEM_ALERT);
		dialog.show();
	}	
	
	
	
	
	
}