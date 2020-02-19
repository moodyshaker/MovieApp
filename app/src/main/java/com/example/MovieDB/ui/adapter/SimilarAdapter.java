package com.example.MovieDB.ui.adapter;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.MovieDB.R;
import com.example.MovieDB.data.movie.Movies;
import com.example.MovieDB.endpoints.EndPoints;
import com.example.MovieDB.ui.activity.MovieDetails;
import com.squareup.picasso.Picasso;

import java.util.List;

public class SimilarAdapter extends RecyclerView.Adapter<SimilarAdapter.SimilarViewHolder> {

    private Context context;
    private List<Movies> movies;

    public void setMovies(List<Movies> movies) {
        this.movies = movies;
    }

    public SimilarAdapter(Context context) {
        this.context = context;
    }

    @NonNull
    @Override
    public SimilarViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.similar_item, parent, false);
        return new SimilarViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull SimilarViewHolder holder, int position) {
        Movies movie = movies.get(position);
        holder.dataBinding(movie);
    }

    @Override
    public int getItemCount() {
        if (movies != null) {
            return movies.size();
        } else {
            return 0;
        }
    }

    public class SimilarViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        ImageView rateIcon, poster;
        TextView name, rateNumber, releaseYear;
        Movies movies;

        SimilarViewHolder(View view) {
            super(view);
            view.setOnClickListener(this);
            rateIcon = view.findViewById(R.id.rate_star);
            poster = view.findViewById(R.id.poster);
            name = view.findViewById(R.id.name);
            rateNumber = view.findViewById(R.id.rate_number);
            releaseYear = view.findViewById(R.id.release_year);
        }

        private void dataBinding(Movies movies) {
            this.movies = movies;
            Picasso.get().load(EndPoints.Image200W + movies.getPosterPath()).error(R.drawable.cinema).into(poster);
            name.setText(movies.getTitle());
           String[] date = movies.getReleaseDate().split("-");
            releaseYear.setText("(" + date[0] + ")");
            rateNumber.setText(String.valueOf(movies.getVoteAverage()));
            switch ((int) Math.round(movies.getVoteAverage())) {
                case 0:
                    rateIcon.setBackgroundResource(R.drawable.radius_shape_yellow);
                    break;
                case 1:
                    rateIcon.setBackgroundResource(R.drawable.radius_shape_yellow);
                    break;
                case 2:
                    rateIcon.setBackgroundResource(R.drawable.radius_shape_green);
                    break;
                case 3:
                    rateIcon.setBackgroundResource(R.drawable.radius_shape_green);
                    break;
                case 4:
                    rateIcon.setBackgroundResource(R.drawable.radius_shape_blue);
                    break;
                case 5:
                    rateIcon.setBackgroundResource(R.drawable.radius_shape_blue);
                    break;
                case 6:
                    rateIcon.setBackgroundResource(R.drawable.radius_shape_light);
                    break;
                case 7:
                    rateIcon.setBackgroundResource(R.drawable.radius_shape_light);
                    break;
                case 8:
                    rateIcon.setBackgroundResource(R.drawable.radius_shape_red);
                    break;
                case 9:
                    rateIcon.setBackgroundResource(R.drawable.radius_shape_red);
                    break;
                default:
                    rateIcon.setBackgroundResource(R.drawable.radius_shape_gray);
            }
        }

        @Override
        public void onClick(View v) {
            Intent i = new Intent(context, MovieDetails.class);
            Bundle bundle = new Bundle();
            bundle.putSerializable("movie_object", movies);
            i.putExtras(bundle);
            context.startActivity(i);
        }
    }
}
