package com.example.MovieDB.persistance.database.roomdb;

import android.content.Context;

import com.example.MovieDB.persistance.database.dao.MyDao;
import com.example.MovieDB.persistance.database.entity.MovieEntity;
import com.example.MovieDB.persistance.database.entity.SeriesEntity;

import java.util.List;

import io.reactivex.Completable;
import io.reactivex.Observable;
import io.reactivex.Single;
import io.reactivex.schedulers.Schedulers;

public class DatabaseRepository {
    private static DatabaseRepository repo;
    private MovieDataBase db;
    private MyDao dao;

    private DatabaseRepository(Context context) {
        db = MovieDataBase.getMovie(context);
        dao = db.getDao();
    }

    public static DatabaseRepository getRepo(Context context) {
        if (repo == null) {
            repo = new DatabaseRepository(context);
        }
        return repo;
    }

    public Observable<List<MovieEntity>> getWishMovies(String userId) {
        return dao.getWishMovieEntities(userId);
    }

    public Observable<List<MovieEntity>> getSeenMovies(String userId) {
        return dao.getSeenMovieEntities(userId);
    }

    public Observable<List<SeriesEntity>> getWishSeriesList(String userId) {
        return dao.getWishSeriesEntities(userId);
    }

    public Observable<List<SeriesEntity>> getSeenSeriesList(String userId) {
        return dao.getSeenSeriesEntities(userId);
    }

    public Single<MovieEntity> getSeenMovie(int id) {
        return dao.getSeenMovie(id);
    }

    public Single<MovieEntity> getWishMovie(int id) {
        return dao.getWishMovie(id);
    }

    public Single<SeriesEntity> getSeenSeries(int id) {
        return dao.getSeenSeries(id);
    }

    public Single<SeriesEntity> getWishSeries(int id) {
        return dao.getWishSeries(id);
    }

    public Single<MovieEntity> getMovieById(int id) {
        return dao.getMovieById(id);
    }

    public Single<SeriesEntity> getSeriesById(int id) {
        return dao.getSeriesById(id);
    }

    public void deleteAllWishMovies() {
        Completable.fromRunnable(() -> dao.deleteAllWishMovies())
                .subscribeOn(Schedulers.io())
                .subscribe();
    }

    public void deleteAllSeenMovies() {
        Completable.fromRunnable(() -> dao.deleteAllSeenMovies())
                .subscribeOn(Schedulers.io())
                .subscribe();
    }

    public void deleteAllWishSeries() {
        Completable.fromRunnable(() -> dao.deleteAllWishSeries())
                .subscribeOn(Schedulers.io())
                .subscribe();
    }

    public void deleteAllSeenSeries() {
        Completable.fromRunnable(() -> dao.deleteAllSeenSeries())
                .subscribeOn(Schedulers.io())
                .subscribe();
    }

    public void deleteMovie(int id) {
        Completable.fromRunnable(() -> dao.deleteMovie(id))
                .subscribeOn(Schedulers.io())
                .subscribe();
    }

    public void deleteSeries(int id) {
        Completable.fromRunnable(() -> dao.deleteSeries(id))
                .subscribeOn(Schedulers.io())
                .subscribe();
    }

    public void addMovie(MovieEntity entity) {
        Completable.fromRunnable(() -> dao.addMovie(entity))
                .subscribeOn(Schedulers.io())
                .subscribe();
    }

    public void addSeries(SeriesEntity entity) {
        Completable.fromRunnable(() -> dao.addSeries(entity))
                .subscribeOn(Schedulers.io())
                .subscribe();
    }

    public void deleteSeenMovie(int id) {
        Completable.fromRunnable(() -> dao.deleteSeenMovie(id))
                .subscribeOn(Schedulers.io())
                .subscribe();
    }

    public void deleteWishMovie(int id) {
        Completable.fromRunnable(() -> dao.deleteWishMovie(id))
                .subscribeOn(Schedulers.io())
                .subscribe();
    }

    public void deleteSeenSeries(int id) {
        Completable.fromRunnable(() -> dao.deleteSeenSeries(id))
                .subscribeOn(Schedulers.io())
                .subscribe();
    }

    public void deleteWishSeries(int id) {
        Completable.fromRunnable(() -> dao.deleteWishSeries(id))
                .subscribeOn(Schedulers.io())
                .subscribe();
    }

    public void setMovieToWish(int id) {
        Completable.fromRunnable(() -> dao.setMovieToWish(id))
                .subscribeOn(Schedulers.io())
                .subscribe();
    }

    public void setMovieToSeen(int id) {
        Completable.fromRunnable(() -> dao.setMovieToSeen(id))
                .subscribeOn(Schedulers.io())
                .subscribe();
    }

    public void setSeriesToWish(int id) {
        Completable.fromRunnable(() -> dao.setSeriesToWish(id))
                .subscribeOn(Schedulers.io())
                .subscribe();
    }

    public void setSeriesToSeen(int id) {
        Completable.fromRunnable(() -> dao.setSeriesToSeen(id))
                .subscribeOn(Schedulers.io())
                .subscribe();
    }
}
