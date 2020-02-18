package com.adclient.android.sdksampleapp;

import android.content.Context;
import android.support.multidex.MultiDexApplication;

import com.adclient.android.sdk.nativeads.AdClientNativeAd;
import com.adclient.android.sdk.nativeads.AdClientNativeAdBinder;
import com.adclient.android.sdk.nativeads.AdClientNativeAdRenderer;
import com.adclient.android.sdk.nativeads.ClientNativeAdListener;
import com.adclient.android.sdk.type.ParamsType;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class SampleApplication extends MultiDexApplication {
    /**
     * Important: For AD_PLACEMENT_KEY and AD_SERVER_URL parameters, use the debug parameters below when testing your app with our library,
     * BUT don't forget to replace the values with the production values supplied to you.
     * bannerViaXml = 58296e9c9472044364aa6770d8409ede
     * interstitial = 0928de1630a1452b64eaab1813d3af64
     * native ad = ec5086312cf4959dcc54fe8a8ad15401
     * smartbanner = 6e00a5adf0fb82f4d2c5f6459aa082ad
     * rewarded = 34e3df8021d688c7c2fec5b065a0f2c4
     * AD_SERVER_URL = http://appservestar.com/
     * <p>
     * NOTE: IF YOU DECIDED TO TEST YOUR PRODUCT AD_PLACEMENT_KEY - BUILD THIS APP WITH YOUR PACKAGE NAME
     */
    private AdClientNativeAd adClientNativeAd;

    public void loadNativeAd(Context context, HashMap<ParamsType, Object> configuration, ClientNativeAdListener clientNativeAdListener) {

        if (adClientNativeAd != null && adClientNativeAd.isLoadingInProgress()) {
            return;
        }

        if (adClientNativeAd != null) {
            adClientNativeAd.destroy();
        }

        AdClientNativeAdBinder binder = new AdClientNativeAdBinder(R.layout.native_ad_layout);
        binder.bindView(AdClientNativeAdBinder.ViewType.TITLE_TEXT_VIEW, R.id.headlineView);
        binder.bindView(AdClientNativeAdBinder.ViewType.DESCRIPTION_TEXT_VIEW, R.id.descriptionView);
        binder.bindView(AdClientNativeAdBinder.ViewType.ICON_VIEW, R.id.iconView);
        binder.bindView(AdClientNativeAdBinder.ViewType.MEDIA_VIEW, R.id.mainImageView);
        binder.bindView(AdClientNativeAdBinder.ViewType.RATING_VIEW, R.id.ratingBar);
        binder.bindView(AdClientNativeAdBinder.ViewType.CALL_TO_ACTION_VIEW, R.id.callToActionButton);
        binder.bindView(AdClientNativeAdBinder.ViewType.SPONSORED_TEXT_VIEW, R.id.advertiserText);
        binder.bindView(AdClientNativeAdBinder.ViewType.PRIVACY_ICON_VIEW, R.id.sponsoredIcon);

        final List<Integer> clickItems = new ArrayList<>();
        clickItems.add(R.id.callToActionButton);
        binder.setClickableItems(clickItems);

        AdClientNativeAdRenderer renderer = new AdClientNativeAdRenderer(this, binder);
        adClientNativeAd = new AdClientNativeAd(context, configuration, renderer);
        adClientNativeAd.setClientNativeAdListener(clientNativeAdListener);
        adClientNativeAd.load();
    }

    public void clearNativeAd() {
        if (adClientNativeAd != null) {
            adClientNativeAd.destroy();
            adClientNativeAd = null;
        }
    }

    public AdClientNativeAd getAdClientNativeAd() {
        return adClientNativeAd;
    }

    public boolean isNativeAdLoaded() {
        return adClientNativeAd != null && !adClientNativeAd.isDestroyed() && !adClientNativeAd.isLoadingInProgress() && adClientNativeAd.isAdLoaded();
    }
}
