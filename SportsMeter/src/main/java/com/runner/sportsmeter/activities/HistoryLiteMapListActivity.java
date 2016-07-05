package com.runner.sportsmeter.activities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;
import com.google.android.gms.maps.*;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;
import com.parse.*;
import com.runner.sportsmeter.R;
import com.runner.sportsmeter.common.Constants;
import com.runner.sportsmeter.common.ParseCommon;
import com.runner.sportsmeter.common.Utility;
import com.runner.sportsmeter.enums.SportTypes;
import com.runner.sportsmeter.fragments.PostFacebookFragment;
import com.runner.sportsmeter.models.Segments;
import com.runner.sportsmeter.models.Sessions;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;

/**
 * Created by angelr on 15-Dec-15.
 */
public class HistoryLiteMapListActivity extends AppCompatActivity {
    private static final Integer QUERY_SIZE = 15;
    private TextView emptyList;
    private ListFragment mList;
    private MapAdapter mAdapter;
    private SportTypes sportType;
    private List<Sessions> historySession = new ArrayList<>();
    private Spinner chooseTypeSport;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.lite_list_layout);

        sportType = (SportTypes) getIntent().getExtras().get(getString(R.string.type_of_sport));
        emptyList = (TextView) findViewById(R.id.empty_list);
        chooseTypeSport = (Spinner) findViewById(R.id.choose_type_of_sport_history);
        generateSportTypeSpinner();

        ParseQuery(QUERY_SIZE, ParseUser.getCurrentUser());
    }

    private void ParseQuery(int limit, final ParseUser user) {
        ParseQuery<Sessions> query = ParseQuery.getQuery(getString(R.string.session_object));
        query.whereEqualTo(getString(R.string.session_username), user);
        query.include("segmentId");
        query.whereExists("segmentId");
        query.orderByAscending(getString(R.string.session_time_per_kilometer));
        query.setLimit(limit);
        query.findInBackground(new FindCallback<Sessions>() {
            public void done(List<Sessions> sessions, ParseException e) {
                if (e == null) {
                    // todo remove
//                    Toast.makeText(HistoryLiteMapListActivity.this, "Get from Parse.", Toast.LENGTH_SHORT).show();
                    if (user != null) {
                        AssignSessions(sessions);
                        historySession.addAll(sessions);
                        ParseObject.pinAllInBackground("HistorySegments", sessions);
                    }
                } else {
                    Log.e("session", "Error: " + e.getMessage());
                    finish();
                }
            }
        });
    }

    private void AssignSessions(List<Sessions> sessions) {
        Log.d("session", "Retrieved " + sessions.size() + " sessions");
        // Set a custom list adapter for a list of locations
        if (sessions.size() > 0) {
            mAdapter = new MapAdapter(this, sessions);
            mList = (ListFragment) getSupportFragmentManager().findFragmentById(R.id.list);
            mList.setListAdapter(mAdapter);

            // Set a RecyclerListener to clean up MapView from ListView
            AbsListView lv = mList.getListView();
            lv.setRecyclerListener(mRecycleListener);
            emptyList.setVisibility(View.GONE);
            emptyList.setText("");
        } else {
            emptyList.setVisibility(View.VISIBLE);
            emptyList.setText(R.string.msg_empty_list);
        }
    }

    /**
     * Adapter that displays a title and {@link com.google.android.gms.maps.MapView} for each item.
     * The layout is defined in <code>lite_list_demo_row.xml</code>. It contains a MapView
     * that is programatically initialised in
     * {@link #getView(int, android.view.View, android.view.ViewGroup)}
     */
    private class MapAdapter extends ArrayAdapter<Sessions> {

        private final HashSet<MapView> mMaps = new HashSet<MapView>();

        public MapAdapter(Context context, List<Sessions> locations) {
            super(context, R.layout.lite_map_list_row, R.id.lite_listrow_text, locations);
        }


        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            View row = convertView;
            ViewHolder holder;

            // Check if a view can be reused, otherwise inflate a layout and set up the view holder
            if (row == null) {
                // Inflate view from layout file
                row = getLayoutInflater().inflate(R.layout.lite_map_list_row, null);

                // Set up holder and assign it to the View
                holder = new ViewHolder();
                holder.mapView = (MapView) row.findViewById(R.id.lite_listrow_map);
                holder.title = (TextView) row.findViewById(R.id.lite_listrow_text);
                holder.pace = (TextView) row.findViewById(R.id.lite_pace);
                holder.distance = (TextView) row.findViewById(R.id.lite_distance);
                holder.duration = (TextView) row.findViewById(R.id.lite_total_time);
                holder.sportTypeField = (TextView) row.findViewById(R.id.lite_sport_type);
                holder.createdAt = (TextView) row.findViewById(R.id.lite_created);
                holder.shareOnFacebook = (Button) row.findViewById(R.id.button_list_share_facebook);
                // Set holder as tag for row for more efficient access.
                row.setTag(holder);

                // Initialise the MapView
                holder.initializeMapView();

                // Keep track of MapView
                mMaps.add(holder.mapView);
            } else {
                // View has already been initialised, get its holder
                holder = (ViewHolder) row.getTag();
            }

            // Get the NamedLocation for this item and attach it to the MapView
            final Sessions currentSession = getItem(position);
            holder.mapView.setTag(currentSession);

            // Ensure the map has been initialised by the on map ready callback in ViewHolder.
            // If it is not ready yet, it will be initialised with the NamedLocation set as its tag
            // when the callback is received.
            if (holder.map != null) {
                // The map is already ready to be used
                setMapLocation(holder.map, currentSession);
            }

            // Set the text label for this item
            holder.title.setText(currentSession.getName());
            holder.pace.setText(Utility.formatPace(currentSession.getTimePerKilometer()));
            holder.distance.setText(Utility.formatDistance(currentSession.getDistance()));
            holder.duration.setText(Utility.formatDurationToMinutesString(currentSession.getDuration()));
            holder.sportTypeField.setText(currentSession.getSportType());
            holder.createdAt.setText(Utility.formatDate(currentSession.getCreatedAt()));
            if(ParseFacebookUtils.isLinked(ParseUser.getCurrentUser())) {
                holder.shareOnFacebook.setVisibility(View.VISIBLE);
                holder.shareOnFacebook.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        postOnFacebookWall(currentSession);
                    }
                });
            }

            row.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent viewIntent = new Intent(HistoryLiteMapListActivity.this, ShowSessionActivity.class);
                    Bundle viewBundle = new Bundle();
                    viewBundle.putParcelable("Session", new Utility().convertParseSessionsToSession(currentSession));
                    viewIntent.putExtras(viewBundle);
                    startActivity(viewIntent);
                }
            });
            return row;
        }

        /**
         * Retuns the set of all initialised {@link MapView} objects.
         *
         * @return All MapViews that have been initialised programmatically by this adapter
         */
        public HashSet<MapView> getMaps() {
            return mMaps;
        }

        private void postOnFacebookWall(Sessions session) {
            ParseFacebookUtils.linkWithPublishPermissionsInBackground(
                    ParseUser.getCurrentUser(),
                    HistoryLiteMapListActivity.this,
                    Arrays.asList("publish_actions"));
            Intent postIntent = new Intent(HistoryLiteMapListActivity.this, PostFacebookFragment.class);
            Bundle postBundle = new Bundle();
            postBundle.putParcelable("Session", new Utility().convertParseSessionsToSession(session));
            postIntent.putExtras(postBundle);
            startActivity(postIntent);
        }
    }

    /**
     * Displays a {@linkLiteListDemoActivity.NamedLocation} on a
     * {@link com.google.android.gms.maps.GoogleMap}.
     * Adds a marker and centers the camera on the NamedLocation with the normal map type.
     */
    private void setMapLocation(GoogleMap map, Sessions data) {
        Segments currentSegment = null;
        ArrayList<ParseGeoPoint> geoPointsParse = new ArrayList<>();
        PolylineOptions currentPolyline = new PolylineOptions()
                .width(Constants.POLYLINE_WIDTH)
                .color(Constants.POLYLINE_COLOR);

        if (data != null) {
            currentSegment = data.getSegmentId();
        }

        if (currentSegment != null) {
            geoPointsParse = currentSegment.getGeoPointsArray();
        }

        if (geoPointsParse != null && geoPointsParse.size() != 0) {
            List<LatLng> geoPoint = ParseCommon.convertArrayListOfParseGeoPointToList(geoPointsParse);
            currentPolyline.addAll(geoPoint);
            // Add a marker for this item and set the camera
            map.addPolyline(currentPolyline);
            map.addMarker(new MarkerOptions().position(geoPoint.get(0)).title(getString(R.string.start_point)).visible(true));
            map.addMarker(new MarkerOptions().position(geoPoint.get(geoPoint.size() - 1)).title(getString(R.string.end_point)).visible(true));
            map.moveCamera(CameraUpdateFactory.newLatLngZoom(geoPoint.get(0), 13f));
        }

        // Set the map type back to normal.
        map.setMapType(GoogleMap.MAP_TYPE_NORMAL);
    }

    /**
     * Holder for Views used in the {@linkLiteListDemoActivity.MapAdapter}.
     * Once the  the <code>map</code> field is set, otherwise it is null.
     * When the {@link #onMapReady(com.google.android.gms.maps.GoogleMap)} callback is received and
     * the {@link com.google.android.gms.maps.GoogleMap} is ready, it stored in the {@link #map}
     * field. The map is then initialised with the NamedLocation that is stored as the tag of the
     * MapView. This ensures that the map is initialised with the latest data that it should
     * display.
     */
    class ViewHolder implements OnMapReadyCallback {

        MapView mapView;

        TextView title;

        TextView pace;

        TextView distance;

        TextView duration;

        TextView sportTypeField;

        TextView createdAt;

        GoogleMap map;

        Button shareOnFacebook;


        @Override
        public void onMapReady(GoogleMap googleMap) {
            MapsInitializer.initialize(getApplicationContext());
            map = googleMap;
            Sessions data = (Sessions) mapView.getTag();
            if (data != null) {
                setMapLocation(map, data);
            }
        }

        /**
         * Initialises the MapView by calling its lifecycle methods.
         */
        public void initializeMapView() {
            if (mapView != null) {
                // Initialise the MapView
                mapView.onCreate(null);
                // Set the map ready callback to receive the GoogleMap object
                mapView.getMapAsync(this);
            }
        }
    }

    /**
     * RecycleListener that completely clears the {@link com.google.android.gms.maps.GoogleMap}
     * attached to a row in the ListView.
     * Sets the map type to {@link com.google.android.gms.maps.GoogleMap#MAP_TYPE_NONE} and clears
     * the map.
     */
    private AbsListView.RecyclerListener mRecycleListener = new AbsListView.RecyclerListener() {

        @Override
        public void onMovedToScrapHeap(View view) {
            ViewHolder holder = (ViewHolder) view.getTag();
            if (holder != null && holder.map != null) {
                // Clear the map and free up resources by changing the map type to none
                holder.map.clear();
                holder.map.setMapType(GoogleMap.MAP_TYPE_NONE);
            }
        }
    };

    private void generateSportTypeSpinner() {
        // set spinner and data
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(getApplicationContext(),
                R.array.array_type_of_sports, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(R.layout.spinner_dropdown_item);
        chooseTypeSport.setAdapter(adapter);
        chooseTypeSport.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                sportType = sportType.getSportTypeValue(position);
                historySession = sortListBySportType(historySession, sportType);
                refreshListView(historySession);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
    }

    private List<Sessions> sortListBySportType(List<Sessions> historySession, SportTypes sportType) {
        List<Sessions> result = new ArrayList<>();
        for(Sessions ses : historySession){
            if(ses.getSportType().equals(sportType.toString())){
                result.add(ses);
            }
        }
        return result;
    }

    private void refreshListView(List<Sessions> list) {
        if (list.size() > 0) {
            emptyList.setVisibility(View.GONE);
//            mAdapter = new MapAdapter(this, list);
//            mList = (ListFragment) getSupportFragmentManager().findFragmentById(R.id.list);
//            mList.setListAdapter(mAdapter);
//            AbsListView lv = mList.getListView();
//            lv.setRecyclerListener(mRecycleListener);
            mAdapter.notifyDataSetChanged();
        } else {
            emptyList.setVisibility(View.VISIBLE);
            emptyList.setText(R.string.msg_empty_list);
        }
    }
}
