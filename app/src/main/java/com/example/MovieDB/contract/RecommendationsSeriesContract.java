package com.example.MovieDB.contract;

import com.example.MovieDB.model.series.SeriesResult;

import java.util.List;

public interface RecommendationsSeriesContract {
    void RecommendationsSeriesListener(List<SeriesResult> seriesDetailsModels);
}
