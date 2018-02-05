package com.trivectadigital.ziprydeuserapp.modelget;

import com.google.gson.annotations.SerializedName;

/**
 * Created by Hari on 14-06-2017.
 */

public class SingleInstantResponse {

    @SerializedName("mobileNumber")
    private String mobileNumber;
    @SerializedName("otp")
    private String otp;
    @SerializedName("validity")
    private String validity;
    @SerializedName("otpStatus")
    private String otpStatus;
    @SerializedName("userId")
    private String userId;
    @SerializedName("userType")
    private String userType;
    @SerializedName("firstName")
    private String firstName;
    @SerializedName("lastName")
    private String lastName;
    @SerializedName("emailId")
    private String emailId;
    @SerializedName("message")
    private String message;

    @SerializedName("bookingId")
    private String bookingId;
    @SerializedName("cabTypeId")
    private String cabTypeId;
    @SerializedName("cabType")
    private String cabType;
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

    @SerializedName("latitude")
    private String latitude;
    @SerializedName("longitude")
    private String longitude;
    @SerializedName("isOnline")
    private String isOnline;

    @SerializedName("driverId")
    private String driverId;
    @SerializedName("driverStatus")
    private String driverStatus;
    @SerializedName("driverStatusCode")
    private String driverStatusCode;
    @SerializedName("driverName")
    private String driverName;
    @SerializedName("driverImage")
    private String driverImage;
    @SerializedName("geoLocationResponse")
    private GeoLocationRequest geoLocationResponse;
    @SerializedName("driverMobileNumber")
    private String driverMobileNumber;
    @SerializedName("accessToken")
    private String accessToken;

    @SerializedName("vehicleNumber")
    private String vehicleNumber;
    @SerializedName("make")
    private String make;
    @SerializedName("model")
    private String model;
    @SerializedName("licensePlateNumber")
    private String licensePlateNumber;

    @SerializedName("crnNumber")
    private String crnNumber;

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

    public GeoLocationRequest getGeoLocationResponse() {
        return geoLocationResponse;
    }

    public void setGeoLocationResponse(GeoLocationRequest geoLocationResponse) {
        this.geoLocationResponse = geoLocationResponse;
    }

    public String getDriverId() {
        return driverId;
    }

    public void setDriverId(String driverId) {
        this.driverId = driverId;
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

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getMobileNumber() {
        return mobileNumber;
    }

    public void setMobileNumber(String mobileNumber) {
        this.mobileNumber = mobileNumber;
    }

    public String getOtp() {
        return otp;
    }

    public void setOtp(String otp) {
        this.otp = otp;
    }

    public String getValidity() {
        return validity;
    }

    public void setValidity(String validity) {
        this.validity = validity;
    }

    public String getOtpStatus() {
        return otpStatus;
    }

    public void setOtpStatus(String otpStatus) {
        this.otpStatus = otpStatus;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getUserType() {
        return userType;
    }

    public void setUserType(String userType) {
        this.userType = userType;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getEmailId() {
        return emailId;
    }

    public void setEmailId(String emailId) {
        this.emailId = emailId;
    }

    public String getAccessToken() {
        return accessToken;
    }

    public void setAccessToken(String token) {
        this.accessToken = token;
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

    public String getCrnNumber() {
        return crnNumber;
    }

    public void setCrnNumber(String crnNumber) {
        this.crnNumber = crnNumber;
    }

    public String getCabImage() {
        return cabImage;
    }

    public void setCabImage(String cabBinaryImage) {
        this.cabImage = cabBinaryImage;
    }
}
