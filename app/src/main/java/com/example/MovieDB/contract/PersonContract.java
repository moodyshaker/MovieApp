package com.example.MovieDB.contract;

import com.example.MovieDB.model.data.person.Person;

public interface PersonContract extends Contract{
    void personListener(Person person);
}
