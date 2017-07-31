package com.trivectadigital.ziprydedriverapp;

import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.trivectadigital.ziprydedriverapp.assist.CommissionAdapter;
import com.trivectadigital.ziprydedriverapp.assist.CommissionDetails;
import com.trivectadigital.ziprydedriverapp.assist.Utils;
import com.trivectadigital.ziprydedriverapp.service.MyFirebaseMessagingService;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.LinkedList;

public class NotificationActivity extends AppCompatActivity {

    ListView commissionList;

    private static final String TAG = NotificationActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notification);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        LayoutInflater mInflater = LayoutInflater.from(this);
        View mCustomView = mInflater.inflate(R.layout.actionbar_titleback, null);
        toolbar.setContentInsetsAbsolute(0, 0);
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.MATCH_PARENT);
        toolbar.addView(mCustomView, layoutParams);
        TextView titleText = (TextView) mCustomView.findViewById(R.id.titleText);
        titleText.setText("Notifications");
        ImageView backImg = (ImageView) mCustomView.findViewById(R.id.backImg);
        backImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        try {
            SharedPreferences pref = getApplicationContext().getSharedPreferences(Utils.SHARED_NOTIFI, 0);
            String notifications = pref.getString("notification", null);
            if(notifications != null){
                JSONArray jsonArray = new JSONArray(notifications);
                Utils.commissionDetailsList = new LinkedList<CommissionDetails>();
                for(int i = 0; i < jsonArray.length(); i++){
                    JSONObject jsonObject = jsonArray.getJSONObject(i);
                    String message = jsonObject.getString("message");
                    String time = jsonObject.getString("time");
                    Utils.commissionDetailsList.add(new CommissionDetails(""+i, message, time));
                }
            }
        } catch (Exception e) {
            Log.e(TAG, "Exception: " + e.getMessage());
        }
        commissionList = (ListView) findViewById(R.id.commissionList);

        CommissionAdapter commissionAdapter = new CommissionAdapter(this);
        commissionList.setAdapter(commissionAdapter);
    }
}
