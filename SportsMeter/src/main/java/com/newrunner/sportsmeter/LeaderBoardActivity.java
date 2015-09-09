package com.newrunner.sportsmeter;

import android.app.ListActivity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.ProgressBar;
import com.parse.*;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by angelr on 20-Aug-15.
 */
public class LeaderBoardActivity extends ListActivity implements View.OnClickListener {

    ListView showBestScoreList;
    Button bestRunners;
    private ProgressBar bar;
    static Boolean isFinishLoading = false;
    public ArrayList<Session> arrayOfSessions;
    SessionAdapter adapter;

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

        showBestScoreList = (ListView) findViewById(android.R.id.list);

        bar.setVisibility(View.VISIBLE);
        ParseQuery<ParseObject> query = ParseQuery.getQuery("Sessions");
        query.whereEqualTo("username", ParseUser.getCurrentUser());
        query.orderByAscending("timePerKilometer");
        query.setLimit(20);
        query.findInBackground(new FindCallback<ParseObject>() {
            public void done(List<ParseObject> sessions, ParseException e) {
                if (e == null) {
                    Log.d("session", "Retrieved " + sessions.size() + " sessions");
                    arrayOfSessions = new ArrayList<>();
                    arrayOfSessions = Utility.convertFromParseObject(sessions);
                    refreshListView();
                } else {
                    Log.d("session", "Error: " + e.getMessage());
                }
                bar.setVisibility(View.GONE);
            }
        });

//        Bundle bundle = getIntent().getExtras();
//        arrayOfSessions = bundle.getParcelableArrayList("list");
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

    @Override
    protected void onListItemClick(ListView l, View v, int position, long id) {
        super.onListItemClick(l, v, position, id);
//        DialogViewer newDialog = new DialogViewer();
//        newDialog.show(getFragmentManager(), "DialogViewer");
//        deleteItem(position);
//		isClickYes = getIntent().getStringExtra("isClickYes");
//		if (isClickYes == "true") {
//			// delete
//		}
    }

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

    private class ProgressTask extends AsyncTask<Void,Void,Void> {
        @Override
        protected void onPreExecute(){
            bar.setVisibility(View.VISIBLE);
        }

        @Override
        protected Void doInBackground(Void... arg0) {
            ParseQuery<ParseObject> query = ParseQuery.getQuery("Sessions");
            query.orderByAscending("timePerKilometer");
            query.setLimit(10);
            query.findInBackground(new FindCallback<ParseObject>() {
                public void done(List<ParseObject> sessions, ParseException e) {
                    if (e == null) {
                        Log.d("session", "Retrieved " + sessions.size() + " sessions");
                        arrayOfSessions = new ArrayList<>();
                        arrayOfSessions.clear();
                        arrayOfSessions = Utility.convertFromParseObject(sessions);
                        refreshListView();
                    } else {
                        Log.d("session", "Error: " + e.getMessage());
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
        adapter = new SessionAdapter(LeaderBoardActivity.this, R.layout.leaderboard_row, arrayOfSessions);
        showBestScoreList.setAdapter(adapter);
    }

//    public void objectsWereRetrievedSuccessfully(List<ParseObject> sessions) {
//        arrayOfSessions = new ArrayList<>();
//        arrayOfSessions = Utility.convertFromParseObject(sessions);
//    }
}
