package com.trivectadigital.ziprydedriverapp.assist;

import android.content.Context;
import android.location.Location;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;

import com.trivectadigital.ziprydedriverapp.modelget.ListOfBooking;
import com.trivectadigital.ziprydedriverapp.modelget.ListOfPercentage;
import com.trivectadigital.ziprydedriverapp.modelget.ListOfRequestedBooking;
import com.trivectadigital.ziprydedriverapp.modelget.SingleInstantResponse;
import com.google.android.gms.maps.model.LatLng;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.LinkedList;
import java.util.TimeZone;
import java.util.regex.Pattern;

/**
 * Created by Hari on 08-06-2017.
 */

public class Utils {

    public static boolean updateLocationBoolean = false;
    public static boolean fromNotification = false;
    public static Location firstLocation;
    public static GPSLocationService gpsLocationService;
    public static String defaultIP = "mobileservice.zipryde.com:8080";

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
    public static SingleInstantResponse requestBookingResponse;
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

    public static final int NETOWRKERR_OVERRIDE_LOGIN = 409;
    public static final int NETWORKERR_SESSIONTOKEN_EXPIRED= 405;

    public static final String SHARED_PREF = "ah_firebase";
    public static final String SHARED_NOTIFI = "notification";

    public static  boolean isHistory = true;

    // Check Network Availability
    public static boolean connectivity(Context c) {
        ConnectivityManager connec = (ConnectivityManager) c
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        try {
            NetworkInfo wifi = connec
                    .getNetworkInfo(ConnectivityManager.TYPE_WIFI);

            NetworkInfo mobile = connec
                    .getNetworkInfo(ConnectivityManager.TYPE_MOBILE);

            if (wifi.isConnected() || mobile.isConnected())
                return true;
            else if (wifi.isConnected() && mobile.isConnected())
                return true;
            else
                return false;

        } catch (NullPointerException e) {
            e.printStackTrace();
            Log.d("ConStatus", "No Active Connection");
            return false;
        }
    }

    public static String UTCtoSysTime(String utcTime){


        Calendar calendar = Calendar.getInstance();
        TimeZone zone = calendar.getTimeZone();
        SimpleDateFormat sdf = new SimpleDateFormat("MM-dd-yyyy HH:mm:ss");
        sdf.setTimeZone(TimeZone.getTimeZone("UTC"));
        Date sysDate = new Date();
        try {
            sysDate = sdf.parse(utcTime);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        SimpleDateFormat sdf1 = new SimpleDateFormat("EEE, d MMM yyyy hh:mm a");
        sdf1.setTimeZone(zone);
        String systemTime = sdf1.format(sysDate);


        return systemTime;
    }
}
