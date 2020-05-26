package com.example.MovieDB.contract;

import com.example.MovieDB.model.movie_trailer.MovieTrailer;

import java.util.List;

public interface TrailerContract extends Contract {
    void trailers(List<MovieTrailer> trailer);
}
