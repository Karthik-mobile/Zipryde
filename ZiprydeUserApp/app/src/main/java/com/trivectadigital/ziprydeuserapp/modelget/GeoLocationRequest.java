package com.trivectadigital.ziprydeuserapp.modelget;

import com.google.gson.annotations.SerializedName;

/**
 * Created by Hari on 19-07-2017.
 */

public class GeoLocationRequest {

    @SerializedName("fromLatitude")
    private String fromLatitude;
    @SerializedName("fromLongitude")
    private String fromLongitude;
    @SerializedName("toLatitude")
    private String toLatitude;
    @SerializedName("toLongitude")
    private String toLongitude;
    @SerializedName("distanceInMiles")
    private String distanceInMiles;

    public String getFromLatitude() {
        return fromLatitude;
    }

    public void setFromLatitude(String fromLatitude) {
        this.fromLatitude = fromLatitude;
    }

    public String getFromLongitude() {
        return fromLongitude;
    }

    public void setFromLongitude(String fromLongitude) {
        this.fromLongitude = fromLongitude;
    }

    public String getToLatitude() {
        return toLatitude;
    }

    public void setToLatitude(String toLatitude) {
        this.toLatitude = toLatitude;
    }

    public String getToLongitude() {
        return toLongitude;
    }

    public void setToLongitude(String toLongitude) {
        this.toLongitude = toLongitude;
    }

    public String getDistanceInMiles() {
        return distanceInMiles;
    }

    public void setDistanceInMiles(String distanceInMiles) {
        this.distanceInMiles = distanceInMiles;
    }
}
