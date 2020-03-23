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
import com.example.MovieDB.model.data.person.PersonCast;
import com.example.MovieDB.endpoints.EndPoints;
import com.example.MovieDB.ui.activity.MovieDetails;
import com.squareup.picasso.Picasso;

import java.util.List;

public class PersonCastAdapter extends RecyclerView.Adapter<PersonCastAdapter.PersonCastViewHolder> {

    private Context context;
    private List<PersonCast> personCastList;

    public PersonCastAdapter(Context context, List<PersonCast> personCastList) {
        this.context = context;
        this.personCastList = personCastList;
    }

    @NonNull
    @Override
    public PersonCastViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.person_cast_item, parent, false);
        return new PersonCastViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PersonCastViewHolder holder, int position) {
        PersonCast personCast = personCastList.get(position);
        holder.dataBinding(personCast);
    }

    @Override
    public int getItemCount() {
        if (personCastList != null) {
            return personCastList.size();
        } else {
            return 0;
        }
    }

    public class PersonCastViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        ImageView poster;
        TextView movieName, creditName, releaseYear;
        PersonCast cast;

        PersonCastViewHolder(View view) {
            super(view);
            view.setOnClickListener(this);
            poster = view.findViewById(R.id.poster);
            movieName = view.findViewById(R.id.movie_name);
            creditName = view.findViewById(R.id.credit_name);
            releaseYear = view.findViewById(R.id.release_year);
        }

        private void dataBinding(PersonCast personCast) {
            this.cast = personCast;
            if (personCast.getPosterPath() != null) {
                Picasso.get().load(EndPoints.Image500W + personCast.getPosterPath()).into(poster);
            } else {
                poster.setImageResource(R.drawable.cinema);
            }
            if (personCast.getTitle() != null) {
                movieName.setText(personCast.getTitle());
            } else {
                movieName.setVisibility(View.GONE);
            }
            if (personCast.getCharacter() != null) {
                creditName.setText(personCast.getCharacter());
            } else {
                creditName.setVisibility(View.GONE);
            }
            if (personCast.getReleaseDate() != null) {
                String[] arr = personCast.getReleaseDate().split("-");
                releaseYear.setText("(" + arr[0] + ")");
            } else {
                releaseYear.setVisibility(View.GONE);
            }

        }

        @Override
        public void onClick(View v) {
            Intent i = new Intent(context, MovieDetails.class);
            Bundle bundle = new Bundle();
            bundle.putSerializable("movie_object", cast);
            i.putExtras(bundle);
            i.putExtra("type", "two");
            context.startActivity(i);
        }
    }
}
