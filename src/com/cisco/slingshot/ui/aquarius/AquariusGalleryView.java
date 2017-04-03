package com.cisco.slingshot.ui.aquarius;

import android.content.Context;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.widget.FrameLayout;






public class AquariusGalleryView extends FrameLayout {
	public AquariusGalleryView(Context context) {
		super(context);
		mContext = context;
		initView();
	}
	
	public AquariusGalleryView(Context context, AttributeSet attrs) {
		super(context, attrs);
		mContext = context;
		initView();
	}


	public AquariusGalleryView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		mContext = context;
		initView();
	}
	
	private void initView(){
		//ignore
		//setFocusable(true);
	}
	

	@Override
    public void onFocusChanged( boolean hasFocus, int direction, Rect previousFocus) {
		if(hasFocus){
			//ServiceToast.showMassage(mContext, LOG_TAG + "get Foucus");
			if(mChildView != null){
				/*Pass focus to child view*/
				
				mChildView.requestFocus();
			}
		}else{
			//ServiceToast.showMassage(mContext, LOG_TAG + "loss Foucus");
			//removeContentView();
			
		}
	
	}
	
	@Override
    public boolean onKeyDown (int keyCode, KeyEvent event) {
		if(keyCode == KeyEvent.KEYCODE_BACK){
			removeContentView();
			return true;
		}
		if(keyCode == KeyEvent.KEYCODE_DPAD_DOWN){
			removeContentView();
			return true;
		}
		
		return false;
		
	}
	
	
	public void attachContentView(View child){
		if(mChildView != null){
			removeAllViews();
			mChildView = null;
		}
		
		FrameLayout.LayoutParams layoutParams = new FrameLayout.LayoutParams(
				FrameLayout.LayoutParams.WRAP_CONTENT, 
				FrameLayout.LayoutParams.WRAP_CONTENT,
				Gravity.BOTTOM);
		
		child.setLayoutParams(layoutParams);
		
		mChildView = child;
		this.addView(mChildView);
		setFocusable(true);
		
	}
	
	public void removeContentView(){
		if(mChildView == null){
			return;
		}
		removeAllViews();
		mChildView = null;
		setFocusable(false);
	}
	

	
	private static final String LOG_TAG = "AquariusGalleryView";
	private View mChildView = null;
	private Context mContext ;

	
	
}