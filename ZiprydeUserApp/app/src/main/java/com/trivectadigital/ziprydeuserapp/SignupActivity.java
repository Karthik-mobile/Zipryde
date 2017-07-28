package com.trivectadigital.ziprydeuserapp;

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
import com.trivectadigital.ziprydeuserapp.apis.ZiprydeApiClient;
import com.trivectadigital.ziprydeuserapp.apis.ZiprydeApiInterface;
import com.trivectadigital.ziprydeuserapp.assist.Utils;
import com.trivectadigital.ziprydeuserapp.modelget.SingleInstantResponse;
import com.trivectadigital.ziprydeuserapp.modelpost.SingleInstantParameters;

import org.json.JSONObject;

import okhttp3.MediaType;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SignupActivity extends AppCompatActivity implements View.OnClickListener {

    EditText firstnameEdit, lastnameEdit, phonenoEdit, emailaddEdit, passwordEdit, confirmpasswordEdit;

    ZiprydeApiInterface apiService;

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
        phonenoEdit.setText(""+ Utils.getOTPByMobileInstantResponse.getMobileNumber());
        apiService = ZiprydeApiClient.getClient().create(ZiprydeApiInterface.class);

    }

    String phoneno, password;

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.gotoLogin:
                Intent ide = new Intent(SignupActivity.this, LoginActivity.class);
                ide.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(ide);
                finish();
                break;
            case R.id.signupBtn:

                String firstname = firstnameEdit.getText().toString();
                String lastname = lastnameEdit.getText().toString();
                phoneno = phonenoEdit.getText().toString();
                String emailadd = emailaddEdit.getText().toString();
                password = passwordEdit.getText().toString();
                String confirmpassword = confirmpasswordEdit.getText().toString();

                if(firstname.isEmpty()){
                    showInfoDlg("Information", "Please enter the First Name", "OK", "info");
                }else if(lastname.isEmpty()){
                    showInfoDlg("Information", "Please enter the Last Name", "OK", "info");
                }else if(emailadd.isEmpty()){
                    showInfoDlg("Information", "Please enter the Email Id", "OK", "info");
                }else if(password.isEmpty()){
                    showInfoDlg("Information", "Please enter the Password", "OK", "info");
                }else if(confirmpassword.isEmpty()){
                    showInfoDlg("Information", "Please enter the Confirm Password", "OK", "info");
                }else if (!password.equals(confirmpassword)) {
                    showInfoDlg("Information", "Password and Confirm Password are not matching", "OK", "info");
                }else if(phoneno.isEmpty()){
                    showInfoDlg("Information", "Please enter the Mobile Number", "OK", "info");
                }else if(phoneno.length() != 10){
                    showInfoDlg("Information", "Please enter valid Mobile Number", "OK", "info");
                }else{
                    SingleInstantParameters loginCredentials = new SingleInstantParameters();
                    loginCredentials.userType = "RIDER";
                    loginCredentials.firstName = firstname;
                    loginCredentials.lastName = lastname;
                    loginCredentials.emailId = emailadd;
                    loginCredentials.mobileNumber = phoneno;
                    loginCredentials.password = password;
                    loginCredentials.alternateNumber = "";
                    Gson gson = new Gson();
                    String json = gson.toJson(loginCredentials);
                    Log.e("json",""+json);
                    callMobileService(loginCredentials);
                }
                break;
        }
    }

    public void callMobileService(SingleInstantParameters loginCredentials){

        final Dialog dialog = new Dialog(SignupActivity.this, android.R.style.Theme_Dialog);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.loadingimage_layout);
        dialog.setCanceledOnTouchOutside(false);
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

        Call<SingleInstantResponse> call = apiService.saveUser(userType, firstName, lastName, emailId, mobileNumber, passw, alternateNumber);
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
                    Utils.saveUserMobileInstantResponse = response.body();
                    Gson gson = new Gson();
                    String json = gson.toJson(Utils.saveUserMobileInstantResponse);
                    SharedPreferences.Editor editor = getSharedPreferences("LoginCredentials", MODE_PRIVATE).edit();
                    editor.putString("phoneNumber", phoneno);
                    editor.putString("password", password);
                    editor.putString("LoginCredentials", json);
                    editor.commit();
                    Utils.verifyLogInUserMobileInstantResponse = Utils.saveUserMobileInstantResponse;
                    showInfoDlg("Success..", "Successfully registered.", "OK", "success");
                }else{
                    try {
                        JSONObject jObjError = new JSONObject(response.errorBody().string());
                        showInfoDlg("Error..", ""+jObjError.getString("message"), "OK", "error");
                    } catch (Exception e) {
                        showInfoDlg("Error..", "Either there is no network connectivity or server is not available.. Please try again later..", "OK", "server");
                    }
                }
            }

            @Override
            public void onFailure(Call<SingleInstantResponse> call, Throwable t) {
                // Log error here since request failed
                Log.e("onFailure", t.toString());
                dialog.dismiss();
                showInfoDlg("Error..", "Either there is no network connectivity or server is not available.. Please try again later..", "OK", "server");
            }
        });
    }

    Dialog dialog;
    private void showInfoDlg(String title, String content, String btnText, final String navType) {
        dialog = new Dialog(SignupActivity.this, android.R.style.Theme_Dialog);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.infodialog_layout);
        //dialog.setCanceledOnTouchOutside(true);

        ImageView headerIcon = (ImageView) dialog.findViewById(R.id.headerIcon);
        if(navType.equalsIgnoreCase("server") || navType.equalsIgnoreCase("error")){
            headerIcon.setImageResource(R.drawable.erroricon);
        }

        Button positiveBtn = (Button) dialog.findViewById(R.id.positiveBtn);
        positiveBtn.setText(""+btnText);

        Button newnegativeBtn = (Button) dialog.findViewById(R.id.newnegativeBtn);
        if(navType.equalsIgnoreCase("info") || navType.equalsIgnoreCase("error") ){
            newnegativeBtn.setVisibility(View.GONE);
        }

        if(navType.equalsIgnoreCase("success")){
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
