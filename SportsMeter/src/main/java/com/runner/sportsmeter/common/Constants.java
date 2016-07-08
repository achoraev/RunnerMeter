package com.runner.sportsmeter.common;

import android.graphics.Color;
import com.runner.sportsmeter.enums.UserMetrics;

/**
 * Created on 30-Mar-16
 */
public class Constants {
    public static final String TAG = "sportmeter";
    public static final int THREE_SECOND = 3000;
    public static final int TWO_SECOND = 2000;
    public static final int ONE_SECOND = 1000;
    public static final long UPDATE_INTERVAL_IN_MILLISECONDS = THREE_SECOND;
    public static final long FASTEST_UPDATE_INTERVAL_IN_MILLISECONDS =
            UPDATE_INTERVAL_IN_MILLISECONDS / 2;
    public static final int MAP_ZOOM = 15;
    public static final float POLYLINE_WIDTH = 15;
    public static final int POLYLINE_COLOR = Color.parseColor("#1DCCC6");
    public static final int POLYLINE_COLOR_RED = Color.RED;
    public static final int POLYLINE_COLOR_GREEN = Color.GREEN;
    public static final String cookieUrl = "http://www.google.com/intl/bg/policies/privacy/partners/";
    public static final String LAST_UPDATED_TIME_STRING_KEY = "last-updated-time-string-key";
    public static final String SESSION_START_TIME = "sessionStartTime";
    public static final String CURRENT_SEGMENT = "currentSegment";
    public static final String GLOBAL_DISTANCE = "distance";
    public static final String GLOBAL_AVERAGE_SPEED = "averageSpeed";
    public static final String GLOBAL_MAX_SPEED = "maxSpeed";
    public static final String GLOBAL_DURATION = "duration";
    public static final String IS_STARTED = "isStarted";
    public static final String IS_PAUSED = "isPausedActivityEnable";
    public static final String PAUSED_SESSION = "pausedSession";

    public static final String LOCATION_KEY = "location-key";
    public static final UserMetrics speedMetricUnit = UserMetrics.METRIC;

    public static final int REQUEST_CHECK_SETTINGS = 0x2;
    public static final int REQUEST_LOGIN_FROM_RESULT = 100;
    public static final int MY_PERMISSIONS_REQUEST_ACCESS_LOCATION = 0x1;
    public static final double MAX_SPEED_LIMIT = 50.00;

    public static final String ADDRESS = "46, str. Liaskovets, \n 1510, Sofia, Bulgaria";
    public static final String EMAIL = "runner.meter@gmail.com";
    public static final String FACEBOOK_PAGE = "https://www.facebook.com/sportmeter/";
    public static final String WEBSITE = "http://sportmeter.co/";

    public static final int LIMIT_FOR_SPORT_TYPE = 15;
    public static final int LIMIT_FOR_USER_QUERY = 15;
    public static final int QUERY_LIMIT = 500;

    public static final int ONE_HUNDRED = 100;
    public static final int SIXTY = 60;
    public static final int TWENTY_FOUR = 24;
    public static final int ONE_THOUSAND = 1000;
    public static final double BEST_TIME_RUNNER = 1.36;
    public static final double BEST_TIME_BIKER = 0.45;
    public static final double BEST_TIME_DRIVER = 0.17;
}
