package com.cashkaro.utils;

import android.app.Application;
import android.content.Context;

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
        Utils.loadFonts();
    }

    @Override
    public void onTerminate() {

        super.onTerminate();
    }

}
