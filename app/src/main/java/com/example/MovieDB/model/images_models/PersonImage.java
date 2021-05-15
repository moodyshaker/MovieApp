package com.example.MovieDB.model.images_models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class PersonImage {

    @SerializedName("profiles")
    @Expose
    private List<PersonImageDetails> movieImageDetails = null;
    @SerializedName("id")
    @Expose
    private Integer id;

    public List<PersonImageDetails> getMovieImageDetails() {
        return movieImageDetails;
    }

    public void setMovieImageDetails(List<PersonImageDetails> movieImageDetails) {
        this.movieImageDetails = movieImageDetails;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }
}
