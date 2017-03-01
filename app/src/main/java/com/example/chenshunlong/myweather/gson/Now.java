package com.example.chenshunlong.myweather.gson;

import com.google.gson.annotations.SerializedName;

/**
 * Created by chenshunlong on 17-2-8.
 */

public class Now {
    @SerializedName("tmp")
    public String temperature;

    @SerializedName("cond")
    public More more;

    public class More{

        @SerializedName("txt")
        public String info;

    }
}
