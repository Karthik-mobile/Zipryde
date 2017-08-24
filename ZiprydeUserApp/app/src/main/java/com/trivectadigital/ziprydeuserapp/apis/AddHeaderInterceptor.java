package com.trivectadigital.ziprydeuserapp.apis;

import java.io.IOException;

import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

/**
 * Created by naveendevaraj on 8/23/17.
 */

public class AddHeaderInterceptor implements Interceptor {

    String accesstoken;
    AddHeaderInterceptor(String token){
        accesstoken = token;

    }
    @Override
    public Response intercept(Chain chain) throws IOException {

        Request.Builder builder = chain.request().newBuilder();
        builder.addHeader("access-token", accesstoken);

        return chain.proceed(builder.build());
    }
}
