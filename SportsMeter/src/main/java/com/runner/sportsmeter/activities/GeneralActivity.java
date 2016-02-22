package com.runner.sportsmeter.activities;

import android.support.v7.app.AppCompatActivity;
import android.widget.Toast;

/**
 * Created by Angel Raev on 22-Feb-16
 */
public class GeneralActivity extends AppCompatActivity {

    public void showToast(CharSequence text) {
        Toast.makeText(this, text, Toast.LENGTH_SHORT).show();
    }
}
