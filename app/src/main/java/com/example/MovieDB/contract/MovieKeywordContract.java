package com.example.MovieDB.contract;

import com.example.MovieDB.model.keywords.Keyword;

import java.util.List;

public interface MovieKeywordContract extends Contract{
    void keywordListener(List<Keyword> keywords);
}
