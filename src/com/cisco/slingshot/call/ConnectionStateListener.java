package com.cisco.slingshot.call;

/**
 * Interface class to detect the connect status with  SIP server
 * @author yuancui
 *
 */
public interface ConnectionStateListener{
	public void onStatusChanged(ConnectStateData state);
}