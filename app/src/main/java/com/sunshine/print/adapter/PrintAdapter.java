package com.sunshine.print.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.sunshine.print.R;

import java.util.ArrayList;
import java.util.List;


/**
 * Created by wangyungang on 2016/6/15.
 */
public class PrintAdapter extends BaseAdapter {
    Context context;
    private List<String> timeList = new ArrayList<String>();
    private List<String> one_codeList = new ArrayList<String>();
    private List<String> two_codeList = new ArrayList<String>();
    private List<String> statusList = new ArrayList<String>();
    OnItemClickLitener mOnItemClickLitener;

    public PrintAdapter(List<String> timeList, List<String> one_codeList, List<String> two_codeList, List<String> statusList, Context context) {
      //  L.e("wyg --- list : " + doorList);
        this.timeList = timeList;
        this.one_codeList = one_codeList;
        this.two_codeList = two_codeList;
        this.statusList = statusList;
        this.context = context;
    }

    /**
     * ItemClick的回调接口
     */
    public interface OnItemClickLitener {
        public void onItemClick(View view, int position);
    }

    public void setOnItemClickLitener(OnItemClickLitener mOnItemClickLitener) {
        this.mOnItemClickLitener = mOnItemClickLitener;
    }

    @Override
    public int getCount() {
        return timeList.size();
    }

    @Override
    public Object getItem(int position) {
        return timeList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }


    @Override
    public View getView(int position, View convertView, ViewGroup viewGroup) {
        ViewHolder viewHolder;
        if(convertView == null){
            convertView = LayoutInflater.from(context).inflate(R.layout.item_print,null);
            viewHolder=new ViewHolder();
            viewHolder.timeTextView=(TextView)convertView.findViewById(R.id.time);
            viewHolder.onecodeTextView=(TextView)convertView.findViewById(R.id.one_code);
            viewHolder.twocodeTextView=(TextView)convertView.findViewById(R.id.two_code);
            viewHolder.statusTextView=(TextView)convertView.findViewById(R.id.status);
            convertView.setTag(viewHolder);
        }else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        if (position % 2 == 0) {
            convertView.setBackgroundResource(R.color.list_line1_bg);
        } else {
            convertView.setBackgroundResource(R.color.list_line2_bg);
        }
        viewHolder.timeTextView.setText(timeList.get(position));
        viewHolder.onecodeTextView.setText(one_codeList.get(position));
        viewHolder.twocodeTextView.setText(two_codeList.get(position));
        viewHolder.statusTextView.setText(statusList.get(position));
        return convertView;
    }

    class ViewHolder {
        TextView timeTextView;
        TextView onecodeTextView;
        TextView twocodeTextView;
        TextView statusTextView;
    }


}
