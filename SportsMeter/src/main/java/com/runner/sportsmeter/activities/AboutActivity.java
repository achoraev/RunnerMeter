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

    private final String ADDRESS = getString(R.string.app_address);
    private final String EMAIL = getString(R.string.app_email);
    private final String FACEBOOK_PAGE = getString(R.string.app_facebook_page);
    TextView buildVersion, companyAddress, companyEmail, companyFacebookPage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.about_layout);

        buildVersion = (TextView) findViewById(R.id.build_version);
        companyAddress = (TextView) findViewById(R.id.company_address);
        companyEmail = (TextView) findViewById(R.id.company_email);
        companyFacebookPage = (TextView) findViewById(R.id.company_facebook_page);

        buildVersion.setText(getString(R.string.app_version) + BuildConfig.VERSION_NAME);
        companyAddress.setText(ADDRESS);
        companyEmail.setText(EMAIL);
        companyFacebookPage.setText(FACEBOOK_PAGE);
    }
}
