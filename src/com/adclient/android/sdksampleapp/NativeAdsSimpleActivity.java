package com.adclient.android.sdksampleapp;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.adclient.android.sdk.nativeads.AdClientNativeAd;
import com.adclient.android.sdk.nativeads.AdClientNativeAdBinder;
import com.adclient.android.sdk.nativeads.AdClientNativeAdRenderer;
import com.adclient.android.sdk.nativeads.ClientNativeAdImageListener;
import com.adclient.android.sdk.nativeads.ClientNativeAdListener;
import com.adclient.android.sdk.nativeads.ImageDisplayError;
import com.adclient.android.sdk.nativeads.RefreshType;
import com.adclient.android.sdk.type.AdType;
import com.adclient.android.sdk.type.ParamsType;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class NativeAdsSimpleActivity extends Activity implements ClientNativeAdListener {
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

    private Button retrieveNativeAdButton;
    private Button clearButton;
    private TextView statusTextView;
    private LinearLayout adsContainerLayout;
    private AdClientNativeAd adClientNativeAd;
    private boolean isLoadingInProgress = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_native_ads_simple);

        statusTextView = findViewById(R.id.status_text);
        adsContainerLayout = findViewById(R.id.ads_container);
        retrieveNativeAdButton = findViewById(R.id.retrieve_native_ad);
        clearButton = findViewById(R.id.clear_native_ads);

        retrieveNativeAdButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!isLoadingInProgress) {
                    loadNativeAd();
                }
            }
        });

        clearButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                adsContainerLayout.removeAllViews();
            }
        });
    }

    @Override
    public void onImpressionAd(AdClientNativeAd adClientNativeAd, boolean callbackFromUIThread) {
        AdClientNativeAd.executeOnUiThread(this, new Runnable() {
            @Override
            public void run() {
                Log.d("SampleApp", "onImpressionAd");
                isLoadingInProgress = false;
                retrieveNativeAdButton.setEnabled(true);
                statusTextView.setText("Showed");
            }
        });
    }

    @Override
    public void onFailedToReceiveAd(AdClientNativeAd adClientNativeAd, String message, boolean callbackFromUIThread) {

        AdClientNativeAd.executeOnUiThread(this, new Runnable() {
            @Override
            public void run() {
                Log.d("SampleApp", "onFailedToReceiveAd");
                isLoadingInProgress = false;
                retrieveNativeAdButton.setEnabled(true);
                statusTextView.setText("Error");
            }
        });
    }

    @Override
    public void onClickedAd(AdClientNativeAd adClientNativeAd, boolean callbackFromUIThread) {
        Log.d("SampleApp", "onClickedAd");
    }

    @Override
    public void onLoadingAd(final AdClientNativeAd adClientNativeAd, final boolean isLoaded, String message, boolean callbackFromUIThread) {
        AdClientNativeAd.executeOnUiThread(this, new Runnable() {
            @Override
            public void run() {
                Log.d("SampleApp", "onLoadingAd: loaded = " + isLoaded);
                if (isLoaded) {
                    statusTextView.setText("Loadded");
                    showNativeAd(adClientNativeAd);
                    isLoadingInProgress = false;
                    retrieveNativeAdButton.setEnabled(true);
                }
            }
        });
    }

    @Override
    public void onRefreshedAd(AdClientNativeAd adClientNativeAd, RefreshType refreshType, String message, boolean callbackFromUIThread) {
        Log.d("SampleApp", "onRefreshedAd");
    }

    private void loadNativeAd() {
        if (adsContainerLayout != null)
            adsContainerLayout.removeAllViews();

        if (adClientNativeAd != null) {
            adClientNativeAd.destroy();
        }

        statusTextView.setText("Loading...");
        isLoadingInProgress = true;
        retrieveNativeAdButton.setEnabled(false);

        HashMap<ParamsType, Object> configuration = new HashMap<>();
        configuration.put(ParamsType.AD_PLACEMENT_KEY, "ec5086312cf4959dcc54fe8a8ad15401");
        configuration.put(ParamsType.ADTYPE, AdType.NATIVE_AD.toString());
        configuration.put(ParamsType.REFRESH_INTERVAL, 30);
        configuration.put(ParamsType.AD_SERVER_URL, "http://appservestar.com/");

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
        renderer.setClientNativeAdImageListener(new ClientNativeAdImageListener() {
            @Override
            public void onShowImageFailed(ImageView imageView, String uri, ImageDisplayError error) {
                if (imageView != null)
                    AdClientNativeAd.displayImage(imageView, uri, this);
            }

            @Override
            public void onNeedToShowImage(ImageView imageView, String uri) {
                if (imageView != null)
                    AdClientNativeAd.displayImage(imageView, uri, this);
            }

            @Override
            public void onShowImageSuccess(ImageView imageView, String uri) {

            }
        });

        adClientNativeAd = new AdClientNativeAd(this, configuration, renderer);
        adClientNativeAd.setClientNativeAdListener(this);
        adClientNativeAd.load();
    }

    private void showNativeAd(AdClientNativeAd ad) {
        View view = ad.getView(this);
        adClientNativeAd.renderView(view);
        adClientNativeAd.registerImpressionsAndClicks(view);
        adsContainerLayout.addView(view);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (adClientNativeAd != null)
            adClientNativeAd.resume();
    }

    @Override
    protected void onPause() {
        if (adClientNativeAd != null)
            adClientNativeAd.pause();
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        if (adClientNativeAd != null)
            adClientNativeAd.destroy();

        if (adsContainerLayout != null)
            adsContainerLayout.removeAllViews();
        super.onDestroy();
    }
}
