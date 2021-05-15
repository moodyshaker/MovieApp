package com.example.MovieDB.persistance.database.entity;


import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import com.example.MovieDB.model.movie.Movies;

@Entity(tableName = "movie_table")
public class MovieEntity {
    @PrimaryKey
    @ColumnInfo(name = "_id_movie")
    private int movie_id;

    @ColumnInfo(name = "movie_user_id")
    private String userId;

    private int voteCount;

    private boolean video;

    private double voteAverage;

    private String title;

    private double popularity;

    private String posterPath;

    private String originalLanguage;

    private String originalTitle;

    private String backdropPath;

    private boolean adult;

    private String overview;

    private String releaseDate;

    private int seen;

    private int wish;

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public int getVoteCount() {
        return voteCount;
    }

    public void setVoteCount(int voteCount) {
        this.voteCount = voteCount;
    }

    public int getMovie_id() {
        return movie_id;
    }

    public void setMovie_id(int movie_id) {
        this.movie_id = movie_id;
    }

    public boolean getVideo() {
        return video;
    }

    public void setVideo(boolean video) {
        this.video = video;
    }

    public double getVoteAverage() {
        return voteAverage;
    }

    public void setVoteAverage(double voteAverage) {
        this.voteAverage = voteAverage;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public double getPopularity() {
        return popularity;
    }

    public void setPopularity(double popularity) {
        this.popularity = popularity;
    }

    public String getPosterPath() {
        return posterPath;
    }

    public void setPosterPath(String posterPath) {
        this.posterPath = posterPath;
    }

    public String getOriginalLanguage() {
        return originalLanguage;
    }

    public void setOriginalLanguage(String originalLanguage) {
        this.originalLanguage = originalLanguage;
    }

    public String getOriginalTitle() {
        return originalTitle;
    }

    public void setOriginalTitle(String originalTitle) {
        this.originalTitle = originalTitle;
    }

    public String getBackdropPath() {
        return backdropPath;
    }

    public void setBackdropPath(String backdropPath) {
        this.backdropPath = backdropPath;
    }

    public boolean getAdult() {
        return adult;
    }

    public void setAdult(boolean adult) {
        this.adult = adult;
    }

    public String getOverview() {
        return overview;
    }

    public void setOverview(String overview) {
        this.overview = overview;
    }

    public String getReleaseDate() {
        return releaseDate;
    }

    public void setReleaseDate(String releaseDate) {
        this.releaseDate = releaseDate;
    }

    public int getSeen() {
        return seen;
    }

    public void setSeen(int seen) {
        this.seen = seen;
    }

    public int getWish() {
        return wish;
    }

    public void setWish(int wish) {
        this.wish = wish;
    }

    public static MovieEntity getMovieEntity(Movies movie, String userID) {
        if (movie == null) {
            return null;
        }
        MovieEntity movieEntity = new MovieEntity();
        movieEntity.setMovie_id(movie.getId());
        movieEntity.setAdult(movie.getAdult());
        movieEntity.setBackdropPath(movie.getBackdropPath());
        movieEntity.setOriginalLanguage(movie.getOriginalLanguage());
        movieEntity.setOriginalTitle(movie.getOriginalTitle());
        movieEntity.setOverview(movie.getOverview());
        movieEntity.setPopularity(movie.getPopularity());
        movieEntity.setPosterPath(movie.getPosterPath());
        movieEntity.setReleaseDate(movie.getReleaseDate());
        movieEntity.setTitle(movie.getTitle());
        movieEntity.setVideo(movie.getVideo());
        movieEntity.setVoteAverage(movie.getVoteAverage());
        movieEntity.setVoteCount(movie.getVoteCount());
        movieEntity.setSeen(movie.getSeen());
        movieEntity.setWish(movie.getWish());
        movieEntity.setUserId(userID);
        return movieEntity;
    }

    public static Movies getMovieModel(MovieEntity entity) {
        if (entity == null) {
            return null;
        }
        Movies movie = new Movies();
        movie.setId(entity.getMovie_id());
        movie.setAdult(entity.getAdult());
        movie.setBackdropPath(entity.getBackdropPath());
        movie.setOriginalLanguage(entity.getOriginalLanguage());
        movie.setOriginalTitle(entity.getOriginalTitle());
        movie.setOverview(entity.getOverview());
        movie.setPopularity(entity.getPopularity());
        movie.setPosterPath(entity.getPosterPath());
        movie.setReleaseDate(entity.getReleaseDate());
        movie.setTitle(entity.getTitle());
        movie.setVideo(entity.getVideo());
        movie.setVoteAverage(entity.getVoteAverage());
        movie.setVoteCount(entity.getVoteCount());
        return movie;
    }

    @Override
    public String toString() {
        return "MovieEntity{" +
                "movie_id=" + movie_id +
                ", userId='" + userId + '\'' +
                ", voteCount=" + voteCount +
                ", video=" + video +
                ", voteAverage=" + voteAverage +
                ", title='" + title + '\'' +
                ", popularity=" + popularity +
                ", posterPath='" + posterPath + '\'' +
                ", originalLanguage='" + originalLanguage + '\'' +
                ", originalTitle='" + originalTitle + '\'' +
                ", backdropPath='" + backdropPath + '\'' +
                ", adult=" + adult +
                ", overview='" + overview + '\'' +
                ", releaseDate='" + releaseDate + '\'' +
                ", seen=" + seen +
                ", wish=" + wish +
                '}';
    }
}
