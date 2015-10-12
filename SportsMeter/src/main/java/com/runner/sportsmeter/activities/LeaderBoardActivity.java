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
import com.runner.sportsmeter.models.Session;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by angelr on 20-Aug-15.
 */
public class LeaderBoardActivity extends Activity implements View.OnClickListener {

    Button bestRunners;
    private ProgressBar bar;
    public ArrayList<Session> arrayOfSessions;
    AdView mAdView;

    private RecyclerView mRecyclerView;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.leaderboard_layout);

        bar = (ProgressBar) this.findViewById(R.id.progressBar);
        bestRunners = (Button) findViewById(R.id.btn_best_runners);

        bestRunners.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new QueryBestRunnersTask().execute();
            }
        });

        new QueryMyBestResultsTask().execute();

        // for recycle
        mRecyclerView = (RecyclerView) findViewById(R.id.my_recycler_view);

        // use this setting to improve performance if you know that changes
        // in content do not change the layout size of the RecyclerView
        mRecyclerView.setHasFixedSize(true);

        // use a linear layout manager
        mLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(mLayoutManager);

        // setup adds
        mAdView = (AdView) findViewById(R.id.adViewLeaderBoard);
//        new Utility().setupAdds(mAdView, this);
    }

    @Override
    public void onClick(View v) {

    }

    private class QueryBestRunnersTask extends AsyncTask<Void,Void,Void> {
        @Override
        protected void onPreExecute(){
            bar.setVisibility(View.VISIBLE);
        }

        @Override
        protected Void doInBackground(Void... arg0) {
            ParseQuery<ParseObject> query = ParseQuery.getQuery(getString(R.string.session_object));
            // todo make query by type of sport
            query.orderByAscending(getString(R.string.session_time_per_kilometer));
            query.setLimit(15);
            query.findInBackground(new FindCallback<ParseObject>() {
                public void done(List<ParseObject> sessions, ParseException e) {
                    if (e == null) {
                        Log.d("session", "Retrieved " + sessions.size() + " sessions");
                        arrayOfSessions = new ArrayList<>();
                        arrayOfSessions = Utility.convertFromParseObject(sessions);
                        refreshListView();
                    } else {
                        Log.e("session", "Error: " + e.getMessage());
                    }
                }
            });
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            refreshListView();
            bar.setVisibility(View.GONE);
        }
    }

    private class QueryMyBestResultsTask extends AsyncTask<Void,Void,Void> {
        @Override
        protected void onPreExecute(){
            bar.setVisibility(View.VISIBLE);
        }

        @Override
        protected Void doInBackground(Void... arg0) {
            ParseQuery<ParseObject> query = ParseQuery.getQuery(getString(R.string.session_object));
            query.whereEqualTo(getString(R.string.session_username), ParseUser.getCurrentUser());
            query.orderByAscending(getString(R.string.session_time_per_kilometer));
            query.setLimit(20);
            query.findInBackground(new FindCallback<ParseObject>() {
                public void done(List<ParseObject> sessions, ParseException e) {
                    if (e == null) {
                        Log.d("session", "Retrieved " + sessions.size() + " sessions");
                        arrayOfSessions = new ArrayList<>();
                        arrayOfSessions = Utility.convertFromParseObject(sessions);
                        refreshListView();
                    } else {
                        Log.e("session", "Error: " + e.getMessage());
                    }
                }
            });
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
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
