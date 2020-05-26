package com.example.MovieDB.ui.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.MovieDB.R;
import com.example.MovieDB.persistance.sharedpreferences.MovieSharedPreference;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.GraphRequest;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.Task;

import org.json.JSONException;

import java.util.Arrays;

public class LoginActivty extends AppCompatActivity {
    Context context = this;
    Activity activity = this;
    private LoginButton loginButton;
    private CallbackManager callbackManager;
    private MovieSharedPreference.UserPreferences userPreferences;
    private GoogleSignInOptions gso;
    private GoogleSignInClient client;
    private static final int GOOGLE_SING_IN = 4300;
    private SignInButton googleSingIn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_activty);
        loginButton = findViewById(R.id.facebook_login_button);
        googleSingIn = findViewById(R.id.google_sign_in);
        googleSingIn.setOnClickListener(click -> signIn());
        loginButton.setPermissions(Arrays.asList("email", "public_profile"));
        userPreferences = MovieSharedPreference.UserPreferences.getUserPreference(context);
        callbackManager = CallbackManager.Factory.create();
        gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();
        client = GoogleSignIn.getClient(context, gso);
        loginButton.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                GraphRequest request = GraphRequest.newMeRequest(loginResult.getAccessToken(), (object, response) -> {
                    Log.e("hello", object.toString());
                    Log.e("hello", response.toString());
                    try {
                        String name = object.getString("name");
                        String id = object.getString("id");
                        String image = object.getJSONObject("picture").getJSONObject("data").getString("url");
                        String email = object.getString("email");
                        goSplash(id, name, email, image, "FACEBOOK");
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                });
                Bundle bundle = new Bundle();
                bundle.putString("fields", "id,name,email,picture.type(large)");
                request.setParameters(bundle);
                request.executeAsync();
            }

            @Override
            public void onCancel() {
                Log.e("hello", "cancel");
            }

            @Override
            public void onError(FacebookException exception) {
                Log.e("hello", "error");
            }
        });
    }

    private void goSplash(String id, String name, String email, String image, String type) {
        userPreferences.putEmail(email);
        userPreferences.putID(id);
        userPreferences.putUsername(name);
        userPreferences.putImageUri(image);
        userPreferences.putType(type);
        Intent i = new Intent(context, Splash.class);
        startActivity(i);
    }

    private void signIn() {
        Intent signIn = client.getSignInIntent();
        startActivityForResult(signIn, GOOGLE_SING_IN);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        callbackManager.onActivityResult(requestCode, resultCode, data);
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == GOOGLE_SING_IN && resultCode == RESULT_OK) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                GoogleSignInAccount account = task.getResult(ApiException.class);
                if (account != null) {
                    goSplash(account.getId(), account.getDisplayName(), account.getEmail(), account.getPhotoUrl().toString(), "GOOGLE");
                }
            } catch (ApiException e) {
                e.printStackTrace();
            }
        }
    }
}
