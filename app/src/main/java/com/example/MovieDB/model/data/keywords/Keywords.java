package com.example.MovieDB.model.data.keywords;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class Keywords {

    @SerializedName("id")
    @Expose
    private Integer id;
    @SerializedName("keywords")
    @Expose
    private List<Keyword> keywords = null;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public List<Keyword> getKeywords() {
        return keywords;
    }

    public void setKeywords(List<Keyword> keywords) {
        this.keywords = keywords;
    }
}
