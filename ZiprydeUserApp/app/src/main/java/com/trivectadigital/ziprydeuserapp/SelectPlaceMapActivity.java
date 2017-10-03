package com.trivectadigital.ziprydeuserapp;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.IntentSender;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.Html;
import android.text.Spanned;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.AutoCompleteTextView;
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
import com.google.android.gms.location.places.AutocompleteFilter;
import com.google.android.gms.location.places.AutocompletePrediction;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.PlaceBuffer;
import com.google.android.gms.location.places.Places;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.gson.Gson;
import com.trivectadigital.ziprydeuserapp.apis.ZiprydeApiClient;
import com.trivectadigital.ziprydeuserapp.apis.ZiprydeApiInterface;
import com.trivectadigital.ziprydeuserapp.assist.PlaceAutocompleteAdapter;
import com.trivectadigital.ziprydeuserapp.assist.Utils;
import com.trivectadigital.ziprydeuserapp.modelget.ListOfCurrentCabs;
import com.trivectadigital.ziprydeuserapp.modelget.SingleInstantResponse;
import com.trivectadigital.ziprydeuserapp.modelpost.SingleInstantParameters;

import org.json.JSONObject;

import java.util.LinkedList;
import java.util.List;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SelectPlaceMapActivity extends AppCompatActivity implements OnMapReadyCallback,
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

    LatLng crtLatLan;

    String latitude, longitude, address;

    private PlaceAutocompleteAdapter mAdapter;

    private AutoCompleteTextView mAutocompleteView;

    private static final LatLngBounds BOUNDS_GREATER_SYDNEY = new LatLngBounds(
            new LatLng(-34.041458, 150.790100), new LatLng(-33.682247, 151.383362));

    public static final String TAG = "PlacesSearchActivity";

    ImageView clearsearchImageView;

    LinearLayout confirmBooking;

    LinearLayout gotoMyLocation;

    ZiprydeApiInterface apiService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_place_map);

        apiService = ZiprydeApiClient.getClient().create(ZiprydeApiInterface.class);

        mGoogleApiClient = new GoogleApiClient.Builder(SelectPlaceMapActivity.this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .enableAutoManage(this, 0 /* clientId */, this)
                .addApi(Places.GEO_DATA_API)
                .build();

        if (ContextCompat.checkSelfPermission(SelectPlaceMapActivity.this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(SelectPlaceMapActivity.this, android.Manifest.permission.ACCESS_FINE_LOCATION)) {
                //This is called if user has denied the permission before
                //In this case I am just asking the permission again
                ActivityCompat.requestPermissions(SelectPlaceMapActivity.this, new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION}, LOCATION);

            } else {
                ActivityCompat.requestPermissions(SelectPlaceMapActivity.this, new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION}, LOCATION);
            }
        } else {
            LocationManager manager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
            boolean statusOfGPS = manager.isProviderEnabled(LocationManager.GPS_PROVIDER);
            if (statusOfGPS) {
                getGPSLocation();
            } else {
                //askSwitchOnGPS();
                showInfoDlg("Information", "Please turn ON location services in your device.", "OPEN", "gps");
            }
        }
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        ImageView backImg = (ImageView) findViewById(R.id.backImg);
        backImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        // Retrieve the AutoCompleteTextView that will display Place suggestions.
        mAutocompleteView = (AutoCompleteTextView)
                findViewById(R.id.searchPlace);

        clearsearchImageView = (ImageView) findViewById(R.id.clearsearchImageView);
        confirmBooking = (LinearLayout) findViewById(R.id.confirmBooking);

        clearsearchImageView.setVisibility(View.GONE);
        // Register a listener that receives callbacks when a suggestion has been selected
        mAutocompleteView.setOnItemClickListener(mAutocompleteClickListener);

        gotoMyLocation = ((LinearLayout) findViewById(R.id.gotoMyLocation));

        AutocompleteFilter filter = new AutocompleteFilter.Builder().setCountry(Utils.countryCode).build();
        // Set up the adapter that will retrieve suggestions from the Places Geo Data API that cover
        // the entire world.
        mAdapter = new PlaceAutocompleteAdapter(this, mGoogleApiClient, null, null);

        mAutocompleteView.setAdapter(mAdapter);

        final Intent intent = getIntent();
        address = intent.getStringExtra("address");
        latitude = intent.getStringExtra("latitude");
        longitude = intent.getStringExtra("longitude");

        if (!address.equalsIgnoreCase("")) {
            mAutocompleteView.setText(address);
            mAutocompleteView.setSelection(mAutocompleteView.getText().length());
            clearsearchImageView.setVisibility(View.VISIBLE);
        }

        clearsearchImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mAutocompleteView.setText("");
            }
        });

        mAutocompleteView.addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.length() <= 0) {
                    clearsearchImageView.setVisibility(View.GONE);
                } else {
                    clearsearchImageView.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        confirmBooking.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LatLng location = mMap.getCameraPosition().target;

                if (intent.hasExtra("fromPlace")) {
                    Intent intent = new Intent(SelectPlaceMapActivity.this, FromToPlaceActivity.class);
                    intent.putExtra("latitude", "" + location.latitude);
                    intent.putExtra("longitude", "" + location.longitude);
                    intent.putExtra("address", "" + address);
                    startActivity(intent);
                    finish();
                } else {
                    Utils.endingPlaceAddress = address;
                    Utils.endingLatLan = new LatLng(location.latitude, location.longitude);
                    Intent intent = new Intent(SelectPlaceMapActivity.this, DirectionConfirmationActivity.class);
                    startActivity(intent);
                    finish();
                }
            }
        });

        gotoMyLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (crtLatLan != null) {
                    mMap.moveCamera(CameraUpdateFactory.newLatLng(crtLatLan));
                    mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(crtLatLan, 15));
                    mMap.animateCamera(CameraUpdateFactory.zoomIn());
                    mMap.animateCamera(CameraUpdateFactory.zoomTo(15), 2000, null);
                } else {
                    showInfoDlg("Information", "Couldn't get the current location. Please wait..", "OK", "warning");
                }
            }
        });

    }

    /**
     * Listener that handles selections from suggestions from the AutoCompleteTextView that
     * displays Place suggestions.
     * Gets the place id of the selected item and issues a request to the Places Geo Data API
     * to retrieve more details about the place.
     *
     * @see com.google.android.gms.location.places.GeoDataApi#getPlaceById(com.google.android.gms.common.api.GoogleApiClient,
     * String...)
     */
    private AdapterView.OnItemClickListener mAutocompleteClickListener
            = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            /*
             Retrieve the place ID of the selected item from the Adapter.
             The adapter stores each Place suggestion in a AutocompletePrediction from which we
             read the place ID and title.
              */
            final AutocompletePrediction item = mAdapter.getItem(position);
            final String placeId = item.getPlaceId();
            final CharSequence primaryText = item.getPrimaryText(null);

            Log.i(TAG, "Autocomplete item selected: " + primaryText);

            /*
             Issue a request to the Places Geo Data API to retrieve a Place object with additional
             details about the place.
              */
            PendingResult<PlaceBuffer> placeResult = Places.GeoDataApi
                    .getPlaceById(mGoogleApiClient, placeId);
            placeResult.setResultCallback(mUpdatePlaceDetailsCallback);

