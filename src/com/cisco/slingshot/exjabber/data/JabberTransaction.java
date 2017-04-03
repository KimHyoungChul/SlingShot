package com.cisco.slingshot.exjabber.data;

public class JabberTransaction {

	public JabberTransaction(String type, String tranId, JabberMethod method) {
		super();
		Type = type;
		TranId = tranId;
		this.method = method;
	}

	public String getType() {
		return Type;
	}

	public void setType(String type) {
		Type = type;
	}

	public JabberMethod getMethod() {
		return method;
	}

	public void setMethod(JabberMethod method) {
		this.method = method;
	}

	public String getTranId() {
		return TranId;
	}

	public void setTranId(String tranId) {
		TranId = tranId;
	}
	
	public String toXmlString()
	{
		String ret = null;
		
		ret = "<Transaction TYPE=" + "\"" + Type + "\" " +
			"TranId=" + "\"" + TranId  + "\">" +
			method.toXmlString() + 
			"</Transaction>";
		
		return ret;
	}

	private String Type;
	private String TranId;
	private JabberMethod method;
	
	public static final String Type_REQ = "REQ";
	public static final String Type_RES = "RES";
}
