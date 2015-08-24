package com.newrunner.googlemap;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseUser;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by angelr on 03-Jul-15.
 */
public class StartActivity extends Activity implements SimpleGestureFilter.SimpleGestureListener {

    private SimpleGestureFilter detector;
    static ArrayList<Session> arrayOfSessions;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.start_layout);
        arrayOfSessions = new ArrayList<>();

        Button startBtn = (Button) findViewById(R.id.center_right);

        ParseCommon.loadFromParse();

        detector = new SimpleGestureFilter(this,this);

        startBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startMainActivity();
            }
        });
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        // Call onTouchEvent of SimpleGestureFilter class
        this.detector.onTouchEvent(ev);
        return super.dispatchTouchEvent(ev);
    }

    private void startMainActivity() {
        Intent startIntent = new Intent(StartActivity.this, MainActivity.class);
        Bundle bundle = new Bundle();
        bundle.putParcelableArrayList("list", arrayOfSessions);
        startIntent.putExtras(bundle);
        startActivity(startIntent);
    }

    @Override
    public void onSwipe(int direction) {
        String str = "";

        switch (direction) {

            case SimpleGestureFilter.SWIPE_RIGHT :
                str = "Right";
                startMainActivity();
                break;
            case SimpleGestureFilter.SWIPE_LEFT :
                str = "Left";
                startMainActivity();
                break;
            case SimpleGestureFilter.SWIPE_DOWN :
                str = "Down";
                break;
            case SimpleGestureFilter.SWIPE_UP :
                str = "Up";
                break;

        }
        Toast.makeText(this, str, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onDoubleTap() {
        Toast.makeText(this, "Double Tap", Toast.LENGTH_SHORT).show();
        startMainActivity();
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

    public static void objectRetrievalFailed(ParseException e) {
        Log.d("Query", e.getMessage());
    }
}