package com.example.MovieDB.contract;

import com.example.MovieDB.model.movie.Movies;

import java.util.List;

public interface MovieByKeywordContract extends Contract {
    void MovieByKeyword(List<Movies> moviesList, int pagesCount, int currentPage);
}
