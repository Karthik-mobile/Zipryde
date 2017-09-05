package com.trivectadigital.ziprydeuserapp;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;
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
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.gson.Gson;
import com.trivectadigital.ziprydeuserapp.apis.ZiprydeApiClient;
import com.trivectadigital.ziprydeuserapp.apis.ZiprydeApiInterface;
import com.trivectadigital.ziprydeuserapp.assist.DataParser;
import com.trivectadigital.ziprydeuserapp.assist.MessageReceivedEvent;
import com.trivectadigital.ziprydeuserapp.assist.TimeDataParser;
import com.trivectadigital.ziprydeuserapp.assist.Utils;
import com.trivectadigital.ziprydeuserapp.modelget.ListOfCarTypes;
import com.trivectadigital.ziprydeuserapp.modelget.ListOfCurrentCabs;
import com.trivectadigital.ziprydeuserapp.modelget.ListOfFairEstimate;
import com.trivectadigital.ziprydeuserapp.modelget.SingleInstantResponse;
import com.trivectadigital.ziprydeuserapp.modelpost.GeoLocationRequest;
import com.trivectadigital.ziprydeuserapp.modelpost.SingleInstantParameters;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.TimeZone;

import de.greenrobot.event.EventBus;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class DirectionConfirmationActivity extends AppCompatActivity implements OnMapReadyCallback {

    private GoogleMap mMap;

    int REQUEST_CHECK_SETTINGS = 100;
    static final Integer LOCATION = 0x1;

    boolean fairDetailsVisible = false;
    boolean isScheduledTrip;

    ZiprydeApiInterface apiService;

    int selectedCarType = 2;
    String selectedCarModel = "Mini";

    LinearLayout microLaySmall, microLayBig, suvLayBig, suvLaySmall, sedanLaySmall, sedanLayBig, vehicleTypeLay, requestTypeLay, noCabsLay;
    TextView microTextSmall, microTextBig, sedanTextSmall, sedanTextBig, suvTextSmall, suvTextBig, textSeatCapacity;
    ImageView imgNext;
    Button getFareDetailsBtn, requestPickupBtn,requestPickupLaterBtn;
    Spinner noofSeatsSpinner;
    TextView textAmount1, textAmount2, textAmount3, textAmount4, basePrice, priceUpdateText, noCabsText;
    TextView microTimeTextSmall, microTimeTextBig, sedanTimeTextSmall, sedanTimeTextBig, suvTimeTextSmall, suvTimeTextBig;


    public Date userSelDate;
    DatePickerDialog schedulePickupTime;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_direction_confirmation);

        apiService = ZiprydeApiClient.getClient(Utils.verifyLogInUserMobileInstantResponse.getAccessToken()).create(ZiprydeApiInterface.class);

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        microLaySmall = (LinearLayout) findViewById(R.id.microLaySmall);
        microLayBig = (LinearLayout) findViewById(R.id.microLayBig);
        suvLayBig = (LinearLayout) findViewById(R.id.suvLayBig);
        suvLaySmall = (LinearLayout) findViewById(R.id.suvLaySmall);
        sedanLaySmall = (LinearLayout) findViewById(R.id.sedanLaySmall);
        sedanLayBig = (LinearLayout) findViewById(R.id.sedanLayBig);
        vehicleTypeLay = (LinearLayout) findViewById(R.id.vehicleTypeLay);
        requestTypeLay = (LinearLayout) findViewById(R.id.requestTypeLay);
        noCabsLay = (LinearLayout) findViewById(R.id.noCabsLay);

        microTextSmall = (TextView) findViewById(R.id.microTextSmall);
        microTextBig = (TextView) findViewById(R.id.microTextBig);
        sedanTextSmall = (TextView) findViewById(R.id.sedanTextSmall);
        sedanTextBig = (TextView) findViewById(R.id.sedanTextBig);
        suvTextSmall = (TextView) findViewById(R.id.suvTextSmall);
        suvTextBig = (TextView) findViewById(R.id.suvTextBig);
        textSeatCapacity = (TextView) findViewById(R.id.textSeatCapacity);
        noCabsText = (TextView) findViewById(R.id.noCabsText);

        textAmount1 = (TextView) findViewById(R.id.textAmount1);
        textAmount2 = (TextView) findViewById(R.id.textAmount2);
        textAmount3 = (TextView) findViewById(R.id.textAmount3);
        textAmount4 = (TextView) findViewById(R.id.textAmount4);
        basePrice = (TextView) findViewById(R.id.basePrice);
        priceUpdateText = (TextView) findViewById(R.id.priceUpdateText);

        microTimeTextSmall = (TextView) findViewById(R.id.microTimeTextSmall);
        microTimeTextBig = (TextView) findViewById(R.id.microTimeTextBig);
        sedanTimeTextSmall = (TextView) findViewById(R.id.sedanTimeTextSmall);
        sedanTimeTextBig = (TextView) findViewById(R.id.sedanTimeTextBig);
        suvTimeTextSmall = (TextView) findViewById(R.id.suvTimeTextSmall);
        suvTimeTextBig = (TextView) findViewById(R.id.suvTimeTextBig);

        imgNext = (ImageView) findViewById(R.id.imgNext);
        getFareDetailsBtn = (Button) findViewById(R.id.getFareDetailsBtn);
        requestPickupBtn = (Button) findViewById(R.id.requestPickupBtn);
        noofSeatsSpinner = (Spinner) findViewById(R.id.noofSeatsSpinner);


        //Hide the fareDetails Button till it loads all the valid data
        //getFareDetailsBtn.setVisibility(View.INVISIBLE);

        LinearLayout reqBooking = (LinearLayout) findViewById(R.id.reqBooking);
        requestPickupBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                isScheduledTrip = false;

                selectedCarType = Integer.parseInt(getFareDetailsBtn.getTag().toString().trim());
                SingleInstantParameters loginCredentials = new SingleInstantParameters();
                Log.e("selectedCarType", "" + selectedCarType);
                loginCredentials.cabTypeId = selectedCarType;
                Log.e("customerId", "" + Utils.verifyLogInUserMobileInstantResponse.getUserId());
                loginCredentials.customerId = Utils.verifyLogInUserMobileInstantResponse.getUserId();
                Log.e("from", "" + Utils.startingPlaceAddress);
                loginCredentials.from = Utils.startingPlaceAddress;
                Log.e("to", "" + Utils.endingPlaceAddress);
                loginCredentials.to = Utils.endingPlaceAddress;
                Log.e("suggestedPrice", "" + basePrice.getTag().toString().trim());
                loginCredentials.suggestedPrice = basePrice.getTag().toString().trim();
                Log.e("offeredPrice", "" + requestPickupBtn.getTag().toString().trim());
                loginCredentials.offeredPrice = requestPickupBtn.getTag().toString().trim();
                Log.e("noOfPassengers", "" + noofSeatsSpinner.getSelectedItem().toString().trim());
                loginCredentials.noOfPassengers = Integer.parseInt(noofSeatsSpinner.getSelectedItem().toString().trim());

                String km = Utils.parsedDistance.split(" ")[0].trim();
                String kmtomile = "" + (Double.parseDouble(km.replaceAll(",", "")) * 0.6214);
                GeoLocationRequest bookingObjects = new GeoLocationRequest();
                Log.e("fromLatitude", "" + Utils.startingLatLan.latitude);
                bookingObjects.fromLatitude = "" + new DecimalFormat("##.######").format(Utils.startingLatLan.latitude);
                Log.e("fromLongitude", "" + Utils.startingLatLan.longitude);
                bookingObjects.fromLongitude = "" + new DecimalFormat("##.######").format(Utils.startingLatLan.longitude);
                Log.e("toLatitude", "" + Utils.endingLatLan.latitude);
                bookingObjects.toLatitude = "" + new DecimalFormat("##.######").format(Utils.endingLatLan.latitude);
                Log.e("toLongitude", "" + Utils.endingLatLan.longitude);
                bookingObjects.toLongitude = "" + new DecimalFormat("##.######").format(Utils.endingLatLan.longitude);
                Log.e("kmtomile", "" + kmtomile);
                bookingObjects.distanceInMiles = "" + kmtomile;

                loginCredentials.geoLocationRequest = bookingObjects;

                Gson gson = new Gson();
                String json = gson.toJson(loginCredentials);
                Log.e("json", "" + json);
                callRequestBooking(loginCredentials);
            }
        });


        //Scheduled Trip


        requestPickupLaterBtn = (Button) findViewById(R.id.requestPickupLaterBtn);
        //noofSeatsSpinner = (Spinner) findViewById(R.id.noofSeatsSpinner);

        //LinearLayout reqBooking = (LinearLayout) findViewById(R.id.reqBooking);
        requestPickupLaterBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //Call the Date Picker

                schedulePickupTime.show();

