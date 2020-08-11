package com.example.MovieDB.ui.activity;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.util.Patterns;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import com.example.MovieDB.R;
import com.example.MovieDB.endpoints.AppConstants;
import com.example.MovieDB.persistance.sharedpreferences.MovieSharedPreference;
import com.example.MovieDB.receivers.NetworkReceiver;
import com.example.MovieDB.utils.Utils;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.FacebookAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;

public class Login extends AppCompatActivity implements NetworkReceiver.NetworkCallbackListener {
    private Context context = this;
    private Activity activity = this;
    private LoginButton loginButton;
    private CallbackManager callbackManager;
    private MovieSharedPreference.UserPreferences userPreferences;
    private GoogleSignInOptions gso;
    private GoogleSignInClient client;
    private SignInButton googleSingIn;
    private Button signInButton;
    private FirebaseAuth auth;
    TextInputLayout emailTextInput,
            passwordTextInput;
    EditText emailET,
            passwordET;
    private ProgressBar sigInProgressbar;
    private TextView createNew, googleText, forgetPassword;
    private Dialog dialog;
    private Handler handler;
    private boolean intentHasExtra = false;
    private String intentExtraFromWHere, intentExtraWhich;
    private Intent i;
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
        setContentView(R.layout.activity_login_activty);
        loginButton = findViewById(R.id.facebook_login_button);
        googleSingIn = findViewById(R.id.google_sign_in);
        createNew = findViewById(R.id.create_new_account);
        signInButton = findViewById(R.id.sign_in_button);
        emailET = findViewById(R.id.user_name_email);
        passwordET = findViewById(R.id.user_name_password);
        emailTextInput = findViewById(R.id.email_text_input);
        passwordTextInput = findViewById(R.id.password_text_input);
        sigInProgressbar = findViewById(R.id.sign_in_progressbar);
        forgetPassword = findViewById(R.id.forget_password);
        connectionDialog = Utils.showDisconnectionDialog(context);
        connectedContainer = connectionDialog.findViewById(R.id.connected_container);
        disconnectedContainer = connectionDialog.findViewById(R.id.disconnected_container);
        h = new Handler();
        dialog = Utils.transparentDialog(context);
        handler = new Handler();
        i = getIntent();
        filter = new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION);
        receiver = new NetworkReceiver();
        receiver.setListener(this);
        intentHasExtra = i.hasExtra("FROM_WHERE");
        intentExtraFromWHere = i.getStringExtra("FROM_WHERE");
        intentExtraWhich = i.getStringExtra("WHICH");
        auth = FirebaseAuth.getInstance();
        googleText = (TextView) googleSingIn.getChildAt(0);
        googleText.setText("GOOGLE");
        forgetPassword.setOnClickListener(click -> {
            Intent i = new Intent(context, ForgetPassword.class);
            startActivityForResult(i, AppConstants.FORGET_PASSWORD_REQUEST);
        });
        createNew.setOnClickListener(c -> {
            if (intentHasExtra) {
                Log.d(AppConstants.TAG, "goSplash: " + intentHasExtra);
                Log.d(AppConstants.TAG, "goSplash: " + intentExtraFromWHere);
                Log.d(AppConstants.TAG, "goSplash: " + intentExtraWhich);
                switch (intentExtraFromWHere) {
                    case "MOVIE_DETAILS":
                        String movieJson = i.getStringExtra("movie_object");
                        Intent mi = new Intent(context, Registration.class);
                        mi.putExtra("FROM_WHERE", "MOVIE_DETAILS");
                        mi.putExtra("movie_object", movieJson);
                        mi.putExtra("WHICH", intentExtraWhich);
                        startActivity(mi);
                        finish();
                        break;
                    case "SERIES_DETAILS":
                        String seriesJson = i.getStringExtra("series_object");
                        Intent si = new Intent(context, Registration.class);
                        si.putExtra("FROM_WHERE", "SERIES_DETAILS");
                        si.putExtra("series_object", seriesJson);
                        si.putExtra("WHICH", intentExtraWhich);
                        startActivity(si);
                        finish();
                        break;
                }
            } else {
                Utils.goActivityWithFinish(activity, Registration.class);
            }
        });
        signInButton.setOnClickListener(click -> {
            String email, password;
            email = emailET.getText().toString();
            password = passwordET.getText().toString();
            if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                emailTextInput.setError("please enter your email");
                emailTextInput.requestFocus();
            } else if (password.length() < 8) {
                passwordTextInput.setError("please enter your password");
                passwordTextInput.requestFocus();
            } else {
                signInWithFirebase(email, password);
                signInButton.setVisibility(View.GONE);
                sigInProgressbar.setVisibility(View.VISIBLE);
            }
        });
        googleSingIn.setOnClickListener(click -> signIn());
        callbackManager = CallbackManager.Factory.create();
        loginButton.setPermissions("email", "public_profile");
        userPreferences = MovieSharedPreference.UserPreferences.getUserPreference(context);
        gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.web_client_id))
                .requestEmail()
                .build();
        client = GoogleSignIn.getClient(context, gso);
        loginButton.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                String idToken = loginResult.getAccessToken().getToken();
                firebaseAuthWithFacebook(idToken);
            }

            @Override
            public void onCancel() {
            }

            @Override
            public void onError(FacebookException exception) {
            }
        });
    }

    private void goSplash(String id, String name, String email, Uri image, String type) {
        dialog.show();
        handler.postDelayed(() -> {
            userPreferences.putEmail(email);
            userPreferences.putID(id);
            userPreferences.putUsername(name);
            if (image != null) {
                userPreferences.putImageUri(String.valueOf(image));
            }
            userPreferences.putType(type);
            dialog.dismiss();
            if (intentHasExtra) {
                Log.d(AppConstants.TAG, "goSplash: " + intentHasExtra);
                Log.d(AppConstants.TAG, "goSplash: " + intentExtraFromWHere);
                Log.d(AppConstants.TAG, "goSplash: " + intentExtraWhich);
                switch (intentExtraFromWHere) {
                    case "MOVIE_DETAILS":
                        String movieJson = i.getStringExtra("movie_object");
                        Intent mi = new Intent(context, MovieDetails.class);
                        mi.putExtra("type", "three");
                        mi.putExtra("movie_object", movieJson);
                        mi.putExtra("WHICH", intentExtraWhich);
                        startActivity(mi);
                        finish();
                        break;
                    case "SERIES_DETAILS":
                        String seriesJson = i.getStringExtra("series_object");
                        Intent si = new Intent(context, SeriesDetails.class);
                        si.putExtra("WHICH", intentExtraWhich);
                        si.putExtra("series_object", seriesJson);
                        startActivity(si);
                        finish();
                        break;
                }
            } else {
                Utils.goActivityWithFinish(activity, NowPlaying_OnTheAir.class);
            }
        }, 1000);
    }

    private void signIn() {
        Intent signIn = client.getSignInIntent();
        startActivityForResult(signIn, AppConstants.GOOGLE_SING_IN);
    }

    private void signInWithFirebase(String email, String password) {
        Log.d(AppConstants.TAG, "sign in: " + email + " " + password);
        auth.signInWithEmailAndPassword(email, password).addOnCompleteListener(this, task -> {
            if (task.isSuccessful()) {
                sigInProgressbar.setVisibility(View.GONE);
                signInButton.setVisibility(View.VISIBLE);
                Log.d(AppConstants.TAG, "Success");
                FirebaseUser user = auth.getCurrentUser();
                goSplash(user.getUid(), user.getDisplayName(), user.getEmail(), user.getPhotoUrl(), "USER_PASSWORD");
            } else {
                sigInProgressbar.setVisibility(View.GONE);
                signInButton.setVisibility(View.VISIBLE);
                Log.d(AppConstants.TAG, "Error: " + task.getException().getMessage());
                if (task.getException().getMessage().equals("The password is invalid or the user does not have a password.")) {
                    Toast.makeText(context, "Please Check Your Password", Toast.LENGTH_SHORT).show();
                } else if (task.getException().getMessage().equals("There is no user record corresponding to this identifier. The user may have been deleted.")) {
                    Toast.makeText(context, "There is no account corresponding to that", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        callbackManager.onActivityResult(requestCode, resultCode, data);
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == AppConstants.GOOGLE_SING_IN && resultCode == RESULT_OK) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                GoogleSignInAccount account = task.getResult(ApiException.class);
                if (account != null) {
                    Log.d(AppConstants.TAG, "onActivityResult: " + account.getIdToken());
                    String idToken = account.getIdToken();
                    firebaseAuthWithGoogle(idToken);
                }
            } catch (ApiException e) {
                Log.d(AppConstants.TAG, "onActivityResult: " + e.getMessage());
            }
        } else if (requestCode == AppConstants.FORGET_PASSWORD_REQUEST && resultCode == AppConstants.FORGET_PASSWORD_RESULT) {
            emailET.setText(data.getStringExtra("RESET_PASSWORD"));
            passwordTextInput.requestFocus();
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            onBackPressed();
            finish();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    private void firebaseAuthWithFacebook(String idToken) {
        userPreferences.putToken(idToken);
        AuthCredential credential = FacebookAuthProvider.getCredential(idToken);
        auth.signInWithCredential(credential).addOnCompleteListener(activity, task -> {
            if (task.isSuccessful()) {
                dialog.show();
                FirebaseUser user = auth.getCurrentUser();
                goSplash(user.getUid(), user.getDisplayName(), user.getEmail(), user.getPhotoUrl(), "FACEBOOK");
            } else {
                Log.d(AppConstants.TAG, "firebaseAuthWithFacebook: " + task.getException().getMessage());
            }
        });
    }

    private void firebaseAuthWithGoogle(String idToken) {
        userPreferences.putToken(idToken);
        AuthCredential credential = GoogleAuthProvider.getCredential(idToken, null);
        auth.signInWithCredential(credential).addOnCompleteListener(activity, task -> {
            if (task.isSuccessful()) {
                dialog.show();
                FirebaseUser user = auth.getCurrentUser();
                goSplash(user.getUid(), user.getDisplayName(), user.getEmail(), user.getPhotoUrl(), "GOOGLE");
            } else {
                Log.d(AppConstants.TAG, "firebaseAuthWithGoogle: " + task.getException().getMessage());
            }
        });
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
