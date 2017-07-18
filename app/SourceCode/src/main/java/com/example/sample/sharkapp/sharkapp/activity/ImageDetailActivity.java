package com.example.sample.sharkapp.sharkapp.activity;

import android.Manifest;
import android.app.Activity;
import android.app.DownloadManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.example.sample.sharkapp.R;
import com.example.sample.sharkapp.sharkapp.model.Photo;
import com.example.sample.sharkapp.sharkapp.model.PhotoDetail;
import com.example.sample.sharkapp.sharkapp.presentor.ImageDetailPresenterInteractor;
import com.example.sample.sharkapp.sharkapp.presentor.ImageDetailPresentorImplementor;
import com.example.sample.sharkapp.sharkapp.service.FlickerFetchService;
import it.sephiroth.android.library.imagezoom.ImageViewTouch;

/**
 * This activity acts as a image viewer. It displays selected image by the user in the full screen. The user can also download the image as well as view it in flicker web page.
 */
public class ImageDetailActivity extends Activity implements View.OnClickListener, ImageDetailPresentorImplementor.LoadImageDetail {

    public static final String ITEM_INTENT_KEY = "Item";
    private static final String TAG = ImageDetailActivity.class.getSimpleName();
    private static final int REQUEST_WRITE_STORAGE = 112;
    private static final String FLICKER_BASE_URL = "https://www.flickr.com/photos/";
    private ImageViewTouch imageView;
    private Photo item;
    private long imageDownloadID;
    private ImageDetailPresenterInteractor imageDetailPresenter;
    private TextView imageDetailText;

    /**
     * Initializes all the UI elements and sets up the listeners.
     */
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.image_detail_layout);
        imageView = (ImageViewTouch) findViewById(R.id.lightboxView);
        Button downloadButton = (Button) findViewById(R.id.downloadButton);
        Button openInFlicker = (Button) findViewById(R.id.openInFlickerButton);
        imageDetailText = (TextView) findViewById(R.id.imageDetailText);

        FlickerFetchService networkService = FlickerFetchService.getInstance();
        imageDetailPresenter = new ImageDetailPresentorImplementor(networkService);
        imageDetailPresenter.subscribeForImageDetailResult(this);

        downloadButton.setOnClickListener(this);
        openInFlicker.setOnClickListener(this);
        item = getIntent().getParcelableExtra(ITEM_INTENT_KEY);
        loadImageView(item);
        Log.d(TAG, "onCreate: received item " + item.id);
    }

    @Override
    protected void onResume() {
        super.onResume();
        IntentFilter filter = new IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE);
        registerReceiver(downloadReceiver, filter);
        imageDetailPresenter.makeImageDetailRequest(item.id);
        Log.d(TAG, "OnResume: registering download receiver and subscribing to image detail presenter");
    }

    @Override
    protected void onPause() {
        super.onPause();
        unregisterReceiver(downloadReceiver);
        imageDetailPresenter.unSubscribeImageDetailListener();
        Log.d(TAG, "OnPause: unregistering download receiver and unsubscribing from image detail presenter");
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.downloadButton:
                Log.d(TAG, "download button clicked");
                boolean hasPermission = (ContextCompat.checkSelfPermission(this,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED);
                if (!hasPermission) {
                    ActivityCompat.requestPermissions(this,
                            new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                            REQUEST_WRITE_STORAGE);
                } else {
                    imageDownloadID = downLoadToSDCard();
                }

                break;
            case R.id.openInFlickerButton:
                Log.d(TAG, "open in flicker button clicked");
                String URL = FLICKER_BASE_URL + item.owner + "/" + item.id;
                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(URL));
                startActivity(browserIntent);
                break;
        }
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull final String[] permissions,@NonNull final int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case REQUEST_WRITE_STORAGE: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    imageDownloadID = downLoadToSDCard();
                } else {
                    Toast.makeText(this, "The app was not allowed to write to your storage. Please consider granting this permission", Toast.LENGTH_LONG).show();
                }
            }
        }

    }

    @Override
    public void onImageDetailComplete(PhotoDetail photoDetail) {
        String content = "Posted by: " + photoDetail.getOwner().getUsername() + "\n"
                + "Title: " + photoDetail.getTitle().getContent();
        Log.d(TAG, "setting image detail to " + content);
        imageDetailText.setText(content);
        imageDetailText.setVisibility(View.VISIBLE);
    }

    @Override
    public void onError() {
        Log.e(TAG, "error in image detail request");
        Toast toast = Toast.makeText(this, "Error in loading image, Retrying in 5 secs..", Toast.LENGTH_LONG);
        toast.show();
        Handler handler = new Handler(getMainLooper()) ;
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                imageDetailPresenter.makeImageDetailRequest(item.id);
            }
        }, 5000);

    }

    /**
     * This method downloads the highest quality of the image onto the SD card.
     */
    private long downLoadToSDCard() {
        Log.d(TAG, "downloading image");
        long downloadReference;
        String imageURL = TextUtils.isEmpty(item.urlO) ? item.urlL : item.urlO;
        Uri imageUri = Uri.parse(imageURL);
        DownloadManager downLoadManager = (DownloadManager) getSystemService(DOWNLOAD_SERVICE);
        DownloadManager.Request request = new DownloadManager.Request(imageUri);
        request.setTitle(item.title);
        request.setDescription("Downloading image");
        request.setDestinationInExternalPublicDir(Environment.DIRECTORY_DCIM, item.id + ".jpg");
        downloadReference = downLoadManager.enqueue(request);
        return downloadReference;
    }

    /**
     * This method loads the image onto the view. Initially it loads the sub-sampled version of the image and gradually loads the complete image in order to improve performance.
     */
    private void loadImageView(final Photo item) {
        String imageURL = getImageURL(item);
        imageDetailPresenter.makeImageDetailRequest(item.id);
        Glide.with(this)
                .load(imageURL)
                .thumbnail(0.1f)
                .placeholder(R.drawable.loading)
                .diskCacheStrategy(DiskCacheStrategy.ALL)   // cache both original & resized image
                .crossFade()
                .centerCrop()
                .into(imageView);
        Log.d(TAG, "Image view loaded for item " + item.id);
    }

    /**
     * This method gets the URL for the higher quality of the image.
     */
    private String getImageURL(final Photo item) {
        String imageURL;
        if (TextUtils.isEmpty(item.getUrlC())) {
            if (TextUtils.isEmpty(item.getUrlL())) {
                imageURL = item.getUrlT();
            } else {
                imageURL = item.getUrlL();
            }
        } else {
            imageURL = item.getUrlC();
        }
        return imageURL;
    }

    /**
     * This receiver listens for the completion of the download and notifies the user after it is complete.
     */
    private BroadcastReceiver downloadReceiver = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {

            long referenceId = intent.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, -1);

            if (referenceId == imageDownloadID) {

                Toast toast = Toast.makeText(context, "Image Download Complete", Toast.LENGTH_LONG);
                toast.show();
                Log.d(TAG, "download complete");
            }

        }
    };
}
