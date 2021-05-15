package com.example.MovieDB.ui.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.MovieDB.R;
import com.example.MovieDB.endpoints.EndPoints;
import com.example.MovieDB.model.credit_model.Cast;
import com.example.MovieDB.model.credit_model.Crew;
import com.example.MovieDB.model.series_episodes.SeriesCrew;
import com.example.MovieDB.model.series_episodes.SeriesGuestStar;
import com.example.MovieDB.ui.activity.ActorActivity;
import com.google.gson.Gson;
import com.squareup.picasso.Picasso;

import java.util.List;

public class StarsAdapter<T> extends RecyclerView.Adapter<StarsAdapter<T>.CastViewHolder> {

    private Context context;
    private List<T> starList;

    public StarsAdapter(Context context) {
        this.context = context;
    }

    public void setStarList(List<T> castList) {
        this.starList = castList;
    }

    public List<T> getStartList() {
        return starList;
    }

    @NonNull
    @Override
    public CastViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.credit_item, parent, false);
        return new CastViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CastViewHolder holder, int i) {
        T object = starList.get(i);
        holder.dataBinding(object);
    }

    @Override
    public int getItemCount() {
        return starList != null ? starList.size() : 0;
    }


    public class CastViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        ImageView poster;
        TextView name, characterName;
        T object;
        Gson g;

        CastViewHolder(View view) {
            super(view);
            view.setOnClickListener(this);
            poster = view.findViewById(R.id.poster);
            name = view.findViewById(R.id.name);
            characterName = view.findViewById(R.id.movie_character);
            g = new Gson();
        }

        private void dataBinding(T object) {
            this.object = object;
            if (object instanceof Cast) {
                Picasso.get().load(EndPoints.Image200W + ((Cast) object).getProfilePath()).error(R.drawable.actor_icon).into(poster);
                name.setText(((Cast) object).getName());
                characterName.setText(((Cast) object).getCharacter());
            } else if (object instanceof Crew) {
                Picasso.get().load(EndPoints.Image200W + ((Crew) object).getProfilePath()).error(R.drawable.actor_icon).into(poster);
                name.setText(((Crew) object).getName());
                characterName.setText(((Crew) object).getJob());
            } else if (object instanceof SeriesCrew) {
                Picasso.get().load(EndPoints.Image200W + ((SeriesCrew) object).getProfilePath()).error(R.drawable.actor_icon).into(poster);
                name.setText(((SeriesCrew) object).getName());
                characterName.setText(((SeriesCrew) object).getJob());
            } else if (object instanceof SeriesGuestStar) {
                Picasso.get().load(EndPoints.Image200W + ((SeriesGuestStar) object).getProfilePath()).error(R.drawable.actor_icon).into(poster);
                name.setText(((SeriesGuestStar) object).getName());
                characterName.setText(((SeriesGuestStar) object).getCharacter());
            }
        }

        @Override
        public void onClick(View v) {
            if (object instanceof Cast) {
                if (((Cast) object).getProfilePath() != null) {
                    Intent i = new Intent(context, ActorActivity.class);
                    String castJson = g.toJson(object);
                    i.putExtra("Cast", castJson);
                    i.putExtra("type", "cast");
                    context.startActivity(i);
                } else {
                    Toast.makeText(context, "There is no data to display", Toast.LENGTH_SHORT).show();
                }
            } else if (object instanceof Crew) {
                if (((Crew) object).getProfilePath() != null) {
                    Intent i = new Intent(context, ActorActivity.class);
                    String crewJson = g.toJson(object);
                    i.putExtra("Crew", crewJson);
                    i.putExtra("type", "crew");
                    context.startActivity(i);
                } else {
                    Toast.makeText(context, "There is no data to display", Toast.LENGTH_SHORT).show();
                }
            } else if (object instanceof SeriesCrew) {
                if (((SeriesCrew) object).getProfilePath() != null) {
                    Intent i = new Intent(context, ActorActivity.class);
                    String seriesCrewJson = g.toJson(object);
                    i.putExtra("SeriesCrew", seriesCrewJson);
                    i.putExtra("type", "series_crew");
                    context.startActivity(i);
                } else {
                    Toast.makeText(context, "There is no data to display", Toast.LENGTH_SHORT).show();
                }
            } else if (object instanceof SeriesGuestStar) {
                if (((SeriesGuestStar) object).getProfilePath() != null) {
                    Intent i = new Intent(context, ActorActivity.class);
                    String seriesGuestStarJson = g.toJson(object);
                    i.putExtra("SeriesGuestCrew", seriesGuestStarJson);
                    i.putExtra("type", "series_guest");
                    context.startActivity(i);
                } else {
                    Toast.makeText(context, "There is no data to display", Toast.LENGTH_SHORT).show();
                }
            }
        }
    }
}
