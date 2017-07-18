package com.example.sample.sharkapp.sharkapp.adapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.example.sample.sharkapp.R;
import com.example.sample.sharkapp.sharkapp.activity.ImageDetailActivity;
import com.example.sample.sharkapp.sharkapp.activity.MainActivity;
import com.example.sample.sharkapp.sharkapp.model.Photo;

import java.lang.ref.WeakReference;
import java.util.List;

import static com.example.sample.sharkapp.sharkapp.activity.ImageDetailActivity.ITEM_INTENT_KEY;

/**
 * This class is the adapter for the recycler view which is used to show the list of images.
 */
public class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static final String TAG = RecyclerViewAdapter.class.getSimpleName();
    private static final int ITEM = 0;
    private static final int LOADING = 1;

    private List<Photo> photoResults;
    private WeakReference<Context> context;

    private boolean isLoadingAdded = false;

    private RecyclerView recyclerView;

    private View.OnClickListener clickListener = new RecyclerViewClickListener();

    public RecyclerViewAdapter(final Context context, final RecyclerView recyclerView, final List<Photo> photoList) {
        this.context = new WeakReference<>(context);
        photoResults = photoList;
        this.recyclerView = recyclerView;
    }

    /**
     * Adds the footer for showing the loading view when page request for successive page is made.
     */
    public void addLoadingFooter() {
        isLoadingAdded = true;
        add(new Photo());
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(final ViewGroup parent, final int viewType) {
        RecyclerView.ViewHolder viewHolder = null;
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());

        switch (viewType) {
            case ITEM:
                viewHolder = getViewHolder(parent, inflater);
                break;
            case LOADING:
                View loadingView = inflater.inflate(R.layout.item_progress, parent, false);
                viewHolder = new LoadingViewHolder(loadingView);
                break;
        }
        return viewHolder;
    }

    @NonNull
    private RecyclerView.ViewHolder getViewHolder(final ViewGroup parent, final LayoutInflater inflater) {
        RecyclerView.ViewHolder viewHolder;
        View view = inflater.inflate(R.layout.item_list, parent, false);
        view.setOnClickListener(clickListener);
        viewHolder = new PhotoViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder holder, final int position) {

        Photo result = photoResults.get(position);

        switch (getItemViewType(position)) {
            case ITEM:
                String imageURL = TextUtils.isEmpty(result.urlC) ? result.urlT : result.urlC;
                final PhotoViewHolder photoHolder = (PhotoViewHolder) holder;
                Glide.with(context.get())
                        .load(imageURL)
                        .thumbnail(0.1f)
                        .placeholder(R.drawable.loading)
                        .diskCacheStrategy(DiskCacheStrategy.ALL)   // cache both original & resized image
                        .crossFade()
                        .centerCrop()
                        .into(photoHolder.image);
                break;

            case LOADING:
                LoadingViewHolder loadingVH = (LoadingViewHolder) holder;
                loadingVH.progressBar.setVisibility(View.VISIBLE);
                break;
        }

    }

    @Override
    public int getItemCount() {
        return photoResults == null ? 0 : photoResults.size();
    }

    @Override
    public int getItemViewType(final int position) {
        return (position == photoResults.size() - 1 && isLoadingAdded) ? LOADING : ITEM;
    }

    /**
     * Adds the photo object to the list
     */
    private void add(final Photo photo) {
        photoResults.add(photo);
        notifyItemInserted(photoResults.size() - 1);
    }

    /**
     * Sets the data for the adapter. This basically resets the entire data set to the new data set passed in.
     */
    public void setData(final List<Photo> photoList) {
        this.photoResults = photoList;
        notifyDataSetChanged();
    }

    /**
     * Clears the adapter data set
     */
    public void clear() {
        isLoadingAdded = false;
        photoResults.clear();
        notifyDataSetChanged();
    }

    public boolean isEmpty() {
        return getItemCount() == 0;
    }

    /**
     * Removes the loading footer
     */
    public void removeLoadingFooter() {
        isLoadingAdded = false;

        int position = photoResults.size() - 1;
        Photo result = getItem(position);

        if (result != null) {
            photoResults.remove(position);
            notifyItemRemoved(position);
        }
    }

    /**
     * Retrives the item for the given position from the data set
     */
    private Photo getItem(final int position) {
        return photoResults.get(position);
    }


    /**
     * View holder for the image viewer
     */
    private class PhotoViewHolder extends RecyclerView.ViewHolder {
        private ImageView image;

        PhotoViewHolder(View itemView) {
            super(itemView);
            image = (ImageView) itemView.findViewById(R.id.shark_poster);
        }
    }

    /**
     * View holder for the loading view
     */
    private class LoadingViewHolder extends RecyclerView.ViewHolder {
        private ProgressBar progressBar;

        LoadingViewHolder(View itemView) {
            super(itemView);
            progressBar = (ProgressBar) itemView.findViewById(R.id.loadmore_progress);
        }

    }

    /**
     * Click listener for the images in the grid view.
     */
    private class RecyclerViewClickListener implements View.OnClickListener {

        @Override
        public void onClick(View v) {
            int itemPosition = recyclerView.getChildLayoutPosition(v);
            Photo item = photoResults.get(itemPosition);
            Intent intent = new Intent(context.get(), ImageDetailActivity.class);
            intent.putExtra(ITEM_INTENT_KEY, item);
            context.get().startActivity(intent);
            Activity activity = (Activity) recyclerView.getContext();
            activity.overridePendingTransition(R.anim.slide_in, R.anim.slide_out);
            Log.d(TAG, "image clicked " + item.id);
        }
    }

}
