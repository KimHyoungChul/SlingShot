package com.cisco.slingshot.utils;

public class TimeoutTimer {
	
	
	private static final String LOG_TAG = "TimeOutTimer";
	private String mTaskName;
	private TimeoutCallback mCallback = null;
	
	
	private boolean mRunning = true;
	
	public TimeoutTimer(String taskname,TimeoutCallback tcb){
		mTaskName = taskname;
		mCallback = tcb;
	}
	
	public void setTimeoutCallback(final TimeoutCallback cb){
		mCallback = cb;
	}
	
	/**
	 * Start timer 
	 * @param timeout in seconds
	 */
	
	public void start(final int timeout){
        new Thread(new Runnable() {
            public void run() {
            	mRunning = true;
                sleep(timeout);
                if (mRunning && mCallback!=null) {
                	Util.S_Log.d(LOG_TAG, "Time Task: "+ mTaskName + " get timeout!");
                	mCallback.onTimeout();
                }
            }
        }, "SlingshotTimerTaskThread").start();
	}
	
	public synchronized void cancel(){
        mRunning = false;
        this.notify();		
	}
	
    private synchronized void sleep(int timeout) {
        try {
            this.wait(timeout * 1000);
        } catch (InterruptedException e) {
            Util.S_Log.e(LOG_TAG, "Task timer interrupted!");
        }
    }
	
	
	public interface TimeoutCallback{
		abstract void onTimeout();
	}
	
	
	
}