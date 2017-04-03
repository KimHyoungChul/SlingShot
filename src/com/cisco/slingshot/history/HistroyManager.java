package com.cisco.slingshot.history;


import java.sql.Date;
import java.sql.Time;
import java.util.ArrayList;
import java.util.Calendar;

import android.content.Context;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;
import android.os.Message;
import android.util.Log;


public class HistroyManager{
	
	private static final String LOG_TAG = "HistroyManager";
	

	
	private static Context mContext;
	//private static HistoryDatabase mDatabase = null;
	
    /*Handle database in worker thread*/
    private HandlerThread	 mHistoryThread; 
    private HistoryHandler   mHistoryHandler;
    
    //private QueryCallback mQueryCallback = null;
    
    //Task 
    private static final int QUERY_ALL = 1;
    private static final int INTSERT = 2;
    private static final int CLEAR = 3;
  
    
    
    private static class HistoryHandler extends Handler{
    	
    	public HistoryHandler(Looper looper){
    		super(looper);
    	}
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
            case QUERY_ALL:{
            	QueryCallback cb = (QueryCallback)msg.obj;
            	if(cb == null)return;
            	ArrayList<HistoryItem> datas = HistoryDatabase.getInstance(mContext).queryAllHistory();
            	cb.onDone(datas);
            }
            	break;
            case INTSERT:
            	HistoryDatabase.getInstance(mContext).addHistory((HistoryItem)msg.obj);
            	break;
            case CLEAR:
            	HistoryDatabase.getInstance(mContext).clearHistory();
            	break;
                default:
                    Log.v(LOG_TAG, "Unhandled message: " + msg.what);
                    break;
            }
        }    	
    }
    
    public static interface QueryCallback{
    	public abstract void onDone(ArrayList<HistoryItem> items);
    }
    
    
	private HistroyManager(Context context){
		mContext = context;
		init();
	}
	 
	//singleton
	private static HistroyManager mInstance = null;
	public synchronized static HistroyManager getInstance(Context context){
		
		if(mInstance == null){
			mInstance = new HistroyManager(context);
		}
		return mInstance;
	}
	
	/**
	 * initialize
	 */
	private void init(){
		//mDatabase = HistoryDatabase.getInstance(mContext);
		mHistoryThread = new HandlerThread("HistoryThread");
		mHistoryThread.start();
		mHistoryHandler = new HistoryHandler(mHistoryThread.getLooper());
	}
	
    
    public void queryAllHistory(QueryCallback cb){
    	mHistoryHandler.sendMessage(mHistoryHandler.obtainMessage(QUERY_ALL, cb));
    }
    
    public void addHistoryNow(String address, final int type){
    	Calendar c = Calendar.getInstance();
		Date date = new Date(c.get(Calendar.YEAR)-1900,c.get(Calendar.MONTH),c.get(Calendar.DAY_OF_MONTH));
		Time time = new Time(c.get(Calendar.HOUR_OF_DAY),c.get(Calendar.MINUTE),c.get(Calendar.SECOND));
		
		mHistoryHandler.sendMessage(mHistoryHandler.obtainMessage(INTSERT, new HistoryItem(address,date,time,type)));
		
		//mDatabase.addHistory(new HistoryItem(address,date,time,type));
		
    }
    
    public void clearHistory(){
    	mHistoryHandler.sendMessage(mHistoryHandler.obtainMessage(CLEAR));
    	//mDatabase.clearHistory();
    }
	
	
    
    
    
    

    
   

	
	
	
	
	
	
	
}