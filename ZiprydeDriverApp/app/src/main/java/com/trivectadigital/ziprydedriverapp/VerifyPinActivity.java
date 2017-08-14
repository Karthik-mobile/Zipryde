package com.trivectadigital.ziprydedriverapp;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Handler;
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
import android.widget.Toast;

import com.trivectadigital.ziprydedriverapp.apis.ZiprydeApiClient;
import com.trivectadigital.ziprydedriverapp.apis.ZiprydeApiInterface;
import com.trivectadigital.ziprydedriverapp.assist.Utils;
import com.trivectadigital.ziprydedriverapp.modelget.SingleInstantResponse;
import com.trivectadigital.ziprydedriverapp.modelpost.SingleInstantParameters;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class VerifyPinActivity extends AppCompatActivity implements View.OnClickListener {

    EditText otpEdit;
    TextView changeNumber, resendCode;
    ZiprydeApiInterface apiService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_verifypin);

        apiService = ZiprydeApiClient.getClient().create(ZiprydeApiInterface.class);

        final Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        LayoutInflater mInflater = LayoutInflater.from(this);
        View mCustomView = mInflater.inflate(R.layout.actionbar_titleback, null);
        toolbar.setContentInsetsAbsolute(0, 0);
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.MATCH_PARENT);
        toolbar.addView(mCustomView, layoutParams);
        TextView titleText = (TextView) mCustomView.findViewById(R.id.titleText);
        titleText.setText("Verify Mobile");
        ImageView backImg = (ImageView) mCustomView.findViewById(R.id.backImg);
        backImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        Button verifyBtn = (Button) findViewById(R.id.verifyBtn);
        verifyBtn.setOnClickListener(this);

        changeNumber = (TextView) findViewById(R.id.changeNumber);
        changeNumber.setOnClickListener(this);
        resendCode = (TextView) findViewById(R.id.resendCode);
        resendCode.setOnClickListener(this);

        otpEdit = (EditText) findViewById(R.id.otpEdit);
        otpEdit.setText("" + Utils.getOTPByMobileInstantResponse.getOtp());
    }

    @Override
    public void onBackPressed() {
        Utils.fromSplash = false;
        Intent ide = new Intent(VerifyPinActivity.this, MobileNumberActivity.class);
        ide.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(ide);
        finish();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.verifyBtn:
                String mobile = otpEdit.getText().toString().trim();
                if (mobile.isEmpty()) {
                    showInfoDlg("Information", "Please enter the OTP", "OK", "info");
                } else {
                    callMobileService(mobile);
                }
                break;
            case R.id.changeNumber:
                onBackPressed();
                break;
            case R.id.resendCode:
                callMobileNumberService(Utils.getOTPByMobileInstantResponse.getMobileNumber());
                break;
        }
    }

    public void callMobileNumberService(String mobile) {
        if (Utils.connectivity(VerifyPinActivity.this)) {
            final Dialog dialog = new Dialog(VerifyPinActivity.this, android.R.style.Theme_Dialog);
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            dialog.setContentView(R.layout.loadingimage_layout);
            dialog.setCanceledOnTouchOutside(false);
            dialog.setCancelable(false);
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
                    Log.e("statusCode", "" + statusCode);
                    dialog.dismiss();
                    if (statusCode == 200) {
                        Log.e("response", "" + response.body());
                        String mobileNumber = response.body().getMobileNumber();
                        Log.e("mobileNumber", "" + mobileNumber);
                        String otp = response.body().getOtp();
                        Log.e("otp", "" + otp);
                        String validity = response.body().getValidity();
                        Log.e("validity", "" + validity);
                        Utils.getOTPByMobileInstantResponse = response.body();
                        otpEdit.setText("" + Utils.getOTPByMobileInstantResponse.getOtp());
                    } else {
                        Toast.makeText(VerifyPinActivity.this, "Either there is no network connectivity or server is not available.. Please try again later..", Toast.LENGTH_LONG).show();
                    }
                }

                @Override
                public void onFailure(Call<SingleInstantResponse> call, Throwable t) {
                    // Log error here since request failed
                    Log.e("onFailure", t.toString());
                    dialog.dismiss();
                    Toast.makeText(VerifyPinActivity.this, "Either there is no network connectivity or server is not available.. Please try again later..", Toast.LENGTH_LONG).show();
                }
            });
        } else {
            Toast.makeText(VerifyPinActivity.this, "Either there is no network connectivity or server is not available.. Please try again later..", Toast.LENGTH_LONG).show();
        }
    }

    public void callMobileService(String otp) {
        if (Utils.connectivity(VerifyPinActivity.this)) {
            final Dialog dialog = new Dialog(VerifyPinActivity.this, android.R.style.Theme_Dialog);
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            dialog.setContentView(R.layout.loadingimage_layout);
            dialog.setCanceledOnTouchOutside(false);
            dialog.setCancelable(false);
            dialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
            dialog.getWindow().setLayout(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
            dialog.show();

            SingleInstantParameters loginCredentials = new SingleInstantParameters();
            loginCredentials.mobileNumber = Utils.getOTPByMobileInstantResponse.getMobileNumber();
            loginCredentials.otp = otp;

            Call<SingleInstantResponse> call = apiService.verifyOTPByMobile(loginCredentials);
            call.enqueue(new Callback<SingleInstantResponse>() {
                @Override
                public void onResponse(Call<SingleInstantResponse> call, Response<SingleInstantResponse> response) {
                    int statusCode = response.code();
                    Log.e("statusCode", "" + statusCode);
                    dialog.dismiss();
                    if (statusCode == 200) {
                        Log.e("response", "" + response.body());
                        String otpStatus = response.body().getOtpStatus();
                        Log.e("otpStatus", "" + otpStatus);
                        Utils.verifyOTPByMobileInstantResponse = response.body();
                        if (otpStatus.equals("VERIFIED")) {
                            showInfoDlg("Success..", "PIN verified successfully.", "OK", "verify success");
                        } else {
                            showInfoDlg("Error..", "PIN is INVALID. Please try again later..", "Resend", "invalid");
                        }
                    } else {
                        Toast.makeText(VerifyPinActivity.this, "Either there is no network connectivity or server is not available.. Please try again later..", Toast.LENGTH_LONG).show();
                    }
                }

                @Override
                public void onFailure(Call<SingleInstantResponse> call, Throwable t) {
                    // Log error here since request failed
                    Log.e("onFailure", t.toString());
                    dialog.dismiss();
                    Toast.makeText(VerifyPinActivity.this, "Either there is no network connectivity or server is not available.. Please try again later..", Toast.LENGTH_LONG).show();
                }
            });
        } else {
            Toast.makeText(VerifyPinActivity.this, "Either there is no network connectivity or server is not available.. Please try again later..", Toast.LENGTH_LONG).show();
        }
    }

    Dialog dialog;

    private void showInfoDlg(String title, String content, String btnText, final String navType) {
        dialog = new Dialog(VerifyPinActivity.this, android.R.style.Theme_Dialog);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.infodialog_layout);
        //dialog.setCanceledOnTouchOutside(true);
        ImageView headerIcon = (ImageView) dialog.findViewById(R.id.headerIcon);
        if (navType.equalsIgnoreCase("error") || navType.equalsIgnoreCase("invalid")) {
            headerIcon.setImageResource(R.drawable.erroricon);
        }
        Button positiveBtn = (Button) dialog.findViewById(R.id.positiveBtn);
        positiveBtn.setText("" + btnText);

        Button newnegativeBtn = (Button) dialog.findViewById(R.id.newnegativeBtn);
        if (navType.equalsIgnoreCase("info") || navType.equalsIgnoreCase("server")) {
            newnegativeBtn.setVisibility(View.GONE);
        }

        if (navType.equalsIgnoreCase("verify success")) {
            headerIcon.setImageResource(R.drawable.successicon);
            positiveBtn.setVisibility(View.GONE);
            newnegativeBtn.setVisibility(View.GONE);
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    dialog.dismiss();
                    Intent ide = new Intent(VerifyPinActivity.this, SignupActivity.class);
                    ide.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(ide);
                    finish();
                }
            }, 1000);
        }

        TextView dialogtitleText = (TextView) dialog.findViewById(R.id.dialogtitleText);
        dialogtitleText.setText("" + title);
        TextView dialogcontentText = (TextView) dialog.findViewById(R.id.dialogcontentText);
        dialogcontentText.setText("" + content);

        positiveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                if (navType.equals("invalid")) {
                    callMobileNumberService(Utils.getOTPByMobileInstantResponse.getMobileNumber());
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
