package com.example.MovieDB.presenter;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.MovieDB.MovieApp;
import com.example.MovieDB.R;
import com.example.MovieDB.contract.SearchContract;
import com.example.MovieDB.endpoints.EndPoints;
import com.example.MovieDB.model.data.movie.MovieResponse;
import com.example.MovieDB.model.data.movie.Movies;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.util.ArrayList;

public class MovieSearchPresenter {

    private SearchContract contract;
    private int pages = 1;
    private String query = "";
    private ArrayList<Movies> movieList = new ArrayList<>();
    private int totalPage;

    public MovieSearchPresenter(SearchContract contract) {
        this.contract = contract;
    }

    public void getMovieSearch(int pages, String query) {
        this.query = query;
        String url = EndPoints.SEARCH_BASE_URL + EndPoints.API_KEY + EndPoints.PAGES + String.valueOf(pages) + EndPoints.QUERY + query;
        StringRequest request = new StringRequest(Request.Method.GET, url, response -> {
            Gson gson = new GsonBuilder()
                    .setLenient()
                    .create();
            MovieResponse movieResponse = gson.fromJson(response, MovieResponse.class);
            totalPage = movieResponse.getPage();
            movieList.addAll(movieResponse.getMovieList());
            contract.MovieSearchList(movieList);
        }, error -> contract.internetConnectionError(R.drawable.baseline_wifi_off_black_36));
        RequestQueue queue = Volley.newRequestQueue(MovieApp.getInstance().getApplicationContext());
        queue.add(request);
    }

    public int getTotalPage() {
        return totalPage;
    }

    public void increasePages() {
        pages++;
        getMovieSearch(pages, query);
    }
}
