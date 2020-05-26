package com.example.MovieDB.presenter;

import android.util.Log;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.MovieDB.MovieApp;
import com.example.MovieDB.R;
import com.example.MovieDB.contract.CreditContract;
import com.example.MovieDB.endpoints.EndPoints;
import com.example.MovieDB.model.credit_model.CreditsResponse;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public class MovieCreditsPresenter {

    private CreditContract creditContract;

    public MovieCreditsPresenter(CreditContract creditContract) {
        this.creditContract = creditContract;
    }

    public void getMovieCredits(int id) {
        String url = EndPoints.MOVIE_BASE_URL + id + EndPoints.CREDITS + EndPoints.API_KEY;
        Log.e("credit", url);
        StringRequest request = new StringRequest(Request.Method.GET, url, response -> {
            Gson gson = new GsonBuilder().setLenient().create();
            CreditsResponse creditsResponse = gson.fromJson(response, CreditsResponse.class);
            creditContract.crewListener(creditsResponse.getCrew());
            creditContract.castListener(creditsResponse.getCast());
        }, error -> creditContract.internetConnectionError(R.drawable.baseline_wifi_off_black_36));
        RequestQueue queue = Volley.newRequestQueue(MovieApp.getInstance().getApplicationContext());
        queue.add(request);
    }

    public void getSeriesCredits(int id) {
        String url = EndPoints.SERIES_BASE_URL + id + EndPoints.CREDITS + EndPoints.API_KEY;
        Log.e("credit", url);
        StringRequest request = new StringRequest(Request.Method.GET, url, response -> {
            Gson gson = new GsonBuilder().setLenient().create();
            CreditsResponse creditsResponse = gson.fromJson(response, CreditsResponse.class);
            creditContract.crewListener(creditsResponse.getCrew());
            creditContract.castListener(creditsResponse.getCast());
        }, error -> creditContract.internetConnectionError(R.drawable.baseline_wifi_off_black_36));
        RequestQueue queue = Volley.newRequestQueue(MovieApp.getInstance().getApplicationContext());
        queue.add(request);
    }

    public void getSeasonCredit(int id, int seasonNumber) {
        String url = EndPoints.SERIES_BASE_URL + id + EndPoints.SEASON + seasonNumber + EndPoints.CREDITS + EndPoints.API_KEY;
        Log.e("credit", url);
        StringRequest request = new StringRequest(Request.Method.GET, url, response -> {
            Gson gson = new GsonBuilder().setLenient().create();
            CreditsResponse creditsResponse = gson.fromJson(response, CreditsResponse.class);
            creditContract.crewListener(creditsResponse.getCrew());
            creditContract.castListener(creditsResponse.getCast());
        }, error -> creditContract.internetConnectionError(R.drawable.baseline_wifi_off_black_36));
        RequestQueue queue = Volley.newRequestQueue(MovieApp.getInstance().getApplicationContext());
        queue.add(request);
    }

    public void getEpisodeCredit(int id, int seasonNumber, int episodeNumber) {
        String url = EndPoints.SERIES_BASE_URL + id + EndPoints.SEASON + seasonNumber + EndPoints.EPISODE + episodeNumber + EndPoints.CREDITS + EndPoints.API_KEY;
        Log.e("credit", url);
        StringRequest request = new StringRequest(Request.Method.GET, url, response -> {
            Gson gson = new GsonBuilder().setLenient().create();
            CreditsResponse creditsResponse = gson.fromJson(response, CreditsResponse.class);
            creditContract.crewListener(creditsResponse.getCrew());
            creditContract.castListener(creditsResponse.getCast());
        }, error -> creditContract.internetConnectionError(R.drawable.baseline_wifi_off_black_36));
        RequestQueue queue = Volley.newRequestQueue(MovieApp.getInstance().getApplicationContext());
        queue.add(request);
    }
}
