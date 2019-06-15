
package com.technicalrj.halanxscouts.Home.TaskFolder;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Customer {

    @SerializedName("id")
    @Expose
    private Integer id;
    @SerializedName("user")
    @Expose
    private User user;
    @SerializedName("phone_no")
    @Expose
    private String phoneNo;
    @SerializedName("profile_pic_url")
    @Expose
    private String profilePicUrl;
    @SerializedName("profile_pic_thumbnail_url")
    @Expose
    private String profilePicThumbnailUrl;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public String getPhoneNo() {
        return phoneNo;
    }

    public void setPhoneNo(String phoneNo) {
        this.phoneNo = phoneNo;
    }

    public String getProfilePicUrl() {
        return profilePicUrl;
    }

    public void setProfilePicUrl(String profilePicUrl) {
        this.profilePicUrl = profilePicUrl;
    }

    public String getProfilePicThumbnailUrl() {
        return profilePicThumbnailUrl;
    }

    public void setProfilePicThumbnailUrl(String profilePicThumbnailUrl) {
        this.profilePicThumbnailUrl = profilePicThumbnailUrl;
    }

}
