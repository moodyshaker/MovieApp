package com.example.MovieDB.ui.activity;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;

import com.example.MovieDB.R;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

public class NearBy extends NavigationViewActivity implements OnMapReadyCallback, GoogleMap.OnMapLoadedCallback, LocationListener {

    Context context = this;
    Activity activity = this;
    FrameLayout frameLayout;
    TextView title;
    Toolbar toolbar;
    String headTitle;
    GoogleMap googleMap;
    private final int PERMISSION_REQUEST_CODE = 903;
    Location location;
    MapView mapView;
    FusedLocationProviderClient client;
    LocationRequest request;
    LocationCallback callback;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View contentView = inflater.inflate(R.layout.activity_near_by, null, false);
        frameLayout = drawerLayout.findViewById(R.id.frame_layout_holder);
        toolbar = findViewById(R.id.toolbar);
        title = toolbar.findViewById(R.id.title);
        mapView = contentView.findViewById(R.id.nearby_map_view);
        frameLayout.addView(contentView);
        headTitle = getIntent().getStringExtra("title");
        if (headTitle != null) {
            title.setText(headTitle);
        } else {
            title.setText(getResources().getString(R.string.near_by));
        }
        mapView.onCreate(savedInstanceState);
        mapView.onResume();
        mapView.getMapAsync(this);
        initLocation();
    }

    @Override
    protected void onStart() {
        super.onStart();
        grantPermissionFromUser();
    }

    @Override
    protected void onStop() {
        super.onStop();
        client.removeLocationUpdates(callback);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        this.googleMap = googleMap;
        googleMap.setOnMapLoadedCallback(this);
    }

    @Override
    public void onMapLoaded() {
        grantPermissionFromUser();
        if (location != null) {
            LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
            googleMap.addMarker(new MarkerOptions().title("My Location")
                    .position(latLng));
            googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 14));
        }
    }

    private void grantPermissionFromUser() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if ((ActivityCompat.checkSelfPermission(context,
                    Manifest.permission.ACCESS_COARSE_LOCATION)
                    == PackageManager.PERMISSION_GRANTED) || (ActivityCompat.checkSelfPermission(context,
                    Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED)) {
                getLocation();
            } else {
                String[] permissions = {Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION};
                ActivityCompat.requestPermissions(activity, permissions, PERMISSION_REQUEST_CODE);
            }
        } else {
            getLocation();
        }
    }

    private void initLocation() {
        client = LocationServices.getFusedLocationProviderClient(this);
        request = LocationRequest.create();
        request.setInterval(6000);
        request.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        request.setFastestInterval(4000);

        callback = new LocationCallback() {
            @Override
            public void onLocationResult(LocationResult locationResult) {
                googleMap.clear();
                Location location = locationResult.getLastLocation();
                Log.e("location", location.toString());
                LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
                googleMap.addMarker(new MarkerOptions().title("My Location")
                        .position(latLng));
                googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 14));
            }
        };
    }

    private void getLocation() {
        client.requestLocationUpdates(request, callback, null);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == PERMISSION_REQUEST_CODE && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            getLocation();
        } else {
            Toast.makeText(context, "Please open your GPS first", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onLocationChanged(Location location) {
        this.location = location;

    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(String provider) {

    }

    @Override
    public void onProviderDisabled(String provider) {

    }
}
