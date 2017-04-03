package com.cisco.slingshot.history;

import java.sql.Date;
import java.sql.Time;
import java.util.ArrayList;

import com.cisco.slingshot.contact.Contact;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.util.Log;

public class HistoryDatabase{
	private static final String  LOG_TAG = "HistoryDatabase";
	
	/**
	 * database file name
	 */
	private static final String DB_FILE="history.db";
	
	/**
	 * database version
	 */
	private static final int DATABASE_VERSION=3;
	
	/**
	 *  database table
	 */
	private static final String TABLE_HISTORY="history";
	
	
	/**
	 * database columns
	 */
	private static final String COL_ID = "_id"; 			//contact id
	public static final String COL_ADDRESS = "address"; 	//sip address
	public static final String COL_DATE = "date";
	public static final String COL_TIME = "time";
	public static final String COL_DATETIME = "date_time";	//only for order item in a query
	public static final String COL_FLAG_HISTORY_TYPE = "type";

	
	
	private static SQLiteDatabase mDb=null; 
	
	// synchronize lock
    private final Object mDbLock = new Object(); 
    
    private static HistoryDatabase mInstance = null;
    
    private HistoryDatabase(){}
    
    
    public synchronized static HistoryDatabase getInstance(Context context){
		if(mInstance == null){
			mInstance = new HistoryDatabase();
			mDb = context.openOrCreateDatabase(DB_FILE, 0, null);
		}
		mDb.beginTransaction();
		if(mDb != null){
			try{
				initDBFile();
				mDb.setTransactionSuccessful();
			}finally{
				mDb.endTransaction();
			}
		}
		
		return mInstance;
		 
	}
    
    
    private static void initDBFile(){
    	if(mDb != null &&  mDb.getVersion()!=DATABASE_VERSION ){
			try{
				//version certification
				if(mDb.getVersion()<DATABASE_VERSION)
				{
					//Drop table first
					String dropCmd = "DROP TABLE IF EXISTS "
						+ TABLE_HISTORY + ";";
					mDb.execSQL(dropCmd);
				 	
					String createCmd = "CREATE TABLE " 
						+ TABLE_HISTORY
						+ "(" 
						+ COL_ID + " INTEGER PRIMARY KEY,"
						+ COL_ADDRESS + " TEXT,"
						+ COL_DATE + " DATE,"
						+ COL_TIME + " TIME,"
						+ COL_DATETIME + " DATETIME,"
						+ COL_FLAG_HISTORY_TYPE + " INTEGER"
						+ ");";
					
					mDb.execSQL(createCmd);
				}
				
			} catch (SQLiteException e){
				Log.e(LOG_TAG, Log.getStackTraceString(e));
			}
			
			mDb.setVersion(DATABASE_VERSION);
		}
    }
    
    
    public void addHistory(final HistoryItem item){
		if(mDb==null)
			return ;
				
		final String address = item._address;
		final Date date 	 = item._date;
		final Time time 	 = item._time;
		final int type 		 = item._type;

		
		synchronized(mDbLock){
			if(hasAddress(address)){
				
				final String whereClause = "(" + COL_ADDRESS + "==?)";
				final String[] whereArgs = new String[]{address};	
				ContentValues cv = new ContentValues();
				cv.put(COL_DATE, date.toString());
				cv.put(COL_TIME, time.toString());
				cv.put(COL_DATETIME, date.toString() + " " +time.toString());
				cv.put(COL_FLAG_HISTORY_TYPE, type);
				mDb.update(TABLE_HISTORY, cv, whereClause, whereArgs);
				
			}else{
				ContentValues cv = new ContentValues();
				cv.put(COL_ADDRESS, address);
				cv.put(COL_DATE, date.toString());
				cv.put(COL_TIME, time.toString());
				cv.put(COL_DATETIME, date.toString() + " " +time.toString());
				cv.put(COL_FLAG_HISTORY_TYPE, type);
				mDb.insert(TABLE_HISTORY, COL_ADDRESS, cv);	
			}
		}    	
    }
    
    public void removeHistroy(final HistoryItem item){
		if(mDb == null)
			return ;
		
		final String whereClause = "(" 
									+ COL_ADDRESS + "==?) AND (" 
									+ COL_DATE + "==?) AND (" 
									+ COL_TIME + "==?)";
		final String[] whereArgs = new String[]{item._address, item._date.toString(), item._time.toString()};
		synchronized(mDbLock){
			mDb.delete(TABLE_HISTORY, whereClause, whereArgs);
		}    	
    }
    
