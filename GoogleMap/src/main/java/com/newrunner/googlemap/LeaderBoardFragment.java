package com.newrunner.googlemap;

import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;

/**
 * Created by angelr on 22-Jul-15.
 */
public class LeaderBoardFragment extends ListFragment {

    public LeaderBoardFragment() {
        // Empty constructor required for fragment subclasses
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setRetainInstance(true);

//        ListView showInput = (ListView) findViewById(android.R.id.list);
//
//        posts = new ArrayList<Session>();
//
//        Note note = new Note();
//        note.setUsername(username);
//        note.setNote(noteFromInput);
//        posts.add(note);
//
//        refreshListView();
    }

//    @Nullable
//    @Override
//    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
//        return inflater.inflate(R.layout.leaderboard_fragment, container, false);
//    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        String[] values = new String[] { "Android", "iPhone", "WindowsMobile",
                "Blackberry", "WebOS", "Ubuntu", "Windows7", "Max OS X",
                "Linux", "OS/2" };
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(getActivity(),
                android.R.layout.simple_list_item_1, values);
        setListAdapter(adapter);
    }

    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {
        super.onListItemClick(l, v, position, id);
    }
}