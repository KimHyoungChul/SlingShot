package com.cisco.slingshot.ui;

import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.util.AttributeSet;
import android.widget.Checkable;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.cisco.slingshot.R;

/**
 * A List item class used by ListView, has a pin to indicate the focused status.
 * @author yuancui
 *
 */
public class CheckableListItem extends LinearLayout implements Checkable {
	private boolean mChecked = false;
	private Context mContext;

    public CheckableListItem(Context context) {
        super(context);
        mContext = context;
    }

    public CheckableListItem(Context context, AttributeSet attrs) {
        super(context, attrs);
        mContext = context;
    }

    public CheckableListItem(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        mContext = context;
    }

	@Override
	public boolean isChecked() {
		return mChecked;
	}

	@Override
	public void setChecked(boolean checked) {
		mChecked = checked;
		ImageView image = (ImageView)this.findViewById(R.id.menu_item_pin);
		image.setBackgroundDrawable(mChecked ? new ColorDrawable(getResources().getColor(R.color.white)) : null);
		
	}

	@Override
	public void toggle() {
		mChecked = !mChecked;
		ImageView image = (ImageView)this.findViewById(R.id.menu_item_pin);
		image.setBackgroundDrawable(mChecked ? new ColorDrawable(getResources().getColor(R.color.white)) : null);
	}
}