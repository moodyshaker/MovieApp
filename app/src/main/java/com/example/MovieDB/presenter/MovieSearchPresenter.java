package com.example.MovieDB.presenter;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.MovieDB.MovieApp;
import com.example.MovieDB.R;
import com.example.MovieDB.contract.SearchContract;
import com.example.MovieDB.endpoints.EndPoints;
import com.example.MovieDB.model.credit_search.CreditResult;
import com.example.MovieDB.model.credit_search.CreditSearch;
import com.example.MovieDB.model.keywords_from_search.KeywordResult;
import com.example.MovieDB.model.keywords_from_search.SearchKeywords;
import com.example.MovieDB.model.movie.MovieResponse;
import com.example.MovieDB.model.movie.Movies;
import com.example.MovieDB.model.series.Series;
import com.example.MovieDB.model.series.SeriesResult;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.util.ArrayList;

public class MovieSearchPresenter {

    private SearchContract contract;
    private int pages;
    private String query = "";
    private String year;
    private ArrayList<Movies> movieList = new ArrayList<>();
    private ArrayList<CreditResult> creditList = new ArrayList<>();
    private ArrayList<SeriesResult> seriesList = new ArrayList<>();
    private ArrayList<KeywordResult> keywordList = new ArrayList<>();
    private String TYPE = EndPoints.SEARCH_MOVIE;
    private int searchType = 0;

    public MovieSearchPresenter(SearchContract contract) {
        this.contract = contract;
    }

    public void getMovieSearch(int pages, String query, String year, int searchType) {
        this.searchType = searchType;
        this.query = query;
        this.pages = pages;
        this.year = year;
        switch (searchType) {
            case 0:
                TYPE = EndPoints.SEARCH_MOVIE;
                break;
            case 1:
                TYPE = EndPoints.SEARCH_PERSON;
                break;
            case 2:
                TYPE = EndPoints.SEARCH_TV_SHOWS;
                break;
            case 3:
                TYPE = EndPoints.SEARCH_KEYWORD;
                break;
        }
        String url = EndPoints.SEARCH_BASE_URL + TYPE + EndPoints.API_KEY + EndPoints.PAGES + String.valueOf(pages) + EndPoints.YEAR + year + EndPoints.QUERY + query;
        StringRequest request = new StringRequest(Request.Method.GET, url, response -> {
            Gson gson = new GsonBuilder()
                    .setLenient()
                    .create();
            switch (searchType) {
                case 0:
                    MovieResponse movieResponse = gson.fromJson(response, MovieResponse.class);
                    movieList.addAll(movieResponse.getMovieList());
                    contract.MovieSearchList(movieList, movieResponse.getTotalPages(), pages);
                    break;
                case 1:
                    CreditSearch creditSearch = gson.fromJson(response, CreditSearch.class);
                    creditList.addAll(creditSearch.getResults());
                    contract.CreditSearchList(creditList, creditSearch.getTotalPages(), pages);
                    break;
                case 2:
                    Series series = gson.fromJson(response, Series.class);
                    seriesList.addAll(series.getResults());
                    contract.SeriesSearchList(seriesList, series.getTotalPages(), pages);
                    break;
                case 3:
                    SearchKeywords keywords = gson.fromJson(response, SearchKeywords.class);
                    keywordList.addAll(keywords.getResults());
                    contract.KeywordSearchList(keywordList, keywords.getTotalPages(), pages);
                    break;
            }

        }, error -> contract.internetConnectionError(R.drawable.baseline_wifi_off_black_36));
        RequestQueue queue = Volley.newRequestQueue(MovieApp.getInstance().getApplicationContext());
        queue.add(request);
    }

    public String getQuery() {
        return query;
    }

    public void increasePages() {
        pages++;
        getMovieSearch(pages, query, year, searchType);
    }
}
