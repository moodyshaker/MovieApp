package com.example.MovieDB;

import android.app.Application;

public class MovieApp extends Application {
    private static MovieApp app;
    public static MovieApp getInstance(){
        return app;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        app = this;
    }
}
