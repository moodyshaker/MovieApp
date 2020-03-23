package com.example.MovieDB.presenter;

import android.util.Log;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.MovieDB.MovieApp;
import com.example.MovieDB.R;
import com.example.MovieDB.contract.MovieContract;
import com.example.MovieDB.model.data.movie.MovieResponse;
import com.example.MovieDB.model.data.movie.Movies;
import com.example.MovieDB.endpoints.EndPoints;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.util.ArrayList;

public class MoviePresenter {
    public int type;
    private MovieContract contract;
    private ArrayList<Movies> movieList = new ArrayList<>();
    private int page = 1;

    public MoviePresenter(MovieContract contract) {
        this.contract = contract;
    }

    public void getMovie(int type, int page) {
        if (page == 1) {
            contract.showLoading();
        }
        this.page = page;
        this.type = type;
        String TYPE = "";
        switch (type) {
            case 1:
                TYPE = EndPoints.NOW_PLAYING;
                break;
            case 2:
                TYPE = EndPoints.POPULAR;
                break;
            case 3:
                TYPE = EndPoints.UPCOMING;
                break;
            case 4:
                TYPE = EndPoints.TOP_RATED;
                break;
        }
        RequestQueue queue = Volley.newRequestQueue(MovieApp.getInstance().getApplicationContext());
        String url = EndPoints.MOVIE_BASE_URL + TYPE + EndPoints.API_KEY + EndPoints.PAGES + String.valueOf(page);
        Log.e("url", url);
        StringRequest request = new StringRequest(Request.Method.GET, url, response -> {
            Gson gson = new GsonBuilder()
                    .setLenient()
                    .create();
            MovieResponse movieResponse = gson.fromJson(response, MovieResponse.class);
            if (page > 1) {
                movieList.addAll(movieResponse.getMovieList());
                contract.movieListener(movieList);
            } else {
                movieList.clear();
                movieList.addAll(movieResponse.getMovieList());
                contract.movieListener(movieList);
                contract.removeLoading();
            }
        },
                error -> contract.internetConnectionError(R.drawable.baseline_wifi_off_black_36));
        queue.add(request);
    }

    public void increasePages() {
        page++;
        getMovie(type, page);
    }
}
