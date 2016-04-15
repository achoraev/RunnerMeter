package com.runner.sportsmeter.activities;

import android.app.Activity;
import android.os.Bundle;
import android.widget.TextView;
import com.runner.sportsmeter.BuildConfig;
import com.runner.sportsmeter.R;
import com.runner.sportsmeter.common.Constants;

/**
 * Created by angelr on 09-Oct-15.
 */
public class AboutActivity extends Activity {

    private TextView buildVersion, companyAddress, companyEmail, companyFacebookPage, companyWebsite,  googleLegalInfo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.l_about_layout);

        // todo google legal notices
        // this must be used for Services attribution text as part of a "Legal Notices" section in your application.
        // Including legal notices as an independent menu item, or as part of an "About" menu item, is recommended.
        buildVersion = (TextView) findViewById(R.id.build_version);
        googleLegalInfo = (TextView) findViewById(R.id.google_legal);
        companyAddress = (TextView) findViewById(R.id.company_address);
        companyEmail = (TextView) findViewById(R.id.company_email);
        companyWebsite = (TextView) findViewById(R.id.company_website);
        companyFacebookPage = (TextView) findViewById(R.id.company_facebook_page);

        String appVersion = getString(R.string.app_name) + " v. " + BuildConfig.VERSION_NAME;
        buildVersion.setText(appVersion);
        companyAddress.setText(Constants.ADDRESS);
        companyEmail.setText(Constants.EMAIL);
        companyWebsite.setText(Constants.WEBSITE);
        companyFacebookPage.setText(Constants.FACEBOOK_PAGE);
    }
}
