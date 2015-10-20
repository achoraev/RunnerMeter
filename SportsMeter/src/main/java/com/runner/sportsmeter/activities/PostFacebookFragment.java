package com.runner.sportsmeter.activities;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import com.facebook.CallbackManager;
import com.facebook.FacebookSdk;
import com.facebook.share.model.ShareLinkContent;
import com.facebook.share.widget.ShareDialog;
import com.runner.sportsmeter.R;
import com.runner.sportsmeter.models.Session;

/**
 * Created by angelr on 15-Oct-15.
 */
public class PostFacebookFragment extends FragmentActivity {
    CallbackManager callbackManager;
    ShareDialog shareDialog;
    private double sessionDistance, sessionTimeDiff;
    private String sportType, userName;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        savedInstanceState = getIntent().getExtras();
        updateFromBundle(savedInstanceState);
        FacebookSdk.sdkInitialize(getApplicationContext());
        callbackManager = CallbackManager.Factory.create();
        shareDialog = new ShareDialog(this);
        // this part is optional
//        shareDialog.registerCallback(callbackManager, new FacebookCallback<Sharer.Result>() { ... });

        String action = sportType.equals("runner") ? " runs " :
                        sportType.equals("biker") ? " bikes " :
                        sportType.equals("driver") ? " drives " : " ";
        String message = userName + action + sessionDistance + "m for " + sessionTimeDiff + "s with Sport Meter";
        if (ShareDialog.canShow(ShareLinkContent.class)) {
            ShareLinkContent linkContent = new ShareLinkContent.Builder()
                    .setContentTitle(getString(R.string.app_name))
                    .setContentDescription(message)
                    .setContentUrl(Uri.parse(getString(R.string.facebook_page)))
                    .build();

            shareDialog.show(linkContent);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        callbackManager.onActivityResult(requestCode, resultCode, data);
        finish();
    }

    private void updateFromBundle(Bundle savedInstanceState) {
        if (savedInstanceState != null) {
            if (savedInstanceState.keySet().contains("Session")) {
                Session updated = (Session) savedInstanceState.get("Session");
                sessionDistance = updated.getDistance();
                sessionTimeDiff = updated.getDuration();
                sportType = updated.getSportType();
                userName = updated.getUserName();
            }
        }
    }
}