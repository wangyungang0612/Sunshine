package com.sunshine.print.widget;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.sunshine.print.R;

import java.util.ArrayList;
import java.util.List;

public abstract class AbstractSpinerAdapter2<T> extends BaseAdapter {

	public static interface IOnItemSelectListener2{
		public void onItemClick2(int pos);
	};

	public interface IOnItemLongClickListener {
		public void onItemLongClick2(int pos);
	}


	 private Context mContext;
	 private List<T> mObjects2 = new ArrayList<T>();
	 private int mSelectItem = 0;

	 private LayoutInflater mInflater;

	 public AbstractSpinerAdapter2(Context context){
		 init(context);
	 }
	 
	 public void refreshData(List<T> objects, int selIndex){
		 mObjects2 = objects;
		 if (selIndex < 0){
			 selIndex = 0;
		 }
		 if (selIndex >= mObjects2.size()){
			 selIndex = mObjects2.size() - 1;
		 }
		 
		 mSelectItem = selIndex;
	 }
	 
	 private void init(Context context) {
	        mContext = context;
	        mInflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	 }
	    
	    
	@Override
	public int getCount() {

		return mObjects2.size();
	}

	@Override
	public Object getItem(int pos) {
		return mObjects2.get(pos).toString();
	}

	@Override
	public long getItemId(int pos) {
		return pos;
	}

	@Override
	public View getView(int pos, View convertView, ViewGroup arg2) {
		 ViewHolder viewHolder;
    	 
	     if (convertView == null) {
	    	 convertView = mInflater.inflate(R.layout.spiner_item_layout2, null);
	         viewHolder = new ViewHolder();
	         viewHolder.mTextView = (TextView) convertView.findViewById(R.id.textView2);
	         convertView.setTag(viewHolder);
	     } else {
	         viewHolder = (ViewHolder) convertView.getTag();
	     }

	     
	     String item = (String) getItem(pos);
		 viewHolder.mTextView.setText(item);

	     return convertView;
	}



	public static class ViewHolder
	{
	    public TextView mTextView;
    }


}
