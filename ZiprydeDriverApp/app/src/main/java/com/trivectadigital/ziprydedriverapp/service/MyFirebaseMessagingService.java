package com.trivectadigital.ziprydedriverapp.service;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.trivectadigital.ziprydedriverapp.R;
import com.trivectadigital.ziprydedriverapp.RideActivity;
import com.trivectadigital.ziprydedriverapp.assist.MessageReceivedEvent;
import com.trivectadigital.ziprydedriverapp.assist.NotificationUtils;
import com.trivectadigital.ziprydedriverapp.assist.Utils;

import org.json.JSONArray;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Calendar;

import de.greenrobot.event.EventBus;

public class MyFirebaseMessagingService extends FirebaseMessagingService {

    private static final String TAG = MyFirebaseMessagingService.class.getSimpleName();

    private NotificationUtils notificationUtils;

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        Log.e(TAG, "From: " + remoteMessage.getFrom());

        if (remoteMessage == null)
            return;

        // Check if message contains a notification payload.
        if (remoteMessage.getNotification() != null) {
            Log.e(TAG, "MessageId: " + remoteMessage.getMessageId());
            Log.e(TAG, "MessageType: " + remoteMessage.getMessageType());
            Log.e(TAG, "Notification Title: " + remoteMessage.getNotification().getTitle());
            Log.e(TAG, "Notification Body: " + remoteMessage.getNotification().getBody());
            Log.e(TAG, "Notification ClickAction: " + remoteMessage.getNotification().getClickAction());
            Log.e(TAG, "Data Payload: " + remoteMessage.getData().toString());
//            handleNotification(remoteMessage.getNotification().getBody());
            handleDataMessage(remoteMessage.getNotification().getBody(), remoteMessage.getData().toString());
        }

