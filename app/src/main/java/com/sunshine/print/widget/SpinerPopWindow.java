package com.sunshine.print.widget;

import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.PopupWindow;

import com.sunshine.print.MainActivity;
import com.sunshine.print.R;

import java.util.List;

public class SpinerPopWindow extends PopupWindow implements  AdapterView.OnItemClickListener, AdapterView.OnItemLongClickListener  {

	private Context mContext;
	private ListView mListView;
	private NormalSpinerAdapter mAdapter;
	private AbstractSpinerAdapter.IOnItemSelectListener mItemSelectListener;
	private AbstractSpinerAdapter.IOnItemLongClickListener mItemLongListener;
	
	
	public SpinerPopWindow(Context context)
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
	
		
		mListView = (ListView) view.findViewById(R.id.listview);
		

		mAdapter = new NormalSpinerAdapter(mContext);	
		mListView.setAdapter(mAdapter);	
		mListView.setOnItemClickListener(this);
		mListView.setOnItemLongClickListener(this);
	}
	
	
	public void refreshData(List<String> list, int selIndex)
	{
		if (list != null && selIndex  != -1)
		{
			mAdapter.refreshData(list, selIndex);
		}
	}


	@Override
	public void onItemClick(AdapterView<?> arg0, View view, int pos, long arg3) {
		dismiss();
		if (mItemSelectListener != null){
			mItemSelectListener.onItemClick(pos);
		}
	}


	@Override
	public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
		if (mItemLongListener != null){
			mItemLongListener.onItemLongClick(position);
		}
		return true;
	}
}
