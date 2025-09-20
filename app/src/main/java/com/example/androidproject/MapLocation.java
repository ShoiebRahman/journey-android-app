package com.example.androidproject;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

public class MapLocation extends FragmentActivity implements OnMapReadyCallback {


    GoogleMap gmaps;
    FrameLayout map;

    public double latCoords, longCoords;
    public LatLng mapCoords;


    Button exitMap;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //EdgeToEdge.enable(this);
        setContentView(R.layout.activity_map_location);
        /*ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.map), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });*/
        Intent intent = getIntent();
        latCoords = intent.getDoubleExtra("latitude", 43.4643);
        longCoords = intent.getDoubleExtra("longitude", -80.5204);
        Log.e("Map","lat " + latCoords);
        Log.e("Map","long " + longCoords);


        mapCoords = new LatLng(latCoords, longCoords);
        map = findViewById(R.id.map_space);
        exitMap = findViewById(R.id.exit_map);
        exitMap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map_space);
        mapFragment.getMapAsync(this);
    }

    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        this.gmaps = googleMap;
        //LatLng defaultCoords = new LatLng(43.4643, -80.5204);
        this.gmaps.addMarker(new MarkerOptions().position(mapCoords).title("Location Marker"));
        this.gmaps.moveCamera(CameraUpdateFactory.zoomTo(10));
        this.gmaps.moveCamera(CameraUpdateFactory.newLatLng(mapCoords));
        //this.gmaps.setMinZoomPreference(10);

    }
}