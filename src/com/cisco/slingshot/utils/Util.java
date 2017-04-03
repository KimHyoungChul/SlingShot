package com.cisco.slingshot.utils;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.Inet4Address;
import java.net.Inet6Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.Enumeration;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;
import android.widget.Toast;


public class Util{
	
	public static final String LOG_TAG = Util.class.getName();
	
	public static final boolean IS_DEBUG = true;
	
	
	public static class S_Log{
		
		
		public static void d(String tag, String log){
			if(IS_DEBUG) Log.d(tag, log);
		}
		public static void e(String tag, String log){
			if(IS_DEBUG) Log.e(tag, log);
		}
		public static void i(String tag, String log){
			if(IS_DEBUG) Log.i(tag, log);
		}
	}
	
	public static class MathineStatus{
		public static boolean isStandby(){
			String getpropCMD[] = {"/system/bin/getprop","stb.power.state"};
	    	String standby_status = null;
			try {
				standby_status = Util.ShellExecuter.execute(getpropCMD, null);
			} catch (IOException e) {
				e.printStackTrace();
			}
	    	if(standby_status!=null && standby_status.equals("standby")){
	    		return true;
	    	}else{
	    		return false;
	    	}
		}
		
		public static String getSystemVeriosn(){
			String getpropCMD[] = {"/system/bin/getprop","ro.build.display.id"};
	    	String system_ver = null;
			try {
				system_ver = Util.ShellExecuter.execute(getpropCMD, null);
			} catch (IOException e) {
				e.printStackTrace();
			}
			if(system_ver != null){
				return system_ver;
			}
			else{
				return "1.0.0";
			}
		}
	}
	
	public static class Network{
		public static boolean isConnect(Context context) { 
	
		    try { 
		        ConnectivityManager connectivity = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE); 
		        if (connectivity != null) { 
		            NetworkInfo info = connectivity.getActiveNetworkInfo(); 
		
		            if (info != null&& info.isConnected()) { 
		                if (info.getState() == NetworkInfo.State.CONNECTED) { 
		                    return true; 
		                } 
		            } 
		        } 
		    }catch (Exception e) { 
		    	e.printStackTrace();
		    } 
		        
		    return false; 
	    } 
		
		public static ArrayList<String> getIpv4Addresses() {
	        ArrayList<String> ips = new ArrayList<String>();
	        try {
	            for (Enumeration<NetworkInterface> en = NetworkInterface.getNetworkInterfaces(); en.hasMoreElements(); ) {
	                NetworkInterface intf = en.nextElement();
	                for (Enumeration<InetAddress> enumIpAddr = intf.getInetAddresses(); enumIpAddr.hasMoreElements();  ){
	                    InetAddress inetAddress = enumIpAddr.nextElement();
	                    if (!inetAddress.isLoopbackAddress()&& inetAddress instanceof Inet4Address) {
	                        ips.add( inetAddress.getHostAddress());
	                    }
	                }
	            }
	        } catch (SocketException ex) {
	            Log.e(LOG_TAG, ex.toString());
	        }
	        return ips;
	    }
		
		public static ArrayList<String> getIpv6Addresses() {
	        ArrayList<String> ips = new ArrayList<String>();
	        try {
	            for (Enumeration<NetworkInterface> en = NetworkInterface.getNetworkInterfaces(); en.hasMoreElements(); ) {
	                NetworkInterface intf = en.nextElement();
	                for (Enumeration<InetAddress> enumIpAddr = intf.getInetAddresses(); enumIpAddr.hasMoreElements();  ){
	                    InetAddress inetAddress = enumIpAddr.nextElement();
	                    if (!inetAddress.isLoopbackAddress()&& inetAddress instanceof Inet6Address) {
	                        ips.add( inetAddress.getHostAddress());
	                    }
	                }
	            }
	        } catch (SocketException ex) {
	            Log.e(LOG_TAG, ex.toString());
	        }
	        return ips;
	    }
	}
	
	
	//start activity with a safe schedule
	public static  boolean startActivitySafely(Context context,Intent intent){
		 intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
	        try {
	        	context.startActivity(intent);
	        } catch (ActivityNotFoundException e) {
	        	e.printStackTrace();
				return false;
	        } catch (SecurityException e) {
	        	e.printStackTrace();
	        	return false;
	        }
		  return true;
	}
	
	public static class  ShellExecuter {  

	    /**
         * @param command Command to execute
         * @param directory shell file path
         */  
		public static String execute ( String [] cmmand,String directory)throws IOException {  
	        String result = "" ;  
	        try {  
	        	ProcessBuilder builder = new ProcessBuilder(cmmand);  
	        	if ( directory != null )  
	        		builder.directory ( new File ( directory ) ) ;  
	        		builder.redirectErrorStream (true) ;  
	        		Process process = builder.start ( ) ; 
	        		
	            
		        InputStream is = process.getInputStream ( ) ;  
		        
		        InputStreamReader inputstreamreader = new InputStreamReader(is);

		        BufferedReader bufferedreader = new BufferedReader(inputstreamreader);


		        String line = "";
		        StringBuilder sb = new StringBuilder(line);
		        while ((line = bufferedreader.readLine()) != null) {
		                sb.append(line);
		                //sb.append('\n');

		        }
		        result = sb.toString();
	        	
	        } catch ( Exception e ) {  
	            e.printStackTrace ( ) ;  
	        }  
        
        	return result ;  
        }  
		

    }  

	
	
	
}