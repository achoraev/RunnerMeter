package com.newrunner.googlemap;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Toast;

/**
 * Created by angelr on 03-Jul-15.
 */
public class StartActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.start_layout);

        Button startBtn = (Button) findViewById(R.id.center_right);
        LinearLayout layout = (LinearLayout) findViewById(R.id.start_page_layout);

        layout.setOnTouchListener(new OnSwipeTouchListener(this) {

            public void onSwipeLeft() {
                Toast.makeText(getBaseContext(), "left", Toast.LENGTH_SHORT).show();
                startMainActivity();
            }

            public void onSwipeRight() {
                Toast.makeText(getBaseContext(), "right", Toast.LENGTH_SHORT).show();
                startMainActivity();
            }

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                return super.onTouch(v, event);
            }
        });

        startBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startMainActivity();
            }
        });
    }

    private void startMainActivity() {
        Intent startIntent = new Intent(StartActivity.this, MainActivity.class);
        startActivity(startIntent);
    }
}