package com.trivectadigital.ziprydedriverapp;

import android.app.Dialog;
import android.content.Intent;
import android.content.SharedPreferences;
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

import com.google.gson.Gson;
import com.trivectadigital.ziprydedriverapp.apis.ZiprydeApiClient;
import com.trivectadigital.ziprydedriverapp.apis.ZiprydeApiInterface;
import com.trivectadigital.ziprydedriverapp.assist.Utils;
import com.trivectadigital.ziprydedriverapp.modelget.SingleInstantResponse;
import com.trivectadigital.ziprydedriverapp.modelpost.SingleInstantParameters;

import org.json.JSONObject;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LoginActivity extends AppCompatActivity {

    Button sign_btn;
    EditText phonenoEdit, passwordEdit;
    ZiprydeApiInterface apiService;
    TextView gotoRegister;

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

        sign_btn = (Button) findViewById(R.id.sign_btn);
        gotoRegister = (TextView) findViewById(R.id.gotoRegister);

        phonenoEdit = (EditText) findViewById(R.id.phonenoEdit);
        passwordEdit = (EditText) findViewById(R.id.passwordEdit);

        sign_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                phoneno = phonenoEdit.getText().toString().trim();
                password = passwordEdit.getText().toString().trim();
                if (phoneno.isEmpty()) {
                    showInfoDlg("Information", "Please enter the phone number", "Ok", "info");
                } else if (password.isEmpty()) {
                    showInfoDlg("Information", "Please enter the password", "Ok", "info");
                } else {
                    apiService = ZiprydeApiClient.getClient().create(ZiprydeApiInterface.class);
                    SingleInstantParameters loginCredentials = new SingleInstantParameters();
                    loginCredentials.userType = "DRIVER";
                    loginCredentials.mobileNumber = phoneno;
                    loginCredentials.password = password;
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
                finish();
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

    public void insertDriverSession(){
        Log.e("UserId","insertDriverSession - "+Utils.verifyLogInUserMobileInstantResponse.getUserId());
        Log.e("Latitude","insertDriverSession - "+Utils.gpsLocationService.getLatitude());
        Log.e("Longitude","insertDriverSession - "+Utils.gpsLocationService.getLongitude());
        SingleInstantParameters loginCredentials = new SingleInstantParameters();
        loginCredentials.userId = ""+Utils.verifyLogInUserMobileInstantResponse.getUserId();
        loginCredentials.fromLatitude = ""+Utils.gpsLocationService.getLatitude();
        loginCredentials.fromLongitude = ""+Utils.gpsLocationService.getLongitude();

        Call<Void> call = apiService.updateDriverSession(loginCredentials);
        call.enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                int statusCode = response.code();
                Log.e("statusCode", "" + statusCode);
                Log.e("response.body", "" + response.body());
                Log.e("response.errorBody", "" + response.errorBody());
                Log.e("response.isSuccessful", "" + response.isSuccessful());
                if (!response.isSuccessful()) {
                    try {
                        JSONObject jObjError = new JSONObject(response.errorBody().string());
                        showInfoDlg("Error..", "" + jObjError.getString("message"), "Ok", "error");
                    } catch (Exception e) {
                        showInfoDlg("Error..", "Either there is no network connectivity or server is not available.. Please try again later..", "Ok", "server");
                    }
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                // Log error here since request failed
                Log.e("onFailure", t.toString());
                showInfoDlg("Error..", "Either there is no network connectivity or server is not available.. Please try again later..", "Ok", "server");
            }
        });
    }

    String phoneno, password;

    public void callMobileService(SingleInstantParameters loginCredentials){
        final Dialog dialog = new Dialog(LoginActivity.this, android.R.style.Theme_Dialog);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.loadingimage_layout);
        dialog.setCanceledOnTouchOutside(false);
        dialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        dialog.getWindow().setLayout(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        dialog.show();

        Call<SingleInstantResponse> call = apiService.verifyLogInUser(loginCredentials);
        call.enqueue(new Callback<SingleInstantResponse>() {
            @Override
            public void onResponse(Call<SingleInstantResponse> call, Response<SingleInstantResponse> response) {
                int statusCode = response.code();
                Log.e("statusCode",""+statusCode);
                Log.e("response.body",""+response.body());
                Log.e("response.errorBody",""+response.errorBody());
                Log.e("response.isSuccessful",""+response.isSuccessful());
                dialog.dismiss();
                if(response.isSuccessful()){
                    Utils.verifyLogInUserMobileInstantResponse = response.body();
                    Gson gson = new Gson();
                    String json = gson.toJson(Utils.verifyLogInUserMobileInstantResponse);
                    SharedPreferences.Editor editor = getSharedPreferences("LoginCredentials", MODE_PRIVATE).edit();
                    editor.putString("phoneNumber", phoneno);
                    editor.putString("password", password);
                    editor.putString("LoginCredentials", json);
                    editor.commit();
                    insertDriverSession();
                    showInfoDlg("Success..", "Successfully logged in.", "Ok", "success");
                }else{
                    try {
                        JSONObject jObjError = new JSONObject(response.errorBody().string());
                        showInfoDlg("Error..", ""+jObjError.getString("message"), "Ok", "error");
                    } catch (Exception e) {
                        showInfoDlg("Error..", "Either there is no network connectivity or server is not available.. Please try again later..", "Ok", "server");
                    }
                }
            }

            @Override
            public void onFailure(Call<SingleInstantResponse> call, Throwable t) {
                // Log error here since request failed
                Log.e("onFailure", t.toString());
                dialog.dismiss();
                showInfoDlg("Error..", "Either there is no network connectivity or server is not available.. Please try again later..", "Ok", "server");
            }
        });
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
        if (navType.equalsIgnoreCase("info") || navType.equalsIgnoreCase("server") || navType.equalsIgnoreCase("error")) {
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
                    Intent ide = new Intent(LoginActivity.this, NewDashBoardActivity.class);
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

    @Override
    public void onBackPressed() {
        Utils.gpsLocationService.stopUsingGPS();
        finish();
    }
}
