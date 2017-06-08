package com.altrockstech.ziprydeuserapp.assist;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.altrockstech.ziprydeuserapp.R;

import java.util.LinkedList;

/**
 * Created by Hari on 08-06-2017.
 */

public class NotificationAdapter extends BaseAdapter {

    public LinkedList<NotificationDetails> notificationDetailsList;
    public Context context;

    public NotificationAdapter(LinkedList<NotificationDetails> notificationDetailsList, Context context) {
        this.notificationDetailsList = notificationDetailsList;
        this.context = context;
    }

    private class ViewHolder {
        TextView notification_titleName, notification_time, notification_content;
        ImageView notification_profileImg;
    }

    @Override
    public int getCount() {
        return notificationDetailsList.size();
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
            view = li.inflate(R.layout.notification_listitem, parent, false);
            holder = new ViewHolder();

            holder.notification_titleName = (TextView) view.findViewById(R.id.notification_titleName);
            holder.notification_time = (TextView) view.findViewById(R.id.notification_time);
            holder.notification_content = (TextView) view.findViewById(R.id.notification_content);
            holder.notification_profileImg = (ImageView) view.findViewById(R.id.notification_profileImg);

            view.setTag(holder);
        } else {
            holder = (ViewHolder) view.getTag();
        }

        NotificationDetails notificationDetails = notificationDetailsList.get(position);

        String notificationName = notificationDetails.getNotificationName();
        String notificationDateTime = notificationDetails.getNotificationDateTime();
        String notificationContent = notificationDetails.getNotificationContent();
        String notificationImg = notificationDetails.getNotificationImg();

        holder.notification_titleName.setText(""+notificationName);
        holder.notification_time.setText(""+notificationDateTime);
        holder.notification_content.setText(""+notificationContent);

        return view;
    }
}
