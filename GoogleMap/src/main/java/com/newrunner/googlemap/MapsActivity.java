package com.newrunner.googlemap;

import android.app.Activity;
import android.content.Context;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.view.*;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

public class MapsActivity extends FragmentActivity implements NavigationDrawerFragment.NavigationDrawerCallbacks {
//implements GoogleApiClient.ConnectionCallbacks,
//        GoogleApiClient.OnConnectionFailedListener,
//        LocationListener
//{

    /**
     * Fragment managing the behaviors, interactions and presentation of the navigation drawer.
     */
    private NavigationDrawerFragment mNavigationDrawerFragment;
    /**
     * Used to store the last screen title. For use in {@link #restoreActionBar()}.
     */
    private CharSequence mTitle;

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

//        mGoogleApiClient = new GoogleApiClient.Builder(this)
//                .addConnectionCallbacks(this)
//                .addOnConnectionFailedListener(this)
//                .addApi(LocationServices.API)
//                .build();
//
//        // Create the LocationRequest object
//        mLocationRequest = LocationRequest.create()
//                .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)
//                .setInterval(10 * 1000)        // 10 seconds, in milliseconds
//                .setFastestInterval(1 * 1000); // 1 second, in milliseconds
    }

    @Override
    protected void onResume() {
        super.onResume();
        setUpMapIfNeeded();
//        mGoogleApiClient.connect();
    }

//    @Override
//    protected void onPause() {
//        super.onPause();
//
//        if (mGoogleApiClient.isConnected()) {
//            LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient, this);
//            mGoogleApiClient.disconnect();
//        }
//    }

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
        myLocation = new LatLng(42.678874, 35.360368);
//        if (location != null) {
//            myLocation = new LatLng(location.getLatitude(),
//                    location.getLongitude());
//        }
//        Toast.makeText(this, String.format("lat: {} long: {}", location.getLatitude(), location.getLongitude()), Toast.LENGTH_SHORT).show();

//        mMap.addMarker(new MarkerOptions().position(new LatLng(location.getLatitude(),
//                location.getLongitude())).title("My Location"));
        mMap.addMarker(new MarkerOptions().position(myLocation).title("My Location"));
        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(myLocation, 5));
    }

//    private void handleNewLocation(Location location) {
//        Log.d(TAG, location.toString());
//
//        double currentLatitude = location.getLatitude();
//        double currentLongitude = location.getLongitude();
//
//        LatLng latLng = new LatLng(currentLatitude, currentLongitude);
//
//        //mMap.addMarker(new MarkerOptions().position(new LatLng(currentLatitude, currentLongitude)).title("Current Location"));
//        MarkerOptions options = new MarkerOptions()
//                .position(latLng)
//                .title("I am here!");
//        mMap.addMarker(options);
//        mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
//    }

//    @Override
//    public void onConnected(Bundle bundle) {
//        Location location = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
//        if (location == null) {
//            LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this);
//        }
//        else {
//            handleNewLocation(location);
//        }
//    }

//    @Override
//    public void onConnectionSuspended(int i) {
//
//    }
//
//    @Override
//    public void onConnectionFailed(ConnectionResult connectionResult) {
//        /*
//         * Google Play services can resolve some errors it detects.
//         * If the error has a resolution, try sending an Intent to
//         * start a Google Play services activity that can resolve
//         * error.
//         */
//        if (connectionResult.hasResolution()) {
//            try {
//                // Start an Activity that tries to resolve the error
//                connectionResult.startResolutionForResult(this, CONNECTION_FAILURE_RESOLUTION_REQUEST);
//                /*
//                 * Thrown if Google Play services canceled the original
//                 * PendingIntent
//                 */
//            } catch (IntentSender.SendIntentException e) {
//                // Log the error
//                e.printStackTrace();
//            }
//        } else {
//            /*
//             * If no resolution is available, display a dialog to the
//             * user with the error.
//             */
//            Log.i(TAG, "Location services connection failed with code " + connectionResult.getErrorCode());
//        }
//    }

//    @Override
//    public void onLocationChanged(Location location) {
//        handleNewLocation(location);
//    }

    // for navigation drawer
    @Override
    public void onNavigationDrawerItemSelected(int position) {
        // update the main content by replacing fragments
        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction()
                .replace(R.id.container, PlaceholderFragment.newInstance(position + 1))
                .commit();
    }

    public void onSectionAttached(int number) {
        switch (number) {
            case 1:
                mTitle = getString(R.string.title_section1);
                break;
            case 2:
                mTitle = getString(R.string.title_section2);
                break;
            case 3:
                mTitle = getString(R.string.title_section3);
                break;
        }
    }

    public void restoreActionBar() {
        ActionBar actionBar = getSupportActionBar();
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_STANDARD);
        actionBar.setDisplayShowTitleEnabled(true);
        actionBar.setTitle(mTitle);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if (!mNavigationDrawerFragment.isDrawerOpen()) {
            // Only show items in the action bar relevant to this screen
            // if the drawer is not showing. Otherwise, let the drawer
            // decide what to show in the action bar.
            getMenuInflater().inflate(R.menu.main, menu);
            restoreActionBar();
            return true;
        }
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    /**
     * A placeholder fragment containing a simple view.
     */
    public static class PlaceholderFragment extends Fragment {
        /**
         * The fragment argument representing the section number for this
         * fragment.
         */
        private static final String ARG_SECTION_NUMBER = "section_number";

        /**
         * Returns a new instance of this fragment for the given section
         * number.
         */
        public static PlaceholderFragment newInstance(int sectionNumber) {
            PlaceholderFragment fragment = new PlaceholderFragment();
            Bundle args = new Bundle();
            args.putInt(ARG_SECTION_NUMBER, sectionNumber);
            fragment.setArguments(args);
            return fragment;
        }

        public PlaceholderFragment() {
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_main, container, false);
            return rootView;
        }

        @Override
        public void onAttach(Activity activity) {
            super.onAttach(activity);
            ((MapsActivity) activity).onSectionAttached(
                    getArguments().getInt(ARG_SECTION_NUMBER));
        }
    }
}