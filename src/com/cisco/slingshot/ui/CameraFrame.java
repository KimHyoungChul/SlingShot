package com.cisco.slingshot.ui;

import android.app.Activity;
import android.content.Context;
import android.util.AttributeSet;
import android.view.KeyEvent;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.cisco.slingshot.R;
import com.cisco.slingshot.camera.CameraManager;
import com.cisco.slingshot.camera.CameraSettings;
import com.cisco.slingshot.utils.Util;


public class CameraFrame extends RelativeLayout {
	
	private final static String LOG_TAG = "CameraFrame" ;
	private Context mContext = null;
	private boolean mInSettingMode = false;
	private boolean mIsFirstLayout = true;

	private int mLeft;
	private int mTop;
	private int mHeight;
	private int mWidth;
	
	private int mParentHeight;
	private int mParentWidth;
	
	private boolean isHiden = false;
	
	
	private static final int DELTA_MOVING = 20;
	private static final int DELTA_SCALING = 5;
	
	private RelativeLayout mCameraFrameController;
	private ImageView mArrowUp;
	private ImageView mArrowLeft;
	private ImageView mArrowRight;
	private ImageView mArrowDown;
	
	public CameraFrame(Context ctx){
		super(ctx);
		mContext = ctx;
		init();
	}

	public CameraFrame(Context ctx, AttributeSet attrs){
		super(ctx,attrs);
		mContext = ctx;
		init();
	}
	
	public CameraFrame(Context ctx, AttributeSet attrs, int defStyle){
		super(ctx,attrs,defStyle);
		mContext = ctx;	
		init();
	}
	
	@Override 
	public boolean onKeyDown(int keyCode, KeyEvent event){
		switch(keyCode){
		case KeyEvent.KEYCODE_ENTER:
		case KeyEvent.KEYCODE_DPAD_CENTER:
			//Init controller here 
			initController();
			mInSettingMode = !mInSettingMode;
			if(mInSettingMode){
				updateLayoutParams();
				setControllerVisible(true);
			}else{
				setControllerVisible(false);
				savePosition();
			}
			return true;
			
		case KeyEvent.KEYCODE_DPAD_UP:
			if(mInSettingMode){
				highlightArrow(ARROW_UP,true);
				moveUp();
				return true;
			}
			break;
			
		case KeyEvent.KEYCODE_DPAD_DOWN:
			if(mInSettingMode){
				highlightArrow(ARROW_DOWN,true);
				moveDown();
				return true;
			}
			break;
			
		case KeyEvent.KEYCODE_DPAD_LEFT:
			if(mInSettingMode){
				highlightArrow(ARROW_LEFT,true);
				moveLeft();
				return true;
			}
			break;
			
		case KeyEvent.KEYCODE_DPAD_RIGHT:
			if(mInSettingMode){
				highlightArrow(ARROW_RIGHT,true);
				moveRight();
				return true;
			}
			break;
			
		}
		
		return false;
		
	}
	
	@Override
	public boolean onKeyUp(int keyCode, KeyEvent event){
		switch(keyCode){
		case KeyEvent.KEYCODE_ENTER:
		case KeyEvent.KEYCODE_DPAD_CENTER:
			break;
			
		case KeyEvent.KEYCODE_DPAD_UP:
			if(mInSettingMode){
				highlightArrow(ARROW_UP,false);
				return true;
			}
			break;
			
		case KeyEvent.KEYCODE_DPAD_DOWN:
			if(mInSettingMode){
				highlightArrow(ARROW_DOWN,false);
				return true;
			}
			break;
			
		case KeyEvent.KEYCODE_DPAD_LEFT:
			if(mInSettingMode){
				highlightArrow(ARROW_LEFT,false);
				return true;
			}
			break;
			
		case KeyEvent.KEYCODE_DPAD_RIGHT:
			if(mInSettingMode){
				highlightArrow(ARROW_RIGHT,false);
				return true;
			}
			break;
			
		}
		
		return false;
	}
	
	private void init(){
		//nothing to do
	}
	
	private void savePosition(){
		CameraSettings Settings = CameraManager.getInstance(mContext).getSettings();
		Settings.setLeft(mLeft);
		Settings.setTop(mTop);
		/*
		Settings.setWidth(mWidth);
		Settings.setHeight(mHeight);
		*/
		//write to storage
		Settings.flush();
	}
	

	/*Animation*/
	public final static int MINIMIZE_TO_TOPLEFT = 1;
	public final static int MINIMIZE_TO_TOPRIGHT = 2;
	public final static int MINIMIZE_TO_BOTTOMLEFT = 3;
	public final static int MINIMIZE_TO_BOTTOMRIGHT = 4;
	public final static int MINIMIZE_TO_DEFAULT = 0;
	
