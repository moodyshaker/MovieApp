package com.example.MovieDB.ui.activity;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.PorterDuff;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

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
import com.squareup.picasso.Picasso;

import java.io.File;

import de.hdodenhof.circleimageview.CircleImageView;

public class UpdateAccount extends AppCompatActivity implements NetworkReceiver.NetworkCallbackListener {

    private Context context = this;
    private Activity activity = this;
    private FirebaseUser user;
    private MovieSharedPreference.UserPreferences userPreferences;
    private String first, last;
    private EditText firstET, lastET;
    private TextInputLayout firstInput, lastInput;
    private Button saveButton;
    private CircleImageView userImage;
    private TextView title;
    private Toolbar toolbar;
    private ActionBar actionBar;
    private UserProfileChangeRequest profileUpdate;
    private Dialog dialog;
    private Handler handler;
    private String[] arrayName;
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
        setContentView(R.layout.activity_update_account);
        initUi();
        user = FirebaseAuth.getInstance().getCurrentUser();
        userPreferences = MovieSharedPreference.UserPreferences.getUserPreference(context);
        dialog = Utils.transparentDialog(context);
        handler = new Handler();
        setSupportActionBar(toolbar);
        actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
        toolbar.getNavigationIcon().setColorFilter(getResources().getColor(R.color.blue_gray_100),
                PorterDuff.Mode.SRC_ATOP);
        filter = new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION);
        receiver = new NetworkReceiver();
        receiver.setListener(this);
        title.setText("Update Account");
        arrayName = userPreferences.getUsername().split(" ");
        Log.d(AppConstants.TAG, "array name: " + arrayName[0] + " " + arrayName[1]);
        Picasso.get().load(userPreferences.getImage()).into(userImage);
        firstET.setText(arrayName[0]);
        lastET.setText(arrayName[1]);
        userImage.setOnClickListener(click -> {
            chooseDialog();
        });
        saveButton.setOnClickListener(click -> {
            first = firstET.getText().toString();
            last = lastET.getText().toString();
            if (TextUtils.isEmpty(first)) {
                firstInput.setError("Please check your first name");
                firstInput.requestFocus();
            } else if (TextUtils.isEmpty(last)) {
                lastInput.setError("Please check your last name");
                lastInput.requestFocus();
            } else {
                dialog.show();
                handler.postDelayed(() -> updateProfile(first, last), 1000);
            }
        });
    }

    private void initUi() {
        toolbar = findViewById(R.id.toolbar);
        title = toolbar.findViewById(R.id.title);
        userImage = findViewById(R.id.user_image);
        firstET = findViewById(R.id.user_name_first_name);
        lastET = findViewById(R.id.user_name_last_name);
        firstInput = findViewById(R.id.first_name_text_input);
        lastInput = findViewById(R.id.last_name_text_input);
        saveButton = findViewById(R.id.save_button);
        connectionDialog = Utils.showDisconnectionDialog(context);
        connectedContainer = connectionDialog.findViewById(R.id.connected_container);
        disconnectedContainer = connectionDialog.findViewById(R.id.disconnected_container);
        h = new Handler();
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
                Utils.openGallery(activity);
            } else {
                Utils.requestPermissionForPhoto(activity);
            }
            d.dismiss();
        });
        d.show();
    }

    private void updateProfile(String first, String last) {
        profileUpdate = new UserProfileChangeRequest.Builder()
                .setDisplayName(first + " " + last)
                .setPhotoUri(Uri.parse(userPreferences.getImage()))
                .build();
        user.updateProfile(profileUpdate).addOnCompleteListener(task -> {
            Log.d(AppConstants.TAG, "updateProfile: " + user.getDisplayName() + " " + user.getPhotoUrl());
            if (task.isSuccessful()) {
                dialog.dismiss();
                userPreferences.putUsername(user.getDisplayName());
                userPreferences.putImageUri(String.valueOf(user.getPhotoUrl()));
                Log.d(AppConstants.TAG, "updateProfile: successfully uploaded and updated");
                Utils.messageDialog(activity, "Update Profile", "Profile updated successfully");
            } else {
                dialog.dismiss();
                Log.d(AppConstants.TAG, "updateProfile: " + task.getException().getMessage());
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
            Log.d(AppConstants.TAG, "String path: " + path);
            if (path != null) {
                File file = new File(path);
                Uri uriPath = Uri.fromFile(file);
                userImage.setImageURI(uriPath);
                userPreferences.putImageUri(String.valueOf(uriPath));
                Log.d(AppConstants.TAG, "Uri uriPath: " + uriPath);
            }
        } else if (requestCode == AppConstants.IMAGE_CAPTURE_REQUEST && resultCode == RESULT_OK) {
            Bitmap bitmap = (Bitmap) data.getExtras().get("data");
            File f = Utils.saveFile(context, bitmap);
            Uri fileUri = Uri.fromFile(f);
            userImage.setImageBitmap(bitmap);
            userPreferences.putImageUri(fileUri.toString());
            Log.d(AppConstants.TAG, "onActivityResult: " + fileUri);
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