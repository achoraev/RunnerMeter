package com.newrunner.sportsmeter.activities;

import android.app.Activity;
import android.os.Bundle;
import android.widget.*;
import com.facebook.AccessToken;
import com.facebook.login.widget.ProfilePictureView;
import com.newrunner.sportsmeter.R;
import com.newrunner.sportsmeter.common.Utility;
import com.newrunner.sportsmeter.models.Account;
import com.parse.ParseFacebookUtils;
import com.parse.ParseUser;

import java.util.HashMap;

/**
 * Created by angelr on 03-Sep-15.
 */
public class AccountActivity extends Activity {
    private final HashMap accounts = new HashMap();
    ProfilePictureView profilePic;
    ProgressBar progress;
    EditText name, userName, eMail, createdAt;
    TextView isVerified;
    Button editBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.account_layout);

        // init views
        progress = (ProgressBar) findViewById(R.id.progress_bar);

        profilePic = (ProfilePictureView) findViewById(R.id.profile_picture);
        name = (EditText) findViewById(R.id.edit_name);
        userName = (EditText) findViewById(R.id.edit_username);
        eMail = (EditText) findViewById(R.id.edit_mail);
        isVerified = (TextView) findViewById(R.id.edit_is_verified);
        createdAt = (EditText) findViewById(R.id.edit_date);
        editBtn = (Button) findViewById(R.id.edit_btn);

        // todo save account hashmap to local datastore
        if(!accounts.containsKey(ParseUser.getCurrentUser().getUsername())) {
            accounts.put(ParseUser.getCurrentUser().getUsername(), convertFromUserToAccount(ParseUser.getCurrentUser()));
        }

        Account current = (Account) accounts.get(ParseUser.getCurrentUser().getUsername());

        if(ParseFacebookUtils.isLinked(ParseUser.getCurrentUser())){
            String facebookId = AccessToken.getCurrentAccessToken().getUserId();
            profilePic.setProfileId(facebookId);
        }
        name.setHint(current.getName());
        userName.setHint(current.getUserName());
        eMail.setHint(current.getEmail());
        isVerified.setText(current.getIsVerified().toString());
        createdAt.setHint(current.getCreatedAt());
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
