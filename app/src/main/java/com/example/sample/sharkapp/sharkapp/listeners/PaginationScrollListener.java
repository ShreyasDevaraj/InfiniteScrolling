package com.example.sample.sharkapp.sharkapp.listeners;

import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;

import com.example.sample.sharkapp.sharkapp.activity.ImageDetailActivity;

/**
 * This listens to the user's scrolling movement and determines when to load the next set of results. This currently supports pre-fetching of data.
 */
public abstract class PaginationScrollListener extends RecyclerView.OnScrollListener {

    private static final String TAG = PaginationScrollListener.class.getSimpleName();
    private GridLayoutManager layoutManager;

    public PaginationScrollListener(GridLayoutManager layoutManager) {
        this.layoutManager = layoutManager;
    }

    @Override
    public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
        super.onScrolled(recyclerView, dx, dy);

        int visibleItemCount = layoutManager.getChildCount();
        int totalItemCount = layoutManager.getItemCount();
        int firstVisibleItemPosition = layoutManager.findFirstVisibleItemPosition();

        // Starts loading the next page when the user reaches half of the current page.
        if (!isLoading() && !isLastPage()) {
            if ((visibleItemCount + firstVisibleItemPosition) >= (totalItemCount/2) && firstVisibleItemPosition >= 0) {
                Log.d(TAG, "Loading next page");
                loadMoreItems();
            }
        }
    }

    protected abstract void loadMoreItems();

    protected abstract boolean isLastPage();

    protected abstract boolean isLoading();

}
