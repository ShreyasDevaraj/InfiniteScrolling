package com.example.sample.sharkapp.sharkapp.fragment;

import android.app.Fragment;
import android.os.Bundle;

import com.example.sample.sharkapp.sharkapp.adapter.RecyclerViewAdapter;
import com.example.sample.sharkapp.sharkapp.model.Photo;

import java.util.ArrayList;
import java.util.List;

/**
 * This is a retained fragment. This fragment has no UI associated with it. This survives the configuration change and hence is used to hold the adapter data
 */
public class ImageListRetainedFragment extends Fragment {
    private static final String TAG = ImageListRetainedFragment.class.getSimpleName();
    final List<Photo> photoList = new ArrayList<>();
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
    }

    /**
     * Adds data to the existing data set
     */
    public void addData(List<Photo> photoList){
      this.photoList.addAll(photoList);
    }

    /**
     * Returns the current data set
     */
    public List<Photo> getData(){
        return photoList;
    }
}
