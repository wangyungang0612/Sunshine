package com.sunshine.print.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.sunshine.print.CallbackBundle;
import com.sunshine.print.R;
import com.sunshine.print.object.BillObject;
import com.sunshine.print.object.PlanObject;

import java.util.ArrayList;
import java.util.List;

;

public class BillAdapter2 extends BaseAdapter {

	private List<PlanObject> list;
	private LayoutInflater inflater;
	// private String year,month;



	public BillAdapter2(Context context, ArrayList<PlanObject> list) {
		inflater = LayoutInflater.from(context);
		this.list = list;

	}

//	public BillAdapter2(Context context, List<PlanObject> datas,
//							View.OnClickListener onClickListener) {
//		mContext = context;
//		mInflater = LayoutInflater.from(mContext);
//		mDatas = datas;
//		this.onClickListener = onClickListener;
//	}



	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return list.size();
	}

	@Override
	public Object getItem(int position) {
		// TODO Auto-generated method stub
		return list.get(position);
	}

	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return position;
	}

	public void changeList(List<PlanObject> list) {
		this.list = list;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		// TODO Auto-generated method stub
		final ViewHolder holder;
		if (convertView == null) {
			holder = new ViewHolder();
			convertView = inflater.inflate(R.layout.bill_items2, null);
			holder.snTextView = (TextView) convertView.findViewById(R.id.sn_textview);
			holder.passTextView = (TextView) convertView.findViewById(R.id.pass_textview);
			holder.macTextView = (TextView) convertView.findViewById(R.id.mac_textview);
			holder.pnoTextView = (TextView) convertView.findViewById(R.id.pno_textview);
			holder.encTextView = (TextView) convertView.findViewById(R.id.enc_textview);
			holder.dateTextView = (TextView) convertView.findViewById(R.id.date_textview);
			holder.desTextView = (TextView) convertView.findViewById(R.id.des_textview);
			holder.keyTextView = (TextView) convertView.findViewById(R.id.key_textview);
			
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}
		PlanObject planObject = list.get(position);
		holder.snTextView.setText(planObject.getSn());
		holder.passTextView.setText(planObject.getPass());
		holder.macTextView.setText(planObject.getMac());
		holder.pnoTextView.setText(planObject.getPno());
		holder.encTextView.setText(planObject.getEncryption());
		holder.dateTextView.setText(planObject.getDate());
		holder.desTextView.setText(planObject.getDescription());
		holder.keyTextView.setText(planObject.getKey());


		return convertView;
	}

	public class ViewHolder {
		ImageView icon;
		TextView snTextView;
		TextView passTextView;
		TextView macTextView;
		TextView pnoTextView;
		TextView encTextView;
		TextView dateTextView;
		TextView desTextView;
		TextView keyTextView;
	}
}
