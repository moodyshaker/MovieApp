package com.example.MovieDB.ui.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.util.Patterns;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;

import androidx.appcompat.app.AppCompatActivity;

import com.example.MovieDB.R;
import com.example.MovieDB.endpoints.AppConstants;
import com.example.MovieDB.receivers.NetworkReceiver;
import com.example.MovieDB.utils.Utils;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;

public class ForgetPassword extends AppCompatActivity implements NetworkReceiver.NetworkCallbackListener {

    private Context context = this;
    private Activity activity = this;
    private EditText emailET;
    private TextInputLayout emailInput;
    private Button resetPasswordButton;
    private FirebaseAuth auth;
    private String email;
    private Intent i;
    private Handler handler;
    private Dialog dialog;
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
        setContentView(R.layout.activity_forget_password);
        initUi();
        filter = new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION);
        receiver = new NetworkReceiver();
        receiver.setListener(this);
        resetPasswordButton.setOnClickListener(click -> {
            email = emailET.getText().toString();
            if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                emailInput.setError("Please check your email address");
                emailInput.requestFocus();
            } else {
                dialog.show();
                handler.postDelayed(() -> {
                    sendPasswordReset(email);
                }, 1000);
            }
        });

    }

    private void initUi() {
        auth = FirebaseAuth.getInstance();
        handler = new Handler();
        dialog = Utils.transparentDialog(context);
        emailET = findViewById(R.id.email_address_et);
        emailInput = findViewById(R.id.email_address_input);
        resetPasswordButton = findViewById(R.id.reset_password_button);
        connectionDialog = Utils.showDisconnectionDialog(context);
        connectedContainer = connectionDialog.findViewById(R.id.connected_container);
        disconnectedContainer = connectionDialog.findViewById(R.id.disconnected_container);
        h = new Handler();
    }

    private void sendPasswordReset(String emailToSend) {
        auth.sendPasswordResetEmail(emailToSend).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                dialog.dismiss();
                Log.d(AppConstants.TAG, "email sent successfully");
                i = new Intent();
                i.putExtra("RESET_PASSWORD", email);
                setResult(AppConstants.FORGET_PASSWORD_RESULT, i);
                showAlertDialog();
            } else {
                dialog.dismiss();
                Log.d(AppConstants.TAG, "sendPasswordReset: " + task.getException().getMessage());
            }
        });
    }

    private void showAlertDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("Password Reset");
        builder.setMessage("An email has been sent to change your password");
        builder.setPositiveButton("OK", (d, w) -> finish());
        builder.show();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            onBackPressed();
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