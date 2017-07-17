package com.trivectadigital.ziprydeuserapp;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
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
import android.widget.TextView;
import android.widget.Toast;

import com.trivectadigital.ziprydeuserapp.assist.Utils;
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
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;

import java.util.List;
import java.util.Locale;

public class DestinationActivity extends AppCompatActivity implements OnMapReadyCallback,
                                                                            GoogleApiClient.ConnectionCallbacks,
                                                                            GoogleApiClient.OnConnectionFailedListener,
                                                                            ResultCallback<LocationSettingsResult>,
                                                                            LocationListener  {
    private GoogleMap mMap;

    static final Integer LOCATION = 0x1;

    protected GoogleApiClient mGoogleApiClient;
    protected LocationRequest locationRequest;
    int REQUEST_CHECK_SETTINGS = 100;

    private long UPDATE_INTERVAL = 15000;  /* 15 secs */
    private long FASTEST_INTERVAL = 5000; /* 5 secs */

    Location mLastLocation;

    private AppCompatTextView searchPlace, searchPlaceDestination;

    public LinearLayout confirmBooking, gotoMyLocation;

    LatLng crtLatLan;

    ImageView centerMarker;

    TextView title;
    boolean fromSearchPlace = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_destination);

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

        searchPlace = (AppCompatTextView) findViewById(R.id.searchPlace);
        searchPlace.setText(""+Utils.startingPlaceAddress);
        searchPlaceDestination = (AppCompatTextView) findViewById(R.id.searchPlaceDestination);
        centerMarker = (ImageView) findViewById(R.id.centerMarker);
        title = (TextView) findViewById(R.id.title);

        mGoogleApiClient = new GoogleApiClient.Builder(DestinationActivity.this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();

        if (ContextCompat.checkSelfPermission(DestinationActivity.this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(DestinationActivity.this, android.Manifest.permission.ACCESS_FINE_LOCATION)) {
                //This is called if user has denied the permission before
                //In this case I am just asking the permission again
                ActivityCompat.requestPermissions(DestinationActivity.this, new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION}, LOCATION);

            } else {
                ActivityCompat.requestPermissions(DestinationActivity.this, new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION}, LOCATION);
            }
        } else {
            LocationManager manager = (LocationManager) getSystemService(Context.LOCATION_SERVICE );
            boolean statusOfGPS = manager.isProviderEnabled(LocationManager.GPS_PROVIDER);
            if(statusOfGPS){
                getGPSLocation();
            }else{
                //askSwitchOnGPS();
                showInfoDlg("Information", "Please switch ON GPS to get you current location..", "OPEN", "gps");
            }
        }
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

