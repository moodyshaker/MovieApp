package com.example.MovieDB.presenter;

import android.util.Log;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.MovieDB.MovieApp;
import com.example.MovieDB.R;
import com.example.MovieDB.contract.SeriesContract;
import com.example.MovieDB.contract.SeriesDetailsContract;
import com.example.MovieDB.endpoints.EndPoints;
import com.example.MovieDB.model.series.Series;
import com.example.MovieDB.model.series.SeriesDetailsModel;
import com.example.MovieDB.model.series.SeriesResult;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.util.ArrayList;

public class SeriesPresenter {
    private SeriesContract contract;
    private SeriesDetailsContract detailsContract;
    private int type;
    private int page;
    private ArrayList<SeriesResult> seriesList = new ArrayList<>();

    public SeriesPresenter(SeriesContract contract) {
        this.contract = contract;
    }

    public SeriesPresenter(SeriesDetailsContract detailsContract) {
        this.detailsContract = detailsContract;
    }

    public void getSeries(int type, int page) {
        if (page == 1) {
            contract.showLoading();
        }
        String TYPE = "";
        this.type = type;
        this.page = page;
        switch (type) {
            case 1:
                TYPE = EndPoints.AIRING_TODAY;
                break;
            case 2:
                TYPE = EndPoints.ON_THE_AIR;
                break;
            case 3:
                TYPE = EndPoints.POPULAR;
                break;
            case 4:
                TYPE = EndPoints.TOP_RATED;
        }
        String url = EndPoints.SERIES_BASE_URL + TYPE + EndPoints.API_KEY + EndPoints.PAGES + page;
        Log.e("series", url);
        StringRequest request = new StringRequest(Request.Method.GET, url, response -> {
            Gson gson = new GsonBuilder().setLenient().create();
            Series series = gson.fromJson(response, Series.class);
            if (page > 1) {
                seriesList.addAll(series.getResults());
                contract.seriesListener(seriesList);
            } else {
                seriesList.clear();
                seriesList.addAll(series.getResults());
                contract.seriesListener(seriesList);
                contract.removeLoading();
            }

        }, error -> {
            contract.removeLoading();
            contract.internetConnectionError(R.drawable.baseline_wifi_off_black_36);
        });
        RequestQueue queue = Volley.newRequestQueue(MovieApp.getInstance().getApplicationContext());
        queue.add(request);
    }

    public void getSeriesById(int id) {
        String url = EndPoints.SERIES_BASE_URL + id + "?" + EndPoints.API_KEY;
        Log.e("series", url);
        StringRequest request = new StringRequest(Request.Method.GET, url, response -> {
            Gson gson = new GsonBuilder().setLenient().create();
            SeriesDetailsModel seriesDetailsModel = gson.fromJson(response, SeriesDetailsModel.class);
            detailsContract.SeriesListener(seriesDetailsModel);
        }, error -> {
            detailsContract.internetConnectionError(R.drawable.baseline_wifi_off_black_36);
        });
        RequestQueue queue = Volley.newRequestQueue(MovieApp.getInstance().getApplicationContext());
        queue.add(request);
    }

    public void increasePages() {
        page++;
        getSeries(type, page);
    }
}
