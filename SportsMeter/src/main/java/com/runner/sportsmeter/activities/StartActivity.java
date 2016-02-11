package com.runner.sportsmeter.activities;

import android.annotation.TargetApi;
import android.app.AlertDialog;
import android.content.*;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.Toast;
import com.parse.ParsePush;
import com.runner.sportsmeter.MainActivity;
import com.runner.sportsmeter.R;
import com.runner.sportsmeter.common.ParseCommon;
import com.runner.sportsmeter.common.Utility;
import com.runner.sportsmeter.enums.SportTypes;

/**
 * Created by Angel Raev on 09-Feb-16
 */
public class StartActivity extends AppCompatActivity {
    private static final int MY_PERMISSIONS_REQUEST_ACCESS_LOCATION = 123456;
    private final String FIRST_RUN = "firstRun";
    private final String FIVE_RUN = "fiveRun";
//    private AdView mAdView;
    private SportTypes sportType = SportTypes.CHOOSE_SPORT;
    private int runCount = 1;
    private int maxCountForAskRateMe = 5;
    private SharedPreferences fiveRunSettings;


    @TargetApi(Build.VERSION_CODES.M)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.l_new_start_layout);

        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.LOLLIPOP_MR1) {
            if (checkSelfPermission(android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                    && checkSelfPermission(android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(StartActivity.this,
                        new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION, android.Manifest.permission.ACCESS_COARSE_LOCATION},
                        MY_PERMISSIONS_REQUEST_ACCESS_LOCATION);
                return;
            }
        }

        SharedPreferences settings = getSharedPreferences(FIRST_RUN, MODE_PRIVATE);
        if (settings.getBoolean(FIRST_RUN, true)) {
            startActivity(new Intent(StartActivity.this, HelpActivity.class));
            settings.edit().putBoolean(FIRST_RUN, false).apply();
        }

        fiveRunSettings = getSharedPreferences(FIVE_RUN, MODE_PRIVATE);
        runCount = fiveRunSettings.getInt(FIVE_RUN, 1);
        if (fiveRunSettings.getInt(FIVE_RUN, runCount) == maxCountForAskRateMe) {
            askUserToRateApp();
            runCount++;
            fiveRunSettings.edit().putInt(FIVE_RUN, runCount).apply();
        } else {
            runCount++;
            fiveRunSettings.edit().putInt(FIVE_RUN, runCount).apply();
        }

        // set snackbar
        Snackbar.make(findViewById(R.id.start_coordinator), R.string.enter_app, Snackbar.LENGTH_INDEFINITE)
                .setAction(">>>>", new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        startMainActivity(sportType);
                    }
                })
                .show();

        // set spinner and data
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(getApplicationContext(),
                R.array.array_type_of_sports, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(R.layout.spinner_dropdown_item);

        Spinner chooseTypeSport = (Spinner) findViewById(R.id.choose_type_of_sport);

        chooseTypeSport.setAdapter(adapter);
        chooseTypeSport.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                sportType = sportType.getSportTypeValue(position);
                if(!sportType.equals(SportTypes.CHOOSE_SPORT)) {
                    startMainActivity(sportType);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                sportType = sportType.CHOOSE_SPORT;
//                startMainActivity(sportType);
            }
        });

        turnOnWiFiOrDataInternet();

        ParseCommon.createDefaultUser();
        ParseCommon.logInGuestUser(this);
        ParsePush.subscribeInBackground("SportMeter");

//        // setup adds
//        mAdView = (AdView) findViewById(R.id.adViewStart);
//        new Utility().setupAdds(mAdView, this);
    }

    private void askUserToRateApp() {
        new AlertDialog.Builder(StartActivity.this)
                .setTitle(getString(R.string.menu_rate))
                .setMessage(R.string.do_you_like_sport_meter)
                .setPositiveButton(R.string.like, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        new AlertDialog.Builder(StartActivity.this)
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
                                            Toast.makeText(StartActivity.this, getString(R.string.unable_find_market_app) + e.getMessage(), Toast.LENGTH_LONG).show();
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
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_ACCESS_LOCATION: {
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(StartActivity.this, R.string.permission_granted, Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(StartActivity.this, R.string.gps_not_available, Toast.LENGTH_LONG).show();
                }
                return;
            }
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    private void startMainActivity(SportTypes sportType) {
        Intent startIntent = new Intent(StartActivity.this, MainActivity.class);
        Bundle bundle = new Bundle();
        bundle.putSerializable(getString(R.string.type_of_sport), sportType);
        startIntent.putExtras(bundle);
        overridePendingTransition(android.R.anim.fade_in,
                android.R.anim.fade_out);
        startActivity(startIntent);
        finish();
    }
}
