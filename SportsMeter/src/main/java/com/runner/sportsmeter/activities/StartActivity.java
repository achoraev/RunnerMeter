package com.runner.sportsmeter.activities;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.provider.Settings;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;
import com.google.android.gms.ads.AdView;
import com.parse.ParsePush;
import com.runner.sportsmeter.MainActivity;
import com.runner.sportsmeter.R;
import com.runner.sportsmeter.common.ParseCommon;
import com.runner.sportsmeter.common.SimpleGestureFilter;
import com.runner.sportsmeter.common.Utility;
import com.runner.sportsmeter.enums.SportTypes;

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

        detector = new SimpleGestureFilter(this, this);

        turnOnWiFiOrDataInternet();

        ParseCommon.createAnonymousUser();
        ParseCommon.logInGuestUser();
        ParsePush.subscribeInBackground("SportMeter");

        runnerBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sportType = SportTypes.runner;
                startMainActivity(sportType);
            }
        });

        bikerBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sportType = SportTypes.biker;
                startMainActivity(sportType);
            }
        });

        driveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sportType = SportTypes.driver;
                startMainActivity(sportType);
            }
        });

        // setup adds
        mAdView = (AdView) findViewById(R.id.adViewStart);
        new Utility().setupAdds(mAdView, this);
    }

    private void turnOnWiFiOrDataInternet() {
        if (!Utility.isNetworkConnected(StartActivity.this)) {
            new AlertDialog.Builder(StartActivity.this)
                    .setTitle(getString(R.string.no_internet))
                    .setMessage(getString(R.string.no_net_message))
                    .setPositiveButton(getString(R.string.turn_on_wifi), new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            WifiManager wifi = (WifiManager) StartActivity.this.getSystemService(Context.WIFI_SERVICE);
                            wifi.setWifiEnabled(true);
                        }
                    })
                    .setNeutralButton(getString(R.string.turn_on_data), new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            // prompt user
                            // 1
//                            Intent dialogIntent = new Intent(android.provider.Settings.ACTION_SETTINGS);
//                            dialogIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//                            startActivity(dialogIntent);
                            // 5
                            Intent intent = new Intent();
                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            intent.setAction(Settings.ACTION_DATA_ROAMING_SETTINGS);
                            startActivity(intent);
                        }
                    })
                    .setNegativeButton(getString(R.string.close), new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    }).show();
        }
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        // Call onTouchEvent of SimpleGestureFilter class
//        this.detector.onTouchEvent(ev);
        return super.dispatchTouchEvent(ev);
    }

    private void startMainActivity(SportTypes sportType) {
        Intent startIntent = new Intent(StartActivity.this, MainActivity.class);
        Bundle bundle = new Bundle();
        bundle.putSerializable(getString(R.string.type_of_sport), sportType);
        startIntent.putExtras(bundle);
        startActivity(startIntent);
        finish();
    }

    @Override
    public void onSwipe(int direction) {
//        String str = "";
//
//        switch (direction) {
//
//            case SimpleGestureFilter.SWIPE_RIGHT:
//                str = "Right";
//                startMainActivity(sportType);
//                break;
//            case SimpleGestureFilter.SWIPE_LEFT:
//                str = "Left";
//                startMainActivity(sportType);
//                break;
//            case SimpleGestureFilter.SWIPE_DOWN:
//                str = "Down";
//                break;
//            case SimpleGestureFilter.SWIPE_UP:
//                str = "Up";
//                break;
//
//        }
//        Toast.makeText(this, str, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onDoubleTap() {
        Toast.makeText(this, "Double Tap", Toast.LENGTH_SHORT).show();
//        startMainActivity(sportType);
    }
}