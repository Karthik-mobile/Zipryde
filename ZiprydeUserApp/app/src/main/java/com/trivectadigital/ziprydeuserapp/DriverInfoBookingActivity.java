package com.trivectadigital.ziprydeuserapp;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
import android.os.Handler;
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
import com.trivectadigital.ziprydeuserapp.assist.CircleImageView;
import com.trivectadigital.ziprydeuserapp.assist.DataParser;
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

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class DriverInfoBookingActivity extends AppCompatActivity implements OnMapReadyCallback  {

    private GoogleMap mMap;

    int position = -1;

    TextView driverNameText, cabTypeText, fromAddressText, toAddressText, bookingStatus;
    ListOfBooking listOfBooking;
    ZiprydeApiInterface apiService;

    CircleImageView user_view;

    LinearLayout cancelBookingLay;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_driver_info_booking);

        apiService = ZiprydeApiClient.getClient().create(ZiprydeApiInterface.class);

        Intent intent = getIntent();
        if(intent.hasExtra("position")){
            position = intent.getIntExtra("position", -1);
        }

        driverNameText = (TextView) findViewById(R.id.driverNameText);
        cabTypeText = (TextView) findViewById(R.id.cabTypeText);
        fromAddressText = (TextView) findViewById(R.id.fromAddressText);
        toAddressText = (TextView) findViewById(R.id.toAddressText);
        bookingStatus = (TextView) findViewById(R.id.bookingStatus);
        user_view = (CircleImageView) findViewById(R.id.user_view);

        cancelBookingLay = (LinearLayout) findViewById(R.id.cancelBookingLay);

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        if(position != -1){
            listOfBooking = Utils.getBookingByUserIdResponse.get(position);
            driverNameText.setText(listOfBooking.getDriverName());
            cabTypeText.setText(listOfBooking.getCabType());
            fromAddressText.setText(listOfBooking.getFrom());
            toAddressText.setText(listOfBooking.getTo());
            bookingStatus.setText(listOfBooking.getBookingStatus());
            Log.e("DriverId", listOfBooking.getDriverId());
            Log.e("driverImage", "driverImage : "+listOfBooking.getDriverImage());
            String driverImage = ""+listOfBooking.getDriverImage();
            if(driverImage != null){
                if(!driverImage.equalsIgnoreCase("null")){
                    byte[] decodedString = Base64.decode(driverImage, Base64.DEFAULT);
                    Bitmap decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
                    user_view.setImageBitmap(decodedByte);
                }
            }

            if(listOfBooking.getBookingStatus() != null) {
                if (listOfBooking.getBookingStatus().equals("SCHEDULED")) {
                    cancelBookingLay.setVisibility(View.VISIBLE);
                }else {
                    cancelBookingLay.setVisibility(View.GONE);
                }
            }else {
                cancelBookingLay.setVisibility(View.GONE);
            }
            SingleInstantParameters loginCredentials = new SingleInstantParameters();
            loginCredentials.userId = ""+ listOfBooking.getDriverId();
            getGeoLocationByDriverId(loginCredentials);
        }else {
            driverNameText.setText(Utils.requestBookingResponse.getDriverName());
            cabTypeText.setText(Utils.requestBookingResponse.getCabType());
            fromAddressText.setText(Utils.requestBookingResponse.getFrom());
            toAddressText.setText(Utils.requestBookingResponse.getTo());
            bookingStatus.setText(Utils.requestBookingResponse.getBookingStatus());
            Log.e("DriverId", Utils.requestBookingResponse.getDriverId());
            Log.e("driverImage", "driverImage : "+Utils.requestBookingResponse.getDriverImage());
            String driverImage = ""+Utils.requestBookingResponse.getDriverImage();
            if(driverImage != null){
                if(!driverImage.equalsIgnoreCase("null")){
                    byte[] decodedString = Base64.decode(driverImage, Base64.DEFAULT);
                    Bitmap decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
                    user_view.setImageBitmap(decodedByte);
                }
            }
            if(Utils.requestBookingResponse.getBookingStatus() != null) {
                if (Utils.requestBookingResponse.getBookingStatus().equals("SCHEDULED")) {
                    cancelBookingLay.setVisibility(View.VISIBLE);
                }else {
                    cancelBookingLay.setVisibility(View.GONE);
                }
            }else {
                cancelBookingLay.setVisibility(View.GONE);
            }
            SingleInstantParameters loginCredentials = new SingleInstantParameters();
            loginCredentials.userId = ""+ Utils.requestBookingResponse.getDriverId();
            getGeoLocationByDriverId(loginCredentials);
        }

        cancelBookingLay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SingleInstantParameters loginCredentials = new SingleInstantParameters();
                if(position != -1) {
                    loginCredentials.bookingId = listOfBooking.getBookingId();
                }else{
                    loginCredentials.bookingId = Utils.requestBookingResponse.getBookingId();
                }
                loginCredentials.bookingStatus = "CANCELLED";
                updateBookingStatus(loginCredentials);
            }
        });
    }

    private void updateBookingStatus(SingleInstantParameters loginCredentials){
        final Dialog dialog = new Dialog(DriverInfoBookingActivity.this, android.R.style.Theme_Dialog);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.loadingimage_layout);
        dialog.setCanceledOnTouchOutside(false);
        dialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        dialog.getWindow().setLayout(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        dialog.show();

        Call<SingleInstantResponse> call = apiService.updateBookingStatus(loginCredentials);
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
                    Utils.updateBookingStatusInstantResponse = response.body();
                    Log.e("BookingStatus",""+Utils.updateBookingStatusInstantResponse.getBookingStatus());
                    showInfoDlg("Success..", "Request Cancelled Successfully.", "Ok", "success");
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
            public void onFailure(Call<SingleInstantResponse> call, Throwable t) {
                // Log error here since request failed
                Log.e("onFailure", t.toString());
                dialog.dismiss();
                showInfoDlg("Error..", "Either there is no network connectivity or server is not available.. Please try again later..", "Ok", "server");
            }
        });
    }

    Marker driverMarker;
    Handler handler = new Handler();
    Runnable finalizer;

    @Override
    public void onBackPressed() {
        if(handler != null && finalizer != null){
            handler.removeCallbacks(finalizer);
        }

        finish();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(handler != null && finalizer != null){
            handler.removeCallbacks(finalizer);
        }
    }

    public void getGeoLocationByDriverId(final SingleInstantParameters loginCredentials){
        final Dialog dialog = new Dialog(DriverInfoBookingActivity.this, android.R.style.Theme_Dialog);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.loadingimage_layout);
        dialog.setCanceledOnTouchOutside(false);
        dialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        dialog.getWindow().setLayout(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        dialog.show();

        Call<SingleInstantResponse> call = apiService.getGeoLocationByDriverId(loginCredentials);
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
                    Utils.getGeoLocationByDriverIdResponse = response.body();
                    Log.e("Latitude",""+Utils.getGeoLocationByDriverIdResponse.getLatitude());
                    Log.e("Longitude",""+Utils.getGeoLocationByDriverIdResponse.getLongitude());
                    LatLng driverLatLng = new LatLng(Double.parseDouble(Utils.getGeoLocationByDriverIdResponse.getLatitude()), Double.parseDouble(Utils.getGeoLocationByDriverIdResponse.getLongitude()));

                    if(driverMarker != null)
                        driverMarker.remove();

                    driverMarker = mMap.addMarker(new MarkerOptions().anchor(0.5f, 0.5f).position(driverLatLng).icon(BitmapDescriptorFactory.fromResource(R.drawable.movingcar_48)));

                    finalizer = new Runnable() {
                        public void run() {
                            getGeoLocationByDriverId(loginCredentials);
                        }
                    };
                    handler.postDelayed(finalizer, 10000);

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
        dialog = new Dialog(DriverInfoBookingActivity.this, android.R.style.Theme_Dialog);
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
        if(navType.equalsIgnoreCase("info") || navType.equalsIgnoreCase("error")){
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
                    Intent ide = new Intent(DriverInfoBookingActivity.this, NavigationMenuActivity.class);
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
        if(position != -1) {
            ListOfBooking listOfBooking = Utils.getBookingByUserIdResponse.get(position);
            String fromLat = listOfBooking.getGeoLocationResponse().getFromLatitude();
            String fromLong = listOfBooking.getGeoLocationResponse().getFromLongitude();
            String toLat = listOfBooking.getGeoLocationResponse().getToLatitude();
            String toLong = listOfBooking.getGeoLocationResponse().getToLongitude();

            origin = new LatLng(Double.parseDouble(fromLat), Double.parseDouble(fromLong));
            dest = new LatLng(Double.parseDouble(toLat), Double.parseDouble(toLong));
        }else{
            // Booked
            String fromLat = Utils.requestBookingResponse.getGeoLocationResponse().getFromLatitude();
            String fromLong = Utils.requestBookingResponse.getGeoLocationResponse().getFromLongitude();
            String toLat = Utils.requestBookingResponse.getGeoLocationResponse().getToLatitude();
            String toLong = Utils.requestBookingResponse.getGeoLocationResponse().getToLongitude();

            origin = new LatLng(Double.parseDouble(fromLat), Double.parseDouble(fromLong));
            dest = new LatLng(Double.parseDouble(toLat), Double.parseDouble(toLong));
        }

        startingMarker = mMap.addMarker(new MarkerOptions().anchor(0.5f, 0.5f).position(origin).icon(BitmapDescriptorFactory.fromResource(R.drawable.startpoint)));
        startingMarker.setTag("starting");
        startingMarker.showInfoWindow();
        endingMarker = mMap.addMarker(new MarkerOptions().anchor(0.5f, 0.5f).position(dest).icon(BitmapDescriptorFactory.fromResource(R.drawable.endpoint)));
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
