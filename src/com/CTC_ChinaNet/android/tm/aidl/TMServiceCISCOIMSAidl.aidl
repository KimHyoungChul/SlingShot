package com.CTC_ChinaNet.android.tm.aidl;


import com.CTC_ChinaNet.android.tm.aidl.CTCCISCOIMSResult;

interface TMServiceCISCOIMSAidl{
	/**
	 *get IPTV token
	 * if occur error ,will be return null
     * 
     */
    CTCCISCOIMSResult getIMSParameter();
}