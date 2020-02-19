package com.example.MovieDB.contract;

import com.example.MovieDB.data.movie.Movies;

import java.util.List;

public interface MovieContract extends Contract {
    void movieListener(List<Movies> Movies);
}
