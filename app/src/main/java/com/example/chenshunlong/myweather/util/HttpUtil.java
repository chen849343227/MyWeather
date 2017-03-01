package com.example.chenshunlong.myweather.util;

import okhttp3.OkHttpClient;
import okhttp3.Request;

/**
 * Created by chenshunlong on 17-2-8.
 */

public class HttpUtil {

    public static void sendOkHttpRequest(String address, okhttp3.Callback callback){
        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder().url(address).build();
        client.newCall(request).enqueue(callback);
    }
}
