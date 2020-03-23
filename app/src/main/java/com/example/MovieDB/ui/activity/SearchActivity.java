package com.example.MovieDB.ui.activity;

import android.content.Context;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.os.Handler;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.example.MovieDB.R;
import com.example.MovieDB.contract.SearchContract;
import com.example.MovieDB.model.data.movie.Movies;
import com.example.MovieDB.presenter.MovieSearchPresenter;
import com.example.MovieDB.ui.adapter.MovieAdapter;
import com.victor.loading.rotate.RotateLoading;

import java.util.List;

public class SearchActivity extends AppCompatActivity implements SearchContract {
    private Context context = this;
    private Toolbar toolbar;
    private TextView title;
    private ActionBar actionBar;
    private RecyclerView searchRecyclerView;
    private SearchView searchView;
    private ImageView closeIcon;
    private MovieAdapter adapter;
    private MovieSearchPresenter presenter;
    private int visibleItems, totalItem, currentItemPosition, totalPagesCount;
    private boolean isLoading = false;
    private RotateLoading loading;
    private SwipeRefreshLayout refreshLayout;
    private Handler handler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        toolbar = findViewById(R.id.toolbar);
        title = toolbar.findViewById(R.id.title);
        searchView = toolbar.findViewById(R.id.search_view);
        searchRecyclerView = findViewById(R.id.search_recycler_view);
        loading = findViewById(R.id.rotate_loading);
        refreshLayout = findViewById(R.id.swipe_refresh_layout);
        title.setText("MovieDB");
        setSupportActionBar(toolbar);
        actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
        toolbar.getNavigationIcon().setColorFilter(getResources().getColor(R.color.blue_gray_100), PorterDuff.Mode.SRC_ATOP);
        searchView.setIconified(false);
        closeIcon = searchView.findViewById(androidx.appcompat.R.id.search_close_btn);
        closeIcon.setColorFilter(getResources().getColor(R.color.blue_gray_100));
        presenter = new MovieSearchPresenter(this);
        handler = new Handler();
        searchRecyclerView.setLayoutManager(new GridLayoutManager(context, 2));
        adapter = new MovieAdapter(context);
        searchRecyclerView.setAdapter(adapter);
        totalPagesCount = presenter.getTotalPage();
        refreshLayout.setOnRefreshListener(() -> {
            refreshLayout.setRefreshing(true);
            adapter.getList().clear();
            searchRecyclerView.setVisibility(View.GONE);
            handler.postDelayed(() -> {
                presenter.getMovieSearch(1, "");
                searchView.setQuery("", false);
                refreshLayout.setRefreshing(false);
            }, 1500);
        });
        searchRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
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
                            presenter.increasePages();
                        }, 2000);
                    }
                } else {
                    isLoading = false;
                }
            }
        });
        searchView.setOnSearchClickListener(v -> {
            title.setVisibility(View.GONE);
            searchView.requestFocus();
        });
        searchView.setOnCloseListener(() -> {
            title.setVisibility(View.VISIBLE);
            searchView.clearFocus();
            return false;
        });
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {

                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                if (adapter.getList() != null && adapter.getList().size() > 0) {
                    adapter.getList().clear();
                }
                presenter.getMovieSearch(1, newText);
                return false;
            }
        });
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
    public void MovieSearchList(List<Movies> movies) {
        loading.setVisibility(View.GONE);
        loading.stop();
        searchRecyclerView.setVisibility(View.VISIBLE);
        adapter.setList(movies);
        adapter.notifyDataSetChanged();
    }

    @Override
    public void showLoading() {

    }

    @Override
    public void removeLoading() {

    }

    @Override
    public void internetConnectionError(int internetConnectionIcon) {
    }
}
