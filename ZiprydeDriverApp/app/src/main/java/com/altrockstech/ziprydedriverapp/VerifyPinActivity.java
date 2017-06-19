package com.altrockstech.ziprydedriverapp;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

public class VerifyPinActivity extends AppCompatActivity implements View.OnClickListener {

    EditText otpEdit;
    TextView changeNumber, resendCode;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_verifypin);

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

    }

    @Override
    public void onBackPressed() {
//        Utils.fromSplash = false;
        Intent ide = new Intent(VerifyPinActivity.this, MobileNumberActivity.class);
        ide.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(ide);
        finish();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.verifyBtn:
                String mobile = otpEdit.getText().toString().trim();
                if(mobile.isEmpty()){
                    showInfoDlg("Information", "Please enter the OTP", "Ok", "info");
                }else{
                    //callMobileService(mobile);
                    Intent ide = new Intent(VerifyPinActivity.this, SignupActivity.class);
                    ide.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(ide);
                    finish();
                }
                break;
            case R.id.changeNumber:
                onBackPressed();
                break;
            case R.id.resendCode:
                //callMobileNumberService(Utils.getOTPByMobileInstantResponse.getMobileNumber());
                break;
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
        if(navType.equalsIgnoreCase("error") || navType.equalsIgnoreCase("invalid")){
            headerIcon.setImageResource(R.drawable.erroricon);
        }
        Button positiveBtn = (Button) dialog.findViewById(R.id.positiveBtn);
        positiveBtn.setText(""+btnText);

        Button newnegativeBtn = (Button) dialog.findViewById(R.id.newnegativeBtn);
        if(navType.equalsIgnoreCase("info")){
            newnegativeBtn.setVisibility(View.GONE);
        }

        if(navType.equalsIgnoreCase("verify success")){
            headerIcon.setImageResource(R.drawable.successicon);
            positiveBtn.setVisibility(View.GONE);
            newnegativeBtn.setVisibility(View.GONE);
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    dialog.dismiss();
//                    Intent ide = new Intent(VerifyPinActivity.this, SignupActivity.class);
//                    ide.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
//                    startActivity(ide);
//                    finish();
                }
            }, 1000);
        }

        TextView dialogtitleText = (TextView) dialog.findViewById(R.id.dialogtitleText);
        dialogtitleText.setText(""+title);
        TextView dialogcontentText = (TextView) dialog.findViewById(R.id.dialogcontentText);
        dialogcontentText.setText(""+content);

        positiveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                if(navType.equals("invalid")) {
//                    callMobileNumberService(Utils.getOTPByMobileInstantResponse.getMobileNumber());
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
