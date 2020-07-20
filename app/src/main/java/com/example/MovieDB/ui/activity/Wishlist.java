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
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.example.MovieDB.R;
import com.example.MovieDB.ui.fragment.WishlistMovies;
import com.example.MovieDB.ui.fragment.WishlistSeries;
import com.google.android.material.tabs.TabLayout;

public class Wishlist extends AppCompatActivity implements TabLayout.OnTabSelectedListener {

    private Context context = this;
    private FrameLayout frameLayout, innerFrameLayout;
    private TextView title;
    private Toolbar toolbar;
    private TabLayout wishListtabLayout;
    private ImageView searchIcon;
    private FragmentManager manager;
    private FragmentTransaction transaction;
    private ActionBar actionBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wishlist);
        toolbar = findViewById(R.id.toolbar);
        title = toolbar.findViewById(R.id.title);
        setSupportActionBar(toolbar);
        actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
        toolbar.getNavigationIcon().setColorFilter(getResources().getColor(R.color.white), PorterDuff.Mode.SRC_ATOP);
        searchIcon = toolbar.findViewById(R.id.search_icon);
        wishListtabLayout = findViewById(R.id.wishlist_tab_layout);
        innerFrameLayout = findViewById(R.id.wishList_framelayout);
        searchIcon.setVisibility(View.VISIBLE);
        searchIcon.setOnClickListener(click -> {
            Intent i = new Intent(context, Search.class);
            startActivity(i);
        });
        wishListtabLayout.addTab(wishListtabLayout.newTab().setText("Movies"));
        wishListtabLayout.addTab(wishListtabLayout.newTab().setText("Series"));
        TabLayout.Tab tab = wishListtabLayout.getTabAt(0);
        if (tab != null) {
            title.setText(tab.getText());
            tab.select();
        }
        LinearLayout linearLayout = (LinearLayout) wishListtabLayout.getChildAt(0);
        linearLayout.setShowDividers(LinearLayout.SHOW_DIVIDER_MIDDLE);
        GradientDrawable drawable = new GradientDrawable();
        drawable.setColor(getResources().getColor(R.color.white));
        drawable.setSize(2, 1);
        linearLayout.setDividerDrawable(drawable);
        manager = getSupportFragmentManager();
        transaction = manager.beginTransaction();
        WishlistMovies movies = new WishlistMovies();
        transaction.add(R.id.wishList_framelayout, movies);
        transaction.commit();
        wishListtabLayout.addOnTabSelectedListener(this);
    }

    @Override
    public void onTabSelected(TabLayout.Tab tab) {
        int position = tab.getPosition();
        FragmentManager manager = getSupportFragmentManager();
        FragmentTransaction transaction = manager.beginTransaction();
        switch (position) {
            case 0:
                title.setText(tab.getText());
                WishlistMovies movie = new WishlistMovies();
                transaction.replace(R.id.wishList_framelayout, movie);
                transaction.commit();
                break;
            case 1:
                title.setText(tab.getText());
                WishlistSeries series = new WishlistSeries();
                transaction.replace(R.id.wishList_framelayout, series);
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
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            onBackPressed();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }
}
