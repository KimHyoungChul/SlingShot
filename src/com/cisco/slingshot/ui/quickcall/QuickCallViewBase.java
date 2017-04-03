package com.cisco.slingshot.ui.quickcall;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.Keyframe;
import android.animation.LayoutTransition;
import android.animation.ObjectAnimator;
import android.animation.PropertyValuesHolder;
import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;


public class QuickCallViewBase extends LinearLayout{
	public QuickCallViewBase(Context context) {
		super(context);
	}
	
	public QuickCallViewBase(Context context, AttributeSet attrs) {
		super(context, attrs);
	}


	public QuickCallViewBase(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
	}	
	
	
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
	
	public static final String LOG_TAG = "QuickCallViewBase";
	private QuickCallEventListener mQuickCallEventListener = null;

	
}  