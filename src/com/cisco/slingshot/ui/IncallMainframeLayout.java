package com.cisco.slingshot.ui;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.Keyframe;
import android.animation.LayoutTransition;
import android.animation.ObjectAnimator;
import android.animation.PropertyValuesHolder;
import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.RelativeLayout;

import com.cisco.slingshot.utils.Util;

public class IncallMainframeLayout extends RelativeLayout{
	
	private static final String LOG_TAG = "IncallMainframeLayout";
	

	private int mLayerNumber_VideoFrame = 0;
	private int mLayerNumber_Camera = 1;
	private int mLayerNumber_ControlPanel = 2;
	private int mLayerNumber_VideoGroupGuest = 3;
	
	
    Animator appearingAnim, disappearingAnim;
    Animator changingAppearingAnim, changingDisappearingAnim;
	
	public IncallMainframeLayout(Context context){
		super(context);
		setChildrenDrawingOrderEnabled(true);
		setupAnimator();

	}
	public IncallMainframeLayout(Context context, AttributeSet attrs){
		super(context,attrs);
		setChildrenDrawingOrderEnabled(true);
		setupAnimator();
	}
	public IncallMainframeLayout(Context context, AttributeSet attrs, int defStyle){
		super(context,attrs,defStyle);
		setChildrenDrawingOrderEnabled(true);
		setupAnimator();
	}
	
	public void setupAnimator(){
		final LayoutTransition transitioner = new LayoutTransition();
        this.setLayoutTransition(transitioner);
        
        /*
        appearingAnim = transitioner.getAnimator(LayoutTransition.APPEARING);
        disappearingAnim = transitioner.getAnimator(LayoutTransition.DISAPPEARING);
        changingAppearingAnim = transitioner.getAnimator(LayoutTransition.CHANGE_APPEARING);
        changingDisappearingAnim = transitioner.getAnimator(LayoutTransition.CHANGE_DISAPPEARING);
        */
        
        setupCustomTransition(transitioner);
        
        transitioner.setAnimator(LayoutTransition.APPEARING, appearingAnim);
        transitioner.setAnimator(LayoutTransition.DISAPPEARING, disappearingAnim);
        transitioner.setAnimator(LayoutTransition.CHANGE_APPEARING, changingAppearingAnim);
        transitioner.setAnimator(LayoutTransition.CHANGE_DISAPPEARING, changingDisappearingAnim);
        
       
        
        
	}
	
	public void setupCustomTransition(LayoutTransition transition){

        // Changing while Adding
        PropertyValuesHolder pvhLeft =
                PropertyValuesHolder.ofInt("left", 0, 1);
        PropertyValuesHolder pvhTop =
                PropertyValuesHolder.ofInt("top", 0, 1);
        PropertyValuesHolder pvhRight =
                PropertyValuesHolder.ofInt("right", 0, 1);
        PropertyValuesHolder pvhBottom =
                PropertyValuesHolder.ofInt("bottom", 0, 1);
        PropertyValuesHolder pvhScaleX =
                PropertyValuesHolder.ofFloat("scaleX", 1f, 0f, 1f);
        PropertyValuesHolder pvhScaleY =
                PropertyValuesHolder.ofFloat("scaleY", 1f, 0f, 1f);
        
        
        changingAppearingAnim = ObjectAnimator.ofPropertyValuesHolder(
                        this, pvhLeft, pvhTop, pvhRight, pvhBottom, pvhScaleX, pvhScaleY).
                setDuration(transition.getDuration(LayoutTransition.CHANGE_APPEARING));
        
        changingAppearingAnim.addListener(new AnimatorListenerAdapter() {
            public void onAnimationEnd(Animator anim) {
                View view = (View) ((ObjectAnimator) anim).getTarget();
                view.setScaleX(1f);
                view.setScaleY(1f);
            }
        });

        // Changing while Removing
        Keyframe kf0 = Keyframe.ofFloat(0f, 0f);
        Keyframe kf1 = Keyframe.ofFloat(.9999f, 360f);
        Keyframe kf2 = Keyframe.ofFloat(1f, 0f);
        PropertyValuesHolder pvhRotation =
                PropertyValuesHolder.ofKeyframe("rotation", kf0, kf1, kf2);
        
        changingDisappearingAnim = ObjectAnimator.ofPropertyValuesHolder(
                        this, pvhLeft, pvhTop, pvhRight, pvhBottom, pvhRotation).
                setDuration(transition.getDuration(LayoutTransition.CHANGE_DISAPPEARING));
        
        changingDisappearingAnim.addListener(new AnimatorListenerAdapter() {
            public void onAnimationEnd(Animator anim) {
                View view = (View) ((ObjectAnimator) anim).getTarget();
                view.setRotation(0f);
            }
        });

        // Adding
        appearingAnim = ObjectAnimator.ofFloat(null, "rotationY", 90f, 0f).
                setDuration(transition.getDuration(LayoutTransition.APPEARING));
        appearingAnim.addListener(new AnimatorListenerAdapter() {
            public void onAnimationEnd(Animator anim) {
                View view = (View) ((ObjectAnimator) anim).getTarget();
                view.setRotationY(0f);
            }
        });

        // Removing
        disappearingAnim = ObjectAnimator.ofFloat(null, "rotationX", 0f, 90f).
                setDuration(transition.getDuration(LayoutTransition.DISAPPEARING));
        disappearingAnim.addListener(new AnimatorListenerAdapter() {
            public void onAnimationEnd(Animator anim) {
                View view = (View) ((ObjectAnimator) anim).getTarget();
                view.setRotationX(0f);
            }
        });

    
	}
	public enum EventType{Z_ORDER_CHANGED,OTHER};
	
	public void dispatchEvent(EventType et){
		switch(et){
			case Z_ORDER_CHANGED:
			{
				int temp = mLayerNumber_Camera;
				mLayerNumber_Camera = mLayerNumber_VideoFrame;
				mLayerNumber_VideoFrame = temp;
				this.invalidate();
				this.requestLayout();
				break;
			}
		}
		
	}
	@Override
	public int getChildDrawingOrder (int childCount, int domIndex){
		Util.S_Log.d(LOG_TAG, "childCount: "+childCount + ",current index: " + domIndex);
		
		int layoutIndex = getLayerNumber(domIndex);
		return layoutIndex;

	}
	
	
	private int getLayerNumber(int domIndex){
		
		switch (domIndex){
		case DomNodeIndex.DOMINDEX_CAMERA:
			return mLayerNumber_Camera;
		case DomNodeIndex.DOMINDEX_VIDEOFRAME:
			return mLayerNumber_VideoFrame;
		case DomNodeIndex.DOMINDEX_CONTROLPANEL:
			return mLayerNumber_ControlPanel;
		case DomNodeIndex.DOMINDEX_VIDEOGROUPGEST:
			return mLayerNumber_VideoGroupGuest;
			
		}
		
		return domIndex;
		
	}
	private class DomNodeIndex{
		public static final int DOMINDEX_VIDEOFRAME= 0;
		public static final int DOMINDEX_CAMERA = 1;
		public static final int DOMINDEX_CONTROLPANEL = 2;
		public static final int DOMINDEX_VIDEOGROUPGEST	= 3;
	};
}