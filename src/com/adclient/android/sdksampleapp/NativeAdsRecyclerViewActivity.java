package com.adclient.android.sdksampleapp;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.adclient.android.sdk.nativeads.AdClientNativeAdBinder;
import com.adclient.android.sdk.nativeads.AdClientNativeAdPositioning;
import com.adclient.android.sdk.nativeads.AdClientNativeAdRenderer;
import com.adclient.android.sdk.type.AdType;
import com.adclient.android.sdk.type.ParamsType;
import com.adclient.android.sdksampleapp.adapter.AdClientNativeAdRecyclerViewAdapter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class NativeAdsRecyclerViewActivity extends Activity {
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

    private RecyclerView recyclerView;
    private LinearLayoutManager linearLayoutManager;
    private AdClientNativeAdRecyclerViewAdapter clientNativeAdRecyclerViewAdapter;
    private RecyclerViewAdapter adapter;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_native_ads_recyclerview);
        recyclerView = findViewById(R.id.list);
        linearLayoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(linearLayoutManager);

        adapter = new RecyclerViewAdapter(this, createItems(100));
        clientNativeAdRecyclerViewAdapter = new AdClientNativeAdRecyclerViewAdapter(this, adapter, AdClientNativeAdPositioning.clientPositioning().addFixedPosition(2).enableRepeatingPositions(10));

        recyclerView.setAdapter(clientNativeAdRecyclerViewAdapter);

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
        clientNativeAdRecyclerViewAdapter.loadAds(configuration, new AdClientNativeAdRenderer(this, binder));
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (clientNativeAdRecyclerViewAdapter != null) {
            clientNativeAdRecyclerViewAdapter.resume();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (clientNativeAdRecyclerViewAdapter != null) {
            clientNativeAdRecyclerViewAdapter.pause();
        }
    }

    @Override
    protected void onDestroy() {
        if (clientNativeAdRecyclerViewAdapter != null) {
            clientNativeAdRecyclerViewAdapter.destroy();
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

    private static class RecyclerViewAdapter extends RecyclerView.Adapter<ViewHolder> {

        private static final int ITEM_TYPE_0 = 0;
        private static final int ITEM_TYPE_1 = 1;
        private static final int ITEM_TYPES_COUNT = 2;
        private List<Item> items;

        public RecyclerViewAdapter(Context context, List<Item> items) {
            this.items = items;
        }

        public static int getItemTypesCount() {
            return ITEM_TYPES_COUNT;
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            final View itemView = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.view_list_item, parent, false);

            return new ViewHolder(itemView);
        }

        @Override
        public void onBindViewHolder(ViewHolder holder, int position) {
            holder.titleTextView.setText(items.get(position).title);
            holder.descTextView.setText(items.get(position).desc);

            switch (getItemViewType(position)) {
                case ITEM_TYPE_0:
                    holder.titleTextView.setTextColor(Color.WHITE);
                    holder.descTextView.setTextColor(Color.WHITE);
                    break;
                case ITEM_TYPE_1:
                    holder.titleTextView.setTextColor(Color.GREEN);
                    holder.descTextView.setTextColor(Color.GREEN);
                    break;
            }
        }

        @Override
        public int getItemCount() {
            if (items == null) {
                return 0;
            }
            return items.size();
        }

        @Override
        public int getItemViewType(int position) {
            int mod = position % 2;
            return mod > 0 ? ITEM_TYPE_0 : ITEM_TYPE_1;
        }
    }

    private static class ViewHolder extends RecyclerView.ViewHolder {
        TextView titleTextView;
        TextView descTextView;

        public ViewHolder(View itemView) {
            super(itemView);
            titleTextView = itemView.findViewById(R.id.title_text_view);
            descTextView = itemView.findViewById(R.id.desc_text_view);
        }
    }

    public class Item {
        public String title;
        public String desc;
    }


}
