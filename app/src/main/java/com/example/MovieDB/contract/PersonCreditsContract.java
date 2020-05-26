package com.example.MovieDB.contract;

import com.example.MovieDB.model.person.PersonCast;
import com.example.MovieDB.model.person.PersonCrew;

import java.util.List;

public interface PersonCreditsContract extends Contract{

    void creditCast(List<PersonCast> castList);

    void creditCrew(List<PersonCrew> crewList);
}
