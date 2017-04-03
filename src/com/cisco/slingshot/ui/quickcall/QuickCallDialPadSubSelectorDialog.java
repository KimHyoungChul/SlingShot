package com.cisco.slingshot.ui.quickcall;


import java.util.HashMap;
import java.util.Map;

import android.app.Dialog;
import android.content.Context;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.cisco.slingshot.R;

public class QuickCallDialPadSubSelectorDialog implements AdapterView.OnItemClickListener{
	public final static String LOG_TAG = "DialPadSubSelectorDialog";
	
	private View				_root;
	private Context 			mContext;
	
	private Dialog  			mDialogInternal;
	private boolean 			isShown = false;

	private DialSubSelectorAdapter mDialSubSelectorAdapter;
	
	private IOnSubSelected mOnSelectedHandler = null;
	
	
	static private QuickCallDialPadSubSelectorDialog _self = null;
	private QuickCallDialPadSubSelectorDialog(Context context){
		mContext = context;	
		//initDialogView();
	}
	
	public static synchronized QuickCallDialPadSubSelectorDialog getInstance(Context ctx){
		if(_self == null){
			if(ctx == null){
	            throw new IllegalStateException("Creating CallManager need a valid Context.");
			}
			_self = new QuickCallDialPadSubSelectorDialog(ctx);
		}
		return _self;
	}
	
	public void setOnSelectedHandler(IOnSubSelected handler){
		mOnSelectedHandler = handler;
	}
	
	private Dialog initDialogView(QuickCallDialPadView.DialPadItemData data){
		
		initSubDialPadKeyPostionMap();
		
		_root = (ViewGroup)LayoutInflater.from(mContext).inflate(R.layout.dialer_sub_selector, null);
		
		GridView gv = (GridView)_root.findViewById(R.id.dialer_sub_selector);
		mDialSubSelectorAdapter = new DialSubSelectorAdapter(mContext,data);
		gv.setNumColumns(mDialSubSelectorAdapter.getCount());
		gv.setAdapter(mDialSubSelectorAdapter);
		gv.setOnItemClickListener(this);
		
		gv.setOnKeyListener(new View.OnKeyListener() {
			@Override
			public boolean onKey(View v, int keyCode, KeyEvent event) {
				if(event.isDown()){
					switch (keyCode) {
						case KeyEvent.KEYCODE_PROG_RED:
						case KeyEvent.KEYCODE_PROG_GREEN:
						case KeyEvent.KEYCODE_PROG_YELLOW:
						case KeyEvent.KEYCODE_PROG_BLUE:
						{
							
							handleKeyInput(_mKeyPostionMap.get(keyCode));
							return true;
						}
					}
				}
				return false;
			}
		});
		
		Dialog dialog = new Dialog(mContext,R.style.IncomingcallDialog);
		//Dialog dialog = new Dialog(mContext);
		dialog.setContentView(_root);
		
		
		
		return dialog;
	}
	
	public void show(QuickCallDialPadView.DialPadItemData data){
		/*
		if(!isShown){
			mDialogInternal = initDialogView(data);
			
	    	//mDialogInternal.getWindow().setType(WindowManager.LayoutParams.TYPE_SYSTEM_ALERT);
	        mDialogInternal.show();	
	        isShown = true;
		} */
		mDialogInternal = initDialogView(data);
		
    	mDialogInternal.getWindow().setType(WindowManager.LayoutParams.TYPE_SYSTEM_ALERT);
        mDialogInternal.show();	
	}
	
    private void cancel(){
    	/*
    	if(isShown){
	    	if(mDialogInternal !=null){
	    		mDialogInternal.cancel();
	    		mDialogInternal = null;
	    		isShown = false;
	    	}
    	}
    	*/
    	if(mDialogInternal !=null){
    		mDialogInternal.cancel();
    		mDialogInternal = null;
    	}
    }
    
	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position, long id) 
	{		
		handleKeyInput(position);	
	}
	
	
	private final Map<Integer,Integer> _mKeyPostionMap = new HashMap<Integer,Integer>();
	private void initSubDialPadKeyPostionMap(){
		_mKeyPostionMap.put( KeyEvent.KEYCODE_PROG_RED, 0);
		_mKeyPostionMap.put( KeyEvent.KEYCODE_PROG_GREEN, 1);
		_mKeyPostionMap.put( KeyEvent.KEYCODE_PROG_YELLOW, 2);
		_mKeyPostionMap.put( KeyEvent.KEYCODE_PROG_BLUE, 3);
	}
	
	private void handleKeyInput(int pos){
		Character data = (Character)mDialSubSelectorAdapter.getItem(pos);
		if(mOnSelectedHandler != null){
			mOnSelectedHandler.onSubSelected(data.charValue());
		}
		cancel();
	}
    
    public class DialSubSelectorAdapter extends BaseAdapter {
        public DialSubSelectorAdapter(Context c,QuickCallDialPadView.DialPadItemData data) {
            mContext = c;
            initData(data);
        }
        
        

        public int getCount() {
            return _data.length;
        }

        public Object getItem(int position) {
            return  Character.valueOf(_data[position]);
        }

        public long getItemId(int position) {
            return position;
        }

        public View getView(int position, View convertView, ViewGroup parent) {

        	LinearLayout itemView;
        	if(convertView == null){
        		itemView = (LinearLayout)LayoutInflater.from(mContext).inflate(R.layout.dialer_sub_selector_item, null);
        		TextView text = (TextView)itemView.findViewById(R.id.dialer_sub_selector_item_text);
        		text.setText(Character.valueOf(_data[position]).toString());
        		
        		ImageView indicator = (ImageView)itemView.findViewById(R.id.dialer_sub_selector_item_indicator);
        		
        		switch(position){
        		case 0:
        			indicator.setBackgroundColor(mContext.getResources().getColor(android.R.color.holo_red_dark));
        			break;
        		case 1:
        			indicator.setBackgroundColor(mContext.getResources().getColor(android.R.color.holo_green_dark));
        			break;
        		case 2:
        			indicator.setBackgroundColor(mContext.getResources().getColor(android.R.color.holo_orange_dark));
        			break;
        		case 3:
        			indicator.setBackgroundColor(mContext.getResources().getColor(android.R.color.holo_blue_dark));
        			break;
        		}
        	
        	}else{
        		itemView = (LinearLayout)convertView;
        	}
        	return itemView;
        }
        
        //DialPadView.DialPadItemData _data;
        
        void initData(QuickCallDialPadView.DialPadItemData data){
        	final String main = data.main;
        	final String sub  = data.sub;
        	
        	_data = new char[sub.length()+1];
        	for(int i=0; i<sub.length();i++){
        		_data[i] = sub.charAt(i);
        	}
        	_data[sub.length()] = main.charAt(0);
        	
        }
        char[] _data;

	}
    
    public interface IOnSubSelected{
    	public abstract void onSubSelected(char c);
    }
	
	
}