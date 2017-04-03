package com.cisco.slingshot.exjabber.data;

import android.sax.Element;

public class JabberCallSession {

	public JabberCallSession(String callID, JabberTransaction transaction) {
		super();
		CallID = callID;
		this.transaction = transaction;
	}

	public String getCallID() {
		return CallID;
	}

	public void setCallID(String callID) {
		CallID = callID;
	}

	public JabberTransaction getTransaction() {
		return transaction;
	}

	public void setTransaction(JabberTransaction transaction) {
		this.transaction = transaction;
	}
	
	public String toXmlString()
	{
		String ret = null;
		
		ret = "<CallSession CallID=" + "\"" + CallID + "\">"
			+ transaction.toXmlString() 
			+ "</CallSession>";
		
		return ret;
	}

	private String CallID;
	private JabberTransaction transaction;
}
