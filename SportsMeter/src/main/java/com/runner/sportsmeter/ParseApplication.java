package com.runner.sportsmeter;

import android.content.Context;
import android.support.multidex.MultiDex;
import android.support.multidex.MultiDexApplication;
import com.parse.Parse;
import com.parse.ParseACL;
import com.parse.ParseInstallation;
import com.parse.ParseObject;
import com.runner.sportsmeter.common.ParseCommon;
import com.runner.sportsmeter.models.Coordinates;
import com.runner.sportsmeter.models.Segments;

/**
 * Created by angelr on 30-Jul-15.
 */
public class ParseApplication extends MultiDexApplication {
    @Override
    public void onCreate() {
        super.onCreate();
//
//        ParseObject.registerSubclass(Session.class);
//        ParseCrashReporting.enable(this);
        Parse.enableLocalDatastore(this);
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
