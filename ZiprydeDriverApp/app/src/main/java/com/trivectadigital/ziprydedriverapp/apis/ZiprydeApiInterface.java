package com.trivectadigital.ziprydedriverapp.apis;

import com.trivectadigital.ziprydedriverapp.modelget.ListOfBooking;
import com.trivectadigital.ziprydedriverapp.modelget.ListOfPercentage;
import com.trivectadigital.ziprydedriverapp.modelget.ListOfRequestedBooking;
import com.trivectadigital.ziprydedriverapp.modelget.SingleInstantResponse;
import com.trivectadigital.ziprydedriverapp.modelpost.SingleInstantParameters;

import java.util.LinkedList;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;

/**
 * Created by Hari on 14-06-2017.
 */

public interface ZiprydeApiInterface {

    @POST("zipryde/getOTPByMobile")
    Call<SingleInstantResponse> getOTPByMobile(@Body SingleInstantParameters parameters);

    @POST("zipryde/verifyOTPByMobile")
    Call<SingleInstantResponse> verifyOTPByMobile(@Body SingleInstantParameters parameters);

    @Multipart
    @POST("zipryde/saveUser")
    Call<SingleInstantResponse> saveUser(@Part MultipartBody.Part userImage, @Part MultipartBody.Part frontImage, @Part MultipartBody.Part backImage, @Part("userType") RequestBody userType, @Part("firstName") RequestBody firstName,
            @Part("lastName") RequestBody lastName, @Part("emailId") RequestBody emailId, @Part("mobileNumber") RequestBody mobileNumber, @Part("password") RequestBody password, @Part("licenseNo") RequestBody licenseNo
            , @Part("licenseValidUntil") RequestBody licenseValidUntil, @Part("licenseIssuedOn") RequestBody licenseIssuedOn, @Part("alternateNumber") RequestBody alternateNumber, @Part("status") RequestBody status, @Part("deviceToken") RequestBody deviceToken);

    @Multipart
    @POST("zipryde/saveUser")
    Call<SingleInstantResponse> saveUser(@Part MultipartBody.Part userImage, @Part("userType") RequestBody userType, @Part("firstName") RequestBody firstName,
                                         @Part("lastName") RequestBody lastName, @Part("emailId") RequestBody emailId, @Part("mobileNumber") RequestBody mobileNumber, @Part("password") RequestBody password, @Part("licenseNo") RequestBody licenseNo
            , @Part("licenseValidUntil") RequestBody licenseValidUntil, @Part("licenseIssuedOn") RequestBody licenseIssuedOn, @Part("alternateNumber") RequestBody alternateNumber, @Part("status") RequestBody status, @Part("deviceToken") RequestBody deviceToken);

    @Multipart
    @POST("zipryde/saveUser")
    Call<SingleInstantResponse> saveUser(@Part MultipartBody.Part userImage, @Part MultipartBody.Part backImage, @Part("userType") RequestBody userType, @Part("firstName") RequestBody firstName,
                                         @Part("lastName") RequestBody lastName, @Part("emailId") RequestBody emailId, @Part("mobileNumber") RequestBody mobileNumber, @Part("password") RequestBody password, @Part("licenseNo") RequestBody licenseNo
            , @Part("licenseValidUntil") RequestBody licenseValidUntil, @Part("licenseIssuedOn") RequestBody licenseIssuedOn, @Part("alternateNumber") RequestBody alternateNumber, @Part("status") RequestBody status, @Part("deviceToken") RequestBody deviceToken);

    @POST("zipryde/verifyLogInUser")
    Call<SingleInstantResponse> verifyLogInUser(@Body SingleInstantParameters parameters);

    @POST("zipryde/updatePasswordByUserAndType")
    Call<SingleInstantResponse> updatePasswordByUserAndType(@Body SingleInstantParameters parameters);

    @GET("zipryde/getAllNYOPList")
    Call<LinkedList<ListOfPercentage>> getAllNYOPList();

    @POST("zipryde/insertDriverSession")
    Call<Void> insertDriverSession(@Body SingleInstantParameters parameters);

    @POST("zipryde/updateDriverSession")
    Call<Void> updateDriverSession(@Body SingleInstantParameters parameters);

    @POST("zipryde/updateDriverStatus")
    Call<Void> updateDriverStatus(@Body SingleInstantParameters parameters);

    @POST("zipryde/getBookingRequestedByDriverId")
    Call<LinkedList<ListOfRequestedBooking>> getBookingRequestedByDriverId(@Body SingleInstantParameters parameters);

    @POST("zipryde/updateBookingDriverStatus")
    Call<SingleInstantResponse> updateBookingDriverStatus(@Body SingleInstantParameters parameters);

    @POST("zipryde/getRevenueByDateAndDriverId")
    Call<SingleInstantResponse> getRevenueByDateAndDriverId(@Body SingleInstantParameters parameters);

    @POST("zipryde/getBookingCountByDateAndDriverId")
    Call<SingleInstantResponse> getBookingCountByDateAndDriverId(@Body SingleInstantParameters parameters);

    @POST("zipryde/getBookingByDriverId")
    Call<LinkedList<ListOfBooking>> getBookingByDriverId(@Body SingleInstantParameters parameters);

    @POST("zipryde/savePayment")
    Call<Void> savePayment(@Body SingleInstantParameters parameters);
}