//                //Show the date and time pick up window with one hour ahead of the current time.
//                selectedCarType = Integer.parseInt(getFareDetailsBtn.getTag().toString().trim());
//                SingleInstantParameters loginCredentials = new SingleInstantParameters();
//                Log.e("selectedCarType", "" + selectedCarType);
//                loginCredentials.cabTypeId = selectedCarType;
//                Log.e("customerId", "" + Utils.verifyLogInUserMobileInstantResponse.getUserId());
//                loginCredentials.customerId = Utils.verifyLogInUserMobileInstantResponse.getUserId();
//                Log.e("from", "" + Utils.startingPlaceAddress);
//                loginCredentials.from = Utils.startingPlaceAddress;
//                Log.e("to", "" + Utils.endingPlaceAddress);
//                loginCredentials.to = Utils.endingPlaceAddress;
//                Log.e("suggestedPrice", "" + basePrice.getTag().toString().trim());
//                loginCredentials.suggestedPrice = basePrice.getTag().toString().trim();
//                Log.e("offeredPrice", "" + requestPickupBtn.getTag().toString().trim());
//                loginCredentials.offeredPrice = requestPickupBtn.getTag().toString().trim();
//                Log.e("noOfPassengers", "" + noofSeatsSpinner.getSelectedItem().toString().trim());
//                loginCredentials.noOfPassengers = Integer.parseInt(noofSeatsSpinner.getSelectedItem().toString().trim());
//
//                //Get the date and time.
//                loginCredentials.bookingDateTime = "08-24-2017 21:30:00";
//
//                String km = Utils.parsedDistance.split(" ")[0].trim();
//                String kmtomile = "" + (Double.parseDouble(km.replaceAll(",", "")) * 0.6214);
//                GeoLocationRequest bookingObjects = new GeoLocationRequest();
//                Log.e("fromLatitude", "" + Utils.startingLatLan.latitude);
//                bookingObjects.fromLatitude = "" + new DecimalFormat("##.######").format(Utils.startingLatLan.latitude);
//                Log.e("fromLongitude", "" + Utils.startingLatLan.longitude);
//                bookingObjects.fromLongitude = "" + new DecimalFormat("##.######").format(Utils.startingLatLan.longitude);
//                Log.e("toLatitude", "" + Utils.endingLatLan.latitude);
//                bookingObjects.toLatitude = "" + new DecimalFormat("##.######").format(Utils.endingLatLan.latitude);
//                Log.e("toLongitude", "" + Utils.endingLatLan.longitude);
//                bookingObjects.toLongitude = "" + new DecimalFormat("##.######").format(Utils.endingLatLan.longitude);
//                Log.e("kmtomile", "" + kmtomile);
//                bookingObjects.distanceInMiles = "" + kmtomile;
//
//                loginCredentials.geoLocationRequest = bookingObjects;
//
//                Gson gson = new Gson();
//                String json = gson.toJson(loginCredentials);
//                Log.e("json", "" + json);
//                callRequestBooking(loginCredentials);
            }
        });

        microLaySmall.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String timeText = microTimeTextSmall.getText().toString().trim();
                if (!timeText.equals("Not Available")) {
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
                    textSeatCapacity.setText("");
                }
            }
        });

        suvLaySmall.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String timeText = suvTimeTextSmall.getText().toString().trim();
                if (!timeText.equals("Not Available")) {
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
                    textSeatCapacity.setText("");
                }
            }
        });

        sedanLaySmall.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String timeText = sedanTimeTextSmall.getText().toString().trim();
                if (!timeText.equals("Not Available")) {
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
                    textSeatCapacity.setText("");
                }
            }
        });

        getFareDetailsBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectedCarType = Integer.parseInt(getFareDetailsBtn.getTag().toString().trim());
                SingleInstantParameters singleInstantParameters = new SingleInstantParameters();
                singleInstantParameters.cabTypeId = selectedCarType;
                String km = Utils.parsedDistance.split(" ")[0].trim();
                String kmtomile = "" + (Double.parseDouble(km.replaceAll(",", "")) * 0.6214);
                Log.e("kmtomile", "" + kmtomile);
                Log.e("selectedCarType", "" + selectedCarType);
                int count = Integer.parseInt(noofSeatsSpinner.getSelectedItem().toString().trim());
                Log.e("count", "" + count);
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
                requestPickupBtn.setTag(textAmount1.getTag().toString().trim());
                priceUpdateText.setText(textAmount1.getText().toString().trim());
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
                requestPickupBtn.setTag(textAmount2.getTag().toString().trim());
                priceUpdateText.setText(textAmount2.getText().toString().trim());
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
                requestPickupBtn.setTag(textAmount3.getTag().toString().trim());
                priceUpdateText.setText(textAmount3.getText().toString().trim());
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
                requestPickupBtn.setTag(textAmount4.getTag().toString().trim());
                priceUpdateText.setText(textAmount4.getText().toString().trim());
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

        //Create the datepikcer to select the date from current date

        Calendar newCalendar  = Calendar.getInstance();
        //TimeZone zone = .getTimeZone();
        schedulePickupTime = new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener() {
            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                Calendar newDate = Calendar.getInstance();
                newDate.set(year, monthOfYear, dayOfMonth);
                // activitydate.setText(dateFormatter.format(newDate.getTime()));
                userSelDate = newDate.getTime();

                //Call the Timer Picker Dialog

                showTimePicker();
            }


        }, newCalendar.get(Calendar.YEAR), newCalendar.get(Calendar.MONTH), newCalendar.get(Calendar.DAY_OF_MONTH));

        //Set the minimum date as today
        schedulePickupTime.getDatePicker().setMinDate(newCalendar.getTimeInMillis());
    }

    Handler handler = new Handler();
    Runnable finalizer;

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (handler != null && finalizer != null) {
            handler.removeCallbacks(finalizer);
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
        Log.e("Threadmessage ", "" + messageReceivedEvent.message);
        Log.e("Thread title", "" + messageReceivedEvent.title);

        if (requestdialog.isShowing()) {
            requestdialog.dismiss();
        }
        Log.e("PUSH_NOTIFICATION", "PUSH_NOTIFICATION");
        if (handler != null && finalizer != null) {
            handler.removeCallbacks(finalizer);
        }

        if (!messageReceivedEvent.title.equals("BOOKING_CANCELLED")) {
            //snack.bar

            //Toast.makeText(this, messageReceivedEvent.message, Toast.LENGTH_SHORT).show();
            SingleInstantParameters loginCredentials = new SingleInstantParameters();
            loginCredentials.bookingId = "" + Utils.requestBookingResponse.getBookingId();
            getBookingByBookingId(loginCredentials, 1);
        }
    }

    Dialog requestdialog;


    public void showTimePicker(){

        Calendar mcurrentTime = Calendar.getInstance();
        int hour = mcurrentTime.get(Calendar.HOUR_OF_DAY) + 1;
        int minute = mcurrentTime.get(Calendar.MINUTE);
        TimePickerDialog mTimePicker;
        mTimePicker = new TimePickerDialog(this, new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker timePicker, int selectedHour, int selectedMinute) {
                // eReminderTime.setText( selectedHour + ":" + selectedMinute);

                //Form the DateTime with UTC TimeZone and call the Booking Serivce.

                Calendar pickupDT = Calendar.getInstance();
                //pickupDT.setTimeZone(TimeZone.getTimeZone("UTC"));
                pickupDT.setTime(userSelDate);

                pickupDT.set(Calendar.HOUR_OF_DAY, selectedHour);
                pickupDT.set(Calendar.MINUTE, selectedMinute);
                pickupDT.set(Calendar.SECOND, 0);

                SimpleDateFormat sdf = new SimpleDateFormat("MM-dd-yyyy HH:mm:ss");
                sdf.setTimeZone(TimeZone.getTimeZone("UTC"));
                String  selTime = sdf.format(pickupDT.getTime());
                submitScheduleZipBook(selTime);
            }
        }, hour, minute, true);//Yes 24 hour time
        mTimePicker.setTitle("Select Time");
       // mTimePicker.getTime
        mTimePicker.show();
    }

    public void submitScheduleZipBook(String schedulePickupTime){

        isScheduledTrip = true;
        //Show the date and time pick up window with one hour ahead of the current time.
        selectedCarType = Integer.parseInt(getFareDetailsBtn.getTag().toString().trim());
        SingleInstantParameters loginCredentials = new SingleInstantParameters();
        Log.e("selectedCarType", "" + selectedCarType);
        loginCredentials.cabTypeId = selectedCarType;
        Log.e("customerId", "" + Utils.verifyLogInUserMobileInstantResponse.getUserId());
        loginCredentials.customerId = Utils.verifyLogInUserMobileInstantResponse.getUserId();
        Log.e("from", "" + Utils.startingPlaceAddress);
        loginCredentials.from = Utils.startingPlaceAddress;
        Log.e("to", "" + Utils.endingPlaceAddress);
        loginCredentials.to = Utils.endingPlaceAddress;
        Log.e("suggestedPrice", "" + basePrice.getTag().toString().trim());
        loginCredentials.suggestedPrice = basePrice.getTag().toString().trim();
        Log.e("offeredPrice", "" + requestPickupBtn.getTag().toString().trim());
        loginCredentials.offeredPrice = requestPickupBtn.getTag().toString().trim();
        Log.e("noOfPassengers", "" + noofSeatsSpinner.getSelectedItem().toString().trim());
        loginCredentials.noOfPassengers = Integer.parseInt(noofSeatsSpinner.getSelectedItem().toString().trim());

        //Get the date and time.
        loginCredentials.bookingDateTime = schedulePickupTime;//"08-24-2017 21:30:00";

        String km = Utils.parsedDistance.split(" ")[0].trim();
        String kmtomile = "" + (Double.parseDouble(km.replaceAll(",", "")) * 0.6214);
        GeoLocationRequest bookingObjects = new GeoLocationRequest();
        Log.e("fromLatitude", "" + Utils.startingLatLan.latitude);
        bookingObjects.fromLatitude = "" + new DecimalFormat("##.######").format(Utils.startingLatLan.latitude);
        Log.e("fromLongitude", "" + Utils.startingLatLan.longitude);
        bookingObjects.fromLongitude = "" + new DecimalFormat("##.######").format(Utils.startingLatLan.longitude);
        Log.e("toLatitude", "" + Utils.endingLatLan.latitude);
        bookingObjects.toLatitude = "" + new DecimalFormat("##.######").format(Utils.endingLatLan.latitude);
        Log.e("toLongitude", "" + Utils.endingLatLan.longitude);
        bookingObjects.toLongitude = "" + new DecimalFormat("##.######").format(Utils.endingLatLan.longitude);
        Log.e("kmtomile", "" + kmtomile);
        bookingObjects.distanceInMiles = "" + kmtomile;

        loginCredentials.geoLocationRequest = bookingObjects;

        Gson gson = new Gson();
        String json = gson.toJson(loginCredentials);
        Log.e("json", "" + json);

        //Toast.makeText(this,json.toString(),Toast.LENGTH_LONG).show();
        callRequestBooking(loginCredentials);

    }


    public void callRequestBooking(SingleInstantParameters loginCredentials) {
        if (Utils.connectivity(DirectionConfirmationActivity.this)) {
            requestdialog = new Dialog(DirectionConfirmationActivity.this, android.R.style.Theme_Dialog);
            requestdialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            requestdialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            requestdialog.setContentView(R.layout.requetsloadingimage_layout);
            requestdialog.setCanceledOnTouchOutside(false);
            requestdialog.setCancelable(false);
            requestdialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
            requestdialog.getWindow().setLayout(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
            requestdialog.show();

            Call<SingleInstantResponse> call = apiService.requestBooking(Utils.verifyLogInUserMobileInstantResponse.getAccessToken(),loginCredentials);
            call.enqueue(new Callback<SingleInstantResponse>() {
                @Override
                public void onResponse(Call<SingleInstantResponse> call, Response<SingleInstantResponse> response) {
                    int statusCode = response.code();
                    Log.e("statusCode", "" + statusCode);
                    Log.e("response.body", "" + response.body());
                    Log.e("response.errorBody", "" + response.errorBody());
                    Log.e("response.isSuccessful", "" + response.isSuccessful());
                    if (response.isSuccessful()) {
                        Utils.requestBookingResponse = response.body();
                        Log.e("bookingId", "" + Utils.requestBookingResponse.getBookingId());
                        Log.e("bookingStatus", "" + Utils.requestBookingResponse.getBookingStatusCode());
                        if (Utils.requestBookingResponse.getBookingStatusCode().equals("SCHEDULED") || Utils.requestBookingResponse.getBookingStatusCode().equals("ACCEPTED")) {
                            requestdialog.dismiss();
                            showInfoDlg("Booking Successful", "Your Booking request has been submitted successfully. Please wait till driver accepts...", "Done", "successBooking");
                        } else {
                            finalizer = new Runnable() {
                                public void run() {
                                    requestdialog.dismiss();
                                    SingleInstantParameters loginCredentials = new SingleInstantParameters();
                                    loginCredentials.bookingId = "" + Utils.requestBookingResponse.getBookingId();
                                    getBookingByBookingId(loginCredentials, 1);
                                }
                            };
                            handler.postDelayed(finalizer, 60000);
                        }
                    } else {
                        requestdialog.dismiss();
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
                            Toast.makeText(DirectionConfirmationActivity.this, getString(R.string.errmsg_network_noconnection), Toast.LENGTH_LONG).show();
                        }
                    }
                }

                @Override
                public void onFailure(Call<SingleInstantResponse> call, Throwable t) {
                    // Log error here since request failed
                    Log.e("onFailure", t.toString());
                    requestdialog.dismiss();
                    showInfoDlg(getString(R.string.error), "" + getString(R.string.errmsg_sessionexpired), getString(R.string.btn_ok), "logout");
                    //Toast.makeText(DirectionConfirmationActivity.this, getString(R.string.errmsg_network_noconnection), Toast.LENGTH_LONG).show();
                }
            });
        } else {
            Toast.makeText(this,getString(R.string.errmsg_network_noconnection), Toast.LENGTH_LONG).show();
        }
    }

    public void getBookingByBookingId(SingleInstantParameters loginCredentials, final int count) {
        if (Utils.connectivity(DirectionConfirmationActivity.this)) {
            final Dialog dialog = new Dialog(DirectionConfirmationActivity.this, android.R.style.Theme_Dialog);
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            dialog.setContentView(R.layout.requetsloadingimage_layout);
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
                        Log.e("bookingId", "" + Utils.requestBookingResponse.getBookingId());
                        Log.e("bookingStatus", "" + Utils.requestBookingResponse.getBookingStatusCode());
                        if (Utils.requestBookingResponse.getBookingStatusCode().equals("SCHEDULED") ) {

                            showInfoDlg(getString(R.string.bookingsuccess), getString(R.string.usermsg_driverscheduleaccepted), getString(R.string.btn_done), "successBooking");
                        } else if(Utils.requestBookingResponse.getBookingStatusCode().equals("ACCEPTED")){

                            showInfoDlg(getString(R.string.bookingsuccess), getString(R.string.usermsg_driveraccepted), getString(R.string.btn_done), "successBooking");

                        }  else{
//                            if(count != 3){
//                                new Handler().postDelayed(new Runnable() {
//                                    @Override
//                                    public void run() {
//                                        dialog.dismiss();
//                                        SingleInstantParameters loginCredentials = new SingleInstantParameters();
//                                        loginCredentials.bookingId = ""+ Utils.requestBookingResponse.getBookingId();
//                                        getBookingByBookingId(loginCredentials, 3);
//                                    }
//                                }, 10000);
//                            }else{
                            String cabType = Utils.requestBookingResponse.getCabType();
                            showInfoDlg("Booking Cancelled", "No " + cabType + " available / Driver Not accepted the request.\n Hint: Try with higher fare", "Done", "requestCancelled");
//                            }
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
                            showInfoDlg("Error..", getString(R.string.errmsg_network_noconnection), "OK", "server");
                        }
                    }
                }

                @Override
                public void onFailure(Call<SingleInstantResponse> call, Throwable t) {
                    // Log error here since request failed
                    Log.e("onFailure", t.toString());
                    dialog.dismiss();
                    showInfoDlg(getString(R.string.error), "" + getString(R.string.errmsg_sessionexpired), getString(R.string.btn_ok), "logout");
                   // Toast.makeText(DirectionConfirmationActivity.this, getString(R.string.errmsg_network_noconnection), Toast.LENGTH_LONG).show();
                }
            });
        } else {
            Toast.makeText(this, getString(R.string.errmsg_network_noconnection), Toast.LENGTH_LONG).show();
        }
    }

    private void updateBookingStatus(SingleInstantParameters loginCredentials) {
        if (Utils.connectivity(DirectionConfirmationActivity.this)) {
            final Dialog dialog = new Dialog(DirectionConfirmationActivity.this, android.R.style.Theme_Dialog);
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
                        Utils.updateBookingStatusInstantResponse = response.body();
                        Log.e("BookingStatus", "" + Utils.updateBookingStatusInstantResponse.getBookingStatus());
                        Intent ide = new Intent(DirectionConfirmationActivity.this, NavigationMenuActivity.class);
                        ide.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(ide);
                        finish();
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
                            Toast.makeText(DirectionConfirmationActivity.this, getString(R.string.errmsg_network_noconnection), Toast.LENGTH_LONG).show();
                        }
                    }
                }

                @Override
                public void onFailure(Call<SingleInstantResponse> call, Throwable t) {
                    // Log error here since request failed
                    Log.e("onFailure", t.toString());
                    dialog.dismiss();
                    showInfoDlg(getString(R.string.error), "" + getString(R.string.errmsg_sessionexpired), getString(R.string.btn_ok), "logout");
                   // Toast.makeText(DirectionConfirmationActivity.this, getString(R.string.errmsg_network_noconnection), Toast.LENGTH_LONG).show();
                }
            });
        } else {
            Toast.makeText(this, getString(R.string.errmsg_network_noconnection), Toast.LENGTH_LONG).show();
        }
    }

    private void getAllCabTypes() {
        if (Utils.connectivity(DirectionConfirmationActivity.this)) {
            final Dialog dialog = new Dialog(DirectionConfirmationActivity.this, android.R.style.Theme_Dialog);
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            dialog.setContentView(R.layout.loadingimage_layout);
            dialog.setCanceledOnTouchOutside(false);
            dialog.setCancelable(false);
            dialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
            dialog.getWindow().setLayout(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
            dialog.show();

            Call<LinkedList<ListOfCarTypes>> call = apiService.getAllCabTypes(Utils.verifyLogInUserMobileInstantResponse.getAccessToken());
            call.enqueue(new Callback<LinkedList<ListOfCarTypes>>() {
                @Override
                public void onResponse(Call<LinkedList<ListOfCarTypes>> call, Response<LinkedList<ListOfCarTypes>> response) {
                    int statusCode = response.code();
                    Log.e("statusCode", "" + statusCode);
                    Log.e("response.body", "" + response.body());
                    Log.e("response.errorBody", "" + response.errorBody());
                    Log.e("response.isSuccessful", "" + response.isSuccessful());
                    dialog.dismiss();
                    if (response.isSuccessful()) {
                        Utils.getAllCabTypesInstantResponse = response.body();
                        Log.e("Size", "" + Utils.getAllCabTypesInstantResponse.size());
                        selectedCarType = 2;
                        selectedCarModel = "Mini";
                        for (int i = 0; i < Utils.getAllCabTypesInstantResponse.size(); i++) {
                            Log.e("CARTYPE", "" + Utils.getAllCabTypesInstantResponse.get(i).getType());
                            if (i == 0) {
                                microTextSmall.setText(Utils.getAllCabTypesInstantResponse.get(i).getType());
                                //getFareDetailsBtn.setTag(Utils.getAllCabTypesInstantResponse.get(i).getCabTypeId());
                                microTextBig.setText(Utils.getAllCabTypesInstantResponse.get(i).getType());
                                microTimeTextSmall.setTag(Utils.getAllCabTypesInstantResponse.get(i).getCabTypeId());
                                microTimeTextBig.setTag(Utils.getAllCabTypesInstantResponse.get(i).getCabTypeId());
                            } else if (i == 1) {
                                sedanTextSmall.setText(Utils.getAllCabTypesInstantResponse.get(i).getType());
                                sedanTextBig.setText(Utils.getAllCabTypesInstantResponse.get(i).getType());
                                sedanTimeTextSmall.setTag(Utils.getAllCabTypesInstantResponse.get(i).getCabTypeId());
                                sedanTimeTextBig.setTag(Utils.getAllCabTypesInstantResponse.get(i).getCabTypeId());
                            } else {
                                suvTextSmall.setText(Utils.getAllCabTypesInstantResponse.get(i).getType());
                                getFareDetailsBtn.setTag(Utils.getAllCabTypesInstantResponse.get(i).getCabTypeId());
                                suvTextBig.setText(Utils.getAllCabTypesInstantResponse.get(i).getType());
                                suvTimeTextSmall.setTag(Utils.getAllCabTypesInstantResponse.get(i).getCabTypeId());
                                suvTimeTextBig.setTag(Utils.getAllCabTypesInstantResponse.get(i).getCabTypeId());
                            }
                        }
                        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(DirectionConfirmationActivity.this,
                                R.array.suv_array, android.R.layout.simple_spinner_item);
                        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                        noofSeatsSpinner.setAdapter(adapter);
                        noofSeatsSpinner.setSelection(0, true);
                        getNearByActiveDrivers("" + Utils.startingLatLan.latitude, "" + Utils.startingLatLan.longitude);
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
                            Toast.makeText(DirectionConfirmationActivity.this, getString(R.string.errmsg_network_noconnection), Toast.LENGTH_LONG).show();
                        }
                    }
                }

                @Override
                public void onFailure(Call<LinkedList<ListOfCarTypes>> call, Throwable t) {
                    // Log error here since request failed
                    Log.e("onFailure", t.toString());
                    dialog.dismiss();
                    showInfoDlg(getString(R.string.error), "" + getString(R.string.errmsg_sessionexpired), getString(R.string.btn_ok), "logout");
                    //Toast.makeText(DirectionConfirmationActivity.this, getString(R.string.errmsg_network_noconnection), Toast.LENGTH_LONG).show();
                }
            });
        } else {
            Toast.makeText(this, getString(R.string.errmsg_network_noconnection), Toast.LENGTH_LONG).show();
        }
    }

    private void getFairEstimateDetails(SingleInstantParameters singleInstantParameters) {
        if (Utils.connectivity(DirectionConfirmationActivity.this)) {
            final Dialog dialog = new Dialog(DirectionConfirmationActivity.this, android.R.style.Theme_Dialog);
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            dialog.setContentView(R.layout.loadingimage_layout);
            dialog.setCanceledOnTouchOutside(false);
            dialog.setCancelable(false);
            dialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
            dialog.getWindow().setLayout(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
            dialog.show();

            Call<LinkedList<ListOfFairEstimate>> call = apiService.getAllNYOPByCabTypeAndDistance(Utils.verifyLogInUserMobileInstantResponse.getAccessToken(),singleInstantParameters);
            call.enqueue(new Callback<LinkedList<ListOfFairEstimate>>() {
                @Override
                public void onResponse(Call<LinkedList<ListOfFairEstimate>> call, Response<LinkedList<ListOfFairEstimate>> response) {
                    int statusCode = response.code();
                    Log.e("statusCode", "" + statusCode);
                    Log.e("response.body", "" + response.body());
                    Log.e("response.errorBody", "" + response.errorBody());
                    Log.e("response.isSuccessful", "" + response.isSuccessful());
                    dialog.dismiss();
                    if (response.isSuccessful()) {
                        Utils.getAllNYOPByCabTypeAndDistanceInstantResponse = response.body();
                        Log.e("size", "" + Utils.getAllNYOPByCabTypeAndDistanceInstantResponse.size());
                        if (Utils.getAllNYOPByCabTypeAndDistanceInstantResponse.size() > 0) {
                            String status = Utils.getAllNYOPByCabTypeAndDistanceInstantResponse.get(0).getStatus();
                            Log.e("status", "" + status);
                            String errorMessage = Utils.getAllNYOPByCabTypeAndDistanceInstantResponse.get(0).getErrorMessage();
                            Log.e("errorMessage", "" + errorMessage);
                            if (status.equalsIgnoreCase("true")) {
                                fairDetailsVisible = true;
                                vehicleTypeLay.setVisibility(View.GONE);
                                requestTypeLay.setVisibility(View.VISIBLE);
                                if (Utils.getAllNYOPByCabTypeAndDistanceInstantResponse.size() > 0) {
                                    Log.e("getPrice", "" + Utils.getAllNYOPByCabTypeAndDistanceInstantResponse.get(0).getPrice());
                                    double price = Double.parseDouble(Utils.getAllNYOPByCabTypeAndDistanceInstantResponse.get(0).getPrice());
                                    Log.e("price 11", "" + price);
                                    textAmount1.setText(getString(R.string.currencysymbol)+ new DecimalFormat("##.#").format(price));
                                    textAmount1.setTag("" + price);
                                    requestPickupBtn.setTag(textAmount1.getTag().toString().trim());
                                    basePrice.setText(getString(R.string.currencysymbol) + new DecimalFormat("##.#").format(price));
                                    basePrice.setTag("" + price);
                                    priceUpdateText.setText(getString(R.string.currencysymbol) + new DecimalFormat("##.#").format(price));
                                }

                                if (Utils.getAllNYOPByCabTypeAndDistanceInstantResponse.size() > 1) {
                                    Log.e("getPrice", "" + Utils.getAllNYOPByCabTypeAndDistanceInstantResponse.get(1).getPrice());
                                    double price = Double.parseDouble(Utils.getAllNYOPByCabTypeAndDistanceInstantResponse.get(1).getPrice());
                                    Log.e("price 22", "" + price);
                                    textAmount2.setText(getString(R.string.currencysymbol) + new DecimalFormat("##.#").format(price));
                                    textAmount2.setTag("" + price);
                                }

                                if (Utils.getAllNYOPByCabTypeAndDistanceInstantResponse.size() > 2) {
                                    Log.e("getPrice", "" + Utils.getAllNYOPByCabTypeAndDistanceInstantResponse.get(2).getPrice());
                                    double price = Double.parseDouble(Utils.getAllNYOPByCabTypeAndDistanceInstantResponse.get(2).getPrice());
                                    Log.e("price 33", "" + price);
                                    textAmount3.setText(getString(R.string.currencysymbol) + new DecimalFormat("##.#").format(price));
                                    textAmount3.setTag("" + price);
                                }

                                if (Utils.getAllNYOPByCabTypeAndDistanceInstantResponse.size() > 3) {
                                    Log.e("getPrice", "" + Utils.getAllNYOPByCabTypeAndDistanceInstantResponse.get(3).getPrice());
                                    double price = Double.parseDouble(Utils.getAllNYOPByCabTypeAndDistanceInstantResponse.get(3).getPrice());
                                    Log.e("price 44", "" + price);
                                    textAmount4.setText(getString(R.string.currencysymbol)+ new DecimalFormat("##.#").format(price));
                                    textAmount4.setTag("" + price);
                                }
                            } else {
                                showInfoDlg("Invalid drop!", "" + errorMessage, "OK", "invalid");
                            }
                        }

                        //showInfoDlg("Success..", "Successfully registered.", "OK", "success");
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
                            e.printStackTrace();
                            showInfoDlg(getString(R.string.error), "" + getString(R.string.errmsg_sessionexpired), getString(R.string.btn_ok), "logout");
                            Toast.makeText(DirectionConfirmationActivity.this, getString(R.string.errmsg_network_noconnection), Toast.LENGTH_LONG).show();
                        }
                    }
                }

                @Override
                public void onFailure(Call<LinkedList<ListOfFairEstimate>> call, Throwable t) {
                    // Log error here since request failed
                    Log.e("onFailure", t.toString());
                    dialog.dismiss();
                    showInfoDlg(getString(R.string.error), "" + getString(R.string.errmsg_sessionexpired), getString(R.string.btn_ok), "logout");
                    //Toast.makeText(DirectionConfirmationActivity.this, getString(R.string.errmsg_network_noconnection), Toast.LENGTH_LONG).show();
                }
            });
        } else {
            Toast.makeText(this, getString(R.string.errmsg_network_noconnection), Toast.LENGTH_LONG).show();
        }
    }

    private void showInfoDlg(String title, String content, String btnText, final String navType) {
        final Dialog dialog = new Dialog(DirectionConfirmationActivity.this, android.R.style.Theme_Dialog);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.infodialog_layout);
        dialog.setCanceledOnTouchOutside(false);
        dialog.setCancelable(false);

        Button positiveBtn = (Button) dialog.findViewById(R.id.positiveBtn);
        positiveBtn.setText("" + btnText);

        Button newnegativeBtn = (Button) dialog.findViewById(R.id.newnegativeBtn);
        if (navType.equalsIgnoreCase("gps") || navType.equalsIgnoreCase("server")|| navType.equalsIgnoreCase("logout")) {
            newnegativeBtn.setVisibility(View.GONE);
        } else {
            newnegativeBtn.setVisibility(View.VISIBLE);
        }

        ImageView headerIcon = (ImageView) dialog.findViewById(R.id.headerIcon);
        if (navType.equalsIgnoreCase("successBooking")) {
            headerIcon.setImageResource(R.drawable.successicon);
            newnegativeBtn.setVisibility(View.GONE);
        } else if (navType.equalsIgnoreCase("requestCancelled")) {
            headerIcon.setImageResource(R.drawable.erroricon);
            newnegativeBtn.setVisibility(View.GONE);
        } else if (navType.equalsIgnoreCase("invalid")) {
            headerIcon.setVisibility(View.GONE);
            newnegativeBtn.setVisibility(View.GONE);
        }

        TextView dialogtitleText = (TextView) dialog.findViewById(R.id.dialogtitleText);
        dialogtitleText.setText("" + title);
        TextView dialogcontentText = (TextView) dialog.findViewById(R.id.dialogcontentText);
        dialogcontentText.setText("" + content);

        positiveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                if (navType.equalsIgnoreCase("gps")) {
                    startActivityForResult(new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS), REQUEST_CHECK_SETTINGS);
                } else if (navType.equalsIgnoreCase("successBooking")) {

                    Intent ide;
                    if(isScheduledTrip){
                        ide = new Intent(DirectionConfirmationActivity.this, NavigationMenuActivity.class);
                    }else {
                        ide = new Intent(DirectionConfirmationActivity.this, DriverInfoBookingActivity.class);
                    }
                    ide.putExtra("from", "Acceptance");
                    ide.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(ide);
                    finish();
                } else if (navType.equalsIgnoreCase("requestCancelled")) {
                    SingleInstantParameters loginCredentials = new SingleInstantParameters();
                    loginCredentials.bookingId = Utils.requestBookingResponse.getBookingId();
                    loginCredentials.bookingStatus = "CANCELLED";
                    updateBookingStatus(loginCredentials);
                } else if (navType.equalsIgnoreCase("invalid")) {
                    onBackPressed();
                }else if(navType.equalsIgnoreCase("logout")){
                    SharedPreferences.Editor editor = getSharedPreferences("LoginCredentials", MODE_PRIVATE).edit();
                    editor.remove("phoneNumber");
                    editor.remove("password");
                    editor.commit();
                    SharedPreferences.Editor deditor = getSharedPreferences("DisclaimerCredentials", MODE_PRIVATE).edit();
                    deditor.putString("disclaimer", "");
                    deditor.commit();
                    Intent ide = new Intent(DirectionConfirmationActivity.this, LoginActivity.class);
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
        if (fairDetailsVisible) {
            fairDetailsVisible = false;
            vehicleTypeLay.setVisibility(View.VISIBLE);
            requestTypeLay.setVisibility(View.GONE);
        } else {
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

    List<Marker> markers = new LinkedList<Marker>();

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        LatLng origin = Utils.startingLatLan;
        LatLng dest = Utils.endingLatLan;
        startingMarker = mMap.addMarker(new MarkerOptions().anchor(0.5f, 0.5f).position(origin).icon(BitmapDescriptorFactory.fromResource(R.drawable.startpoint)));
        startingMarker.setTag("starting");
        startingMarker.showInfoWindow();
        endingMarker = mMap.addMarker(new MarkerOptions().anchor(0.5f, 0.5f).position(dest).icon(BitmapDescriptorFactory.fromResource(R.drawable.endpoint)));
        endingMarker.setTag("ending");
        markers.add(startingMarker);
        markers.add(endingMarker);



        mMap.setOnMapLoadedCallback(new GoogleMap.OnMapLoadedCallback() {
            @Override
            public void onMapLoaded() {
                LatLngBounds.Builder builder = new LatLngBounds.Builder();
                for (Marker marker : markers) {
                    builder.include(marker.getPosition());
                }
                LatLngBounds bounds = builder.build();
                int padding = 50; // offset from edges of the map in pixels
                CameraUpdate cu = CameraUpdateFactory.newLatLngBounds(bounds, padding);
                mMap.moveCamera(cu);
                mMap.animateCamera(cu);

                if (Utils.connectivity(DirectionConfirmationActivity.this)) {

                    LatLng origin = Utils.startingLatLan;
                    LatLng dest = Utils.endingLatLan;
                    // Getting URL to the Google Directions API
                    String url = getUrl(origin, dest);
                    Log.d("url", "" + url);
                    FetchUrl FetchUrl = new FetchUrl();
                    // Start downloading json data from Google Directions API
                    FetchUrl.execute(url);
                } else {
                    Toast.makeText(getApplicationContext(), getString(R.string.errmsg_network_noconnection), Toast.LENGTH_LONG).show();
                }
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

        String cabType = "";

        public FetchUrl(String cabType) {
            this.cabType = cabType;
        }

        public  FetchUrl() {
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

            if(cabType.equalsIgnoreCase("")) {
                ParserTask parserTask = new ParserTask();
                // Invokes the thread for parsing the JSON data
                parserTask.execute(result);
            }else{
                ParserTimeTask parserTask = new ParserTimeTask(cabType);
                // Invokes the thread for parsing the JSON data
                parserTask.execute(result);
            }

        }
    }

    /**
     * A class to parse the Google Places in JSON format
     */
    private class ParserTimeTask extends AsyncTask<String, Integer, List<List<HashMap<String, String>>>> {

        String cabType = "";

        public ParserTimeTask(String cabType) {
            this.cabType = cabType;
        }

        // Parsing the data in non-ui thread
        @Override
        protected List<List<HashMap<String, String>>> doInBackground(String... jsonData) {

            JSONObject jObject;
            List<List<HashMap<String, String>>> routes = null;

            try {
                jObject = new JSONObject(jsonData[0]);
                Log.d("ParserTask", jsonData[0].toString());
                TimeDataParser parser = new TimeDataParser();
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

            if(cabType.equalsIgnoreCase("micro")){
                microTimeTextSmall.setText(Utils.cabparsedDuration);
                microTimeTextBig.setText(Utils.cabparsedDuration);
            }else if(cabType.equalsIgnoreCase("sedan")){
                sedanTimeTextSmall.setText(Utils.cabparsedDuration);
                sedanTimeTextBig.setText(Utils.cabparsedDuration);
            }else{
                suvTimeTextSmall.setText(Utils.cabparsedDuration);
                suvTimeTextBig.setText(Utils.cabparsedDuration);
            }

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

            Log.e("result.size()", "" + result.size());

            if (result.size() > 0) {
                vehicleTypeLay.setVisibility(View.VISIBLE);
                for (int i = 0; i < result.size(); i++) {
                    points = new ArrayList<>();
                    lineOptions = new PolylineOptions();

                    // Fetching i-th route
                    List<HashMap<String, String>> path = result.get(i);

                    Log.e("path.size()", "" + path.size());
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
            } else {
                mMap.getUiSettings().setScrollGesturesEnabled(false);
                mMap.getUiSettings().setZoomGesturesEnabled(false);
                noCabsLay.setVisibility(View.VISIBLE);
                vehicleTypeLay.setVisibility(View.GONE);
                noCabsText.setText("The service is not available for the selected route");
            }

            // Drawing polyline in the Google Map for the i-th route
            if (lineOptions != null) {
                mMap.addPolyline(lineOptions);
                getAllCabTypes();
            } else {
                Log.d("onPostExecute", "without Polylines drawn");
            }
        }
    }

    boolean microCabTimeSearch = true;
    boolean sedanCabTimeSearch = true;
    boolean suvCabTimeSearch = true;

    public void getNearByActiveDrivers(String latitude, String longitude) {
        if (Utils.connectivity(DirectionConfirmationActivity.this)) {
            Log.e("fromLatitude", "" + latitude);
            Log.e("fromLongitude", "" + longitude);
            SingleInstantParameters loginCredentials = new SingleInstantParameters();
            loginCredentials.fromLatitude = latitude;
            loginCredentials.fromLongitude = longitude;

            final Dialog dialog = new Dialog(DirectionConfirmationActivity.this, android.R.style.Theme_Dialog);
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            dialog.setContentView(R.layout.loadingimage_layout);
            dialog.setCanceledOnTouchOutside(false);
            dialog.setCancelable(false);
            dialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
            dialog.getWindow().setLayout(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
            dialog.show();

            Call<LinkedList<ListOfCurrentCabs>> call = apiService.getNearByActiveDrivers(Utils.verifyLogInUserMobileInstantResponse.getAccessToken(),loginCredentials);
            call.enqueue(new Callback<LinkedList<ListOfCurrentCabs>>() {
                @Override
                public void onResponse(Call<LinkedList<ListOfCurrentCabs>> call, Response<LinkedList<ListOfCurrentCabs>> response) {
                    int statusCode = response.code();
                    Log.e("statusCode", "" + statusCode);
                    Log.e("response.body", "" + response.body());
                    Log.e("response.errorBody", "" + response.errorBody());
                    Log.e("response.isSuccessful", "" + response.isSuccessful());
                    dialog.dismiss();
                    if (response.isSuccessful()) {
                        Utils.getNearByActiveDriversInstantResponse = response.body();
                        Log.e("size", "" + Utils.getNearByActiveDriversInstantResponse.size());

                        if (Utils.getNearByActiveDriversInstantResponse.size() <= 0) {
                            mMap.getUiSettings().setScrollGesturesEnabled(false);
                            mMap.getUiSettings().setZoomGesturesEnabled(false);
                            noCabsLay.setVisibility(View.VISIBLE);
                            vehicleTypeLay.setVisibility(View.GONE);
                        } else {
                            vehicleTypeLay.setVisibility(View.VISIBLE);

                            for (int i = 0; i < Utils.getNearByActiveDriversInstantResponse.size(); i++) {
                                String cabtypeId = Utils.getNearByActiveDriversInstantResponse.get(i).getCabTypeId();
                                Log.e("cabtypeId", "" + cabtypeId);
                                if (cabtypeId != null) {
                                    if (cabtypeId.equalsIgnoreCase(microTimeTextSmall.getTag().toString().trim())) {
                                        if(microCabTimeSearch){
                                            microCabTimeSearch = false;
                                            if (Utils.connectivity(DirectionConfirmationActivity.this)) {
                                                LatLng cabLatLng = new LatLng(Double.parseDouble(Utils.getNearByActiveDriversInstantResponse.get(i).getLatitude()), Double.parseDouble(Utils.getNearByActiveDriversInstantResponse.get(i).getLongitude()));
                                                String url = getUrl(cabLatLng, Utils.startingLatLan);
                                                Log.d("url", "" + url);
                                                FetchUrl FetchUrl = new FetchUrl("micro");
                                                FetchUrl.execute(url);
                                            } else {
                                                Toast.makeText(DirectionConfirmationActivity.this, getString(R.string.errmsg_network_noconnection), Toast.LENGTH_LONG).show();
                                            }
                                        }
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
                                    } else if (cabtypeId.equalsIgnoreCase(sedanTimeTextSmall.getTag().toString().trim())) {
                                        if(sedanCabTimeSearch){
                                            sedanCabTimeSearch = false;
                                            if (Utils.connectivity(DirectionConfirmationActivity.this)) {
                                                LatLng cabLatLng = new LatLng(Double.parseDouble(Utils.getNearByActiveDriversInstantResponse.get(i).getLatitude()), Double.parseDouble(Utils.getNearByActiveDriversInstantResponse.get(i).getLongitude()));
                                                String url = getUrl(cabLatLng, Utils.startingLatLan);
                                                Log.d("url", "" + url);
                                                FetchUrl FetchUrl = new FetchUrl("sedan");
                                                FetchUrl.execute(url);
                                            } else {
                                                Toast.makeText(DirectionConfirmationActivity.this, getString(R.string.errmsg_network_noconnection), Toast.LENGTH_LONG).show();
                                            }
                                        }
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
                                    } else if (cabtypeId.equalsIgnoreCase(suvTimeTextSmall.getTag().toString().trim())) {
                                        if(suvCabTimeSearch){
                                            suvCabTimeSearch = false;
                                            if (Utils.connectivity(DirectionConfirmationActivity.this)) {
                                                LatLng cabLatLng = new LatLng(Double.parseDouble(Utils.getNearByActiveDriversInstantResponse.get(i).getLatitude()), Double.parseDouble(Utils.getNearByActiveDriversInstantResponse.get(i).getLongitude()));
                                                String url = getUrl(cabLatLng, Utils.startingLatLan);
                                                Log.d("url", "" + url);
                                                FetchUrl FetchUrl = new FetchUrl("suv");
                                                FetchUrl.execute(url);
                                            } else {
                                                Toast.makeText(DirectionConfirmationActivity.this, getString(R.string.errmsg_network_noconnection), Toast.LENGTH_LONG).show();
                                            }
                                        }
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
                                }
                                String latitude = Utils.getNearByActiveDriversInstantResponse.get(i).getLatitude();
                                String longitude = Utils.getNearByActiveDriversInstantResponse.get(i).getLongitude();
                                Log.e("latitude longitude", "latitude : " + latitude + " longitude : " + longitude);
                                LatLng tempLatLong = new LatLng(Double.parseDouble(latitude), Double.parseDouble(longitude));
                                Marker marker = mMap.addMarker(new MarkerOptions().position(tempLatLong).icon(BitmapDescriptorFactory.fromResource(R.drawable.movingcar_48)));
                                markers.add(marker);
                            }
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
                            Toast.makeText(DirectionConfirmationActivity.this, getString(R.string.errmsg_network_noconnection), Toast.LENGTH_LONG).show();
                        }
                    }
                }

                @Override
                public void onFailure(Call<LinkedList<ListOfCurrentCabs>> call, Throwable t) {
                    // Log error here since request failed
                    Log.e("onFailure", t.toString());
                    dialog.dismiss();
                    showInfoDlg(getString(R.string.error), "" + getString(R.string.errmsg_sessionexpired), getString(R.string.btn_ok), "logout");
                    //Toast.makeText(DirectionConfirmationActivity.this, getString(R.string.errmsg_network_noconnection), Toast.LENGTH_LONG).show();
                }
            });
        } else {
            Toast.makeText(this, getString(R.string.errmsg_network_noconnection), Toast.LENGTH_LONG).show();
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

    @Override
    protected void onPause() {
        super.onPause();
    }
}
