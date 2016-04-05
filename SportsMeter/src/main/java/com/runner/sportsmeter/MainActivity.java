package com.runner.sportsmeter;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.*;
import android.content.pm.PackageManager;
import android.content.res.ColorStateList;
import android.content.res.Configuration;
import android.location.Location;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.v4.app.ActivityCompat;
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
import android.widget.*;
import com.applovin.adview.AppLovinIncentivizedInterstitial;
import com.applovin.sdk.AppLovinAd;
import com.applovin.sdk.AppLovinAdLoadListener;
import com.applovin.sdk.AppLovinSdk;
import com.facebook.AccessToken;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.appevents.AppEventsLogger;
import com.facebook.login.widget.ProfilePictureView;
import com.google.ads.mediation.admob.AdMobAdapter;
import com.google.android.gms.ads.*;
import com.google.android.gms.ads.reward.RewardItem;
import com.google.android.gms.ads.reward.RewardedVideoAd;
import com.google.android.gms.ads.reward.RewardedVideoAdListener;
import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;
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
import com.parse.ui.LoginBuilder;
import com.runner.sportsmeter.activities.*;
import com.runner.sportsmeter.common.Calculations;
import com.runner.sportsmeter.common.Constants;
import com.runner.sportsmeter.common.ParseCommon;
import com.runner.sportsmeter.common.Utility;
import com.runner.sportsmeter.enums.Gender;
import com.runner.sportsmeter.enums.SportTypes;
import com.runner.sportsmeter.enums.UserMetrics;
import com.runner.sportsmeter.interfaces.UserMetricsInterface;
import com.runner.sportsmeter.models.*;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

/**
 * Created by Angel Raev on 29-April-15.
 */
public class MainActivity extends AppCompatActivity implements
        View.OnClickListener,
        OnMapReadyCallback,
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        LocationListener,
        RewardedVideoAdListener,
        ResultCallback<LocationSettingsResult> {

    private static double SMOOTH_FACTOR = 0.2; // between 0 and 1

    private ActionBarDrawerToggle drawerToggle;
    private NavigationView nvDrawer;
    private DrawerLayout mDrawer;
    private Toolbar toolbar;
    private ProgressBar progressBar;
    private FloatingActionButton fab;

    private GoogleMap mMap;
    private GoogleApiClient mGoogleApiClient;
    private SupportMapFragment mapFragment;
    private LatLng currentCoordinates;
    private LatLng lastUpdatedCoord;
    private LatLng startPointCoord;
    private LatLng endPointCoord;
    private LatLng SOFIA_CENTER = new LatLng(42.697748, 23.321658);
    private Location currentLocation;
    private PolylineOptions currentSegment;
    private ArrayList<ParseGeoPoint> listOfPoints = new ArrayList<>();

    private Boolean exit = false;

    private long lastUpdateTimeMillis, currentUpdateTimeMillis, sessionStartTimeMillis = 0;
    private LocationRequest mLocationRequest;
    private LocationSettingsRequest mLocationSettingsRequest;

    private SportTypes sportType;
    private double currentDistance, sessionDistance, currentSpeed, averageSpeed, currentMaxSpeed;
    private long currentTimeDiff, sessionTimeDiff;
    private Boolean startButtonEnabled = false;
    private boolean isPausedActivityEnable = false;
    private Account currentUserAccount = new Account();

    private String userName, facebookId;

    private TextView distanceMeter, speedMeter, maxSpeedMeter, timeMeter, showUsername, showTotalDistance;
    private Button startStopBtn;
    private ProfilePictureView facebookProfilePicture;
    private Spinner chooseTypeSport;
    private Session pausedSession;

    private InterstitialAd mInterstitialAd;
    private RewardedVideoAd mRewardedAd;
    private AppLovinIncentivizedInterstitial myIncent;
    private AdView mAdView;

    private SharedPreferences settings;
    private Tracker mTracker;
    private ParseInstallation installation = ParseInstallation.getCurrentInstallation();
    private Double totalDistance = 0.00;
    private UserMetricsInterface usersMetrics = new Metrics();
    private WeakReference<MainActivity> weakRef = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.l_main_layout);

        // EU user consent policy
        applyEUcookiePolicy();

        ParseCommon.logInGuestUser(this);

        initializeUiViews();

        usersMetrics = UserMetrics.METRIC.equals(Constants.speedMetricUnit) ? new Metrics() : new Imperial();
        showTotalDistance.setText(generateTotalString(totalDistance, usersMetrics.getDistanceUnit()));
        sportType = (SportTypes) getIntent().getExtras().get(getString(R.string.type_of_sport));

        setToolbarAndDrawer();

        if (mapFragment == null) {
            createGoogleMap();
        }

        if (mGoogleApiClient == null) {
            buildGoogleApiClient();
        }

        updateValuesFromBundle(savedInstanceState);

        setCurrentUserUsername();
        calculateTotalDistance(ParseUser.getCurrentUser(), usersMetrics);

        // setup add
        setupInterstitialAd();
        // applovin todo make it works return error 400
        AppLovinSdk.initializeSdk(this);
        myIncent = AppLovinIncentivizedInterstitial.create(getApplicationContext());
        myIncent.preload(new AppLovinAdLoadListener() {
            @Override
            public void adReceived(AppLovinAd appLovinAd) {
                // todo remove
                Toast.makeText(MainActivity.this, "Video loaded" + appLovinAd.getType().toString(), Toast.LENGTH_SHORT).show();
            }

            @Override
            public void failedToReceiveAd(int i) {
                // todo remove
                Toast.makeText(MainActivity.this, "Failed loaded", Toast.LENGTH_SHORT).show();
            }
        });
        weakRef = new WeakReference<MainActivity>(this);

