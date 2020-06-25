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
import com.example.MovieDB.contract.SeriesContract;
import com.example.MovieDB.model.series.SeriesResult;
import com.example.MovieDB.presenter.SeriesPresenter;
import com.example.MovieDB.ui.adapter.SearchAdapter;
import com.victor.loading.rotate.RotateLoading;

import java.util.List;

public class OnTheAir extends Fragment implements SeriesContract {


    private RecyclerView onTheAirRecyclerView;
    private SearchAdapter<SeriesResult> seriesAdapter;
    private boolean isLoading = false;
    private int pages = 1;
    private RotateLoading loading;
    private ProgressBar progressBar;
    private int totalItem = 0;
    private int visibleItems = 0;
    private int currentItemPosition = 0;
    private int lastPage = 55;
    private boolean isLastPage = false;
    private Handler handler;
    private SeriesPresenter seriesPresenter;
    private SwipeRefreshLayout refreshLayout;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.on_the_air, container, false);
        onTheAirRecyclerView = v.findViewById(R.id.on_the_air_recycler_view);
        refreshLayout = v.findViewById(R.id.swipe_refresh_layout);
        loading = v.findViewById(R.id.rotate_loading);
        onTheAirRecyclerView.setLayoutManager(new GridLayoutManager(getActivity(), 2, GridLayoutManager.VERTICAL, false));
        seriesAdapter = new SearchAdapter<>(getActivity());
        onTheAirRecyclerView.setAdapter(seriesAdapter);
        handler = new Handler();
        progressBar = v.findViewById(R.id.progress_bar);
        if ((pages <= lastPage)) {
            isLastPage = false;
        }
        seriesPresenter = new SeriesPresenter(this);
        seriesPresenter.getSeries(2, 1);
        onTheAirRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
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
                            seriesPresenter.increasePages();
                        }, 2000);
                    }
                } else {
                    isLoading = false;
                }
            }
        });
        refreshLayout.setOnRefreshListener(() -> {
            refreshLayout.setRefreshing(true);
            seriesAdapter.getList().clear();
            onTheAirRecyclerView.setVisibility(View.GONE);
            handler.postDelayed(() -> {
                seriesPresenter.getSeries(2, 1);
            }, 1500);
        });
        return v;
    }

    @Override
    public void seriesListener(List<SeriesResult> seriesList) {
        loading.setVisibility(View.GONE);
        loading.stop();
        refreshLayout.setRefreshing(false);
        onTheAirRecyclerView.setVisibility(View.VISIBLE);
        seriesAdapter.setList(seriesList);
        seriesAdapter.notifyDataSetChanged();
    }

    @Override
    public void showLoading() {
        onTheAirRecyclerView.setVisibility(View.GONE);
        progressBar.setVisibility(View.VISIBLE);
    }

    @Override
    public void removeLoading() {
        progressBar.setVisibility(View.GONE);
        onTheAirRecyclerView.setVisibility(View.VISIBLE);
    }

    @Override
    public void internetConnectionError(int internetConnectionIcon) {
        Toast.makeText(getActivity(), "no internet connection", Toast.LENGTH_SHORT).show();
    }
}
