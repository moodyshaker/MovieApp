package com.example.MovieDB.ui.activity;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.util.Log;
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
import com.example.MovieDB.endpoints.AppConstants;
import com.example.MovieDB.persistance.sharedpreferences.MovieSharedPreference;
import com.example.MovieDB.receivers.NetworkReceiver;
import com.example.MovieDB.utils.Utils;
import com.facebook.login.LoginManager;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.firebase.auth.FirebaseAuth;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;

public class Settings extends NavigationViewActivity implements NetworkReceiver.NetworkCallbackListener {

    private Context context = this;
    private Activity activity = this;
    private CardView wishList, seenList, logOutContainer;
    private LinearLayout profile;
    private FrameLayout frameLayout;
    private Toolbar toolbar;
    private TextView title, name, logoutText, viewProfile;
    private String headTitle;
    private ImageView logoutIcon;
    private CircleImageView userIcon;
    private GoogleSignInOptions gso;
    private GoogleSignInClient client;
    private MovieSharedPreference.UserPreferences userPreferences;
    private Dialog dialog;
    private Handler handler;
    private NetworkReceiver receiver;
    private IntentFilter filter;
    private LinearLayout connectedContainer, disconnectedContainer;
    private BottomSheetDialog connectionDialog;
    private Handler h;

    @Override
    protected void onStart() {
        super.onStart();
        registerReceiver(receiver, filter);
    }

    @Override
    protected void onStop() {
        super.onStop();
        unregisterReceiver(receiver);
    }


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
        logOutContainer = findViewById(R.id.logout_container);
        logoutIcon = findViewById(R.id.logout_icon);
        logoutText = findViewById(R.id.logout_text);
        viewProfile = findViewById(R.id.view_profile);
        dialog = Utils.transparentDialog(context);
        connectionDialog = Utils.showDisconnectionDialog(context);
        connectedContainer = connectionDialog.findViewById(R.id.connected_container);
        disconnectedContainer = connectionDialog.findViewById(R.id.disconnected_container);
        h = new Handler();
        handler = new Handler();
        filter = new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION);
        receiver = new NetworkReceiver();
        receiver.setListener(this);
        gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();
        client = GoogleSignIn.getClient(context, gso);
        userPreferences = MovieSharedPreference.UserPreferences.getUserPreference(context);
        if (userPreferences.isFirstTime()) {
            logoutIcon.setImageResource(R.drawable.login);
            logoutText.setText(getResources().getString(R.string.login));
            if (!userPreferences.getImage().isEmpty()) {
                userIcon.setImageResource(R.drawable.baseline_account_circle_black_36);
            }
            name.setText(getResources().getString(R.string.profile_name_without_sign));
            viewProfile.setVisibility(View.GONE);
        } else {
            logoutIcon.setImageResource(R.drawable.logout);
            logoutText.setText(getResources().getString(R.string.logout));
            if (!userPreferences.getImage().isEmpty()) {
                Picasso.get().load(userPreferences.getImage()).into(userIcon);
            }
            if (!TextUtils.isEmpty(userPreferences.getUsername())) {
                name.setText(getResources().getString(R.string.profile_name_with_sign, userPreferences.getUsername()));
            } else {
                name.setText(getResources().getString(R.string.profile_name_without_sign));
            }
            viewProfile.setVisibility(View.VISIBLE);
        }
        logOutContainer.setOnClickListener(click -> {
            if (logoutText.getText().toString().equals(getResources().getString(R.string.login))) {
                Utils.goActivity(activity, Login.class);
            } else {
                Log.d(AppConstants.TAG, "onCreate: else clicked");
                switch (userPreferences.getType()) {
                    case "GOOGLE":
                        googleSignOut();
                        break;
                    case "FACEBOOK":
                        facebookSignOut();
                        break;
                    case "USER_PASSWORD":
                        userLogOut();
                        break;
                }
            }
        });
        seenList.setOnClickListener(c -> Utils.goActivity(activity, Seenlist.class));
        wishList.setOnClickListener(c -> Utils.goActivity(activity, Wishlist.class));
        profile.setOnClickListener(c -> {
            if (!userPreferences.isFirstTime()) {
                Utils.goActivity(activity, Profile.class);
            }
        });
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawers();
            return true;
        } else if (keyCode == KeyEvent.KEYCODE_BACK) {
            Utils.goActivity(activity, NowPlaying_OnTheAir.class);
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    private void facebookSignOut() {
        Log.d(AppConstants.TAG, "onCreate: else clicked");
        if (userPreferences.getID().length() > 0) {
            Log.d(AppConstants.TAG, "onCreate: else clicked");
            dialog.show();
            handler.postDelayed(() -> {
                FirebaseAuth.getInstance().signOut();
                LoginManager.getInstance().logOut();
                userPreferences.logOut();
                dialog.dismiss();
                Utils.goActivity(activity, NowPlaying_OnTheAir.class);
            }, 1000);
        }
    }

    private void googleSignOut() {
        if (userPreferences.getID().length() > 0) {
            dialog.show();
            handler.postDelayed(() -> client.signOut().addOnCompleteListener(task -> {
                FirebaseAuth.getInstance().signOut();
                userPreferences.logOut();
                dialog.dismiss();
                Utils.goActivity(activity, NowPlaying_OnTheAir.class);
            }), 1000);
        }
    }

    private void userLogOut() {
        if (userPreferences.getID().length() > 0) {
            dialog.show();
            handler.postDelayed(() -> {
                FirebaseAuth.getInstance().signOut();
                userPreferences.logOut();
                dialog.dismiss();
                Utils.goActivity(activity, NowPlaying_OnTheAir.class);
            }, 1000);
        }
    }

    @Override
    public void callbackListener(boolean isConnected) {
        if (isConnected) {
            connectedContainer.setVisibility(View.VISIBLE);
            disconnectedContainer.setVisibility(View.GONE);
            h.postDelayed(() -> connectionDialog.dismiss(), 1000);
        } else {
            connectedContainer.setVisibility(View.GONE);
            disconnectedContainer.setVisibility(View.VISIBLE);
            connectionDialog.show();
        }
    }
}