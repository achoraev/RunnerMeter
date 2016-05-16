package com.runner.sportsmeter.activities;

import android.Manifest;
import android.annotation.TargetApi;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import com.facebook.share.widget.LikeView;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;
import com.parse.*;
import com.runner.sportsmeter.R;
import com.runner.sportsmeter.common.Calculations;
import com.runner.sportsmeter.common.ParseCommon;
import com.runner.sportsmeter.common.Utility;
import com.runner.sportsmeter.fragments.PostFacebookFragment;
import com.runner.sportsmeter.models.Segments;
import com.runner.sportsmeter.models.Session;
import com.runner.sportsmeter.models.Sessions;

import java.util.*;

/**
 * Created by angelr on 09-Oct-15.
 */
public class SaveSessionActivity extends AppCompatActivity implements OnMapReadyCallback {

    public static final int MAP_WIDTH = 400;
    public static final int MAP_HEIGHT = 200;
    public static final int MAP_PADDING = 20;
    private static final int MY_PERMISSIONS_REQUEST_ACCESS_LOCATION = 0x1;
    private Session currentSession;
    private String sessionImagePath;
    private TextView saveTimeKm, saveDistance, saveDuration, saveUsername,
            saveMaxSpeed, saveAvgSpeed, saveTypeSport, saveCreatedAt;
    private Button saveBtn, notSaveBtn, postOnFacebookBtn;
    private ImageView sessionScreenshot;
    private ParseObject saveSession;
    private LatLng startPointCoordinates, endPointCoordinates;
    private LikeView likeView;
    private Boolean isSaveSession = false;
    private SupportMapFragment mapFragment;
    private GoogleMap mMap;
    private PolylineOptions currentSegment;
    private LatLngBounds bound;
    private Sessions currentParseSession;
    private List<LatLng> listOfPoints;
    private ArrayList<ParseGeoPoint> arrayListOfParseGeoPoints;
    private AdView mAdView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.l_save_session_layout);
        updateFromBundle(getIntent().getExtras());
        ParseCommon.logInGuestUser(this);
        initializeViews();
        initializeMap();
        currentParseSession = new Utility().convertSessionToParseSessions(currentSession);
        setTextViewsFromSession(currentSession);

        saveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveParseSession(currentParseSession);
                if (endPointCoordinates != null) {
                    String url = "google.streetview:cbll=" + endPointCoordinates.latitude + "," + endPointCoordinates.longitude;
                    Uri gmmIntentUri = Uri.parse(url);
                    Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
                    mapIntent.setPackage("com.google.android.apps.maps");
                    if (mapIntent.resolveActivity(getPackageManager()) != null) {
                        startActivity(mapIntent);
                    }
                }
                finish();
            }
        });

        postOnFacebookBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveParseSession(currentParseSession);
                // todo check for logged user with facebook
                postOnFacebookWall();
                finish();
            }
        });

        notSaveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        // initialize like for facebook likes
        likeView = (LikeView) findViewById(R.id.like_page);
        likeView.setObjectIdAndType(
                getString(R.string.facebook_page),
                LikeView.ObjectType.PAGE);
        likeView.setVisibility(View.VISIBLE);
        likeView.setLikeViewStyle(LikeView.Style.STANDARD);

        mAdView = (AdView) findViewById(R.id.adViewSave);
        new Utility().setupAdds(mAdView, this);
    }

    private void initializeMap() {
        mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }

    private void setTextViewsFromSession(Session session) {
//        sessionScreenshot.setImageResource(R.mipmap.icon_new);

        saveTimeKm.setText(Utility.formatPace(session.getTimePerKilometer()));
        saveDistance.setText(Utility.formatDistance(session.getDistance()));
        saveDuration.setText(Calculations.convertTimeToString(session.getDuration()));
        saveUsername.setText(String.valueOf(session.getUserName()));
        saveMaxSpeed.setText(Utility.formatSpeed(session.getMaxSpeed()));
        saveAvgSpeed.setText(Utility.formatSpeed(session.getAverageSpeed()));
        saveTypeSport.setText(session.getSportType());
        saveCreatedAt.setText(Utility.formatDate(new Date()));
    }



