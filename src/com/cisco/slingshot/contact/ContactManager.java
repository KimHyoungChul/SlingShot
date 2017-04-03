package com.cisco.slingshot.contact;

import android.content.Context;
import android.database.Cursor;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;
import android.os.Message;
import android.util.Log;

public class ContactManager{
	private static final String LOG_TAG = "ContactManager";
	
	private static Context mContext;
	//private static ContactDatabase mDatabase = null;
	
    /*Handle database in worker thread*/
    private HandlerThread	 mContactThread; 
    private ContactHandler   mContactHandler;
    
    
    //Task 
    private static final int QUERY_ALL = 1;
    private static final int INTSERT = 2;
    private static final int CLEAR = 3;
    
	private static class ContactHandler extends Handler{
	    	
	    	public ContactHandler(Looper looper){
	    		super(looper);
	    	}
	        @Override
	        public void handleMessage(Message msg) {
	            switch (msg.what) {
	            case QUERY_ALL:{
	            	QueryCallback cb = (QueryCallback)msg.obj;
	            	if(cb == null)return;
	            	Cursor dataCor = ContactDatabase.getInstance(mContext).queryAllUsers();
	            	cb.onDone(dataCor);
	            }
	            	break;
	            case INTSERT:
	            	//mDatabase.addHistory((HistoryItem)msg.obj);
	            	break;
	            case CLEAR:
	            	//mDatabase.clearHistory();
	            	break;
	                default:
	                    Log.v(LOG_TAG, "Unhandled message: " + msg.what);
	                    break;
	            }
	        }    	
	    }
    
    public static interface QueryCallback{
    	public abstract void onDone(Cursor data);
    }
    
	private ContactManager(Context context){
		mContext = context;
		init();
	}
	
	//singleton
	private static ContactManager mInstance = null;
	public synchronized static ContactManager getInstance(Context context){
		
		if(mInstance == null){
			mInstance = new ContactManager(context);
		}
		return mInstance;
	}
	
	/**
	 * initialize
	 */
	private void init(){
		//mDatabase = ContactDatabase.getInstance(mContext);
		mContactThread = new HandlerThread("ContactThread");
		mContactThread.start();
		mContactHandler = new ContactHandler(mContactThread.getLooper());
	}
	
	//none-block query
	public void queryAllContact(QueryCallback cb){
		mContactHandler.sendMessage(mContactHandler.obtainMessage(QUERY_ALL, cb));
	}
	
	
	
}