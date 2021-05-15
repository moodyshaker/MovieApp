package com.example.MovieDB.contract;

import com.example.MovieDB.model.movie.Movies;

import java.util.List;

public interface RecommendationsMoviesContract extends Contract{
    void recommendationsListener(List<Movies> recommendationsList);
}
