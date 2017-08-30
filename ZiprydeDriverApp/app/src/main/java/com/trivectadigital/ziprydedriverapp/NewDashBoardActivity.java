package com.trivectadigital.ziprydedriverapp;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.Point;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Display;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.trivectadigital.ziprydedriverapp.apis.ZiprydeApiClient;
import com.trivectadigital.ziprydedriverapp.apis.ZiprydeApiInterface;
import com.trivectadigital.ziprydedriverapp.assist.MessageReceivedEvent;
import com.trivectadigital.ziprydedriverapp.assist.ObservableObject;
import com.trivectadigital.ziprydedriverapp.assist.Utils;
import com.trivectadigital.ziprydedriverapp.modelget.SingleInstantResponse;
import com.trivectadigital.ziprydedriverapp.modelpost.SingleInstantParameters;

import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Observable;
import java.util.Observer;

import br.com.safety.locationlistenerhelper.core.LocationTracker;
import de.greenrobot.event.EventBus;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class NewDashBoardActivity extends AppCompatActivity implements Observer {

    LinearLayout rideLayout, historyLayout, notificationLayout, logoutLayout, helpLayout,paymentSetupLayout, rideLaterLayout;

    RelativeLayout onofflineLay;

    boolean isOnline = true;

    View viewOffline, viewOnline;

    TextView driverInfoText, revenueText, rideCountText, dateText, buildNumber;

    ZiprydeApiInterface apiService;

    PopupWindow paymentPopwindow;

    private LocationTracker locationTracker;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_dash_board);

        SharedPreferences prefs = getSharedPreferences("LoginCredentials", MODE_PRIVATE);
        Gson gson = new Gson();
        String json = prefs.getString("LoginCredentials", "");
        Utils.verifyLogInUserMobileInstantResponse = gson.fromJson(json, SingleInstantResponse.class);

        apiService = ZiprydeApiClient.getClient().create(ZiprydeApiInterface.class);

        onofflineLay = (RelativeLayout) findViewById(R.id.onofflineLay);
        viewOffline = findViewById(R.id.viewOffline);
        viewOnline = findViewById(R.id.viewOnline);

        driverInfoText = (TextView) findViewById(R.id.driverInfoText);
        revenueText = (TextView) findViewById(R.id.revenueText);
        rideCountText = (TextView) findViewById(R.id.rideCountText);
        dateText = (TextView) findViewById(R.id.dateText);
        buildNumber = (TextView) findViewById(R.id.buildNumber);

        try {
            PackageInfo pInfo = this.getPackageManager().getPackageInfo(getPackageName(), 0);
            String version = pInfo.versionName;
            buildNumber.setText(version);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }

        rideLayout = (LinearLayout) findViewById(R.id.rideLayout);
        rideLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent ide = new Intent(NewDashBoardActivity.this, RideActivity.class);
                ide.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(ide);
            }
        });

        historyLayout = (LinearLayout) findViewById(R.id.historyLayout);
        historyLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Utils.isHistory = true;
                Intent ide = new Intent(NewDashBoardActivity.this, HistoryActivity.class);
                ide.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(ide);
            }
        });

        notificationLayout = (LinearLayout) findViewById(R.id.notificationLayout);
        notificationLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent ide = new Intent(NewDashBoardActivity.this, NotificationActivity.class);
                ide.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(ide);
            }
        });

        logoutLayout = (LinearLayout) findViewById(R.id.logoutLayout);
        logoutLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showInfoDlg("Information", "Are you sure you want to Log Out?", "YES", "logout");
            }
        });

        helpLayout = (LinearLayout) findViewById(R.id.helpLayout);
        helpLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent ide = new Intent(NewDashBoardActivity.this, HelpActivity.class);
                ide.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(ide);
            }
        });

        rideLaterLayout = (LinearLayout) findViewById(R.id.rideLaterLayout);
        rideLaterLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               // Toast.makeText(NewDashBoardActivity.this,"Reached here",Toast.LENGTH_SHORT).show();
                Utils.isHistory = false;
                Intent ide = new Intent(NewDashBoardActivity.this, HistoryActivity.class);
                //ide.putExtra("schedule",1);
                ide.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(ide);
            }
        });

        paymentSetupLayout = (LinearLayout) findViewById(R.id.paymentLayout);
        paymentSetupLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onShowPopup(v);
            }
        });



        TextView nameProfile = (TextView) findViewById(R.id.driverText);
        nameProfile.setText(Utils.verifyLogInUserMobileInstantResponse.getFirstName() + " " + Utils.verifyLogInUserMobileInstantResponse.getLastName());

        prefs = getSharedPreferences("DisclaimerCredentials", MODE_PRIVATE);
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

                    updateDriverStatus(1);
                    isOnline = true;

                }
            });

            dialog.setCanceledOnTouchOutside(false);
            dialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
            dialog.getWindow().setLayout(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
            dialog.show();
        }else{

            updateDriverStatus(1);
            isOnline = true;
//            onofflineLay.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//                    if (isOnline) {
//                        updateDriverStatus(1);
//                    } else {
//                        updateDriverStatus(0);
//                    }
//                }
//            });
        }

        onofflineLay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isOnline) {
                    updateDriverStatus(1);
                } else {
                    updateDriverStatus(0);
                }
            }
        });

        ObservableObject.getInstance().addObserver(this);


    }

    @Override
    public void update(Observable observable, Object data) {
        //Toast.makeText(this, String.valueOf("activity observer " + data), Toast.LENGTH_SHORT).show();
        //insertDriverSession();
        //Open RideActivity
        Intent ide = new Intent(NewDashBoardActivity.this, RideActivity.class);
        ide.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(ide);

    }



    @Override
    protected void onResume() {
        super.onResume();
        SharedPreferences prefs = getSharedPreferences("LoginCredentials", MODE_PRIVATE);
        Gson gson = new Gson();
        String json = prefs.getString("LoginCredentials", "");
        Utils.verifyLogInUserMobileInstantResponse = gson.fromJson(json, SingleInstantResponse.class);
//        updateDriverStatus(1);
//        isOnline = true;
        getRevenueByDateAndDriverId();
    }

    @Override
    public void onStart() {
        super.onStart();
        EventBus.getDefault().register(this);

//            locationTracker=new LocationTracker("my.action")
//                    .setInterval(20000)
//                    .setGps(true)
//                    .setNetWork(false)
//                    .start(getBaseContext(), this);
    }

    @Override
    protected void onStop() {
        EventBus.getDefault().unregister(this);
        super.onStop();
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        locationTracker.onRequestPermission(requestCode, permissions, grantResults);
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
       // locationTracker.stopLocationService(this);

    }


    public void onEventMainThread(MessageReceivedEvent messageReceivedEvent) {
        Log.e("onEventMainThread", "" + messageReceivedEvent.message);
        Log.e("PUSH_NOTIFICATION", "PUSH_NOTIFICATION");
        Intent ide = new Intent(NewDashBoardActivity.this, RideActivity.class);
        ide.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(ide);
    }

    public void getRevenueByDateAndDriverId() {
        if (Utils.connectivity(NewDashBoardActivity.this)) {
            final Dialog dialog = new Dialog(NewDashBoardActivity.this, android.R.style.Theme_Dialog);
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            dialog.setContentView(R.layout.loadingimage_layout);
            dialog.setCanceledOnTouchOutside(false);
            dialog.setCancelable(false);
            dialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
            dialog.getWindow().setLayout(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
            dialog.show();

            Calendar c = Calendar.getInstance();
            System.out.println("Current time => " + c.getTime());
            SimpleDateFormat df = new SimpleDateFormat("MM-dd-yyyy");
            String formattedDate = df.format(c.getTime());
            final SingleInstantParameters loginCredentials = new SingleInstantParameters();
            loginCredentials.driverId = "" + Utils.verifyLogInUserMobileInstantResponse.getUserId();
            loginCredentials.paidDateTime = formattedDate;
            dateText.setText(formattedDate);

            Call<SingleInstantResponse> call = apiService.getRevenueByDateAndDriverId(Utils.verifyLogInUserMobileInstantResponse.getAccessToken(),loginCredentials);
            call.enqueue(new Callback<SingleInstantResponse>() {
                @Override
                public void onResponse(Call<SingleInstantResponse> call, Response<SingleInstantResponse> response) {
                    int statusCode = response.code();
                    Log.e("statusCode", "" + statusCode);
                    Log.e("response.body", "" + response.body());
                    Log.e("response.errorBody", "" + response.errorBody());
                    Log.e("response.isSuccessful", "" + response.isSuccessful());
                    dialog.dismiss();
                    if (response.isSuccessful()) {
                        Utils.getRevenueByDateInstantResponse = response.body();
                        String revenueAmount = Utils.getRevenueByDateInstantResponse.getRevenueAmount();
                        revenueText.setText("$ " + revenueAmount);
                        getBookingCountByDateAndDriverId(loginCredentials);
                    } else {
                        try {
                            JSONObject jObjError = new JSONObject(response.errorBody().string());
                            if(response.code() == Utils.NETWORKERR_SESSIONTOKEN_EXPIRED){


                                // JSONObject jObjError = new JSONObject(response.errorBody().string());
                                // Toast.makeText(LoginActivity.this, jObjError.toString(), Toast.LENGTH_LONG).show();
                                //if(jObjError.getString("message"))
                                showInfoDlg(getString(R.string.error), "" + jObjError.getString("message"), getString(R.string.btn_ok), "forcelogout");

                            }else {
                                showInfoDlg("Error..", "" + jObjError.getString("message"), "OK", "error");
                            }
                        } catch (Exception e) {
                            Toast.makeText(NewDashBoardActivity.this, "Either there is no network connectivity or server is not available.. Please try again later..", Toast.LENGTH_LONG).show();
                        }
                    }
                }

                @Override
                public void onFailure(Call<SingleInstantResponse> call, Throwable t) {
                    // Log error here since request failed
                    Log.e("onFailure", t.toString());
                    dialog.dismiss();
                    showInfoDlg(getString(R.string.error), "" + getString(R.string.errmsg_sessionexpired), getString(R.string.btn_ok), "forcelogout");
                   // Toast.makeText(NewDashBoardActivity.this, "Either there is no network connectivity or server is not available.. Please try again later..", Toast.LENGTH_LONG).show();
                }
            });
        } else {
            Toast.makeText(NewDashBoardActivity.this, "Either there is no network connectivity or server is not available.. Please try again later..", Toast.LENGTH_LONG).show();
        }
    }

    public void getBookingCountByDateAndDriverId(SingleInstantParameters singleInstantParameters) {
        if (Utils.connectivity(NewDashBoardActivity.this)) {
            final Dialog dialog = new Dialog(NewDashBoardActivity.this, android.R.style.Theme_Dialog);
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            dialog.setContentView(R.layout.loadingimage_layout);
            dialog.setCanceledOnTouchOutside(false);
            dialog.setCancelable(false);
            dialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
            dialog.getWindow().setLayout(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
            dialog.show();

            Call<SingleInstantResponse> call = apiService.getBookingCountByDateAndDriverId(Utils.verifyLogInUserMobileInstantResponse.getAccessToken(),singleInstantParameters);
            call.enqueue(new Callback<SingleInstantResponse>() {
                @Override
                public void onResponse(Call<SingleInstantResponse> call, Response<SingleInstantResponse> response) {
                    int statusCode = response.code();
                    Log.e("statusCode", "" + statusCode);
                    Log.e("response.body", "" + response.body());
                    Log.e("response.errorBody", "" + response.errorBody());
                    Log.e("response.isSuccessful", "" + response.isSuccessful());
                    dialog.dismiss();
                    if (response.isSuccessful()) {
                        Utils.getBookingCountByDateInstantResponse = response.body();
                        String count = Utils.getBookingCountByDateInstantResponse.getCount();
                        rideCountText.setText(count);
                    } else {
                        try {
                            JSONObject jObjError = new JSONObject(response.errorBody().string());
                            if(response.code() == Utils.NETWORKERR_SESSIONTOKEN_EXPIRED){


                                // JSONObject jObjError = new JSONObject(response.errorBody().string());
                                // Toast.makeText(LoginActivity.this, jObjError.toString(), Toast.LENGTH_LONG).show();
                                //if(jObjError.getString("message"))
                                showInfoDlg(getString(R.string.error), "" + jObjError.getString("message"), getString(R.string.btn_ok), "forcelogout");

                            }else {
                                showInfoDlg("Error..", "" + jObjError.getString("message"), "OK", "error");
                            }
                        } catch (Exception e) {
                            Toast.makeText(NewDashBoardActivity.this, "Either there is no network connectivity or server is not available.. Please try again later..", Toast.LENGTH_LONG).show();
                        }
                    }
                }

                @Override
                public void onFailure(Call<SingleInstantResponse> call, Throwable t) {
                    // Log error here since request failed
                    Log.e("onFailure", t.toString());
                    dialog.dismiss();
                    showInfoDlg(getString(R.string.error), "" + getString(R.string.errmsg_sessionexpired), getString(R.string.btn_ok), "forcelogout");
                   // Toast.makeText(NewDashBoardActivity.this, "Either there is no network connectivity or server is not available.. Please try again later..", Toast.LENGTH_LONG).show();
                }
            });
        } else {
            Toast.makeText(NewDashBoardActivity.this, "Either there is no network connectivity or server is not available.. Please try again later..", Toast.LENGTH_LONG).show();
        }
    }

    public void updateDriverStatus(final int active) {
        if (Utils.connectivity(NewDashBoardActivity.this)) {
            SharedPreferences prefs = getSharedPreferences("LoginCredentials", MODE_PRIVATE);
            Gson gson = new Gson();
            String json = prefs.getString("LoginCredentials", "");
            Utils.verifyLogInUserMobileInstantResponse = gson.fromJson(json, SingleInstantResponse.class);
            Log.e("UserId", "updateDriverStatus - " + Utils.verifyLogInUserMobileInstantResponse.getUserId());
            Log.e("active", "updateDriverStatus - " + active);
            final Dialog dialog = new Dialog(NewDashBoardActivity.this, android.R.style.Theme_Dialog);
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            dialog.setContentView(R.layout.loadingimage_layout);
            dialog.setCanceledOnTouchOutside(false);
            dialog.setCancelable(false);
            dialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
            dialog.getWindow().setLayout(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
            dialog.show();
            SingleInstantParameters loginCredentials = new SingleInstantParameters();
            loginCredentials.userId = "" + Utils.verifyLogInUserMobileInstantResponse.getUserId();
            loginCredentials.isOnline = active;

            Call<Void> call = apiService.updateDriverStatus(Utils.verifyLogInUserMobileInstantResponse.getAccessToken(),loginCredentials);
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
                            if(response.code() == Utils.NETWORKERR_SESSIONTOKEN_EXPIRED){


                                // JSONObject jObjError = new JSONObject(response.errorBody().string());
                                // Toast.makeText(LoginActivity.this, jObjError.toString(), Toast.LENGTH_LONG).show();
                                //if(jObjError.getString("message"))
                                showInfoDlg(getString(R.string.error), "" + jObjError.getString("message"), getString(R.string.btn_ok), "forcelogout");

                            }else {
                                showInfoDlg("Error..", "" + jObjError.getString("message"), "OK", "error");
                            }
                        } catch (Exception e) {
                            Toast.makeText(NewDashBoardActivity.this, "Either there is no network connectivity or server is not available.. Please try again later..", Toast.LENGTH_LONG).show();
                        }
                    } else {
                        if (active == 1) {
                            insertDriverSession();
                            isOnline = false;
                            viewOffline.setVisibility(View.GONE);
                            viewOnline.setVisibility(View.VISIBLE);
                            driverInfoText.setText("Online");
                        } else {
                            isOnline = true;
                            viewOffline.setVisibility(View.VISIBLE);
                            viewOnline.setVisibility(View.GONE);
                            driverInfoText.setText("Offline");
                        }
                    }
                }

                @Override
                public void onFailure(Call<Void> call, Throwable t) {
                    // Log error here since request failed
                    Log.e("onFailure", t.toString());
                    dialog.dismiss();
                    showInfoDlg(getString(R.string.error), "" + getString(R.string.errmsg_sessionexpired), getString(R.string.btn_ok), "forcelogout");
                    //Toast.makeText(NewDashBoardActivity.this, "Either there is no network connectivity or server is not available.. Please try again later..", Toast.LENGTH_LONG).show();
                }
            });
        } else {
            Toast.makeText(NewDashBoardActivity.this, "Either there is no network connectivity or server is not available.. Please try again later..", Toast.LENGTH_LONG).show();
        }
    }

    public void insertDriverSession(){
        Log.e("UserId","insertDriverSession - "+Utils.verifyLogInUserMobileInstantResponse.getUserId());
        Log.e("Latitude","insertDriverSession - "+Utils.gpsLocationService.getLatitude());
        Log.e("Longitude","insertDriverSession - "+Utils.gpsLocationService.getLongitude());
        SingleInstantParameters loginCredentials = new SingleInstantParameters();
        loginCredentials.userId = ""+Utils.verifyLogInUserMobileInstantResponse.getUserId();
        loginCredentials.fromLatitude = ""+Utils.gpsLocationService.getLatitude();
        loginCredentials.fromLongitude = ""+Utils.gpsLocationService.getLongitude();
        ZiprydeApiInterface apiService = ZiprydeApiClient.getClient().create(ZiprydeApiInterface.class);
        Call<SingleInstantResponse> call = apiService.updateDriverSession(Utils.verifyLogInUserMobileInstantResponse.getAccessToken(),loginCredentials);
        call.enqueue(new Callback<SingleInstantResponse>() {
            @Override
            public void onResponse(Call<SingleInstantResponse> call, Response<SingleInstantResponse> response) {
                int statusCode = response.code();
                Log.e("statusCode", "" + statusCode);
                Log.e("response.body", "" + response.body());
                Log.e("response.errorBody", "" + response.errorBody());
                Log.e("response.isSuccessful", "" + response.isSuccessful());

                if (!response.isSuccessful()) {
                    try {
                        JSONObject jObjError = new JSONObject(response.errorBody().string());
                        if (response.code() == Utils.NETWORKERR_SESSIONTOKEN_EXPIRED) {


                            // JSONObject jObjError = new JSONObject(response.errorBody().string());
                            // Toast.makeText(LoginActivity.this, jObjError.toString(), Toast.LENGTH_LONG).show();
                            //if(jObjError.getString("message"))
                            showInfoDlg(getString(R.string.error), "" + jObjError.getString("message"), getString(R.string.btn_ok), "forcelogout");

                        } else {
                            showInfoDlg("Error..", "" + jObjError.getString("message"), "OK", "error");
                        }
                    } catch (Exception e) {
                        Toast.makeText(NewDashBoardActivity.this, "Either there is no network connectivity or server is not available.. Please try again later..", Toast.LENGTH_LONG).show();
                    }
                }
            }

            @Override
            public void onFailure(Call<SingleInstantResponse> call, Throwable t) {
                // Log error here since request failed
                Log.e("onFailure", t.toString());
                showInfoDlg(getString(R.string.error), "" + getString(R.string.errmsg_sessionexpired), getString(R.string.btn_ok), "forcelogout");
            }
        });
    }

    Dialog dialog;
    private void showInfoDlg(String title, String content, String btnText, final String navType) {
        dialog = new Dialog(this, android.R.style.Theme_Dialog);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.infodialog_layout);
        //dialog.setCanceledOnTouchOutside(true);

        Button positiveBtn = (Button) dialog.findViewById(R.id.positiveBtn);
        positiveBtn.setText("" + btnText);

        Button newnegativeBtn = (Button) dialog.findViewById(R.id.newnegativeBtn);
        if (navType.equalsIgnoreCase("server") || navType.equalsIgnoreCase("forcelogout") ) {
            newnegativeBtn.setVisibility(View.GONE);
        }

        TextView dialogtitleText = (TextView) dialog.findViewById(R.id.dialogtitleText);
        dialogtitleText.setText("" + title);
        TextView dialogcontentText = (TextView) dialog.findViewById(R.id.dialogcontentText);
        dialogcontentText.setText("" + content);

        positiveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                if (navType.equalsIgnoreCase("logout")) {
                    SharedPreferences.Editor deditor = getSharedPreferences("DisclaimerCredentials", MODE_PRIVATE).edit();
                    deditor.putString("disclaimer", "");
                    deditor.commit();
                    updateDriverStatus();
                }else if(navType.equalsIgnoreCase("forcelogout")){
                    SharedPreferences.Editor editor = getSharedPreferences("LoginCredentials", MODE_PRIVATE).edit();
                    editor.remove("phoneNumber");
                    editor.remove("password");
                    editor.commit();
                    SharedPreferences.Editor deditor = getSharedPreferences("DisclaimerCredentials", MODE_PRIVATE).edit();
                    deditor.putString("disclaimer", "");
                    deditor.commit();
                    Intent ide = new Intent(NewDashBoardActivity.this, LoginActivity.class);
                    ide.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(ide);
                    // finish();
                }
