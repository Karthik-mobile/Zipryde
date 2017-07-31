package com.trivectadigital.ziprydedriverapp.assist;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.trivectadigital.ziprydedriverapp.NotificationActivity;
import com.trivectadigital.ziprydedriverapp.R;
import com.trivectadigital.ziprydedriverapp.RideActivity;

import org.json.JSONArray;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.LinkedList;

/**
 * Created by Hari on 18-06-2017.
 */

public class CommissionAdapter extends BaseAdapter {

    public Context context;

    public CommissionAdapter(Context context) {
        this.context = context;
    }

    private class ViewHolder {
        TextView contentText, timeText;
        ImageView deleteImg;
    }

    @Override
    public int getCount() {
        return Utils.commissionDetailsList.size();
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
            holder.deleteImg = (ImageView) view.findViewById(R.id.deleteImg);

            view.setTag(holder);
        } else {
            holder = (ViewHolder) view.getTag();
        }

        CommissionDetails commissionDetails = Utils.commissionDetailsList.get(position);

        String commissionContent = commissionDetails.getCommissionContent();
        String commissionTime = commissionDetails.getCommissionTime();

        holder.contentText.setText("" + commissionContent);
        holder.timeText.setText("" + commissionTime);

        holder.deleteImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.e("size 11", "" + Utils.commissionDetailsList.size());
                Utils.commissionDetailsList.remove(position);
                Log.e("size 22", "" + Utils.commissionDetailsList.size());
                try {
                    SharedPreferences pref = context.getSharedPreferences(Utils.SHARED_NOTIFI, 0);
                    String notifications = pref.getString("notification", null);
                    if (notifications != null) {
                        JSONArray jsonArray = new JSONArray();
                        if(Utils.commissionDetailsList.size() > 0){
                            for (int i = 0 ; i < Utils.commissionDetailsList.size(); i++){
                                JSONObject jsonObject = new JSONObject();
                                jsonObject.put("message", "" + Utils.commissionDetailsList.get(i).getCommissionContent());
                                jsonObject.put("time", "" + Utils.commissionDetailsList.get(i).getCommissionTime());
                                jsonArray.put(jsonObject);
                            }
                            SharedPreferences.Editor editor = pref.edit();
                            editor.putString("notification", "" + jsonArray);
                            editor.commit();
                        }else{
                            SharedPreferences.Editor editor = pref.edit();
                            editor.putString("notification", null);
                            editor.commit();
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                Intent ide = new Intent(context, NotificationActivity.class);
                ide.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                context.startActivity(ide);
                ((AppCompatActivity) context).finish();
            }
        });

        return view;
    }
}
