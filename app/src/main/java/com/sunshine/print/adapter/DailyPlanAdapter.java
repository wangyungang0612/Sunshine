package com.sunshine.print.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.TextView;

import com.sunshine.print.R;
import com.sunshine.print.object.BillObject;
import com.sunshine.print.object.PlanObject;

import java.util.HashMap;
import java.util.List;

/**
 * Created by wdongjia on 2016/8/15.
 */
public class DailyPlanAdapter extends BaseAdapter {

    private Context mContext;

    private LayoutInflater mInflater;

    private List<PlanObject> mDatas;

    //定义hashMap 用来存放之前创建的每一项item
    HashMap<Integer, View> hashMap = new HashMap<Integer, View>();

    private OnClickListener onClickListener;

    public DailyPlanAdapter(Context context, List<PlanObject> datas,
                            OnClickListener onClickListener) {
        mContext = context;
        mInflater = LayoutInflater.from(mContext);
        mDatas = datas;
        this.onClickListener = onClickListener;
    }

    @Override
    public int getCount() {
        return (mDatas != null ? mDatas.size() : 0);
    }

    @Override
    public Object getItem(int position) {
        return (mDatas != null ? mDatas.get(position) : null);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        //创建每一个滑动出来的item项，将创建出来的项，放入数组中，为下次复用使用
        if (hashMap.get(position) == null) {
            convertView = mInflater.inflate(R.layout.item_layout, null);
            holder = new ViewHolder(convertView);
            convertView.setTag(holder);
            hashMap.put(position, convertView);
        } else {
            convertView = hashMap.get(position);
            holder = (ViewHolder) convertView.getTag();
        }

        PlanObject plan = mDatas.get(position);
        if (position % 2 == 0) {
            convertView.setBackgroundResource(R.color.item_1);
        } else {
            convertView.setBackgroundResource(R.color.item_2);
        }

        if (plan != null) {
            holder.snTextView.setText(plan.getSn());
            holder.passTextView.setText(plan.getPass());
            holder.macTextView.setText(plan.getMac());
            holder.pnoTextView.setText(plan.getPno());
            holder.encTextView.setText(plan.getEncryption());
            holder.dateTextView.setText(plan.getDate());
            holder.desTextView.setText(plan.getDescription());
            holder.keyTextView.setText(plan.getKey());

        }
        return convertView;
    }

    class ViewHolder {
        //        ImageView avatar;
        TextView snTextView;
        TextView passTextView;
        TextView macTextView;
        TextView pnoTextView;
        TextView encTextView;
        TextView dateTextView;
        TextView desTextView;
        TextView keyTextView;

        public ViewHolder(View convertView) {
            snTextView = (TextView) convertView.findViewById(R.id.sn_textview);
            passTextView = (TextView) convertView.findViewById(R.id.pass_textview);
            macTextView = (TextView) convertView.findViewById(R.id.mac_textview);
            pnoTextView = (TextView) convertView.findViewById(R.id.pno_textview);
            encTextView = (TextView) convertView.findViewById(R.id.enc_textview);
            dateTextView = (TextView) convertView.findViewById(R.id.date_textview);
            desTextView = (TextView) convertView.findViewById(R.id.des_textview);
            keyTextView = (TextView) convertView.findViewById(R.id.key_textview);
        }
    }
}