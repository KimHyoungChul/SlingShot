package com.cisco.slingshot.ui.statistic;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Timer;
import java.util.TimerTask;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.cisco.slingshot.R;
import com.cisco.slingshot.ui.statistic.StatisticXMLParser.XmlParseErrorCode;
import com.cisco.slingshot.utils.Util;


public abstract class StatisticAdapter implements StatisticXMLParser.XmlParseCallback{
	
	public static final String LOG_TAG = "StatisticAdapter";
	
	private static final long MS_THREE_SECONDS = 3*1000L;
	
	private Context mContext;
	private StatisticView mAnchorStatisticView = null;
	
	private StatisticXMLParser mStatisticXMLParser = null;
	private HashMap<String,TextView> mDataSendingValueTexts = null;
	private HashMap<String,TextView> mDataReceivingValueTexts = null;
	
	private ArrayList<StatisticData> mDataReceived = null;
	private ArrayList<StatisticData> mDataSent = null; 
	
	private TimerTask mUpdateTimerTask = null;
	
	private HashMap<String,NameUnit> mKeyNameMap = new HashMap<String,NameUnit>();
	
	private class UiHandler extends Handler{
		public static final int UPDATE_UI = 1;
        @Override
        public void handleMessage(Message msg) {
        	switch(msg.what){
        	case UPDATE_UI:
        		upDateUi();
        		break;
        	}
        }
	}
	private UiHandler mUiHandler;
	
	public StatisticAdapter(Context ctx){
		mContext = ctx;
		init();
	}
	
	public void startUpdateAsync(){
		mUpdateTimerTask = new TimerTask()
		{		
			public void run()
			{
				Util.S_Log.d(LOG_TAG, "Updating data");
				mStatisticXMLParser.parseXML(getDataXMLPath());
			}
		};
		Timer timer = new Timer();
		timer.scheduleAtFixedRate(mUpdateTimerTask, MS_THREE_SECONDS, MS_THREE_SECONDS);
	}
	
	
	public void stopUpdate(){
		if(mUpdateTimerTask != null){
			mUpdateTimerTask.cancel();
			mUpdateTimerTask = null;
		}
	}
	
	
	
	private void updateOnce(){
		mStatisticXMLParser.parseXML(getDataXMLPath());
	}
	
	private void init(){
		mUiHandler = new UiHandler();
		mStatisticXMLParser = new StatisticXMLParser(this);
		createKeyNameMap();
	}
	
	/*Align with key defined in stat.xml,"totalplr","curplr","jitter","chanrate"*/
	
	private void createKeyNameMap(){
		mKeyNameMap.put("totalplr", new NameUnit((String)mContext.getResources().getText(R.string.stat_name_total_loss_rate),
												 (String)mContext.getResources().getText(R.string.stat_unit_rate)));
		mKeyNameMap.put("curplr", new NameUnit((String)mContext.getResources().getText(R.string.stat_name_current_loss_rate),
											   (String)mContext.getResources().getText(R.string.stat_unit_rate)));
		mKeyNameMap.put("jitter", new NameUnit((String)mContext.getResources().getText(R.string.stat_name_jitter),
												(String)mContext.getResources().getText(R.string.stat_unit_ms)));
		mKeyNameMap.put("chanrate", new NameUnit((String)mContext.getResources().getText(R.string.stat_name_bit_rate),
												 (String)mContext.getResources().getText(R.string.stat_unit_kbps)));
	}
	
	private View createItemView(String key, String value){
		View item = LayoutInflater.from(mContext).inflate(R.layout.statistic_view_item, null);
		TextView tv_name = (TextView)item.findViewById(R.id.statistic_item_name);
		TextView tv_value = (TextView)item.findViewById(R.id.statistic_item_value);
		
		/*Search the readable name for the key, if not exists, use the key directly*/
		NameUnit readble_name = mKeyNameMap.get(key);
		if(readble_name != null){
			tv_name.setText(readble_name.name);
			tv_value.setText(value + readble_name.unit);
		}else{
			tv_name.setText(key);
			tv_value.setText(value);
		}

		return item;
	}

	
	
	public void setAnchorStatisticView(StatisticView view){
		mAnchorStatisticView = view;
	}
	
	private void updateInUIThread(){
		mUiHandler.sendEmptyMessage(UiHandler.UPDATE_UI);
	}
	
	private void upDateUi(){
		
		if(mDataSent != null){
			
			if(mDataSendingValueTexts == null){
				/*first time sending data*/
				mDataSendingValueTexts = new HashMap<String,TextView>();
				Iterator<StatisticData> itor = mDataSent.iterator();
				while(itor.hasNext()){
					StatisticData item = itor.next();
					View itemView = createItemView(item.key,item.value);
					TextView tv = (TextView)itemView.findViewById(R.id.statistic_item_value);
					mDataSendingValueTexts.put(item.key, tv);
					mAnchorStatisticView.addDataSendingItem(itemView, true);
				}	
			}else{
				
		    	Iterator<StatisticData> itor = mDataSent.iterator();
				while(itor.hasNext()){
					StatisticData item = itor.next();
					TextView tv = mDataSendingValueTexts.get(item.key);
					
					//tv.setText(item.value);
					NameUnit readble_name = mKeyNameMap.get(item.key);
					if(readble_name != null){
						tv.setText(item.value + readble_name.unit);
					}else{
						tv.setText(item.value );
					}
				}
			}
		}
		
		if(mDataReceived != null){
			if(mDataReceivingValueTexts == null){
				/*first time receiving data*/
				mDataReceivingValueTexts = new HashMap<String,TextView>();
				Iterator<StatisticData> itor = mDataReceived.iterator();
				while(itor.hasNext()){
					StatisticData item = itor.next();
					View itemView = createItemView(item.key,item.value);
					TextView tv = (TextView)itemView.findViewById(R.id.statistic_item_value);
					mDataReceivingValueTexts.put(item.key, tv);
					mAnchorStatisticView.addDataReceivingItem(itemView, true);
				}	
			}else{
		    	Iterator<StatisticData> itor = mDataReceived.iterator();
				while(itor.hasNext()){
					StatisticData item = itor.next();
					TextView tv = mDataReceivingValueTexts.get(item.key);
					//tv.setText(item.value);
					NameUnit readble_name = mKeyNameMap.get(item.key);
					if(readble_name != null){
						tv.setText(item.value + readble_name.unit);
					}else{
						tv.setText(item.value );
					}
				}
			}
		}

		
	}
	

	
	@Override
	public void onXmlParseError(XmlParseErrorCode err){
		//ignore
	}
	public void onXmlParseCompleted(ArrayList<StatisticData> dataReceived,
									  ArrayList<StatisticData> dataSent){
		
		mDataReceived = new ArrayList<StatisticData>(dataReceived);
		mDataSent = new ArrayList<StatisticData>(dataSent);
		updateInUIThread();
	}
	
	/*Return the xml recording the video params such as jitter, bit rate,etc*/
	public abstract String getDataXMLPath();
	/*Return video resolution */
	public abstract int getVideoHeight();
	public abstract int getVideoWidth();
	
	private class NameUnit{
		public String name;
		public String unit;
		public NameUnit(String name, String unit){
			this.name = name;
			this.unit = unit;
		}
	}
	
	
	
	
	
	
	
}