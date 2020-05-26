package com.example.MovieDB;

import android.app.Application;
import android.content.Context;

import androidx.multidex.MultiDex;

public class MovieApp extends Application {
    private static MovieApp app;

    public static MovieApp getInstance() {
        return app;
    }

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        MultiDex.install(this);
    }

    @Override
    public void onCreate() {
        super.onCreate();
        app = this;
    }
}
