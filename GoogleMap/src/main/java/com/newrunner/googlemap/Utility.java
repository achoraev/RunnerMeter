package com.newrunner.googlemap;

import android.content.Context;
import android.content.DialogInterface;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.v7.app.AlertDialog;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import com.parse.ParseObject;
import com.parse.ParseUser;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by angelr on 14-May-15.
 */
public class Utility {
    public static ArrayList<Session> convertFromParseObject(List<ParseObject> sessions) {
        ArrayList<Session> arrayOfSessions = new ArrayList<>();
        for(ParseObject ses : sessions){
            Session newSession = new Session(
                    ses.getDouble("distance"),
                    ses.getDouble("duration"),
                    ses.getDouble("maxSpeed"),
                    ses.getDouble("averageSpeed"),
//                    ses.getCreatedAt(),
                    ParseUser.getCurrentUser());
            arrayOfSessions.add(newSession);
        }
        return arrayOfSessions;
    }

    public static void hideKeyboard(View view, Context context)
    {
        InputMethodManager in = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
        in.hideSoftInputFromWindow(view.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
    }

    public static boolean isNetworkConnected(Context context) {
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        // There are no active networks.
        return activeNetwork != null && activeNetwork.isConnectedOrConnecting();
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