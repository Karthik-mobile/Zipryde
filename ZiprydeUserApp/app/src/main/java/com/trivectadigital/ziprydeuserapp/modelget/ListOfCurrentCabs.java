package com.trivectadigital.ziprydeuserapp.modelget;

/**
 * Created by Hari on 06-07-2017.
 */

public class ListOfCurrentCabs {

    String userId, latitude, longitude, isOnline, cabTypeId, cabType;

    public String getCabTypeId() {
        return cabTypeId;
    }

    public void setCabTypeId(String cabTypeId) {
        this.cabTypeId = cabTypeId;
    }

    public String getCabType() {
        return cabType;
    }

    public void setCabType(String cabType) {
        this.cabType = cabType;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getLatitude() {
        return latitude;
    }

    public void setLatitude(String latitude) {
        this.latitude = latitude;
    }

    public String getLongitude() {
        return longitude;
    }

    public void setLongitude(String longitude) {
        this.longitude = longitude;
    }

    public String getIsOnline() {
        return isOnline;
    }

    public void setIsOnline(String isOnline) {
        this.isOnline = isOnline;
    }
}
