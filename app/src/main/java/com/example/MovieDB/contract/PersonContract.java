package com.example.MovieDB.contract;

import com.example.MovieDB.model.person.Person;

public interface PersonContract extends Contract{
    void personListener(Person person);
}
