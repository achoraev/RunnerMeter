package com.parse.ui;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.Toast;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.OptionalPendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.parse.*;

import java.util.List;

/**
 * Created by Angel Raev on 19-Feb-16
 */
public class GoogleLogin extends AppCompatActivity implements GoogleApiClient.OnConnectionFailedListener {

    private GoogleApiClient mGoogleApiClient;
    private static final String TAG = "SignInActivity";
    private static final int RC_SIGN_IN = 9001;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
//                .requestScopes(new Scope(Scopes.PLUS_LOGIN))
                .requestEmail()
                .build();

        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this /* FragmentActivity */, this /* OnConnectionFailedListener */)
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build();

        signIn();
    }

    @Override
    public void onStart() {
        super.onStart();

        OptionalPendingResult<GoogleSignInResult> opr = Auth.GoogleSignInApi.silentSignIn(mGoogleApiClient);
        if (opr.isDone()) {
            Log.d(TAG, "Got cached sign-in");
            GoogleSignInResult result = opr.get();
            handleSignInResult(result);
        } else {
            opr.setResultCallback(new ResultCallback<GoogleSignInResult>() {
                @Override
                public void onResult(GoogleSignInResult googleSignInResult) {
                    handleSignInResult(googleSignInResult);
                }
            });
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
            handleSignInResult(result);
        }
    }

    private void signIn() {
        Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient);
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    private void signOut() {
        Auth.GoogleSignInApi.signOut(mGoogleApiClient).setResultCallback(
                new ResultCallback<Status>() {
                    @Override
                    public void onResult(Status status) {
//                        updateUI(false);
                    }
                });
    }

    private void revokeAccess() {
        Auth.GoogleSignInApi.revokeAccess(mGoogleApiClient).setResultCallback(
                new ResultCallback<Status>() {
                    @Override
                    public void onResult(Status status) {
//                        updateUI(false);
                    }
                });
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        // An unresolvable error has occurred and Google APIs (including Sign-In) will not
        // be available.
        Log.d(TAG, "onConnectionFailed:" + connectionResult);
    }

    private void handleSignInResult(GoogleSignInResult result) {
        Log.d(TAG, "handleSignInResult:" + result.isSuccess());
        if (result.isSuccess()) {
            // Signed in successfully, show authenticated UI.
            final GoogleSignInAccount acct = result.getSignInAccount();
            if (acct != null) {
                ParseQuery<ParseUser> query = ParseUser.getQuery();
                query.whereContains("provider", "GooglePlus");
                query.whereNotEqualTo("username", acct.getEmail());
                query.findInBackground(new FindCallback<ParseUser>() {
                    @Override
                    public void done(List<ParseUser> objects, ParseException e) {
                        if(e == null){
                            if(objects.size() == 0) {
                                createAndSignInGoogleUser(acct);
                            } else {
                                signInGoogleUser(acct , objects);
                            }
                        }
                    }
                });
            }
            finish();
        } else {
            // todo remove
            Toast.makeText(GoogleLogin.this, "Status is: " + result.getStatus().toString(), Toast.LENGTH_SHORT).show();
        }
    }

    private void signInGoogleUser(GoogleSignInAccount acct, List<ParseUser> objects) {
        ParseUser.becomeInBackground(objects.get(0).getCurrentUser().getSessionToken(), new LogInCallback() {
            @Override
            public void done(ParseUser user, ParseException e) {
                finish();
            }
        });
    }

    private void createAndSignInGoogleUser(GoogleSignInAccount acct) {
        ParseUser user = new ParseUser();
        user.setUsername(acct.getEmail());
        user.setPassword(acct.getId());
        user.put("name", acct.getDisplayName());
        if (acct.getPhotoUrl() != null) {
            user.put("pictureURI", acct.getPhotoUrl().toString());
        }
        user.put("provider", "GooglePlus");
        user.setEmail(acct.getEmail());
        user.signUpInBackground(new SignUpCallback() {
            @Override
            public void done(ParseException e) {
                if (e == null) {
                    // todo remove toast
                    Toast.makeText(GoogleLogin.this, "Create user", Toast.LENGTH_SHORT).show();
                    ParseUser.becomeInBackground(ParseUser.getCurrentUser().getSessionToken(), new LogInCallback() {
                        public void done(ParseUser user, ParseException e) {
                            if (user != null) {
                                // The current user is now set to user.
                                Toast.makeText(GoogleLogin.this, "become user " + user.getUsername(), Toast.LENGTH_LONG).show();
                            } else {
                                // The token could not be validated.
                                Toast.makeText(GoogleLogin.this, "Error become", Toast.LENGTH_LONG).show();
                            }
                            finish();
                        }
                    });
                } else {
                    // todo remove toast
                    Toast.makeText(GoogleLogin.this, "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}