//            Toast.makeText(getApplicationContext(), "Clicked: " + primaryText,
//                    Toast.LENGTH_SHORT).show();
            Log.i(TAG, "Called getPlaceById to get Place details for " + placeId);
        }
    };

    /**
     * Callback for results from a Places Geo Data API query that shows the first place result in
     * the details view on screen.
     */
    private ResultCallback<PlaceBuffer> mUpdatePlaceDetailsCallback
            = new ResultCallback<PlaceBuffer>() {
        @Override
        public void onResult(PlaceBuffer places) {
            if (!places.getStatus().isSuccess()) {
                // Request did not complete successfully
                Log.e(TAG, "Place query did not complete. Error: " + places.getStatus().toString());
                places.release();
                return;
            }
            // Get the Place object from the buffer.
            final Place place = places.get(0);

            // Format details of the place for display and show it in a TextView.
//            Log.e(TAG, "" + formatPlaceDetails(getResources(), place.getName(),
//                    place.getId(), place.getAddress(), place.getPhoneNumber(),
//                    place.getWebsiteUri()));

            LatLng geoLatLng = place.getLatLng();
            Log.e(TAG, "Lat : " + geoLatLng.latitude + " Lng : " + geoLatLng.longitude);
            String tempAddress = "" + place.getAddress();
            String address = "";
            if (tempAddress.contains(place.getName())) {
                address = tempAddress;
            } else {
                address = place.getName() + "," + place.getAddress();
            }
//            String address = ""+place.getAddress();


            // Display the third party attributions if set.
//            final CharSequence thirdPartyAttribution = places.getAttributions();
//            if (thirdPartyAttribution != null) {
//                Log.e(TAG, "" + Html.fromHtml(thirdPartyAttribution.toString()));
//            }

            Log.i(TAG, "Place details received: " + place.getName());

            places.release();

            mAutocompleteView.setText(address);
            crtLocation = new LatLng(geoLatLng.latitude, geoLatLng.longitude);
            mMap.moveCamera(CameraUpdateFactory.newLatLng(crtLocation));
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(crtLocation, 15));
            mMap.animateCamera(CameraUpdateFactory.zoomIn());
            mMap.animateCamera(CameraUpdateFactory.zoomTo(15), 2000, null);
            mAutocompleteView.setSelection(mAutocompleteView.getText().length());
            clearsearchImageView.setVisibility(View.VISIBLE);

        }
    };

    private static Spanned formatPlaceDetails(Resources res, CharSequence name, String id,
                                              CharSequence address, CharSequence phoneNumber, Uri websiteUri) {
        Log.e(TAG, res.getString(R.string.place_details, name, id, address, phoneNumber,
                websiteUri));
        return Html.fromHtml(res.getString(R.string.place_details, name, id, address, phoneNumber,
                websiteUri));

    }

    @Override
    public void onBackPressed() {
        finish();
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

        if (!latitude.equalsIgnoreCase("") && !longitude.equalsIgnoreCase("")) {
            crtLocation = new LatLng(Double.parseDouble(latitude), Double.parseDouble(longitude));
            mMap.moveCamera(CameraUpdateFactory.newLatLng(crtLocation));
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(crtLocation, 15));
            mMap.animateCamera(CameraUpdateFactory.zoomIn());
            mMap.animateCamera(CameraUpdateFactory.zoomTo(15), 2000, null);
        }

        mMap.setOnCameraMoveListener(new GoogleMap.OnCameraMoveListener() {
            @Override
            public void onCameraMove() {
                Log.e("onCameraMove", "onCameraMove");
                mAutocompleteView.setText("Getting Address");
                clearsearchImageView.setVisibility(View.GONE);
            }
        });

        mMap.setOnCameraIdleListener(new GoogleMap.OnCameraIdleListener() {
            @Override
            public void onCameraIdle() {
                Log.e("onCameraIdle", "onCameraIdle");
                LatLng location = mMap.getCameraPosition().target;
                Utils.endingLatLan = location;
                Utils.backchkendingLatLan = location;
                Log.e("latitude", "" + location.latitude);
                Log.e("longitude", "" + location.longitude);
                address = getCompleteAddressString(location.latitude, location.longitude);
                Log.e("address", "" + address);
                if(!address.isEmpty()) {
                    mAutocompleteView.setText(address);
                }
                mAutocompleteView.setSelection(mAutocompleteView.getText().length());
                clearsearchImageView.setVisibility(View.VISIBLE);
                if(!address.isEmpty()) {
                    getNearByActiveDrivers("" + location.latitude, "" + location.longitude);
                }
            }
        });
    }

    public void getNearByActiveDrivers(String latitude, String longitude) {
        Log.e("fromLatitude", "" + latitude);
        Log.e("fromLongitude", "" + longitude);
        SingleInstantParameters loginCredentials = new SingleInstantParameters();
        loginCredentials.fromLatitude = latitude;
        loginCredentials.fromLongitude = longitude;

        final Dialog dialog = new Dialog(SelectPlaceMapActivity.this, android.R.style.Theme_Dialog);
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
                    final List<Marker> markers = new LinkedList<Marker>();
                    for (int i = 0; i < Utils.getNearByActiveDriversInstantResponse.size(); i++) {
                        String latitude = Utils.getNearByActiveDriversInstantResponse.get(i).getLatitude();
                        String longitude = Utils.getNearByActiveDriversInstantResponse.get(i).getLongitude();
                        String cabID = Utils.getNearByActiveDriversInstantResponse.get(i).getCabTypeId();
                        String userId = Utils.getNearByActiveDriversInstantResponse.get(i).getUserId();
                        Log.e("cabID userId", "userId : " + userId + " cabID : " + cabID);
                        Log.e("latitude longitude", "latitude : " + latitude + " longitude : " + longitude);
                        LatLng tempLatLong = new LatLng(Double.parseDouble(latitude), Double.parseDouble(longitude));
                        Marker marker = mMap.addMarker(new MarkerOptions().position(tempLatLong).icon(BitmapDescriptorFactory.fromResource(R.drawable.movingcar_48)));
                        Log.e("marker contains", "" + markers.contains(marker));
                        markers.add(marker);
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
                            showInfoDlg("Error..", "" + jObjError.getString("message"), "OK", "error");
                        }

                    } catch (Exception e) {
                        Toast.makeText(SelectPlaceMapActivity.this, "Either there is no network connectivity or server is not available.. Please try again later..", Toast.LENGTH_LONG).show();
                    }
                }
            }

            @Override
            public void onFailure(Call<LinkedList<ListOfCurrentCabs>> call, Throwable t) {
                // Log error here since request failed
                Log.e("onFailure", t.toString());
                dialog.dismiss();
                showInfoDlg(getString(R.string.error), "" + getString(R.string.errmsg_sessionexpired), getString(R.string.btn_ok), "logout");
                //Toast.makeText(SelectPlaceMapActivity.this, "Either there is no network connectivity or server is not available.. Please try again later..", Toast.LENGTH_LONG).show();
            }
        });
    }

    public void getGPSLocation() {
        if (ActivityCompat.checkSelfPermission(SelectPlaceMapActivity.this, android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            mLastLocation = LocationServices.FusedLocationApi.getLastLocation(
                    mGoogleApiClient);
            if (mLastLocation != null) {
                Log.e("Latitude", "" + String.valueOf(mLastLocation.getLatitude()));
                Log.e("Longitude", "" + String.valueOf(mLastLocation.getLongitude()));

                if (mMap != null) {
                    crtLocation = new LatLng(mLastLocation.getLatitude(), (mLastLocation.getLongitude()));
                    String address = getCompleteAddressString(mLastLocation.getLatitude(), mLastLocation.getLongitude());
                    Log.e("address", "" + address);
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
        SharedPreferences prefs = getSharedPreferences("LoginCredentials", MODE_PRIVATE);
        Gson gson = new Gson();
        String json = prefs.getString("LoginCredentials", "");
        Utils.verifyLogInUserMobileInstantResponse = gson.fromJson(json, SingleInstantResponse.class);
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
        dialog = new Dialog(SelectPlaceMapActivity.this, android.R.style.Theme_Dialog);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.infodialog_layout);
        //dialog.setCanceledOnTouchOutside(true);

        Button positiveBtn = (Button) dialog.findViewById(R.id.positiveBtn);
        positiveBtn.setText("" + btnText);

        Button newnegativeBtn = (Button) dialog.findViewById(R.id.newnegativeBtn);
        if (navType.equalsIgnoreCase("gps") || navType.equalsIgnoreCase("warning") || navType.equalsIgnoreCase("server")) {
            newnegativeBtn.setVisibility(View.GONE);
        } else {
            newnegativeBtn.setVisibility(View.VISIBLE);
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
                }else if(navType.equalsIgnoreCase("logout")){

                    SharedPreferences.Editor editor = getSharedPreferences("LoginCredentials", MODE_PRIVATE).edit();
                    editor.remove("phoneNumber");
                    editor.remove("password");
                    editor.commit();
                    SharedPreferences.Editor deditor = getSharedPreferences("DisclaimerCredentials", MODE_PRIVATE).edit();
                    deditor.putString("disclaimer", "");
                    deditor.commit();
                    Intent ide = new Intent(SelectPlaceMapActivity.this, LoginActivity.class);
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

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (ActivityCompat.checkSelfPermission(SelectPlaceMapActivity.this, permissions[0]) == PackageManager.PERMISSION_GRANTED) {
            switch (requestCode) {
                //Location
                case 1:
                    LocationManager manager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
                    boolean statusOfGPS = manager.isProviderEnabled(LocationManager.GPS_PROVIDER);
                    if (statusOfGPS) {
                        getGPSLocation();
                    } else {
                        //askSwitchOnGPS();
                        showInfoDlg("Information", "Please turn ON location services in your device.", "OPEN", "gps");
                    }
                    break;
            }
            LocationManager manager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
            boolean statusOfGPS = manager.isProviderEnabled(LocationManager.GPS_PROVIDER);
            Log.e("statusOfGPS", "" + statusOfGPS);
            if (statusOfGPS) {
                getGPSLocation();
            } else {
                //askSwitchOnGPS();
                showInfoDlg("Information", "Please turn ON location services in your device.", "OPEN", "gps");
            }
        } else {
            Toast.makeText(SelectPlaceMapActivity.this, "Permission denied", Toast.LENGTH_SHORT).show();
        }
    }

    LatLng crtLocation;

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        if (ActivityCompat.checkSelfPermission(SelectPlaceMapActivity.this, android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            mLastLocation = LocationServices.FusedLocationApi.getLastLocation(
                    mGoogleApiClient);
            if (mLastLocation != null) {
                Log.e("onConnected Latitude", "" + String.valueOf(mLastLocation.getLatitude()));
                Log.e("onConnected Longitude", "" + String.valueOf(mLastLocation.getLongitude()));
                crtLocation = new LatLng(mLastLocation.getLatitude(), (mLastLocation.getLongitude()));
                crtLatLan = crtLocation;
                if (mMap != null) {
                    String address = getCompleteAddressString(mLastLocation.getLatitude(), mLastLocation.getLongitude());
                    Log.e("address", "" + address);
                    if (latitude.equalsIgnoreCase("") && longitude.equalsIgnoreCase("")) {
                        Log.e("onConnected Latitude", "" + String.valueOf(crtLatLan.latitude));
                        Log.e("onConnected Longitude", "" + String.valueOf(crtLatLan.longitude));
                        mMap.moveCamera(CameraUpdateFactory.newLatLng(crtLatLan));
                        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(crtLatLan, 15));
                        mMap.animateCamera(CameraUpdateFactory.zoomIn());
                        mMap.animateCamera(CameraUpdateFactory.zoomTo(15), 2000, null);
                    }
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
                    status.startResolutionForResult(SelectPlaceMapActivity.this, REQUEST_CHECK_SETTINGS);
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
            LocationManager manager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
            boolean statusOfGPS = manager.isProviderEnabled(LocationManager.GPS_PROVIDER);
            if (statusOfGPS) {
                getGPSLocation();
            } else {
                //askSwitchOnGPS();
                showInfoDlg("Information", "Please turn ON location services in your device.", "OPEN", "gps");
            }
        } else if (requestCode == Utils.REQUEST_GET_PLACES_DETAILS) {
            if (data != null) {
                String latitude = data.getStringExtra("latitude");
                String longitude = data.getStringExtra("longitude");
                String address = data.getStringExtra("address");
                Log.e("RESULT_OK", "Lat : " + latitude + " Lng : " + longitude);
            }
        }
    }

    protected void startLocationUpdates() {
        locationRequest = new LocationRequest();
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        locationRequest.setInterval(UPDATE_INTERVAL);
        locationRequest.setFastestInterval(FASTEST_INTERVAL);
        if (ActivityCompat.checkSelfPermission(SelectPlaceMapActivity.this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(SelectPlaceMapActivity.this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            Toast.makeText(SelectPlaceMapActivity.this, "Enable Permissions", Toast.LENGTH_LONG).show();
        }
        if (mGoogleApiClient.isConnected()) {
            Log.e("mGoogleApiClient", "Connected");
            LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, locationRequest, this);
        } else {
            Log.e("mGoogleApiClient", "Not Connected");
            //connectGoogleApiClient();
        }
    }

    public void connectGoogleApiClient() {
        if (mGoogleApiClient != null) {
            mGoogleApiClient.connect();
        }

        if (ActivityCompat.checkSelfPermission(SelectPlaceMapActivity.this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(SelectPlaceMapActivity.this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            Toast.makeText(SelectPlaceMapActivity.this, "Enable Permissions", Toast.LENGTH_LONG).show();
        }
        if (mGoogleApiClient.isConnected()) {
            Log.e("mGoogleApiClient", "Connected");
            LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, locationRequest, this);
        } else {
            Log.e("mGoogleApiClient", "Not Connected");
            connectGoogleApiClient();
        }
    }

    @Override
    public void onLocationChanged(Location location) {
        if (location != null) {
            Log.e("LocationChanged", "Latitude : " + location.getLatitude() + " , Longitude : " + location.getLongitude());
            if (mLastLocation == null) {
                mLastLocation = location;
                if (mMap != null) {
                    crtLocation = new LatLng(mLastLocation.getLatitude(), (mLastLocation.getLongitude()));
                    crtLatLan = crtLocation;
                    String address = getCompleteAddressString(mLastLocation.getLatitude(), mLastLocation.getLongitude());
                    Log.e("address", "" + address);
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

    private String getCompleteAddressStringto(double LATITUDE, double LONGITUDE) {
        String strAdd = "";
        Geocoder geocoder = new Geocoder(SelectPlaceMapActivity.this, Locale.getDefault());
        try {
            List<Address> addresses = geocoder.getFromLocation(LATITUDE, LONGITUDE, 1);
            if (addresses != null) {
                Address returnedAddress = addresses.get(0);
                StringBuilder strReturnedAddress = new StringBuilder("");

                for (int i = 0; i < returnedAddress.getMaxAddressLineIndex(); i++) {
                    strReturnedAddress.append(returnedAddress.getAddressLine(i)).append("\n");
                }
                strAdd = strReturnedAddress.toString();
                Log.e("location address", "" + strReturnedAddress.toString());
               // Toast.makeText(this,"strAdd",Toast.LENGTH_SHORT).show();
            } else {
                Log.e("location address", "No Address returned!");
                //Toast.makeText(this,"No Address returned!",Toast.LENGTH_SHORT).show();
            }
        } catch (Exception e) {
            e.printStackTrace();
            Log.e("location address", "Cann't get Address!");
            //Toast.makeText(this,"Cann't get Address!",Toast.LENGTH_SHORT).show();
        }
        return strAdd;
    }


    private String getCompleteAddressString(double LATITUDE, double LONGITUDE) {
        String strAdd = "";
        Geocoder geocoder = new Geocoder(SelectPlaceMapActivity.this, Locale.getDefault());
        try {
            int count = 0;
            do {
                List<Address> addresses = geocoder.getFromLocation(LATITUDE, LONGITUDE, 1);
                if (addresses != null) {
                    Address returnedAddress = addresses.get(0);
                    StringBuilder strReturnedAddress = new StringBuilder("");
                    Log.e("Country CODE", "" + returnedAddress.getCountryCode());
                    Utils.countryCode = returnedAddress.getCountryCode();
                    Log.e("Country NAME", "" + returnedAddress.getCountryName());
                    Log.e("Country Locality", "" + returnedAddress.getLocality());
                    for (int i = 0; i < returnedAddress.getMaxAddressLineIndex(); i++) {
                        strReturnedAddress.append(returnedAddress.getAddressLine(i)).append(", ");
                    }
                    strAdd = strReturnedAddress.toString();
                    String formattedAdd = returnedAddress.getAddressLine(0)+ ", "+returnedAddress.getAddressLine(1)
                            + ", "+returnedAddress.getLocality()+", "+returnedAddress.getCountryName()+"-"+returnedAddress.getPostalCode();

                    strAdd = formattedAdd.replaceAll("null","");

                   // Toast.makeText(this,"Address-->"+returnedAddress.toString(),Toast.LENGTH_SHORT).show();
                    //Log.e("loction address", "" + strReturnedAddress.toString());
                    if (strAdd.equalsIgnoreCase("") || strAdd.length() <= 0) {
                    count = 6;
                    }else {
                        count = 11;
                    }
                } else {
                    Log.e("loction address", "No Address returned!");
                }
            }while (count < 5 );

        } catch (Exception e) {
            e.printStackTrace();
            Log.e("loction address", "Cannot get Address!");

        }
        return strAdd;

    }

//    private class  MyGeoPoint extends AsyncTask<String, Void, Address> {
//        @Override
//        protected Address doInBackground(String... latlng) {
//            try {
//                Geocoder geoCoder = new Geocoder(SelectPlaceMapActivity.this);
//                double latitude = latlng[0];
//                double longitude = latlng[1];
//                List<Address> addresses = geoCoder.getFromLocation(latitude, longitude, 1);
//                if (addresses.size() > 0)
//                    return addresses.get(0);
//            } catch (IOException ex) {
//                // log exception or do whatever you want to do with it!
//            }
//            return null;
//        }
//
//        @Override
//        protected void onPostExecute(Address address) {
//            // do whatever you want/need to do with the address found
//            // remember to check first that it's not null
//            if (address != null) {
//                //Address returnedAddress = addresses.get(0);
//                StringBuilder strReturnedAddress = new StringBuilder("");
//                Log.e("Country CODE", "" + address.getCountryCode());
//                Utils.countryCode = address.getCountryCode();
//                Log.e("Country NAME", "" + address.getCountryName());
//                Log.e("Country Locality", "" + address.getLocality());
//                for (int i = 0; i < address.getMaxAddressLineIndex(); i++) {
//                    strReturnedAddress.append(address.getAddressLine(i)).append(", ");
//                }
//                String strAdd = strReturnedAddress.toString();
//            }
//        }
//    }
}
