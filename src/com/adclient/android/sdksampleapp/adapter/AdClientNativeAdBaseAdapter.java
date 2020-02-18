package com.adclient.android.sdksampleapp.adapter;

import android.app.Activity;
import android.database.DataSetObserver;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Adapter;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ListAdapter;
import android.widget.ListView;

import com.adclient.android.sdk.nativeads.AdClientNativeAdLoadedListener;
import com.adclient.android.sdk.nativeads.AdClientNativeAdPlacer;
import com.adclient.android.sdk.nativeads.AdClientNativeAdPositioning;
import com.adclient.android.sdk.nativeads.AdClientNativeAdRenderer;
import com.adclient.android.sdk.nativeads.VisibilityTracker;
import com.adclient.android.sdk.type.ParamsType;

import java.util.HashMap;

import static android.widget.AdapterView.OnItemClickListener;
import static android.widget.AdapterView.OnItemLongClickListener;
import static android.widget.AdapterView.OnItemSelectedListener;

public class AdClientNativeAdBaseAdapter extends BaseAdapter {

    @NonNull
    private final Adapter originalAdapter;
    @NonNull
    private final AdClientNativeAdPlacer mStreamAdPlacer;

    @Nullable
    private AdClientNativeAdLoadedListener mAdLoadedListener;

    private int loadingLayoutId = -1;

    /**
     * Creates a new AdClientNativeAdBaseAdapter object, using client positioning.
     *
     * @param activity        The activity.
     * @param originalAdapter Your original adapter.
     * @param adPositioning   A positioning object for specifying where ads will be placed in your
     *                        stream. See {@link AdClientNativeAdPositioning#clientPositioning()}.
     */
    public AdClientNativeAdBaseAdapter(@NonNull final Activity activity,
                                       @NonNull final Adapter originalAdapter,
                                       @NonNull final AdClientNativeAdPositioning.ClientPositioning adPositioning) {
        this(new AdClientNativeAdPlacer(activity, adPositioning), originalAdapter,
                new VisibilityTracker(activity));
    }

    private AdClientNativeAdBaseAdapter(@NonNull final AdClientNativeAdPlacer streamAdPlacer,
                                        @NonNull final Adapter originalAdapter,
                                        @NonNull final VisibilityTracker visibilityTracker) {
        this.originalAdapter = originalAdapter;
        mStreamAdPlacer = streamAdPlacer;

        this.originalAdapter.registerDataSetObserver(new DataSetObserver() {
            @Override
            public void onChanged() {
                mStreamAdPlacer.setItemCount(originalAdapter.getCount());
                notifyDataSetChanged();
            }

            @Override
            public void onInvalidated() {
                notifyDataSetInvalidated();
            }
        });

        mStreamAdPlacer.setAdLoadedListener(new AdClientNativeAdLoadedListener() {
            @Override
            public void onAdLoaded(final int position) {
                handleAdLoaded(position);
            }

            @Override
            public void onAdRemoved(final int position) {
                handleAdRemoved(position);
            }

        });

        mStreamAdPlacer.setItemCount(this.originalAdapter.getCount());
    }

    public void setLoadingLayoutId(int loadingLayoutId) {
        this.loadingLayoutId = loadingLayoutId;
        this.mStreamAdPlacer.setLoadingLayoutId(loadingLayoutId);
    }

    private void handleAdLoaded(final int position) {
        if (mAdLoadedListener != null) {
            mAdLoadedListener.onAdLoaded(position);
        }
        notifyDataSetChanged();
    }

    private void handleAdRemoved(final int position) {
        if (mAdLoadedListener != null) {
            mAdLoadedListener.onAdRemoved(position);
        }
        notifyDataSetChanged();
    }


    public final void setAdLoadedListener(@Nullable final AdClientNativeAdLoadedListener listener) {
        mAdLoadedListener = listener;
    }

    public void loadAds(HashMap<ParamsType, Object> configuration, AdClientNativeAdRenderer renderer) {
        mStreamAdPlacer.loadAds(configuration, renderer);
    }

