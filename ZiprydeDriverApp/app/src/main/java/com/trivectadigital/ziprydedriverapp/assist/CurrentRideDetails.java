package com.trivectadigital.ziprydedriverapp.assist;

/**
 * Created by Hari on 18-06-2017.
 */

public class CurrentRideDetails {

    private String rideId, crnNumber, dateTime, pickupLocation, dropLocation;

    public CurrentRideDetails(String rideId, String crnNumber, String dateTime, String pickupLocation, String dropLocation) {
        this.rideId = rideId;
        this.crnNumber = crnNumber;
        this.dateTime = dateTime;
        this.pickupLocation = pickupLocation;
        this.dropLocation = dropLocation;
    }

    public String getRideId() {
        return rideId;
    }

    public void setRideId(String rideId) {
        this.rideId = rideId;
    }

    public String getCrnNumber() {
        return crnNumber;
    }

    public void setCrnNumber(String crnNumber) {
        this.crnNumber = crnNumber;
    }

    public String getDateTime() {
        return dateTime;
    }

    public void setDateTime(String dateTime) {
        this.dateTime = dateTime;
    }

    public String getPickupLocation() {
        return pickupLocation;
    }

    public void setPickupLocation(String pickupLocation) {
        this.pickupLocation = pickupLocation;
    }

    public String getDropLocation() {
        return dropLocation;
    }

    public void setDropLocation(String dropLocation) {
        this.dropLocation = dropLocation;
    }
}
