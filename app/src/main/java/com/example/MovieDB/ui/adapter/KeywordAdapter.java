package com.example.MovieDB.ui.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.MovieDB.R;
import com.example.MovieDB.model.keywords.Keyword;
import com.example.MovieDB.model.keywords.SeriesKeywords;
import com.example.MovieDB.model.keywords_from_search.KeywordResult;

import java.util.List;

public class KeywordAdapter<E> extends RecyclerView.Adapter<KeywordAdapter.KeywordViewHolder> {

    private Context context;
    private List<E> keywordList;
    private OnMovieKeywordClickListener<E> clickListener;

    public KeywordAdapter(Context context, OnMovieKeywordClickListener<E> clickListener) {
        this.context = context;
        this.clickListener = clickListener;
    }

    public List<E> getKeywordList() {
        return keywordList;
    }

    public void setKeywordList(List<E> keywordList) {
        this.keywordList = keywordList;
    }

    @NonNull
    @Override
    public KeywordViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.keyword_item, parent, false);
        return new KeywordViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull KeywordAdapter.KeywordViewHolder holder, int position) {
        E object = keywordList.get(position);
        holder.dataBinding(object);
    }

    @Override
    public int getItemCount() {
        if (keywordList != null) {
            return keywordList.size();
        } else {
            return 0;
        }
    }

    public class KeywordViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView keywordText;
        E object;

        KeywordViewHolder(View view) {
            super(view);
            view.setOnClickListener(this);
            keywordText = view.findViewById(R.id.keyword_text);
        }

        private void dataBinding(E object) {
            this.object = object;
            if (object instanceof Keyword) {
                keywordText.setText(((Keyword) object).getName());
            } else if (object instanceof KeywordResult) {
                keywordText.setText(((KeywordResult) object).getName());
            }else if (object instanceof SeriesKeywords) {
                keywordText.setText(((SeriesKeywords) object).getName());
            }
        }

        @Override
        public void onClick(View v) {
            clickListener.OnMovieKeywordClick(object);
        }
    }

    public interface OnMovieKeywordClickListener<E> {
        void OnMovieKeywordClick(E object);
    }
}
