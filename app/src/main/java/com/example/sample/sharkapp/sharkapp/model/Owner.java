package com.example.sample.sharkapp.sharkapp.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Owner model class
 */

public class Owner {
    @SerializedName("username")
    @Expose
    private String username;
    @SerializedName("realname")
    @Expose
    private String realname;
    @SerializedName("location")
    @Expose
    private String location;

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getRealname() {
        return realname;
    }

    public void setRealname(String realname) {
        this.realname = realname;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }
}
