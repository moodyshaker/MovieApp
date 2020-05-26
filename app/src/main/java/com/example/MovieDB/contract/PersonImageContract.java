package com.example.MovieDB.contract;

import com.example.MovieDB.model.images_models.PersonImageDetails;

import java.util.List;

public interface PersonImageContract extends Contract {
    void personImageListener(List<PersonImageDetails> personImageDetails);
}
