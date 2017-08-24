package com.trivectadigital.ziprydedriverapp.modelget;

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
    @SerializedName("customerMobileNumber")
    private String customerMobileNumber;
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

    public String getCustomerMobileNumber() {
        return customerMobileNumber;
    }

    public void setCustomerMobileNumber(String customerMobileNumber) {
        this.customerMobileNumber = customerMobileNumber;
    }

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
}
