package com.cisco.slingshot.ui.aquarius;

import java.io.InputStream;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.cisco.slingshot.R;
import com.cisco.slingshot.contact.Contact;
import com.cisco.slingshot.utils.AsyncCallTask;

public class AquariusContactDetailView extends  AquariusViewBase{
	public final static String LOG_TAG = "QuickCallContactDetailView"; 
	private Context		 mContext;
	
    TextView 	mUserName;
    TextView 	mUserAddress;
    ImageView 	mUserImage;
    //current user
    Contact 	mUserShown = null;
	
	public AquariusContactDetailView(Context context) {
		super(context);
		mContext = context;
		initView();
	}
	
	public AquariusContactDetailView(Context context, AttributeSet attrs) {
		super(context, attrs);
		mContext = context;
		initView();
	}


	public AquariusContactDetailView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		mContext = context;
		initView();
	}
	
	
	private void initView(){
		View layout = LayoutInflater.from(mContext).inflate(R.layout.diag_outgoing_call_contact_detail, null);
        
		mUserName 		= (TextView)layout.findViewById(R.id.diag_outgoing_user_name);       
        mUserAddress 	= (TextView)layout.findViewById(R.id.diag_outgoing_user_address);
        mUserImage 		= (ImageView)layout.findViewById(R.id.diag_outgoing_user_image);
        //support key
        
        LinearLayout layoutCall = (LinearLayout)layout.findViewById(R.id.diag_outgoing_layout_call);
        layoutCall.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				//getLauncherEventLister().onMenuClose();	
				if(mUserShown!=null){
					try{
						AsyncCallTask.newTask(mContext, 
											  AsyncCallTask.ASYNC_OUTGOING, 
											  mUserShown).execute();
										
					}catch(Exception e){
						e.printStackTrace();
					}
				}		
			} 
		});
        
        layoutCall.setOnKeyListener(new View.OnKeyListener() {
			@Override
			public boolean onKey(View v, int keyCode, KeyEvent event) {
				if(event.isDown()){
					switch (keyCode) {
						case KeyEvent.KEYCODE_ESCAPE:
						case KeyEvent.KEYCODE_DPAD_LEFT:
						case KeyEvent.KEYCODE_BACK:
						{
							//getLauncherEventLister().onMenuBack(QuickCallLauncherDialog.SEL_CONTACTDETAIL, null);
							return true;			
						}
					}
				}
				return false;
			}
		});
        
        
        LinearLayout layoutCancel = (LinearLayout)layout.findViewById(R.id.diag_outgoing_layout_cancel);
        layoutCancel.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				//getLauncherEventLister().onMenuBack(QuickCallLauncherDialog.SEL_CONTACTDETAIL, null);
			}	
        });		
        
        layoutCancel.setOnKeyListener(new View.OnKeyListener() {
			@Override
			public boolean onKey(View v, int keyCode, KeyEvent event) {
				if(event.isDown()){
					switch (keyCode) {
						case KeyEvent.KEYCODE_ESCAPE:
						case KeyEvent.KEYCODE_DPAD_LEFT:
						case KeyEvent.KEYCODE_BACK:
						{
							//getLauncherEventLister().onMenuBack(QuickCallLauncherDialog.SEL_CONTACTDETAIL, null);
							return true;			
						}
					}
				}
				return false;
			}
		});
        this.addView(layout);
	}
	
	
	public void showContact(final Contact user){		
		//update view
		mUserShown = user;

		mUserName.setText(mUserShown.get_username());
		mUserAddress.setText(mUserShown.get_address());
        InputStream photo = Contact.findPhotoInAssets(mContext, mUserShown.get_address());
        if(photo!=null){
        	mUserImage.setImageDrawable(Drawable.createFromStream(photo, null));
        }else{
        	mUserImage.setImageResource(R.drawable.contact_photo_default1);
        }
        
        this.invalidate();
        this.show();
	}
	
	public void setContact(final Contact user){		
		//update view
		mUserShown = user;

		mUserName.setText(mUserShown.get_username());
		mUserAddress.setText(mUserShown.get_address());
        InputStream photo = Contact.findPhotoInAssets(mContext, mUserShown.get_address());
        if(photo!=null){
        	mUserImage.setImageDrawable(Drawable.createFromStream(photo, null));
        }else{
        	mUserImage.setImageResource(R.drawable.contact_photo_default1);
        }   
	}
      
}