package com.newrunner.googlemap;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

/**
 * Created by angelr on 03-Jul-15.
 */
public class StartActivity extends Activity implements SimpleGestureFilter.SimpleGestureListener {

    private SimpleGestureFilter detector;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.start_layout);

        Button startBtn = (Button) findViewById(R.id.center_right);

        detector = new SimpleGestureFilter(this,this);

        ParseCommon.logInGuestUser();

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