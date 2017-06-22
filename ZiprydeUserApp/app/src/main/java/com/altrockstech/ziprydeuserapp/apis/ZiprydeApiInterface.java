package com.altrockstech.ziprydeuserapp.apis;

import com.altrockstech.ziprydeuserapp.modelget.ListOfCarTypes;
import com.altrockstech.ziprydeuserapp.modelget.ListOfFairEstimate;
import com.altrockstech.ziprydeuserapp.modelget.SingleInstantResponse;
import com.altrockstech.ziprydeuserapp.modelpost.SingleInstantParameters;

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

    @POST("getAllNYOPByCabTypeAndDistance")
    Call<LinkedList<ListOfFairEstimate>> getAllNYOPByCabTypeAndDistance(@Body SingleInstantParameters parameters);
}