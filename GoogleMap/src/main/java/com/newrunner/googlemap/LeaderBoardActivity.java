package com.newrunner.googlemap;

import android.app.ListActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ListView;

import java.util.ArrayList;

/**
 * Created by angelr on 20-Aug-15.
 */
public class LeaderBoardActivity extends ListActivity implements View.OnClickListener {

    ListView showInput;
    Session newSession;
    ArrayList<Session> arrayOfSessions;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.leaderboard_fragment);

        showInput = (ListView) findViewById(android.R.id.list);

        Bundle bundle = getIntent().getExtras();
        arrayOfSessions = new ArrayList<>();
        arrayOfSessions = bundle.getParcelableArrayList("list");

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
        SessionAdapter adapter = new SessionAdapter(this, R.layout.leaderboard_row, arrayOfSessions);
        showInput.setAdapter(adapter);
    }
}