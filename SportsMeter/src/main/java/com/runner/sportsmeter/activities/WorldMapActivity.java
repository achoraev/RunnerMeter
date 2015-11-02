package com.runner.sportsmeter.activities;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;
import com.runner.sportsmeter.R;
import com.runner.sportsmeter.models.Coordinates;

import java.util.List;

/**
 * Created by angelr on 02-Nov-15.
 */
public class WorldMapActivity extends AppCompatActivity implements OnMapReadyCallback {
    private GoogleMap mMap;
    private SupportMapFragment mapFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.world_map_layout);

        mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        Log.d("world_map", "Map is ready");
        mMap = googleMap;
        mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
        mMap.setMyLocationEnabled(true);
        LatLng startPoint = new LatLng(42.697748, 23.321658);
        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(startPoint, 1), 1000, null);
        mMap.getUiSettings().setCompassEnabled(true);
        mMap.getUiSettings().setMyLocationButtonEnabled(false);

        getCoordinatesFromParse();
    }

    private void getCoordinatesFromParse() {
        ParseQuery<Coordinates> query = Coordinates.getQuery();
        query.findInBackground(new FindCallback<Coordinates>() {
            public void done(List<Coordinates> coordinates, ParseException e) {
                if (e == null) {
                    Log.d("coordinates", "Retrieved " + coordinates.size() + " coordinates");
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

                } else {
                    Log.e("coordinates", "Error: " + e.getMessage());
                }
            }
        });
    }

    private void iterateOverCoordinates(List<Coordinates> coordinates) {
        for (Coordinates coord : coordinates) {
            LatLng curPosition = new LatLng(coord.getStartAndEndPoint().getLatitude(), coord.getStartAndEndPoint().getLongitude());
//            String username = coord.getCurrentUser().getUsername();
            mMap.addMarker(new MarkerOptions().position(curPosition));
        }
    }
}
