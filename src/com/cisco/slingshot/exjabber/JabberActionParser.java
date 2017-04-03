package com.cisco.slingshot.exjabber;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import android.util.Log;

import com.cisco.slingshot.exjabber.data.JabberCallSession;
import com.cisco.slingshot.exjabber.data.JabberMethod;
import com.cisco.slingshot.exjabber.data.JabberTransaction;

/*
 * For Request: from caller-->callee
 <CallSession CallID="1234">
 <Transaction TYPE="REQ" TranId="123">
 <Method TYPE="INVITE" URL="24057000"></Method>
 </Transaction>
 </CallSession>

 For Response: from callee-->caller
 <CallSession CallID="1234">
 <Transaction TYPE="RES" TranId="123">
 <Method TYPE="INVITE" CODE="180"></Method>
 </Transaction>
 </CallSession>

 For Response: from callee-->caller
 <CallSession CallID="1234">
 <Transaction TYPE="RES" TranId="123">
 <Method TYPE="INVITE" CODE="200"></Method>
 </Transaction>
 </CallSession>

 For Request: from callee-->caller
 <CallSession CallID="1234">
 <Transaction TYPE="REQ" TranId="124">
 <Method TYPE="BYE" URL="24057000"></Method>
 </Transaction>
 </CallSession

 For Response: from caller-->callee
 <CallSession CallID="1234">
 <Transaction TYPE="RES" TranId="124">
 <Method TYPE="BYE" CODE="200"></Method>
 </Transaction>
 </CallSession>


 */

public class JabberActionParser {

	public JabberActionParser(InputStream stream) {

		xmlStream = stream;
	}

	public JabberActionParser(String xmlString) {

		try {
			xmlStream = new ByteArrayInputStream(xmlString.getBytes("UTF-8"));
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public JabberCallSession parseStream() {
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();

		DocumentBuilder builder;
		Document document = null;

		try {
			builder = factory.newDocumentBuilder();
			document = builder.parse(xmlStream);
		} catch (SAXException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		} catch (ParserConfigurationException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
			return null;
		}

		Element root = document.getDocumentElement();
		root.normalize();

		if(!TAG_CallSession.equals(root.getNodeName()))
		{
			Log.e(TAG, "worng format xml : no CallSession");
			return null;
		}
		
		String CallSession_CallID = root.getAttribute(ATTR_CallSession_CallID);
		Log.d(TAG, "attr value = " + CallSession_CallID);
		
		if(null == CallSession_CallID)
		{
			Log.e(TAG, "worng format xml : no CallID");
			return null;
		}

		NodeList items_Transaction = root.getElementsByTagName(TAG_Transaction);
		NodeList items_Method = root.getElementsByTagName(TAG_Method);
		
		if (1 != items_Transaction.getLength() 
				|| 1 != items_Method.getLength()) {
			Log.e(TAG,
					"receive wrong format xml(Transaction, Method) = ("
							+ items_Transaction.getLength() + ","
							+ items_Method.getLength() + ")");
			return null;
		}

		Element element_Transaction  = (Element) items_Transaction.item(0);
		Element element_Method  = (Element) items_Method.item(0);
		
		JabberMethod method = new JabberMethod(
				element_Method.getAttribute(ATTR_Method_TYPE), 
				element_Method.getAttribute(ATTR_Method_URL), 
				element_Method.getAttribute(ATTR_Method_CODE));
		
		JabberTransaction transaction = new JabberTransaction(
				element_Transaction.getAttribute(ATTR_Transaction_TYPE), 
				element_Transaction.getAttribute(ATTR_Transaction_TranId), 
				method);
		return new JabberCallSession(CallSession_CallID, transaction);
	}

	// -----------------------------------------------------------------
	private static final String TAG = JabberActionParser.class.getSimpleName();

	public static final String TAG_CallSession = "CallSession";
	public static final String ATTR_CallSession_CallID = "CallID";
	
	public static final String TAG_Transaction = "Transaction";
	public static final String ATTR_Transaction_TYPE = "TYPE";
	public static final String VAL_Transaction_TYPE_RES = "RES";
	public static final String VAL_Transaction_TYPE_REQ = "REQ";
	public static final String ATTR_Transaction_TranId = "TranId";
	
	public static final String TAG_Method = "Method";
	public static final String ATTR_Method_TYPE = "TYPE";
	public static final String VAL_Method_TYPE_INVITE = "INVITE";
	public static final String VAL_Method_TYPE_BYE = "BYE";
	public static final String ATTR_Method_CODE = "CODE";
	public static final String VAL_Method_CODE_RINGING = "180";
	public static final String VAL_Method_CODE_OK = "200";
	public static final String VAL_Method_CODE_BUSY = "486";
	public static final String VAL_Method_CODE_SERVICEUNAVALIABLE = "503";
	public static final String ATTR_Method_URL = "URL";
	

	private InputStream xmlStream;
	private InputSource xmlSource;
}
