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
import com.example.MovieDB.model.data.movie_credits.Cast;
import com.example.MovieDB.endpoints.EndPoints;
import com.example.MovieDB.ui.activity.ActorActivity;
import com.squareup.picasso.Picasso;

import java.util.List;

public class CastAdapter extends RecyclerView.Adapter<CastAdapter.CastViewHolder> {

    private Context context;
    private List<Cast> castList;

    public CastAdapter(Context context, List<Cast> castList) {
        this.context = context;
        this.castList = castList;
    }

    @NonNull
    @Override
    public CastViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.credit_item, parent, false);
        return new CastViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CastViewHolder holder, int position) {
        Cast cast = castList.get(position);
        holder.dataBinding(cast);
    }

    @Override
    public int getItemCount() {
        if (castList != null) {
            return castList.size();
        } else {
            return 0;
        }
    }

    public class CastViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        ImageView poster;
        TextView name, characterName;
        Cast cast;

        CastViewHolder(View view) {
            super(view);
            view.setOnClickListener(this);
            poster = view.findViewById(R.id.poster);
            name = view.findViewById(R.id.name);
            characterName = view.findViewById(R.id.movie_character);
        }

        private void dataBinding(Cast cast) {
            this.cast = cast;
            Picasso.get().load(EndPoints.Image200W + cast.getProfilePath()).error(R.drawable.actor_icon).into(poster);
            name.setText(cast.getName());
            characterName.setText(cast.getCharacter());
        }

        @Override
        public void onClick(View v) {
            Intent i = new Intent(context, ActorActivity.class);
            Bundle bundle = new Bundle();
            bundle.putSerializable("Cast", cast);
            i.putExtras(bundle);
            i.putExtra("type", "cast");
            context.startActivity(i);
        }
    }
}
