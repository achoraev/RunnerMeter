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

//    ListView showBestScoreList;
    Button bestRunners;
    private ProgressBar bar;
    public ArrayList<Session> arrayOfSessions;
//    SessionAdapter adapter;
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
                new ProgressTask().execute();
            }
        });

//        showBestScoreList = (ListView) findViewById(android.R.id.list);

        bar.setVisibility(View.VISIBLE);
        Thread newThread = new Thread(new Runnable() {
            @Override
            public void run() {
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
                        bar.setVisibility(View.GONE);
                    }
                });
            }
        });
        newThread.start();

        // for recycle
        mRecyclerView = (RecyclerView) findViewById(R.id.my_recycler_view);

        // use this setting to improve performance if you know that changes
        // in content do not change the layout size of the RecyclerView
        mRecyclerView.setHasFixedSize(true);

        // use a linear layout manager
        mLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(mLayoutManager);

//        Bundle bundle = getIntent().getExtras();
//        arrayOfSessions = bundle.getParcelableArrayList("list");

        // setup adds
        mAdView = (AdView) findViewById(R.id.adViewLeaderBoard);
//        new Utility().setupAdds(mAdView, this);
    }

    private class ProgressTask extends AsyncTask<Void,Void,Void> {
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

    private void refreshListView() {
//        adapter = new SessionAdapter(LeaderBoardActivity.this, R.layout.leaderboard_row, arrayOfSessions);
//        showBestScoreList.setAdapter(adapter);
//        adapter.notifyDataSetChanged();
        // specify an adapter (see also next example)
        mAdapter = new RecyclerAdapter(arrayOfSessions);
        mRecyclerView.setAdapter(mAdapter);
    }

    public void onClick(View v) {
//        if (v.getId() == R.id.btn_delete) {
//            deleteItem(0);
//        }
//        else if (v.getId() == R.id.btn_createNewNote) {
//            final Intent createNewNoteIntent = new Intent(this, MainActivity.class);
//            overridePendingTransition(android.R.anim.fade_in,
//                    android.R.anim.fade_out);
//            startActivity(createNewNoteIntent);
//        }
    }

//    @Override
//    protected void onListItemClick(ListView l, View v, int position, long id) {
//        super.onListItemClick(l, v, position, id);
////        DialogViewer newDialog = new DialogViewer();
////        newDialog.show(getFragmentManager(), "DialogViewer");
////        deleteItem(position);
////		isClickYes = getIntent().getStringExtra("isClickYes");
////		if (isClickYes == "true") {
////			// delete
////		}
//    }

    public void deleteItem(int position) {
//        ArrayList<Session> allSessions = datasource.getAllNotes();
//        Session toDeleteNote = allSessions.get(position);
//        if (allSessions.toArray().length != 0) {
//            datasource.deleteNote(toDeleteNote);
//            Toast.makeText(
//                    this,
//                    String.valueOf(toDeleteNote.getUsername()
//                            + "'s post deleted"), Toast.LENGTH_SHORT).show();
//        refreshListView();
//        }
    }

//    public void objectsWereRetrievedSuccessfully(List<ParseObject> sessions) {
//        arrayOfSessions = new ArrayList<>();
//        arrayOfSessions = Utility.convertFromParseObject(sessions);
//    }
}
