
package com.cisco.slingshot.receiver;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.cisco.slingshot.R;
import com.cisco.slingshot.call.CallManager;
import com.cisco.slingshot.contact.Contact;
import com.cisco.slingshot.contact.ContactDatabase;
import com.cisco.slingshot.exjabber.utils.CallStatusChangeListener;
import com.cisco.slingshot.history.HistoryItem;
import com.cisco.slingshot.history.HistroyManager;
import com.cisco.slingshot.net.sip.SipConfCall;
import com.cisco.slingshot.service.SlingShotService;
import com.cisco.slingshot.ui.quickcall.QuickCallLauncherFlipperDialog;
import com.cisco.slingshot.utils.Ringtone;
import com.cisco.slingshot.utils.Util;
/**
 * Listens for incoming SIP calls.
 */
public class IncomingCallReceiver extends BroadcastReceiver {
	
	public static final String ACTION_RECEIVER_INCOMING_CALL = "cisco.slingshot.action.INCOMING_CALL";
    
	private IncomingcallListener mFundaMentalListener = null;
	private IncomingcallListener mReplacedListener = null;
	
	private Context _context;
	private Intent  _current_call_intent = null;
	private boolean _is_curent_call_replace = false;
	private boolean _is_ringing = false;
	
	private Contact _currentCallee;
	private String historyAddr;
	
	private static final String LOG_TAG = "IncomingCallReceiver";
    
    private Dialog mIncomingcallDiag = null;
	

	
	private static IncomingCallReceiver mInstance = null;
	

	
	/**
	 * Get a global CallManager instance
	 * @param context
	 * @return
	 */
	public static synchronized IncomingCallReceiver getInstance(){
		if(mInstance == null){
			mInstance = new IncomingCallReceiver();
		}
		return mInstance;
	}
	
	private IncomingCallReceiver(){
		super();
	}
	
	
	/**
     * Processes the incoming call
     */
    @Override
    public void onReceive(Context context, Intent intent) {
    	Util.S_Log.d(LOG_TAG, "Receive a call");
    	
    	_context = context;
    	_current_call_intent = intent;
    	
    	//check standby status

    	if(Util.MathineStatus.isStandby())return;
    	
    	//Hide quick call launcher if needed
    	QuickCallLauncherFlipperDialog.getInstance(_context).cancel();

    	_currentCallee = getIncomingCallUserInfo(_context,_current_call_intent);
    	
    	
    	if(mReplacedListener != null)
    	{
    		mReplacedListener.onNewIncoming(intent);
    		_is_curent_call_replace = true;
    		//Contact caller_contact = getIncomingCallUserInfo(_context,_current_call_intent);
    		showIncomingcallDialog(_context,_current_call_intent,_currentCallee,_is_curent_call_replace);
    		return;
    	}
    	
    	_is_curent_call_replace = false;
    	
    	/*When not support replace a call, refuse it*/
    	if(CallManager.getInstance(context).isInCall() ||  _is_ringing)
    	{
    		mFundaMentalListener.onDeny(intent);
    		return;
    	}
    	
    	historyAddr = _currentCallee.get_address();
    			
		if(!historyAddr.contains("@")){
			Util.S_Log.d(LOG_TAG, "Append domain to address....");
			SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(_context);
			String domain 	= prefs.getString(_context.getString(R.string.str_pref_domain), "");
			historyAddr += "@"+domain;
		}
    			
    	
    	/*Check if auto answer*/
    	if(isAutoAnswer(context) && mFundaMentalListener != null){
    		mFundaMentalListener.onAnswer(intent);
    		HistroyManager.getInstance(_context).addHistoryNow(/*_currentCallee.get_address()*/historyAddr, HistoryItem.HISTORY_TYPE_INCOMING);
    		return;
    	}
    	
    	/*Take the call here to get the caller info and show the notification;*/
    	
    	//onRinging() can not be called, so we change the ringing state here
		
		showIncomingcallDialog(_context,_current_call_intent,_currentCallee,_is_curent_call_replace);
		processListener(_currentCallee.get_address());
		
    	//CallManager.getInstance(context).takeConfCall(intent, createSimpleListener());
    	
    }
    
