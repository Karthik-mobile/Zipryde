package com.trivectadigital.ziprydeuserapp;

import android.app.Dialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
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
import com.trivectadigital.ziprydeuserapp.apis.ZiprydeApiClient;
import com.trivectadigital.ziprydeuserapp.apis.ZiprydeApiInterface;
import com.trivectadigital.ziprydeuserapp.assist.MessageReceivedEvent;
import com.trivectadigital.ziprydeuserapp.assist.Utils;
import com.trivectadigital.ziprydeuserapp.modelget.SingleInstantResponse;
import com.trivectadigital.ziprydeuserapp.modelpost.SingleInstantParameters;

import org.json.JSONObject;

import de.greenrobot.event.EventBus;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CashDisplyActivity extends AppCompatActivity {

    Button cashBtn, paypalBtn, cashAppBtn, creditBtn;

    String bookingId = "";
    String suggestedPrice = "";
    String offeredPrice = "";
    String distanceInMiles = "";
    String fromaddress = "";
    String toaddress = "";

    ZiprydeApiInterface apiService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cash_disply);

        SharedPreferences prefs = getSharedPreferences("LoginCredentials", MODE_PRIVATE);
        Gson gson = new Gson();
        String json = prefs.getString("LoginCredentials", "");
        Utils.verifyLogInUserMobileInstantResponse = gson.fromJson(json, SingleInstantResponse.class);

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
        titleText.setText("ZipRyde");
        ImageView backImg = (ImageView) mCustomView.findViewById(R.id.backImg);
        backImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        TextView mainTotalText = (TextView) findViewById(R.id.mainTotalText);
        TextView distanceText = (TextView) findViewById(R.id.distanceText);
        TextView bookingStarting = (TextView) findViewById(R.id.bookingStarting);
        TextView bookingEnding = (TextView) findViewById(R.id.bookingEnding);
        TextView fareText = (TextView) findViewById(R.id.fareText);

        Intent intent = getIntent();
        if (intent != null) {
            bookingId = intent.getStringExtra("bookingId");
            suggestedPrice = intent.getStringExtra("suggestedPrice");
            offeredPrice = intent.getStringExtra("offeredPrice");
            distanceInMiles = intent.getStringExtra("distanceInMiles");
            fromaddress = intent.getStringExtra("fromaddress");
            toaddress = intent.getStringExtra("toaddress");
            mainTotalText.setText(getString(R.string.currencysymbol)+" " + offeredPrice);
            distanceText.setText(distanceInMiles + getString(R.string.distanceunit));
            bookingStarting.setText(fromaddress);
            bookingEnding.setText(toaddress);
            fareText.setText(getString(R.string.currencysymbol)+" " + offeredPrice);
        }

        cashBtn = (Button) findViewById(R.id.cashBtn);
        paypalBtn = (Button) findViewById(R.id.paypalBtn);
        cashAppBtn = (Button) findViewById(R.id.cashAppBtn);
        creditBtn = (Button) findViewById(R.id.creditBtn);
        cashBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.e("bookingId", "" + bookingId);
                Log.e("offeredPrice", "" + offeredPrice);
                cashBtn.setAlpha(1f);
                paypalBtn.setAlpha(0.5f);
                cashAppBtn.setAlpha(0.5f);
                creditBtn.setAlpha(0.5f);

                showInfoDlg(getString(R.string.information),getString(R.string.usermsg_cashmsg),getString(R.string.btn_ok),"info");
            }
        });

        paypalBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.e("bookingId", "" + bookingId);
                Log.e("offeredPrice", "" + offeredPrice);
                cashBtn.setAlpha(0.5f);
                paypalBtn.setAlpha(1f);
                cashAppBtn.setAlpha(0.5f);
                creditBtn.setAlpha(0.5f);
                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://www.paypal.com/us/home"));
                startActivity(browserIntent);
            }
        });

        cashAppBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.e("bookingId", "" + bookingId);
                Log.e("offeredPrice", "" + offeredPrice);
                cashBtn.setAlpha(0.5f);
                paypalBtn.setAlpha(0.5f);
                cashAppBtn.setAlpha(1f);
                creditBtn.setAlpha(0.5f);
                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://cash.me/app/WTXRWNB"));
                startActivity(browserIntent);
            }
        });

        creditBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.e("bookingId", "" + bookingId);
                Log.e("offeredPrice", "" + offeredPrice);
                cashBtn.setAlpha(0.5f);
                paypalBtn.setAlpha(0.5f);
                cashAppBtn.setAlpha(0.5f);
                creditBtn.setAlpha(1f);

                showInfoDlg(getString(R.string.information),getString(R.string.usermsg_ccmsg),getString(R.string.btn_ok),"info");
            }
        });
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
        Log.e("Thread message", "" + messageReceivedEvent.message);
        Log.e("Thread title", "" + messageReceivedEvent.title);
        Log.e("PUSH_NOTIFICATION", "PUSH_NOTIFICATION");
        SharedPreferences.Editor editor = getSharedPreferences("BookingCredentials", MODE_PRIVATE).edit();
        editor.putString("bookingId", "");
        editor.commit();

        //Toast.makeText(this, messageReceivedEvent.message, Toast.LENGTH_SHORT).show();
        showInfoDlg(getString(R.string.information), getString(R.string.usermsg_tripcompletedreminder), getString(R.string.btn_ok), "back");

    }

    @Override
    public void onBackPressed() {

        //Show a pop-up alert for their belongings

        showInfoDlg(getString(R.string.information), getString(R.string.usermsg_tripcompletedreminder), getString(R.string.btn_ok), "back");


    }

    @Override
    protected void onResume() {
        super.onResume();
        SharedPreferences prefs = getSharedPreferences("LoginCredentials", MODE_PRIVATE);
        Gson gson = new Gson();
        String json = prefs.getString("LoginCredentials", "");
        Utils.verifyLogInUserMobileInstantResponse = gson.fromJson(json, SingleInstantResponse.class);
        Intent intent = getIntent();
        if (intent != null) {
            bookingId = intent.getStringExtra("bookingId");
            SingleInstantParameters loginCredentials = new SingleInstantParameters();
            loginCredentials.bookingId = "" + bookingId;
            getBookingByBookingId(loginCredentials);
        }
    }

    public void getBookingByBookingId(SingleInstantParameters loginCredentials) {
        if (Utils.connectivity(CashDisplyActivity.this)) {
            final Dialog dialog = new Dialog(CashDisplyActivity.this, android.R.style.Theme_Dialog);
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            dialog.setContentView(R.layout.loadingimage_layout);
            dialog.setCanceledOnTouchOutside(false);
            dialog.setCancelable(false);
            dialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
            dialog.getWindow().setLayout(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
            dialog.show();

            Call<SingleInstantResponse> call = apiService.getBookingByBookingId(Utils.verifyLogInUserMobileInstantResponse.getAccessToken(),loginCredentials);
            call.enqueue(new Callback<SingleInstantResponse>() {
                @Override
                public void onResponse(Call<SingleInstantResponse> call, Response<SingleInstantResponse> response) {
                    int statusCode = response.code();
                    Log.e("statusCode", "" + statusCode);
                    Log.e("response.body", "" + response.body());
                    Log.e("response.errorBody", "" + response.errorBody());
                    Log.e("response.isSuccessful", "" + response.isSuccessful());
                    if (response.isSuccessful()) {
                        dialog.dismiss();
                        Utils.requestBookingResponse = response.body();
                        String bookingStatusFinal = Utils.requestBookingResponse.getBookingStatusCode();
                        if(bookingStatusFinal.equals("PAID") ){
                            SharedPreferences.Editor editor = getSharedPreferences("BookingCredentials", MODE_PRIVATE).edit();
                            editor.putString("bookingId", "");
                            editor.commit();
                            Intent ide = new Intent(CashDisplyActivity.this, NavigationMenuActivity.class);
                            ide.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                            startActivity(ide);
                            finish();
                        }
                    } else {
                        dialog.dismiss();
                        try {
                            JSONObject jObjError = new JSONObject(response.errorBody().string());
                            if(response.code() == Utils.NETWORKERR_SESSIONTOKEN_EXPIRED){


                                // JSONObject jObjError = new JSONObject(response.errorBody().string());
                                // Toast.makeText(LoginActivity.this, jObjError.toString(), Toast.LENGTH_LONG).show();
                                //if(jObjError.getString("message"))
                                showInfoDlg(getString(R.string.error), "" + jObjError.getString("message"), getString(R.string.btn_ok), "logout");

                            }else {
                                showInfoDlg("Error..", "" + jObjError.getString("message"), "Ok", "error");
                            }
                        } catch (Exception e) {
                            Toast.makeText(CashDisplyActivity.this, getString(R.string.errmsg_network_noconnection), Toast.LENGTH_LONG).show();
                        }
                    }
                }

                @Override
                public void onFailure(Call<SingleInstantResponse> call, Throwable t) {
                    // Log error here since request failed
                    Log.e("onFailure", t.toString());
                    dialog.dismiss();
                    showInfoDlg(getString(R.string.error), "" + getString(R.string.errmsg_sessionexpired), getString(R.string.btn_ok), "logout");
                   // Toast.makeText(CashDisplyActivity.this, getString(R.string.errmsg_network_noconnection), Toast.LENGTH_LONG).show();
                }
            });
        } else {
            Toast.makeText(CashDisplyActivity.this, getString(R.string.errmsg_network_noconnection), Toast.LENGTH_LONG).show();
        }
    }

    Dialog dialog;
    private void showInfoDlg(String title, String content, String btnText, final String navType) {
        dialog = new Dialog(CashDisplyActivity.this, android.R.style.Theme_Dialog);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.infodialog_layout);
        //dialog.setCanceledOnTouchOutside(true);

        ImageView headerIcon = (ImageView) dialog.findViewById(R.id.headerIcon);
        if (navType.equalsIgnoreCase("server")) {
            headerIcon.setImageResource(R.drawable.erroricon);
        }

        Button positiveBtn = (Button) dialog.findViewById(R.id.positiveBtn);
        positiveBtn.setText("" + btnText);

        Button newnegativeBtn = (Button) dialog.findViewById(R.id.newnegativeBtn);
        if (navType.equalsIgnoreCase("info") || navType.equalsIgnoreCase("server") || navType.equalsIgnoreCase("logout") || navType.equalsIgnoreCase("back")) {
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
                    Intent ide = new Intent(CashDisplyActivity.this, LoginActivity.class);
                    ide.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(ide);
                    // finish();
                }else if(navType.equalsIgnoreCase("back")){

                    Intent ide = new Intent(CashDisplyActivity.this, NavigationMenuActivity.class);
                    ide.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(ide);
                    //finish();
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
