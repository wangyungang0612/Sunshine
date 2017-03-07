package com.sunshine.print.widget;


import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.widget.AdapterView;

import android.widget.ListView;
import android.widget.PopupWindow;

import com.sunshine.print.MainActivity;
import com.sunshine.print.R;

import java.util.List;

public class SpinerPopWindow2 extends PopupWindow implements AdapterView.OnItemClickListener, AdapterView.OnItemLongClickListener {

	private Context mContext;
	private ListView mListView2;
	private NormalSpinerAdapter2 mAdapter2;
	private AbstractSpinerAdapter2.IOnItemSelectListener2 mItemSelectListener;
	private AbstractSpinerAdapter2.IOnItemLongClickListener mItemLongListener;


	public SpinerPopWindow2(Context context)
	{
		super(context);
		
		mContext = context;
		init();
	}
	
	
	public void setItemListener(MainActivity listener){

		mItemSelectListener = listener;
	}
	public void setItemLongListener(MainActivity listener){
		mItemLongListener = listener;
	}

	
	private void init()
	{
		View view = LayoutInflater.from(mContext).inflate(R.layout.spiner_window_layout, null);
		setContentView(view);		
		setWidth(LayoutParams.WRAP_CONTENT);
		setHeight(LayoutParams.WRAP_CONTENT);
		
		setFocusable(true);
    	ColorDrawable dw = new ColorDrawable(0x00);
		setBackgroundDrawable(dw);
	
		
		mListView2 = (ListView) view.findViewById(R.id.listview);
		

		mAdapter2 = new NormalSpinerAdapter2(mContext);
		mListView2.setAdapter(mAdapter2);
		mListView2.setOnItemClickListener(this);

		mListView2.setOnItemLongClickListener(this);
	}
	
	
	public void refreshData(List<String> list, int selIndex)
	{
		if (list != null && selIndex  != -1)
		{
			mAdapter2.refreshData(list, selIndex);
		}
	}


	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
		dismiss();
		if (mItemSelectListener != null){
			mItemSelectListener.onItemClick2(position);
		}
	}

	@Override
	public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
		if (mItemLongListener != null){
			mItemLongListener.onItemLongClick2(position);
		}
		return true;
	}
}
