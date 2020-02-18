package com.adclient.android.sdksampleapp;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.adclient.android.sdk.nativeads.AdClientNativeAd;

public class NativeAdsFromApplicationActivity extends Activity {
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
    private LinearLayout adsContainerLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_native_ad_from_app);

        adsContainerLayout = findViewById(R.id.nativeAdContainer);

        if (((SampleApplication) getApplication()).isNativeAdLoaded()) {
            showNativeAd(((SampleApplication) getApplication()).getAdClientNativeAd());
        } else {
            Toast.makeText(this, "No ads", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

    }

    private void showNativeAd(AdClientNativeAd adClientNativeAd) {
        View view = adClientNativeAd.getView(this);
        adClientNativeAd.renderView(view);
        adClientNativeAd.registerImpressionsAndClicks(view);
        adsContainerLayout.addView(view);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (((SampleApplication) getApplication()).getAdClientNativeAd() != null) {
            ((SampleApplication) getApplication()).getAdClientNativeAd().resume();
        }
    }

    @Override
    protected void onPause() {
        if (((SampleApplication) getApplication()).getAdClientNativeAd() != null) {
            ((SampleApplication) getApplication()).getAdClientNativeAd().pause();
        }
        super.onPause();
    }

    @Override
    protected void onDestroy() {

        if (((SampleApplication) getApplication()).getAdClientNativeAd() != null) {
            ((SampleApplication) getApplication()).clearNativeAd();
        }

        if (adsContainerLayout != null)
            adsContainerLayout.removeAllViews();
        super.onDestroy();
    }

}
