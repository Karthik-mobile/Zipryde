package com.trivectadigital.ziprydedriverapp;

import android.app.Dialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Switch;
import android.widget.TextView;

import com.trivectadigital.ziprydedriverapp.apis.ZiprydeApiClient;
import com.trivectadigital.ziprydedriverapp.apis.ZiprydeApiInterface;
import com.trivectadigital.ziprydedriverapp.assist.Utils;
import com.trivectadigital.ziprydedriverapp.modelpost.SingleInstantParameters;

import org.json.JSONObject;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class NavigationMenuActivity extends AppCompatActivity implements View.OnClickListener {

    TextView titleText, statusText;
    LinearLayout switchLay;
    Switch switchStatus;
    ZiprydeApiInterface apiService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_navigation_menu);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        apiService = ZiprydeApiClient.getClient().create(ZiprydeApiInterface.class);

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        //navigationView.setNavigationItemSelectedListener(this);

        LayoutInflater mInflater = LayoutInflater.from(this);
        View mCustomView = mInflater.inflate(R.layout.actionbar_navtitle, null);
        toolbar.setContentInsetsAbsolute(0, 0);
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.MATCH_PARENT);
        toolbar.addView(mCustomView, layoutParams);
        titleText = (TextView) mCustomView.findViewById(R.id.titleText);
        titleText.setText("ZIPRYDE");
        ImageView menuImg = (ImageView) mCustomView.findViewById(R.id.menuImg);
        switchStatus = (Switch) mCustomView.findViewById(R.id.switchStatus);
        statusText = (TextView) mCustomView.findViewById(R.id.statusText);
        switchLay = (LinearLayout) mCustomView.findViewById(R.id.switchLay);
        menuImg.setOnClickListener(this);

        View headerview = navigationView.getHeaderView(0);
        LinearLayout notificationLayout = (LinearLayout) headerview.findViewById(R.id.notificationLayout);
        notificationLayout.setOnClickListener(this);
        LinearLayout historyLayout = (LinearLayout) headerview.findViewById(R.id.historyLayout);
        historyLayout.setOnClickListener(this);
        LinearLayout aboutLayout = (LinearLayout) headerview.findViewById(R.id.aboutLayout);
        aboutLayout.setOnClickListener(this);
        LinearLayout logoutLayout = (LinearLayout) headerview.findViewById(R.id.logoutLayout);
        logoutLayout.setOnClickListener(this);

        TextView nameProfile = (TextView) headerview.findViewById(R.id.nameProfile);
        nameProfile.setText(Utils.verifyLogInUserMobileInstantResponse.getFirstName()+" "+Utils.verifyLogInUserMobileInstantResponse.getLastName());

        showNotificationFragment();
        switchLay.setVisibility(View.VISIBLE);
        switchStatus.setChecked(true);
        updateDriverStatus(1);
        switchStatus.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked){
                    statusText.setText("ONLINE");
                    updateDriverStatus(1);
                }else{
                    statusText.setText("OFFLINE");
                    updateDriverStatus(0);
                }
            }
        });
    }

    public void updateDriverStatus(int active){
        Log.e("UserId","updateDriverStatus - "+Utils.verifyLogInUserMobileInstantResponse.getUserId());
        Log.e("active","updateDriverStatus - "+active);
        final Dialog dialog = new Dialog(NavigationMenuActivity.this, android.R.style.Theme_Dialog);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.loadingimage_layout);
        dialog.setCanceledOnTouchOutside(false);
        dialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        dialog.getWindow().setLayout(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        dialog.show();
        SingleInstantParameters loginCredentials = new SingleInstantParameters();
        loginCredentials.userId = ""+Utils.verifyLogInUserMobileInstantResponse.getUserId();
        loginCredentials.isOnline = active;

        Call<Void> call = apiService.updateDriverStatus(loginCredentials);
        call.enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                int statusCode = response.code();
                Log.e("statusCode", "" + statusCode);
                Log.e("response.body", "" + response.body());
                Log.e("response.errorBody", "" + response.errorBody());
                Log.e("response.isSuccessful", "" + response.isSuccessful());
                dialog.dismiss();
                if (!response.isSuccessful()) {
                    try {
                        JSONObject jObjError = new JSONObject(response.errorBody().string());
                        showInfoDlg("Error..", "" + jObjError.getString("message"), "OK", "error");
                    } catch (Exception e) {
                        showInfoDlg("Error..", "Either there is no network connectivity or server is not available.. Please try again later..", "OK", "server");
                    }
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                // Log error here since request failed
                Log.e("onFailure", t.toString());
                dialog.dismiss();
                showInfoDlg("Error..", "Either there is no network connectivity or server is not available.. Please try again later..", "OK", "server");
            }
        });
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            Utils.gpsLocationService.stopUsingGPS();
            super.onBackPressed();
        }
    }

    public void showHideNavigationMenu() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            drawer.openDrawer(GravityCompat.START);
        }
    }

    public void showNotificationFragment() {
        // Creating a fragment object
        NotificationsFragment sFragment = new NotificationsFragment();
        // Creating a Bundle object
        Bundle data = new Bundle();
        // Setting the index of the currently selected item of mDrawerList
//            data.putInt("position", position);
        // Setting the position to the fragment
        sFragment.setArguments(data);
        // Getting reference to the FragmentManager
        FragmentManager fragmentManager = getSupportFragmentManager();
        // Creating a fragment transaction
        FragmentTransaction ft = fragmentManager.beginTransaction();
        // Adding a fragment to the fragment transaction
        ft.replace(R.id.content_frame, sFragment);
        // Committing the transaction
        ft.commit();
    }

    public void showHistoryFragment() {
        // Creating a fragment object
        YourZiprydeFragment sFragment = new YourZiprydeFragment();
        // Creating a Bundle object
        Bundle data = new Bundle();
        // Setting the index of the currently selected item of mDrawerList
//            data.putInt("position", position);
        // Setting the position to the fragment
        sFragment.setArguments(data);
        // Getting reference to the FragmentManager
        FragmentManager fragmentManager = getSupportFragmentManager();
        // Creating a fragment transaction
        FragmentTransaction ft = fragmentManager.beginTransaction();
        // Adding a fragment to the fragment transaction
        ft.replace(R.id.content_frame, sFragment);
        // Committing the transaction
        ft.commit();
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        switch (id) {
            case R.id.menuImg:
                DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
                if (drawer.isDrawerOpen(GravityCompat.START)) {
                    drawer.closeDrawer(GravityCompat.START);
                } else {
                    drawer.openDrawer(GravityCompat.START);
                }
                break;
            case R.id.notificationLayout:
                titleText.setText("ZIPRYDE");
                showHideNavigationMenu();
                showNotificationFragment();
                switchLay.setVisibility(View.VISIBLE);
                break;
            case R.id.historyLayout:
                titleText.setText("Your Ziprydes");
                showHideNavigationMenu();
                switchLay.setVisibility(View.GONE);
                showHistoryFragment();
                break;
            case R.id.aboutLayout:
                //titleText.setText("About");
                showHideNavigationMenu();
                //switchLay.setVisibility(View.GONE);
                break;
            case R.id.logoutLayout:
                //showHideNavigationMenu();
                showInfoDlg("Information", "Are you sure do you want to Logout??", "YES", "logout");
                break;
        }
    }

    Dialog dialog;
    private void showInfoDlg(String title, String content, String btnText, final String navType) {
        dialog = new Dialog(this, android.R.style.Theme_Dialog);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.infodialog_layout);
        //dialog.setCanceledOnTouchOutside(true);

        Button positiveBtn = (Button) dialog.findViewById(R.id.positiveBtn);
        positiveBtn.setText(""+btnText);

        Button newnegativeBtn = (Button) dialog.findViewById(R.id.newnegativeBtn);

        TextView dialogtitleText = (TextView) dialog.findViewById(R.id.dialogtitleText);
        dialogtitleText.setText(""+title);
        TextView dialogcontentText = (TextView) dialog.findViewById(R.id.dialogcontentText);
        dialogcontentText.setText(""+content);

        positiveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                if(navType.equalsIgnoreCase("logout")){
                    SharedPreferences.Editor editor = getSharedPreferences("LoginCredentials", MODE_PRIVATE).edit();
                    editor.remove("phoneNumber");
                    editor.remove("password");
                    editor.commit();
                    Intent ide = new Intent(NavigationMenuActivity.this, LoginActivity.class);
                    ide.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(ide);
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
}
