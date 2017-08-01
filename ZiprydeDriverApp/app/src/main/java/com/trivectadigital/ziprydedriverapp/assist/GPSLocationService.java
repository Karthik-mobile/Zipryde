package com.trivectadigital.ziprydedriverapp.assist;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.IBinder;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsResult;
import com.trivectadigital.ziprydedriverapp.SplashActivity;
import com.trivectadigital.ziprydedriverapp.apis.ZiprydeApiClient;
import com.trivectadigital.ziprydedriverapp.apis.ZiprydeApiInterface;
import com.trivectadigital.ziprydedriverapp.modelpost.SingleInstantParameters;

import org.json.JSONObject;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class GPSLocationService extends Service implements GoogleApiClient.ConnectionCallbacks,
                                                                GoogleApiClient.OnConnectionFailedListener,
                                                                LocationListener {

    private final Context mContext;

    static final Integer LOCATION = 0x1;

    protected GoogleApiClient mGoogleApiClient;
    protected LocationRequest locationRequest;
    int REQUEST_CHECK_SETTINGS = 100;

    private long UPDATE_INTERVAL = 15000;  /* 15 secs */
    private long FASTEST_INTERVAL = 5000; /* 5 secs */

    private static final long MIN_DISTANCE_CHANGE_FOR_UPDATES = 10; // 10 meters

    Location location; // location
    double latitude; // latitude
    double longitude; // longitude

    public GPSLocationService(Context context) {
        this.mContext = context;
        getLocation();
    }

    public Location getLocation() {

        mGoogleApiClient = new GoogleApiClient.Builder(mContext)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();

        mGoogleApiClient.connect();

        location = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);

        startLocationUpdates();

        return location;
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        if(ActivityCompat.checkSelfPermission(mContext, android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED){
            Location mLastLocation = LocationServices.FusedLocationApi.getLastLocation(
                    mGoogleApiClient);
            if(mLastLocation!=null) {
                Log.e("onConnected", "GPSLocationService Latitude : " + mLastLocation.getLatitude() + " , Longitude : " + mLastLocation.getLongitude());
                startLocationUpdates();
            }
        }
    }

    @Override
    public void onConnectionSuspended(int i) {
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
    }


    /**
     * Stop using GPS listener
     * Calling this function will stop using GPS in your app
     * */
    public void stopUsingGPS(){
        if (mGoogleApiClient.isConnected()) {
            LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient, this);
            mGoogleApiClient.disconnect();
        }
    }

    protected void startLocationUpdates() {
        locationRequest = new LocationRequest();
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        locationRequest.setInterval(UPDATE_INTERVAL);
        locationRequest.setFastestInterval(FASTEST_INTERVAL);
        if(mGoogleApiClient.isConnected()) {
            Log.e("mGoogleApiClient","GPSLocationService Connected");
            LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, locationRequest, this);
        }else{
            Log.e("mGoogleApiClient","GPSLocationService Not Connected");
            //connectGoogleApiClient();
        }
    }

    public void connectGoogleApiClient() {
        if (mGoogleApiClient != null) {
            mGoogleApiClient.connect();
        }
        if(mGoogleApiClient.isConnected()) {
            Log.e("mGoogleApiClient","GPSLocationService Connected");
            LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, locationRequest, this);
        }else{
            Log.e("mGoogleApiClient","GPSLocationService Not Connected");
            connectGoogleApiClient();
        }
    }

    @Override
    public void onLocationChanged(Location location) {
        if(location!=null) {
            if(Utils.updateLocationBoolean){
                Utils.firstLocation = location;
                Utils.updateLocationBoolean = false;
            }else{
                if(Utils.firstLocation != null) {
                    double distance = Utils.firstLocation.distanceTo(location);
                    Log.e("distance", "" + distance);
                    if (distance >= 3) {
                        Utils.firstLocation = location;
                        SharedPreferences prefs = mContext.getSharedPreferences("LoginCredentials", MODE_PRIVATE);
                        String phoneno = prefs.getString("phoneNumber", null);
                        Log.e("phoneno", "" + phoneno);
                        if (phoneno != null) {
                            insertDriverSession();
                        }
                    }
                }
            }
            Log.e("LocationChanged", "GPSLocationService Latitude : " + location.getLatitude() + " , Longitude : " + location.getLongitude());
            this.location = location;
        }
    }

    public void insertDriverSession(){
        Log.e("UserId","insertDriverSession - "+Utils.verifyLogInUserMobileInstantResponse.getUserId());
        Log.e("Latitude","insertDriverSession - "+Utils.gpsLocationService.getLatitude());
        Log.e("Longitude","insertDriverSession - "+Utils.gpsLocationService.getLongitude());
        SingleInstantParameters loginCredentials = new SingleInstantParameters();
        loginCredentials.userId = ""+Utils.verifyLogInUserMobileInstantResponse.getUserId();
        loginCredentials.fromLatitude = ""+Utils.gpsLocationService.getLatitude();
        loginCredentials.fromLongitude = ""+Utils.gpsLocationService.getLongitude();
        ZiprydeApiInterface apiService = ZiprydeApiClient.getClient().create(ZiprydeApiInterface.class);
        Call<Void> call = apiService.updateDriverSession(loginCredentials);
        call.enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                int statusCode = response.code();
                Log.e("statusCode", "" + statusCode);
                Log.e("response.body", "" + response.body());
                Log.e("response.errorBody", "" + response.errorBody());
                Log.e("response.isSuccessful", "" + response.isSuccessful());
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                // Log error here since request failed
                Log.e("onFailure", t.toString());
            }
        });
    }

    /**
     * Function to get latitude
     * */
    public double getLatitude(){
        if(location != null){
            latitude = location.getLatitude();
        }

        // return latitude
        return latitude;
    }

    /**
     * Function to get longitude
     * */
    public double getLongitude(){
        if(location != null){
            longitude = location.getLongitude();
        }

        // return longitude
        return longitude;
    }

    /**
     * Function to check GPS/wifi enabled
     * @return boolean
     *
    public boolean canGetLocation() {
        return this.canGetLocation;
    }*/

    @Override
    public IBinder onBind(Intent arg0) {
        return null;
    }

}
