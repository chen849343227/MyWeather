package com.example.chenshunlong.myweather.util;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;

import com.squareup.leakcanary.LeakCanary;

import org.litepal.LitePalApplication;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by chenshunlong on 16-12-13.
 */

public class MyApplication extends LitePalApplication {

    public static final String APP_KEY="23567754";

    public static final String APP_SECRET="d02bd556928889a88b811aa28a9ec5c7";

    public static final String URL = "http://gw.api.taobao.com/router/rest";

    private static Context mContext;//全局的context

    public static SharedPreferences sharedPreferences;

    private List<Activity>  activityList = new ArrayList<>();

    @Override
    public void onCreate() {
        super.onCreate();
        LeakCanary.install(this);
        mContext=getApplicationContext();
        //sharedPreferences = getSharedPreferences("info",MODE_PRIVATE);
    }

    public static Context getContext(){
        return mContext;
    }


    /**
     * 添加Activity
     * @param activity
     */
    public void addActivity(Activity activity){
        activityList.add(activity);
    }

    /**
     * 结束单一的Activity
     * @param activity
     */
    public void finishSingle(Activity activity){
        activityList.remove(activity);
        if(!activity.isFinishing()){
            activity.finish();
        }
    }

    //结束所有的Activity
    public void finishAll(){
        for(Activity activity:activityList){
            if(!activity.isFinishing()){
                activity.finish();
            }
        }
    }



}
