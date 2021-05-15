package com.example.MovieDB.presenter;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.MovieDB.MovieApp;
import com.example.MovieDB.contract.MovieSeriesImageContract;
import com.example.MovieDB.contract.PersonImageContract;
import com.example.MovieDB.endpoints.EndPoints;
import com.example.MovieDB.model.images_models.EpisodeImage;
import com.example.MovieDB.model.images_models.MovieImage;
import com.example.MovieDB.model.images_models.PersonImage;
import com.example.MovieDB.model.images_models.SeriesImage;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public class ImagesPresenter {

    private PersonImageContract personImageContract;
    private MovieSeriesImageContract movieSeriesImageContract;

    public ImagesPresenter(PersonImageContract personImageContract) {
        this.personImageContract = personImageContract;
    }

    public ImagesPresenter(MovieSeriesImageContract movieSeriesImageContract) {
        this.movieSeriesImageContract = movieSeriesImageContract;
    }

    public void getPersonImages(int id) {
        String url = EndPoints.PERSON_BASE_URL + id + EndPoints.IMAGES + EndPoints.API_KEY;
        StringRequest request = new StringRequest(Request.Method.GET, url, response -> {
            Gson gson = new GsonBuilder()
                    .setLenient()
                    .create();
            PersonImage personImage = gson.fromJson(response, PersonImage.class);
            personImageContract.personImageListener(personImage.getMovieImageDetails());
        }, error -> {});

        RequestQueue queue = Volley.newRequestQueue(MovieApp.getInstance().getApplicationContext());
        queue.add(request);
    }

    public void getMovieImages(int id) {
        String url = EndPoints.MOVIE_BASE_URL + id + EndPoints.IMAGES + EndPoints.API_KEY;
        StringRequest request = new StringRequest(Request.Method.GET, url, response -> {
            Gson gson = new GsonBuilder()
                    .setLenient()
                    .create();
            MovieImage movieImage = gson.fromJson(response, MovieImage.class);
            movieSeriesImageContract.movieSeriesImageListener(movieImage.getStills());
        }, error -> {});

        RequestQueue queue = Volley.newRequestQueue(MovieApp.getInstance().getApplicationContext());
        queue.add(request);
    }

    public void getSeriesImages(int id) {
        String url = EndPoints.SERIES_BASE_URL + id + EndPoints.IMAGES + EndPoints.API_KEY;
        StringRequest request = new StringRequest(Request.Method.GET, url, response -> {
            Gson gson = new GsonBuilder()
                    .setLenient()
                    .create();
            SeriesImage seriesImage = gson.fromJson(response, SeriesImage.class);
            movieSeriesImageContract.movieSeriesImageListener(seriesImage.getStills());
        }, error -> {});

        RequestQueue queue = Volley.newRequestQueue(MovieApp.getInstance().getApplicationContext());
        queue.add(request);
    }

    public void getSeasonImages(int id, int seasonNumber) {
        String url = EndPoints.SERIES_BASE_URL + id + EndPoints.SEASON + seasonNumber + EndPoints.IMAGES + EndPoints.API_KEY;
        StringRequest request = new StringRequest(Request.Method.GET, url, response -> {
            Gson gson = new GsonBuilder()
                    .setLenient()
                    .create();
            SeriesImage seriesImage = gson.fromJson(response, SeriesImage.class);
            movieSeriesImageContract.movieSeriesImageListener(seriesImage.getStills());
        }, error -> {});

        RequestQueue queue = Volley.newRequestQueue(MovieApp.getInstance().getApplicationContext());
        queue.add(request);
    }

    public void getEpisodeImages(int id, int seasonNumber, int episodeNumber) {
        String url = EndPoints.SERIES_BASE_URL + id + EndPoints.SEASON + seasonNumber + EndPoints.EPISODE + episodeNumber + EndPoints.IMAGES + EndPoints.API_KEY;
        StringRequest request = new StringRequest(Request.Method.GET, url, response -> {
            Gson gson = new GsonBuilder()
                    .setLenient()
                    .create();
            EpisodeImage episodeImage = gson.fromJson(response, EpisodeImage.class);
            movieSeriesImageContract.movieSeriesImageListener(episodeImage.getStills());
        }, error -> {});

        RequestQueue queue = Volley.newRequestQueue(MovieApp.getInstance().getApplicationContext());
        queue.add(request);
    }
}
