package com.altrockstech.ziprydeuserapp;

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
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.altrockstech.ziprydeuserapp.apis.ZiprydeApiClient;
import com.altrockstech.ziprydeuserapp.apis.ZiprydeApiInterface;
import com.altrockstech.ziprydeuserapp.assist.DataParser;
import com.altrockstech.ziprydeuserapp.assist.Utils;
import com.altrockstech.ziprydeuserapp.modelget.ListOfCarTypes;
import com.altrockstech.ziprydeuserapp.modelget.ListOfFairEstimate;
import com.altrockstech.ziprydeuserapp.modelget.SingleInstantResponse;
import com.altrockstech.ziprydeuserapp.modelpost.SingleInstantParameters;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
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
    ImageView micro_circle, micro_car, mini_circle, mini_car, suv_circle, suv_car, centerMarker;

    LinearLayout fairBooking, carTypeLay, fairDetailsLay;

    boolean fairDetailsVisible = false;

    ZiprydeApiInterface apiService;

    int selectedCarType = 2;
    String selectedCarModel = "Mini";

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
        searchPlace.setText(""+Utils.startingPlaceAddress);
        searchPlaceDestination = (AppCompatTextView) findViewById(R.id.searchPlaceDestination);
        searchPlaceDestination.setText(""+Utils.endingPlaceAddress);

        microLay = (RelativeLayout) findViewById(R.id.microLay);
        miniLay = (RelativeLayout) findViewById(R.id.miniLay);
        sedanLay = (RelativeLayout) findViewById(R.id.sedanLay);

        fairBooking = (LinearLayout) findViewById(R.id.fairBooking);
        carTypeLay = (LinearLayout) findViewById(R.id.carTypeLay);
        fairDetailsLay = (LinearLayout) findViewById(R.id.fairDetailsLay);

        micro_circle = (ImageView) findViewById(R.id.micro_circle);
        micro_car = (ImageView) findViewById(R.id.micro_car);
        mini_circle = (ImageView) findViewById(R.id.mini_circle);
        mini_car = (ImageView) findViewById(R.id.mini_car);
        suv_circle = (ImageView) findViewById(R.id.suv_circle);
        suv_car = (ImageView) findViewById(R.id.suv_car);

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
                changeCircleBigtoSmall(mini_circle, suv_circle, mini_car, suv_car);
                changeCircleSmalltoBig(micro_circle, micro_car);
                micro_car.setImageResource(R.drawable.ic_micro_white_car);
                mini_car.setImageResource(R.drawable.ic_mini_car);
                suv_car.setImageResource(R.drawable.ic_suv_car);
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
                changeCircleBigtoSmall(micro_circle, suv_circle, micro_car, suv_car);
                changeCircleSmalltoBig(mini_circle, mini_car);
                micro_car.setImageResource(R.drawable.ic_micro_car);
                mini_car.setImageResource(R.drawable.ic_mini_white_car);
                suv_car.setImageResource(R.drawable.ic_suv_car);
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
                changeCircleBigtoSmall(micro_circle, mini_circle, micro_car, mini_car);
                changeCircleSmalltoBig(suv_circle, suv_car);
                micro_car.setImageResource(R.drawable.ic_micro_car);
                mini_car.setImageResource(R.drawable.ic_mini_car);
                suv_car.setImageResource(R.drawable.ic_suv_white_car);
                selectedCarType = 3;
                selectedCarModel = "Sedan";
                carTypeText.setText(selectedCarModel);
            }
        });

        fairBooking.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SingleInstantParameters singleInstantParameters = new SingleInstantParameters();
                singleInstantParameters.cabTypeId = selectedCarType;
                String km = Utils.parsedDistance.split(" ")[0].trim();
                String kmtomile = ""+(Double.parseDouble(km) * 0.6214);
                Log.e("kmtomile",""+kmtomile);
                singleInstantParameters.distanceInMiles = kmtomile;
                getFairEstimateDetails(singleInstantParameters);
                fairDetailsVisible = true;
                carTypeLay.setVisibility(View.GONE);
                fairDetailsLay.setVisibility(View.VISIBLE);
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
        reqBooking.setOnClickListener(new View.OnClickListener() {
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

        getAllCabTypes();
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
                        long price = Math.round(Double.parseDouble(Utils.getAllNYOPByCabTypeAndDistanceInstantResponse.get(0).getPrice()));
                        amountText1.setText("$"+price);
                    }

                    if(Utils.getAllNYOPByCabTypeAndDistanceInstantResponse.size() > 1){
                        long price = Math.round(Double.parseDouble(Utils.getAllNYOPByCabTypeAndDistanceInstantResponse.get(1).getPrice()));
                        amountText2.setText("$"+price);
                    }

                    if(Utils.getAllNYOPByCabTypeAndDistanceInstantResponse.size() > 2){
                        long price = Math.round(Double.parseDouble(Utils.getAllNYOPByCabTypeAndDistanceInstantResponse.get(2).getPrice()));
                        amountText3.setText("$"+price);
                    }

                    if(Utils.getAllNYOPByCabTypeAndDistanceInstantResponse.size() > 3){
                        long price = Math.round(Double.parseDouble(Utils.getAllNYOPByCabTypeAndDistanceInstantResponse.get(3).getPrice()));
                        amountText4.setText("$"+price);
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
                    Intent ide = new Intent(DirectionConfirmationActivity.this, OnGoingBookingActivity.class);
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

    public void changeCircleSmalltoBig(ImageView imageView1, ImageView imageView2) {
        RelativeLayout.LayoutParams parms = new RelativeLayout.LayoutParams(170,170);
        parms.addRule(RelativeLayout.CENTER_IN_PARENT, RelativeLayout.TRUE);
        imageView1.setLayoutParams(parms);
        imageView1.setBackgroundResource(R.drawable.circular_orange_bg);
        parms = new RelativeLayout.LayoutParams(130,130);
        parms.addRule(RelativeLayout.CENTER_IN_PARENT, RelativeLayout.TRUE);
        imageView2.setLayoutParams(parms);
    }

    public void changeCircleBigtoSmall(ImageView imageView1, ImageView imageView2, ImageView imageView3, ImageView imageView4){
        RelativeLayout.LayoutParams parms = new RelativeLayout.LayoutParams(130,130);
        parms.addRule(RelativeLayout.CENTER_IN_PARENT, RelativeLayout.TRUE);
        imageView1.setLayoutParams(parms);
        imageView2.setLayoutParams(parms);
        imageView1.setBackgroundResource(R.drawable.circular_bg);
        imageView2.setBackgroundResource(R.drawable.circular_bg);
        parms = new RelativeLayout.LayoutParams(110,110);
        parms.addRule(RelativeLayout.CENTER_IN_PARENT, RelativeLayout.TRUE);
        imageView3.setLayoutParams(parms);
        imageView4.setLayoutParams(parms);
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
            carTypeLay.setVisibility(View.VISIBLE);
            fairDetailsLay.setVisibility(View.GONE);
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
        startingMarker = mMap.addMarker(new MarkerOptions().position(origin).title(""+Utils.startingPlaceAddress).icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_action_location_48)));
        startingMarker.setTag("starting");
        startingMarker.showInfoWindow();
        endingMarker = mMap.addMarker(new MarkerOptions().position(dest).title(""+Utils.endingPlaceAddress).icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_action_location_new_48)));
        endingMarker.setTag("ending");

        // Getting URL to the Google Directions API
        String url = getUrl(origin, dest);
        Log.d("url", ""+url);
        Log.d("onMapClick", url.toString());
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
                lineOptions.width(15);
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
