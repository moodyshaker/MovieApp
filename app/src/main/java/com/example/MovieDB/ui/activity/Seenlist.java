package com.example.MovieDB.ui.activity;

import android.content.Context;
import android.content.Intent;
import android.graphics.PorterDuff;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.example.MovieDB.R;
import com.example.MovieDB.ui.fragment.SeenlistMovie;
import com.example.MovieDB.ui.fragment.SeenlistSeries;
import com.google.android.material.tabs.TabLayout;

public class Seenlist extends NavigationViewActivity implements TabLayout.OnTabSelectedListener {

    private Context context = this;
    private FrameLayout innerFrameLayout;
    private TextView title;
    private Toolbar toolbar;
    private ImageView searchIcon;
    private TabLayout seenlistTablayout;
    private FragmentManager manager;
    private FragmentTransaction transaction;
    private ActionBar actionBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_seenlist);
        toolbar = findViewById(R.id.toolbar);
        title = toolbar.findViewById(R.id.title);
        setSupportActionBar(toolbar);
        actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
        toolbar.getNavigationIcon().setColorFilter(getResources().getColor(R.color.white), PorterDuff.Mode.SRC_ATOP);
        searchIcon = toolbar.findViewById(R.id.search_icon);
        seenlistTablayout = findViewById(R.id.seenlist_tab_layout);
        innerFrameLayout = findViewById(R.id.seenlist_framelayout);
        searchIcon.setVisibility(View.VISIBLE);
        searchIcon.setOnClickListener(click -> {
            Intent i = new Intent(context, Search.class);
            startActivity(i);
        });
        seenlistTablayout.addTab(seenlistTablayout.newTab().setText("Movies"));
        seenlistTablayout.addTab(seenlistTablayout.newTab().setText("Series"));
        TabLayout.Tab tab = seenlistTablayout.getTabAt(0);
        if (tab != null) {
            title.setText(tab.getText());
            tab.select();
        }
        LinearLayout linearLayout = (LinearLayout) seenlistTablayout.getChildAt(0);
        linearLayout.setShowDividers(LinearLayout.SHOW_DIVIDER_MIDDLE);
        GradientDrawable drawable = new GradientDrawable();
        drawable.setColor(getResources().getColor(R.color.white));
        drawable.setSize(2, 1);
        linearLayout.setDividerDrawable(drawable);
        manager = getSupportFragmentManager();
        transaction = manager.beginTransaction();
        SeenlistMovie movies = new SeenlistMovie();
        transaction.add(R.id.seenlist_framelayout, movies);
        transaction.commit();
        seenlistTablayout.addOnTabSelectedListener(this);
    }

    @Override
    public void onTabSelected(TabLayout.Tab tab) {
        int position = tab.getPosition();
        FragmentManager manager = getSupportFragmentManager();
        FragmentTransaction transaction = manager.beginTransaction();
        switch (position) {
            case 0:
                title.setText(tab.getText());
                SeenlistMovie movie = new SeenlistMovie();
                transaction.replace(R.id.seenlist_framelayout, movie);
                transaction.commit();
                break;
            case 1:
                title.setText(tab.getText());
                SeenlistSeries series = new SeenlistSeries();
                transaction.replace(R.id.seenlist_framelayout, series);
                transaction.commit();
                break;
        }
    }

    @Override
    public void onTabUnselected(TabLayout.Tab tab) {

    }

    @Override
    public void onTabReselected(TabLayout.Tab tab) {

    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawers();
            return true;
        }
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            onBackPressed();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }
}
