package com.example.MovieDB.ui.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.MovieDB.R;
import com.example.MovieDB.persistance.sharedpreferences.MovieSharedPreference;

public class Splash extends AppCompatActivity {
    private Context context = this;
    private Activity activity = this;
    private Handler handler;
    private Animation animation;
    private ImageView logo;
    private MovieSharedPreference.UserPreferences userPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        logo = findViewById(R.id.app_logo);
        userPreferences = MovieSharedPreference.UserPreferences.getUserPreference(context);
        handler = new Handler();
        handler.postDelayed(() -> {
            if (userPreferences.isFirstTime()) {
                Intent i = new Intent(this, LoginActivty.class);
                startActivity(i);
            } else {
                Intent i = new Intent(this, NowPlaying_OnTheAir.class);
                startActivity(i);
            } finish();
        }, 2000);

        animation = AnimationUtils.loadAnimation(this, R.anim.animation_splash);
        logo.startAnimation(animation);
    }
}
