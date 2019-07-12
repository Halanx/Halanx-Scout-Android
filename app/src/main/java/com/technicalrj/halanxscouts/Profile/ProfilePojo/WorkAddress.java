
package com.technicalrj.halanxscouts.Profile.ProfilePojo;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class WorkAddress {

    @SerializedName("id")
    @Expose
    private Integer id;
    @SerializedName("street_address")
    @Expose
    private String streetAddress;
    @SerializedName("city")
    @Expose
    private String city;
    @SerializedName("state")
    @Expose
    private String state;
    @SerializedName("pincode")
    @Expose
    private Integer pincode;
    @SerializedName("country")
    @Expose
    private String country;
    @SerializedName("complete_address")
    @Expose
    private String completeAddress;
    @SerializedName("latitude")
    @Expose
    private Double latitude;
    @SerializedName("longitude")
    @Expose
    private Double longitude;
    @SerializedName("same_as_permanent_address")
    @Expose
    private Boolean sameAsPermanentAddress;
    @SerializedName("scout")
    @Expose
    private Integer scout;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
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

    public Integer getPincode() {
        return pincode;
    }

    public void setPincode(Integer pincode) {
        this.pincode = pincode;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getCompleteAddress() {
        return completeAddress;
    }

    public void setCompleteAddress(String completeAddress) {
        this.completeAddress = completeAddress;
    }

    public Double getLatitude() {
        return latitude;
    }

    public void setLatitude(Double latitude) {
        this.latitude = latitude;
    }

    public Double getLongitude() {
        return longitude;
    }

    public void setLongitude(Double longitude) {
        this.longitude = longitude;
    }

    public Boolean getSameAsPermanentAddress() {
        return sameAsPermanentAddress;
    }

    public void setSameAsPermanentAddress(Boolean sameAsPermanentAddress) {
        this.sameAsPermanentAddress = sameAsPermanentAddress;
    }

    public Integer getScout() {
        return scout;
    }

    public void setScout(Integer scout) {
        this.scout = scout;
    }

}
