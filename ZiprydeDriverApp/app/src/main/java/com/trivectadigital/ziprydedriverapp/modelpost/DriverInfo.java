
package com.trivectadigital.ziprydedriverapp.modelpost;

import com.google.gson.annotations.SerializedName;

public class DriverInfo
{

    @SerializedName("userId")
    private Integer userId;

    @SerializedName("userType")
    private String userType;

    @SerializedName("firstName")
    private String firstName;
    @SerializedName("lastName")
    private String lastName;
    @SerializedName("mobileNumber")
    private String mobileNumber;
    @SerializedName("emailId")
    private String emailId;
    @SerializedName("driverProfileId")
    private Integer driverProfileId;
    @SerializedName("licenseNo")
    private String licenseNo;
    @SerializedName("licenseIssuedOn")
    private String licenseIssuedOn;
    @SerializedName("licenseValidUntil")
    private String licenseValidUntil;
    @SerializedName("defaultPercentageAccepted")
    private Integer defaultPercentageAccepted;
    @SerializedName("isOnline")
    private Integer isOnline;
    @SerializedName("isEnable")
    private Integer isEnable;
    @SerializedName("statusCode")
    private String statusCode;
    @SerializedName("status")
    private String status;
    @SerializedName("comments")
    private String comments;
    @SerializedName("vehicleNumber")
    private String vehicleNumber;
    @SerializedName("bookingId")
    private Integer bookingId;
    @SerializedName("insuranceCompany")
    private String insuranceCompany;
    @SerializedName("insuranceValidUntil")
    private String insuranceValidUntil;
    @SerializedName("insuranceNo")
    private String insuranceNo;
    @SerializedName("make")
    private String make;
    @SerializedName("model")
    private String model;
    @SerializedName("licenseFrontImage")
    private String licenseFrontImage;
    @SerializedName("licenseBackImage")
    private String licenseBackImage;
    @SerializedName("userImage")
    private String userImage;
    @SerializedName("cabImage")
    private String cabImage;


    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
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

    public String getMobileNumber() {
        return mobileNumber;
    }

    public void setMobileNumber(String mobileNumber) {
        this.mobileNumber = mobileNumber;
    }

    public String getEmailId() {
        return emailId;
    }

    public void setEmailId(String emailId) {
        this.emailId = emailId;
    }

    public Integer getDriverProfileId() {
        return driverProfileId;
    }

    public void setDriverProfileId(Integer driverProfileId) {
        this.driverProfileId = driverProfileId;
    }

    public String getLicenseNo() {
        return licenseNo;
    }

    public void setLicenseNo(String licenseNo) {
        this.licenseNo = licenseNo;
    }

    public String getLicenseIssuedOn() {
        return licenseIssuedOn;
    }

    public void setLicenseIssuedOn(String licenseIssuedOn) {
        this.licenseIssuedOn = licenseIssuedOn;
    }

    public String getLicenseValidUntil() {
        return licenseValidUntil;
    }

    public void setLicenseValidUntil(String licenseValidUntil) {
        this.licenseValidUntil = licenseValidUntil;
    }

    public Integer getDefaultPercentageAccepted() {
        return defaultPercentageAccepted;
    }

    public void setDefaultPercentageAccepted(Integer defaultPercentageAccepted) {
        this.defaultPercentageAccepted = defaultPercentageAccepted;
    }

    public Integer getIsOnline() {
        return isOnline;
    }

    public void setIsOnline(Integer isOnline) {
        this.isOnline = isOnline;
    }

    public Integer getIsEnable() {
        return isEnable;
    }

    public void setIsEnable(Integer isEnable) {
        this.isEnable = isEnable;
    }

    public String getStatusCode() {
        return statusCode;
    }

    public void setStatusCode(String statusCode) {
        this.statusCode = statusCode;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getComments() {
        return comments;
    }

    public void setComments(String comments) {
        this.comments = comments;
    }

    public String getVehicleNumber() {
        return vehicleNumber;
    }

    public void setVehicleNumber(String vehicleNumber) {
        this.vehicleNumber = vehicleNumber;
    }

    public Integer getBookingId() {
        return bookingId;
    }

    public void setBookingId(Integer bookingId) {
        this.bookingId = bookingId;
    }

    public String getInsuranceCompany() {
        return insuranceCompany;
    }

    public void setInsuranceCompany(String insuranceCompany) {
        this.insuranceCompany = insuranceCompany;
    }

    public String getInsuranceValidUntil() {
        return insuranceValidUntil;
    }

    public void setInsuranceValidUntil(String insuranceValidUntil) {
        this.insuranceValidUntil = insuranceValidUntil;
    }

    public String getInsuranceNo() {
        return insuranceNo;
    }

    public void setInsuranceNo(String insuranceNo) {
        this.insuranceNo = insuranceNo;
    }

    public String getMake() {
        return make;
    }

    public void setMake(String make) {
        this.make = make;
    }

    public String getModel() {
        return model;
    }

    public void setModel(String model) {
        this.model = model;
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

    public String getUserImage() {
        return userImage;
    }

    public void setUserImage(String userImage) {
        this.userImage = userImage;
    }

    public String getCabImage() {
        return cabImage;
    }

    public void setCabImage(String cabBinaryImage) {
        this.cabImage = cabBinaryImage;
    }



}
