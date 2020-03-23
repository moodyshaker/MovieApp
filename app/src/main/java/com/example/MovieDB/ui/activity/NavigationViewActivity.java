package com.example.MovieDB.ui.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.drawerlayout.widget.DrawerLayout;

import com.example.MovieDB.R;
import com.google.android.material.navigation.NavigationView;

public class NavigationViewActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private Activity activity = this;
    private Context context = this;
    protected DrawerLayout drawerLayout;
    private NavigationView navigationView;
    private ActionBarDrawerToggle toggle;
    private Toolbar toolbar;
    private TextView title;
    private Intent i;

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
                i = new Intent(context, NowPlaying.class);
                i.putExtra("title", menuItem.getTitle());
                startActivity(i);
                break;
            case R.id.popular:
                i = new Intent(context, Popular.class);
                i.putExtra("title", menuItem.getTitle());
                startActivity(i);
                break;
            case R.id.upcoming:
                i = new Intent(context, Upcoming.class);
                i.putExtra("title", menuItem.getTitle());
                startActivity(i);
                break;
            case R.id.top_rated:
                i = new Intent(context, TopRated.class);
                i.putExtra("title", menuItem.getTitle());
                startActivity(i);
                break;
            case R.id.about_app:
                i = new Intent(context, AboutApp.class);
                i.putExtra("title", menuItem.getTitle());
                startActivity(i);
                break;
            case R.id.profile:
                i = new Intent(context, Profile.class);
                i.putExtra("title", menuItem.getTitle());
                startActivity(i);
                break;
            case R.id.near_by:
                i = new Intent(context, NearBy.class);
                i.putExtra("title", menuItem.getTitle());
                startActivity(i);
                break;
            case R.id.wishList:
                i = new Intent(context, Wishlist.class);
                i.putExtra("title", menuItem.getTitle());
                startActivity(i);
                break;
            case R.id.seenlist:
                i = new Intent(context, Seenlist.class);
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
        if (context.getClass().equals(NowPlaying.class)) {
            navigationView.setCheckedItem(R.id.now_playing);
            title.setText("Hello");
        } else if (context.getClass().equals(Popular.class)) {
            navigationView.setCheckedItem(R.id.popular);
        } else if (context.getClass().equals(Upcoming.class)) {
            navigationView.setCheckedItem(R.id.upcoming);
        } else if (context.getClass().equals(TopRated.class)) {
            navigationView.setCheckedItem(R.id.top_rated);
        } else if (context.getClass().equals(Profile.class)) {
            navigationView.setCheckedItem(R.id.profile);
        } else if (context.getClass().equals(AboutApp.class)) {
            navigationView.setCheckedItem(R.id.about_app);
        } else if (context.getClass().equals(NearBy.class)) {
            navigationView.setCheckedItem(R.id.near_by);
        } else if (context.getClass().equals(Wishlist.class)) {
            navigationView.setCheckedItem(R.id.wishList);
        }else if (context.getClass().equals(Seenlist.class)) {
            navigationView.setCheckedItem(R.id.seenlist);
        }
    }

}
