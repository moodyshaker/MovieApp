package com.example.MovieDB.ui.adapter;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.MovieDB.R;
import com.example.MovieDB.model.data.movie.Movies;
import com.example.MovieDB.endpoints.EndPoints;
import com.example.MovieDB.ui.activity.MovieDetails;
import com.squareup.picasso.Picasso;

import java.util.List;

public class MovieAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private Context context;

    public void setMovies(Movies movies) {
        this.movies = movies;
    }

    public Movies getMovies() {
        return movies;
    }

    private List<Movies> list;
    private Movies movies;

    public MovieAdapter(Context context) {
        this.context = context;
    }

    public void setList(List<Movies> list) {
        this.list = list;
    }

    public List<Movies> getList() {
        return list;
    }

    @Override
    public int getItemViewType(int position) {
        if (list.get(position) == null) {
            return 1;
        } else {
            return 2;
        }

    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.movie_item, viewGroup, false);
        return new MovieHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int i) {
        if (holder instanceof MovieHolder) {
            movies = list.get(i);
            ((MovieHolder) holder).onBindData(movies);
        }
    }

    @Override
    public int getItemCount() {
        if (list != null) {
            return list.size();
        } else {
            return 0;
        }
    }

    public class MovieHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        LinearLayout container;
        ImageView moviePoster;
        TextView movieName;
        Movies movies;

        public MovieHolder(View itemView) {
            super(itemView);
            container = itemView.findViewById(R.id.movie_item_container);
            moviePoster = itemView.findViewById(R.id.poster);
            movieName = itemView.findViewById(R.id.name);
            itemView.setOnClickListener(this);
        }

        private void onBindData(Movies movies) {
            Picasso.get().load(EndPoints.Image500W + movies.getPosterPath()).error(R.drawable.cinema).into(moviePoster);
            movieName.setText(movies.getTitle());
            this.movies = movies;
        }

        @Override
        public void onClick(View v) {
            Intent i = new Intent(context, MovieDetails.class);
            i.putExtra("type", "one");
            Bundle bundle = new Bundle();
            bundle.putSerializable("movie_object", movies);
            i.putExtras(bundle);
            context.startActivity(i);
        }
    }
}
