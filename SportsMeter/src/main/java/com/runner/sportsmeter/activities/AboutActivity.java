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
    private TextView buildVersion, companyAddress, companyEmail, companyFacebookPage, googleLegalInfo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.l_about_layout);

        // todo google legal notices
        // this must be used for Services attribution text as part of a "Legal Notices" section in your application.
        // Including legal notices as an independent menu item, or as part of an "About" menu item, is recommended.
//        String legal = getOpenSourceSoftwareLicenseInfo(AboutActivity.this);
        buildVersion = (TextView) findViewById(R.id.build_version);
        googleLegalInfo = (TextView) findViewById(R.id.google_legal);
        companyAddress = (TextView) findViewById(R.id.company_address);
        companyEmail = (TextView) findViewById(R.id.company_email);
        companyFacebookPage = (TextView) findViewById(R.id.company_facebook_page);

        buildVersion.setText(getString(R.string.app_name) + " v. " + BuildConfig.VERSION_NAME);
        companyAddress.setText(ADDRESS);
//        googleLegalInfo.setText(legal);
        companyEmail.setText(EMAIL);
        companyFacebookPage.setText(FACEBOOK_PAGE);
    }
}
