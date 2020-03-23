package com.example.MovieDB.ui.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.MovieDB.R;

public class Splash extends AppCompatActivity {
    Handler handler;
    Animation animation;
    ImageView logo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        logo = findViewById(R.id.app_logo);
        handler = new Handler();
        handler.postDelayed(() -> {
            Intent i = new Intent(this, NowPlaying.class);
            startActivity(i);
            finish();
        }, 2000);

        animation = AnimationUtils.loadAnimation(this, R.anim.animation_splash);
        logo.startAnimation(animation);
    }
}
