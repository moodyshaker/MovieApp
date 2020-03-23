package com.example.MovieDB.ui.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.MovieDB.R;
import com.example.MovieDB.model.data.person_images.Profile;
import com.example.MovieDB.endpoints.EndPoints;
import com.squareup.picasso.Picasso;

import java.util.List;

public class PersonImageAdapter extends RecyclerView.Adapter<PersonImageAdapter.PersonImageViewHolder> {

    private Context context;
    private List<Profile> profileList;

    public PersonImageAdapter(Context context, List<Profile> profileList) {
        this.context = context;
        this.profileList = profileList;
    }

    @NonNull
    @Override
    public PersonImageViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.person_image_layout, parent, false);
        return new PersonImageViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PersonImageViewHolder holder, int position) {
        Profile profile = profileList.get(position);
        holder.dataBinding(profile);
    }

    @Override
    public int getItemCount() {
        if (profileList != null) {
            return profileList.size();
        } else {
            return 0;
        }
    }

    public class PersonImageViewHolder extends RecyclerView.ViewHolder {
        ImageView actorImage;

        PersonImageViewHolder(View view) {
            super(view);
            actorImage = view.findViewById(R.id.actor_work_image);
        }

        private void dataBinding(Profile profile) {
            Picasso.get().load(EndPoints.Image500W + profile.getFilePath()).into(actorImage);
        }
    }
}
