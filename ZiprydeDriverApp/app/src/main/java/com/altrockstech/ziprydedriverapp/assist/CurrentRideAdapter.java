package com.altrockstech.ziprydedriverapp.assist;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.altrockstech.ziprydedriverapp.R;

import java.util.LinkedList;

/**
 * Created by Hari on 18-06-2017.
 */

public class CurrentRideAdapter extends BaseAdapter {

    public LinkedList<CurrentRideDetails> currentRideDetailsList;
    public Context context;

    public CurrentRideAdapter(LinkedList<CurrentRideDetails> currentRideDetailsList, Context context) {
        this.currentRideDetailsList = currentRideDetailsList;
        this.context = context;
    }

    private class ViewHolder {
        TextView bookingCRN, bookingDate, bookingStarting, bookingEnding;
    }

    @Override
    public int getCount() {
        return currentRideDetailsList.size();
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
            view = li.inflate(R.layout.currentzipryde_listitem, parent, false);
            holder = new ViewHolder();

            holder.bookingCRN = (TextView) view.findViewById(R.id.bookingCRN);
            holder.bookingDate = (TextView) view.findViewById(R.id.bookingDate);
            holder.bookingStarting = (TextView) view.findViewById(R.id.bookingStarting);
            holder.bookingEnding = (TextView) view.findViewById(R.id.bookingEnding);

            view.setTag(holder);
        } else {
            holder = (ViewHolder) view.getTag();
        }

        CurrentRideDetails currentRideDetails = currentRideDetailsList.get(position);

        String crnNumber = currentRideDetails.getCrnNumber();
        String dateTime = currentRideDetails.getDateTime();
        String pickupLocation = currentRideDetails.getPickupLocation();
        String dropLocation = currentRideDetails.getDropLocation();

        holder.bookingCRN.setText("" + crnNumber);
        holder.bookingDate.setText("" + dateTime);
        holder.bookingStarting.setText("" + pickupLocation);
        holder.bookingEnding.setText("" + dropLocation);

        return view;
    }

}