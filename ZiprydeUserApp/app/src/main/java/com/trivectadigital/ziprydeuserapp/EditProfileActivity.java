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

public class EditProfileActivity extends AppCompatActivity {

    EditText firstnameEdit, lastnameEdit, emailaddEdit, mobileEdit;
    Button edit_btn;
    ZiprydeApiInterface apiService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_editprofile);
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
        titleText.setText("Edit Profile");
        ImageView backImg = (ImageView) mCustomView.findViewById(R.id.backImg);
        backImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        firstnameEdit = (EditText) findViewById(R.id.firstnameEdit);
        firstnameEdit.setText(""+ Utils.verifyLogInUserMobileInstantResponse.getFirstName());

        lastnameEdit = (EditText) findViewById(R.id.lastnameEdit);
        lastnameEdit.setText(""+ Utils.verifyLogInUserMobileInstantResponse.getLastName());

        emailaddEdit = (EditText) findViewById(R.id.emailaddEdit);
        emailaddEdit.setText(""+ Utils.verifyLogInUserMobileInstantResponse.getEmailId());

        mobileEdit = (EditText) findViewById(R.id.mobileEdit);
        mobileEdit.setText(""+ Utils.verifyLogInUserMobileInstantResponse.getMobileNumber());

        edit_btn = (Button) findViewById(R.id.edit_btn);
        edit_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String firstname = firstnameEdit.getText().toString();
                String lastname = lastnameEdit.getText().toString();
                String phoneno = mobileEdit.getText().toString();
                String emailadd = emailaddEdit.getText().toString();

                if(firstname.isEmpty()){
                    showInfoDlg("Information", "Please enter the First Name", "OK", "info");
                }else if(lastname.isEmpty()){
                    showInfoDlg("Information", "Please enter the Last Name", "OK", "info");
                }else if(emailadd.isEmpty()){
                    showInfoDlg("Information", "Please enter the Email Id", "OK", "info");
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
                    loginCredentials.userId = Utils.verifyLogInUserMobileInstantResponse.getUserId();
                    loginCredentials.alternateNumber = "";
                    Gson gson = new Gson();
                    String json = gson.toJson(loginCredentials);
                    Log.e("json",""+json);
                    callMobileService(loginCredentials);
                }
            }
        });
    }

    public void callMobileService(SingleInstantParameters loginCredentials){
        final Dialog dialog = new Dialog(EditProfileActivity.this, android.R.style.Theme_Dialog);
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
        RequestBody userId = RequestBody.create(MediaType.parse("text/plain"), loginCredentials.userId);
        RequestBody alternateNumber = RequestBody.create(MediaType.parse("text/plain"), loginCredentials.alternateNumber);

        Call<SingleInstantResponse> call = apiService.saveUpdateUser(userType, firstName, lastName, emailId, mobileNumber, userId, alternateNumber);
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
                    Utils.verifyLogInUserMobileInstantResponse = Utils.saveUserMobileInstantResponse;
                    showInfoDlg("Success..", "Successfully updated.", "OK", "success");
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
        dialog = new Dialog(EditProfileActivity.this, android.R.style.Theme_Dialog);
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
                    Intent ide = new Intent(EditProfileActivity.this, NavigationMenuActivity.class);
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

    @Override
    public void onBackPressed() {
        finish();
    }

}