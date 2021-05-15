package com.example.MovieDB.ui.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.MovieDB.R;
import com.example.MovieDB.model.BSDObject;

import java.util.List;

public class BottomSheetAdapter extends BaseAdapter {
    private Context context;
    private List<BSDObject> items;

    public BottomSheetAdapter(Context context, List<BSDObject> items) {
        this.context = context;
        this.items = items;
    }

    @Override
    public int getCount() {
        return items.size();
    }

    @Override
    public Object getItem(int position) {
        return items.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int i, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(parent.getContext()).inflate(R.layout.bottom_sheet_dialog_item, parent, false);
        }
        BSDObject o = items.get(i);
        TextView actionText = convertView.findViewById(R.id.action_tv);
        ImageView actionIcon = convertView.findViewById(R.id.resource_icon);
        actionText.setTextColor(context.getResources().getColor(o.getColor()));
        actionText.setText(o.getAction());
        actionIcon.setImageResource(o.getResource());
        return convertView;
    }
}
