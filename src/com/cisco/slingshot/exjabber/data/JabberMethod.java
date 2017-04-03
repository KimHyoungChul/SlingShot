package com.cisco.slingshot.exjabber.data;

public class JabberMethod {

	public JabberMethod(String type, String url, String code) {
		super();
		Type = type;
		this.url = url;
		this.code = code;
	}

	public String getType() {
		return Type;
	}

	public void setType(String type) {
		Type = type;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}
	
	public String toXmlString()
	{
		String ret = null;
		
		ret = "<Method TYPE=" + "\"" + Type  +"\" ";
		if((null == code) || code.isEmpty())
		{
			ret += "URL=" + "\"" + url  +"\">" + "</Method>";
		}
		else
		{
			ret += "CODE=" + "\"" + code  +"\">" + "</Method>";
		}
		
		return ret;
	}

	private String Type;
	private String url;
	private String code;
	
	public static final String Type_INVITE = "INVITE";
	public static final String Type_BYE = "BYE";
}
