package com.example.sample.sharkapp.sharkapp.presentor;

/**
 * Interface which exposes the following methods to the View to interact with the presenter.
 */
public interface ImageDetailPresenterInteractor {

    /**
     * Clears the observable cache
     */
    void clearObservableCache();

    /**
     * Unsubscribes the observer from the observable
     */
    void unSubscribeImageDetailListener();

    /**
     * Subscribes the observer to the observable.
     */
    void subscribeForImageDetailResult(ImageDetailPresentorImplementor.LoadImageDetail imageDetail);

    /**
     * Makes the REST call get fetch the image detail for the given image ID.
     */
    void makeImageDetailRequest(final String imageId);
}
