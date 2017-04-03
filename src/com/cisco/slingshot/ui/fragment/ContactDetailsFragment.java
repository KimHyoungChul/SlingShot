package com.cisco.slingshot.ui.fragment;

import android.app.Fragment;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.cisco.slingshot.R;
import com.cisco.slingshot.contact.Contact;
import com.cisco.slingshot.contact.ContactEditListener;
import com.cisco.slingshot.utils.AsyncCallTask;

public class ContactDetailsFragment extends Fragment implements OnClickListener{
	
	public static final String LOG_TAG = "ContactDetailsFragment";
	private View 		mFragmentView;
	private Contact 	mUser;
	private Context		mContext;
	
	private ContactEditListener mEditListener = null;
	
	public static ContactDetailsFragment createFragmentForContact(Contact user,ContactEditListener listener){
			ContactDetailsFragment df = new ContactDetailsFragment(user,listener);
			return df;
	}
	public ContactDetailsFragment(Contact user,ContactEditListener listener){
		mUser = user;
		mEditListener = listener;
	}

	public void setEditListener(ContactEditListener listener){
		mEditListener = listener;
	}
	
	public String getShownContactName(){
		return mUser.get_username();
	}
	

	
	private void initFragmentView(View v){
		if(v == null)
			return;
		
		ImageView iv_photo = (ImageView)v.findViewById(R.id.contact_details_image);
		TextView  tv_name  = (TextView)v.findViewById(R.id.contact_details_name);
		TextView  tv_address = (TextView)v.findViewById(R.id.contact_details_address);
		//TextView  tv_status = (TextView)v.findViewById(R.id.contact_details_status);
		tv_name.setText(mUser.get_username());
		tv_address.setText(mUser.get_address());
		
		//Button call_bn = (Button)v.findViewById(R.id.contact_details_call_button);
		ImageButton call_bn = (ImageButton)v.findViewById(R.id.contact_details_call_button);
		Button edit_bn = (Button)v.findViewById(R.id.contact_details_edit_botton);
		Button delete_bn = (Button)v.findViewById(R.id.contact_details_delete_botton);
		
		call_bn.setOnClickListener(this);
		edit_bn.setOnClickListener(this);
		delete_bn.setOnClickListener(this);
	}
	
	
    @Override 
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState){
    	
    	mFragmentView = inflater.inflate(R.layout.contact_details, container,false);
    	initFragmentView(mFragmentView);
    	
    	return mFragmentView;
    }
    
    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mContext = this.getActivity();
    }

	@Override
	public void onClick(View v) {
		int id = v.getId();
		switch(id){
		case R.id.contact_details_call_button:
			callUser(mUser);
			break;
		case R.id.contact_details_edit_botton:			
			editUser(mUser);
			break;
		case R.id.contact_details_delete_botton:
			deleteUser(mUser);
			break;
		}
	}
	
	private void callUser(Contact user){
		
		
		AsyncCallTask.newTask(mContext, 
				  AsyncCallTask.ASYNC_OUTGOING, 
				  user).execute();
		/*
		String name = user.get_username();
		String addr = user.get_address();
		Util.S_Log.d(LOG_TAG, "user = " + name + ", addr = " + addr);
		Intent intent = new Intent(); 
		intent.setClass(this.getActivity(), InCallActivity.class);
		Bundle bundle = new Bundle();
		bundle.putString(getString(R.string.str_contact_name), name);
		bundle.putString(getString(R.string.str_contact_addr), addr);
		intent.putExtra(getString(R.string.str_bundle_outgoing), bundle);
		intent.setAction(InCallActivity.ACTION_CALL_OUTGOING);
		
		startActivity(intent); 
		*/
	}
	
	private void deleteUser(Contact user){
		if(mEditListener == null){
			Log.e(LOG_TAG, "no Edit listener registered");
			return;
		}
		mEditListener.onDelete(user);
	}
	
	private void editUser(Contact user){
		if(mEditListener == null){
			Log.e(LOG_TAG, "no Edit listener registered");
			return;
		}
		mEditListener.onEdit(user);		
	}
	

	
	
}