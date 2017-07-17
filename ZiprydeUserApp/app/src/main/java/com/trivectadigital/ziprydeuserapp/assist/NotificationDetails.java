package com.trivectadigital.ziprydeuserapp.assist;

/**
 * Created by Hari on 08-06-2017.
 */

public class NotificationDetails {

    public String notificationID, notificationName, notificationDateTime, notificationContent, notificationImg;

    public NotificationDetails(String notificationID, String notificationName, String notificationDateTime, String notificationContent, String notificationImg) {
        this.notificationID = notificationID;
        this.notificationName = notificationName;
        this.notificationDateTime = notificationDateTime;
        this.notificationContent = notificationContent;
        this.notificationImg = notificationImg;
    }

    public String getNotificationID() {
        return notificationID;
    }

    public void setNotificationID(String notificationID) {
        this.notificationID = notificationID;
    }

    public String getNotificationName() {
        return notificationName;
    }

    public void setNotificationName(String notificationName) {
        this.notificationName = notificationName;
    }

    public String getNotificationDateTime() {
        return notificationDateTime;
    }

    public void setNotificationDateTime(String notificationDateTime) {
        this.notificationDateTime = notificationDateTime;
    }

    public String getNotificationContent() {
        return notificationContent;
    }

    public void setNotificationContent(String notificationContent) {
        this.notificationContent = notificationContent;
    }

    public String getNotificationImg() {
        return notificationImg;
    }

    public void setNotificationImg(String notificationImg) {
        this.notificationImg = notificationImg;
    }
}
