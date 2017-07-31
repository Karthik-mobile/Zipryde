package com.trivectadigital.ziprydedriverapp;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.location.Location;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResult;
import com.google.android.gms.location.LocationSettingsStatusCodes;
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
import com.trivectadigital.ziprydedriverapp.apis.ZiprydeApiClient;
import com.trivectadigital.ziprydedriverapp.apis.ZiprydeApiInterface;
import com.trivectadigital.ziprydedriverapp.assist.DataParser;
import com.trivectadigital.ziprydedriverapp.assist.Utils;
import com.trivectadigital.ziprydedriverapp.modelget.ListOfBooking;
import com.trivectadigital.ziprydedriverapp.modelget.ListOfRequestedBooking;
import com.trivectadigital.ziprydedriverapp.modelget.SingleInstantResponse;
import com.trivectadigital.ziprydedriverapp.modelpost.SingleInstantParameters;

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

public class OnGoingBookingActivity extends AppCompatActivity implements OnMapReadyCallback,
                                                                            GoogleApiClient.ConnectionCallbacks,
                                                                            GoogleApiClient.OnConnectionFailedListener,
                                                                            ResultCallback<LocationSettingsResult>,
                                                                            LocationListener {

    private GoogleMap mMap;
    static final Integer LOCATION = 0x1;

    protected GoogleApiClient mGoogleApiClient;
    protected LocationRequest locationRequest;
    int REQUEST_CHECK_SETTINGS = 100;

    private long UPDATE_INTERVAL = 15000;  /* 15 secs */
    private long FASTEST_INTERVAL = 5000; /* 5 secs */

    Location mLastLocation;

    Button starttripBtn, endtripBtn, acceptBtn, declineBtn;
    TextView distanceText, pickdropText, pickdroplocationText,
            fromPlaceText, toPlaceText, suggestedPrice, offerPriceText, timeText, bookingStatus;
    ImageView navigationImg;
    LinearLayout confirmBtn;

    Intent intent;
    ListOfRequestedBooking listOfRequestedBooking;
    ListOfBooking listOfBooking;

    ZiprydeApiInterface apiService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ongoingbooking);

        apiService = ZiprydeApiClient.getClient().create(ZiprydeApiInterface.class);

        confirmBtn = (LinearLayout) findViewById(R.id.confirmBtn);

        starttripBtn = (Button) findViewById(R.id.startBtn);
        endtripBtn = (Button) findViewById(R.id.endBtn);
        acceptBtn = (Button) findViewById(R.id.acceptBtn);
        declineBtn = (Button) findViewById(R.id.declineBtn);

        distanceText = (TextView) findViewById(R.id.distanceText);

        fromPlaceText = (TextView) findViewById(R.id.fromPlaceText);
        toPlaceText = (TextView) findViewById(R.id.toPlaceText);
        suggestedPrice = (TextView) findViewById(R.id.suggestedPrice);
        offerPriceText = (TextView) findViewById(R.id.offerPriceText);
        timeText = (TextView) findViewById(R.id.timeText);
        bookingStatus = (TextView) findViewById(R.id.bookingStatus);

        pickdropText = (TextView) findViewById(R.id.pickdropText);
        pickdroplocationText = (TextView) findViewById(R.id.pickdroplocationText);
        navigationImg = (ImageView) findViewById(R.id.navigationImg);

        intent = getIntent();
        if(intent.hasExtra("type")) {
            int position = intent.getIntExtra("position", 0);
            listOfBooking = Utils.getBookingByDriverIdInstantResponse.get(position);
            fromPlaceText.setText(listOfBooking.getFrom());
            toPlaceText.setText(listOfBooking.getTo());
            suggestedPrice.setText("$" + listOfBooking.getSuggestedPrice());
            offerPriceText.setText("$" + listOfBooking.getOfferedPrice());
            bookingStatus.setText(""+listOfBooking.getBookingStatus());

            String driverStatus = listOfBooking.getDriverStatus();
            Log.e("driverStatus",""+driverStatus);
            if(driverStatus.equals("ACCEPTED")){
                endtripBtn.setVisibility(View.GONE);
                starttripBtn.setVisibility(View.VISIBLE);
                confirmBtn.setVisibility(View.GONE);
            }else if(driverStatus.equals("ON_TRIP")){
                confirmBtn.setVisibility(View.GONE);
                endtripBtn.setVisibility(View.VISIBLE);
                starttripBtn.setVisibility(View.GONE);
            }else {
                endtripBtn.setVisibility(View.GONE);
                starttripBtn.setVisibility(View.GONE);
                confirmBtn.setVisibility(View.GONE);
            }
        }else{
            int position = intent.getIntExtra("position", 0);
            listOfRequestedBooking = Utils.getBookingRequestedByDriverIdResponse.get(position);
            fromPlaceText.setText(listOfRequestedBooking.getFrom());
            toPlaceText.setText(listOfRequestedBooking.getTo());
            suggestedPrice.setText("$" + listOfRequestedBooking.getSuggestedPrice());
            offerPriceText.setText("$" + listOfRequestedBooking.getOfferedPrice());
            bookingStatus.setText(""+listOfRequestedBooking.getBookingStatus());
        }

        mGoogleApiClient = new GoogleApiClient.Builder(OnGoingBookingActivity.this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();

        if (ContextCompat.checkSelfPermission(OnGoingBookingActivity.this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(OnGoingBookingActivity.this, android.Manifest.permission.ACCESS_FINE_LOCATION)) {
                //This is called if user has denied the permission before
                //In this case I am just asking the permission again
                ActivityCompat.requestPermissions(OnGoingBookingActivity.this, new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION}, LOCATION);

            } else {
                ActivityCompat.requestPermissions(OnGoingBookingActivity.this, new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION}, LOCATION);
            }
        } else {
            LocationManager manager = (LocationManager) getSystemService(Context.LOCATION_SERVICE );
            boolean statusOfGPS = manager.isProviderEnabled(LocationManager.GPS_PROVIDER);
            if(statusOfGPS){
                getGPSLocation();
                //showConfirmationDlg();
            }else{
                //askSwitchOnGPS();
                showInfoDlg("Information", "Please turn ON location services in your device.", "OPEN", "gps");
            }
        }
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        acceptBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(intent.hasExtra("type")) {
                    SingleInstantParameters loginCredentials = new SingleInstantParameters();
                    loginCredentials.driverId = ""+Utils.verifyLogInUserMobileInstantResponse.getUserId();
                    loginCredentials.bookingId = listOfBooking.getBookingId();
                    loginCredentials.driverStatus = "ACCEPTED";
                    updateBookingDriverStatus(loginCredentials);
                }else{
                    SingleInstantParameters loginCredentials = new SingleInstantParameters();
                    loginCredentials.driverId = ""+Utils.verifyLogInUserMobileInstantResponse.getUserId();
                    loginCredentials.bookingId = listOfRequestedBooking.getBookingId();
                    loginCredentials.driverStatus = "ACCEPTED";
                    updateBookingDriverStatus(loginCredentials);
                }
            }
        });

        declineBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        starttripBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(intent.hasExtra("type")) {
                    SingleInstantParameters loginCredentials = new SingleInstantParameters();
                    loginCredentials.driverId = ""+Utils.verifyLogInUserMobileInstantResponse.getUserId();
                    loginCredentials.bookingId = listOfBooking.getBookingId();
                    loginCredentials.driverStatus = "ON_TRIP";
                    updateBookingDriverStatus(loginCredentials);
                }else {
                    SingleInstantParameters loginCredentials = new SingleInstantParameters();
                    loginCredentials.driverId = "" + Utils.verifyLogInUserMobileInstantResponse.getUserId();
                    loginCredentials.bookingId = listOfRequestedBooking.getBookingId();
                    loginCredentials.driverStatus = "ON_TRIP";
                    updateBookingDriverStatus(loginCredentials);
                }
            }
        });

        endtripBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(intent.hasExtra("type")) {
                    SingleInstantParameters loginCredentials = new SingleInstantParameters();
                    loginCredentials.driverId = ""+Utils.verifyLogInUserMobileInstantResponse.getUserId();
                    loginCredentials.bookingId = listOfBooking.getBookingId();
                    loginCredentials.driverStatus = "COMPLETED";
                    updateBookingDriverStatus(loginCredentials);
                }else {
                    SingleInstantParameters loginCredentials = new SingleInstantParameters();
                    loginCredentials.driverId = "" + Utils.verifyLogInUserMobileInstantResponse.getUserId();
                    loginCredentials.bookingId = listOfRequestedBooking.getBookingId();
                    loginCredentials.driverStatus = "COMPLETED";
                    updateBookingDriverStatus(loginCredentials);
                }
            }
        });
    }

    public void updateBookingDriverStatus(final SingleInstantParameters loginCredentials){
        final Dialog dialog = new Dialog(OnGoingBookingActivity.this, android.R.style.Theme_Dialog);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.loadingimage_layout);
        dialog.setCanceledOnTouchOutside(false);
        dialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        dialog.getWindow().setLayout(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        dialog.show();

        Call<SingleInstantResponse> call = apiService.updateBookingDriverStatus(loginCredentials);
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
                    Utils.updateBookingDriverStatusInstantResponse = response.body();
                    Log.e("CustomerName",""+Utils.updateBookingDriverStatusInstantResponse.getCustomerName());
                    Log.e("DistanceInMiles",""+Utils.updateBookingDriverStatusInstantResponse.getGeoLocationResponse().getDistanceInMiles());
                    if(loginCredentials.driverStatus.equals("ACCEPTED")){
                        endtripBtn.setVisibility(View.GONE);
                        starttripBtn.setVisibility(View.VISIBLE);
                        confirmBtn.setVisibility(View.GONE);
                        showInfoDlg("Success..", "Request Accepted Successfully.", "OK", "success");
                    }else if(loginCredentials.driverStatus.equals("ON_TRIP")){
                        endtripBtn.setVisibility(View.VISIBLE);
                        starttripBtn.setVisibility(View.GONE);
                        showInfoDlg("Success..", "Trip Started Successfully.", "OK", "success");
                    }else if(loginCredentials.driverStatus.equals("COMPLETED")){
                        showInfoDlg("Success..", "Trip Ended Successfully.", "OK", "trip success");
                    }
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
                timeText.setText(Utils.parsedDuration);
                Log.d("onPostExecute","onPostExecute lineoptions decoded");

            }

            // Drawing polyline in the Google Map for the i-th route
            if(lineOptions != null) {
                mMap.addPolyline(lineOptions);
            }
            else {
                Log.d("onPostExecute","without Polylines drawn");
            }

            distanceText.setText(Utils.parsedDistance);
        }
    }

    List<Marker> markers = new LinkedList<Marker>();

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        // Add a marker in Sydney and move the camera
        crtLocation = new LatLng(-34, 151);
        //mMap.addMarker(new MarkerOptions().position(crtLocation).icon(BitmapDescriptorFactory.fromResource(R.drawable.location_48)));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(crtLocation));

