package com.trivectadigital.ziprydeuserapp.assist;

import com.trivectadigital.ziprydeuserapp.modelget.ListOfBooking;
import com.trivectadigital.ziprydeuserapp.modelget.ListOfCarTypes;
import com.trivectadigital.ziprydeuserapp.modelget.ListOfCurrentCabs;
import com.trivectadigital.ziprydeuserapp.modelget.ListOfFairEstimate;
import com.trivectadigital.ziprydeuserapp.modelget.SingleInstantResponse;
import com.google.android.gms.maps.model.LatLng;

import java.util.LinkedList;
import java.util.regex.Pattern;

/**
 * Created by Hari on 08-06-2017.
 */

public class Utils {

    public static String defaultIP = "54.213.246.198:8080";
    public static final Pattern IP_ADDRESS
            = Pattern.compile(
            "((25[0-5]|2[0-4][0-9]|[0-1][0-9]{2}|[1-9][0-9]|[1-9])\\.(25[0-5]|2[0-4]"
                    + "[0-9]|[0-1][0-9]{2}|[1-9][0-9]|[1-9]|0)\\.(25[0-5]|2[0-4][0-9]|[0-1]"
                    + "[0-9]{2}|[1-9][0-9]|[1-9]|0)\\.(25[0-5]|2[0-4][0-9]|[0-1][0-9]{2}"
                    + "|[1-9][0-9]|[0-9]))");

    public static final int REQUEST_GET_PLACES_DETAILS = 101;

    public static LatLng location;

    public static boolean fromSplash = true;

    public static SingleInstantResponse getOTPByMobileInstantResponse;
    public static SingleInstantResponse verifyOTPByMobileInstantResponse;
    public static SingleInstantResponse saveUserMobileInstantResponse;
    public static SingleInstantResponse verifyLogInUserMobileInstantResponse;
    public static SingleInstantResponse requestBookingResponse;
    public static SingleInstantResponse getGeoLocationByDriverIdResponse;
    public static LinkedList<ListOfBooking> getBookingByUserIdResponse;
    public static LinkedList<ListOfFairEstimate> getAllNYOPByCabTypeAndDistanceInstantResponse;
    public static LinkedList<ListOfCarTypes> getAllCabTypesInstantResponse;
    public static LinkedList<ListOfCurrentCabs> getNearByActiveDriversInstantResponse;

    public static LatLng startingLatLan;
    public static String startingPlaceAddress = "";
    public static LatLng endingLatLan;
    public static String endingPlaceAddress = "";
    public static LatLng backchkendingLatLan;
    public static String backchkendingPlaceAddress = "";

    public static String parsedDistance = "";
    public static String parsedDuration = "";
}
