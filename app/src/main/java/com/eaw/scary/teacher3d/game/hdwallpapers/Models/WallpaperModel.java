package com.eaw.scary.teacher3d.game.hdwallpapers.Models;

import java.io.Serializable;

public class WallpaperModel implements Serializable {
    String id, title, picUrl, category;
    long time;
    boolean featured;

    public WallpaperModel(String id, String title, String picUrl, String category, long time, boolean featured) {
        this.id = id;
        this.title = title;
        this.picUrl = picUrl;
        this.category = category;
        this.time = time;
        this.featured = featured;
    }

    public WallpaperModel() {

    }

    public boolean isFeatured() {
        return featured;
    }

    public void setFeatured(boolean featured) {
        this.featured = featured;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getPicUrl() {
        return picUrl;
    }

    public void setPicUrl(String picUrl) {
        this.picUrl = picUrl;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public long getTime() {
        return time;
    }

    public void setTime(long time) {
        this.time = time;
    }
}
