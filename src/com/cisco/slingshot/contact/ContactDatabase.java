package com.cisco.slingshot.contact;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.util.Log;

import com.cisco.slingshot.utils.Util;



public class ContactDatabase{
	private static final String LOG_TAG = "ContactDatabase";
	
	/**
	 * database file name
	 */
	private static final String DB_FILE="contact.db";
	
	/**
	 * database version
	 */
	private static final int DATABASE_VERSION=1;
	
	/**
	 *  database table
	 */
	private static final String TABLE_CONTACT="contact";
	
	/**
	 * database columns
	 */
	private static final String COL_ID="_id"; 			//contact id
	public static final String COL_NAME="name"; 		//contact name
	public static final String COL_ADDRESS="address"; 	//sip address
	
	private static SQLiteDatabase mDb=null; 
	// synchronize lock
    private final Object mDbLock = new Object(); 
    
    private static ContactDatabase mInstance = null;
    
    private ContactDatabase(){}
    
    public synchronized static ContactDatabase getInstance(Context context){
		if(mInstance == null){
			mInstance = new ContactDatabase();
			mDb = context.openOrCreateDatabase(DB_FILE, 0, null);
			//mDb = SQLiteDatabase.openOrCreateDatabase("/sdcard/prefs.db",null);
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
						+ TABLE_CONTACT + ";";
					mDb.execSQL(dropCmd);
					
					String createCmd = "CREATE TABLE " 
						+ TABLE_CONTACT
						+ "(" 
						+ COL_ID + " INTEGER PRIMARY KEY,"
						+ COL_NAME + " TEXT,"
						+ COL_ADDRESS + " TEXT"
						+ ");";
					
					mDb.execSQL(createCmd);
				}
				
			} catch (SQLiteException e){
				Log.e(LOG_TAG, Log.getStackTraceString(e));
			}
			
			mDb.setVersion(DATABASE_VERSION);
		}
	}
    /**
     * Don't forget call closeQuery() when do not use the cursor any more
     * @return
     */
    public Cursor queryAllUsers(){
    	Util.S_Log.d(LOG_TAG, "ContactDatabase.getAllUsers()!");

		if(mDb == null)
			return null;


		synchronized(mDbLock) {
			Cursor cor = mDb.query(TABLE_CONTACT, null, null, null, null, null, COL_NAME);
			return cor;
		}
    	
    }
    
    public Contact queryUserByName(String name){
    	Util.S_Log.d(LOG_TAG, "ContactDatabase.queryUserByName!");

		if(mDb == null)
			return null;

		final String[] columns = new String[]{COL_NAME,COL_ADDRESS};
		final String selection = "(" + COL_NAME + "==?)";
		final String[] selectionArgs = new String[]{name};

		synchronized(mDbLock) {
			Cursor cor = mDb.query(TABLE_CONTACT, columns, selection, selectionArgs, null, null, null);
			if(cor.moveToFirst() == false){
				cor.close();
				return null;
			}
			
			
			String address = cor.getString(cor.getColumnIndex(COL_ADDRESS));
			cor.close();
			return new Contact(name,address);
		}
    	
    }
    
    public Contact queryUserByAddress(String address){
		if(mDb == null)
			return null;

		final String[] columns = new String[]{COL_NAME,COL_ADDRESS};
		final String selection = "(" + COL_ADDRESS + "==?)";
		final String[] selectionArgs = new String[]{address};

		synchronized(mDbLock) {
			Cursor cor = mDb.query(TABLE_CONTACT, columns, selection, selectionArgs, null, null, null);
			if(cor.moveToFirst() == false){
				cor.close();
				return null;
			}
			String name = cor.getString(cor.getColumnIndex(COL_NAME));
			cor.close();
			return new Contact(name,address);
		}
    }
    
    public void closeQuery(Cursor cor){
    	if(cor == null)
    		return;
    	cor.close();
    }
    
    
    public void setUser(Contact user){
		if(mDb==null)
			return ;
				
		String name = user.get_username();
		String address = user.get_address();
		synchronized(mDbLock){
			if(hasUser(user)){
				/*
				if(itemIsReadOnly( uniqueID, key))
			return false;
				*/
				
				final String whereClause = "(" + COL_NAME + "==?)";
				final String[] whereArgs = new String[]{name};	
				ContentValues cv = new ContentValues();
				cv.put(COL_ADDRESS, address);
				
				mDb.update(TABLE_CONTACT, cv, whereClause, whereArgs);
			}else{
				ContentValues cv = new ContentValues();
				cv.put(COL_NAME, name);
				cv.put(COL_ADDRESS, address);
		
				mDb.insert(TABLE_CONTACT, COL_NAME, cv);	
			}
		}
    }
    
	public void removeUser(Contact user){
		if(mDb == null)
			return ;
		
		final String whereClause = "(" + COL_NAME + "==?)";
		final String[] whereArgs = new String[]{user.get_username()};
		synchronized(mDbLock){
			mDb.delete(TABLE_CONTACT, whereClause, whereArgs);
		}
	}
    
    private boolean hasUser(Contact user){
		if(mDb == null)
			return false;
		
		final String[] columns = new String[]{
				COL_NAME
		};
		
		final String selection = "(" + COL_NAME + "==?)";
		final String[] selectionArgs = new String[]{user.get_username()};
		
		synchronized(mDbLock){
			Cursor cor = mDb.query(TABLE_CONTACT, columns, selection, selectionArgs, null, null, null);
			boolean result = cor.moveToFirst();
			cor.close();
			return result;
		}
	}
    
    
   
}