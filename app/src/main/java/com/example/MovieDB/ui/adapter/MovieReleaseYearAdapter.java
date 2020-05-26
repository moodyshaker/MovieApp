package com.example.MovieDB.ui.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.MovieDB.R;

import java.util.List;

public class MovieReleaseYearAdapter extends RecyclerView.Adapter<MovieReleaseYearAdapter.MovieReleaseViewHolder> {
    private Context context;
    private List<String> releaseYearList;
    private ReleaseDateOnClickListener releaseDateOnClickListener;
    private int selectedPosition = 0;

    public MovieReleaseYearAdapter(Context context, List<String> releaseYearList, ReleaseDateOnClickListener releaseDateOnClickListener) {
        this.context = context;
        this.releaseYearList = releaseYearList;
        this.releaseDateOnClickListener = releaseDateOnClickListener;
    }

    @NonNull
    @Override
    public MovieReleaseViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.release_date_item_view, parent, false);
        return new MovieReleaseViewHolder(v);
    }


    @Override
    public void onBindViewHolder(@NonNull MovieReleaseViewHolder holder, int position) {
        String releaseYearItem = releaseYearList.get(position);
        holder.dataBind(releaseYearItem);
    }

    @Override
    public int getItemCount() {
        return releaseYearList != null ? releaseYearList.size() : 0;
    }

    public class MovieReleaseViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView releaseYearTV;
        LinearLayout container;
        String releaseDate;

        public MovieReleaseViewHolder(@NonNull View v) {
            super(v);
            v.setOnClickListener(this);
            releaseYearTV = v.findViewById(R.id.release_year_tv);
            container = v.findViewById(R.id.release_date_container);
        }

        private void dataBind(String releaseDate) {
            this.releaseDate = releaseDate;
            releaseYearTV.setText(releaseDate);
            if (selectedPosition == -1) {
                container.setBackgroundResource(R.drawable.release_date_unclicked);
                releaseYearTV.setTextColor(context.getResources().getColor(R.color.gray_600));
            } else {
                if (selectedPosition == getAdapterPosition()) {
                    container.setBackgroundResource(R.drawable.release_date_clicked);
                    releaseYearTV.setTextColor(context.getResources().getColor(R.color.white));
                } else {
                    container.setBackgroundResource(R.drawable.release_date_unclicked);
                    releaseYearTV.setTextColor(context.getResources().getColor(R.color.gray_600));
                }
            }
        }

        @Override
        public void onClick(View v) {
            container.setBackgroundResource(R.drawable.release_date_clicked);
            releaseDateOnClickListener.releaseDateOnClick(releaseDate);
                releaseYearTV.setTextColor(context.getResources().getColor(R.color.white));
                    if (selectedPosition != getAdapterPosition()) {
                        notifyItemChanged(selectedPosition);
                        selectedPosition = getAdapterPosition();
                    }
        }
    }

    public interface ReleaseDateOnClickListener {
        void releaseDateOnClick(String releaseItem);
    }

    public void setSelected(int pos){
        selectedPosition = pos;
    }
}
