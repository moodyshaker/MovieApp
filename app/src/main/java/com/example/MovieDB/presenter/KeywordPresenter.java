package com.example.MovieDB.presenter;

import android.util.Log;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.MovieDB.MovieApp;
import com.example.MovieDB.R;
import com.example.MovieDB.contract.MovieKeywordContract;
import com.example.MovieDB.contract.SeriesKeywordContract;
import com.example.MovieDB.endpoints.EndPoints;
import com.example.MovieDB.model.keywords.Keywords;
import com.example.MovieDB.model.keywords.SeriesKeyword;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public class KeywordPresenter {

    private MovieKeywordContract movieKeywordContract;
    private SeriesKeywordContract seriesKeywordContract;

    public KeywordPresenter(MovieKeywordContract movieKeywordContract) {
        this.movieKeywordContract = movieKeywordContract;
    }

    public KeywordPresenter(SeriesKeywordContract seriesKeywordContract) {
        this.seriesKeywordContract = seriesKeywordContract;
    }

    public void getMovieKeyword(int id) {
        String url = EndPoints.MOVIE_BASE_URL + id + EndPoints.KEYWORDS + EndPoints.API_KEY;
        StringRequest request = new StringRequest(Request.Method.GET, url, response -> {
            Gson gson = new GsonBuilder()
                    .setLenient()
                    .create();
            Keywords keywords = gson.fromJson(response, Keywords.class);
            movieKeywordContract.keywordListener(keywords.getKeywords());
        }, error -> movieKeywordContract.internetConnectionError(R.drawable.baseline_wifi_off_black_36));
        RequestQueue queue = Volley.newRequestQueue(MovieApp.getInstance().getApplicationContext());
        queue.add(request);
    }

    public void getSeriesKeyword(int id) {
        String url = EndPoints.SERIES_BASE_URL + id + EndPoints.KEYWORDS + EndPoints.API_KEY;
        StringRequest request = new StringRequest(Request.Method.GET, url, response -> {
            Gson gson = new GsonBuilder()
                    .setLenient()
                    .create();
            SeriesKeyword keywords = gson.fromJson(response, SeriesKeyword.class);
            seriesKeywordContract.keywordListener(keywords.getResults());
        }, error -> seriesKeywordContract.internetConnectionError(R.drawable.baseline_wifi_off_black_36));
        RequestQueue queue = Volley.newRequestQueue(MovieApp.getInstance().getApplicationContext());
        queue.add(request);
    }
}