//        mMap.setInfoWindowAdapter(new GoogleMap.InfoWindowAdapter() {
//            @Override
//            public View getInfoWindow(Marker marker) {
//                LayoutInflater li = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
//                View  mWindow = li.inflate(R.layout.infowindow_layout, null);
//                TextView titleUi = ((TextView) mWindow.findViewById(R.id.title));
//                titleUi.setText("Set Pickup Location");
//                return mWindow;
//            }
//
//            @Override
//            public View getInfoContents(Marker marker) {
//                return null;
//            }
//        });
        markers = new LinkedList<Marker>();
        mMap.setOnMapLoadedCallback(new GoogleMap.OnMapLoadedCallback() {
            @Override
            public void onMapLoaded() {
                if(markers.size() > 0){
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
            }
        });

        mMap.setOnInfoWindowClickListener(new GoogleMap.OnInfoWindowClickListener() {
            @Override
            public void onInfoWindowClick(Marker marker) {

            }
        });

        mMap.setOnCameraMoveListener(new GoogleMap.OnCameraMoveListener() {
            @Override
            public void onCameraMove() {
                Log.e("onCameraMove","onCameraMove");
            }
        });

        mMap.setOnCameraIdleListener(new GoogleMap.OnCameraIdleListener() {
            @Override
            public void onCameraIdle() {
                Log.e("onCameraIdle","onCameraIdle");
            }
        });

        if(intent.hasExtra("type")) {
            String fromLat = listOfBooking.getGeoLocationResponse().getFromLatitude();
            String fromLong = listOfBooking.getGeoLocationResponse().getFromLongitude();
            String toLat = listOfBooking.getGeoLocationResponse().getToLatitude();
            String toLong = listOfBooking.getGeoLocationResponse().getToLongitude();

            LatLng fromLatLng = new LatLng(Double.parseDouble(fromLat), Double.parseDouble(fromLong));
            LatLng toLatLng = new LatLng(Double.parseDouble(toLat), Double.parseDouble(toLong));
            String url = getUrl(fromLatLng, toLatLng);
            Log.d("url", ""+url);
            FetchUrl FetchUrl = new FetchUrl();
            FetchUrl.execute(url);

            Marker marker = mMap.addMarker(new MarkerOptions().anchor(0.5f, 0.5f).position(fromLatLng).icon(BitmapDescriptorFactory.fromResource(R.drawable.startpoint)));
            markers.add(marker);
            marker = mMap.addMarker(new MarkerOptions().anchor(0.5f, 0.5f).position(toLatLng).icon(BitmapDescriptorFactory.fromResource(R.drawable.endpoint)));
            markers.add(marker);
        }else{
            String fromLat = listOfRequestedBooking.getGeoLocationResponse().getFromLatitude();
            String fromLong = listOfRequestedBooking.getGeoLocationResponse().getFromLongitude();
            String toLat = listOfRequestedBooking.getGeoLocationResponse().getToLatitude();
            String toLong = listOfRequestedBooking.getGeoLocationResponse().getToLongitude();

            LatLng fromLatLng = new LatLng(Double.parseDouble(fromLat), Double.parseDouble(fromLong));
            LatLng toLatLng = new LatLng(Double.parseDouble(toLat), Double.parseDouble(toLong));
            String url = getUrl(fromLatLng, toLatLng);
            Log.d("url", ""+url);
            FetchUrl FetchUrl = new FetchUrl();
            FetchUrl.execute(url);

            Marker marker = mMap.addMarker(new MarkerOptions().anchor(0.5f, 0.5f).position(fromLatLng).icon(BitmapDescriptorFactory.fromResource(R.drawable.startpoint)));
            markers.add(marker);
            marker = mMap.addMarker(new MarkerOptions().anchor(0.5f, 0.5f).position(toLatLng).icon(BitmapDescriptorFactory.fromResource(R.drawable.endpoint)));
            markers.add(marker);
        }
    }

    public void getGPSLocation() {
        if(ActivityCompat.checkSelfPermission(OnGoingBookingActivity.this, android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED){
            mLastLocation = LocationServices.FusedLocationApi.getLastLocation(
                    mGoogleApiClient);
            if (mLastLocation != null) {
                Log.e("Latitude",""+String.valueOf(mLastLocation.getLatitude()));
                Log.e("Longitude",""+String.valueOf(mLastLocation.getLongitude()));

                if(mMap != null) {
                    crtLocation = new LatLng(mLastLocation.getLatitude(), (mLastLocation.getLongitude()));
                    Marker marker = mMap.addMarker(new MarkerOptions().position(crtLocation).icon(BitmapDescriptorFactory.fromResource(R.drawable.movingcar_48)));
                    markers.add(marker);
//                    Marker marker = mMap.addMarker(new MarkerOptions().position(crtLocation).icon(BitmapDescriptorFactory.fromResource(R.drawable.micro_car_48)));
//                    mMap.moveCamera(CameraUpdateFactory.newLatLng(crtLocation));
//                    mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(crtLocation,15));
//                    mMap.animateCamera(CameraUpdateFactory.zoomIn());
//                    mMap.animateCamera(CameraUpdateFactory.zoomTo(15), 2000, null);
                    //marker.showInfoWindow();
//                    markers.add(marker);
//                    double meters = 500;
//                    double coef = meters * 0.0000089;
//                    double new_lat = mLastLocation.getLatitude() + coef;
//                    double new_long = mLastLocation.getLongitude() + coef / Math.cos(mLastLocation.getLatitude() * 0.018);
//                    tempLocation = new LatLng(new_lat, new_long);
//                    marker = mMap.addMarker(new MarkerOptions().position(tempLocation).icon(BitmapDescriptorFactory.fromResource(R.drawable.pickup_icon_48)));
//                    markers.add(marker);
//
//                    String url = getUrl(crtLocation, tempLocation);
//                    Log.d("url", ""+url);
//                    FetchUrl FetchUrl = new FetchUrl();
//                    FetchUrl.execute(url);
                }
            }
            startLocationUpdates();
        }
    }


    @Override
    public void onStart() {
        super.onStart();
        if (mGoogleApiClient != null) {
            mGoogleApiClient.connect();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if (mGoogleApiClient != null) {
            mGoogleApiClient.connect();
        }
    }

    public void askSwitchOnGPS() {
        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder()
                .addLocationRequest(locationRequest);
        builder.setAlwaysShow(true);
        PendingResult<LocationSettingsResult> result =
                LocationServices.SettingsApi.checkLocationSettings(
                        mGoogleApiClient,
                        builder.build()
                );
        result.setResultCallback(this);
    }

    Dialog dialog;
    private void showInfoDlg(String title, String content, String btnText, final String navType) {
        dialog = new Dialog(OnGoingBookingActivity.this, android.R.style.Theme_Dialog);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.infodialog_layout);
        //dialog.setCanceledOnTouchOutside(true);

        ImageView headerIcon = (ImageView) dialog.findViewById(R.id.headerIcon);
        if (navType.equalsIgnoreCase("server")) {
            headerIcon.setImageResource(R.drawable.erroricon);
        }

        Button positiveBtn = (Button) dialog.findViewById(R.id.positiveBtn);
        positiveBtn.setText(""+btnText);

        Button newnegativeBtn = (Button) dialog.findViewById(R.id.newnegativeBtn);
        if(navType.equalsIgnoreCase("gps") || navType.equalsIgnoreCase("warning")){
            newnegativeBtn.setVisibility(View.GONE);
        }else{
            newnegativeBtn.setVisibility(View.VISIBLE);
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
        }else if(navType.equalsIgnoreCase("trip success")){
            headerIcon.setImageResource(R.drawable.successicon);
            positiveBtn.setVisibility(View.GONE);
            newnegativeBtn.setVisibility(View.GONE);
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    dialog.dismiss();
                    String bookingId = "";
                    String suggestedPrice = "";
                    String offeredPrice = "";
                    String distanceInMiles = "";
                    String fromaddress = "";
                    String toaddress = "";
                    if(intent.hasExtra("type")) {
                        bookingId = listOfBooking.getBookingId();
                        suggestedPrice = listOfBooking.getSuggestedPrice();
                        offeredPrice = listOfBooking.getOfferedPrice();
                        distanceInMiles = listOfBooking.getGeoLocationResponse().getDistanceInMiles();
                        fromaddress = listOfBooking.getFrom();
                        toaddress = listOfBooking.getTo();
                    }else {
                        bookingId = listOfRequestedBooking.getBookingId();
                        suggestedPrice = listOfRequestedBooking.getSuggestedPrice();
                        offeredPrice = listOfRequestedBooking.getOfferedPrice();
                        distanceInMiles = listOfRequestedBooking.getGeoLocationResponse().getDistanceInMiles();
                        fromaddress = listOfRequestedBooking.getFrom();
                        toaddress = listOfRequestedBooking.getTo();
                    }
                    Intent ide = new Intent(OnGoingBookingActivity.this, CashDisplyActivity.class);
                    ide.putExtra("bookingId",""+bookingId);
                    ide.putExtra("suggestedPrice",""+suggestedPrice);
                    ide.putExtra("offeredPrice",""+offeredPrice);
                    ide.putExtra("distanceInMiles",""+distanceInMiles);
                    ide.putExtra("fromaddress",""+fromaddress);
                    ide.putExtra("toaddress",""+toaddress);
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
                if(navType.equalsIgnoreCase("gps")){
                    startActivityForResult(new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS), REQUEST_CHECK_SETTINGS);
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
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if(ActivityCompat.checkSelfPermission(OnGoingBookingActivity.this, permissions[0]) == PackageManager.PERMISSION_GRANTED){
            switch (requestCode) {
                //Location
                case 1:
                    LocationManager manager = (LocationManager) getSystemService(Context.LOCATION_SERVICE );
                    boolean statusOfGPS = manager.isProviderEnabled(LocationManager.GPS_PROVIDER);
                    if(statusOfGPS){
                        getGPSLocation();
                        //showConfirmationDlg();
                    }else{
                        //askSwitchOnGPS();
                        showInfoDlg("Information", "Please turn ON location services in your device.", "OPEN", "gps");
                    }
                    break;
            }
        }else{
            Toast.makeText(OnGoingBookingActivity.this, "Permission denied", Toast.LENGTH_SHORT).show();
        }
    }

    LatLng crtLocation, tempLocation;

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        if(ActivityCompat.checkSelfPermission(OnGoingBookingActivity.this, android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED){
            mLastLocation = LocationServices.FusedLocationApi.getLastLocation(
                    mGoogleApiClient);
            if (mLastLocation != null) {
                Log.e("Latitude",""+String.valueOf(mLastLocation.getLatitude()));
                Log.e("Longitude",""+String.valueOf(mLastLocation.getLongitude()));

                if(mMap != null) {
                    crtLocation = new LatLng(mLastLocation.getLatitude(), (mLastLocation.getLongitude()));
                    Marker marker = mMap.addMarker(new MarkerOptions().position(crtLocation).icon(BitmapDescriptorFactory.fromResource(R.drawable.movingcar_48)));
                    markers.add(marker);
//                    Marker marker = mMap.addMarker(new MarkerOptions().position(crtLocation).icon(BitmapDescriptorFactory.fromResource(R.drawable.micro_car_48)));
//                    mMap.moveCamera(CameraUpdateFactory.newLatLng(crtLocation));
//                    mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(crtLocation,15));
//                    mMap.animateCamera(CameraUpdateFactory.zoomIn());
//                    mMap.animateCamera(CameraUpdateFactory.zoomTo(15), 2000, null);
//                    //marker.showInfoWindow();
//                    markers.add(marker);
//                    double meters = 500;
//                    double coef = meters * 0.0000089;
//                    double new_lat = mLastLocation.getLatitude() + coef;
//                    double new_long = mLastLocation.getLongitude() + coef / Math.cos(mLastLocation.getLatitude() * 0.018);
//                    tempLocation = new LatLng(new_lat, new_long);
//                    marker = mMap.addMarker(new MarkerOptions().position(tempLocation).icon(BitmapDescriptorFactory.fromResource(R.drawable.pickup_icon_48)));
//                    markers.add(marker);
//
//                    String url = getUrl(crtLocation, tempLocation);
//                    Log.d("url", ""+url);
//                    FetchUrl FetchUrl = new FetchUrl();
//                    FetchUrl.execute(url);
                }
            }
        }
    }

    @Override
    public void onConnectionSuspended(int i) {
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
    }

    @Override
    public void onResult(@NonNull LocationSettingsResult locationSettingsResult) {
        final Status status = locationSettingsResult.getStatus();
        switch (status.getStatusCode()) {
            case LocationSettingsStatusCodes.SUCCESS:
                // NO need to show the dialog;
                break;

            case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:
                //  Location settings are not satisfied. Show the user a dialog
                try {
                    // Show the dialog by calling startResolutionForResult(), and check the result
                    // in onActivityResult().
                    status.startResolutionForResult(OnGoingBookingActivity.this, REQUEST_CHECK_SETTINGS);
                } catch (IntentSender.SendIntentException e) {
                    //failed to show
                }
                break;

            case LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE:
                // Location settings are unavailable so not possible to show any dialog now
                break;
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CHECK_SETTINGS) {
            LocationManager manager = (LocationManager) getSystemService(Context.LOCATION_SERVICE );
            boolean statusOfGPS = manager.isProviderEnabled(LocationManager.GPS_PROVIDER);
            if(statusOfGPS){
                getGPSLocation();
                //showConfirmationDlg();
            }else{
                //askSwitchOnGPS();
                showInfoDlg("Information", "Please turn ON location services in your device.", "OPEN", "gps");
            }
        }
    }

    protected void startLocationUpdates() {
        locationRequest = new LocationRequest();
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        locationRequest.setInterval(UPDATE_INTERVAL);
        locationRequest.setFastestInterval(FASTEST_INTERVAL);
        if (ActivityCompat.checkSelfPermission(OnGoingBookingActivity.this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(OnGoingBookingActivity.this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            Toast.makeText(OnGoingBookingActivity.this, "Enable Permissions", Toast.LENGTH_LONG).show();
        }
        if(mGoogleApiClient.isConnected()) {
            Log.e("mGoogleApiClient","Connected");
            LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, locationRequest, this);
        }else{
            Log.e("mGoogleApiClient","Not Connected");
            //connectGoogleApiClient();
        }
    }

    public void connectGoogleApiClient() {
        if (mGoogleApiClient != null) {
            mGoogleApiClient.connect();
        }

        if (ActivityCompat.checkSelfPermission(OnGoingBookingActivity.this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(OnGoingBookingActivity.this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            Toast.makeText(OnGoingBookingActivity.this, "Enable Permissions", Toast.LENGTH_LONG).show();
        }
        if(mGoogleApiClient.isConnected()) {
            Log.e("mGoogleApiClient","Connected");
            LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, locationRequest, this);
        }else{
            Log.e("mGoogleApiClient","Not Connected");
            connectGoogleApiClient();
        }
    }

    @Override
    public void onLocationChanged(Location location) {
        if(location!=null) {
            Log.e("LocationChanged", "Latitude : " + location.getLatitude() + " , Longitude : " + location.getLongitude());
            if (mLastLocation == null) {
                mLastLocation = location;
                if(mMap != null) {
                    crtLocation = new LatLng(mLastLocation.getLatitude(), (mLastLocation.getLongitude()));
                    Marker marker = mMap.addMarker(new MarkerOptions().position(crtLocation).icon(BitmapDescriptorFactory.fromResource(R.drawable.micro_car_48)));
                    mMap.moveCamera(CameraUpdateFactory.newLatLng(crtLocation));
                    mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(crtLocation,15));
                    mMap.animateCamera(CameraUpdateFactory.zoomIn());
                    mMap.animateCamera(CameraUpdateFactory.zoomTo(15), 2000, null);
                    //marker.showInfoWindow();
                    markers.add(marker);
                    double meters = 500;
                    double coef = meters * 0.0000089;
                    double new_lat = mLastLocation.getLatitude() + coef;
                    double new_long = mLastLocation.getLongitude() + coef / Math.cos(mLastLocation.getLatitude() * 0.018);
                    tempLocation = new LatLng(new_lat, new_long);
                    marker = mMap.addMarker(new MarkerOptions().position(tempLocation).icon(BitmapDescriptorFactory.fromResource(R.drawable.pickup_icon_48)));
                    markers.add(marker);

                    String url = getUrl(crtLocation, tempLocation);
                    Log.d("url", ""+url);
                    FetchUrl FetchUrl = new FetchUrl();
                    FetchUrl.execute(url);
                }
            }
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        stopLocationUpdates();
    }

    public void stopLocationUpdates() {
        if (mGoogleApiClient.isConnected()) {
            LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient, this);
            mGoogleApiClient.disconnect();
        }
    }

    Dialog confirmdialog;
    private void showConfirmationDlg() {
        confirmdialog = new Dialog(OnGoingBookingActivity.this, android.R.style.Theme_Dialog);
        confirmdialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        confirmdialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        confirmdialog.setContentView(R.layout.confirmationdialog_layout);
        //dialog.setCanceledOnTouchOutside(true);

        Button positiveBtn = (Button) confirmdialog.findViewById(R.id.positiveBtn);
        Button newnegativeBtn = (Button) confirmdialog.findViewById(R.id.newnegativeBtn);

        positiveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                confirmdialog.dismiss();
            }
        });

        newnegativeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                confirmdialog.dismiss();
                onBackPressed();
            }
        });

        confirmdialog.setCanceledOnTouchOutside(false);
        confirmdialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        confirmdialog.getWindow().setLayout(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        confirmdialog.show();
    }
}
