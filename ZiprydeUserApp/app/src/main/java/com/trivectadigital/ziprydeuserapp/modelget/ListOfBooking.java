package com.trivectadigital.ziprydeuserapp.modelget;

import com.google.gson.annotations.SerializedName;

/**
 * Created by Hari on 19-07-2017.
 */

public class ListOfBooking {
    @SerializedName("bookingId")
    private String bookingId;
    @SerializedName("cabTypeId")
    private String cabTypeId;
    @SerializedName("cabType")
    private String cabType;
    @SerializedName("customerId")
    private String customerId;
    @SerializedName("customerName")
    private String customerName;
    @SerializedName("from")
    private String from;
    @SerializedName("to")
    private String to;
    @SerializedName("suggestedPrice")
    private String suggestedPrice;
    @SerializedName("offeredPrice")
    private String offeredPrice;
    @SerializedName("bookingStatus")
    private String bookingStatus;
    @SerializedName("bookingStatusCode")
    private String bookingStatusCode;
    @SerializedName("noOfPassengers")
    private String noOfPassengers;
    @SerializedName("geoLocationResponse")
    private GeoLocationRequest geoLocationResponse;
    @SerializedName("crnNumber")
    private String crnNumber;
    @SerializedName("bookingDateTime")
    private String bookingDateTime;
    @SerializedName("driverStatus")
    private String driverStatus;
    @SerializedName("driverStatusCode")
    private String driverStatusCode;
    @SerializedName("driverName")
    private String driverName;
    @SerializedName("driverId")
    private String driverId;
    @SerializedName("driverImage")
    private String driverImage;
    @SerializedName("driverMobileNumber")
    private String driverMobileNumber;

    @SerializedName("vehicleNumber")
    private String vehicleNumber;
    @SerializedName("make")
    private String make;
    @SerializedName("model")
    private String model;
    @SerializedName("licensePlateNumber")
    private String licensePlateNumber;

    @SerializedName("cabImage")
    private String cabImage;


    public String getBookingStatusCode() {
        return bookingStatusCode;
    }

    public void setBookingStatusCode(String bookingStatusCode) {
        this.bookingStatusCode = bookingStatusCode;
    }

    public String getDriverStatusCode() {
        return driverStatusCode;
    }

    public void setDriverStatusCode(String driverStatusCode) {
        this.driverStatusCode = driverStatusCode;
    }

    public String getDriverMobileNumber() {
        return driverMobileNumber;
    }

    public void setDriverMobileNumber(String driverMobileNumber) {
        this.driverMobileNumber = driverMobileNumber;
    }

    public String getDriverImage() {
        return driverImage;
    }

    public void setDriverImage(String driverImage) {
        this.driverImage = driverImage;
    }

    public String getBookingId() {
        return bookingId;
    }

    public void setBookingId(String bookingId) {
        this.bookingId = bookingId;
    }

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

    public String getCustomerId() {
        return customerId;
    }

    public void setCustomerId(String customerId) {
        this.customerId = customerId;
    }

    public String getCustomerName() {
        return customerName;
    }

    public void setCustomerName(String customerName) {
        this.customerName = customerName;
    }

    public String getFrom() {
        return from;
    }

    public void setFrom(String from) {
        this.from = from;
    }

    public String getTo() {
        return to;
    }

    public void setTo(String to) {
        this.to = to;
    }

    public String getSuggestedPrice() {
        return suggestedPrice;
    }

    public void setSuggestedPrice(String suggestedPrice) {
        this.suggestedPrice = suggestedPrice;
    }

    public String getOfferedPrice() {
        return offeredPrice;
    }

    public void setOfferedPrice(String offeredPrice) {
        this.offeredPrice = offeredPrice;
    }

    public String getBookingStatus() {
        return bookingStatus;
    }

    public void setBookingStatus(String bookingStatus) {
        this.bookingStatus = bookingStatus;
    }

    public String getNoOfPassengers() {
        return noOfPassengers;
    }

    public void setNoOfPassengers(String noOfPassengers) {
        this.noOfPassengers = noOfPassengers;
    }

    public GeoLocationRequest getGeoLocationResponse() {
        return geoLocationResponse;
    }

    public void setGeoLocationResponse(GeoLocationRequest geoLocationResponse) {
        this.geoLocationResponse = geoLocationResponse;
    }

    public String getCrnNumber() {
        return crnNumber;
    }

    public void setCrnNumber(String crnNumber) {
        this.crnNumber = crnNumber;
    }

    public String getBookingDateTime() {
        return bookingDateTime;
    }

    public void setBookingDateTime(String bookingDateTime) {
        this.bookingDateTime = bookingDateTime;
    }

    public String getDriverStatus() {
        return driverStatus;
    }

    public void setDriverStatus(String driverStatus) {
        this.driverStatus = driverStatus;
    }

    public String getDriverName() {
        return driverName;
    }

    public void setDriverName(String driverName) {
        this.driverName = driverName;
    }

    public String getDriverId() {
        return driverId;
    }

    public void setDriverId(String driverId) {
        this.driverId = driverId;
    }

    public String getVehicleNumber() {
        return vehicleNumber;
    }

    public void setVehicleNumber(String cabNo) {
        this.vehicleNumber = cabNo;
    }

    public String getMake() {
        return make;
    }

    public void setMake(String cabMake) {
        this.make = cabMake;
    }

    public String getModel() {
        return model;
    }

    public void setModel(String cabModel) {
        this.model = cabModel;
    }

    public String getLicensePlateNumber() {
        return licensePlateNumber;
    }

    public void setLicensePlateNumber(String cabLicenseNo) {
        this.licensePlateNumber = cabLicenseNo;

    }

    public String getCabImage() {
        return cabImage;
    }

    public void setCabImage(String cabBinaryImage) {
        this.cabImage = cabBinaryImage;
    }

}
