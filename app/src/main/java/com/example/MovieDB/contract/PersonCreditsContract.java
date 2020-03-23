package com.example.MovieDB.contract;

import com.example.MovieDB.model.data.person.PersonCast;
import com.example.MovieDB.model.data.person.PersonCrew;

import java.util.List;

public interface PersonCreditsContract extends Contract{

    void creditCast(List<PersonCast> castList);

    void creditCrew(List<PersonCrew> crewList);
}
