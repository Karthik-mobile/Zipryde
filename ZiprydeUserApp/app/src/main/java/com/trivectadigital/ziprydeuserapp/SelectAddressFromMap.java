package com.trivectadigital.ziprydeuserapp;

/**
 * Created by naveendevaraj on 9/28/17.
 */


import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.ResultReceiver;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlaceAutocompleteFragment;
import com.google.android.gms.location.places.ui.PlaceSelectionListener;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.trivectadigital.ziprydeuserapp.assist.FetchAddressIntentService;
import com.trivectadigital.ziprydeuserapp.assist.Utils;

import java.util.Date;






public class SelectAddressFromMap extends AppCompatActivity
        implements
        GoogleMap.OnCameraMoveStartedListener,
        GoogleMap.OnCameraMoveListener,
        GoogleMap.OnCameraMoveCanceledListener,
        GoogleMap.OnCameraIdleListener,OnMapReadyCallback, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener,LocationListener {

    private GoogleMap mGoogleMap;
    private GoogleApiClient mGoogleApiClient;
    private SharedPreferences preferences;
    private SharedPreferences.Editor editor;
    private LocationRequest mLocationRequest;
    private Location mLastLocation;
    private Marker mCurrLocationMarker;
    public static final int MY_PERMISSIONS_REQUEST_LOCATION = 99;
    private static final int PLAY_SERVICES_RESOLUTION_REQUEST = 9000;
    private AddressResultReceiver mResultReceiver;
    private ImageView locationMarker,onMoveMarker;
    private EditText autoComplete;
    private  Address mAddressOutput;
    private LinearLayout llSelectservice;
    private String header;
    String latitude, longitude, address;

    Intent intent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map_address);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

//        preferences = getSharedPreferences(Utils.PREF, 0);
//        editor = preferences.edit();

        intent = getIntent();
        address = intent.getStringExtra("address");
        latitude = intent.getStringExtra("latitude");
        longitude = intent.getStringExtra("longitude");

        ActionBar actionBar = getSupportActionBar(); // As you said you are using support library
        actionBar.setDisplayShowHomeEnabled(false);
        actionBar.setDisplayShowCustomEnabled(true);
        actionBar.setDisplayShowTitleEnabled(false);
        LayoutInflater inflator = (LayoutInflater) this .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View v = inflator.inflate(R.layout.custom_actionbar_all, null);
        actionBar.setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        ActionBar.LayoutParams params = new ActionBar.LayoutParams(
                ActionBar.LayoutParams.MATCH_PARENT,
                ActionBar.LayoutParams.MATCH_PARENT);
        actionBar.setCustomView(v,params);

        header = getIntent().getStringExtra("heading");
        TextView textView = (TextView)v.findViewById(R.id.toolbar_txt);
        textView.setText("Add new address");
        Toolbar parent = (Toolbar) v.getParent();
        parent.setContentInsetsAbsolute(0, 0);
        toolbar.setContentInsetsAbsolute(0, 0);
        toolbar.getContentInsetEnd();
        toolbar.setPadding(0, 0, 0, 0);

        ImageView backImg = (ImageView) findViewById(R.id.image_arrow);
        backImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });




        locationMarker = (ImageView)findViewById(R.id.location_marker_current);
        onMoveMarker = (ImageView) findViewById(R.id.on_move_marker);
        llSelectservice = (LinearLayout)findViewById(R.id.llSelectservice);

        checkPlayServices();
        mResultReceiver = new AddressResultReceiver(new Handler());
        mLocationRequest = new LocationRequest();

        MapFragment mapFragment = (MapFragment) getFragmentManager().findFragmentById(R.id.mapAddress);
        mapFragment.getMapAsync(this);


        PlaceAutocompleteFragment autocompleteFragment = (PlaceAutocompleteFragment)
                getFragmentManager().findFragmentById(R.id.place_autocomplete_fragment);
//
//        AutocompleteFilter typeFilter = new AutocompleteFilter.Builder()
//                .setCountry("IN")
//                .build();
//        autocompleteFragment.setFilter(typeFilter);

