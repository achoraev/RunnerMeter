package com.newrunner.googlemap;

import android.app.Application;
import com.parse.Parse;

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
        Parse.enableLocalDatastore(this);
        ParseCommon.ParseInitialize(this);
//        ParseUser.enableAutomaticUser();
//        ParseACL defaultACL = new ParseACL();
//        ParseACL.setDefaultACL(defaultACL, true);
    }
}
