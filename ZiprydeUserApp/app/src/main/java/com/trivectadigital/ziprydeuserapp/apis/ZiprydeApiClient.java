package com.trivectadigital.ziprydeuserapp.apis;

import android.util.Log;

import com.trivectadigital.ziprydeuserapp.assist.Utils;

import java.io.IOException;

import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by Hari on 14-06-2017.
 */

public class ZiprydeApiClient {

//    private static final String BASE_URL = "http://54.218.115.164:8080/zipryde/";
    private static String BASE_URL = "http://"+Utils.defaultIP+"/zipryde/";
    private static Retrofit retrofit = null;

    public static Retrofit getClient(String accessToken) {
        BASE_URL = "http://"+Utils.defaultIP+"/zipryde/";
        Log.e("BASE_URL",""+BASE_URL);

//        OkHttpClient.Builder builder = new OkHttpClient().newBuilder();
//        builder.readTimeout(10, TimeUnit.SECONDS);
//        builder.connectTimeout(5, TimeUnit.SECONDS);
//
//        if (BuildConfig.DEBUG) {
//            HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor();
//            interceptor.setLevel(HttpLoggingInterceptor.Level.BASIC);
//            builder.addInterceptor(interceptor);
//        }
//
//        builder.addInterceptor(chain -> {
//            DownloadManager.Request request = chain.request().newBuilder().addHeader("key", "value").build();
//            return chain.proceed(request);
//        });
//
//        builder.addInterceptor(new UnauthorisedInterceptor(context));
//        OkHttpClient client = builder.build();

//        if (retrofit==null) {




        OkHttpClient.Builder httpClient = new myOkHttp.Builder();
        httpClient.addNetworkInterceptor(new AddHeaderInterceptor(accessToken));
       // httpClient.addInterceptor(new LogJsonInterceptor());

            retrofit = new Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create())
                    //.client(httpClient.build())
                    .build();
//        }


        return retrofit;
    }

    public static Retrofit getClient() {
        BASE_URL = "http://"+Utils.defaultIP+"/zipryde/";
        Log.e("BASE_URL",""+BASE_URL);

//        OkHttpClient.Builder builder = new OkHttpClient().newBuilder();
//        builder.readTimeout(10, TimeUnit.SECONDS);
//        builder.connectTimeout(5, TimeUnit.SECONDS);
//
//        if (BuildConfig.DEBUG) {
//            HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor();
//            interceptor.setLevel(HttpLoggingInterceptor.Level.BASIC);
//            builder.addInterceptor(interceptor);
//        }
//
//        builder.addInterceptor(chain -> {
//            DownloadManager.Request request = chain.request().newBuilder().addHeader("key", "value").build();
//            return chain.proceed(request);
//        });
//
//        builder.addInterceptor(new UnauthorisedInterceptor(context));
//        OkHttpClient client = builder.build();

//        if (retrofit==null) {
        retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
//        }

        return retrofit;
    }

    public static OkHttpClient getHeader(final String authorizationValue ) {
        HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor();
        interceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
        OkHttpClient okClient = new OkHttpClient.Builder()
                .addInterceptor(interceptor)
                .addNetworkInterceptor(
                        new Interceptor() {
                            @Override
                            public okhttp3.Response intercept(Interceptor.Chain chain) throws IOException {
                                Request request = null;
                                if (authorizationValue != null) {
                                    Log.d("--Authorization-- ", authorizationValue);

                                    Request original = chain.request();
                                    // Request customization: add request headers
                                    Request.Builder requestBuilder = original.newBuilder()
                                            .addHeader("access-token", authorizationValue);

                                    request = requestBuilder.build();
                                }
                                return chain.proceed(request);
                            }
                        })
                .build();
        return okClient;

    }

    public  class myOkHttp extends OkHttpClient{

    }
}
