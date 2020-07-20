package com.example.MovieDB.presenter;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.MovieDB.MovieApp;
import com.example.MovieDB.R;
import com.example.MovieDB.contract.SeasonContract;
import com.example.MovieDB.endpoints.EndPoints;
import com.example.MovieDB.model.series_seasons.SeasonDetailsModel;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public class SeasonPresenter {
    private SeasonContract contract;

    public SeasonPresenter(SeasonContract contract) {
        this.contract = contract;
    }

    public void getSeason(int id, int seasonNumber) {
        String url = EndPoints.SERIES_BASE_URL + id + EndPoints.SEASON + seasonNumber + "?" + EndPoints.API_KEY;
        StringRequest request = new StringRequest(Request.Method.GET, url, response -> {
            Gson gson = new GsonBuilder().setLenient().create();
            SeasonDetailsModel season = gson.fromJson(response, SeasonDetailsModel.class);
            contract.seasonListener(season);
        }, error -> {
            contract.internetConnectionError(R.drawable.baseline_wifi_off_black_36);
        });
        RequestQueue queue = Volley.newRequestQueue(MovieApp.getInstance().getApplicationContext());
        queue.add(request);
    }
}
