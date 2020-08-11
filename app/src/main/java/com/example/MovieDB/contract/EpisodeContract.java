package com.example.MovieDB.contract;

import com.example.MovieDB.model.series_episodes.SeriesEpisodeDetails;

public interface EpisodeContract extends Contract {
    void episodeListener(SeriesEpisodeDetails episode);
}
