package com.example.MovieDB.ui.activity;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.ColorDrawable;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;

import com.example.MovieDB.R;
import com.example.MovieDB.endpoints.AppConstants;
import com.example.MovieDB.persistance.sharedpreferences.MovieSharedPreference;
import com.example.MovieDB.receivers.NetworkReceiver;
import com.example.MovieDB.utils.Utils;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FacebookAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;

public class Profile extends AppCompatActivity implements NetworkReceiver.NetworkCallbackListener {

    private Context context = this;
    private Activity activity = this;
    private TextView titleTV, nameTV, emailTV;
    private CircleImageView userIcon;
    private Toolbar toolbar;
    private LinearLayout update,
            delete,
            changeEmail,
            changePassword,
            credentialButtonsContainer,
            deleteButtonsContainer,
            emailButtonsContainer,
            passwordButtonsContainer;
    private ProgressBar credentialProgressbar,
            updatePasswordProgressbar,
            updateEmailProgressbar,
            deleteAccountProgressbar;
    private MovieSharedPreference.UserPreferences userPreferences;
    private ActionBar actionBar;
    private Dialog deleteDialog, credentialDialog, updatePasswordDialog, updateEmailDialog;
    private CardView deleteButton, cancelButton;
    private FirebaseUser user;
    private String password, confirmPassword, newPassword, confirmNewPassword, newEmail, confirmNewEmail;
    private Handler handler;
    private GoogleSignInOptions gso;
    private GoogleSignInClient client;
    private CallbackManager callbackManager;
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
        setContentView(R.layout.activity_profile);
        userIcon = findViewById(R.id.user_picture);
        nameTV = findViewById(R.id.name);
        emailTV = findViewById(R.id.email);
        update = findViewById(R.id.update_account);
        delete = findViewById(R.id.delete_account);
        changePassword = findViewById(R.id.change_password);
        changeEmail = findViewById(R.id.change_email);
        toolbar = findViewById(R.id.toolbar);
        connectionDialog = Utils.showDisconnectionDialog(context);
        connectedContainer = connectionDialog.findViewById(R.id.connected_container);
        disconnectedContainer = connectionDialog.findViewById(R.id.disconnected_container);
        h = new Handler();
        titleTV = toolbar.findViewById(R.id.title);
        setSupportActionBar(toolbar);
        actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
        toolbar.getNavigationIcon().setColorFilter(getResources().getColor(R.color.white), PorterDuff.Mode.SRC_ATOP);
        userPreferences = MovieSharedPreference.UserPreferences.getUserPreference(context);
        titleTV.setText("User Account");
        filter = new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION);
        receiver = new NetworkReceiver();
        receiver.setListener(this);
        handler = new Handler();
        user = FirebaseAuth.getInstance().getCurrentUser();
        gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.web_client_id))
                .requestEmail()
                .build();
        client = GoogleSignIn.getClient(context, gso);
        callbackManager = CallbackManager.Factory.create();
        if (!userPreferences.getID().isEmpty()) {
            if (!userPreferences.getImage().isEmpty()) {
                Picasso.get().load(userPreferences.getImage()).into(userIcon);
            } else {
                userIcon.setImageResource(R.drawable.baseline_account_circle_black_36);
            }
            nameTV.setText(userPreferences.getUsername());
            emailTV.setText(userPreferences.getEmail());
        }
        update.setOnClickListener(click -> {
            Intent i = new Intent(context, UpdateAccount.class);
            startActivity(i);
        });
        delete.setOnClickListener(click -> deleteAccountDialog());

        changePassword.setOnClickListener(click -> updatePasswordDialog());

        changeEmail.setOnClickListener(click -> updateEmailDialog());
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

    private void credentialDialog(String actionType, String updatedPassword, String updatedEmail) {
        TextView actionText;
        ImageView actionIcon;
        LinearLayout actionBackground;
        CardView actionButton, cancelButton;
        EditText passwordET, confirmPasswordET;
        TextInputLayout passwordInput, confirmPasswordInput;
        credentialDialog = new Dialog(context);
        credentialDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        credentialDialog.setContentView(R.layout.credential_dialog);
        credentialDialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        credentialDialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
        credentialDialog.setCancelable(false);
        actionButton = credentialDialog.findViewById(R.id.action_button);
        cancelButton = credentialDialog.findViewById(R.id.cancel_button);
        passwordET = credentialDialog.findViewById(R.id.user_password);
        confirmPasswordET = credentialDialog.findViewById(R.id.user_confirm_password);
        passwordInput = credentialDialog.findViewById(R.id.password_input);
        confirmPasswordInput = credentialDialog.findViewById(R.id.confirm_password_input);
        credentialProgressbar = credentialDialog.findViewById(R.id.credentials_progressbar);
        actionBackground = credentialDialog.findViewById(R.id.action_background_container);
        actionText = credentialDialog.findViewById(R.id.action_text);
        actionIcon = credentialDialog.findViewById(R.id.action_icon);
        credentialButtonsContainer = credentialDialog.findViewById(R.id.credential_buttons_container);
        switch (actionType) {
            case "DELETE":
                actionText.setText(getResources().getString(R.string.action_delete_account));
                actionIcon.setImageResource(R.drawable.ic_outline_delete_forever_24);
                actionBackground.setBackgroundResource(R.drawable.delete_background_for_dialog);
                break;
            case "PASSWORD":
                actionText.setText(getResources().getString(R.string.action_update_password));
                actionIcon.setImageResource(R.drawable.ic_baseline_password_email_update_24);
                actionBackground.setBackgroundResource(R.drawable.update_password_background_button);
                break;
            case "EMAIL":
                actionText.setText(getResources().getString(R.string.action_update_email));
                actionIcon.setImageResource(R.drawable.ic_baseline_password_email_update_24);
                actionBackground.setBackgroundResource(R.drawable.update_password_background_button);
        }
        credentialDialog.show();
        cancelButton.setOnClickListener(cancel -> credentialDialog.dismiss());
        actionButton.setOnClickListener(click -> {
            password = passwordET.getText().toString();
            confirmPassword = confirmPasswordET.getText().toString();
            if (password.length() < 8) {
                passwordInput.setError("please enter your password");
                passwordInput.requestFocus();
            } else if (!confirmPassword.equals(password)) {
                confirmPasswordInput.setError("please check your password");
                confirmPasswordInput.requestFocus();
            } else {
                credentialButtonsContainer.setVisibility(View.GONE);
                credentialProgressbar.setVisibility(View.VISIBLE);
                handler.postDelayed(() -> checkCredentialEmailPassword(actionType, confirmPassword, updatedPassword, updatedEmail), 1000);
            }
        });
    }

    private void checkCredentialEmailPassword(String type, String password, String updatedPassword, String updatedEmail) {
        AuthCredential credential = EmailAuthProvider
                .getCredential(userPreferences.getEmail(), password);
        user.reauthenticate(credential)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        credentialButtonsContainer.setVisibility(View.GONE);
                        credentialProgressbar.setVisibility(View.VISIBLE);
                        Log.d(AppConstants.TAG, "User re-authenticated.");
                        switch (type) {
                            case "DELETE":
                                deleteAccount(userPreferences.getType());
                                break;
                            case "PASSWORD":
                                newPasswordUpdater(updatedPassword);
                                break;
                            case "EMAIL":
                                newEmailUpdate(updatedEmail);
                        }

                    } else {
                        credentialButtonsContainer.setVisibility(View.VISIBLE);
                        credentialProgressbar.setVisibility(View.GONE);
                        Log.d(AppConstants.TAG, "Credential Email Password: " + task.getException().getMessage());
                    }
                    credentialDialog.dismiss();
                });
    }

    private void credentialWithFacebookAndGoogle(String type, String actionType, String updatedPassword, String updatedEmail) {
        switch (type) {
            case "FACEBOOK":
                AuthCredential facebookCredential =
                        FacebookAuthProvider.getCredential(userPreferences.getToken() + "m1,23m12m,3");
                user.reauthenticate(facebookCredential)
                        .addOnCompleteListener(task -> {
                    Log.d(AppConstants.TAG, "Facebook re-authenticated." + userPreferences.getToken());
                    if (task.isSuccessful()) {
                        Log.d(AppConstants.TAG, "actionTypeFromFacebbok: " + actionType);
                        switch (actionType) {
                            case "DELETE":
                                deleteAccount(userPreferences.getType());
                                break;
                            case "PASSWORD":
                                newPasswordUpdater(updatedPassword);
                                break;
                            case "EMAIL":
                                newEmailUpdate(updatedEmail);
                        }
                    } else {
                        Log.d(AppConstants.TAG, "credentialWithFacebookError: " + task.getException().getMessage());
                        userPreferences.putActionType(actionType);
                        signInWithFacebook();
                    }
                });
                break;
            case "GOOGLE":
                AuthCredential googleCredential = GoogleAuthProvider.getCredential(userPreferences.getToken(), null);
                user.reauthenticate(googleCredential).addOnCompleteListener(task -> {
                    Log.d(AppConstants.TAG, "Google re-authenticated." + userPreferences.getToken());
                    if (task.isSuccessful()) {
                        switch (actionType) {
                            case "DELETE":
                                deleteAccount(userPreferences.getType());
                                break;
                            case "PASSWORD":
                                newPasswordUpdater(updatedPassword);
                                break;
                            case "EMAIL":
                                newEmailUpdate(updatedEmail);
                        }
                    } else {
                        Log.d(AppConstants.TAG, "credentialWithGoogleError: " + task.getException().getMessage());
                        userPreferences.putActionType(actionType);
                        signInWithGoogle();
                    }
                });
                break;
        }
    }

    private void updatePasswordDialog() {
        CardView updatePasswordButton, cancelButton;
        EditText newPasswordET, confirmNewPasswordET;
        TextInputLayout newPasswordInput, confirmNewPasswordInput;
        updatePasswordDialog = new Dialog(context);
        updatePasswordDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        updatePasswordDialog.setContentView(R.layout.update_password_dialog);
        updatePasswordDialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        updatePasswordDialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
        updatePasswordDialog.setCancelable(false);
        updatePasswordButton = updatePasswordDialog.findViewById(R.id.update_password_button);
        cancelButton = updatePasswordDialog.findViewById(R.id.cancel_button);
        newPasswordET = updatePasswordDialog.findViewById(R.id.user_update_password_et);
        confirmNewPasswordET = updatePasswordDialog.findViewById(R.id.user_update_confirm_password);
        newPasswordInput = updatePasswordDialog.findViewById(R.id.update_password_input);
        confirmNewPasswordInput = updatePasswordDialog.findViewById(R.id.update_confirm_password_input);
        updatePasswordProgressbar = updatePasswordDialog.findViewById(R.id.update_password_progressbar);
        passwordButtonsContainer = updatePasswordDialog.findViewById(R.id.password_buttons_container);
        updatePasswordDialog.show();
        cancelButton.setOnClickListener(cancel -> updatePasswordDialog.dismiss());
        updatePasswordButton.setOnClickListener(update -> {
            newPassword = newPasswordET.getText().toString();
            confirmNewPassword = confirmNewPasswordET.getText().toString();
            if (newPassword.length() < 8) {
                Log.d(AppConstants.TAG, "new password : ");
                newPasswordInput.setError("please enter your password");
                newPasswordInput.requestFocus();
            } else if (!confirmNewPassword.equals(newPassword)) {
                Log.d(AppConstants.TAG, "confirm new password : ");
                confirmNewPasswordInput.setError("please check your password");
                confirmNewPasswordInput.requestFocus();
            } else {
                Log.d(AppConstants.TAG, "else : ");
                passwordButtonsContainer.setVisibility(View.GONE);
                updatePasswordProgressbar.setVisibility(View.VISIBLE);
                newPasswordUpdater(confirmNewPassword);
            }
        });
    }

    private void newPasswordUpdater(String newPassword) {
        user.updatePassword(newPassword).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                passwordButtonsContainer.setVisibility(View.VISIBLE);
                updatePasswordProgressbar.setVisibility(View.GONE);
                Log.d(AppConstants.TAG, "updatePassword: success");
                Utils.messageDialog(activity, "Password Changed", getResources().getString(R.string.password_changed_successfully));
            } else {
                passwordButtonsContainer.setVisibility(View.VISIBLE);
                updatePasswordProgressbar.setVisibility(View.GONE);
                if (userPreferences.getType().equals("USER_PASSWORD")) {
                    credentialDialog("PASSWORD", newPassword, "");
                } else if (userPreferences.getType().equals("FACEBOOK") || userPreferences.getType().equals("GOOGLE")) {
                    credentialWithFacebookAndGoogle(userPreferences.getType(), "PASSWORD", newPassword, "");
                }
            }
            updatePasswordDialog.dismiss();
        });

    }

    private void deleteAccountDialog() {
        deleteDialog = new Dialog(context);
        deleteDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        deleteDialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
        deleteDialog.setCancelable(false);
        deleteDialog.setContentView(R.layout.delete_account_dialog);
        deleteDialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        deleteButton = deleteDialog.findViewById(R.id.delete_button);
        cancelButton = deleteDialog.findViewById(R.id.cancel_button);
        deleteAccountProgressbar = deleteDialog.findViewById(R.id.delete_account_progressbar);
        deleteButtonsContainer = deleteDialog.findViewById(R.id.delete_buttons_container);
        cancelButton.setOnClickListener(cancel -> deleteDialog.dismiss());
        deleteButton.setOnClickListener(delete -> {
            deleteAccountProgressbar.setVisibility(View.VISIBLE);
            deleteButtonsContainer.setVisibility(View.GONE);
            deleteAccount(userPreferences.getType());
        });
        deleteDialog.show();
    }

    private void deleteAccount(String type) {
        user.delete().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                deleteAccountProgressbar.setVisibility(View.GONE);
                deleteButtonsContainer.setVisibility(View.VISIBLE);
                Log.d(AppConstants.TAG, "deleteAccount: " + "success");
                if (type.equals("FACEBOOK")) {
                    LoginManager.getInstance().logOut();
                    userPreferences.logOut();
                } else if (type.equals("GOOGLE")) {
                    client.signOut().addOnCompleteListener(googleTask -> {
                        userPreferences.logOut();
                    });
                } else {
                    userPreferences.logOut();
                }
                Utils.messageDialog(activity, "Account Deleted", getResources().getString(R.string.account_deleted_successfully));
            } else {
                deleteAccountProgressbar.setVisibility(View.GONE);
                deleteButtonsContainer.setVisibility(View.VISIBLE);
                if (userPreferences.getType().equals("USER_PASSWORD")) {
                    credentialDialog("DELETE", "", "");
                } else if (userPreferences.getType().equals("FACEBOOK") || userPreferences.getType().equals("GOOGLE")) {
                    credentialWithFacebookAndGoogle(userPreferences.getType(), "DELETE", "", "");
                }
            }
            deleteDialog.dismiss();
        });
    }

    private void updateEmailDialog() {
        CardView updateEmailButton, cancelButton;
        EditText newEmailET, confirmNewEmailET;
        TextInputLayout newEmailInput, confirmNewEmailInput;
        updateEmailDialog = new Dialog(context);
        updateEmailDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        updateEmailDialog.setContentView(R.layout.update_email_dialog);
        updateEmailDialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        updateEmailDialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
        updateEmailDialog.setCancelable(false);
        updateEmailButton = updateEmailDialog.findViewById(R.id.update_email_button);
        cancelButton = updateEmailDialog.findViewById(R.id.cancel_button);
        newEmailET = updateEmailDialog.findViewById(R.id.update_email_et);
        confirmNewEmailET = updateEmailDialog.findViewById(R.id.confirm_email_et);
        newEmailInput = updateEmailDialog.findViewById(R.id.update_email_input);
        confirmNewEmailInput = updateEmailDialog.findViewById(R.id.update_confirm_email_input);
        updateEmailProgressbar = updateEmailDialog.findViewById(R.id.update_email_progressbar);
        emailButtonsContainer = updateEmailDialog.findViewById(R.id.email_buttons_container);
        updateEmailDialog.show();
        cancelButton.setOnClickListener(cancel -> updateEmailDialog.dismiss());
        updateEmailButton.setOnClickListener(update -> {
            newEmail = newEmailET.getText().toString();
            confirmNewEmail = confirmNewEmailET.getText().toString();
            if (newEmail.length() < 8) {
                newEmailInput.setError("please enter your Email");
                newEmailInput.requestFocus();
            } else if (!confirmNewEmail.equals(newEmail)) {
                confirmNewEmailInput.setError("please check your Email");
                confirmNewEmailInput.requestFocus();
            } else {
                emailButtonsContainer.setVisibility(View.GONE);
                updateEmailProgressbar.setVisibility(View.VISIBLE);
                Log.d(AppConstants.TAG, "updateEmailDialog: " + confirmNewEmail);
                newEmailUpdate(confirmNewEmail);
            }
        });
    }

    private void newEmailUpdate(String newEmail) {
        user.updateEmail(newEmail)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Log.d(AppConstants.TAG, "User email address updated successfully");
                        emailButtonsContainer.setVisibility(View.VISIBLE);
                        updateEmailProgressbar.setVisibility(View.GONE);
                        user.sendEmailVerification().addOnCompleteListener(verify -> {
                            if (verify.isSuccessful()) {
                                Log.d(AppConstants.TAG, "verification: email sent successfully ");
                                userPreferences.putEmail(newEmail);
                                Utils.messageDialog(activity, "Email Changed", getResources().getString(R.string.email_changed_successfully));
                            } else {
                            }
                        });
                    } else {
                        emailButtonsContainer.setVisibility(View.VISIBLE);
                        updateEmailProgressbar.setVisibility(View.GONE);
                        if (userPreferences.getType().equals("USER_PASSWORD")) {
                            credentialDialog("EMAIL", "", newEmail);
                        } else if (userPreferences.getType().equals("FACEBOOK") || userPreferences.getType().equals("GOOGLE")) {
                            credentialWithFacebookAndGoogle(userPreferences.getType(), "EMAIL", "", newEmail);
                        }
                    }
                    updateEmailDialog.dismiss();
                });
    }

    private void signInWithGoogle() {
        Intent signIn = client.getSignInIntent();
        startActivityForResult(signIn, AppConstants.GOOGLE_SING_IN);
    }

    private void signInWithFacebook() {
        LoginManager.getInstance().registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                String idToken = loginResult.getAccessToken().getToken();
                Log.d(AppConstants.TAG, "(onSuccess) Token: " + idToken);
                Log.d(AppConstants.TAG, "Re-Auth From Facbook: " + "action type " + userPreferences.getActionType());
                userPreferences.putToken(idToken);
                credentialWithFacebookAndGoogle("FACEBOOK", userPreferences.getActionType(), "", "");
            }

            @Override
            public void onCancel() {
                Log.d(AppConstants.TAG, "onCancel");
            }

            @Override
            public void onError(FacebookException exception) {
                Log.d(AppConstants.TAG, "onError" + exception.getMessage());
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        callbackManager.onActivityResult(requestCode, resultCode, data);
        if (requestCode == AppConstants.GOOGLE_SING_IN && resultCode == RESULT_OK) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                GoogleSignInAccount account = task.getResult(ApiException.class);
                if (account != null) {
                    String idToken = account.getIdToken();
                    Log.d(AppConstants.TAG, "Re-Auth From Google: " + "action type " + userPreferences.getActionType());
                    Log.d(AppConstants.TAG, "Re-Auth From Google: " + "google token " + idToken);
                    userPreferences.putToken(idToken);
                    credentialWithFacebookAndGoogle("GOOGLE", userPreferences.getActionType(), "", "");
                }
            } catch (ApiException e) {
                Log.d(AppConstants.TAG, "onActivityResult: " + e.getMessage());
            }
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
