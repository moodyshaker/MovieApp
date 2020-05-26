package com.example.MovieDB.contract;

import com.example.MovieDB.model.series_seasons.SeasonDetailsModel;

public interface SeasonContract extends Contract{
    void seasonListener(SeasonDetailsModel season);
}
