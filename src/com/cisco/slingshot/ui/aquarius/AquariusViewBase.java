package com.cisco.slingshot.ui.aquarius;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.FrameLayout;


public class AquariusViewBase extends FrameLayout{
	public AquariusViewBase(Context context) {
		super(context);
	}
	
	public AquariusViewBase(Context context, AttributeSet attrs) {
		super(context, attrs);
	}


	public AquariusViewBase(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
	}	
	
	
	
	/*
	public QuickCallEventListener getLauncherEventLister(){
		if(mQuickCallEventListener == null){
			return createDefaultCallEventListener();
		}else{
			return mQuickCallEventListener;
		}
	}
	
	public void setLauncherEventLister(QuickCallEventListener listener){
		mQuickCallEventListener = listener;
	}
	*/
	/*
	public void setViewGroupFocusable(boolean focusable){
		setFocusable(focusable);
		for(int i = 0;i < this.getChildCount();i++){
			this.getChildAt(i).setFocusable(focusable);
		}
	}
	*/
	
	public void show(){
		this.setVisibility(View.VISIBLE);
		this.requestFocus();
	}
	
	public void hide(){
		this.setVisibility(View.INVISIBLE);
	}
	
	public void gone(){
		this.setVisibility(View.GONE);
	}
	
	/*
	private QuickCallEventListener createDefaultCallEventListener(){
		return new QuickCallEventListener(){

			@Override
			public void onMenuBack(int selection, Object arg) {
				Log.i(LOG_TAG, "Event Menu Back: " + selection + ", No external handler, using default. ");
			}

			@Override
			public void onMenuForward(int selection, Object arg) {
				Log.i(LOG_TAG, "Event Menu Forward: " + selection + ", No external handler, using default. ");
			}

			@Override
			public void onMenuClose() {
				Log.i(LOG_TAG, "Event Menu Close, No external handler, using default. ");
			}};
	}
	*/
	public static final String LOG_TAG = "QuickCallViewBase";
	//private QuickCallEventListener mQuickCallEventListener = null;

	
}  