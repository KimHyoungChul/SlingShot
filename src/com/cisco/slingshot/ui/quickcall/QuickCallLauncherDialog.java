package com.cisco.slingshot.ui.quickcall;

import android.app.Dialog;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.view.WindowManager;

import com.cisco.slingshot.R;
import com.cisco.slingshot.contact.Contact;
import com.cisco.slingshot.utils.Util;


public class QuickCallLauncherDialog implements QuickCallEventListener{
	
	public final static String LOG_TAG = "QuickCallLauncherDialog";
	
	private Context 			mContext;
	
	//root
	private ViewGroup				_rootView;
	
	//Three sub-views were overlapped and only one could be visible.
	private QuickCallMenuView 			_menuView;
	private QuickCallContactListView 	_contactView;
	private QuickCallContactDetailView  _contactDetailView;
	
	private Dialog  			mDialogInternal;
	
	public QuickCallLauncherDialog(Context context){
		mContext = context;	
		initDialogView();
	}
	
	private void initDialogView(){
		_rootView = (ViewGroup)LayoutInflater.from(mContext).inflate(R.layout.diag_outgoing_call_launcher, null);
		
		_menuView 	= (QuickCallMenuView)_rootView.findViewById(R.id.quick_call_menu);
		_menuView.setLauncherEventLister(this);
		
		_contactView = (QuickCallContactListView)_rootView.findViewById(R.id.quick_call_contact);
		_contactView.setLauncherEventLister(this);
		
		_contactDetailView = (QuickCallContactDetailView)_rootView.findViewById(R.id.quick_call_contact_detail);
		_contactDetailView.setLauncherEventLister(this);
		
		mDialogInternal = new Dialog(mContext,R.style.IncomingcallDialog);
		mDialogInternal.setContentView(_rootView);
		
	}
	/**
	 * Show this dialog.
	 */
	public void show(){
		Util.S_Log.d(LOG_TAG, "ShowDialog");
		
    	mDialogInternal.getWindow().setType(WindowManager.LayoutParams.TYPE_SYSTEM_ALERT);
       // mIncomingcallDiag.getWindow().getAttributes().windowAnimations = R.style.DialogAnimation;
        mDialogInternal.show();			
		
	}
	
    private void cancel(){
    	if(mDialogInternal !=null){
    		mDialogInternal.cancel();
    	}
    }

    
    public final static int SEL_MENU = 0;
    public final static int SEL_CONTACT = 1;
    public final static int SEL_CONTACTDETAIL = 2;
    
	@Override
	public void onMenuBack(int selection, Object arg) {
		switch(selection){
		case SEL_MENU:
			this.onMenuClose();
			break;
		case SEL_CONTACT:
			_contactView.gone();
			_menuView.show();
			break;
		case SEL_CONTACTDETAIL:
			_contactDetailView.gone();
			_contactView.show();			
			break;
		
		}
		
	}

	@Override
	public void onMenuForward(int selection,Object arg) {
		switch(selection){
		case SEL_MENU:
			Util.S_Log.d(LOG_TAG, "onMenuForward,SEL_MENU");
			_menuView.gone();
			_contactView.show();
			break;
			
		case SEL_CONTACT:
			
			Util.S_Log.d(LOG_TAG, "onMenuForward,SEL_CONTACT");
			_contactView.gone();
			if(arg != null){
				_contactDetailView.showContact((Contact)arg);
				break;
			}else{
				Log.e(LOG_TAG, "Arg == null!!");
			}

			break;
		case SEL_CONTACTDETAIL:
			//ignore
			break;
		
		}
		
	}

	@Override
	public void onMenuClose() {
		cancel();
	}
}