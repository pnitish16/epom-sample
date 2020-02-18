package com.adclient.android.sdksampleapp;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.adclient.android.sdk.listeners.ClientAdListener;
import com.adclient.android.sdk.type.AdType;
import com.adclient.android.sdk.type.ParamsType;
import com.adclient.android.sdk.view.AbstractAdClientView;
import com.adclient.android.sdk.view.AdClientInterstitial;

import java.util.HashMap;

public class InterstitialActivity extends Activity {
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
    private String PLACEMENT_KEY = "0928de1630a1452b64eaab1813d3af64";
    private Button buttonRetreiveInterstitialAd;
    private Button buttonShowInterstitialAd;

    private AdClientInterstitial interstitial;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_interstitials);

        buttonRetreiveInterstitialAd = findViewById(R.id.button_retrieve_interstitial);
        buttonRetreiveInterstitialAd.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {

                buttonShowInterstitialAd.setEnabled(false);
                buttonRetreiveInterstitialAd.setEnabled(false);
                loadInterstitial();
            }
        });

        buttonShowInterstitialAd = findViewById(R.id.button_show_interstitial);
        buttonShowInterstitialAd.setEnabled(false);
        buttonShowInterstitialAd.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                if (interstitial != null && interstitial.isAdLoaded()) {
                    interstitial.show();
                    buttonShowInterstitialAd.setEnabled(false);
                    buttonRetreiveInterstitialAd.setEnabled(true);
                }
            }
        });
    }

    private void loadInterstitial() {
        if (interstitial != null) {
            interstitial.destroy();
            interstitial = null;
        }
        interstitial = new AdClientInterstitial(this);
        HashMap<ParamsType, Object> configuration = new HashMap<ParamsType, Object>();
        configuration.put(ParamsType.AD_PLACEMENT_KEY, PLACEMENT_KEY);
        configuration.put(ParamsType.ADTYPE, AdType.INTERSTITIAL.toString());
        configuration.put(ParamsType.AD_SERVER_URL, "http://appservestar.com/");
        interstitial.setConfiguration(configuration);

        interstitial.addClientAdListener(new ClientAdListener() {

            @Override
            public void onReceivedAd(AbstractAdClientView adClientView) {
                Log.d("SampleApp", "--> Ad received callback (interstitial).");
            }

            @Override
            public void onFailedToReceiveAd(AbstractAdClientView adClientView) {
                buttonShowInterstitialAd.setEnabled(false);
                buttonRetreiveInterstitialAd.setEnabled(true);
                Log.d("SampleApp", "--> Ad failed to be received callback (interstitial).");
            }

            public void onClickedAd(AbstractAdClientView adClientView) {
                Log.d("SampleApp", "--> Ad show ad screen callback (interstitial).");
            }

            @Override
            public void onLoadingAd(AbstractAdClientView adClientView, String message) {
                if (interstitial.isAdLoaded()) {
                    Log.d("SampleApp", "--> Ad loaded (interstitial).");
                    buttonShowInterstitialAd.setEnabled(true);
                    buttonRetreiveInterstitialAd.setEnabled(false);
                } else {
                    Log.d("SampleApp", "--> Ad not loaded (interstitial).");
                }
            }

            @Override
            public void onClosedAd(AbstractAdClientView adClientView) {
                Log.d("SampleApp", "--> Ad closed callback (interstitial).");
            }
        });
        interstitial.load();
    }

    @Override
    protected void onResume() {
        super.onResume();

        if (interstitial != null) {
            interstitial.resume();
        }
    }

    @Override
    protected void onPause() {
        if (interstitial != null) {
            interstitial.pause();
        }
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        if (interstitial != null) {
            interstitial.destroy();
        }
        super.onDestroy();
    }
}