    public boolean isAd(final int position) {
        return mStreamAdPlacer.isAd(position);
    }

    public void clearAds() {
        mStreamAdPlacer.clearAds();
    }

    public void resume() {
        mStreamAdPlacer.resume();
    }

    public void pause() {
        mStreamAdPlacer.pause();
    }

    public void destroy() {
        mStreamAdPlacer.destroy();
    }

    @Override
    public boolean areAllItemsEnabled() {
        return originalAdapter instanceof ListAdapter
                && ((ListAdapter) originalAdapter).areAllItemsEnabled();
    }

    @Override
    public boolean isEnabled(final int position) {
        return isAd(position) || (originalAdapter instanceof ListAdapter
                && ((ListAdapter) originalAdapter).isEnabled(mStreamAdPlacer.getOriginalPosition(
                position)));
    }

    @Override
    public int getCount() {
        return mStreamAdPlacer.getAdjustedCount(originalAdapter.getCount());
    }

    @Nullable
    @Override
    public Object getItem(final int position) {
        final Object ad = mStreamAdPlacer.getAdData(position);
        if (ad != null) {
            return ad;
        }
        return originalAdapter.getItem(mStreamAdPlacer.getOriginalPosition(position));
    }

    @Override
    public long getItemId(final int position) {
        final Object adData = mStreamAdPlacer.getAdData(position);
        if (adData != null) {
            return -System.identityHashCode(adData);
        }
        return originalAdapter.getItemId(mStreamAdPlacer.getOriginalPosition(position));
    }

    @Override
    public boolean hasStableIds() {
        return originalAdapter.hasStableIds();
    }

    @Override
    public int getItemViewType(final int position) {
        final int viewType = mStreamAdPlacer.getAdViewType(position);
        if (viewType != AdClientNativeAdPlacer.CONTENT_VIEW_TYPE) {
            return viewType;
        }
        return originalAdapter.getItemViewType(mStreamAdPlacer.getOriginalPosition(position));
    }

    @Override
    public int getViewTypeCount() {
        return originalAdapter.getViewTypeCount() + mStreamAdPlacer.getAdViewTypeCount();
    }

    @Nullable
    @Override
    public View getView(final int position, final View view, final ViewGroup viewGroup) {
        final View resultView;
        final View adView = mStreamAdPlacer.getAdView(position, view, viewGroup);
        if (adView != null) {
            resultView = adView;
        } else {
            resultView = originalAdapter.getView(mStreamAdPlacer.getOriginalPosition(position), view, viewGroup);
        }
        mStreamAdPlacer.addView(resultView, position);

        return resultView;
    }


    @Override
    public boolean isEmpty() {
        return originalAdapter.isEmpty() && mStreamAdPlacer.getAdjustedCount(0) == 0;
    }

    public int getOriginalPosition(final int position) {
        return mStreamAdPlacer.getOriginalPosition(position);
    }

    public int getAdjustedPosition(final int originalPosition) {
        return mStreamAdPlacer.getAdjustedPosition(originalPosition);
    }


    public void insertItem(final int originalPosition) {
        mStreamAdPlacer.insertItem(originalPosition);
    }


    public void removeItem(final int originalPosition) {
        mStreamAdPlacer.removeItem(originalPosition);
    }