    /*
    class AsyncAnswer extends AsyncTask<Void,Void,Boolean>{
    	private Context mContext;
    	private Intent 	mIntent;
    	private boolean mIsRepalce;
    	
    	private Contact mIncomingUser;
    	
    	public AsyncAnswer(Context ctx, Intent i, boolean isReplace){
    		mContext  = ctx;
    		mIntent = i;
    		mIsRepalce = isReplace;
      	 }
    	
       	@Override
       	protected Boolean doInBackground(Void... params) {
    		
       		//mIncomingUser = getIncomingCallUserInfo(mContext,mIntent);
    		
       		return true;
       	}
       	
       	 @Override
       	 protected void onPostExecute(Boolean result) {
       		mIncomingUser = getIncomingCallUserInfo(mContext,mIntent);
       		showIncomingcallDialog(mContext,mIntent,mIncomingUser,mIsRepalce);
       		processListener(mIncomingUser.get_address());

       	 }
       	 
        }
    
    	*/
    
    public void registerFundaMentalListener(IncomingcallListener listener){
    	if(listener instanceof SlingShotService){
    		mFundaMentalListener = listener;
    	}else{
    		throw new IllegalStateException("Fundamental listener for slingshot  should only be a instance of SlingShotService ");
    	}
    	
    }
    
    public void registerReplacedListener(IncomingcallListener listener){
    	mReplacedListener = listener;
    }
    
    private Contact getIncomingCallUserInfo(Context context, Intent intent){
    	
    	
    	SipConfCall  curCall = CallManager.getInstance(context).takeConfCall(intent, createSimpleListener());
    	String user_address = curCall.getPeerProfile().getUserName() + "@" + curCall.getPeerProfile().getSipDomain();
    	
    	//Whether in the contact list
    	Contact userInDb =  Contact.findContactByAddress(_context, user_address);//ContactDatabase.getInstance(context).queryUserByAddress(user_address);
    	if(userInDb != null){
    		return userInDb;
    	}else{
    		return new Contact(user_address, user_address);
    	}
    }
    /*
    
    private Contact getContactFromProfile(Context context, SipProfile caller){
    	
    	
    	//SipConfCall  curCall = CallManager.getInstance(context).takeConfCall(intent, createSimpleListener());
    	String user_address = caller.getUserName() + "@" + caller.getSipDomain();
    	
    	//Whether in the contact list
    	Contact userInDb = ContactDatabase.getInstance(context).queryUserByAddress(user_address);
    	if(userInDb != null){
    		return userInDb;
    	}else{
    		//use user_address as name
    		return new Contact(user_address, user_address);
    	}
    }
     */
	private SipConfCall.Listener createSimpleListener(){
		return new SipConfCall.Listener(){
			
			/*
			@Override 
			public void onRinging(SipConfCall call, SipProfile caller){
				_is_ringing = true;
				Contact caller_contact = getIncomingCallUserInfo(_context,_current_call_intent);
				showIncomingcallDialog(_context,_current_call_intent,caller_contact,_is_curent_call_replace);
				processListener(caller_contact.get_address());
			} 
			*/
            @Override
    		public void onCallEnded(SipConfCall call) {
            	_is_ringing = false;
            	Util.S_Log.d(LOG_TAG, "Default listener,onCallEnded");
            	cancelIncomingcallDialog();
            	HistroyManager.getInstance(_context).addHistoryNow(/*_currentCallee.get_address()*/historyAddr, HistoryItem.HISTORY_TYPE_MISSING);
    		}
            @Override
    		public void onError(SipConfCall call, int errorCode, String errorMessage) {
            	_is_ringing = false;
    			Log.e(LOG_TAG, "Default listener,onError: " + errorMessage);
    			cancelIncomingcallDialog();
    			HistroyManager.getInstance(_context).addHistoryNow(/*_currentCallee.get_address()*/historyAddr, HistoryItem.HISTORY_TYPE_MISSING);
    		}			
            
		};
	}
    
