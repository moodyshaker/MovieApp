package com.example.MovieDB.contract;

import com.example.MovieDB.model.movie.Movies;

import java.util.List;

public interface SimilarMoviesContract extends Contract {
    void similarListener(List<Movies> similarList);
}
