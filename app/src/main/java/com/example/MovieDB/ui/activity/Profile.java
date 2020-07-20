package com.example.MovieDB.ui.activity;

import android.content.Context;
import android.content.Intent;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.example.MovieDB.R;
import com.example.MovieDB.persistance.sharedpreferences.MovieSharedPreference;
import com.facebook.login.LoginManager;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.squareup.picasso.Picasso;

public class Profile extends AppCompatActivity {

    private Context context = this;
    private TextView title, name, email;
    private ImageView userIcon;
    private Toolbar toolbar;
    private Button update, delete, signOut, changePassword;
    private MovieSharedPreference.UserPreferences userPreferences;
    private GoogleSignInOptions gso;
    private GoogleSignInClient client;
    private ActionBar actionBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        userIcon = findViewById(R.id.user_picture);
        name = findViewById(R.id.name);
        email = findViewById(R.id.email);
        update = findViewById(R.id.update_account);
        delete = findViewById(R.id.delete_account);
        changePassword = findViewById(R.id.change_password);
        signOut = findViewById(R.id.sign_out);
        toolbar = findViewById(R.id.toolbar);
        title = toolbar.findViewById(R.id.title);
        setSupportActionBar(toolbar);
        actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
        toolbar.getNavigationIcon().setColorFilter(getResources().getColor(R.color.white), PorterDuff.Mode.SRC_ATOP);
        userPreferences = MovieSharedPreference.UserPreferences.getUserPreference(context);
        title.setText("User Account");
        gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();
        client = GoogleSignIn.getClient(context, gso);
        switch (userPreferences.getType()) {
            case "GOOGLE":
                signOut.setOnClickListener(click -> googleSignOut());
                break;
            case "FACBOOK":
                signOut.setOnClickListener(click -> facebookSignOut());
                break;
            case "USER_PASSWORD":
                signOut.setOnClickListener(click -> userLogOut());
                break;
        }
        if (!userPreferences.getID().isEmpty()) {
            if (!userPreferences.getImage().isEmpty()) {
                Picasso.get().load(userPreferences.getImage()).into(userIcon);
            }
            name.setText(userPreferences.getUsername());
            email.setText(userPreferences.getEmail());
        }
        if (userPreferences.getType().equals("GOOGLE")) {
            delete.setVisibility(View.GONE);
            update.setVisibility(View.GONE);
            changePassword.setVisibility(View.GONE);
        } else if (userPreferences.getType().equals("FACEBOOK")) {
            delete.setVisibility(View.GONE);
            update.setVisibility(View.GONE);
            changePassword.setVisibility(View.GONE);
        }
        update.setOnClickListener(click -> {

        });
        delete.setOnClickListener(click -> {

        });
        changePassword.setOnClickListener(click -> {

        });
    }

    private void userLogOut() {
        if (userPreferences.getID().length() > 0) {
            FirebaseAuth.getInstance().signOut();
            userPreferences.logOut();
            Intent i = new Intent(context, LoginActivty.class);
            startActivity(i);
        }
    }

    private void facebookSignOut() {
        if (userPreferences.getID().length() > 0) {
            LoginManager.getInstance().logOut();
            userPreferences.logOut();
            Intent i = new Intent(context, LoginActivty.class);
            startActivity(i);
        }
    }

    private void googleSignOut() {
        if (userPreferences.getID().length() > 0) {
            client.signOut().addOnCompleteListener(task -> {
                userPreferences.logOut();
                Intent i = new Intent(context, LoginActivty.class);
                startActivity(i);
            });
        }
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            onBackPressed();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }
}
