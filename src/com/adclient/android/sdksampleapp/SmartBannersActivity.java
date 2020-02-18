package com.adclient.android.sdksampleapp;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Spinner;

import com.adclient.android.sdk.nativeads.view.AdSize;
import com.adclient.android.sdk.nativeads.view.SmartBannerAdView;
import com.adclient.android.sdk.type.ParamsType;

import java.util.HashMap;

public class SmartBannersActivity extends Activity {
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

    private Spinner bannerFromCodeTypeSpinner;
    private Button buttonRetrieveBannerFromCode;
    private Button buttonShowBannerFromCode;
    private SmartBannerAdView bannerFromCode;
    private LinearLayout bannerContainer;

    private Button buttonRetreiveBannerFromLayout;
    private SmartBannerAdView bannerFromLayout;


    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_smart_banners);
        bannerContainer = findViewById(R.id.banner_container);
        bannerFromCodeTypeSpinner = findViewById(R.id.native_banner_from_code_type);
        bannerFromCodeTypeSpinner.setAdapter(new ArrayAdapter<>(this, R.layout.simple_spinner_item, AdSize.values()));
        bannerFromCodeTypeSpinner.setSelection(AdSize.SMART_BANNER.ordinal());
        buttonRetrieveBannerFromCode = findViewById(R.id.load_smart_banner_from_code);
        buttonRetrieveBannerFromCode.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                loadBannerFromCode();
            }
        });
        buttonShowBannerFromCode = findViewById(R.id.show_smart_banner_from_code);
        buttonShowBannerFromCode.setEnabled(false);
        buttonShowBannerFromCode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showBannerFromCode();
            }
        });

        bannerFromLayout = findViewById(R.id.smart_banner_ad_view);
        buttonRetreiveBannerFromLayout = findViewById(R.id.load_smart_banner_from_layout);
        buttonRetreiveBannerFromLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loadBannerFromLayout();

            }
        });
    }

    private void loadBannerFromCode() {
        if (bannerFromCode != null) {
            bannerFromCode.destroy();
            bannerFromCode = null;
        }
        HashMap<ParamsType, Object> configuration = new HashMap<ParamsType, Object>();
        configuration.put(ParamsType.AD_PLACEMENT_KEY, "6e00a5adf0fb82f4d2c5f6459aa082ad");
        configuration.put(ParamsType.AD_SERVER_URL, "http://appservestar.com/");
        configuration.put(ParamsType.REFRESH_INTERVAL, 30);

        bannerFromCode = new SmartBannerAdView(this);
        bannerFromCode.setConfiguration(configuration);
        bannerFromCode.setListener(new SmartBannerAdView.SmartBannerAdViewListener() {
            @Override
            public void onBannerLoading(SmartBannerAdView banner, boolean isLoaded, String message) {
                Log.d("SampleApp", "onBannerLoading from code: loaded = " + isLoaded);

                if (isLoaded) {
                    buttonShowBannerFromCode.setEnabled(true);
                }
            }

            @Override
            public void onBannerRefreshed(SmartBannerAdView banned, String message) {
                Log.d("SampleApp", "onBannerRefreshed from code");
            }

            @Override
            public void onBannerImpression(SmartBannerAdView banner) {
                Log.d("SampleApp", "onBannerImpression from code");
            }

            @Override
            public void onBannerFailed(SmartBannerAdView banner, String message) {
                Log.d("SampleApp", "onBannerFailed from code");
                buttonRetrieveBannerFromCode.setEnabled(true);
                buttonShowBannerFromCode.setEnabled(false);
            }

            @Override
            public void onBannerClicked(SmartBannerAdView banner) {
                Log.d("SampleApp", "onBannerClicked from code");
            }
        });

        buttonRetrieveBannerFromCode.setEnabled(false);
        buttonShowBannerFromCode.setEnabled(false);
        bannerContainer.removeAllViews();
        bannerFromCode.setAdSize((AdSize) bannerFromCodeTypeSpinner.getSelectedItem());
        bannerFromCode.load();
    }

    private void showBannerFromCode() {
        bannerContainer.addView(bannerFromCode);
        buttonRetrieveBannerFromCode.setEnabled(true);
        buttonShowBannerFromCode.setEnabled(false);
    }

    private void loadBannerFromLayout() {
        buttonRetreiveBannerFromLayout.setEnabled(false);
        if (bannerFromLayout != null) {

            bannerFromLayout.setListener(new SmartBannerAdView.SmartBannerAdViewListener() {
                @Override
                public void onBannerLoading(SmartBannerAdView banner, boolean isLoaded, String message) {
                    Log.d("SampleApp", "onBannerLoading from layout: loaded = " + isLoaded);
                    if (isLoaded) {
                        buttonRetreiveBannerFromLayout.setEnabled(true);
                    }
                }

                @Override
                public void onBannerRefreshed(SmartBannerAdView banned, String message) {
                    Log.d("SampleApp", "onBannerRefreshed  from layout");
                }

                @Override
                public void onBannerImpression(SmartBannerAdView banner) {
                    Log.d("SampleApp", "onBannerImpression  from layout");
                }

                @Override
                public void onBannerFailed(SmartBannerAdView banner, String message) {
                    Log.d("SampleApp", "onBannerFailed  from layout");
                    buttonRetreiveBannerFromLayout.setEnabled(true);
                }

                @Override
                public void onBannerClicked(SmartBannerAdView banner) {
                    Log.d("SampleApp", "onBannerClicked  from layout");
                }
            });
            bannerFromLayout.load();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (bannerFromLayout != null) {
            bannerFromLayout.resume();
        }

        if (bannerFromCode != null) {
            bannerFromCode.resume();
        }
    }

    @Override
    protected void onPause() {
        if (bannerFromLayout != null) {
            bannerFromLayout.pause();
        }
        if (bannerFromCode != null) {
            bannerFromCode.pause();
        }
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        if (bannerFromLayout != null) {
            bannerFromLayout.destroy();
        }
        if (bannerFromCode != null) {
            bannerFromCode.destroy();
        }
        super.onDestroy();
    }
}
