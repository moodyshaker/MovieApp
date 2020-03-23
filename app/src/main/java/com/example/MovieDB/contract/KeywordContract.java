package com.example.MovieDB.contract;

import com.example.MovieDB.model.data.keywords.Keyword;

import java.util.List;

public interface KeywordContract extends Contract{
    void keywordListener(List<Keyword> keywords);
}