//        setupRewardedAd();
//        loadRewardedVideoAd();
        requestNewInterstitial();

        startStopBtn.setOnClickListener(MainActivity.this);

        // setup adds
        new Utility().setupAdds(mAdView, this);
        ParseAnalytics.trackAppOpenedInBackground(getIntent());

        // Obtain the shared Tracker instance.
        ParseApplication application = (ParseApplication) getApplication();
        mTracker = application.getDefaultTracker();
    }

    private void setupRewardedAd() {
        mRewardedAd = MobileAds.getRewardedVideoAdInstance(this);
        mRewardedAd.setRewardedVideoAdListener(this);
    }

    private void setupInterstitialAd() {
        mInterstitialAd = new InterstitialAd(this);
        mInterstitialAd.setAdUnitId(getString(R.string.interestitial_add));
        mInterstitialAd.setAdListener(new AdListener() {
            @Override
            public void onAdClosed() {
                requestNewInterstitial();
            }
        });
    }

    private void loadRewardedVideoAd() {
        Bundle extras = new Bundle();
        extras.putBoolean("_noRefresh", true);
        AdRequest adRequest = new AdRequest.Builder()
                .addNetworkExtrasBundle(AdMobAdapter.class, extras)
                .build();
        mRewardedAd.loadAd(getString(R.string.interestitial_add_rewarded), adRequest);
    }

    private void requestNewInterstitial() {
        AdRequest adRequest = new AdRequest.Builder()
                .addTestDevice(AdRequest.DEVICE_ID_EMULATOR)
//                .addTestDevice(getString(R.string.huawei_device_id))
                .build();

        mInterstitialAd.loadAd(adRequest);
    }

    private void applyEUcookiePolicy() {
        settings =
                getSharedPreferences(getString(R.string.local_preferences), MODE_PRIVATE);
        String timeZone = (String) installation.get("timeZone");
        if (timeZone.toLowerCase().contains("europe") && settings.getBoolean(getString(R.string.is_first_run), true)) {
            new AlertDialog.Builder(this)
                    .setTitle(getString(R.string.cookies))
                    .setMessage(getString(R.string.cookie_policy))
                    .setPositiveButton(getString(R.string.dialog_see_details), new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(Constants.cookieUrl)));
                        }
                    })
                    .setNeutralButton(getString(R.string.dialog_close_message), new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            settings.edit().putBoolean(getString(R.string.is_first_run), false).apply();
                        }
                    })
                    .setOnDismissListener(new DialogInterface.OnDismissListener() {
                        @Override
                        public void onDismiss(DialogInterface dialog) {
                            settings.edit().putBoolean(getString(R.string.is_first_run), false).apply();
                        }
                    })
                    .show();
        }
    }

    private void initializeUiViews() {
        distanceMeter = (TextView) findViewById(R.id.distance_meter);
        speedMeter = (TextView) findViewById(R.id.speed_meter);
        maxSpeedMeter = (TextView) findViewById(R.id.max_speed);
        timeMeter = (TextView) findViewById(R.id.time_meter);
        startStopBtn = (Button) findViewById(R.id.start_stop_btn);
        showUsername = (TextView) findViewById(R.id.header_username);
        showTotalDistance = (TextView) findViewById(R.id.h_total_distance);
        facebookProfilePicture = (ProfilePictureView) findViewById(R.id.profile_picture);
        progressBar = (ProgressBar) findViewById(R.id.progress_bar);
        chooseTypeSport = (Spinner) findViewById(R.id.type_of_sports);
        mAdView = (AdView) findViewById(R.id.adView);
        fab = (FloatingActionButton) findViewById(R.id.fab);
        if (fab != null) {
            fab.setOnClickListener(MainActivity.this);
        }
    }

    @TargetApi(Build.VERSION_CODES.M)
    private void startLogic() {
//         check on 6.0 for permission
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.LOLLIPOP_MR1) {
            if (checkSelfPermission(android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                    && checkSelfPermission(android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

                ActivityCompat.requestPermissions(MainActivity.this,
                        new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION, android.Manifest.permission.ACCESS_COARSE_LOCATION},
                        Constants.MY_PERMISSIONS_REQUEST_ACCESS_LOCATION);
                return;
            }
        }
        fab.setVisibility(View.VISIBLE);
        openDialogToLoginIfLoggedAsGuest();
        setVariablesToNull();
        currentSegment = new PolylineOptions()
                .width(Constants.POLYLINE_WIDTH)
                .color(Constants.POLYLINE_COLOR);
        startStopBtn.setBackgroundResource(R.drawable.stop_btn);
        startButtonEnabled = true;
        if (mGoogleApiClient != null) {
            startLocationUpdates();
        }
        // clear map
        mMap.clear();

//        currentUpdateTime = DateFormat.getTimeInstance().format(new Date());
        currentUpdateTimeMillis = new Date().getTime();
        if (sessionStartTimeMillis == 0) {
//            sessionStartTime = currentUpdateTime;
            sessionStartTimeMillis = currentUpdateTimeMillis;
        }

        if (mMap.getMyLocation() != null) {
            startPointCoord = new LatLng(mMap.getMyLocation().getLatitude(), mMap.getMyLocation().getLongitude());
        } else {
            try {
                Thread.sleep(Constants.ONE_SECOND);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            if (mMap.getMyLocation() != null) {
                startPointCoord = new LatLng(mMap.getMyLocation().getLatitude(), mMap.getMyLocation().getLongitude());
            }
        }
        if (mMap.getMyLocation() != null) {
            currentSegment.add(startPointCoord);
            listOfPoints.add(new ParseGeoPoint(startPointCoord.latitude, startPointCoord.longitude));
            mMap.addMarker(new MarkerOptions().position(startPointCoord).title(getString(R.string.start_point)));
            mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(startPointCoord, Constants.MAP_ZOOM), Constants.ONE_SECOND, null);
        } else {
//            Toast.makeText(MainActivity.this, getString(R.string.gps_not_available), Toast.LENGTH_LONG).show();
        }

        updateInfoPanel(sessionDistance, averageSpeed, currentMaxSpeed, sessionTimeDiff, usersMetrics);
    }

    private void pauseLogic() {
        Toast.makeText(MainActivity.this, "Activity Paused", Toast.LENGTH_SHORT).show();
        isPausedActivityEnable = true;
        fab.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.app_color)));
        fab.setImageDrawable(getResources().getDrawable(R.drawable.resume_btn));
        startStopBtn.setOnClickListener(null);
        pausedSession = new Session(
                sessionDistance,
                sessionTimeDiff,
                currentMaxSpeed,
                averageSpeed,
                new Calculations().calculateTimePerKilometer(sessionDistance, sessionTimeDiff),
                "",
                ParseUser.getCurrentUser(),
                userName,
                sportType.toString());
    }

    private void resumeLogic() {
//        Toast.makeText(MainActivity.this, "Activity resumed", Toast.LENGTH_SHORT).show();
        isPausedActivityEnable = false;
        startStopBtn.setOnClickListener(MainActivity.this);
        fab.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.app_color)));
        fab.setImageDrawable(getResources().getDrawable(R.drawable.pause_btn));
        sessionDistance = pausedSession.getDistance();
        sessionTimeDiff = pausedSession.getDuration();
        currentMaxSpeed = pausedSession.getMaxSpeed();
        averageSpeed = pausedSession.getAverageSpeed();
        sessionStartTimeMillis = new Date().getTime();
        currentUpdateTimeMillis = sessionStartTimeMillis;
        startPointCoord = null;
        currentCoordinates = null;
        updateInfoPanel(sessionDistance, averageSpeed, currentMaxSpeed, sessionTimeDiff, usersMetrics);
    }

    private void stopLogic() {
        startStopBtn.setBackgroundResource(R.drawable.start_btn);
        startButtonEnabled = false;

        if (currentCoordinates != null) {
            endPointCoord = currentCoordinates;
            currentSegment.add(currentCoordinates);
            mMap.addMarker(new MarkerOptions().position(currentCoordinates).title(getString(R.string.end_point)));
//            Thread snapShotThread = new Thread(new Runnable() {
//                @Override
//                public void run() {
//                    mMap.snapshot(new GoogleMap.SnapshotReadyCallback() {
//                        @Override
//                        public void onSnapshotReady(Bitmap bitmap) {
//                            sessionScreenShot = bitmap;
//                            sessionImagePath = Utility.saveToExternalStorage(bitmap, getApplicationContext());
//                            Toast.makeText(MainActivity.this, getString(R.string.screen_shot_successfully_saved), Toast.LENGTH_LONG).show();
//                            Log.d("url", sessionImagePath);
//                            // clear map
//                            mMap.clear();
//                        }
//                    }, sessionScreenShot);
//                }
//            });
//            snapShotThread.start();
            if (ParseUser.getCurrentUser() != null) {
                new ParseCommon().saveTraceStartAndEndCoord(startPointCoord, endPointCoord);
            }

            mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(currentCoordinates, Constants.MAP_ZOOM), Constants.ONE_SECOND, null);
        }

        stopLocationUpdates();

        Intent saveSessionIntent = new Intent(MainActivity.this, SaveSessionActivity.class);

        Session saveSession = new Session(
                sessionDistance,
                sessionTimeDiff,
                currentMaxSpeed,
                averageSpeed,
                new Calculations().calculateTimePerKilometer(sessionDistance, sessionTimeDiff),
                "",
                ParseUser.getCurrentUser(),
                userName,
                sportType.toString());

        Bundle saveBundle = new Bundle();
        saveBundle.putParcelable("Session", saveSession);
        saveBundle.putParcelable("start_coords", startPointCoord);
        saveBundle.putParcelable("end_coords", endPointCoord);
        saveBundle.putParcelable("currentSegment", currentSegment);

        saveSessionIntent.putExtras(saveBundle);
        overridePendingTransition(android.R.anim.fade_in,
                android.R.anim.fade_out);
        startActivity(saveSessionIntent);

