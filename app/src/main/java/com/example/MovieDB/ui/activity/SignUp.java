package com.example.MovieDB.ui.activity;

import android.Manifest;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.provider.MediaStore;
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
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

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
import com.google.firebase.auth.UserProfileChangeRequest;

import org.json.JSONException;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.Locale;

public class SignUp extends AppCompatActivity {
    private static final String TAG = "TAG";
    private static final int GOOGLE_SING_IN = 4300;
    private TextView accountExist;
    private Context context = this;
    private Button signUpButton;
    private ProgressBar progressBar;
    private ImageView profile;
    private final static int IMAGE_CAPTURE_REQUEST = 5793;
    private final static int IMAGE_GALLERY_REQUEST = 3490;
    private EditText firstET,
            lastET,
            emailET,
            confirmEmailET,
            passwordET,
            confirmPasswordET;
    private LoginButton loginButton;
    private SignInButton googleSingIn;
    private GoogleSignInOptions gso;
    private GoogleSignInClient client;
    private CallbackManager callbackManager;
    private FirebaseAuth auth;
    private MovieSharedPreference.UserPreferences userPreferences;
    private TextInputLayout firstTextInput,
            lastTextInput,
            emailTextInput,
            passwordTextInput,
            confirmEmailTextInput,
            confirmPasswordTextInput;
    private final static int REQUEST_CODE_PERMISSION = 2913;
    String first, last, email, password, confirmEmail, confirmPassword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);
        initUi();
    }

    private void initUi() {
        auth = FirebaseAuth.getInstance();
        userPreferences = MovieSharedPreference.UserPreferences.getUserPreference(context);
        loginButton = findViewById(R.id.facebook_login_button);
        googleSingIn = findViewById(R.id.google_sign_in);
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
        progressBar = findViewById(R.id.sign_up_progressbar);
        profile = findViewById(R.id.user_image);
        accountExist.setOnClickListener(c -> {
            Intent i = new Intent(context, LoginActivty.class);
            startActivity(i);
            finish();
        });
        googleSingIn.setOnClickListener(click -> signIn());
        loginButton.setPermissions(Arrays.asList("email", "public_profile"));
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
                return;
            } else if (TextUtils.isEmpty(last)) {
                lastTextInput.setError("Please check your last name");
                lastTextInput.requestFocus();
                return;
            } else if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                emailET.setError("Please check your email address");
                emailTextInput.requestFocus();
                return;
            } else if (!Patterns.EMAIL_ADDRESS.matcher(confirmEmail).matches() || !confirmEmail.equals(email)) {
                confirmEmailTextInput.setError("Please check your email address");
                confirmEmailTextInput.requestFocus();
                return;
            } else if (password.length() < 8) {
                passwordTextInput.setError("Please check your email address");
                passwordTextInput.requestFocus();
                return;
            } else if (confirmPassword.length() < 8 || !confirmPassword.equals(password)) {
                confirmPasswordTextInput.setError("Please check your email address");
                confirmPasswordTextInput.requestFocus();
                return;
            } else {
                signUpButton.setVisibility(View.GONE);
                progressBar.setVisibility(View.VISIBLE);
                confirmEmailTextInput.setErrorEnabled(false);
                confirmPasswordTextInput.setErrorEnabled(false);
                passwordTextInput.setErrorEnabled(false);
                emailTextInput.setErrorEnabled(false);
                firstTextInput.setErrorEnabled(false);
                lastTextInput.setErrorEnabled(false);
                signUpWithFirebase(email, password);
            }
        });

    }


    private void chooseDialog() {
        Dialog d = new Dialog(context);
        d.setContentView(R.layout.choos_image_dialog);
        d.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        LinearLayout camera = d.findViewById(R.id.camera_image);
        LinearLayout photo = d.findViewById(R.id.photo_image);
        camera.setOnClickListener(cam -> {
            openCamera();
            d.dismiss();
        });
        photo.setOnClickListener(pho -> {
            checkPermissionForPhoto();
            d.dismiss();
        });
        d.show();
    }


    private void openCamera() {
        Intent i = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(i, IMAGE_CAPTURE_REQUEST);
    }

    private void openGallery() {
        Intent i = new Intent(Intent.ACTION_PICK);
        i.setType("image/*");
        startActivityForResult(i, IMAGE_GALLERY_REQUEST);
    }

    private void checkPermissionForPhoto() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ActivityCompat.checkSelfPermission(context, Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                openGallery();
            } else {
                String[] permissions = {Manifest.permission.READ_EXTERNAL_STORAGE};
                ActivityCompat.requestPermissions(this, permissions, REQUEST_CODE_PERMISSION);
            }
        } else {
            openGallery();
        }
    }

    public String getPathFromURI(Uri contentUri) {
        String res = null;
        String[] proj = {MediaStore.Images.Media.DATA};
        Cursor cursor = getContentResolver().query(contentUri, proj, null, null, null);
        if (cursor != null) {
            if (cursor.moveToFirst()) {
                int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
                res = cursor.getString(column_index);
            }
            cursor.close();
        }
        return res;
    }

    private void goSplash(String id, String name, String email, String image, String type) {
        if (type.equals("FACEBOOK") || type.equals("GOOGLE")) {
            userPreferences.putEmail(email);
            userPreferences.putID(id);
            userPreferences.putUsername(name);
            userPreferences.putImageUri(image);
            userPreferences.putType(type);
            Intent i = new Intent(context, Splash.class);
            startActivity(i);
        } else if (type.equals("USER_PASSWORD")) {
            userPreferences.putEmail(email);
            userPreferences.putUsername(name);
            userPreferences.putID(id);
            userPreferences.putImageUri(image);
            userPreferences.putType(type);
            Intent i = new Intent(context, Splash.class);
            startActivity(i);
        }
        Log.d(TAG, "goSplash: " + id + " " + name + " " + email + " " + image + " " + type);
    }

    private void signIn() {
        Intent signIn = client.getSignInIntent();
        startActivityForResult(signIn, GOOGLE_SING_IN);
    }


    private void signUpWithFirebase(String email, String password) {
        Log.d(TAG, "sign up: " + email + " " + password);
        auth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(this, task -> {
            if (task.isSuccessful()) {
                Log.d(TAG, "Success");
                FirebaseUser user = auth.getCurrentUser();
                Log.d(TAG, "UserId: " + user.getUid() + "\n*UserEmail: " + user.getEmail() + "\n*UserName: " + user.getDisplayName());
                UserProfileChangeRequest userProfile = new UserProfileChangeRequest
                        .Builder()
                        .setDisplayName(first + " " + last)
                        .setPhotoUri(Uri.parse(userPreferences.getImage()))
                        .build();
                user.updateProfile(userProfile).addOnCompleteListener(t -> {
                    if (t.isSuccessful()) {
                        goSplash(user.getUid(), first + " " + last, user.getEmail(), String.valueOf(user.getPhotoUrl()), "USER_PASSWORD");
                    }
                });
                new Handler().postDelayed(() -> {
                    signUpButton.setText("DONE");
                    signUpButton.setVisibility(View.VISIBLE);
                    progressBar.setVisibility(View.GONE);
                }, 1500);
                Intent i = new Intent(context, NowPlaying_OnTheAir.class);
                startActivity(i);
            } else {
                Log.d(TAG, "Error: " + task.getException().getMessage());
                if (task.getException().getMessage().equals("The email address is already in use by another account.")) {
                    new Handler().postDelayed(() -> {
                        signUpButton.setText("SIGN UP");
                        signUpButton.setVisibility(View.VISIBLE);
                        progressBar.setVisibility(View.GONE);
                        Toast.makeText(context, "This email is already in use", Toast.LENGTH_SHORT).show();
                    }, 1500);
                }
            }
        });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_CODE_PERMISSION && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            openGallery();
        } else {
            Toast.makeText(context, "You need this permission", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == IMAGE_GALLERY_REQUEST && resultCode == RESULT_OK) {
            Uri uri = data.getData();
            String path = getPathFromURI(uri);
            profile.setImageURI(uri);
            userPreferences.putImageUri(path);
        } else if (requestCode == IMAGE_CAPTURE_REQUEST && resultCode == RESULT_OK) {
            Bitmap bitmap = (Bitmap) data.getExtras().get("data");
            File f = saveFile(bitmap);
            Uri fileUri = Uri.fromFile(f);
            profile.setImageBitmap(bitmap);
            userPreferences.putImageUri(fileUri.toString());
            Log.d(TAG, "onActivityResult: " + fileUri);
        } else if (requestCode == GOOGLE_SING_IN && resultCode == RESULT_OK) {
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

    private File loadImage(String picturePath) {
        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(picturePath, options);
        int height_ = options.outHeight;
        int width_ = options.outWidth;
        Log.d(TAG, "loadImage: " + height_ + " " + width_);
        float ratio = (float) width_ / height_;
        Log.d(TAG, "loadImage: " + ratio);
        int width = 480;
        int height = 480;
        if (width_ > height_) {
            height = Math.round(width / ratio);
            Log.d(TAG, "loadImage: " + height);
        } else {
            width = Math.round(width * ratio);
            Log.d(TAG, "loadImage: " + width);
        }
        options.inSampleSize = calculateInSampleSize(options, width, height);
        options.inJustDecodeBounds = false;
        Bitmap pic = BitmapFactory.decodeFile(picturePath, options);
        return saveFile(pic);
    }

    File saveFile(Bitmap pic) {
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss",
                Locale.getDefault()).format(new Date());
        File dir = new File(getExternalFilesDir(Environment.DIRECTORY_PICTURES), "JPEG_"
                + timeStamp
                + ".jpg");
        //Form a directory with a file named "pic"
        Log.d(TAG, "saveFile: " + dir.getAbsolutePath());
        try {
            //Prepare output stream that write byte to the directory
            FileOutputStream out = new FileOutputStream(dir);
            //Save the picture to the directory
            pic.compress(Bitmap.CompressFormat.JPEG, 100, out);
            out.flush();
            out.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return dir;
    }

    public static int calculateInSampleSize(BitmapFactory.Options options, int reqWidth,
                                            int reqHeight) {
        // Raw height and width of image
        final int height = options.outHeight;
        final int width = options.outWidth;
        Log.d(TAG, "calculateInSampleSize: " + height + " " + width);
        int stretch_width = Math.round((float) width / (float) reqWidth);
        int stretch_height = Math.round((float) height / (float) reqHeight);
        Log.d(TAG, "calculateInSampleSize: " + stretch_height + " " + stretch_width);
        if (stretch_width <= stretch_height)
            return stretch_height;
        else
            return stretch_width;
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