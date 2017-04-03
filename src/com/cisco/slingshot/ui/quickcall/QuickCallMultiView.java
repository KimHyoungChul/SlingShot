package com.cisco.slingshot.ui.quickcall;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TabHost;
import android.widget.TextView;

import com.cisco.slingshot.R;
import com.cisco.slingshot.utils.Util;

public class QuickCallMultiView  extends  QuickCallViewBase{
	
	
	public QuickCallMultiView(Context context) {
		super(context);
		mContext = context;
		initView();
	}
	
	public QuickCallMultiView(Context context, AttributeSet attrs) {
		super(context, attrs);
		mContext = context;
		initView();
	}


	public QuickCallMultiView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		mContext = context;
		initView();
	}
	
	@Override
	public void setLauncherEventLister(QuickCallEventListener listener){
		_contactlist.setLauncherEventLister(listener);
		_dialer.setLauncherEventLister(listener);
		_historyList.setLauncherEventLister(listener);
	}
	
	
	private void initView(){
		mRootView = LayoutInflater.from(mContext).inflate(R.layout.diag_outgoing_call_launcher_multi_tabs, null);
		this.addView(mRootView);
		
		_contactlist = (QuickCallContactListView)mRootView.findViewById(R.id.quick_call_tab_contactlist);
		_dialer = (QuickCallDialPadView)mRootView.findViewById(R.id.quick_call_tab_dialer);
		_historyList = (QuickCallHistoryListView)mRootView.findViewById(R.id.quick_call_tab_history);
		
		mTab=(TabHost)findViewById(R.id.quick_call_tabhost);

		mTab.setup();

	    //add contact list
	    TabHost.TabSpec spec=mTab.newTabSpec(TAB_TAG_CONTACT);
	    spec.setContent(R.id.quick_call_tab_contactlist);
	    spec.setIndicator(mContext.getString(R.string.diag_outgoing_call_launcher_contact_list_title),mContext.getResources().getDrawable(R.drawable.ic_contact_list2));
	    mTab.addTab(spec);
	    
	    //add dialer
	    spec=mTab.newTabSpec(TAB_TAG_DIALER);
	    spec.setContent(R.id.quick_call_tab_dialer);
	    spec.setIndicator(mContext.getString(R.string.diag_outgoing_call_launcher_dialer_title),mContext.getResources().getDrawable(R.drawable.ic_dialer));
	    mTab.addTab(spec);	   
	    
	    //add history
	    spec=mTab.newTabSpec(TAB_TAG_HISTORY);
	    spec.setContent(R.id.quick_call_tab_history);
	    spec.setIndicator(mContext.getString(R.string.diag_outgoing_call_launcher_history_title),mContext.getResources().getDrawable(R.drawable.ic_history));
	    mTab.addTab(spec);		    
	}
	
	public void restoreState(){
		mTab.setCurrentTabByTag(TAB_TAG_CONTACT);
	}
	
	public void clearWhenQuit(){
		_contactlist.closeQueryWhenQuit();
	}
	
	
	
	
	private final String TAB_TAG_CONTACT = "contact";
	private final String TAB_TAG_DIALER  = "dialer";
	private final String TAB_TAG_HISTORY = "history";
	
	private View 		mRootView;
	private Context		mContext;
	
	private QuickCallContactListView _contactlist;
	private QuickCallDialPadView	 _dialer;
	private QuickCallHistoryListView _historyList;
	private TabHost mTab;
	
}