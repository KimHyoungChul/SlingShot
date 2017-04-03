package com.cisco.slingshot.service;

import com.CTC_ChinaNet.android.tm.aidl.CTCCISCOIMSResult;
import com.CTC_ChinaNet.android.tm.aidl.TMServiceCISCOIMSAidl;
import com.cisco.slingshot.utils.AESCrypto;
import com.cisco.slingshot.utils.Util;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.os.RemoteException;
import android.util.Base64;
import android.util.Log;


public class IMSServiceStub extends Service{
	
	private static final String LOG_TAG = "IMSServiceStub";
	
	private AMSServiceBinder mService = null;
	
	@Override
	public IBinder onBind(Intent intent) {
		
		return mService;
	}
	
	@Override
	public void onCreate(){
		Util.S_Log.d(LOG_TAG, "Create IMS stub service");
		mService = new AMSServiceBinder();
		
	}
	
	@Override
	public int onStartCommand(Intent intent, int flags,int startId){
		Log.v(LOG_TAG, "IMS stub service start, startId is " + startId + ":" + intent );
		

		return START_STICKY;
	}
	
	
	@Override
	public void onDestroy(){
		Log.v(LOG_TAG, "IMS stub service is destroyed");

	}
	
	private class AMSServiceBinder extends TMServiceCISCOIMSAidl.Stub{

		@Override
		public CTCCISCOIMSResult getIMSParameter() throws RemoteException {
			// TODO Auto-generated method stub
			try {
				Thread.sleep(50*1000);
			} catch (InterruptedException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			
			
			String domain = new String("10.74.122.182");
			String password = new String("yuancui");
			String port = new String("5060");
			String protocol = new String("UDP");
			String proxy = new String("10.74.122.182");
			String userName = new String("yuancui");
			

			
			try{
				CTCCISCOIMSResult result = new CTCCISCOIMSResult();
				result.setDomain(domain);
				//result.setPassword(new String(AESCrypto.encrypt(password.getBytes(), userName.getBytes()),"ISO-8859-1"));
				//result.setPassword(Base64.encodeToString(AESCrypto.encrypt(password.getBytes(), userName.getBytes()),Base64.DEFAULT));
				//result.setPassword(password);
				result.setPassword(AESCrypto.encryptAES(password, userName));
				result.setPort(port);
				result.setProtocol(protocol);
				result.setProxy(proxy);
				result.setUserName(userName);
				return result;
				}catch(Exception e){
					Util.S_Log.e(LOG_TAG,e.getMessage());
					return null;
				}
			
		}
		
	}
}