package com.cisco.slingshot.ui.quickcall;

public interface QuickCallEventListener {
	public abstract void onMenuBack(int selection,Object arg);
	public abstract void onMenuForward(int selection,Object arg);
	public abstract void onMenuClose();
}