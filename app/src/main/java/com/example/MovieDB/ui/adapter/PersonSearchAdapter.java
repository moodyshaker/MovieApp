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
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.MovieDB.R;
import com.example.MovieDB.endpoints.EndPoints;
import com.example.MovieDB.model.credit_search.CreditResult;
import com.example.MovieDB.ui.activity.ActorActivity;
import com.squareup.picasso.Picasso;

import java.util.List;

public class PersonSearchAdapter extends RecyclerView.Adapter<PersonSearchAdapter.PersonSearchViewHolder> {
    private Context context;
    private List<CreditResult> list;

    public PersonSearchAdapter(Context context) {
        this.context = context;
    }

    public List<CreditResult> getList() {
        return list;
    }

    public void setList(List<CreditResult> list) {
        this.list = list;
    }

    @Override
    public PersonSearchViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.movie_item, viewGroup, false);
        return new PersonSearchViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PersonSearchViewHolder holder, int i) {
        CreditResult creditResult = list.get(i);
        holder.onBindData(creditResult);
    }

    @Override
    public int getItemCount() {
        if (list != null) {
            return list.size();
        } else {
            return 0;
        }
    }

    public class PersonSearchViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        LinearLayout container;
        ImageView moviePoster;
        TextView movieName;
        CreditResult creditResult;

        public PersonSearchViewHolder(View itemView) {
            super(itemView);
            container = itemView.findViewById(R.id.movie_item_container);
            moviePoster = itemView.findViewById(R.id.poster);
            movieName = itemView.findViewById(R.id.name);
            itemView.setOnClickListener(this);
        }

        private void onBindData(CreditResult creditResult) {
            this.creditResult = creditResult;
            Picasso.get().load(EndPoints.Image500W + creditResult.getProfilePath()).error(R.drawable.cinema).into(moviePoster);
            movieName.setText(creditResult.getName());

        }

        @Override
        public void onClick(View v) {
                if (creditResult.getProfilePath() != null) {
                    Intent i = new Intent(context, ActorActivity.class);
                    Bundle bundle = new Bundle();
                    bundle.putSerializable("credit_result", creditResult);
                    i.putExtra("type", "search");
                    i.putExtras(bundle);
                    context.startActivity(i);
                } else {
                    Toast.makeText(context, "There is n data to display", Toast.LENGTH_SHORT).show();
                }
        }
    }
}