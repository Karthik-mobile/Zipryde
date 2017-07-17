package com.trivectadigital.ziprydeuserapp.apis;

import android.util.Log;

import com.trivectadigital.ziprydeuserapp.assist.Utils;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by Hari on 14-06-2017.
 */

public class ZiprydeApiClient {

//    private static final String BASE_URL = "http://54.218.115.164:8080/zipryde/";
    private static String BASE_URL = "http://"+Utils.defaultIP+"/zipryde/";
    private static Retrofit retrofit = null;

    public static Retrofit getClient() {
        BASE_URL = "http://"+Utils.defaultIP+"/zipryde/";
        Log.e("BASE_URL",""+BASE_URL);
//        if (retrofit==null) {
            retrofit = new Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
//        }
        return retrofit;
    }
}
