package com.example.sample.sharkapp.sharkapp.activity;

import android.app.FragmentManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Parcelable;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.example.sample.sharkapp.R;
import com.example.sample.sharkapp.sharkapp.adapter.RecyclerViewAdapter;
import com.example.sample.sharkapp.sharkapp.fragment.ImageListRetainedFragment;
import com.example.sample.sharkapp.sharkapp.listeners.PaginationScrollListener;
import com.example.sample.sharkapp.sharkapp.model.Photo;
import com.example.sample.sharkapp.sharkapp.model.Photos;
import com.example.sample.sharkapp.sharkapp.presentor.ImageListPresenterInteractor;
import com.example.sample.sharkapp.sharkapp.presentor.ImageListPresenterImplementor;
import com.example.sample.sharkapp.sharkapp.service.FlickerFetchService;

import java.util.ArrayList;
import java.util.List;

/**
 * This activity displays the list of images in a grid layout. It supports infinite scrolling.
 */

public class ImageListActivity extends AppCompatActivity implements ImageListPresenterImplementor.LoadResults {

    private static final String TAG = ImageListActivity.class.getSimpleName();
    private static final String TAG_TASK_FRAGMENT = "task_fragment";
    private static final String PROGRESS_BAR_VISIBILITY_KEY = "progressBarVisibility";
    private static final String SCROLL_STATE_KEY = "ScrollState";
    private static final String TOTAL_PAGES_KEY = "totalPages";
    private static final String IS_LAST_PAGE_KEY = "isLastPage";
    private static final String CURRENT_PAGE_KEY = "currentPage";
    private static final String IS_LOADING_KEY = "isLoading";

    private ImageListPresenterInteractor imageListPresenter;
    private boolean isLoading = false;
    private boolean isLastPage = false;
    private int totalPages = Integer.MAX_VALUE;
    private int currentPage = 1;
    private ProgressBar progressBar;
    private RecyclerView recyclerView;
    private RecyclerViewAdapter adapter;

    private ImageListRetainedFragment taskFragment;
    private List<Photo> photosList = new ArrayList<>();
    private SwipeRefreshLayout swipeRefreshLayout;

    /**
     * Initializes all the UI elements and sets up the listeners.
     */
    @Override
    public void onCreate(@Nullable final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "onCreate");
        setContentView(R.layout.image_list_layout);
        getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        getSupportActionBar().setDisplayShowCustomEnabled(true);
        getSupportActionBar().setCustomView(R.layout.custom_action_bar_layout);

