package com.runner.sportsmeter.activities;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import com.parse.ParseACL;
import com.parse.ParseObject;
import com.parse.ParseUser;
import com.runner.sportsmeter.R;
import com.runner.sportsmeter.models.Session;

/**
 * Created by angelr on 09-Oct-15.
 */
public class  SaveSessionActivity extends Activity {

    private Session currentSession;
    private double sessionDistance;
    private double sessionTimeDiff;
    private double currentMaxSpeed;
    private double averageSpeed;
    private String sportType;
    private String sessionImagePath;
    private TextView saveTimeKm, saveDistance, saveDuration, saveUsername,
            saveMaxSpeed, saveAvgSpeed, saveTypeSport, saveCreatedAt;
    private Button saveBtn, notSaveBtn, postOnFacebookBtn;
    private ImageView sessionScreenshot;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.save_session_layout);
        savedInstanceState = getIntent().getExtras();

        updateFromBundle(savedInstanceState);
        initializeViews();
        createCurrentSession();
        setTextViewsFromSession();

        saveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveParseSession();
            }
        });

        postOnFacebookBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveParseSession();
                postOnFacebookWall();
            }
        });

        notSaveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    private void setTextViewsFromSession() {
        sessionScreenshot.setImageResource(R.drawable.runner_logo);

        saveTimeKm.setText(String.valueOf(currentSession.getTimePerKilometer()));
        saveDistance.setText(String.valueOf(currentSession.getDistance()));
        saveDuration.setText(String.valueOf(currentSession.getDuration() / 1000));
        saveUsername.setText(String.valueOf(currentSession.getUserName()));
        saveMaxSpeed.setText(String.valueOf(currentSession.getMaxSpeed()));
        saveAvgSpeed.setText(String.valueOf(currentSession.getAverageSpeed()));
        saveTypeSport.setText(sportType);
        saveCreatedAt.setText(String.valueOf(currentSession.getCreatedAt()));
    }

    private void createCurrentSession() {
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
    }

    private void initializeViews() {
        sessionScreenshot = (ImageView) findViewById(R.id.session_screenshot);

        saveBtn = (Button) findViewById(R.id.button_save);
        notSaveBtn = (Button) findViewById(R.id.button_not_save);
        postOnFacebookBtn = (Button) findViewById(R.id.button_post_facebook);

        saveTimeKm = (TextView) findViewById(R.id.save_time_kilometer);
        saveDistance = (TextView) findViewById(R.id.save_distance);
        saveDuration = (TextView) findViewById(R.id.save_duration);
        saveUsername = (TextView) findViewById(R.id.save_username);
        saveMaxSpeed = (TextView) findViewById(R.id.save_max_speed);
        saveAvgSpeed = (TextView) findViewById(R.id.save_average_speed);
        saveTypeSport = (TextView) findViewById(R.id.save_type_sport);
        saveCreatedAt = (TextView) findViewById(R.id.save_created_at);
    }

    private void postOnFacebookWall() {

    }

    @Override
    public void onBackPressed() {
        saveParseSession();
        super.onBackPressed();
    }

    private void saveParseSession() {
        ParseACL acl = new ParseACL();
        acl.setPublicReadAccess(true);
        acl.setPublicWriteAccess(false);

        ParseObject saveSession = new ParseObject(getString(R.string.session_object));
        saveSession.put(getString(R.string.session_name), currentSession.getUserName());
        saveSession.put(getString(R.string.session_username), currentSession.getCurrentUser());
        saveSession.put(getString(R.string.session_max_speed), currentSession.getMaxSpeed());
        saveSession.put(getString(R.string.session_average_speed), currentSession.getAverageSpeed());
        saveSession.put(getString(R.string.session_distance), currentSession.getDistance());
        saveSession.put(getString(R.string.session_duration), currentSession.getDuration() / 1000);
        saveSession.put(getString(R.string.session_time_per_kilometer), currentSession.getTimePerKilometer());
        saveSession.put(getString(R.string.session_sport_type), sportType);
        saveSession.setACL(acl);
        saveSession.saveInBackground();
    }

    private void updateFromBundle(Bundle savedInstanceState) {
        if (savedInstanceState != null) {
            if(savedInstanceState.keySet().contains("session_distance")) {
                sessionDistance = savedInstanceState.getDouble("session_distance");
            }
            if(savedInstanceState.keySet().contains("session_time_diff")) {
                sessionTimeDiff = savedInstanceState.getDouble("session_time_diff");
            }
            if(savedInstanceState.keySet().contains("current_max_speed")) {
                currentMaxSpeed = savedInstanceState.getDouble("current_max_speed");
            }
            if(savedInstanceState.keySet().contains("average_speed")) {
                averageSpeed = savedInstanceState.getDouble("average_speed");
            }
            if(savedInstanceState.keySet().contains("sport_type")) {
                sportType = savedInstanceState.getString("sport_type");
            }
            if(savedInstanceState.keySet().contains("session_image_path")) {
                sessionImagePath = savedInstanceState.getString("session_image_path");
            }
        }
    }
}
