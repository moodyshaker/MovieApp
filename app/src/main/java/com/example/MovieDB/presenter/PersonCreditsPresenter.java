package com.example.MovieDB.presenter;

import android.util.Log;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.MovieDB.MovieApp;
import com.example.MovieDB.R;
import com.example.MovieDB.contract.PersonCreditsContract;
import com.example.MovieDB.endpoints.EndPoints;
import com.example.MovieDB.model.person.PersonCredits;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public class PersonCreditsPresenter {

    private PersonCreditsContract contract;

    public PersonCreditsPresenter(PersonCreditsContract contract) {
        this.contract = contract;
    }

    public void getPersonWork(int id) {
        String url = EndPoints.PERSON_BASE_URL + id + EndPoints.PERSON_MOVIE_CREDITS + EndPoints.API_KEY;
        Log.e("person", url);
        StringRequest request = new StringRequest(Request.Method.GET, url, response -> {
            Gson gson = new GsonBuilder().setLenient().create();
            PersonCredits personCredits = gson.fromJson(response, PersonCredits.class);
            contract.creditCast(personCredits.getCast());
            contract.creditCrew(personCredits.getCrew());
        }, error -> contract.internetConnectionError(R.drawable.baseline_wifi_off_black_36));

        RequestQueue queue = Volley.newRequestQueue(MovieApp.getInstance().getApplicationContext());
        queue.add(request);
    }
}
