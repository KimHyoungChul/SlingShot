package com.CTC_ChinaNet.android.tm.aidl;

import android.os.Parcel;
import android.os.Parcelable;

public class CTCCISCOIMSResult implements Parcelable{
	private String UserName;
	private String Password;
	private String Domain;
	private String Proxy;
	private String Port;
	private String Protocol;
	

	/**
	 * @return the userName
	 */
	public String getUserName() {
		return UserName;
	}

	/**
	 * @param userName the userName to set
	 */
	public void setUserName(String userName) {
		UserName = userName;
	}

	/**
	 * @return the password
	 */
	public String getPassword() {
		return Password;
	}

	/**
	 * @param password the password to set
	 */
	public void setPassword(String password) {
		Password = password;
	}

	/**
	 * @return the domain
	 */
	public String getDomain() {
		return Domain;
	}

	/**
	 * @param domain the domain to set
	 */
	public void setDomain(String domain) {
		Domain = domain;
	}

	/**
	 * @return the proxy
	 */
	public String getProxy() {
		return Proxy;
	}

	/**
	 * @param proxy the proxy to set
	 */
	public void setProxy(String proxy) {
		Proxy = proxy;
	}

	/**
	 * @return the port
	 */
	public String getPort() {
		return Port;
	}

	/**
	 * @param port the port to set
	 */
	public void setPort(String port) {
		Port = port;
	}

	/**
	 * @return the protocol
	 */
	public String getProtocol() {
		return Protocol;
	}

	/**
	 * @param protocol the protocol to set
	 */
	public void setProtocol(String protocol) {
		Protocol = protocol;
	}

	public CTCCISCOIMSResult() {
	}
	
	private CTCCISCOIMSResult(Parcel in) {
		readFromParcel(in);
	}

	
	public void readFromParcel(Parcel in) {
		 UserName =in.readString();
		 Password =in.readString();
		 Domain =in.readString();
		 Proxy =in.readString();
		 Port =in.readString();
		 Protocol =in.readString();
	}
	
	@Override
	public void writeToParcel(Parcel out, int flags) {
		 out.writeString(UserName);
		 out.writeString(Password);
		 out.writeString(Domain);
		 out.writeString(Proxy);
		 out.writeString(Port);
		 out.writeString(Protocol);
	}
	
    public static final Parcelable.Creator<CTCCISCOIMSResult> CREATOR = new Creator<CTCCISCOIMSResult>() {  
          
        @Override  
        public CTCCISCOIMSResult[] newArray(int size) {  
            return new CTCCISCOIMSResult[size];  
        }  
          
        @Override  
        public CTCCISCOIMSResult createFromParcel(Parcel source) {  
        	CTCCISCOIMSResult rslt = new CTCCISCOIMSResult(source);  
            return rslt;  
        }  
    };


	@Override
	public int describeContents() {
		return 0;
	}  
}
