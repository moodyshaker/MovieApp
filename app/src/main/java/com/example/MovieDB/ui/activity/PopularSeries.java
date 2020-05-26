package com.example.MovieDB.ui.activity;

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

public class PopularSeries extends Fragment implements SeriesContract {

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
    private SeriesPresenter seriesPresenter;
    private RecyclerView popularSeriesRecyclerView;
    private SearchAdapter<SeriesResult> seriesAdapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.popular_series, container, false);
        popularSeriesRecyclerView = v.findViewById(R.id.popular_series_recycler_view);
        refreshLayout = v.findViewById(R.id.swipe_refresh_layout);
        loading = v.findViewById(R.id.rotate_loading);
        popularSeriesRecyclerView.setLayoutManager(new GridLayoutManager(getActivity(), 2, GridLayoutManager.VERTICAL, false));
        seriesAdapter = new SearchAdapter<>(getActivity());
        popularSeriesRecyclerView.setAdapter(seriesAdapter);
        handler = new Handler();
        progressBar = v.findViewById(R.id.progress_bar);
        if ((pages <= lastPage)) {
            isLastPage = false;
        }
        seriesPresenter = new SeriesPresenter(this);
        seriesPresenter.getSeries(3, 1);
        popularSeriesRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
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
            popularSeriesRecyclerView.setVisibility(View.GONE);
            handler.postDelayed(() -> {
                seriesPresenter.getSeries(3, 1);
            }, 1500);
        });
        return v;
    }

    @Override
    public void internetConnectionError(int internetConnectionIcon) {
        Toast.makeText(getActivity(), "no internet connection", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void showLoading() {
        popularSeriesRecyclerView.setVisibility(View.GONE);
        progressBar.setVisibility(View.VISIBLE);
    }

    @Override
    public void removeLoading() {
        progressBar.setVisibility(View.GONE);
        popularSeriesRecyclerView.setVisibility(View.VISIBLE);
    }

    @Override
    public void seriesListener(List<SeriesResult> seriesList) {
        loading.setVisibility(View.GONE);
        loading.stop();
        refreshLayout.setRefreshing(false);
        popularSeriesRecyclerView.setVisibility(View.VISIBLE);
        seriesAdapter.setList(seriesList);
        seriesAdapter.notifyDataSetChanged();

    }
}
