package com.newrunner.googlemap;

import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

/**
 * Created by angelr on 12-Jun-15.
 */
public class MapLocationListener extends ActionBarActivity implements LocationListener {

    private static final long MIN_TIME = 5;
    private static final float MIN_DISTANCE = 5;

    private GoogleMap mMap; // Might be null if Google Play services APK is not available.
    private LatLng currentCoordinates;
    private LocationManager locationManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_map);

//        Fragment fragment = new MapFragment();
//        FragmentManager fragmentManager = getFragmentManager();
//        fragmentManager.beginTransaction().replace(R.id.content_frame, fragment).commit();
    }

    @Override
    public void onLocationChanged(Location location) {
        locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, MIN_TIME, MIN_DISTANCE, this);
        currentCoordinates = new LatLng(location.getLatitude(), location.getLongitude());

        mMap.addMarker(new MarkerOptions().position(currentCoordinates)
                .title("Marker"))
                .setDraggable(true);
//        CameraPosition camPosition = new CameraPosition.Builder()
//                .target(currentCoordinates)
//                .zoom(15)
//                .build();
//        mMap.animateCamera(CameraUpdateFactory.newCameraPosition(camPosition));
        currentCoordinates = mMap.getCameraPosition().target;
        mMap.animateCamera(CameraUpdateFactory.newLatLng(currentCoordinates));
        locationManager.removeUpdates(this);
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