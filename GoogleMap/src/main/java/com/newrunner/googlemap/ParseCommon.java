package com.newrunner.googlemap;

import android.content.Context;
import android.widget.Toast;
import com.parse.*;

/**
 * Created by angelr on 14-May-15.
 */
public class ParseCommon {
    public static void logOutUser(Context cont){
        if(ParseUser.getCurrentUser() != null){
            Toast.makeText(cont, cont.getString(R.string.successfull_logout), Toast.LENGTH_SHORT).show();
            ParseUser.logOut();
        } else {
            Toast.makeText(cont, cont.getString(R.string.msg_not_logged_in), Toast.LENGTH_SHORT).show();
        }
    }

    public static boolean isUserLoggedIn(){
        ParseUser currentUser = ParseUser.getCurrentUser();
        if (currentUser != null) {
            // do stuff with the user
        } else {
            // show the signup or login screen
            return false;
        }

        return true;
    }

    public static void registerOnParse(String userName, String passWord, String eMail, Context context) throws ParseException {
        ParseUser user = new ParseUser();
        user.setUsername(userName);
        user.setPassword(passWord);
        user.setEmail(eMail);

        user.signUp();
//        user.signUpInBackground(new SignUpCallback() {
//            public void done(ParseException e) {
//                if (e == null) {
//                    // Hooray! Let them use the app now.
//                } else {
//                    // Sign up didn't succeed. Look at the ParseException
//                    // to figure out what went wrong
//                    Toast.makeText(context, e.getMessage(), Toast.LENGTH_SHORT).show();
//                    throw new IllegalArgumentException(e.getMessage());
//                }
//            }
//        });
    }

    public static void logInInParse(String userName, String passWord, final Context context) {
        ParseUser.logInInBackground(userName, passWord, new LogInCallback() {
            public void done(ParseUser user, ParseException e) {
                if (user != null) {
//                    homeScreenIntent.putExtra("username", userName);
//                    homeScreenIntent.putExtra("password", passWord);
////                            Toast.makeText(this, "Welcome " + userName, Toast.LENGTH_SHORT).show();
//                    overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
//                    startActivity(homeScreenIntent);
                } else {
//                    startActivity(registerScreenIntent);
                    // Signup failed. Look at the ParseException to see what happened.
                    Toast.makeText(context, e.getMessage(), Toast.LENGTH_SHORT).show();
                    throw new IllegalArgumentException(e.getMessage());
                }
            }
        });
    }

    public static void ParseInitialize(Context context) {
        Parse.initialize(context, context.getString(R.string.parse_app_id), context.getString(R.string.parse_client_key));
        Parse.setLogLevel(Parse.LOG_LEVEL_DEBUG);
        ParseFacebookUtils.initialize(context);
        ParseTwitterUtils.initialize(context.getString(R.string.twitter_consumer_key),
                context.getString(R.string.twitter_consumer_secret));
    }

    public static ParseUser createGuestUser(ParseUser guestUser) {
        guestUser = new ParseUser();
        guestUser.setUsername("Guest");
        guestUser.setEmail("tester@tester.com");
        guestUser.setPassword("123456");
        guestUser.signUpInBackground();
        return guestUser;
    }
}