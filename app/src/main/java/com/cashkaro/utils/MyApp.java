package com.cashkaro.utils;

import android.app.Application;
import android.content.Context;

import com.facebook.stetho.Stetho;

public class MyApp extends Application {

    private static MyApp instance;

    public MyApp() {
        super();
        instance = this;
    }

    public static MyApp getApp() {
        return instance;
    }

    public static Context getContext() {
        return instance;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Stetho.initializeWithDefaults(this);
        Utils.loadFonts();
    }

    @Override
    public void onTerminate() {

        super.onTerminate();
    }

}
