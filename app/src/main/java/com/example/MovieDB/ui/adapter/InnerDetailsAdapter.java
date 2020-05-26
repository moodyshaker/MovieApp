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
import com.example.MovieDB.model.series_seasons.SeasonEpisodes;
import com.example.MovieDB.ui.activity.MovieDetails;
import com.example.MovieDB.ui.activity.SeriesDetails;
import com.squareup.picasso.Picasso;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class InnerDetailsAdapter<E> extends RecyclerView.Adapter<InnerDetailsAdapter.InnerDetailsViewHolder> {
    private Context context;
    private List<E> list;
    private EpisodeCLickListener episodeCLickListener;

    public void setEpisodeCLickListener(EpisodeCLickListener episodeCLickListener) {
        this.episodeCLickListener = episodeCLickListener;
    }

    public InnerDetailsAdapter(Context context) {
        this.context = context;
    }

    public void setList(List<E> list) {
        this.list = list;
    }

    public List<E> getList() {
        return list;
    }

    @Override
    public InnerDetailsViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.inner_details_item, viewGroup, false);
        return new InnerDetailsViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull InnerDetailsAdapter.InnerDetailsViewHolder holder, int i) {
        E object = list.get(i);
        holder.onBindData(object, i);
    }

    @Override
    public int getItemCount() {
        if (list != null) {
            return list.size();
        } else {
            return 0;
        }
    }

    public class InnerDetailsViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        LinearLayout container;
        ImageView moviePoster;
        TextView movieName, releaseDate, rateText;
        ProgressBar rateProgressbar;
        E object;
        private Date date;
        private SimpleDateFormat formatter;
        private String finalDate;

        public InnerDetailsViewHolder(View itemView) {
            super(itemView);
            container = itemView.findViewById(R.id.movie_item_container);
            moviePoster = itemView.findViewById(R.id.poster);
            movieName = itemView.findViewById(R.id.name);
            releaseDate = itemView.findViewById(R.id.release_date);
            rateText = itemView.findViewById(R.id.rate_number_text);
            rateProgressbar = itemView.findViewById(R.id.rate_progress_bar);
            itemView.setOnClickListener(this);
        }

        private void onBindData(E object, int position) {
            this.object = object;
            if (object instanceof Movies) {
                Picasso.get().load(EndPoints.Image500W + ((Movies) object).getPosterPath()).error(R.drawable.cinema).into(moviePoster);
                movieName.setText(((Movies) object).getTitle());
                if (((Movies) object).getReleaseDate() != null) {
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
                try {
                    date = new SimpleDateFormat("yyyy-MM-dd").parse(((SeriesResult) object).getFirstAirDate());
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                formatter = new SimpleDateFormat("MMM dd yyyy");
                finalDate = formatter.format(date);
                releaseDate.setText(finalDate);
                rateText.setText(String.valueOf(((SeriesResult) object).getVoteAverage()));
                setRate(((SeriesResult) object).getVoteAverage());
            } else if (object instanceof SeasonEpisodes) {
                Picasso.get().load(EndPoints.Image500W + ((SeasonEpisodes) object).getStillPath()).error(R.drawable.cinema).into(moviePoster);
                movieName.setText(((SeasonEpisodes) object).getEpisodeNumber() + "  " + ((SeasonEpisodes) object).getName());
                try {
                    date = new SimpleDateFormat("yyyy-MM-dd").parse(((SeasonEpisodes) object).getAirDate());
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                formatter = new SimpleDateFormat("MMM dd yyyy");
                finalDate = formatter.format(date);
                releaseDate.setText(finalDate);
                NumberFormat formatter = new DecimalFormat("0.0");
                String finalVoteAverage = formatter.format(((SeasonEpisodes) object).getVoteAverage());
                rateText.setText(finalVoteAverage);
                setRate(Double.parseDouble(finalVoteAverage));
            }
        }

        @Override
        public void onClick(View v) {
            if (object instanceof SeriesResult) {
                if (((SeriesResult) object).getPosterPath() != null) {
                    Intent i = new Intent(context, SeriesDetails.class);
                    Bundle bundle = new Bundle();
                    bundle.putSerializable("series_object", ((SeriesResult) object));
                    i.putExtras(bundle);
                    context.startActivity(i);
                } else {
                    Toast.makeText(context, "There is n data to display", Toast.LENGTH_SHORT).show();
                }
            } else if (object instanceof Movies) {
                Intent i = new Intent(context, MovieDetails.class);
                i.putExtra("type", "one");
                Bundle bundle = new Bundle();
                bundle.putSerializable("movie_object", ((Movies) object));
                i.putExtras(bundle);
                context.startActivity(i);
            } else if (object instanceof SeasonEpisodes) {
                episodeCLickListener.onEpisodeClickListener((SeasonEpisodes) object);
            }
        }

        private void setRate(double rateNumber) {
            rateProgressbar.setMax(10);
            switch ((int) Math.round(rateNumber)) {
                case 0:
                    rateProgressbar.setProgressDrawable(context.getResources().getDrawable(R.drawable.cyan_progress_bar));
                    rateProgressbar.setProgress((int) rateNumber);
                    rateText.setTextColor(context.getResources().getColor(R.color.cyan_900));
                    break;
                case 1:
                    rateProgressbar.setProgressDrawable(context.getResources().getDrawable(R.drawable.yellow_progress_bar));
                    rateProgressbar.setProgress((int) rateNumber);
                    rateText.setTextColor(context.getResources().getColor(R.color.yellow_A200));
                    break;
                case 2:
                    rateProgressbar.setProgressDrawable(context.getResources().getDrawable(R.drawable.brown_progress_bar));
                    rateProgressbar.setProgress((int) rateNumber);
                    rateText.setTextColor(context.getResources().getColor(R.color.brown_900));
                    break;
                case 3:
                    rateProgressbar.setProgressDrawable(context.getResources().getDrawable(R.drawable.green_progress_bar));
                    rateProgressbar.setProgress((int) rateNumber);
                    rateText.setTextColor(context.getResources().getColor(R.color.light_green_900));
                    break;
                case 4:
                    rateProgressbar.setProgressDrawable(context.getResources().getDrawable(R.drawable.amber_progress_bar));
                    rateProgressbar.setProgress((int) rateNumber);
                    rateText.setTextColor(context.getResources().getColor(R.color.amber_900));
                    break;
                case 5:
                    rateProgressbar.setProgressDrawable(context.getResources().getDrawable(R.drawable.blue_progress_bar));
                    rateProgressbar.setProgress((int) rateNumber);
                    rateText.setTextColor(context.getResources().getColor(R.color.light_blue_900));
                    break;
                case 6:
                    rateProgressbar.setProgressDrawable(context.getResources().getDrawable(R.drawable.white_progress_bar));
                    rateProgressbar.setProgress((int) rateNumber);
                    rateText.setTextColor(context.getResources().getColor(R.color.white));
                    break;
                case 7:
                    rateProgressbar.setProgressDrawable(context.getResources().getDrawable(R.drawable.purple_progress_bar));
                    rateProgressbar.setProgress((int) rateNumber);
                    rateText.setTextColor(context.getResources().getColor(R.color.purple_900));
                    break;
                case 8:
                    rateProgressbar.setProgressDrawable(context.getResources().getDrawable(R.drawable.pink_progress_bar));
                    rateProgressbar.setProgress((int) rateNumber);
                    rateText.setTextColor(context.getResources().getColor(R.color.pink_A200));
                    break;
                case 9:
                    rateProgressbar.setProgressDrawable(context.getResources().getDrawable(R.drawable.red_progress_bar));
                    rateProgressbar.setProgress((int) rateNumber);
                    rateText.setTextColor(context.getResources().getColor(R.color.red_900));
                    break;
                default:
                    rateProgressbar.setProgressDrawable(context.getResources().getDrawable(R.drawable.gray_progress_bar));
                    rateProgressbar.setProgress((int) rateNumber);
                    rateText.setTextColor(context.getResources().getColor(R.color.blue_gray_700));
            }
        }
    }

    public interface EpisodeCLickListener {
        void onEpisodeClickListener(SeasonEpisodes episodes);
    }
}
