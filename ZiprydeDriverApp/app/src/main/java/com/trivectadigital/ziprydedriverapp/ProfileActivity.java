package com.trivectadigital.ziprydedriverapp;

import android.app.Dialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.trivectadigital.ziprydedriverapp.apis.ZiprydeApiClient;
import com.trivectadigital.ziprydedriverapp.apis.ZiprydeApiInterface;
import com.trivectadigital.ziprydedriverapp.assist.MessageReceivedEvent;
import com.trivectadigital.ziprydedriverapp.assist.Utils;
import com.trivectadigital.ziprydedriverapp.modelget.SingleInstantResponse;
import com.trivectadigital.ziprydedriverapp.modelpost.DriverInfo;
import com.trivectadigital.ziprydedriverapp.modelpost.SingleInstantParameters;

import org.json.JSONObject;

import de.greenrobot.event.EventBus;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ProfileActivity extends AppCompatActivity {

    TextView fname, lname, emailid, mobileno,dlno,dlIssueDate,dlExpiyDate,cabMake,cabModel,cabLiscensePlateNo,cabInsuranceNo,cabInsuranceIssueDate,cabInsuranceExpiryDate;

    ImageView driverProfileImage, vehicleImage;
    Button edit_btn;
    ZiprydeApiInterface apiService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        apiService = ZiprydeApiClient.getClient().create(ZiprydeApiInterface.class);

        Toolbar toolbar = (Toolbar) findViewById(R.id.profiletoolbar);
        setSupportActionBar(toolbar);

        LayoutInflater mInflater = LayoutInflater.from(this);
        View mCustomView = mInflater.inflate(R.layout.actionbar_titlealone, null);
        toolbar.setContentInsetsAbsolute(0, 0);
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.MATCH_PARENT);
        toolbar.addView(mCustomView, layoutParams);
        TextView titleText = (TextView) mCustomView.findViewById(R.id.titleText);


        titleText.setText("ZipDriver Information");


