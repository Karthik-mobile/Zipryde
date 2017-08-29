package com.trivectadigital.ziprydedriverapp;

import android.app.Dialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.InputFilter;
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

import org.json.JSONObject;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ForgetPasswordActivity extends AppCompatActivity {

    EditText otpEdit, mobileNumberEdit, mobileNumberFinalEdit, passwordEdit, confirmpasswordEdit;
    Button verifyBtn, generateBtn, changeBtn;
    TextView generateOTP, enterOTP;

    LinearLayout otpLay, mobileLay, passwordLay;
    ZiprydeApiInterface apiService;

    String mobileNo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forget_password);

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
        titleText.setText("Forget Password");

        otpLay = (LinearLayout) findViewById(R.id.otpLay);
        mobileLay = (LinearLayout) findViewById(R.id.mobileLay);
        passwordLay = (LinearLayout) findViewById(R.id.passwordLay);

        otpEdit = (EditText) findViewById(R.id.otpEdit);
        otpEdit.setFilters(new InputFilter[]{new InputFilter.AllCaps()});
        mobileNumberEdit = (EditText) findViewById(R.id.mobileNumberEdit);

        verifyBtn = (Button) findViewById(R.id.verifyBtn);
        verifyBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String mobile = otpEdit.getText().toString().trim();
                if (mobile.isEmpty()) {
                    showInfoDlg("Information", "Please enter the PIN", "OK", "info");
                } else {
                    callMobileOTPService(mobile);
                }
            }
        });

        generateOTP = (TextView) findViewById(R.id.generateOTP);
        generateOTP.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                otpLay.setVisibility(View.GONE);
                mobileLay.setVisibility(View.VISIBLE);
                passwordLay.setVisibility(View.GONE);
            }
        });

        generateBtn = (Button) findViewById(R.id.generateBtn);
        generateBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String mobile = mobileNumberEdit.getText().toString().trim();
                if (mobile.isEmpty()) {
                    showInfoDlg("Information", "Please enter the mobile number", "OK", "info");
                } else if (mobile.length() != 10) {
                    showInfoDlg("Information", "Please enter valid mobile number", "OK", "info");
                } else {
                    callMobileService(mobile);
                }
            }
        });

        enterOTP = (TextView) findViewById(R.id.enterOTP);
        enterOTP.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                otpLay.setVisibility(View.VISIBLE);
                mobileLay.setVisibility(View.GONE);
                passwordLay.setVisibility(View.GONE);
            }
        });

        mobileNumberFinalEdit = (EditText) findViewById(R.id.mobileNumberFinalEdit);
        passwordEdit = (EditText) findViewById(R.id.passwordEdit);
        confirmpasswordEdit = (EditText) findViewById(R.id.confirmpasswordEdit);

        changeBtn = (Button) findViewById(R.id.changeBtn);
        changeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               // String mobile = mobileNumberEdit.getText().toString().trim();
                String password = passwordEdit.getText().toString();
                String confirmpassword = confirmpasswordEdit.getText().toString();
                if (password.isEmpty()) {
                    showInfoDlg("Information", "Please enter the Password", "OK", "info");
                } else if (confirmpassword.isEmpty()) {
                    showInfoDlg("Information", "Please enter the Confirm Password", "OK", "info");
                } else if (!password.equals(confirmpassword)) {
                    showInfoDlg("Information", "Password and Confirm Password are not matching", "OK", "info");
                } else {
                    callMobilePasswordUpdateService(mobileNo, password);
                }
            }
        });
    }

    public void callMobilePasswordUpdateService(String mobile, String password) {
        if (Utils.connectivity(ForgetPasswordActivity.this)) {
            final Dialog dialog = new Dialog(ForgetPasswordActivity.this, android.R.style.Theme_Dialog);
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
            loginCredentials.password = password;
            loginCredentials.userType = "DRIVER";

            Call<SingleInstantResponse> call = apiService.updatePasswordByUserAndType(loginCredentials);
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
                        showInfoDlg("Success..", "Your Password has been reset successfully.", "OK", "password success");
                    } else {
                        try {
                            JSONObject jObjError = new JSONObject(response.errorBody().string());
                            showInfoDlg("Error..", "" + jObjError.getString("message"), "OK", "error");
                        } catch (Exception e) {
                            Toast.makeText(ForgetPasswordActivity.this, "Either there is no network connectivity or server is not available.. Please try again later..", Toast.LENGTH_LONG).show();
                        }
                    }
                }

                @Override
                public void onFailure(Call<SingleInstantResponse> call, Throwable t) {
                    // Log error here since request failed
                    Log.e("onFailure", t.toString());
                    dialog.dismiss();
                    Toast.makeText(ForgetPasswordActivity.this, "Either there is no network connectivity or server is not available.. Please try again later..", Toast.LENGTH_LONG).show();
                }
            });
        } else {
            Toast.makeText(ForgetPasswordActivity.this, "Either there is no network connectivity or server is not available.. Please try again later..", Toast.LENGTH_LONG).show();
        }
    }

    public void callMobileOTPService(String otp) {
        if (Utils.connectivity(ForgetPasswordActivity.this)) {
            final Dialog dialog = new Dialog(ForgetPasswordActivity.this, android.R.style.Theme_Dialog);
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
                    Log.e("response.body", "" + response.body());
                    Log.e("response.errorBody", "" + response.errorBody());
                    Log.e("response.isSuccessful", "" + response.isSuccessful());
                    dialog.dismiss();
                    if (response.isSuccessful()) {
                        Log.e("response", "" + response.body());
                        String otpStatus = response.body().getOtpStatus();
                        Log.e("otpStatus", "" + otpStatus);
                        Utils.verifyOTPByMobileInstantResponse = response.body();
                        if (otpStatus.equals("VERIFIED")) {
                            showInfoDlg("Success..", "PIN verified successfully.", "OK", "verify success");
                        } else {
                            showInfoDlg("Error..", "PIN is INVALID. Please try again later..", "OK", "invalid");
                        }
                    } else {
                        try {
                            JSONObject jObjError = new JSONObject(response.errorBody().string());
                            showInfoDlg("Error..", "" + jObjError.getString("message"), "OK", "error");
                        } catch (Exception e) {
                            Toast.makeText(ForgetPasswordActivity.this, "Either there is no network connectivity or server is not available.. Please try again later..", Toast.LENGTH_LONG).show();
                        }
                    }
                }

                @Override
                public void onFailure(Call<SingleInstantResponse> call, Throwable t) {
                    // Log error here since request failed
                    Log.e("onFailure", t.toString());
                    dialog.dismiss();
                    Toast.makeText(ForgetPasswordActivity.this, "Either there is no network connectivity or server is not available.. Please try again later..", Toast.LENGTH_LONG).show();
                }
            });
        } else {
            Toast.makeText(ForgetPasswordActivity.this, "Either there is no network connectivity or server is not available.. Please try again later..", Toast.LENGTH_LONG).show();
        }
    }

    public void callMobileService(String mobile) {
        if (Utils.connectivity(ForgetPasswordActivity.this)) {
            final Dialog dialog = new Dialog(ForgetPasswordActivity.this, android.R.style.Theme_Dialog);
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
            mobileNo = mobile;

            Call<SingleInstantResponse> call = apiService.getOTPByMobile(loginCredentials);
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
                        Log.e("response", "" + response.body());
                        String mobileNumber = response.body().getMobileNumber();
                        Log.e("mobileNumber", "" + mobileNumber);
                        String otp = response.body().getOtp();
                        Log.e("otp", "" + otp);
                        String validity = response.body().getValidity();
                        Log.e("validity", "" + validity);
                        Utils.getOTPByMobileInstantResponse = response.body();
                        otpEdit.setText("" + Utils.getOTPByMobileInstantResponse.getOtp());
                        otpLay.setVisibility(View.VISIBLE);
                        mobileLay.setVisibility(View.GONE);
                        passwordLay.setVisibility(View.GONE);
                    } else {
                        try {
                            JSONObject jObjError = new JSONObject(response.errorBody().string());
                            showInfoDlg("Error..", "" + jObjError.getString("message"), "OK", "error");
                        } catch (Exception e) {
                            Toast.makeText(ForgetPasswordActivity.this, "Either there is no network connectivity or server is not available.. Please try again later..", Toast.LENGTH_LONG).show();
                        }
                    }
                }

                @Override
                public void onFailure(Call<SingleInstantResponse> call, Throwable t) {
                    // Log error here since request failed
                    Log.e("onFailure", t.toString());
                    dialog.dismiss();
                    Toast.makeText(ForgetPasswordActivity.this, "Either there is no network connectivity or server is not available.. Please try again later..", Toast.LENGTH_LONG).show();
                }
            });
        } else {
            Toast.makeText(ForgetPasswordActivity.this, "Either there is no network connectivity or server is not available.. Please try again later..", Toast.LENGTH_LONG).show();
        }
    }

    Dialog dialog;

    private void showInfoDlg(String title, String content, String btnText, final String navType) {
        dialog = new Dialog(ForgetPasswordActivity.this, android.R.style.Theme_Dialog);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.infodialog_layout);
        //dialog.setCanceledOnTouchOutside(true);

        ImageView headerIcon = (ImageView) dialog.findViewById(R.id.headerIcon);
        if (navType.equalsIgnoreCase("error")) {
            headerIcon.setImageResource(R.drawable.erroricon);
        }

        Button positiveBtn = (Button) dialog.findViewById(R.id.positiveBtn);
        positiveBtn.setText("" + btnText);

        Button newnegativeBtn = (Button) dialog.findViewById(R.id.newnegativeBtn);
        if (navType.equalsIgnoreCase("info") || navType.equalsIgnoreCase("server")) {
            newnegativeBtn.setVisibility(View.GONE);
        } else {
            newnegativeBtn.setVisibility(View.VISIBLE);
        }

        if (navType.equalsIgnoreCase("verify success")) {
            headerIcon.setImageResource(R.drawable.successicon);
            positiveBtn.setVisibility(View.GONE);
            newnegativeBtn.setVisibility(View.GONE);
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    dialog.dismiss();
                    otpLay.setVisibility(View.GONE);
                    mobileLay.setVisibility(View.GONE);
                    passwordLay.setVisibility(View.VISIBLE);
                }
            }, 1000);
        }

        if (navType.equalsIgnoreCase("password success")) {
            headerIcon.setImageResource(R.drawable.successicon);
            positiveBtn.setVisibility(View.GONE);
            newnegativeBtn.setVisibility(View.GONE);
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    dialog.dismiss();
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
