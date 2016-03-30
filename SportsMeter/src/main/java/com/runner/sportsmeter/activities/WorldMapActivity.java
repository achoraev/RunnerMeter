package com.runner.sportsmeter.activities;

import android.annotation.TargetApi;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.parse.*;
import com.runner.sportsmeter.R;
import com.runner.sportsmeter.common.Constants;
import com.runner.sportsmeter.models.Coordinates;

import java.util.List;

/**
 * Created by angelr on 02-Nov-15.
 */
public class WorldMapActivity extends AppCompatActivity implements OnMapReadyCallback {
    private static final int MY_PERMISSIONS_REQUEST_ACCESS_LOCATION = 0x1;
    private GoogleMap mMap;
    private SupportMapFragment mapFragment;
    private ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.l_world_map_layout);

        progressBar = (ProgressBar) findViewById(R.id.progress_bar);
        progressBar.setVisibility(View.VISIBLE);

        mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        getCoordinatesFromParse();
    }

    @TargetApi(Build.VERSION_CODES.M)
    @Override
    public void onMapReady(GoogleMap googleMap) {
        Log.i(Constants.TAG, "Map is ready");
        mMap = googleMap;
        mMap.setMapType(GoogleMap.MAP_TYPE_HYBRID);
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.LOLLIPOP_MR1) {
            if (checkSelfPermission(android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                    && checkSelfPermission(android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

                ActivityCompat.requestPermissions(WorldMapActivity.this,
                        new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION, android.Manifest.permission.ACCESS_COARSE_LOCATION},
                        MY_PERMISSIONS_REQUEST_ACCESS_LOCATION);
                return;
            }
        }
        mMap.setMyLocationEnabled(true);
        LatLng startPoint = new LatLng(42.697748, 23.321658);
        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(startPoint, 1), 1000, null);
        mMap.getUiSettings().setCompassEnabled(true);
        mMap.getUiSettings().setMyLocationButtonEnabled(false);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_ACCESS_LOCATION: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
//                    Toast.makeText(WorldMapActivity.this, "Permissions granted", Toast.LENGTH_LONG).show();
                } else {
//                    Toast.makeText(WorldMapActivity.this, "Need GPS to use this app", Toast.LENGTH_LONG).show();
                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                }
                return;
            }
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    private void getCoordinatesFromParse() {
        ParseQuery<Coordinates> query = Coordinates.getQuery();
        query.setLimit(Constants.QUERY_LIMIT);
        query.findInBackground(new FindCallback<Coordinates>() {
            public void done(List<Coordinates> coordinates, ParseException e) {
                progressBar.setVisibility(View.INVISIBLE);
                if (e == null) {
                    Log.i(Constants.TAG, "Retrieved " + coordinates.size() + " coordinates");
//                    Toast.makeText(WorldMapActivity.this, "Retrieved " + coordinates.size(), Toast.LENGTH_LONG).show();
                    if (mMap != null) {
                        iterateOverCoordinates(coordinates);
                    } else {
                        try {
                            Thread.sleep(1000);
                        } catch (InterruptedException e1) {
                            e1.printStackTrace();
                        }
                        iterateOverCoordinates(coordinates);
                    }
                    ParseObject.pinAllInBackground("worldCoordinates", coordinates);
                } else {
                    Log.i(Constants.TAG, "Error: " + e.getMessage());
                    Toast.makeText(WorldMapActivity.this, "Error " + e.getMessage(), Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    private void iterateOverCoordinates(List<Coordinates> coordinates) {
        for (int i = 0; i < coordinates.size(); i++) {
            Coordinates current = coordinates.get(i);

            List<ParseGeoPoint> list = current.getStartAndEndCoordinates();
            for (ParseGeoPoint coord : list) {
                LatLng pos = new LatLng(
                        coord.getLatitude(),
                        coord.getLongitude());
                mMap.addMarker(new MarkerOptions().position(pos));
            }
        }
    }
}
