package com.cisco.slingshot.ui.quickcall;

import android.content.Context;
import android.database.Cursor;
import android.widget.ImageView;
import android.widget.SimpleCursorAdapter;

import com.cisco.slingshot.R;
import com.cisco.slingshot.contact.Contact;
import com.cisco.slingshot.contact.ContactDatabase;
import com.cisco.slingshot.utils.Util;

public class QuickCallContactListAdapter extends SimpleCursorAdapter{
	
	public QuickCallContactListAdapter(Context context){
		super(
				context,
				R.layout.diag_outgoing_call_contact_item,
				null,
				new String[] {ContactDatabase.COL_NAME,ContactDatabase.COL_ADDRESS},
				new int[] {R.id.contact_list_item_name,R.id.contact_list_item_photo}
			);
		
	}
	
	@Override
	public void setViewImage (ImageView v, String value){
		Util.S_Log.d(LOG_TAG, "setViewImage has been called!!Url: " + value);
		
		String user_address = value;
		/*
		InputStream photo = Contact.findPhotoInAssets(mContext, user_address);
		
        if(photo!=null){
        	v.setImageDrawable(Drawable.createFromStream(photo, null));
        }else{
        	v.setImageDrawable(mContext.getResources().getDrawable(R.drawable.contact_photo_default));
        }
        */		
		v.setImageDrawable(mContext.getResources().getDrawable(R.drawable.contact_photo_default));
	}
	
	@Override
	public int getCount() {
		
		Cursor cor =  this.getCursor();
		if(cor == null) return 0;
		else return cor.getCount();
			
		
    }
	@Override
    public Object getItem(int pos) {
		Cursor cor =  this.getCursor();
		if(cor == null) return null;
		else{
			if(!cor.moveToPosition(pos)){
				return null;
			}
			String name = cor.getString(cor.getColumnIndex(ContactDatabase.COL_NAME));
			String address = cor.getString(cor.getColumnIndex(ContactDatabase.COL_ADDRESS));
			return new Contact(name, address);
		}
    }
	

	public final static String LOG_TAG = "QuickCallContactListAdapter";
}