        Log.e(TAG, "Data: " + remoteMessage.getData());
        // Check if message contains a data payload.
        if (remoteMessage.getData().size() > 0) {
            Log.e(TAG, "Data Payload: " + remoteMessage.getData().toString());
            try {
//                JSONObject json = new JSONObject(remoteMessage.getData().toString());
                SharedPreferences pref = getApplicationContext().getSharedPreferences(Utils.SHARED_NOTIFI, 0);
                String notifications = pref.getString("notification", null);
                if(notifications != null){
                    JSONArray jsonArray = new JSONArray(notifications);
                    Log.e(TAG, "jsonArray 11: " + jsonArray.toString());
                    JSONObject jsonObject = new JSONObject();
                    Calendar c = Calendar.getInstance();
                    System.out.println("Current time => " + c.getTime());
                    SimpleDateFormat df = new SimpleDateFormat("dd MMM yyyy hh:mm a");
                    String formattedDate = df.format(c.getTime());
                    jsonObject.put("message",""+remoteMessage.getData().get("body"));
                    jsonObject.put("time",""+formattedDate);
                    Log.e(TAG, "jsonObject: " + jsonObject.toString());
                    jsonArray.put(jsonObject);
                    Log.e(TAG, "jsonArray 22: " + jsonArray.toString());
                    SharedPreferences.Editor editor = pref.edit();
                    editor.putString("notification", ""+jsonArray);
                    editor.commit();
                }else{
                    JSONArray jsonArray = new JSONArray();
                    Log.e(TAG, "jsonArray 11: " + jsonArray.toString());
                    JSONObject jsonObject = new JSONObject();
                    Calendar c = Calendar.getInstance();
                    System.out.println("Current time => " + c.getTime());
                    SimpleDateFormat df = new SimpleDateFormat("dd MMM yyyy hh:mm a");
                    String formattedDate = df.format(c.getTime());
                    jsonObject.put("message",""+remoteMessage.getData().get("body"));
                    jsonObject.put("time",""+formattedDate);
                    Log.e(TAG, "jsonObject: " + jsonObject.toString());
                    jsonArray.put(jsonObject);
                    Log.e(TAG, "jsonArray 22: " + jsonArray.toString());
                    SharedPreferences.Editor editor = pref.edit();
                    editor.putString("notification", ""+jsonArray.toString());
                    editor.commit();
                }
            } catch (Exception e) {
                e.printStackTrace();
                Log.e(TAG, "Exception: " + e.getMessage());
            }
        }
    }

    private void handleNotification(String message) {
        if (!NotificationUtils.isAppIsInBackground(getApplicationContext())) {
            // app is in foreground, broadcast the push message
            Intent pushNotification = new Intent(Utils.PUSH_NOTIFICATION);
            pushNotification.putExtra("message", message);
            LocalBroadcastManager.getInstance(this).sendBroadcast(pushNotification);

            // play notification sound
            NotificationUtils notificationUtils = new NotificationUtils(getApplicationContext());
            notificationUtils.playNotificationSound();
        } else {
            // If the app is in background, firebase itself handles the notification
        }
    }

    private void handleDataMessage(String message, String extras) {
        Log.e(TAG, "push json: ");

//        try {
//            JSONObject data = json.getJSONObject("data");
//
//            String title = data.getString("title");
//            String message = data.getString("message");
//            boolean isBackground = data.getBoolean("is_background");
//            String imageUrl = data.getString("image");
//            String timestamp = data.getString("timestamp");
//            JSONObject payload = data.getJSONObject("payload");
//
//            Log.e(TAG, "title: " + title);
//            Log.e(TAG, "message: " + message);
//            Log.e(TAG, "isBackground: " + isBackground);
//            Log.e(TAG, "payload: " + payload.toString());
//            Log.e(TAG, "imageUrl: " + imageUrl);
//            Log.e(TAG, "timestamp: " + timestamp);

        NotificationUtils.clearNotifications(getApplicationContext());


        if (!NotificationUtils.isAppIsInBackground(getApplicationContext())) {
            EventBus.getDefault().post(new MessageReceivedEvent(message));
//        }
                // app is in foreground, broadcast the push message
               // Intent pushNotification = new Intent(Utils.PUSH_NOTIFICATION);
                //pushNotification.putExtra("message", message);
                //LocalBroadcastManager.getInstance(this).sendBroadcast(pushNotification);
//
//                // play notification sound
                NotificationUtils notificationUtils = new NotificationUtils(getApplicationContext());
                notificationUtils.playNotificationSound();

            Intent resultIntent = new Intent(getApplicationContext(), RideActivity.class);
            Calendar c = Calendar.getInstance();
            System.out.println("Current time => " + c.getTime());
            SimpleDateFormat df = new SimpleDateFormat("MM-dd-yyyy hh:mm:ss a");
            String formattedDate = df.format(c.getTime());
            showNotificationMessage(getApplicationContext(), getString(R.string.app_name), message, formattedDate, resultIntent);
        } else {
            EventBus.getDefault().post(new MessageReceivedEvent(message));
//                // app is in background, show the notification in notification tray
        Intent resultIntent = new Intent(getApplicationContext(), RideActivity.class);
//        Utils.fromNotification = true;
//        Log.e("Utils.fromNotification",""+Utils.fromNotification);
//                resultIntent.putExtra("message", message);
//
//                // check for image attachment
//                if (TextUtils.isEmpty(imageUrl)) {
            NotificationUtils notificationUtils = new NotificationUtils(getApplicationContext());
            notificationUtils.playNotificationSound();

        Calendar c = Calendar.getInstance();
        System.out.println("Current time => " + c.getTime());
        SimpleDateFormat df = new SimpleDateFormat("MM-dd-yyyy hh:mm:ss a");
        String formattedDate = df.format(c.getTime());
        showNotificationMessage(getApplicationContext(), getString(R.string.app_name), message, formattedDate, resultIntent);
//                } else {
//                    // image is present, show notification with image
//                    showNotificationMessageWithBigImage(getApplicationContext(), title, message, timestamp, resultIntent, imageUrl);
//                }
        }
//        } catch (JSONException e) {
//            Log.e(TAG, "Json Exception: " + e.getMessage());
//        } catch (Exception e) {
//            Log.e(TAG, "Exception: " + e.getMessage());
//        }
    }

    /**
     * Showing notification with text only
     */
    private void showNotificationMessage(Context context, String title, String message, String timeStamp, Intent intent) {
        notificationUtils = new NotificationUtils(context);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        notificationUtils.showNotificationMessage(title, message, timeStamp, intent);
    }

    /**
     * Showing notification with text and image
     */
    private void showNotificationMessageWithBigImage(Context context, String title, String message, String timeStamp, Intent intent, String imageUrl) {
        notificationUtils = new NotificationUtils(context);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        notificationUtils.showNotificationMessage(title, message, timeStamp, intent, imageUrl);
    }

    @Override
    public void onStart(Intent intent, int startId) {
        super.onStart(intent, startId);
        EventBus.getDefault().register(this);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }
}
