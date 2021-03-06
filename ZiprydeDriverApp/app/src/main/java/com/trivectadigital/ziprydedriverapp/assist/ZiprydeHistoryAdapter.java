package com.trivectadigital.ziprydedriverapp.assist;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.trivectadigital.ziprydedriverapp.R;
import com.trivectadigital.ziprydedriverapp.modelget.ListOfBooking;

import java.util.LinkedList;

/**
 * Created by Hari on 09-06-2017.
 */

public class ZiprydeHistoryAdapter extends BaseAdapter {

    LinkedList<ListOfBooking> ziprydeHistoryDetailsList;
    public Context context;

    public ZiprydeHistoryAdapter(LinkedList<ListOfBooking> ziprydeHistoryDetailsList, Context context) {
        this.ziprydeHistoryDetailsList = ziprydeHistoryDetailsList;
        this.context = context;
    }

    private class ViewHolder {
        TextView bookingDatetime, bookingCRN, bookingStarting, bookingEnding, bookingPrice, bookingOfferPrice, bookingStatus;
        ImageView bookingCarType, bookingProfileImg;
    }

    @Override
    public int getCount() {
        return ziprydeHistoryDetailsList.size();
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
            view = li.inflate(R.layout.yourzipryde_listitem, parent, false);
            holder = new ViewHolder();

            holder.bookingDatetime = (TextView) view.findViewById(R.id.bookingDatetime);
            holder.bookingCRN = (TextView) view.findViewById(R.id.bookingCRN);
            holder.bookingStarting = (TextView) view.findViewById(R.id.bookingStarting);
            holder.bookingEnding = (TextView) view.findViewById(R.id.bookingEnding);
            holder.bookingPrice = (TextView) view.findViewById(R.id.bookingPrice);
            holder.bookingOfferPrice = (TextView) view.findViewById(R.id.bookingOfferPrice);
            holder.bookingStatus = (TextView) view.findViewById(R.id.bookingStatus);

            holder.bookingCarType = (ImageView) view.findViewById(R.id.bookingCarType);
            holder.bookingProfileImg = (ImageView) view.findViewById(R.id.bookingProfileImg);

            view.setTag(holder);
        } else {
            holder = (ViewHolder) view.getTag();
        }

        //Toast.makeText(context,"Reached Here",Toast.LENGTH_SHORT).show();
        ListOfBooking ziprydeHistoryDetails = ziprydeHistoryDetailsList.get(position);

        //Convert the time to system time.
//        String ziprydeBookingCarType = ziprydeHistoryDetails.getZiprydeBookingCarType();
//        Calendar calendar = Calendar.getInstance();
//        TimeZone zone = calendar.getTimeZone();
//        SimpleDateFormat sdf = new SimpleDateFormat("EEE, d MMM yyyy HH:mm");
//        sdf.setTimeZone(zone);
//        Date sysDate = new Date();
//        try {
//            sysDate = sdf.parse(ziprydeHistoryDetails.getBookingDateTime());
//        } catch (ParseException e) {
//            e.printStackTrace();
//        }
//        SimpleDateFormat sdf1 = new SimpleDateFormat("EEE, d MMM yyyy HH:mm");
//        sdf.setTimeZone(zone);
//        String systemTime = sdf1.format(sysDate.getTime());

        String ziprydeBookingDateTime =  Utils.UTCtoSysTime(ziprydeHistoryDetails.getBookingDateTime());
        String ziprydeBookingCRN = ziprydeHistoryDetails.getCrnNumber();
        String ziprydeBookingStarting = ziprydeHistoryDetails.getFrom();
        String ziprydeBookingEnding = ziprydeHistoryDetails.getTo();
        String ziprydeBookingPrice = ziprydeHistoryDetails.getSuggestedPrice();
        String ziprydeBookingOfferPrice = ziprydeHistoryDetails.getOfferedPrice();
        String ziprydeBookingStatus = ziprydeHistoryDetails.getBookingStatus();
//        String ziprydeBookingProfileImg = ziprydeHistoryDetails.getZiprydeBookingProfileImg();

        holder.bookingDatetime.setText(""+ziprydeBookingDateTime);
        holder.bookingCRN.setText(""+ziprydeBookingCRN);
        holder.bookingStarting.setText(""+ziprydeBookingStarting);
        holder.bookingEnding.setText(""+ziprydeBookingEnding);
        holder.bookingPrice.setText("$ "+ziprydeBookingPrice);
        holder.bookingOfferPrice.setText("Offer $ "+ziprydeBookingOfferPrice);
        holder.bookingStatus.setText(""+ziprydeBookingStatus);

        return view;
    }
}
