package com.adclient.android.sdksampleapp;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;

import com.adclient.android.sdk.listeners.ClientAdListener;
import com.adclient.android.sdk.type.AdType;
import com.adclient.android.sdk.type.ParamsType;
import com.adclient.android.sdk.view.AbstractAdClientView;
import com.adclient.android.sdk.view.AdClientView;

import java.util.HashMap;

public class BannersActivity extends Activity {
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

    private AdClientView bannerViaXml;
    private AdClientView bannerViaCode;
    private LinearLayout bannerContainer;


    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_banners);
        bannerViaXml = findViewById(R.id.bannerView);
        if (bannerViaXml != null) {

            bannerViaXml.addClientAdListener(new ClientAdListener() {
                public void onReceivedAd(AbstractAdClientView adClientView) {
                    Log.d("SampleApp", "--> Ad received callback (bannerViaXml).");
                }

                public void onFailedToReceiveAd(AbstractAdClientView adClientView) {
                    Log.d("SampleApp", "--> Ad failed to be received callback (bannerViaXml).");
                }

                public void onClickedAd(AbstractAdClientView adClientView) {
                    Log.d("SampleApp", "-->  Ad has been clicked (bannerViaXml).");
                }

                @Override
                public void onLoadingAd(AbstractAdClientView adClientView, String message) {
                    Log.d("SampleApp", "--> Ad loaded callback (bannerViaXml).");
                }

                @Override
                public void onClosedAd(AbstractAdClientView adClientView) {
                    Log.d("SampleApp", "--> Ad closed callback (bannerViaXml).");
                }
            });
            bannerViaXml.load();
        }

        bannerContainer = findViewById(R.id.banner_container);

        Button buttonRetreiveBanner = findViewById(R.id.button_show_banner_code);
        buttonRetreiveBanner.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                loadBannerViaCode();
            }
        });
    }

    private void loadBannerViaCode() {
        if (bannerViaCode == null) {
            HashMap<ParamsType, Object> configuration = new HashMap<ParamsType, Object>();
            configuration.put(ParamsType.AD_PLACEMENT_KEY, "58296e9c9472044364aa6770d8409ede");
            configuration.put(ParamsType.ADTYPE, AdType.BANNER_320X50.toString());
            configuration.put(ParamsType.AD_SERVER_URL, "http://appservestar.com/");
            configuration.put(ParamsType.REFRESH_INTERVAL, 30);

            bannerViaCode = new AdClientView(this);
            bannerViaCode.setConfiguration(configuration);
            bannerViaCode.addClientAdListener(new ClientAdListener() {
                public void onReceivedAd(AbstractAdClientView adClientView) {
                    Log.d("SampleApp", "--> Ad received callback (bannerViaCode).");
                }

                public void onFailedToReceiveAd(AbstractAdClientView adClientView) {
                    Log.d("SampleApp", "--> Ad failed to be received callback (bannerViaCode).");
                }

                public void onClickedAd(AbstractAdClientView adClientView) {
                    Log.d("SampleApp", "-->  Ad has been clicked (bannerViaCode).");
                }

                @Override
                public void onLoadingAd(AbstractAdClientView adClientView, String message) {
                    Log.d("SampleApp", "--> Ad loaded callback (bannerViaCode).");
                }

                @Override
                public void onClosedAd(AbstractAdClientView adClientView) {
                    Log.d("SampleApp", "--> Ad closed callback (bannerViaCode).");
                }
            });

            bannerContainer.removeAllViews();
            bannerContainer.addView(bannerViaCode);

        }
        bannerViaCode.load();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (bannerViaXml != null) {
            bannerViaXml.resume();
        }

        if (bannerViaCode != null) {
            bannerViaCode.resume();
        }
    }

    @Override
    protected void onPause() {
        if (bannerViaXml != null) {
            bannerViaXml.pause();
        }
        if (bannerViaCode != null) {
            bannerViaCode.pause();
        }
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        if (bannerViaXml != null) {
            bannerViaXml.destroy();
        }
        if (bannerViaCode != null) {
            bannerViaCode.destroy();
        }
        super.onDestroy();
    }
}
