package com.example.MovieDB.model.data.person_images;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class PersonImages {

    @SerializedName("profiles")
    @Expose
    private List<Profile> profiles = null;
    @SerializedName("id")
    @Expose
    private Integer id;

    public List<Profile> getProfiles() {
        return profiles;
    }

    public void setProfiles(List<Profile> profiles) {
        this.profiles = profiles;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }
}
