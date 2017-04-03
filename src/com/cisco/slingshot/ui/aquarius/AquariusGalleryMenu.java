package com.cisco.slingshot.ui.aquarius;

import java.util.ArrayList;
import java.util.Iterator;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.Gallery;
import android.widget.TextView;
import android.widget.Toast;

import com.cisco.slingshot.R;
import com.cisco.slingshot.utils.Util;

public class AquariusGalleryMenu extends AquariusViewBase{
	
	public static final String LOG_TAG = "AquariusGalleryMenu";
	private View _rootView;
	
	private ArrayList<AquariusMenuCommandHandler> mCmdHandlers = new ArrayList<AquariusMenuCommandHandler>(); 
	private MenuAdapter mMenuAdapter;
	
	public AquariusGalleryMenu(Context context) {
		super(context);
		mContext = context;
		initView();
	}
	
	public AquariusGalleryMenu(Context context, AttributeSet attrs) {
		super(context, attrs);
		mContext = context;
		initView();
	}


	public AquariusGalleryMenu(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		mContext = context;
		initView();
	}

	
	private void initView(){
		Util.S_Log.d(LOG_TAG, "Create AquariusGalleryMenu... ");
		_rootView = LayoutInflater.from(mContext).inflate(R.layout.aquarius_menu_layout, null);
		this.addView(_rootView);
        Gallery g = (Gallery) findViewById(R.id.aquarius_menu_gallery);
        // Set the adapter to our custom adapter (below)
        
        mMenuAdapter = new MenuAdapter(mContext);
        g.setAdapter(mMenuAdapter);
        // Set a item click listener, and just Toast the clicked position
        g.setOnItemClickListener(new OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View v, int position, long id) {
                Toast.makeText(mContext, "" + position, Toast.LENGTH_SHORT).show();
                View contentView = ((DataItem)mMenuAdapter.getItem(position)).contentView;
                if(contentView == null)
                	return;
                postCommand(AquariusMenuCommandHandler.CommandType.SHOW_CONTENT,contentView);
            }
        });
     
    	g.setOnFocusChangeListener(new OnFocusChangeListener() {

			@Override
			public void onFocusChange(View v, boolean hasFocus) {
				Util.S_Log.d(LOG_TAG, "Focus changed!");
				
			}
    		
    	});
	}
	
	public void addMenuCommandHandler(AquariusMenuCommandHandler handler){
		mCmdHandlers.add(handler);
	}
	
	public void removeMenuCommandHandler(AquariusMenuCommandHandler handler){
		mCmdHandlers.remove(handler);
	}
	

	public void setViewGroupFocusable(boolean focusable){
		Gallery g = (Gallery) findViewById(R.id.aquarius_menu_gallery);
		g.setFocusable(focusable);
	}
	

	
	
	
	private void postCommand(AquariusMenuCommandHandler.CommandType type,View customView){
    	Iterator<AquariusMenuCommandHandler> itor = mCmdHandlers.iterator();
		while(itor.hasNext()){
			AquariusMenuCommandHandler handler = itor.next();
			handler.handleMenuCommand(type,customView);
		}
	}
	
	
	public class MenuAdapter extends BaseAdapter {
        private static final int ITEM_WIDTH = 200;
        private static final int ITEM_HEIGHT = 36;

        private final Context mContext;
        
        private final ArrayList<DataItem> mDataItem = new  ArrayList<DataItem>();
        /*
        private final DataItem[] mDataItem = {
        		new DataItem("Dial",null),
        		new DataItem("Contact",Object.class),
        		new DataItem("History",null),
        		new DataItem("Settings",null),
        		
        };
        */
        /*
        private final String[] mTitles = {
        	"Dial",
        	"Contact",
        	"History",
        	"Settings"
        };
		*/


        public MenuAdapter(Context c) {
            mContext = c;
            mDataItem.add(new DataItem("Dial",null ));
            mDataItem.add(new DataItem("Contact",new AquariusContactListView(mContext) ));
            mDataItem.add(new DataItem("History",null ));
            mDataItem.add(new DataItem("Settings",null ));
        }

        public int getCount() {
            return mDataItem.size();
        }

        public Object getItem(int position) {
            return mDataItem.get(position);
        }

        public long getItemId(int position) {
            return position;
        }

        public View getView(int position, View convertView, ViewGroup parent) {
        	
        	TextView tv;
        	if(convertView == null){
        		convertView = LayoutInflater.from(mContext).inflate(R.layout.aquarius_menu_item_layout, null);
        		tv = (TextView)convertView;
        		
        		tv.setLayoutParams(new Gallery.LayoutParams(
                        (int) (ITEM_WIDTH),
                        (int) (ITEM_HEIGHT)));
                        
        		tv.setText(((DataItem)getItem(position)).title);
        	}else{
        		tv = (TextView)convertView;
        	}
        	return tv;
        }
	}
	
	
	private class DataItem{
		public String title;
		public View contentView;
		public DataItem(String title, View view){
			this.title = title;
			this.contentView = view;
		}
	}
}