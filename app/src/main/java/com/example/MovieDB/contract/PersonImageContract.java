package com.example.MovieDB.contract;

import com.example.MovieDB.model.data.person_images.Profile;

import java.util.List;

public interface PersonImageContract extends Contract {
    void personImageListener(List<Profile> profiles);
}
