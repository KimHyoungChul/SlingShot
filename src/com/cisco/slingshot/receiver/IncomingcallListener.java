package com.cisco.slingshot.receiver;

import android.content.Intent;
/**
 * Interface class for handling incoming call, implementing the abstract methods to deal with the call.
 * @author yuancui
 *
 */
public interface IncomingcallListener {
	public abstract void onNewIncoming(Intent intent);
	public abstract void onAnswer(Intent intent);
	public abstract void onDeny(Intent intent);
}
