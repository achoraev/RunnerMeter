package com.runner.sportsmeter;

import android.app.Activity;
import com.applovin.adview.AppLovinAdView;
import com.applovin.sdk.*;
import com.google.ads.AdSize;
import com.google.ads.mediation.MediationAdRequest;
import com.google.ads.mediation.customevent.CustomEventBanner;
import com.google.ads.mediation.customevent.CustomEventBannerListener;

/**
 * This class must be defined and referenced from AdMob's website for AdMob
 * Mediation.
 */

public class AdMobMediationBannerEvent implements CustomEventBanner
{

    private AppLovinAdView adView;

    /**
     * This method will be called by AdMob's Mediation through Custom Event
     * mechanism.
     */
    @Override
    public void requestBannerAd(final CustomEventBannerListener listener,
            final Activity activity,
            String label,
            String serverParameter,
            AdSize adSize,
            MediationAdRequest request,
            Object unused)
    {

        // Create AppLovin Ad View
        final AppLovinSdk sdk = AppLovinSdk.getInstance(activity);
        adView = new AppLovinAdView(sdk, AppLovinAdSize.BANNER, activity);
        adView.setAdClickListener(new AppLovinAdClickListener() {
            @Override
            public void adClicked(AppLovinAd arg0)
            {
                listener.onClick();
                listener.onPresentScreen();
                listener.onLeaveApplication();
            }
        });

        // fetch an ad
        sdk.getAdService().loadNextAd(AppLovinAdSize.BANNER, new AppLovinAdLoadListener() {

            @Override
            public void failedToReceiveAd(int errorCode)
            {
                listener.onFailedToReceiveAd();
            }

            @Override
            public void adReceived(AppLovinAd ad)
            {
                adView.renderAd(ad);
                listener.onReceivedAd(adView);
            }
        });
    }

    @Override
    public void destroy()
    {
        adView.destroy();
    }
}
