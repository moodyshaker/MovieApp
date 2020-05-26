package com.example.MovieDB.presenter;

import android.util.Log;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.MovieDB.MovieApp;
import com.example.MovieDB.R;
import com.example.MovieDB.contract.TrailerContract;
import com.example.MovieDB.endpoints.EndPoints;
import com.example.MovieDB.model.movie_trailer.Trailers;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public class TrailerPresenter {

    private TrailerContract contract;

    public TrailerPresenter(TrailerContract contract) {
        this.contract = contract;
    }

    public void getMovieTrailers(int movieId) {
        String url = EndPoints.MOVIE_BASE_URL + movieId + EndPoints.VIDEOS + EndPoints.API_KEY;
        Log.e("trailer", url.toString());
        StringRequest request = new StringRequest(Request.Method.GET, url, response -> {
            Gson gson = new GsonBuilder().setLenient().create();
            Trailers trailers = gson.fromJson(response, Trailers.class);
            contract.trailers(trailers.getResults());
        }, error -> contract.internetConnectionError(R.drawable.baseline_wifi_off_black_36));
        RequestQueue queue = Volley.newRequestQueue(MovieApp.getInstance().getApplicationContext());
        queue.add(request);
    }

    public void getSeriesTrailers(int seriesId) {
        String url = EndPoints.SERIES_BASE_URL + seriesId + EndPoints.VIDEOS + EndPoints.API_KEY;
        Log.e("trailer", url.toString());
        StringRequest request = new StringRequest(Request.Method.GET, url, response -> {
            Gson gson = new GsonBuilder().setLenient().create();
            Trailers trailers = gson.fromJson(response, Trailers.class);
            contract.trailers(trailers.getResults());
        }, error -> contract.internetConnectionError(R.drawable.baseline_wifi_off_black_36));
        RequestQueue queue = Volley.newRequestQueue(MovieApp.getInstance().getApplicationContext());
        queue.add(request);
    }

    public void getSeasonTrailers(int id, int seasonNumber) {
        String url = EndPoints.SERIES_BASE_URL + id + EndPoints.SEASON + seasonNumber + EndPoints.VIDEOS + EndPoints.API_KEY;
        Log.e("trailer", url.toString());
        StringRequest request = new StringRequest(Request.Method.GET, url, response -> {
            Gson gson = new GsonBuilder()
                    .setLenient()
                    .create();
            Trailers trailers = gson.fromJson(response, Trailers.class);
            contract.trailers(trailers.getResults());
        }, error -> contract.internetConnectionError(R.drawable.baseline_wifi_off_black_36));

        RequestQueue queue = Volley.newRequestQueue(MovieApp.getInstance().getApplicationContext());
        queue.add(request);
    }

    public void getEpisodeTrailers(int id, int seasonNumber, int episodeNumber) {
        String url = EndPoints.SERIES_BASE_URL + id + EndPoints.SEASON + seasonNumber + EndPoints.EPISODE + episodeNumber + EndPoints.VIDEOS + EndPoints.API_KEY;
        Log.e("trailer", url.toString());
        StringRequest request = new StringRequest(Request.Method.GET, url, response -> {
            Gson gson = new GsonBuilder()
                    .setLenient()
                    .create();
            Trailers trailers = gson.fromJson(response, Trailers.class);
            contract.trailers(trailers.getResults());
        }, error -> contract.internetConnectionError(R.drawable.baseline_wifi_off_black_36));

        RequestQueue queue = Volley.newRequestQueue(MovieApp.getInstance().getApplicationContext());
        queue.add(request);
    }
}