//        searchPlace.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Intent intent = new Intent(DestinationActivity.this, PlacesSearchActivity.class);
//                intent.putExtra("previous","starting");
//                startActivityForResult(intent, Utils.REQUEST_GET_PLACES_DETAILS);
//            }
//        });

        searchPlaceDestination.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(DestinationActivity.this, PlacesSearchActivity.class);
                intent.putExtra("next","destination");
                startActivityForResult(intent, Utils.REQUEST_GET_PLACES_DETAILS);
            }
        });

        confirmBooking = ((LinearLayout) findViewById(R.id.confirmBooking));
        gotoMyLocation = ((LinearLayout) findViewById(R.id.gotoMyLocation));

        gotoMyLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(crtLatLan != null){
                    mMap.moveCamera(CameraUpdateFactory.newLatLng(crtLatLan));
                    mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(crtLatLan,15));
                    mMap.animateCamera(CameraUpdateFactory.zoomIn());
                    mMap.animateCamera(CameraUpdateFactory.zoomTo(15), 2000, null);
                }else{
                    showInfoDlg("Information", "Couldn't get the current location. Please wait..", "Ok", "warning");
                }
            }
        });
    }

    @Override
    public void onBackPressed() {
        Utils.backchkendingLatLan = null;
        Utils.backchkendingPlaceAddress = "";
        finish();
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
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
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        // Add a marker in Sydney and move the camera
        crtLocation = new LatLng(-34, 151);
        //mMap.addMarker(new MarkerOptions().position(crtLocation).icon(BitmapDescriptorFactory.fromResource(R.drawable.location_48)));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(crtLocation));

        mMap.setInfoWindowAdapter(new GoogleMap.InfoWindowAdapter() {
            @Override
            public View getInfoWindow(Marker marker) {
                LayoutInflater li = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                View  mWindow = li.inflate(R.layout.infowindow_layout, null);
                TextView titleUi = ((TextView) mWindow.findViewById(R.id.title));
                titleUi.setText("Set Pickup Location");
                return mWindow;
            }

            @Override
            public View getInfoContents(Marker marker) {
                return null;
            }
        });

        mMap.setOnInfoWindowClickListener(new GoogleMap.OnInfoWindowClickListener() {
            @Override
            public void onInfoWindowClick(Marker marker) {
                Utils.location = crtLocation;
                Log.e("Latitude",""+crtLocation.latitude);
                Log.e("Longitude",""+crtLocation.longitude);
                Intent ide = new Intent(DestinationActivity.this, BookingConfirmationActivity.class);
                ide.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                ide.putExtra("Latitude",""+crtLocation.latitude);
                ide.putExtra("Longitude",""+crtLocation.longitude);
                startActivity(ide);
            }
        });

        confirmBooking.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(Utils.endingLatLan != null || !Utils.endingPlaceAddress.equalsIgnoreCase("")){
                    Utils.backchkendingLatLan = null;
                    Utils.backchkendingPlaceAddress = "";
                    Utils.location = crtLocation;
                    Log.e("Latitude",""+crtLocation.latitude);
                    Log.e("Longitude",""+crtLocation.longitude);
                    Intent ide = new Intent(DestinationActivity.this, DirectionConfirmationActivity.class);
                    ide.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    ide.putExtra("Latitude",""+crtLocation.latitude);
                    ide.putExtra("Longitude",""+crtLocation.longitude);
                    startActivity(ide);
                }else{
                    showInfoDlg("Information", "You must enter destination to proceed.", "Ok", "warning");
                }
            }
        });

        mMap.setOnCameraMoveListener(new GoogleMap.OnCameraMoveListener() {
            @Override
            public void onCameraMove() {
                Log.e("onCameraMove","onCameraMove");
                confirmBooking.setVisibility(View.GONE);
                searchPlaceDestination.setText("Getting Address");
                centerMarker.setImageResource(R.drawable.ic_action_location);
            }
        });

        mMap.setOnCameraIdleListener(new GoogleMap.OnCameraIdleListener() {
            @Override
            public void onCameraIdle() {
                Log.e("onCameraIdle","onCameraIdle");
                confirmBooking.setVisibility(View.VISIBLE);
                LatLng location = mMap.getCameraPosition().target;
                Utils.endingLatLan = location;
                Utils.backchkendingLatLan = location;
                Log.e("latitude",""+location.latitude);
                Log.e("longitude",""+location.longitude);
                String address = getCompleteAddressString(location.latitude, location.longitude);
                Log.e("address",""+address);
                if(!fromSearchPlace) {
                    searchPlaceDestination.setText("" + address);
                    title.setText(""+address);
                    Utils.endingPlaceAddress = address;
                    Utils.backchkendingPlaceAddress = address;
                }else{
                    searchPlaceDestination.setText("" + title.getText().toString().trim());
                    fromSearchPlace = false;
                }
                centerMarker.setImageResource(R.drawable.ic_action_location_new);
            }
        });
    }

    public void getGPSLocation() {
        if(ActivityCompat.checkSelfPermission(DestinationActivity.this, android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED){
            mLastLocation = LocationServices.FusedLocationApi.getLastLocation(
                    mGoogleApiClient);
            if (mLastLocation != null) {
                Log.e("Latitude",""+String.valueOf(mLastLocation.getLatitude()));
                Log.e("Longitude",""+String.valueOf(mLastLocation.getLongitude()));

                if(mMap != null) {
                    crtLocation = new LatLng(mLastLocation.getLatitude(), (mLastLocation.getLongitude()));
                    String address = getCompleteAddressString(mLastLocation.getLatitude(), mLastLocation.getLongitude());
                    Log.e("address",""+address);
                    searchPlaceDestination.setText(""+address);
                    //Marker marker = mMap.addMarker(new MarkerOptions().position(crtLocation).title("Current Location").icon(BitmapDescriptorFactory.fromResource(R.drawable.location_48)));
                    mMap.moveCamera(CameraUpdateFactory.newLatLng(crtLocation));
                    mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(crtLocation,15));
                    mMap.animateCamera(CameraUpdateFactory.zoomIn());
                    mMap.animateCamera(CameraUpdateFactory.zoomTo(15), 2000, null);
                    //marker.showInfoWindow();
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
        dialog = new Dialog(DestinationActivity.this, android.R.style.Theme_Dialog);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.infodialog_layout);
        //dialog.setCanceledOnTouchOutside(true);

        Button positiveBtn = (Button) dialog.findViewById(R.id.positiveBtn);
        positiveBtn.setText(""+btnText);

        Button newnegativeBtn = (Button) dialog.findViewById(R.id.newnegativeBtn);
        if(navType.equalsIgnoreCase("gps") || navType.equalsIgnoreCase("warning")){
            newnegativeBtn.setVisibility(View.GONE);
        }else{
            newnegativeBtn.setVisibility(View.VISIBLE);
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
        if(ActivityCompat.checkSelfPermission(DestinationActivity.this, permissions[0]) == PackageManager.PERMISSION_GRANTED){
            switch (requestCode) {
                //Location
                case 1:
                    LocationManager manager = (LocationManager) getSystemService(Context.LOCATION_SERVICE );
                    boolean statusOfGPS = manager.isProviderEnabled(LocationManager.GPS_PROVIDER);
                    if(statusOfGPS){
                        getGPSLocation();
                    }else{
                        //askSwitchOnGPS();
                        showInfoDlg("Information", "Please switch ON GPS to get you current location..", "OPEN", "gps");
                    }
                    break;
            }
            LocationManager manager = (LocationManager) getSystemService(Context.LOCATION_SERVICE );
            boolean statusOfGPS = manager.isProviderEnabled(LocationManager.GPS_PROVIDER);
            Log.e("statusOfGPS",""+statusOfGPS);
            if(statusOfGPS){
                getGPSLocation();
            }else{
                //askSwitchOnGPS();
                showInfoDlg("Information", "Please switch ON GPS to get you current location..", "OPEN", "gps");
            }
        }else{
            Toast.makeText(DestinationActivity.this, "Permission denied", Toast.LENGTH_SHORT).show();
        }
    }

    LatLng crtLocation;

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        if(ActivityCompat.checkSelfPermission(DestinationActivity.this, android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED){
            mLastLocation = LocationServices.FusedLocationApi.getLastLocation(
                    mGoogleApiClient);
            if (mLastLocation != null) {
                Log.e("Latitude",""+String.valueOf(mLastLocation.getLatitude()));
                Log.e("Longitude",""+String.valueOf(mLastLocation.getLongitude()));

                if(mMap != null) {
                    crtLocation = new LatLng(mLastLocation.getLatitude(), (mLastLocation.getLongitude()));
                    crtLatLan = crtLocation;
                    String address = getCompleteAddressString(mLastLocation.getLatitude(), mLastLocation.getLongitude());
                    Log.e("address",""+address);
                    searchPlaceDestination.setText(""+address);
                    //Marker marker = mMap.addMarker(new MarkerOptions().position(crtLocation).title("Current Location").icon(BitmapDescriptorFactory.fromResource(R.drawable.location_48)));
                    mMap.moveCamera(CameraUpdateFactory.newLatLng(crtLocation));
                    mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(crtLocation,15));
                    mMap.animateCamera(CameraUpdateFactory.zoomIn());
                    mMap.animateCamera(CameraUpdateFactory.zoomTo(15), 2000, null);
                    //marker.showInfoWindow();

                    String latitude = getIntent().getStringExtra("latitude");
                    String longitude = getIntent().getStringExtra("longitude");
                    address = getIntent().getStringExtra("address");
                    Log.e("RESULT_OK", "Lat : "+latitude+" Lng : "+longitude);
                    fromSearchPlace = true;
                    Utils.endingPlaceAddress = address;
                    Utils.backchkendingPlaceAddress = address;
                    searchPlaceDestination.setText(address);
                    title.setText(address);
                    mMap.clear();
                    crtLocation = new LatLng(Double.parseDouble(latitude), Double.parseDouble(longitude));
                    //Marker marker = mMap.addMarker(new MarkerOptions().position(crtLocation).title(""+address).icon(BitmapDescriptorFactory.fromResource(R.drawable.location_48)));
                    mMap.moveCamera(CameraUpdateFactory.newLatLng(crtLocation));
                    mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(crtLocation,15));
                    mMap.animateCamera(CameraUpdateFactory.zoomIn());
                    mMap.animateCamera(CameraUpdateFactory.zoomTo(15), 2000, null);
                        //marker.showInfoWindow();
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
                    status.startResolutionForResult(DestinationActivity.this, REQUEST_CHECK_SETTINGS);
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
            }else{
                //askSwitchOnGPS();
                showInfoDlg("Information", "Please switch ON GPS to get you current location..", "OPEN", "gps");
            }
        }else if(requestCode == Utils.REQUEST_GET_PLACES_DETAILS){
//            if (resultCode == RESULT_OK) {
            if(data != null){
                String latitude = data.getStringExtra("latitude");
                String longitude = data.getStringExtra("longitude");
                String address = data.getStringExtra("address");
                Log.e("RESULT_OK", "Lat : "+latitude+" Lng : "+longitude);
                fromSearchPlace = true;
                title.setText(""+address);
                Utils.endingPlaceAddress = address;
                Utils.backchkendingPlaceAddress = address;
                searchPlaceDestination.setText(address);
                if(mMap != null) {
                    mMap.clear();
                    crtLocation = new LatLng(Double.parseDouble(latitude), Double.parseDouble(longitude));
                    //Marker marker = mMap.addMarker(new MarkerOptions().position(crtLocation).title(""+address).icon(BitmapDescriptorFactory.fromResource(R.drawable.location_48)));
                    mMap.moveCamera(CameraUpdateFactory.newLatLng(crtLocation));
                    mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(crtLocation,15));
                    mMap.animateCamera(CameraUpdateFactory.zoomIn());
                    mMap.animateCamera(CameraUpdateFactory.zoomTo(15), 2000, null);
                    //marker.showInfoWindow();
                }
            }
//            }
        }
    }

    protected void startLocationUpdates() {
        locationRequest = new LocationRequest();
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        locationRequest.setInterval(UPDATE_INTERVAL);
        locationRequest.setFastestInterval(FASTEST_INTERVAL);
        if (ActivityCompat.checkSelfPermission(DestinationActivity.this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(DestinationActivity.this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            Toast.makeText(DestinationActivity.this, "Enable Permissions", Toast.LENGTH_LONG).show();
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

        if (ActivityCompat.checkSelfPermission(DestinationActivity.this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(DestinationActivity.this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            Toast.makeText(DestinationActivity.this, "Enable Permissions", Toast.LENGTH_LONG).show();
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
                    crtLatLan = crtLocation;
                    String address = getCompleteAddressString(mLastLocation.getLatitude(), mLastLocation.getLongitude());
                    Log.e("address",""+address);
                    searchPlaceDestination.setText(""+address);
                    //Marker marker = mMap.addMarker(new MarkerOptions().position(crtLocation).title("Current Location").icon(BitmapDescriptorFactory.fromResource(R.drawable.location_48)));
                    mMap.moveCamera(CameraUpdateFactory.newLatLng(crtLocation));
                    mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(crtLocation,15));
                    mMap.animateCamera(CameraUpdateFactory.zoomIn());
                    mMap.animateCamera(CameraUpdateFactory.zoomTo(15), 2000, null);
                    //marker.showInfoWindow();

                    String latitude = getIntent().getStringExtra("latitude");
                    String longitude = getIntent().getStringExtra("longitude");
                    address = getIntent().getStringExtra("address");
                    Log.e("RESULT_OK", "Lat : "+latitude+" Lng : "+longitude);
                    fromSearchPlace = true;
                    Utils.endingPlaceAddress = address;
                    Utils.backchkendingPlaceAddress = address;
                    title.setText(address);
                    searchPlaceDestination.setText(address);
                    mMap.clear();
                    crtLocation = new LatLng(Double.parseDouble(latitude), Double.parseDouble(longitude));
                    //Marker marker = mMap.addMarker(new MarkerOptions().position(crtLocation).title(""+address).icon(BitmapDescriptorFactory.fromResource(R.drawable.location_48)));
                    mMap.moveCamera(CameraUpdateFactory.newLatLng(crtLocation));
                    mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(crtLocation,15));
                    mMap.animateCamera(CameraUpdateFactory.zoomIn());
                    mMap.animateCamera(CameraUpdateFactory.zoomTo(15), 2000, null);
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

    private String getCompleteAddressString(double LATITUDE, double LONGITUDE) {
        String strAdd = "";
        Geocoder geocoder = new Geocoder(DestinationActivity.this, Locale.getDefault());
        try {
            List<Address> addresses = geocoder.getFromLocation(LATITUDE, LONGITUDE, 1);
            if (addresses != null) {
                Address returnedAddress = addresses.get(0);
                StringBuilder strReturnedAddress = new StringBuilder("");

                for (int i = 0; i < returnedAddress.getMaxAddressLineIndex(); i++) {
                    strReturnedAddress.append(returnedAddress.getAddressLine(i)).append("\n");
                }
                strAdd = strReturnedAddress.toString();
                Log.e("loction address", "" + strReturnedAddress.toString());
            } else {
                Log.e("loction address", "No Address returned!");
            }
        } catch (Exception e) {
            e.printStackTrace();
            Log.e("loction address", "Canont get Address!");
        }
        return strAdd;
    }
}
