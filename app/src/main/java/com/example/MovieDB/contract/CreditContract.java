package com.example.MovieDB.contract;

import com.example.MovieDB.model.data.movie_credits.Cast;
import com.example.MovieDB.model.data.movie_credits.Crew;

import java.util.List;

public interface CreditContract extends Contract {

    void crewListener(List<Crew> crews);

    void castListener(List<Cast> casts);
}
