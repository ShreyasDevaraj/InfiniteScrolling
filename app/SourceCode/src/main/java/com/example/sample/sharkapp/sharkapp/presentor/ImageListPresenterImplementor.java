package com.example.sample.sharkapp.sharkapp.presentor;

import android.util.Log;

import java.util.HashMap;
import java.util.Map;


import com.example.sample.sharkapp.sharkapp.model.Photos;
import com.example.sample.sharkapp.sharkapp.model.SearchResult;
import com.example.sample.sharkapp.sharkapp.service.FlickerFetchService;

import io.reactivex.Observable;
import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;



/**
 * This class is the Presentor in the 'Model-View-Presentor' pattern. This takes request from ImageListActivity and makes REST call to fetch data and deliver back to the activity.
 */

public class ImageListPresenterImplementor implements ImageListPresenterInteractor {

    private static final String TAG = ImageListPresenterImplementor.class.getSimpleName();
    private static final String METHOD_NAME_PARAMETER = "method";
    private static final String API_PARAMETER = "api_key";
    private static final String FORMAT_PARAMETER = "format";
    private static final String JSON_CALL_BACK_PARAMETER = "nojsoncallback";
    private static final String PAGE_NUMBER_PARAMETER = "page";
    private static final String SEARCH_STRING_PARAMETER = "text";
    private static final String EXTRAS_PARAMETER = "extras";
    private static final String PER_PAGE_PARAMETER = "per_page";
    private static final String API_KEY = "949e98778755d1982f537d56236bbb42";
    private FlickerFetchService service;
    private LoadResults result;
    private Disposable imageListDisposable;

    public ImageListPresenterImplementor(final FlickerFetchService service) {
        Log.d(TAG, "Presenter layer created");
        this.service = service;

    }

    @Override
    public void subscribeForImageListResult(LoadResults resultListener){
        this.result = resultListener;
    }

    @Override
    public void makePageRequest(final int pageIndex) {
        final Map<String, String> map = new HashMap<>();
        map.put(METHOD_NAME_PARAMETER, "flickr.photos.search");
        map.put(API_PARAMETER, API_KEY);
        map.put(SEARCH_STRING_PARAMETER, "shark");
        map.put(FORMAT_PARAMETER, "json");
        map.put(JSON_CALL_BACK_PARAMETER, "1");
        map.put(PAGE_NUMBER_PARAMETER, String.valueOf(pageIndex));
        map.put(EXTRAS_PARAMETER, "url_t,url_c,url_l,url_o");
        map.put(PER_PAGE_PARAMETER, "99");
        final Observable<SearchResult> photosObservable = (Observable<SearchResult>) service.getPreparedObservable(service.getAPI().getSearchResults(map), Photos.class.getSimpleName() + pageIndex, true, true);
        photosObservable.subscribe(new Observer<SearchResult>() {
            @Override
            public void onSubscribe(Disposable d) {
                imageListDisposable = d;
                Log.d(TAG, "disposable subscribed in makepage request " + pageIndex  + " " + imageListDisposable.toString());
            }

            @Override
            public void onNext(SearchResult value) {
                Log.d(TAG, "got " + value.photos.photo.size() + " results");
                if(result != null) {
                    if (pageIndex == 1) {
                        result.onInitialLoadComplete(value.photos);
                    } else {
                        result.onNextLoadComplete(value.photos);
                    }
                }
            }

            @Override
            public void onError(Throwable e) {
                if (result != null) {
                    result.onError();
                }
            }

            @Override
            public void onComplete() {

            }
        });

    }

    @Override
    public void clearObservableCache() {
        service.clearCache();
    }

    @Override
    public void unSubscribeImageListListener() {
        if (imageListDisposable != null && !imageListDisposable.isDisposed()) {
            Log.d(TAG, "disposing " + imageListDisposable.toString());
            imageListDisposable.dispose();
        }
    }


    /**
     * This interface is used to deliver result after the image detail request is successful
     */
    public interface LoadResults {

        /**
         * This is called when the request for the first page of results for the serach key comes through
         */
        void onInitialLoadComplete(Photos photos);

        /**
         * This is called when the request for the successive pages for search key comes through
         */
        void onNextLoadComplete(Photos photos);

        /**
         * This is called when the REST call fails for some reason.
         */
        void onError();
    }
}
