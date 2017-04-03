package com.cisco.slingshot.contact;

import java.io.InputStream;
import java.util.regex.Pattern;

import android.content.Context;
import android.util.Log;

public class Contact {
	
	public Contact(String username, String address)
	{
		_username = new String(username);
		_address = new String(address);
	}
	
	public String get_username() {
		return _username;
	}
	
	public void set_username(String _username) {
		this._username = _username;
	}
	
	public String get_address() {
		return _address;
	}
	
	public void set_address(String _address) {
		this._address = _address;
	}
	/*
	public boolean isValidAddress(){

		
					String format = "[@]";
		            if (_address.matches(format)){
		            	return true;
		            }else{
		            	return false;
		            }

	}
	*/
    
	public static InputStream findPhotoInAssets(Context context, String address_as_id){
    	InputStream is;
		try{
			String photo_uri = "photos/" + address_as_id+".jpg";
			is =  context.getAssets().open(photo_uri);
			return is;
		}catch(java.io.IOException ex){
			Log.v("Contact","File: assets/" + ex.getMessage()+" not found");
			return null;				
		}
    }
	
	public static Contact findContactByAddress(Context context, String address){
		/*
		if(hasDomainInAddressDatabase)
			return ContactDatabase.getInstance(context).queryUserByAddress(address);
		else{
			String addressWithoutDomain;
			if(address.contains("@")){
				addressWithoutDomain = address.split("@")[0];
			}else{
				addressWithoutDomain = address;
			}
			return ContactDatabase.getInstance(context).queryUserByAddress(addressWithoutDomain);
		}*/
		
		Contact user = null;
		/*First search: With domain*/
		user = ContactDatabase.getInstance(context).queryUserByAddress(address);
		if(user != null)
			return user;
		/*Second search: Without domain*/
		String addressWithoutDomain;
		if(address.contains("@")){
			addressWithoutDomain = address.split("@")[0];
		}else{
			addressWithoutDomain = address;
		}
		user = ContactDatabase.getInstance(context).queryUserByAddress(addressWithoutDomain);
		
		if(user != null)
			return user;
		
		/*Can not find the address in cantact*/
		return null;
		
		
	}

	private String _username;
	private String _address;
	public static final boolean hasDomainInAddressDatabase = false;
}
