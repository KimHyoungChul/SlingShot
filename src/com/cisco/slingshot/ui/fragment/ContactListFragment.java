package com.cisco.slingshot.ui.fragment;

import android.app.AlertDialog;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.DialogInterface;
import android.database.Cursor;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.EditText;
import android.widget.ListView;
import com.cisco.slingshot.R;
import com.cisco.slingshot.contact.Contact;
import com.cisco.slingshot.contact.ContactDatabase;
import com.cisco.slingshot.contact.ContactEditListener;
import com.cisco.slingshot.contact.ContactManager;
import com.cisco.slingshot.utils.ServiceToast;
import com.cisco.slingshot.utils.Util;


public class ContactListFragment extends Fragment implements OnItemClickListener,ContactEditListener{
	
	
	private static final String TAG =  "ContactListFragment";

	
	//private ContactDatabase mCdb = null;
	
	private ListView mContactList = null;
	private View     mContactListFooter = null;    
	
	//private Cursor 				mAllUserCursor = null;
	//private Cursor				mCurrentUserCursor = null;
	//private SimpleCursorAdapter mContactAdapter = null;
	
	private ContactListAdapter mContactAdapter = null;
	private Cursor mDataCursor = null;
	
	private View 		mFragmentView;
	private Context		mContext;
	
	
	private int mCurCheckPosition = 0;
	//private int mTotalUser = 0;
	
	private Handler mainHandler;
	
