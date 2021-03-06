package com.example.MovieDB.presenter;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.MovieDB.MovieApp;
import com.example.MovieDB.contract.EpisodeContract;
import com.example.MovieDB.endpoints.EndPoints;
import com.example.MovieDB.model.series_episodes.SeriesEpisodeDetails;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public class EpisodePresenter {

    private EpisodeContract contract;

    public EpisodePresenter(EpisodeContract contract) {
        this.contract = contract;
    }

    public void getEpisode(int id, int seasonNumber, int episodeNumber) {
        String url = EndPoints.SERIES_BASE_URL + id + EndPoints.SEASON + seasonNumber + EndPoints.EPISODE + episodeNumber + "?" + EndPoints.API_KEY;
        StringRequest request = new StringRequest(Request.Method.GET, url, response -> {
            Gson gson = new GsonBuilder().setLenient().create();
            SeriesEpisodeDetails episode = gson.fromJson(response, SeriesEpisodeDetails.class);
            contract.episodeListener(episode);
        }, error -> {});
        RequestQueue queue = Volley.newRequestQueue(MovieApp.getInstance().getApplicationContext());
        queue.add(request);
    }
}
