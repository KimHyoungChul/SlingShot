package com.cisco.slingshot.ui.aquarius;

import android.app.Activity;
import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import com.cisco.slingshot.R;
import com.cisco.slingshot.utils.Util;


public class AquariusView extends AquariusViewBase{
	public final static String LOG_TAG = "Aquarius";
	
	private View _rootView;
	private AquariusGalleryView mContentContainer;
	private AquariusGalleryMenu mMenu;
	private AquariusMenuCommandHandler mCmdHandler = null;
	
	public AquariusView(Context context) {
		super(context);
		mContext = context;
		initView();
	}
	
	public AquariusView(Context context, AttributeSet attrs) {
		super(context, attrs);
		mContext = context;
		initView();
	}


	public AquariusView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		mContext = context;
		initView();
	}
	
	
	private void initView(){
		Util.S_Log.d(LOG_TAG, "Create AquariusView... ");
		_rootView = LayoutInflater.from(mContext).inflate(R.layout.aquarius_main_layout, null);
		this.addView(_rootView);
		
		mContentContainer = (AquariusGalleryView)_rootView.findViewById(R.id.aquarius_menu_detail_container);
		mContentContainer.setNextFocusDownId(R.id.aquarius_menu);
		/*
		mContentContainer.setOnFocusChangeListener(new OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean focused) {
                if( !focused ) {
                	mContentContainer.removeAllViews();
                }
            }
        });
        */
		
		
		mMenu = (AquariusGalleryMenu)_rootView.findViewById(R.id.aquarius_menu);
		
		mCmdHandler = new AquariusMenuCommandHandler(){
			@Override
			void handleMenuCommand(final CommandType type, final View customChildVIew) {
				Activity containerActivity = (Activity)mContext;
				containerActivity.runOnUiThread(new Runnable(){
					@Override
					public void run() {
						switch(type){
						case SHOW_CONTENT:
							/*
							mContentContainer.addView(customChildVIew);
							customChildVIew.requestFocus();
							*/
							mContentContainer.attachContentView(customChildVIew);
							mContentContainer.requestFocus();
							break;
						case HIDE_CONTENT:
							//mContentContainer.removeView(customChildVIew);
							mContentContainer.removeContentView();
						}
					}
				});
			}
			
		};
		mMenu.addMenuCommandHandler(mCmdHandler);
	}
	
	
	
	
}