package com.cisco.slingshot.history;

import java.sql.Date;
import java.sql.Time;

 public  class HistoryItem{
	 
	    public static final int HISTORY_TYPE_INCOMING = 1;
	    public static final int HISTORY_TYPE_OUTGOING = 2;
	    public static final int HISTORY_TYPE_MISSING = 3;
	    
    	public HistoryItem(
    			String address, 
    			Date date, 
    			Time time,
    			int type){
    		_address = address;
    		_date = date;
    		_time = time;
    		_type = type;
    	}
    	
    	public HistoryItem(){}
    	
    	public String _address;
    	public Date _date;
    	public Time _time;
    	public int _type;
    }
    