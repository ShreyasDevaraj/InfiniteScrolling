package com.example.sample.sharkapp.sharkapp.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Search result model class
 */

public class SearchResult {

    @SerializedName("photos")
    @Expose
    public Photos photos;
    @SerializedName("stat")
    @Expose
    public String stat;

    public Photos getPhotos() {
        return photos;
    }

    public void setPhotos(Photos photos) {
        this.photos = photos;
    }

    public String getStat() {
        return stat;
    }

    public void setStat(String stat) {
        this.stat = stat;
    }
}