        progressBar = (ProgressBar) findViewById(R.id.main_progress);
        recyclerView = (RecyclerView) findViewById(R.id.main_recycler);
        swipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swipeContainer);

        FragmentManager fragmentManager = getFragmentManager();
        taskFragment = (ImageListRetainedFragment) fragmentManager.findFragmentByTag(TAG_TASK_FRAGMENT);
        if (taskFragment == null) {
            taskFragment = new ImageListRetainedFragment();
            fragmentManager.beginTransaction().add(taskFragment, TAG_TASK_FRAGMENT).commit();
        } else {
            photosList = taskFragment.getData();
        }
        FlickerFetchService networkService = FlickerFetchService.getInstance();
        imageListPresenter = new ImageListPresenterImplementor(networkService);
        imageListPresenter.subscribeForImageListResult(this);

        adapter = new RecyclerViewAdapter(this, recyclerView, photosList);
        final GridLayoutManager gridLayoutManager = new GridLayoutManager(this, 3);
        recyclerView.setLayoutManager(gridLayoutManager);
        swipeRefreshLayout.setOnRefreshListener(new RefreshListener());
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(adapter);
        recyclerView.addOnScrollListener(new PageScrollListener(gridLayoutManager));

        if(savedInstanceState != null) {
            Log.d(TAG, "loading from previous saved instance ");
            int progressBarVisibility = savedInstanceState.getInt(PROGRESS_BAR_VISIBILITY_KEY);
            progressBarVisibility = progressBarVisibility == 0 ? View.VISIBLE : View.GONE;
            progressBar.setVisibility(progressBarVisibility);
            Parcelable savedRecyclerLayoutState = savedInstanceState.getParcelable(SCROLL_STATE_KEY);
            recyclerView.getLayoutManager().onRestoreInstanceState(savedRecyclerLayoutState);
            totalPages = savedInstanceState.getInt(TOTAL_PAGES_KEY);
            isLastPage = savedInstanceState.getBoolean(IS_LAST_PAGE_KEY);
            currentPage = savedInstanceState.getInt(CURRENT_PAGE_KEY);
            isLoading = savedInstanceState.getBoolean(IS_LOADING_KEY);
        } else {
            Log.d(TAG, "loading first page");
            loadFirstPage();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        imageListPresenter.unSubscribeImageListListener();
        Log.d(TAG, "OnPause: unsubscribing from image list presenter");
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (isLoading) {
            imageListPresenter.makePageRequest(currentPage);
            Log.d(TAG, "onResume : unfinished request present, making the request again for page " + currentPage);
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelable(SCROLL_STATE_KEY, recyclerView.getLayoutManager().onSaveInstanceState());
        outState.putInt(PROGRESS_BAR_VISIBILITY_KEY, progressBar.getVisibility());
        outState.putInt(TOTAL_PAGES_KEY, totalPages);
        outState.putBoolean(IS_LAST_PAGE_KEY, isLastPage);
        outState.putInt(CURRENT_PAGE_KEY, currentPage);
        outState.putBoolean(IS_LOADING_KEY, isLoading);
    }

    @Override
    public void onInitialLoadComplete(final Photos photos) {
        taskFragment.addData(photos.photo);
        totalPages = photos.pages;
        progressBar.setVisibility(View.GONE);
        adapter.addData(photos.photo);
        isLoading = false;
        if (currentPage <= totalPages) {
            adapter.addLoadingFooter();
        } else {
            isLastPage = true;
        }
        Log.d(TAG, "first page result success with " + photos.photo.size() + " results");
    }

    @Override
    public void onNextLoadComplete(final Photos photos) {
        if (photos.photo.size() == 0) {
            currentPage++;
            loadNextPage();
            Log.d(TAG, "page request has 0 results, calling loadNextPage()");
            return;
        }
        adapter.removeLoadingFooter();
        isLoading = false;
        taskFragment.addData(photos.photo);
        adapter.addData(photos.photo);

        if (currentPage != totalPages) {
            adapter.addLoadingFooter();
        } else {
            isLastPage = true;
        }
        Log.d(TAG, currentPage + " page result success with " + photos.photo.size() + " results");
    }

    @Override
    public void onError() {
        Log.e(TAG, "error response ");
        Toast toast = Toast.makeText(this, "Error in loading images, Retrying in 5 secs..", Toast.LENGTH_LONG);
        toast.show();
        Handler handler = new Handler(getMainLooper()) ;
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                imageListPresenter.makePageRequest(currentPage);
            }
        }, 5000);
    }

    /**
     * This method refreshes the view. Clears the adapter and makes a new request to get the updated results
     */
    private void refreshItems() {
        Log.d(TAG, "pull to refresh performed.. refreshing results");
        adapter.clear();
        taskFragment.clearData();
        progressBar.setVisibility(View.VISIBLE);
        imageListPresenter.clearObservableCache();
        imageListPresenter.makePageRequest(1);
        currentPage = 1;
        swipeRefreshLayout.setRefreshing(false);
    }

    /**
     * This method makes a call to the presenter in order to make a REST call for the current page Index.
     */
    private void loadNextPage() {
        Log.d(TAG, "making request for page " + currentPage);
        imageListPresenter.makePageRequest(currentPage);
    }

    /**
     * This method makes a call to the presenter in order to make a REST call for the first page.
     */
    private void loadFirstPage() {
        Log.d(TAG, "making request for page 1");
        imageListPresenter.makePageRequest(1);
    }

    /**
     * This class defines when to load the next page
     */
    private class PageScrollListener extends PaginationScrollListener {

        private PageScrollListener(GridLayoutManager layoutManager) {
            super(layoutManager);
        }

        @Override
        protected void loadMoreItems() {
            isLoading = true;
            currentPage++;
            Log.d("TAG", "making new page request " + currentPage);
            loadNextPage();
        }

        @Override
        public boolean isLastPage() {
            return isLastPage;
        }

        @Override
        public boolean isLoading() {
            return isLoading;
        }
    }

    /**
     * This class detects the 'pull to refresh' gesture from the user and refreshes the view.
     */
    private class RefreshListener implements SwipeRefreshLayout.OnRefreshListener{

        @Override
        public void onRefresh() {
            refreshItems();
        }
    }
}