//    public Sessions createCurrentSessions(double dist, Long time, double max, double average, String type) {
//        Sessions current = new Sessions();
//        current.setDistance(dist);
//        current.setDuration(time);
//        current.setMaxSpeed(max);
//        current.setAverageSpeed(average);
//        current.setSportType(type.toLowerCase());
//        current.setParseUser(ParseUser.getCurrentUser() != null ? ParseUser.getCurrentUser() : new ParseUser());
//        current.setName(new ParseCommon().getCurrentUserUsername());
//        current.setTimePerKilometer(new Calculations().calculateTimePerKilometer(dist, time));
////        Session current = new Session(
////                dist,
////                time,
////                max,
////                average,
////                "",
////                ParseUser.getCurrentUser() != null ? ParseUser.getCurrentUser() : new ParseUser(),
////                new ParseCommon().getCurrentUserUsername(),
////                type.toLowerCase());
//        return current;
//    }

    private void initializeViews() {
//        sessionScreenshot = (ImageView) findViewById(R.id.session_screenshot);

        saveBtn = (Button) findViewById(R.id.button_save);
        notSaveBtn = (Button) findViewById(R.id.button_not_save);
        postOnFacebookBtn = (Button) findViewById(R.id.button_post_facebook);

        saveTimeKm = (TextView) findViewById(R.id.save_time_kilometer);
        saveDistance = (TextView) findViewById(R.id.save_distance);
        saveDuration = (TextView) findViewById(R.id.save_duration);
        saveUsername = (TextView) findViewById(R.id.save_username);
        saveMaxSpeed = (TextView) findViewById(R.id.save_max_speed);
        saveAvgSpeed = (TextView) findViewById(R.id.save_average_speed);
        saveTypeSport = (TextView) findViewById(R.id.save_type_sport);
        saveCreatedAt = (TextView) findViewById(R.id.save_created_at);
    }

    private void postOnFacebookWall() {
        ParseFacebookUtils.linkWithPublishPermissionsInBackground(
                ParseUser.getCurrentUser(),
                SaveSessionActivity.this,
                Arrays.asList("publish_actions"));
        Intent postIntent = new Intent(SaveSessionActivity.this, PostFacebookFragment.class);
        Bundle postBundle = new Bundle();
        postBundle.putParcelable("Session", currentSession);
        postIntent.putExtras(postBundle);
        startActivity(postIntent);
    }

    @Override
    public void onBackPressed() {
        if (!isSaveSession) {
            saveParseSession(currentParseSession);
            isSaveSession = false;
        }
        super.onBackPressed();
    }

    public void saveParseSession(Sessions current) {
        saveSegmentToParse(arrayListOfParseGeoPoints, current);
    }

    private void saveSegmentToParse(ArrayList<ParseGeoPoint> points, final Sessions current) {
        Random rand = new Random();
        final ParseACL defaultACL = new ParseACL();
        defaultACL.setPublicReadAccess(true);
        defaultACL.setPublicWriteAccess(true);
        if (current.getDistance() > 20) {
            final Segments segment = new Segments();
            segment.setCurrentUser(ParseUser.getCurrentUser() != null ? ParseUser.getCurrentUser() : new ParseUser());
            segment.setName("segment_" + rand.nextInt(123456));
            segment.setDistance(current.getDistance());
            segment.setGeoPointsArray(points);
            segment.setACL(defaultACL);
            segment.saveEventually(new SaveCallback() {
                @Override
                public void done(ParseException e) {
                    if(e == null){
                        isSaveSession = true;
                        Boolean isValid = new Calculations().isTimePerKilometerValid(current.getTimePerKilometer(), current.getSportType());
                        if (current.getDistance() > 20 && current.getTimePerKilometer() != 0 && isValid) {
                            current.setSegmentId(segment);
                            current.setACL(defaultACL);
                            current.saveEventually();
                            current.pinInBackground();
                        } else if (current.getTimePerKilometer() != 0 && !isValid) {
                            String message = getString(R.string.this_time) + " " + current.getTimePerKilometer() + " " + getString(R.string.time_is_fastest) + " " + current.getSportType();
                            Toast.makeText(SaveSessionActivity.this, message, Toast.LENGTH_LONG).show();
                        }
                    } else {
                        Toast.makeText(SaveSessionActivity.this, R.string.save_notsave_btn, Toast.LENGTH_LONG).show();
                    }
                }
            });
        }
    }

    private void updateFromBundle(Bundle savedInstanceState) {
        if (savedInstanceState != null) {
            if (savedInstanceState.keySet().contains("currentSegment")) {
                currentSegment = savedInstanceState.getParcelable("currentSegment");
                if(currentSegment != null) {
                    listOfPoints = currentSegment.getPoints();
                    arrayListOfParseGeoPoints = ParseCommon.convertListToArrayListOfParseGeoPoint(listOfPoints);
                }
            }
            if (savedInstanceState.keySet().contains("Session")) {
                currentSession = savedInstanceState.getParcelable("Session");
            }
            if (savedInstanceState.keySet().contains("start_coords")) {
                startPointCoordinates = savedInstanceState.getParcelable("start_coords");
            }
            if (savedInstanceState.keySet().contains("end_coords")) {
                endPointCoordinates = savedInstanceState.getParcelable("end_coords");
            }
        }
    }

    @TargetApi(Build.VERSION_CODES.M)
    @Override
    public void onMapReady(GoogleMap googleMap) {
        Log.d("save_map", "Map is ready");
        mMap = googleMap;
        mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
        // for android 6.0
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.LOLLIPOP_MR1) {
            if (checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                    && checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

                ActivityCompat.requestPermissions(SaveSessionActivity.this,
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION},
                        MY_PERMISSIONS_REQUEST_ACCESS_LOCATION);
                return;
            }
        }
        mMap.setMyLocationEnabled(true);
        mMap.getUiSettings().setCompassEnabled(true);
        mMap.getUiSettings().setMyLocationButtonEnabled(true);
        if (currentSegment != null && currentSegment.getPoints().size() != 0) {
            List<LatLng> list = currentSegment.getPoints();
            startPointCoordinates = list.get(0);
            endPointCoordinates = list.get(list.size() - 1);
            LatLngBounds.Builder builder = new LatLngBounds.Builder();
            builder.include(startPointCoordinates);
            builder.include(endPointCoordinates);
            bound = builder.build();
//            bound = new LatLngBounds(endPointCoordinates, startPointCoordinates);
            mMap.addMarker(new MarkerOptions().position(startPointCoordinates).title(getString(R.string.start_point)));
            mMap.addMarker(new MarkerOptions().position(endPointCoordinates).title(getString(R.string.end_point)));
            mMap.addPolyline(currentSegment);
            mMap.animateCamera(CameraUpdateFactory.newLatLngBounds(bound, MAP_WIDTH, MAP_HEIGHT, MAP_PADDING));
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_ACCESS_LOCATION: {
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(SaveSessionActivity.this, "Permissions granted", Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(SaveSessionActivity.this, "Need GPS to use this app", Toast.LENGTH_LONG).show();
                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                }
                return;
            }
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }
}
