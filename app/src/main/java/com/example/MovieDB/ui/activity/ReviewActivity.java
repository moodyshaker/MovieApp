package com.example.MovieDB.ui.activity;

import android.content.Context;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.MovieDB.R;
import com.example.MovieDB.contract.ReviewContract;
import com.example.MovieDB.model.data.reviews.Reviews;
import com.example.MovieDB.presenter.MovieReviewPresenter;
import com.example.MovieDB.ui.adapter.ReviewAdapter;

import java.util.List;

public class ReviewActivity extends NavigationViewActivity implements ReviewContract {

    private Context context = this;
    private TextView title;
    private Toolbar toolbar;
    private MovieReviewPresenter reviewPresenter;
    private ReviewAdapter adapter;
    private RecyclerView recyclerView;
    private ActionBar actionBar;
    private int id;
    private String movieTitle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_review);
        toolbar = findViewById(R.id.toolbar);
        title = toolbar.findViewById(R.id.title);
        recyclerView = findViewById(R.id.review_recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(context, RecyclerView.VERTICAL, false));
        setSupportActionBar(toolbar);
        actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
        toolbar.getNavigationIcon().setColorFilter(getResources().getColor(R.color.blue_gray_100), PorterDuff.Mode.SRC_ATOP);
        id = getIntent().getIntExtra("movie_id", 0);
        movieTitle = getIntent().getStringExtra("movie_title");
        title.setText(getResources().getString(R.string.reviews, movieTitle));
        reviewPresenter = new MovieReviewPresenter(this);
        reviewPresenter.getReviews(id);
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
    public void reviewListener(List<Reviews> reviews) {
        adapter = new ReviewAdapter(context, reviews);
        recyclerView.setAdapter(adapter);
    }

    @Override
    public void reviewResultListener(Integer totalReviews) {

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
