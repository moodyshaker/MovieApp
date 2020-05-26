package com.example.MovieDB.presenter;

import android.util.Log;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.MovieDB.MovieApp;
import com.example.MovieDB.R;
import com.example.MovieDB.contract.RecommendationsMoviesContract;
import com.example.MovieDB.contract.RecommendationsSeriesContract;
import com.example.MovieDB.model.movie.MovieResponse;
import com.example.MovieDB.model.movie.Movies;
import com.example.MovieDB.endpoints.EndPoints;
import com.example.MovieDB.model.series.Series;
import com.example.MovieDB.model.series.SeriesResult;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.util.ArrayList;

public class RecommendationsPresenter {

    private RecommendationsMoviesContract movieContract;
    private RecommendationsSeriesContract seriesContract;
    private ArrayList<Movies> movieList = new ArrayList<>();
    private ArrayList<SeriesResult> seriesList = new ArrayList<>();
    private int page = 1;
    private int id;

    public RecommendationsPresenter(RecommendationsMoviesContract movieContract) {
        this.movieContract = movieContract;
    }

    public RecommendationsPresenter(RecommendationsSeriesContract seriesContract) {
        this.seriesContract = seriesContract;
    }

    public void getMovieRecommendations(int id, int page) {
        this.id = id;
        RequestQueue queue = Volley.newRequestQueue(MovieApp.getInstance().getApplicationContext());
        String url = EndPoints.MOVIE_BASE_URL + id + EndPoints.RECOMMENDATIONS + EndPoints.API_KEY + EndPoints.PAGES + String.valueOf(page);
        Log.e("recommendation", url.toString());
        StringRequest request = new StringRequest(Request.Method.GET, url, response -> {
            Gson gson = new GsonBuilder()
                    .setLenient()
                    .create();
            MovieResponse movieResponse = gson.fromJson(response, MovieResponse.class);
            movieList.addAll(movieResponse.getMovieList());
            movieContract.recommendationsListener(movieList);
        },
                error -> movieContract.internetConnectionError(R.drawable.baseline_wifi_off_black_36));
        queue.add(request);
    }

    public void getSeriesRecommendations(int id, int page) {
        this.id = id;
        RequestQueue queue = Volley.newRequestQueue(MovieApp.getInstance().getApplicationContext());
        String url = EndPoints.SERIES_BASE_URL + id + EndPoints.RECOMMENDATIONS + EndPoints.API_KEY + EndPoints.PAGES + String.valueOf(page);
        Log.e("recommendation", url.toString());
        StringRequest request = new StringRequest(Request.Method.GET, url, response -> {
            Gson gson = new GsonBuilder()
                    .setLenient()
                    .create();
            Series series = gson.fromJson(response, Series.class);
            seriesList.addAll(series.getResults());
            seriesContract.RecommendationsSeriesListener(seriesList);
        },
                error -> movieContract.internetConnectionError(R.drawable.baseline_wifi_off_black_36));
        queue.add(request);
    }

    public void increasePages(){
        page++;
        getMovieRecommendations(id, page);
    }

    public void increaseSeriesPages(){
        page++;
        getSeriesRecommendations(id, page);
    }
}
