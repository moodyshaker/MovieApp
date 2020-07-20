package com.example.MovieDB.ui.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.util.Patterns;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

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
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import org.json.JSONException;

import java.util.Arrays;

public class LoginActivty extends AppCompatActivity {
    Context context = this;
    Activity activity = this;
    private static final String TAG = "TAG";
    private LoginButton loginButton;
    private CallbackManager callbackManager;
    private MovieSharedPreference.UserPreferences userPreferences;
    private GoogleSignInOptions gso;
    private GoogleSignInClient client;
    private static final int GOOGLE_SING_IN = 4300;
    private SignInButton googleSingIn;
    private Button signInButton;
    private FirebaseAuth auth;
    private ProgressBar progressBar;
    TextInputLayout emailTextInput,
            passwordTextInput;
    EditText emailET,
            passwordET;
    private TextView createNew;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_activty);
        loginButton = findViewById(R.id.facebook_login_button);
        googleSingIn = findViewById(R.id.google_sign_in);
        createNew = findViewById(R.id.create_new_account);
        signInButton = findViewById(R.id.sign_in_button);
        progressBar = findViewById(R.id.sign_in_progressbar);
        emailET = findViewById(R.id.user_name_email);
        passwordET = findViewById(R.id.user_name_password);
        emailTextInput = findViewById(R.id.email_text_input);
        passwordTextInput = findViewById(R.id.password_text_input);
        createNew.setOnClickListener(c -> {
            Intent intent = new Intent(context, SignUp.class);
            startActivity(intent);
            finish();
        });
        signInButton.setOnClickListener(click -> {
            String email, password;
            email = emailET.getText().toString();
            password = passwordET.getText().toString();
            if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                emailTextInput.setError("please enter your email");
                emailTextInput.requestFocus();
                return;
            } else if (password.length() < 8) {
                passwordTextInput.setError("please enter your password");
                passwordTextInput.requestFocus();
                return;
            } else {
                signInButton.setVisibility(View.GONE);
                progressBar.setVisibility(View.VISIBLE);
                signInWithFirebase(email, password);
            }
        });
        googleSingIn.setOnClickListener(click -> signIn());
        auth = FirebaseAuth.getInstance();
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
            }

            @Override
            public void onError(FacebookException exception) {
            }
        });
    }

    private void goSplash(String id, String name, String email, String image, String type) {
        if (type == "FACEBOOK" || type == "GOOGLE") {
            userPreferences.putEmail(email);
            userPreferences.putID(id);
            userPreferences.putUsername(name);
            userPreferences.putImageUri(image);
            userPreferences.putType(type);
            Intent i = new Intent(context, Splash.class);
            startActivity(i);
        } else if (type == "USER_PASSWORD") {
            userPreferences.putEmail(email);
            userPreferences.putUsername(name);
            userPreferences.putID(id);
            userPreferences.putImageUri(image);
        }
    }

    private void signIn() {
        Intent signIn = client.getSignInIntent();
        startActivityForResult(signIn, GOOGLE_SING_IN);
    }

    private void signInWithFirebase(String email, String password) {
        Log.d(TAG, "sign in: " + email + " " + password);
        auth.signInWithEmailAndPassword(email, password).addOnCompleteListener(this, task -> {
            if (task.isSuccessful()) {
                Log.d(TAG, "Success");
                FirebaseUser user = auth.getCurrentUser();
                Log.d(TAG, "UserId: " + user.getUid() + "\n*UserEmail: " + user.getEmail() + "\n*UserName: " + user.getDisplayName() + "\n*ImageUrl: " + user.getPhotoUrl());
                goSplash(user.getUid(), user.getDisplayName(), user.getEmail(), String.valueOf(user.getPhotoUrl()), "USER_PASSWORD");
                Log.d(TAG, "signInWithFirebase: " + user.getPhotoUrl());
                new Handler().postDelayed(() -> {
                    signInButton.setText("DONE");
                    signInButton.setVisibility(View.VISIBLE);
                    progressBar.setVisibility(View.GONE);
                }, 1500);
                Intent i = new Intent(context, NowPlaying_OnTheAir.class);
                startActivity(i);
            } else {
                Log.d(TAG, "Error: " + task.getException().getMessage());
                if (task.getException().getMessage().equals("The password is invalid or the user does not have a password.")) {
                    new Handler().postDelayed(() -> {
                        signInButton.setText("SIGN IN");
                        signInButton.setVisibility(View.VISIBLE);
                        progressBar.setVisibility(View.GONE);
                        Toast.makeText(context, "Please Check Your Password", Toast.LENGTH_SHORT).show();
                    }, 1500);
                } else if (task.getException().getMessage().equals("There is no user record corresponding to this identifier. The user may have been deleted.")) {
                    new Handler().postDelayed(() -> {
                        signInButton.setText("SIGN IN");
                        signInButton.setVisibility(View.VISIBLE);
                        progressBar.setVisibility(View.GONE);
                        Toast.makeText(context, "There is no account corresponding to that", Toast.LENGTH_SHORT).show();
                    }, 1500);
                }
            }
        });
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

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
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
