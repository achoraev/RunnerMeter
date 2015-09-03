package com.newrunner.googlemap;

import android.app.Activity;
import android.os.Bundle;
import com.parse.ParseUser;

import java.util.HashMap;

/**
 * Created by angelr on 03-Sep-15.
 */
public class AccountActivity extends Activity {
    private final HashMap accounts = new HashMap();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.account_layout);

        // todo check if user contains
        accounts.put(ParseUser.getCurrentUser().getUsername(), convertFromUserToAccount(ParseUser.getCurrentUser()));
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
