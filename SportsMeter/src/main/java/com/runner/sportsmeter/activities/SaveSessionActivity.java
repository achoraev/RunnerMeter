package com.runner.sportsmeter.activities;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import com.facebook.share.widget.LikeView;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;
import com.parse.ParseFacebookUtils;
import com.parse.ParseObject;
import com.parse.ParseUser;
import com.runner.sportsmeter.R;
import com.runner.sportsmeter.common.Calculations;
import com.runner.sportsmeter.common.ParseCommon;
import com.runner.sportsmeter.common.Utility;
import com.runner.sportsmeter.models.Session;

import java.util.Arrays;
import java.util.Date;
import java.util.List;

/**
 * Created by angelr on 09-Oct-15.
 */
public class SaveSessionActivity extends AppCompatActivity implements OnMapReadyCallback {

    public static final int MAP_WIDTH = 400;
    public static final int MAP_HEIGHT = 200;
    public static final int MAP_PADDING = 20;
    private Session currentSession;
    private double sessionDistance;
    private double sessionTimeDiff;
    private double currentMaxSpeed;
    private double averageSpeed;
    private String sportType;
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.l_save_session_layout);
        savedInstanceState = getIntent().getExtras();
        updateFromBundle(savedInstanceState);
        initializeMap();
        ParseCommon.logInGuestUser(this);
        initializeViews();
        currentSession = createCurrentSession(sessionDistance, sessionTimeDiff, currentMaxSpeed, averageSpeed, sportType);
        setTextViewsFromSession(currentSession);

        saveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveParseSession(currentSession);
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
                saveParseSession(currentSession);
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
    }

    private void initializeMap() {
        mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }

    private void setTextViewsFromSession(Session session) {
//        sessionScreenshot.setImageResource(R.mipmap.icon_new);

        saveTimeKm.setText(String.valueOf(session.getTimePerKilometer()) + " min/km");
        saveDistance.setText(String.valueOf(session.getDistance()) + " m");
        saveDuration.setText(Calculations.convertTimeToString((long) session.getDuration()));
        saveUsername.setText(String.valueOf(session.getUserName()));
        saveMaxSpeed.setText(String.valueOf(session.getMaxSpeed()) + " km/h");
        saveAvgSpeed.setText(String.valueOf(session.getAverageSpeed()) + " km/h");
        saveTypeSport.setText(sportType);
        saveCreatedAt.setText(Utility.formatDate(new Date()));
    }

    public Session createCurrentSession(double dist, double time, double max, double average, String type) {
        Session current = new Session(
                dist,
                time,
                max,
                average,
                "",
                ParseUser.getCurrentUser() != null ? ParseUser.getCurrentUser() : new ParseUser(),
                new ParseCommon().getCurrentUserUsername(),
                type.toLowerCase());
        return current;
    }

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
            saveParseSession(currentSession);
        }
        super.onBackPressed();
    }

    public void saveParseSession(Session current) {
        isSaveSession = true;
        saveSession = new ParseObject("Sessions");
        saveSession.put(getString(R.string.session_name), current.getUserName());
        saveSession.put(getString(R.string.session_username), current.getCurrentUser());
        saveSession.put(getString(R.string.session_max_speed), current.getMaxSpeed());
        saveSession.put(getString(R.string.session_average_speed), current.getAverageSpeed());
        saveSession.put(getString(R.string.session_distance), current.getDistance());
        saveSession.put(getString(R.string.session_duration), current.getDuration() / 1000);
        saveSession.put(getString(R.string.session_time_per_kilometer), current.getTimePerKilometer());
        saveSession.put(getString(R.string.session_sport_type), current.getSportType());
        Boolean isValid = new Calculations().isTimePerKilometerValid(current.getTimePerKilometer(), current.getSportType());
        if (current.getTimePerKilometer() != 0 && isValid) {
            saveSession.saveEventually();
            saveSession.pinInBackground();
        } else if (current.getTimePerKilometer() != 0 && !isValid) {
            String message = getString(R.string.this_time) + " " + current.getTimePerKilometer() + " " + getString(R.string.time_is_fastest) + " " + current.getSportType();
            Toast.makeText(SaveSessionActivity.this, message, Toast.LENGTH_LONG).show();
        }
    }

    private void updateFromBundle(Bundle savedInstanceState) {
        if (savedInstanceState != null) {

            if (savedInstanceState.keySet().contains("Session")) {
                currentSession = savedInstanceState.getParcelable("Session");
                sessionDistance = currentSession.getDistance();
                sessionTimeDiff = currentSession.getDuration();
                currentMaxSpeed = currentSession.getMaxSpeed();
                averageSpeed = currentSession.getAverageSpeed();
                sportType = currentSession.getSportType();
            }
            if (savedInstanceState.keySet().contains("start_coords")) {
                startPointCoordinates = savedInstanceState.getParcelable("start_coords");
            }
            if (savedInstanceState.keySet().contains("end_coords")) {
                endPointCoordinates = savedInstanceState.getParcelable("end_coords");
            }
            if (savedInstanceState.keySet().contains("currentSegment")) {
                currentSegment = savedInstanceState.getParcelable("currentSegment");
            }
        }
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        Log.d("save_map", "Map is ready");
        mMap = googleMap;
        mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
        mMap.setMyLocationEnabled(true);
        mMap.getUiSettings().setCompassEnabled(true);
        mMap.getUiSettings().setMyLocationButtonEnabled(true);
        if (currentSegment != null) {
            List<LatLng> list = currentSegment.getPoints();
            startPointCoordinates = list.get(0);
            endPointCoordinates = list.get(list.size() - 1);
            if (startPointCoordinates.latitude < endPointCoordinates.latitude) {
                bound = new LatLngBounds(startPointCoordinates, endPointCoordinates);
            } else {
                bound = new LatLngBounds(endPointCoordinates, startPointCoordinates);
            }
            mMap.addMarker(new MarkerOptions().position(startPointCoordinates).title(getString(R.string.start_point)));
            mMap.addMarker(new MarkerOptions().position(endPointCoordinates).title(getString(R.string.end_point)));
            mMap.addPolyline(currentSegment);
            mMap.animateCamera(CameraUpdateFactory.newLatLngBounds(bound, MAP_WIDTH, MAP_HEIGHT, MAP_PADDING));
        } else if(startPointCoordinates != null && endPointCoordinates != null){
            mMap.addMarker(new MarkerOptions().position(startPointCoordinates).title(getString(R.string.start_point)));
            mMap.addMarker(new MarkerOptions().position(endPointCoordinates).title(getString(R.string.end_point)));
            mMap.animateCamera(CameraUpdateFactory.newLatLng(startPointCoordinates));
        } else if(startPointCoordinates != null){
            mMap.addMarker(new MarkerOptions().position(startPointCoordinates).title(getString(R.string.start_point)));
            mMap.animateCamera(CameraUpdateFactory.newLatLng(startPointCoordinates));
        }
    }
}
