package com.example.MovieDB.ui.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.drawerlayout.widget.DrawerLayout;

import com.example.MovieDB.R;
import com.example.MovieDB.persistance.sharedpreferences.MovieSharedPreference;
import com.google.android.material.navigation.NavigationView;
import com.squareup.picasso.Picasso;

public class NavigationViewActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private Activity activity = this;
    private Context context = this;
    protected DrawerLayout drawerLayout;
    private NavigationView navigationView;
    private ActionBarDrawerToggle toggle;
    private Toolbar toolbar;
    private TextView title;
    private Intent i;
        private View headerView;
    private TextView username;
    private ImageView userIcon;
    private MovieSharedPreference.UserPreferences userPreferences;

    @Override

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_navigation_view);
        drawerLayout = findViewById(R.id.movie_drawer);
        navigationView = findViewById(R.id.navigation_movie_view);
        toolbar = findViewById(R.id.toolbar);
        title = toolbar.findViewById(R.id.title);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
        toggle = new ActionBarDrawerToggle(activity, drawerLayout, toolbar, R.string.drawer_open, R.string.drawer_close);
        drawerLayout.addDrawerListener(toggle);
        toggle.setDrawerIndicatorEnabled(true);
        toggle.syncState();
        navigationView.setNavigationItemSelectedListener(this);
        userPreferences = MovieSharedPreference.UserPreferences.getUserPreference(context);
        headerView = navigationView.getHeaderView(0);
        username = headerView.findViewById(R.id.username);
        userIcon = headerView.findViewById(R.id.user_icon);
        if (!userPreferences.getID().isEmpty()) {
            Picasso.get().load(userPreferences.getImage()).into(userIcon);
            username.setText(userPreferences.getUsername());
        }
        headerView.setOnClickListener(click -> {
            Intent i = new Intent(context, Profile.class);
            startActivity(i);
        });
        setNavigationItemChecked();
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (toggle.onOptionsItemSelected(item)) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
        int id = menuItem.getItemId();
        switch (id) {
            case R.id.now_playing:
                i = new Intent(context, NowPlaying_OnTheAir.class);
                i.putExtra("title", menuItem.getTitle());
                startActivity(i);
                break;
            case R.id.popular:
                i = new Intent(context, Popular_Movies_TVShows.class);
                i.putExtra("title", menuItem.getTitle());
                startActivity(i);
                break;
            case R.id.upcoming:
                i = new Intent(context, Upcoming.class);
                i.putExtra("title", menuItem.getTitle());
                startActivity(i);
                break;
            case R.id.top_rated:
                i = new Intent(context, TopRated_Movies_TVShows.class);
                i.putExtra("title", menuItem.getTitle());
                startActivity(i);
                break;
            case R.id.about_app:
                i = new Intent(context, AboutApp.class);
                i.putExtra("title", menuItem.getTitle());
                startActivity(i);
                break;
            case R.id.near_by:
                i = new Intent(context, NearBy.class);
                i.putExtra("title", menuItem.getTitle());
                startActivity(i);
                break;
            case R.id.settings:
                i = new Intent(context, Settings.class);
                i.putExtra("title", menuItem.getTitle());
                startActivity(i);
                break;
        }
        menuItem.setChecked(true);
        title.setText(menuItem.getTitle());
        drawerLayout.closeDrawers();
        return true;
    }

    private void setNavigationItemChecked() {
        if (context.getClass().equals(NowPlaying_OnTheAir.class)) {
            navigationView.setCheckedItem(R.id.now_playing);
        } else if (context.getClass().equals(Popular_Movies_TVShows.class)) {
            navigationView.setCheckedItem(R.id.popular);
        } else if (context.getClass().equals(Upcoming.class)) {
            navigationView.setCheckedItem(R.id.upcoming);
        } else if (context.getClass().equals(TopRated_Movies_TVShows.class)) {
            navigationView.setCheckedItem(R.id.top_rated);
        } else if (context.getClass().equals(AboutApp.class)) {
            navigationView.setCheckedItem(R.id.about_app);
        } else if (context.getClass().equals(NearBy.class)) {
            navigationView.setCheckedItem(R.id.near_by);
        }else if (context.getClass().equals(Settings.class)) {
            navigationView.setCheckedItem(R.id.settings);
        }
    }
}