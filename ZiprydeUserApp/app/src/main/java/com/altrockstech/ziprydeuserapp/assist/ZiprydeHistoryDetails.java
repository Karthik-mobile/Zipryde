package com.altrockstech.ziprydeuserapp.assist;

/**
 * Created by Hari on 09-06-2017.
 */

public class ZiprydeHistoryDetails {

    String ziprydeBookingID, ziprydeBookingCarType, ziprydeBookingDateTime, ziprydeBookingCRN, ziprydeBookingStarting, ziprydeBookingEnding, ziprydeBookingPrice, ziprydeBookingOfferPrice, ziprydeBookingProfileImg;

    public ZiprydeHistoryDetails(String ziprydeBookingID, String ziprydeBookingCarType, String ziprydeBookingDateTime, String ziprydeBookingCRN, String ziprydeBookingStarting, String ziprydeBookingEnding, String ziprydeBookingPrice, String ziprydeBookingOfferPrice, String ziprydeBookingProfileImg) {
        this.ziprydeBookingID = ziprydeBookingID;
        this.ziprydeBookingCarType = ziprydeBookingCarType;
        this.ziprydeBookingDateTime = ziprydeBookingDateTime;
        this.ziprydeBookingCRN = ziprydeBookingCRN;
        this.ziprydeBookingStarting = ziprydeBookingStarting;
        this.ziprydeBookingEnding = ziprydeBookingEnding;
        this.ziprydeBookingPrice = ziprydeBookingPrice;
        this.ziprydeBookingOfferPrice = ziprydeBookingOfferPrice;
        this.ziprydeBookingProfileImg = ziprydeBookingProfileImg;
    }

    public String getZiprydeBookingID() {
        return ziprydeBookingID;
    }

    public void setZiprydeBookingID(String ziprydeBookingID) {
        this.ziprydeBookingID = ziprydeBookingID;
    }

    public String getZiprydeBookingCarType() {
        return ziprydeBookingCarType;
    }

    public void setZiprydeBookingCarType(String ziprydeBookingCarType) {
        this.ziprydeBookingCarType = ziprydeBookingCarType;
    }

    public String getZiprydeBookingDateTime() {
        return ziprydeBookingDateTime;
    }

    public void setZiprydeBookingDateTime(String ziprydeBookingDateTime) {
        this.ziprydeBookingDateTime = ziprydeBookingDateTime;
    }

    public String getZiprydeBookingCRN() {
        return ziprydeBookingCRN;
    }

    public void setZiprydeBookingCRN(String ziprydeBookingCRN) {
        this.ziprydeBookingCRN = ziprydeBookingCRN;
    }

    public String getZiprydeBookingStarting() {
        return ziprydeBookingStarting;
    }

    public void setZiprydeBookingStarting(String ziprydeBookingStarting) {
        this.ziprydeBookingStarting = ziprydeBookingStarting;
    }

    public String getZiprydeBookingEnding() {
        return ziprydeBookingEnding;
    }

    public void setZiprydeBookingEnding(String ziprydeBookingEnding) {
        this.ziprydeBookingEnding = ziprydeBookingEnding;
    }

    public String getZiprydeBookingPrice() {
        return ziprydeBookingPrice;
    }

    public void setZiprydeBookingPrice(String ziprydeBookingPrice) {
        this.ziprydeBookingPrice = ziprydeBookingPrice;
    }

    public String getZiprydeBookingOfferPrice() {
        return ziprydeBookingOfferPrice;
    }

    public void setZiprydeBookingOfferPrice(String ziprydeBookingOfferPrice) {
        this.ziprydeBookingOfferPrice = ziprydeBookingOfferPrice;
    }

    public String getZiprydeBookingProfileImg() {
        return ziprydeBookingProfileImg;
    }

    public void setZiprydeBookingProfileImg(String ziprydeBookingProfileImg) {
        this.ziprydeBookingProfileImg = ziprydeBookingProfileImg;
    }
}
