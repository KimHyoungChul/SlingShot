/**
 * 
 */
package com.cisco.slingshot.net.rtp.test;

import java.io.FileDescriptor;
import java.io.IOException;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketAddress;
import java.net.SocketException;
import java.net.UnknownHostException;

import android.net.sip.SipException;
import android.os.ParcelFileDescriptor;

import com.cisco.slingshot.utils.Util;

/**
 * @author zhiqli
 *
 */
public class RtpVideo {
	private static final String LOGTAG = RtpVideo.class.getSimpleName();
	private DatagramSocket mSocket = null;
	private FileDescriptor mFd = null;
	private ParcelFileDescriptor   mPFd = null;
	private int mNativeFd = -1;
	private String mPeerIP = null;
	private int  mLocalPort = -1;
	private int   mPeerPort = -1;
	private String mVideoURL = new String( "sdp://v=0\n" +
								"c=IN IP4 127.0.0.1\n" +
								"t=0 0\n" +
								"m=video 5004 RTP/AVP 97\n" +
								"a=rtpmap:97 H264/90000\n" +
								"a=framesize:97 640-480\n"
								);

	public RtpVideo(InetAddress address) throws SocketException, UnknownHostException {
	//	super(address);
		int port = 0;
		while(true) {
			mSocket = new DatagramSocket(23456);
			port = mSocket.getLocalPort();
			if(0 == (port&1))
			{
				mLocalPort = port;				
				break;
			}
			
			mSocket.close();
		}

		
		mSocket.connect(InetAddress.getLocalHost(), 5004);
		
		Util.S_Log.d(LOGTAG, "Local ip:" + InetAddress.getLocalHost().toString());
		
		boolean connected = mSocket.isConnected();
		
		if(false == connected){
			throw new SocketException("Can't connect to local port 5004.");
		}

		//		SocketAddress addr = new InetSocketAddress(InetAddress.getLocalHost(), 54320);
//		mSocket.bind(null);
		// TODO Auto-generated constructor stub
	}
	
	public String GetVideoURL(){
		
		
		
		return mVideoURL;
	}
	
	public final FileDescriptor getVideoFileDescriptor() throws IOException {
		 
		mPFd = ParcelFileDescriptor.fromDatagramSocket(mSocket);
		 
		mFd = mPFd.getFileDescriptor();
		mNativeFd = mPFd.getFd();
		 
		Util.S_Log.d(LOGTAG, "(" + mNativeFd + ")" + " file descriptor was  " + mFd.toString());
		return mFd;
	}
	
//	public int getLocalPort(){
		
//		return 5004;
		//return mSocket.getLocalPort();
//	}
	
	public boolean release() {
		
		mSocket.close();
		
		return true;
	}

	public void setRemoteAddress(String ip , int port) throws UnknownHostException, SipException{
		synchronized(this) {
			
			if(port == mPeerPort && mPeerIP.equals(ip)){
				return;
			}
			mPeerIP = new String(ip);
			mPeerPort = port;
	
			boolean connected = mSocket.isConnected();
			
			if(false == connected)
				mSocket.connect(InetAddress.getByName(ip), port);		
					
			/*get connect status again*/
			connected = mSocket.isConnected();
			
			if(false == connected){
				throw new SipException("Can't connect to remote ip");
			}
			SocketAddress remote = mSocket.getRemoteSocketAddress();
			int remote_port = mSocket.getPort();

			InetAddress inet_local = mSocket.getLocalAddress();
			
			Util.S_Log.d(LOGTAG,"Local address was " + inet_local.toString());
			Util.S_Log.d(LOGTAG , "remote address was "+remote.toString()+" port was "+remote_port);
				
			return;
		}
	}	

}
