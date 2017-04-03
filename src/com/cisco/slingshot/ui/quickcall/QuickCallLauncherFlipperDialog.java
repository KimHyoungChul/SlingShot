package com.cisco.slingshot.ui.quickcall;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.content.DialogInterface.OnShowListener;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ViewFlipper;

import com.cisco.slingshot.R;
import com.cisco.slingshot.contact.Contact;
import com.cisco.slingshot.utils.Util;


public class QuickCallLauncherFlipperDialog implements QuickCallEventListener{
	
	
	
	public final static String LOG_TAG = "QuickCallLauncherFlipperDialog";
	
	private Context 			mContext;
	
	//root
	private ViewGroup				_rootView;
	private ViewFlipper				_flipper;
	
	//Three sub-views were overlapped and only one could be visible.
	private QuickCallMenuView 			_menuView;
	//private QuickCallContactListView 	_contactView;
	private QuickCallMultiView          _mMultiView;
	private QuickCallContactDetailView  _contactDetailView;
	
	//Animation
	private Animation _left_in;
	private Animation _left_out;
	private Animation _right_in;
	private Animation _right_out;
	
	
	private Dialog  			mDialogInternal;
	private boolean 			isShown = false;
	
	static private QuickCallLauncherFlipperDialog _self = null;
	private QuickCallLauncherFlipperDialog(Context context){
		mContext = context;	
		//initDialogView();
	}
	
	public static synchronized QuickCallLauncherFlipperDialog getInstance(Context ctx){
		if(_self == null){
			if(ctx == null){
	            throw new IllegalStateException("Creating CallManager need a valid Context.");
			}
			_self = new QuickCallLauncherFlipperDialog(ctx);
		}
		return _self;
	}
	
	
	
	private Dialog initDialogView(){
		_rootView = (ViewGroup)LayoutInflater.from(mContext).inflate(R.layout.diag_outgoing_call_launcher_flipper, null);
		_flipper = (ViewFlipper)_rootView.findViewById(R.id.quick_call_menu_flipper);
		Dialog dialog = new Dialog(mContext,R.style.IncomingcallDialog);
		dialog.setContentView(_rootView);
		
		//dialog.setCancelable(false);
		dialog.setOnCancelListener(new OnCancelListener(){
			@Override
			public void onCancel(DialogInterface dialog) {
				isShown = false;
				_mMultiView.clearWhenQuit();
				//Do gc here to avoid doing this at activity initialization.
				System.gc();
			}
			
		});
		
		dialog.setOnShowListener(new OnShowListener(){
			@Override
			public void onShow(DialogInterface dialog) {
				isShown = true;
			}
		});
		
		_menuView 	= new QuickCallMenuView(mContext);
		_menuView.setLauncherEventLister(this);
		
		//_contactView = new QuickCallContactListView(mContext);
		//_contactView.setLauncherEventLister(this);
		_mMultiView = new QuickCallMultiView(mContext);
		_mMultiView.setLauncherEventLister(this);
		
		_contactDetailView = new QuickCallContactDetailView(mContext);
		_contactDetailView.setLauncherEventLister(this);
		
		//add flipper view
		
		_flipper.addView(_menuView);
		//_flipper.addView(_contactView);
		_flipper.addView(_mMultiView);
		_flipper.addView(_contactDetailView);
		
		
		//init amination value
		_left_in 	= AnimationUtils.loadAnimation(mContext, R.anim.quickcall_flipper_left_in);
		_left_out 	= AnimationUtils.loadAnimation(mContext, R.anim.quickcall_flipper_left_out);
		_right_in 	= AnimationUtils.loadAnimation(mContext, R.anim.quickcall_flipper_right_in);
		_right_out 	= AnimationUtils.loadAnimation(mContext, R.anim.quickcall_flipper_right_out);
		
		return dialog;
		
		
	}
	
	/**
	 * Show this dialog.
	 */
	public void show(){
		Util.S_Log.d(LOG_TAG, "ShowDialog");
		if(!isShown){
			mDialogInternal = initDialogView();
			//disableAnimation();
			//_flipper.setDisplayedChild(0);
	    	mDialogInternal.getWindow().setType(WindowManager.LayoutParams.TYPE_SYSTEM_ALERT);
	        mDialogInternal.show();	
	        //isShown = true;
		}
        
	}
	
    public void cancel(){
    	if(isShown){
	    	if(mDialogInternal !=null){
	    		mDialogInternal.cancel();
	    		mDialogInternal = null;
	    		//isShown = false;
	    	}
    	}
    }
    
    
    private void disableAnimation(){
		_flipper.setInAnimation(null);
		_flipper.setOutAnimation(null);
    }
    
	
	private void loadBackAnimation(){
		_flipper.setInAnimation(_right_in);
		_flipper.setOutAnimation(_right_out);
	}
	
	private void loadForwardAnimation(){
		_flipper.setInAnimation(_left_in);
		_flipper.setOutAnimation(_left_out);
	}


    public final static int SEL_MENU = 0;
    public final static int SEL_CONTACT = 1;
    public final static int SEL_CONTACTDETAIL = 2;
    
	@Override
	public void onMenuBack(int selection, Object arg) {
		
		loadBackAnimation();
		switch(selection){
		case SEL_MENU:
			this.onMenuClose();
			break;
		case SEL_CONTACT:
			_flipper.showPrevious();
			break;
		case SEL_CONTACTDETAIL:
			//_mMultiView.restoreState();
			_flipper.showPrevious();	
			break;
		
		}
		
	}

	@Override
	public void onMenuForward(int selection,Object arg) {
		
		loadForwardAnimation();
		switch(selection){
		case SEL_MENU:
			_flipper.showNext();
			break;
			
		case SEL_CONTACT:
			if(arg != null){
				_contactDetailView.setContact((Contact)arg);
				_flipper.showNext();
				break;
			}else{
				Log.e(LOG_TAG, "Arg == null!!");
			}
			break;
		case SEL_CONTACTDETAIL:
			break;
		
		}
	}
	
	@Override
	public void onMenuClose() {
		cancel();
	}
}