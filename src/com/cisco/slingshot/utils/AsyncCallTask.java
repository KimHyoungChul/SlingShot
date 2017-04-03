package com.cisco.slingshot.utils;

import java.sql.Date;
import java.sql.Time;
import java.util.Calendar;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.WindowManager;

import com.cisco.slingshot.R;
import com.cisco.slingshot.activity.InCallActivity;
import com.cisco.slingshot.contact.Contact;
import com.cisco.slingshot.history.HistoryDatabase;
import com.cisco.slingshot.history.HistoryItem;
import com.cisco.slingshot.history.HistroyManager;

/**
 * Start InCallActivity after showing a dialog indicator. Typically used when start from a background thread.
 * @author yuancui
 *
 */
public class AsyncCallTask extends AsyncTask<Void,Void,Boolean>{
	
	public static final int ASYNC_INCOMING = 1;
	public static final int ASYNC_OUTGOING = 2;
	
	public static AsyncCallTask newTask(Context ctx , int callType, Contact user){
		return new AsyncCallTask(ctx,callType,user);
	}
	public AsyncCallTask(Context ctx , int callType, Contact user){
  		_context  = ctx;
  		_callType = callType;
  		_to		  = user;
  	 }
  	 
	 @Override
	 protected void onPreExecute(){
		 startDilogActivity();
	 }

	@Override
	protected Boolean doInBackground(Void... params) {
		try {
			java.lang.Thread.sleep(1000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		return true;
	}
	
	 @Override
	 protected void onPostExecute(Boolean result) {
		//closeStartCallDialog();
		if(_callType == ASYNC_OUTGOING){
			if(_to == null){
				Log.e(LOG_TAG, "User == null");
				return;
			}
			callUser(_to);
		}else if(_callType == ASYNC_INCOMING){
			answser();
		}
	 }
	 
	 /*
	 private void showStartCallDialog(){
		 try{
			 mProgressDialog = new ProgressDialog(_context);
			 mProgressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
			 mProgressDialog.setMessage("Starting call...");
			 mProgressDialog.setCancelable(false);
			 mProgressDialog.getWindow().setType(WindowManager.LayoutParams.TYPE_SYSTEM_ALERT);
			 mProgressDialog.show();	
		 }catch(Exception e){
			 e.printStackTrace();
		 }
	 }
	 private void closeStartCallDialog(){
		 if(mProgressDialog!=null)
			mProgressDialog.cancel();
		 	mProgressDialog = null;
	 }
	 */
	 private void answser(){
		Intent newIntent = new Intent(); 
		newIntent.setClass(_context, InCallActivity.class);
		newIntent.setAction(InCallActivity.ACTION_CALL_INCOMING);
		newIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		_context.startActivity(newIntent);
	 }
	 
	 private void callUser(Contact user){
		 
		 
		String name = user.get_username();
		String addr = user.get_address();
		Util.S_Log.d(LOG_TAG, "user = " + name + ", addr = " + addr);
		
		if(!addr.contains("@")){
			Util.S_Log.d(LOG_TAG, "Append domain to address....");
			SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(_context);
			String domain 	= prefs.getString(_context.getString(R.string.str_pref_domain), "");
			addr += "@"+domain;
		}
		
		
		Intent intent = new Intent(); 
		intent.setClass(_context, InCallActivity.class);
		Bundle bundle = new Bundle();
		bundle.putString(_context.getString(R.string.str_contact_name), name);
		bundle.putString(_context.getString(R.string.str_contact_addr), addr);
		intent.putExtra(_context.getString(R.string.str_bundle_outgoing), bundle);
		intent.setAction(InCallActivity.ACTION_CALL_OUTGOING);
		intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		_context.startActivity(intent); 
		
		HistroyManager.getInstance(_context).addHistoryNow(addr, HistoryItem.HISTORY_TYPE_OUTGOING);
	}
	 private void startDilogActivity(){
			Intent newIntent = new Intent(); 
			newIntent.setClass(_context, CallTaskLoadingActivityDialog.class);
			newIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			_context.startActivity(newIntent); 
	 }
	 
	 

	
	private ProgressDialog mProgressDialog = null;
  	private Contact _to = null;
  	private Context _context;
  	private int     _callType;
  	public static final String LOG_TAG = "AsyncStartCallTask";
	
	
	
}