    public void setOnClickListener(@NonNull final ListView listView,
                                   @Nullable final OnItemClickListener listener) {
        if (listView == null) {
            throw new NullPointerException("You called AdClientNativeAdBaseAdapter.setOnClickListener(...) with null ListView");
        }
        if (listener == null) {
            listView.setOnItemClickListener(null);
            return;
        }

        listView.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(final AdapterView<?> adapterView, final View view,
                                    final int position, final long id) {
                if (!mStreamAdPlacer.isAd(position)) {
                    listener.onItemClick(
                            adapterView, view, mStreamAdPlacer.getOriginalPosition(position), id);
                }
            }
        });
    }


    public void setOnItemLongClickListener(@NonNull final ListView listView,
                                           @Nullable final OnItemLongClickListener listener) {
        if (listView == null) {
            throw new NullPointerException("You called AdClientNativeAdBaseAdapter.setOnItemLongClickListener(...) with null ListView");
        }
        if (listener == null) {
            listView.setOnItemLongClickListener(null);
            return;
        }

        listView.setOnItemLongClickListener(new OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(final AdapterView<?> adapterView,
                                           final View view, final int position, final long id) {
                return isAd(position) || listener.onItemLongClick(
                        adapterView, view, mStreamAdPlacer.getOriginalPosition(position), id);
            }
        });
    }

    public void setOnItemSelectedListener(@NonNull final ListView listView,
                                          @Nullable final OnItemSelectedListener listener) {
        if (listView == null) {
            throw new NullPointerException("You called AdClientNativeAdBaseAdapter.setOnItemSelectedListener(...) with null ListView");
        }
        if (listener == null) {
            listView.setOnItemSelectedListener(null);
            return;
        }

        listView.setOnItemSelectedListener(new OnItemSelectedListener() {
            @Override
            public void onItemSelected(final AdapterView<?> adapterView,
                                       final View view, final int position, final long id) {
                if (!isAd(position)) {
                    listener.onItemSelected(adapterView, view,
                            mStreamAdPlacer.getOriginalPosition(position), id);
                }
            }

            @Override
            public void onNothingSelected(final AdapterView<?> adapterView) {
                listener.onNothingSelected(adapterView);
            }
        });
    }

    public void setSelection(@NonNull final ListView listView, final int originalPosition) {
        if (listView == null) {
            throw new NullPointerException("You called AdClientNativeAdBaseAdapter.setSelection(...) with null ListView");
        }

        listView.setSelection(mStreamAdPlacer.getAdjustedPosition(originalPosition));
    }

    public void smoothScrollToPosition(@NonNull final ListView listView,
                                       final int originalPosition) {
        if (listView == null) {
            throw new NullPointerException("You called AdClientNativeAdBaseAdapter.smoothScrollToPosition(...) with null ListView");
        }
        listView.smoothScrollToPosition(mStreamAdPlacer.getAdjustedPosition(originalPosition));
    }

    public void refreshAds(@NonNull final ListView listView, @NonNull HashMap<ParamsType, Object> configuration, AdClientNativeAdRenderer renderer) {
        if (listView == null) {
            throw new NullPointerException("You called AdClientNativeAdBaseAdapter.refreshAds(...) with null ListView");
        }

        // Get scroll offset of the first view, if it exists.
        View firstView = listView.getChildAt(0);
        int offsetY = (firstView == null) ? 0 : firstView.getTop();

        // Find the range of positions where we should not destroyController ads.
        int firstPosition = listView.getFirstVisiblePosition();
        int startRange = Math.max(firstPosition - 1, 0);
        while (mStreamAdPlacer.isAd(startRange) && startRange > 0) {
            startRange--;
        }
        int lastPosition = listView.getLastVisiblePosition();
        while (mStreamAdPlacer.isAd(lastPosition) && lastPosition < getCount() - 1) {
            lastPosition++;
        }
        int originalStartRange = mStreamAdPlacer.getOriginalPosition(startRange);
        int originalEndRange = mStreamAdPlacer.getOriginalCount(lastPosition + 1);

        // Remove ads before and after the range.
        int originalCount = mStreamAdPlacer.getOriginalCount(getCount());
        mStreamAdPlacer.removeAdsInRange(originalEndRange, originalCount);
        int numAdsRemoved = mStreamAdPlacer.removeAdsInRange(0, originalStartRange);

        // Reset the scroll position, and reload ads.
        if (numAdsRemoved > 0) {
            listView.setSelectionFromTop(firstPosition - numAdsRemoved, offsetY);
        }
        loadAds(configuration, renderer);
    }
}
