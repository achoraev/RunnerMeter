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
import com.runner.sportsmeter.models.Coordinates;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by angelr on 02-Nov-15.
 */
public class WorldMapActivity extends AppCompatActivity implements OnMapReadyCallback {
    private static final int MY_PERMISSIONS_REQUEST_ACCESS_LOCATION = 123456;
    private final int QUERY_LIMIT = 500;
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
        Log.d("world_map", "Map is ready");
        mMap = googleMap;
        mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.LOLLIPOP_MR1) {
            if (checkSelfPermission(android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                    && checkSelfPermission(android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

                ActivityCompat.requestPermissions(WorldMapActivity.this,
                        new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION, android.Manifest.permission.ACCESS_COARSE_LOCATION},
                        MY_PERMISSIONS_REQUEST_ACCESS_LOCATION);

                //    public void requestPermissions(@NonNull String[] permissions, int requestCode)
                // here to request the missing permissions, and then overriding
                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                //                                          int[] grantResults)
                // to handle the case where the user grants the permission. See the documentation
                // for Activity#requestPermissions for more details.
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

                    Toast.makeText(WorldMapActivity.this, "Permissions granted", Toast.LENGTH_LONG).show();
                    // permission was granted, yay! Do the
                    // contacts-related task you need to do.

                } else {

                    Toast.makeText(WorldMapActivity.this, "Need GPS to use this app", Toast.LENGTH_LONG).show();
                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                }
                return;
            }

            // other 'case' lines to check for other
            // permissions this app might request
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    private void getCoordinatesFromParse() {
        ParseQuery<Coordinates> query = Coordinates.getQuery();
        query.setLimit(QUERY_LIMIT);
        query.findInBackground(new FindCallback<Coordinates>() {
            public void done(List<Coordinates> coordinates, ParseException e) {
                progressBar.setVisibility(View.INVISIBLE);
                if (e == null) {
                    Log.d("coordinates", "Retrieved " + coordinates.size() + " coordinates");
                    // todo remove before release
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
                    Log.e("coordinates", "Error: " + e.getMessage());
                    Toast.makeText(WorldMapActivity.this, "Error " + e.getMessage(), Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    private void iterateOverCoordinates(List<Coordinates> coordinates) {
        for (int i = 0; i < coordinates.size(); i++) {
            Coordinates current = coordinates.get(i);
            // todo remove when data is filled
            if (current.getStartAndEndPoint() != null) {
                LatLng curPosition = new LatLng(
                        current.getStartAndEndPoint().getLatitude(),
                        current.getStartAndEndPoint().getLongitude());
                mMap.addMarker(new MarkerOptions().position(curPosition));
                ArrayList<ParseGeoPoint> currentAsList = new ArrayList<>();
                currentAsList.add(new ParseGeoPoint(curPosition.latitude, curPosition.longitude));
                current.setStartAndEndCoordinates(currentAsList);
                current.saveEventually();
            } else if (current.getStartAndEndCoordinates() != null) {
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
}
