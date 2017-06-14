package com.altrockstech.ziprydeuserapp;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.provider.Settings;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.altrockstech.ziprydeuserapp.apis.ZiprydeApiClient;
import com.altrockstech.ziprydeuserapp.apis.ZiprydeApiInterface;
import com.altrockstech.ziprydeuserapp.assist.Utils;
import com.altrockstech.ziprydeuserapp.modelget.SingleInstantResponse;
import com.altrockstech.ziprydeuserapp.modelpost.SingleInstantParameters;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MobileNumberActivity extends AppCompatActivity implements View.OnClickListener {

    ZiprydeApiInterface apiService;
    EditText mobileEdit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mobilenumber);

        final Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        LayoutInflater mInflater = LayoutInflater.from(this);
        View mCustomView = mInflater.inflate(R.layout.actionbar_titlealone, null);
        toolbar.setContentInsetsAbsolute(0, 0);
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.MATCH_PARENT);
        toolbar.addView(mCustomView, layoutParams);
        TextView titleText = (TextView) mCustomView.findViewById(R.id.titleText);
        titleText.setText("ZIPRYDE");

        Button pinBtn = (Button) findViewById(R.id.pinBtn);
        mobileEdit = (EditText) findViewById(R.id.mobileEdit);
        pinBtn.setOnClickListener(this);

        apiService = ZiprydeApiClient.getClient().create(ZiprydeApiInterface.class);

        if(!Utils.fromSplash){
            mobileEdit.setText(""+Utils.getOTPByMobileInstantResponse.getMobileNumber());
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.pinBtn:
                String mobile = mobileEdit.getText().toString().trim();
                if(mobile.isEmpty()){
                    showInfoDlg("Info..!", "Please enter the mobile number", "Ok", "info");
                }else if(mobile.length() != 10){
                    showInfoDlg("Info..!", "Please enter valid mobile number", "Ok", "info");
                }else{
                    callMobileService(mobile);
                }
                break;
        }
    }

    public void callMobileService(String mobile){
        final Dialog dialog = new Dialog(MobileNumberActivity.this, android.R.style.Theme_Dialog);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.loadingimage_layout);
        dialog.setCanceledOnTouchOutside(false);
        dialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        dialog.getWindow().setLayout(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        dialog.show();
        SingleInstantParameters loginCredentials = new SingleInstantParameters();
        loginCredentials.mobileNumber = mobile;

        Call<SingleInstantResponse> call = apiService.getOTPByMobile(loginCredentials);
        call.enqueue(new Callback<SingleInstantResponse>() {
            @Override
            public void onResponse(Call<SingleInstantResponse> call, Response<SingleInstantResponse> response) {
                int statusCode = response.code();
                Log.e("statusCode",""+statusCode);
                dialog.dismiss();
                if(statusCode == 200) {
                    Log.e("response", "" + response.body());
                    String mobileNumber = response.body().getMobileNumber();
                    Log.e("mobileNumber", "" + mobileNumber);
                    String otp = response.body().getOtp();
                    Log.e("otp", "" + otp);
                    String validity = response.body().getValidity();
                    Log.e("validity", "" + validity);
                    Utils.getOTPByMobileInstantResponse = response.body();
                    Intent ide = new Intent(MobileNumberActivity.this, VerifyPinActivity.class);
                    ide.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(ide);
                    finish();
                }else{
                    showInfoDlg("Error..!", "Either there is no network connectivity or server is not available..! Please try again later..", "Ok", "info");
                }
            }

            @Override
            public void onFailure(Call<SingleInstantResponse> call, Throwable t) {
                // Log error here since request failed
                Log.e("onFailure", t.toString());
                dialog.dismiss();
                showInfoDlg("Error..!", "Either there is no network connectivity or server is not available..! Please try again later..", "Ok", "info");
            }
        });
    }

    Dialog dialog;
    private void showInfoDlg(String title, String content, String btnText, final String navType) {
        dialog = new Dialog(MobileNumberActivity.this, android.R.style.Theme_Dialog);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.infodialog_layout);
        //dialog.setCanceledOnTouchOutside(true);

        Button positiveBtn = (Button) dialog.findViewById(R.id.positiveBtn);
        positiveBtn.setText(""+btnText);

        ImageView negativeBtn = (ImageView) dialog.findViewById(R.id.negativeBtn);
        if(navType.equalsIgnoreCase("gps")){
            negativeBtn.setVisibility(View.GONE);
        }else{
            negativeBtn.setVisibility(View.VISIBLE);
        }

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