    /*
    public final Cursor queryAllHistory(){
		if(mDb == null)
			return null;


		synchronized(mDbLock) {
			Cursor cor = mDb.query(TABLE_CONTACT, null, null, null, null, null, COL_DATETIME);

			return cor;
		}    	
    }
    */
    
    public final ArrayList<HistoryItem> queryAllHistory(){
		if(mDb == null)
			return null;


		synchronized(mDbLock) {
			Cursor cor = mDb.query(TABLE_HISTORY, null, null, null, null, null, COL_DATETIME + " DESC");
			final ArrayList<HistoryItem> items = getItemArrayFormCursor(cor);
			cor.close();
			return items;
		}    	
    }
    
    private boolean hasAddress(String address){
		if(mDb == null)
			return false;
		
		final String[] columns = new String[]{
				COL_ADDRESS
		};
		
		final String selection = "(" + COL_ADDRESS + "==?)";
		final String[] selectionArgs = new String[]{address};
		
		synchronized(mDbLock){
			Cursor cor = mDb.query(TABLE_HISTORY, columns, selection, selectionArgs, null, null, null);
			boolean result = cor.moveToFirst();
			cor.close();
			return result;
		}
	}
    
    
    public final HistoryItem[] queryHistoryByAddress(final String address){
    	
		if(mDb == null)
			return null;

		final String selection = "(" + COL_ADDRESS + "==?)";
		final String[] selectionArgs = new String[]{address};

		synchronized(mDbLock) {
			Cursor cor = mDb.query(TABLE_HISTORY, null, selection, selectionArgs, null, null, COL_DATETIME + " DESC");
			final HistoryItem[] items = getItemFormCursor(cor);
			cor.close();
			return items;
		}
    }
    
    public final HistoryItem[] queryHistoryByDate(final String date){
		if(mDb == null)
			return null;

		final String selection = "(" + COL_DATE + "==?)";
		final String[] selectionArgs = new String[]{date};

		synchronized(mDbLock) {
			Cursor cor = mDb.query(TABLE_HISTORY, null, selection, selectionArgs, null, null, COL_DATETIME + " DESC");
			final HistoryItem[] items = getItemFormCursor(cor);
			cor.close();
			return items;
		}
    }
    
    public final HistoryItem[] queryHistoryByIsIncoming(){
    	return null;
	
    }
    
    public final HistoryItem[] queryHistoryByIsOutgoing(){
    	return null;
    }
    
    public final HistoryItem[] queryHistoryByIsMissing(){
    	return null;
    }
    
    public void clearHistory(){
		if(mDb == null)
			return;
		
		final String whereClause = "1";
		final String[] whereArgs = null;
		
		synchronized(mDbLock) {
			mDb.delete(TABLE_HISTORY, whereClause, whereArgs);
		}
    }
    
    private final HistoryItem[] getItemFormCursor(final Cursor c){
    	if(c == null )
    		return null;
    	
		if(c.moveToFirst() == false){
			c.close();
			return null;
		}
		
		final int row_count = c.getCount();
		HistoryItem[] items = new HistoryItem[row_count]; 

		int i = 0;
		do{
			items[i]._address = c.getString(c.getColumnIndex(COL_ADDRESS));
			items[i]._date = Date.valueOf(c.getString(c.getColumnIndex(COL_DATE)));
			items[i]._time = Time.valueOf(c.getString(c.getColumnIndex(COL_TIME)));
			items[i]._type = c.getInt(c.getColumnIndex(COL_FLAG_HISTORY_TYPE));

			i++;
		}while(c.moveToNext() != false);
		return items;
    }
    
    private final ArrayList<HistoryItem> getItemArrayFormCursor(final Cursor c){
    	
    	
    	 ArrayList<HistoryItem> items = new ArrayList<HistoryItem>();
    	 
    	if(c == null )
    		return items;
    	
		if(c.moveToFirst() == false){
			c.close();
			return items;
		}
		
		do{
			HistoryItem item = new HistoryItem();;
			item._address = c.getString(c.getColumnIndex(COL_ADDRESS));
			item._date = Date.valueOf(c.getString(c.getColumnIndex(COL_DATE)));
			item._time = Time.valueOf(c.getString(c.getColumnIndex(COL_TIME)));
			item._type = c.getInt(c.getColumnIndex(COL_FLAG_HISTORY_TYPE));
			items.add(item);
		}while(c.moveToNext() != false);
		return items;
    }
    
    
   
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
	
	
}