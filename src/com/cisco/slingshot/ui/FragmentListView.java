package com.cisco.slingshot.ui;

import android.content.Context;
import android.os.Handler;
import android.util.AttributeSet;
import android.widget.ListView;


public class FragmentListView extends ListView{
	
    final private Handler mHandler = new Handler();

    final private Runnable mRequestFocus = new Runnable() {
        public void run() {
        	FragmentListView.this.focusableViewAvailable(FragmentListView.this);
        }
    };

	public FragmentListView(Context ctx) {
		super(ctx);
		mHandler.post(mRequestFocus);
		
	}
	public FragmentListView(Context ctx, AttributeSet attrs) {
		super(ctx,attrs);
		mHandler.post(mRequestFocus);
		
	}
	public FragmentListView(Context ctx, AttributeSet attrs, int defStyle) {
		super(ctx,attrs);
		mHandler.post(mRequestFocus);
	}
	
}