package com.example.MovieDB.contract;

import com.example.MovieDB.model.data.movie.Movies;

import java.util.List;

public interface SearchContract extends Contract{
    void MovieSearchList(List<Movies> movies);
}
