package com.example.MovieDB.contract;

import com.example.MovieDB.model.credit_model.Cast;
import com.example.MovieDB.model.credit_model.Crew;

import java.util.List;

public interface CreditContract extends Contract {

    void crewListener(List<Crew> crews);

    void castListener(List<Cast> casts);
}
