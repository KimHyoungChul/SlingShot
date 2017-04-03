package com.cisco.slingshot.ui.aquarius;

import java.io.InputStream;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.widget.ImageView;
import android.widget.SimpleCursorAdapter;

import com.cisco.slingshot.R;
import com.cisco.slingshot.contact.Contact;
import com.cisco.slingshot.contact.ContactDatabase;
import com.cisco.slingshot.utils.Util;

public class AquariusContactListAdapter extends SimpleCursorAdapter{
	
	public AquariusContactListAdapter(Context context){
		super(
				context,
				R.layout.diag_outgoing_call_contact_item,
				ContactDatabase.getInstance(context).queryAllUsers(),
				new String[] {ContactDatabase.COL_NAME,ContactDatabase.COL_ADDRESS},
				new int[] {R.id.contact_list_item_name,R.id.contact_list_item_photo}
			);
	}
	
	@Override
	public void setViewImage (ImageView v, String value){
		Util.S_Log.d(LOG_TAG, "setViewImage has been called!!Url: " + value);
		
		String user_address = value;
		InputStream photo = Contact.findPhotoInAssets(mContext, user_address);
		
        if(photo!=null){
        	v.setImageDrawable(Drawable.createFromStream(photo, null));
        }else{
        	v.setImageDrawable(mContext.getResources().getDrawable(R.drawable.contact_photo_default));
        }			
	}
	
	public final static String LOG_TAG = "QuickCallContactListAdapter";
}