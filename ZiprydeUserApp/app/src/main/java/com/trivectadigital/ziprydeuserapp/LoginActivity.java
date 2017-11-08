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

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LoginActivity extends AppCompatActivity {

    Button sign_btn;
    EditText phonenoEdit, passwordEdit;
    ZiprydeApiInterface apiService;
    TextView gotoRegister, gotoForgetPwd;
    String appVersionName, appVersionCode;

    IntlPhoneInput phoneInputView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        final Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        LayoutInflater mInflater = LayoutInflater.from(this);
        View mCustomView = mInflater.inflate(R.layout.actionbar_titlealone, null);
        toolbar.setContentInsetsAbsolute(0, 0);
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.MATCH_PARENT);
        toolbar.addView(mCustomView, layoutParams);
        TextView titleText = (TextView) mCustomView.findViewById(R.id.titleText);
        titleText.setText("Login");

        try {
            PackageInfo pInfo = this.getPackageManager().getPackageInfo(getPackageName(), 0);
            appVersionName = pInfo.versionName;
            appVersionCode = String.valueOf(pInfo.versionCode);
        } catch (PackageManager.NameNotFoundException e) {
        e.printStackTrace();
        }
       // buildNumber.setText(version);


        sign_btn = (Button) findViewById(R.id.sign_btn);
        gotoRegister = (TextView) findViewById(R.id.gotoRegister);
        gotoForgetPwd = (TextView) findViewById(R.id.gotoForgetPwd);

        phonenoEdit = (EditText) findViewById(R.id.phonenoEdit);
        passwordEdit = (EditText) findViewById(R.id.passwordEdit);

        phoneInputView = (IntlPhoneInput) findViewById(R.id.login_phone_input);

        sign_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                phoneno = phonenoEdit.getText().toString().trim();//phoneInputView.getNumber();//phonenoEdit.getText().toString().trim();
                password = passwordEdit.getText().toString().trim();

                if(phoneno.isEmpty()){
                    showInfoDlg("Information", "Please enter valid mobile number", "OK", "info");
                }
                else if (password.isEmpty()) {
                    showInfoDlg("Information", "Please enter the Password", "OK", "info");
                } else {

                    SharedPreferences pref = getApplicationContext().getSharedPreferences(Utils.SHARED_PREF, 0);
                    String regId = pref.getString("regId", null);
                    apiService = ZiprydeApiClient.getClient().create(ZiprydeApiInterface.class);
                    SingleInstantParameters loginCredentials = new SingleInstantParameters();
                    loginCredentials.userType = "RIDER";
                    loginCredentials.mobileNumber = phoneno;
                    loginCredentials.password = password;
                    loginCredentials.deviceToken = regId;
                    loginCredentials.overrideSessionToken=0;
                    loginCredentials.mobileOS ="ANDROID";
                    loginCredentials.buildNo = appVersionCode;
                    loginCredentials.versionNumber = appVersionName;
                    loginCredentials.appName = "ZIPRYDE";


                    Gson gson = new Gson();
                    String json = gson.toJson(loginCredentials);
                    Log.e("json", "" + json);
                    callMobileService(loginCredentials);
                }
            }
        });

        gotoRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent ide = new Intent(LoginActivity.this, MobileNumberActivity.class);
                ide.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(ide);
            }
        });

        gotoForgetPwd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent ide = new Intent(LoginActivity.this, ForgetPasswordActivity.class);
                ide.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(ide);
            }
        });

        ImageView settingsPage = (ImageView) findViewById(R.id.settingsPage);
        settingsPage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent ide = new Intent(LoginActivity.this, SettingsActivity.class);
                ide.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(ide);
            }
        });
    }

    String phoneno, password;

    public void callMobileService(SingleInstantParameters loginCredentials) {
        if (Utils.connectivity(LoginActivity.this)) {
            final Dialog dialog = new Dialog(LoginActivity.this, android.R.style.Theme_Dialog);
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
                        showInfoDlg(getString(R.string.success), getString(R.string.usermsg_successfullogin), getString(R.string.btn_ok), "success");
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
                            Toast.makeText(LoginActivity.this, getString(R.string.errmsg_network_noconnection), Toast.LENGTH_LONG).show();
                        }
                    }
                }

                @Override
                public void onFailure(Call<SingleInstantResponse> call, Throwable t) {
                    // Log error here since request failed
                    Log.e("onFailure", t.toString());
                    //Toast.makeText(LoginActivity.this, t.toString(), Toast.LENGTH_LONG).show();
                    dialog.dismiss();
                    Toast.makeText(LoginActivity.this, getString(R.string.errmsg_network_noconnection), Toast.LENGTH_LONG).show();
                }
            });
        } else {
            Toast.makeText(LoginActivity.this, getString(R.string.errmsg_network_noconnection), Toast.LENGTH_LONG).show();
        }
    }


    Dialog dialog;

    private void showInfoDlg(String title, String content, String btnText, final String navType) {
        dialog = new Dialog(LoginActivity.this, android.R.style.Theme_Dialog);
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

        if(navType.equalsIgnoreCase("forcelogin")) {
            newnegativeBtn.setText(getString(R.string.btn_no));
        }

        if (navType.equalsIgnoreCase("success")) {
            headerIcon.setImageResource(R.drawable.successicon);
            positiveBtn.setVisibility(View.GONE);
            newnegativeBtn.setVisibility(View.GONE);
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    dialog.dismiss();
                    String bookingId = Utils.verifyLogInUserMobileInstantResponse.getBookingId();
                    if(bookingId.equals("0")) {
                        Intent ide = new Intent(LoginActivity.this, NavigationMenuActivity.class);
                        ide.putExtra("fromLogin", "fromLogin");
                        ide.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(ide);
                        finish();
                    }else{
                        Intent ide = new Intent(LoginActivity.this, DriverInfoBookingActivity.class);
                        ide.putExtra("bookingId",""+bookingId);
                        ide.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(ide);
                        finish();
                    }
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

                if(navType.equalsIgnoreCase("forcelogin")) {

                    //initiate the login with overwrite function as 1


                    SharedPreferences pref = getApplicationContext().getSharedPreferences(Utils.SHARED_PREF, 0);
                    String regId = pref.getString("regId", null);
                    apiService = ZiprydeApiClient.getClient().create(ZiprydeApiInterface.class);
                    SingleInstantParameters loginCredentials = new SingleInstantParameters();
                    loginCredentials.userType = "RIDER";
                    loginCredentials.mobileNumber = phoneno;
                    loginCredentials.password = password;
                    loginCredentials.deviceToken = regId;
                    loginCredentials.overrideSessionToken=1;
                    loginCredentials.mobileOS ="ANDROID";
                    loginCredentials.buildNo =appVersionCode;
                    loginCredentials.versionNumber = appVersionName;
                    loginCredentials.appName = "ZIPRYDE";

                    Gson gson = new Gson();
                    String json = gson.toJson(loginCredentials);
                    Log.e("json", "" + json);
                    callMobileService(loginCredentials);
                }

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

    @Override
    public void onBackPressed() {

    }
}
