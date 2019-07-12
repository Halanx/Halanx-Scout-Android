package com.technicalrj.halanxscouts.Pojo;

public class MyLocation {

    private String lat;
    private String lng;

    public MyLocation(String lat, String lng) {
        this.lat = lat;
        this.lng = lng;
    }

    public String getLat() {
        return lat;
    }

    public String getLng() {
        return lng;
    }
}

