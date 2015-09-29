package com.runner.sportsmeter.common;

import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.parse.ParseObject;
import com.runner.sportsmeter.R;
import com.runner.sportsmeter.models.Session;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by angelr on 14-May-15.
 */
public class Utility {

    public void setupAdds(AdView mAdView, Context cont) {
        AdRequest adRequest = new AdRequest.Builder()
                .addTestDevice(AdRequest.DEVICE_ID_EMULATOR)
                .addTestDevice(cont.getString(R.string.huawei_device_id))
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

    public static String saveToExternalStorage(Bitmap bitmapImage, Context cont) {
        String root = Environment.getExternalStorageDirectory().toString();
        File myDir = new File(root + "/sport_meter_sessions");
        myDir.mkdirs();
//        int n = new Random().nextInt(10000);
        String fileName = "img-" + new Date().getTime() + ".jpg";
        File file = new File(myDir, fileName);
        Log.i("file", "" + file);
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

        // other
//        ContextWrapper cw = new ContextWrapper(cont);
//        // path to /data/data/yourapp/app_data/imageDir
//        File directory = cw.getDir("imageDir", Context.MODE_PRIVATE);
//        // Create imageDir
//        File mypath=new File(directory,"profile.jpg");

        return file.getAbsolutePath();
    }

    public static ArrayList<Session> convertFromParseObject(List<ParseObject> sessions) {
        ArrayList<Session> arrayOfSessions = new ArrayList<>();
        for (ParseObject ses : sessions) {
            Session newSession = new Session(
                    ses.getDouble("distance"),
                    ses.getDouble("duration"),
                    ses.getDouble("maxSpeed"),
                    ses.getDouble("averageSpeed"),
                    ses.getDouble("timePerKilometer"),
                    Utility.formatDate(ses.getCreatedAt()),
                    ses.getParseUser("username"),
                    ses.getString("name"),
                    ses.getString("sportType") != null ? ses.getString("sportType") : null);
            arrayOfSessions.add(newSession);
        }
        return arrayOfSessions;
    }

    public static String formatDate(Date createdAt) {
        String formatted = new SimpleDateFormat("dd MMM yyyy HH:mm:ss").format(createdAt);
        return formatted;
    }

    public static void hideKeyboard(View view, Context context) {
        InputMethodManager in = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
        in.hideSoftInputFromWindow(view.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
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

        return activeNetwork.getType() == ConnectivityManager.TYPE_WIFI;
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