package com.example.MovieDB.persistance.database.entity;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import com.example.MovieDB.model.series.SeriesResult;

@Entity(tableName = "series_table")
public class SeriesEntity {
    @PrimaryKey
    @ColumnInfo(name = "_id_series")
    private int series_id;

    @ColumnInfo(name = "series_user_id")
    private String userId;

    private String originalName;

    private String name;

    private Double popularity;

    private int voteCount;

    private String firstAirDate;

    private String backdropPath;

    private String originalLanguage;

    private double voteAverage;

    private String overview;

    private String posterPath;

    private int seen;

    private int wish;

    public String getOriginalName() {
        return originalName;
    }

    public void setOriginalName(String originalName) {
        this.originalName = originalName;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Double getPopularity() {
        return popularity;
    }

    public void setPopularity(Double popularity) {
        this.popularity = popularity;
    }

    public int getVoteCount() {
        return voteCount;
    }

    public void setVoteCount(int voteCount) {
        this.voteCount = voteCount;
    }

    public String getFirstAirDate() {
        return firstAirDate;
    }

    public void setFirstAirDate(String firstAirDate) {
        this.firstAirDate = firstAirDate;
    }

    public String getBackdropPath() {
        return backdropPath;
    }

    public void setBackdropPath(String backdropPath) {
        this.backdropPath = backdropPath;
    }

    public String getOriginalLanguage() {
        return originalLanguage;
    }

    public void setOriginalLanguage(String originalLanguage) {
        this.originalLanguage = originalLanguage;
    }

    public int getSeries_id() {
        return series_id;
    }

    public void setSeries_id(int series_id) {
        this.series_id = series_id;
    }

    public double getVoteAverage() {
        return voteAverage;
    }

    public void setVoteAverage(double voteAverage) {
        this.voteAverage = voteAverage;
    }

    public String getOverview() {
        return overview;
    }

    public void setOverview(String overview) {
        this.overview = overview;
    }

    public String getPosterPath() {
        return posterPath;
    }

    public void setPosterPath(String posterPath) {
        this.posterPath = posterPath;
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

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public static SeriesEntity getSeriesEntity(SeriesResult series, String userId) {
        if (series == null) {
            return null;
        }
        SeriesEntity entity = new SeriesEntity();
        entity.setSeries_id(series.getId());
        entity.setBackdropPath(series.getBackdropPath());
        entity.setFirstAirDate(series.getFirstAirDate());
        entity.setName(series.getName());
        entity.setOriginalLanguage(series.getOriginalLanguage());
        entity.setOriginalName(series.getOriginalName());
        entity.setOverview(series.getOverview());
        entity.setPopularity(series.getPopularity());
        entity.setPosterPath(series.getPosterPath());
        entity.setVoteAverage(series.getVoteAverage());
        entity.setVoteCount(series.getVoteCount());
        entity.setSeen(series.getSeen());
        entity.setWish(series.getWish());
        entity.setUserId(userId);
        return entity;
    }

    public static SeriesResult getSeriesModel(SeriesEntity entity) {
        if (entity == null) {
            return null;
        }
        SeriesResult series = new SeriesResult();
        series.setId(entity.getSeries_id());
        series.setBackdropPath(entity.getBackdropPath());
        series.setFirstAirDate(entity.getFirstAirDate());
        series.setName(entity.getName());
        series.setOriginalLanguage(entity.getOriginalLanguage());
        series.setOriginalName(entity.getOriginalName());
        series.setOverview(entity.getOverview());
        series.setPopularity(entity.getPopularity());
        series.setPosterPath(entity.getPosterPath());
        series.setVoteAverage(entity.getVoteAverage());
        series.setVoteCount(entity.getVoteCount());
        return series;
    }

    @Override
    public String toString() {
        return "SeriesEntity{" +
                "series_id=" + series_id +
                ", userId='" + userId + '\'' +
                ", originalName='" + originalName + '\'' +
                ", name='" + name + '\'' +
                ", popularity=" + popularity +
                ", voteCount=" + voteCount +
                ", firstAirDate='" + firstAirDate + '\'' +
                ", backdropPath='" + backdropPath + '\'' +
                ", originalLanguage='" + originalLanguage + '\'' +
                ", voteAverage=" + voteAverage +
                ", overview='" + overview + '\'' +
                ", posterPath='" + posterPath + '\'' +
                ", seen=" + seen +
                ", wish=" + wish +
                '}';
    }
}
