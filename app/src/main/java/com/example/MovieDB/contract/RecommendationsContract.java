package com.example.MovieDB.contract;

import com.example.MovieDB.data.movie.Movies;

import java.util.List;

public interface RecommendationsContract extends Contract{
    void recommendationsListener(List<Movies> recommendationsList);
}
