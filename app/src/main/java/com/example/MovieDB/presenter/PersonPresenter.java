package com.example.MovieDB.presenter;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.MovieDB.MovieApp;
import com.example.MovieDB.R;
import com.example.MovieDB.contract.PersonContract;
import com.example.MovieDB.data.person.Person;
import com.example.MovieDB.endpoints.EndPoints;
import com.google.gson.Gson;

public class PersonPresenter {

    private PersonContract contract;

    public PersonPresenter(PersonContract contract) {
        this.contract = contract;
    }

    public void getPerson(int id) {
        String url = EndPoints.PERSON_BASE_URL + id + "?" + EndPoints.API_KEY;
        StringRequest request = new StringRequest(Request.Method.GET, url, response -> {
            Gson gson = new Gson();
            Person person = gson.fromJson(response, Person.class);
            contract.personListener(person);
        }, error -> contract.internetConnectionError(R.drawable.baseline_wifi_off_black_36));

        RequestQueue queue = Volley.newRequestQueue(MovieApp.getInstance().getApplicationContext());
        queue.add(request);
    }
}
