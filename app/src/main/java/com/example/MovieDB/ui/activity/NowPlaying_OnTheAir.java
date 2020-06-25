package com.example.MovieDB.ui.activity;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.example.MovieDB.R;
import com.example.MovieDB.ui.fragment.AiringToday;
import com.example.MovieDB.ui.fragment.NowPlaying;
import com.example.MovieDB.ui.fragment.OnTheAir;
import com.google.android.material.tabs.TabLayout;

public class NowPlaying_OnTheAir extends NavigationViewActivity implements TabLayout.OnTabSelectedListener {

    private Context context = this;
    private FrameLayout frameLayout, nowPlayingFrameLayout;
    private Toolbar toolbar;
    private TextView title;
    private String head_title;
    private ImageView searchIcon;
    private TabLayout nowPlayingTabLayout;
    FragmentTransaction transaction;
    FragmentManager manager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View contentView = inflater.inflate(R.layout.now_playing_on_the_air, null, false);
        frameLayout = drawerLayout.findViewById(R.id.frame_layout_holder);
        nowPlayingTabLayout = contentView.findViewById(R.id.now_playing_tab_layout);
        nowPlayingFrameLayout = contentView.findViewById(R.id.now_playing_framelayout);
        toolbar = findViewById(R.id.toolbar);
        title = toolbar.findViewById(R.id.title);
        searchIcon = toolbar.findViewById(R.id.search_icon);
        frameLayout.addView(contentView);
        searchIcon.setVisibility(View.VISIBLE);
        searchIcon.setOnClickListener(click -> {
            Intent i = new Intent(context, Search.class);
            startActivity(i);
        });
        nowPlayingTabLayout.addTab(nowPlayingTabLayout.newTab().setText("Movies"));
        nowPlayingTabLayout.addTab(nowPlayingTabLayout.newTab().setText("On The Air"));
        nowPlayingTabLayout.addTab(nowPlayingTabLayout.newTab().setText("Airing Today"));
        TabLayout.Tab tab = nowPlayingTabLayout.getTabAt(0);
        if (tab != null) {
            title.setText(tab.getText());
            tab.select();
        }
        LinearLayout linearLayout = (LinearLayout) nowPlayingTabLayout.getChildAt(0);
        linearLayout.setShowDividers(LinearLayout.SHOW_DIVIDER_MIDDLE);
        GradientDrawable drawable = new GradientDrawable();
        drawable.setColor(getResources().getColor(R.color.white));
        drawable.setSize(2, 1);
        linearLayout.setDividerDrawable(drawable);
        manager = getSupportFragmentManager();
        transaction = manager.beginTransaction();
        NowPlaying nowPlaying = new NowPlaying();
        transaction.add(R.id.now_playing_framelayout, nowPlaying);
        transaction.commit();
        nowPlayingTabLayout.addOnTabSelectedListener(this);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawers();
            return true;
        }
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            exitDialog();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    private void exitDialog() {
        AlertDialog.Builder dialog = new AlertDialog.Builder(context);
        dialog.setTitle("Exit");
        dialog.setIcon(R.drawable.movie_icon);
        dialog.setMessage("Do you want to exit ?");
        dialog.setPositiveButton("Yes", (dialog1, which) -> {
            finishAffinity();
        });
        dialog.setNegativeButton("No", (dialog1, which) -> {

        });
        dialog.setCancelable(false);
        dialog.show();
    }

    @Override
    public void onTabSelected(TabLayout.Tab tab) {
        int position = tab.getPosition();
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        switch (position) {
            case 0:
                title.setText(tab.getText());
                NowPlaying nowPlaying = new NowPlaying();
                fragmentTransaction.add(R.id.now_playing_framelayout, nowPlaying);
                fragmentTransaction.commit();
                break;
            case 1:
                title.setText(tab.getText());
                OnTheAir onTheAir = new OnTheAir();
                fragmentTransaction.add(R.id.now_playing_framelayout, onTheAir);
                fragmentTransaction.commit();
                break;
            case 2:
                title.setText(tab.getText());
                AiringToday airingToday = new AiringToday();
                fragmentTransaction.add(R.id.now_playing_framelayout, airingToday);
                fragmentTransaction.commit();
                break;
        }
    }

    @Override
    public void onTabUnselected(TabLayout.Tab tab) {

    }

    @Override
    public void onTabReselected(TabLayout.Tab tab) {

    }
}