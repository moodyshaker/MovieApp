package com.example.MovieDB.model.series_seasons;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.List;

public class SeasonEpisodes implements Serializable {
    @SerializedName("air_date")
    @Expose
    private String airDate;
    @SerializedName("episode_number")
    @Expose
    private Integer episodeNumber;
    @SerializedName("id")
    @Expose
    private Integer id;
    @SerializedName("name")
    @Expose
    private String name;
    @SerializedName("overview")
    @Expose
    private String overview;
    @SerializedName("production_code")
    @Expose
    private String productionCode;
    @SerializedName("season_number")
    @Expose
    private Integer seasonNumber;
    @SerializedName("show_id")
    @Expose
    private Integer showId;
    @SerializedName("still_path")
    @Expose
    private String stillPath;
    @SerializedName("vote_average")
    @Expose
    private Double voteAverage;
    @SerializedName("vote_count")
    @Expose
    private Integer voteCount;
    @SerializedName("crew")
    @Expose
    private List<SeasonCrews> crew = null;
    @SerializedName("guest_stars")
    @Expose
    private List<SeasonGuests> guestStars = null;

    public String getAirDate() {
        return airDate;
    }

    public void setAirDate(String airDate) {
        this.airDate = airDate;
    }

    public Integer getEpisodeNumber() {
        return episodeNumber;
    }

    public void setEpisodeNumber(Integer episodeNumber) {
        this.episodeNumber = episodeNumber;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getOverview() {
        return overview;
    }

    public void setOverview(String overview) {
        this.overview = overview;
    }

    public String getProductionCode() {
        return productionCode;
    }

    public void setProductionCode(String productionCode) {
        this.productionCode = productionCode;
    }

    public Integer getSeasonNumber() {
        return seasonNumber;
    }

    public void setSeasonNumber(Integer seasonNumber) {
        this.seasonNumber = seasonNumber;
    }

    public Integer getShowId() {
        return showId;
    }

    public void setShowId(Integer showId) {
        this.showId = showId;
    }

    public String getStillPath() {
        return stillPath;
    }

    public void setStillPath(String stillPath) {
        this.stillPath = stillPath;
    }

    public Double getVoteAverage() {
        return voteAverage;
    }

    public void setVoteAverage(Double voteAverage) {
        this.voteAverage = voteAverage;
    }

    public Integer getVoteCount() {
        return voteCount;
    }

    public void setVoteCount(Integer voteCount) {
        this.voteCount = voteCount;
    }

    public List<SeasonCrews> getCrew() {
        return crew;
    }

    public void setCrew(List<SeasonCrews> crew) {
        this.crew = crew;
    }

    public List<SeasonGuests> getGuestStars() {
        return guestStars;
    }

    public void setGuestStars(List<SeasonGuests> guestStars) {
        this.guestStars = guestStars;
    }
}
