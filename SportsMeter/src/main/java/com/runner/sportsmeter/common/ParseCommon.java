package com.runner.sportsmeter.common;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;
import com.google.android.gms.maps.model.LatLng;
import com.parse.*;
import com.runner.sportsmeter.MainActivity;
import com.runner.sportsmeter.R;
import com.runner.sportsmeter.enums.SportTypes;
import com.runner.sportsmeter.enums.UserMetrics;
import com.runner.sportsmeter.models.Account;
import com.runner.sportsmeter.models.Coordinates;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by angelr on 14-May-15.
 */
public class ParseCommon {
    public static void createDefaultUser() {
        // create guest user if not created
        if (ParseUser.getCurrentUser() == null) {
            ParseCommon.createGuestUser();
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
                    Log.i(Constants.TAG, "Retrieved " + sessions.size() + " sessions");
                } else {
                    Log.i(Constants.TAG, "Error: " + e.getMessage());
                }
            }
        });
    }

    public static void logInGuestUser(final Context cont) {
        if (!ParseCommon.isUserLoggedIn()) {
            ParseUser.logInInBackground("Guest", "123456", new LogInCallback() {
                @Override
                public void done(ParseUser user, ParseException e) {
                    if(e == null) {
                        Toast.makeText(cont, R.string.logged_in_as_guest, Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }
    }

    public void logOutUser(final Context cont) {
        final String userName = getCurrentUserUsername() + " ";
        if (ParseUser.getCurrentUser() != null) {
            ParseUser.logOutInBackground(new LogOutCallback() {
                @Override
                public void done(ParseException e) {
                    if(e == null) {
//                        if(ParseUser.getCurrentUser().get("provider") != null){
////                            new GoogleLogin(true);
//                            Toast.makeText(cont, "Google plus login", Toast.LENGTH_SHORT).show();
//                        }
                        Toast.makeText(cont, userName + cont.getString(R.string.successfully_logout), Toast.LENGTH_SHORT).show();
                    }
                }
            });
        } else {
            Toast.makeText(cont, userName + cont.getString(R.string.msg_not_logged_in), Toast.LENGTH_SHORT).show();
        }
    }

    public String getCurrentUserUsername() {
        if (ParseUser.getCurrentUser() != null && ParseUser.getCurrentUser().get("name") != null) {
            return ParseUser.getCurrentUser().get("name").toString();
        } else if(ParseUser.getCurrentUser() != null) {
            return "Anonymous";
        } else {
            return "";
        }
    }

    public static boolean isUserLoggedIn() {
        return ParseUser.getCurrentUser() != null && ParseUser.getCurrentUser().isAuthenticated();
    }

    public static void ParseInitialize(Context context) {
        Parse.initialize(context, context.getString(R.string.parse_app_id), context.getString(R.string.parse_client_key));
        // todo switch to parse server before release
//        Parse.initialize(new Parse.Configuration.Builder(context)
//                .applicationId("test")
//                .clientKey(null)
//                .server("http://10.3.72.24:1337/parse/")
//                .enableLocalDataStore()
//                .build()
//        );

        Parse.setLogLevel(Parse.LOG_LEVEL_DEBUG);
        ParseFacebookUtils.initialize(context);
//        ParseTwitterUtils.initialize(context.getString(R.string.twitter_consumer_key),
//                context.getString(R.string.twitter_consumer_secret));
    }

    public static void createGuestUser() {
        ParseUser guestUser = new ParseUser();
        guestUser.setUsername("Guest");
        guestUser.setEmail("tester@tester.com");
        guestUser.setPassword("123456");
        guestUser.put("name", "Guest");
        guestUser.signUpInBackground();
    }

    public void saveTraceStartAndEndCoord(LatLng startPointCoord, LatLng endPointCoord) {
        ArrayList<ParseGeoPoint> coordinates = new ArrayList<>();
        coordinates.add(new ParseGeoPoint(startPointCoord.latitude, startPointCoord.longitude));
        coordinates.add(new ParseGeoPoint(endPointCoord.latitude, endPointCoord.longitude));

        Coordinates saveCoords = new Coordinates();
        saveCoords.setCurrentUser(ParseUser.getCurrentUser());
        saveCoords.setStartAndEndCoordinates(coordinates);
        saveCoords.saveEventually();
    }

    public static Account createAndSaveAccount(String mail, String facebookId, Account user, UserMetrics metric, MainActivity context) {
        ParseACL acl = new ParseACL(ParseUser.getCurrentUser());
        acl.setPublicReadAccess(true);
        acl.setPublicWriteAccess(true);
        Account currentUser = new Account();
        currentUser.setCurrentUser(user.getCurrentUser());
        currentUser.setIsVerified((Boolean) (user.get("emailVerified") != null ? user.get("emailVerified") : false));
        currentUser.setMemberSince(user.getMemberSince());
        currentUser.setName(user.get("name") != null ? user.get("name").toString() : context.getString(R.string.anonymous));
        currentUser.setACL(acl);
        currentUser.setUsersMetricsUnits(metric);
        currentUser.setGender(user.getGender());
        currentUser.setUserWeight(user.getUserWeight());
        currentUser.setUserHeight(user.getUserHeight());
        currentUser.setSportType(user.getSportType());
        currentUser.setEmail(user.getEmail().equals("") ? mail : user.getEmail());
        currentUser.setFacebookId(facebookId);
        return currentUser;
    }

    public static Account convertFromUserToAccount(ParseUser currentUser, Context context, SportTypes sportType) {
        ParseACL acl = new ParseACL(ParseUser.getCurrentUser());
        acl.setPublicReadAccess(true);
        acl.setPublicWriteAccess(true);
        Account usersAccount = new Account();
        usersAccount.setCurrentUser(currentUser);
        usersAccount.setSportType(sportType);
        usersAccount.setIsVerified((Boolean) (currentUser.get("emailVerified") != null ? currentUser.get("emailVerified") : false));
        usersAccount.setMemberSince(currentUser.getCreatedAt());
        usersAccount.setName(currentUser.get("name") != null ? currentUser.get("name").toString() : context.getString(R.string.anonymous));
        usersAccount.setEmail(currentUser.getEmail() != null ? currentUser.getEmail() : "");
        usersAccount.setACL(acl);
        return usersAccount;
    }

    public static void checkIfAccountExistAndSave(final Account finalAccount) {
        ParseQuery<Account> query = Account.getQuery();
        query.whereEqualTo("user", ParseUser.getCurrentUser());
        query.findInBackground(new FindCallback<Account>() {
            @Override
            public void done(List<Account> objects, ParseException e) {
                if(e == null){
                    if(objects.size() == 0) {
                        finalAccount.saveEventually();
                    }
                }
            }
        });
    }

    public static ArrayList<ParseGeoPoint> convertListToArrayListOfParseGeoPoint(List<LatLng> listOfPoints) {
        ArrayList<ParseGeoPoint> result = new ArrayList<>();
        for(LatLng l : listOfPoints){
            result.add(new ParseGeoPoint(l.latitude, l.longitude));
        }

        return result;
    }

    public static List<LatLng> convertArrayListOfParseGeoPointToList(ArrayList<ParseGeoPoint> listOfPoints) {
        List<LatLng> result = new ArrayList<>();
        for(ParseGeoPoint p : listOfPoints){
            result.add(new LatLng(p.getLatitude(), p.getLongitude()));
        }

        return result;
    }
}