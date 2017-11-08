package com.trivectadigital.ziprydeuserapp;

import android.app.Dialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
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
import android.widget.Toast;

import com.google.gson.Gson;
import com.trivectadigital.ziprydeuserapp.apis.ZiprydeApiClient;
import com.trivectadigital.ziprydeuserapp.apis.ZiprydeApiInterface;
import com.trivectadigital.ziprydeuserapp.assist.Utils;
import com.trivectadigital.ziprydeuserapp.modelget.SingleInstantResponse;
import com.trivectadigital.ziprydeuserapp.modelpost.SingleInstantParameters;

import net.rimoto.intlphoneinput.IntlPhoneInput;

import org.json.JSONObject;

import okhttp3.MediaType;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SignupActivity extends AppCompatActivity implements View.OnClickListener {

    EditText firstnameEdit, lastnameEdit, phonenoEdit, emailaddEdit, passwordEdit, confirmpasswordEdit;

    ZiprydeApiInterface apiService;
    IntlPhoneInput phoneInputView;
    String appVersionName, appVersionCode;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

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

        phoneInputView = (IntlPhoneInput) findViewById(R.id.my_phone_input);

        String phoneNumber = Utils.getOTPByMobileInstantResponse.getMobileNumber();
        String get_Mo = phoneNumber.substring(phoneNumber.lastIndexOf(' ')+1);

        phonenoEdit.setText(phoneNumber);
        phoneInputView.setNumber(get_Mo);
        apiService = ZiprydeApiClient.getClient().create(ZiprydeApiInterface.class);

        try {
            PackageInfo pInfo = this.getPackageManager().getPackageInfo(getPackageName(), 0);
            appVersionName = pInfo.versionName;
            appVersionCode = String.valueOf(pInfo.versionCode);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }

    }

    String phoneno, password;

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
                phoneno =  phonenoEdit.getText().toString();//phoneInputView.getNumber();// phonenoEdit.getText().toString();

                String emailadd = emailaddEdit.getText().toString();
                password = passwordEdit.getText().toString();
                String confirmpassword = confirmpasswordEdit.getText().toString();

                if (firstname.isEmpty()) {
                    showInfoDlg("Information", "Please enter the First Name", "OK", "info");
                } else if (lastname.isEmpty()) {
                    showInfoDlg("Information", "Please enter the Last Name", "OK", "info");
                } else if (emailadd.isEmpty()) {
                    showInfoDlg("Information", "Please enter the Email Id", "OK", "info");
                } else if (!Patterns.EMAIL_ADDRESS.matcher(emailadd).matches()) {
                    showInfoDlg("Information", "Please enter the Email Id in a valid format", "OK", "info");
                } else if (password.isEmpty()) {
                    showInfoDlg("Information", "Please enter the Password", "OK", "info");
                } else if (confirmpassword.isEmpty()) {
                    showInfoDlg("Information", "Please enter the Confirm Password", "OK", "info");
                } else if (!password.equals(confirmpassword)) {
                    showInfoDlg("Information", "Password and Confirm Password are not matching", "OK", "info");
                } else if (phoneno.isEmpty()) {
                    showInfoDlg("Information", "Please enter valid Mobile Number", "OK", "info");
                } else {
                    SharedPreferences pref = getApplicationContext().getSharedPreferences(Utils.SHARED_PREF, 0);
                    String regId = pref.getString("regId", null);
                    SingleInstantParameters loginCredentials = new SingleInstantParameters();
                    loginCredentials.userType = "RIDER";
                    loginCredentials.firstName = firstname;
                    loginCredentials.lastName = lastname;
                    loginCredentials.emailId = emailadd;
                    loginCredentials.mobileNumber = phoneno;
                    loginCredentials.password = password;
                    loginCredentials.alternateNumber = "";
                    loginCredentials.deviceToken = regId;
                    loginCredentials.isEnable = 1;
                    Gson gson = new Gson();
                    String json = gson.toJson(loginCredentials);
                    Log.e("json", "" + json);
                    callMobileService(loginCredentials);
                }
                break;
        }
    }

    public void callMobileService(SingleInstantParameters loginCredentials) {
        if (Utils.connectivity(SignupActivity.this)) {
            final Dialog dialog = new Dialog(SignupActivity.this, android.R.style.Theme_Dialog);
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            dialog.setContentView(R.layout.loadingimage_layout);
            dialog.setCanceledOnTouchOutside(false);
            dialog.setCancelable(false);
            dialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
            dialog.getWindow().setLayout(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
            dialog.show();

            RequestBody userType = RequestBody.create(MediaType.parse("text/plain"), loginCredentials.userType);
            RequestBody firstName = RequestBody.create(MediaType.parse("text/plain"), loginCredentials.firstName);
            RequestBody lastName = RequestBody.create(MediaType.parse("text/plain"), loginCredentials.lastName);
            RequestBody emailId = RequestBody.create(MediaType.parse("text/plain"), loginCredentials.emailId);
            RequestBody mobileNumber = RequestBody.create(MediaType.parse("text/plain"), loginCredentials.mobileNumber);
            RequestBody passw = RequestBody.create(MediaType.parse("text/plain"), loginCredentials.password);
            RequestBody alternateNumber = RequestBody.create(MediaType.parse("text/plain"), loginCredentials.alternateNumber);
            RequestBody deviceToken = RequestBody.create(MediaType.parse("text/plain"), loginCredentials.deviceToken);
            RequestBody isEnable = RequestBody.create(MediaType.parse("text/plain"), "" + loginCredentials.isEnable);

            Call<SingleInstantResponse> call = apiService.saveUser(userType, firstName, lastName, emailId, mobileNumber, passw, alternateNumber, deviceToken, isEnable);
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
                        Utils.saveUserMobileInstantResponse = response.body();
                        Gson gson = new Gson();
                        String json = gson.toJson(Utils.saveUserMobileInstantResponse);
                        SharedPreferences.Editor editor = getSharedPreferences("LoginCredentials", MODE_PRIVATE).edit();
                        editor.putString("phoneNumber", phoneno);
                        editor.putString("password", password);
                        editor.putString("LoginCredentials", json);
                        editor.commit();
                        Utils.verifyLogInUserMobileInstantResponse = Utils.saveUserMobileInstantResponse;
                        SharedPreferences pref = getApplicationContext().getSharedPreferences(Utils.SHARED_PREF, 0);
                        String regId = pref.getString("regId", null);
                        SingleInstantParameters loginCredentials = new SingleInstantParameters();
                        loginCredentials.userType = "RIDER";
                        loginCredentials.mobileNumber = phoneno;
                        loginCredentials.password = password;
                        loginCredentials.deviceToken = regId;
                        loginCredentials.overrideSessionToken=0;
                        loginCredentials.mobileOS ="ANDROID";
                        loginCredentials.buildNo =appVersionCode;
                        loginCredentials.versionNumber = appVersionName;
                        loginCredentials.appName = "ZIPRYDE";


                        callLoginTogetAccessToken(loginCredentials);

                    } else {
                        try {
                            JSONObject jObjError = new JSONObject(response.errorBody().string());
                            showInfoDlg("Error..", "" + jObjError.getString("message"), "OK", "error");
                        } catch (Exception e) {
                            Toast.makeText(SignupActivity.this, "Either there is no network connectivity or server is not available.. Please try again later..", Toast.LENGTH_LONG).show();
                        }
                    }
                }

                @Override
                public void onFailure(Call<SingleInstantResponse> call, Throwable t) {
                    // Log error here since request failed
                    Log.e("onFailure", t.toString());
                    dialog.dismiss();
                    Toast.makeText(SignupActivity.this, "Either there is no network connectivity or server is not available.. Please try again later..", Toast.LENGTH_LONG).show();
                }
            });
        } else {
            Toast.makeText(SignupActivity.this, "Either there is no network connectivity or server is not available.. Please try again later..", Toast.LENGTH_LONG).show();
        }
    }


    public void callLoginTogetAccessToken(SingleInstantParameters loginCredentials) {
        if (Utils.connectivity(SignupActivity.this)) {
            final Dialog dialog = new Dialog(SignupActivity.this, android.R.style.Theme_Dialog);
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            dialog.setContentView(R.layout.loadingimage_layout);
            dialog.setCanceledOnTouchOutside(false);
            dialog.setCancelable(false);
            dialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
            dialog.getWindow().setLayout(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
            dialog.show();

            Call<SingleInstantResponse> call = apiService.verifyLogInUser(loginCredentials);
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
                        Utils.verifyLogInUserMobileInstantResponse = response.body();
                        Gson gson = new Gson();
                        String json = gson.toJson(Utils.verifyLogInUserMobileInstantResponse);
                        SharedPreferences.Editor editor = getSharedPreferences("LoginCredentials", MODE_PRIVATE).edit();
                        editor.putString("phoneNumber", phoneno);
                        editor.putString("password", password);
                        // editor.putString("accesstoken",)
                        editor.putString("LoginCredentials", json);
                        //String s = Utils.verifyLogInUserMobileInstantResponse.getAccessToken();
                        //Toast.makeText(getApplicationContext(),s,Toast.LENGTH_LONG).show();
                        editor.commit();
                        //showInfoDlg(getString(R.string.success), getString(R.string.usermsg_successfullogin), getString(R.string.btn_ok), "success");
                        showInfoDlg("Success..", "Successfully registered.", "OK", "success");
                    } else {
                        try {

                            JSONObject jObjError = new JSONObject(response.errorBody().string());

                            if(response.code() == Utils.NETOWRKERR_OVERRIDE_LOGIN){


                                // JSONObject jObjError = new JSONObject(response.errorBody().string());
                                // Toast.makeText(LoginActivity.this, jObjError.toString(), Toast.LENGTH_LONG).show();
                                //if(jObjError.getString("message"))
                                showInfoDlg(getString(R.string.error), "" + jObjError.getString("message"), getString(R.string.btn_yes), "forcelogin");

                            }else {


                                // Toast.makeText(LoginActivity.this, jObjError.toString(), Toast.LENGTH_LONG).show();
                                //if(jObjError.getString("message"))
                                showInfoDlg(getString(R.string.error), "" + jObjError.getString("message"), getString(R.string.btn_ok), "error");
                            }
                        } catch (Exception e) {
                            Toast.makeText(SignupActivity.this, getString(R.string.errmsg_network_noconnection), Toast.LENGTH_LONG).show();
                        }
                    }
                }

                @Override
                public void onFailure(Call<SingleInstantResponse> call, Throwable t) {
                    // Log error here since request failed
                    Log.e("onFailure", t.toString());
                    //Toast.makeText(LoginActivity.this, t.toString(), Toast.LENGTH_LONG).show();
                    dialog.dismiss();
                    Toast.makeText(SignupActivity.this, getString(R.string.errmsg_network_noconnection), Toast.LENGTH_LONG).show();
                }
            });
        } else {
            Toast.makeText(SignupActivity.this, getString(R.string.errmsg_network_noconnection), Toast.LENGTH_LONG).show();
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
        if (navType.equalsIgnoreCase("info") || navType.equalsIgnoreCase("error") || navType.equalsIgnoreCase("server")) {
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
                    Intent ide = new Intent(SignupActivity.this, NavigationMenuActivity.class);
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
