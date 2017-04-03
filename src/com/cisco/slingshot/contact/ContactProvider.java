package com.cisco.slingshot.contact;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.net.Uri;

public class ContactProvider extends ContentProvider{
	
	
    private static final int PEOPLE = 1;
    private static final int PEOPLE_ID = 2;
    private static final int PEOPLE_NAME = 3;
    private static final int PEOPLE_ADDRESS = 4;
    
    private static final UriMatcher sUriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
    static{
    	
    	sUriMatcher.addURI("contacts", "people/*", PEOPLE);
    	sUriMatcher.addURI("contacts", "people/name", PEOPLE_NAME);
    	sUriMatcher.addURI("contacts", "people/address", PEOPLE_ADDRESS);
    }
    
    private ContactDatabase mContactDB = null;

	@Override
	public int delete(Uri uri, String selection, String[] selectionArgs) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public String getType(Uri uri) {
        int match = sUriMatcher.match(uri);
        switch (match)
        {
            case PEOPLE:
                return "vnd.cisco.cursor.dir/person";
            case PEOPLE_ID:
                return "vnd.android.cursor.item/person";
            case PEOPLE_NAME:
                return "vnd.android.cursor.dir/snail-mail";
            case PEOPLE_ADDRESS:
                return "vnd.android.cursor.item/snail-mail";
            default:
                return null;
        }
	}

	@Override
	public Uri insert(Uri uri, ContentValues values) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean onCreate() {
		// TODO Auto-generated method stub
		mContactDB = ContactDatabase.getInstance(getContext());
		if(mContactDB == null)
			return false;
		else
			return true;
	}

	@Override
	public Cursor query(Uri uri, String[] projection, String selection,
			String[] selectionArgs, String sortOrder) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public int update(Uri uri, ContentValues values, String selection,
			String[] selectionArgs) {
		// TODO Auto-generated method stub
		return 0;
	}
	
}