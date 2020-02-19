package com.example.MovieDB.contract;

import com.example.MovieDB.data.person.Person;

public interface PersonContract extends Contract{
    void personListener(Person person);
}
