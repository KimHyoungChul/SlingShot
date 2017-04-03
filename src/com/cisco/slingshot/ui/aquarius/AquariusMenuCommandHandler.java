package com.cisco.slingshot.ui.aquarius;

import android.view.View;


public  abstract class  AquariusMenuCommandHandler{
	public enum CommandType{SHOW_CONTENT,
							HIDE_CONTENT,
							};
	
	abstract void handleMenuCommand(CommandType type, View customChildVIew);

}