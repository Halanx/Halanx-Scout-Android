package com.technicalrj.halanxscouts.Pojo;

import com.google.gson.annotations.SerializedName;

public class LocationOnBoarding {

    private String longitude;

    private String latitude;

    @SerializedName("location")
    private String resolvedAddress;

    @SerializedName("street_address")
    private String streetAddress;

    private String city;

    private String state;

    public LocationOnBoarding(String longitude, String latitude, String resolvedAddress,
                              String streetAddress, String city, String state) {
        this.longitude = longitude;
        this.latitude = latitude;
        this.resolvedAddress = resolvedAddress;
        this.streetAddress = streetAddress;
        this.city = city;
        this.state = state;
    }

    public String getLongitude() {
        return longitude;
    }

    public void setLongitude(String longitude) {
        this.longitude = longitude;
    }

    public String getLatitude() {
        return latitude;
    }

    public void setLatitude(String latitude) {
        this.latitude = latitude;
    }

    public String getResolvedAddress() {
        return resolvedAddress;
    }

    public void setResolvedAddress(String resolvedAddress) {
        this.resolvedAddress = resolvedAddress;
    }

    public String getStreetAddress() {
        return streetAddress;
    }

    public void setStreetAddress(String streetAddress) {
        this.streetAddress = streetAddress;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }
}
