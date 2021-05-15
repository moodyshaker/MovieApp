package com.example.MovieDB.contract;

import com.example.MovieDB.model.series.SeriesResult;

import java.util.List;

public interface SeriesContract extends Contract {
    void seriesListener(List<SeriesResult> seriesList);
}
