package com.newrunner.googlemap;

import android.content.Context;
import android.widget.Toast;
import com.parse.*;

import java.util.List;

/**
 * Created by angelr on 14-May-15.
 */
public class ParseCommon {
    public static boolean checkIfUserExist() {
        final boolean[] ifExist = {false};
        ParseQuery<ParseUser> query = ParseUser.getQuery();
        query.whereEqualTo("username", "Guest");
        query.findInBackground(new FindCallback<ParseUser>() {
            @Override
            public void done(List<ParseUser> list, ParseException e) {
                if (e == null) {
                    ifExist[0] = true;
                } else {

                }
            }
        });

        return ifExist[0];
    }

    public static void logOutUser(Context cont){
        if(ParseUser.getCurrentUser() != null){
            Toast.makeText(cont, cont.getString(R.string.successfull_logout), Toast.LENGTH_SHORT).show();
            ParseUser.logOutInBackground();
        } else {
            Toast.makeText(cont, cont.getString(R.string.msg_not_logged_in), Toast.LENGTH_SHORT).show();
        }
    }

    public static boolean isUserLoggedIn(){
        return ParseUser.getCurrentUser() != null;
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
        guestUser.signUp();
        return guestUser;
    }
}