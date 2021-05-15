package com.example.MovieDB.ui.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.MovieDB.R;
import com.example.MovieDB.endpoints.EndPoints;
import com.example.MovieDB.model.series.SeriesSeasons;
import com.squareup.picasso.Picasso;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class SeasonAdapter extends RecyclerView.Adapter<SeasonAdapter.SimilarViewHolder> {

    private Context context;
    private List<SeriesSeasons> seasons;
    private SeasonClickListener seasonClickListener;

    public void setMovies(List<SeriesSeasons> seasons) {
        this.seasons = seasons;
    }

    public SeasonAdapter(Context context, SeasonClickListener seasonClickListener) {
        this.context = context;
        this.seasonClickListener = seasonClickListener;
    }

    @NonNull
    @Override
    public SimilarViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.season_item, parent, false);
        return new SimilarViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull SimilarViewHolder holder, int position) {
        SeriesSeasons season = seasons.get(position);
        holder.dataBinding(season);
    }

    @Override
    public int getItemCount() {
        return seasons != null ? seasons.size() : 0;
    }

    public class SimilarViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        ImageView seasonPoster;
        TextView seriesName, releaseDate, seasonNumber;
        ProgressBar rateProgressbar;
        SeriesSeasons season;
        private Date date;
        private SimpleDateFormat formatter;
        private String finalDate;

        SimilarViewHolder(View view) {
            super(view);
            view.setOnClickListener(this);
            seasonPoster = itemView.findViewById(R.id.poster);
            seriesName = itemView.findViewById(R.id.name);
            releaseDate = itemView.findViewById(R.id.release_date);
            seasonNumber = itemView.findViewById(R.id.rate_number_text);
            rateProgressbar = itemView.findViewById(R.id.rate_progress_bar);
        }

        private void dataBinding(SeriesSeasons season) {
            this.season = season;
            Picasso.get().load(EndPoints.Image200W + season.getPosterPath()).error(R.drawable.cinema).into(seasonPoster);
            seriesName.setText(season.getName());
            if (season.getAirDate() != null) {
                try {
                    date = new SimpleDateFormat("yyyy-MM-dd").parse(season.getAirDate());
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                formatter = new SimpleDateFormat("MMM dd yyyy");
                finalDate = formatter.format(date);
                releaseDate.setText(finalDate);
            } else {
                releaseDate.setText("not provided");
            }
            seasonNumber.setText(String.valueOf(season.getSeasonNumber()));
            setRate(season.getSeasonNumber());
        }

        @Override
        public void onClick(View v) {
            seasonClickListener.onSeasonClickListener(season);
        }

        private void setRate(int rateNumber) {
            rateProgressbar.setMax(10);
            rateProgressbar.setProgress(10);
            switch (rateNumber) {
                case 0:
                    rateProgressbar.setVisibility(View.GONE);
                    seasonNumber.setVisibility(View.GONE);
                    break;
                case 1:
                    rateProgressbar.setProgressDrawable(context.getResources().getDrawable(R.drawable.yellow_progress_bar));
                    break;
                case 2:
                    rateProgressbar.setProgressDrawable(context.getResources().getDrawable(R.drawable.cyan_progress_bar));
                    break;
                case 3:
                    rateProgressbar.setProgressDrawable(context.getResources().getDrawable(R.drawable.green_progress_bar));
                    break;
                case 4:
                    rateProgressbar.setProgressDrawable(context.getResources().getDrawable(R.drawable.amber_progress_bar));
                    break;
                case 5:
                    rateProgressbar.setProgressDrawable(context.getResources().getDrawable(R.drawable.blue_progress_bar));
                    break;
                case 6:
                    rateProgressbar.setProgressDrawable(context.getResources().getDrawable(R.drawable.white_progress_bar));
                    break;
                case 7:
                    rateProgressbar.setVisibility(View.VISIBLE);
                    seasonNumber.setVisibility(View.VISIBLE);
                    rateProgressbar.setProgressDrawable(context.getResources().getDrawable(R.drawable.purple_progress_bar));
                    break;
                case 8:
                    rateProgressbar.setProgressDrawable(context.getResources().getDrawable(R.drawable.pink_progress_bar));
                    break;
                case 9:
                    rateProgressbar.setProgressDrawable(context.getResources().getDrawable(R.drawable.red_progress_bar));
                    break;
                default:
                    rateProgressbar.setProgressDrawable(context.getResources().getDrawable(R.drawable.gray_progress_bar));
            }
        }
    }

    public interface SeasonClickListener {
        void onSeasonClickListener(SeriesSeasons season);
    }
}
