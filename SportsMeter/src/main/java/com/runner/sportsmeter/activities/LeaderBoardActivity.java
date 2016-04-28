package com.runner.sportsmeter.activities;

import android.app.Activity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.*;
import com.google.android.gms.ads.AdView;
import com.parse.*;
import com.runner.sportsmeter.R;
import com.runner.sportsmeter.common.Constants;
import com.runner.sportsmeter.adapters.RecyclerAdapter;
import com.runner.sportsmeter.common.Utility;
import com.runner.sportsmeter.enums.SportTypes;
import com.runner.sportsmeter.models.Sessions;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by angelr on 20-Aug-15.
 */
public class LeaderBoardActivity extends Activity {

    private Button bestRunners, bestBikers, bestDrivers, myBest;
    private Spinner chooseTypeSport;
    private ProgressBar bar;
    public ArrayList<Sessions> arrayOfSessions;
    private AdView mAdView;


    private RecyclerView mRecyclerView;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;
    private SportTypes sportType;
    private SportTypes sportTypeForQuery = SportTypes.CHOOSE_SPORT;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.l_leaderboard_layout);

        sportType = (SportTypes) getIntent().getExtras().get(getString(R.string.type_of_sport));

        initializeViews();

        // set spinner
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(getApplicationContext(),
                R.array.array_type_of_sports, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(R.layout.toolbar_spinner_dropdown_item);

        chooseTypeSport.setAdapter(adapter);
        chooseTypeSport.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                sportTypeForQuery = sportType.getSportTypeValue(position);
                if(!sportTypeForQuery.equals(SportTypes.CHOOSE_SPORT)) {
                    ParseQuery(sportTypeForQuery, Constants.LIMIT_FOR_SPORT_TYPE, null);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
//        bestRunners.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                ParseQuery(SportTypes.RUNNING, LIMIT_FOR_SPORT_TYPE, null);
//            }
//        });
//
//        bestBikers.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                ParseQuery(SportTypes.BIKING, LIMIT_FOR_SPORT_TYPE, null);
//            }
//        });
//
//        bestDrivers.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                ParseQuery(SportTypes.RUNNING, LIMIT_FOR_SPORT_TYPE, null);
//            }
//        });

        myBest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ParseQuery(sportType, Constants.LIMIT_FOR_USER_QUERY, ParseUser.getCurrentUser());
            }
        });

        // use this setting to improve performance if you know that changes
        // in content do not change the layout size of the RecyclerView
        mRecyclerView.setHasFixedSize(true);

        // use a linear layout manager
        mLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(mLayoutManager);

        ParseQuery(sportType, Constants.LIMIT_FOR_USER_QUERY, ParseUser.getCurrentUser());

        // setup adds
        mAdView = (AdView) findViewById(R.id.adViewLeaderBoard);
        new Utility().setupAdds(mAdView, this);
    }

    private void initializeViews() {
        bar = (ProgressBar) this.findViewById(R.id.progressBar);
        chooseTypeSport = (Spinner) findViewById(R.id.leaderboard_spinner);
//        bestRunners = (Button) findViewById(R.id.btn_best_runners);
//        bestBikers = (Button) findViewById(R.id.btn_best_bikers);
//        bestDrivers = (Button) findViewById(R.id.btn_best_drivers);
        myBest = (Button) findViewById(R.id.btn_my_best_result);
        // for recycle
        mRecyclerView = (RecyclerView) findViewById(R.id.my_recycler_view);
    }

    private void BestResultByType(List<Sessions> sessions) {
        Log.d("session", "Retrieved " + sessions.size() + " sessions");
        arrayOfSessions = new ArrayList<>();
        arrayOfSessions.addAll(sessions);
        refreshListView();
        bar.setVisibility(View.GONE);
    }

    private void BestResultByUser(List<Sessions> sessions) {
        Log.d("session", "Retrieved " + sessions.size() + " sessions");
        arrayOfSessions = new ArrayList<>();
        arrayOfSessions.addAll(sessions);
        refreshListView();
        bar.setVisibility(View.GONE);
        ParseObject.pinAllInBackground("myBestResult", sessions);
    }

    private void ParseQuery(SportTypes type, int limit, final ParseUser user) {
        bar.setVisibility(View.VISIBLE);
        ParseQuery<Sessions> query = Sessions.getQuery();
        if (user != null) {
            query.whereEqualTo(getString(R.string.session_username), user);
        }
        query.whereEqualTo(getString(R.string.session_sport_type), type.toString().toLowerCase());
        query.orderByAscending(getString(R.string.session_time_per_kilometer));
        query.setLimit(limit);
        query.findInBackground(new FindCallback<Sessions>() {
            public void done(List<Sessions> sessions, ParseException e) {
                if (e == null) {
                    if(user != null) {
                        BestResultByUser(sessions);
                    } else {
                        BestResultByType(sessions);
                    }
                } else {
                    Log.e("session", "Error: " + e.getMessage());
                    finish();
                }
            }
        });
    }

    private void refreshListView() {
//        adapter = new SessionAdapter(LeaderBoardActivity.this, R.layout.leaderboard_row, arrayOfSessions);
        mAdapter = new RecyclerAdapter(arrayOfSessions);
        mRecyclerView.setAdapter(mAdapter);
        mAdapter.notifyDataSetChanged();
    }
}
