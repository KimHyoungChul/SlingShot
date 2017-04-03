package com.cisco.slingshot.ui.quickcall;

import android.content.Context;
import android.database.Cursor;
import android.os.Handler;
import android.util.AttributeSet;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.LinearLayout;
import android.widget.ListView;

import com.cisco.slingshot.R;
import com.cisco.slingshot.contact.Contact;
import com.cisco.slingshot.contact.ContactDatabase;
import com.cisco.slingshot.contact.ContactManager;
import com.cisco.slingshot.utils.Util;

public class QuickCallContactListView extends  QuickCallViewBase implements OnItemClickListener{

	public final static String LOG_TAG = "QuickCallContactListView"; 
	
	private ListView mContactList = null;
	
	private QuickCallContactListAdapter mContactAdapter;
	
	private int _currentContactListPosition = 0;
	
	private View 		mRootView;
	private Context		mContext;
	
	private Cursor 		mDataCursor = null;
	
	private Handler mainHandler;

	
	public QuickCallContactListView(Context context) {
		super(context);
		mContext = context;
		initView();
	}
	
	public QuickCallContactListView(Context context, AttributeSet attrs) {
		super(context, attrs);
		mContext = context;
		initView();
	}


	public QuickCallContactListView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		mContext = context;
		initView();
	}
	
	private void initView(){
		mainHandler = new Handler();
		mRootView = (LinearLayout)LayoutInflater.from(mContext).inflate(R.layout.diag_outgoing_call_launcher_contact_list, null);
		this.addView(mRootView);
		
		mContactList = (ListView)mRootView.findViewById(R.id.diag_outgoing_call_launcher_contact_list_list);

		mContactAdapter = new QuickCallContactListAdapter(mContext);
		mContactList.setAdapter(mContactAdapter);
		mContactList.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
		mContactList.setOnItemClickListener(this);
		mContactList.setDividerHeight(0);
		
		mContactList.setOnFocusChangeListener(new OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean focused) {
                if( focused ) {
                	mContactList.setSelection(_currentContactListPosition);
                }
            }
        });
		
		
		mContactList.setOnItemSelectedListener(new OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> parent, View view,
                    int position, long id) {
            	 _currentContactListPosition = position;
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
            
        });     
		
		mContactList.setOnKeyListener(new View.OnKeyListener() {
			
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
							Contact user = (Contact)mContactAdapter.getItem(_currentContactListPosition);
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
		Contact user = (Contact)mContactAdapter.getItem(position);
		if(user == null){
			return;
		}
		getLauncherEventLister().onMenuForward(QuickCallLauncherDialog.SEL_CONTACT, user);
	}
	
	//none-block update 
	public void update(){
		ContactManager.getInstance(mContext).queryAllContact(new ContactManager.QueryCallback() {
			
			@Override
			public void onDone(Cursor data) {
				mDataCursor = data;
				updateViewInUI();
				
			}
		});
	}
	
	private void updateViewInUI(){
		
		mainHandler.post(new Runnable(){

			@Override
			public void run() {
				mContactAdapter.changeCursor(mDataCursor);
			}
			
		});
	}
	
	public void closeQueryWhenQuit(){
		Util.S_Log.d(LOG_TAG, "close");
		if(mDataCursor == null)return;
		mDataCursor.close();
	}

	
}