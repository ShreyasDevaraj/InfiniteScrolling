package com.example.sample.sharkapp.sharkapp.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * ImageDetail model class
 */

public class ImageDetailResult {
    public PhotoDetail getPhotoDetail() {
        return photo;
    }

    public void setPhoto(PhotoDetail photo) {
        this.photo = photo;
    }

    @SerializedName("photo")
    @Expose
    private PhotoDetail photo;


}
