package com.runner.sportsmeter;

import android.app.Application;
import com.parse.ParseACL;
import com.parse.ParseInstallation;
import com.runner.sportsmeter.common.ParseCommon;

/**
 * Created by angelr on 30-Jul-15.
 */
public class ParseApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
//
//        ParseObject.registerSubclass(Session.class);
//        ParseCrashReporting.enable(this);
//        Parse.enableLocalDatastore(this);
        ParseCommon.ParseInitialize(this);
//        ParseUser.enableAutomaticUser();
        ParseACL defaultACL = new ParseACL();
        ParseACL.setDefaultACL(defaultACL, true);
        ParseInstallation.getCurrentInstallation().saveInBackground();
    }
}
