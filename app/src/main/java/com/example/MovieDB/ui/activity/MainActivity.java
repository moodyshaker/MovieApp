package com.example.MovieDB.ui.activity;

import android.app.AlertDialog;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.view.GravityCompat;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.MovieDB.R;
import com.example.MovieDB.contract.MovieContract;
import com.example.MovieDB.data.movie.Movies;
import com.example.MovieDB.presenter.MoviePresenter;
import com.example.MovieDB.ui.adapter.MovieAdapter;
import com.google.android.material.navigation.NavigationView;
import com.victor.loading.rotate.RotateLoading;

import java.util.List;

public class MainActivity extends NavigationViewActivity implements MovieContract, NavigationView.OnNavigationItemSelectedListener {

    RecyclerView movieRecyclerView;
    View loadingLayout;
    MovieAdapter movieAdapter;
    boolean isLoading = false;
    int pages = 1;
    RotateLoading loading;
    ProgressBar progressBar;
    int totalItem = 0;
    int visibleItems = 0;
    int currentItemPosition = 0;
    int lastPage = 55;
    boolean isLastPage = false;
    Context context = this;
    Handler handler;
    MoviePresenter moviePresenter;
    FrameLayout frameLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View contentView = inflater.inflate(R.layout.activity_main, null, false);
        frameLayout = drawerLayout.findViewById(R.id.frame_layout_holder);
        movieRecyclerView = contentView.findViewById(R.id.movie_recycler_view);
        loading = contentView.findViewById(R.id.rotate_loading);
        navigationView.setNavigationItemSelectedListener(this);
        frameLayout.addView(contentView);
        movieRecyclerView.setLayoutManager(new GridLayoutManager(context, 2, GridLayoutManager.VERTICAL, false));
        movieAdapter = new MovieAdapter(context);
        movieRecyclerView.setAdapter(movieAdapter);
        handler = new Handler();
        progressBar = findViewById(R.id.progress_bar);
        if ((pages <= lastPage)) {
            isLastPage = false;
        }
        moviePresenter = new MoviePresenter(this);
        moviePresenter.getMovie(1, 1);
        movieRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                GridLayoutManager manager = (GridLayoutManager) recyclerView.getLayoutManager();
                visibleItems = manager.getChildCount();
                totalItem = manager.getItemCount();
                currentItemPosition = manager.findFirstVisibleItemPosition();
//                Log.e("123", "visibleItems:- " + String.valueOf(visibleItems) + " currentItemPosition:- " + String.valueOf(currentItemPosition) + " totalItems:- " + String.valueOf(totalItem));
                if ((visibleItems + currentItemPosition) >= totalItem) {
                    if (!isLoading) {
                        isLoading = true;
                        loading.setVisibility(View.VISIBLE);
                        loading.start();
                        handler.postDelayed(() -> {
                            moviePresenter.increasePages();
                        }, 2000);
                    }
                } else {
                    isLoading = false;
                }
            }
        });
        choiceSelection();
    }

    private void choiceSelection() {
        switch (moviePresenter.type) {
            case 1:
                navigationView.setCheckedItem(R.id.now_playing);
                title.setText(R.string.now_playing);
                break;
            case 2:
                navigationView.setCheckedItem(R.id.popular);
                title.setText(R.string.popular);
                break;
            case 3:
                navigationView.setCheckedItem(R.id.upcoming);
                title.setText(R.string.upcoming);
                break;
            case 4:
                navigationView.setCheckedItem(R.id.top_rated);
                title.setText(R.string.top_rated);
                break;
        }
    }

    @Override
    public void internetConnectionError(int internetConnectionIcon) {
        Toast.makeText(context, "no internet connection", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void movieListener(List<Movies> Movies) {
        loading.setVisibility(View.GONE);
        loading.stop();
        movieAdapter.setList(Movies);
        movieAdapter.notifyDataSetChanged();
    }

    @Override
    public void showLoading() {
        movieRecyclerView.setVisibility(View.GONE);
        progressBar.setVisibility(View.VISIBLE);
    }

    @Override
    public void removeLoading() {
        progressBar.setVisibility(View.GONE);
        movieRecyclerView.setVisibility(View.VISIBLE);
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
        int id = menuItem.getItemId();
        switch (id) {
            case R.id.now_playing:
                moviePresenter.getMovie(1, 1);
                break;
            case R.id.popular:
                moviePresenter.getMovie(2, 1);
                break;
            case R.id.upcoming:
                moviePresenter.getMovie(3, 1);
                break;
            case R.id.top_rated:
                moviePresenter.getMovie(4, 1);
                break;
        }
        menuItem.setChecked(true);
        title.setText(menuItem.getTitle());
        drawerLayout.closeDrawers();
        return true;
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
}
