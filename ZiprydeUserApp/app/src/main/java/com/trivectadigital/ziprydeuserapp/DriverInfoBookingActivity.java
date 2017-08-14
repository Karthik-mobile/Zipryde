package com.trivectadigital.ziprydeuserapp;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.location.LocationManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Handler;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
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

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.gson.Gson;
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

    TextView driverNameText, cabTypeText, fromAddressText, toAddressText, bookingStatus, callDriverText, driverArrivingTime;
    ListOfBooking listOfBooking;
    ZiprydeApiInterface apiService;

    CircleImageView user_view;

    LinearLayout cancelBookingLay, getDirections;

    String bookingStatusFinal = "";
    String bookingIdFinal = "";
    String bookingDriverMobileNumber = "";

    ImageView callDriverImg;

    public static final int ACTIONCALL = 0x1;
    Polyline polyline;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_driver_info_booking);

        apiService = ZiprydeApiClient.getClient().create(ZiprydeApiInterface.class);

        driverNameText = (TextView) findViewById(R.id.driverNameText);
        cabTypeText = (TextView) findViewById(R.id.cabTypeText);
        fromAddressText = (TextView) findViewById(R.id.fromAddressText);
        toAddressText = (TextView) findViewById(R.id.toAddressText);
        bookingStatus = (TextView) findViewById(R.id.bookingStatus);
        callDriverText = (TextView) findViewById(R.id.callDriverText);
        driverArrivingTime = (TextView) findViewById(R.id.driverArrivingTime);
        user_view = (CircleImageView) findViewById(R.id.user_view);

        callDriverImg = (ImageView) findViewById(R.id.callDriverImg);

        cancelBookingLay = (LinearLayout) findViewById(R.id.cancelBookingLay);
        getDirections = (LinearLayout) findViewById(R.id.getDirections);

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        Intent intent = getIntent();
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


        cancelBookingLay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
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
    }

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
            cabTypeText.setText(listOfBooking.getCabType());
            fromAddressText.setText(listOfBooking.getFrom());
            toAddressText.setText(listOfBooking.getTo());
            bookingStatus.setText(listOfBooking.getBookingStatus());
            bookingStatusFinal = listOfBooking.getBookingStatusCode();
            bookingDriverMobileNumber = listOfBooking.getDriverMobileNumber();
            Log.e("DriverId", listOfBooking.getDriverId());
            Log.e("driverImage", "driverImage : " + listOfBooking.getDriverImage());
            String driverImage = "" + listOfBooking.getDriverImage();
            if (driverImage != null) {
                if (!driverImage.equalsIgnoreCase("null")) {
                    byte[] decodedString = Base64.decode(driverImage, Base64.DEFAULT);
                    Bitmap decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
                    user_view.setImageBitmap(decodedByte);
                }
            }

            if (bookingStatusFinal != null) {
                if (bookingStatusFinal.equals("SCHEDULED")) {
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

            if (!bookingStatusFinal.equals("COMPLETED")) {
                SingleInstantParameters loginCredentials = new SingleInstantParameters();
                loginCredentials.userId = "" + listOfBooking.getDriverId();
                getGeoLocationByDriverId(loginCredentials);
            }
        } else {
            bookingIdFinal = Utils.requestBookingResponse.getBookingId();
            driverNameText.setText(Utils.requestBookingResponse.getDriverName());
            cabTypeText.setText(Utils.requestBookingResponse.getCabType());
            fromAddressText.setText(Utils.requestBookingResponse.getFrom());
            toAddressText.setText(Utils.requestBookingResponse.getTo());
            bookingStatus.setText(Utils.requestBookingResponse.getBookingStatus());
            bookingStatusFinal = Utils.requestBookingResponse.getBookingStatusCode();
            bookingDriverMobileNumber = Utils.requestBookingResponse.getDriverMobileNumber();
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
            if (bookingStatusFinal != null) {
                if (bookingStatusFinal.equals("SCHEDULED")) {
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

            if (!bookingStatusFinal.equals("COMPLETED")) {
                SingleInstantParameters loginCredentials = new SingleInstantParameters();
                loginCredentials.userId = "" + Utils.requestBookingResponse.getDriverId();
                getGeoLocationByDriverId(loginCredentials);
            }
        }

        if (!bookingStatusFinal.equals("CANCELLED") && !bookingStatusFinal.equals("COMPLETED")) {
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
        super.onStop();
    }

    public void onEventMainThread(MessageReceivedEvent messageReceivedEvent) {
        Log.e("Thread message", "" + messageReceivedEvent.message);
        Log.e("Thread title", "" + messageReceivedEvent.title);
        Log.e("PUSH_NOTIFICATION", "PUSH_NOTIFICATION");
        if(!messageReceivedEvent.title.equals("BOOKING_PAYMENT_SUCCESS")) {
            SingleInstantParameters loginCredentials = new SingleInstantParameters();
            loginCredentials.bookingId = "" + bookingIdFinal;
            getBookingByBookingId(loginCredentials);
        }else {
            onBackPressed();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        SharedPreferences prefs = getSharedPreferences("LoginCredentials", MODE_PRIVATE);
        Gson gson = new Gson();
        String json = prefs.getString("LoginCredentials", "");
        Utils.verifyLogInUserMobileInstantResponse = gson.fromJson(json, SingleInstantResponse.class);
    }

    public void getBookingByBookingId(SingleInstantParameters loginCredentials) {
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

            Call<SingleInstantResponse> call = apiService.getBookingByBookingId(loginCredentials);
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
                        cabTypeText.setText(Utils.requestBookingResponse.getCabType());
                        fromAddressText.setText(Utils.requestBookingResponse.getFrom());
                        toAddressText.setText(Utils.requestBookingResponse.getTo());
                        bookingStatus.setText(Utils.requestBookingResponse.getBookingStatus());
                        bookingStatusFinal = Utils.requestBookingResponse.getBookingStatusCode();
                        bookingDriverMobileNumber = Utils.requestBookingResponse.getDriverMobileNumber();
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
                        if (bookingStatusFinal != null) {
                            if (bookingStatusFinal.equals("SCHEDULED")) {
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

                        if (!bookingStatusFinal.equals("COMPLETED")) {
                            SingleInstantParameters loginCredentials = new SingleInstantParameters();
                            loginCredentials.userId = "" + Utils.requestBookingResponse.getDriverId();
                            getGeoLocationByDriverId(loginCredentials);
                        }

                        if (bookingStatusFinal.equals("CANCELLED")) {
                            showInfoDlg("Booking Cancelled", "Requested booking has been cancelled. Try after sometime", "Done", "requestCancelled");
                        }

                        if (!bookingStatusFinal.equals("CANCELLED") && !bookingStatusFinal.equals("COMPLETED")) {
                            SharedPreferences.Editor editor = getSharedPreferences("BookingCredentials", MODE_PRIVATE).edit();
                            editor.putString("bookingId", bookingIdFinal);
                            editor.commit();
                        } else {
                            SharedPreferences.Editor editor = getSharedPreferences("BookingCredentials", MODE_PRIVATE).edit();
                            editor.putString("bookingId", "");
                            editor.commit();
                        }

                    } else {
                        dialog.dismiss();
                        try {
                            JSONObject jObjError = new JSONObject(response.errorBody().string());
                            showInfoDlg("Error..", "" + jObjError.getString("message"), "OK", "error");
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
                    Toast.makeText(DriverInfoBookingActivity.this, "Either there is no network connectivity or server is not available.. Please try again later..", Toast.LENGTH_LONG).show();
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

            Call<SingleInstantResponse> call = apiService.getBookingByBookingId(loginCredentials);
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
                        cabTypeText.setText(Utils.requestBookingResponse.getCabType());
                        fromAddressText.setText(Utils.requestBookingResponse.getFrom());
                        toAddressText.setText(Utils.requestBookingResponse.getTo());
                        bookingStatus.setText(Utils.requestBookingResponse.getBookingStatus());
                        bookingStatusFinal = Utils.requestBookingResponse.getBookingStatusCode();
                        bookingDriverMobileNumber = Utils.requestBookingResponse.getDriverMobileNumber();
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
                        if (bookingStatusFinal != null) {
                            if (bookingStatusFinal.equals("SCHEDULED")) {
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

                        if (!bookingStatusFinal.equals("COMPLETED")) {
                            SingleInstantParameters loginCredentials = new SingleInstantParameters();
                            loginCredentials.userId = "" + Utils.requestBookingResponse.getDriverId();
                            getGeoLocationByDriverId(loginCredentials);
                        }

                        if (!bookingStatusFinal.equals("CANCELLED") && !bookingStatusFinal.equals("COMPLETED")) {
                            SharedPreferences.Editor editor = getSharedPreferences("BookingCredentials", MODE_PRIVATE).edit();
                            editor.putString("bookingId", bookingIdFinal);
                            editor.commit();
                        } else {
                            SharedPreferences.Editor editor = getSharedPreferences("BookingCredentials", MODE_PRIVATE).edit();
                            editor.putString("bookingId", "");
                            editor.commit();
                        }

                    } else {
                        dialog.dismiss();
                        try {
                            JSONObject jObjError = new JSONObject(response.errorBody().string());
                            showInfoDlg("Error..", "" + jObjError.getString("message"), "OK", "error");
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
                    Toast.makeText(DriverInfoBookingActivity.this, "Either there is no network connectivity or server is not available.. Please try again later..", Toast.LENGTH_LONG).show();
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

            Call<SingleInstantResponse> call = apiService.updateBookingStatus(loginCredentials);
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
                        Utils.updateBookingStatusInstantResponse = response.body();
                        Log.e("BookingStatus", "" + Utils.updateBookingStatusInstantResponse.getBookingStatus());
                        showInfoDlg("Success..", "Your ZipRyde has been cancelled.", "Ok", "success");
                    } else {
                        try {
                            JSONObject jObjError = new JSONObject(response.errorBody().string());
                            showInfoDlg("Error..", "" + jObjError.getString("message"), "Ok", "error");
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
                    Toast.makeText(DriverInfoBookingActivity.this, "Either there is no network connectivity or server is not available.. Please try again later..", Toast.LENGTH_LONG).show();
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
        if (bookingStatusFinal.equals("COMPLETED") || bookingStatusFinal.equals("CANCELLED")) {
            if (handler != null && finalizer != null) {
                handler.removeCallbacks(finalizer);
            }
            Intent ide = new Intent(DriverInfoBookingActivity.this, NavigationMenuActivity.class);
            ide.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(ide);
            finish();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (handler != null && finalizer != null) {
            handler.removeCallbacks(finalizer);
        }
    }

    public void getGeoLocationByDriverId(final SingleInstantParameters loginCredentials) {
        if (Utils.connectivity(DriverInfoBookingActivity.this)) {
            Gson gson = new Gson();
            String json = gson.toJson(loginCredentials);
            Log.e("GeoLocationId json", "" + json);
            Call<SingleInstantResponse> call = apiService.getGeoLocationByDriverId(loginCredentials);
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
                        Log.e("Latitude", "" + Utils.getGeoLocationByDriverIdResponse.getLatitude());
                        Log.e("Longitude", "" + Utils.getGeoLocationByDriverIdResponse.getLongitude());

                        if (Utils.getGeoLocationByDriverIdResponse.getLatitude() != null && Utils.getGeoLocationByDriverIdResponse.getLongitude() != null) {
                            LatLng driverLatLng = new LatLng(Double.parseDouble(Utils.getGeoLocationByDriverIdResponse.getLatitude()), Double.parseDouble(Utils.getGeoLocationByDriverIdResponse.getLongitude()));

                            if (driverMarker != null)
                                driverMarker.remove();

                            driverMarker = mMap.addMarker(new MarkerOptions().anchor(0.5f, 0.5f).position(driverLatLng).icon(BitmapDescriptorFactory.fromResource(R.drawable.movingcar_48)));

                            finalizer = new Runnable() {
                                public void run() {
                                    Log.e("bookingStatusFinal", "" + bookingStatusFinal);
                                    if (!bookingStatusFinal.equals("COMPLETED")) {
                                        if (!bookingStatusFinal.equals("CANCELLED")) {
                                            getGeoLocationByDriverId(loginCredentials);
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
                                            getGeoLocationByDriverId(loginCredentials);
                                        }
                                    }
                                }
                            };
                            handler.postDelayed(finalizer, 10000);
                        }
                    } else {
                        try {
                            JSONObject jObjError = new JSONObject(response.errorBody().string());
                            showInfoDlg("Error..", "" + jObjError.getString("message"), "OK", "error");
                        } catch (Exception e) {
                            Toast.makeText(DriverInfoBookingActivity.this, "Either there is no network connectivity or server is not available.. Please try again later..", Toast.LENGTH_LONG).show();
                        }
                    }
                }

                @Override
                public void onFailure(Call<SingleInstantResponse> call, Throwable t) {
                    // Log error here since request failed
                    Log.e("onFailure", t.toString());
                    Toast.makeText(DriverInfoBookingActivity.this, "Either there is no network connectivity or server is not available.. Please try again later..", Toast.LENGTH_LONG).show();
                }
            });
        } else {
            Toast.makeText(DriverInfoBookingActivity.this, "Either there is no network connectivity or server is not available.. Please try again later..", Toast.LENGTH_LONG).show();
        }
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
        if (navType.equalsIgnoreCase("info") || navType.equalsIgnoreCase("error") || navType.equalsIgnoreCase("server") || navType.equals("requestCancelled")) {
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
        mMap.setOnMapLoadedCallback(new GoogleMap.OnMapLoadedCallback() {
            @Override
            public void onMapLoaded() {
                //markers.add(endingMarker);
                if (markers.size() > 0) {
                    LatLngBounds.Builder builder = new LatLngBounds.Builder();
                    for (Marker marker : markers) {
                        builder.include(marker.getPosition());
                    }
                    LatLngBounds bounds = builder.build();
                    int padding = 0; // offset from edges of the map in pixels
                    CameraUpdate cu = CameraUpdateFactory.newLatLngBounds(bounds, padding);
                    mMap.moveCamera(cu);
                    mMap.animateCamera(cu);
                    mMap.animateCamera(CameraUpdateFactory.zoomTo(8), 1000, null);
                }
            }
        });

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
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(origin, 15));
            mMap.animateCamera(CameraUpdateFactory.zoomIn());
            mMap.animateCamera(CameraUpdateFactory.zoomTo(15), 2000, null);
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
                    mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(origin, 15));
                    mMap.animateCamera(CameraUpdateFactory.zoomIn());
                    mMap.animateCamera(CameraUpdateFactory.zoomTo(15), 2000, null);
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

            }

            // Drawing polyline in the Google Map for the i-th route
            if (lineOptions != null) {
                if(displayType.equalsIgnoreCase("")) {
                    polyline = mMap.addPolyline(lineOptions);
                }
                if(!displayType.equalsIgnoreCase("")) {
                    driverArrivingTime.setText(Utils.parsedDuration);
                }
            } else {
                Log.d("onPostExecute", "without Polylines drawn");
            }
        }
    }
}
