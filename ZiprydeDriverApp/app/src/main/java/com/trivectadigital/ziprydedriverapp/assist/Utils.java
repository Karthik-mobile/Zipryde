package com.trivectadigital.ziprydedriverapp.assist;

import android.location.Location;

import com.trivectadigital.ziprydedriverapp.modelget.ListOfBooking;
import com.trivectadigital.ziprydedriverapp.modelget.ListOfPercentage;
import com.trivectadigital.ziprydedriverapp.modelget.ListOfRequestedBooking;
import com.trivectadigital.ziprydedriverapp.modelget.SingleInstantResponse;
import com.google.android.gms.maps.model.LatLng;

import java.util.LinkedList;
import java.util.regex.Pattern;

/**
 * Created by Hari on 08-06-2017.
 */

public class Utils {

    public static boolean updateLocationBoolean = false;
    public static boolean fromNotification = false;
    public static Location firstLocation;
    public static GPSLocationService gpsLocationService;
    public static String defaultIP = "54.213.246.198:8080";
    public static final Pattern IP_ADDRESS
            = Pattern.compile(
            "((25[0-5]|2[0-4][0-9]|[0-1][0-9]{2}|[1-9][0-9]|[1-9])\\.(25[0-5]|2[0-4]"
                    + "[0-9]|[0-1][0-9]{2}|[1-9][0-9]|[1-9]|0)\\.(25[0-5]|2[0-4][0-9]|[0-1]"
                    + "[0-9]{2}|[1-9][0-9]|[1-9]|0)\\.(25[0-5]|2[0-4][0-9]|[0-1][0-9]{2}"
                    + "|[1-9][0-9]|[0-9]))");

    public static final int REQUEST_GET_PLACES_DETAILS = 101;

    public static LinkedList<CommissionDetails> commissionDetailsList = new LinkedList<CommissionDetails>();

    public static LatLng location;

    public static boolean fromSplash = true;

    public static SingleInstantResponse getOTPByMobileInstantResponse;
    public static SingleInstantResponse verifyOTPByMobileInstantResponse;
    public static SingleInstantResponse saveUserMobileInstantResponse;
    public static SingleInstantResponse updateBookingDriverStatusInstantResponse;
    public static SingleInstantResponse verifyLogInUserMobileInstantResponse;
    public static SingleInstantResponse getRevenueByDateInstantResponse;
    public static SingleInstantResponse getBookingCountByDateInstantResponse;
    public static LinkedList<ListOfBooking> getBookingByDriverIdInstantResponse;
    public static LinkedList<ListOfPercentage> getAllNYOPListInstantResponse;
    public static LinkedList<ListOfRequestedBooking> getBookingRequestedByDriverIdResponse;

    public static LatLng startingLatLan;
    public static String startingPlaceAddress = "";
    public static LatLng endingLatLan;
    public static String endingPlaceAddress = "";
    public static LatLng backchkendingLatLan;
    public static String backchkendingPlaceAddress = "";

    public static String parsedDistance = "";
    public static String parsedDuration = "";

    // global topic to receive app wide push notifications
    public static final String TOPIC_GLOBAL = "global";

    // broadcast receiver intent filters
    public static final String REGISTRATION_COMPLETE = "registrationComplete";
    public static final String PUSH_NOTIFICATION = "pushNotification";

    // id to handle the notification in the notification tray
    public static final int NOTIFICATION_ID = 100;
    public static final int NOTIFICATION_ID_BIG_IMAGE = 101;

    public static final String SHARED_PREF = "ah_firebase";
    public static final String SHARED_NOTIFI = "notification";
}
