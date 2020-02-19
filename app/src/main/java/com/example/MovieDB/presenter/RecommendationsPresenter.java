package com.example.MovieDB.presenter;

import android.util.Log;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.MovieDB.MovieApp;
import com.example.MovieDB.R;
import com.example.MovieDB.contract.RecommendationsContract;
import com.example.MovieDB.data.movie.MovieResponse;
import com.example.MovieDB.data.movie.Movies;
import com.example.MovieDB.endpoints.EndPoints;
import com.google.gson.Gson;

import java.util.ArrayList;

public class RecommendationsPresenter {

    private RecommendationsContract contract;
    private ArrayList<Movies> movieList = new ArrayList<>();
    private int page = 1;
    private int id;

    public RecommendationsPresenter(RecommendationsContract contract) {
        this.contract = contract;
    }

    public void getRecommendations(int id, int page) {
        this.id = id;
        RequestQueue queue = Volley.newRequestQueue(MovieApp.getInstance().getApplicationContext());
        String url = EndPoints.MOVIE_BASE_URL + id + EndPoints.RECOMMENDATIONS + EndPoints.API_KEY + EndPoints.PAGES + String.valueOf(page);
        Log.e("123", url.toString());
        StringRequest request = new StringRequest(Request.Method.GET, url, response -> {
            Gson gson = new Gson();
            MovieResponse movieResponse = gson.fromJson(response, MovieResponse.class);
            movieList.addAll(movieResponse.getMovieList());
            contract.recommendationsListener(movieList);
        },
                error -> contract.internetConnectionError(R.drawable.baseline_wifi_off_black_36));
        queue.add(request);
    }

    public void increasePages(){
        page++;
        getRecommendations(id, page);
    }
}