	public void minimize(final int direction){
		Util.S_Log.d(LOG_TAG, "Minimize camera view");

		((Activity)mContext).runOnUiThread(new Runnable(){

			@Override
			public void run() {
				Animation minimizeAnimation = null;
				switch(direction){
				case MINIMIZE_TO_TOPLEFT:
					minimizeAnimation = AnimationUtils.loadAnimation(mContext, R.anim.anim_cameraview_minimize_to_topleft);
					break;
				case MINIMIZE_TO_TOPRIGHT:
					minimizeAnimation = AnimationUtils.loadAnimation(mContext, R.anim.anim_cameraview_minimize_to_topright);
					break;
				case MINIMIZE_TO_BOTTOMLEFT:
					minimizeAnimation = AnimationUtils.loadAnimation(mContext, R.anim.anim_cameraview_minimize_to_bottomleft);
					break;
				case MINIMIZE_TO_BOTTOMRIGHT:
					minimizeAnimation = AnimationUtils.loadAnimation(mContext, R.anim.anim_cameraview_minimize_to_bottomright);
					break;		
				case MINIMIZE_TO_DEFAULT:
					minimizeAnimation = AnimationUtils.loadAnimation(mContext, R.anim.anim_cameraview_minimize_to_bottomleft);
					break;
				}
				CameraFrame.this.startAnimation(minimizeAnimation);
			}
			
		});

	}
    
	
	private void initController(){
		if(mCameraFrameController == null){
			mCameraFrameController = (RelativeLayout)this.findViewById(R.id.CameraFrameController);
			mArrowUp = (ImageView)this.findViewById(R.id.arrowUp);
			mArrowLeft = (ImageView)this.findViewById(R.id.arrowLeft);
			mArrowRight = (ImageView)this.findViewById(R.id.arrowRight);
			mArrowDown = (ImageView)this.findViewById(R.id.arrowDown);
		}
	}
	
	private void setControllerVisible(final boolean visible){
		mCameraFrameController.setVisibility(visible?View.VISIBLE:View.INVISIBLE);
	}
	
	private final static int ARROW_UP = 1;
	private final static int ARROW_LEFT = 2;
	private final static int ARROW_RIGHT = 3;
	private final static int ARROW_DOWN = 4;
	
	
	private void highlightArrow(final int which, final boolean toHightlight){
		
		switch(which){
		case ARROW_UP:
			if(toHightlight){
				mArrowUp.setImageDrawable(
							mContext.getResources().getDrawable(R.drawable.arrow_up_black)
						);
			}else{
				mArrowUp.setImageDrawable(
							mContext.getResources().getDrawable(R.drawable.arrow_up_white)
						);
			}
			break;
		case ARROW_LEFT:
			if(toHightlight){
				mArrowLeft.setImageDrawable(
							mContext.getResources().getDrawable(R.drawable.arrow_left_black)
						);
			}else{
				mArrowLeft.setImageDrawable(
							mContext.getResources().getDrawable(R.drawable.arrow_left_white)
						);
			}
			
			break;
		case ARROW_RIGHT:
			if(toHightlight){
				mArrowRight.setImageDrawable(
							mContext.getResources().getDrawable(R.drawable.arrow_right_black)
						);
			}else{
				mArrowRight.setImageDrawable(
							mContext.getResources().getDrawable(R.drawable.arrow_right_white)
						);
			}
			break;
		case ARROW_DOWN:
			if(toHightlight){
				mArrowDown.setImageDrawable(
							mContext.getResources().getDrawable(R.drawable.arrow_down_black)
						);
			}else{
				mArrowDown.setImageDrawable(
							mContext.getResources().getDrawable(R.drawable.arrow_down_white)
						);
			}
			break;			
		}
			
	}
	
	private void updateLayoutParams(){
		mLeft 	= this.getLeft();
		mTop 	= this.getTop();
		mHeight 	= this.getHeight();
		mWidth 	= this.getWidth();
		
		View parent = (View)this.getParent();
		mParentHeight = parent.getHeight();
		mParentWidth = parent.getWidth();
		
	}
	/*Hide the PIP window by moving it outside the screen*/
	public void hide(){
		if(isHiden)return;
		/*Read the layout param for resume. Dont save to mLeft,mTop,mHeight,mWidth*/
		updateLayoutParams();
		this.layout(-this.getWidth(), -this.getHeight(), 0 , 0);
		this.postInvalidate();	
		isHiden = true;
		
	}
	/*Show PIP window*/
	public void show(){
		if(!isHiden)return;
		updateLayout();
		isHiden = false;
		
	}
	
	private void updateLayout(){
		this.layout(mLeft, mTop, mLeft + mWidth , mTop + mHeight);
		this.postInvalidate();	
		
	}
	

	private void moveLeft(){
		int tmpLeft = mLeft - DELTA_MOVING;
		if(tmpLeft > 0){
			mLeft = tmpLeft;
		}else{
			mLeft = 0;
		}
		updateLayout();
	}
	private void moveRight(){
		
		int tmpLeft =  mLeft + DELTA_MOVING;
		if(tmpLeft + mWidth < mParentWidth){
			mLeft = tmpLeft ;
		}else {
			mLeft = mParentWidth - mWidth;
		}
		
		updateLayout();
		
	}
	private void moveUp(){
		int tmpTop =  mTop - DELTA_MOVING;
		if(tmpTop > 0){
			mTop = tmpTop;
		}else {
			mTop = 0;
		} 
		updateLayout();
	}
	private void moveDown(){
		int tmpTop =  mTop + DELTA_MOVING;
		if(tmpTop + mHeight < mParentHeight){
			mTop = tmpTop;
		}else{
			mTop = mParentHeight - mHeight;
		}
		updateLayout();
	}
	
}