package com.example.chenshunlong.myweather.gson;

import com.google.gson.annotations.SerializedName;

/**
 * Created by chenshunlong on 17-2-8.
 */

public class AQI {
    public AQICity city;

    public class AQICity {
        public String aqi;
        public String pm25;

        @SerializedName("qlty")
        public String airQuality;
    }
}
