package com.trivectadigital.ziprydeuserapp;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.Html;
import android.text.Spanned;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AutoCompleteTextView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.location.places.AutocompleteFilter;
import com.google.android.gms.location.places.AutocompletePrediction;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.PlaceBuffer;
import com.google.android.gms.location.places.Places;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.gson.Gson;
import com.trivectadigital.ziprydeuserapp.assist.PlaceAutocompleteAdapter;
import com.trivectadigital.ziprydeuserapp.assist.Utils;
import com.trivectadigital.ziprydeuserapp.modelget.SingleInstantResponse;

public class FromToPlaceActivity extends AppCompatActivity implements GoogleApiClient.OnConnectionFailedListener {

    protected GoogleApiClient mGoogleApiClient;

    AutoCompleteTextView autocomplete_places, autocomplete_placesdest;
    ImageView clearsearchImageView, clearsearchImageViewdest;

    String latitude, longitude, address;

    private PlaceAutocompleteAdapter mAdapter;

    private static final LatLngBounds BOUNDS_GREATER_SYDNEY = new LatLngBounds(
            new LatLng(-34.041458, 150.790100), new LatLng(-33.682247, 151.383362));

    public static final String TAG = "PlacesSearchActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fromto_place);

        // Construct a GoogleApiClient for the {@link Places#GEO_DATA_API} using AutoManage
        // functionality, which automatically sets up the API client to handle Activity lifecycle
        // events. If your activity does not extend FragmentActivity, make sure to call connect()
        // and disconnect() explicitly.
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this, 0 /* clientId */, this)
                .addApi(Places.GEO_DATA_API)
                .build();

        autocomplete_places = (AutoCompleteTextView) findViewById(R.id.autocomplete_places);
        autocomplete_placesdest = (AutoCompleteTextView) findViewById(R.id.autocomplete_placesdest);

        AutocompleteFilter filter = new AutocompleteFilter.Builder().setCountry(Utils.countryCode).build();

        clearsearchImageView = (ImageView) findViewById(R.id.clearsearchImageView);
        clearsearchImageViewdest = (ImageView) findViewById(R.id.clearsearchImageViewdest);

        ImageView backImg = (ImageView) findViewById(R.id.backImg);
        backImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        //latitude longitude address

        Intent intent = getIntent();
        address = intent.getStringExtra("address");
        latitude = intent.getStringExtra("latitude");
        longitude = intent.getStringExtra("longitude");
        autocomplete_places.setText(address);
        Utils.startingPlaceAddress = address;
        Utils.startingLatLan = new LatLng(Double.parseDouble(latitude), Double.parseDouble(longitude));
        //clearsearchImageView.setVisibility(View.VISIBLE);

        clearsearchImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                autocomplete_places.setText("");
            }
        });

        clearsearchImageViewdest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                autocomplete_placesdest.setText("");
            }
        });

        autocomplete_placesdest.setOnItemClickListener(mAutocompleteClickListener);

        // Set up the adapter that will retrieve suggestions from the Places Geo Data API that cover
        // the entire world.
        mAdapter = new PlaceAutocompleteAdapter(this, mGoogleApiClient, BOUNDS_GREATER_SYDNEY, filter);

        autocomplete_placesdest.setAdapter(mAdapter);

        autocomplete_placesdest.addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.length() <= 0) {
                    clearsearchImageViewdest.setVisibility(View.GONE);
                } else {
                    clearsearchImageViewdest.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        autocomplete_places.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(FromToPlaceActivity.this, SelectPlaceMapActivity.class);
                intent.putExtra("address", "" + address);
                intent.putExtra("latitude", "" + latitude);
                intent.putExtra("longitude", "" + longitude);
                intent.putExtra("fromPlace", "fromPlace");
                startActivity(intent);
                finish();
            }
        });

