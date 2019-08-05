package com.technicalrj.halanxscouts.Pojo;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

public class PropertyOnBoarding {

    private int id;

    private double rent;

    @SerializedName("furnish_type")
    private String furnishType;

    @SerializedName("space_type")
    private ArrayList<String> accommodationTypeList;

    @SerializedName("bhk_cout")
    private int bhkCount;

    public PropertyOnBoarding(double rent, String furnishType, ArrayList<String> accommodationTypeList) {
        this.rent = rent;
        this.furnishType = furnishType;
        this.accommodationTypeList = accommodationTypeList;
    }

    public PropertyOnBoarding(String furnishType, ArrayList<String> accommodationTypeList, int bhkCount) {
        this.furnishType = furnishType;
        this.accommodationTypeList = accommodationTypeList;
        this.bhkCount = bhkCount;
    }

    public double getRent() {
        return rent;
    }

    public void setRent(double rent) {
        this.rent = rent;
    }

    public String getFurnishType() {
        return furnishType;
    }

    public void setFurnishType(String furnishType) {
        this.furnishType = furnishType;
    }

    public ArrayList<String> getAccommodationTypeList() {
        return accommodationTypeList;
    }

    public void setAccommodationTypeList(ArrayList<String> accommodationTypeList) {
        this.accommodationTypeList = accommodationTypeList;
    }

    public int getBhkCount() {
        return bhkCount;
    }

    public void setBhkCount(int bhkCount) {
        this.bhkCount = bhkCount;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

}
