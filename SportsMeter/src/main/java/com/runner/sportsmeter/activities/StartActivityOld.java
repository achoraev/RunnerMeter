package com.runner.sportsmeter.activities;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.*;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.util.Log;
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
public class StartActivityOld extends Activity implements SimpleGestureFilter.SimpleGestureListener {

    private static final int MY_PERMISSIONS_REQUEST_ACCESS_LOCATION = 123456;
    private final String FIRST_RUN = "firstRun";
    private final String FIVE_RUN = "fiveRun";
    private SimpleGestureFilter detector;
    private AdView mAdView;
    private SportTypes sportType = SportTypes.RUNNING;
    private Button runnerBtn, bikerBtn, driveBtn;
    private int runCount = 1;
    private int maxCountForAskRateMe = 5;
    private SharedPreferences fiveRunSettings;


    @TargetApi(Build.VERSION_CODES.M)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.l_start_layout);

        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.LOLLIPOP_MR1) {
            if (checkSelfPermission(android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                    && checkSelfPermission(android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(StartActivityOld.this,
                        new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION, android.Manifest.permission.ACCESS_COARSE_LOCATION},
                        MY_PERMISSIONS_REQUEST_ACCESS_LOCATION);
                return;
            }
        }

        SharedPreferences settings = getSharedPreferences(FIRST_RUN, MODE_PRIVATE);
        if(settings.getBoolean(FIRST_RUN, true)){
            startActivity(new Intent(StartActivityOld.this, HelpActivity.class));
            settings.edit().putBoolean(FIRST_RUN, false).apply();
        }

        fiveRunSettings = getSharedPreferences(FIVE_RUN, MODE_PRIVATE);
        runCount = fiveRunSettings.getInt(FIVE_RUN, 1);
        if(fiveRunSettings.getInt(FIVE_RUN, runCount) == maxCountForAskRateMe){
            askUserToRateApp();
            runCount++;
            fiveRunSettings.edit().putInt(FIVE_RUN, runCount).apply();
        } else {
            runCount++;
            fiveRunSettings.edit().putInt(FIVE_RUN, runCount).apply();
        }

        bikerBtn = (Button) findViewById(R.id.top_right);
        runnerBtn = (Button) findViewById(R.id.center_right);
        driveBtn = (Button) findViewById(R.id.bottom_right);

        detector = new SimpleGestureFilter(this, this);

        turnOnWiFiOrDataInternet();

        ParseCommon.createAnonymousUser();
        ParseCommon.logInGuestUser(this);
        ParsePush.subscribeInBackground("SportMeter");

        runnerBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startMainActivity(SportTypes.RUNNING);
            }
        });

        bikerBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startMainActivity(SportTypes.BIKING);
            }
        });

        driveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startMainActivity(SportTypes.DRIVING);
            }
        });

        // setup adds
        mAdView = (AdView) findViewById(R.id.adViewStart);
        new Utility().setupAdds(mAdView, this);
    }

    private void askUserToRateApp() {
        new AlertDialog.Builder(StartActivityOld.this)
                .setTitle(getString(R.string.menu_rate))
                .setMessage(R.string.do_you_like_sport_meter)
                .setPositiveButton(R.string.like, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        new AlertDialog.Builder(StartActivityOld.this)
                                .setTitle(getString(R.string.menu_rate))
                                .setMessage(R.string.do_you_want_rate_now)
                                .setPositiveButton(getString(R.string.yes), new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        Uri uri = Uri.parse("market://details?id=" + getPackageName());
//                                        Uri uri = Uri.parse("https://play.google.com/store/ereview?docId=" + getPackageName());
                                        try {
                                            overridePendingTransition(android.R.anim.fade_in,
                                                    android.R.anim.fade_out);
                                            startActivity(new Intent(Intent.ACTION_VIEW, uri));
                                            runCount = maxCountForAskRateMe + 1;
                                            fiveRunSettings.edit().putInt(FIVE_RUN, runCount).apply();
                                        } catch (ActivityNotFoundException e) {
                                            Toast.makeText(StartActivityOld.this, getString(R.string.unable_find_market_app) + e.getMessage(), Toast.LENGTH_LONG).show();
                                            Log.d("App", e.getMessage());
                                        }
                                    }
                                })
                                .setNeutralButton(R.string.remind_later, new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        runCount = 1;
                                        fiveRunSettings.edit().putInt(FIVE_RUN, runCount).apply();
                                    }
                                })
                                .setNegativeButton(getString(R.string.no), new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        dialog.dismiss();
                                    }
                                })
                                .show();
                    }
                })
                .setNegativeButton(R.string.no, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                })
                .show();
    }

    @Override
    public void onBackPressed() {
        if (new ParseCommon().getCurrentUserUsername().equals("Guest")) {
            new ParseCommon().logOutUser(this);
        }
        super.onBackPressed();
    }

    private void turnOnWiFiOrDataInternet() {
        if (!Utility.isNetworkConnected(StartActivityOld.this)) {
            new AlertDialog.Builder(StartActivityOld.this)
                    .setTitle(getString(R.string.no_internet))
                    .setMessage(getString(R.string.no_net_message))
                    .setPositiveButton(getString(R.string.turn_on_wifi), new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            WifiManager wifi = (WifiManager) StartActivityOld.this.getSystemService(Context.WIFI_SERVICE);
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
                            overridePendingTransition(android.R.anim.fade_in,
                                    android.R.anim.fade_out);
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
        this.detector.onTouchEvent(ev);
        return super.dispatchTouchEvent(ev);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_ACCESS_LOCATION: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(StartActivityOld.this, "Permissions granted", Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(StartActivityOld.this, "Need GPS to use this app", Toast.LENGTH_LONG).show();
                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                }
                return;
            }
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    private void startMainActivity(SportTypes sportType) {
        Intent startIntent = new Intent(StartActivityOld.this, MainActivity.class);
        Bundle bundle = new Bundle();
        bundle.putSerializable(getString(R.string.type_of_sport), sportType);
        startIntent.putExtras(bundle);
        overridePendingTransition(android.R.anim.fade_in,
                android.R.anim.fade_out);
        startActivity(startIntent);
        finish();
    }

    @Override
    public void onSwipe(int direction) {
        String str = "";

        switch (direction) {

            case SimpleGestureFilter.SWIPE_RIGHT:
                str = "Right";
                startMainActivity(sportType);
                break;
            case SimpleGestureFilter.SWIPE_LEFT:
                str = "Left";
                startMainActivity(sportType);
                break;
            case SimpleGestureFilter.SWIPE_DOWN:
                str = "Down";
                break;
            case SimpleGestureFilter.SWIPE_UP:
                str = "Up";
                break;

        }
//        Toast.makeText(this, str, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onDoubleTap() {
//        Toast.makeText(this, "Double Tap", Toast.LENGTH_SHORT).show();
        startMainActivity(sportType);
    }
}