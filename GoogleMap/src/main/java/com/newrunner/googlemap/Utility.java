package com.newrunner.googlemap;

import android.content.Context;
import android.content.DialogInterface;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.v7.app.AlertDialog;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

/**
 * Created by angelr on 14-May-15.
 */
public class Utility {
    public static void hideKeyboard(View view, Context context)
    {
        InputMethodManager in = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
        in.hideSoftInputFromWindow(view.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
    }

    public static boolean isNetworkConnected(Context context) {
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        if (activeNetwork == null && !activeNetwork.isConnectedOrConnecting()) {
            // There are no active networks.
            return false;
        } else
            return true;
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