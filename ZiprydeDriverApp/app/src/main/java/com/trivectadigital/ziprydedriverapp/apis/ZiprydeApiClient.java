package com.trivectadigital.ziprydedriverapp.apis;

import android.util.Log;

import com.trivectadigital.ziprydedriverapp.assist.Utils;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by Hari on 14-06-2017.
 */

public class ZiprydeApiClient {

    private static String BASE_URL = "http://"+ Utils.defaultIP+"/";
    private static Retrofit retrofit = null;

    public static Retrofit getClient() {
        BASE_URL = "http://"+ Utils.defaultIP+"/";
        Log.e("BASE_URL",""+BASE_URL);
        //if (retrofit==null) {
            retrofit = new Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
        //}
        return retrofit;
    }
}
