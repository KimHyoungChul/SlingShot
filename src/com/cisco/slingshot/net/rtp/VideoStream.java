package com.cisco.slingshot.net.rtp;

import java.io.FileDescriptor;
import java.io.IOException;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketAddress;
import java.net.SocketException;
import java.net.UnknownHostException;

import android.annotation.TargetApi;
import android.net.sip.SipException;
import android.os.Build;
import android.os.ParcelFileDescriptor;

import com.cisco.slingshot.utils.Util;

@TargetApi(Build.VERSION_CODES.ICE_CREAM_SANDWICH)
public class VideoStream {
	private static final String LOGTAG = VideoStream.class.getSimpleName();
	
	private DatagramSocket mSocket = null;
	private ParcelFileDescriptor mParcelFileDescriptor = null;
	//private FileDescriptor mFd = null;
	
	private String mPeerIP = null;
	private int  mLocalPort = -1;
	private int   mPeerPort = -1;

	public VideoStream(InetAddress address) throws SocketException, UnknownHostException {
	//	super(address);
		/*
		mLocalPort = port;
		mSocket = new DatagramSocket(port);
		mSocket.setReuseAddress(true);
		*/
		
		int port = 0;
		while(true) {
			mSocket = new DatagramSocket();
			
			port = mSocket.getLocalPort();
			if(0 == (port&1))
			{
				mLocalPort = port;				
				break;
			}
			
			mSocket.setReuseAddress(true);

			mSocket.close();
		}
		

		
//		SocketAddress addr = new InetSocketAddress(InetAddress.getLocalHost(), 54320);
//		mSocket.bind(null);
		// TODO Auto-generated constructor stub
	}

    /**
     * 
     *
     * @return return the file descriptor from a udp socket 
     */
	public FileDescriptor getVideoFileDescriptor() throws IOException {
		 
		mParcelFileDescriptor = ParcelFileDescriptor.fromDatagramSocket(mSocket);
		 
		FileDescriptor fd = mParcelFileDescriptor.getFileDescriptor();
		 
		Util.S_Log.d(LOGTAG, "file descriptor was  " + fd.toString());
		return fd;
	}
	
//	public int getLocalPort(){
		
//		return 5004;
		//return mSocket.getLocalPort();
//	}
	
    /**
     * release the resource.
     *
     * @return return true when success release the resource.
     */
	public boolean release() {
		//mSocket.disconnect();
		
		if(mParcelFileDescriptor != null){
			try {
				mParcelFileDescriptor.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
		if(mSocket != null)
			mSocket.close();
		
		return true;
	}

    /**
     * set the remote video ip address and port number
     *
     * @param ip the remote ip address
     * @param port the remote port
     * @throws SipException if the remote ip:port can't be connected .
     */
	public void setRemoteAddress(String ip , int port) throws UnknownHostException, SipException{
		synchronized(this) {


			boolean connected = mSocket.isConnected();
			
					
			if(false == connected)
				mSocket.connect(InetAddress.getByName(ip), port);
			else if(mPeerIP.equals(ip) && mPeerPort == port){
			/*different ip or port*/
				mSocket.disconnect();
				mSocket.connect(InetAddress.getByName(ip),port);
				Util.S_Log.d(LOGTAG, "the previous ip is " + mPeerIP + "and port is " + mPeerPort);
				Util.S_Log.d(LOGTAG, "the current ip is " + ip + "and port is " + port);
			}
			
			mPeerIP = new String(ip);
			mPeerPort = port;
						 
			
			/*get connect status again*/
			connected = mSocket.isConnected();
			
			if(false == connected){
				throw new SipException("Can't connect to remote ip");
			}
			SocketAddress remote = mSocket.getRemoteSocketAddress();
			int remote_port = mSocket.getPort();
			InetAddress inet_remote = mSocket.getInetAddress();
			InetAddress inet_local = mSocket.getLocalAddress();
			
			//ParcelFileDescriptor pfd = ParcelFileDescriptor.fromDatagramSocket(mSocket);
			//mFd = pfd.getFileDescriptor();
			
			Util.S_Log.d(LOGTAG,"Local address was " + inet_local.toString());
			Util.S_Log.d(LOGTAG , "remote address was "+remote.toString()+" port was "+remote_port);
				
			return;
		}
	}
}
