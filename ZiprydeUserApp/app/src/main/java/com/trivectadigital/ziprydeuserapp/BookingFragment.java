package com.trivectadigital.ziprydeuserapp;

import android.Manifest;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.IntentSender;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
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
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.AppCompatTextView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MarkerOptions;
import com.trivectadigital.ziprydeuserapp.apis.ZiprydeApiClient;
import com.trivectadigital.ziprydeuserapp.apis.ZiprydeApiInterface;
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


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link BookingFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link BookingFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class BookingFragment extends Fragment implements OnMapReadyCallback,
                                                            GoogleApiClient.ConnectionCallbacks,
                                                            GoogleApiClient.OnConnectionFailedListener,
                                                            ResultCallback<LocationSettingsResult>,
                                                            LocationListener  {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    //private OnFragmentInteractionListener mListener;

    public BookingFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment BookingFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static BookingFragment newInstance(String param1, String param2) {
        BookingFragment fragment = new BookingFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    private GoogleMap mMap;

    static final Integer LOCATION = 0x1;

    protected GoogleApiClient mGoogleApiClient;
    protected LocationRequest locationRequest;
    int REQUEST_CHECK_SETTINGS = 100;

    private long UPDATE_INTERVAL = 15000;  /* 15 secs */
    private long FASTEST_INTERVAL = 5000; /* 5 secs */

    Location mLastLocation;

    private AppCompatTextView searchPlace, searchPlaceDestination;

    public LinearLayout infoWindowLay, gotoMyLocation;

    TextView microText, microInfoText, miniText, miniInfoText, sedanText, sedanInfoText;
    RelativeLayout microLay, miniLay, sedanLay;
    ImageView micro_circle, micro_car, mini_circle, mini_car, suv_circle, suv_car, centerMarker;

    LatLng crtLatLan;

    boolean fromSearchPlace = false;

    ZiprydeApiInterface apiService;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_booking, container, false);

        apiService = ZiprydeApiClient.getClient().create(ZiprydeApiInterface.class);

        searchPlace = (AppCompatTextView) view.findViewById(R.id.searchPlace);
        searchPlaceDestination = (AppCompatTextView) view.findViewById(R.id.searchPlaceDestination);

        microLay = (RelativeLayout) view.findViewById(R.id.microLay);
        miniLay = (RelativeLayout) view.findViewById(R.id.miniLay);
        sedanLay = (RelativeLayout) view.findViewById(R.id.sedanLay);

        centerMarker = (ImageView) view.findViewById(R.id.centerMarker);

        micro_circle = (ImageView) view.findViewById(R.id.micro_circle);
        micro_car = (ImageView) view.findViewById(R.id.micro_car);
        mini_circle = (ImageView) view.findViewById(R.id.mini_circle);
        mini_car = (ImageView) view.findViewById(R.id.mini_car);
        suv_circle = (ImageView) view.findViewById(R.id.suv_circle);
        suv_car = (ImageView) view.findViewById(R.id.suv_car);

        microText = (TextView) view.findViewById(R.id.microText);
        microInfoText = (TextView) view.findViewById(R.id.microInfoText);
        miniText = (TextView) view.findViewById(R.id.miniText);
        miniInfoText = (TextView) view.findViewById(R.id.miniInfoText);
        sedanText = (TextView) view.findViewById(R.id.sedanText);
        sedanInfoText = (TextView) view.findViewById(R.id.sedanInfoText);

        mGoogleApiClient = new GoogleApiClient.Builder(getActivity())
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();

        if (ContextCompat.checkSelfPermission(getActivity(), android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(getActivity(), android.Manifest.permission.ACCESS_FINE_LOCATION)) {
                //This is called if user has denied the permission before
                //In this case I am just asking the permission again
                ActivityCompat.requestPermissions(getActivity(), new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION}, LOCATION);

            } else {
                ActivityCompat.requestPermissions(getActivity(), new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION}, LOCATION);
            }
        } else {
            LocationManager manager = (LocationManager) getActivity().getSystemService(Context.LOCATION_SERVICE );
            boolean statusOfGPS = manager.isProviderEnabled(LocationManager.GPS_PROVIDER);
            if(statusOfGPS){
                getGPSLocation();
            }else{
                //askSwitchOnGPS();
                showInfoDlg("Information", "Please switch ON GPS to get you current location..", "OPEN", "gps");
            }
        }
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) this.getChildFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        searchPlace.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), PlacesSearchActivity.class);
                intent.putExtra("previous","starting");
                startActivityForResult(intent, Utils.REQUEST_GET_PLACES_DETAILS);
            }
        });

        searchPlaceDestination.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), PlacesSearchActivity.class);
                intent.putExtra("next","destination");
                startActivityForResult(intent, Utils.REQUEST_GET_PLACES_DETAILS);
            }
        });

        TextView titleUi = ((TextView) view.findViewById(R.id.title));
        infoWindowLay = ((LinearLayout) view.findViewById(R.id.infoWindowLay));
        gotoMyLocation = ((LinearLayout) view.findViewById(R.id.gotoMyLocation));
        titleUi.setText("Set Pickup Location");

        microLay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                changeTextColorToBlack(miniText, sedanText, miniInfoText, sedanInfoText);
                changeTextColorToOrange(microText, microInfoText);
                changeCircleBigtoSmall(mini_circle, suv_circle, mini_car, suv_car);
                changeCircleSmalltoBig(micro_circle, micro_car);
            }
        });

        miniLay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                changeTextColorToBlack(microText, sedanText, microInfoText, sedanInfoText);
                changeTextColorToOrange(miniText, miniInfoText);
                changeCircleBigtoSmall(micro_circle, suv_circle, micro_car, suv_car);
                changeCircleSmalltoBig(mini_circle, mini_car);
            }
        });

        sedanLay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                changeTextColorToBlack(microText, miniText, microInfoText, miniInfoText);
                changeTextColorToOrange(sedanText, sedanInfoText);
                changeCircleBigtoSmall(micro_circle, mini_circle, micro_car, mini_car);
                changeCircleSmalltoBig(suv_circle, suv_car);
            }
        });

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

        return view;
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

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
//        if (mListener != null) {
//            mListener.onFragmentInteraction(uri);
//        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
//        if (context instanceof OnFragmentInteractionListener) {
//            mListener = (OnFragmentInteractionListener) context;
//        } else {
//            throw new RuntimeException(context.toString()
//                    + " must implement OnFragmentInteractionListener");
//        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
//        mListener = null;
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

