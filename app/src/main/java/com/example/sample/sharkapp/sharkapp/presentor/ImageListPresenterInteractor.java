package com.example.sample.sharkapp.sharkapp.presentor;

/**
 * Interface which exposes the following methods to the View to interact with the presenter.
 */
public interface ImageListPresenterInteractor {

    /**
     * Makes paged REST call to fetch the list of images for that particular page
     *
     * @param pageIndex page index of request
     */
    void makePageRequest(final int pageIndex);

    /**
     * Clears the observable cache
     */
    void clearObservableCache();

    /**
     * Un-subscribes the observer
     */
    void unSubscribeImageListListener();

    /**
     * Subscribes the observer to the observable.
     */
    void subscribeForImageListResult(ImageListPresenterImplementor.LoadResults resultListener);
}
