package com.example.MovieDB.presenter;

import android.util.Log;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.MovieDB.MovieApp;
import com.example.MovieDB.contract.MovieByKeywordContract;
import com.example.MovieDB.endpoints.EndPoints;
import com.example.MovieDB.model.movie.MovieResponse;
import com.example.MovieDB.model.movie.Movies;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.util.ArrayList;
import java.util.List;

public class MovieByKeywordPresenter {
    private MovieByKeywordContract contract;
    private int id;
    private int page = 1;
    private List<Movies> movies = new ArrayList<>();

    public MovieByKeywordPresenter(MovieByKeywordContract contract) {
        this.contract = contract;
    }

    public void getMoviesByKeyword(int id, int page) {
        this.id = id;
        this.page = page;
        String url = EndPoints.KEYWORD_BASE_URL + id + EndPoints.KEYWORD_MOVIES + EndPoints.API_KEY + EndPoints.PAGES + String.valueOf(page);
        Log.e("search", url.toString());
        StringRequest request = new StringRequest(Request.Method.GET, url, response -> {
            Gson gson = new GsonBuilder()
                    .setLenient()
                    .create();
            MovieResponse movieResponse = gson.fromJson(response, MovieResponse.class);
            movies.addAll(movieResponse.getMovieList());
            contract.MovieByKeyword(movies, movieResponse.getTotalPages(), page);
        }, error -> {

        });
        RequestQueue queue = Volley.newRequestQueue(MovieApp.getInstance().getApplicationContext());
        queue.add(request);
    }

    public void increasePage() {
        page++;
        Log.e("search", page + "");
        getMoviesByKeyword(id, page);
    }
}
