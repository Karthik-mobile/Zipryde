package com.trivectadigital.ziprydedriverapp;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.trivectadigital.ziprydedriverapp.apis.ZiprydeApiClient;
import com.trivectadigital.ziprydedriverapp.apis.ZiprydeApiInterface;
import com.trivectadigital.ziprydedriverapp.assist.Utils;

public class SignupActivity extends AppCompatActivity implements View.OnClickListener {

    EditText firstnameEdit, lastnameEdit, phonenoEdit, emailaddEdit, vehiclenoEdit, passwordEdit, confirmpasswordEdit;
    ZiprydeApiInterface apiService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);
        apiService = ZiprydeApiClient.getClient().create(ZiprydeApiInterface.class);
        final Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        LayoutInflater mInflater = LayoutInflater.from(this);
        View mCustomView = mInflater.inflate(R.layout.actionbar_titlealone, null);
        toolbar.setContentInsetsAbsolute(0, 0);
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.MATCH_PARENT);
        toolbar.addView(mCustomView, layoutParams);
        TextView titleText = (TextView) mCustomView.findViewById(R.id.titleText);
        titleText.setText("Sign Up");

        TextView gotoLogin = (TextView) findViewById(R.id.gotoLogin);
        gotoLogin.setOnClickListener(this);
        Button signupBtn = (Button) findViewById(R.id.signupBtn);
        signupBtn.setOnClickListener(this);

        firstnameEdit = (EditText) findViewById(R.id.firstnameEdit);
        lastnameEdit = (EditText) findViewById(R.id.lastnameEdit);
        phonenoEdit = (EditText) findViewById(R.id.phonenoEdit);
        emailaddEdit = (EditText) findViewById(R.id.emailaddEdit);
        passwordEdit = (EditText) findViewById(R.id.passwordEdit);
        confirmpasswordEdit = (EditText) findViewById(R.id.confirmpasswordEdit);
        vehiclenoEdit = (EditText) findViewById(R.id.vehiclenoEdit);
        phonenoEdit.setText("" + Utils.getOTPByMobileInstantResponse.getMobileNumber());
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.gotoLogin:
                Intent ide = new Intent(SignupActivity.this, LoginActivity.class);
                ide.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(ide);
                finish();
                break;
            case R.id.signupBtn:

                String firstname = firstnameEdit.getText().toString();
                String lastname = lastnameEdit.getText().toString();
                String phoneno = phonenoEdit.getText().toString();
                String emailadd = emailaddEdit.getText().toString();
                String password = passwordEdit.getText().toString();
                //String vehicleno = vehiclenoEdit.getText().toString();
                String confirmpassword = confirmpasswordEdit.getText().toString();

                if (firstname.isEmpty()) {
                    showInfoDlg("Information", "Please enter the first Name", "OK", "info");
                } else if (lastname.isEmpty()) {
                    showInfoDlg("Information", "Please enter the Last Name", "OK", "info");
                } else if (emailadd.isEmpty()) {
                    showInfoDlg("Information", "Please enter the Email Id", "OK", "info");
                } else if (!Patterns.EMAIL_ADDRESS.matcher(emailadd).matches()) {
                    showInfoDlg("Information", "Please enter the Proper Email Id", "OK", "info");
                }else if (password.isEmpty()) {
                    showInfoDlg("Information", "Please enter the Password", "OK", "info");
                }else if (confirmpassword.isEmpty()) {
                    showInfoDlg("Information", "Please enter the Confirm Password", "OK", "info");
                }else if (!password.equals(confirmpassword)) {
                    showInfoDlg("Information", "Password and Confirm Password are not Matched", "OK", "info");
                } else if (phoneno.isEmpty()) {
                    showInfoDlg("Information", "Please enter the Mobile Number", "OK", "info");
                } else if (phoneno.length() != 10) {
                    showInfoDlg("Information", "Please enter valid Mobile Number", "OK", "info");
                }
//                else if (vehicleno.isEmpty()) {
//                    showInfoDlg("Information", "Please enter the vehicle number", "Ok", "info");
//                }
                else {
                    ide = new Intent(SignupActivity.this, DocumentUploadActivity.class);
                    ide.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    ide.putExtra("firstName", firstname);
                    ide.putExtra("lastName", lastname);
                    ide.putExtra("emailId", emailadd);
                    ide.putExtra("mobileNumber", phoneno);
                    ide.putExtra("password", password);
//                    ide.putExtra("vehicleno", vehicleno);
                    startActivity(ide);
                    finish();
                }
                break;
        }
    }

    Dialog dialog;
    private void showInfoDlg(String title, String content, String btnText, final String navType) {
        dialog = new Dialog(SignupActivity.this, android.R.style.Theme_Dialog);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.infodialog_layout);
        //dialog.setCanceledOnTouchOutside(true);

        ImageView headerIcon = (ImageView) dialog.findViewById(R.id.headerIcon);
        if (navType.equalsIgnoreCase("server") || navType.equalsIgnoreCase("error")) {
            headerIcon.setImageResource(R.drawable.erroricon);
        }

        Button positiveBtn = (Button) dialog.findViewById(R.id.positiveBtn);
        positiveBtn.setText("" + btnText);

        Button newnegativeBtn = (Button) dialog.findViewById(R.id.newnegativeBtn);
        if (navType.equalsIgnoreCase("info") || navType.equalsIgnoreCase("error")) {
            newnegativeBtn.setVisibility(View.GONE);
        }

        if (navType.equalsIgnoreCase("success")) {
            headerIcon.setImageResource(R.drawable.successicon);
            positiveBtn.setVisibility(View.GONE);
            newnegativeBtn.setVisibility(View.GONE);
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    dialog.dismiss();
                    Intent ide = new Intent(SignupActivity.this, DocumentUploadActivity.class);
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
