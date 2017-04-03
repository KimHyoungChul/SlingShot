package com.cisco.slingshot.service;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.LinkedList;
import java.util.Random;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.RemoteException;
import android.util.Log;
import android.widget.Toast;

import com.cisco.slingshot.call.CallManager;
import com.cisco.slingshot.contact.Contact;
import com.cisco.slingshot.exjabber.JabberActionParser;
import com.cisco.slingshot.exjabber.data.JabberCallSession;
import com.cisco.slingshot.exjabber.data.JabberMethod;
import com.cisco.slingshot.exjabber.data.JabberTransaction;
import com.cisco.slingshot.exjabber.utils.CallStatusChangeListener;
import com.cisco.slingshot.net.sip.SipConfCall;
import com.cisco.slingshot.receiver.IncomingCallReceiver;
import com.cisco.slingshot.ui.InCallView;
import com.cisco.slingshot.utils.AsyncCallTask;
import com.cisco.slingshot.utils.Util;

public class SocketListenerService extends Service implements
		CallStatusChangeListener {

	@Override
	public IBinder onBind(Intent arg0) {
		// TODO Auto-generated method stub
		return stub;
	}

	@Override
	public void onCreate() {
		// TODO Auto-generated method stub

		Util.S_Log.d(TAG, "enter onCreate");

		super.onCreate();

		startListener(Integer.parseInt(serverPort));
	}

	@Override
	@Deprecated
	public void onStart(Intent intent, int startId) {
		// TODO Auto-generated method stub
		Util.S_Log.d(TAG, "enter onStart");
		super.onStart(intent, startId);
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		// TODO Auto-generated method stub
		Util.S_Log.d(TAG, "enter onStartCommand");

		return super.onStartCommand(intent, flags, startId);
	}

	ISocketListenerService.Stub stub = new ISocketListenerService.Stub() {

		public String getIP() throws RemoteException {
			return serverGetIP();
		}

		public String getPort() throws RemoteException {
			// TODO Auto-generated method stub
			return serverGetPort();
		}

		public String getStatus() throws RemoteException {
			// TODO Auto-generated method stub
			return serverGetStatus();
		}

		@Override
		public void reStart(String port) throws RemoteException {
			// TODO Auto-generated method stub

		}

	};

	@Override
	public boolean onUnbind(Intent intent) {
		// TODO Auto-generated method stub
		Util.S_Log.d(TAG, "calling onUnbind");
		// return super.onUnbind(intent);
		return true;
	}

	@Override
	public void onReceiveCall(String addr) {
		// TODO Auto-generated method stub
		Util.S_Log.d(TAG, "receiving call from " + addr);
		
		// switch status
		if(CALLSTATUS.IDLE != callStatus)
		{
			Util.S_Log.d(TAG, "!!!(BUG), incoming call, but callStatus = " + callStatus +"(not IDLE)!!!");
		}
		switchCallstatus(CALLSTATUS.INCOMING_CALL);

		// generate a new session
		JabberMethod method = new JabberMethod(null, null, null);
		method.setType(JabberActionParser.VAL_Method_TYPE_INVITE);
		method.setUrl(addr);

		JabberTransaction transaction = new JabberTransaction(null, null,
				method);
		transaction.setType(JabberActionParser.VAL_Transaction_TYPE_REQ);
		Random rdm = new Random(System.currentTimeMillis());
		String transid = String.valueOf(Math.abs(rdm.nextInt())%100 + 100);
		//String transid = new String("123"); // !!! debug code !!!
		transaction.setTranId(transid);

		JabberCallSession retSession = new JabberCallSession("", transaction);
		rdm = new Random(System.currentTimeMillis());
		String callid = String.valueOf(Math.abs(rdm.nextInt())%1000 + 1000);
		//String callid = new String("1234"); // !!! debug code !!!
		retSession.setCallID(callid);

		curSession = retSession;

		sendSessionXmlString(retSession);
	}

	@Override
	public void onStatusChange(int status, ArrayList<String> list) {
		// all these status are from current session

		Util.S_Log.d(TAG, "onStatusChange >>> current callStatus = " + callStatus
				+ ", status = " + status + "(" + msg2String(status) + ")");

		switch (status) {
		case MSG_RINGINGBACK: {
			if (CALLSTATUS.OUTGOING_CALL != callStatus)
				return;

			JabberMethod method = new JabberMethod(null, null, null);
			method.setType(JabberActionParser.VAL_Method_TYPE_INVITE);
			method.setCode(JabberActionParser.VAL_Method_CODE_RINGING);

			JabberTransaction transaction = new JabberTransaction(null, null,
					method);
			transaction.setType(JabberActionParser.VAL_Transaction_TYPE_RES);
			transaction.setTranId(curSession.getTransaction().getTranId());

			JabberCallSession retSession = new JabberCallSession("",
					transaction);
			retSession.setCallID(curSession.getCallID());

			sendSessionXmlString(retSession);
		}
			break;
		case MSG_OK: {
			
			if (callStatus == CALLSTATUS.INCOMING_CALL) {
				switchCallstatus(CALLSTATUS.IN_A_CALL);
				return;
			}

			if (CALLSTATUS.OUTGOING_CALL != callStatus)
				return;
			
			switchCallstatus(CALLSTATUS.IN_A_CALL);

			JabberMethod method = new JabberMethod(null, null, null);
			method.setType(JabberActionParser.VAL_Method_TYPE_INVITE);
			method.setCode(JabberActionParser.VAL_Method_CODE_OK);

			JabberTransaction transaction = new JabberTransaction(null, null,
					method);
			transaction.setType(JabberActionParser.VAL_Transaction_TYPE_RES);
			transaction.setTranId(getSessionTransId());

			JabberCallSession retSession = new JabberCallSession("",
					transaction);
			retSession.setCallID(getSessionCallId());

			sendSessionXmlString(retSession);
		}
			break;
		case MSG_BUSY: {
			if (CALLSTATUS.OUTGOING_CALL != callStatus)
				return;

			// should change the call status??
			switchCallstatus(CALLSTATUS.IDLE);

			JabberMethod method = new JabberMethod(null, null, null);
			method.setType(JabberActionParser.VAL_Method_TYPE_INVITE);
			method.setCode(JabberActionParser.VAL_Method_CODE_BUSY);

			JabberTransaction transaction = new JabberTransaction(null, null,
					method);
			transaction.setType(JabberActionParser.VAL_Transaction_TYPE_RES);
			transaction.setTranId(getSessionTransId());

			JabberCallSession retSession = new JabberCallSession("",
					transaction);
			retSession.setCallID(curSession.getCallID());

			sendSessionXmlString(retSession);
		}
			break;
		case MSG_ERROR:
		{
			Util.S_Log.d(TAG, "process MSG_ERROR!!!");
			Util.S_Log.d(TAG, "para[0] = " + list.get(0));
			Util.S_Log.d(TAG, "para[1] = " + list.get(1));
			
			switchCallstatus(CALLSTATUS.IDLE);
			
			// process as busy
			JabberMethod method = new JabberMethod(null, null, null);
			method.setType(JabberActionParser.VAL_Method_TYPE_INVITE);
			method.setCode(JabberActionParser.VAL_Method_CODE_BUSY);

			JabberTransaction transaction = new JabberTransaction(null, null,
					method);
			transaction.setType(JabberActionParser.VAL_Transaction_TYPE_RES);
			transaction.setTranId(curSession.getTransaction().getTranId());

			JabberCallSession retSession = new JabberCallSession("",
					transaction);
			retSession.setCallID(curSession.getCallID());

			sendSessionXmlString(retSession);
			
			curSession = null;

		}
			break;
		case MSG_END: {
			Util.S_Log.d(TAG, "process MSG_END!!!");

			if(CALLSTATUS.IDLE == callStatus)
			{
				// if status has been set to IDLE, no further process
				Util.S_Log.d(TAG, "in IDLE, receive MSG_END");
				return;
			}
			
			switchCallstatus(CALLSTATUS.IDLE);

			JabberMethod method = new JabberMethod(null, null, null);
			method.setType(JabberActionParser.VAL_Method_TYPE_BYE);
			String url = curSession.getTransaction().getMethod().getUrl();
			if((null == url) || url.isEmpty())
			{
				method.setUrl("empty");
			}
			else
			{
				method.setUrl(curSession.getTransaction().getMethod().getUrl());
			}

			JabberTransaction transaction = new JabberTransaction(null, null,
					method);
			transaction.setType(JabberActionParser.VAL_Transaction_TYPE_REQ);
			String transid = curSession.getTransaction().getTranId();
			transid = String.valueOf(Integer.parseInt(transid) + 1); // increase
																		// i to
																		// the
																		// transid
			curSession.getTransaction().setTranId(transid);
			transaction.setTranId(curSession.getTransaction().getTranId());

			JabberCallSession retSession = new JabberCallSession("",
					transaction);
			retSession.setCallID(curSession.getCallID());
			
			sendSessionXmlString(retSession);

			curSession = null;
		}
			break;
		case MSG_RINGING:
			break;
		}
	}

	// -----------------------------------------------------------------
	
	private String getSessionCallId()
	{
		String callid = null;
		if(null != curSession)
		{
			callid = String.valueOf(Integer.parseInt(curSession.getCallID()));
		}
		else
		{
			Random rdm = new Random(System.currentTimeMillis());
			callid = String.valueOf(Math.abs(rdm.nextInt())%1000 + 1000);
		}
		
		return callid;
	}
	
	private String getSessionTransId()
	{
		String transid = null;
		if(null != curSession)
		{
			transid = String.valueOf(Integer.parseInt(curSession.getTransaction().getTranId()));
		}
		else
		{
			Random rdm = new Random(System.currentTimeMillis());
			transid = String.valueOf(Math.abs(rdm.nextInt())%100 + 100);
		}
		return transid;
	}
	
	private void sendSessionXmlString(JabberCallSession session)
	{
		try {
			Util.S_Log.d(TAG, "xmlString :::: (to send)" + session.toXmlString());
			if(null != out)
			{
				out.write(session.toXmlString() + "\n");
				out.flush();
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			Util.S_Log.d(TAG, "!!! xml string send error : " + e.getMessage());
			e.printStackTrace();
		}
	}

	private String serverGetIP() {
		try {
			for (Enumeration<NetworkInterface> en = NetworkInterface
					.getNetworkInterfaces(); en.hasMoreElements();) {
				NetworkInterface intf = en.nextElement();
				for (Enumeration<InetAddress> enumIpAddr = intf
						.getInetAddresses(); enumIpAddr.hasMoreElements();) {
					InetAddress inetAddress = enumIpAddr.nextElement();
					if (!inetAddress.isLoopbackAddress()
							&& (inetAddress instanceof Inet4Address)) { // only
																		// care
																		// IPv4
						serverIP = inetAddress.getHostAddress().toString();
						return serverIP;
					}
				}
			}
		} catch (SocketException ex) {
			Log.e(TAG, ex.toString());
			ex.printStackTrace();
		}
		return null;
	}

	private String serverGetPort() {
		return serverPort;
	}

	private String serverGetStatus() {
		switch (status) {
		case UNINIT:
			serverStatus = "uninit";
		case INIT:
			serverStatus = "init";
		}
		return serverStatus;
	}

	// register listener for the new session
	private void preConnectInit() {
		callManager = CallManager.getInstance(getApplicationContext());

		Util.S_Log.d(TAG, "!!! set listener !!!");
		IncomingCallReceiver.registerCallListener(SocketListenerService.this);
		SipConfCall.registerCallListener(SocketListenerService.this);
	}
	
	// unregister listener for the new session
	private void postConnectInit() {
		Util.S_Log.d(TAG, "!!! set listener to null !!!");
		IncomingCallReceiver.registerCallListener(null);
		SipConfCall.registerCallListener(null);
	}

	private boolean isValidSession(JabberCallSession session) {
		switch (callStatus) {
		case IDLE: {
			if (null != curSession) {
				Util.S_Log.d(TAG,
						"!!! (BUG) not clear curSession when switch to CALLSTATUS.IDLE !!!");
			}

			// in idle, only receive INVITE
			String methodType = session.getTransaction().getMethod().getType();
			if (!methodType.equals(JabberActionParser.VAL_Method_TYPE_INVITE))
				return false;

			curSession = session;
			return true;
		}
		case IN_A_CALL: {
			if (null == curSession) {
				// in a calling, session should not be null
				return false;
			}

			// in a call, only receive BYE
			String methodType = session.getTransaction().getMethod().getType();
			if (!methodType.equals(JabberActionParser.VAL_Method_TYPE_BYE))
				return false;

			// should be same CallID, but not same TransId
			String callID = session.getCallID();
			if (!callID.equals(curSession.getCallID()))
				return false;

			// should not be same TransID
			String transId = session.getTransaction().getTranId();
			if (transId.equals(curSession.getTransaction().getTranId()))
				return false;

			curSession = session;
			return true;

		}

		case INCOMING_CALL: {
			// should be same CallID, same TransId
			String callID = session.getCallID();
			if (!callID.equals(curSession.getCallID())) {
				Util.S_Log.d(TAG, "INCOMING_CALL : not same CallID");
				return false;
			}
			String transId = session.getTransaction().getTranId();
			if (!transId.equals(curSession.getTransaction().getTranId())) {
				Util.S_Log.d(TAG, "INCOMING_CALL : not same transId");
				return false;
			}

			// incoming a call, only receive INVEITE(RES), BUSY, OK, RINGING

			// must be a response
			String transType = session.getTransaction().getType();
			if (!transType.equals(JabberActionParser.VAL_Transaction_TYPE_RES)) {
				Util.S_Log.d(TAG, "INCOMING_CALL : not RES trasaction");
				return false;
			}

			// must be INVITE
			String methodType = session.getTransaction().getMethod().getType();
			if (!methodType.equals(JabberActionParser.VAL_Method_TYPE_INVITE)) {
				Util.S_Log.d(TAG, "INCOMING_CALL : not INVITE method");
				return false;
			}
			// must be BUSY, OK, RINGING
			String methodCode = session.getTransaction().getMethod().getCode();
			if (null == methodCode)
				return false;
			if (!(methodCode.equals(JabberActionParser.VAL_Method_CODE_BUSY)
					|| methodCode.equals(JabberActionParser.VAL_Method_CODE_OK) || methodCode
					.equals(JabberActionParser.VAL_Method_CODE_RINGING) ||
					methodCode.equals(JabberActionParser.VAL_Method_CODE_SERVICEUNAVALIABLE)))

			{
				Util.S_Log.d(TAG, "INCOMING_CALL : invalid methodCode = " + methodCode);
				return false;
			}

			curSession = session;
			return true;
		}
		case OUTGOING_CALL: {
			// outgoing a call, can not accept
			return false;
		}

		}

		return true;
	}

	// process the received xml command
	private void processSession(JabberCallSession session) {
		switch (callStatus) {
		case IDLE: {	
			// in IDLE, can only start a new call
			switchCallstatus(CALLSTATUS.OUTGOING_CALL);
			curSession = session;

			sendCmdStartCall("Jabber", curSession.getTransaction().getMethod()
					.getUrl());
			break;
		}
		case IN_A_CALL: {
			// process the BYE

			// <1> end the call
			InCallView.exEndCall();

			/*
			 // MSG_END will send this message, so do no process here
			// <2> send "OK" Response
			JabberMethod method = new JabberMethod(null, null, null, null);
			method.setType(JabberActionParser.VAL_Method_TYPE_BYE);
			method.setCode(JabberActionParser.VAL_Method_CODE_OK);

			JabberTransaction transaction = new JabberTransaction(null, null,
					method);
			transaction.setType(JabberActionParser.VAL_Transaction_TYPE_RES);
			transaction.setTranId(curSession.getTransaction().getTranId());

			JabberCallSession retSession = new JabberCallSession("",
					transaction);
			retSession.setCallID(curSession.getCallID());

			
			try {
				Util.S_Log.d(TAG, "xmlString :::: " + retSession.toXmlString());
				out.write(retSession.toXmlString() + "\n");
				out.flush();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				Util.S_Log.d(TAG, "write error");
				e.printStackTrace();
			}*/

			break;
		}
		case INCOMING_CALL: {
			// only accept RES :: INVITE :: BUSY/OK/RINGING

			String methodCode = session.getTransaction().getMethod().getCode();

			if (methodCode.equals(JabberActionParser.VAL_Method_CODE_OK)) {
				// answer the call
				switchCallstatus(CALLSTATUS.IN_A_CALL);
				IncomingCallReceiver.answerCall();
				
			} else if (methodCode
					.equals(JabberActionParser.VAL_Method_CODE_BUSY) ||
					methodCode
					.equals(JabberActionParser.VAL_Method_CODE_SERVICEUNAVALIABLE)) {
				// deny the call
				IncomingCallReceiver.denyCall();
				switchCallstatus(CALLSTATUS.IDLE);
			} else if (methodCode
					.equals(JabberActionParser.VAL_Method_CODE_RINGING)) {
				// do nothing to RINGING
				;
			} else {
				// should not be here
				Util.S_Log.d(TAG, "!!!(BUG) why here!!! methodCode = " + methodCode);
			}

			break;
		}
		case OUTGOING_CALL: {
			// we should not receive any session in this state
			Util.S_Log.d(TAG,
					"!!! (BUG) has run isValidSession, should not come here!!!");
			break;
		}

		}
	}

	private void doProcess(JabberCallSession session) {
		if(null == session)
		{
			Log.e(TAG, "!!! session is null, no further process !!!");
			return;
		}
		if (!isValidSession(session)) {
			// invalid session
			Util.S_Log.d(TAG, "callSatus = " + callStatus + ", invalid session : "
					+ session.toXmlString());
			return;
		}

		Util.S_Log.d(TAG, "callSatus = " + callStatus + ", valid session : session = "
				+ session.toXmlString());
		processSession(session);
	}
	
	// process a new socket connection
	private void processSocket(final Socket sock) {
		Thread procThread = null;

		procThread = new Thread() {

			public void run() {
				try {
					in = new BufferedReader(new InputStreamReader(
							sock.getInputStream()));
					out = new BufferedWriter(new OutputStreamWriter(
							sock.getOutputStream()));
					out.flush();

					String xmlMsg = null;
					String str = null;

					while (!sock.isClosed()) {

						str = in.readLine();
						if (null == str) {
							// remote socket is closed
							Util.S_Log.d(TAG, "remote socket is colsed!!!");
							break;
						}

						if (str.startsWith(Strng_Xml_Start)) {
							xmlMsg = str;
							while (!xmlMsg.endsWith(Strng_Xml_End)) {
								xmlMsg += in.readLine();
								if (xmlMsg.length() >= Max_Xml_Len) {
									Log.e(TAG, "<SOS> xmlMsg.length = "
											+ xmlMsg.length());
									break;
								}
							}

							if (xmlMsg.length() < Max_Xml_Len) {
								Util.S_Log.d(TAG, "xmlMsg(to parse)  = "
										+ xmlMsg);
								JabberActionParser jparser = new JabberActionParser(
										xmlMsg);

								doProcess(jparser.parseStream());

							} else {
								Log.e(TAG, "<SOS> xmlMsg  = "
										+ xmlMsg);
							}
						}
						else
						{
							Util.S_Log.d(TAG, "invalid string received: " + str);
							if(str.equals("GET STATUS"))
							{
								// for debug usage
								String ret = new String("");
								ret = "<" + callStatus + "> : xml = " + 
								((null != curSession) ? curSession.toXmlString() : "null");
								
								out.write(ret + "\n");
								out.flush();
							}
						}
					}

				} catch (IOException e1) {
					// TODO Auto-generated catch block
					Util.S_Log.d(TAG, "Exception in socket thread : " + e1.getMessage());
					e1.printStackTrace();
				} finally {
					// here, socket connection is terminated
					Util.S_Log.d(TAG, "remove thread");
					threadList.remove(this);
					

					try {
						if(null != curSocket)	curSocket.close();
						if(null != in )			in.close();
						if(null != out)			out.close();
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}

					curSocket = null;
					in = null;
					out = null;
					
					//postConnectInit();
				}
			}
		};

		if (null != procThread) {
			Util.S_Log.d(TAG, "add procThread +++ " + procThread);
			threadList.add(procThread);
			procThread.start();
		}
	}

	private void startListener(int port) {

		try {
			serverSocket = new ServerSocket(Integer.parseInt(serverPort));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		preConnectInit();

		listenerThread = new Thread() {
			@Override
			public void run() {
				// TODO Auto-generated method stub
				while (true) {

					try {
						status = STATUS.INIT;
						Socket socket = serverSocket.accept();

						Util.S_Log.d(TAG,
								"new incoming connection : IP = "
										+ socket.getInetAddress() + "alive time = " + socket.getKeepAlive());

						if(null == curSocket)
						{
							sendCmdShowMsg("incoming connection : " + socket.getInetAddress() +
									", port : " + socket.getPort());
							curSocket = socket;
							//preConnectInit();// do some register to listen call events
							processSocket(socket);
						}
						else
						{
							if(curSocket.getInetAddress().equals(socket.getInetAddress()))
							{
								Util.S_Log.d(TAG, "reconnect IP : " + socket.getInetAddress() +
										", port : " + socket.getPort());
								{
									// close old connection
									Thread item = threadList.getLast();
									int lastIndex  = threadList.lastIndexOf(item);
									Util.S_Log.d(TAG, "lastIndex (should be zero)= " + lastIndex);
									curSocket.close();
									while(item.isAlive())
									{
										// wait until the previous thread is ended 
										try {
											Util.S_Log.d(TAG, "still alive");
											sleep(50);
										} catch (InterruptedException e) {
											// TODO Auto-generated catch block
											e.printStackTrace();
										}
									}
									
									sendCmdShowMsg("reconnect connection : " + socket.getInetAddress());
									curSocket = socket;
									//preConnectInit();// do some register to listen call events
									processSocket(socket);
									continue;
								}
							}
							else
							{
								Util.S_Log.d(TAG, "reject because exist a socket connection IP : " + curSocket.getInetAddress()  +
										", port : " + socket.getPort());
								socket.close();
								continue;	
							}
						}
						
						

					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			}

		};

		listenerThread.start();
	}

	private void startNewCall(Bundle b) {
		switchCallstatus(CALLSTATUS.OUTGOING_CALL);

		String name = b.getString(CMD_STARTCALL_PARANAME);
		String addr = b.getString(CMD_STARTCALL_PARAADDR);

		Util.S_Log.d(TAG, "name = " + name + ", addr = " + addr);
		/*
		Intent intent = new Intent();
		intent.setClass(this, InCallActivity.class);
		Bundle bundle = new Bundle();
		bundle.putString(getString(R.string.str_contact_name), name);
		bundle.putString(getString(R.string.str_contact_addr), addr);
		intent.putExtra(getString(R.string.str_bundle_outgoing), bundle);
		intent.setAction(InCallActivity.ACTION_CALL_OUTGOING);
		intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

		startActivity(intent);*/
		
		AsyncCallTask.newTask(
				getApplicationContext(), 
				AsyncCallTask.ASYNC_OUTGOING, 
				new Contact(name, addr)).execute();
	}

	private void sendCmdStartCall(String name, String addr) {		
		Message msg = handler.obtainMessage();

		Bundle b = new Bundle();

		b.putInt(CMD, CMD_STARTCALL);
		b.putString(CMD_STARTCALL_PARANAME, name);
		b.putString(CMD_STARTCALL_PARAADDR, addr);
		msg.setData(b);
		msg.sendToTarget();
	}
	
	private void sendCmdShowMsg(String str) {	
		Util.S_Log.d(TAG, "cmd to show msg : " + str);
		Message msg = handler.obtainMessage();

		Bundle b = new Bundle();

		b.putInt(CMD, CMD_SHOWMSG);
		b.putString(CMD_SHOWMSG_PARAMSG, str);
		msg.setData(b);
		msg.sendToTarget();
	}

	private String msg2String(int msg) {
		String ret = null;
		switch (msg) {
		case MSG_RINGINGBACK:
			ret = "MSG_RINGINGBACK";
			break;
		case MSG_OK:
			ret = "MSG_OK";
			break;
		case MSG_BUSY:
			ret = "MSG_BUSY";
			break;
		case MSG_END:
			ret = "MSG_END";
			break;
		case MSG_RINGING:
			ret = "MSG_RINGING";
			break;
		case MSG_ERROR:
			ret = "MSG_ERROR";
			break;
		}
		return ret;
	}

	private void switchCallstatus(CALLSTATUS status) {
		Util.S_Log.d(TAG, "swtich status : " + callStatus + "-->" + status);

		callStatus = status;
	}

	Handler handler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			// TODO Auto-generated method stub
			Bundle b = msg.getData();
			int cmd = b.getInt(CMD);

			switch (cmd) {
			case CMD_STARTCALL: {
				startNewCall(b);
			}
				break;
			case CMD_SHOWMSG:
			{
				String str = b.getString(CMD_SHOWMSG_PARAMSG);
				Toast.makeText(
						getApplicationContext(), 
						str, 
						Toast.LENGTH_LONG).show();
			}
				break;
			}
			super.handleMessage(msg);
		};
	};

	private static final String TAG = SocketListenerService.class
			.getSimpleName();
	Context mContext = this;

	private String serverIP = null;
	private String serverPort = "5060"; // the default listener port
	private String serverStatus = null;

	enum STATUS {
		UNINIT, INIT
	};

	STATUS status = STATUS.UNINIT;

	private Thread listenerThread = null;
	private ServerSocket serverSocket;
	LinkedList<Thread> threadList = new LinkedList<Thread>();

	// make both String has the same length
	private static final String Strng_Xml_Start = "<CallSession";
	private static final String Strng_Xml_End = "CallSession>";
	private static final int Max_Xml_Len = 500;

	// handler command
	private static final String CMD = "cmd";

	private static final int CMD_STARTCALL = 1;
	private static final String CMD_STARTCALL_PARANAME = "name";
	private static final String CMD_STARTCALL_PARAADDR = "address";
	
	private static final int CMD_SHOWMSG = 2;
	private static final String CMD_SHOWMSG_PARAMSG = "msg";

	private CallManager callManager = null;
	private JabberCallSession curSession = null;
	private Socket curSocket = null;
	private BufferedReader in = null;
	private BufferedWriter out = null;

	public static final int MSG_RINGINGBACK = 1;
	public static final int MSG_OK = 2;
	public static final int MSG_BUSY = 3;
	public static final int MSG_END = 4;
	public static final int MSG_RINGING = 5;
	public static final int MSG_ERROR = 6;

	enum CALLSTATUS {
		IDLE, IN_A_CALL, INCOMING_CALL, OUTGOING_CALL
	};

	CALLSTATUS callStatus = CALLSTATUS.IDLE;
}
