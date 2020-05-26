package com.example.MovieDB.model.keywords;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class SeriesKeyword {
    @SerializedName("id")
    @Expose
    private Integer id;
    @SerializedName("results")
    @Expose
    private List<SeriesKeywords> results = null;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public List<SeriesKeywords> getResults() {
        return results;
    }

    public void setResults(List<SeriesKeywords> results) {
        this.results = results;
    }

}
