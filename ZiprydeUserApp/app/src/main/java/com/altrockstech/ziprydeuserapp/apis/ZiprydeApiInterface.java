package com.altrockstech.ziprydeuserapp.apis;

import com.altrockstech.ziprydeuserapp.modelget.SingleInstantResponse;
import com.altrockstech.ziprydeuserapp.modelpost.SingleInstantParameters;

import retrofit2.Call;
import retrofit2.http.Body;
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
}
