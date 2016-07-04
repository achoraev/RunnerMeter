package com.runner.sportsmeter.activities;

import android.Manifest;
import android.annotation.TargetApi;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.PolylineOptions;
import com.parse.ParseFacebookUtils;
import com.parse.ParseUser;
import com.runner.sportsmeter.R;
import com.runner.sportsmeter.common.Utility;
import com.runner.sportsmeter.fragments.PostFacebookFragment;
import com.runner.sportsmeter.models.Session;
import com.runner.sportsmeter.models.Sessions;

import java.util.Arrays;
import java.util.List;

/**
 * Created on 04-Jul-16
 */
public class ShowSessionActivity extends AppCompatActivity implements OnMapReadyCallback {

    private static final int MY_PERMISSIONS_REQUEST_ACCESS_LOCATION = 0x1;
    private Session currentSession;
//    private TextView saveTimeKm, saveDistance, saveDuration, saveUsername,
//            saveMaxSpeed, saveAvgSpeed, saveTypeSport, saveCreatedAt;
    private Button postOnFacebookBtn;
    private LatLng startPointCoordinates, endPointCoordinates;
//    private LikeView likeView;
    private SupportMapFragment mapFragment;
    private GoogleMap mMap;
    private PolylineOptions currentSegment;
//    private LatLngBounds bound;
    private Sessions currentParseSession;
//    private ArrayList<ParseGeoPoint> arrayListOfParseGeoPoints;
//    private AdView mAdView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.l_show_session_layout);
        updateFromBundle(getIntent().getExtras());
        initializeViews();
        initializeMap();
        currentParseSession = new Utility().convertSessionToParseSessions(currentSession);
        setTextViewsFromSession(currentSession);

        postOnFacebookBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                postOnFacebookWall();
            }
        });

        // initialize like for facebook likes
//        likeView = (LikeView) findViewById(R.id.like_page);
//        likeView.setObjectIdAndType(
//                getString(R.string.facebook_page),
//                LikeView.ObjectType.PAGE);
//        likeView.setVisibility(View.VISIBLE);
//        likeView.setLikeViewStyle(LikeView.Style.STANDARD);
//
//        mAdView = (AdView) findViewById(R.id.adViewSave);
//        new Utility().setupAdds(mAdView, this);
    }

    private void initializeMap() {
        mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.save_map);
        mapFragment.getMapAsync(this);
    }

    private void setTextViewsFromSession(Session session) {
//        saveTimeKm.setText(Utility.formatPace(session.getTimePerKilometer()));
//        saveDistance.setText(Utility.formatDistance(session.getDistance()));
//        saveDuration.setText(Calculations.convertTimeToString(session.getDuration()));
//        saveUsername.setText(String.valueOf(session.getUserName()));
//        saveMaxSpeed.setText(Utility.formatSpeed(session.getMaxSpeed()));
//        saveAvgSpeed.setText(Utility.formatSpeed(session.getAverageSpeed()));
//        saveTypeSport.setText(session.getSportType());
//        saveCreatedAt.setText(Utility.formatDate(new Date()));
    }

    private void initializeViews() {
        postOnFacebookBtn = (Button) findViewById(R.id.button_list_share_facebook);
//        saveTimeKm = (TextView) findViewById(R.id.save_time_kilometer);
//        saveDistance = (TextView) findViewById(R.id.save_distance);
//        saveDuration = (TextView) findViewById(R.id.save_duration);
//        saveUsername = (TextView) findViewById(R.id.save_username);
//        saveMaxSpeed = (TextView) findViewById(R.id.save_max_speed);
//        saveAvgSpeed = (TextView) findViewById(R.id.save_average_speed);
//        saveTypeSport = (TextView) findViewById(R.id.save_type_sport);
//        saveCreatedAt = (TextView) findViewById(R.id.save_created_at);
    }

    private void postOnFacebookWall() {
        ParseFacebookUtils.linkWithPublishPermissionsInBackground(
                ParseUser.getCurrentUser(),
                ShowSessionActivity.this,
                Arrays.asList("publish_actions"));
        Intent postIntent = new Intent(ShowSessionActivity.this, PostFacebookFragment.class);
        Bundle postBundle = new Bundle();
        postBundle.putParcelable("Session", currentSession);
        postIntent.putExtras(postBundle);
        startActivity(postIntent);
    }

    private void updateFromBundle(Bundle savedInstanceState) {
        if (savedInstanceState != null) {
//            if (savedInstanceState.keySet().contains("currentSegment")) {
//                currentSegment = savedInstanceState.getParcelable("currentSegment");
//                if(currentSegment != null) {
//                    arrayListOfParseGeoPoints = ParseCommon.convertListToArrayListOfParseGeoPoint(currentSegment.getPoints());
//                }
//            }
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

                ActivityCompat.requestPermissions(ShowSessionActivity.this,
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION},
                        MY_PERMISSIONS_REQUEST_ACCESS_LOCATION);
                return;
            }
        }
        mMap.setMyLocationEnabled(true);
        mMap.getUiSettings().setCompassEnabled(true);
        mMap.getUiSettings().setMyLocationButtonEnabled(true);
//        mMap.addMarker(new MarkerOptions().position(startPointCoordinates).title(getString(R.string.start_point)));
//        mMap.addMarker(new MarkerOptions().position(endPointCoordinates).title(getString(R.string.end_point)));
        if (currentSegment != null && currentSegment.getPoints().size() != 0) {
            List<LatLng> list = currentSegment.getPoints();
//            startPointCoordinates = list.get(0);
//            endPointCoordinates = list.get(list.size() - 1);
//            LatLngBounds.Builder builder = new LatLngBounds.Builder();
//            builder.include(startPointCoordinates);
//            builder.include(endPointCoordinates);
//            bound = builder.build();
//            bound = new LatLngBounds(endPointCoordinates, startPointCoordinates);
//            mMap.addMarker(new MarkerOptions().position(startPointCoordinates).title(getString(R.string.start_point)));
//            mMap.addMarker(new MarkerOptions().position(endPointCoordinates).title(getString(R.string.end_point)));
            mMap.addPolyline(currentSegment);
//            mMap.animateCamera(CameraUpdateFactory.newLatLngBounds(bound, MAP_WIDTH, MAP_HEIGHT, MAP_PADDING));
        }
    }
}
