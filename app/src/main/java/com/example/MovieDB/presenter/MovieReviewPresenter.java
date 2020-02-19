package com.example.MovieDB.presenter;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.MovieDB.MovieApp;
import com.example.MovieDB.R;
import com.example.MovieDB.contract.ReviewContract;
import com.example.MovieDB.data.reviews.ReviewResponse;
import com.example.MovieDB.endpoints.EndPoints;
import com.google.gson.Gson;

public class MovieReviewPresenter {

    private ReviewContract contract;

    public MovieReviewPresenter(ReviewContract contract) {
        this.contract = contract;
    }

    public void getReviews(int id) {
        String url = EndPoints.MOVIE_BASE_URL + id + EndPoints.REVIEWS + EndPoints.API_KEY;
        StringRequest request = new StringRequest(Request.Method.GET, url, response -> {
            Gson gson = new Gson();
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
