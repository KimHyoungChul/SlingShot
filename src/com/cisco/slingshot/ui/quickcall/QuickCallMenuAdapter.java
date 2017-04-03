package com.cisco.slingshot.ui.quickcall;

import java.util.ArrayList;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.cisco.slingshot.R;


public class QuickCallMenuAdapter extends BaseAdapter{
	
    private Context mContext;
	private ArrayList<QuickCallMenuItem> mMenuItemList = new ArrayList<QuickCallMenuItem>();

	public QuickCallMenuAdapter(Context ctx){
		mContext = ctx;
	}
    public void addItem(QuickCallMenuItem item){
    	mMenuItemList.add(item);
    }
    
	@Override
	public int getCount() {
        return mMenuItemList.size();
    }
	@Override
    public Object getItem(int pos) {
        return mMenuItemList.get(pos);
    }
	@Override
    public long getItemId(int pos) {
        return pos;
    }

   @Override
    public View getView(int position, View convertView, ViewGroup parent){
	   if( convertView == null){
		   	convertView = LayoutInflater.from(mContext).inflate(R.layout.diag_outgoing_call_menu_item, null);
		   	
		   	QuickCallMenuItem item= mMenuItemList.get(position);
		    		   	
			TextView menuItem = (TextView)convertView.findViewById(R.id.menu_item_name);
			menuItem.setText(item.mName);
			
		   	ImageView image = (ImageView)convertView.findViewById(R.id.menu_item_icon);
		   	image.setImageResource(item.mIconId);
		   	
		   	
		   	if(item.mHasSubMenu == false){
		   		ImageView pin = (ImageView)convertView.findViewById(R.id.menu_item_pin);
		   		pin.setVisibility(View.INVISIBLE);
		   	}
			
	   }
	   return convertView;
    }	

}

 abstract class QuickCallMenuItem {
	public String mName;
	public int mIconId;
	public boolean mHasSubMenu;

	public QuickCallMenuItem(String name,int iconId,boolean hasSubMenu){
		mName = name;
		mIconId = iconId;
		mHasSubMenu = hasSubMenu;
		
	}
	abstract public void handleMenuIntent();
	
}

