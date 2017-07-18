package com.example.sample.sharkapp.sharkapp.service;


import android.support.v4.util.LruCache;
import android.util.Log;
import java.io.IOException;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import com.example.sample.sharkapp.sharkapp.model.ImageDetailResult;
import com.example.sample.sharkapp.sharkapp.model.SearchResult;
import com.example.sample.sharkapp.sharkapp.presentor.ImageListPresenterImplementor;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.GET;
import retrofit2.http.QueryMap;

/**
 * This class is responsible for setting up the networking side of the application. It initializes the HTTP client object and creates the Observables for all the networking requests.
 */
public class FlickerFetchService {

    private static final String TAG = FlickerFetchService.class.getSimpleName();
    private static final String BASE_URL = "https://www.flickr.com/services/";
    private final NetworkAPI networkAPI;
    private final LruCache<String, Observable<?>> apiObservables;
    private static FlickerFetchService service;

    /**
     * Returns the instance of FlickerFetchService. Makes sure that there is only one instance of the service
     */
    public static FlickerFetchService getInstance(){
        if(service == null){
            service = new FlickerFetchService();
        }
        return service;
    }

    private FlickerFetchService() {
        this(BASE_URL);
    }

    private FlickerFetchService(final String baseUrl) {
        Log.d(TAG, "Creating service ");
        final OkHttpClient okHttpClient = buildClient();
        apiObservables = new LruCache<>(10);
        final Retrofit retrofit = new Retrofit.Builder().baseUrl(baseUrl).addConverterFactory(GsonConverterFactory.create()).addCallAdapterFactory(RxJava2CallAdapterFactory.create()).client(okHttpClient).build();
        networkAPI = retrofit.create(NetworkAPI.class);
    }

    /**
     * Returns the instance of the retrofit network API
     */
    public NetworkAPI getAPI() {
        return networkAPI;
    }

    /**
     * Builds the HTTP client object
     */
    private OkHttpClient buildClient() {

        final OkHttpClient.Builder builder = new OkHttpClient.Builder();

        builder.addInterceptor(new Interceptor() {
            @Override
            public Response intercept(final Chain chain) throws IOException {
                final Response response = chain.proceed(chain.request());
                // Can do anything with response here if we ant to grab a specific cookie or something..
                return response;
            }
        });

        builder.addInterceptor(new Interceptor() {
            @Override
            public Response intercept(final Chain chain) throws IOException {
                final Request request = chain.request().newBuilder().addHeader("Accept", "application/json").build();
                return chain.proceed(request);
            }
        });

        return builder.connectTimeout(100, TimeUnit.SECONDS).readTimeout(100, TimeUnit.SECONDS).build();
    }

    /**
     *  This method clears the observable cache
     */
    public void clearCache() {
        apiObservables.evictAll();
    }


    /**
     * Returns an Observable either from the cache ( cache hit ) or it creates a new observable object.
     */
    public Observable<?> getPreparedObservable(io.reactivex.Observable<?> unPreparedObservable, String key, boolean cacheObservable, boolean useCache) {

        Observable<?> preparedObservable = null;

        if(useCache)//this way we don't reset anything in the cache if this is the only instance of us not wanting to use it.
        {
            preparedObservable = apiObservables.get(key);
        }

        if(preparedObservable != null) {
            Log.d(TAG, "Observable from cache");
            return preparedObservable;
        }


        //we are here because we have never created this observable before or we didn't want to use the cache...
        Log.d(TAG, "Observable not in cache. loading new one");
        preparedObservable = unPreparedObservable.subscribeOn(Schedulers.newThread()).observeOn(AndroidSchedulers.mainThread());

        if(cacheObservable) {
            preparedObservable = preparedObservable.cache();
            apiObservables.put(key, preparedObservable);
        }
        return preparedObservable;
    }

    /**
     * Interface API which provides the REST call request definition
     */
     public interface NetworkAPI {

        @GET("rest")
        Observable<SearchResult> getSearchResults(@QueryMap Map<String, String> options);

        @GET("rest")
        Observable<ImageDetailResult> getImageDetails(@QueryMap Map<String, String> options);

    }
}
