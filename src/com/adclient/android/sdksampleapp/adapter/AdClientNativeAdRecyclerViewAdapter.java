package com.adclient.android.sdksampleapp.adapter;

import android.app.Activity;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;

import com.adclient.android.sdk.nativeads.AdClientNativeAd;
import com.adclient.android.sdk.nativeads.AdClientNativeAdLoadedListener;
import com.adclient.android.sdk.nativeads.AdClientNativeAdPlacer;
import com.adclient.android.sdk.nativeads.AdClientNativeAdPositioning;
import com.adclient.android.sdk.nativeads.AdClientNativeAdRenderer;
import com.adclient.android.sdk.type.ParamsType;

import java.util.HashMap;

public final class AdClientNativeAdRecyclerViewAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private final static String TAG = "AdClientSdkTestApp";
    // RecyclerView ad views will have negative types to avoid colliding with original view types.
    @NonNull
    private final RecyclerView.AdapterDataObserver adapterDataObserver;
    @NonNull
    private final AdClientNativeAdPlacer streamAdPlacer;
    @NonNull
    private final RecyclerView.Adapter originalAdapter;
    @Nullable
    private RecyclerView recyclerView;
    @NonNull
    private ContentChangeStrategy strategy = ContentChangeStrategy.INSERT_AT_END;
    @Nullable
    private AdClientNativeAdLoadedListener adLoadedListener;
    public AdClientNativeAdRecyclerViewAdapter(@NonNull Activity activity,
                                               @NonNull RecyclerView.Adapter originalAdapter,
                                               @NonNull AdClientNativeAdPositioning.ClientPositioning adPositioning) {
        this(new AdClientNativeAdPlacer(activity, adPositioning), originalAdapter);
    }


    private AdClientNativeAdRecyclerViewAdapter(@NonNull final AdClientNativeAdPlacer streamAdPlacer,
                                                @NonNull final RecyclerView.Adapter originalAdapter) {
        this.originalAdapter = originalAdapter;

        setHasStableIdsInternal(this.originalAdapter.hasStableIds());

        this.streamAdPlacer = streamAdPlacer;
        this.streamAdPlacer.setAdLoadedListener(new AdClientNativeAdLoadedListener() {
            @Override
            public void onAdLoaded(final int position) {
                handleAdLoaded(position);
            }

            @Override
            public void onAdRemoved(final int position) {
                handleAdRemoved(position);
            }
        });
        this.streamAdPlacer.setItemCount(this.originalAdapter.getItemCount());

        adapterDataObserver = new RecyclerView.AdapterDataObserver() {
            @Override
            public void onChanged() {
                streamAdPlacer.setItemCount(originalAdapter.getItemCount());
                notifyDataSetChanged();
            }

            @Override
            public void onItemRangeChanged(final int positionStart, final int itemCount) {
                int adjustedEndPosition = AdClientNativeAdRecyclerViewAdapter.this.streamAdPlacer.getAdjustedPosition(positionStart + itemCount - 1);
                int adjustedStartPosition = AdClientNativeAdRecyclerViewAdapter.this.streamAdPlacer.getAdjustedPosition(positionStart);
                int adjustedCount = adjustedEndPosition - adjustedStartPosition + 1;
                notifyItemRangeChanged(adjustedStartPosition, adjustedCount);
            }

            @Override
            public void onItemRangeInserted(final int positionStart, final int itemCount) {
                final int adjustedStartPosition = AdClientNativeAdRecyclerViewAdapter.this.streamAdPlacer.getAdjustedPosition(positionStart);
                final int newOriginalCount = AdClientNativeAdRecyclerViewAdapter.this.originalAdapter.getItemCount();
                AdClientNativeAdRecyclerViewAdapter.this.streamAdPlacer.setItemCount(newOriginalCount);
                final boolean addingToEnd = positionStart + itemCount >= newOriginalCount;
                if (ContentChangeStrategy.KEEP_ADS_FIXED == strategy
                        || (ContentChangeStrategy.INSERT_AT_END == strategy
                        && addingToEnd)) {
                    notifyDataSetChanged();
                } else {
                    for (int i = 0; i < itemCount; i++) {
                        // We insert itemCount items at the original position, moving ads downstream.
                        AdClientNativeAdRecyclerViewAdapter.this.streamAdPlacer.insertItem(positionStart);
                    }
                    notifyItemRangeInserted(adjustedStartPosition, itemCount);
                }
            }

            @Override
            public void onItemRangeRemoved(final int positionStart, final int itemsRemoved) {
                int adjustedStartPosition = AdClientNativeAdRecyclerViewAdapter.this.streamAdPlacer.getAdjustedPosition(positionStart);
                final int newOriginalCount = AdClientNativeAdRecyclerViewAdapter.this.originalAdapter.getItemCount();
                AdClientNativeAdRecyclerViewAdapter.this.streamAdPlacer.setItemCount(newOriginalCount);
                final boolean removingFromEnd = positionStart + itemsRemoved >= newOriginalCount;
                if (ContentChangeStrategy.KEEP_ADS_FIXED == strategy
                        || (ContentChangeStrategy.INSERT_AT_END == strategy
                        && removingFromEnd)) {
                    notifyDataSetChanged();
                } else {
                    final int oldAdjustedCount = AdClientNativeAdRecyclerViewAdapter.this.streamAdPlacer.getAdjustedCount(newOriginalCount + itemsRemoved);
                    for (int i = 0; i < itemsRemoved; i++) {
                        // We remove itemsRemoved items at the original position.
                        AdClientNativeAdRecyclerViewAdapter.this.streamAdPlacer.removeItem(positionStart);
                    }

                    final int itemsRemovedIncludingAds = oldAdjustedCount - AdClientNativeAdRecyclerViewAdapter.this.streamAdPlacer.getAdjustedCount(newOriginalCount);
                    // Need to move the start position back by the # of ads removed.
                    adjustedStartPosition -= itemsRemovedIncludingAds - itemsRemoved;
                    notifyItemRangeRemoved(adjustedStartPosition, itemsRemovedIncludingAds);
                }
            }

            @Override
            public void onItemRangeMoved(final int fromPosition, final int toPosition,
                                         final int itemCount) {
                notifyDataSetChanged();
            }
        };

        this.originalAdapter.registerAdapterDataObserver(adapterDataObserver);
    }

    public static int computeScrollOffset(@NonNull final LinearLayoutManager linearLayoutManager,
                                          @Nullable final RecyclerView.ViewHolder holder) {
        if (holder == null) {
            return 0;
        }
        final View view = holder.itemView;

        int offset = 0;
        if (linearLayoutManager.canScrollVertically()) {
            if (linearLayoutManager.getStackFromEnd()) {
                offset = view.getBottom();
            } else {
                offset = view.getTop();
            }
        } else if (linearLayoutManager.canScrollHorizontally()) {
            if (linearLayoutManager.getStackFromEnd()) {
                offset = view.getRight();
            } else {
                offset = view.getLeft();
            }
        }

        return offset;
    }

    @Override
    public void onAttachedToRecyclerView(@NonNull final RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
        this.recyclerView = recyclerView;
    }

    @Override
    public void onDetachedFromRecyclerView(@NonNull final RecyclerView recyclerView) {
        super.onDetachedFromRecyclerView(recyclerView);
        this.recyclerView = null;
    }

    /**
     * Sets a listener that will be called after the SDK loads new ads from the server and places
     * them into your stream.
     * <p/>
     * The listener will be active between when you call {@link #loadAds} and when you call
     * onDestroy(). You can also set the listener to {@code null} to remove the listener.
     * <p/>
     * Note that there is not a one to one correspondence between calls to {@link #loadAds} and this
     * listener. The SDK will call the listener every time an ad loads.
     *
     * @param listener The listener.
     */
    public void setAdLoadedListener(@Nullable final AdClientNativeAdLoadedListener listener) {
        adLoadedListener = listener;
    }


    public void loadAds(HashMap<ParamsType, Object> configuration, AdClientNativeAdRenderer renderer) {
        streamAdPlacer.loadAds(configuration, renderer);
    }

    public void refreshAds(HashMap<ParamsType, Object> configuration, AdClientNativeAdRenderer renderer) {
        if (recyclerView == null) {
            Log.w(TAG, "This adapter is not attached to a RecyclerView and cannot be refreshed.", null);
            return;
        }

        final RecyclerView.LayoutManager layoutManager = recyclerView.getLayoutManager();
        if (layoutManager == null) {
            Log.w(TAG, "Can't tryToRefresh ads when there is no layout manager on a RecyclerView.", null);
            return;
        }

        if (layoutManager instanceof LinearLayoutManager) {
            // Includes GridLayoutManager

            // Get the range & offset of scroll position.
            LinearLayoutManager linearLayoutManager = (LinearLayoutManager) layoutManager;
            final int firstPosition = linearLayoutManager.findFirstVisibleItemPosition();
            RecyclerView.ViewHolder holder = recyclerView.findViewHolderForLayoutPosition(firstPosition);
            final int scrollOffset = computeScrollOffset(linearLayoutManager, holder);

            // Calculate the range of ads not to remove ads from.
            int startOfRange = Math.max(0, firstPosition - 1);
            while (streamAdPlacer.isAd(startOfRange) && startOfRange > 0) {
                startOfRange--;
            }


            final int itemCount = getItemCount();
            int endOfRange = linearLayoutManager.findLastVisibleItemPosition();
            while (streamAdPlacer.isAd(endOfRange) && endOfRange < itemCount - 1) {
                endOfRange++;
            }

            final int originalStartOfRange = streamAdPlacer.getOriginalPosition(startOfRange);
            final int originalEndOfRange = streamAdPlacer.getOriginalPosition(endOfRange);
            final int endCount = originalAdapter.getItemCount();

            streamAdPlacer.removeAdsInRange(originalEndOfRange, endCount);
            final int numAdsRemoved = streamAdPlacer.removeAdsInRange(0, originalStartOfRange);

            if (numAdsRemoved > 0) {
                linearLayoutManager.scrollToPositionWithOffset(firstPosition - numAdsRemoved, scrollOffset);
            }

            loadAds(configuration, renderer);
        } else {
            Log.w(TAG, "This LayoutManager can't be refreshed.", null);
        }
    }

    public void clearAds() {
        streamAdPlacer.clearAds();
    }

    /**
     * Whether the given position is an ad.
     */
    public boolean isAd(final int position) {
        return streamAdPlacer.isAd(position);
    }

    /**
     * Returns the position of an item considering ads in the stream.
     */
    public int getAdjustedPosition(final int originalPosition) {
        return streamAdPlacer.getAdjustedPosition(originalPosition);
    }

    /**
     * Returns the original position of an item considering ads in the stream.
     */
    public int getOriginalPosition(final int position) {
        return streamAdPlacer.getOriginalPosition(position);
    }

    /**
     * Sets the strategy this adapter should use for moving ads when content is added or removed
     * from the wrapped original adapter. This strategy can be set at any time to change the
     * behavior of the adapter.
     */
    public void setContentChangeStrategy(@NonNull ContentChangeStrategy strategy) {
        this.strategy = strategy;
    }

    @Override
    public int getItemCount() {
        return streamAdPlacer.getAdjustedCount(originalAdapter.getItemCount());
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull final ViewGroup parent, final int viewType) {
        if (viewType == AdClientNativeAdPlacer.DEFAULT_AD_VIEW_TYPE && streamAdPlacer.getRenderer() != null) {
            final View view = streamAdPlacer.getRenderer().getAdView(parent.getContext(), parent);
            return new AdClientNativeAdRecyclerViewHolder(view);
        }

        return originalAdapter.onCreateViewHolder(parent, viewType);
    }

    @Override
    public void onBindViewHolder(@NonNull final RecyclerView.ViewHolder holder, final int position) {
        AdClientNativeAd nativeAd = (AdClientNativeAd) streamAdPlacer.getAdData(position);
        if (nativeAd != null) {
            streamAdPlacer.bindAdView(nativeAd, holder.itemView);
        } else {
            //noinspection unchecked
            originalAdapter.onBindViewHolder(holder, streamAdPlacer.getOriginalPosition(position));
        }
        streamAdPlacer.addView(holder.itemView, position);
    }

    @Override
    public int getItemViewType(final int position) {
        int type = streamAdPlacer.getAdViewType(position);
        if (type != AdClientNativeAdPlacer.CONTENT_VIEW_TYPE) {
            return AdClientNativeAdPlacer.DEFAULT_AD_VIEW_TYPE;
        }
        return originalAdapter.getItemViewType(streamAdPlacer.getOriginalPosition(position));
    }

    @Override
    public void setHasStableIds(final boolean hasStableIds) {
        setHasStableIdsInternal(hasStableIds);

        // We can only setHasStableIds when there are no observers on the adapter.
        originalAdapter.unregisterAdapterDataObserver(adapterDataObserver);
        originalAdapter.setHasStableIds(hasStableIds);
        originalAdapter.registerAdapterDataObserver(adapterDataObserver);
    }

    public void resume() {
        streamAdPlacer.resume();
    }

    public void pause() {
        streamAdPlacer.pause();
    }

    public void destroy() {
        originalAdapter.unregisterAdapterDataObserver(adapterDataObserver);
        streamAdPlacer.destroy();
    }

    /**
     * Returns a stable negative item ARG_OBJECT_ID for ad items & calls getItemId on your original adapter for
     * non-ad items.
     * <p/>
     * Returns {@link RecyclerView#NO_ID} if your original adapter does
     * not have stable IDs.
     *
     * @inheritDoc
     */
    @Override
    public long getItemId(final int position) {
        if (!originalAdapter.hasStableIds()) {
            return RecyclerView.NO_ID;
        }

        final Object adData = streamAdPlacer.getAdData(position);
        if (adData != null) {
            return -System.identityHashCode(adData);
        }

        return originalAdapter.getItemId(streamAdPlacer.getOriginalPosition(position));
    }

    // Notification methods to forward to the original adapter.
    @Override
    public boolean onFailedToRecycleView(@NonNull final RecyclerView.ViewHolder holder) {
        if (holder instanceof AdClientNativeAdRecyclerViewHolder) {
            return super.onFailedToRecycleView(holder);
        }

        // noinspection unchecked
        return originalAdapter.onFailedToRecycleView(holder);
    }

    @Override
    public void onViewAttachedToWindow(@NonNull final RecyclerView.ViewHolder holder) {
        if (holder instanceof AdClientNativeAdRecyclerViewHolder) {
            super.onViewAttachedToWindow(holder);
            return;
        }

        // noinspection unchecked
        originalAdapter.onViewAttachedToWindow(holder);
    }

    @Override
    public void onViewDetachedFromWindow(@NonNull final RecyclerView.ViewHolder holder) {
        if (holder instanceof AdClientNativeAdRecyclerViewHolder) {
            super.onViewDetachedFromWindow(holder);
            return;
        }

        // noinspection unchecked
        originalAdapter.onViewDetachedFromWindow(holder);
    }

    @Override
    public void onViewRecycled(@NonNull final RecyclerView.ViewHolder holder) {
        if (holder instanceof AdClientNativeAdRecyclerViewHolder) {
            super.onViewRecycled(holder);
            return;
        }

        // noinspection unchecked
        originalAdapter.onViewRecycled(holder);
    }

    private void handleAdLoaded(final int position) {
        if (adLoadedListener != null) {
            adLoadedListener.onAdLoaded(position);
        }

        notifyItemInserted(position);
    }
    // End forwarded methods.

    private void handleAdRemoved(final int position) {
        if (adLoadedListener != null) {
            adLoadedListener.onAdRemoved(position);
        }

        notifyItemRemoved(position);
    }

    /**
     * Sets the hasStableIds value on this adapter only, not also on the wrapped adapter.
     */
    private void setHasStableIdsInternal(final boolean hasStableIds) {
        super.setHasStableIds(hasStableIds);
    }

    public enum ContentChangeStrategy {
        INSERT_AT_END, MOVE_ALL_ADS_WITH_CONTENT, KEEP_ADS_FIXED
    }
}
