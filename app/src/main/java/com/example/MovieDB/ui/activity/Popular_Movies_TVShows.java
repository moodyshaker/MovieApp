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
import com.google.android.material.tabs.TabLayout;

public class Popular_Movies_TVShows extends NavigationViewActivity implements TabLayout.OnTabSelectedListener {

    private Context context = this;
    private FrameLayout frameLayout, popularFrameLayout;
    private Toolbar toolbar;
    private TextView title;
    private ImageView searchIcon;
    private TabLayout popularTabLayout;
    private FragmentTransaction transaction;
    private FragmentManager manager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View contentView = inflater.inflate(R.layout.popular_movies_tv_shows, null, false);
        frameLayout = drawerLayout.findViewById(R.id.frame_layout_holder);
        popularTabLayout = contentView.findViewById(R.id.popular_tab_layout);
        popularFrameLayout = contentView.findViewById(R.id.popular_framelayout);
        toolbar = findViewById(R.id.toolbar);
        title = toolbar.findViewById(R.id.title);
        searchIcon = toolbar.findViewById(R.id.search_icon);
        frameLayout.addView(contentView);
        searchIcon.setVisibility(View.VISIBLE);
        searchIcon.setOnClickListener(click -> {
            Intent i = new Intent(context, Search.class);
            startActivity(i);
        });
        popularTabLayout.addTab(popularTabLayout.newTab().setText("Movies"));
        popularTabLayout.addTab(popularTabLayout.newTab().setText("Series"));
        TabLayout.Tab tab = popularTabLayout.getTabAt(0);
        if (tab != null) {
            title.setText(tab.getText());
            tab.select();
        }
        LinearLayout linearLayout = (LinearLayout) popularTabLayout.getChildAt(0);
        linearLayout.setShowDividers(LinearLayout.SHOW_DIVIDER_MIDDLE);
        GradientDrawable drawable = new GradientDrawable();
        drawable.setColor(getResources().getColor(R.color.white));
        drawable.setSize(2, 1);
        linearLayout.setDividerDrawable(drawable);
        manager = getSupportFragmentManager();
        transaction = manager.beginTransaction();
        PopularMovies popularMovies = new PopularMovies();
        transaction.add(R.id.popular_framelayout, popularMovies);
        transaction.commit();
        popularTabLayout.addOnTabSelectedListener(this);
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
                PopularMovies popularMovies = new PopularMovies();
                fragmentTransaction.add(R.id.popular_framelayout, popularMovies);
                fragmentTransaction.commit();
                break;
            case 1:
                title.setText(tab.getText());
                PopularSeries popularSeries = new PopularSeries();
                fragmentTransaction.add(R.id.popular_framelayout, popularSeries);
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