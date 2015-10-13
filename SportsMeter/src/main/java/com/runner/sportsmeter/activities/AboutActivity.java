package com.runner.sportsmeter.activities;

import android.app.Activity;
import android.os.Bundle;
import android.widget.TextView;
import com.runner.sportsmeter.BuildConfig;
import com.runner.sportsmeter.R;

/**
 * Created by angelr on 09-Oct-15.
 */
public class AboutActivity extends Activity {

    private static final String ADDRESS = "46, str. Liaskovets, \n 1510, Sofia, Bulgaria";
    private static final String EMAIL = "runner.meter@gmail.com";
    private static final String FACEBOOK_PAGE = "https://www.facebook.com/sportmeter/";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.about_layout);

        TextView buildVersion = (TextView) findViewById(R.id.build_version);
        TextView companyAddress = (TextView) findViewById(R.id.company_address);
        TextView companyEmail = (TextView) findViewById(R.id.company_email);
        TextView companyFacebookPage = (TextView) findViewById(R.id.company_facebook_page);

        buildVersion.setText("Version: " + BuildConfig.VERSION_NAME + "." + String.valueOf(BuildConfig.VERSION_CODE));
        companyAddress.setText(ADDRESS);
        companyEmail.setText(EMAIL);
        companyFacebookPage.setText(FACEBOOK_PAGE);
    }
}
