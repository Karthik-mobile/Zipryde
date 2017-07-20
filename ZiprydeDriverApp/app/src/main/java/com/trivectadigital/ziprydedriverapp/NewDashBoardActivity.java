package com.trivectadigital.ziprydedriverapp;

import android.app.Dialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.trivectadigital.ziprydedriverapp.apis.ZiprydeApiClient;
import com.trivectadigital.ziprydedriverapp.apis.ZiprydeApiInterface;
import com.trivectadigital.ziprydedriverapp.assist.Utils;
import com.trivectadigital.ziprydedriverapp.modelpost.SingleInstantParameters;

import org.json.JSONObject;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class NewDashBoardActivity extends AppCompatActivity {

    LinearLayout rideLayout, historyLayout, notificationLayout;

    RelativeLayout onofflineLay;

    boolean isOnline = true;

    View viewOffline, viewOnline;

    TextView driverInfoText;

    ZiprydeApiInterface apiService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_dash_board);

        apiService = ZiprydeApiClient.getClient().create(ZiprydeApiInterface.class);

        onofflineLay = (RelativeLayout) findViewById(R.id.onofflineLay);
        viewOffline = findViewById(R.id.viewOffline);
        viewOnline = findViewById(R.id.viewOnline);

        driverInfoText = (TextView) findViewById(R.id.driverInfoText);

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

        updateDriverStatus(1);
        isOnline = true;
        onofflineLay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(isOnline){
                    updateDriverStatus(1);
                }else{
                    updateDriverStatus(0);
                }
            }
        });
    }

    public void updateDriverStatus(final int active){
        Log.e("UserId","updateDriverStatus - "+ Utils.verifyLogInUserMobileInstantResponse.getUserId());
        Log.e("active","updateDriverStatus - "+active);
        final Dialog dialog = new Dialog(NewDashBoardActivity.this, android.R.style.Theme_Dialog);
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
                        showInfoDlg("Error..", "" + jObjError.getString("message"), "Ok", "error");
                    } catch (Exception e) {
                        showInfoDlg("Error..", "Either there is no network connectivity or server is not available.. Please try again later..", "Ok", "server");
                    }
                }else{
                    if(active == 1) {
                        isOnline = false;
                        viewOffline.setVisibility(View.GONE);
                        viewOnline.setVisibility(View.VISIBLE);
                        driverInfoText.setText("Online");
                    }else{
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
                showInfoDlg("Error..", "Either there is no network connectivity or server is not available.. Please try again later..", "Ok", "server");
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
