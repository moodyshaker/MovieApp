package com.example.MovieDB.ui.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.widget.Toolbar;

import com.example.MovieDB.R;
import com.example.MovieDB.persistance.sharedpreferences.MovieSharedPreference;
import com.facebook.login.LoginManager;
import com.facebook.login.widget.LoginButton;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.SignInButton;
import com.squareup.picasso.Picasso;

public class Profile extends NavigationViewActivity {

    private Context context = this;
    private FrameLayout frameLayout;
    private TextView title, name, email;
    private ImageView userIcon;
    private Toolbar toolbar;
    private String headTitle;
    private MovieSharedPreference.UserPreferences userPreferences;
    private LoginButton facebookLogin;
    private SignInButton googleSignIn;
    private GoogleSignInOptions gso;
    private GoogleSignInClient client;
    private TextView googleSignInText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View contentView = inflater.inflate(R.layout.activity_profile, null, false);
        frameLayout = drawerLayout.findViewById(R.id.frame_layout_holder);
        userIcon = contentView.findViewById(R.id.user_picture);
        name = contentView.findViewById(R.id.name);
        email = contentView.findViewById(R.id.email);
        facebookLogin = contentView.findViewById(R.id.facebook_login);
        googleSignIn = contentView.findViewById(R.id.google_sign_in);
        toolbar = findViewById(R.id.toolbar);
        title = toolbar.findViewById(R.id.title);
        frameLayout.addView(contentView);
        googleSignInText = (TextView) googleSignIn.getChildAt(0);
        userPreferences = MovieSharedPreference.UserPreferences.getUserPreference(context);
        headTitle = getIntent().getStringExtra("title");
        if (headTitle != null) {
            title.setText(headTitle);
        } else {
            title.setText(getResources().getString(R.string.now_playing));
        }
        gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();
        client = GoogleSignIn.getClient(context, gso);
        googleSignIn.setOnClickListener(click -> googleSignOut());
        facebookLogin.setOnClickListener(click -> facebookSignOut());
        if (userPreferences.getType().equals("GOOGLE") && userPreferences.getID().length() > 0) {
            googleSignInText.setText(getResources().getString(R.string.logout_google));
        }
        if (!userPreferences.getID().isEmpty()) {
            if (!userPreferences.getImage().isEmpty()) {
                Picasso.get().load(userPreferences.getImage()).into(userIcon);
            }
            name.setText(userPreferences.getUsername());
            email.setText(userPreferences.getEmail());
        }

        if (userPreferences.getType().equals("GOOGLE")) {
            facebookLogin.setVisibility(View.GONE);
        } else if (userPreferences.getType().equals("FACEBOOK")) {
            googleSignIn.setVisibility(View.GONE);
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
}
