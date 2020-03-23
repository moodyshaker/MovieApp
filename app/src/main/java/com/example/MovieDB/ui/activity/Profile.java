package com.example.MovieDB.ui.activity;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.TextView;

import androidx.appcompat.widget.Toolbar;

import com.example.MovieDB.R;

public class Profile extends NavigationViewActivity {

    Context context = this;
    FrameLayout frameLayout;
    TextView title;
    Toolbar toolbar;
    String headTitle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View contentView = inflater.inflate(R.layout.activity_profile, null, false);
        frameLayout = drawerLayout.findViewById(R.id.frame_layout_holder);
        toolbar = findViewById(R.id.toolbar);
        title = toolbar.findViewById(R.id.title);
        frameLayout.addView(contentView);
        headTitle = getIntent().getStringExtra("title");
        if (headTitle != null) {
            title.setText(headTitle);
        } else {
            title.setText(getResources().getString(R.string.now_playing));
        }
    }

}
