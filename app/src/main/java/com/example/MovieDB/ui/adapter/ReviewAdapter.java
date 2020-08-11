package com.example.MovieDB.ui.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.MovieDB.R;
import com.example.MovieDB.model.reviews.Reviews;

import java.util.List;

public class ReviewAdapter extends RecyclerView.Adapter<ReviewAdapter.ReviewViewHolder> {

    private Context context;
    private List<Reviews> list;

    public ReviewAdapter(Context context, List<Reviews> list) {
        this.context = context;
        this.list = list;
    }

    @NonNull
    @Override
    public ReviewViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.review_list_item, parent, false);
        return new ReviewViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ReviewViewHolder holder, int position) {
        Reviews reviews = list.get(position);
        holder.dataBinding(reviews);
    }

    @Override
    public int getItemCount() {
        return list != null ? list.size() : 0;
    }

    public class ReviewViewHolder extends RecyclerView.ViewHolder {

        TextView name, content;

        public ReviewViewHolder(@NonNull View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.author_name);
            content = itemView.findViewById(R.id.review_content);
        }

        private void dataBinding(Reviews reviews) {
            name.setText(reviews.getAuthor());
            content.setText(reviews.getContent());
        }
    }
}
