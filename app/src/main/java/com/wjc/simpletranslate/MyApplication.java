package com.wjc.simpletranslate;

import android.annotation.SuppressLint;
import android.app.Application;
import android.content.Context;

import org.litepal.LitePalApplication;

/**
 * Created by Administrator on 2017/5/1.
 */
public class MyApplication extends Application {
    private static Context context;

    @SuppressLint("MissingSuperCall")
    @Override
    public void onCreate() {
        context=getApplicationContext();
        LitePalApplication.initialize(context);
    }

    public static  Context getContext(){
        return context;
    }
}
