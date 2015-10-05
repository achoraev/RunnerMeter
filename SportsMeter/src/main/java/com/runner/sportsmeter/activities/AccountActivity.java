package com.runner.sportsmeter.activities;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
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
import com.runner.sportsmeter.common.Utility;
import com.runner.sportsmeter.models.Account;
import org.json.JSONException;
import org.json.JSONObject;

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
    String facebookId;

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

        closeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        // todo save account hashmap to local datastore
        if(!accounts.containsKey(ParseUser.getCurrentUser().getUsername())) {
            accounts.put(ParseUser.getCurrentUser().getUsername(), convertFromUserToAccount(ParseUser.getCurrentUser()));
        }

        Account current = (Account) accounts.get(ParseUser.getCurrentUser().getUsername());

        if(ParseFacebookUtils.isLinked(ParseUser.getCurrentUser())){
            facebookId = AccessToken.getCurrentAccessToken().getUserId();
            profilePic.setProfileId(facebookId);
        }
        name.setText(current.getName());
        userName.setText(current.getName());
        if(ParseFacebookUtils.isLinked(ParseUser.getCurrentUser())){
            facebookGraphMeRequestForUserInfo();
        } else if(ParseTwitterUtils.isLinked(ParseUser.getCurrentUser())) {
            eMail.setText(getString(R.string.twitter_email_not_present));
        } else {
            eMail.setText(current.getEmail());
        }
        isVerified.setText(current.getIsVerified().toString());
        createdAt.setText(current.getCreatedAt());

        // setup adds
        mAdView = (AdView) findViewById(R.id.adViewAccount);
        new Utility().setupAdds(mAdView, this);
    }

    private void facebookGraphMeRequestForUserInfo() {
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
                            String mail = object.getString("email");
                            eMail.setText(mail);
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

    private Account convertFromUserToAccount(ParseUser currentUser) {
        return new Account(currentUser.getUsername(),
                currentUser.get("name").toString(),
                currentUser.getEmail(),
                (Boolean)currentUser.get("emailVerified"),
                Utility.formatDate(currentUser.getCreatedAt()));
    }
}
