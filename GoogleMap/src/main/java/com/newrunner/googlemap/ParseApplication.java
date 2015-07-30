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
//        ParseCrashReporting.enable(this);
        Parse.enableLocalDatastore(this);
        ParseCommon.ParseInitialize(this);
    }
}
