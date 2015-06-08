package com.newrunner.googlemap;

import android.content.Context;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

public class MapsActivity extends FragmentActivity {

    private GoogleMap mMap; // Might be null if Google Play services APK is not available.
    private LocationManager locationManager;
    private Criteria criteria;
    private Location location;
    private LatLng myLocation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        criteria = new Criteria();
        location = locationManager.getLastKnownLocation(locationManager.getBestProvider(criteria, false));
        setContentView(R.layout.activity_maps);
        setUpMapIfNeeded();
    }

    @Override
    protected void onResume() {
        super.onResume();
        setUpMapIfNeeded();
    }

//    private void centerMapOnMyLocation() {
//        mMap.setMyLocationEnabled(true);
//        location = mMap.getMyLocation();
//        LatLng myLocation = new LatLng(42.678874,23.360368);
//        if (location != null) {
//            myLocation = new LatLng(location.getLatitude(),
//                    location.getLongitude());
//        }
//        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(myLocation, 2));
//    }

    private void setUpMapIfNeeded() {
        // Do a null check to confirm that we have not already instantiated the map.
        if (mMap == null) {
            // Try to obtain the map from the SupportMapFragment.
            mMap = ((SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map))
                    .getMap();
            mMap.setMapType(GoogleMap.MAP_TYPE_HYBRID);
            // Check if we were successful in obtaining the map.
            if (mMap != null) {
                setUpMap();
            }
        }
    }

    private void setUpMap() {
//        mMap.setMyLocationEnabled(true);
//        location = mMap.getMyLocation();
        myLocation = new LatLng(42.678874,23.360368);
//        if (location != null) {
//            myLocation = new LatLng(location.getLatitude(),
//                    location.getLongitude());
//        }
//        Toast.makeText(this, String.format("lat: {} long: {}", location.getLatitude(), location.getLongitude()), Toast.LENGTH_SHORT).show();

//        mMap.addMarker(new MarkerOptions().position(new LatLng(location.getLatitude(),
//                location.getLongitude())).title("My Location"));
        mMap.addMarker(new MarkerOptions().position(myLocation).title("My Location"));
        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(myLocation, 10));
    }
}