//        if (listOfPoints.size() != 0 && sessionDistance != 0) {
//            saveSegmentToParse(currentSegment, listOfPoints, sessionDistance);
//        }

        // set all to null
        setVariablesToNull();
        updateInfoPanel(sessionDistance, averageSpeed, currentMaxSpeed, sessionTimeDiff, usersMetrics);

        ParseInstallation installation = ParseInstallation.getCurrentInstallation();
        installation.put("user", ParseUser.getCurrentUser());
        installation.saveEventually();
        fab.setVisibility(View.GONE);
    }

//    private void saveSegmentToParse(PolylineOptions polyLine, final ArrayList<ParseGeoPoint> points, final double dist) {
//        mMap.addPolyline(polyLine);
//
//        Segments segment = new Segments();
//        segment.setCurrentUser(ParseUser.getCurrentUser() != null ? ParseUser.getCurrentUser() : new ParseUser());
//        segment.setName("current_segment");
//        segment.setDistance(dist);
//        segment.setGeoPointsArray(points);
//        segment.saveEventually();

//        mMap.snapshot(new GoogleMap.SnapshotReadyCallback() {
//            @Override
//            public void onSnapshotReady(Bitmap bitmap) {
//                sessionScreenShot = bitmap;
//                // this save session image on sdcard
////                sessionImagePath = Utility.saveToExternalStorage(bitmap, getApplicationContext());
////                Toast.makeText(MainActivity.this, getString(R.string.screen_shot_successfully_saved), Toast.LENGTH_LONG).show();
////                Log.d("url", sessionImagePath);
//
//                // this save bitmap on parse and after callback save Segment
////                ByteArrayOutputStream stream = new ByteArrayOutputStream();
////                bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
////                byte[] data = stream.toByteArray();
////                final ParseFile file = new ParseFile("test" + segmentId + ".png", data);
////                file.saveInBackground(new SaveCallback() {
////                    @Override
////                    public void done(com.parse.ParseException e) {
////                        if (e == null) {
////                            Segments segment = new Segments();
////
////                            segment.setCurrentUser(ParseUser.getCurrentUser() != null ? ParseUser.getCurrentUser() : new ParseUser());
////                            segment.setSegmentId(segmentId);
////                            segment.setName("test" + segmentId);
////                            segment.setDistance(dist);
////                            segment.setMapImage(file);
////                            segment.setGeoPointsArray(points);
////                            segment.saveEventually();
////
////                            segmentId++;
////                            settings.edit().putInt("segmentId", segmentId).apply();
////                            currentSegment = null;
////                            // clear map
////                            mMap.clear();
////                        }
////                    }
////                });
//            }
//        });
//    }

    private void openDialogToLoginIfLoggedAsGuest() {
        if (new ParseCommon().getCurrentUserUsername().equals("Guest")) {
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
        sessionStartTimeMillis = 0;
        averageSpeed = 0;
        currentMaxSpeed = 0;
        currentTimeDiff = 0;
        currentDistance = 0;
        currentSpeed = 0;
        currentSegment = null;
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
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayShowTitleEnabled(false);
        }

        // set spinner
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(getApplicationContext(),
                R.array.array_type_of_sports, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(R.layout.toolbar_spinner_dropdown_item);
        chooseTypeSport.setAdapter(adapter);
        chooseTypeSport.setSelection(sportType.getIntValue(sportType.toString()));
        chooseTypeSport.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                sportType = sportType.getSportTypeValue(position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                Toast.makeText(MainActivity.this, "Nothing", Toast.LENGTH_SHORT).show();
            }
        });

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

        switch (menuItem.getItemId()) {
            case R.id.nav_login_fragment:
                if (ParseCommon.isUserLoggedIn() && !new ParseCommon().getCurrentUserUsername().equals("Guest")) {
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
                    logOutCurrentUser();
                    openParseLoginActivity();
                }
                break;
            case R.id.nav_history_fragment:
                Intent historyIntent = new Intent(MainActivity.this, HistoryLiteMapListActivity.class);
                overridePendingTransition(android.R.anim.fade_in,
                        android.R.anim.fade_out);
                startActivity(historyIntent);
                break;
            case R.id.nav_account_fragment:
                Intent accountIntent = new Intent(MainActivity.this, AccountActivity.class);
                Bundle accBundle = new Bundle();
                accBundle.putSerializable(getString(R.string.type_of_sport), sportType);
                accountIntent.putExtras(accBundle);
                overridePendingTransition(android.R.anim.fade_in,
                        android.R.anim.fade_out);
                startActivity(accountIntent);
                break;
            case R.id.nav_leaderboard_fragment:
                Intent leaderIntent = new Intent(MainActivity.this, LeaderBoardActivity.class);
                Bundle bundle = new Bundle();
                bundle.putSerializable(getString(R.string.type_of_sport), sportType);
                leaderIntent.putExtras(bundle);
                overridePendingTransition(android.R.anim.fade_in,
                        android.R.anim.fade_out);
                startActivity(leaderIntent);
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

        menuItem.setChecked(true);
        mDrawer.closeDrawers();
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
        // google analytics
        Log.i(Constants.TAG, "Setting screen name: " + "MainActivity");
        mTracker.setAppInstallerId(ParseInstallation.getCurrentInstallation().getObjectId());
        mTracker.send(new HitBuilders.ScreenViewBuilder().build());
//        setTitle(getString(R.string.app_name));
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

        if (myIncent.isAdReadyToDisplay()) {
            // An ad is ready.  Display the ad with one of the available show methods.
            myIncent.show(weakRef.get(), null, null, null, null);
        } else {
            // No rewarded ad is currently available.
            // todo remove
            Toast.makeText(this, "video ad is not available", Toast.LENGTH_SHORT).show();
        }

//        if (mRewardedAd.isLoaded()) {
//            mRewardedAd.show();
//        }

        if (mGoogleApiClient.isConnected()) {
            stopLocationUpdates();
            mGoogleApiClient.disconnect();
        }

        if (startButtonEnabled) {
            if (ParseUser.getCurrentUser() != null) {
                endPointCoord = new LatLng(currentLocation.getLatitude(), currentLocation.getLongitude());
                new ParseCommon().saveTraceStartAndEndCoord(startPointCoord, endPointCoord);
            }
//            saveSegmentToParse(currentSegment, listOfPoints, sessionDistance);
//            Session current = new SaveSessionActivity().createCurrentSession(sessionDistance, sessionTimeDiff, currentMaxSpeed, averageSpeed, sportType.toString());
//            new SaveSessionActivity().saveParseSession(current);
        }
        super.onDestroy();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (drawerToggle.onOptionsItemSelected(item)) {
            return true;
        }

        switch (item.getItemId()) {
            case R.id.action_about:
                Intent aboutIntent = new Intent(MainActivity.this, AboutActivity.class);
                overridePendingTransition(android.R.anim.fade_in,
                        android.R.anim.fade_out);
                startActivity(aboutIntent);
                return true;
            case R.id.action_help:
                Intent helpIntent = new Intent(MainActivity.this, HelpActivity.class);
                overridePendingTransition(android.R.anim.fade_in,
                        android.R.anim.fade_out);
                startActivity(helpIntent);
                return true;
//            case R.id.action_legal_notice:
//                // todo find to show google license
////                String licenseInfo = GooglePlayServicesUtil.getOpenSourceSoftwareLicenseInfo(
////                        getApplicationContext());
////                AlertDialog.Builder licenseDialog = new AlertDialog.Builder(MainActivity.this);
////                licenseDialog.setTitle(R.string.google_legal_notices);
////                licenseDialog.setMessage(licenseInfo);
////                licenseDialog.show();
//                return true;
            case R.id.action_world_map:
                Intent worldMapIntent = new Intent(MainActivity.this, WorldMapActivity.class);
                overridePendingTransition(android.R.anim.fade_in,
                        android.R.anim.fade_out);
                startActivity(worldMapIntent);
                return true;
//            case R.id.action_google_login:
//                Intent google = new Intent(MainActivity.this, GooglePlusLoginHelper.class);
//                overridePendingTransition(android.R.anim.fade_in,
//                        android.R.anim.fade_out);
//                startActivity(google);
////                new GoogleLogin(true);
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
        LoginBuilder builder = new LoginBuilder(MainActivity.this);
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
        startActivityForResult(parseLoginIntent, Constants.REQUEST_LOGIN_FROM_RESULT);
    }

    private void launchMarket() {
        Uri uri = Uri.parse("market://details?id=" + getPackageName());
//        Uri uri = Uri.parse("https://play.google.com/store/ereview?docId=" + getPackageName());
        try {
            overridePendingTransition(android.R.anim.fade_in,
                    android.R.anim.fade_out);
            startActivity(new Intent(Intent.ACTION_VIEW, uri));
        } catch (ActivityNotFoundException e) {
            Toast.makeText(MainActivity.this, getString(R.string.unable_find_market_app) + e.getMessage(), Toast.LENGTH_LONG).show();
            Log.i(Constants.TAG, e.getMessage());
        }
    }

    private void logOutCurrentUser() {
        new ParseCommon().logOutUser(this);
        showUsername.setText(R.string.guest);
        showTotalDistance.setText(generateTotalString(0.00, usersMetrics.getDistanceUnit()));
        facebookProfilePicture.setProfileId("");
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main_menu, menu);
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

    @TargetApi(Build.VERSION_CODES.M)
    @Override
    public void onMapReady(GoogleMap googleMap) {
        Log.i(Constants.TAG, "Map is ready");
//        Toast.makeText(this, "Map is ready", Toast.LENGTH_LONG).show();
        mMap = googleMap;
//        startPointCoord = new LatLng(mMap.getMyLocation().getLatitude(), mMap.getMyLocation().getLongitude());
//        Log.d(TAG, String.valueOf(mMap.getMyLocation().getLatitude()));
        mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.LOLLIPOP_MR1) {
            if (checkSelfPermission(android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                    && checkSelfPermission(android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(MainActivity.this,
                        new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION, android.Manifest.permission.ACCESS_COARSE_LOCATION},
                        Constants.MY_PERMISSIONS_REQUEST_ACCESS_LOCATION);
                return;
            }
        }

        mMap.setMyLocationEnabled(true);
        startPointCoord = SOFIA_CENTER;
        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(startPointCoord, Constants.MAP_ZOOM), Constants.ONE_SECOND, null);
        mMap.getUiSettings().setCompassEnabled(true);
        mMap.getUiSettings().setMyLocationButtonEnabled(false);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case Constants.MY_PERMISSIONS_REQUEST_ACCESS_LOCATION: {
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(MainActivity.this, "Permissions granted", Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(MainActivity.this, "Need GPS to use this app", Toast.LENGTH_LONG).show();
                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                }
                return;
            }
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    @Override
    public void onConnected(Bundle bundle) {
        Log.i(Constants.TAG, "Connected to GoogleApiClient");
        progressBar.setVisibility(View.GONE);
        startStopBtn.setVisibility(View.VISIBLE);
//        if (currentLocation == null) {
//            currentLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
//        }
//        currentUpdateTime = DateFormat.getTimeInstance().format(new Date());
        currentUpdateTimeMillis = new Date().getTime();
    }

    @Override
    public void onConnectionSuspended(int i) {
        // The connection to Google Play services was lost for some reason. We call connect() to
        // attempt to re-establish the connection.
        Log.i(Constants.TAG, "Connection suspended");
        mGoogleApiClient.connect();
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        // Refer to the javadoc for ConnectionResult to see what error codes might be returned in
        // onConnectionFailed.
        Log.i(Constants.TAG, "Connection failed: ConnectionResult.getErrorCode() = " + connectionResult.getErrorCode());
    }

    @Override
    public void setTitle(CharSequence title) {
//        getSupportActionBar().setTitle(title);
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
            case Constants.REQUEST_CHECK_SETTINGS:
                switch (resultCode) {
                    case Activity.RESULT_OK:
                        Log.i(Constants.TAG, "User agreed to make required location settings changes.");
                        startLocationUpdates();
                        break;
                    case Activity.RESULT_CANCELED:
                        Log.i(Constants.TAG, "User chose not to make required location settings changes.");
                        break;
                }
                break;
            case Constants.REQUEST_LOGIN_FROM_RESULT:
                // from parse login
                if (mInterstitialAd.isLoaded()) {
                    mInterstitialAd.show();
                }

                // connect installation with user
                if (ParseUser.getCurrentUser() != null) {
                    installation.put("user", ParseUser.getCurrentUser());
                    installation.saveEventually();

                    // create account object from current user
                    currentUserAccount = ParseCommon.convertFromUserToAccount(ParseUser.getCurrentUser(), MainActivity.this, sportType);
                    currentUserAccount.setGender((Gender) getIntent().getExtras().get("gender"));
                    Double width = getIntent().getExtras().getDouble("weight") != 0 ? getIntent().getExtras().getDouble("weight") : 0;
                    Double height = getIntent().getExtras().getDouble("height") != 0 ? getIntent().getExtras().getDouble("height") : 0;
                    currentUserAccount.setUserWeight(width);
                    currentUserAccount.setUserHeight(height);

                    String mail = currentUserAccount.getEmail() != null ? currentUserAccount.getEmail() : "";
                    String faceId = "";
                    if (ParseFacebookUtils.isLinked(ParseUser.getCurrentUser())) {
                        faceId = AccessToken.getCurrentAccessToken().getUserId();
                        // get facebook mail
                        getFacebookMail(currentUserAccount, faceId);
                    } else {
                        currentUserAccount = ParseCommon.createAndSaveAccount(mail, faceId, currentUserAccount, UserMetrics.METRIC, MainActivity.this);
                        ParseCommon.checkIfAccountExistAndSave(currentUserAccount);
                    }
                }

                calculateTotalDistance(ParseUser.getCurrentUser(), usersMetrics);

                break;
        }

        setCurrentUserUsername();

        super.onActivityResult(requestCode, resultCode, data);
    }

    private void calculateTotalDistance(ParseUser currentUser, final UserMetricsInterface unit) {
        if (!new ParseCommon().getCurrentUserUsername().equals("Guest")) {
            ParseQuery<Sessions> query = Sessions.getQuery();
            query.whereEqualTo("username", currentUser);
            query.findInBackground(new FindCallback<Sessions>() {
                @Override
                public void done(List<Sessions> objects, ParseException e) {
                    if (e == null && objects.size() > 0) {
                        Double total = 0.0;
                        for (Sessions ses : objects) {
                            total += ses.getDistance();
                        }
                        total = Calculations.roundDigitsAfterDecimalPoint(total / 1000, 3);
                        showTotalDistance.setText(getString(R.string.total_distance) + " " + total + " " + unit.getDistanceUnit());
                    }
                }
            });
        }
    }

    private void getFacebookMail(final Account current, final String faceId) {
        GraphRequest request = GraphRequest.newMeRequest(
                AccessToken.getCurrentAccessToken(),
                new GraphRequest.GraphJSONObjectCallback() {
                    @Override
                    public void onCompleted(JSONObject object, GraphResponse graphResponse) {
                        try {
                            String userEmail = object.getString("email");
                            Account finalAccount = ParseCommon.createAndSaveAccount(userEmail, faceId, current, UserMetrics.METRIC, MainActivity.this);
                            ParseCommon.checkIfAccountExistAndSave(finalAccount);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                });
        Bundle parameters = new Bundle();
        parameters.putString("fields", "age_range,gender,name,id,link,email,picture.type(large),first_name,last_name");
        request.setParameters(parameters);
        request.executeAsync();
    }

    @Override
    public void onResult(LocationSettingsResult locationSettingsResult) {
        final Status status = locationSettingsResult.getStatus();
        switch (status.getStatusCode()) {
            case LocationSettingsStatusCodes.SUCCESS:
                Log.i(Constants.TAG, "All location settings are satisfied.");
//                startLocationUpdates();
                break;
            case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:
                Log.i(Constants.TAG, "Location settings are not satisfied. Show the user a dialog to" +
                        "upgrade location settings ");
                try {
                    // Show the dialog by calling startResolutionForResult(), and check the result
                    // in onActivityResult().
                    status.startResolutionForResult(MainActivity.this, Constants.REQUEST_CHECK_SETTINGS);
                } catch (IntentSender.SendIntentException e) {
                    Log.i(Constants.TAG, "PendingIntent unable to execute request.");
                }
                break;
            case LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE:
                Log.i(Constants.TAG, "Location settings are inadequate, and cannot be fixed here. Dialog " +
                        "not created.");
                break;
        }
    }

    @Override
    public void onBackPressed() {
        if (exit) {
            if (new ParseCommon().getCurrentUserUsername().equals("Guest")) {
                logOutCurrentUser();
            }

            finish();
            stopLocationUpdates();
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
            }, Constants.THREE_SECOND);
        }
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        // Pass any configuration change to the drawer toggls
        drawerToggle.onConfigurationChanged(newConfig);
    }

    private void updateValuesFromBundle(Bundle savedInstanceState) {
        Log.i(Constants.TAG, "Updating values from bundle");
        if (savedInstanceState != null) {
            if (savedInstanceState.keySet().contains(Constants.PAUSED_SESSION)) {
                pausedSession = savedInstanceState.getParcelable(Constants.PAUSED_SESSION);
            }
            if (savedInstanceState.keySet().contains(Constants.LAST_UPDATED_TIME_STRING_KEY)) {
                currentUpdateTimeMillis = savedInstanceState.getLong(
                        Constants.LAST_UPDATED_TIME_STRING_KEY);
            }
            if (savedInstanceState.keySet().contains(Constants.LOCATION_KEY)) {
                currentLocation = savedInstanceState.getParcelable(Constants.LOCATION_KEY);
            }
            if (savedInstanceState.keySet().contains(Constants.GLOBAL_DISTANCE)) {
                sessionDistance = savedInstanceState.getDouble(Constants.GLOBAL_DISTANCE);
            }
            if (savedInstanceState.keySet().contains(Constants.GLOBAL_AVERAGE_SPEED)) {
                averageSpeed = savedInstanceState.getDouble(Constants.GLOBAL_AVERAGE_SPEED);
            }
            if (savedInstanceState.keySet().contains(Constants.GLOBAL_MAX_SPEED)) {
                currentMaxSpeed = savedInstanceState.getDouble(Constants.GLOBAL_MAX_SPEED);
            }
            if (savedInstanceState.keySet().contains(Constants.GLOBAL_DURATION)) {
                sessionTimeDiff = savedInstanceState.getLong(Constants.GLOBAL_DURATION);
            }
            if (savedInstanceState.keySet().contains(Constants.SESSION_START_TIME)) {
                sessionStartTimeMillis = savedInstanceState.getLong(Constants.SESSION_START_TIME);
            }
            if (savedInstanceState.keySet().contains(Constants.CURRENT_SEGMENT)) {
                currentSegment = savedInstanceState.getParcelable(Constants.CURRENT_SEGMENT);
            }
            if (savedInstanceState.keySet().contains(Constants.IS_STARTED)) {
                startButtonEnabled = savedInstanceState.getBoolean(Constants.IS_STARTED);
                if (startButtonEnabled) {
                    startLocationUpdates();
                    startStopBtn.setBackgroundResource(R.drawable.stop_btn);
                    updateInfoPanel(sessionDistance, averageSpeed, currentMaxSpeed, sessionTimeDiff, usersMetrics);
                }
            }

            if (savedInstanceState.keySet().contains(Constants.IS_PAUSED)) {
                isPausedActivityEnable = savedInstanceState.getBoolean(Constants.IS_PAUSED);
                if (isPausedActivityEnable && pausedSession != null) {
                    fab.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.app_color)));
                    fab.setImageDrawable(getResources().getDrawable(R.drawable.resume_btn));
                    updateInfoPanel(pausedSession.getDistance(), pausedSession.getAverageSpeed(), pausedSession.getMaxSpeed(), pausedSession.getDuration(), usersMetrics);
                }
            }
        }
    }

    public void onSaveInstanceState(Bundle savedInstanceState) {
        // todo save mGoogleApiClient and mLocationRequest
        savedInstanceState.putParcelable(Constants.LOCATION_KEY, currentLocation);
        savedInstanceState.putBoolean(Constants.IS_STARTED, startButtonEnabled);
        savedInstanceState.putBoolean(Constants.IS_PAUSED, isPausedActivityEnable);
        savedInstanceState.putDouble(Constants.GLOBAL_DISTANCE, sessionDistance);
        savedInstanceState.putDouble(Constants.GLOBAL_AVERAGE_SPEED, averageSpeed);
        savedInstanceState.putDouble(Constants.GLOBAL_MAX_SPEED, currentMaxSpeed);
        savedInstanceState.putLong(Constants.GLOBAL_DURATION, sessionTimeDiff);
        savedInstanceState.putLong(Constants.SESSION_START_TIME, sessionStartTimeMillis);
        savedInstanceState.putLong(Constants.LAST_UPDATED_TIME_STRING_KEY, currentUpdateTimeMillis);
        savedInstanceState.putParcelable(Constants.CURRENT_SEGMENT, currentSegment);
        savedInstanceState.putParcelable(Constants.PAUSED_SESSION, pausedSession);

        super.onSaveInstanceState(savedInstanceState);
    }

    protected void startLocationUpdates() {
        try {
            if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                // TODO: Consider calling
                //    ActivityCompat#requestPermissions
                // here to request the missing permissions, and then overriding
                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                //                                          int[] grantResults)
                // to handle the case where the user grants the permission. See the documentation
                // for ActivityCompat#requestPermissions for more details.
                return;
            }
            LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this)
                    .setResultCallback(new ResultCallback<Status>() {
                        @Override
                        public void onResult(Status status) {

                        }
                    });
        } catch (Throwable e) {
            Toast.makeText(MainActivity.this, getString(R.string.gps_not_available), Toast.LENGTH_LONG).show();
        }
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

    @Override
    public void onLocationChanged(Location location) {
        Log.i(Constants.TAG, "Location changed");
//        Toast.makeText(this, "Location changed", Toast.LENGTH_LONG).show();
        currentLocation = location;
        if (startButtonEnabled && !isPausedActivityEnable && location != null) {
            updateUI(currentLocation);
        }
    }

    private void updateUI(Location currLoc) {
        if (currLoc != null) {
            Log.i(Constants.TAG, "Update UI");
            lastUpdateTimeMillis = currentUpdateTimeMillis;
            currentUpdateTimeMillis = new Date().getTime();
            if (currentCoordinates != null) {
                lastUpdatedCoord = currentCoordinates;
                currentCoordinates = smoothLocation(currLoc, lastUpdatedCoord.latitude, lastUpdatedCoord.longitude);
            } else {
                currentCoordinates = smoothLocation(currLoc, currLoc.getLatitude(), currLoc.getLongitude());
                startPointCoord = currentCoordinates;
                lastUpdatedCoord = currentCoordinates;
            }

            currentDistance = new Calculations().calculateDistance(lastUpdatedCoord, currentCoordinates);
            sessionDistance += currentDistance;
            currentTimeDiff = currentUpdateTimeMillis - lastUpdateTimeMillis;
            sessionTimeDiff += currentTimeDiff;
            currentSpeed = Calculations.calculateSpeed(currentTimeDiff, currentDistance);
            averageSpeed = Calculations.calculateSpeed(sessionTimeDiff, sessionDistance);
            currentMaxSpeed = Calculations.calculateMaxSpeed(currentSpeed, currentMaxSpeed, sportType);

            updateInfoPanel(sessionDistance, averageSpeed, currentMaxSpeed, sessionTimeDiff, usersMetrics);

            if (mMap != null) {
                if (currentSegment != null) {
                    currentSegment.add(lastUpdatedCoord, currentCoordinates);

                    listOfPoints.add(new ParseGeoPoint(currentCoordinates.latitude, currentCoordinates.longitude));
                    // todo fix this to change color of polyline not add all segment but only current points
                    // this change color but is not good

//                    PolylineOptions miniSegment = new PolylineOptions();
//                    miniSegment.width(POLYLINE_WIDTH);
//                    miniSegment.add(lastUpdatedCoord, currentCoordinates);
//
//                    if (currentSpeed > MAX_SPEED_LIMIT) {
//                        miniSegment.color(POLYLINE_COLOR_RED);
//                    } else {
//                        miniSegment.color(POLYLINE_COLOR);
//                    }
//
//                    mMap.addPolyline(miniSegment);
                    mMap.addPolyline(currentSegment);
                }
                mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(currentCoordinates, Constants.MAP_ZOOM), Constants.ONE_SECOND, null);
            }
        }
    }

    private void updateInfoPanel(double sessionDistance, double averageSpeed, double currentMaxSpeed, long sessionTimeDiff, UserMetricsInterface speedMetricUnit) {
        String speedUnit = speedMetricUnit.getSpeedUnit();
        distanceMeter.setText(String.valueOf((sessionDistance / 1000) + " " + speedMetricUnit.getDistanceUnit()));
        speedMeter.setText(String.valueOf(averageSpeed) + speedUnit);
        timeMeter.setText(Calculations.convertTimeToString(sessionTimeDiff));
        maxSpeedMeter.setText(String.valueOf(currentMaxSpeed) + speedUnit);
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

    private void createGoogleMap() {
        mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }

    protected synchronized void buildGoogleApiClient() {
        Log.i(Constants.TAG, "Building GoogleApiClient");
        progressBar.setVisibility(View.VISIBLE);
        Thread buildGoogleApiThread = new Thread(new Runnable() {
            @Override
            public void run() {
                mGoogleApiClient = new GoogleApiClient.Builder(MainActivity.this)
                        .addConnectionCallbacks(MainActivity.this)
                        .addOnConnectionFailedListener(MainActivity.this)
                        .addApi(LocationServices.API)
                        .build();
                createLocationRequest();
                buildLocationSettingsRequest();
                checkLocationSettings();
            }
        });
        buildGoogleApiThread.start();
    }

    protected void createLocationRequest() {
        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(Constants.UPDATE_INTERVAL_IN_MILLISECONDS);
        mLocationRequest.setFastestInterval(Constants.FASTEST_UPDATE_INTERVAL_IN_MILLISECONDS);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
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

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.fab:
                if (!isPausedActivityEnable) {
                    pauseLogic();
                } else {
                    resumeLogic();
                }
                break;
            case R.id.start_stop_btn:
                if (!startButtonEnabled) {
                    startLogic();
                } else {
                    stopLogic();
                    if (mInterstitialAd.isLoaded()) {
                        mInterstitialAd.show();
                    }
                }
                break;
        }
    }

    private String generateTotalString(Double totalDistance, String distanceUnit) {
        return getString(R.string.total_distance) + " " + totalDistance + " " + distanceUnit;
    }

    // for applovin video ads
    @Override
    public void onRewardedVideoAdLoaded() {
        Toast.makeText(this, "onRewardedVideoAdLoaded", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onRewardedVideoAdOpened() {
        Toast.makeText(this, "onRewardedVideoAdOpened", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onRewardedVideoStarted() {
        Toast.makeText(this, "onRewardedVideoStarted", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onRewardedVideoAdClosed() {
        Toast.makeText(this, "onRewardedVideoAdClosed", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onRewarded(RewardItem rewardItem) {
        Toast.makeText(this, "onRewarded! currency: " + rewardItem.getType() + "  amount: " +
                rewardItem.getAmount(), Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onRewardedVideoAdLeftApplication() {
        Toast.makeText(this, "onRewardedVideoAdLeftApplication",
                Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onRewardedVideoAdFailedToLoad(int i) {
        Toast.makeText(this, "onRewardedVideoAdFailedToLoad", Toast.LENGTH_SHORT).show();
    }
}