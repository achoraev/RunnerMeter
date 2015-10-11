package com.runner.sportsmeter.common;

import android.app.Activity;
import android.net.Uri;
import android.net.http.AndroidHttpClient;
import android.os.AsyncTask;
import android.os.Bundle;
import android.widget.ImageView;
import com.parse.ParseTwitterUtils;
import com.runner.sportsmeter.R;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpGet;

import java.io.IOException;
import java.util.List;

/**
 * Created by AMR on 11.10.2015 ã..
 */

public class HttpGetJson extends Activity {

    static String screenName = ParseTwitterUtils.getTwitter().getScreenName();
    private static final String URL = "https://api.twitter.com/1.1/users/show.json?screen_name="
            + screenName;
    ImageView twitterImage;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        twitterImage = (ImageView) findViewById(R.id.image);
        new HttpGetTask().execute();
    }

    private class HttpGetTask extends AsyncTask<Void, Void, List<String>> {

        AndroidHttpClient mClient = AndroidHttpClient.newInstance("");

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
            if (null != mClient) {
                mClient.close();
            }

            twitterImage.setImageURI(Uri.parse(result.get(0)));
        }
    }
}
