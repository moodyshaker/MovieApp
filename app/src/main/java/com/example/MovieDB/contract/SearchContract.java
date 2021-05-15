package com.example.MovieDB.contract;

import com.example.MovieDB.model.credit_search.CreditResult;
import com.example.MovieDB.model.keywords_from_search.KeywordResult;
import com.example.MovieDB.model.movie.Movies;
import com.example.MovieDB.model.series.SeriesResult;

import java.util.List;

public interface SearchContract extends Contract {
    void MovieSearchList(List<Movies> movies, int pagesCount, int currentPages);

    void CreditSearchList(List<CreditResult> creditResults, int pagesCount, int currentPages);

    void SeriesSearchList(List<SeriesResult> seriesList, int pagesCount, int currentPages);

    void KeywordSearchList(List<KeywordResult> keywordList, int pagesCount, int currentPages);
}
