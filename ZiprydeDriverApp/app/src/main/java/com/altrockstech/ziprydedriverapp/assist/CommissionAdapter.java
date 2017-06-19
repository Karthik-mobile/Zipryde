package com.altrockstech.ziprydedriverapp.assist;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.altrockstech.ziprydedriverapp.R;

import java.util.LinkedList;

/**
 * Created by Hari on 18-06-2017.
 */

public class CommissionAdapter extends BaseAdapter {

    public LinkedList<CommissionDetails> commissionDetailsList;
    public Context context;

    public CommissionAdapter(LinkedList<CommissionDetails> commissionDetailsList, Context context) {
        this.commissionDetailsList = commissionDetailsList;
        this.context = context;
    }

    private class ViewHolder {
        TextView contentText, timeText;
    }

    @Override
    public int getCount() {
        return commissionDetailsList.size();
    }

    @Override
    public Object getItem(int position) {
        return position;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        View view = convertView;
        final ViewHolder holder;
        if (convertView == null) {
            LayoutInflater li = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = li.inflate(R.layout.commission_listitem, parent, false);
            holder = new ViewHolder();

            holder.contentText = (TextView) view.findViewById(R.id.contentText);
            holder.timeText = (TextView) view.findViewById(R.id.timeText);

            view.setTag(holder);
        } else {
            holder = (ViewHolder) view.getTag();
        }

        CommissionDetails commissionDetails = commissionDetailsList.get(position);

        String commissionContent = commissionDetails.getCommissionContent();
        String commissionTime = commissionDetails.getCommissionTime();

        holder.contentText.setText(""+commissionContent);
        holder.timeText.setText(""+commissionTime);

        return view;
    }
}
