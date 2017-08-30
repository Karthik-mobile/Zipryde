package com.trivectadigital.ziprydeuserapp.apis;

import com.trivectadigital.ziprydeuserapp.modelget.ListOfBooking;
import com.trivectadigital.ziprydeuserapp.modelget.ListOfCarTypes;
import com.trivectadigital.ziprydeuserapp.modelget.ListOfCurrentCabs;
import com.trivectadigital.ziprydeuserapp.modelget.ListOfFairEstimate;
import com.trivectadigital.ziprydeuserapp.modelget.SingleInstantResponse;
import com.trivectadigital.ziprydeuserapp.modelpost.SingleInstantParameters;

import java.util.LinkedList;

import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;

/**
 * Created by Hari on 14-06-2017.
 */

public interface ZiprydeApiInterface {

    @POST("getOTPByMobile")
    Call<SingleInstantResponse> getOTPByMobile(@Body SingleInstantParameters parameters);

    @POST("verifyOTPByMobile")
    Call<SingleInstantResponse> verifyOTPByMobile(@Body SingleInstantParameters parameters);

    @Multipart
    @POST("saveUser")
    Call<SingleInstantResponse> saveUser(@Part("userType") RequestBody userType, @Part("firstName") RequestBody firstName,
                                         @Part("lastName") RequestBody lastName, @Part("emailId") RequestBody emailId, @Part("mobileNumber") RequestBody mobileNumber, @Part("password") RequestBody password, @Part("alternateNumber") RequestBody alternateNumber, @Part("deviceToken") RequestBody deviceToken, @Part("isEnable") RequestBody isEnable);

    @Multipart
    @POST("saveUser")
    Call<SingleInstantResponse> saveUpdateUser(@Part("userType") RequestBody userType, @Part("firstName") RequestBody firstName,
                                         @Part("lastName") RequestBody lastName, @Part("emailId") RequestBody emailId, @Part("mobileNumber") RequestBody mobileNumber, @Part("userId") RequestBody userId, @Part("alternateNumber") RequestBody alternateNumber);


    //@Header("Content-Range") String contentRange
    @POST("verifyLogInUser")
    Call<SingleInstantResponse> verifyLogInUser(@Body SingleInstantParameters parameters);

    @POST("updatePasswordByUserAndType")
    Call<SingleInstantResponse> updatePasswordByUserAndType(@Body SingleInstantParameters parameters);

    @GET("getAllCabTypes")
    Call<LinkedList<ListOfCarTypes>> getAllCabTypes(@Header("access-token") String contentRange);

    @POST("getAllNYOPByCabTypeDistAndNoOfPassenger")
    Call<LinkedList<ListOfFairEstimate>> getAllNYOPByCabTypeAndDistance(@Header("access-token") String contentRange,@Body SingleInstantParameters parameters);

    @POST("getNearByActiveDrivers")
    Call<LinkedList<ListOfCurrentCabs>> getNearByActiveDrivers(@Header("access-token") String contentRange,@Body SingleInstantParameters parameters);

    @POST("requestBooking")
    Call<SingleInstantResponse> requestBooking(@Header("access-token") String contentRange,@Body SingleInstantParameters parameters);

    @POST("getBookingByBookingId")
    Call<SingleInstantResponse> getBookingByBookingId(@Header("access-token") String contentRange,@Body SingleInstantParameters parameters);

    @POST("getBookingByuserId")
    Call<LinkedList<ListOfBooking>> getBookingByUserId(@Header("access-token") String contentRange,@Body SingleInstantParameters parameters);

    @POST("getGeoLocationByDriverId")
    Call<SingleInstantResponse> getGeoLocationByDriverId(@Header("access-token") String contentRange,@Body SingleInstantParameters parameters);

    @POST("updateBookingStatus")
    Call<SingleInstantResponse> updateBookingStatus(@Header("access-token") String contentRange,@Body SingleInstantParameters parameters);

    @POST("saveLostItem")
    Call<SingleInstantResponse> saveLostItem(@Header("access-token") String contentRange,@Body SingleInstantParameters parameters);

    @POST("logoutUser")
    Call<Void> logoutUser(@Header("access-token") String contentRange,@Body SingleInstantParameters parameters);

    @POST("getBookingByBookingStatusAndUserId")
    Call<LinkedList<ListOfBooking>> getBookingByBookingStatusAndUserId(@Header("access-token") String contentRange,@Body SingleInstantParameters parameters);

    @POST("zipryde/updateDeviceToken")
    Call<Void> updateDeviceToken(@Header("access-token") String contentRange,@Body SingleInstantParameters parameters);

}
