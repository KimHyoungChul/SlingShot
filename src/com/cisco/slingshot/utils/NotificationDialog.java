package com.cisco.slingshot.utils;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;

import com.cisco.slingshot.R;

public class NotificationDialog{
	
	public static interface NotificationHandler{
		abstract void handleNotification();
	}
	
	public static void showWarning(final Context ctx, 
								   final String title,
								   final String text, 
								   final NotificationHandler handler){
		
		AlertDialog.Builder builder = new AlertDialog.Builder(ctx);
		final View addDlgView= LayoutInflater.from(ctx).inflate(R.layout.diag_notification_warning, null);
		final TextView tv = (TextView)addDlgView.findViewById(R.id.diag_notification_text);
		tv.setText(text);
		
		AlertDialog dialog = builder.setTitle(title)
		.setView(addDlgView)
		.setPositiveButton((String)ctx.getString(R.string.dialog_nav_ok),new DialogInterface.OnClickListener() {
	           public void onClick(DialogInterface dialog, int id) {
	        	   if(handler!=null){
	        		   handler.handleNotification();
	        	   }
	           }
	       })
	    .setNegativeButton((String)ctx.getString(R.string.dialog_nav_cancel),new DialogInterface.OnClickListener() {
	           public void onClick(DialogInterface dialog, int id) {
	        	   
	           }
	       })
		.create();
		
		dialog.getWindow().setType(WindowManager.LayoutParams.TYPE_SYSTEM_ALERT);
		dialog.show();
	}
	
	public static void showError( final Context ctx, 
								  final String title,
								  final String text, 
								  final NotificationHandler handler){

		AlertDialog.Builder builder = new AlertDialog.Builder(ctx);
		final View addDlgView= LayoutInflater.from(ctx).inflate(R.layout.diag_notification_error, null);
		final TextView tv = (TextView)addDlgView.findViewById(R.id.diag_notification_text);
		tv.setText(text);
		
		AlertDialog dialog = builder.setTitle(title)
		.setView(addDlgView)
		.setPositiveButton((String)ctx.getString(R.string.dialog_nav_ok),new DialogInterface.OnClickListener() {
	           public void onClick(DialogInterface dialog, int id) {
	        	   if(handler!=null){
	        		   handler.handleNotification();
	        	   }
	           }
	       })
	    .setNegativeButton((String)ctx.getString(R.string.dialog_nav_cancel),new DialogInterface.OnClickListener() {
	           public void onClick(DialogInterface dialog, int id) {
	        	   
	           }
	       })
		.create();
		dialog.getWindow().setType(WindowManager.LayoutParams.TYPE_SYSTEM_ALERT);
		dialog.show();
	}
	
	public static void showInfo( final Context ctx, 
								  final String title,
								  final String text, 
								  final NotificationHandler handler){
		AlertDialog.Builder builder = new AlertDialog.Builder(ctx);
		final View addDlgView= LayoutInflater.from(ctx).inflate(R.layout.diag_notification_info, null);
		final TextView tv = (TextView)addDlgView.findViewById(R.id.diag_notification_text);
		tv.setText(text);
		
		
		AlertDialog dialog = builder.setTitle(title)
		.setView(addDlgView)
		.setPositiveButton((String)ctx.getString(R.string.dialog_nav_ok),new DialogInterface.OnClickListener() {
	           public void onClick(DialogInterface dialog, int id) {
	        	   if(handler!=null){
	        		   handler.handleNotification();
	        	   }
	           }
	       })
	    .setNegativeButton((String)ctx.getString(R.string.dialog_nav_cancel),new DialogInterface.OnClickListener() {
	           public void onClick(DialogInterface dialog, int id) {
	        	   
	           }
	       })
		.create();
		dialog.getWindow().setType(WindowManager.LayoutParams.TYPE_SYSTEM_ALERT);
		dialog.show();
	}

	
	
}