	@Override 
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState){
		mContext = this.getActivity();
		mFragmentView = inflater.inflate(R.layout.contactlist, container, false);
		initView();
		return mFragmentView;
		
	}
	
	
	@Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        //mContext = this.getActivity();
        
        if (savedInstanceState != null) {
            // Restore last state for checked position.
            mCurCheckPosition = savedInstanceState.getInt("curChoice", 0);
            Util.S_Log.d(TAG, "============Current choice is " + mCurCheckPosition);
        }
        //initView();
	}
	
    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt("curChoice", mCurCheckPosition);
    }

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
		// TODO Auto-generated method stub
		Util.S_Log.d(TAG, "Click Item in ListView : ");
		Util.S_Log.d(TAG, "(pos, id) = (" + position + ", " + id + ")");
		if(mContactListFooter == view){
			Util.S_Log.d(TAG, "AddOne item had been clicked!");
			showAddAccountDialog();
			return; 
		}
		mCurCheckPosition = position;
		showDetails();
		
	}
	
	@Override
	public void onDestroy(){
		super.onDestroy();
		if(mDataCursor != null)
			mDataCursor.close();
	}
	
	@Override
	public  void onEdit(Contact user){
		showEditAccountDialog(user);
	}
	
	@Override
	public  void onDelete(Contact user){
		showDelAccountDialog(user);
	}
	
	private void showDetails(){
		if(mContactAdapter.getCount() == 0)
			return;
		
		mContactList.setItemChecked(mCurCheckPosition,true);
		
		/*
		mCurrentUserCursor.moveToPosition(mCurCheckPosition);
		String name = mCurrentUserCursor.getString(mCurrentUserCursor.getColumnIndex(ContactDatabase.COL_NAME));
		String address = mCurrentUserCursor.getString(mCurrentUserCursor.getColumnIndex(ContactDatabase.COL_ADDRESS));
		Contact user = new Contact(name, address);
		*/
		Contact user = (Contact)mContactAdapter.getItem(mCurCheckPosition);
				
		ContactDetailsFragment newDf = ContactDetailsFragment.createFragmentForContact(user,this);
		
        FragmentTransaction ft = getFragmentManager().beginTransaction();
        ft.replace(R.id.contactDetails, newDf);
        ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
        ft.commit();	
	}
	
	private void showContact(Contact user){
		
		ContactDetailsFragment newDf = ContactDetailsFragment.createFragmentForContact(user,this);
        FragmentTransaction ft = getFragmentManager().beginTransaction();
        ft.replace(R.id.contactDetails, newDf);
        ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
        ft.commit();	
	}


	
	private void initView()
	{			
		mainHandler = new Handler();
		//Contact list
		mContactList = (ListView)mFragmentView.findViewById(R.id.lstvw_contact);

		//mCdb = ContactDatabase.getInstance(mContext);
		//preLoadContact();

		mContactAdapter = new ContactListAdapter(mContext);
		
		//"Add Account" footer
		LayoutInflater inflater = (LayoutInflater)mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		mContactListFooter = inflater.inflate(R.layout.contactlist_footer, null);
		mContactList.addFooterView(mContactListFooter);
		mContactList.setAdapter(mContactAdapter);
		mContactList.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
		mContactList.setOnItemClickListener(this);
		
		ContactManager.getInstance(mContext).queryAllContact(new ContactManager.QueryCallback() {
			
			@Override
			public void onDone(Cursor data) {
				mDataCursor = data;
				updateViewInUI();
			}
		});	
		
		
			

	}

	
	private void preLoadContact()
	{
		if(ContactDatabase.getInstance(mContext) != null){
			/*
			mCdb.setUser(new Contact("Herbert", 		"huaguan@cisco.com"));
			mCdb.setUser(new Contact("Dashant", 		"pateld@cisco.com"));
			mCdb.setUser(new Contact("Ken", 			"kthomps2@cisco.com"));
			mCdb.setUser(new Contact("Yuan", 			"yuancui@10.74.122.19"));
			mCdb.setUser(new Contact("Dashant19", 		"pateld@10.74.122.19"));
			mCdb.setUser(new Contact("3000", 		"3000@10.74.122.19"));
			*/
			/*
			mCdb.setUser(new Contact("8001", 				"8001@10.74.122.19"));
			mCdb.setUser(new Contact("8002", 				"8002@10.74.122.19"));
			mCdb.setUser(new Contact("8003", 				"8003@10.74.122.19"));
			mCdb.setUser(new Contact("8004", 				"8004@10.74.122.19"));
			mCdb.setUser(new Contact("8005", 				"8005@10.74.122.19"));
			mCdb.setUser(new Contact("8006", 				"8006@10.74.122.19"));
			mCdb.setUser(new Contact("8007", 				"8007@10.74.122.19"));
			mCdb.setUser(new Contact("8008", 				"8008@10.74.122.19"));
			
			mCdb.setUser(new Contact("8011", 				"8011@61.152.148.93"));
			mCdb.setUser(new Contact("8012", 				"8012@61.152.148.93"));
			mCdb.setUser(new Contact("8013", 				"8013@61.152.148.93"));
			
			mCdb.setUser(new Contact("182001", 				"182001@10.74.122.182"));
			mCdb.setUser(new Contact("182002", 				"182002@10.74.122.182"));
			mCdb.setUser(new Contact("182003", 				"182003@10.74.122.182"));
			mCdb.setUser(new Contact("182004", 				"182004@10.74.122.182"));
			mCdb.setUser(new Contact("182005", 				"182005@10.74.122.182"));
			mCdb.setUser(new Contact("182006", 				"182006@10.74.122.182"));
			mCdb.setUser(new Contact("182007", 				"182007@10.74.122.182"));
			mCdb.setUser(new Contact("182008", 				"182008@10.74.122.182"));
			mCdb.setUser(new Contact("182009", 				"182009@10.74.122.182"));
			
			mCdb.setUser(new Contact("yuancui", 			"yuancui@10.74.122.19"));
			mCdb.setUser(new Contact("taoliu2", 			"taoliu2@10.74.122.19"));
			mCdb.setUser(new Contact("zhiqli", 				"zhiqli@10.74.122.19"));
			mCdb.setUser(new Contact("levinlzq", 			"levinlzq@10.74.122.19"));
			*/
			/*
			mCdb.setUser(new Contact("CRDC TP STB 1", 		"crdc@10.74.122.19"));
			mCdb.setUser(new Contact("CRDC TP STB 2", 		"levinlzq@10.74.122.19"));
			*/
			/*
			mCdb.setUser(new Contact("Tao liu", 			"taoliu2@10.74.122.19"));
			mCdb.setUser(new Contact("atl", 				"atl@10.74.122.19"));
			*/
			
			
			/*
			mCdb.setUser(new Contact("Li Chang", 	"licha@10.74.122.100"));
			mCdb.setUser(new Contact("Yuan Cui", 	"yuancui@10.74.122.100"));
			mCdb.setUser(new Contact("Chao Sun", 	"csun2@10.74.122.100"));
			mCdb.setUser(new Contact("Qian Chang", 	"qianchan@10.74.122.100"));
			mCdb.setUser(new Contact("Levin2", 		"levinlzq@10.74.122.100"));
			*/
		}
	}
	
	private void updateContact()
	{
		
		ContactManager.getInstance(mContext).queryAllContact(new ContactManager.QueryCallback() {
			
			@Override
			public void onDone(Cursor data) {
				mDataCursor = data;
				mCurCheckPosition = 0;
				updateViewInUI();
			}
		});
		
		
		/*
		if(mContactAdapter!=null){
			mCdb.closeQuery(mAllUserCursor);
			mCurrentUserCursor = mAllUserCursor = mCdb.queryAllUsers();
			mContactAdapter.changeCursor(mAllUserCursor);
			mTotalUser = mAllUserCursor.getCount();
			mCurCheckPosition = 0;
		}
		*/
		
		
	}
	
	private void updateViewInUI(){
		
		mainHandler.post(new Runnable(){

			@Override
			public void run() {
				mContactAdapter.changeCursor(mDataCursor);
				showDetails();
			}
			
		});
		
	}
	
	/**
	 * Adding-Account Dialog
	 * @param user
	 */
	private void showAddAccountDialog(){
		
		AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
		final View addDlgView = LayoutInflater.from(mContext).inflate(R.layout.diag_add_edit_contact, null);

		
		builder.setTitle(R.string.str_contact_adddlg_title)
		.setView(addDlgView)
		.setPositiveButton((String)mContext.getString(R.string.dialog_nav_ok),new DialogInterface.OnClickListener() {
	           public void onClick(DialogInterface dialog, int id) {
	        	   
	        	    EditText editUsername = (EditText)addDlgView.findViewById(R.id.edttxt_adddlg_username);
		       		String username =  editUsername.getText().toString();
		       		EditText editAddress = (EditText)addDlgView.findViewById(R.id.edttxt_adddlg_address);
		       		String address =  editAddress.getText().toString();
		       		
		       		if(username.equals("") || address.equals("")){
		       			ServiceToast.showMassage(mContext, mContext.getString(R.string.str_contact_warning_invalid_account));
		       			return;
		       		}
	
		       		Util.S_Log.d(TAG, "(username, address) = (" + username + ", " + address + ")");
		       		ContactDatabase.getInstance(mContext).setUser(new Contact(username, address));
		       		updateContact(); // update UI
	           }
	       })
	    .setNegativeButton((String)mContext.getString(R.string.dialog_nav_cancel),new DialogInterface.OnClickListener() {
	           public void onClick(DialogInterface dialog, int id) {
	        	   dialog.cancel();
	           }
	       })
		.create()
		.show();
		
	}
	
	
	private void showEditAccountDialog(Contact user){
		
		AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
		View editDlgView = LayoutInflater.from(mContext).inflate(R.layout.diag_add_edit_contact, null);
		final Contact userToEdit = user;
		final EditText editUsername = (EditText)editDlgView.findViewById(R.id.edttxt_adddlg_username); 
		final EditText editAddress = (EditText)editDlgView.findViewById(R.id.edttxt_adddlg_address);
		editUsername.setText(userToEdit.get_username());
		editAddress.setText(userToEdit.get_address());
		
		
		builder.setTitle(R.string.str_contact_editdlg_title)
		.setView(editDlgView)
		.setPositiveButton((String)mContext.getString(R.string.dialog_nav_ok),new DialogInterface.OnClickListener() {
	           public void onClick(DialogInterface dialog, int id) {
		       		String username =  editUsername.getText().toString();
		       		String address =  editAddress.getText().toString();
	
		       		Util.S_Log.d(TAG, "(username, address) = (" + username + ", " + address + ")");
		       		ContactDatabase.getInstance(mContext).removeUser(userToEdit);
		       		ContactDatabase.getInstance(mContext).setUser(new Contact(username, address));
		       		updateContact(); // update UI
	           }
	       })
	    .setNegativeButton((String)mContext.getString(R.string.dialog_nav_cancel),new DialogInterface.OnClickListener() {
	           public void onClick(DialogInterface dialog, int id) {
	        	   dialog.cancel();
	           }
	       })
		.create()
		.show();
		
	}
	
	private void showDelAccountDialog(Contact user){
		AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
		final Contact userToDelete = user;
		
		builder.setTitle(mContext.getString(R.string.str_contact_deldlg_title) + " " + userToDelete.get_username()+"?")
		.setPositiveButton((String)mContext.getString(R.string.dialog_nav_ok),new DialogInterface.OnClickListener() {
	           public void onClick(DialogInterface dialog, int id) {
		       		Util.S_Log.d(TAG, "(username, address) = (" + userToDelete.get_username() + ", " + userToDelete + ")");
		       		ContactDatabase.getInstance(mContext).removeUser(userToDelete);
		       		updateContact(); // update UI
	           }
	       })
	    .setNegativeButton((String)mContext.getString(R.string.dialog_nav_cancel),new DialogInterface.OnClickListener() {
	           public void onClick(DialogInterface dialog, int id) {
	        	   dialog.cancel();
	           }
	       })
		.create()
		.show();
	}	
}