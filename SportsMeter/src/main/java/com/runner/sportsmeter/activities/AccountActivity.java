package com.runner.sportsmeter.activities;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.provider.Settings;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import com.facebook.AccessToken;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.login.widget.ProfilePictureView;
import com.google.android.gms.ads.AdView;
import com.parse.ParseFacebookUtils;
import com.parse.ParseTwitterUtils;
import com.parse.ParseUser;
import com.runner.sportsmeter.R;
import com.runner.sportsmeter.common.ParseCommon;
import com.runner.sportsmeter.common.Utility;
import com.runner.sportsmeter.enums.SportTypes;
import com.runner.sportsmeter.models.Account;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

/**
 * Created by angelr on 03-Sep-15.
 */
public class AccountActivity extends Activity {
    private final HashMap<String, Account> accounts = new HashMap<String, Account>();
    ProfilePictureView profilePic;
    ProgressBar progressBar;
    TextView name, userName, eMail, createdAt, isVerified;
    Button closeBtn;
    AdView mAdView;
    String facebookId = "";
    ImageView twitterImageViewPicture;
    private String userEmail = "";
    private SportTypes sportType;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.l_account_layout);

        sportType = (SportTypes) getIntent().getExtras().get(getString(R.string.type_of_sport));

        initializeViews();

        closeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        if (Utility.isNetworkConnected(AccountActivity.this) && ParseUser.getCurrentUser() != null) {
            if (!accounts.containsKey(ParseUser.getCurrentUser().getUsername())) {
                accounts.put(ParseUser.getCurrentUser().getUsername(), ParseCommon.convertFromUserToAccount(ParseUser.getCurrentUser(), AccountActivity.this, sportType));
            }

            Account current = accounts.get(ParseUser.getCurrentUser().getUsername());

            if (ParseFacebookUtils.isLinked(ParseUser.getCurrentUser())) {
                facebookId = AccessToken.getCurrentAccessToken().getUserId();
                profilePic.setProfileId(facebookId);
            } else if (ParseTwitterUtils.isLinked(ParseUser.getCurrentUser())) {
                profilePic.setVisibility(View.INVISIBLE);
                twitterImageViewPicture.setVisibility(View.VISIBLE);
            }

            name.setText(current.getName());
            userName.setText(current.getName());
            if (ParseFacebookUtils.isLinked(ParseUser.getCurrentUser())) {
                facebookGraphMeRequestForUserInfo(current);
            } else if (ParseTwitterUtils.isLinked(ParseUser.getCurrentUser())) {
                eMail.setText(getString(R.string.twitter_email_not_present));
                userEmail = getString(R.string.twitter_email_not_present);
                //            new HttpGetTask().execute();
                //            try {
                //                twitterImagePath = getTwitterProfileImage();
                //            } catch (IOException e) {
                //                e.printStackTrace();
                //            } catch (JSONException e) {
                //                e.printStackTrace();
                //            }
            } else {
                eMail.setText(current.getEmail());
                userEmail = current.getEmail();
            }
            isVerified.setText(current.getIsVerified().toString());
            createdAt.setText(Utility.formatDate(current.getMemberSince()));
//            createAndSaveAccount(userEmail, facebookId, current);
        } else {
            turnOnWiFiOrDataInternet();
        }
        // setup adds
        mAdView = (AdView) findViewById(R.id.adViewAccount);
        new Utility().setupAdds(mAdView, this);
    }

    private void initializeViews() {
        progressBar = (ProgressBar) findViewById(R.id.progress_bar);
        profilePic = (ProfilePictureView) findViewById(R.id.profile_picture);
        name = (TextView) findViewById(R.id.edit_name);
        userName = (TextView) findViewById(R.id.edit_username);
        eMail = (TextView) findViewById(R.id.edit_mail);
        isVerified = (TextView) findViewById(R.id.edit_is_verified);
        createdAt = (TextView) findViewById(R.id.edit_date);
        twitterImageViewPicture = (ImageView) findViewById(R.id.twitter_image_view);
        closeBtn = (Button) findViewById(R.id.close_btn);
    }

    private void turnOnWiFiOrDataInternet() {
        if (!Utility.isNetworkConnected(AccountActivity.this)) {
            new AlertDialog.Builder(AccountActivity.this)
                    .setTitle(getString(R.string.no_internet))
                    .setMessage(getString(R.string.no_net_message))
                    .setPositiveButton(getString(R.string.turn_on_wifi), new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            WifiManager wifi = (WifiManager) AccountActivity.this.getSystemService(Context.WIFI_SERVICE);
                            wifi.setWifiEnabled(true);
                        }
                    })
                    .setNeutralButton(getString(R.string.turn_on_data), new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            // prompt user
                            // 1
//                            Intent dialogIntent = new Intent(android.provider.Settings.ACTION_SETTINGS);
//                            dialogIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//                            startActivity(dialogIntent);
                            // 5
                            Intent intent = new Intent();
                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            intent.setAction(Settings.ACTION_DATA_ROAMING_SETTINGS);
                            overridePendingTransition(android.R.anim.fade_in,
                                    android.R.anim.fade_out);
                            startActivity(intent);
                        }
                    })
                    .setNegativeButton(getString(R.string.close), new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    }).show();
        }
    }

    private void facebookGraphMeRequestForUserInfo(final Account current) {
        GraphRequest request = GraphRequest.newMeRequest(
                AccessToken.getCurrentAccessToken(),
                new GraphRequest.GraphJSONObjectCallback() {
                    @Override
                    public void onCompleted(JSONObject object, GraphResponse graphResponse) {
//                            userName = object.getString("name");
//                            userId = object.getString("id");
//                            userGender = object.getString("gender");
//                            userProfileURL = object.getString("link");
//                            firstName = object.getString("first_name");
//                            lastName = object.getString("last_name");
                        try {
                            userEmail = object.getString("email");
                            eMail.setText(userEmail);
//                            createAndSaveAccount(userEmail, facebookId, current);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                });
        Bundle parameters = new Bundle();
        parameters.putString("fields", "age_range,gender,name,id,link,email,picture.type(large),first_name,last_name");
        request.setParameters(parameters);
        request.executeAsync();
    }
}
