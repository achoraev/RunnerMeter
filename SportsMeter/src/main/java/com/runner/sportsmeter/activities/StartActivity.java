package com.runner.sportsmeter.activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;
import com.google.android.gms.ads.AdView;
import com.runner.sportsmeter.MainActivity;
import com.runner.sportsmeter.R;
import com.runner.sportsmeter.common.SimpleGestureFilter;
import com.runner.sportsmeter.common.ParseCommon;
import com.runner.sportsmeter.common.Utility;
import com.runner.sportsmeter.enums.SportTypes;
import com.parse.ParseAnalytics;

/**
 * Created by angelr on 03-Jul-15.
 */
public class StartActivity extends Activity implements SimpleGestureFilter.SimpleGestureListener {

    private SimpleGestureFilter detector;
    AdView mAdView;
    SportTypes sportType;
    Button runnerBtn, bikerBtn, driveBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.start_layout);

        bikerBtn = (Button) findViewById(R.id.top_right);
        runnerBtn = (Button) findViewById(R.id.center_right);
        driveBtn = (Button) findViewById(R.id.bottom_right);

        detector = new SimpleGestureFilter(this,this);

        ParseCommon.createAnonymousUser();
        ParseCommon.logInGuestUser();
        ParseAnalytics.trackAppOpenedInBackground(getIntent());
//        ParsePush.subscribeInBackground();

        runnerBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sportType = SportTypes.runner;
                startMainActivity();
            }
        });

        bikerBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sportType = SportTypes.biker;
                startMainActivity();
            }
        });

        driveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sportType = SportTypes.driver;
                startMainActivity();
            }
        });

        // setup adds
        mAdView = (AdView) findViewById(R.id.adViewStart);
        new Utility().setupAdds(mAdView, this);
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
        bundle.putSerializable("sportType", sportType);
        startIntent.putExtras(bundle);
        startActivity(startIntent);
        finish();
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
}