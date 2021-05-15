package com.example.MovieDB.contract;

import com.example.MovieDB.model.keywords.SeriesKeywords;

import java.util.List;

public interface SeriesKeywordContract extends Contract{
    void keywordListener(List<SeriesKeywords> keywords);
}
