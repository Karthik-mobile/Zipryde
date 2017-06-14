package com.altrockstech.ziprydeuserapp.assist;

import com.altrockstech.ziprydeuserapp.modelget.SingleInstantResponse;
import com.google.android.gms.maps.model.LatLng;

/**
 * Created by Hari on 08-06-2017.
 */

public class Utils {

    public static final int REQUEST_GET_PLACES_DETAILS = 101;

    public static LatLng location;

    public static boolean fromSplash = true;

    public static SingleInstantResponse getOTPByMobileInstantResponse;
    public static SingleInstantResponse verifyOTPByMobileInstantResponse;
    public static SingleInstantResponse saveUserMobileInstantResponse;

}
