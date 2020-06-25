package com.example.MovieDB.persistance.database.dao;

import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import com.example.MovieDB.persistance.database.entity.MovieEntity;
import com.example.MovieDB.persistance.database.entity.SeriesEntity;

import java.util.List;

import io.reactivex.Observable;
import io.reactivex.Single;

@androidx.room.Dao
public interface MyDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void addMovie(MovieEntity movieEntity);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void addSeries(SeriesEntity seriesEntity);

    @Query("UPDATE movie_table SET seen = 0 WHERE _id_movie == :id")
    void deleteSeenMovie(int id);

    @Query("UPDATE movie_table SET wish = 0 WHERE _id_movie == :id")
    void deleteWishMovie(int id);

    @Query("UPDATE series_table SET seen = 0 WHERE _id_series == :id")
    void deleteSeenSeries(int id);

    @Query("UPDATE series_table SET wish = 0 WHERE _id_series == :id")
    void deleteWishSeries(int id);

    @Query("SELECT * FROM movie_table WHERE wish == 1 AND movie_user_id == :userId")
    Observable<List<MovieEntity>> getWishMovieEntities(String userId);

    @Query("SELECT * FROM movie_table WHERE movie_user_id == :userId AND seen == 1")
    Observable<List<MovieEntity>> getSeenMovieEntities(String userId);

    @Query("SELECT * FROM series_table  WHERE wish == 1 AND series_user_id == :userId")
    Observable<List<SeriesEntity>> getWishSeriesEntities(String userId);

    @Query("SELECT * FROM series_table WHERE seen == 1 AND series_user_id == :userId")
    Observable<List<SeriesEntity>> getSeenSeriesEntities(String userId);

    @Query("SELECT * FROM movie_table WHERE _id_movie == :id AND seen == 1")
    Single<MovieEntity> getSeenMovie(int id);

    @Query("SELECT * FROM movie_table WHERE _id_movie == :id AND wish == 1")
    Single<MovieEntity> getWishMovie(int id);

    @Query("SELECT * FROM series_table WHERE _id_series == :id AND seen == 1")
    Single<SeriesEntity> getSeenSeries(int id);

    @Query("SELECT * FROM series_table WHERE _id_series == :id AND wish == 1")
    Single<SeriesEntity> getWishSeries(int id);

    @Query("SELECT * FROM movie_table WHERE _id_movie == :id")
    Single<MovieEntity> getMovieById(int id);

    @Query("SELECT * FROM series_table WHERE _id_series == :id")
    Single<SeriesEntity> getSeriesById(int id);

    @Query("DELETE FROM movie_table WHERE seen == 1")
    void deleteAllSeenMovies();

    @Query("DELETE FROM movie_table WHERE wish == 1")
    void deleteAllWishMovies();

    @Query("DELETE FROM series_table WHERE wish == 1")
    void deleteAllWishSeries();

    @Query("DELETE FROM series_table WHERE seen == 1")
    void deleteAllSeenSeries();

    @Query("UPDATE movie_table SET seen = 1 WHERE _id_movie == :id")
    void setMovieToSeen(int id);

    @Query("UPDATE movie_table SET wish = 1 WHERE _id_movie == :id")
    void setMovieToWish(int id);

    @Query("UPDATE series_table SET seen = 1 WHERE _id_series == :id")
    void setSeriesToSeen(int id);

    @Query("UPDATE series_table SET wish = 1 WHERE _id_series == :id")
    void setSeriesToWish(int id);

}
