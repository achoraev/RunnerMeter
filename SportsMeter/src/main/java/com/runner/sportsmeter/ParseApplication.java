package com.runner.sportsmeter;

import android.content.Context;
import android.support.multidex.MultiDex;
import android.support.multidex.MultiDexApplication;
import com.google.android.gms.analytics.GoogleAnalytics;
import com.google.android.gms.analytics.Tracker;
import com.parse.Parse;
import com.parse.ParseACL;
import com.parse.ParseInstallation;
import com.parse.ParseObject;
import com.runner.sportsmeter.common.ParseCommon;
import com.runner.sportsmeter.models.Account;
import com.runner.sportsmeter.models.Coordinates;
import com.runner.sportsmeter.models.Segments;
import com.runner.sportsmeter.models.Sessions;

/**
 * Created by angelr on 30-Jul-15.
 */
public class ParseApplication extends MultiDexApplication {
    private Tracker mTracker;

    /**
     * Gets the default {@link Tracker} for this {@linkApplication}.
     * @return tracker
     */
    synchronized public Tracker getDefaultTracker() {
        if (mTracker == null) {
            GoogleAnalytics analytics = GoogleAnalytics.getInstance(this);
            // To enable debug logging use: adb shell setprop log.tag.GAv4 DEBUG
            mTracker = analytics.newTracker(R.xml.global_tracker);
        }
        return mTracker;
    }

    @Override
    public void onCreate() {
        super.onCreate();
//        ParseCrashReporting.enable(this);
        Parse.enableLocalDatastore(this);
        ParseObject.registerSubclass(Account.class);
        ParseObject.registerSubclass(Sessions.class);
        ParseObject.registerSubclass(Coordinates.class);
        ParseObject.registerSubclass(Segments.class);
        ParseCommon.ParseInitialize(this);
//        ParseUser.enableAutomaticUser();
        ParseACL defaultACL = new ParseACL();
        defaultACL.setPublicReadAccess(true);
        defaultACL.setPublicWriteAccess(false);
        ParseACL.setDefaultACL(defaultACL, true);
        ParseInstallation.getCurrentInstallation().saveEventually();
    }

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        MultiDex.install(this);
    }
}
