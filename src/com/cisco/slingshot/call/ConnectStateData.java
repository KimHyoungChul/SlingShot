package com.cisco.slingshot.call;

import com.cisco.slingshot.call.AccountManager.LoginAccount;

/**
 * Wrapper structure of the connect status data
 * @author yuancui
 *
 */
public class ConnectStateData{
	public static final int CONN_STATE_REGISTERING = 1;
	public static final int CONN_STATE_READY = 2;
	public static final int CONN_STATE_FAIL = 3;

	public ConnectStateData(){
	}
	public ConnectStateData(LoginAccount account,int state){
		this.account =  account;
		this.state = state;
	}
	

	public LoginAccount account;
	public int state;
	
}