package com.cisco.slingshot.ui.aquarius;

import android.content.Context;
import android.database.Cursor;
import android.util.AttributeSet;
import android.util.Log;
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

public class AquariusContactListView extends  AquariusViewBase implements OnItemClickListener{

	public final static String LOG_TAG = "AquariusContactListView"; 
	
	private ContactDatabase mCdb = null;
	
	private ListView mContactList = null;
	
	private Cursor 				mAllUserCursor = null;
	private Cursor				mCurrentUserCursor = null;
	//private SimpleCursorAdapter mContactAdapter = null;
	private AquariusContactListAdapter mContactAdapter;
	
	private int _currentContactListPosition = 0;
	
	private View 		mRootView;
	private AquariusGalleryView mParentGalleryView;
	private Context		mContext;
	private int			mTotalUser = 0;
	
	public AquariusContactListView(Context context) {
		super(context);
		mContext = context;
		initView();
	}
	
	public AquariusContactListView(Context context, AttributeSet attrs) {
		super(context, attrs);
		mContext = context;
		initView();
	}


	public AquariusContactListView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		mContext = context;
		initView();
	}
	
	private void initView(){
		mRootView = (LinearLayout)LayoutInflater.from(mContext).inflate(R.layout.aquarius_contact_list, null);
		this.addView(mRootView);
		
		mParentGalleryView = (AquariusGalleryView)this.getParent();
		
		mContactList = (ListView)mRootView.findViewById(R.id.aquarius_contact_list_list);

		mCdb = ContactDatabase.getInstance(mContext);
		mCurrentUserCursor = mAllUserCursor = mCdb.queryAllUsers();
		mTotalUser = mAllUserCursor.getCount();
		Log.d(LOG_TAG, "Total user number is "+mTotalUser);
		
		mContactAdapter = new AquariusContactListAdapter(mContext);
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
				Log.d(LOG_TAG, "Key Code: " + keyCode);
				if(event.isDown()){
					switch (keyCode) {
						case KeyEvent.KEYCODE_ESCAPE:
						case KeyEvent.KEYCODE_BACK:
						case KeyEvent.KEYCODE_DPAD_LEFT:
						{
							//getLauncherEventLister().onMenuBack(QuickCallLauncherDialog.SEL_CONTACT, null);
							mParentGalleryView.removeContentView();
							return true;			
						}
						
						case KeyEvent.KEYCODE_DPAD_RIGHT:
						{
							Contact user = getContactByPosition(_currentContactListPosition);
							if(user == null){
								return false;
							}
							//getLauncherEventLister().onMenuForward(QuickCallLauncherDialog.SEL_CONTACT, user);
							return true;
						}
						
					}
				}
				
				return false;
			}
		});
		
	}
	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
		Contact user = getContactByPosition(position);
		if(user == null){
			return;
		}
		//getLauncherEventLister().onMenuForward(QuickCallLauncherDialog.SEL_CONTACT, user);
	}
	
	private Contact getContactByPosition(int pos){
		if(!mCurrentUserCursor.moveToPosition(pos)){
			return null;
		}
		String name = mCurrentUserCursor.getString(mCurrentUserCursor.getColumnIndex(ContactDatabase.COL_NAME));
		String address = mCurrentUserCursor.getString(mCurrentUserCursor.getColumnIndex(ContactDatabase.COL_ADDRESS));
		return new Contact(name, address);
	}
	
	
}