//        autocomplete_placesdest.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Utils.startingPlaceAddress = address;
//                Utils.startingLatLan = new LatLng(Double.parseDouble(latitude), Double.parseDouble(longitude));
//                Intent intent = new Intent(FromToPlaceActivity.this, SelectPlaceMapActivity.class);
//                intent.putExtra("address","");
//                intent.putExtra("latitude","");
//                intent.putExtra("longitude","");
//                intent.putExtra("toPlace","toPlace");
//                startActivity(intent);
//                finish();
//            }
//        });

//        autocomplete_places.addTextChangedListener(new TextWatcher() {
//            @Override
//            public void onTextChanged(CharSequence s, int start, int before, int count) {
//                if (s.length() <= 0) {
//                    clearsearchImageView.setVisibility(View.GONE);
//                } else {
//                    clearsearchImageView.setVisibility(View.VISIBLE);
//                }
//            }
//
//            @Override
//            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
//
//            }
//
//            @Override
//            public void afterTextChanged(Editable s) {
//
//            }
//        });
//
//        autocomplete_placesdest.addTextChangedListener(new TextWatcher() {
//            @Override
//            public void onTextChanged(CharSequence s, int start, int before, int count) {
//                if (s.length() <= 0) {
//                    clearsearchImageViewdest.setVisibility(View.GONE);
//                } else {
//                    clearsearchImageViewdest.setVisibility(View.VISIBLE);
//                }
//            }
//
//            @Override
//            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
//
//            }
//
//            @Override
//            public void afterTextChanged(Editable s) {
//
//            }
//        });

    }

    @Override
    protected void onResume() {
        super.onResume();
        SharedPreferences prefs = getSharedPreferences("LoginCredentials", MODE_PRIVATE);
        Gson gson = new Gson();
        String json = prefs.getString("LoginCredentials", "");
        Utils.verifyLogInUserMobileInstantResponse = gson.fromJson(json, SingleInstantResponse.class);
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
            Log.e(TAG, "" + formatPlaceDetails(getResources(), place.getName(),
                    place.getId(), place.getAddress(), place.getPhoneNumber(),
                    place.getWebsiteUri()));

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
            final CharSequence thirdPartyAttribution = places.getAttributions();
            if (thirdPartyAttribution != null) {
                Log.e(TAG, "" + Html.fromHtml(thirdPartyAttribution.toString()));
            }

            Log.i(TAG, "Place details received: " + place.getName());

            places.release();

            Utils.endingPlaceAddress = address;
            Utils.endingLatLan = new LatLng(geoLatLng.latitude, geoLatLng.longitude);
            autocomplete_placesdest.setText(address);
            autocomplete_placesdest.setSelection(autocomplete_placesdest.getText().length());
            clearsearchImageViewdest.setVisibility(View.VISIBLE);
            Intent intent = new Intent(FromToPlaceActivity.this, DirectionConfirmationActivity.class);
            startActivity(intent);
            finish();
        }
    };

    private static Spanned formatPlaceDetails(Resources res, CharSequence name, String id,
                                              CharSequence address, CharSequence phoneNumber, Uri websiteUri) {
        Log.e(TAG, res.getString(R.string.place_details, name, id, address, phoneNumber,
                websiteUri));
        return Html.fromHtml(res.getString(R.string.place_details, name, id, address, phoneNumber,
                websiteUri));

    }

    /**
     * Called when the Activity could not connect to Google Play services and the auto manager
     * could resolve the error automatically.
     * In this case the API is not available and notify the user.
     *
     * @param connectionResult can be inspected to determine the cause of the failure
     */
    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {

        Log.e(TAG, "onConnectionFailed: ConnectionResult.getErrorCode() = "
                + connectionResult.getErrorCode());

        // TODO(Developer): Check error code and notify the user of error state and resolution.
        Toast.makeText(this,
                "Could not connect to Google API Client: Error " + connectionResult.getErrorCode(),
                Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onBackPressed() {
        finish();
    }
}
