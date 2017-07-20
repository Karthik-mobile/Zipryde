package com.trivectadigital.ziprydeuserapp.apis;

import com.trivectadigital.ziprydeuserapp.modelget.ListOfBooking;
import com.trivectadigital.ziprydeuserapp.modelget.ListOfCarTypes;
import com.trivectadigital.ziprydeuserapp.modelget.ListOfCurrentCabs;
import com.trivectadigital.ziprydeuserapp.modelget.ListOfFairEstimate;
import com.trivectadigital.ziprydeuserapp.modelget.SingleInstantResponse;
import com.trivectadigital.ziprydeuserapp.modelpost.SingleInstantParameters;

import java.util.LinkedList;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;

/**
 * Created by Hari on 14-06-2017.
 */

public interface ZiprydeApiInterface {

    @POST("getOTPByMobile")
    Call<SingleInstantResponse> getOTPByMobile(@Body SingleInstantParameters parameters);

    @POST("verifyOTPByMobile")
    Call<SingleInstantResponse> verifyOTPByMobile(@Body SingleInstantParameters parameters);

    @POST("saveUser")
    Call<SingleInstantResponse> saveUser(@Body SingleInstantParameters parameters);

    @POST("verifyLogInUser")
    Call<SingleInstantResponse> verifyLogInUser(@Body SingleInstantParameters parameters);

    @GET("getAllCabTypes")
    Call<LinkedList<ListOfCarTypes>> getAllCabTypes();

    @POST("getAllNYOPByCabTypeDistAndNoOfPassenger")
    Call<LinkedList<ListOfFairEstimate>> getAllNYOPByCabTypeAndDistance(@Body SingleInstantParameters parameters);

    @POST("getNearByActiveDrivers")
    Call<LinkedList<ListOfCurrentCabs>> getNearByActiveDrivers(@Body SingleInstantParameters parameters);

    @POST("requestBooking")
    Call<SingleInstantResponse> requestBooking(@Body SingleInstantParameters parameters);

    @POST("getBookingByBookingId")
    Call<SingleInstantResponse> getBookingByBookingId(@Body SingleInstantParameters parameters);

    @POST("getBookingByuserId")
    Call<LinkedList<ListOfBooking>> getBookingByUserId(@Body SingleInstantParameters parameters);

    @POST("getGeoLocationByDriverId")
    Call<SingleInstantResponse> getGeoLocationByDriverId(@Body SingleInstantParameters parameters);
}
