package com.example.MovieDB.ui.adapter;

import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;
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
import com.example.MovieDB.ui.activity.MovieDetails;
import com.example.MovieDB.ui.activity.SeriesDetails;
import com.google.gson.Gson;
import com.squareup.picasso.Picasso;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class SearchAdapter<E> extends RecyclerView.Adapter<SearchAdapter<E>.SearchViewHolder> {
    private Context context;
    private List<E> list;

    public SearchAdapter(Context context) {
        this.context = context;
    }

    public void setList(List<E> list) {
        this.list = list;
    }

    public List<E> getList() {
        return list;
    }

    @NonNull
    @Override
    public SearchViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.movie_item, viewGroup, false);
        return new SearchViewHolder(view);
    }


    @Override
    public void onBindViewHolder(@NonNull SearchAdapter<E>.SearchViewHolder holder, int i) {
        E object = list.get(i);
        holder.onBindData(object);
    }

    @Override
    public int getItemCount() {
        return list != null ? list.size() : 0;
    }

    public class SearchViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        LinearLayout container;
        ImageView moviePoster;
        TextView movieName, releaseDate, rateText;
        ProgressBar rateProgressbar;
        private Date date;
        E object;
        private SimpleDateFormat formatter;
        private String finalDate;
        private Gson g;

        public SearchViewHolder(View itemView) {
            super(itemView);
            container = itemView.findViewById(R.id.movie_item_container);
            moviePoster = itemView.findViewById(R.id.poster);
            movieName = itemView.findViewById(R.id.name);
            releaseDate = itemView.findViewById(R.id.release_date);
            rateText = itemView.findViewById(R.id.rate_number_text);
            rateProgressbar = itemView.findViewById(R.id.rate_progress_bar);
            g = new Gson();
            itemView.setOnClickListener(this);
        }

        private void onBindData(E object) {
            this.object = object;
            if (object instanceof Movies) {
                Picasso.get().load(EndPoints.Image500W + ((Movies) object).getPosterPath()).placeholder(R.drawable.baseline_account_circle_white_36).error(R.drawable.cinema).into(moviePoster);
                movieName.setText(((Movies) object).getTitle());
                if (!TextUtils.isEmpty(((Movies) object).getReleaseDate())) {
                    try {
                        date = new SimpleDateFormat("yyyy-MM-dd").parse(((Movies) object).getReleaseDate());
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                    formatter = new SimpleDateFormat("MMM dd yyyy");
                    finalDate = formatter.format(date);
                    releaseDate.setText(finalDate);
                } else {
                    releaseDate.setText("not provided");
                }
                rateText.setText(String.valueOf(((Movies) object).getVoteAverage()));
                setRate(((Movies) object).getVoteAverage());
            } else if (object instanceof SeriesResult) {
                Picasso.get().load(EndPoints.Image500W + ((SeriesResult) object).getPosterPath()).error(R.drawable.cinema).into(moviePoster);
                movieName.setText(((SeriesResult) object).getName());
                if (!TextUtils.isEmpty(((SeriesResult) object).getFirstAirDate())) {
                    try {
                        date = new SimpleDateFormat("yyyy-MM-dd").parse(((SeriesResult) object).getFirstAirDate());
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                    formatter = new SimpleDateFormat("MMM dd yyyy");
                    finalDate = formatter.format(date);
                    releaseDate.setText(finalDate);
                } else {
                    releaseDate.setText("not provided");
                }
                rateText.setText(String.valueOf(((SeriesResult) object).getVoteAverage()));
                setRate(((SeriesResult) object).getVoteAverage());
            }
        }

        @Override
        public void onClick(View v) {
            if (object instanceof SeriesResult) {
                if (((SeriesResult) object).getPosterPath() != null) {
                    Intent i = new Intent(context, SeriesDetails.class);
                    String seriesJson = g.toJson(object);
                    i.putExtra("series_object", seriesJson);
                    context.startActivity(i);
                } else {
                    Toast.makeText(context, "There is n data to display", Toast.LENGTH_SHORT).show();
                }
            } else if (object instanceof Movies) {
                Intent i = new Intent(context, MovieDetails.class);
                i.putExtra("type", "two");
                String movieJson = g.toJson(object);
                i.putExtra("movie_object", movieJson);
                context.startActivity(i);
            }
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
    }
}
