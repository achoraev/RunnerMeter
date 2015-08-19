package com.newrunner.googlemap;

import android.app.Activity;
import android.app.SearchManager;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentSender;
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
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import com.facebook.AccessToken;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.appevents.AppEventsLogger;
import com.facebook.login.widget.ProfilePictureView;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.*;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;
import com.parse.*;
import com.parse.ui.ParseLoginBuilder;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DateFormat;
import java.text.ParseException;
import java.util.Date;

/**
 * Created by Angel Raev on 29-April-15.
 */
public class MainActivity extends AppCompatActivity implements OnMapReadyCallback,
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        LocationListener,
        ResultCallback<LocationSettingsResult> {

    public static final long UPDATE_INTERVAL_IN_MILLISECONDS = 2000;
    public static final long FASTEST_UPDATE_INTERVAL_IN_MILLISECONDS =
            UPDATE_INTERVAL_IN_MILLISECONDS / 2;
    public static final int ONE_SECOND = 1000;
    public static final int TWO_SECOND = 2000;
    public static final int MAP_ZOOM = 17;
    public static final float POLYLINE_WIDTH = 17;
    public static final int POLYLINE_COLOR = Color.RED;

    private static double SMOOTH_FACTOR = 0.2; // between 0 and 1

    protected static final String TAG = "location";

    protected final static String REQUESTING_LOCATION_UPDATES_KEY = "requesting-location-updates-key";
    protected final static String LOCATION_KEY = "location-key";
    protected final static String LAST_UPDATED_TIME_STRING_KEY = "last-updated-time-string-key";

    private ActionBarDrawerToggle drawerToggle;
    private NavigationView nvDrawer;
    private DrawerLayout mDrawer;
    private Toolbar toolbar;

    private GoogleMap mMap;
    private SupportMapFragment mapFragment;
    private LatLng currentCoordinates = null;
    private LatLng lastUpdatedCoord = null;
    private LatLng startPointCoord = null;
    private Location currentLocation;

    private Boolean exit = false;
    private GoogleApiClient mGoogleApiClient;
    private String lastUpdateTime, currentUpdateTime, startTime = null;
    private boolean mRequestingLocationUpdates = true;
    protected LocationRequest mLocationRequest;
    protected LocationSettingsRequest mLocationSettingsRequest;
    protected static final int REQUEST_CHECK_SETTINGS = 0x1;

    private double currentDistance, sessionDistance, currentSpeed, averageSpeed, currentMaxSpeed;
    private long currentTimeDiff, sessionTimeDiff;
    private String speedMetricUnit = " km/h";

    boolean startButtonEnabled;

    private String userName, facebookId;
    private Session currentSession;
    private ParseUser guestUser = null;

    Fragment fragment = null;
    TextView distanceMeter, speedMeter, maxSpeedMeter, timeMeter, showUsername;
    Button startStopBtn;
    ProfilePictureView facebookProfilePicture;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initializeUiViews();

        if (!Utility.isNetworkConnected(this)) {
            Utility.createDialogWithButtons(this, this.getString(R.string.need_internet_msg), "");
        }

//        checkForGpsOnDevice();

        setToolbarAndDrawer();

        updateValuesFromBundle(savedInstanceState);

        if (mapFragment == null) {
            createGoogleMap();
        }

        if (mGoogleApiClient == null) {
            buildGoogleApiClient();
        }

        buildLocationSettingsRequest();
        checkLocationSettings();

        if (ParseCommon.isUserLoggedIn()) {
            if (ParseFacebookUtils.isLinked(ParseUser.getCurrentUser())) {
                setCurrentUserUsernameInHeader();
                facebookId = AccessToken.getCurrentAccessToken().getUserId();
                facebookProfilePicture.setProfileId(facebookId);
            } else {
                setCurrentUserUsernameInHeader();
            }
        } else {
            showUsername.setText("Guest");
        }


        startStopBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!startButtonEnabled) {
                    startLogic();
                } else {
                    stopLogic();
                }
            }
        });

        // setup adds
        setupAdds();
    }

    private void initializeUiViews() {
        distanceMeter = (TextView) findViewById(R.id.distance_meter);
        speedMeter = (TextView) findViewById(R.id.speed_meter);
        maxSpeedMeter = (TextView) findViewById(R.id.max_speed);
        timeMeter = (TextView) findViewById(R.id.time_meter);
        startStopBtn = (Button) findViewById(R.id.start_stop_btn);
        showUsername = (TextView) findViewById(R.id.header_username);
        facebookProfilePicture = (ProfilePictureView) findViewById(R.id.profile_picture);
    }

    private void startLogic() {
        startStopBtn.setBackgroundResource(R.drawable.stop_btn);
        startButtonEnabled = true;
        startLocationUpdates();
        if (startPointCoord == null) {
            startPointCoord = new LatLng(mMap.getMyLocation().getLatitude(), mMap.getMyLocation().getLongitude());
        }
        mMap.addMarker(new MarkerOptions().position(startPointCoord).title("Start point"));

        // create guest user if not created
        if (guestUser == null && !ParseCommon.checkIfUserExist()) {
            try {
                guestUser = ParseCommon.createGuestUser();
            } catch (com.parse.ParseException e) {
                e.printStackTrace();
            }
        }

        if (!ParseCommon.isUserLoggedIn()) {
            ParseUser.logInInBackground("Guest", "123456");
        }
    }


    private void stopLogic() {
        startStopBtn.setBackgroundResource(R.drawable.start_btn);
        stopLocationUpdates();
        startButtonEnabled = false;
        currentSession = new Session(sessionDistance, sessionTimeDiff, currentMaxSpeed, averageSpeed,
                (ParseUser.getCurrentUser() != null ? ParseUser.getCurrentUser() : guestUser));
        ParseObject saveSession = new ParseObject(getString(R.string.session_object));
        saveSession.put("username", currentSession.getCurrentUser());
        saveSession.put("maxSpeed", currentSession.getMaxSpeed());
        saveSession.put("averageSpeed", currentSession.getAverageSpeed());
        saveSession.put("distance", currentSession.getDistance());
        saveSession.put("duration", currentSession.getDuration());
        saveSession.put("timePerKilometer", currentSession.getTimePerKilometer());
        saveSession.saveInBackground();
        sessionDistance = 0;
        currentMaxSpeed = 0;
        Calculations.setMaxSpeed(0);
        averageSpeed = 0;
        sessionTimeDiff = 0;
        if (currentCoordinates != null) {
            mMap.addMarker(new MarkerOptions().position(currentCoordinates).title("End point"));
        }
    }

    protected void buildLocationSettingsRequest() {
        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder();
        builder.addLocationRequest(mLocationRequest);
        mLocationSettingsRequest = builder.build();
    }

    protected void checkLocationSettings() {
        PendingResult<LocationSettingsResult> result =
                LocationServices.SettingsApi.checkLocationSettings(
                        mGoogleApiClient,
                        mLocationSettingsRequest
                );
        result.setResultCallback(this);
    }

    private void setCurrentUserUsernameInHeader() {
        ParseUser currentUser = ParseUser.getCurrentUser();
        if (ParseFacebookUtils.isLinked(currentUser) ||
                ParseTwitterUtils.isLinked(currentUser)) {
            userName = currentUser.get("name").toString();
        } else {
            userName = currentUser.getUsername();
        }
        showUsername.setText(userName);
        Toast.makeText(this, ("Welcome " + userName), Toast.LENGTH_LONG).show();
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
        // 1 start onCreate
        // 2 start
        super.onStart();
        mGoogleApiClient.connect();
    }

    @Override
    protected void onPause() {
        // 1 - pause
        super.onPause();
//        if (mGoogleApiClient.isConnected()) {
//            stopLocationUpdates();
//        }
        // for facebook API
        AppEventsLogger.deactivateApp(this);
    }

    @Override
    protected void onResume() {
        // 4- pause
        // 3 start
        super.onResume();
//        if (mGoogleApiClient.isConnected()) {
//            startLocationUpdates();
//        }
        // for facebook API
        AppEventsLogger.activateApp(this);
    }

    @Override
    protected void onStop() {
        // 2- pause
        super.onStop();
//        if (mGoogleApiClient.isConnected()) {
//            stopLocationUpdates();
//            mGoogleApiClient.disconnect();
//        }
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

        FragmentManager fragmentManager = getSupportFragmentManager();

        switch (menuItem.getItemId()) {
            case R.id.nav_map_fragment:
                if (fragment != null) {
                    fragmentManager.beginTransaction()
                            .remove(fragment)
                            .addToBackStack(null)
                            .commit();
                }
                fragment = null;
                break;
            case R.id.nav_login_fragment:
                fragment = null;
                if (ParseCommon.isUserLoggedIn()) {
                    new AlertDialog.Builder(this)
                            .setMessage(getString(R.string.do_you_want_logout))
                            .setPositiveButton(getString(R.string.yes), new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int id) {
                                    logOutCurrentUser();
                                    dialog.cancel();
                                }
                            })
                            .setNegativeButton(getString(R.string.no), new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.cancel();
                                }
                            })
                            .setOnDismissListener(new DialogInterface.OnDismissListener() {
                                @Override
                                public void onDismiss(DialogInterface dialog) {
                                    dialog.cancel();
                                }
                            })
                            .setCancelable(false)
                            .create()
                            .show();

                } else {
                    if (!ParseCommon.isUserLoggedIn()) {
                        ParseLoginBuilder builder = new ParseLoginBuilder(MainActivity.this);
                        startActivityForResult(builder.build(), 0);
                    } else {
                        Toast.makeText(this, getString(R.string.already_logged_in), Toast.LENGTH_LONG).show();
                    }
                }
                break;
            case R.id.nav_feedback_fragment:
                fragment = null;
                Intent emailIntent = new Intent(Intent.ACTION_SEND);
                emailIntent.setType("message/rfc822");
                emailIntent.putExtra(Intent.EXTRA_EMAIL, new String[]{"runner.meter@gmail.com"});
                emailIntent.putExtra(Intent.EXTRA_SUBJECT, "Feedback");
                emailIntent.putExtra(Intent.EXTRA_TEXT, "Your text here ...");
                try {
                    startActivity(Intent.createChooser(emailIntent, "Send Feedback:"));
                } catch (android.content.ActivityNotFoundException ex) {
                    Toast.makeText(MainActivity.this, "There are no email clients installed.", Toast.LENGTH_SHORT).show();
                }
                break;
            case R.id.nav_account_fragment:
                fragment = new AccountFragment();
                break;
            case R.id.nav_Leatherboard_fragment:
                fragment = new LeatherBoardFragment();
                break;
            case R.id.rate_app_fragment:
