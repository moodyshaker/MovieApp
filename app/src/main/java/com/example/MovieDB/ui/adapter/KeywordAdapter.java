package com.example.MovieDB.ui.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.MovieDB.R;
import com.example.MovieDB.model.data.keywords.Keyword;

import java.util.List;

public class KeywordAdapter extends RecyclerView.Adapter<KeywordAdapter.KeywordViewHolder> {

    private Context context;
    private List<Keyword> keywordList;

    public KeywordAdapter(Context context, List<Keyword> keywordList) {
        this.context = context;
        this.keywordList = keywordList;
    }

    @NonNull
    @Override
    public KeywordViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.keyword_item, parent, false);
        return new KeywordViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull KeywordViewHolder holder, int position) {
        Keyword keyword = keywordList.get(position);
        holder.dataBinding(keyword);
    }

    @Override
    public int getItemCount() {
        if (keywordList != null) {
            return keywordList.size();
        } else {
            return 0;
        }
    }

    public class KeywordViewHolder extends RecyclerView.ViewHolder {
        TextView keywordText;
        KeywordViewHolder(View view) {
            super(view);
            keywordText = view.findViewById(R.id.keyword_text);
        }

        private void dataBinding(Keyword keyword) {
            keywordText.setText(keyword.getName());
        }
    }
}
