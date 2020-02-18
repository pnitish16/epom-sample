package com.adclient.android.sdksampleapp;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.adclient.android.sdk.listeners.ClientRewardedAdListener;
import com.adclient.android.sdk.type.AdType;
import com.adclient.android.sdk.type.ParamsType;
import com.adclient.android.sdk.view.AbstractAdClientView;
import com.adclient.android.sdk.view.AdClientRewarded;

import java.util.HashMap;

public class RewardedActivity extends Activity {
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

    private String PLACEMENT_KEY = "34e3df8021d688c7c2fec5b065a0f2c4";
    private Button buttonRetreiveRewardedAd;
    private Button buttonShowRewardedAd;

    private AdClientRewarded adClientRewarded;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rewarded);

        buttonRetreiveRewardedAd = findViewById(R.id.button_retrieve_rewarded);
        buttonRetreiveRewardedAd.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {

                buttonShowRewardedAd.setEnabled(false);
                buttonRetreiveRewardedAd.setEnabled(false);

                loadRewarded();
            }
        });

        buttonShowRewardedAd = findViewById(R.id.button_show_rewarded);
        buttonShowRewardedAd.setEnabled(false);
        buttonShowRewardedAd.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                if (adClientRewarded != null && adClientRewarded.isAdLoaded()) {
                    adClientRewarded.show();
                    buttonShowRewardedAd.setEnabled(false);
                    buttonRetreiveRewardedAd.setEnabled(true);
                }
            }
        });
    }

    private void loadRewarded() {
        if (adClientRewarded != null) {
            adClientRewarded.destroy();
            adClientRewarded = null;
        }
        adClientRewarded = new AdClientRewarded(this);
        HashMap<ParamsType, Object> configuration = new HashMap<ParamsType, Object>();
        configuration.put(ParamsType.AD_PLACEMENT_KEY, PLACEMENT_KEY);
        configuration.put(ParamsType.ADTYPE, AdType.REWARDED.toString());
        configuration.put(ParamsType.AD_SERVER_URL, "http://appservestar.com/");
        adClientRewarded.setConfiguration(configuration);

        adClientRewarded.addClientAdListener(new ClientRewardedAdListener() {

            @Override
            public void onReceivedAd(AbstractAdClientView adClientView) {
                Log.d("SampleApp", "--> Ad received callback (adClientRewarded).");
            }

            @Override
            public void onFailedToReceiveAd(AbstractAdClientView adClientView) {
                buttonShowRewardedAd.setEnabled(false);
                buttonRetreiveRewardedAd.setEnabled(true);
                Log.d("SampleApp", "--> Ad failed to be received callback (adClientRewarded).");
            }

            public void onClickedAd(AbstractAdClientView adClientView) {
                Log.d("SampleApp", "--> Ad show ad screen callback (adClientRewarded).");
            }

            @Override
            public void onLoadingAd(AbstractAdClientView adClientView, String message) {
                if (adClientRewarded.isAdLoaded()) {
                    Log.d("SampleApp", "--> Ad loaded (adClientRewarded).");
                    buttonShowRewardedAd.setEnabled(true);
                    buttonRetreiveRewardedAd.setEnabled(false);
                } else {
                    Log.d("SampleApp", "--> Ad not loaded (adClientRewarded).");
                }
            }

            @Override
            public void onClosedAd(AbstractAdClientView adClientView) {
                Log.d("SampleApp", "--> Ad closed callback (adClientRewarded).");
            }

            @Override
            public void onRewarded() {
                Log.d("SampleApp", "--> Ad rewarded callback (adClientRewarded).");
                Toast.makeText(RewardedActivity.this, "Ad rewarded", Toast.LENGTH_SHORT).show();
            }
        });

        adClientRewarded.load();
    }

    @Override
    protected void onResume() {
        super.onResume();

        if (adClientRewarded != null) {
            adClientRewarded.resume();
        }
    }

    @Override
    protected void onPause() {
        if (adClientRewarded != null) {
            adClientRewarded.pause();
        }
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        if (adClientRewarded != null) {
            adClientRewarded.destroy();
        }
        super.onDestroy();
    }
}