    private boolean isAutoAnswer(Context context){
    	return PreferenceManager.getDefaultSharedPreferences(context)
		.getBoolean(
				context.getString(R.string.key_call_auto_answer), 
				false
		);
    }
    
    /**
     * Call Alert Dialog
     * @param context
     * @param intent
     */
    private void showIncomingcallDialog(final Context context,final Intent intent,final Contact incomingUser,boolean isReplace){
    	
    	
    	/*We start and stop Ringtone here with the dialog*/
    	_is_ringing = true;
    	Ringtone.play(context, Ringtone.RING_IN);
    	
    	if(incomingUser == null){
    		return;
    	}
    	 
        LayoutInflater inflater = LayoutInflater.from(context);
        View layout = inflater.inflate(R.layout.diag_incoming_call, null);
        
        TextView user_info = (TextView)layout.findViewById(R.id.diag_incoming_user_description);
        //user_info.setText("User name");
        user_info.setText(incomingUser.get_username());
        
        ImageView user_image = (ImageView)layout.findViewById(R.id.diag_incoming_user_image);
        //user_image.setImageResource(R.drawable.contact_photo_default1);
        
        InputStream photo = Contact.findPhotoInAssets(context, incomingUser.get_address());
        if(photo!=null){
        	user_image.setImageDrawable(Drawable.createFromStream(photo, null));
        }else{
        	user_image.setImageResource(R.drawable.contact_photo_default1);
        }
        final IncomingcallListener _chosenLisener 
        		= (mReplacedListener != null)?
        		   mReplacedListener:mFundaMentalListener;
        
        //support key
        
        LinearLayout layoutAnswer = (LinearLayout)layout.findViewById(R.id.diag_incoming_layout_answser);
        layoutAnswer.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				Util.S_Log.d(LOG_TAG, "Anwser button is clicked!");
				cancelIncomingcallDialog();
				
		    	if(_chosenLisener != null){
		    		_chosenLisener.onAnswer(intent);
		    		HistroyManager.getInstance(_context).addHistoryNow(/*_currentCallee.get_address()*/historyAddr, HistoryItem.HISTORY_TYPE_INCOMING);
		    	}else{
		    		Util.S_Log.d(LOG_TAG, "Don't register call back, ignore incoming call");
		    	}       				
		    	mIncomingcallDiag = null;
			} 
		});
        
        LinearLayout layoutDeny = (LinearLayout)layout.findViewById(R.id.diag_incoming_layout_deny);
        layoutDeny.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {

				Util.S_Log.d(LOG_TAG, "Deny button is clicked!");
				cancelIncomingcallDialog();
				
				
		    	if(_chosenLisener != null){
		    		_chosenLisener.onDeny(intent);
		    		HistroyManager.getInstance(_context).addHistoryNow(/*_currentCallee.get_address()*/historyAddr, HistoryItem.HISTORY_TYPE_MISSING);
		    	}else{
		    		Util.S_Log.d(LOG_TAG, "Don't register call back, ignore incoming call");
		    	}          				
		    	mIncomingcallDiag = null;
			}
		});
        
        
      //support mouse
        Button btnAnswer = (Button)layout.findViewById(R.id.diag_incoming_btn_answser);
        if(isReplace){
        	btnAnswer.setText(R.string.diag_incoming_call_text_btn_replace);
        }
		exBtnAnswer = btnAnswer;
        btnAnswer.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				Util.S_Log.d(LOG_TAG, "Anwser button is clicked!");
				cancelIncomingcallDialog();
				
		    	if(_chosenLisener != null){
		    		_chosenLisener.onAnswer(intent);
		    		HistroyManager.getInstance(_context).addHistoryNow(/*_currentCallee.get_address()*/historyAddr, HistoryItem.HISTORY_TYPE_INCOMING);
		    	}else{
		    		Util.S_Log.d(LOG_TAG, "Don't register call back, ignore incoming call");
		    	}       				
		    	mIncomingcallDiag = null;
			} 
		});
        
        Button btnDeny = (Button)layout.findViewById(R.id.diag_incoming_btn_deny);
        exBtnDeny = btnDeny;
        btnDeny.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {

				Util.S_Log.d(LOG_TAG, "Deny button is clicked!");
				cancelIncomingcallDialog();
				
		    	if(_chosenLisener != null){
		    		_chosenLisener.onDeny(intent);
		    		HistroyManager.getInstance(_context).addHistoryNow(/*_currentCallee.get_address()*/historyAddr, HistoryItem.HISTORY_TYPE_MISSING);
		    	}else{
		    		Util.S_Log.d(LOG_TAG, "Don't register call back, ignore incoming call");
		    	}          				
		    	mIncomingcallDiag = null;
			} 
		});
        
        

        mIncomingcallDiag = new Dialog(context,R.style.IncomingcallDialog);
        mIncomingcallDiag.setContentView(layout);
        mIncomingcallDiag.setCancelable(false);

        
        /*Use this when  invoked by a background services*/
        mIncomingcallDiag.getWindow().setType(WindowManager.LayoutParams.TYPE_SYSTEM_ERROR);
       // mIncomingcallDiag.getWindow().getAttributes().windowAnimations = R.style.DialogAnimation;

        
        /* set size & pos */  
        
        WindowManager.LayoutParams lp = mIncomingcallDiag.getWindow().getAttributes();                
        WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);  
        Display display = wm.getDefaultDisplay();  
        lp.x = (display.getWidth()/2)-lp.width - 150;
        lp.y =  -(display.getHeight()/2) + 150;
        mIncomingcallDiag.getWindow().setAttributes(lp);  
        
        mIncomingcallDiag.show();	
    }	  
    
    private void cancelIncomingcallDialog(){
    	_is_ringing = false;
    	Ringtone.stop(_context);
    	if(mIncomingcallDiag !=null){
    		mIncomingcallDiag.cancel();
    	}
    }
    
    
    
 // add for Jabber <--> STB
	public static void registerCallListener(CallStatusChangeListener listener)
	{
		if((callListener != null) && (listener != null))
		{
			Util.S_Log.d(LOG_TAG, "!!! (Complain) only support one listener now !!!");
		}
		
		callListener = listener;
	}
	
	public static void processListener(String addr)
	{
		Util.S_Log.d(LOG_TAG, "addr = " + addr);
		
		if(null != callListener)
			callListener.onReceiveCall(addr);
		else
		{
			Util.S_Log.d(LOG_TAG, "!!! callListener == null !!!");
		}
	}
	
	private static Handler handler = new Handler()
	{

		@Override
		public void handleMessage(Message msg) {
			// TODO Auto-generated method stub
			Bundle b = msg.getData();
			String cmd = b.getString(CMD);
			if(cmd.equals(CMD_ANSWER))
			{
				exBtnAnswer.performClick();
			}
			else if (cmd.equals(CMD_DENY))
			{
				exBtnDeny.performClick();
			}
			super.handleMessage(msg);
		}
	};
	
	
	public static void answerCall()
	{
		Message msg = handler.obtainMessage();
		Bundle b = new Bundle();
		b.putString(CMD, CMD_ANSWER);
		msg.setData(b);
		msg.sendToTarget();
	}
	
	public static void denyCall()
	{
		Message msg = handler.obtainMessage();
		Bundle b = new Bundle();
		b.putString(CMD, CMD_DENY);
		msg.setData(b);
		msg.sendToTarget();
	}
	
	private static CallStatusChangeListener callListener = null;
	public static Button exBtnDeny;
	public static Button exBtnAnswer;
	private static final String CMD = "CMD";
	private static final String CMD_ANSWER = "answer";
	private static final String CMD_DENY = "deny";
}
