package com.example.MovieDB.presenter;

import android.util.Log;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.MovieDB.MovieApp;
import com.example.MovieDB.R;
import com.example.MovieDB.contract.SimilarMoviesContract;
import com.example.MovieDB.contract.SimilarSeriesContract;
import com.example.MovieDB.endpoints.EndPoints;
import com.example.MovieDB.model.movie.MovieResponse;
import com.example.MovieDB.model.movie.Movies;
import com.example.MovieDB.model.series.Series;
import com.example.MovieDB.model.series.SeriesResult;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.util.ArrayList;
import java.util.List;

public class SimilarPresenter {

    private SimilarMoviesContract movieContract;
    private SimilarSeriesContract seriesContract;
    private int id;
    private int page = 1;
    private List<Movies> moviesList = new ArrayList<>();
    private List<SeriesResult> seriesList = new ArrayList<>();

    public SimilarPresenter(SimilarMoviesContract contract) {
        this.movieContract = contract;
    }

    public SimilarPresenter(SimilarSeriesContract contract) {
        this.seriesContract = contract;
    }

    public void getMovieSimilars(int id, int page) {
        this.id = id;
        RequestQueue queue = Volley.newRequestQueue(MovieApp.getInstance().getApplicationContext());
        String url = EndPoints.MOVIE_BASE_URL + id + EndPoints.SIMILAR + EndPoints.API_KEY + EndPoints.PAGES + String.valueOf(page);
        Log.e("similar", url.toString());
        StringRequest request = new StringRequest(Request.Method.GET, url, response -> {
            Gson gson = new GsonBuilder()
                    .setLenient()
                    .create();
            MovieResponse movieResponse = gson.fromJson(response, MovieResponse.class);
            moviesList.addAll(movieResponse.getMovieList());
            movieContract.similarListener(moviesList);
        },
                error -> movieContract.internetConnectionError(R.drawable.baseline_wifi_off_black_36));
        queue.add(request);
    }

    public void getSeriesSimilars(int id, int page) {
        this.id = id;
        RequestQueue queue = Volley.newRequestQueue(MovieApp.getInstance().getApplicationContext());
        String url = EndPoints.SERIES_BASE_URL + id + EndPoints.SIMILAR + EndPoints.API_KEY + EndPoints.PAGES + String.valueOf(page);
        Log.e("similar", url.toString());
        StringRequest request = new StringRequest(Request.Method.GET, url, response -> {
            Gson gson = new GsonBuilder()
                    .setLenient()
                    .create();
            Series result = gson.fromJson(response, Series.class);
            seriesList.addAll(result.getResults());
            seriesContract.similarSeriesListener(seriesList);
        },
                error -> seriesContract.internetConnectionError(R.drawable.baseline_wifi_off_black_36));
        queue.add(request);
    }

    public void increasePages() {
        page++;
        getMovieSimilars(id, page);
    }

    public void increaseSeriesPages() {
        page++;
        getSeriesSimilars(id, page);
    }
}
