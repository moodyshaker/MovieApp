package com.example.MovieDB.ui.adapter;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.MovieDB.R;
import com.example.MovieDB.endpoints.EndPoints;
import com.example.MovieDB.model.movie.Movies;
import com.example.MovieDB.model.series.SeriesResult;
import com.example.MovieDB.persistance.database.entity.MovieEntity;
import com.example.MovieDB.persistance.database.entity.SeriesEntity;
import com.example.MovieDB.ui.activity.MovieDetails;
import com.example.MovieDB.ui.activity.SeriesDetails;
import com.squareup.picasso.Picasso;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class SeenWishAdapter<T> extends RecyclerView.Adapter<SeenWishAdapter.SeenWishViewHolder> {
    private Context context;
    private List<T> items;
    private OnMoreListener<T> onMoreListener;

    public SeenWishAdapter(Context context, List<T> items) {
        this.context = context;
        this.items = items;
    }

    public void setOnMoreListener(OnMoreListener<T> onMoreListener) {
        this.onMoreListener = onMoreListener;
    }

    @NonNull
    @Override
    public SeenWishViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.wish_seen_item, parent, false);
        return new SeenWishViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull SeenWishAdapter.SeenWishViewHolder holder, int i) {
        T object = items.get(i);
        holder.onBind(object);
    }


    @Override
    public int getItemCount() {
        return items != null ? items.size() : 0;
    }

    public class SeenWishViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        LinearLayout container;
        ImageView moviePoster;
        TextView movieName, releaseDate, rateText;
        ProgressBar rateProgressbar;
        T object;
        private Date date;
        private SimpleDateFormat formatter;
        private String finalDate;
        private ImageView moreIcon;

        public SeenWishViewHolder(View v) {
            super(v);
            container = v.findViewById(R.id.movie_item_container);
            moviePoster = v.findViewById(R.id.poster);
            movieName = v.findViewById(R.id.name);
            releaseDate = v.findViewById(R.id.release_date);
            rateText = v.findViewById(R.id.rate_number_text);
            rateProgressbar = v.findViewById(R.id.rate_progress_bar);
            moreIcon = v.findViewById(R.id.more_option);
            v.setOnClickListener(this);
        }

        private void onBind(T object) {
            this.object = object;
            if (object instanceof MovieEntity) {
                Picasso.get().load(EndPoints.Image500W + ((MovieEntity) object).getPosterPath()).placeholder(R.drawable.baseline_account_circle_white_36).error(R.drawable.cinema).into(moviePoster);
                movieName.setText(((MovieEntity) object).getTitle());
                if (!((MovieEntity) object).getReleaseDate().isEmpty()) {
                    try {
                        date = new SimpleDateFormat("yyyy-MM-dd").parse(((MovieEntity) object).getReleaseDate());
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                    formatter = new SimpleDateFormat("MMM dd yyyy");
                    finalDate = formatter.format(date);
                    releaseDate.setText(finalDate);
                } else {
                    releaseDate.setText("not provided");
                }
                rateText.setText(String.valueOf(((MovieEntity) object).getVoteAverage()));
                setRate(((MovieEntity) object).getVoteAverage());
            } else if (object instanceof SeriesEntity) {
                Picasso.get().load(EndPoints.Image500W + ((SeriesEntity) object).getPosterPath()).placeholder(R.drawable.baseline_account_circle_white_36).error(R.drawable.cinema).into(moviePoster);
                movieName.setText(((SeriesEntity) object).getName());
                if (!((SeriesEntity) object).getFirstAirDate().isEmpty()) {
                    try {
                        date = new SimpleDateFormat("yyyy-MM-dd").parse(((SeriesEntity) object).getFirstAirDate());
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                    formatter = new SimpleDateFormat("MMM dd yyyy");
                    finalDate = formatter.format(date);
                    releaseDate.setText(finalDate);
                } else {
                    releaseDate.setText("not provided");
                }
                rateText.setText(String.valueOf(((SeriesEntity) object).getVoteAverage()));
                setRate(((SeriesEntity) object).getVoteAverage());
            }
            moreIcon.setOnClickListener(c -> onMoreListener.onMoreClickItem(object));
        }

        private void setRate(double rateNumber) {
            rateProgressbar.setMax(10);
            switch ((int) Math.round(rateNumber)) {
                case 0:
                case 1:
                    rateProgressbar.setProgressDrawable(context.getResources().getDrawable(R.drawable.yellow_progress_bar));
                    rateProgressbar.setProgress((int) rateNumber);
                    break;
                case 2:
                case 3:
                    rateProgressbar.setProgressDrawable(context.getResources().getDrawable(R.drawable.green_progress_bar));
                    rateProgressbar.setProgress((int) rateNumber);
                    break;
                case 4:
                case 5:
                    rateProgressbar.setProgressDrawable(context.getResources().getDrawable(R.drawable.blue_progress_bar));
                    rateProgressbar.setProgress((int) rateNumber);
                    break;
                case 6:
                case 7:
                    rateProgressbar.setProgressDrawable(context.getResources().getDrawable(R.drawable.purple_progress_bar));
                    rateProgressbar.setProgress((int) rateNumber);
                    break;
                case 8:
                case 9:
                    rateProgressbar.setProgressDrawable(context.getResources().getDrawable(R.drawable.red_progress_bar));
                    rateProgressbar.setProgress((int) rateNumber);
                    break;
                default:
                    rateProgressbar.setProgressDrawable(context.getResources().getDrawable(R.drawable.gray_progress_bar));
                    rateProgressbar.setProgress((int) rateNumber);
            }
        }

        @Override
        public void onClick(View v) {
            if (object instanceof SeriesEntity) {
                SeriesResult seriesResult = SeriesEntity.getSeriesModel((SeriesEntity) object);
                if (seriesResult.getPosterPath() != null) {
                    Intent i = new Intent(context, SeriesDetails.class);
                    Bundle bundle = new Bundle();
                    bundle.putSerializable("series_object", seriesResult);
                    i.putExtras(bundle);
                    context.startActivity(i);
                } else {
                    Toast.makeText(context, "There is n data to display", Toast.LENGTH_SHORT).show();
                }
            } else if (object instanceof MovieEntity) {
                Movies movies = MovieEntity.getMovieModel((MovieEntity) object);
                Intent i = new Intent(context, MovieDetails.class);
                i.putExtra("type", "one");
                Bundle bundle = new Bundle();
                bundle.putSerializable("movie_object", movies);
                i.putExtras(bundle);
                context.startActivity(i);
            }
        }
    }

    public interface OnMoreListener<T> {
        void onMoreClickItem(T object);
    }
}
