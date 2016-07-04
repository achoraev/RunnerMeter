package com.runner.sportsmeter.fragments;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import com.facebook.CallbackManager;
import com.facebook.FacebookSdk;
import com.facebook.share.model.ShareLinkContent;
import com.facebook.share.model.ShareOpenGraphAction;
import com.facebook.share.model.ShareOpenGraphContent;
import com.facebook.share.model.ShareOpenGraphObject;
import com.facebook.share.widget.ShareDialog;
import com.runner.sportsmeter.R;
import com.runner.sportsmeter.common.Calculations;
import com.runner.sportsmeter.enums.SportTypes;
import com.runner.sportsmeter.models.Session;

/**
 * Created by angelr on 15-Oct-15.
 */
public class PostFacebookFragment extends FragmentActivity {
    CallbackManager callbackManager;
    ShareDialog shareDialog;
    private Long sessionTimeDiff;
    private double sessionDistance;
    private String sportType, userName;
    private double sessionAverageSpeed;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        updateFromBundle(getIntent().getExtras());
        FacebookSdk.sdkInitialize(getApplicationContext());
        callbackManager = CallbackManager.Factory.create();
        shareDialog = new ShareDialog(this);
        // this part is optional
//        shareDialog.registerCallback(callbackManager, new FacebookCallback<Sharer.Result>() { ... });

        String action = SportTypes.valueOf(sportType.toUpperCase()).equals(SportTypes.RUNNING) ? " running " :
                        SportTypes.valueOf(sportType.toUpperCase()).equals(SportTypes.CYCLING) ? " cycling " :
                        SportTypes.valueOf(sportType.toUpperCase()).equals(SportTypes.CLIMBING) ? " climbing " :
                        SportTypes.valueOf(sportType.toUpperCase()).equals(SportTypes.WALKING) ? " walking " : " ";
        // todo action not works
        String message = userName + action + sessionDistance + " m for " + Calculations.convertTimeToString(sessionTimeDiff) + " with Sport Meter";
        if (ShareDialog.canShow(ShareLinkContent.class)) {
            ShareLinkContent linkContent = new ShareLinkContent.Builder()
                    .setContentTitle(getString(R.string.app_name))
                    .setContentDescription(message)
                    .setContentUrl(Uri.parse(getString(R.string.facebook_page)))
                    .build();

            // todo try this for share
            ShareOpenGraphObject object = new ShareOpenGraphObject.Builder()
                    .putString("og:type", "fitness.course")
                    .putString("og:title", "Sample Course")
                    .putString("og:description", message)
                    .putInt("fitness:duration:value", Integer.valueOf(sessionTimeDiff.toString()))
                    .putString("fitness:duration:units", "s")
                    .putInt("fitness:distance:value", (int) sessionDistance)
                    .putString("fitness:distance:units", "m")
                    .putInt("fitness:speed:value", (int) sessionAverageSpeed)
                    .putString("fitness:speed:units", "km/h")
                    .build();
            ShareOpenGraphAction act = new ShareOpenGraphAction.Builder()
                    .setActionType("fitness.runs")
                    .putObject("fitness:course", object)
                    .build();
            ShareOpenGraphContent content = new ShareOpenGraphContent.Builder()
                    .setContentUrl(Uri.parse(getString(R.string.facebook_page)))
                    .setPreviewPropertyName("fitness:course")
                    .setAction(act)
                    .build();

//            ShareApi.share(content, null);
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
                sessionAverageSpeed = updated.getAverageSpeed();
                sportType = updated.getSportType();
                userName = updated.getUserName();
            }
        }
    }
}
