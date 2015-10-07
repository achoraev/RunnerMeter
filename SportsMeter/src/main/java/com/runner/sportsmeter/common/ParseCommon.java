package com.runner.sportsmeter.common;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;
import com.runner.sportsmeter.R;
import com.parse.*;

import java.util.List;

/**
 * Created by angelr on 14-May-15.
 */
public class ParseCommon {
    public static void createAnonymousUser() {
        // create guest user if not created
        if (ParseUser.getCurrentUser() == null) {
            try {
                ParseUser guestUser = ParseCommon.createGuestUser();
            } catch (com.parse.ParseException e) {
                e.printStackTrace();
            }
        }
    }

    public static void loadFromParse() {
        ParseQuery<ParseObject> query = ParseQuery.getQuery("Sessions");
        query.whereEqualTo("username", ParseUser.getCurrentUser());
        query.orderByAscending("timePerKilometer");
        query.setLimit(20);
        query.findInBackground(new FindCallback<ParseObject>() {
            public void done(List<ParseObject> sessions, ParseException e) {
                if (e == null) {
//                    new LeaderBoardActivity().objectsWereRetrievedSuccessfully(sessions);
                    Log.d("session", "Retrieved " + sessions.size() + " sessions");
                } else {
                    Log.d("session", "Error: " + e.getMessage());
                }
            }
        });
    }

    public static void logInGuestUser() {
        if (!ParseCommon.isUserLoggedIn()) {
            ParseUser.logInInBackground("Guest", "123456");
        }
    }

    public static void logOutUser(Context cont) {
        if (ParseUser.getCurrentUser() != null) {
            Toast.makeText(cont, cont.getString(R.string.successfully_logout), Toast.LENGTH_SHORT).show();
            ParseUser.logOutInBackground();
        } else {
            Toast.makeText(cont, cont.getString(R.string.msg_not_logged_in), Toast.LENGTH_SHORT).show();
        }
    }

    public static boolean isUserLoggedIn() {
        return ParseUser.getCurrentUser() != null && ParseUser.getCurrentUser().isAuthenticated();
    }

    public static void ParseInitialize(Context context) {
        Parse.initialize(context, context.getString(R.string.parse_app_id), context.getString(R.string.parse_client_key));
        Parse.setLogLevel(Parse.LOG_LEVEL_DEBUG);
        ParseFacebookUtils.initialize(context);
        ParseTwitterUtils.initialize(context.getString(R.string.twitter_consumer_key),
                context.getString(R.string.twitter_consumer_secret));
    }

    public static ParseUser createGuestUser() throws ParseException {
        ParseUser guestUser = new ParseUser();
        guestUser.setUsername("Guest");
        guestUser.setEmail("tester@tester.com");
        guestUser.setPassword("123456");
        guestUser.put("name", "Guest");
        guestUser.signUpInBackground();
        return guestUser;
    }
}