package com.example.sample.sharkapp.sharkapp.presentor;

import android.util.Log;

import com.example.sample.sharkapp.sharkapp.listeners.PaginationScrollListener;
import com.example.sample.sharkapp.sharkapp.model.ImageDetailResult;
import com.example.sample.sharkapp.sharkapp.model.Photo;
import com.example.sample.sharkapp.sharkapp.model.PhotoDetail;
import com.example.sample.sharkapp.sharkapp.model.Photos;
import com.example.sample.sharkapp.sharkapp.service.FlickerFetchService;

import java.util.HashMap;
import java.util.Map;

import io.reactivex.Observable;
import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;

/**
 * This class is the Presentor in the 'Model-View-Presentor' pattern. This takes request from ImageDetailActivity and makes REST call to fetch data and deliver back to the activity.
 */
public class ImageDetailPresentorImplementor implements ImageDetailPresenterInteractor {

    private static final String TAG = ImageDetailPresentorImplementor.class.getSimpleName();
    private static final String METHOD_NAME_PARAMETER = "method";
    private static final String API_PARAMETER = "api_key";
    private static final String FORMAT_PARAMETER = "format";
    private static final String JSON_CALL_BACK_PARAMETER = "nojsoncallback";
    private static final String PHOTO_ID_PARAMETER = "photo_id";
    private FlickerFetchService service;
    private LoadImageDetail result;
    private static final String API_KEY = "949e98778755d1982f537d56236bbb42";
    private Disposable imageDetailDisposable;

    public ImageDetailPresentorImplementor(final FlickerFetchService service) {
        this.service = service;
    }

    @Override
    public void clearObservableCache() {
        Log.d(TAG, "Clearing observables cache");
        service.clearCache();
    }

    @Override
    public void unSubscribeImageDetailListener() {
        if (imageDetailDisposable != null && !imageDetailDisposable.isDisposed()) {
            Log.d(TAG, "disposing " + imageDetailDisposable.toString());
            imageDetailDisposable.dispose();
        }
    }

    @Override
    public void subscribeForImageDetailResult(final LoadImageDetail resultListener) {
        this.result = resultListener;
        Log.d(TAG, "Subscribed for image detail implementor");
    }

    @Override
    public void makeImageDetailRequest(String imageId) {
        Log.d(TAG, "Making image detail request for id " + imageId);
        final Map<String, String> map = new HashMap<>();
        map.put(METHOD_NAME_PARAMETER, "flickr.photos.getInfo");
        map.put(API_PARAMETER, API_KEY);
        map.put(FORMAT_PARAMETER, "json");
        map.put(JSON_CALL_BACK_PARAMETER, "1");
        map.put(PHOTO_ID_PARAMETER, imageId);
        final Observable<ImageDetailResult> imageDetailResultObservable = (Observable<ImageDetailResult>) service.getPreparedObservable(service.getAPI().getImageDetails(map), Photo.class.getSimpleName() + imageId, true, true);
        imageDetailResultObservable.subscribe(new Observer<ImageDetailResult>() {
            @Override
            public void onSubscribe(Disposable d) {
                imageDetailDisposable = d;
            }

            @Override
            public void onNext(ImageDetailResult value) {
                if (result != null) {
                    result.onImageDetailComplete(value.getPhotoDetail());
                }
            }

            @Override
            public void onError(Throwable e) {
                if(result != null){
                    result.onError();
                }
            }

            @Override
            public void onComplete() {

            }
        });

    }

    /**
     * This interface is used to deliver result after the image detail request is successful
     */
    public interface LoadImageDetail {

        /**
         * Called when the request for the image comes through.
         */
        void onImageDetailComplete(PhotoDetail photoDetail);

        /**
         * Called when there is an error while making a request for the image
         */
        void onError();
    }
}
