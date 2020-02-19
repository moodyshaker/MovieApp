package com.example.MovieDB.ui.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.MovieDB.R;
import com.example.MovieDB.data.movie_credits.Crew;
import com.example.MovieDB.endpoints.EndPoints;
import com.squareup.picasso.Picasso;

import java.util.List;

public class CrewAdapter extends RecyclerView.Adapter<com.example.MovieDB.ui.adapter.CrewAdapter.CrewViewHolder> {

    private Context context;
    private List<Crew> crewList;

    public CrewAdapter(Context context, List<Crew> crewList) {
        this.context = context;
        this.crewList = crewList;
    }

    @NonNull
    @Override
    public CrewViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.credit_item, parent, false);
        return new CrewViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CrewViewHolder holder, int position) {
        Crew crew = crewList.get(position);
        holder.dataBinding(crew);
    }

    @Override
    public int getItemCount() {
        if (crewList != null) {
            return crewList.size();
        } else {
            return 0;
        }
    }

    public class CrewViewHolder extends RecyclerView.ViewHolder {
        ImageView poster;
        TextView name, characterName;

        CrewViewHolder(View view) {
            super(view);
            poster = view.findViewById(R.id.poster);
            name = view.findViewById(R.id.name);
            characterName = view.findViewById(R.id.movie_character);
        }

        private void dataBinding(Crew crew) {
            Picasso.get().load(EndPoints.Image200W + crew.getProfilePath()).error(R.drawable.crew_icon).into(poster);
            name.setText(crew.getName());
            characterName.setText(crew.getJob());
        }
    }
}
