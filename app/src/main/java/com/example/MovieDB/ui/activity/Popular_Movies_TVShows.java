package com.example.MovieDB.ui.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.drawable.GradientDrawable;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.os.Handler;
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
import com.example.MovieDB.receivers.NetworkReceiver;
import com.example.MovieDB.ui.fragment.PopularMovies;
import com.example.MovieDB.ui.fragment.PopularSeries;
import com.example.MovieDB.utils.Utils;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.tabs.TabLayout;

public class Popular_Movies_TVShows extends NavigationViewActivity implements TabLayout.OnTabSelectedListener, NetworkReceiver.NetworkCallbackListener {

    private Context context = this;
    private Activity activity = this;
    private FrameLayout frameLayout, popularFrameLayout;
    private Toolbar toolbar;
    private TextView title;
    private ImageView searchIcon;
    private TabLayout popularTabLayout;
    private FragmentTransaction transaction;
    private FragmentManager manager;
    private NetworkReceiver receiver;
    private IntentFilter filter;
    private LinearLayout connectedContainer, disconnectedContainer;
    private BottomSheetDialog connectionDialog;
    private Handler h;

    @Override
    protected void onStart() {
        super.onStart();
        registerReceiver(receiver, filter);
    }

    @Override
    protected void onStop() {
        super.onStop();
        unregisterReceiver(receiver);
    }

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
        connectionDialog = Utils.showDisconnectionDialog(context);
        connectedContainer = connectionDialog.findViewById(R.id.connected_container);
        disconnectedContainer = connectionDialog.findViewById(R.id.disconnected_container);
        h = new Handler();
        frameLayout.addView(contentView);
        searchIcon.setVisibility(View.VISIBLE);
        filter = new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION);
        receiver = new NetworkReceiver();
        receiver.setListener(this);
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
        } else if (keyCode == KeyEvent.KEYCODE_BACK) {
            Utils.goActivity(activity, NowPlaying_OnTheAir.class);
            return true;
        }
        return super.onKeyDown(keyCode, event);
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

    @Override
    public void callbackListener(boolean isConnected) {
        if (isConnected) {
            connectedContainer.setVisibility(View.VISIBLE);
            disconnectedContainer.setVisibility(View.GONE);
            h.postDelayed(() -> connectionDialog.dismiss(), 1000);
        } else {
            connectedContainer.setVisibility(View.GONE);
            disconnectedContainer.setVisibility(View.VISIBLE);
            connectionDialog.show();
        }
    }
}