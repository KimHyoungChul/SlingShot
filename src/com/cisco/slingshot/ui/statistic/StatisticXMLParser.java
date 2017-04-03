package com.cisco.slingshot.ui.statistic;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import android.util.Log;

import com.cisco.slingshot.utils.Util;

public class StatisticXMLParser{
	
	final static String LOG_TAG = "StatisticXMLParser";
	
    //private Map<String, String> xmlConfig = new HashMap<String, String>();
	
	private ArrayList<StatisticData> mDataReceived = new ArrayList<StatisticData>();
	private ArrayList<StatisticData> mDataSent = new ArrayList<StatisticData>();
	
    private XmlParseCallback mCallback;
	
    //XML Name space
    private static final String XMLNS = "";
    
    //Update XML tags
    
    /*
     *  curplr  :   current packet loss rate 
	 *  jitter  :   current jitter
     *  TX		:  	Sending statistics
     *  RX		: 	Receiving statistics
     */
    private static final String TAG_STAT    =  "stat";
    private static final String TAG_TX 		= "TX";
    private static final String TAG_RX 		= "RX";
    
    public StatisticXMLParser(XmlParseCallback callback){
    	mCallback = callback;
    }
	
    public void parseXML(String path){
		Util.S_Log.d(LOG_TAG, "begin parseXML ...");
		try{
			if(parse(path)==true){
				mCallback.onXmlParseCompleted(mDataReceived,mDataSent);
			}
		}catch(SAXException e){
			Log.e(LOG_TAG, e.getMessage());
			mCallback.onXmlParseError(XmlParseErrorCode.XML_ERROR);
		}catch(ParserConfigurationException e){
			Log.e(LOG_TAG, e.getMessage());
			mCallback.onXmlParseError(XmlParseErrorCode.XML_ERROR);
		}catch(IOException e){
			Log.e(LOG_TAG, e.getMessage());
			mCallback.onXmlParseError(XmlParseErrorCode.XML_ERROR);
		}      	
    }
    
    private boolean parse(String xmlurl) throws ParserConfigurationException, IOException, SAXException 
	{
		
		Document doc = createDocumentModel(xmlurl);
		if(doc == null){
			return false;
		}
		
		/*Parse root objects <stat> */
		Node stat_node = doc.getFirstChild();
		Util.S_Log.d(LOG_TAG, "objects node : " + stat_node.getNodeName());
		
		if((null == stat_node) || (false == stat_node.getNodeName().equals(TAG_STAT)))
		{
			Log.w(LOG_TAG, "no <stat> in xml file");
			return false;
		}
		
		/*Parse root objects <stat> */
		NodeList nodeList = stat_node.getChildNodes();
		Node tx_node = null , rx_node = null;
		
		/*Parse TX and RX*/
		for(int i = 0; i < nodeList.getLength(); i++)
		{
			Node node = nodeList.item(i);
			
			if(node != null && node.getNodeName().equals(TAG_TX)){
				tx_node = node;
			}
			
			if(node != null && node.getNodeName().equals(TAG_RX)){
				rx_node = node;
			}
			
		}
		
		if(tx_node != null)
		{
			Node node_itor = tx_node.getFirstChild();
			while(null != node_itor)
			{
				if(Node.ELEMENT_NODE == node_itor.getNodeType())
				{
					String key = new String(node_itor.getNodeName());
					String value = null;
					
					Node node_value = node_itor.getFirstChild();
					
					if(null != node_value){
						value = node_value.getNodeValue();
					}			
					mDataSent.add(new StatisticData(key,value));
				}
				node_itor = node_itor.getNextSibling();
			}
		}else{
			Log.w(LOG_TAG, "no <TX> in xml file");
		}
		
		if(rx_node != null)
		{
			Node node_itor = rx_node.getFirstChild();
			while(null != node_itor)
			{
				if(Node.ELEMENT_NODE == node_itor.getNodeType())
				{
					String key = new String(node_itor.getNodeName());
					String value = null;
					
					Node node_value = node_itor.getFirstChild();
					
					if(null != node_value){
						value = node_value.getNodeValue();
					}			
					mDataReceived.add(new StatisticData(key,value));
				}
				node_itor = node_itor.getNextSibling();
			}
		}else{
			Log.w(LOG_TAG, "no <RX> in xml file");
		}
		
		return true;
	}
    
	private Document createDocumentModel(String path) throws ParserConfigurationException, IOException, SAXException 
	{
		Util.S_Log.d(LOG_TAG, "path = " + path);
		
		DocumentBuilderFactory docBuilderFactory = DocumentBuilderFactory.newInstance();
		DocumentBuilder docBuilder = docBuilderFactory.newDocumentBuilder();
		Document doc;
		File file = new File(path);
		if(!file.exists())
			return null;
		doc = docBuilder.parse(file);
		doc.normalize();
		
		return doc;
	}	
	
	
	public interface XmlParseCallback{

		abstract void onXmlParseError(XmlParseErrorCode err);
		abstract void onXmlParseCompleted(ArrayList<StatisticData> dataReceived,
										  ArrayList<StatisticData> dataSent);
	}
	
	public enum XmlParseErrorCode {XML_ERROR}; 
}