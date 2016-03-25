package com.runner.sportsmeter.activities;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TabLayout;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;
import com.runner.sportsmeter.R;
import com.runner.sportsmeter.common.ViewPagerAdapter;
import com.runner.sportsmeter.fragments.HelpFragmentFour;
import com.runner.sportsmeter.fragments.HelpFragmentOne;
import com.runner.sportsmeter.fragments.HelpFragmentThree;
import com.runner.sportsmeter.fragments.HelpFragmentTwo;

/**
 * Created by angelr on 09-Oct-15.
 */
public class HelpActivity extends AppCompatActivity implements View.OnClickListener {

    private DrawerLayout drawerLayout;
    private FloatingActionButton fab;
    private TabLayout tabLayout;
    private ViewPager viewPager;
//    private AdView mAdView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.l_help_layout);
        initializeViewPager();
        setupTablayout();
        setupFab();

//        mAdView = (AdView) findViewById(R.id.adViewHelp);
//        new Utility().setupAdds(mAdView, this);
    }

    private void initializeViewPager() {
        viewPager = (ViewPager) findViewById(R.id.view_pager);
        setupViewPager(viewPager);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                if (drawerLayout != null)
                    drawerLayout.openDrawer(GravityCompat.START);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void setupViewPager(ViewPager viewPager) {
        ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager());
        adapter.addFragment(new HelpFragmentOne(), getString(R.string.tab_how_to_login));
        adapter.addFragment(new HelpFragmentTwo(), getString(R.string.tab_how_start_page));
        adapter.addFragment(new HelpFragmentThree(), getString(R.string.tab_main_page));
        adapter.addFragment(new HelpFragmentFour(), getString(R.string.tab_save_page));
        viewPager.setAdapter(adapter);
    }

    private void setupTablayout() {
        tabLayout = (TabLayout) findViewById(R.id.tabLayout);
        tabLayout.setupWithViewPager(viewPager);
    }

    private void setupFab() {
        fab = (FloatingActionButton) findViewById(R.id.fab);
        if (fab != null) {
            fab.setOnClickListener(this);
        }
    }

    @Override
    public void onClick(View view) {

        if (view.getId() == R.id.fab) {
            Snackbar.make(findViewById(R.id.coordinatorLayout), R.string.skip_how_start, Snackbar.LENGTH_LONG)
                    .setAction(R.string.skip, new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            finish();
                        }
                    })
                    .show();
        }
    }
}
