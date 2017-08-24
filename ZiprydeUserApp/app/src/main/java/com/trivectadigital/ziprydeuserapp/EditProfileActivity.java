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
import android.widget.Toast;

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
        apiService = ZiprydeApiClient.getClient(Utils.verifyLogInUserMobileInstantResponse.getAccessToken()).create(ZiprydeApiInterface.class);

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
        firstnameEdit.setText("" + Utils.verifyLogInUserMobileInstantResponse.getFirstName());

        Log.e("LastName", "" + Utils.verifyLogInUserMobileInstantResponse.getLastName());
        lastnameEdit = (EditText) findViewById(R.id.lastnameEdit);
        lastnameEdit.setText("" + Utils.verifyLogInUserMobileInstantResponse.getLastName());

        emailaddEdit = (EditText) findViewById(R.id.emailaddEdit);
        emailaddEdit.setText("" + Utils.verifyLogInUserMobileInstantResponse.getEmailId());

        mobileEdit = (EditText) findViewById(R.id.mobileEdit);
        mobileEdit.setText("" + Utils.verifyLogInUserMobileInstantResponse.getMobileNumber());

        edit_btn = (Button) findViewById(R.id.edit_btn);
        edit_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String firstname = firstnameEdit.getText().toString();
                String lastname = lastnameEdit.getText().toString();
                String phoneno = mobileEdit.getText().toString();
                String emailadd = emailaddEdit.getText().toString();

                if (firstname.isEmpty()) {
                    showInfoDlg("Information", "Please enter the First Name", "OK", "info");
                } else if (lastname.isEmpty()) {
                    showInfoDlg("Information", "Please enter the Last Name", "OK", "info");
                } else if (emailadd.isEmpty()) {
                    showInfoDlg("Information", "Please enter the Email Id", "OK", "info");
                } else if (phoneno.isEmpty()) {
                    showInfoDlg("Information", "Please enter the Mobile Number", "OK", "info");
                } else if (phoneno.length() != 10) {
                    showInfoDlg("Information", "Please enter valid Mobile Number", "OK", "info");
                } else {
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
                    Log.e("json", "" + json);
                    callMobileService(loginCredentials);
                }
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        SharedPreferences prefs = getSharedPreferences("LoginCredentials", MODE_PRIVATE);
        Gson gson = new Gson();
        String json = prefs.getString("LoginCredentials", "");
        Utils.verifyLogInUserMobileInstantResponse = gson.fromJson(json, SingleInstantResponse.class);
    }

    public void callMobileService(SingleInstantParameters loginCredentials) {
        if (Utils.connectivity(EditProfileActivity.this)) {
            final Dialog dialog = new Dialog(EditProfileActivity.this, android.R.style.Theme_Dialog);
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
            RequestBody userId = RequestBody.create(MediaType.parse("text/plain"), loginCredentials.userId);
            RequestBody alternateNumber = RequestBody.create(MediaType.parse("text/plain"), loginCredentials.alternateNumber);

            Call<SingleInstantResponse> call = apiService.saveUpdateUser(userType, firstName, lastName, emailId, mobileNumber, userId, alternateNumber);
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
                        Log.e("LastName", "" + Utils.saveUserMobileInstantResponse.getLastName());
                        Utils.verifyLogInUserMobileInstantResponse = Utils.saveUserMobileInstantResponse;
                        Gson gson = new Gson();
                        String json = gson.toJson(Utils.verifyLogInUserMobileInstantResponse);
                        SharedPreferences.Editor editor = getSharedPreferences("LoginCredentials", MODE_PRIVATE).edit();
                        editor.putString("LoginCredentials", json);
                        editor.commit();
                        showInfoDlg("Success..", "Successfully updated.", "OK", "success");
                    } else {
                        try {
                            JSONObject jObjError = new JSONObject(response.errorBody().string());
                            if(response.code() == Utils.NETWORKERR_SESSIONTOKEN_EXPIRED){


                                // JSONObject jObjError = new JSONObject(response.errorBody().string());
                                // Toast.makeText(LoginActivity.this, jObjError.toString(), Toast.LENGTH_LONG).show();
                                //if(jObjError.getString("message"))
                                showInfoDlg(getString(R.string.error), "" + jObjError.getString("message"), getString(R.string.btn_ok), "logout");

                            }else {
                                showInfoDlg("Error..", "" + jObjError.getString("message"), "OK", "error");
                            }
                        } catch (Exception e) {
                            Toast.makeText(EditProfileActivity.this, "Either there is no network connectivity or server is not available.. Please try again later..", Toast.LENGTH_LONG).show();
                        }
                    }
                }

                @Override
                public void onFailure(Call<SingleInstantResponse> call, Throwable t) {
                    // Log error here since request failed
                    Log.e("onFailure", t.toString());
                    dialog.dismiss();

                    showInfoDlg(getString(R.string.error), "" + getString(R.string.errmsg_sessionexpired), getString(R.string.btn_ok), "logout");

                    //Toast.makeText(EditProfileActivity.this, "Either there is no network connectivity or server is not available.. Please try again later..", Toast.LENGTH_LONG).show();
                }
            });
        } else {
            Toast.makeText(EditProfileActivity.this, "Either there is no network connectivity or server is not available.. Please try again later..", Toast.LENGTH_LONG).show();
        }
    }

    Dialog dialog;

    private void showInfoDlg(String title, String content, String btnText, final String navType) {
        dialog = new Dialog(EditProfileActivity.this, android.R.style.Theme_Dialog);
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
        if (navType.equalsIgnoreCase("info")  || navType.equalsIgnoreCase("logout") || navType.equalsIgnoreCase("error") || navType.equalsIgnoreCase("server")) {
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
                    Intent ide = new Intent(EditProfileActivity.this, NavigationMenuActivity.class);
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

                if(navType.equalsIgnoreCase("logout")){
                    SharedPreferences.Editor editor = getSharedPreferences("LoginCredentials", MODE_PRIVATE).edit();
                    editor.remove("phoneNumber");
                    editor.remove("password");
                    editor.commit();
                    SharedPreferences.Editor deditor = getSharedPreferences("DisclaimerCredentials", MODE_PRIVATE).edit();
                    deditor.putString("disclaimer", "");
                    deditor.commit();
                    Intent ide = new Intent(EditProfileActivity.this, LoginActivity.class);
                    ide.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(ide);
                    // finish();
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

    @Override
    public void onBackPressed() {
        finish();
    }

}