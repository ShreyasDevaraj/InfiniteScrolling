package com.example.sample.sharkapp.sharkapp.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 *  Photo model class
 */

public class Photo implements Parcelable{

    public Photo(){

    }
    @SerializedName("id")
    @Expose
    public String id;
    @SerializedName("owner")
    @Expose
    public String owner;
    @SerializedName("secret")
    @Expose
    public String secret;
    @SerializedName("server")
    @Expose
    public String server;
    @SerializedName("farm")
    @Expose
    public int farm;


    @SerializedName("title")
    @Expose
    public String title;
    @SerializedName("ispublic")
    @Expose
    public int ispublic;
    @SerializedName("isfriend")
    @Expose
    public int isfriend;
    @SerializedName("isfamily")
    @Expose
    public int isfamily;
    @SerializedName("url_t")
    @Expose
    public String urlT;
    @SerializedName("height_t")
    @Expose
    public String heightT;
    @SerializedName("width_t")
    @Expose
    public String widthT;
    @SerializedName("url_c")
    @Expose
    public String urlC;
    @SerializedName("height_c")
    @Expose
    public int heightC;
    @SerializedName("width_c")
    @Expose
    public String widthC;

    @SerializedName("url_l")
    @Expose
    public String urlL;
    @SerializedName("height_l")
    @Expose
    public String heightL;
    @SerializedName("width_l")
    @Expose
    public String widthL;
    @SerializedName("url_o")
    @Expose
    public String urlO;
    @SerializedName("height_o")
    @Expose
    public String heightO;
    @SerializedName("width_o")
    @Expose
    public String widthO;

    protected Photo(Parcel in) {
        id = in.readString();
        owner = in.readString();
        secret = in.readString();
        server = in.readString();
        farm = in.readInt();
        title = in.readString();
        ispublic = in.readInt();
        isfriend = in.readInt();
        isfamily = in.readInt();
        urlT = in.readString();
        heightT = in.readString();
        widthT = in.readString();
        urlC = in.readString();
        heightC = in.readInt();
        widthC = in.readString();
        urlL = in.readString();
        heightL = in.readString();
        widthL = in.readString();
        urlO = in.readString();
        heightO = in.readString();
        widthO = in.readString();
    }

    public static final Creator<Photo> CREATOR = new Creator<Photo>() {
        @Override
        public Photo createFromParcel(Parcel in) {
            return new Photo(in);
        }

        @Override
        public Photo[] newArray(int size) {
            return new Photo[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(id);
        dest.writeString(owner);
        dest.writeString(secret);
        dest.writeString(server);
        dest.writeInt(farm);
        dest.writeString(title);
        dest.writeInt(ispublic);
        dest.writeInt(isfriend);
        dest.writeInt(isfamily);
        dest.writeString(urlT);
        dest.writeString(heightT);
        dest.writeString(widthT);
        dest.writeString(urlC);
        dest.writeInt(heightC);
        dest.writeString(widthC);
        dest.writeString(urlL);
        dest.writeString(heightL);
        dest.writeString(widthL);
        dest.writeString(urlO);
        dest.writeString(heightO);
        dest.writeString(widthO);
    }

    public String getUrlT() {
        return urlT;
    }

    public void setUrlT(String urlT) {
        this.urlT = urlT;
    }

    public String getUrlC() {
        return urlC;
    }

    public void setUrlC(String urlC) {
        this.urlC = urlC;
    }

    public String getUrlO() {
        return urlO;
    }

    public void setUrlO(String urlO) {
        this.urlO = urlO;
    }

    public String getTitle() {
        return title;
    }

    public String getUrlL() {
        return urlL;
    }

    public void setUrlL(String urlL) {
        this.urlL = urlL;
    }
    public void setTitle(String title) {
        this.title = title;
    }
}
