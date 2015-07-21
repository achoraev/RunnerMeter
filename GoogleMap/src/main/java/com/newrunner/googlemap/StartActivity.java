package com.newrunner.googlemap;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.Toast;

/**
 * Created by angelr on 03-Jul-15.
 */
public class StartActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.start_layout);

        Button startBtn = (Button) findViewById(R.id.why_btn);
        RelativeLayout layout = (RelativeLayout) findViewById(R.id.start_page_layout);

        layout.setOnTouchListener(new OnSwipeTouchListener(this) {

            public void onSwipeRight() {
                Toast.makeText(getBaseContext(), "right", Toast.LENGTH_LONG).show();
            }
            public void onSwipeLeft() {
                Toast.makeText(getBaseContext(), "right", Toast.LENGTH_LONG).show();
            }
        });

        startBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(v.getContext(), MainActivity.class);
                startActivity(intent);
            }
        });
    }
}