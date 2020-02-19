package com.example.MovieDB.presenter;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.MovieDB.MovieApp;
import com.example.MovieDB.R;
import com.example.MovieDB.contract.PersonCreditsContract;
import com.example.MovieDB.data.person.PersonCredits;
import com.example.MovieDB.endpoints.EndPoints;
import com.google.gson.Gson;

public class PersonCreditsPresenter {

    private PersonCreditsContract contract;

    public PersonCreditsPresenter(PersonCreditsContract contract) {
        this.contract = contract;
    }

    public void getPerson(int id) {
        String url = EndPoints.PERSON_BASE_URL + id + EndPoints.PERSON_MOVIE_CREDITS + EndPoints.API_KEY;
        StringRequest request = new StringRequest(Request.Method.GET, url, response -> {
            Gson gson = new Gson();
            PersonCredits personCredits = gson.fromJson(response, PersonCredits.class);
            contract.creditCast(personCredits.getCast());
            contract.creditCrew(personCredits.getCrew());
        }, error -> contract.internetConnectionError(R.drawable.baseline_wifi_off_black_36));

        RequestQueue queue = Volley.newRequestQueue(MovieApp.getInstance().getApplicationContext());
        queue.add(request);
    }
}
