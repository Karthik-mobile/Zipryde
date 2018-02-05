package com.trivectadigital.ziprydeuserapp;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Point;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.SystemClock;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Base64;
import android.util.Log;
import android.view.Display;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.LinearInterpolator;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.Projection;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.gson.Gson;
import com.squareup.picasso.Picasso;
import com.trivectadigital.ziprydeuserapp.apis.ZiprydeApiClient;
import com.trivectadigital.ziprydeuserapp.apis.ZiprydeApiInterface;
import com.trivectadigital.ziprydeuserapp.assist.CircleImageView;
import com.trivectadigital.ziprydeuserapp.assist.DataParser;
import com.trivectadigital.ziprydeuserapp.assist.MessageReceivedEvent;
import com.trivectadigital.ziprydeuserapp.assist.Utils;
import com.trivectadigital.ziprydeuserapp.modelget.ListOfBooking;
import com.trivectadigital.ziprydeuserapp.modelget.SingleInstantResponse;
import com.trivectadigital.ziprydeuserapp.modelpost.SingleInstantParameters;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

import de.greenrobot.event.EventBus;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class DriverInfoBookingActivity extends AppCompatActivity implements OnMapReadyCallback {

    private GoogleMap mMap;

    int position = -1;

    TextView driverNameText, cabTypeText, cabLicenseNoText,fromAddressText, toAddressText, bookingStatus, callDriverText, driverArrivingTime,crnNo;
    ListOfBooking listOfBooking;
    ZiprydeApiInterface apiService;

    CircleImageView user_view;

    LinearLayout cancelBookingLay, getDirections, reportLay;

    RelativeLayout mapLayout, staticImgLayout;

    String bookingStatusFinal = "";
    String bookingIdFinal = "";
    String bookingDriverMobileNumber = "";

    ImageView callDriverImg,staticMapImg;

    PopupWindow pushNotificationPopwindow;

    public static final int ACTIONCALL = 0x1;
    Polyline polyline;

    int isHome = 0;

    public  int isDriverImage;

    public Bitmap driver = null,cab = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_driver_info_booking);

        apiService = ZiprydeApiClient.getClient(Utils.verifyLogInUserMobileInstantResponse.getAccessToken()).create(ZiprydeApiInterface.class);

        driverNameText = (TextView) findViewById(R.id.driverNameText);
        cabTypeText = (TextView) findViewById(R.id.cabTypeText);
        cabLicenseNoText = (TextView) findViewById(R.id.cabLicenseNo);
        fromAddressText = (TextView) findViewById(R.id.fromAddressText);
        toAddressText = (TextView) findViewById(R.id.toAddressText);
        bookingStatus = (TextView) findViewById(R.id.bookingStatus);
        callDriverText = (TextView) findViewById(R.id.callDriverText);
        driverArrivingTime = (TextView) findViewById(R.id.driverArrivingTime);
        user_view = (CircleImageView) findViewById(R.id.user_view);

        callDriverImg = (ImageView) findViewById(R.id.callDriverImg);

        cancelBookingLay = (LinearLayout) findViewById(R.id.cancelBookingLay);
        getDirections = (LinearLayout) findViewById(R.id.getDirections);
        reportLay = (LinearLayout) findViewById(R.id.reportLay);

        mapLayout = (RelativeLayout) findViewById(R.id.mapLayout);
        staticImgLayout = (RelativeLayout) findViewById(R.id.staticMapImgLayout);

        crnNo = (TextView) findViewById(R.id.crnNo);



        mapLayout.setVisibility(View.VISIBLE);
        staticImgLayout.setVisibility(View.GONE);


        staticMapImg = (ImageView) findViewById(R.id.staticMapImgView);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        Intent intent = getIntent();


        if(intent.hasExtra("back")){
            isHome = intent.getIntExtra("back",0);
            mapLayout.setVisibility(View.GONE);
        }


        cancelBookingLay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (handler != null && finalizer != null) {
                    handler.removeCallbacks(finalizer);
                }

                SingleInstantParameters loginCredentials = new SingleInstantParameters();
                loginCredentials.bookingId = bookingIdFinal;
                loginCredentials.bookingStatus = "CANCELLED";
                updateBookingStatus(loginCredentials);
            }
        });

        callDriverImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (ContextCompat.checkSelfPermission(DriverInfoBookingActivity.this, android.Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
                    // Should we show an explanation?
                    if (ActivityCompat.shouldShowRequestPermissionRationale(DriverInfoBookingActivity.this, android.Manifest.permission.CALL_PHONE)) {
                        //This is called if user has denied the permission before
                        //In this case I am just asking the permission again
                        ActivityCompat.requestPermissions(DriverInfoBookingActivity.this, new String[]{android.Manifest.permission.CALL_PHONE}, ACTIONCALL);

                    } else {
                        ActivityCompat.requestPermissions(DriverInfoBookingActivity.this, new String[]{android.Manifest.permission.CALL_PHONE}, ACTIONCALL);
                    }
                } else {
                    Log.e("DriverMobileNumber", "" + bookingDriverMobileNumber);
                    if (bookingDriverMobileNumber != null) {
                        if (!bookingDriverMobileNumber.equalsIgnoreCase("null")) {
                            Intent intent = new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + bookingDriverMobileNumber));
                            startActivity(intent);
                        }
                    }
                }

            }
        });

        getDirections.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (Utils.connectivity(DriverInfoBookingActivity.this)) {
                    if (polyline != null) {
                        polyline.remove();
                    }

                    //Move the camera to the origin.
                    mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(origin, 18));
                    mMap.animateCamera(CameraUpdateFactory.zoomIn());
                    mMap.animateCamera(CameraUpdateFactory.zoomTo(18), 2000, null);

                    if (origin != null && dest != null) {
                        String url = getUrl(origin, dest);
                        Log.d("url", "" + url);
                        FetchUrl FetchUrl = new FetchUrl();
                        FetchUrl.execute(url);
                    }
                } else {
                    Toast.makeText(DriverInfoBookingActivity.this, "Either there is no network connectivity or server is not available.. Please try again later..", Toast.LENGTH_LONG).show();
                }
            }
        });

        reportLay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                inputDialog = new Dialog(DriverInfoBookingActivity.this, android.R.style.Theme_Dialog);
                inputDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                inputDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                inputDialog.setContentView(R.layout.inputdialog_layout);
                inputDialog.setCanceledOnTouchOutside(true);

                final EditText nameEdit = (EditText) inputDialog.findViewById(R.id.nameEdit);
                Button addNameBtn = (Button) inputDialog.findViewById(R.id.addNameBtn);

                addNameBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        String userName = nameEdit.getText().toString();
                        if (!userName.isEmpty()) {
                            SingleInstantParameters loginCredentials = new SingleInstantParameters();
                            loginCredentials.bookingId = "" + bookingIdFinal;
                            loginCredentials.comments = "" + userName;
                            Gson gson = new Gson();
                            String json = gson.toJson(loginCredentials);
                            Log.e("json", "" + json);
                            saveLostItem(loginCredentials);
                        }
                    }
                });

                inputDialog.setCanceledOnTouchOutside(false);
                inputDialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
                inputDialog.getWindow().setLayout(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                inputDialog.show();
            }
        });

       // staticImgLayout.setVisibility(View.GONE);

        if (intent.hasExtra("position")) {
            Log.e("intent 111", "intent : " + intent.getExtras());
            position = intent.getIntExtra("position", -1);
            initiateViewToDisplay();
        } else if (intent.hasExtra("bookingId")) {
            Log.e("intent 222", "intent : " + intent.getExtras());
            String id = intent.getStringExtra("bookingId");
            Log.e("id 111", "" + id);
            SingleInstantParameters loginCredentials = new SingleInstantParameters();
            loginCredentials.bookingId = "" + id;
            Gson gson = new Gson();
            String json = gson.toJson(loginCredentials);
            Log.e("json", "" + json);
            getBookingByBookingId(loginCredentials, 1);
        } else {
            Log.e("intent 333", "intent : " + intent.getExtras());
            if (intent.hasExtra("from")) {
                initiateViewToDisplay();
            }
        }

        isDriverImage = 0;

        //Toggle the driver image with car image and vice versa
        user_view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(isDriverImage == 0){
                    if(cab != null) {
                        user_view.setImageBitmap(cab);
                        isDriverImage = 1;
                    }
                }else{
                    if(driver != null) {
                        user_view.setImageBitmap(driver);
                        isDriverImage = 0;
                    }
                }
            }
            });


    }



    private void saveLostItem(SingleInstantParameters loginCredentials) {
        if (Utils.connectivity(DriverInfoBookingActivity.this)) {
            final Dialog dialog = new Dialog(DriverInfoBookingActivity.this, android.R.style.Theme_Dialog);
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            dialog.setContentView(R.layout.loadingimage_layout);
            dialog.setCanceledOnTouchOutside(false);
            dialog.setCancelable(false);
            dialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
            dialog.getWindow().setLayout(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
            dialog.show();

            Call<SingleInstantResponse> call = apiService.saveLostItem(Utils.verifyLogInUserMobileInstantResponse.getAccessToken(),loginCredentials);
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
                        inputDialog.dismiss();
                        showInfoDlg("Success..", "Your query has been successfully submitted.", "Ok", "success");
                    } else {
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
                            Toast.makeText(DriverInfoBookingActivity.this, "Either there is no network connectivity or server is not available.. Please try again later..", Toast.LENGTH_LONG).show();
                        }
                    }
                }

                @Override
                public void onFailure(Call<SingleInstantResponse> call, Throwable t) {
                    // Log error here since request failed
                    Log.e("onFailure", t.toString());
                    dialog.dismiss();
                    showInfoDlg(getString(R.string.error), "" + getString(R.string.errmsg_sessionexpired), getString(R.string.btn_ok), "logout");
                    //Toast.makeText(DriverInfoBookingActivity.this, "Either there is no network connectivity or server is not available.. Please try again later..", Toast.LENGTH_LONG).show();
                }
            });
        } else {
            Toast.makeText(DriverInfoBookingActivity.this, "Either there is no network connectivity or server is not available.. Please try again later..", Toast.LENGTH_LONG).show();
        }
    }

    Dialog inputDialog;
    LatLng origin = Utils.startingLatLan;
    LatLng dest = Utils.endingLatLan;

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (ActivityCompat.checkSelfPermission(DriverInfoBookingActivity.this, permissions[0]) == PackageManager.PERMISSION_GRANTED) {
            switch (requestCode) {
                case ACTIONCALL:
                    Log.e("DriverMobileNumber", "" + bookingDriverMobileNumber);
                    if (bookingDriverMobileNumber != null) {
                        if (!bookingDriverMobileNumber.equalsIgnoreCase("null")) {
                            Intent intent = new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + bookingDriverMobileNumber));
                            startActivity(intent);
                        }
                    }
                    break;
            }
        } else {
            Toast.makeText(DriverInfoBookingActivity.this, "Permission denied", Toast.LENGTH_SHORT).show();
        }
    }

    public void initiateViewToDisplay() {
        if (position != -1) {
            listOfBooking = Utils.getBookingByUserIdResponse.get(position);
            bookingIdFinal = listOfBooking.getBookingId();
            driverNameText.setText(listOfBooking.getDriverName());
            cabTypeText.setText(listOfBooking.getMake()+" "+listOfBooking.getModel());
            cabLicenseNoText.setText(listOfBooking.getLicensePlateNumber());
            fromAddressText.setText(listOfBooking.getFrom());
            toAddressText.setText(listOfBooking.getTo());
            bookingStatus.setText(listOfBooking.getBookingStatus());
            bookingStatusFinal = listOfBooking.getBookingStatusCode();
            bookingDriverMobileNumber = listOfBooking.getDriverMobileNumber();
            crnNo.setText(listOfBooking.getCrnNumber());
            Log.e("DriverId", listOfBooking.getDriverId());
            Log.e("driverImage", "driverImage : " + listOfBooking.getDriverImage());
            String driverImage = "" + listOfBooking.getDriverImage();
            if (driverImage != null) {
                if (!driverImage.equalsIgnoreCase("null")) {
                    byte[] decodedString = Base64.decode(driverImage, Base64.DEFAULT);
                    Bitmap decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
                    driver = decodedByte;
                    user_view.setImageBitmap(decodedByte);
                }
            }

            String cabImage = "" + listOfBooking.getCabImage();
            if (cabImage != null) {
                if (!cabImage.equalsIgnoreCase("null")) {
                    byte[] decodedString = Base64.decode(driverImage, Base64.DEFAULT);
                    Bitmap decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
                    cab = decodedByte;
                    //user_view.setImageBitmap(decodedByte);
                }
            }

            if (bookingStatusFinal != null) {
                if (bookingStatusFinal.equals("SCHEDULED") || bookingStatusFinal.equals("ACCEPTED")) {
                    cancelBookingLay.setVisibility(View.VISIBLE);
                    callDriverImg.setVisibility(View.VISIBLE);
                    callDriverText.setVisibility(View.VISIBLE);
                    driverArrivingTime.setVisibility(View.VISIBLE);
                } else {
                    cancelBookingLay.setVisibility(View.GONE);
                    callDriverImg.setVisibility(View.GONE);
                    callDriverText.setVisibility(View.GONE);
                    driverArrivingTime.setVisibility(View.GONE);
                }
            } else {
                cancelBookingLay.setVisibility(View.GONE);
                callDriverImg.setVisibility(View.GONE);
                callDriverText.setVisibility(View.GONE);
                driverArrivingTime.setVisibility(View.GONE);
            }

            if (!bookingStatusFinal.equals("COMPLETED") && !bookingStatusFinal.equals("PAID")) {
                SingleInstantParameters loginCredentials = new SingleInstantParameters();
                loginCredentials.userId = "" + listOfBooking.getDriverId();
                getGeoLocationByDriverId(loginCredentials);
            }

            if (bookingStatusFinal.equals("PAID")) {
                reportLay.setVisibility(View.VISIBLE);
               // staticImgLayout.setVisibility(View.VISIBLE);
                mapLayout.setVisibility(View.GONE);
            } else {
                reportLay.setVisibility(View.GONE);
               // staticImgLayout.setVisibility(View.GONE);
                mapLayout.setVisibility(View.VISIBLE);
            }


            if (bookingStatusFinal.equals("COMPLETED")) {
                Intent ide = new Intent(DriverInfoBookingActivity.this, CashDisplyActivity.class);
                ide.putExtra("bookingId", "" + bookingIdFinal);
                ide.putExtra("suggestedPrice", "" + listOfBooking.getSuggestedPrice());
                ide.putExtra("offeredPrice", "" + listOfBooking.getOfferedPrice());
                ide.putExtra("distanceInMiles", "" + listOfBooking.getGeoLocationResponse().getDistanceInMiles());
                ide.putExtra("fromaddress", "" + listOfBooking.getFrom());
                ide.putExtra("toaddress", "" + listOfBooking.getTo());
                ide.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(ide);
                //finish();
            }
        } else {
            bookingIdFinal = Utils.requestBookingResponse.getBookingId();
            driverNameText.setText(Utils.requestBookingResponse.getDriverName());
            cabTypeText.setText(Utils.requestBookingResponse.getMake()+" "+Utils.requestBookingResponse.getModel());
            cabLicenseNoText.setText(Utils.requestBookingResponse.getLicensePlateNumber());
            fromAddressText.setText(Utils.requestBookingResponse.getFrom());
            toAddressText.setText(Utils.requestBookingResponse.getTo());
            bookingStatus.setText(Utils.requestBookingResponse.getBookingStatus());
            bookingStatusFinal = Utils.requestBookingResponse.getBookingStatusCode();
            bookingDriverMobileNumber = Utils.requestBookingResponse.getDriverMobileNumber();
            crnNo.setText(Utils.requestBookingResponse.getCrnNumber());
            Log.e("DriverId", Utils.requestBookingResponse.getDriverId());
            Log.e("driverImage", "driverImage : " + Utils.requestBookingResponse.getDriverImage());
            String driverImage = "" + Utils.requestBookingResponse.getDriverImage();
            if (driverImage != null) {
                if (!driverImage.equalsIgnoreCase("null")) {
                    byte[] decodedString = Base64.decode(driverImage, Base64.DEFAULT);
                    Bitmap decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
                    user_view.setImageBitmap(decodedByte);
                }
            }
            String cabImage = "" + Utils.requestBookingResponse.getCabImage();
            if (cabImage != null) {
                if (!cabImage.equalsIgnoreCase("null")) {
                    byte[] decodedString = Base64.decode(driverImage, Base64.DEFAULT);
                    Bitmap decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
                    cab = decodedByte;
                    //user_view.setImageBitmap(decodedByte);
                }
            }
            if (bookingStatusFinal != null) {
                if (bookingStatusFinal.equals("SCHEDULED") || bookingStatusFinal.equals("ACCEPTED") ) {
                    cancelBookingLay.setVisibility(View.VISIBLE);
                    callDriverImg.setVisibility(View.VISIBLE);
                    callDriverText.setVisibility(View.VISIBLE);
                    driverArrivingTime.setVisibility(View.VISIBLE);
                } else {
                    cancelBookingLay.setVisibility(View.GONE);
                    callDriverImg.setVisibility(View.GONE);
                    callDriverText.setVisibility(View.GONE);
                    driverArrivingTime.setVisibility(View.GONE);
                }
            } else {
                cancelBookingLay.setVisibility(View.GONE);
                callDriverImg.setVisibility(View.GONE);
                callDriverText.setVisibility(View.GONE);
                driverArrivingTime.setVisibility(View.GONE);
            }

            if (!bookingStatusFinal.equals("COMPLETED") && !bookingStatusFinal.equals("PAID")) {
                SingleInstantParameters loginCredentials = new SingleInstantParameters();
                loginCredentials.userId = "" + Utils.requestBookingResponse.getDriverId();
                getGeoLocationByDriverId(loginCredentials);
            }

            if (bookingStatusFinal.equals("PAID")) {
                reportLay.setVisibility(View.VISIBLE);
              //  staticImgLayout.setVisibility(View.VISIBLE);
                mapLayout.setVisibility(View.GONE);
            } else {
                reportLay.setVisibility(View.GONE);
               // staticImgLayout.setVisibility(View.GONE);
                mapLayout.setVisibility(View.VISIBLE);
            }

            if (bookingStatusFinal.equals("COMPLETED")) {
                Intent ide = new Intent(DriverInfoBookingActivity.this, CashDisplyActivity.class);
                ide.putExtra("bookingId", "" + bookingIdFinal);
                ide.putExtra("suggestedPrice", "" + Utils.requestBookingResponse.getSuggestedPrice());
                ide.putExtra("offeredPrice", "" + Utils.requestBookingResponse.getOfferedPrice());
                ide.putExtra("distanceInMiles", "" + Utils.requestBookingResponse.getGeoLocationResponse().getDistanceInMiles());
                ide.putExtra("fromaddress", "" + Utils.requestBookingResponse.getFrom());
                ide.putExtra("toaddress", "" + Utils.requestBookingResponse.getTo());
                ide.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(ide);
                //finish();
            }
        }

        if (!bookingStatusFinal.equals("CANCELLED") && !bookingStatusFinal.equals("PAID") && !bookingStatusFinal.equals("COMPLETED") && !bookingStatusFinal.equalsIgnoreCase(("ACCEPTED"))) {
            SharedPreferences.Editor editor = getSharedPreferences("BookingCredentials", MODE_PRIVATE).edit();
            editor.putString("bookingId", bookingIdFinal);
            editor.commit();
        } else {
            SharedPreferences.Editor editor = getSharedPreferences("BookingCredentials", MODE_PRIVATE).edit();
            editor.putString("bookingId", "");
            editor.commit();
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        EventBus.getDefault().register(this);
    }

    @Override
    protected void onStop() {
        EventBus.getDefault().unregister(this);

        if (handler != null && finalizer != null) {
            handler.removeCallbacks(finalizer);
        }

        super.onStop();
    }

    public void onEventMainThread(MessageReceivedEvent messageReceivedEvent) {
        Log.e("Thread message", "" + messageReceivedEvent.message);
        Log.e("Thread title", "" + messageReceivedEvent.title);
        Log.e("PUSH_NOTIFICATION", "PUSH_NOTIFICATION");

        //Toast.makeText(this,messageReceivedEvent.message,Toast.LENGTH_SHORT).show();

        //onShowPopup(view,messageReceivedEvent.message);
        if (!messageReceivedEvent.title.equals("BOOKING_PAYMENT_SUCCESS")) {

            //Get the booking id from the message
            String str = messageReceivedEvent.message;
            String[] splitStr = str.split("\\s+");
            String bookingId = splitStr[splitStr.length-1];
           // ide.putExtra("bookingId", splitStr[splitStr.length-1]);
            //ide.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

            if(bookingIdFinal.equalsIgnoreCase("") || bookingIdFinal.length() <=0){
                bookingIdFinal = bookingId;
            }
            SingleInstantParameters loginCredentials = new SingleInstantParameters();
            loginCredentials.bookingId = "" + bookingIdFinal;
            getBookingByBookingId(loginCredentials);
        } else {
            onBackPressed();
        }
    }

    boolean isOnPause = false;
    @Override
    protected void onPause() {
        super.onPause();
        isOnPause = true;
    }

    @Override
    protected void onResume() {
        super.onResume();
        SharedPreferences prefs = getSharedPreferences("LoginCredentials", MODE_PRIVATE);
        Gson gson = new Gson();
        String json = prefs.getString("LoginCredentials", "");
        Utils.verifyLogInUserMobileInstantResponse = gson.fromJson(json, SingleInstantResponse.class);

        if(isOnPause){
            isOnPause = false;
            if (!bookingStatusFinal.equals("CANCELLED") && !bookingStatusFinal.equals("PAID") && !bookingStatusFinal.equals("ACCEPTED")) {
                SingleInstantParameters loginCredentials = new SingleInstantParameters();
                loginCredentials.bookingId = "" + bookingIdFinal;
                getBookingByBookingId(loginCredentials);
            }
        }
    }

    public void getBookingByBookingId(SingleInstantParameters loginCredentials) {
        if (Utils.connectivity(DriverInfoBookingActivity.this)) {

            final Dialog bookingDialog = new Dialog(DriverInfoBookingActivity.this, android.R.style.Theme_Dialog);
            bookingDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            bookingDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            bookingDialog.setContentView(R.layout.loadingimage_layout);
            bookingDialog.setCanceledOnTouchOutside(false);
            bookingDialog.setCancelable(false);
            bookingDialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
            bookingDialog.getWindow().setLayout(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
            bookingDialog.show();

            Call<SingleInstantResponse> call = apiService.getBookingByBookingId(Utils.verifyLogInUserMobileInstantResponse.getAccessToken(),loginCredentials);
            call.enqueue(new Callback<SingleInstantResponse>() {
                @Override
                public void onResponse(Call<SingleInstantResponse> call, Response<SingleInstantResponse> response) {
                    int statusCode = response.code();
//                    Log.e("statusCode", "" + statusCode);
//                    Log.e("response.body", "" + response.body());
//                    Log.e("response.errorBody", "" + response.errorBody());
//                    Log.e("response.isSuccessful", "" + response.isSuccessful());
                    if (response.isSuccessful()) {
                        bookingDialog.dismiss();
                        Utils.requestBookingResponse = response.body();
                        bookingIdFinal = Utils.requestBookingResponse.getBookingId();
                        driverNameText.setText(Utils.requestBookingResponse.getDriverName());
                        cabTypeText.setText(Utils.requestBookingResponse.getMake()+" "+Utils.requestBookingResponse.getModel());
                        cabLicenseNoText.setText(Utils.requestBookingResponse.getLicensePlateNumber());
                        fromAddressText.setText(Utils.requestBookingResponse.getFrom());
                        toAddressText.setText(Utils.requestBookingResponse.getTo());
                        bookingStatus.setText(Utils.requestBookingResponse.getBookingStatus());
                        bookingStatusFinal = Utils.requestBookingResponse.getBookingStatusCode();
                        bookingDriverMobileNumber = Utils.requestBookingResponse.getDriverMobileNumber();
                        crnNo.setText(Utils.requestBookingResponse.getCrnNumber());
//                        Log.e("DriverId", Utils.requestBookingResponse.getDriverId());
//                        Log.e("driverImage", "driverImage : " + Utils.requestBookingResponse.getDriverImage());
                        String driverImage = "" + Utils.requestBookingResponse.getDriverImage();
                        if (driverImage != null) {
                            if (!driverImage.equalsIgnoreCase("null")) {
                                byte[] decodedString = Base64.decode(driverImage, Base64.DEFAULT);
                                Bitmap decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
                                user_view.setImageBitmap(decodedByte);
                            }
                        }

                        String cabImage = "" + Utils.requestBookingResponse.getCabImage();
                        if (cabImage != null) {
                            if (!cabImage.equalsIgnoreCase("null")) {
                                byte[] decodedString = Base64.decode(driverImage, Base64.DEFAULT);
                                Bitmap decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
                                cab = decodedByte;
                                //user_view.setImageBitmap(decodedByte);
                            }
                        }

                        if (bookingStatusFinal != null) {
                            if (bookingStatusFinal.equals("SCHEDULED") || bookingStatusFinal.equals("ACCEPTED")) {
                                cancelBookingLay.setVisibility(View.VISIBLE);
                                callDriverImg.setVisibility(View.VISIBLE);
                                callDriverText.setVisibility(View.VISIBLE);
                                driverArrivingTime.setVisibility(View.VISIBLE);
                            } else {
                                cancelBookingLay.setVisibility(View.GONE);
                                callDriverImg.setVisibility(View.GONE);
                                callDriverText.setVisibility(View.GONE);
                                driverArrivingTime.setVisibility(View.GONE);
                            }
                        } else {
                            cancelBookingLay.setVisibility(View.GONE);
                            callDriverImg.setVisibility(View.GONE);
                            callDriverText.setVisibility(View.GONE);
                            driverArrivingTime.setVisibility(View.GONE);
                        }

                        if (!bookingStatusFinal.equals("COMPLETED") && !bookingStatusFinal.equals("PAID")) {
                            SingleInstantParameters loginCredentials = new SingleInstantParameters();
                            loginCredentials.userId = "" + Utils.requestBookingResponse.getDriverId();
                            getGeoLocationByDriverId(loginCredentials);
                        }

                        if (bookingStatusFinal.equals("CANCELLED")) {
                            showInfoDlg("Booking Cancelled", "Requested booking has been cancelled. Try after sometime", "Done", "requestCancelled");
                        }

                        if (!bookingStatusFinal.equals("CANCELLED") && !bookingStatusFinal.equals("PAID") && !bookingStatusFinal.equals("ACCEPTED")) {
                            SharedPreferences.Editor editor = getSharedPreferences("BookingCredentials", MODE_PRIVATE).edit();
                            editor.putString("bookingId", bookingIdFinal);
                            editor.commit();
                        } else {
                            SharedPreferences.Editor editor = getSharedPreferences("BookingCredentials", MODE_PRIVATE).edit();
                            editor.putString("bookingId", "");
                            editor.commit();
                        }

                        if (bookingStatusFinal.equals("COMPLETED")) {
                            Intent ide = new Intent(DriverInfoBookingActivity.this, CashDisplyActivity.class);
                            ide.putExtra("bookingId", "" + bookingIdFinal);
                            ide.putExtra("suggestedPrice", "" + Utils.requestBookingResponse.getSuggestedPrice());
                            ide.putExtra("offeredPrice", "" + Utils.requestBookingResponse.getOfferedPrice());
                            ide.putExtra("distanceInMiles", "" + Utils.requestBookingResponse.getGeoLocationResponse().getDistanceInMiles());
                            ide.putExtra("fromaddress", "" + Utils.requestBookingResponse.getFrom());
                            ide.putExtra("toaddress", "" + Utils.requestBookingResponse.getTo());
                            ide.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                            startActivity(ide);
                            //finish();
                        }

                    } else {
                        bookingDialog.dismiss();
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
                            Toast.makeText(DriverInfoBookingActivity.this, "Either there is no network connectivity or server is not available.. Please try again later..", Toast.LENGTH_LONG).show();
                        }
                    }
                }

                @Override
                public void onFailure(Call<SingleInstantResponse> call, Throwable t) {
                    // Log error here since request failed
                    Log.e("onFailure", t.toString());
                    bookingDialog.dismiss();
                    showInfoDlg(getString(R.string.error), "" + getString(R.string.errmsg_sessionexpired), getString(R.string.btn_ok), "logout");
                   // Toast.makeText(DriverInfoBookingActivity.this, "Either there is no network connectivity or server is not available.. Please try again later..", Toast.LENGTH_LONG).show();
                }
            });
        } else {
            Toast.makeText(DriverInfoBookingActivity.this, "Either there is no network connectivity or server is not available.. Please try again later..", Toast.LENGTH_LONG).show();
        }
    }

    public void getBookingByBookingId(SingleInstantParameters loginCredentials, final int count) {
        if (Utils.connectivity(DriverInfoBookingActivity.this)) {
            final Dialog dialog = new Dialog(DriverInfoBookingActivity.this, android.R.style.Theme_Dialog);
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
                        bookingIdFinal = Utils.requestBookingResponse.getBookingId();
                        driverNameText.setText(Utils.requestBookingResponse.getDriverName());
                        cabTypeText.setText(Utils.requestBookingResponse.getMake()+" "+Utils.requestBookingResponse.getModel());
                        cabLicenseNoText.setText(Utils.requestBookingResponse.getLicensePlateNumber());
                        fromAddressText.setText(Utils.requestBookingResponse.getFrom());
                        toAddressText.setText(Utils.requestBookingResponse.getTo());
                        bookingStatus.setText(Utils.requestBookingResponse.getBookingStatus());
                        bookingStatusFinal = Utils.requestBookingResponse.getBookingStatusCode();
                        bookingDriverMobileNumber = Utils.requestBookingResponse.getDriverMobileNumber();
                        crnNo.setText(Utils.requestBookingResponse.getCrnNumber());
                        Log.e("DriverId", Utils.requestBookingResponse.getDriverId());
                        Log.e("driverImage", "driverImage : " + Utils.requestBookingResponse.getDriverImage());
                        String driverImage = "" + Utils.requestBookingResponse.getDriverImage();
                        if (driverImage != null) {
                            if (!driverImage.equalsIgnoreCase("null")) {
                                byte[] decodedString = Base64.decode(driverImage, Base64.DEFAULT);
                                Bitmap decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
                                user_view.setImageBitmap(decodedByte);
                            }
                        }

                        String cabImage = "" + Utils.requestBookingResponse.getCabImage();
                        if (cabImage != null) {
                            if (!cabImage.equalsIgnoreCase("null")) {
                                byte[] decodedString = Base64.decode(driverImage, Base64.DEFAULT);
                                Bitmap decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
                                cab = decodedByte;
                                //user_view.setImageBitmap(decodedByte);
                            }
                        }

                        if (bookingStatusFinal != null) {
                            if (bookingStatusFinal.equals("SCHEDULED") || bookingStatusFinal.equals("ACCEPTED")) {
                                cancelBookingLay.setVisibility(View.VISIBLE);
                                callDriverImg.setVisibility(View.VISIBLE);
                                callDriverText.setVisibility(View.VISIBLE);
                                driverArrivingTime.setVisibility(View.VISIBLE);
                            } else {
                                cancelBookingLay.setVisibility(View.GONE);
                                callDriverImg.setVisibility(View.GONE);
                                callDriverText.setVisibility(View.GONE);
                                driverArrivingTime.setVisibility(View.GONE);
                            }
                        } else {
                            cancelBookingLay.setVisibility(View.GONE);
                            callDriverImg.setVisibility(View.GONE);
                            callDriverText.setVisibility(View.GONE);
                            driverArrivingTime.setVisibility(View.GONE);
                        }

                        String fromLat = Utils.requestBookingResponse.getGeoLocationResponse().getFromLatitude();
                        String fromLong = Utils.requestBookingResponse.getGeoLocationResponse().getFromLongitude();
                        String toLat = Utils.requestBookingResponse.getGeoLocationResponse().getToLatitude();
                        String toLong = Utils.requestBookingResponse.getGeoLocationResponse().getToLongitude();

                        origin = new LatLng(Double.parseDouble(fromLat), Double.parseDouble(fromLong));
                        dest = new LatLng(Double.parseDouble(toLat), Double.parseDouble(toLong));

                        startingMarker = mMap.addMarker(new MarkerOptions().anchor(0.5f, 0.5f).position(origin).icon(BitmapDescriptorFactory.fromResource(R.drawable.startpoint)));
                        startingMarker.setTag("starting");
                        markers.add(startingMarker);
                        endingMarker = mMap.addMarker(new MarkerOptions().anchor(0.5f, 0.5f).position(dest).icon(BitmapDescriptorFactory.fromResource(R.drawable.endpoint)));
                        endingMarker.setTag("ending");
                        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(origin, 15));
                        mMap.animateCamera(CameraUpdateFactory.zoomIn());
                        mMap.animateCamera(CameraUpdateFactory.zoomTo(15), 2000, null);

                        if (!bookingStatusFinal.equals("COMPLETED") && !bookingStatusFinal.equals("PAID")) {
                            SingleInstantParameters loginCredentials = new SingleInstantParameters();
                            loginCredentials.userId = "" + Utils.requestBookingResponse.getDriverId();
                            getGeoLocationByDriverId(loginCredentials);
                        }

                        if (!bookingStatusFinal.equals("CANCELLED") && !bookingStatusFinal.equals("COMPLETED") && !bookingStatusFinal.equals("PAID") && !bookingStatusFinal.equals("ACCEPTED")) {
                            SharedPreferences.Editor editor = getSharedPreferences("BookingCredentials", MODE_PRIVATE).edit();
                            editor.putString("bookingId", bookingIdFinal);
                            editor.commit();
                        } else {
                            SharedPreferences.Editor editor = getSharedPreferences("BookingCredentials", MODE_PRIVATE).edit();
                            editor.putString("bookingId", "");
                            editor.commit();
                        }

                        if (bookingStatusFinal.equals("PAID")) {
                            reportLay.setVisibility(View.VISIBLE);
                        } else {
                            reportLay.setVisibility(View.GONE);
                        }

                        if (bookingStatusFinal.equals("COMPLETED")) {
                            Intent ide = new Intent(DriverInfoBookingActivity.this, CashDisplyActivity.class);
                            ide.putExtra("bookingId", "" + bookingIdFinal);
                            ide.putExtra("suggestedPrice", "" + Utils.requestBookingResponse.getSuggestedPrice());
                            ide.putExtra("offeredPrice", "" + Utils.requestBookingResponse.getOfferedPrice());
                            ide.putExtra("distanceInMiles", "" + Utils.requestBookingResponse.getGeoLocationResponse().getDistanceInMiles());
                            ide.putExtra("fromaddress", "" + Utils.requestBookingResponse.getFrom());
                            ide.putExtra("toaddress", "" + Utils.requestBookingResponse.getTo());
                            ide.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                            startActivity(ide);
                           // finish();
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
                                showInfoDlg("Error..", "" + jObjError.getString("message"), "OK", "error");
                            }
                        } catch (Exception e) {
                            Toast.makeText(DriverInfoBookingActivity.this, "Either there is no network connectivity or server is not available.. Please try again later..", Toast.LENGTH_LONG).show();
                        }
                    }
                }

                @Override
                public void onFailure(Call<SingleInstantResponse> call, Throwable t) {
                    // Log error here since request failed
                    Log.e("onFailure", t.toString());
                    dialog.dismiss();
                    showInfoDlg(getString(R.string.error), "" + getString(R.string.errmsg_sessionexpired), getString(R.string.btn_ok), "logout");
                    //Toast.makeText(DriverInfoBookingActivity.this, "Either there is no network connectivity or server is not available.. Please try again later..", Toast.LENGTH_LONG).show();
                }
            });
        } else {
            Toast.makeText(DriverInfoBookingActivity.this, "Either there is no network connectivity or server is not available.. Please try again later..", Toast.LENGTH_LONG).show();
        }
    }

    private void updateBookingStatus(SingleInstantParameters loginCredentials) {
        if (Utils.connectivity(DriverInfoBookingActivity.this)) {
            final Dialog dialog = new Dialog(DriverInfoBookingActivity.this, android.R.style.Theme_Dialog);
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            dialog.setContentView(R.layout.loadingimage_layout);
            dialog.setCanceledOnTouchOutside(false);
            dialog.setCancelable(false);
            dialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
            dialog.getWindow().setLayout(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
            dialog.show();

            Call<SingleInstantResponse> call = apiService.updateBookingStatus(Utils.verifyLogInUserMobileInstantResponse.getAccessToken(),loginCredentials);
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

                        SharedPreferences.Editor editor = getSharedPreferences("BookingCredentials", MODE_PRIVATE).edit();
                        editor.putString("bookingId", "");
                        editor.commit();

                        Utils.updateBookingStatusInstantResponse = response.body();
                        Log.e("BookingStatus", "" + Utils.updateBookingStatusInstantResponse.getBookingStatus());
                        showInfoDlg("Success..", "Your ZipRyde has been cancelled.", "Ok", "success");
                    } else {
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
                            Toast.makeText(DriverInfoBookingActivity.this, "Either there is no network connectivity or server is not available.. Please try again later..", Toast.LENGTH_LONG).show();
                        }
                    }
                }

                @Override
                public void onFailure(Call<SingleInstantResponse> call, Throwable t) {
                    // Log error here since request failed
                    Log.e("onFailure", t.toString());
                    dialog.dismiss();
                    showInfoDlg(getString(R.string.error), "" + getString(R.string.errmsg_sessionexpired), getString(R.string.btn_ok), "logout");
                   // Toast.makeText(DriverInfoBookingActivity.this, "Either there is no network connectivity or server is not available.. Please try again later..", Toast.LENGTH_LONG).show();
                }
            });
        } else {
            Toast.makeText(DriverInfoBookingActivity.this, "Either there is no network connectivity or server is not available.. Please try again later..", Toast.LENGTH_LONG).show();
        }
    }

    Marker driverMarker;
    Handler handler = new Handler();
    Runnable finalizer;

    @Override
    public void onBackPressed() {


//        if(isHome == 1){
//
//            Intent ide = new Intent(DriverInfoBookingActivity.this, NavigationMenuActivity.class);
//            ide.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
//            startActivity(ide);
//            finish();
//
//        }

        if (bookingStatusFinal.equals("PAID") || bookingStatusFinal.equals("CANCELLED") || bookingStatusFinal.equals("COMPLETED") || bookingStatusFinal.equals("ACCEPTED") ) {
            if (handler != null && finalizer != null) {
                handler.removeCallbacks(finalizer);
            }
            Intent ide = new Intent(DriverInfoBookingActivity.this, NavigationMenuActivity.class);

            if(isHome == 1){
                ide.putExtra("body",1);
            }
            ide.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(ide);
           // finish();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        if (handler != null && finalizer != null) {
            handler.removeCallbacks(finalizer);
        }

        cab = null;
        driver = null;
    }

    public void getGeoLocationByDriverId(final SingleInstantParameters loginCredentials) {
        if (Utils.connectivity(DriverInfoBookingActivity.this)) {
            Gson gson = new Gson();
            String json = gson.toJson(loginCredentials);
            Log.e("GeoLocationId json", "" + json);
            Call<SingleInstantResponse> call = apiService.getGeoLocationByDriverId(Utils.verifyLogInUserMobileInstantResponse.getAccessToken(),loginCredentials);
            call.enqueue(new Callback<SingleInstantResponse>() {
                @Override
                public void onResponse(Call<SingleInstantResponse> call, Response<SingleInstantResponse> response) {
                    int statusCode = response.code();
                    Log.e("statusCode", "" + statusCode);
                    Log.e("response.body", "" + response.body());
                    Log.e("response.errorBody", "" + response.errorBody());
                    Log.e("response.isSuccessful", "" + response.isSuccessful());
                    if (response.isSuccessful()) {
                        Utils.getGeoLocationByDriverIdResponse = response.body();
//                        Log.e("Latitude", "" + Utils.getGeoLocationByDriverIdResponse.getLatitude());
//                        Log.e("Longitude", "" + Utils.getGeoLocationByDriverIdResponse.getLongitude());
                        LatLng driverLatLng;
                        //Check the user status and change his status
                        if(Utils.getGeoLocationByDriverIdResponse.getBookingStatusCode() != null && Utils.getGeoLocationByDriverIdResponse.getBookingStatus() != null) {
                            changeDriverStatus(Utils.getGeoLocationByDriverIdResponse.getBookingStatusCode(), Utils.getGeoLocationByDriverIdResponse.getBookingStatus());
                        }

                        if (Utils.getGeoLocationByDriverIdResponse.getLatitude() != null && Utils.getGeoLocationByDriverIdResponse.getLongitude() != null) {
                            if (driverMarker != null) {
//                                driverMarker.remove();
                                LatLng perLatLng = driverMarker.getPosition();
                                driverMarker.setAnchor(0.5f,0.5f);
                                driverLatLng = new LatLng(Double.parseDouble(Utils.getGeoLocationByDriverIdResponse.getLatitude()), Double.parseDouble(Utils.getGeoLocationByDriverIdResponse.getLongitude()));
                                float toRotation = bearingBetweenLocations(perLatLng, driverLatLng);
                                rotateMarker(driverMarker, toRotation);
                                animateMarker(driverLatLng, false);
                                //Move the camera along with this position.
                                mMap.moveCamera(CameraUpdateFactory
                                        .newCameraPosition
                                                (new CameraPosition.Builder().
                                                        target(driverLatLng)
                                                                .zoom(15.5f)
                                                                .build()));

                            } else {
                                driverLatLng = new LatLng(Double.parseDouble(Utils.getGeoLocationByDriverIdResponse.getLatitude()), Double.parseDouble(Utils.getGeoLocationByDriverIdResponse.getLongitude()));
                                driverMarker = mMap.addMarker(new MarkerOptions().anchor(0.5f, 0.5f).position(driverLatLng).icon(BitmapDescriptorFactory.fromResource(R.drawable.movingcar_48)));
                            }
                            finalizer = new Runnable() {
                                public void run() {
                                    Log.e("bookingStatusFinal", "" + bookingStatusFinal);
                                    if (!bookingStatusFinal.equals("COMPLETED")) {
                                        if (!bookingStatusFinal.equals("CANCELLED")) {

                                            if(!bookingStatusFinal.equals("ACCEPTED")) {
                                                getGeoLocationByDriverId(loginCredentials);
                                            }
                                        }
                                    }
                                }
                            };
                            handler.postDelayed(finalizer, 10000);

                            if (bookingStatusFinal.equals("SCHEDULED")) {
                                driverArrivingTime.setVisibility(View.VISIBLE);
                                if (Utils.connectivity(DriverInfoBookingActivity.this)) {
                                    if (origin != null) {
                                        String url = getUrl(driverLatLng, origin);
                                        Log.d("url", "" + url);
                                        FetchUrl FetchUrl = new FetchUrl("driverSelect");
                                        FetchUrl.execute(url);
                                    }
                                } else {
                                    Toast.makeText(DriverInfoBookingActivity.this, "Either there is no network connectivity or server is not available.. Please try again later..", Toast.LENGTH_LONG).show();
                                }
                            }

                        } else {
                            finalizer = new Runnable() {
                                public void run() {
                                    Log.e("bookingStatusFinal", "" + bookingStatusFinal);
                                    if (!bookingStatusFinal.equals("COMPLETED")) {
                                        if (!bookingStatusFinal.equals("CANCELLED")) {
                                            if(!bookingStatusFinal.equals("ACCEPTED")) {
                                                getGeoLocationByDriverId(loginCredentials);
                                            }
                                        }
                                    }
                                }
                            };
                            handler.postDelayed(finalizer, 10000);
                        }
                    } else {
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
                            Toast.makeText(DriverInfoBookingActivity.this, "Either there is no network connectivity or server is not available.. Please try again later..", Toast.LENGTH_LONG).show();
                        }
                    }
                }

                @Override
                public void onFailure(Call<SingleInstantResponse> call, Throwable t) {
                    // Log error here since request failed
                    Log.e("onFailure", t.toString());
                    showInfoDlg(getString(R.string.error), "" + getString(R.string.errmsg_sessionexpired), getString(R.string.btn_ok), "logout");
                    //Toast.makeText(DriverInfoBookingActivity.this, "Either there is no network connectivity or server is not available.. Please try again later..", Toast.LENGTH_LONG).show();
                }
            });
        } else {
            Toast.makeText(DriverInfoBookingActivity.this, "Either there is no network connectivity or server is not available.. Please try again later..", Toast.LENGTH_LONG).show();
        }
    }


    private void changeDriverStatus(String newStatusCode,String newStatus){

       // Toast.makeText(this,"newStatus",Toast.LENGTH_SHORT).show();
        if(newStatus != null && newStatusCode != null) {
            if (bookingStatusFinal.equalsIgnoreCase(newStatusCode)) {
                //Dont do anything here
            } else {

                bookingStatusFinal = newStatusCode;

                bookingStatus.setText(newStatus);

                if (bookingStatusFinal != null) {
                    if (bookingStatusFinal.equals("SCHEDULED") || bookingStatusFinal.equals("ACCEPTED")) {
                        cancelBookingLay.setVisibility(View.VISIBLE);
                        callDriverImg.setVisibility(View.VISIBLE);
                        callDriverText.setVisibility(View.VISIBLE);
                        driverArrivingTime.setVisibility(View.VISIBLE);
                    } else {
                        cancelBookingLay.setVisibility(View.GONE);
                        callDriverImg.setVisibility(View.GONE);
                        callDriverText.setVisibility(View.GONE);
                        driverArrivingTime.setVisibility(View.GONE);
                    }
                } else {
                    cancelBookingLay.setVisibility(View.GONE);
                    callDriverImg.setVisibility(View.GONE);
                    callDriverText.setVisibility(View.GONE);
                    driverArrivingTime.setVisibility(View.GONE);
                }

//            if (!bookingStatusFinal.equals("COMPLETED")) {
//                SingleInstantParameters loginCredentials = new SingleInstantParameters();
//                loginCredentials.userId = "" + Utils.requestBookingResponse.getDriverId();
//                //getGeoLocationByDriverId(loginCredentials);
//            }

                if (bookingStatusFinal.equals("CANCELLED")) {
                    showInfoDlg("Booking Cancelled", "Requested booking has been cancelled. Try after sometime", "Done", "requestCancelled");
                }

                if (!bookingStatusFinal.equals("CANCELLED") && !bookingStatusFinal.equals("PAID") && !bookingStatusFinal.equals("ACCEPTED")) {
                    SharedPreferences.Editor editor = getSharedPreferences("BookingCredentials", MODE_PRIVATE).edit();
                    editor.putString("bookingId", bookingIdFinal);
                    editor.commit();
                } else {
                    SharedPreferences.Editor editor = getSharedPreferences("BookingCredentials", MODE_PRIVATE).edit();
                    editor.putString("bookingId", "");
                    editor.commit();
                }

                if (bookingStatusFinal.equals("COMPLETED")) {
                    Intent ide = new Intent(DriverInfoBookingActivity.this, CashDisplyActivity.class);
                    ide.putExtra("bookingId", "" + bookingIdFinal);
                    ide.putExtra("suggestedPrice", "" + Utils.requestBookingResponse.getSuggestedPrice());
                    ide.putExtra("offeredPrice", "" + Utils.requestBookingResponse.getOfferedPrice());
                    ide.putExtra("distanceInMiles", "" + Utils.requestBookingResponse.getGeoLocationResponse().getDistanceInMiles());
                    ide.putExtra("fromaddress", "" + Utils.requestBookingResponse.getFrom());
                    ide.putExtra("toaddress", "" + Utils.requestBookingResponse.getTo());
                    ide.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(ide);
                    //finish();
                }
            }
        }
    }
    private float bearingBetweenLocations(LatLng latLng1, LatLng latLng2) {

        double PI = 3.14159;
        double lat1 = latLng1.latitude * PI / 180;
        double long1 = latLng1.longitude * PI / 180;
        double lat2 = latLng2.latitude * PI / 180;
        double long2 = latLng2.longitude * PI / 180;

        double dLon = (long2 - long1);

        double y = Math.sin(dLon) * Math.cos(lat2);
        double x = Math.cos(lat1) * Math.sin(lat2) - Math.sin(lat1)
                * Math.cos(lat2) * Math.cos(dLon);

        double brng = Math.atan2(y, x);

        brng = Math.toDegrees(brng);
        brng = (brng + 360) % 360;

        return Float.parseFloat("" + brng);
    }

    boolean isMarkerRotating;

    private void rotateMarker(final Marker marker, final float toRotation) {
        if (!isMarkerRotating) {
            final Handler handler = new Handler();
            final long start = SystemClock.uptimeMillis();
            final float startRotation = marker.getRotation();
            final long duration = 1000;

            final LinearInterpolator interpolator = new LinearInterpolator();

            handler.post(new Runnable() {
                @Override
                public void run() {
                    isMarkerRotating = true;
                    long elapsed = SystemClock.uptimeMillis() - start;
                    float t = interpolator.getInterpolation((float) elapsed / duration);

                    float rot = t * toRotation + (1 - t) * startRotation;

                    marker.setRotation(-rot > 180 ? rot / 2 : rot);
                    if (t < 1.0) {
                        // Post again 16ms later.
                        handler.postDelayed(this, 16);
                    } else {
                        isMarkerRotating = false;
                    }
                }
            });
        }
    }

    public void animateMarker(final LatLng toPosition, final boolean hideMarke) {
        final Handler handler = new Handler();
        final long start = SystemClock.uptimeMillis();
        Projection proj = mMap.getProjection();
        Point startPoint = proj.toScreenLocation(driverMarker.getPosition());
        final LatLng startLatLng = proj.fromScreenLocation(startPoint);
        final long duration = 5000;

        final LinearInterpolator interpolator = new LinearInterpolator();

        handler.post(new Runnable() {
            @Override
            public void run() {
                long elapsed = SystemClock.uptimeMillis() - start;
                float t = interpolator.getInterpolation((float) elapsed
                        / duration);
                double lng = t * toPosition.longitude + (1 - t)
                        * startLatLng.longitude;
                double lat = t * toPosition.latitude + (1 - t)
                        * startLatLng.latitude;
                driverMarker.setPosition(new LatLng(lat, lng));

                if (t < 1.0) {
                    handler.postDelayed(this, 16);
                } else {
                    if (hideMarke) {
                        driverMarker.setVisible(false);
                    } else {
                        driverMarker.setVisible(true);
                    }
                }
            }
        });
    }

    private void showInfoDlg(String title, String content, String btnText, final String navType) {

        final Dialog dialog = new Dialog(DriverInfoBookingActivity.this, android.R.style.Theme_Dialog);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.infodialog_layout);
        dialog.setCanceledOnTouchOutside(false);
        dialog.setCancelable(false);
        ImageView headerIcon = (ImageView) dialog.findViewById(R.id.headerIcon);
        if (navType.equalsIgnoreCase("server") || navType.equalsIgnoreCase("error")) {
            headerIcon.setImageResource(R.drawable.erroricon);
        }

        Button positiveBtn = (Button) dialog.findViewById(R.id.positiveBtn);
        positiveBtn.setText("" + btnText);

        Button newnegativeBtn = (Button) dialog.findViewById(R.id.newnegativeBtn);
        if (navType.equalsIgnoreCase("info") || navType.equalsIgnoreCase("logout") || navType.equalsIgnoreCase("error") || navType.equalsIgnoreCase("server") || navType.equals("requestCancelled")) {
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
                    Intent ide = new Intent(DriverInfoBookingActivity.this, NavigationMenuActivity.class);
                    ide.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(ide);
                    //finish();
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
                    Intent ide = new Intent(DriverInfoBookingActivity.this, LoginActivity.class);
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


    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    Marker startingMarker, endingMarker;
    boolean mapReady = false;

    List<Marker> markers = new LinkedList<Marker>();

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mapReady = true;

        markers = new LinkedList<Marker>();
//        mMap.setOnMapLoadedCallback(new GoogleMap.OnMapLoadedCallback() {
//            @Override
//            public void onMapLoaded() {
//                //markers.add(endingMarker);
//                if (markers.size() > 0) {
//                    LatLngBounds.Builder builder = new LatLngBounds.Builder();
//                    for (Marker marker : markers) {
//                        builder.include(marker.getPosition());
//                    }
//                    LatLngBounds bounds = builder.build();
//                    int padding = 0; // offset from edges of the map in pixels
//                    CameraUpdate cu = CameraUpdateFactory.newLatLngBounds(bounds, padding);
//                    mMap.moveCamera(cu);
//                    mMap.animateCamera(cu);
//                    mMap.animateCamera(CameraUpdateFactory.zoomTo(8), 1000, null);
//                }
//            }
//        });

        if (position != -1) {
            ListOfBooking listOfBooking = Utils.getBookingByUserIdResponse.get(position);
            String fromLat = listOfBooking.getGeoLocationResponse().getFromLatitude();
            String fromLong = listOfBooking.getGeoLocationResponse().getFromLongitude();
            String toLat = listOfBooking.getGeoLocationResponse().getToLatitude();
            String toLong = listOfBooking.getGeoLocationResponse().getToLongitude();

            origin = new LatLng(Double.parseDouble(fromLat), Double.parseDouble(fromLong));
            dest = new LatLng(Double.parseDouble(toLat), Double.parseDouble(toLong));

            startingMarker = mMap.addMarker(new MarkerOptions().anchor(0.5f, 0.5f).position(origin).icon(BitmapDescriptorFactory.fromResource(R.drawable.startpoint)));
            startingMarker.setTag("starting");
            markers.add(startingMarker);
            endingMarker = mMap.addMarker(new MarkerOptions().anchor(0.5f, 0.5f).position(dest).icon(BitmapDescriptorFactory.fromResource(R.drawable.endpoint)));
            endingMarker.setTag("ending");
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(origin, 18));
            mMap.animateCamera(CameraUpdateFactory.zoomIn());
            mMap.animateCamera(CameraUpdateFactory.zoomTo(18), 2000, null);
        } else {
            // Booked
            if (Utils.requestBookingResponse != null) {
                if (Utils.requestBookingResponse.getGeoLocationResponse() != null) {
                    String fromLat = Utils.requestBookingResponse.getGeoLocationResponse().getFromLatitude();
                    String fromLong = Utils.requestBookingResponse.getGeoLocationResponse().getFromLongitude();
                    String toLat = Utils.requestBookingResponse.getGeoLocationResponse().getToLatitude();
                    String toLong = Utils.requestBookingResponse.getGeoLocationResponse().getToLongitude();

                    origin = new LatLng(Double.parseDouble(fromLat), Double.parseDouble(fromLong));
                    dest = new LatLng(Double.parseDouble(toLat), Double.parseDouble(toLong));

                    startingMarker = mMap.addMarker(new MarkerOptions().anchor(0.5f, 0.5f).position(origin).icon(BitmapDescriptorFactory.fromResource(R.drawable.startpoint)));
                    startingMarker.setTag("starting");
                    markers.add(startingMarker);
                    endingMarker = mMap.addMarker(new MarkerOptions().anchor(0.5f, 0.5f).position(dest).icon(BitmapDescriptorFactory.fromResource(R.drawable.endpoint)));
                    endingMarker.setTag("ending");
                    mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(origin, 18));
                    mMap.animateCamera(CameraUpdateFactory.zoomIn());
                    mMap.animateCamera(CameraUpdateFactory.zoomTo(18), 2000, null);
                }
            }
        }
    }

    private String getUrl(LatLng origin, LatLng dest) {
        // Origin of route
        String str_origin = "origin=" + origin.latitude + "," + origin.longitude;
        // Destination of route
        String str_dest = "destination=" + dest.latitude + "," + dest.longitude;

        // Sensor enabled
        String sensor = "sensor=false";
        // Building the parameters to the web service
        String parameters = str_origin + "&" + str_dest + "&" + sensor;
        // Output format
        String output = "json";
        // Building the url to the web service
        String url = "https://maps.googleapis.com/maps/api/directions/" + output + "?" + parameters;
        Log.e("url", "url -- " + url);
        return url;
    }

    /**
     * A method to download json data from url
     */
    private String downloadUrl(String strUrl) throws IOException {
        String data = "";
        InputStream iStream = null;
        HttpURLConnection urlConnection = null;
        try {
            URL url = new URL(strUrl);

            // Creating an http connection to communicate with url
            urlConnection = (HttpURLConnection) url.openConnection();

            // Connecting to url
            urlConnection.connect();

            // Reading data from url
            iStream = urlConnection.getInputStream();

            BufferedReader br = new BufferedReader(new InputStreamReader(iStream));

            StringBuffer sb = new StringBuffer();

            String line = "";
            while ((line = br.readLine()) != null) {
                sb.append(line);
            }

            data = sb.toString();
            Log.d("downloadUrl", data.toString());
            br.close();

        } catch (Exception e) {
            Log.d("Exception", e.toString());
        } finally {
            iStream.close();
            urlConnection.disconnect();
        }
        return data;
    }

    // Fetches data from url passed
    private class FetchUrl extends AsyncTask<String, Void, String> {

        String displayType = "";

        public FetchUrl(String displayType) {
            this.displayType = displayType;
        }

        public FetchUrl() {
            this.displayType = "";
        }

        @Override
        protected String doInBackground(String... url) {

            // For storing data from web service
            String data = "";

            try {
                // Fetching the data from web service
                data = downloadUrl(url[0]);
                Log.d("Background Task data", data.toString());
            } catch (Exception e) {
                Log.d("Background Task", e.toString());
            }
            return data;
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);

            ParserTask parserTask = new ParserTask(displayType);
            // Invokes the thread for parsing the JSON data
            parserTask.execute(result);

        }
    }

    /**
     * A class to parse the Google Places in JSON format
     */
    private class ParserTask extends AsyncTask<String, Integer, List<List<HashMap<String, String>>>> {

        String displayType = "";

        public ParserTask(String displayType) {
            this.displayType = displayType;
        }

        public ParserTask() {
            this.displayType = "";
        }

        // Parsing the data in non-ui thread
        @Override
        protected List<List<HashMap<String, String>>> doInBackground(String... jsonData) {

            JSONObject jObject;
            List<List<HashMap<String, String>>> routes = null;

            try {
                jObject = new JSONObject(jsonData[0]);
                Log.d("ParserTask", jsonData[0].toString());
                DataParser parser = new DataParser();
                Log.d("ParserTask", parser.toString());

                // Starts parsing data
                routes = parser.parse(jObject);
                Log.d("ParserTask", "Executing routes");
                Log.d("ParserTask", routes.toString());

            } catch (Exception e) {
                Log.d("ParserTask", e.toString());
                e.printStackTrace();
            }
            return routes;
        }

        // Executes in UI thread, after the parsing process
        @Override
        protected void onPostExecute(List<List<HashMap<String, String>>> result) {
            ArrayList<LatLng> points;
            PolylineOptions lineOptions = null;

            // Traversing through all the routes
            for (int i = 0; i < result.size(); i++) {
                points = new ArrayList<>();
                lineOptions = new PolylineOptions();

                // Fetching i-th route
                List<HashMap<String, String>> path = result.get(i);

                // Fetching all the points in i-th route
                for (int j = 0; j < path.size(); j++) {
                    HashMap<String, String> point = path.get(j);

                    double lat = Double.parseDouble(point.get("lat"));
                    double lng = Double.parseDouble(point.get("lng"));
                    LatLng position = new LatLng(lat, lng);

                    points.add(position);
                }

                // Adding all the points in the route to LineOptions
                lineOptions.addAll(points);
                lineOptions.width(10);
                lineOptions.color(Color.parseColor("#df722c"));

                Log.d("onPostExecute", "onPostExecute lineoptions decoded");


                if(bookingStatusFinal.equalsIgnoreCase("PAID")) {
                    //Call the staticMap
                    //getStaticRouteMap(points);

                }

            }

            // Drawing polyline in the Google Map for the i-th route
            if (lineOptions != null) {
                if (displayType.equalsIgnoreCase("")) {
                    polyline = mMap.addPolyline(lineOptions);
                }
                if (!displayType.equalsIgnoreCase("")) {
                    driverArrivingTime.setText(Utils.parsedDuration);
                }

//                if(displayType.equalsIgnoreCase("staticmap")){
//                    if(result != null){
//
//                        //get the static map from the server.
//
//
//                    }
              //  }
            } else {
                Log.d("onPostExecute", "without Polylines drawn");
            }
        }
    }

    // call this method when required to show popup
    public void onShowPopup(View v){

        LayoutInflater layoutInflater = (LayoutInflater)getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        // inflate the custom popup layout
        final View inflatedView = layoutInflater.inflate(R.layout.localpushmsg_popupwindow_layout, null,false);
        // find the ListView in the popup layout


        // get device size
        Display display = getWindowManager().getDefaultDisplay();
        final Point size = new Point();
        display.getSize(size);
        // mDeviceHeight = size.y;


        // fill the data to the list items
        // setSimpleList(listView);

        TextView msg  = (TextView) inflatedView.findViewById(R.id.pushmsgText);

//        Button paypalBtn = (Button) inflatedView.findViewById(R.id.paypalBtn);
//        paypalBtn.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://www.paypal.com/us/home"));
//                startActivity(browserIntent);
//
//
//
//            }
//        });
//
//        Button cashAppBtn = (Button) inflatedView.findViewById(R.id.cashBtn);
//        cashAppBtn.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://cash.me/app/WTXRWNB"));
//                startActivity(browserIntent);
//
//
//
//            }
//        });



        // set height depends on the device size
        pushNotificationPopwindow = new PopupWindow(inflatedView, size.x - 50,size.y - 400, true );
        // set a background drawable with rounders corners
        //pushNotificationPopwindow.setBackgroundDrawable(getResources().getDrawable(R.drawable.paymentsetup_popup_shape));
        // make it focusable to show the keyboard to enter in `EditText`
        pushNotificationPopwindow.setFocusable(true);
        // make it outside touchable to dismiss the popup window
        pushNotificationPopwindow.setOutsideTouchable(true);

        // show the popup at bottom of the screen and set some margin at bottom ie,
        pushNotificationPopwindow.showAtLocation(v, Gravity.BOTTOM, 0,100);
    }

    private void getStaticRouteMap(ArrayList<LatLng> allPoints) {

        //String staticMapURL = "http://maps.google.com/maps/api/staticmap?center=" + origin.latitude + "," + origin.longitude + "&zoom=15&size=200x200&sensor=false";//"https://maps.googleapis.com/maps/api/staticmap?size=400x400&path=enc:"+allPoints;
        String staticMapURL = "http://maps.google.com/maps/api/staticmap?center=-33.882257,151.210243&zoom=13&markers=size:mid|color:red|label:E|-33.882257,151.210243&size=250x188&sensor=false";
        // Glide.with(this).load(staticMapURL).into(staticMapImg);

        Picasso.with(getApplicationContext()).load(staticMapURL).into(staticMapImg);

    }

}
