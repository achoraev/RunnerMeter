package com.newrunner.googlemap;

import android.app.SearchManager;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.graphics.Color;
import android.location.Location;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import com.facebook.appevents.AppEventsLogger;
import com.facebook.login.widget.ProfilePictureView;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.PolylineOptions;
import com.parse.ParseFacebookUtils;
import com.parse.ParseTwitterUtils;
import com.parse.ParseUser;
import com.parse.ui.ParseLoginBuilder;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by Angel Raev on 29-April-15.
 */
public class MainActivity extends AppCompatActivity implements OnMapReadyCallback,
        GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, LocationListener {

    public static final long UPDATE_INTERVAL_IN_MILLISECONDS = 4000;
    public static final long FASTEST_UPDATE_INTERVAL_IN_MILLISECONDS =
            UPDATE_INTERVAL_IN_MILLISECONDS / 2;
    public static final int ONE_SECOND = 1000;
    public static final int TWO_SECOND = 2000;
    public static final int MAP_ZOOM = 17;
    public static final float POLYLINE_WIDTH = 17;
    public static final int POLYLINE_COLOR = Color.RED;

    protected static final String TAG = "location";

    protected final static String REQUESTING_LOCATION_UPDATES_KEY = "requesting-location-updates-key";
    protected final static String LOCATION_KEY = "location-key";
    protected final static String LAST_UPDATED_TIME_STRING_KEY = "last-updated-time-string-key";

    private DrawerLayout mDrawer;
    private Toolbar toolbar;
    private NavigationView nvDrawer;
    private ActionBarDrawerToggle drawerToggle;

    private GoogleMap mMap;
    private SupportMapFragment mapFragment;
    private LatLng currentCoordinates;
    private LatLng lastUpdatedCoord = null;
    private LatLng startPointCoord;
    private Location currentLocation;

    private Boolean exit = false;
    private GoogleApiClient mGoogleApiClient;
    private String lastUpdateTime;
    private String startTime = null;
    private boolean mRequestingLocationUpdates = true;
    protected LocationRequest mLocationRequest;
    double currentDistance = 0;

    private String userName;

    TextView distanceMeter;
    TextView speedMeter;
    TextView timeMeter;
    TextView showUsername;
    Button startBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // 1 start
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // init parse
        ParseCommon.ParseInitialize(this);

        distanceMeter = (TextView) findViewById(R.id.distance_meter);
        speedMeter = (TextView) findViewById(R.id.speed_meter);
        timeMeter = (TextView) findViewById(R.id.time_meter);
        startBtn = (Button) findViewById(R.id.start_btn);

        showUsername = (TextView) findViewById(R.id.header_username);

        if(!Utility.isNetworkConnected(this)) {
            Utility.createDialogWithButtons(this, this.getString(R.string.need_internet_msg), getString(R.string.want_to_continue));
        }

        checkForGpsOnDevice();

        setToolbarAndDrawer();

        updateValuesFromBundle(savedInstanceState);

        createGoogleMap();

        buildGoogleApiClient();

        if(ParseCommon.isUserLoggedIn()) {
            setCurrentUserUsernameInHeader();
        }

        // setup adds
        setupAdds();
    }

    private void setCurrentUserUsernameInHeader() {
        userName = ParseUser.getCurrentUser().get("name").toString();
        if (ParseFacebookUtils.isLinked(ParseUser.getCurrentUser())) {
            showUsername.setText(userName);
        } else if(ParseTwitterUtils.isLinked(ParseUser.getCurrentUser())){
            showUsername.setText(userName);
        } else {
            userName = ParseUser.getCurrentUser().getUsername();
            showUsername.setText(userName);
        }
        Toast.makeText(this, ("Welcome " + userName), Toast.LENGTH_SHORT).show();
    }

    private void setToolbarAndDrawer() {
        // Set a Toolbar to replace the ActionBar.
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // Find our drawer view
        mDrawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        nvDrawer = (NavigationView) findViewById(R.id.nvView);
        // Setup drawer view
        setupDrawerContent(nvDrawer);

        // Set the menu icon instead of the launcher icon.
        final ActionBar ab = getSupportActionBar();
        ab.setHomeAsUpIndicator(R.drawable.ic_menu);
        ab.setDisplayHomeAsUpEnabled(true);

        // Find our drawer view
        drawerToggle = setupDrawerToggle();
        // Tie DrawerLayout events to the ActionBarToggle
        mDrawer.setDrawerListener(drawerToggle);
    }

    private void createGoogleMap() {
        mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }

    private void setupAdds() {
        AdView mAdView = (AdView) findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);
    }

    @Override
    protected void onStart() {
        // 3- pause
        // 2 start
        super.onStart();
        mGoogleApiClient.connect();
    }

    @Override
    protected void onPause() {
        // 1 - pause
        super.onPause();
        if (mGoogleApiClient.isConnected()) {
            stopLocationUpdates();
        }
        // for facebook API
        AppEventsLogger.deactivateApp(this);
    }

    @Override
    protected void onResume() {
        // 4- pause
        // 3 start
        super.onResume();
        if (mGoogleApiClient.isConnected()) {
            startLocationUpdates();
        }
        // for facebook API
        AppEventsLogger.activateApp(this);
    }

    @Override
    protected void onStop() {
        // 2- pause
        super.onStop();
        if (mGoogleApiClient.isConnected()) {
            stopLocationUpdates();
            mGoogleApiClient.disconnect();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mGoogleApiClient.isConnected()) {
            stopLocationUpdates();
            mGoogleApiClient.disconnect();
        }
    }

    private ActionBarDrawerToggle setupDrawerToggle() {
        return new ActionBarDrawerToggle(this, mDrawer, toolbar, R.string.drawer_open, R.string.drawer_close);
    }

    private void setupDrawerContent(NavigationView navigationView) {
        navigationView.setNavigationItemSelectedListener(
                new NavigationView.OnNavigationItemSelectedListener() {
                    @Override
                    public boolean onNavigationItemSelected(MenuItem menuItem) {
                        selectDrawerItem(menuItem);
                        return true;
                    }
                });
    }

    public void selectDrawerItem(MenuItem menuItem) {

        Fragment fragment = null;

        switch (menuItem.getItemId()) {
            case R.id.nav_map_fragment:
                break;
            case R.id.nav_login_fragment:
                // todo check that user is not logged in
                if(!ParseCommon.isUserLoggedIn()) {
                    ParseLoginBuilder builder = new ParseLoginBuilder(MainActivity.this);
                    startActivityForResult(builder.build(), 0);
                } else {
                    Toast.makeText(this, getString(R.string.already_logged_in), Toast.LENGTH_LONG).show();
                }
                break;
            case R.id.nav_feedback_fragment:
                fragment = new FeedbackFragment();
                break;
            case R.id.nav_account_fragment:
                fragment = new AccountFragment();
                break;
            case R.id.nav_Leatherboard_fragment:
                fragment = new LeatherBoardFragment();
                break;
        }

        // Insert the fragment by replacing any existing fragment
        if (fragment != null) {
            FragmentManager fragmentManager = getSupportFragmentManager();
            fragmentManager.beginTransaction()
                    .replace(R.id.flContent, fragment)
                    .addToBackStack(null)
                    .commit();
        }

        // Highlight the selected item, update the title, and close the drawer
        menuItem.setChecked(true);
        setTitle(menuItem.getTitle());
        mDrawer.closeDrawers();
    }

    private void checkForGpsOnDevice() {
        if (!hasGps()) {
            // If this hardware doesn't support GPS, we throw message
            Log.d(TAG, getString(R.string.dontHaveGps));
            Utility.createDialogWithButtons(this, getString(R.string.gps_not_available), "");
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main, menu);
        return super.onCreateOptionsMenu(menu);
    }

    /* Called whenever we call invalidateOptionsMenu() */
