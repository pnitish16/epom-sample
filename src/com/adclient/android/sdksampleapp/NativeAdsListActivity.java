package com.adclient.android.sdksampleapp;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.adclient.android.sdk.nativeads.AdClientNativeAdBinder;
import com.adclient.android.sdk.nativeads.AdClientNativeAdPositioning;
import com.adclient.android.sdk.nativeads.AdClientNativeAdRenderer;
import com.adclient.android.sdk.type.AdType;
import com.adclient.android.sdk.type.ParamsType;
import com.adclient.android.sdksampleapp.adapter.AdClientNativeAdBaseAdapter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class NativeAdsListActivity extends Activity {
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

    private ListView listView;
    private ListAdapter adapter;
    private AdClientNativeAdBaseAdapter clientNativeAdBaseAdapter;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_native_ads_list_view);

        listView = findViewById(R.id.list);
        adapter = new ListAdapter(this, createItems(120));

        AdClientNativeAdPositioning.ClientPositioning positioning = AdClientNativeAdPositioning.clientPositioning()
                .addFixedPosition(5)
                .addFixedPosition(30)
                .enableRepeatingPositions(10);
        clientNativeAdBaseAdapter = new AdClientNativeAdBaseAdapter(this, adapter, positioning);

        listView.setAdapter(clientNativeAdBaseAdapter);

        HashMap<ParamsType, Object> configuration = new HashMap<>();
        configuration.put(ParamsType.AD_PLACEMENT_KEY, "ec5086312cf4959dcc54fe8a8ad15401");
        configuration.put(ParamsType.ADTYPE, AdType.NATIVE_AD.toString());
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
        clientNativeAdBaseAdapter.loadAds(configuration, new AdClientNativeAdRenderer(this, binder));
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (clientNativeAdBaseAdapter != null) {
            clientNativeAdBaseAdapter.resume();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (clientNativeAdBaseAdapter != null) {
            clientNativeAdBaseAdapter.pause();
        }
    }

    @Override
    protected void onDestroy() {

        if (clientNativeAdBaseAdapter != null) {
            clientNativeAdBaseAdapter.destroy();
        }
        super.onDestroy();
    }

    private List<Item> createItems(int count) {
        if (count < 0) {
            return null;
        }
        List<Item> items = new ArrayList<>(count);
        for (int i = 0; i < count; i++) {

            Item item = new Item();
            item.title = "Item Title " + i;
            item.desc = "Item Description " + i;

            items.add(item);
        }
        return items;
    }

    public class Item {
        public String title;
        public String desc;
    }

    public class ListAdapter extends BaseAdapter {

        private static final int ITEM_TYPE_0 = 0;
        private static final int ITEM_TYPE_1 = 1;
        private static final int ITEM_TYPES_COUNT = 2;

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
        public int getItemViewType(int position) {
            int mod = position % 2;
            return mod > 0 ? ITEM_TYPE_0 : ITEM_TYPE_1;
        }

        @Override
        public int getViewTypeCount() {
            return ITEM_TYPES_COUNT;
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

            switch (getItemViewType(position)) {
                case ITEM_TYPE_0:
                    viewHolder.titleTextView.setTextColor(Color.WHITE);
                    viewHolder.descTextView.setTextColor(Color.WHITE);
                    break;
                case ITEM_TYPE_1:
                    viewHolder.titleTextView.setTextColor(Color.GREEN);
                    viewHolder.descTextView.setTextColor(Color.GREEN);
                    break;
            }

            return convertView;
        }

        class ViewHolder {
            TextView titleTextView;
            TextView descTextView;
        }
    }


}
