package com.cisco.slingshot.ui.statistic;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.widget.FrameLayout;

import com.cisco.slingshot.R;


public class StatisticView extends FrameLayout{
	
	private Context mContext;
	private ViewGroup 	_rootView;
	private ViewGroup   mDataSendingContainerView;
	private ViewGroup   mDataReceivingContainerView;
	private StatisticAdapter mAdapter;
	
	public StatisticView(Context context){
		super(context);
		mContext = context;
		initView();
	}

	public StatisticView(Context context, AttributeSet attr) {
		super(context, attr);
		mContext = context ;
		initView();
	}
	
	private void initView(){
		_rootView = (ViewGroup)LayoutInflater.from(mContext).inflate(R.layout.statistic_view, null);
		mDataSendingContainerView = (ViewGroup)_rootView.findViewById(R.id.stat_send);
		mDataReceivingContainerView = (ViewGroup)_rootView.findViewById(R.id.stat_receive);
		this.addView(_rootView);
	}
	
	protected void addDataSendingItem(View view, boolean visible){
		if(!visible)
			return;
		mDataSendingContainerView.addView(view);
	}
	
	protected void addDataReceivingItem(View view, boolean visible){
		if(!visible)
			return;
		mDataReceivingContainerView.addView(view);
	}
	
	public void setStatisticAdapter(StatisticAdapter adapter){
		mAdapter = adapter;
		mAdapter.setAnchorStatisticView(this);
	}
	
	public void startUpdate(){
		mAdapter.startUpdateAsync();
	}
	
	public void stopUpdate(){
		mAdapter.stopUpdate();
	}

	
	
	
}