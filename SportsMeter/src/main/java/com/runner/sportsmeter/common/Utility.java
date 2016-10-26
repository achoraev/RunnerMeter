package com.runner.sportsmeter.common;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Toast;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.parse.ParseFacebookUtils;
import com.parse.ParseObject;
import com.parse.ParseUser;
import com.runner.sportsmeter.R;
import com.runner.sportsmeter.fragments.PostFacebookFragment;
import com.runner.sportsmeter.models.Session;
import com.runner.sportsmeter.models.Sessions;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * Created by angelr on 14-May-15.
 */
public class Utility {

    public static void postOnFacebookWall(Session currentSession, Activity activity) {
        if (ParseFacebookUtils.isLinked(ParseUser.getCurrentUser())) {
            ParseFacebookUtils.linkWithPublishPermissionsInBackground(
                    ParseUser.getCurrentUser(),
                    activity,
                    Arrays.asList("publish_actions"));
            Intent postIntent = new Intent(activity, PostFacebookFragment.class);
            Bundle postBundle = new Bundle();
            postBundle.putParcelable("Session", currentSession);
            postIntent.putExtras(postBundle);
            activity.startActivity(postIntent);
        } else {
            Toast.makeText(activity.getBaseContext(), activity.getString(R.string.facebook_not_logged_in), Toast.LENGTH_SHORT).show();
        }
    }

    public void hideKeyboard(View view, Context context) {
        if (view != null) {
            InputMethodManager in = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
            in.hideSoftInputFromWindow(view.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
        }
    }

    public void setupAdds(AdView mAdView, Context cont) {
        AdRequest adRequest = new AdRequest.Builder()
                .addTestDevice(AdRequest.DEVICE_ID_EMULATOR)
//                .addTestDevice(cont.getString(R.string.huawei_device_id))
                .build();
        mAdView.loadAd(adRequest);
    }

    public static void loadImageFromStorage(String path) {
        try {
            File f = new File(path, "profile.jpg");
            Bitmap b = BitmapFactory.decodeStream(new FileInputStream(f));
//            ImageView img=(ImageView)findViewById(R.id.imgPicker);
//            img.setImageBitmap(b);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    public Bitmap getBitmapFromURL(String src) {
        try {
            HttpURLConnection connection = (HttpURLConnection) new URL(src).openConnection();
            connection.setDoInput(true);
            connection.connect();
            InputStream input = connection.getInputStream();
            connection.disconnect();
            return BitmapFactory.decodeStream(input);
        } catch (IOException e) {
            // Log exception
            return null;
        }
    }

    public static String saveToExternalStorage(Bitmap bitmapImage, Context cont) {
        String root = Environment.getExternalStorageDirectory().toString();
        File myDir = new File(root + "/sport_meter");
        myDir.mkdirs();
        String fileName = "img-" + new Date().getTime() + ".png";
        File file = new File(myDir, fileName);
        Log.i(Constants.TAG, "" + file);
        if (file.exists())
            file.delete();
        try {
            FileOutputStream out = new FileOutputStream(file);
            bitmapImage.compress(Bitmap.CompressFormat.PNG, 100, out);
            out.flush();
            out.close();

            MediaStore.Images.Media.insertImage(cont.getContentResolver(), file.getAbsolutePath(), file.getName(), file.getName());
        } catch (Exception e) {
            e.printStackTrace();
        }

        return file.getAbsolutePath();
    }

    public static ArrayList<Sessions> convertFromParseObject(List<ParseObject> sessions) {
        ArrayList<Sessions> arrayOfSessions = new ArrayList<>();
        for (ParseObject ses : sessions) {
            Sessions newSession = new Sessions();
            newSession.setDistance(ses.getDouble("distance"));
            newSession.setDuration(ses.getLong("duration"));
            newSession.setMaxSpeed(ses.getDouble("maxSpeed"));
            newSession.setAverageSpeed(ses.getDouble("averageSpeed"));
            newSession.setParseUser(ses.getParseUser("username"));
            newSession.setName(ses.getString("name"));
            newSession.setSportType(ses.getString("sportType"));

//            Session newSession = new Session(
//                    ses.getDouble("distance"),
//                    ses.getLong("duration"),
//                    ses.getDouble("maxSpeed"),
//                    ses.getDouble("averageSpeed"),
//                    ses.getDouble("timePerKilometer"),
//                    Utility.formatDate(ses.getCreatedAt()),
//                    ses.getParseUser("username"),
//                    ses.getString("name"),
//                    ses.getString("sportType"));
            arrayOfSessions.add(newSession);
        }
        return arrayOfSessions;
    }

    public Session convertParseSessionsToSession(Sessions sess) {
        Session newSession = new Session(
                sess.getDistance(),
                sess.getDuration(),
                sess.getMaxSpeed(),
                sess.getAverageSpeed(),
                sess.getTimePerKilometer(),
                Utility.formatDate(sess.getCreatedAt()),
                sess.getParseUser(),
                sess.getName(),
                sess.getSportType());
        return newSession;
    }

    public Sessions convertSessionToParseSessions(Session sess) {
        Sessions newSession = new Sessions();
        newSession.setDistance(sess.getDistance());
        newSession.setDuration(sess.getDuration() / 1000);
        newSession.setMaxSpeed(sess.getMaxSpeed());
        newSession.setAverageSpeed(sess.getAverageSpeed());
        newSession.setTimePerKilometer(sess.getTimePerKilometer());
        newSession.setParseUser(ParseUser.getCurrentUser());
        newSession.setName(sess.getUserName());
        newSession.setSportType(sess.getSportType().toLowerCase());
        return newSession;
    }

    public static Date formatStringToDate(String createdAt) {
        try {
            return new SimpleDateFormat("dd MMM yyyy HH:mm:ss", Locale.getDefault()).parse(createdAt);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static String formatParseDate(Date createdAt) {
        return new SimpleDateFormat("YYYY-MM-DDThh:mm:ss.SSSZ").format(createdAt);
    }

    public static String formatDate(Date createdAt) {
        return new SimpleDateFormat("dd MMM yyyy HH:mm:ss").format(createdAt);
    }

    public static String formatPace(double pace) {
        return String.valueOf(pace) + " min/km";
    }

    public static String formatSpeed(double speed) {
        return String.valueOf(speed) + " km/h";
    }

    public static String formatDistance(double distance) {
        return String.valueOf(distance) + " m";
    }

    public static String formatDurationToMinutesString(Long s) {
        return String.valueOf(Calculations.roundToTwoDigitsAfterDecimalPoint(Calculations.convertDoubleToTime(s / 60.00))) + " min";
    }

    public static boolean isNetworkConnected(Context context) {
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        // There are no active networks.
        return activeNetwork != null && activeNetwork.isConnectedOrConnecting();
    }

    public static boolean isWiFiEnabled(Context ctx) {
        ConnectivityManager cm = (ConnectivityManager) ctx.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();

        return activeNetwork != null && activeNetwork.getType() == ConnectivityManager.TYPE_WIFI;
    }

    public static void createDialogWithButtons(Context context, String message, String question) {
        new AlertDialog.Builder(context)
                .setMessage(message + question)
                .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
//                            finish();
                        dialog.cancel();
                    }
                })
                .setOnDismissListener(new DialogInterface.OnDismissListener() {
                    @Override
                    public void onDismiss(DialogInterface dialog) {
                        dialog.cancel();
//                            finish();
                    }
                })
                .setCancelable(false)
                .create()
                .show();
    }
}