//    @Override
//    public boolean onPrepareOptionsMenu(Menu menu) {
//        // If the nav drawer is open, hide action items related to the content view
//        boolean drawerOpen = mDrawerLayout.isDrawerOpen(mDrawerList);
//        menu.findItem(R.id.action_websearch).setVisible(!drawerOpen);
//        return super.onPrepareOptionsMenu(menu);
//    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (drawerToggle.onOptionsItemSelected(item)) {
            return true;
        }

        // Handle action button
        switch (item.getItemId()) {
            case R.id.action_websearch:
                Intent intent = new Intent(Intent.ACTION_WEB_SEARCH);
                intent.putExtra(SearchManager.QUERY, "");
                if (intent.resolveActivity(getPackageManager()) != null) {
                    startActivity(intent);
                } else {
                    Toast.makeText(this, R.string.app_not_available, Toast.LENGTH_LONG).show();
                }
                return true;
            case R.id.action_logout:
                ParseCommon.logOutUser(this);
                showUsername.setText("Guest");
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        Log.d(TAG, "Map is ready");
//        Toast.makeText(this, "Map is ready", Toast.LENGTH_LONG).show();
        mMap = googleMap;
//        startPointCoord = new LatLng(mMap.getMyLocation().getLatitude(), mMap.getMyLocation().getLongitude());
//        Log.d(TAG, String.valueOf(mMap.getMyLocation().getLatitude()));
        mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
        mMap.setMyLocationEnabled(true);
        mMap.getUiSettings().setCompassEnabled(true);
        mMap.getUiSettings().setMyLocationButtonEnabled(false);
    }

    @Override
    public void onConnected(Bundle bundle) {
        Log.d(TAG, "Connected to GoogleApiClient");
        Toast.makeText(this, "Connected to GoogleAPI", Toast.LENGTH_LONG).show();
        if (currentLocation == null) {
            currentLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
//            startPointCoord = new LatLng(currentLocation.getLatitude(), currentLocation.getLongitude());
        }
        lastUpdateTime = DateFormat.getTimeInstance().format(new Date());

        if (startTime == null) {
            startTime = lastUpdateTime;
        }

        if (mRequestingLocationUpdates) {
            Log.d(TAG, "Starting updates");
            startLocationUpdates();
        }
    }

    @Override
    public void onConnectionSuspended(int i) {
        // The connection to Google Play services was lost for some reason. We call connect() to
        // attempt to re-establish the connection.
        Log.d(TAG, "Connection suspended");
        mGoogleApiClient.connect();
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        // Refer to the javadoc for ConnectionResult to see what error codes might be returned in
        // onConnectionFailed.
        Log.d(TAG, "Connection failed: ConnectionResult.getErrorCode() = " + connectionResult.getErrorCode());
    }

    @Override
    public void onLocationChanged(Location location) {
        Log.d(TAG, "Location changed");
//        Toast.makeText(this, "Location changed", Toast.LENGTH_LONG).show();
        currentLocation = location;
        lastUpdateTime = DateFormat.getTimeInstance().format(new Date());
        try {
            updateUI();
        } catch (ParseException e) {
            e.printStackTrace();
        }
//        Toast.makeText(this, lastUpdateTime, Toast.LENGTH_LONG).show();
    }

    @Override
    public void setTitle(CharSequence title) {
//        mTitle = title;
//        getSupportActionBar().setTitle(mTitle);
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        // Sync the toggle state after onRestoreInstanceState has occurred.
        drawerToggle.syncState();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(ParseCommon.isUserLoggedIn()){
            if(ParseFacebookUtils.isLinked(ParseUser.getCurrentUser())){
                showUsername.setText(ParseUser.getCurrentUser().get("name").toString());
                ProfilePictureView facebookProfilePicture = (ProfilePictureView) findViewById(R.id.profile_picture);
                facebookProfilePicture.setProfileId(ParseUser.getCurrentUser().getSessionToken());
            } else {
                showUsername.setText(ParseUser.getCurrentUser().getUsername());
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void onBackPressed() {
        if (exit) {
            super.onBackPressed();
            finish();
        } else {
            Toast.makeText(this, getString(R.string.press_back_again),
                    Toast.LENGTH_SHORT).show();
            exit = true;
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    exit = false;
                }
            }, 3 * ONE_SECOND);
        }
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        // Pass any configuration change to the drawer toggls
        drawerToggle.onConfigurationChanged(newConfig);
    }

    private void updateValuesFromBundle(Bundle savedInstanceState) {
        Log.d(TAG, "Updating values from bundle");
        if (savedInstanceState != null) {
            // Update the value of mRequestingLocationUpdates from the Bundle, and
            if (savedInstanceState.keySet().contains(REQUESTING_LOCATION_UPDATES_KEY)) {
                mRequestingLocationUpdates = savedInstanceState.getBoolean(
                        REQUESTING_LOCATION_UPDATES_KEY);
            }

            if (savedInstanceState.keySet().contains(LOCATION_KEY)) {
                currentLocation = savedInstanceState.getParcelable(LOCATION_KEY);
//                updateUI();
            }

//            if(savedInstanceState.keySet().contains("Start")){
//                startPointCoord = savedInstanceState.getParcelable("Start");
//            }

            // Update the value of lastUpdateTime from the Bundle and update the UI.
            if (savedInstanceState.keySet().contains(LAST_UPDATED_TIME_STRING_KEY)) {
                lastUpdateTime = savedInstanceState.getString(
                        LAST_UPDATED_TIME_STRING_KEY);
            }
        }
    }

    public void onSaveInstanceState(Bundle savedInstanceState) {
        savedInstanceState.putBoolean(REQUESTING_LOCATION_UPDATES_KEY,
                mRequestingLocationUpdates);
        savedInstanceState.putParcelable(LOCATION_KEY, currentLocation);
//        savedInstanceState.putParcelable("Start", startPointCoord);
        savedInstanceState.putString(LAST_UPDATED_TIME_STRING_KEY, lastUpdateTime);

        super.onSaveInstanceState(savedInstanceState);
    }

    protected void startLocationUpdates() {
        LocationServices.FusedLocationApi.requestLocationUpdates(
                mGoogleApiClient, mLocationRequest, this);
    }

    protected void stopLocationUpdates() {
        LocationServices.FusedLocationApi.removeLocationUpdates(
                mGoogleApiClient, this);
    }

    private void updateUI() throws ParseException {
//        lastUpdatedCoord = new LatLng(42.679, 23.360);
        if (currentLocation != null) {
            Log.d(TAG, "Update UI");
//            Toast.makeText(this, "Update UI", Toast.LENGTH_LONG).show();
            if (currentCoordinates != null) {
                lastUpdatedCoord = currentCoordinates;
                currentCoordinates = new LatLng(currentLocation.getLatitude(), currentLocation.getLongitude());
            } else {
                currentCoordinates = new LatLng(currentLocation.getLatitude(), currentLocation.getLongitude());
                startPointCoord = currentCoordinates;
                lastUpdatedCoord = currentCoordinates;
            }

            currentDistance += calculateDistance(lastUpdatedCoord, currentCoordinates);
            distanceMeter.setText(String.format("%.2f m", currentDistance));
            speedMeter.setText(calculateSpeed());
            // save distance in variable
            timeMeter.setText(calculateTime(lastUpdateTime, startTime));
            PolylineOptions line = new PolylineOptions()
                    .add(lastUpdatedCoord, currentCoordinates)
                    .width(POLYLINE_WIDTH)
                    .color(POLYLINE_COLOR);
            if (mMap != null) {
                mMap.addPolyline(line);
                mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(currentCoordinates, MAP_ZOOM), TWO_SECOND, null);
            }
        }
    }

    private double calculateDistance(LatLng lastUpdatedCoord, LatLng currentCoordinates) {
        float[] result = new float[4];
        Location.distanceBetween(lastUpdatedCoord.latitude,
                lastUpdatedCoord.longitude,
                currentCoordinates.latitude,
                currentCoordinates.longitude, result);
        return result[0];
    }

    private String calculateSpeed() {
        String result = "0";
        return (result + " km/h");
    }

    private String calculateTime(String lastUpdateTime, String startTime) throws ParseException {
        Date lastDate = new SimpleDateFormat("HH:mm:ss").parse(lastUpdateTime);
        Date startDate = new SimpleDateFormat("HH:mm:ss").parse(startTime);

        long diff = lastDate.getTime() - startDate.getTime();
        long diffSeconds = diff / 1000 % 60;
        long diffMinutes = diff / (60 * 1000) % 60;
        long diffHours = diff / (60 * 60 * 1000) % 24;

        String result = diffHours + "h:" + diffMinutes + "m:" + diffSeconds + "s";

        return result;
    }


    protected void createLocationRequest() {
        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(UPDATE_INTERVAL_IN_MILLISECONDS);
        mLocationRequest.setFastestInterval(FASTEST_UPDATE_INTERVAL_IN_MILLISECONDS);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
    }

    protected synchronized void buildGoogleApiClient() {
        Log.d(TAG, "Building GoogleApiClient");
        Toast.makeText(this, "Building Google Api", Toast.LENGTH_LONG).show();
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
        createLocationRequest();
    }

    private boolean hasGps() {
        return getPackageManager().hasSystemFeature(PackageManager.FEATURE_LOCATION_GPS);
    }
}