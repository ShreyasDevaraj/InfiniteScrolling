package com.example.sample.sharkapp.sharkapp.model;

import java.util.List;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Photos model class
 */

public class Photos {
    @SerializedName("page")
    @Expose
    public int page;
    @SerializedName("pages")
    @Expose
    public int pages;
    @SerializedName("perpage")
    @Expose
    public int perpage;
    @SerializedName("total")
    @Expose
    public String total;
    @SerializedName("photo")
    @Expose
    public List<Photo> photo = null;

    public int getPage() {
        return page;
    }

    public void setPage(int page) {
        this.page = page;
    }

    public int getPages() {
        return pages;
    }

    public void setPages(int pages) {
        this.pages = pages;
    }

    public int getPerpage() {
        return perpage;
    }

    public void setPerpage(int perpage) {
        this.perpage = perpage;
    }

    public String getTotal() {
        return total;
    }

    public void setTotal(String total) {
        this.total = total;
    }

    public List<Photo> getPhoto() {
        return photo;
    }

    public void setPhoto(List<Photo> photo) {
        this.photo = photo;
    }
}
