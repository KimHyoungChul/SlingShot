package com.cisco.slingshot.ui.quickcall;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.AttributeSet;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.cisco.slingshot.R;
import com.cisco.slingshot.activity.VideoTestActivity;
import com.cisco.slingshot.contact.Contact;
import com.cisco.slingshot.utils.AsyncCallTask;
import com.cisco.slingshot.utils.Util;

public class QuickCallDialPadView extends QuickCallViewBase implements AdapterView.OnItemClickListener, QuickCallDialPadSubSelectorDialog.IOnSubSelected{
	
	
	public QuickCallDialPadView(Context context){
		super(context);
		mContext = context;
		initView();
	}

	public QuickCallDialPadView(Context context, AttributeSet attr) {
		super(context, attr);
		mContext = context ;
		initView();
	}
	private void initView(){
		initDialPadKeyPostionMap();
		_root = LayoutInflater.from(mContext).inflate(R.layout.dialer_view, null);
		this.addView(_root);
		
        GridView g = (GridView) findViewById(R.id.dialer_grid);
        mDialPadAdapter = new DialPadAdapter(mContext);
        g.setAdapter(mDialPadAdapter);
        g.setOnItemClickListener(this);
        
        g.setOnKeyListener(new View.OnKeyListener() {
			@Override
			public boolean onKey(View v, int keyCode, KeyEvent event) {
				if(event.isDown()){
					switch (keyCode) {
						case KeyEvent.KEYCODE_ESCAPE:
						case KeyEvent.KEYCODE_BACK:
						{
							getLauncherEventLister().onMenuBack(QuickCallLauncherDialog.SEL_CONTACTDETAIL, null);
							return true;			
						}
						case KeyEvent.KEYCODE_0:
						case KeyEvent.KEYCODE_1:
						case KeyEvent.KEYCODE_2:
						case KeyEvent.KEYCODE_3:
						case KeyEvent.KEYCODE_4:
						case KeyEvent.KEYCODE_5:
						case KeyEvent.KEYCODE_6:
						case KeyEvent.KEYCODE_7:
						case KeyEvent.KEYCODE_8:
						case KeyEvent.KEYCODE_9:
						{
							
							handleKeyInput(_mKeyPostionMap.get(keyCode));
							return true;
						}
					}
				}
				return false;
			}
		});
        
        mDialNumTv = (TextView)findViewById(R.id.dialer_display);
        mBackSpace = (ImageButton)findViewById(R.id.dial_backspace);
        mBackSpace.setOnClickListener(new View.OnClickListener(){
			@Override
			public void onClick(View v) {
				String str = mDialNumTv.getText().toString().trim();
				if(str.length()!=0){
					str  = str.substring( 0, str.length() - 1 );			   
				}
				mDialNumTv.setText(str);
			}
		});
        
        mBackSpace.setOnKeyListener(new View.OnKeyListener() {
			@Override
			public boolean onKey(View v, int keyCode, KeyEvent event) {
				if(event.isDown()){
					switch (keyCode) {
						case KeyEvent.KEYCODE_ESCAPE:
						case KeyEvent.KEYCODE_BACK:
						{
							getLauncherEventLister().onMenuBack(QuickCallLauncherDialog.SEL_CONTACTDETAIL, null);
							return true;			
						}
						case KeyEvent.KEYCODE_0:
						case KeyEvent.KEYCODE_1:
						case KeyEvent.KEYCODE_2:
						case KeyEvent.KEYCODE_3:
						case KeyEvent.KEYCODE_4:
						case KeyEvent.KEYCODE_5:
						case KeyEvent.KEYCODE_6:
						case KeyEvent.KEYCODE_7:
						case KeyEvent.KEYCODE_8:
						case KeyEvent.KEYCODE_9:
						{
							
							handleKeyInput(_mKeyPostionMap.get(keyCode));
							return true;
						}
					}
				}
				return false;
			}
		});
        
        mDialButton = (ImageButton)findViewById(R.id.dial_button);
        
        mDialButton.setOnClickListener(new View.OnClickListener(){
			@Override
			public void onClick(View v) {

				
				String address = mDialNumTv.getText().toString();
				if(address.equals(""))
					return;
				
				if(!address.contains("@")){
					SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(mContext);
					String domain 	= prefs.getString(mContext.getString(R.string.str_pref_domain), "");
					address += "@"+domain;
				}
				
				Contact callee = Contact.findContactByAddress(mContext, address);
				if(callee == null){
					callee = new Contact(address,address);
				}

				try{
					AsyncCallTask.newTask(mContext, 
										  AsyncCallTask.ASYNC_OUTGOING, 
										  callee).execute();
									
				}catch(Exception e){
					e.printStackTrace();
				}
				
				getLauncherEventLister().onMenuClose();	
				
			}
		});
        
        mDialButton.setOnKeyListener(new View.OnKeyListener() {
			@Override
			public boolean onKey(View v, int keyCode, KeyEvent event) {
				if(event.isDown()){
					switch (keyCode) {
						case KeyEvent.KEYCODE_ESCAPE:
						case KeyEvent.KEYCODE_BACK:
						{
							getLauncherEventLister().onMenuBack(QuickCallLauncherDialog.SEL_CONTACTDETAIL, null);
							return true;			
						}
						case KeyEvent.KEYCODE_0:
						case KeyEvent.KEYCODE_1:
						case KeyEvent.KEYCODE_2:
						case KeyEvent.KEYCODE_3:
						case KeyEvent.KEYCODE_4:
						case KeyEvent.KEYCODE_5:
						case KeyEvent.KEYCODE_6:
						case KeyEvent.KEYCODE_7:
						case KeyEvent.KEYCODE_8:
						case KeyEvent.KEYCODE_9:
						{
							
							handleKeyInput(_mKeyPostionMap.get(keyCode));
							return true;
						}
					}
				}
				return false;
			}
		});
        
        mNumEngSwitcher = (LinearLayout)findViewById(R.id.num_char_switcher);
        _num = (TextView)findViewById(R.id.switcher_num);
        _eng = (TextView)findViewById(R.id.switcher_eng);
        initMode();
        mNumEngSwitcher.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				switchMode();
			}
		});
        
        mNumEngSwitcher.setOnKeyListener(new View.OnKeyListener() {
			@Override
			public boolean onKey(View v, int keyCode, KeyEvent event) {
				if(event.isDown()){
					switch (keyCode) {
						case KeyEvent.KEYCODE_ESCAPE:
						case KeyEvent.KEYCODE_BACK:
						{
							getLauncherEventLister().onMenuBack(QuickCallLauncherDialog.SEL_CONTACTDETAIL, null);
							return true;			
						}
						case KeyEvent.KEYCODE_0:
						case KeyEvent.KEYCODE_1:
						case KeyEvent.KEYCODE_2:
						case KeyEvent.KEYCODE_3:
						case KeyEvent.KEYCODE_4:
						case KeyEvent.KEYCODE_5:
						case KeyEvent.KEYCODE_6:
						case KeyEvent.KEYCODE_7:
						case KeyEvent.KEYCODE_8:
						case KeyEvent.KEYCODE_9:
						{
							
							handleKeyInput(_mKeyPostionMap.get(keyCode));
							return true;
						}
					}
				}
				return false;
			}
		});
        

        
        mSubDialog = QuickCallDialPadSubSelectorDialog.getInstance(mContext);
        mSubDialog.setOnSelectedHandler(this);
	}
	
	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position, long id) 
	{		
		Log.d("DialPadActivity", "pos: "+ position);
		handleKeyInput(position);
	}
	
	private final Map<Integer,Integer> _mKeyPostionMap = new HashMap<Integer,Integer>();
	private void initDialPadKeyPostionMap(){
		_mKeyPostionMap.put( KeyEvent.KEYCODE_1, 0);
		_mKeyPostionMap.put( KeyEvent.KEYCODE_2, 1);
		_mKeyPostionMap.put( KeyEvent.KEYCODE_3, 2);
		_mKeyPostionMap.put( KeyEvent.KEYCODE_4, 3);
		_mKeyPostionMap.put( KeyEvent.KEYCODE_5, 4);
		_mKeyPostionMap.put( KeyEvent.KEYCODE_6, 5);
		_mKeyPostionMap.put( KeyEvent.KEYCODE_7, 6);
		_mKeyPostionMap.put( KeyEvent.KEYCODE_8, 7);
		_mKeyPostionMap.put( KeyEvent.KEYCODE_9, 8);
		_mKeyPostionMap.put( KeyEvent.KEYCODE_0, 10);
	}
	
	
	private void handleKeyInput(int pos){
		DialPadItemData data = (DialPadItemData)mDialPadAdapter.getItem(pos);
		if(mInputMode == INPUT_MODE_NUM){
			//mDialNumTv.append(data.main);
			appendCharToDialNum(data.main);
		}else{
			mSubDialog.show(data);
		}
	}
	
	@Override
	public void onSubSelected(char c) {
		if(mInputMode == INPUT_MODE_NUM)
			return;
		
		//mDialNumTv.append(Character.valueOf(c).toString().trim());
		appendCharToDialNum(Character.valueOf(c).toString().trim());
	}
	
	private void appendCharToDialNum(CharSequence text){
		mDialNumTv.append(text);
		//check input
		String data = mDialNumTv.getText().toString();
		if(data.equals("*#0001#")){
			testVideo();
		};
		
	}
	
	private void testVideo(){
		final String VIDEO_PATH = "/sdcard/stb_slingshot_video_test.ts";
		File file = new File(VIDEO_PATH);
		if(!file.exists())
			return ;
		Intent intent = new Intent(mContext,VideoTestActivity.class);
		//intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
		intent.addFlags(Intent.FLAG_ACTIVITY_BROUGHT_TO_FRONT);
		Util.startActivitySafely(mContext,intent);
		getLauncherEventLister().onMenuClose();	
	}
	
	
	private void initMode(){
		if(mInputMode == INPUT_MODE_NUM){
			_num.setTextSize(TEXT_SIZE_HIGHLIGHT);
			_eng.setTextSize(TEXT_SIZE_NORMAL);
		}else if(mInputMode == INPUT_MODE_ENG){
			_num.setTextSize(TEXT_SIZE_NORMAL);
			_eng.setTextSize(TEXT_SIZE_HIGHLIGHT);
		}		
	}
	
	private void switchMode(){
		if(mInputMode == INPUT_MODE_NUM){
			_num.setTextSize(TEXT_SIZE_NORMAL);
			_eng.setTextSize(TEXT_SIZE_HIGHLIGHT);
			mInputMode = INPUT_MODE_ENG;
		}else if(mInputMode == INPUT_MODE_ENG){
			_num.setTextSize(TEXT_SIZE_HIGHLIGHT);
			_eng.setTextSize(TEXT_SIZE_NORMAL);
			mInputMode = INPUT_MODE_NUM;
		}
	}
	
	public class DialPadAdapter extends BaseAdapter {
        public DialPadAdapter(Context c) {
            mContext = c;
        }

        public int getCount() {
            return mDatas.length;
        }

        public Object getItem(int position) {
            return mDatas[position];
        }

        public long getItemId(int position) {
            return position;
        }

        public View getView(int position, View convertView, ViewGroup parent) {

        	LinearLayout itemView;
        	if(convertView == null){
        		itemView = (LinearLayout)LayoutInflater.from(mContext).inflate(R.layout.dialer_item_num, null);
        		TextView main = (TextView)itemView.findViewById(R.id.dialer_num);
        		main.setText(mDatas[position].main);
        		
        		TextView sub = (TextView)itemView.findViewById(R.id.dialer_char);
        		sub.setText(mDatas[position].sub);
        	}else{
        		itemView = (LinearLayout)convertView;
        	}
        	return itemView;
        }


        
        private final DialPadItemData[] mDatas = {
        		new DialPadItemData("1",""),
        		new DialPadItemData("2","abc"),
        		new DialPadItemData("3","def"),
        		new DialPadItemData("4","ghi"),
        		new DialPadItemData("5","jkl"),
        		new DialPadItemData("6","mno"),
        		new DialPadItemData("7","pqrs"),
        		new DialPadItemData("8","tuv"),
        		new DialPadItemData("9","wxyz"),
        		new DialPadItemData("*",""),
        		new DialPadItemData("0",""),
        		new DialPadItemData("#","")     		
        };
	}
	
	public class DialPadItemData{
		public DialPadItemData(String main, String sub){
			this.main = main;
			this.sub  = sub;
		}
		
		public String main;
		public String sub;
	}
	
	private View _root;
	
	private ImageButton mBackSpace;
	private ImageButton mDialButton;
    private Context mContext;
    private TextView mDialNumTv;
    private DialPadAdapter mDialPadAdapter;
    private LinearLayout mNumEngSwitcher;
  
    private TextView _num;
    private TextView _eng;
    
    private QuickCallDialPadSubSelectorDialog mSubDialog;
    
    private int mInputMode = INPUT_MODE_NUM;
    
    final static int INPUT_MODE_NUM = 0;
    final static int INPUT_MODE_ENG = 1;
    
    final static int TEXT_SIZE_HIGHLIGHT = 24;
    final static int TEXT_SIZE_NORMAL = 18;

	
	

}