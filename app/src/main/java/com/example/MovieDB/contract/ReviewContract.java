package com.example.MovieDB.contract;

import com.example.MovieDB.data.reviews.Reviews;

import java.util.List;

public interface ReviewContract extends Contract {
    void reviewListener(List<Reviews> reviews);

    void reviewResultListener(Integer totalReviews);
}
