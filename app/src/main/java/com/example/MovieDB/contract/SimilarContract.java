package com.example.MovieDB.contract;

import com.example.MovieDB.data.movie.Movies;

import java.util.List;

public interface SimilarContract extends Contract {
    void similarListener(List<Movies> similarList);
}
