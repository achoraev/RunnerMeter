package com.newrunner.sportsmeter.activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;
import com.google.android.gms.ads.AdView;
import com.newrunner.sportsmeter.MainActivity;
import com.newrunner.sportsmeter.R;
import com.newrunner.sportsmeter.common.SimpleGestureFilter;
import com.newrunner.sportsmeter.common.ParseCommon;
import com.newrunner.sportsmeter.common.Utility;
import com.parse.ParseAnalytics;

/**
 * Created by angelr on 03-Jul-15.
 */
public class StartActivity extends Activity implements SimpleGestureFilter.SimpleGestureListener {

    private SimpleGestureFilter detector;
    AdView mAdView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.start_layout);

        Button startBtn = (Button) findViewById(R.id.center_right);

        detector = new SimpleGestureFilter(this,this);

        ParseCommon.createAnonymousUser();
        ParseCommon.logInGuestUser();
        ParseAnalytics.trackAppOpenedInBackground(getIntent());
//        ParsePush.subscribeInBackground();

        startBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
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