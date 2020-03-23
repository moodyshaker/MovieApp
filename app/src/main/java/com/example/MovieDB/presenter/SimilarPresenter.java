package com.example.MovieDB.presenter;

import android.util.Log;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.MovieDB.MovieApp;
import com.example.MovieDB.R;
import com.example.MovieDB.contract.SimilarContract;
import com.example.MovieDB.model.data.movie.MovieResponse;
import com.example.MovieDB.model.data.movie.Movies;
import com.example.MovieDB.endpoints.EndPoints;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.util.ArrayList;
import java.util.List;

public class SimilarPresenter {

    private SimilarContract contract;
    private int id;
    private int page = 1;
    private List<Movies> moviesList = new ArrayList<>();

    public SimilarPresenter(SimilarContract contract) {
        this.contract = contract;
    }

    public void getSimilars(int id, int page) {
        this.id = id;
        RequestQueue queue = Volley.newRequestQueue(MovieApp.getInstance().getApplicationContext());
        String url = EndPoints.MOVIE_BASE_URL + id + EndPoints.SIMILAR + EndPoints.API_KEY + EndPoints.PAGES + String.valueOf(page);
        Log.e("123", url.toString());
        StringRequest request = new StringRequest(Request.Method.GET, url, response -> {
            Gson gson = new GsonBuilder()
                    .setLenient()
                    .create();
            MovieResponse movieResponse = gson.fromJson(response, MovieResponse.class);
            moviesList.addAll(movieResponse.getMovieList());
            contract.similarListener(moviesList);
        },
                error -> contract.internetConnectionError(R.drawable.baseline_wifi_off_black_36));
        queue.add(request);
    }

    public void increasePages() {
        page++;
        getSimilars(id, page);
    }
}
