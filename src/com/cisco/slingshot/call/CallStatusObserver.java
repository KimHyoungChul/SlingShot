package com.cisco.slingshot.call;


public interface CallStatusObserver{
	public abstract void onCallStart();
	public abstract void onCallEnd();
	public abstract void onCallError();
}