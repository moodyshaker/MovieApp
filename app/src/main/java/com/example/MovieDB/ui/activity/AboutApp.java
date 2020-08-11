package com.example.MovieDB.ui.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;

import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.example.MovieDB.R;
import com.example.MovieDB.receivers.NetworkReceiver;
import com.example.MovieDB.utils.Utils;

public class AboutApp extends com.example.MovieDB.ui.activity.NavigationViewActivity implements NetworkReceiver.NetworkCallbackListener {

    private Context context = this;
    private Activity activity = this;
    private FrameLayout frameLayout;
    private Toolbar toolbar;
    private TextView title, versionName;
    private String headTitle;
    private PackageInfo packageInfo;
    private ImageView whatsAppButton, facebookButton, linkedInButton;
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
        View contentView = inflater.inflate(R.layout.activity_about_app, null, false);
        initUi(contentView);
        headTitle = getIntent().getStringExtra("title");
        if (headTitle != null) {
            title.setText(headTitle);
        } else {
            title.setText(getResources().getString(R.string.about_app));
        }
        filter = new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION);
        receiver = new NetworkReceiver();
        receiver.setListener(this);
        try {
            packageInfo = getPackageManager().getPackageInfo(getPackageName(), 0);
            versionName.setText(getResources().getString(R.string.version_string, packageInfo.versionName));
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        whatsAppButton.setOnClickListener(click -> {
            Intent i = getPackageManager().getLaunchIntentForPackage("com.whatsapp");
            if (i != null) {
                String whatsAppUrl = "http://api.whatsapp.com/send?phone=" + "201018144255";
                i = new Intent(Intent.ACTION_VIEW);
                i.setData(Uri.parse(whatsAppUrl));
                startActivity(i);
            } else {
                i = new Intent(Intent.ACTION_VIEW);
                i.setData(Uri.parse("https://play.google.com/store/apps/details?id=com.whatsapp"));
                startActivity(i);
            }

        });
        facebookButton.setOnClickListener(click -> {
            String profileUrl = "https://www.facebook.com/MoodyShakeer";
            Intent i = getPackageManager().getLaunchIntentForPackage("com.facebook.katana");
            if (i != null) {
                String facebookUrl = "fb://facewebmodal/f?href=" + profileUrl;
                i = new Intent(Intent.ACTION_VIEW);
                i.setData(Uri.parse(facebookUrl));
                startActivity(i);
            } else {
                i = new Intent(Intent.ACTION_VIEW);
                i.setData(Uri.parse("https://play.google.com/store/apps/details?id=com.facebook.katana"));
                startActivity(i);
            }
        });
        linkedInButton.setOnClickListener(click -> {
            Intent i = getPackageManager().getLaunchIntentForPackage("com.linkedin.android");
            if (i != null) {
                String linkedIn = "https://www.linkedin.com/in/ahmed-shaker-40a41995/";
                i = new Intent(Intent.ACTION_VIEW);
                i.setData(Uri.parse(linkedIn));
                startActivity(i);
            } else {
                i = new Intent(Intent.ACTION_VIEW);
                i.setData(Uri.parse("https://play.google.com/store/apps/details?id=com.linkedin.android"));
                startActivity(i);
            }
        });
    }

    private void initUi(View v) {
        frameLayout = drawerLayout.findViewById(R.id.frame_layout_holder);
        toolbar = findViewById(R.id.toolbar);
        title = toolbar.findViewById(R.id.title);
        versionName = v.findViewById(R.id.version_text);
        whatsAppButton = v.findViewById(R.id.whatsapp_button);
        facebookButton = v.findViewById(R.id.facebook_button);
        linkedInButton = v.findViewById(R.id.linkedin_button);
        connectionDialog = Utils.showDisconnectionDialog(context);
        connectedContainer = connectionDialog.findViewById(R.id.connected_container);
        disconnectedContainer = connectionDialog.findViewById(R.id.disconnected_container);
        h = new Handler();
        frameLayout.addView(v);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawers();
            return true;
        } else if (keyCode == KeyEvent.KEYCODE_BACK) {
            Utils.goActivity(activity, com.example.MovieDB.ui.activity.NowPlaying_OnTheAir.class);
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
