package com.trivectadigital.ziprydedriverapp;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.trivectadigital.ziprydedriverapp.assist.MessageReceivedEvent;
import com.trivectadigital.ziprydedriverapp.assist.Utils;

import org.json.JSONArray;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Calendar;

import de.greenrobot.event.EventBus;

public class RideActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ride);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        LayoutInflater mInflater = LayoutInflater.from(this);
        View mCustomView = mInflater.inflate(R.layout.actionbar_titleback, null);
        toolbar.setContentInsetsAbsolute(0, 0);
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.MATCH_PARENT);
        toolbar.addView(mCustomView, layoutParams);
        TextView titleText = (TextView) mCustomView.findViewById(R.id.titleText);
        titleText.setText("Your ZipRyde Requests");
        ImageView backImg = (ImageView) mCustomView.findViewById(R.id.backImg);
        backImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        Intent intent = getIntent();
        Log.e("intent ","intent : "+intent.getExtras());
        if(intent.hasExtra("body")){
            Log.e("message ","message : "+intent.getStringExtra("body"));
            try {
                SharedPreferences pref = getApplicationContext().getSharedPreferences(Utils.SHARED_NOTIFI, 0);
                String notifications = pref.getString("notification", null);
                if(notifications != null){
                    JSONArray jsonArray = new JSONArray(notifications);
                    Log.e("JSONArray", "jsonArray 11: " + jsonArray.toString());
                    JSONObject jsonObject = new JSONObject();
                    Calendar c = Calendar.getInstance();
                    System.out.println("Current time => " + c.getTime());
                    SimpleDateFormat df = new SimpleDateFormat("dd MMM yyyy hh:mm a");
                    String formattedDate = df.format(c.getTime());
                    jsonObject.put("message",""+intent.getStringExtra("body"));
                    jsonObject.put("time",""+formattedDate);
                    Log.e("jsonObject", "jsonObject: " + jsonObject.toString());
                    jsonArray.put(jsonObject);
                    Log.e("JSONArray", "jsonArray 22: " + jsonArray.toString());
                    SharedPreferences.Editor editor = pref.edit();
                    editor.putString("notification", ""+jsonArray);
                    editor.commit();
                }else{
                    JSONArray jsonArray = new JSONArray();
                    Log.e("JSONArray", "jsonArray 11: " + jsonArray.toString());
                    JSONObject jsonObject = new JSONObject();
                    Calendar c = Calendar.getInstance();
                    System.out.println("Current time => " + c.getTime());
                    SimpleDateFormat df = new SimpleDateFormat("dd MMM yyyy hh:mm a");
                    String formattedDate = df.format(c.getTime());
                    jsonObject.put("message",""+intent.getStringExtra("body"));
                    jsonObject.put("time",""+formattedDate);
                    Log.e("jsonObject", "jsonObject: " + jsonObject.toString());
                    jsonArray.put(jsonObject);
                    Log.e("JSONArray", "jsonArray 22: " + jsonArray.toString());
                    SharedPreferences.Editor editor = pref.edit();
                    editor.putString("notification", ""+jsonArray);
                    editor.commit();
                }
            } catch (Exception e) {
                e.printStackTrace();
                Log.e("Exception", "Exception: " + e.getMessage());
            }
        }
    }
}
