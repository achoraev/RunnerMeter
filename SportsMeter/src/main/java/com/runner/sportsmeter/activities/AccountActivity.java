package com.runner.sportsmeter.activities;

import android.app.Activity;
import android.graphics.Bitmap;
import android.net.http.AndroidHttpClient;
import android.os.AsyncTask;
import android.os.Bundle;
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
import com.parse.signpost.http.HttpResponse;
import com.runner.sportsmeter.R;
import com.runner.sportsmeter.common.JsonResponseHandler;
import com.runner.sportsmeter.common.Utility;
import com.runner.sportsmeter.models.Account;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;

/**
 * Created by angelr on 03-Sep-15.
 */
public class AccountActivity extends Activity {
    private final HashMap accounts = new HashMap();
    ProfilePictureView profilePic;
    ProgressBar progressBar;
    TextView name, userName, eMail, createdAt, isVerified;
    Button closeBtn;
    AdView mAdView;
    String facebookId, twitterImageUrl, twitterImagePath;
    ImageView twitterImageViewPicture;
    Bitmap twitterBitmap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.account_layout);

        // init views
        progressBar = (ProgressBar) findViewById(R.id.progress_bar);

        profilePic = (ProfilePictureView) findViewById(R.id.profile_picture);
        name = (TextView) findViewById(R.id.edit_name);
        userName = (TextView) findViewById(R.id.edit_username);
        eMail = (TextView) findViewById(R.id.edit_mail);
        isVerified = (TextView) findViewById(R.id.edit_is_verified);
        createdAt = (TextView) findViewById(R.id.edit_date);
        twitterImageViewPicture = (ImageView) findViewById(R.id.twitter_image_view);

        closeBtn = (Button) findViewById(R.id.close_btn);
        closeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        // todo save account hashmap to local datastore
        if (!accounts.containsKey(ParseUser.getCurrentUser().getUsername())) {
            accounts.put(ParseUser.getCurrentUser().getUsername(), convertFromUserToAccount(ParseUser.getCurrentUser()));
        }

        Account current = (Account) accounts.get(ParseUser.getCurrentUser().getUsername());

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
            facebookGraphMeRequestForUserInfo();
        } else if (ParseTwitterUtils.isLinked(ParseUser.getCurrentUser())) {
            eMail.setText(getString(R.string.twitter_email_not_present));
            new HttpGetTask().execute();
            try {
                twitterImagePath = getTwitterProfileImage();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (JSONException e) {
                e.printStackTrace();
            }
        } else {
            eMail.setText(current.getEmail());
        }
        isVerified.setText(current.getIsVerified().toString());
        createdAt.setText(current.getCreatedAt());

        // setup adds
        mAdView = (AdView) findViewById(R.id.adViewAccount);
        new Utility().setupAdds(mAdView, this);
    }

    private String getTwitterProfileImage() throws IOException, JSONException {
        // 2
        String screenName = ParseTwitterUtils.getTwitter().getScreenName();
        String url = "https://api.twitter.com/1.1/users/show.json?screen_name="
                + screenName;

//        ImageRequest request = new ImageRequest(url,
//                new Response.Listener<Bitmap>() {
//                    @Override
//                    public void onResponse(Bitmap bitmap) {
//                        twitterImageViewPicture.setImageBitmap(bitmap);
//                    }
//                }, 0, 0, null,
//                new Response.ErrorListener() {
//                    public void onErrorResponse(VolleyError error) {
//                    }
//                });
//// Access the RequestQueue through your singleton class.
//        MySingleton.getInstance(this).addToRequestQueue(request);
//        TwitterApiClient twitterApiClient = TwitterCore.getInstance().getApiClient();
        // 1
        HttpClient client = new DefaultHttpClient();
        HttpGet verifyGetParse = new HttpGet(
                "https://api.twitter.com/1.1/users/show.json?screen_name="
                        + screenName);
        ParseTwitterUtils.getTwitter().signRequest(verifyGetParse);
        HttpResponse response = (HttpResponse) client.execute(verifyGetParse);

//        HttpEntity entity = new DefaultHttpClient().execute(verifyGet).getEntity();
//        JSONObject responseJson = new JSONObject(IOUtils.toString(entity.getContent()));
//        String url = responseJson.get("profile_image_url").toString();

        return response.toString();
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
                (Boolean) currentUser.get("emailVerified"),
                Utility.formatDate(currentUser.getCreatedAt()));
    }

    private class HttpGetTask extends AsyncTask<Void, Void, List<String>> {

        String screenName = ParseTwitterUtils.getTwitter().getScreenName();
        String URL = "https://api.twitter.com/1.1/users/show.json?screen_name="
                + screenName;

        AndroidHttpClient mClient = AndroidHttpClient.newInstance("");

        @Override
        protected void onPreExecute() {
            progressBar.setVisibility(View.VISIBLE);
        }

        @Override
        protected List<String> doInBackground(Void... params) {
            HttpGet request = new HttpGet(URL);
            ParseTwitterUtils.getTwitter().signRequest(request);
            JsonResponseHandler responseHandler = new JsonResponseHandler();
            try {
                return mClient.execute(request, responseHandler);
            } catch (ClientProtocolException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(List<String> result) {
//            if (null != mClient) {
//                mClient.close();
//            }

            progressBar.setVisibility(View.GONE);
            twitterImageUrl = result.get(0);
            twitterImageUrl.replace("_normal", "_bigger");
//            Thread netThread = new Thread(new Runnable() {
//                @Override
//                public void run() {
//                    twitterBitmap = new Utility().getBitmapFromURL(twitterImageUrl);
//                    twitterImagePath = Utility.saveToExternalStorage(twitterBitmap, AccountActivity.this);
//                }
//            });
//            netThread.start();
//            twitterImageViewPicture.setImageBitmap(twitterBitmap);
        }
    }
}
