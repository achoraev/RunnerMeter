package com.newrunner.googlemap;

import android.app.ListActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ListView;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseUser;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by angelr on 20-Aug-15.
 */
public class LeatherBoardActivity extends ListActivity implements View.OnClickListener {

    ListView showInput;
    static ArrayList<Session> arrayOfSessions;
    Session newSession;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.leatherboard_fragment);

        showInput = (ListView) findViewById(android.R.id.list);

        arrayOfSessions = new ArrayList<>();
        ParseCommon.loadFromParse();

//        arrayOfSessions = objects;
//        for (int i = 0; i < 2; i++) {
//            newSession = new Session(10.5, 20.5, 30.5, 15.5, ParseUser.getCurrentUser());
//            arrayOfSessions.add(newSession);
//        }

        refreshListView();
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
        // todo make this work
//		isClickYes = getIntent().getStringExtra("isClickYes");
//		if (isClickYes == "true") {
//			// delete
//		}
    }

    public void deleteItem(int position) {
        // todo parse query
//        ArrayList<Session> allSessions = datasource.getAllNotes();
//        Session toDeleteNote = allSessions.get(position);
//        if (allSessions.toArray().length != 0) {
//            datasource.deleteNote(toDeleteNote);
//            Toast.makeText(
//                    this,
//                    String.valueOf(toDeleteNote.getUsername()
//                            + "'s post deleted"), Toast.LENGTH_SHORT).show();
        refreshListView();
//        }
    }

    private void refreshListView() {
        SessionAdapter adapter = new SessionAdapter(this, R.layout.leatherboard_row, arrayOfSessions);
        showInput.setAdapter(adapter);
    }

    public static void objectRetrievalFailed(ParseException e) {
        Log.d("Query", e.getMessage());
    }

    public static void objectsWereRetrievedSuccessfully(List<ParseObject> sessions) {
        for(ParseObject ses : sessions){
            Session newSession = new Session(
                    ses.getDouble("distance"),
                    ses.getDouble("duration"),
                    ses.getDouble("maxSpeed"),
                    ses.getDouble("averageSpeed"),
                    ParseUser.getCurrentUser());
            arrayOfSessions.add(newSession);

        }
    }
}
