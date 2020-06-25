package com.example.MovieDB.ui.fragment;

import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
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

public class PopularMovies extends Fragment implements MovieContract {

    private boolean isLoading = false;
    private int pages = 1;
    private RotateLoading loading;
    private ProgressBar progressBar;
    private int totalItem = 0;
    private int visibleItems = 0;
    private int currentItemPosition = 0;
    private int lastPage = 55;
    private boolean isLastPage = false;
    private SwipeRefreshLayout refreshLayout;
    private Handler handler;
    private MoviePresenter moviePresenter;
    private RecyclerView popularRecyclerView;
    private SearchAdapter<Movies> movieAdapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.popular_movies, container, false);
        popularRecyclerView = v.findViewById(R.id.popular_movies_recycler_view);
        refreshLayout = v.findViewById(R.id.swipe_refresh_layout);
        loading = v.findViewById(R.id.rotate_loading);
        popularRecyclerView.setLayoutManager(new GridLayoutManager(getActivity(), 2, GridLayoutManager.VERTICAL, false));
        movieAdapter = new SearchAdapter<>(getActivity());
        popularRecyclerView.setAdapter(movieAdapter);
        handler = new Handler();
        progressBar = v.findViewById(R.id.progress_bar);
        if ((pages <= lastPage)) {
            isLastPage = false;
        }
        moviePresenter = new MoviePresenter(this);
        moviePresenter.getMovie(2, 1);
        popularRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
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
            popularRecyclerView.setVisibility(View.GONE);
            handler.postDelayed(() -> {
                moviePresenter.getMovie(2, 1);
            }, 1500);
        });
        return v;
    }

    @Override
    public void internetConnectionError(int internetConnectionIcon) {
        Toast.makeText(getActivity(), "no internet connection", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void movieListener(List<Movies> Movies) {
        loading.setVisibility(View.GONE);
        loading.stop();
        refreshLayout.setRefreshing(false);
        popularRecyclerView.setVisibility(View.VISIBLE);
        movieAdapter.setList(Movies);
        movieAdapter.notifyDataSetChanged();
    }

    @Override
    public void showLoading() {
        popularRecyclerView.setVisibility(View.GONE);
        progressBar.setVisibility(View.VISIBLE);
    }

    @Override
    public void removeLoading() {
        progressBar.setVisibility(View.GONE);
        popularRecyclerView.setVisibility(View.VISIBLE);
    }
}
