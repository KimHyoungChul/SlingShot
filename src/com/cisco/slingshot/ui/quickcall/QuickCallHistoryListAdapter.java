package com.cisco.slingshot.ui.quickcall;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.cisco.slingshot.R;
import com.cisco.slingshot.contact.Contact;
import com.cisco.slingshot.contact.ContactDatabase;
import com.cisco.slingshot.history.HistoryItem;
/**
 * Create a empty list when there is not any data
 * @author yuancui
 *
 */
public class QuickCallHistoryListAdapter extends BaseAdapter{
	
    private Context mContext;
	private ArrayList<HistoryItem> mMenuItemList;

    
	public QuickCallHistoryListAdapter(Context ctx, ArrayList<HistoryItem> data){
		mContext = ctx;
		mMenuItemList = data;
	}
	
    public void addItem(HistoryItem item){
    	if(mMenuItemList == null)return;
    	mMenuItemList.add(item);
    	this.notifyDataSetChanged();
    }
    
    public void removeItem(HistoryItem item){
    	if(mMenuItemList == null)return;
    	mMenuItemList.remove(item);
    	this.notifyDataSetChanged();
    }
    
    
    public void changeData(ArrayList<HistoryItem> data){
    	mMenuItemList = data;
    	this.notifyDataSetChanged();
    }
    
    
	@Override
	public int getCount() {

		if(mMenuItemList == null || mMenuItemList.size() == 0){
			return 1;
		}else{
			return mMenuItemList.size();
		}
    }
	@Override
    public Object getItem(int pos) {

		if(mMenuItemList == null || mMenuItemList.size() == 0){
			return null;
		}else{
			return mMenuItemList.get(pos);	
		}
    }
	@Override
    public long getItemId(int pos) {
        return pos;
    }

   @Override
    public View getView(int position, View convertView, ViewGroup parent){
	   if(mMenuItemList == null ||mMenuItemList.size() == 0){
		   //create a empty list view
			convertView = LayoutInflater.from(mContext).inflate(R.layout.historylist_footer, null);
	   }else{
	   
		   if( convertView == null){
			   
				 //create normal list views
			   	convertView = LayoutInflater.from(mContext).inflate(R.layout.diag_outgoing_call_history_item, null);
			   	
			   	TextView name = (TextView)convertView.findViewById(R.id.history_list_item_name);
			   	TextView address = (TextView)convertView.findViewById(R.id.history_list_item_address);
			   	TextView date = (TextView)convertView.findViewById(R.id.history_list_item_date);
			   	TextView time = (TextView)convertView.findViewById(R.id.history_list_item_time);
			   	ImageView image = (ImageView)convertView.findViewById(R.id.history_list_item_image);
			   	
			   	HistoryItem item = mMenuItemList.get(position);
			   	
			   	String text_address = item._address;
			   	String text_date    = item._date.toString();
			   	String text_time    = item._time.toString();
			   	String text_name;
		    	Contact userInDb = Contact.findContactByAddress(mContext, item._address);
		    	if(userInDb != null){
		    		text_name = userInDb.get_username();
		    	}else{
		    		text_name = text_address;
		    	}
		    	if(item._type == HistoryItem.HISTORY_TYPE_INCOMING){
		    		image.setImageDrawable(mContext.getResources().getDrawable(android.R.drawable.sym_call_incoming));
		    	}else if(item._type == HistoryItem.HISTORY_TYPE_OUTGOING){
		    		image.setImageDrawable(mContext.getResources().getDrawable(android.R.drawable.sym_call_outgoing));
		    	}else if(item._type == HistoryItem.HISTORY_TYPE_MISSING){
		    		image.setImageDrawable(mContext.getResources().getDrawable(android.R.drawable.sym_call_missed));
		    	}
		    		
		    	
		    	name.setText(text_name);
		    	address.setText(text_address);
		    	date.setText(text_date);
		    	time.setText(text_time);
			   
		   }
		   	
	   }
	   
	   return convertView;
    }	
}