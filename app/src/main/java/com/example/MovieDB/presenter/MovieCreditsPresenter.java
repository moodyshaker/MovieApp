package com.example.MovieDB.presenter;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.MovieDB.MovieApp;
import com.example.MovieDB.R;
import com.example.MovieDB.contract.CreditContract;
import com.example.MovieDB.data.movie_credits.MovieCredits;
import com.example.MovieDB.endpoints.EndPoints;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public class MovieCreditsPresenter {

    private CreditContract creditContract;

    public MovieCreditsPresenter(CreditContract creditContract) {
        this.creditContract = creditContract;
    }

    public void getCredits(int id) {
        String url = EndPoints.MOVIE_BASE_URL + id + EndPoints.CREDITS + EndPoints.API_KEY;
        StringRequest request = new StringRequest(Request.Method.GET, url, response -> {
            Gson gson = new GsonBuilder().setLenient().create();
            MovieCredits movieCredits = gson.fromJson(response, MovieCredits.class);
            creditContract.crewListener(movieCredits.getCrew());
            creditContract.castListener(movieCredits.getCast());
        }, error -> creditContract.internetConnectionError(R.drawable.baseline_wifi_off_black_36));
        RequestQueue queue = Volley.newRequestQueue(MovieApp.getInstance().getApplicationContext());
        queue.add(request);
    }
}
