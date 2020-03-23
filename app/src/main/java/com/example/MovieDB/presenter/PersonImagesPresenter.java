package com.example.MovieDB.presenter;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.MovieDB.MovieApp;
import com.example.MovieDB.R;
import com.example.MovieDB.contract.PersonImageContract;
import com.example.MovieDB.model.data.person_images.PersonImages;
import com.example.MovieDB.endpoints.EndPoints;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public class PersonImagesPresenter {

    private PersonImageContract contract;
    private int id;

    public PersonImagesPresenter(PersonImageContract contract) {
        this.contract = contract;
    }

    public void getPersonImages(int id) {
        this.id = id;
        String url = EndPoints.PERSON_BASE_URL + id + EndPoints.PERSON_IMAGES + EndPoints.API_KEY;
        StringRequest request = new StringRequest(Request.Method.GET, url, response -> {
            Gson gson = new GsonBuilder()
                    .setLenient()
                    .create();;
            PersonImages personImages = gson.fromJson(response, PersonImages.class);
            contract.personImageListener(personImages.getProfiles());
        }, error -> contract.internetConnectionError(R.drawable.baseline_wifi_off_black_36));

        RequestQueue queue = Volley.newRequestQueue(MovieApp.getInstance().getApplicationContext());
        queue.add(request);
    }
}
