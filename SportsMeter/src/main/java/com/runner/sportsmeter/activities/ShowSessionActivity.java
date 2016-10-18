package com.runner.sportsmeter.activities;

import android.Manifest;
import android.annotation.TargetApi;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;
import com.parse.ParseGeoPoint;
import com.runner.sportsmeter.R;
import com.runner.sportsmeter.common.Constants;
import com.runner.sportsmeter.common.Utility;
import com.runner.sportsmeter.models.Session;
import com.runner.sportsmeter.models.Sessions;

import java.util.ArrayList;

/**
 * Created on 04-Jul-16
 */
public class ShowSessionActivity extends AppCompatActivity implements OnMapReadyCallback {

    private static final int MY_PERMISSIONS_REQUEST_ACCESS_LOCATION = 0x1;
    private Session currentSession;
    private TextView showTimeKm, showDistance, showDuration, showUsername,
            showMaxSpeed, showAvgSpeed, showTypeSport, showCreatedAt;
    private Button shareFacebook;
    private LatLng startPointCoordinates, endPointCoordinates;
    //    private LikeView likeView;
    private SupportMapFragment mapFragment;
    private GoogleMap mMap;
    private PolylineOptions currentSegment;
    private Sessions currentParseSession;
    private ArrayList<ParseGeoPoint> arrayListOfParseGeoPoints;
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
        shareFacebook.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Utility.postOnFacebookWall(currentSession, ShowSessionActivity.this);
            }
        });
        showTimeKm.setText(Utility.formatPace(session.getTimePerKilometer()));
        showDistance.setText(Utility.formatDistance(session.getDistance()));
        showDuration.setText(Utility.formatDurationToMinutesString(session.getDuration()));
        showUsername.setText(String.valueOf(session.getUserName()));
//        saveMaxSpeed.setText(Utility.formatSpeed(session.getMaxSpeed()));
//        saveAvgSpeed.setText(Utility.formatSpeed(session.getAverageSpeed()));
        showTypeSport.setText(session.getSportType());
//        showCreatedAt.setText(Utility.formatDate(session.getCreatedAt()));
        showCreatedAt.setText(session.getCreatedAt());
    }

    private void initializeViews() {
        shareFacebook = (Button) findViewById(R.id.button_share);
        showTimeKm = (TextView) findViewById(R.id.show_pace);
        showDistance = (TextView) findViewById(R.id.show_distance);
        showDuration = (TextView) findViewById(R.id.show_total_time);
        showUsername = (TextView) findViewById(R.id.show_name);
//        showMaxSpeed = (TextView) findViewById(R.id.save_max_speed);
//        saveAvgSpeed = (TextView) findViewById(R.id.save_average_speed);
        showTypeSport = (TextView) findViewById(R.id.show_sport_type);
        showCreatedAt = (TextView) findViewById(R.id.show_created);
    }

    private void updateFromBundle(Bundle savedInstanceState) {
        if (savedInstanceState != null) {
            if (savedInstanceState.keySet().contains("Session")) {
                currentSession = savedInstanceState.getParcelable("Session");
            }
            if (savedInstanceState.keySet().contains("Segment")) {
                currentSegment = savedInstanceState.getParcelable("Segment");
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
                    && checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager
                    .PERMISSION_GRANTED) {

                ActivityCompat.requestPermissions(ShowSessionActivity.this,
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission
                                .ACCESS_COARSE_LOCATION},
                        MY_PERMISSIONS_REQUEST_ACCESS_LOCATION);
                return;
            }
        }
        mMap.setMyLocationEnabled(true);
        mMap.getUiSettings().setCompassEnabled(true);
        mMap.getUiSettings().setMyLocationButtonEnabled(true);

        if (currentSegment != null) {
            startPointCoordinates = currentSegment.getPoints().get(0);
            endPointCoordinates = currentSegment.getPoints().get(currentSegment.getPoints().size() - 1);

            mMap.addMarker(new MarkerOptions().position(startPointCoordinates).title(getString(R.string.start_point)));
            mMap.addMarker(new MarkerOptions().position(endPointCoordinates).title(getString(R.string.end_point)));
            mMap.addPolyline(currentSegment);
            mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(endPointCoordinates, Constants.MAP_ZOOM));
            mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(startPointCoordinates, Constants.MAP_ZOOM));
        }
    }
}
