package com.trivectadigital.ziprydedriverapp.apis;

import com.trivectadigital.ziprydedriverapp.modelget.ListOfPercentage;
import com.trivectadigital.ziprydedriverapp.modelget.SingleInstantResponse;
import com.trivectadigital.ziprydedriverapp.modelpost.SingleInstantParameters;

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

    @GET("getAllNYOPList")
    Call<LinkedList<ListOfPercentage>> getAllNYOPList();

    @POST("insertDriverSession")
    Call<Void> insertDriverSession(@Body SingleInstantParameters parameters);

    @POST("updateDriverSession")
    Call<Void> updateDriverSession(@Body SingleInstantParameters parameters);

    @POST("updateDriverStatus")
    Call<Void> updateDriverStatus(@Body SingleInstantParameters parameters);
}