//                fragment = new LeatherBoardFragment();
                break;
        }

        // Insert the fragment by replacing any existing fragment
        if (fragment != null) {
            fragmentManager.beginTransaction()
                    .add(R.id.flContent, fragment)
                    .addToBackStack(null)
                    .commit();
        }

        // Highlight the selected item, update the title, and close the drawer
        menuItem.setChecked(true);
        setTitle(menuItem.getTitle());
        mDrawer.closeDrawers();
    }

    private void logOutCurrentUser() {
        ParseCommon.logOutUser(this);
        showUsername.setText("");
        facebookProfilePicture.setProfileId("");
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
                logOutCurrentUser();
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
        currentUpdateTime = DateFormat.getTimeInstance().format(new Date());

        if (startTime == null) {
            startTime = currentUpdateTime;
        }

        if (mRequestingLocationUpdates) {
            Log.d(TAG, "Starting updates");
//            startLocationUpdates();
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
        lastUpdateTime = currentUpdateTime;
        currentUpdateTime = DateFormat.getTimeInstance().format(new Date());
        try {
            updateUI();
        } catch (ParseException e) {
            e.printStackTrace();
        }
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
        // result from check Gps on/off
        switch (requestCode) {
            // Check for the integer request code originally supplied to startResolutionForResult().
            case REQUEST_CHECK_SETTINGS:
                switch (resultCode) {
                    case Activity.RESULT_OK:
                        Log.i(TAG, "User agreed to make required location settings changes.");
//                        startLocationUpdates();
                        break;
                    case Activity.RESULT_CANCELED:
                        Log.i(TAG, "User chose not to make required location settings changes.");
                        break;
                }
                break;
        }


        // check current user
        if (ParseCommon.isUserLoggedIn()) {
            setCurrentUserUsernameInHeader();
            if (ParseFacebookUtils.isLinked(ParseUser.getCurrentUser())) {
                facebookId = AccessToken.getCurrentAccessToken().getUserId();
                facebookProfilePicture.setProfileId(facebookId);
                facebookProfilePicture.setCropped(true);
//                Bitmap pic = getUserPic(ParseUser.getCurrentUser().getUsername());
            }
        }

        super.onActivityResult(requestCode, resultCode, data);
    }

    private String getFacebookIdInBackground() {
        final String[] faceId = {"1034308419914405"};
        GraphRequest req = GraphRequest.newMeRequest(AccessToken.getCurrentAccessToken(), new GraphRequest.GraphJSONObjectCallback() {
            @Override
            public void onCompleted(JSONObject jsonObject, GraphResponse graphResponse) {
                try {
                    faceId[0] = jsonObject.getJSONObject("me").get("id").toString();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
        req.executeAsync();
//        Parse.Cloud.beforeSave("_User", function(request, response) {
//            request.object.set("facebook_id", request.user.get("authData").facebook.id);
//
//            response.success();
//        });

//        new GraphRequest(
//                AccessToken.getCurrentAccessToken(),
//                "/me",
//                null,
//                HttpMethod.GET,
//                new GraphRequest.Callback() {
//                    public void onCompleted(GraphResponse response) {
////                        response
//                    }
//                }
//        ).executeAsync();

//        LoginClient.Request.executeMeRequestAsync(ParseFacebookUtils.getSession(), new LoginClient.Request.GraphUserCallback() {
//            @Override
//            public void onCompleted(GraphUser user, GetRecentContextCall.Response response) {
//                if (user != null) {
//                    faceId = user.getId();
////                    ParseUser.getCurrentUser().put("fbId", user.getId());
////                    ParseUser.getCurrentUser().saveInBackground();
//                }
//            }
//        });
        return faceId[0];
    }

//    private void makeMeRequest(final Session session) {
//        GraphRequest request = new GraphRequest(
//                AccessToken.getCurrentAccessToken(),
//                "/me",
//                null,
//                HttpMethod.GET,
//                new GraphRequest.Callback() {
//                    public void onCompleted(GraphResponse response) {
//            /* handle the result */
//                    }
//                });
//        request.executeAsync();
//    }

//    public Bitmap getUserPic(String userID) {
//        String imageURL;
//        Bitmap bitmap = null;
//        Log.d(TAG, "Loading Picture");
//        imageURL = "http://graph.facebook.com/" + userID + "/picture?type=small";
//        try {
//            bitmap = BitmapFactory.decodeStream((InputStream) new URL(imageURL).getContent());
//        } catch (Exception e) {
//            Log.d("TAG", "Loading Picture FAILED");
//            e.printStackTrace();
//        }
//        return bitmap;
//    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();

        if (exit) {
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
                currentUpdateTime = savedInstanceState.getString(
                        LAST_UPDATED_TIME_STRING_KEY);
            }
        }
    }

    public void onSaveInstanceState(Bundle savedInstanceState) {
        savedInstanceState.putBoolean(REQUESTING_LOCATION_UPDATES_KEY,
                mRequestingLocationUpdates);
        savedInstanceState.putParcelable(LOCATION_KEY, currentLocation);
//        savedInstanceState.putParcelable("Start", startPointCoord);
        savedInstanceState.putString(LAST_UPDATED_TIME_STRING_KEY, currentUpdateTime);

        super.onSaveInstanceState(savedInstanceState);
    }

    protected void startLocationUpdates() {
        LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this)
                .setResultCallback(new ResultCallback<Status>() {
                    @Override
                    public void onResult(Status status) {

                    }
                });
    }

    protected void stopLocationUpdates() {
        LocationServices.FusedLocationApi.removeLocationUpdates(
                mGoogleApiClient, this)
                .setResultCallback(new ResultCallback<Status>() {
                    @Override
                    public void onResult(Status status) {

                    }
                });
    }

    private void updateUI() throws ParseException {
//        lastUpdatedCoord = new LatLng(42.679, 23.360);
        if (currentLocation != null) {
            Log.d(TAG, "Update UI");
//            Toast.makeText(this, "Update UI", Toast.LENGTH_LONG).show();
            if (currentCoordinates != null) {
                lastUpdatedCoord = currentCoordinates;
                currentCoordinates = smoothLocation(currentLocation, lastUpdatedCoord.latitude, lastUpdatedCoord.longitude);
//                currentCoordinates = new LatLng(currentLocation.getLatitude(), currentLocation.getLongitude());
            } else {
                currentCoordinates = new LatLng(currentLocation.getLatitude(), currentLocation.getLongitude());
                startPointCoord = currentCoordinates;
                lastUpdatedCoord = currentCoordinates;
            }

            currentDistance = Calculations.calculateDistance(lastUpdatedCoord, currentCoordinates);
            sessionDistance += Calculations.calculateDistance(lastUpdatedCoord, currentCoordinates);
            currentTimeDiff = Calculations.calculateTime(currentUpdateTime, lastUpdateTime);
            sessionTimeDiff = Calculations.calculateTime(lastUpdateTime, startTime);
            currentSpeed = Calculations.calculateSpeed(currentTimeDiff, currentDistance);
            averageSpeed = Calculations.calculateSpeed(sessionTimeDiff, sessionDistance);
            currentMaxSpeed = Calculations.calculateMaxSpeed(currentSpeed);

            distanceMeter.setText(String.valueOf((sessionDistance / 1000) + " km"));
            speedMeter.setText(String.valueOf(averageSpeed) + speedMetricUnit);
            timeMeter.setText(Calculations.convertTimeToString(sessionTimeDiff));
            maxSpeedMeter.setText(String.valueOf(currentMaxSpeed) + speedMetricUnit);

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

    private LatLng smoothLocation(Location location, double oldLat, double oldLon) {
        double lat = smooth(location.getLatitude(), oldLat);
        double lon = smooth(location.getLongitude(), oldLon);

        return new LatLng(lat, lon);
    }

    // wikipedia.org/wiki/Exponential_smoothing
    private double smooth(double newVal, double oldVal) {
        return (newVal * SMOOTH_FACTOR) + oldVal * (1 - SMOOTH_FACTOR);
    }

    protected void createLocationRequest() {
        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(UPDATE_INTERVAL_IN_MILLISECONDS);
        mLocationRequest.setFastestInterval(FASTEST_UPDATE_INTERVAL_IN_MILLISECONDS);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
    }

    protected synchronized void buildGoogleApiClient() {
        Log.d(TAG, "Building GoogleApiClient");
//        Toast.makeText(this, "Building Google Api", Toast.LENGTH_LONG).show();
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

    @Override
    public void onResult(LocationSettingsResult locationSettingsResult) {
        final Status status = locationSettingsResult.getStatus();
        switch (status.getStatusCode()) {
            case LocationSettingsStatusCodes.SUCCESS:
                Log.i(TAG, "All location settings are satisfied.");
//                startLocationUpdates();
                break;
            case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:
                Log.i(TAG, "Location settings are not satisfied. Show the user a dialog to" +
                        "upgrade location settings ");

                try {
                    // Show the dialog by calling startResolutionForResult(), and check the result
                    // in onActivityResult().
                    status.startResolutionForResult(MainActivity.this, REQUEST_CHECK_SETTINGS);
                } catch (IntentSender.SendIntentException e) {
                    Log.i(TAG, "PendingIntent unable to execute request.");
                }
                break;
            case LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE:
                Log.i(TAG, "Location settings are inadequate, and cannot be fixed here. Dialog " +
                        "not created.");
                break;
        }
    }
}