//        ImageView backImg = (ImageView) mCustomView.findViewById(R.id.backImg);
//        backImg.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                onBackPressed();
//            }
//        });


        //Assing UI to variable

        fname = (TextView) findViewById(R.id.driverFN);
        lname = (TextView) findViewById(R.id.driverLN);
        emailid = (TextView) findViewById(R.id.driverEmailId);

        mobileno = (TextView) findViewById(R.id.driverMobileNo);

        dlno = (TextView) findViewById(R.id.driverDLno);
        dlIssueDate = (TextView) findViewById(R.id.driverDLIssueDate);
        dlExpiyDate = (TextView) findViewById(R.id.driverDLExpiryDate);

        cabMake = (TextView) findViewById(R.id.driverCabMake);
        cabModel = (TextView) findViewById(R.id.driverCabModel);
        cabLiscensePlateNo = (TextView) findViewById(R.id.driverCabLicensePlateNo);

        cabInsuranceNo = (TextView) findViewById(R.id.driverCabInsuranceNo);
        cabInsuranceExpiryDate = (TextView) findViewById(R.id.driverCabInsuranceValidUntil);

        driverProfileImage = (ImageView) findViewById(R.id.driverImage);
        vehicleImage = (ImageView) findViewById(R.id.cabImage);



        //fname = (TextView) findViewById(R.id.driverFN);

    }

    @Override
    public void onBackPressed() {

        finish();

    }

    @Override
    protected void onResume() {
        super.onResume();
        SharedPreferences prefs = getSharedPreferences("LoginCredentials", MODE_PRIVATE);
        Gson gson = new Gson();
        String json = prefs.getString("LoginCredentials", "");
        Utils.verifyLogInUserMobileInstantResponse = gson.fromJson(json, SingleInstantResponse.class);

        onInitView();
    }

    private void onInitView(){

        //fname = (TextView) findViewById(R.id.driverFN);
//        fname.setText("" + Utils.verifyLogInUserMobileInstantResponse.getFirstName());
//
//        Log.e("LastName", "" + Utils.verifyLogInUserMobileInstantResponse.getLastName());
//        //lname = (TextView) findViewById(R.id.driverLN);
//        lname.setText("" + Utils.verifyLogInUserMobileInstantResponse.getLastName());
//
//        //emailid = (TextView) findViewById(R.id.driverEmailId);
//        emailid.setText("" + Utils.verifyLogInUserMobileInstantResponse.getEmailId());
//
//       // mobileno = (TextView) findViewById(R.id.driverMobileNo);
//        mobileno.setText("" + Utils.verifyLogInUserMobileInstantResponse.getMobileNumber());
        SingleInstantParameters loginCredentials = new SingleInstantParameters();
        loginCredentials.userId = ""+Utils.verifyLogInUserMobileInstantResponse.getUserId();

        getDriverInfo(loginCredentials);


    }

    public void getDriverInfo(SingleInstantParameters loginCredentials) {
        if (Utils.connectivity(ProfileActivity.this)) {
            final Dialog loadingDialog = new Dialog(ProfileActivity.this, android.R.style.Theme_Dialog);
            loadingDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            loadingDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            loadingDialog.setContentView(R.layout.loadingimage_layout);
            loadingDialog.setCanceledOnTouchOutside(false);
            loadingDialog.setCancelable(false);
            loadingDialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
            loadingDialog.getWindow().setLayout(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
            loadingDialog.show();

            Call<DriverInfo> call = apiService.getUserByUserId(Utils.verifyLogInUserMobileInstantResponse.getAccessToken(),loginCredentials);
            call.enqueue(new Callback<DriverInfo>() {
                @Override
                public void onResponse(Call<DriverInfo> call, Response<DriverInfo> response) {
                    //int statusCode = response.code();
//                    Log.e("statusCode", "" + statusCode);
//                    Log.e("response.body", "" + response.body());
//                    Log.e("response.errorBody", "" + response.errorBody());
//                    Log.e("response.isSuccessful", "" + response.isSuccessful());
                    loadingDialog.dismiss();
                    if (response.isSuccessful()) {
                         DriverInfo driverProfile = response.body();

                        fname.setText(driverProfile.getFirstName());
                        lname.setText(driverProfile.getLastName());
                        emailid.setText(driverProfile.getEmailId());
                        mobileno.setText(driverProfile.getMobileNumber());

                        dlno.setText(driverProfile.getLicenseNo());
                        dlIssueDate.setText(driverProfile.getLicenseIssuedOn());
                        dlExpiyDate.setText(driverProfile.getLicenseValidUntil());

                        cabMake.setText(driverProfile.getMake());
                        cabModel.setText(driverProfile.getModel());

                        cabLiscensePlateNo.setText(driverProfile.getLicenseNo());
                        cabInsuranceNo.setText(driverProfile.getInsuranceNo());
                        cabInsuranceExpiryDate.setText(driverProfile.getInsuranceValidUntil());


                        String driverImage = "" + driverProfile.getUserImage();
                        if (driverImage != null) {
                            if (!driverImage.equalsIgnoreCase("null")) {
                                byte[] decodedString = Base64.decode(driverImage, Base64.DEFAULT);
                                Bitmap decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
                                driverProfileImage.setImageBitmap(decodedByte);
                            }
                        }

                        String cabImage = "" + driverProfile.getCabImage();
                        if (cabImage != null) {
                            if (!cabImage.equalsIgnoreCase("null")) {
                                byte[] decodedString = Base64.decode(cabImage, Base64.DEFAULT);
                                Bitmap decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
                                vehicleImage.setImageBitmap(decodedByte);
                            }
                        }


                    } else {
                        try {
                            JSONObject jObjError = new JSONObject(response.errorBody().string());




                                // Toast.makeText(LoginActivity.this, jObjError.toString(), Toast.LENGTH_LONG).show();
                                //if(jObjError.getString("message"))
                                showInfoDlg(getString(R.string.error), "" + jObjError.getString("message"), getString(R.string.btn_ok), "logout");

                        } catch (Exception e) {
                            Toast.makeText(ProfileActivity.this, "Either there is no network connectivity or server is not available.. Please try again later..", Toast.LENGTH_LONG).show();
                        }
                    }
                }

                @Override
                public void onFailure(Call<DriverInfo> call, Throwable t) {
                    // Log error here since request failed
                    Log.e("onFailure", t.toString());
                    loadingDialog.dismiss();
                    Toast.makeText(ProfileActivity.this, "Either there is no network connectivity or server is not available.. Please try again later..", Toast.LENGTH_LONG).show();
                }
            });
        } else {
            Toast.makeText(ProfileActivity.this, "Either there is no network connectivity or server is not available.. Please try again later..", Toast.LENGTH_LONG).show();
        }
    }

    Dialog dialog;

    private void showInfoDlg(String title, String content, String btnText, final String navType) {
        dialog = new Dialog(ProfileActivity.this, android.R.style.Theme_Dialog);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.infodialog_layout);
        //dialog.setCanceledOnTouchOutside(true);

        ImageView headerIcon = (ImageView) dialog.findViewById(R.id.headerIcon);
        if (navType.equalsIgnoreCase("server") || navType.equalsIgnoreCase("error") || navType.equalsIgnoreCase("logout")) {
            headerIcon.setImageResource(R.drawable.erroricon);
        }

        Button positiveBtn = (Button) dialog.findViewById(R.id.positiveBtn);
        positiveBtn.setText("" + btnText);

        Button newnegativeBtn = (Button) dialog.findViewById(R.id.newnegativeBtn);
        if (navType.equalsIgnoreCase("info") || navType.equalsIgnoreCase("server") || navType.equalsIgnoreCase("error") || navType.equalsIgnoreCase("logout"))   {
            newnegativeBtn.setVisibility(View.GONE);
        }

        if(navType.equalsIgnoreCase("forcelogin")) {
            newnegativeBtn.setText(getString(R.string.btn_no));
        }

        if (navType.equalsIgnoreCase("success")) {
            headerIcon.setImageResource(R.drawable.successicon);
            positiveBtn.setVisibility(View.GONE);
            newnegativeBtn.setVisibility(View.GONE);

                    dialog.dismiss();



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
                    Intent ide = new Intent(ProfileActivity.this, LoginActivity.class);
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
    public void onStart() {
        super.onStart();
        EventBus.getDefault().register(this);
    }

    @Override
    protected void onStop() {
        EventBus.getDefault().unregister(this);
        super.onStop();
    }

    public void onEventMainThread(MessageReceivedEvent messageReceivedEvent) {
        Log.e("onEventMainThread", "" + messageReceivedEvent.message);
        Log.e("PUSH_NOTIFICATION", "PUSH_NOTIFICATION");
        Intent ide = new Intent(ProfileActivity.this, RideActivity.class);
        ide.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(ide);
    }

}
