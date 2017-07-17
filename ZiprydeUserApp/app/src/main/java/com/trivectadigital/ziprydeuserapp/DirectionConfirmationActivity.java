package com.trivectadigital.ziprydeuserapp;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
import android.os.Handler;
import android.provider.Settings;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.AppCompatTextView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;

import com.trivectadigital.ziprydeuserapp.apis.ZiprydeApiClient;
import com.trivectadigital.ziprydeuserapp.apis.ZiprydeApiInterface;
import com.trivectadigital.ziprydeuserapp.assist.DataParser;
import com.trivectadigital.ziprydeuserapp.assist.Utils;
import com.trivectadigital.ziprydeuserapp.modelget.ListOfCarTypes;
import com.trivectadigital.ziprydeuserapp.modelget.ListOfFairEstimate;
import com.trivectadigital.ziprydeuserapp.modelpost.SingleInstantParameters;
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
import com.google.android.gms.maps.model.PolylineOptions;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class DirectionConfirmationActivity extends AppCompatActivity implements OnMapReadyCallback {

    private GoogleMap mMap;

    int REQUEST_CHECK_SETTINGS = 100;
    static final Integer LOCATION = 0x1;

    private AppCompatTextView searchPlace, searchPlaceDestination;

    TextView microText, microInfoText, miniText, miniInfoText, sedanText, sedanInfoText
            , amountText1, amountText2, amountText3, amountText4, carTypeText;
    RelativeLayout microLay, miniLay, sedanLay;

    ImageView micro_circle_small, micro_circle_big;
    ImageView mini_circle_small, mini_circle_big;
    ImageView suv_circle_small, suv_circle_big;
    ImageView micro_car_small, micro_car_big;
    ImageView mini_car_small, mini_car_big;
    ImageView suv_car_small, suv_car_big;

    LinearLayout fairBooking, carTypeLay, fairDetailsLay;

    boolean fairDetailsVisible = false;

    ZiprydeApiInterface apiService;

    int selectedCarType = 2;
    String selectedCarModel = "Mini";

    LinearLayout microLaySmall, microLayBig, suvLayBig, suvLaySmall, sedanLaySmall, sedanLayBig, vehicleTypeLay, requestTypeLay;
    TextView microTextSmall, microTextBig, sedanTextSmall, sedanTextBig, suvTextSmall, suvTextBig, textSeatCapacity;
    ImageView imgNext;
    Button getFareDetailsBtn, requestPickupBtn;
    Spinner noofSeatsSpinner;
    TextView textAmount1, textAmount2, textAmount3, textAmount4, basePrice;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_direction_confirmation);

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
        titleText.setText("ZIPRYDE");
        ImageView backImg = (ImageView) mCustomView.findViewById(R.id.backImg);
        backImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        searchPlace = (AppCompatTextView) findViewById(R.id.searchPlace);
        searchPlace.setText(""+ Utils.startingPlaceAddress);
        searchPlaceDestination = (AppCompatTextView) findViewById(R.id.searchPlaceDestination);
        searchPlaceDestination.setText(""+Utils.endingPlaceAddress);

        microLaySmall = (LinearLayout) findViewById(R.id.microLaySmall);
        microLayBig = (LinearLayout) findViewById(R.id.microLayBig);
        suvLayBig = (LinearLayout) findViewById(R.id.suvLayBig);
        suvLaySmall = (LinearLayout) findViewById(R.id.suvLaySmall);
        sedanLaySmall = (LinearLayout) findViewById(R.id.sedanLaySmall);
        sedanLayBig = (LinearLayout) findViewById(R.id.sedanLayBig);
        vehicleTypeLay = (LinearLayout) findViewById(R.id.vehicleTypeLay);
        requestTypeLay = (LinearLayout) findViewById(R.id.requestTypeLay);

        microTextSmall = (TextView) findViewById(R.id.microTextSmall);
        microTextBig = (TextView) findViewById(R.id.microTextBig);
        sedanTextSmall = (TextView) findViewById(R.id.sedanTextSmall);
        sedanTextBig = (TextView) findViewById(R.id.sedanTextBig);
        suvTextSmall = (TextView) findViewById(R.id.suvTextSmall);
        suvTextBig = (TextView) findViewById(R.id.suvTextBig);
        textSeatCapacity = (TextView) findViewById(R.id.textSeatCapacity);

        textAmount1 = (TextView) findViewById(R.id.textAmount1);
        textAmount2 = (TextView) findViewById(R.id.textAmount2);
        textAmount3 = (TextView) findViewById(R.id.textAmount3);
        textAmount4 = (TextView) findViewById(R.id.textAmount4);
        basePrice = (TextView) findViewById(R.id.basePrice);

        imgNext = (ImageView) findViewById(R.id.imgNext);
        getFareDetailsBtn = (Button) findViewById(R.id.getFareDetailsBtn);
        requestPickupBtn = (Button) findViewById(R.id.requestPickupBtn);
        noofSeatsSpinner = (Spinner) findViewById(R.id.noofSeatsSpinner);

        microLay = (RelativeLayout) findViewById(R.id.microLay);
        miniLay = (RelativeLayout) findViewById(R.id.miniLay);
        sedanLay = (RelativeLayout) findViewById(R.id.sedanLay);

        fairBooking = (LinearLayout) findViewById(R.id.fairBooking);
        carTypeLay = (LinearLayout) findViewById(R.id.carTypeLay);
        fairDetailsLay = (LinearLayout) findViewById(R.id.fairDetailsLay);

        micro_circle_small = (ImageView) findViewById(R.id.micro_circle_small);
        micro_circle_big = (ImageView) findViewById(R.id.micro_circle_big);

        mini_circle_small = (ImageView) findViewById(R.id.mini_circle_small);
        mini_circle_big = (ImageView) findViewById(R.id.mini_circle_big);

        suv_circle_small = (ImageView) findViewById(R.id.suv_circle_small);
        suv_circle_big = (ImageView) findViewById(R.id.suv_circle_big);

        micro_car_small = (ImageView) findViewById(R.id.micro_car_small);
        micro_car_big = (ImageView) findViewById(R.id.micro_car_big);

        mini_car_small = (ImageView) findViewById(R.id.mini_car_small);
        mini_car_big = (ImageView) findViewById(R.id.mini_car_big);

        suv_car_small = (ImageView) findViewById(R.id.suv_car_small);
        suv_car_big = (ImageView) findViewById(R.id.suv_car_big);

        microText = (TextView) findViewById(R.id.microText);
        microInfoText = (TextView) findViewById(R.id.microInfoText);
        miniText = (TextView) findViewById(R.id.miniText);
        miniInfoText = (TextView) findViewById(R.id.miniInfoText);
        sedanText = (TextView) findViewById(R.id.sedanText);
        sedanInfoText = (TextView) findViewById(R.id.sedanInfoText);

        amountText1 = (TextView) findViewById(R.id.amountText1);
        amountText2 = (TextView) findViewById(R.id.amountText2);
        amountText3 = (TextView) findViewById(R.id.amountText3);
        amountText4 = (TextView) findViewById(R.id.amountText4);

        carTypeText = (TextView) findViewById(R.id.carTypeText);

        microLay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                changeTextColorToBlack(miniText, sedanText, miniInfoText, sedanInfoText);
                changeTextColorToOrange(microText, microInfoText);
                changeCircleBigtoSmall(mini_circle_small, suv_circle_small, mini_car_small, suv_car_small, micro_circle_big, micro_car_big);
                changeCircleSmalltoBig(mini_circle_big, suv_circle_big, mini_car_big, suv_car_big, micro_circle_small, micro_car_small);
                selectedCarType = 1;
                selectedCarModel = "Micro";
                carTypeText.setText(selectedCarModel);
            }
        });

        miniLay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                changeTextColorToBlack(microText, sedanText, microInfoText, sedanInfoText);
                changeTextColorToOrange(miniText, miniInfoText);
                changeCircleBigtoSmall(micro_circle_small, suv_circle_small, micro_car_small, suv_car_small, mini_circle_big, mini_car_big);
                changeCircleSmalltoBig(micro_circle_big, micro_car_big, suv_circle_big, suv_car_big, mini_circle_small, mini_car_small);
                selectedCarType = 2;
                selectedCarModel = "Mini";
                carTypeText.setText(selectedCarModel);
            }
        });

        sedanLay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                changeTextColorToBlack(microText, miniText, microInfoText, miniInfoText);
                changeTextColorToOrange(sedanText, sedanInfoText);
                changeCircleBigtoSmall(micro_circle_small, micro_car_small, mini_circle_small, mini_car_small, suv_circle_big, suv_car_big);
                changeCircleSmalltoBig(micro_circle_big, micro_car_big, mini_circle_big, mini_car_big, suv_circle_small, suv_car_small);
                selectedCarType = 3;
                selectedCarModel = "Sedan";
                carTypeText.setText(selectedCarModel);
            }
        });

        fairBooking.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                final Dialog dialog = new Dialog(DirectionConfirmationActivity.this, android.R.style.Theme_Dialog);
                dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                dialog.setContentView(R.layout.noofpassenger_layout);

                TextView passenger1 = (TextView) dialog.findViewById(R.id.passenger1);
                TextView passenger2 = (TextView) dialog.findViewById(R.id.passenger2);
                TextView passenger3 = (TextView) dialog.findViewById(R.id.passenger3);
                TextView passenger4 = (TextView) dialog.findViewById(R.id.passenger4);

                dialog.setCanceledOnTouchOutside(false);
                dialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
                dialog.getWindow().setLayout(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                dialog.show();

                passenger1.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.dismiss();
                        sendPassengerInformation(1);
                    }
                });
                passenger2.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.dismiss();
                        sendPassengerInformation(2);
                    }
                });

                passenger3.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.dismiss();
                        sendPassengerInformation(3);
                    }
                });
                passenger4.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.dismiss();
                        sendPassengerInformation(4);
                    }
                });

            }
        });

        amountText1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                changeTextBgColorToOrange(amountText1, amountText2, amountText3, amountText4);
            }
        });

        amountText2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                changeTextBgColorToOrange(amountText2, amountText1, amountText3, amountText4);
            }
        });

        amountText3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                changeTextBgColorToOrange(amountText3, amountText2, amountText1, amountText4);
            }
        });

        amountText4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                changeTextBgColorToOrange(amountText4, amountText1, amountText2, amountText3);
            }
        });

        LinearLayout reqBooking = (LinearLayout) findViewById(R.id.reqBooking);
        requestPickupBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Dialog dialog = new Dialog(DirectionConfirmationActivity.this, android.R.style.Theme_Dialog);
                dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                dialog.setContentView(R.layout.loadingimage_layout);
                dialog.setCanceledOnTouchOutside(false);
                dialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
                dialog.getWindow().setLayout(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                dialog.show();
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        dialog.dismiss();
                        showInfoDlg("Booking Successful", "Your Zipryde has been confirmed. Driver will pick up you in 4 minutes.", "Done", "successBooking");
                    }
                }, 1000);

            }
        });

        microLaySmall.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getFareDetailsBtn.setTag(Utils.getAllCabTypesInstantResponse.get(0).getCabTypeId());
                microLayBig.setVisibility(View.VISIBLE);
                suvLaySmall.setVisibility(View.VISIBLE);
                sedanLaySmall.setVisibility(View.VISIBLE);
                microLaySmall.setVisibility(View.GONE);
                suvLayBig.setVisibility(View.GONE);
                sedanLayBig.setVisibility(View.GONE);
                ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(DirectionConfirmationActivity.this,
                        R.array.sedan_micro_array, android.R.layout.simple_spinner_item);
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                noofSeatsSpinner.setAdapter(adapter);
                noofSeatsSpinner.setSelection(0, true);
                textSeatCapacity.setText("Seats 1-4");
            }
        });

        suvLaySmall.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getFareDetailsBtn.setTag(Utils.getAllCabTypesInstantResponse.get(2).getCabTypeId());
                microLaySmall.setVisibility(View.VISIBLE);
                suvLayBig.setVisibility(View.VISIBLE);
                sedanLaySmall.setVisibility(View.VISIBLE);
                microLayBig.setVisibility(View.GONE);
                suvLaySmall.setVisibility(View.GONE);
                sedanLayBig.setVisibility(View.GONE);
                ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(DirectionConfirmationActivity.this,
                        R.array.suv_array, android.R.layout.simple_spinner_item);
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                noofSeatsSpinner.setAdapter(adapter);
                noofSeatsSpinner.setSelection(0, true);
                textSeatCapacity.setText("Seats 1-7");
            }
        });

        sedanLaySmall.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getFareDetailsBtn.setTag(Utils.getAllCabTypesInstantResponse.get(1).getCabTypeId());
                microLaySmall.setVisibility(View.VISIBLE);
                suvLayBig.setVisibility(View.GONE);
                sedanLaySmall.setVisibility(View.GONE);
                microLayBig.setVisibility(View.GONE);
                suvLaySmall.setVisibility(View.VISIBLE);
                sedanLayBig.setVisibility(View.VISIBLE);
                ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(DirectionConfirmationActivity.this,
                        R.array.sedan_micro_array, android.R.layout.simple_spinner_item);
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                noofSeatsSpinner.setAdapter(adapter);
                noofSeatsSpinner.setSelection(0, true);
                textSeatCapacity.setText("Seats 1-4");
            }
        });

        getFareDetailsBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                fairDetailsVisible = true;
                vehicleTypeLay.setVisibility(View.GONE);
                requestTypeLay.setVisibility(View.VISIBLE);
                selectedCarType = Integer.parseInt(getFareDetailsBtn.getTag().toString().trim());
                SingleInstantParameters singleInstantParameters = new SingleInstantParameters();
                singleInstantParameters.cabTypeId = selectedCarType;
                String km = Utils.parsedDistance.split(" ")[0].trim();
                String kmtomile = ""+(Double.parseDouble(km) * 0.6214);
                Log.e("kmtomile",""+kmtomile);
                Log.e("selectedCarType",""+selectedCarType);
                int count = Integer.parseInt(noofSeatsSpinner.getSelectedItem().toString().trim());
                Log.e("count",""+count);
                singleInstantParameters.distanceInMiles = kmtomile;
                singleInstantParameters.noOfPassengers = count;
                getFairEstimateDetails(singleInstantParameters);
            }
        });

        noofSeatsSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                ((TextView) view).setTextColor(getResources().getColor(R.color.whiteColor));
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        textAmount1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                textAmount1.setBackgroundResource(R.drawable.rounded_button);
                textAmount1.setTextColor(getResources().getColor(R.color.primaryText));
                textAmount2.setBackgroundResource(0);
                textAmount2.setTextColor(getResources().getColor(R.color.whiteColor));
                textAmount3.setBackgroundResource(0);
                textAmount3.setTextColor(getResources().getColor(R.color.whiteColor));
                textAmount4.setBackgroundResource(0);
                textAmount4.setTextColor(getResources().getColor(R.color.whiteColor));
            }
        });

        textAmount2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                textAmount2.setBackgroundResource(R.drawable.rounded_button);
                textAmount2.setTextColor(getResources().getColor(R.color.primaryText));
                textAmount1.setBackgroundResource(0);
                textAmount1.setTextColor(getResources().getColor(R.color.whiteColor));
                textAmount3.setBackgroundResource(0);
                textAmount3.setTextColor(getResources().getColor(R.color.whiteColor));
                textAmount4.setBackgroundResource(0);
                textAmount4.setTextColor(getResources().getColor(R.color.whiteColor));
            }
        });

        textAmount3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                textAmount3.setBackgroundResource(R.drawable.rounded_button);
                textAmount3.setTextColor(getResources().getColor(R.color.primaryText));
                textAmount1.setBackgroundResource(0);
                textAmount1.setTextColor(getResources().getColor(R.color.whiteColor));
                textAmount2.setBackgroundResource(0);
                textAmount2.setTextColor(getResources().getColor(R.color.whiteColor));
                textAmount4.setBackgroundResource(0);
                textAmount4.setTextColor(getResources().getColor(R.color.whiteColor));
            }
        });

        textAmount4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                textAmount4.setBackgroundResource(R.drawable.rounded_button);
                textAmount4.setTextColor(getResources().getColor(R.color.primaryText));
                textAmount1.setBackgroundResource(0);
                textAmount1.setTextColor(getResources().getColor(R.color.whiteColor));
                textAmount2.setBackgroundResource(0);
                textAmount2.setTextColor(getResources().getColor(R.color.whiteColor));
                textAmount3.setBackgroundResource(0);
                textAmount3.setTextColor(getResources().getColor(R.color.whiteColor));
            }
        });

        getAllCabTypes();
    }

    public void sendPassengerInformation(int count){
        SingleInstantParameters singleInstantParameters = new SingleInstantParameters();
        singleInstantParameters.cabTypeId = selectedCarType;
        String km = Utils.parsedDistance.split(" ")[0].trim();
        String kmtomile = ""+(Double.parseDouble(km) * 0.6214);
        Log.e("kmtomile",""+kmtomile);
        Log.e("selectedCarType",""+selectedCarType);
        Log.e("count",""+count);
        singleInstantParameters.distanceInMiles = kmtomile;
        singleInstantParameters.noOfPassengers = count;
        getFairEstimateDetails(singleInstantParameters);
        fairDetailsVisible = true;
        carTypeLay.setVisibility(View.GONE);
        fairDetailsLay.setVisibility(View.VISIBLE);
    }

    private void getAllCabTypes(){
        final Dialog dialog = new Dialog(DirectionConfirmationActivity.this, android.R.style.Theme_Dialog);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.loadingimage_layout);
        dialog.setCanceledOnTouchOutside(false);
        dialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        dialog.getWindow().setLayout(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        dialog.show();

        Call<LinkedList<ListOfCarTypes>> call = apiService.getAllCabTypes();
        call.enqueue(new Callback<LinkedList<ListOfCarTypes>>() {
            @Override
            public void onResponse(Call<LinkedList<ListOfCarTypes>> call, Response<LinkedList<ListOfCarTypes>> response) {
                int statusCode = response.code();
                Log.e("statusCode",""+statusCode);
                Log.e("response.body",""+response.body());
                Log.e("response.errorBody",""+response.errorBody());
                Log.e("response.isSuccessful",""+response.isSuccessful());
                dialog.dismiss();
                if(response.isSuccessful()){
                    Utils.getAllCabTypesInstantResponse = response.body();
                    Log.e("Size",""+ Utils.getAllCabTypesInstantResponse.size());
                    selectedCarType = 2;
                    selectedCarModel = "Mini";
                    carTypeText.setText(selectedCarModel);
                    for(int i = 0; i < Utils.getAllCabTypesInstantResponse.size(); i++){
                        Log.e("CARTYPE",""+ Utils.getAllCabTypesInstantResponse.get(i).getType());
                        if(i == 0){
                            microTextSmall.setText(Utils.getAllCabTypesInstantResponse.get(i).getType());
                            getFareDetailsBtn.setTag(Utils.getAllCabTypesInstantResponse.get(i).getCabTypeId());
                            microTextBig.setText(Utils.getAllCabTypesInstantResponse.get(i).getType());
                        }else if(i == 1){
                            sedanTextSmall.setText(Utils.getAllCabTypesInstantResponse.get(i).getType());
                            sedanTextBig.setText(Utils.getAllCabTypesInstantResponse.get(i).getType());
                        }else {
                            suvTextSmall.setText(Utils.getAllCabTypesInstantResponse.get(i).getType());
                            getFareDetailsBtn.setTag(Utils.getAllCabTypesInstantResponse.get(i).getCabTypeId());
                            suvTextBig.setText(Utils.getAllCabTypesInstantResponse.get(i).getType());
                        }
                    }
                    ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(DirectionConfirmationActivity.this,
                            R.array.suv_array, android.R.layout.simple_spinner_item);
                    adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    noofSeatsSpinner.setAdapter(adapter);
                    noofSeatsSpinner.setSelection(0, true);
                    //showInfoDlg("Success..", "Successfully registered.", "Ok", "success");
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
            public void onFailure(Call<LinkedList<ListOfCarTypes>> call, Throwable t) {
                // Log error here since request failed
                Log.e("onFailure", t.toString());
                dialog.dismiss();
                showInfoDlg("Error..", "Either there is no network connectivity or server is not available.. Please try again later..", "Ok", "server");
            }
        });
    }

    private void getFairEstimateDetails(SingleInstantParameters singleInstantParameters){
        final Dialog dialog = new Dialog(DirectionConfirmationActivity.this, android.R.style.Theme_Dialog);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.loadingimage_layout);
        dialog.setCanceledOnTouchOutside(false);
        dialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        dialog.getWindow().setLayout(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        dialog.show();

        Call<LinkedList<ListOfFairEstimate>> call = apiService.getAllNYOPByCabTypeAndDistance(singleInstantParameters);
        call.enqueue(new Callback<LinkedList<ListOfFairEstimate>>() {
            @Override
            public void onResponse(Call<LinkedList<ListOfFairEstimate>> call, Response<LinkedList<ListOfFairEstimate>> response) {
                int statusCode = response.code();
                Log.e("statusCode",""+statusCode);
                Log.e("response.body",""+response.body());
                Log.e("response.errorBody",""+response.errorBody());
                Log.e("response.isSuccessful",""+response.isSuccessful());
                dialog.dismiss();
                if(response.isSuccessful()){
                    Utils.getAllNYOPByCabTypeAndDistanceInstantResponse = response.body();
                    Log.e("size",""+Utils.getAllNYOPByCabTypeAndDistanceInstantResponse.size());
                    if(Utils.getAllNYOPByCabTypeAndDistanceInstantResponse.size() > 0){
                        Log.e("getPrice",""+Utils.getAllNYOPByCabTypeAndDistanceInstantResponse.get(0).getPrice());
                        double price = Double.parseDouble(Utils.getAllNYOPByCabTypeAndDistanceInstantResponse.get(0).getPrice());
                        Log.e("price 11",""+price);
                        textAmount1.setText("$"+new DecimalFormat("##.#").format(price));
                        basePrice.setText("$"+new DecimalFormat("##.#").format(price));
                    }

                    if(Utils.getAllNYOPByCabTypeAndDistanceInstantResponse.size() > 1){
                        Log.e("getPrice",""+Utils.getAllNYOPByCabTypeAndDistanceInstantResponse.get(1).getPrice());
                        double price = Double.parseDouble(Utils.getAllNYOPByCabTypeAndDistanceInstantResponse.get(1).getPrice());
                        Log.e("price 22",""+price);
                        textAmount2.setText("$"+new DecimalFormat("##.#").format(price));
                    }

                    if(Utils.getAllNYOPByCabTypeAndDistanceInstantResponse.size() > 2){
                        Log.e("getPrice",""+Utils.getAllNYOPByCabTypeAndDistanceInstantResponse.get(2).getPrice());
                        double price = Double.parseDouble(Utils.getAllNYOPByCabTypeAndDistanceInstantResponse.get(2).getPrice());
                        Log.e("price 33",""+price);
                        textAmount3.setText("$"+new DecimalFormat("##.#").format(price));
                    }

                    if(Utils.getAllNYOPByCabTypeAndDistanceInstantResponse.size() > 3){
                        Log.e("getPrice",""+Utils.getAllNYOPByCabTypeAndDistanceInstantResponse.get(3).getPrice());
                        double price = Double.parseDouble(Utils.getAllNYOPByCabTypeAndDistanceInstantResponse.get(3).getPrice());
                        Log.e("price 44",""+price);
                        textAmount4.setText("$"+new DecimalFormat("##.#").format(price));
                    }
                    //showInfoDlg("Success..", "Successfully registered.", "Ok", "success");
                }else{
                    try {
                        JSONObject jObjError = new JSONObject(response.errorBody().string());
                        showInfoDlg("Error..", ""+jObjError.getString("message"), "Ok", "error");
                    } catch (Exception e) {
                        e.printStackTrace();
                        showInfoDlg("Error..", "Either there is no network connectivity or server is not available.. Please try again later..", "Ok", "server");
                    }
                }
            }

            @Override
            public void onFailure(Call<LinkedList<ListOfFairEstimate>> call, Throwable t) {
                // Log error here since request failed
                Log.e("onFailure", t.toString());
                dialog.dismiss();
                showInfoDlg("Error..", "Either there is no network connectivity or server is not available.. Please try again later..", "Ok", "server");
            }
        });
    }

    private void changeTextBgColorToOrange(TextView amountText1, TextView amountText2, TextView amountText3, TextView amountText4){
        amountText1.setBackgroundResource(R.color.colorPrimary);

        amountText2.setBackgroundColor(Color.parseColor("#F4F8F9"));
        amountText3.setBackgroundColor(Color.parseColor("#F4F8F9"));
        amountText4.setBackgroundColor(Color.parseColor("#F4F8F9"));
    }

    Dialog dialog;
    private void showInfoDlg(String title, String content, String btnText, final String navType) {
        dialog = new Dialog(DirectionConfirmationActivity.this, android.R.style.Theme_Dialog);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.infodialog_layout);
        //dialog.setCanceledOnTouchOutside(true);

        Button positiveBtn = (Button) dialog.findViewById(R.id.positiveBtn);
        positiveBtn.setText(""+btnText);

        Button newnegativeBtn = (Button) dialog.findViewById(R.id.newnegativeBtn);
        if(navType.equalsIgnoreCase("gps")){
            newnegativeBtn.setVisibility(View.GONE);
        }else{
            newnegativeBtn.setVisibility(View.VISIBLE);
        }

        ImageView headerIcon = (ImageView) dialog.findViewById(R.id.headerIcon);
        if(navType.equalsIgnoreCase("successBooking")){
            headerIcon.setImageResource(R.drawable.successicon);
            newnegativeBtn.setVisibility(View.GONE);
        }

        TextView dialogtitleText = (TextView) dialog.findViewById(R.id.dialogtitleText);
        dialogtitleText.setText(""+title);
        TextView dialogcontentText = (TextView) dialog.findViewById(R.id.dialogcontentText);
        dialogcontentText.setText(""+content);

        positiveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                if(navType.equalsIgnoreCase("gps")){
                    startActivityForResult(new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS), REQUEST_CHECK_SETTINGS);
                }else if(navType.equalsIgnoreCase("successBooking")){
                    Intent ide = new Intent(DirectionConfirmationActivity.this, DriverInfoBookingActivity.class);
                    ide.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(ide);
                    finish();
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

    public void changeCircleSmalltoBig(ImageView imageView1, ImageView imageView2, ImageView imageView3, ImageView imageView4, ImageView imageView5, ImageView imageView6){
        imageView1.setVisibility(View.GONE);
        imageView2.setVisibility(View.GONE);
        imageView3.setVisibility(View.GONE);
        imageView4.setVisibility(View.GONE);
        imageView5.setVisibility(View.GONE);
        imageView6.setVisibility(View.GONE);
    }

    public void changeCircleBigtoSmall(ImageView imageView1, ImageView imageView2, ImageView imageView3, ImageView imageView4, ImageView imageView5, ImageView imageView6){
        imageView1.setVisibility(View.VISIBLE);
        imageView2.setVisibility(View.VISIBLE);
        imageView3.setVisibility(View.VISIBLE);
        imageView4.setVisibility(View.VISIBLE);
        imageView5.setVisibility(View.VISIBLE);
        imageView6.setVisibility(View.VISIBLE);
    }

    public void changeTextColorToBlack(TextView textView1, TextView textView2, TextView textView3, TextView textView4){
        textView1.setTextColor(getResources().getColor(R.color.primaryText));
        textView2.setTextColor(getResources().getColor(R.color.primaryText));
        textView3.setTextColor(getResources().getColor(R.color.black_overlay));
        textView4.setTextColor(getResources().getColor(R.color.black_overlay));
    }

    public void changeTextColorToOrange(TextView textView1, TextView textView2){
        textView1.setTextColor(getResources().getColor(R.color.colorPrimary));
        textView2.setTextColor(getResources().getColor(R.color.primaryText));
    }

    @Override
    public void onBackPressed() {
        if(fairDetailsVisible){
            fairDetailsVisible = false;
            vehicleTypeLay.setVisibility(View.VISIBLE);
            requestTypeLay.setVisibility(View.GONE);
//            carTypeLay.setVisibility(View.VISIBLE);
//            fairDetailsLay.setVisibility(View.GONE);
        }else {
            finish();
        }
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
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        LatLng origin = Utils.startingLatLan;
        LatLng dest = Utils.endingLatLan;
        startingMarker = mMap.addMarker(new MarkerOptions().anchor(0.5f, 0.5f).position(origin).title(""+Utils.startingPlaceAddress).icon(BitmapDescriptorFactory.fromResource(R.drawable.startpoint)));
        startingMarker.setTag("starting");
        startingMarker.showInfoWindow();
        endingMarker = mMap.addMarker(new MarkerOptions().anchor(0.5f, 0.5f).position(dest).title(""+Utils.endingPlaceAddress).icon(BitmapDescriptorFactory.fromResource(R.drawable.endpoint)));
        endingMarker.setTag("ending");

        // Getting URL to the Google Directions API
        String url = getUrl(origin, dest);
        Log.d("url", ""+url);
        FetchUrl FetchUrl = new FetchUrl();
        // Start downloading json data from Google Directions API
        FetchUrl.execute(url);

        mMap.setOnMapLoadedCallback(new GoogleMap.OnMapLoadedCallback() {
            @Override
            public void onMapLoaded() {
                List<Marker> markers = new LinkedList<Marker>();
                markers.add(startingMarker);
                markers.add(endingMarker);
                LatLngBounds.Builder builder = new LatLngBounds.Builder();
                for (Marker marker : markers) {
                    builder.include(marker.getPosition());
                }
                LatLngBounds bounds = builder.build();
                int padding = 50; // offset from edges of the map in pixels
                CameraUpdate cu = CameraUpdateFactory.newLatLngBounds(bounds, padding);
                mMap.moveCamera(cu);
                mMap.animateCamera(cu);
            }
        });

        mMap.setInfoWindowAdapter(new GoogleMap.InfoWindowAdapter() {
            @Override
            public View getInfoWindow(Marker marker) {
                LayoutInflater li = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                View  mWindow = li.inflate(R.layout.infowindow_layout, null);
                TextView titleUi = ((TextView) mWindow.findViewById(R.id.title));
                if(marker.getTag().toString().equalsIgnoreCase("starting")) {
                    titleUi.setText(""+Utils.startingPlaceAddress);
                }else{
                    titleUi.setText(""+Utils.endingPlaceAddress);
                }
                return mWindow;
            }

            @Override
            public View getInfoContents(Marker marker) {
                return null;
            }
        });
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
        Log.e("url","url -- "+url);
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

            ParserTask parserTask = new ParserTask();
            // Invokes the thread for parsing the JSON data
            parserTask.execute(result);

        }
    }

    /**
     * A class to parse the Google Places in JSON format
     */
    private class ParserTask extends AsyncTask<String, Integer, List<List<HashMap<String, String>>>> {

        // Parsing the data in non-ui thread
        @Override
        protected List<List<HashMap<String, String>>> doInBackground(String... jsonData) {

            JSONObject jObject;
            List<List<HashMap<String, String>>> routes = null;

            try {
                jObject = new JSONObject(jsonData[0]);
                Log.d("ParserTask",jsonData[0].toString());
                DataParser parser = new DataParser();
                Log.d("ParserTask", parser.toString());

                // Starts parsing data
                routes = parser.parse(jObject);
                Log.d("ParserTask","Executing routes");
                Log.d("ParserTask",routes.toString());

            } catch (Exception e) {
                Log.d("ParserTask",e.toString());
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

                Log.d("onPostExecute","onPostExecute lineoptions decoded");

            }

            // Drawing polyline in the Google Map for the i-th route
            if(lineOptions != null) {
                mMap.addPolyline(lineOptions);
            }
            else {
                Log.d("onPostExecute","without Polylines drawn");
            }
        }
    }

}
