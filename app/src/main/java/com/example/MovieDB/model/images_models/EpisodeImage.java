package com.example.MovieDB.model.images_models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class EpisodeImage {
    @SerializedName("id")
    @Expose
    private Integer id;
    @SerializedName("stills")
    @Expose
    private List<MovieSeriesImageDetails> stills = null;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public List<MovieSeriesImageDetails> getStills() {
        return stills;
    }

    public void setStills(List<MovieSeriesImageDetails> stills) {
        this.stills = stills;
    }
}
