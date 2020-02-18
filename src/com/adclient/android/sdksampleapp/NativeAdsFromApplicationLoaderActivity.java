package com.adclient.android.sdksampleapp;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.TextView;

import com.adclient.android.sdk.nativeads.AdClientNativeAd;
import com.adclient.android.sdk.nativeads.ClientNativeAdListener;
import com.adclient.android.sdk.nativeads.RefreshType;
import com.adclient.android.sdk.type.AdType;
import com.adclient.android.sdk.type.ParamsType;

import java.util.HashMap;

public class NativeAdsFromApplicationLoaderActivity extends Activity implements ClientNativeAdListener {
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
    private Button showButton;
    private CheckBox contextCheckBox;
    private TextView statusTextView;

    private boolean isLoadingInProgress = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_native_ad_from_app_loader);

        statusTextView = findViewById(R.id.status_text);
        retrieveNativeAdButton = findViewById(R.id.retrieve_native_ad);
        showButton = findViewById(R.id.show_native_ads);
        showButton.setEnabled(false);
        contextCheckBox = findViewById(R.id.context_type);

        retrieveNativeAdButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!isLoadingInProgress) {
                    loadNativeAd();
                }
            }
        });

        showButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showNativeAd();
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
                showButton.setEnabled(false);
                retrieveNativeAdButton.setEnabled(true);
                contextCheckBox.setEnabled(true);

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
                    showButton.setEnabled(true);
                    contextCheckBox.setEnabled(true);
                    isLoadingInProgress = false;
                    retrieveNativeAdButton.setEnabled(true);
                }
            }
        });


    }

    @Override
    public void onRefreshedAd(AdClientNativeAd adClientNativeAd, RefreshType refreshType, String message, boolean callbackFromUIThread) {
        Log.d("SampleApp", "onRefreshed");
    }

    private void loadNativeAd() {
        statusTextView.setText("Loading...");
        isLoadingInProgress = true;
        retrieveNativeAdButton.setEnabled(false);

        HashMap<ParamsType, Object> configuration = new HashMap<>();
        configuration.put(ParamsType.AD_PLACEMENT_KEY, "ec5086312cf4959dcc54fe8a8ad15401");
        configuration.put(ParamsType.ADTYPE, AdType.NATIVE_AD.toString());
        configuration.put(ParamsType.AD_SERVER_URL, "http://appservestar.com/");

        Context context = contextCheckBox.isChecked() ? getApplicationContext() : this;

        ((SampleApplication) getApplication()).loadNativeAd(context, configuration, this);
        retrieveNativeAdButton.setEnabled(false);
        showButton.setEnabled(false);
        contextCheckBox.setEnabled(false);
    }

    private void showNativeAd() {
        retrieveNativeAdButton.setEnabled(true);
        showButton.setEnabled(false);
        startActivity(new Intent(this, NativeAdsFromApplicationActivity.class));
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (((SampleApplication) getApplication()).getAdClientNativeAd() != null)
            ((SampleApplication) getApplication()).getAdClientNativeAd().resume();
    }

    @Override
    protected void onPause() {
        if (((SampleApplication) getApplication()).getAdClientNativeAd() != null)
            ((SampleApplication) getApplication()).getAdClientNativeAd().pause();
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        if (((SampleApplication) getApplication()).getAdClientNativeAd() != null)
            ((SampleApplication) getApplication()).getAdClientNativeAd().destroy();

        super.onDestroy();
    }

}
