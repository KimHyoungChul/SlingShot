package com.cisco.slingshot.ui.quickcall;

import android.content.Context;
import android.content.Intent;
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
import com.cisco.slingshot.activity.FragmentMainActivity;
import com.cisco.slingshot.utils.Util;

public class QuickCallMenuView extends  QuickCallViewBase implements OnItemClickListener{

	public final static String LOG_TAG = "QuickCallMenuView"; 
	
	private int 		 mCurCheckPosition = 0;
	private LinearLayout mRootView;
	private ListView	 mListView;
	private Context		 mContext;
	 
	private QuickCallMenuAdapter mMenuAdapter;
	
	public QuickCallMenuView(Context context) {
		super(context);
		mContext = context;
		initView();
	}
	
	public QuickCallMenuView(Context context, AttributeSet attrs) {
		super(context, attrs);
		mContext = context;
		initView();
	}


	public QuickCallMenuView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		mContext = context;
		initView();
	}
	
	
	private void initView(){
		mRootView = (LinearLayout)LayoutInflater.from(mContext).inflate(R.layout.diag_outgoing_call_launcher_main_menu, null);
		this.addView(mRootView);
		
        mListView = (ListView)mRootView.findViewById(R.id.diag_outgoing_call_launcher_main_menu_list);
        mListView.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
    	
    	if (mListView != null)
		{	
			mMenuAdapter = new QuickCallMenuAdapter(mContext);
			 
			mMenuAdapter.addItem(new QuickCallMenuItem(mContext.getString(R.string.diag_outgoing_call_launcher_main_menu_item_quick_call),
								android.R.drawable.ic_menu_call,true)
			{
				@Override
				public void handleMenuIntent(){					
					getLauncherEventLister().onMenuForward(QuickCallLauncherDialog.SEL_MENU, null);
				}
			});
			mMenuAdapter.addItem(new QuickCallMenuItem(mContext.getString(R.string.diag_outgoing_call_launcher_main_menu_item_launch_app),
					R.drawable.ic_settings2,false)
			{
				@Override
				public void handleMenuIntent(){
					getLauncherEventLister().onMenuClose();
					Intent intent = new Intent(mContext, FragmentMainActivity.class);
					intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK );
					intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
					intent.addFlags(Intent.FLAG_ACTIVITY_BROUGHT_TO_FRONT);
					mContext.startActivity(intent);
				}
			});
		
			mListView.setOnKeyListener(new View.OnKeyListener() {
				
				@Override
				public boolean onKey(View v, int keyCode, KeyEvent event) {
					Util.S_Log.d(LOG_TAG, "Key Code: " + keyCode);
					if(event.isDown()){
						switch (keyCode) {
							case KeyEvent.KEYCODE_ESCAPE:
							case KeyEvent.KEYCODE_BACK:
							case KeyEvent.KEYCODE_DPAD_LEFT:
							{
								getLauncherEventLister().onMenuBack(QuickCallLauncherDialog.SEL_MENU, null);
								return true;			
							}
							case KeyEvent.KEYCODE_DPAD_RIGHT:
							{
								QuickCallMenuItem item = (QuickCallMenuItem)mMenuAdapter.getItem(mCurCheckPosition);
								item.handleMenuIntent();
								//getLauncherEventLister().onMenuForward(QuickCallLauncherDialog.SEL_MENU, null);
								return true;
							}
							
						}
					}
					
					return false;
				}
			});		
			
			
			mListView.setOnFocusChangeListener(new OnFocusChangeListener() {

				@Override
				public void onFocusChange(View v, boolean hasFocus) {
					if(hasFocus){
						mListView.setSelection(mCurCheckPosition);
					}
				}
				
			});
			
			mListView.setOnItemSelectedListener(new OnItemSelectedListener() {

				@Override
				public void onItemSelected(AdapterView<?> parent, View view,
	                    int position, long id) {
					mCurCheckPosition = position;
				}

				@Override
				public void onNothingSelected(AdapterView<?> arg0) {
					
				}
				
			});
			mListView.setAdapter(mMenuAdapter);
			mListView.setOnItemClickListener(this);
			mListView.requestFocus();
			Util.S_Log.d(LOG_TAG, "setChoiceMode");
			
			
		}
		else
		{
			Util.S_Log.d(LOG_TAG, "ListView is null, nothing to display");
		}    	
	}
   
    
	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position, long id) 
	{		
		QuickCallMenuItem handler = (QuickCallMenuItem)mMenuAdapter.getItem(position);
		mCurCheckPosition = position;
		handler.handleMenuIntent();
	}
	
	public void addMenuItem(QuickCallMenuItem item){
		mMenuAdapter.addItem(item);
	}  	
	
	
}