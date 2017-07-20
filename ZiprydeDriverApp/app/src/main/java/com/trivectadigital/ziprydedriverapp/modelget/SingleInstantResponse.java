package com.trivectadigital.ziprydedriverapp.modelget;

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
    private int userId;
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
    @SerializedName("licenseFrontImage")
    private String licenseFrontImage;
    @SerializedName("licenseBackImage")
    private String licenseBackImage;

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
    @SerializedName("driverId")
    private String driverId;
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
    @SerializedName("driverStatus")
    private String driverStatus;
    @SerializedName("noOfPassengers")
    private String noOfPassengers;
    @SerializedName("geoLocationResponse")
    private GeoLocationRequest geoLocationResponse;

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

    public String getDriverId() {
        return driverId;
    }

    public void setDriverId(String driverId) {
        this.driverId = driverId;
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

    public String getDriverStatus() {
        return driverStatus;
    }

    public void setDriverStatus(String driverStatus) {
        this.driverStatus = driverStatus;
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

    public String getLicenseFrontImage() {
        return licenseFrontImage;
    }

    public void setLicenseFrontImage(String licenseFrontImage) {
        this.licenseFrontImage = licenseFrontImage;
    }

    public String getLicenseBackImage() {
        return licenseBackImage;
    }

    public void setLicenseBackImage(String licenseBackImage) {
        this.licenseBackImage = licenseBackImage;
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

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
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
}
