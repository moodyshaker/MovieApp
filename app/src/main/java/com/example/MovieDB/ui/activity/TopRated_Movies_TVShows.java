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
import com.example.MovieDB.ui.fragment.TopRatedMovies;
import com.example.MovieDB.ui.fragment.TopRatedSeries;
import com.google.android.material.tabs.TabLayout;

public class TopRated_Movies_TVShows extends NavigationViewActivity implements TabLayout.OnTabSelectedListener {

    private Context context = this;
    private FrameLayout frameLayout, topRatedFrameLayout;
    private Toolbar toolbar;
    private TextView title;
    private String head_title;
    private ImageView searchIcon;
    private TabLayout topRatedTabLayout;
    private FragmentTransaction transaction;
    private FragmentManager manager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View contentView = inflater.inflate(R.layout.top_rated_movies_series, null, false);
        frameLayout = drawerLayout.findViewById(R.id.frame_layout_holder);
        toolbar = findViewById(R.id.toolbar);
        topRatedTabLayout = contentView.findViewById(R.id.top_rated_tab_layout);
        topRatedFrameLayout = contentView.findViewById(R.id.top_rated_framelayout);
        title = toolbar.findViewById(R.id.title);
        searchIcon = toolbar.findViewById(R.id.search_icon);
        frameLayout.addView(contentView);
        head_title = getIntent().getStringExtra("title");
        if (head_title != null) {
            title.setText(head_title);
        } else {
            title.setText(getResources().getString(R.string.top_rated));
        }
        searchIcon.setVisibility(View.VISIBLE);
        searchIcon.setOnClickListener(click -> {
            Intent i = new Intent(context, Search.class);
            startActivity(i);
        });
        topRatedTabLayout.addTab(topRatedTabLayout.newTab().setText("Movies"));
        topRatedTabLayout.addTab(topRatedTabLayout.newTab().setText("Series"));
        TabLayout.Tab tab = topRatedTabLayout.getTabAt(0);
        if (tab != null) {
            title.setText(tab.getText());
            tab.select();
        }
        LinearLayout linearLayout = (LinearLayout) topRatedTabLayout.getChildAt(0);
        linearLayout.setShowDividers(LinearLayout.SHOW_DIVIDER_MIDDLE);
        GradientDrawable drawable = new GradientDrawable();
        drawable.setColor(getResources().getColor(R.color.white));
        drawable.setSize(2, 1);
        linearLayout.setDividerDrawable(drawable);
        manager = getSupportFragmentManager();
        transaction = manager.beginTransaction();
        TopRatedMovies topRatedMovies = new TopRatedMovies();
        transaction.add(R.id.top_rated_framelayout, topRatedMovies);
        transaction.commit();
        topRatedTabLayout.addOnTabSelectedListener(this);
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
                TopRatedMovies topRatedMovies = new TopRatedMovies();
                fragmentTransaction.add(R.id.top_rated_framelayout, topRatedMovies);
                fragmentTransaction.commit();
                break;
            case 1:
                title.setText(tab.getText());
                TopRatedSeries topRatedSeries = new TopRatedSeries();
                fragmentTransaction.add(R.id.top_rated_framelayout, topRatedSeries);
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