package com.example.MovieDB.contract;

import com.example.MovieDB.model.images_models.MovieSeriesImageDetails;

import java.util.List;

public interface MovieSeriesImageContract extends Contract{
    void movieSeriesImageListener(List<MovieSeriesImageDetails> movieSeriesImageDetails);
}
