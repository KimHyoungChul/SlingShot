package com.cisco.slingshot.utils;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.cisco.slingshot.R;


public class ServiceToast{
	
	private final static boolean isDebug = true;
	public static void showMassage(final Context context,final String msg){
		
		if(isDebug == false)
			return;
		
		Handler mainHandler = new Handler(Looper.getMainLooper());
		
		mainHandler.post(new Runnable(){

			@Override
			public void run() {
				View toastView = LayoutInflater.from(context).inflate(R.layout.service_toast, null);
				TextView tv = (TextView)toastView.findViewById(R.id.service_toast_text);
				tv.setText(msg);
				Toast toast = new Toast(context);
				toast.setGravity(Gravity.CENTER, 0, 0);
				toast.setView(toastView);
				toast.setDuration(Toast.LENGTH_SHORT);
				toast.show();
			}
			
		});
	}
	
}