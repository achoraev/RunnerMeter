package com.newrunner.googlemap;

import android.app.Activity;
import android.content.Context;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

/**
 * Created by angelr on 12-Jun-15.
 */
public class MapLocationListener extends Activity implements OnMapReadyCallback, LocationListener {

    private GoogleMap mMap; // Might be null if Google Play services APK is not available.
    private LatLng currentCoordinates;
    private Location currentLocation;
    private Double lat;
    private Double lon;

    private LocationManager locationManager;
    private Criteria criteria;
    private LocationListener locListener;

    @Override
    public void onLocationChanged(Location location) {
        currentLocation = location;
        currentCoordinates = new LatLng(currentLocation.getLatitude(), currentLocation.getLongitude());
        mMap.addMarker(new MarkerOptions().position(currentCoordinates)
                .title("Marker"));
        CameraPosition camPosition = new CameraPosition.Builder()
                .target(currentCoordinates)
                .zoom(15)
                .build();
        mMap.moveCamera(CameraUpdateFactory.newCameraPosition(camPosition));
//        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(myLocation, 15));
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

    @Override
    public void onMapReady(GoogleMap googleMap) {
        // Map is ready to be used.
        mMap = googleMap;
//        initializeLocationManager();
        mMap.setMapType(GoogleMap.MAP_TYPE_HYBRID);
        mMap.setMyLocationEnabled(true);
//        mMap.setOnMyLocationChangeListener(new GoogleMap.OnMyLocationChangeListener, );

        if (currentLocation != null) {
            lat = currentLocation.getLatitude();
            lon = currentLocation.getLongitude();
            currentCoordinates = new LatLng(lat, lon);
//            Toast.makeText(this, String.format("lat: %f long: %f", lat, lon),
//                    Toast.LENGTH_SHORT).show();
        } else {
            currentCoordinates = new LatLng(42.7079, 23.3613);
//            String message = String.format("lat: %f long: %f ", 10.5, 15.5);
////            String message = "lat: " + 10.5 + " long: " + 15.5;
//            Toast.makeText(this, message,
//                    Toast.LENGTH_SHORT).show();
//            System.out.print(message);
        }

        // Add a marker with a title that is shown in its info window.
        mMap.addMarker(new MarkerOptions().position(currentCoordinates)
                .title("Marker"));

        // Move the camera to show the marker.
        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(currentCoordinates, 15), 2000, null);
    }

    private void initializeLocationManager() {
        locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
//        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        criteria = new Criteria();
        criteria.setPowerRequirement(Criteria.POWER_LOW);
        currentLocation = locationManager.getLastKnownLocation(locationManager.getBestProvider(criteria, false));
//        locListener = new MapLocationListener();
    }
}