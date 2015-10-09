package com.runner.sportsmeter.activities;

import android.app.Activity;
import android.os.Bundle;
import com.parse.ParseACL;
import com.parse.ParseObject;
import com.parse.ParseUser;
import com.runner.sportsmeter.R;
import com.runner.sportsmeter.models.Session;

/**
 * Created by angelr on 09-Oct-15.
 */
public class SaveSessionActivity extends Activity {

    private Session currentSession;
    private double sessionDistance;
    private double sessionTimeDiff;
    private double currentMaxSpeed;
    private double averageSpeed;
    private String sportType;
    private String sessionImagePath;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.save_session_layout);

        ParseACL acl = new ParseACL();
        acl.setPublicReadAccess(true);
        acl.setPublicWriteAccess(false);

        updateFromBundle(savedInstanceState);

        currentSession = new Session(
                sessionDistance,
                sessionTimeDiff,
                currentMaxSpeed,
                averageSpeed,
                "",
                ParseUser.getCurrentUser(),
                ParseUser.getCurrentUser().get(getString(R.string.session_name)) != null
                        ? ParseUser.getCurrentUser().get(getString(R.string.session_name)).toString()
                        : null);
        ParseObject saveSession = new ParseObject(getString(R.string.session_object));
        saveSession.put(getString(R.string.session_name), currentSession.getUserName());
        saveSession.put(getString(R.string.session_username), currentSession.getCurrentUser());
        saveSession.put(getString(R.string.session_max_speed), currentSession.getMaxSpeed());
        saveSession.put(getString(R.string.session_average_speed), currentSession.getAverageSpeed());
        saveSession.put(getString(R.string.session_distance), currentSession.getDistance());
        saveSession.put(getString(R.string.session_duration), currentSession.getDuration() / 1000);
        saveSession.put(getString(R.string.session_time_per_kilometer), currentSession.getTimePerKilometer());
        saveSession.put(getString(R.string.session_sport_type), sportType.toString());
        saveSession.setACL(acl);
        saveSession.saveInBackground();
    }

    private void updateFromBundle(Bundle savedInstanceState) {
        if(savedInstanceState.getDouble("session_distance") != 0) {
            sessionDistance = savedInstanceState.getDouble("session_distance");
        }
        if(savedInstanceState.getDouble("session_time_diff") != 0) {
            sessionTimeDiff = savedInstanceState.getDouble("session_time_diff");
        }
        if(savedInstanceState.getDouble("current_max_speed") != 0) {
            currentMaxSpeed = savedInstanceState.getDouble("current_max_speed");
        }
        if(savedInstanceState.getDouble("average_speed") != 0) {
            averageSpeed = savedInstanceState.getDouble("average_speed");
        }
        if(savedInstanceState.getString("sport_type") != null) {
            sportType = savedInstanceState.getString("sport_type");
        }
        if(savedInstanceState.getString("session_image_path") != null) {
            sessionImagePath = savedInstanceState.getString("session_image_path");
        }
    }
}