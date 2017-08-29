package com.trivectadigital.ziprydedriverapp.service;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.util.Log;

import com.trivectadigital.ziprydedriverapp.assist.ObservableObject;

import br.com.safety.locationlistenerhelper.core.SettingsLocationTracker;

public class LocationTrackerReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        if (null != intent && intent.getAction().equals("my.action")) {
            Location locationData = (Location) intent.getParcelableExtra(SettingsLocationTracker.LOCATION_MESSAGE);
            Log.d("Location: ", "Latitude: " + locationData.getLatitude() + "Longitude:" + locationData.getLongitude());
            //send your call to api or do any things with the of location data


                ObservableObject.getInstance().updateValue(locationData);

        }
    }



}
