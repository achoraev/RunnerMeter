package com.runner.sportsmeter.activities;

import android.app.Activity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import com.google.android.gms.ads.AdView;
import com.parse.*;
import com.runner.sportsmeter.R;
import com.runner.sportsmeter.common.RecyclerAdapter;
import com.runner.sportsmeter.common.Utility;
import com.runner.sportsmeter.enums.SportTypes;
import com.runner.sportsmeter.models.Session;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by angelr on 20-Aug-15.
 */
public class LeaderBoardActivity extends Activity implements View.OnClickListener {

    Button bestRunners, bestBikers, bestDrivers, myBest;
    private ProgressBar bar;
    public ArrayList<Session> arrayOfSessions;
    AdView mAdView;

    private RecyclerView mRecyclerView;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;
    private SportTypes sportType;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.l_leaderboard_layout);

        Bundle bundle = getIntent().getExtras();
        sportType = (SportTypes) bundle.get(getString(R.string.type_of_sport));

        bar = (ProgressBar) this.findViewById(R.id.progressBar);
        bestRunners = (Button) findViewById(R.id.btn_best_runners);
        bestBikers = (Button) findViewById(R.id.btn_best_bikers);
        bestDrivers = (Button) findViewById(R.id.btn_best_drivers);
        myBest = (Button) findViewById(R.id.btn_my_best_result);

        bestRunners.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ParseQueryBestResultsTask(SportTypes.runner);
//                new QueryBestRunnersTask().execute();
            }
        });

        bestBikers.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ParseQueryBestResultsTask(SportTypes.biker);
            }
        });

        bestDrivers.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ParseQueryBestResultsTask(SportTypes.driver);
            }
        });

        myBest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ParseQueryMyBestResult();
            }
        });

        // for recycle
        mRecyclerView = (RecyclerView) findViewById(R.id.my_recycler_view);

        // use this setting to improve performance if you know that changes
        // in content do not change the layout size of the RecyclerView
        mRecyclerView.setHasFixedSize(true);

        // use a linear layout manager
        mLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(mLayoutManager);

        ParseQueryMyBestResult();

        // setup adds
        mAdView = (AdView) findViewById(R.id.adViewLeaderBoard);
        new Utility().setupAdds(mAdView, this);
    }

    private void ParseQueryBestResultsTask(SportTypes type) {
        bar.setVisibility(View.VISIBLE);
        ParseQuery<ParseObject> query = ParseQuery.getQuery(getString(R.string.session_object));
        query.whereEqualTo(getString(R.string.session_sport_type), type.toString());
        query.orderByAscending(getString(R.string.session_time_per_kilometer));
        query.setLimit(15);
        query.findInBackground(new FindCallback<ParseObject>() {
            public void done(List<ParseObject> sessions, ParseException e) {
                if (e == null) {
                    Log.d("session", "Retrieved " + sessions.size() + " sessions");
                    arrayOfSessions = new ArrayList<>();
                    arrayOfSessions = Utility.convertFromParseObject(sessions);
                    refreshListView();
                    bar.setVisibility(View.GONE);
                } else {
                    Log.e("session", "Error: " + e.getMessage());
                }
            }
        });
    }

    private void ParseQueryMyBestResult() {
        bar.setVisibility(View.VISIBLE);
        ParseQuery<ParseObject> query = ParseQuery.getQuery(getString(R.string.session_object));
//        query.fromLocalDatastore()
        query.whereEqualTo(getString(R.string.session_username), ParseUser.getCurrentUser());
        query.whereEqualTo(getString(R.string.session_sport_type), sportType.toString());
        query.orderByAscending(getString(R.string.session_time_per_kilometer));
        query.setLimit(20);
        query.findInBackground(new FindCallback<ParseObject>() {
            public void done(List<ParseObject> sessions, ParseException e) {
                if (e == null) {
                    Log.d("session", "Retrieved " + sessions.size() + " sessions");
                    arrayOfSessions = new ArrayList<>();
                    arrayOfSessions = Utility.convertFromParseObject(sessions);
                    refreshListView();
                    bar.setVisibility(View.GONE);
                    ParseObject.pinAllInBackground("myBestResult", sessions);
//                    ParseObject.unpinAllInBackground("highScores", new DeleteCallback() {
//                        @Override
//                        public void done(ParseException e) {
//                            ParseObject.pinAllInBackground("highScores", sessions);
//                        }
//                    });
                } else {
                    Log.e("session", "Error: " + e.getMessage());
                }
            }
        });
    }

    @Override
    public void onClick(View v) {

    }

    private class QueryBestRunnersTask extends AsyncTask<Void, Void, ArrayList<Session>> {
        @Override
        protected void onPreExecute() {
            bar.setVisibility(View.VISIBLE);
        }

        @Override
        protected ArrayList<Session> doInBackground(Void... arg0) {
//            ParseQuery<ParseObject> query = ParseQuery.getQuery(getString(R.string.session_object));
//            query.orderByAscending(getString(R.string.session_time_per_kilometer));
//            query.setLimit(15);
//            query.findInBackground(new FindCallback<ParseObject>() {
//                public void done(List<ParseObject> sessions, ParseException e) {
//                    if (e == null) {
//                        Log.d("session", "Retrieved " + sessions.size() + " sessions");
//                        arrayOfSessions = new ArrayList<>();
//                        arrayOfSessions = Utility.convertFromParseObject(sessions);
////                        refreshListView();
//                    } else {
//                        Log.e("session", "Error: " + e.getMessage());
//                    }
//                }
//            });
            return arrayOfSessions;
        }

        @Override
        protected void onPostExecute(ArrayList<Session> result) {
            refreshListView();
            bar.setVisibility(View.GONE);
        }
    }

    private void refreshListView() {
//        adapter = new SessionAdapter(LeaderBoardActivity.this, R.layout.leaderboard_row, arrayOfSessions);
        mAdapter = new RecyclerAdapter(arrayOfSessions);
        mRecyclerView.setAdapter(mAdapter);
        mAdapter.notifyDataSetChanged();
    }
}
