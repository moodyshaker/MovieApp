package com.example.MovieDB.ui.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.MovieDB.R;
import com.example.MovieDB.endpoints.EndPoints;
import com.example.MovieDB.model.images_models.MovieSeriesImageDetails;
import com.example.MovieDB.model.images_models.PersonImageDetails;
import com.squareup.picasso.Picasso;

import java.util.List;

public class ImageAdapter<E> extends RecyclerView.Adapter<ImageAdapter.PersonImageViewHolder> {

    private Context context;
    private List<E> movieImageDetailsList;

    public ImageAdapter(Context context, List<E> movieImageDetailsList) {
        this.context = context;
        this.movieImageDetailsList = movieImageDetailsList;
    }

    @NonNull
    @Override
    public PersonImageViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.person_image_layout, parent, false);
        return new PersonImageViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ImageAdapter.PersonImageViewHolder holder, int position) {
        E movieImageDetails = movieImageDetailsList.get(position);
        holder.dataBinding(movieImageDetails);
    }

    @Override
    public int getItemCount() {
        return movieImageDetailsList != null ? movieImageDetailsList.size() : 0;
    }

    public class PersonImageViewHolder extends RecyclerView.ViewHolder {
        ImageView actorImage;

        PersonImageViewHolder(View view) {
            super(view);
            actorImage = view.findViewById(R.id.actor_work_image);
        }

        private void dataBinding(E movieImageDetails) {
            if (movieImageDetails instanceof PersonImageDetails) {
                Picasso.get().load(EndPoints.Image500W + ((PersonImageDetails) movieImageDetails).getFilePath()).into(actorImage);
            } else if (movieImageDetails instanceof MovieSeriesImageDetails) {
                Picasso.get().load(EndPoints.Image500W + ((MovieSeriesImageDetails) movieImageDetails).getFilePath()).into(actorImage);
            }
        }
    }
}
