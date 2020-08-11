package com.example.MovieDB.ui.activity;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.util.Log;
import android.util.Patterns;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import com.example.MovieDB.R;
import com.example.MovieDB.endpoints.AppConstants;
import com.example.MovieDB.persistance.sharedpreferences.MovieSharedPreference;
import com.example.MovieDB.receivers.NetworkReceiver;
import com.example.MovieDB.utils.Utils;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;

import java.io.File;

public class Registration extends AppCompatActivity implements NetworkReceiver.NetworkCallbackListener {
    private TextView accountExist, googleText;
    private Context context = this;
    private Activity activity = this;
    private Button signUpButton;
    private ImageView profile;
    private EditText firstET,
            lastET,
            emailET,
            confirmEmailET,
            passwordET,
            confirmPasswordET;
    private FirebaseAuth auth;
    private MovieSharedPreference.UserPreferences userPreferences;
    private TextInputLayout firstTextInput,
            lastTextInput,
            emailTextInput,
            passwordTextInput,
            confirmEmailTextInput,
            confirmPasswordTextInput;
    private String first, last, email, password, confirmEmail, confirmPassword;
    private Dialog dialog;
    private Handler handler;
    private boolean intentHasExtra = false;
    private String intentExtraFromWhere, intentExtraWhich;
    private Intent i;
    private ProgressBar signUpProgressbar;
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
        setContentView(R.layout.activity_registration);
        initUi();
    }

    private void initUi() {
        auth = FirebaseAuth.getInstance();
        userPreferences = MovieSharedPreference.UserPreferences.getUserPreference(context);
        accountExist = findViewById(R.id.account_exist);
        firstET = findViewById(R.id.user_name_first_name);
        lastET = findViewById(R.id.user_name_last_name);
        emailET = findViewById(R.id.user_name_email);
        confirmEmailET = findViewById(R.id.confirm_user_name_email);
        passwordET = findViewById(R.id.user_name_password);
        confirmPasswordET = findViewById(R.id.user_name_password_confirm);
        firstTextInput = findViewById(R.id.first_name_text_input);
        lastTextInput = findViewById(R.id.last_name_text_input);
        emailTextInput = findViewById(R.id.email_text_input);
        passwordTextInput = findViewById(R.id.confirm_email_text_input);
        confirmEmailTextInput = findViewById(R.id.password_text_input);
        confirmPasswordTextInput = findViewById(R.id.confirm_password_text_input);
        signUpButton = findViewById(R.id.sign_up_button);
        signUpProgressbar = findViewById(R.id.sign_up_progressbar);
        connectionDialog = Utils.showDisconnectionDialog(context);
        connectedContainer = connectionDialog.findViewById(R.id.connected_container);
        disconnectedContainer = connectionDialog.findViewById(R.id.disconnected_container);
        connectionDialog = Utils.showDisconnectionDialog(context);
        connectedContainer = connectionDialog.findViewById(R.id.connected_container);
        disconnectedContainer = connectionDialog.findViewById(R.id.disconnected_container);
        h = new Handler();
        profile = findViewById(R.id.user_image);
        dialog = Utils.transparentDialog(context);
        h = new Handler();
        handler = new Handler();
        filter = new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION);
        receiver = new NetworkReceiver();
        receiver.setListener(this);
        i = getIntent();
        intentHasExtra = i.hasExtra("FROM_WHERE");
        intentExtraFromWhere = i.getStringExtra("FROM_WHERE");
        intentExtraWhich = i.getStringExtra("WHICH");
        accountExist.setOnClickListener(c -> Utils.goActivityWithFinish(activity, Login.class));
        profile.setOnClickListener(c -> chooseDialog());
        signUpButton.setOnClickListener(click -> {
            first = firstET.getText().toString();
            last = lastET.getText().toString();
            email = emailET.getText().toString();
            password = passwordET.getText().toString();
            confirmEmail = confirmEmailET.getText().toString();
            confirmPassword = confirmPasswordET.getText().toString();
            if (TextUtils.isEmpty(first)) {
                firstTextInput.setError("Please check your first name");
                firstTextInput.requestFocus();
            } else if (TextUtils.isEmpty(last)) {
                lastTextInput.setError("Please check your last name");
                lastTextInput.requestFocus();
            } else if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                emailET.setError("Please check your email address");
                emailTextInput.requestFocus();
            } else if (!Patterns.EMAIL_ADDRESS.matcher(confirmEmail).matches() || !confirmEmail.equals(email)) {
                confirmEmailTextInput.setError("Please check your email address");
                confirmEmailTextInput.requestFocus();
            } else if (password.length() < 8) {
                passwordTextInput.setError("Please check your email address");
                passwordTextInput.requestFocus();
            } else if (confirmPassword.length() < 8 || !confirmPassword.equals(password)) {
                confirmPasswordTextInput.setError("Please check your email address");
                confirmPasswordTextInput.requestFocus();
            } else {
                confirmEmailTextInput.setErrorEnabled(false);
                confirmPasswordTextInput.setErrorEnabled(false);
                passwordTextInput.setErrorEnabled(false);
                emailTextInput.setErrorEnabled(false);
                firstTextInput.setErrorEnabled(false);
                lastTextInput.setErrorEnabled(false);
                signUpButton.setVisibility(View.GONE);
                signUpProgressbar.setVisibility(View.VISIBLE);
                signUpWithFirebase(email, password);
            }
        });

    }


    private void chooseDialog() {
        Dialog d = new Dialog(context);
        d.setContentView(R.layout.choose_image_dialog);
        d.getWindow().setLayout(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        LinearLayout camera = d.findViewById(R.id.camera_image);
        LinearLayout photo = d.findViewById(R.id.photo_image);
        camera.setOnClickListener(cam -> {
            Utils.openCamera(activity);
            d.dismiss();
        });
        photo.setOnClickListener(pho -> {
            if (Utils.checkPermissionForPhoto(context)) {
                Utils.openCamera(activity);
            } else {
                Utils.requestPermissionForPhoto(activity);
            }
            d.dismiss();
        });
        d.show();
    }


    private void goSplash(String id, String name, String email, Uri image, String type) {
        dialog.show();
        handler.postDelayed(() -> {
            dialog.dismiss();
            userPreferences.putEmail(email);
            userPreferences.putUsername(name);
            userPreferences.putID(id);
            if (image != null) {
                userPreferences.putImageUri(String.valueOf(image));
            }
            userPreferences.putType(type);
            if (intentHasExtra) {
                Log.d(AppConstants.TAG, "goSplash: " + intentHasExtra);
                Log.d(AppConstants.TAG, "goSplash: " + intentExtraFromWhere);
                Log.d(AppConstants.TAG, "goSplash: " + intentExtraWhich);
                switch (intentExtraFromWhere) {
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
                        si.putExtra("series_object", seriesJson);
                        si.putExtra("WHICH", intentExtraWhich);
                        startActivity(si);
                        finish();
                        break;
                }
            } else {
                Utils.goActivityWithFinish(activity, NowPlaying_OnTheAir.class);
            }
        }, 1000);

    }

    private void signUpWithFirebase(String email, String password) {
        Log.d(AppConstants.TAG, "sign up: " + email + " " + password);
        auth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(this, task -> {
            if (task.isSuccessful()) {
                Log.d(AppConstants.TAG, "signUpWithFirebase: " + userPreferences.getImage());
                Log.d(AppConstants.TAG, "Success");
                FirebaseUser user = auth.getCurrentUser();
                UserProfileChangeRequest userProfile = new UserProfileChangeRequest
                        .Builder()
                        .setDisplayName(first + " " + last)
                        .setPhotoUri(Uri.parse(userPreferences.getImage()))
                        .build();
                user.updateProfile(userProfile).addOnCompleteListener(t -> {
                    if (t.isSuccessful()) {
                        signUpButton.setVisibility(View.VISIBLE);
                        signUpProgressbar.setVisibility(View.GONE);
                        goSplash(user.getUid(), user.getDisplayName(), user.getEmail(), user.getPhotoUrl(), "USER_PASSWORD");
                    }
                });
            } else {
                signUpButton.setVisibility(View.VISIBLE);
                signUpProgressbar.setVisibility(View.GONE);
                Log.d(AppConstants.TAG, "Error: " + task.getException().getMessage());
                if (task.getException().getMessage().equals("The email address is already in use by another account.")) {
                    Toast.makeText(context, "This email is already in use", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == AppConstants.REQUEST_CODE_PERMISSION && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            Utils.openGallery(activity);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == AppConstants.IMAGE_GALLERY_REQUEST && resultCode == RESULT_OK) {
            Uri uri = data.getData();
            String path = Utils.getPathFromURI(context, uri);
            if (path != null) {
                File file = new File(path);
                Uri uriPath = Uri.fromFile(file);
                profile.setImageURI(uriPath);
                userPreferences.putImageUri(String.valueOf(uriPath));
            }
        } else if (requestCode == AppConstants.IMAGE_CAPTURE_REQUEST && resultCode == RESULT_OK) {
            Bitmap bitmap = (Bitmap) data.getExtras().get("data");
            File f = Utils.saveFile(context, bitmap);
            Uri fileUri = Uri.fromFile(f);
            profile.setImageBitmap(bitmap);
            userPreferences.putImageUri(fileUri.toString());
            Log.d(AppConstants.TAG, "onActivityResult: " + fileUri);
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