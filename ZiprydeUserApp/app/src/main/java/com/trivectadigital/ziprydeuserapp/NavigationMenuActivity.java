package com.trivectadigital.ziprydeuserapp;

import android.app.Dialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.gson.Gson;
import com.trivectadigital.ziprydeuserapp.apis.ZiprydeApiClient;
import com.trivectadigital.ziprydeuserapp.apis.ZiprydeApiInterface;
import com.trivectadigital.ziprydeuserapp.assist.Utils;
import com.trivectadigital.ziprydeuserapp.modelget.SingleInstantResponse;

public class NavigationMenuActivity extends AppCompatActivity
                                                implements View.OnClickListener {

    TextView titleText;
    Toolbar toolbar;
    ImageView menuImgBlack;
    ZiprydeApiInterface apiService;
    LinearLayout pgsetupLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_navigationmenu);
        apiService = ZiprydeApiClient.getClient().create(ZiprydeApiInterface.class);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

//        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
//        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
//                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
//        drawer.setDrawerListener(toggle);
//        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);

        LayoutInflater mInflater = LayoutInflater.from(this);
        View mCustomView = mInflater.inflate(R.layout.actionbar_navtitle, null);
        toolbar.setContentInsetsAbsolute(0, 0);
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.MATCH_PARENT);
        toolbar.addView(mCustomView, layoutParams);
        titleText = (TextView) mCustomView.findViewById(R.id.titleText);
        titleText.setText("ZIPRYDE");
        ImageView menuImg = (ImageView) mCustomView.findViewById(R.id.menuImg);
        menuImg.setOnClickListener(this);

        View headerview = navigationView.getHeaderView(0);
        LinearLayout homeLayout = (LinearLayout) headerview.findViewById(R.id.homeLayout);
        homeLayout.setOnClickListener(this);
        LinearLayout notificationLayout = (LinearLayout) headerview.findViewById(R.id.notificationLayout);
        notificationLayout.setOnClickListener(this);
        LinearLayout historyLayout = (LinearLayout) headerview.findViewById(R.id.historyLayout);
        historyLayout.setOnClickListener(this);
        LinearLayout aboutLayout = (LinearLayout) headerview.findViewById(R.id.aboutLayout);
        aboutLayout.setOnClickListener(this);
        LinearLayout logoutLayout = (LinearLayout) headerview.findViewById(R.id.logoutLayout);
        logoutLayout.setOnClickListener(this);
        LinearLayout helpLayout = (LinearLayout) headerview.findViewById(R.id.helpLayout);
        helpLayout.setOnClickListener(this);

        LinearLayout psLayout = (LinearLayout) headerview.findViewById(R.id.paymentSetupLayout);
        psLayout.setOnClickListener(this);

        pgsetupLayout = (LinearLayout) headerview.findViewById(R.id.pgsetupLayout);
        //pgsetupLayout.setOnClickListener(this);


        LinearLayout paypalLayout = (LinearLayout) headerview.findViewById(R.id.paypalLayout);
        paypalLayout.setOnClickListener(this);
        LinearLayout cashappLayout = (LinearLayout) headerview.findViewById(R.id.cashAppLayout);
        cashappLayout.setOnClickListener(this);

        TextView editProfile = (TextView) headerview.findViewById(R.id.editProfile);
        editProfile.setOnClickListener(this);
        TextView buildNumber = (TextView) headerview.findViewById(R.id.buildNumber);
        try {
            PackageInfo pInfo = this.getPackageManager().getPackageInfo(getPackageName(), 0);
            String version = pInfo.versionName;
            buildNumber.setText(version);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }

        TextView nameProfile = (TextView) headerview.findViewById(R.id.nameProfile);
        Log.e("LastName", "" + Utils.verifyLogInUserMobileInstantResponse.getLastName());
        nameProfile.setText(Utils.verifyLogInUserMobileInstantResponse.getFirstName()+" "+Utils.verifyLogInUserMobileInstantResponse.getLastName());

        menuImgBlack = (ImageView) findViewById(R.id.menuImgBlack);
        menuImgBlack.setVisibility(View.GONE);

        navigationView.setCheckedItem(R.id.nav_home);

        Intent intent = getIntent();
        Log.e("intent ","intent : "+intent.getExtras());
        if(intent.hasExtra("body")){
            titleText.setText("ZipRyde Requests");
            showHideNavigationMenu();
            showHistoryFragment();
        }else{
            showBookingFragment();
        }

        menuImgBlack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showHideNavigationMenu();
            }
        });

        SharedPreferences prefs = getSharedPreferences("DisclaimerCredentials", MODE_PRIVATE);
        String disclaimer = prefs.getString("disclaimer", "");
        if(disclaimer.equals("")) {
            final Dialog dialog = new Dialog(this, android.R.style.Theme_Dialog);
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            dialog.setContentView(R.layout.dialog_userdisclaimer_scrollbar);
            dialog.setCancelable(false);

            Button acceptBtn = (Button) dialog.findViewById(R.id.acceptBtn);
            acceptBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    SharedPreferences.Editor editor = getSharedPreferences("DisclaimerCredentials", MODE_PRIVATE).edit();
                    editor.putString("disclaimer", "accept");
                    editor.commit();
                    dialog.dismiss();
                }
            });

            dialog.setCanceledOnTouchOutside(false);
            dialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
            dialog.getWindow().setLayout(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
            dialog.show();
        }


    }

    public void showBookingFragment() {
        menuImgBlack.setVisibility(View.VISIBLE);
        toolbar.setVisibility(View.GONE);
        // Creating a fragment object
//        BookingFragment sFragment = new BookingFragment();
        HomeBookingFragment sFragment = new HomeBookingFragment();
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
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
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

    public void showHelpFragment() {
        // Creating a fragment object
        HelpFragment sFragment = new HelpFragment();
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
        toolbar.setVisibility(View.VISIBLE);
        menuImgBlack.setVisibility(View.GONE);
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
            case R.id.homeLayout:
                titleText.setText("ZIPRYDE");
                showHideNavigationMenu();
                showBookingFragment();
                break;
            case R.id.notificationLayout:
                titleText.setText("Notifications");
                showHideNavigationMenu();
                break;
            case R.id.historyLayout:
                titleText.setText("ZipRyde Requests");
                showHideNavigationMenu();
                showHistoryFragment();
                break;
            case R.id.aboutLayout:
                //titleText.setText("About");
                showHideNavigationMenu();
                break;
            case R.id.helpLayout:
                titleText.setText("Help");
                showHideNavigationMenu();
                showHelpFragment();
                break;
            case R.id.logoutLayout:
                //showHideNavigationMenu();
                showInfoDlg("Information", "Are you sure you want to Log Out?", "YES", "logout");
                break;
            case R.id.editProfile:
                Intent ide = new Intent(NavigationMenuActivity.this, EditProfileActivity.class);
                ide.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(ide);
                break;
            case R.id.paymentSetupLayout: {

                if(pgsetupLayout.getVisibility() == View.VISIBLE) {
                    pgsetupLayout.setVisibility(View.GONE);
                }else{

                    pgsetupLayout.setVisibility(View.VISIBLE);
                }
            }
                break;
            case R.id.paypalLayout: {
                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://www.paypal.com/us/home"));
                startActivity(browserIntent);
            }
                break;
            case R.id.cashAppLayout: {
                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://cash.me/app/WTXRWNB"));
                startActivity(browserIntent);
            }

                break;
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        SharedPreferences prefs = getSharedPreferences("LoginCredentials", MODE_PRIVATE);
        Gson gson = new Gson();
        String json = prefs.getString("LoginCredentials", "");
        Utils.verifyLogInUserMobileInstantResponse = gson.fromJson(json, SingleInstantResponse.class);
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
                    SharedPreferences.Editor deditor = getSharedPreferences("DisclaimerCredentials", MODE_PRIVATE).edit();
                    editor.putString("disclaimer", "");
                    deditor.commit();
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
