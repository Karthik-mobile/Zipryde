package com.trivectadigital.ziprydeuserapp;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.IntentSender;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.LocationSettingsResult;
import com.google.android.gms.location.LocationSettingsStatusCodes;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.gson.Gson;
import com.trivectadigital.ziprydeuserapp.assist.NotificationUtils;
import com.trivectadigital.ziprydeuserapp.assist.Utils;
import com.trivectadigital.ziprydeuserapp.modelget.SingleInstantResponse;

public class SplashActivity extends AppCompatActivity implements ResultCallback<LocationSettingsResult>{

    private final Handler mHideHandler = new Handler();

    private final Runnable mHidePart2Runnable = new Runnable() {
        @SuppressLint("InlinedApi")
        @Override
        public void run() {
            // Delayed removal of status and navigation bar

            // Note that some of these constants are new as of API 16 (Jelly Bean)
            // and API 19 (KitKat). It is safe to use them, as they are inlined
            // at compile-time and do nothing on earlier devices.
            mContentView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LOW_PROFILE
                    | View.SYSTEM_UI_FLAG_FULLSCREEN
                    | View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                    | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                    | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                    | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION);
        }
    };

    private static final int UI_ANIMATION_DELAY = 300;
    private View mContentView;

    static final Integer LOCATION = 0x1;
    int REQUEST_CHECK_SETTINGS = 100;

    private BroadcastReceiver mRegistrationBroadcastReceiver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.hide();
        }

        mContentView = findViewById(R.id.fullscreen_content);
        delayedHide(100);

        int status = GooglePlayServicesUtil.isGooglePlayServicesAvailable(getBaseContext());
        // Showing status
        if(status != ConnectionResult.SUCCESS){
            int requestCode = 10;
            Dialog dialog = GooglePlayServicesUtil.getErrorDialog(status, this, requestCode);
            dialog.show();
        }else{
            if (Utils.connectivity(SplashActivity.this)) {
                mRegistrationBroadcastReceiver = new BroadcastReceiver() {
                    @Override
                    public void onReceive(Context context, Intent intent) {

                        // checking for type intent filter
                        if (intent.getAction().equals(Utils.REGISTRATION_COMPLETE)) {
                            // gcm successfully registered
                            // now subscribe to `global` topic to receive app wide notifications
                            FirebaseMessaging.getInstance().subscribeToTopic(Utils.TOPIC_GLOBAL);

                            displayFirebaseRegId();

                        }
                    }
                };

                displayFirebaseRegId();

                if (ContextCompat.checkSelfPermission(SplashActivity.this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    // Should we show an explanation?
                    if (ActivityCompat.shouldShowRequestPermissionRationale(SplashActivity.this, android.Manifest.permission.ACCESS_FINE_LOCATION)) {
                        //This is called if user has denied the permission before
                        //In this case I am just asking the permission again
                        ActivityCompat.requestPermissions(SplashActivity.this, new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION}, LOCATION);

                    } else {
                        ActivityCompat.requestPermissions(SplashActivity.this, new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION}, LOCATION);
                    }
                } else {
                    LocationManager manager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
                    boolean statusOfGPS = manager.isProviderEnabled(LocationManager.GPS_PROVIDER);
                    if (statusOfGPS) {
                        gotoNextActivity();
                    } else {
                        showInfoDlg(getString(R.string.information), getString(R.string.usermsg_locationturnon), getString(R.string.btn_open), "gps");
                    }
                }
            } else {
                showInfoDlg(getString(R.string.information), getString(R.string.errmsg_network_noconnection), getString(R.string.btn_ok), "network");
            }
        }
    }

    public void gotoNextActivity(){
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                Utils.fromSplash = true;
                SharedPreferences prefs = getSharedPreferences("URLCredentials", MODE_PRIVATE);
                String url = prefs.getString("url", null);
                if(url != null){
                    Utils.defaultIP = url;
                }
                prefs = getSharedPreferences("LoginCredentials", MODE_PRIVATE);
                String phoneno = prefs.getString("phoneNumber", null);
                if (phoneno != null) {
                    phoneno = prefs.getString("phoneNumber", "");//"No name defined" is the default value.
                    String password = prefs.getString("password", ""); //0 is the default value.
                    Gson gson = new Gson();
                    String json = prefs.getString("LoginCredentials", "");
                    Utils.verifyLogInUserMobileInstantResponse = gson.fromJson(json, SingleInstantResponse.class);
                    prefs = getSharedPreferences("BookingCredentials", MODE_PRIVATE);
                    String bookingIdFinal = prefs.getString("bookingId", "");
                    Intent ide;
                    if(bookingIdFinal.equals("")){
                        ide = new Intent(SplashActivity.this, NavigationMenuActivity.class);
                    }else{
                        ide = new Intent(SplashActivity.this, DriverInfoBookingActivity.class);
                        ide.putExtra("bookingId",""+bookingIdFinal);
                    }
                    ide.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(ide);
                    finish();
                }else{
                    Intent ide = new Intent(SplashActivity.this, LoginActivity.class);
                    ide.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(ide);
                    finish();
                }
            }
        }, 3000);
    }

    /**
     * Schedules a call to hide() in [delay] milliseconds, canceling any
     * previously scheduled calls.
     */
    private void delayedHide(int delayMillis) {
        // Schedule a runnable to remove the status and navigation bar after a delay
        mHideHandler.postDelayed(mHidePart2Runnable, UI_ANIMATION_DELAY);
    }

    Dialog dialog;
    private void showInfoDlg(String title, String content, String btnText, final String navType) {
        dialog = new Dialog(SplashActivity.this, android.R.style.Theme_Dialog);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.infodialog_layout);
        //dialog.setCanceledOnTouchOutside(true);

        Button positiveBtn = (Button) dialog.findViewById(R.id.positiveBtn);
        positiveBtn.setText(""+btnText);

        Button newnegativeBtn = (Button) dialog.findViewById(R.id.newnegativeBtn);
        if(navType.equalsIgnoreCase("gps") || navType.equalsIgnoreCase("warning") || navType.equalsIgnoreCase("network")){
            newnegativeBtn.setVisibility(View.GONE);
        }else{
            newnegativeBtn.setVisibility(View.VISIBLE);
        }

        TextView dialogtitleText = (TextView) dialog.findViewById(R.id.dialogtitleText);
        dialogtitleText.setText(""+title);
        TextView dialogcontentText = (TextView) dialog.findViewById(R.id.dialogcontentText);
        dialogcontentText.setText(""+content);

        positiveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                if(navType.equalsIgnoreCase("gps")){
                    startActivityForResult(new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS), REQUEST_CHECK_SETTINGS);
                }else if(navType.equalsIgnoreCase("network")) {
                    finish();
                }
            }
        });

        newnegativeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        dialog.setCanceledOnTouchOutside(false);
        dialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        dialog.getWindow().setLayout(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        dialog.show();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if(ActivityCompat.checkSelfPermission(SplashActivity.this, permissions[0]) == PackageManager.PERMISSION_GRANTED){
            switch (requestCode) {
                //Location
                case 1:
                    LocationManager manager = (LocationManager) getSystemService(Context.LOCATION_SERVICE );
                    boolean statusOfGPS = manager.isProviderEnabled(LocationManager.GPS_PROVIDER);
                    if(statusOfGPS){
                        gotoNextActivity();
                    }else{
                        //askSwitchOnGPS();
                        showInfoDlg(getString(R.string.information), getString(R.string.usermsg_locationturnon), getString(R.string.btn_open), "gps");
                    }
                    break;
            }
        }
    }

    @Override
    public void onResult(@NonNull LocationSettingsResult locationSettingsResult) {
        final Status status = locationSettingsResult.getStatus();
        switch (status.getStatusCode()) {
            case LocationSettingsStatusCodes.SUCCESS:
                // NO need to show the dialog;
                break;

            case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:
                //  Location settings are not satisfied. Show the user a dialog
                try {
                    // Show the dialog by calling startResolutionForResult(), and check the result
                    // in onActivityResult().
                    status.startResolutionForResult(SplashActivity.this, REQUEST_CHECK_SETTINGS);
                } catch (IntentSender.SendIntentException e) {
                    //failed to show
                }
                break;

            case LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE:
                // Location settings are unavailable so not possible to show any dialog now
                break;
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CHECK_SETTINGS) {
            LocationManager manager = (LocationManager) getSystemService(Context.LOCATION_SERVICE );
            boolean statusOfGPS = manager.isProviderEnabled(LocationManager.GPS_PROVIDER);
            if(statusOfGPS){
                gotoNextActivity();
            }else{
                showInfoDlg(getString(R.string.information), getString(R.string.usermsg_locationturnon), getString(R.string.btn_open), "gps");
            }
        }
    }

    // Fetches reg id from shared preferences
    // and displays on the screen
    private void displayFirebaseRegId() {
        SharedPreferences pref = getApplicationContext().getSharedPreferences(Utils.SHARED_PREF, 0);
        String regId = pref.getString("regId", null);

        Log.e("FirebaseRegId", "Firebase reg id: " + regId);
    }

    @Override
    protected void onResume() {
        super.onResume();

        // register GCM registration complete receiver
        LocalBroadcastManager.getInstance(this).registerReceiver(mRegistrationBroadcastReceiver,
                new IntentFilter(Utils.REGISTRATION_COMPLETE));

        // register new push message receiver
        // by doing this, the activity will be notified each time a new message arrives
        LocalBroadcastManager.getInstance(this).registerReceiver(mRegistrationBroadcastReceiver,
                new IntentFilter(Utils.PUSH_NOTIFICATION));

        // clear the notification area when the app is opened
        NotificationUtils.clearNotifications(getApplicationContext());
    }

    @Override
    protected void onPause() {
        LocalBroadcastManager.getInstance(this).unregisterReceiver(mRegistrationBroadcastReceiver);
        super.onPause();
    }
}