//                if(navType.equalsIgnoreCase("logout")){
//                    SharedPreferences.Editor editor = getSharedPreferences("LoginCredentials", MODE_PRIVATE).edit();
//                    editor.remove("phoneNumber");
//                    editor.remove("password");
//                    editor.commit();
//                    SharedPreferences.Editor deditor = getSharedPreferences("DisclaimerCredentials", MODE_PRIVATE).edit();
//                    deditor.putString("disclaimer", "");
//                    deditor.commit();
//                    Intent ide = new Intent(NewDashBoardActivity.this, LoginActivity.class);
//                    ide.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
//                    startActivity(ide);
//                    // finish();
//                }
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

    public void updateDriverStatus() {
        if (Utils.connectivity(NewDashBoardActivity.this)) {
            SharedPreferences prefs = getSharedPreferences("LoginCredentials", MODE_PRIVATE);
            Gson gson = new Gson();
            String json = prefs.getString("LoginCredentials", "");
            Utils.verifyLogInUserMobileInstantResponse = gson.fromJson(json, SingleInstantResponse.class);
            Log.e("UserId", "updateDriverStatus - " + Utils.verifyLogInUserMobileInstantResponse.getUserId());
            Log.e("active", "updateDriverStatus - " + 0);
            final Dialog dialog = new Dialog(NewDashBoardActivity.this, android.R.style.Theme_Dialog);
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            dialog.setContentView(R.layout.loadingimage_layout);
            dialog.setCanceledOnTouchOutside(false);
            dialog.setCancelable(false);
            dialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
            dialog.getWindow().setLayout(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
            dialog.show();
            SingleInstantParameters loginCredentials = new SingleInstantParameters();
            loginCredentials.userId = "" + Utils.verifyLogInUserMobileInstantResponse.getUserId();
            loginCredentials.isOnline = 0;

            Call<Void> call = apiService.updateDriverStatus(Utils.verifyLogInUserMobileInstantResponse.getAccessToken(),loginCredentials);
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
                            if(response.code() == Utils.NETWORKERR_SESSIONTOKEN_EXPIRED){


                                // JSONObject jObjError = new JSONObject(response.errorBody().string());
                                // Toast.makeText(LoginActivity.this, jObjError.toString(), Toast.LENGTH_LONG).show();
                                //if(jObjError.getString("message"))
                                showInfoDlg(getString(R.string.error), "" + jObjError.getString("message"), getString(R.string.btn_ok), "forcelogout");

                            }else {
                                showInfoDlg("Error..", "" + jObjError.getString("message"), "OK", "error");
                            }
                        } catch (Exception e) {
                            Toast.makeText(NewDashBoardActivity.this, "Either there is no network connectivity or server is not available.. Please try again later..", Toast.LENGTH_LONG).show();
                        }
                    } else {
//                        SharedPreferences.Editor editor = getSharedPreferences("LoginCredentials", MODE_PRIVATE).edit();
//                        editor.remove("phoneNumber");
//                        editor.remove("password");
//                        editor.commit();
//                        Intent ide = new Intent(NewDashBoardActivity.this, LoginActivity.class);
//                        ide.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
//                        startActivity(ide);
//                        finish();

                        //Call Logout service from here.
                        sendLogout();

                    }
                }

                @Override
                public void onFailure(Call<Void> call, Throwable t) {
                    // Log error here since request failed
                    Log.e("onFailure", t.toString());
                    dialog.dismiss();
                    showInfoDlg(getString(R.string.error), "" + getString(R.string.errmsg_sessionexpired), getString(R.string.btn_ok), "forcelogout");
                   // Toast.makeText(NewDashBoardActivity.this, "Either there is no network connectivity or server is not available.. Please try again later..", Toast.LENGTH_LONG).show();
                }
            });
        } else {
            Toast.makeText(NewDashBoardActivity.this, "Either there is no network connectivity or server is not available.. Please try again later..", Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public void onBackPressed() {
        Utils.gpsLocationService.stopUsingGPS();
        super.onBackPressed();
    }

    // call this method when required to show popup
    public void onShowPopup(View v){

        LayoutInflater layoutInflater = (LayoutInflater)getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        // inflate the custom popup layout
        final View inflatedView = layoutInflater.inflate(R.layout.paymentsetup_popup_layout, null,false);
        // find the ListView in the popup layout


        // get device size
        Display display = getWindowManager().getDefaultDisplay();
        final Point size = new Point();
        display.getSize(size);
       // mDeviceHeight = size.y;


        // fill the data to the list items
       // setSimpleList(listView);


        Button paypalBtn = (Button) inflatedView.findViewById(R.id.paypalBtn);
        paypalBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://www.paypal.com/us/home"));
                startActivity(browserIntent);



            }
        });

        Button cashAppBtn = (Button) inflatedView.findViewById(R.id.cashBtn);
        cashAppBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://cash.me/app/WTXRWNB"));
                startActivity(browserIntent);



            }
        });



        // set height depends on the device size
        paymentPopwindow = new PopupWindow(inflatedView, size.x - 50,size.y - 400, true );
        // set a background drawable with rounders corners
        paymentPopwindow.setBackgroundDrawable(getResources().getDrawable(R.drawable.paymentsetup_popup_shape));
        // make it focusable to show the keyboard to enter in `EditText`
        paymentPopwindow.setFocusable(true);
        // make it outside touchable to dismiss the popup window
        paymentPopwindow.setOutsideTouchable(true);

        // show the popup at bottom of the screen and set some margin at bottom ie,
        paymentPopwindow.showAtLocation(v, Gravity.BOTTOM, 0,100);
    }

    private void sendLogout(){

        if (Utils.connectivity(NewDashBoardActivity.this)) {
            SharedPreferences prefs = getSharedPreferences("LoginCredentials", MODE_PRIVATE);
            Gson gson = new Gson();
            String json = prefs.getString("LoginCredentials", "");
            Utils.verifyLogInUserMobileInstantResponse = gson.fromJson(json, SingleInstantResponse.class);
            Log.e("UserId", "updateDriverStatus - " + Utils.verifyLogInUserMobileInstantResponse.getUserId());
            Log.e("active", "updateDriverStatus - " + 0);
            final Dialog dialog = new Dialog(NewDashBoardActivity.this, android.R.style.Theme_Dialog);
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            dialog.setContentView(R.layout.loadingimage_layout);
            dialog.setCanceledOnTouchOutside(false);
            dialog.setCancelable(false);
            dialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
            dialog.getWindow().setLayout(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
            dialog.show();
            SingleInstantParameters loginCredentials = new SingleInstantParameters();
            loginCredentials.userId = "" + Utils.verifyLogInUserMobileInstantResponse.getUserId();


            Call<Void> call = apiService.logoutUser(Utils.verifyLogInUserMobileInstantResponse.getAccessToken(),loginCredentials);
            call.enqueue(new Callback<Void>() {
                @Override
                public void onResponse(Call<Void> call, Response<Void> response) {
                    int statusCode = response.code();

                    dialog.dismiss();
                    if (response.isSuccessful()) {

                        SharedPreferences.Editor editor = getSharedPreferences("LoginCredentials", MODE_PRIVATE).edit();
                        editor.remove("phoneNumber");
                        editor.remove("password");
                        editor.commit();
                        Intent ide = new Intent(NewDashBoardActivity.this, LoginActivity.class);
                        ide.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(ide);
                        finish();



                    }
                }

                @Override
                public void onFailure(Call<Void> call, Throwable t) {
                    // Log error here since request failed
                    Log.e("onFailure", t.toString());
                    dialog.dismiss();
                    showInfoDlg(getString(R.string.error), "" +getString(R.string.errmsg_sessionexpired), getString(R.string.btn_ok), "forcelogout");
                    //Toast.makeText(NewDashBoardActivity.this, "Either there is no network connectivity or server is not available.. Please try again later..", Toast.LENGTH_LONG).show();
                }
            });
        } else {
            Toast.makeText(NewDashBoardActivity.this, "Either there is no network connectivity or server is not available.. Please try again later..", Toast.LENGTH_LONG).show();
        }


    }
}
