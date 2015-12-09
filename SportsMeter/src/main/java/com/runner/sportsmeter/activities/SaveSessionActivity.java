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
import com.parse.ParseACL;
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

/**
 * Created by angelr on 09-Oct-15.
 */
public class SaveSessionActivity extends AppCompatActivity implements OnMapReadyCallback {

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.l_save_session_layout);
        savedInstanceState = getIntent().getExtras();
        updateFromBundle(savedInstanceState);
        initializeMap();
        ParseCommon.logInGuestUser(this);
        initializeViews();
        createCurrentSession();
        setTextViewsFromSession();

        saveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveParseSession();
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
                saveParseSession();
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

    private void setTextViewsFromSession() {
//        sessionScreenshot.setImageResource(R.mipmap.icon_new);

        saveTimeKm.setText(String.valueOf(currentSession.getTimePerKilometer()) + " min/km");
        saveDistance.setText(String.valueOf(currentSession.getDistance()) + " m");
        saveDuration.setText(Calculations.convertTimeToString((long) currentSession.getDuration()));
        saveUsername.setText(String.valueOf(currentSession.getUserName()));
        saveMaxSpeed.setText(String.valueOf(currentSession.getMaxSpeed()) + " km/h");
        saveAvgSpeed.setText(String.valueOf(currentSession.getAverageSpeed()) + " km/h");
        saveTypeSport.setText(sportType);
        saveCreatedAt.setText(Utility.formatDate(new Date()));
    }

    private void createCurrentSession() {
        currentSession = new Session(
                sessionDistance,
                sessionTimeDiff,
                currentMaxSpeed,
                averageSpeed,
                "",
                ParseUser.getCurrentUser() != null ? ParseUser.getCurrentUser() : new ParseUser(),
                ParseUser.getCurrentUser() != null && ParseUser.getCurrentUser().get(getString(R.string.session_name)) != null
                        ? ParseUser.getCurrentUser().get(getString(R.string.session_name)).toString()
                        : null,
                sportType.toLowerCase());
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
            saveParseSession();
        }
        super.onBackPressed();
    }

    private void saveParseSession() {
        isSaveSession = true;
        ParseACL acl = new ParseACL();
        acl.setPublicReadAccess(true);
        acl.setPublicWriteAccess(false);

        saveSession = new ParseObject(getString(R.string.session_object));
        saveSession.put(getString(R.string.session_name), currentSession.getUserName());
        saveSession.put(getString(R.string.session_username), currentSession.getCurrentUser());
        saveSession.put(getString(R.string.session_max_speed), currentSession.getMaxSpeed());
        saveSession.put(getString(R.string.session_average_speed), currentSession.getAverageSpeed());
        saveSession.put(getString(R.string.session_distance), currentSession.getDistance());
        saveSession.put(getString(R.string.session_duration), currentSession.getDuration() / 1000);
        saveSession.put(getString(R.string.session_time_per_kilometer), currentSession.getTimePerKilometer());
        saveSession.put(getString(R.string.session_sport_type), currentSession.getSportType());
        saveSession.setACL(acl);
        Boolean isValid = new Calculations().isTimePerKilometerValid(currentSession.getTimePerKilometer(), currentSession.getSportType());
        if (currentSession.getTimePerKilometer() != 0 && isValid) {
            saveSession.saveEventually();
            saveSession.pinInBackground();
        } else if (currentSession.getTimePerKilometer() != 0 && !isValid) {
            String message = getString(R.string.this_time) + " " + currentSession.getTimePerKilometer() + " " + getString(R.string.time_is_fastest) + " " + currentSession.getSportType();
            Toast.makeText(SaveSessionActivity.this, message, Toast.LENGTH_LONG).show();
        }
    }

    private void updateFromBundle(Bundle savedInstanceState) {
        if (savedInstanceState != null) {

            if (savedInstanceState.keySet().contains("currentSegment")) {
                currentSegment = savedInstanceState.getParcelable("currentSegment");
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
            if (savedInstanceState.keySet().contains("session_distance")) {
                sessionDistance = savedInstanceState.getDouble("session_distance");
            }
            if (savedInstanceState.keySet().contains("session_time_diff")) {
                sessionTimeDiff = savedInstanceState.getDouble("session_time_diff");
            }
            if (savedInstanceState.keySet().contains("current_max_speed")) {
                currentMaxSpeed = savedInstanceState.getDouble("current_max_speed");
            }
            if (savedInstanceState.keySet().contains("average_speed")) {
                averageSpeed = savedInstanceState.getDouble("average_speed");
            }
            if (savedInstanceState.keySet().contains("sport_type")) {
                sportType = savedInstanceState.getString("sport_type");
            }
            if (savedInstanceState.keySet().contains("session_image_path")) {
                sessionImagePath = savedInstanceState.getString("session_image_path");
            }
        }
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        Log.d("save_map", "Map is ready");
        mMap = googleMap;
        mMap.setMapType(GoogleMap.MAP_TYPE_HYBRID);
        mMap.setMyLocationEnabled(true);
        mMap.getUiSettings().setCompassEnabled(true);
        mMap.getUiSettings().setMyLocationButtonEnabled(true);
        if(startPointCoordinates != null && endPointCoordinates != null && currentSegment != null) {
            mMap.addMarker(new MarkerOptions().position(startPointCoordinates).title(getString(R.string.start_point)));
//            mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(startPointCoordinates, 14), 1000, null);
            mMap.animateCamera(CameraUpdateFactory.newLatLngBounds(new LatLngBounds(startPointCoordinates, endPointCoordinates), 10));
            mMap.addPolyline(currentSegment);
            mMap.addMarker(new MarkerOptions().position(endPointCoordinates).title(getString(R.string.end_point)));
        }
    }
}
