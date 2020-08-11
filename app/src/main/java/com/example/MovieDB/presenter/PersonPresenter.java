package com.example.MovieDB.presenter;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.MovieDB.MovieApp;
import com.example.MovieDB.contract.PersonContract;
import com.example.MovieDB.endpoints.EndPoints;
import com.example.MovieDB.model.person.Person;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public class PersonPresenter {

    private PersonContract contract;

    public PersonPresenter(PersonContract contract) {
        this.contract = contract;
    }

    public void getPerson(int id) {
        String url = EndPoints.PERSON_BASE_URL + id + "?" + EndPoints.API_KEY;
        StringRequest request = new StringRequest(Request.Method.GET, url, response -> {
            Gson gson = new GsonBuilder()
                    .setLenient()
                    .create();
            Person person = gson.fromJson(response, Person.class);
            contract.personListener(person);
        }, error -> {});

        RequestQueue queue = Volley.newRequestQueue(MovieApp.getInstance().getApplicationContext());
        queue.add(request);
    }
}
