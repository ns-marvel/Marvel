package com.springwoodcomputers.marvel.utility;

import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;

public class InfiniteScrollListener extends RecyclerView.OnScrollListener {

    private final GridLayoutManager layoutManager;
    private final int threshold;
    private final OnInfiniteScrollThresholdReachedListener listener;
    private boolean previousThresholdReachHandled;

    public InfiniteScrollListener(GridLayoutManager layoutManager, int threshold, OnInfiniteScrollThresholdReachedListener listener) {
        this.layoutManager = layoutManager;
        this.threshold = threshold;
        this.listener = listener;
    }

    @Override
    public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
        boolean wasLayoutChange = dx == 0 && dy == 0;
        boolean wasScrollDown = dy > 0;
        if (!wasLayoutChange && wasScrollDown) {
            calculateThreshold();
        }
    }

    private void calculateThreshold() {
        int totalItemCount = layoutManager.getItemCount();
        int lastVisibleItemPosition = layoutManager.findLastVisibleItemPosition();
        boolean itemThresholdReached = totalItemCount <= (lastVisibleItemPosition + threshold);
        boolean notifyThresholdReached = previousThresholdReachHandled && itemThresholdReached;
        if (notifyThresholdReached) {
            notifyThresholdReached();
        }
    }

    private void notifyThresholdReached() {
        if (listener != null) {
            listener.onInfiniteScrollThresholdReached();
            previousThresholdReachHandled = false;
        }
    }

    public void setThresholdReachHandled(int numberOfElementsAdded) {
        previousThresholdReachHandled = true;
        if (numberOfElementsAdded < threshold) {
            calculateThreshold();
        }
    }

    public interface OnInfiniteScrollThresholdReachedListener {
        void onInfiniteScrollThresholdReached();
    }
}