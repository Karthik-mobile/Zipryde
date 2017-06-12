package com.altrockstech.ziprydeuserapp;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

public class NavigationMenuActivity extends AppCompatActivity
        implements View.OnClickListener {

    TextView titleText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_navigationmenu);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
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
        TextView editProfile = (TextView) headerview.findViewById(R.id.editProfile);
        editProfile.setOnClickListener(this);

        navigationView.setCheckedItem(R.id.nav_home);
        showBookingFragment();
    }

    public void showBookingFragment() {
        // Creating a fragment object
        BookingFragment sFragment = new BookingFragment();
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
            case R.id.homeLayout:
                titleText.setText("ZIPRYDE");
                showHideNavigationMenu();
                showBookingFragment();
                break;
            case R.id.notificationLayout:
                titleText.setText("Notifications");
                showHideNavigationMenu();
                showNotificationFragment();
                break;
            case R.id.historyLayout:
                titleText.setText("Your Zipryde");
                showHideNavigationMenu();
                showHistoryFragment();
                break;
            case R.id.aboutLayout:
                //titleText.setText("About");
                showHideNavigationMenu();
                break;
            case R.id.logoutLayout:
                //showHideNavigationMenu();
                showInfoDlg("Info..!", "Are you sure do you want to Logout??", "YES", "logout");
                break;
            case R.id.editProfile:
                Intent ide = new Intent(NavigationMenuActivity.this, EditProfileActivity.class);
                ide.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(ide);
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

        ImageView negativeBtn = (ImageView) dialog.findViewById(R.id.negativeBtn);

        TextView dialogtitleText = (TextView) dialog.findViewById(R.id.dialogtitleText);
        dialogtitleText.setText(""+title);
        TextView dialogcontentText = (TextView) dialog.findViewById(R.id.dialogcontentText);
        dialogcontentText.setText(""+content);

        positiveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                if(navType.equalsIgnoreCase("logout")){
                    Intent ide = new Intent(NavigationMenuActivity.this, LoginActivity.class);
                    ide.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(ide);
                    finish();
                }
            }
        });

        negativeBtn.setOnClickListener(new View.OnClickListener() {
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
