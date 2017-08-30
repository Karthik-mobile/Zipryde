package com.trivectadigital.ziprydeuserapp.service;

import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.FirebaseInstanceIdService;
import com.trivectadigital.ziprydeuserapp.apis.ZiprydeApiClient;
import com.trivectadigital.ziprydeuserapp.apis.ZiprydeApiInterface;
import com.trivectadigital.ziprydeuserapp.assist.Utils;
import com.trivectadigital.ziprydeuserapp.modelpost.SingleInstantParameters;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MyFirebaseInstanceIDService extends FirebaseInstanceIdService {
    private static final String TAG = MyFirebaseInstanceIDService.class.getSimpleName();

    @Override
    public void onTokenRefresh() {
        super.onTokenRefresh();
        String refreshedToken = FirebaseInstanceId.getInstance().getToken();

        // Saving reg id to shared preferences
        storeRegIdInPref(refreshedToken);

        // sending reg id to your server
        sendRegistrationToServer(refreshedToken);

        // Notify UI that registration has completed, so the progress indicator can be hidden.
        Intent registrationComplete = new Intent(Utils.REGISTRATION_COMPLETE);
        registrationComplete.putExtra("token", refreshedToken);
        LocalBroadcastManager.getInstance(this).sendBroadcast(registrationComplete);
    }

    private void sendRegistrationToServer(final String token) {
        // sending gcm token to server
        Log.e(TAG, "sendRegistrationToServer: " + token);
    }

    private void storeRegIdInPref(String token) {
        SharedPreferences pref = getApplicationContext().getSharedPreferences(Utils.SHARED_PREF, 0);
        SharedPreferences.Editor editor = pref.edit();
        editor.putString("regId", token);
        editor.commit();
        //Update the token to the remote server
        updateDeviceToken(token);
        //ObservableObject.getInstance().onActivityChange();

    }

    public void updateDeviceToken(final String regId){

        SingleInstantParameters loginCredentials = new SingleInstantParameters();
        loginCredentials.deviceToken = ""+regId;

        ZiprydeApiInterface apiService = ZiprydeApiClient.getClient().create(ZiprydeApiInterface.class);
        Call<Void> call = apiService.updateDeviceToken(Utils.verifyLogInUserMobileInstantResponse.getAccessToken(),loginCredentials);
        call.enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {

                //SingleInstantResponse bookings = response.body();

                Log.e("onResponse-->", response.toString());

            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                // Log error here since request failed
                Log.e("onFailure==>", t.toString());
            }
        });
    }
}