//        mMap.setInfoWindowAdapter(new GoogleMap.InfoWindowAdapter() {
//            @Override
//            public View getInfoWindow(Marker marker) {
//                LayoutInflater li = (LayoutInflater) getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
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

//        mMap.setOnInfoWindowClickListener(new GoogleMap.OnInfoWindowClickListener() {
//            @Override
//            public void onInfoWindowClick(Marker marker) {
//                Utils.location = crtLocation;
//                Log.e("Latitude",""+crtLocation.latitude);
//                Log.e("Longitude",""+crtLocation.longitude);
//                Intent ide = new Intent(getActivity(), BookingConfirmationActivity.class);
//                ide.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
//                ide.putExtra("Latitude",""+crtLocation.latitude);
//                ide.putExtra("Longitude",""+crtLocation.longitude);
//                startActivity(ide);
//            }
//        });

        infoWindowLay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(Utils.backchkendingLatLan != null || !Utils.backchkendingPlaceAddress.equalsIgnoreCase("")){
                    Utils.location = crtLocation;
                    Log.e("Latitude",""+crtLocation.latitude);
                    Log.e("Longitude",""+crtLocation.longitude);
                    Intent ide = new Intent(getActivity(), BookingConfirmationActivity.class);
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
                infoWindowLay.setVisibility(View.GONE);
                searchPlace.setText("Getting Address");
                centerMarker.setImageResource(R.drawable.ic_action_location);
            }
        });

        mMap.setOnCameraIdleListener(new GoogleMap.OnCameraIdleListener() {
            @Override
            public void onCameraIdle() {
                Log.e("onCameraIdle","onCameraIdle");
                infoWindowLay.setVisibility(View.VISIBLE);
                LatLng location = mMap.getCameraPosition().target;
                Utils.startingLatLan = location;
                Log.e("latitude",""+location.latitude);
                Log.e("longitude",""+location.longitude);
                String address = getCompleteAddressString(location.latitude, location.longitude);
                Log.e("address",""+address);
                if(!fromSearchPlace) {
                    searchPlace.setText("" + address);
                    Utils.startingPlaceAddress = address;
                }else{
                    fromSearchPlace = false;
                }
                centerMarker.setImageResource(R.drawable.ic_action_location_new);
            }
        });
    }

    public void getGPSLocation() {
        if(ActivityCompat.checkSelfPermission(getActivity(), android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED){
            mLastLocation = LocationServices.FusedLocationApi.getLastLocation(
                    mGoogleApiClient);
            if (mLastLocation != null) {
                Log.e("GPSLocation Latitude",""+String.valueOf(mLastLocation.getLatitude()));
                Log.e("GPSLocation Longitude",""+String.valueOf(mLastLocation.getLongitude()));

                if(mMap != null) {
                    crtLocation = new LatLng(mLastLocation.getLatitude(), (mLastLocation.getLongitude()));
                    String address = getCompleteAddressString(mLastLocation.getLatitude(), mLastLocation.getLongitude());
                    Log.e("address",""+address);
                    searchPlace.setText(""+address);
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
        dialog = new Dialog(getActivity(), android.R.style.Theme_Dialog);
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
        if(ActivityCompat.checkSelfPermission(getActivity(), permissions[0]) == PackageManager.PERMISSION_GRANTED){
            switch (requestCode) {
                //Location
                case 1:
                    LocationManager manager = (LocationManager) getActivity().getSystemService(Context.LOCATION_SERVICE );
                    boolean statusOfGPS = manager.isProviderEnabled(LocationManager.GPS_PROVIDER);
                    if(statusOfGPS){
                        getGPSLocation();
                    }else{
                        //askSwitchOnGPS();
                        showInfoDlg("Information", "Please switch ON GPS to get you current location..", "OPEN", "gps");
                    }
                    break;
            }
            LocationManager manager = (LocationManager) getActivity().getSystemService(Context.LOCATION_SERVICE );
            boolean statusOfGPS = manager.isProviderEnabled(LocationManager.GPS_PROVIDER);
            Log.e("statusOfGPS",""+statusOfGPS);
            if(statusOfGPS){
                getGPSLocation();
            }else{
                //askSwitchOnGPS();
                showInfoDlg("Information", "Please switch ON GPS to get you current location..", "OPEN", "gps");
            }
        }else{
            Toast.makeText(getActivity(), "Permission denied", Toast.LENGTH_SHORT).show();
        }
    }

    LatLng crtLocation;

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        if(ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED){
            mLastLocation = LocationServices.FusedLocationApi.getLastLocation(
                    mGoogleApiClient);
            if (mLastLocation != null) {
                Log.e("Connected Latitude",""+String.valueOf(mLastLocation.getLatitude()));
                Log.e("Connected Longitude",""+String.valueOf(mLastLocation.getLongitude()));
                getNearByActiveDrivers(String.valueOf(mLastLocation.getLatitude()), String.valueOf(mLastLocation.getLongitude()));
                if(mMap != null) {
                    crtLocation = new LatLng(mLastLocation.getLatitude(), (mLastLocation.getLongitude()));
                    crtLatLan = crtLocation;
                    String address = getCompleteAddressString(mLastLocation.getLatitude(), mLastLocation.getLongitude());
                    Log.e("address",""+address);
                    searchPlace.setText(""+address);
                    //Marker marker = mMap.addMarker(new MarkerOptions().position(crtLocation).title("Current Location").icon(BitmapDescriptorFactory.fromResource(R.drawable.location_48)));
                    mMap.moveCamera(CameraUpdateFactory.newLatLng(crtLocation));
                    mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(crtLocation,15));
                    mMap.animateCamera(CameraUpdateFactory.zoomIn());
                    mMap.animateCamera(CameraUpdateFactory.zoomTo(15), 2000, null);
                    //marker.showInfoWindow();
                }
            }
        }
    }

    public void getNearByActiveDrivers(String latitude, String longitude){
        Log.e("fromLatitude",""+latitude);
        Log.e("fromLongitude",""+longitude);
        SingleInstantParameters loginCredentials = new SingleInstantParameters();
        loginCredentials.fromLatitude = latitude;
        loginCredentials.fromLongitude = longitude;

        final Dialog dialog = new Dialog(getActivity(), android.R.style.Theme_Dialog);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.loadingimage_layout);
        dialog.setCanceledOnTouchOutside(false);
        dialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        dialog.getWindow().setLayout(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        dialog.show();

        Call<LinkedList<ListOfCurrentCabs>> call = apiService.getNearByActiveDrivers(loginCredentials);
        call.enqueue(new Callback<LinkedList<ListOfCurrentCabs>>() {
            @Override
            public void onResponse(Call<LinkedList<ListOfCurrentCabs>> call, Response<LinkedList<ListOfCurrentCabs>> response) {
                int statusCode = response.code();
                Log.e("statusCode",""+statusCode);
                Log.e("response.body",""+response.body());
                Log.e("response.errorBody",""+response.errorBody());
                Log.e("response.isSuccessful",""+response.isSuccessful());
                dialog.dismiss();
                if(response.isSuccessful()){
                    Utils.getNearByActiveDriversInstantResponse = response.body();
                    Log.e("size",""+Utils.getNearByActiveDriversInstantResponse.size());
//                    showInfoDlg("Success..", "Successfully logged in.", "Ok", "success");
                    final List<Marker> markers = new LinkedList<Marker>();
                    for(int i = 0; i < Utils.getNearByActiveDriversInstantResponse.size(); i++){
                        String latitude = Utils.getNearByActiveDriversInstantResponse.get(i).getLatitude();
                        String longitude = Utils.getNearByActiveDriversInstantResponse.get(i).getLongitude();
                        Log.e("latitude longitude","latitude : "+latitude+" longitude : "+longitude);
                        LatLng tempLatLong = new LatLng(Double.parseDouble(latitude), Double.parseDouble(longitude));
                        Marker marker = mMap.addMarker(new MarkerOptions().position(tempLatLong).icon(BitmapDescriptorFactory.fromResource(R.drawable.micro_car_48)));
//                        mMap.moveCamera(CameraUpdateFactory.newLatLng(tempLatLong));
//                        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(tempLatLong,15));
//                        mMap.animateCamera(CameraUpdateFactory.zoomIn());
//                        mMap.animateCamera(CameraUpdateFactory.zoomTo(15), 2000, null);
                        markers.add(marker);
                    }

//                    mMap.setOnMapLoadedCallback(new GoogleMap.OnMapLoadedCallback() {
//                        @Override
//                        public void onMapLoaded() {
//                            LatLngBounds.Builder builder = new LatLngBounds.Builder();
//                            for (Marker marker : markers) {
//                                builder.include(marker.getPosition());
//                            }
//                            LatLngBounds bounds = builder.build();
//                            int padding = 50; // offset from edges of the map in pixels
//                            CameraUpdate cu = CameraUpdateFactory.newLatLngBounds(bounds, padding);
//                            mMap.moveCamera(cu);
//                            mMap.animateCamera(cu);
//                        }
//                    });
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
            public void onFailure(Call<LinkedList<ListOfCurrentCabs>> call, Throwable t) {
                // Log error here since request failed
                Log.e("onFailure", t.toString());
                dialog.dismiss();
                showInfoDlg("Error..", "Either there is no network connectivity or server is not available.. Please try again later..", "Ok", "server");
            }
        });
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
                    status.startResolutionForResult(getActivity(), REQUEST_CHECK_SETTINGS);
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
            LocationManager manager = (LocationManager) getActivity().getSystemService(Context.LOCATION_SERVICE );
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
                Utils.startingPlaceAddress = address;
                searchPlace.setText(address);
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
        if (ActivityCompat.checkSelfPermission(getActivity(), android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getActivity(), android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            Toast.makeText(getActivity(), "Enable Permissions", Toast.LENGTH_LONG).show();
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

        if (ActivityCompat.checkSelfPermission(getActivity(), android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getActivity(), android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            Toast.makeText(getActivity(), "Enable Permissions", Toast.LENGTH_LONG).show();
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
                    getNearByActiveDrivers(""+mLastLocation.getLatitude(), ""+mLastLocation.getLongitude());
                    crtLatLan = crtLocation;
                    String address = getCompleteAddressString(mLastLocation.getLatitude(), mLastLocation.getLongitude());
                    Log.e("address",""+address);
                    searchPlace.setText(""+address);
                    //Marker marker = mMap.addMarker(new MarkerOptions().position(crtLocation).title("Current Location").icon(BitmapDescriptorFactory.fromResource(R.drawable.location_48)));
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
        Geocoder geocoder = new Geocoder(getActivity(), Locale.getDefault());
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
