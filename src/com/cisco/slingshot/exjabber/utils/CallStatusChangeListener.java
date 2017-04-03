package com.cisco.slingshot.exjabber.utils;

import java.util.ArrayList;

public interface CallStatusChangeListener {
	public abstract void onStatusChange(int msg,  ArrayList<String> list);
	public abstract void onReceiveCall(String addr);
}