//        if(preferences.getString("city855", "").equalsIgnoreCase("Chennai")) {
//
//            autocompleteFragment.setBoundsBias(new LatLngBounds(
//                    new LatLng(12.812042, 80.015316),
//                    new LatLng(13.197739, 80.320187)
//            ));
//        }
//
//        if(preferences.getString("city855", "").equalsIgnoreCase("Bengaluru")) {
//
//            autocompleteFragment.setBoundsBias(new LatLngBounds(
//                    new LatLng(12.700104, 77.345251),
//                    new LatLng(13.178587, 77.901426)
//            ));
//        }

        autoComplete = ((EditText)autocompleteFragment.getView().findViewById(R.id.place_autocomplete_search_input));
        autoComplete.setTextSize(12);
        autoComplete.setMaxLines(3);
        autoComplete.setMinLines(3);
        autoComplete.setSingleLine(false);

        autoComplete.setText(address);

        autocompleteFragment.setOnPlaceSelectedListener(new PlaceSelectionListener() {
            @Override
            public void onPlaceSelected(Place place) {
                // TODO: Get info about the selected place.
                Log.i("Map", "Place: " + place.getAddress());
//
//                Boolean checkCity= false;
//
//                if(preferences.getString("city855", "").equalsIgnoreCase("Chennai")) {
//
//                    if(place.getLatLng().latitude>12.812042 && place.getLatLng().latitude<13.197739
//                            &&place.getLatLng().longitude>80.015316 && place.getLatLng().longitude<80.320187)
//                        checkCity=true;
//                }
//
//                if(preferences.getString("city855", "").equalsIgnoreCase("Bengaluru")) {
//
//                    if(place.getLatLng().latitude>12.700104 && place.getLatLng().latitude<13.178587
//                            &&place.getLatLng().longitude>77.345251 && place.getLatLng().longitude<77.901426)
//                        checkCity=true;
//
//
//                }
                // if(checkCity) {
                mGoogleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(place.getLatLng(), 16));
                autoComplete.setText(place.getAddress());
//                }else {
//                    Toast.makeText(MapAddress.this, "Please change the city selection",
//                            Toast.LENGTH_LONG).show();
//                    onCameraIdle();
//                }
            }

            @Override
            public void onError(Status status) {
                // TODO: Handle the error.
//                Log.i(TAG, "An error occurred: " + status);
            }
        });


    }

    @Override
    public void onBackPressed() {

        super.onBackPressed();

    }



    @Override
    public void onPause() {
        super.onPause();
        //stop location updates when Activity is no longer active
        if (mGoogleApiClient != null) {
            LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient, this);
        }
    }

    @Override
    public void onMapReady(GoogleMap googleMap)
    {
        mGoogleMap=googleMap;
        LatLngBounds ADELAIDE = new LatLngBounds( new LatLng(12.812042, 80.015316),
                new LatLng(13.197739, 80.320187));;
//        if(preferences.getString("city855", "").equalsIgnoreCase("Chennai")) {
//
//            ADELAIDE = new LatLngBounds( new LatLng(12.812042, 80.015316),
//                    new LatLng(13.197739, 80.320187));
//        }
//
//        if(preferences.getString("city855", "").equalsIgnoreCase("Bengaluru")) {
//
//            ADELAIDE = new LatLngBounds(new LatLng(12.700104, 77.345251),
//                    new LatLng(13.178587, 77.901426));
//
//
//        }


// Constrain the camera target to the Adelaide bounds.
        //if(ADELAIDE!=null)
        mGoogleMap.setLatLngBoundsForCameraTarget(ADELAIDE);

        mGoogleMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
        mGoogleMap.getUiSettings().isCompassEnabled();
        mGoogleMap.getUiSettings().isZoomGesturesEnabled();

        //Initialize Google Play Services
        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(this,
                    android.Manifest.permission.ACCESS_FINE_LOCATION)
                    == PackageManager.PERMISSION_GRANTED) {
                //Location Permission already granted
                buildGoogleApiClient();
                mGoogleMap.setMyLocationEnabled(true);
            } else {
                //Request Location Permission
                checkLocationPermission();
            }
        }
        else {
            buildGoogleApiClient();
            mGoogleMap.setMyLocationEnabled(true);
        }

        mGoogleMap.setOnCameraMoveStartedListener(this);
        mGoogleMap.setOnCameraIdleListener(this);


    }

    protected synchronized void buildGoogleApiClient() {
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
        mGoogleApiClient.connect();
    }

    @Override
    public void onConnected(Bundle bundle) {
        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(2147483647);
        mLocationRequest.setFastestInterval(2147483647);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY);
        if (ContextCompat.checkSelfPermission(this,
                android.Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this);
        }
    }

    @Override
    public void onConnectionSuspended(int i) {}

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {}

    @Override
    public void onLocationChanged(Location location)
    {
        Boolean isMarker ;
        if(mLastLocation==null) {
            mLastLocation = location;
            isMarker = true;
        }else isMarker = distance(mLastLocation.getLatitude(), mLastLocation.getLongitude(),
                location.getLatitude(), location.getLongitude()) > 0.3;
        if (isMarker){
            mLastLocation = location;
            startIntentService(mLastLocation);
            if (mCurrLocationMarker != null) {
                mCurrLocationMarker.remove();
            }

            //Place current location marker
            LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
            /*MarkerOptions markerOptions = new MarkerOptions();
            markerOptions.position(latLng);
            markerOptions.title("Current Position");
            markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_MAGENTA));
            mCurrLocationMarker = mGoogleMap.addMarker(markerOptions);*/

            //move map camera
            mGoogleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 16));
            locationMarker.setVisibility(View.VISIBLE);
        }

    }


    private void checkLocationPermission() {
        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {

            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    android.Manifest.permission.ACCESS_FINE_LOCATION)) {

                // Show an explanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.
                new AlertDialog.Builder(this)
                        .setTitle("Location Permission Needed")
                        .setMessage("This app needs the Location permission, please accept to use location functionality")
                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                //Prompt the user once explanation has been shown
                                ActivityCompat.requestPermissions(SelectAddressFromMap.this,
                                        new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},
                                        MY_PERMISSIONS_REQUEST_LOCATION );
                            }
                        })
                        .create()
                        .show();


            } else {
                // No explanation needed, we can request the permission.
                ActivityCompat.requestPermissions(this,
                        new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},
                        MY_PERMISSIONS_REQUEST_LOCATION );
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_LOCATION: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    // permission was granted, yay! Do the
                    // location-related task you need to do.
                    if (ContextCompat.checkSelfPermission(this,
                            android.Manifest.permission.ACCESS_FINE_LOCATION)
                            == PackageManager.PERMISSION_GRANTED) {

                        if (mGoogleApiClient == null) {
                            buildGoogleApiClient();
                        }
                        mGoogleMap.setMyLocationEnabled(true);
                    }

                } else {

                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                    Toast.makeText(this, "permission denied", Toast.LENGTH_LONG).show();
                }

            }

            // other 'case' lines to check for other
            // permissions this app might request
        }
    }


    private double distance(double lat1, double lng1, double lat2, double lng2) {

        double earthRadius =6371 ; // in kilometer, change to 3958.75 for miles output

        double dLat = Math.toRadians(lat2-lat1);
        double dLng = Math.toRadians(lng2-lng1);

        double sindLat = Math.sin(dLat / 2);
        double sindLng = Math.sin(dLng / 2);

        double a = Math.pow(sindLat, 2) + Math.pow(sindLng, 2)
                * Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2));

        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1-a));


        return earthRadius * c; // output distance, in kilometer
    }

    private boolean checkPlayServices() {
        GoogleApiAvailability googleAPI = GoogleApiAvailability.getInstance();
        int result = googleAPI.isGooglePlayServicesAvailable(this);
        if(result != ConnectionResult.SUCCESS) {
            if(googleAPI.isUserResolvableError(result)) {
                googleAPI.getErrorDialog(this, result,
                        PLAY_SERVICES_RESOLUTION_REQUEST).show();
            }

            return false;
        }

        return true;
    }


    protected void startIntentService(Location location) {
        Intent intent = new Intent(this, FetchAddressIntentService.class);
        intent.putExtra(Utils.RECEIVER, mResultReceiver);
        intent.putExtra(Utils.LOCATION_DATA_EXTRA, location);
        startService(intent);
    }

    @Override
    public void onCameraIdle() {
      /*  Animation bottomDown = AnimationUtils.loadAnimation(MapAddress.this,
                R.anim.bottom_down);
        locationMarker.startAnimation(bottomDown);*/
        locationMarker.setVisibility(View.VISIBLE);
        onMoveMarker.setVisibility(View.GONE);
        LatLng latLng =  mGoogleMap.getCameraPosition().target;

        Location location = new Location("Test");
        location.setLatitude(latLng.latitude);
        location.setLongitude(latLng.longitude);
        location.setTime(new Date().getTime()); //Set time as current Date
        startIntentService(location);

    }

    @Override
    public void onCameraMoveCanceled() {

    }

    @Override
    public void onCameraMove() {
        onMoveMarker.setVisibility(View.VISIBLE);
        locationMarker.setVisibility(View.GONE);
    }

    @Override
    public void onCameraMoveStarted(int i) {
       /* Animation bottomUp = AnimationUtils.loadAnimation(MapAddress.this,
                R.anim.bottom_up);
        onMoveMarker.startAnimation(bottomUp);*/
        onMoveMarker.setVisibility(View.VISIBLE);
        locationMarker.setVisibility(View.GONE);
    }

    public void ConfirmLocation(View view) {

        if(mAddressOutput==null){
            Toast.makeText(SelectAddressFromMap.this, "Fail to fetch your location. Please select proper location", Toast.LENGTH_SHORT).show();
        }else {
//            Utils.sellatitude = mAddressOutput.getLatitude();
//            Utils.sellongitude = mAddressOutput.getLongitude();
//            Intent intent = new Intent(SelectAddressFromMap.this,SelectAddressFromMap.class);
//            if(header!=null )
//                if(!header.equalsIgnoreCase("")){
//                    intent.putExtra("heading","Address book");
//                }
//
//            intent.putExtra("Address",mAddressOutput.getAddressLine(0)+mAddressOutput.getAddressLine(1));
//            intent.putExtra("AdressTitle",mAddressOutput.getAddressLine(0)+ ", "+mAddressOutput.getAddressLine(1)
//                    + ", "+mAddressOutput.getLocality()+", "+mAddressOutput.getCountryName()+"-"+mAddressOutput.getPostalCode());
//            intent.putExtra("city",mAddressOutput.getLocality());
//            intent.putExtra("zipCode",mAddressOutput.getPostalCode());
//            startActivity(intent);
//            fini

           // LatLng location = mMap.getCameraPosition().target;

            if (intent.hasExtra("fromPlace")) {
                Intent newIntent = new Intent(SelectAddressFromMap.this, FromToPlaceActivity.class);
                newIntent.putExtra("latitude", "" + mAddressOutput.getLatitude());
                newIntent.putExtra("longitude", "" + mAddressOutput.getLongitude());
                newIntent.putExtra("address", "" + autoComplete.getText().toString());
                startActivity(newIntent);
                finish();
            } else {
                Utils.endingPlaceAddress = autoComplete.getText().toString();
                Utils.endingLatLan = new LatLng(mAddressOutput.getLatitude(), mAddressOutput.getLongitude());
                Intent intent = new Intent(SelectAddressFromMap.this, DirectionConfirmationActivity.class);
                startActivity(intent);
                finish();
            }
        }
    }

    private class AddressResultReceiver extends ResultReceiver {
        private AddressResultReceiver(Handler handler) {
            super(handler);
        }

        @Override
        protected void onReceiveResult(int resultCode, Bundle resultData) {

            // Display the address string
            // or an error message sent from the intent service.
            mAddressOutput = resultData.getParcelable(Utils.RESULT_DATA_KEY);
//            displayAddressOutput();

            // Show a toast message if an address was found.
            if (resultCode == Utils.SUCCESS_RESULT) {
//                showToast(getString(R.string.address_found));
                if (mAddressOutput != null) {
                    autoComplete.setText(mAddressOutput.getAddressLine(0)+ ", "+mAddressOutput.getAddressLine(1)
                            + ", "+mAddressOutput.getLocality()+", "+mAddressOutput.getCountryName()+"-"+mAddressOutput.getPostalCode());
                }
            }

        }
    }
}

