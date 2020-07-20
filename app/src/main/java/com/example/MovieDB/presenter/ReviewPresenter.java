package com.example.MovieDB.presenter;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.MovieDB.MovieApp;
import com.example.MovieDB.R;
import com.example.MovieDB.contract.ReviewContract;
import com.example.MovieDB.endpoints.EndPoints;
import com.example.MovieDB.model.reviews.ReviewResponse;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public class ReviewPresenter {

    private ReviewContract contract;

    public ReviewPresenter(ReviewContract contract) {
        this.contract = contract;
    }

    public void getMovieReviews(int id) {
        String url = EndPoints.MOVIE_BASE_URL + id + EndPoints.REVIEWS + EndPoints.API_KEY;
        StringRequest request = new StringRequest(Request.Method.GET, url, response -> {
            Gson gson = new GsonBuilder()
                    .setLenient()
                    .create();
            ReviewResponse reviewResponse = gson.fromJson(response, ReviewResponse.class);
            contract.reviewListener(reviewResponse.getReview());
            contract.reviewResultListener(reviewResponse.getTotalResults());
        }, error -> {
            contract.internetConnectionError(R.drawable.baseline_wifi_off_black_36);
        });
        RequestQueue queue = Volley.newRequestQueue(MovieApp.getInstance().getApplicationContext());
        queue.add(request);
    }

    public void getSeriesReviews(int id) {
        String url = EndPoints.SERIES_BASE_URL + id + EndPoints.REVIEWS + EndPoints.API_KEY;
        StringRequest request = new StringRequest(Request.Method.GET, url, response -> {
            Gson gson = new GsonBuilder()
                    .setLenient()
                    .create();
            ReviewResponse reviewResponse = gson.fromJson(response, ReviewResponse.class);
            contract.reviewListener(reviewResponse.getReview());
            contract.reviewResultListener(reviewResponse.getTotalResults());
        }, error -> {
            contract.internetConnectionError(R.drawable.baseline_wifi_off_black_36);
        });
        RequestQueue queue = Volley.newRequestQueue(MovieApp.getInstance().getApplicationContext());
        queue.add(request);
    }
}
