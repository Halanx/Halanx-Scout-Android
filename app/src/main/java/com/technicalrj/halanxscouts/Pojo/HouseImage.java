package com.technicalrj.halanxscouts.Pojo;

import android.graphics.Bitmap;

import androidx.annotation.NonNull;

public class HouseImage {

    public final static int UPLOADING = 1;
    public final static int ERROR = 2;
    public final static int UPLOADED = 3;
    public final static int WAITING = 4;

    private String url;

    private int status;

    private Bitmap bitmap;

    public HouseImage(String url, int status) {
        this.url = url;
        this.status = status;
    }

    public HouseImage(String url, int status, Bitmap bitmap) {
        this.url = url;
        this.status = status;
        this.bitmap = bitmap;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public Bitmap getBitmap() {
        return bitmap;
    }

    public void setBitmap(Bitmap bitmap) {
        this.bitmap = bitmap;
    }
}
