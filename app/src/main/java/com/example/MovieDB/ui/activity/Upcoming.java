package com.example.MovieDB.ui.activity;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.example.MovieDB.R;
import com.example.MovieDB.contract.MovieContract;
import com.example.MovieDB.model.movie.Movies;
import com.example.MovieDB.presenter.MoviePresenter;
import com.example.MovieDB.ui.adapter.SearchAdapter;
import com.victor.loading.rotate.RotateLoading;

import java.util.List;

public class Upcoming extends NavigationViewActivity implements MovieContract {


    private View loadingLayout;
    private boolean isLoading = false;
    private int pages = 1;
    private RotateLoading loading;
    private ProgressBar progressBar;
    private int totalItem = 0;
    private int visibleItems = 0;
    private int currentItemPosition = 0;
    private int lastPage = 55;
    private boolean isLastPage = false;
    private Context context = this;
    private Handler handler;
    private MoviePresenter moviePresenter;
    private RecyclerView upcomingRecyclerView;
    private SearchAdapter<Movies> movieAdapter;
    private FrameLayout frameLayout;
    private Toolbar toolbar;
    private TextView title;
    private String head_title;
    private SwipeRefreshLayout refreshLayout;
    private ImageView searchIcon;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View contentView = inflater.inflate(R.layout.upcoming, null, false);
        frameLayout = drawerLayout.findViewById(R.id.frame_layout_holder);
        upcomingRecyclerView = contentView.findViewById(R.id.upcoming_recycler_view);
        refreshLayout = contentView.findViewById(R.id.swipe_refresh_layout);
        loading = contentView.findViewById(R.id.rotate_loading);
        toolbar = findViewById(R.id.toolbar);
        title = toolbar.findViewById(R.id.title);
        searchIcon = toolbar.findViewById(R.id.search_icon);
        frameLayout.addView(contentView);
        head_title = getIntent().getStringExtra("title");
        if (head_title != null) {
            title.setText(head_title);
        } else {
            title.setText(getResources().getString(R.string.upcoming));
        }
        searchIcon.setVisibility(View.VISIBLE);
        upcomingRecyclerView.setLayoutManager(new GridLayoutManager(context, 2, GridLayoutManager.VERTICAL, false));
        movieAdapter = new SearchAdapter<>(context);
        upcomingRecyclerView.setAdapter(movieAdapter);
        handler = new Handler();
        progressBar = findViewById(R.id.progress_bar);
        if ((pages <= lastPage)) {
            isLastPage = false;
        }
        searchIcon.setOnClickListener(click -> {
            Intent i = new Intent(context, Search.class);
            startActivity(i);
        });
        moviePresenter = new MoviePresenter(this);
        moviePresenter.getMovie(3, 1);
        upcomingRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                GridLayoutManager manager = (GridLayoutManager) recyclerView.getLayoutManager();
                visibleItems = manager.getChildCount();
                totalItem = manager.getItemCount();
                currentItemPosition = manager.findFirstVisibleItemPosition();
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
        refreshLayout.setOnRefreshListener(() -> {
            refreshLayout.setRefreshing(true);
            movieAdapter.getList().clear();
            upcomingRecyclerView.setVisibility(View.GONE);
            handler.postDelayed(() -> {
                moviePresenter.getMovie(3, 1);
            }, 1500);
        });
    }

    @Override
    public void internetConnectionError(int internetConnectionIcon) {
        Toast.makeText(context, "no internet connection", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void movieListener(List<Movies> Movies) {
        loading.setVisibility(View.GONE);
        loading.stop();
        refreshLayout.setRefreshing(false);
        upcomingRecyclerView.setVisibility(View.VISIBLE);
        movieAdapter.setList(Movies);
        movieAdapter.notifyDataSetChanged();
    }

    @Override
    public void showLoading() {
        upcomingRecyclerView.setVisibility(View.GONE);
        progressBar.setVisibility(View.VISIBLE);
    }

    @Override
    public void removeLoading() {
        progressBar.setVisibility(View.GONE);
        upcomingRecyclerView.setVisibility(View.VISIBLE);
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