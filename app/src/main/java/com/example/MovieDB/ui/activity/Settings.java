package com.example.MovieDB.ui.activity;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;
import androidx.core.view.GravityCompat;

import com.example.MovieDB.R;
import com.example.MovieDB.persistance.sharedpreferences.MovieSharedPreference;
import com.squareup.picasso.Picasso;

public class Settings extends NavigationViewActivity {

    private Context context = this;
    private CardView wishList, seenList;
    private LinearLayout profile;
    private FrameLayout frameLayout;
    private Toolbar toolbar;
    private TextView title, name;
    private String headTitle;
    private ImageView userIcon;
    private MovieSharedPreference.UserPreferences userPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View contentView = inflater.inflate(R.layout.activity_settings, null, false);
        frameLayout = drawerLayout.findViewById(R.id.frame_layout_holder);
        toolbar = findViewById(R.id.toolbar);
        title = toolbar.findViewById(R.id.title);
        userIcon = contentView.findViewById(R.id.user_picture);
        name = contentView.findViewById(R.id.name);
        frameLayout.addView(contentView);
        headTitle = getIntent().getStringExtra("title");
        title.setText(headTitle);
        wishList = findViewById(R.id.wishList_container);
        seenList = findViewById(R.id.seenlist_container);
        profile = findViewById(R.id.profile_container);
        userPreferences = MovieSharedPreference.UserPreferences.getUserPreference(context);
        if (!userPreferences.getID().isEmpty()) {
            if (!userPreferences.getImage().isEmpty()) {
                Picasso.get().load(userPreferences.getImage()).into(userIcon);
            }
            name.setText(getResources().getString(R.string.profile_name, userPreferences.getUsername()));
        }
        seenList.setOnClickListener(c -> {
            Intent i = new Intent(context, Seenlist.class);
            startActivity(i);
        });
        wishList.setOnClickListener(c -> {
            Intent i = new Intent(context, Wishlist.class);
            startActivity(i);
        });
        profile.setOnClickListener(c -> {
            Intent i = new Intent(context, Profile.class);
            startActivity(i);
        });
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawers();
            return true;
        }
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            exitDialog();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    private void exitDialog() {
        AlertDialog.Builder dialog = new AlertDialog.Builder(context);
        dialog.setTitle("Exit");
        dialog.setIcon(R.drawable.movie_icon);
        dialog.setMessage("Do you want to exit ?");
        dialog.setPositiveButton("Yes", (dialog1, which) -> {
            finishAffinity();
        });
        dialog.setNegativeButton("No", (dialog1, which) -> {

        });
        dialog.setCancelable(false);
        dialog.show();
    }
}