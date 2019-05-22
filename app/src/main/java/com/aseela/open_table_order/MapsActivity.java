package com.aseela.open_table_order;

import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    // Google Maps
    private GoogleMap mMap;

    // widgets
    TextView tvLocation, tvLatitude, tvLongitude;

    // attributes
    String name = "", location = "", latitude = "", longitude = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);

        // initialize
        tvLocation = findViewById(R.id.tv_map_location);
        tvLatitude = findViewById(R.id.tv_map_latitude);
        tvLongitude = findViewById(R.id.tv_map_longitude);

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        // get passed data
        if(getIntent() != null) {
            name = getIntent().getStringExtra("name");
            location = getIntent().getStringExtra("location");
            latitude = getIntent().getStringExtra("latitude");
            longitude = getIntent().getStringExtra("longitude");
        }

        // set TextViews
        tvLocation.setText(location);
        tvLatitude.setText(latitude);
        tvLongitude.setText(longitude);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        // Add a marker in Sydney and move the camera
        LatLng latLng = new LatLng(Double.parseDouble(latitude), Double.parseDouble(longitude));
        mMap.addMarker(new MarkerOptions().position(latLng).title(name));
        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 12.0f));
    }

    public void backToMeals(View view) {
        finish();
    }
}
