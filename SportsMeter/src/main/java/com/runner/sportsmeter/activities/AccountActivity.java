package com.runner.sportsmeter.activities;

import android.app.Activity;
import android.os.Bundle;
import android.widget.*;
import com.facebook.AccessToken;
import com.facebook.login.widget.ProfilePictureView;
import com.google.android.gms.ads.AdView;
import com.runner.sportsmeter.R;
import com.runner.sportsmeter.common.Utility;
import com.runner.sportsmeter.models.Account;
import com.parse.ParseFacebookUtils;
import com.parse.ParseUser;

import java.util.HashMap;

/**
 * Created by angelr on 03-Sep-15.
 */
public class AccountActivity extends Activity {
    private final HashMap accounts = new HashMap();
    ProfilePictureView profilePic;
//    ProgressBar progress;
    TextView name, userName, eMail, createdAt, isVerified;
    Button closeBtn;
    AdView mAdView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.account_layout);

        // init views
//        progress = (ProgressBar) findViewById(R.id.progress_bar);

        profilePic = (ProfilePictureView) findViewById(R.id.profile_picture);
        name = (TextView) findViewById(R.id.edit_name);
        userName = (TextView) findViewById(R.id.edit_username);
        eMail = (TextView) findViewById(R.id.edit_mail);
        isVerified = (TextView) findViewById(R.id.edit_is_verified);
        createdAt = (TextView) findViewById(R.id.edit_date);
        closeBtn = (Button) findViewById(R.id.close_btn);

        // todo save account hashmap to local datastore
        if(!accounts.containsKey(ParseUser.getCurrentUser().getUsername())) {
            accounts.put(ParseUser.getCurrentUser().getUsername(), convertFromUserToAccount(ParseUser.getCurrentUser()));
        }

        Account current = (Account) accounts.get(ParseUser.getCurrentUser().getUsername());

        if(ParseFacebookUtils.isLinked(ParseUser.getCurrentUser())){
            String facebookId = AccessToken.getCurrentAccessToken().getUserId();
            profilePic.setProfileId(facebookId);
        }
        name.setText(current.getName());
        userName.setText(current.getName());
        eMail.setText(current.getEmail());
        isVerified.setText(current.getIsVerified().toString());
        createdAt.setText(current.getCreatedAt());

        // setup adds
        mAdView = (AdView) findViewById(R.id.adViewAccount);
        new Utility().setupAdds(mAdView, this);
    }

    private Account convertFromUserToAccount(ParseUser currentUser) {
        Account acc = new Account(currentUser.getUsername(),
                currentUser.get("name").toString(),
                currentUser.getEmail(),
                (Boolean)currentUser.get("emailVerified"),
                Utility.formatDate(currentUser.getCreatedAt()));
        return acc;
    }
}
