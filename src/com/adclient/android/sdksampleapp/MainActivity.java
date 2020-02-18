package com.adclient.android.sdksampleapp;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.epomapps.android.consent.ConsentForm;
import com.epomapps.android.consent.ConsentFormListener;
import com.epomapps.android.consent.ConsentInformationManager;
import com.epomapps.android.consent.OnConsentStatusUpdateListener;
import com.epomapps.android.consent.model.ConsentStatus;
import com.epomapps.android.consent.model.LocationStatus;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends Activity implements AdapterView.OnItemClickListener {

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

    private static final String TAG = "SampleApp";
    private static final int BANNERS_ID = 0;
    private static final int SMART_BANNER_VIEW = 1;
    private static final int INTERSTITIAL_ID = 2;
    private static final int REWARDED_ID = 3;
    private static final int NATIVE_ADS_ID_SIMPLE = 4;
    private static final int NATIVE_ADS_ID_MULTIPLE = 5;
    private static final int NATIVE_ADS_ID_APPLICATION = 6;
    private static final int NATIVE_ADS_ID_LIST_VIEW = 7;
    private static final int NATIVE_ADS_ID_RECYCLER_VIEW = 8;
    private ConsentInformationManager consentInformationManager;
    private ConsentForm consentForm;
    private ListView listView;

    private ListAdapter adapter;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        consentInformationManager = ConsentInformationManager.getInstance(this);

        if (savedInstanceState == null) {
            //first activity start

            consentInformationManager.requestConsentStatusUpdate(new OnConsentStatusUpdateListener() {

                @Override
                public void onConsentStateUpdated(ConsentStatus consentStatus, LocationStatus locationStatus) {
                    if (consentStatus == ConsentStatus.UNKNOWN && consentInformationManager.getLocationStatus() == LocationStatus.IN_EEA) {
                        loadConsentForm();
                    }
                }

                @Override
                public void onFailed(String message) {

                }
            });
        }

        listView = findViewById(R.id.list);

        adapter = new ListAdapter(this, createItems());
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(this);
    }

    private void loadConsentForm() {
        consentForm = ConsentForm.createBuilder(this)
                .withListener(new ConsentFormListener() {
                    @Override
                    public void onConsentFormLoaded() {
                        Log.d(TAG, "SampleApp onConsentFormLoaded ", null);
                        consentForm.show();
                    }

                    @Override
                    public void onConsentFormError(String reason) {
                        Log.d(TAG, "SampleApp onConsentFormError: " + reason, null);
                    }

                    @Override
                    public void onConsentFormOpened() {
                        Log.d(TAG, "SampleApp onConsentFormOpened ", null);
                    }

                    @Override
                    public void onConsentFormClosed(ConsentStatus consentStatus, Boolean userPrefersAdFree) {
                        Log.d(TAG, "SampleApp onConsentFormClosed ", null);

                    }
                })
                .withPayOption()
                .build();

        consentForm.load();
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        switch (adapter.getItem(position).id) {
            case BANNERS_ID:
                startActivity(new Intent(this, BannersActivity.class));
                break;
            case SMART_BANNER_VIEW:
                startActivity(new Intent(this, SmartBannersActivity.class));
                break;
            case INTERSTITIAL_ID:
                startActivity(new Intent(this, InterstitialActivity.class));
                break;
            case REWARDED_ID:
                startActivity(new Intent(this, RewardedActivity.class));
                break;
            case NATIVE_ADS_ID_SIMPLE:
                startActivity(new Intent(this, NativeAdsSimpleActivity.class));
                break;
            case NATIVE_ADS_ID_MULTIPLE:
                startActivity(new Intent(this, MultipleNativeAdsScrollViewActivity.class));
                break;
            case NATIVE_ADS_ID_APPLICATION:
                startActivity(new Intent(this, NativeAdsFromApplicationLoaderActivity.class));
                break;
            case NATIVE_ADS_ID_LIST_VIEW:
                startActivity(new Intent(this, NativeAdsListActivity.class));
                break;
            case NATIVE_ADS_ID_RECYCLER_VIEW:
                startActivity(new Intent(this, NativeAdsRecyclerViewActivity.class));
                break;

        }
    }

    private List<Item> createItems() {
        List<Item> items = new ArrayList<>();

        items.add(new Item(BANNERS_ID, getResources().getString(R.string.title_banners), getResources().getString(R.string.desc_banner)));
        items.add(new Item(SMART_BANNER_VIEW, getResources().getString(R.string.title_banners), getResources().getString(R.string.desc_smart_banner_view)));
        items.add(new Item(INTERSTITIAL_ID, getResources().getString(R.string.title_interstitial), getResources().getString(R.string.desc_interstitial)));
        items.add(new Item(REWARDED_ID, getResources().getString(R.string.title_rewarded), getResources().getString(R.string.desc_interstitial)));
        items.add(new Item(NATIVE_ADS_ID_SIMPLE, getResources().getString(R.string.title_native_ads), getResources().getString(R.string.desc_native_ads_simple)));
        items.add(new Item(NATIVE_ADS_ID_MULTIPLE, getResources().getString(R.string.title_native_ads), getResources().getString(R.string.desc_native_ads_multiple_instances)));
        items.add(new Item(NATIVE_ADS_ID_APPLICATION, getResources().getString(R.string.title_native_ads), getResources().getString(R.string.desc_native_ads_application)));
        items.add(new Item(NATIVE_ADS_ID_LIST_VIEW, getResources().getString(R.string.title_native_ads), getResources().getString(R.string.desc_native_ads_list_view)));
        items.add(new Item(NATIVE_ADS_ID_RECYCLER_VIEW, getResources().getString(R.string.title_native_ads), getResources().getString(R.string.desc_native_ads_recycler_view)));


        return items;
    }

    public class Item {
        public int id;
        public String title;
        public String desc;

        public Item(int id, String title, String desc) {
            this.id = id;
            this.title = title;
            this.desc = desc;
        }
    }

    public class ListAdapter extends BaseAdapter {

        private Context context;
        private List<Item> items;

        public ListAdapter(Context context, List<Item> items) {
            this.context = context;
            this.items = items;
        }

        @Override
        public int getCount() {
            if (items == null) {
                return 0;
            }
            return items.size();
        }

        @Override
        public Item getItem(int position) {
            return items.get(position);
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }


        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder viewHolder;

            if (convertView == null) {
                LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                convertView = inflater.inflate(R.layout.view_list_item, parent, false);
                viewHolder = new ViewHolder();
                viewHolder.titleTextView = convertView.findViewById(R.id.title_text_view);
                viewHolder.descTextView = convertView.findViewById(R.id.desc_text_view);
                convertView.setTag(viewHolder);
            } else {
                viewHolder = (ViewHolder) convertView.getTag();
            }

            viewHolder.titleTextView.setText(getItem(position).title);
            viewHolder.descTextView.setText(getItem(position).desc);

            return convertView;
        }

        class ViewHolder {
            ImageView imgView;
            TextView titleTextView;
            TextView descTextView;
        }
    }
}
