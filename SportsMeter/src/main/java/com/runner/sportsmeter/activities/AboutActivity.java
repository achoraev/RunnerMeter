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

    private final String ADDRESS = "46, str. Liaskovets, \n 1510, Sofia, Bulgaria";
    private final String EMAIL = "runner.meter@gmail.com";
    private final String FACEBOOK_PAGE = "https://www.facebook.com/sportmeter/";
    TextView buildVersion, companyAddress, companyEmail, companyFacebookPage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.about_layout);

        buildVersion = (TextView) findViewById(R.id.build_version);
        companyAddress = (TextView) findViewById(R.id.company_address);
        companyEmail = (TextView) findViewById(R.id.company_email);
        companyFacebookPage = (TextView) findViewById(R.id.company_facebook_page);

        buildVersion.setText(getString(R.string.app_name) + " v. " + BuildConfig.VERSION_NAME);
        companyAddress.setText(ADDRESS);
        companyEmail.setText(EMAIL);
        companyFacebookPage.setText(FACEBOOK_PAGE);
    }
}
