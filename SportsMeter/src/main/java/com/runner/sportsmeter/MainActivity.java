package com.runner.sportsmeter;

import android.app.Activity;
import android.content.*;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.location.Location;
import android.net.Uri;
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
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import com.facebook.AccessToken;
import com.facebook.appevents.AppEventsLogger;
import com.facebook.login.widget.ProfilePictureView;
import com.facebook.share.widget.LikeView;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.InterstitialAd;
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
import com.parse.ParseACL;
import com.parse.ParseAnalytics;
import com.parse.ParseFacebookUtils;
import com.parse.ParseUser;
import com.parse.ui.ParseLoginBuilder;
import com.runner.sportsmeter.activities.*;
import com.runner.sportsmeter.common.Calculations;
import com.runner.sportsmeter.common.ParseCommon;
import com.runner.sportsmeter.common.Utility;
import com.runner.sportsmeter.enums.SportTypes;
import com.runner.sportsmeter.models.Session;

import java.text.DateFormat;
import java.text.ParseException;
import java.util.Arrays;
import java.util.Date;

/**
 * Created by Angel Raev on 29-April-15.
 */
public class MainActivity extends AppCompatActivity implements
        OnMapReadyCallback,
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        LocationListener,
        ResultCallback<LocationSettingsResult> {

    public static final int TWO_SECOND = 2000;
    public static final int ONE_SECOND = 1000;
    public static final long UPDATE_INTERVAL_IN_MILLISECONDS = ONE_SECOND;
    public static final long FASTEST_UPDATE_INTERVAL_IN_MILLISECONDS =
            UPDATE_INTERVAL_IN_MILLISECONDS / 2;
    public static final int MAP_ZOOM = 17;
    public static final float POLYLINE_WIDTH = 17;
    public static final int POLYLINE_COLOR = Color.CYAN;

    protected static final String cookieUrl = "http://www.google.com/intl/bg/policies/privacy/partners/";
    protected static final String REQUESTING_LOCATION_UPDATES_KEY = "requesting-location-updates-key";
    protected static final String LAST_UPDATED_TIME_STRING_KEY = "last-updated-time-string-key";
    protected static final String LOCATION_KEY = "location-key";
    protected static final String speedMetricUnit = " km/h";
    protected static final String TAG = "location";

    protected static final int REQUEST_CHECK_SETTINGS = 0x1;
    protected static final int REQUEST_LOGIN_FROM_RESULT = 100;
    private static double SMOOTH_FACTOR = 0.2; // between 0 and 1

    public static String sessionImagePath;
    public SportTypes sportType;
    private ActionBarDrawerToggle drawerToggle;
    private NavigationView nvDrawer;
    private DrawerLayout mDrawer;
    private Toolbar toolbar;
    private ProgressBar progressBar;

    private GoogleMap mMap;
    private GoogleApiClient mGoogleApiClient;
    private SupportMapFragment mapFragment;
    private LatLng currentCoordinates = null;
    private LatLng lastUpdatedCoord = null;
    private LatLng startPointCoord = null;
    private LatLng endPointCoord = null;
    private Location currentLocation;
    private Bitmap sessionScreenShot;

    private Boolean exit = false;

    private String lastUpdateTime, currentUpdateTime, sessionStartTime = null;
    private boolean mRequestingLocationUpdates = true;
    protected LocationRequest mLocationRequest;
    protected LocationSettingsRequest mLocationSettingsRequest;

    private double currentDistance, sessionDistance, currentSpeed, averageSpeed, currentMaxSpeed;
    private long currentTimeDiff, sessionTimeDiff;

    public boolean startButtonEnabled;

    private String userName, facebookId;

    private Fragment fragment = null;
    private TextView distanceMeter, speedMeter, maxSpeedMeter, timeMeter, showUsername;
    private Button startStopBtn;
    private ProfilePictureView facebookProfilePicture;
    private LikeView likeView;

    private InterstitialAd mInterstitialAd;
    private AdView mAdView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // EU user consent policy
        applyEUcookiePolicy();

        initializeUiViews();

        setToolbarAndDrawer();

        if (mapFragment == null) {
            createGoogleMap();
        }

        if (mGoogleApiClient == null) {
            buildGoogleApiClient();
        }

        Bundle bundle = getIntent().getExtras();
        sportType = (SportTypes) bundle.get(getString(R.string.type_of_sport));

        updateValuesFromBundle(savedInstanceState);

        setCurrentUserUsername();

        // setup add
        mAdView = (AdView) findViewById(R.id.adView);
        mInterstitialAd = new InterstitialAd(this);
        mInterstitialAd.setAdUnitId(getString(R.string.interestitial_add));
        mInterstitialAd.setAdListener(new AdListener() {
            @Override
            public void onAdClosed() {
                requestNewInterstitial();
            }
        });

        requestNewInterstitial();

        startStopBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!startButtonEnabled) {
                    startLogic();
                } else {
                    stopLogic();
                    if (mInterstitialAd.isLoaded()) {
                        mInterstitialAd.show();
                    }
                }
            }
        });

        // setup adds
        new Utility().setupAdds(mAdView, this);
        ParseAnalytics.trackAppOpenedInBackground(getIntent());
    }

    private void applyEUcookiePolicy() {
        final SharedPreferences settings =
                getSharedPreferences(getString(R.string.local_preferences), MODE_PRIVATE);
        if (settings.getBoolean(getString(R.string.is_first_run), true)) {
            new AlertDialog.Builder(this)
                    .setTitle(getString(R.string.cookies))
                    .setMessage(getString(R.string.cookie_policy))
                    .setPositiveButton(getString(R.string.dialog_see_details), new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(cookieUrl)));
                        }
                    })
                    .setNeutralButton(getString(R.string.dialog_close_message), new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            settings.edit().putBoolean(getString(R.string.is_first_run), false).commit();
                        }
                    })
                    .setOnDismissListener(new DialogInterface.OnDismissListener() {
                        @Override
                        public void onDismiss(DialogInterface dialog) {
                            settings.edit().putBoolean(getString(R.string.is_first_run), false).commit();
                        }
                    })
                    .show();
        }
    }

    private void requestNewInterstitial() {
        AdRequest adRequest = new AdRequest.Builder()
                .addTestDevice(AdRequest.DEVICE_ID_EMULATOR)
//                .addTestDevice(getString(R.string.huawei_device_id))
                .build();

        mInterstitialAd.loadAd(adRequest);
    }

    private void initializeUiViews() {
        distanceMeter = (TextView) findViewById(R.id.distance_meter);
        speedMeter = (TextView) findViewById(R.id.speed_meter);
        maxSpeedMeter = (TextView) findViewById(R.id.max_speed);
        timeMeter = (TextView) findViewById(R.id.time_meter);
        startStopBtn = (Button) findViewById(R.id.start_stop_btn);
        showUsername = (TextView) findViewById(R.id.header_username);
        facebookProfilePicture = (ProfilePictureView) findViewById(R.id.profile_picture);
        progressBar = (ProgressBar) findViewById(R.id.progress_bar);
    }

    private void startLogic() {
        openDialogToLoginIfLoggedAsGuest();
        startStopBtn.setBackgroundResource(R.drawable.stop_btn);
        ParseCommon.logInGuestUser(this);
        startButtonEnabled = true;
        if(mGoogleApiClient != null) {
            startLocationUpdates();
        }
        setVariablesToNull();
        currentUpdateTime = DateFormat.getTimeInstance().format(new Date());
        if (sessionStartTime == null) {
            sessionStartTime = currentUpdateTime;
        }

        if (mMap.getMyLocation() != null) {
            startPointCoord = new LatLng(mMap.getMyLocation().getLatitude(), mMap.getMyLocation().getLongitude());
        } else {
            startPointCoord = new LatLng(40, 25);
        }
        mMap.addMarker(new MarkerOptions().position(startPointCoord).title(getString(R.string.start_point)));
        updateInfoPanel(sessionDistance, averageSpeed, currentMaxSpeed, sessionTimeDiff, speedMetricUnit);
    }

    private void stopLogic() {
        startStopBtn.setBackgroundResource(R.drawable.start_btn);
        stopLocationUpdates();
        startButtonEnabled = false;
        ParseACL acl = new ParseACL();
        acl.setPublicReadAccess(true);
        acl.setPublicWriteAccess(false);

        if (currentCoordinates != null) {
            endPointCoord = currentCoordinates;
            mMap.addMarker(new MarkerOptions().position(currentCoordinates).title(getString(R.string.end_point)));
            Thread snapShotThread = new Thread(new Runnable() {
                @Override
                public void run() {
                    mMap.snapshot(new GoogleMap.SnapshotReadyCallback() {
                        @Override
                        public void onSnapshotReady(Bitmap bitmap) {
                            sessionScreenShot = bitmap;
                            sessionImagePath = Utility.saveToExternalStorage(bitmap, getApplicationContext());
                            Toast.makeText(MainActivity.this, getString(R.string.screen_shot_successfully_saved), Toast.LENGTH_LONG).show();
                            Log.d("url", sessionImagePath);
                            // clear map
                            mMap.clear();
                        }
                    }, sessionScreenShot);
                }
            });
            snapShotThread.start();
            new ParseCommon().saveTraceStartAndEndCoord(startPointCoord, endPointCoord);
        }

        Intent saveSessionIntent = new Intent(MainActivity.this, SaveSessionActivity.class);

        Session saveSession = new Session(
                sessionDistance,
                sessionTimeDiff,
                currentMaxSpeed,
                averageSpeed,
                "",
                ParseUser.getCurrentUser(),
                userName,
                sportType.toString());
        Bundle saveBundle = new Bundle();
        saveBundle.putParcelable("Session", saveSession);
        saveBundle.putParcelable("start_coords", startPointCoord);
        saveBundle.putParcelable("end_coords", endPointCoord);
        saveBundle.putDouble("session_distance", sessionDistance);
        saveBundle.putDouble("session_time_diff", sessionTimeDiff);
        saveBundle.putDouble("current_max_speed", currentMaxSpeed);
        saveBundle.putDouble("average_speed", averageSpeed);
        saveBundle.putString("sport_type", sportType.toString());
        if (sessionImagePath != null) {
            saveBundle.putString("session_image_path", sessionImagePath);
        }

        saveSessionIntent.putExtras(saveBundle);
        overridePendingTransition(android.R.anim.fade_in,
                android.R.anim.fade_out);
        startActivity(saveSessionIntent);

        // set all to null
        setVariablesToNull();

        updateInfoPanel(sessionDistance, averageSpeed, currentMaxSpeed, sessionTimeDiff, speedMetricUnit);
    }

    private void openDialogToLoginIfLoggedAsGuest() {
        if(ParseUser.getCurrentUser() != null && ParseUser.getCurrentUser().getUsername().equals("Guest")){
            new AlertDialog.Builder(this)
                    .setTitle(getString(R.string.logged_in_as_guest))
                    .setMessage(getString(R.string.do_u_want_to_login))
                    .setPositiveButton(getString(R.string.yes), new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int id) {
                            logOutCurrentUser();
                            openParseLoginActivity();
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
        }
    }

    private void setVariablesToNull() {
        sessionDistance = 0;
        sessionTimeDiff = 0;
        sessionStartTime = null;
        Calculations.setMaxSpeed(0);
        averageSpeed = 0;
        currentMaxSpeed = 0;
        currentTimeDiff = 0;
        currentDistance = 0;
        currentSpeed = 0;
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

    private void setCurrentUserUsername() {
        if (ParseCommon.isUserLoggedIn()) {
            if (ParseFacebookUtils.isLinked(ParseUser.getCurrentUser())) {
                setCurrentUserUsernameInHeader();
                facebookId = AccessToken.getCurrentAccessToken().getUserId();
                facebookProfilePicture.setProfileId(facebookId);
                facebookProfilePicture.setCropped(true);
            } else {
                setCurrentUserUsernameInHeader();
//                Toast.makeText(MainActivity.this, getString(R.string.logged_in_as_guest), Toast.LENGTH_LONG).show();
            }
        } else {
            showUsername.setText(getString(R.string.guest));
        }
    }

    private void setCurrentUserUsernameInHeader() {
        userName = new ParseCommon().getCurrentUserUsername();
        showUsername.setText(userName);
        Toast.makeText(this, getString(R.string.welcome) + " " + userName, Toast.LENGTH_LONG).show();
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
        if (ab != null) {
            ab.setHomeAsUpIndicator(R.drawable.ic_menu);
            ab.setDisplayHomeAsUpEnabled(true);
        }

        // Find our drawer view
        drawerToggle = setupDrawerToggle();
        // Tie DrawerLayout events to the ActionBarToggle
        mDrawer.setDrawerListener(drawerToggle);
    }

    private void createGoogleMap() {
        mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }

    @Override
    protected void onStart() {
        // 3- pause
        // 1 - land start onCreate
        // 2 - land start
        super.onStart();
        if (mGoogleApiClient != null && !mGoogleApiClient.isConnected()) {
            mGoogleApiClient.connect();
        }
    }

    @Override
    protected void onPause() {
        // 1 land
        super.onPause();
//        if (mGoogleApiClient.isConnected()) {
//            stopLocationUpdates();
//        }
        // for facebook API
        AppEventsLogger.deactivateApp(this);
    }

    @Override
    protected void onResume() {
        // 3 land start
        super.onResume();
        setTitle(getString(R.string.app_name));
//        if (mGoogleApiClient.isConnected()) {
//            startLocationUpdates();
//        }
        // for facebook API
        AppEventsLogger.activateApp(this);
    }

    @Override
    protected void onStop() {
        // 2 land
        super.onStop();
//        if (mGoogleApiClient.isConnected()) {
//            stopLocationUpdates();
//            mGoogleApiClient.disconnect();
//        }
    }

    @Override
    protected void onDestroy() {
        // 3 - land
        if (mInterstitialAd.isLoaded()) {
            mInterstitialAd.show();
        }

        if (mGoogleApiClient.isConnected()) {
            stopLocationUpdates();
            mGoogleApiClient.disconnect();
        }

        super.onDestroy();
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
                if (ParseCommon.isUserLoggedIn()) {
                    new AlertDialog.Builder(this)
                            .setMessage(getString(R.string.do_you_want_logout))
                            .setPositiveButton(getString(R.string.yes), new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int id) {
                                    logOutCurrentUser();
                                    openParseLoginActivity();
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
                        openParseLoginActivity();
                    } else {
                        Toast.makeText(this, getString(R.string.already_logged_in), Toast.LENGTH_LONG).show();
                    }
                }
                break;
            case R.id.nav_feedback_fragment:

                Intent emailIntent = new Intent(Intent.ACTION_SENDTO);
                emailIntent.setData(Uri.parse("mailto:"));
                emailIntent.putExtra(Intent.EXTRA_EMAIL, new String[]{getString(R.string.app_email)});
                emailIntent.putExtra(Intent.EXTRA_SUBJECT, getString(R.string.feedback));
                emailIntent.putExtra(Intent.EXTRA_TEXT, getString(R.string.feedback_text));
                if (emailIntent.resolveActivity(getPackageManager()) != null) {
                    overridePendingTransition(android.R.anim.fade_in,
                            android.R.anim.fade_out);
                    startActivity(Intent.createChooser(emailIntent, getString(R.string.send_feedback)));
                } else {
                    emailIntent.setType("message/rfc822");
                    overridePendingTransition(android.R.anim.fade_in,
                            android.R.anim.fade_out);
                    startActivity(Intent.createChooser(emailIntent, getString(R.string.send_feedback)));
                    Toast.makeText(MainActivity.this, getString(R.string.no_email_client_installed), Toast.LENGTH_SHORT).show();
                }

                break;
            case R.id.nav_account_fragment:
                Intent accountIntent = new Intent(MainActivity.this, AccountActivity.class);
                overridePendingTransition(android.R.anim.fade_in,
                        android.R.anim.fade_out);
                startActivity(accountIntent);
                break;
            case R.id.nav_leaderboard_fragment:
                Intent leaderIntent = new Intent(MainActivity.this, LeaderBoardActivity.class);
                Bundle bundle = new Bundle();
                bundle.putSerializable(getString(R.string.type_of_sport), sportType);
                leaderIntent.putExtras(bundle);
//                Bundle bundle = new Bundle();
//                bundle.putParcelableArrayList("list", arrayOfSessions);
//                leaderIntent.putExtras(bundle);
                overridePendingTransition(android.R.anim.fade_in,
                        android.R.anim.fade_out);
                startActivity(leaderIntent);
                break;
            case R.id.like_on_facebook:
                Intent likeFacebook = new Intent(Intent.ACTION_VIEW, Uri.parse("https://www.facebook.com/sportmeter/"));
                overridePendingTransition(android.R.anim.fade_in,
                        android.R.anim.fade_out);
                startActivity(likeFacebook);
                break;
            case R.id.rate_app_fragment:
                launchMarket();
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

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (drawerToggle.onOptionsItemSelected(item)) {
            return true;
        }

        // Handle action button
        switch (item.getItemId()) {
//            case R.id.action_websearch:
//                Intent intent = new Intent(Intent.ACTION_WEB_SEARCH);
//                intent.putExtra(SearchManager.QUERY, "");
//                if (intent.resolveActivity(getPackageManager()) != null) {
//                    startActivity(intent);
//                } else {
//                    Toast.makeText(this, R.string.app_not_available, Toast.LENGTH_LONG).show();
//                }
//                return true;
            case R.id.action_about:
                Intent aboutIntent = new Intent(MainActivity.this, AboutActivity.class);
                overridePendingTransition(android.R.anim.fade_in,
                        android.R.anim.fade_out);
                startActivity(aboutIntent);
                return true;
            case R.id.action_world_map:
                Intent worldMapIntent = new Intent(MainActivity.this, WorldMapActivity.class);
                overridePendingTransition(android.R.anim.fade_in,
                        android.R.anim.fade_out);
                startActivity(worldMapIntent);
                return true;
//            case R.id.action_help:
//                Intent helpIntent = new Intent(MainActivity.this, HelpActivity.class);
//                startActivity(helpIntent);
//                return true;
            case R.id.action_logout:
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
                }
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void openParseLoginActivity() {
        ParseLoginBuilder builder = new ParseLoginBuilder(MainActivity.this);
        Intent parseLoginIntent = builder
                .setFacebookLoginPermissions(Arrays.asList(
                        "public_profile",
//                                                    "publish_actions",
//                                                    "manage_pages",
//                                                    "publish_pages",
                        "email"))
//                                                    "user_birthday",
//                                                    "user_likes"))
                .build();
        startActivityForResult(parseLoginIntent, REQUEST_LOGIN_FROM_RESULT);
    }

    private void launchMarket() {
        Uri uri = Uri.parse("market://details?id=" + getPackageName());
        Intent myAppLinkToMarket = new Intent(Intent.ACTION_VIEW, uri);
        try {
            overridePendingTransition(android.R.anim.fade_in,
                    android.R.anim.fade_out);
            startActivity(myAppLinkToMarket);
        } catch (ActivityNotFoundException e) {
            Toast.makeText(this, getString(R.string.unable_find_market_app) + e.getMessage(), Toast.LENGTH_LONG).show();
            Log.d("App", e.getMessage());
        }
    }

    private void logOutCurrentUser() {
        new ParseCommon().logOutUser(this);
        showUsername.setText(R.string.guest);
        facebookProfilePicture.setProfileId("");
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
    public void onMapReady(GoogleMap googleMap) {
        Log.d(TAG, "Map is ready");
//        Toast.makeText(this, "Map is ready", Toast.LENGTH_LONG).show();
        mMap = googleMap;
//        startPointCoord = new LatLng(mMap.getMyLocation().getLatitude(), mMap.getMyLocation().getLongitude());
//        Log.d(TAG, String.valueOf(mMap.getMyLocation().getLatitude()));
        mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
        mMap.setMyLocationEnabled(true);
        startPointCoord = new LatLng(42.697748, 23.321658);
        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(startPointCoord, MAP_ZOOM), ONE_SECOND, null);
        mMap.getUiSettings().setCompassEnabled(true);
        mMap.getUiSettings().setMyLocationButtonEnabled(false);
    }

    @Override
    public void onConnected(Bundle bundle) {
        Log.d(TAG, "Connected to GoogleApiClient");
        progressBar.setVisibility(View.GONE);
        startStopBtn.setVisibility(View.VISIBLE);
        Toast.makeText(this, getString(R.string.connected_to_googleApi), Toast.LENGTH_LONG).show();
        if (currentLocation == null) {
            currentLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
//            startPointCoord = new LatLng(currentLocation.getLatitude(), currentLocation.getLongitude());
        }
        currentUpdateTime = DateFormat.getTimeInstance().format(new Date());

//        if (sessionStartTime == null) {
//            sessionStartTime = currentUpdateTime;
//        }

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
        getSupportActionBar().setTitle(title);
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
                        // startLocationUpdates();
                        break;
                    case Activity.RESULT_CANCELED:
                        Log.i(TAG, "User chose not to make required location settings changes.");
                        break;
                }
                break;
            case REQUEST_LOGIN_FROM_RESULT:
                // from parse login
                if (mInterstitialAd.isLoaded()) {
                    mInterstitialAd.show();
                }

                break;
        }
        // check current user
        setCurrentUserUsername();

        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void onBackPressed() {
        if (exit) {
            if(new ParseCommon().getCurrentUserUsername().equals("Guest")){
                logOutCurrentUser();
            }

            finish();
            super.onBackPressed();
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
            // Update the value of mRequestingLocationUpdates from the Bundle
            if (savedInstanceState.keySet().contains(REQUESTING_LOCATION_UPDATES_KEY)) {
                mRequestingLocationUpdates = savedInstanceState.getBoolean(
                        REQUESTING_LOCATION_UPDATES_KEY);
            }

            // Update the value of lastUpdateTime from the Bundle and update the UI.
            if (savedInstanceState.keySet().contains(LAST_UPDATED_TIME_STRING_KEY)) {
                currentUpdateTime = savedInstanceState.getString(
                        LAST_UPDATED_TIME_STRING_KEY);
            }

            if (savedInstanceState.keySet().contains(LOCATION_KEY)) {
                currentLocation = savedInstanceState.getParcelable(LOCATION_KEY);
            }

            if (savedInstanceState.keySet().contains(getString(R.string.global_distance))) {
                sessionDistance = savedInstanceState.getDouble(getString(R.string.global_distance));
            }

            if (savedInstanceState.keySet().contains(getString(R.string.global_average_speed))) {
                averageSpeed = savedInstanceState.getDouble(getString(R.string.global_average_speed));
            }

            if (savedInstanceState.keySet().contains(getString(R.string.global_max_speed))) {
                currentMaxSpeed = savedInstanceState.getDouble(getString(R.string.global_max_speed));
            }

            if (savedInstanceState.keySet().contains(getString(R.string.global_duration))) {
                sessionTimeDiff = savedInstanceState.getLong(getString(R.string.global_duration));
            }

            if (savedInstanceState.keySet().contains(getString(R.string.global_is_started))) {
                startButtonEnabled = savedInstanceState.getBoolean(getString(R.string.global_is_started));
                if (startButtonEnabled) {
                    startLocationUpdates();
                    startStopBtn.setBackgroundResource(R.drawable.stop_btn);
                    updateInfoPanel(sessionDistance, averageSpeed, currentMaxSpeed, sessionTimeDiff, speedMetricUnit);
                }
            }
        }
    }

    public void onSaveInstanceState(Bundle savedInstanceState) {
        savedInstanceState.putBoolean(REQUESTING_LOCATION_UPDATES_KEY,
                mRequestingLocationUpdates);
        // todo save mGoogleApiClient and mLocationRequest
        savedInstanceState.putParcelable(LOCATION_KEY, currentLocation);
        savedInstanceState.putBoolean(getString(R.string.global_is_started), startButtonEnabled);
        savedInstanceState.putDouble(getString(R.string.global_distance), sessionDistance);
        savedInstanceState.putDouble(getString(R.string.global_average_speed), averageSpeed);
        savedInstanceState.putDouble(getString(R.string.global_max_speed), currentMaxSpeed);
        savedInstanceState.putLong(getString(R.string.global_duration), sessionTimeDiff);
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
//                currentCoordinates = new LatLng(currentLocation.getLatitude(), currentLocation.getLongitude());
                currentCoordinates = smoothLocation(currentLocation, currentLocation.getLatitude(), currentLocation.getLongitude());
                startPointCoord = currentCoordinates;
                lastUpdatedCoord = currentCoordinates;
            }

            currentDistance = Calculations.calculateDistance(lastUpdatedCoord, currentCoordinates);
            sessionDistance += Calculations.calculateDistance(lastUpdatedCoord, currentCoordinates);
            currentTimeDiff = Calculations.calculateTime(currentUpdateTime, lastUpdateTime);
            sessionTimeDiff = Calculations.calculateTime(lastUpdateTime, sessionStartTime);
            currentSpeed = Calculations.calculateSpeed(currentTimeDiff, currentDistance);
            averageSpeed = Calculations.calculateSpeed(sessionTimeDiff, sessionDistance);
            currentMaxSpeed = Calculations.calculateMaxSpeed(currentSpeed, sportType);

            updateInfoPanel(sessionDistance, averageSpeed, currentMaxSpeed, sessionTimeDiff, speedMetricUnit);

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

    private void updateInfoPanel(double sessionDistance, double averageSpeed, double currentMaxSpeed, long sessionTimeDiff, String speedMetricUnit) {
        distanceMeter.setText(String.valueOf((sessionDistance / 1000) + " km"));
        speedMeter.setText(String.valueOf(averageSpeed) + speedMetricUnit);
        timeMeter.setText(Calculations.convertTimeToString(sessionTimeDiff));
        maxSpeedMeter.setText(String.valueOf(currentMaxSpeed) + speedMetricUnit);
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

    protected synchronized void buildGoogleApiClient() {
        Log.d(TAG, "Building GoogleApiClient");
        progressBar.setVisibility(View.VISIBLE);
//        Toast.makeText(this, "Building Google Api", Toast.LENGTH_LONG).show();
        Thread buildGoogleApiThread = new Thread(new Runnable() {
            @Override
            public void run() {
                mGoogleApiClient = new GoogleApiClient.Builder(MainActivity.this)
                        .addConnectionCallbacks(MainActivity.this)
                        .addOnConnectionFailedListener(MainActivity.this)
                        .addApi(LocationServices.API)
                        .build();
                createLocationRequest();
                // check gps status and turn on if not
                buildLocationSettingsRequest();
                checkLocationSettings();
            }
        });
        buildGoogleApiThread.start();
    }

    protected void createLocationRequest() {
        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(UPDATE_INTERVAL_IN_MILLISECONDS);
        mLocationRequest.setFastestInterval(FASTEST_UPDATE_INTERVAL_IN_MILLISECONDS);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
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