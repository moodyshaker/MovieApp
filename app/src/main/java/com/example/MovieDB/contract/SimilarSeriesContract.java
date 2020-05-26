package com.example.MovieDB.contract;

import com.example.MovieDB.model.series.SeriesResult;

import java.util.List;

public interface SimilarSeriesContract extends Contract{
    void similarSeriesListener(List<SeriesResult> seriesDetailsModels);
}
