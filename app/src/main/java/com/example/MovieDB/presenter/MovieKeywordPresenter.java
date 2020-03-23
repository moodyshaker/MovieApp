package com.example.MovieDB.presenter;

import android.util.Log;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.MovieDB.MovieApp;
import com.example.MovieDB.R;
import com.example.MovieDB.contract.KeywordContract;
import com.example.MovieDB.model.data.keywords.Keywords;
import com.example.MovieDB.endpoints.EndPoints;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public class MovieKeywordPresenter {

    private KeywordContract keywordContract;

    public MovieKeywordPresenter(KeywordContract keywordContract) {
        this.keywordContract = keywordContract;
    }

    public void getKeyword(int id) {
        String url = EndPoints.MOVIE_BASE_URL + id + EndPoints.KEYWORDS + EndPoints.API_KEY;
        StringRequest request = new StringRequest(Request.Method.GET, url, response -> {
            Log.e("very", response.toString());
            Gson gson = new GsonBuilder()
                    .setLenient()
                    .create();
            Keywords keywords = gson.fromJson(response, Keywords.class);
            keywordContract.keywordListener(keywords.getKeywords());
        }, error -> keywordContract.internetConnectionError(R.drawable.baseline_wifi_off_black_36));
        RequestQueue queue = Volley.newRequestQueue(MovieApp.getInstance().getApplicationContext());
        queue.add(request);
    }
}
