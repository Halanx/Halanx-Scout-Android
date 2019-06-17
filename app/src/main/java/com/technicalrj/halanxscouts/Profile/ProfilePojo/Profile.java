
package com.technicalrj.halanxscouts.Profile.ProfilePojo;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Profile {

    @SerializedName("id")
    @Expose
    private Integer id;
    @SerializedName("user")
    @Expose
    private User user;
    @SerializedName("permanent_address")
    @Expose
    private PermanentAddress permanentAddress;
    @SerializedName("work_address")
    @Expose
    private WorkAddress workAddress;
    @SerializedName("bank_detail")
    @Expose
    private BankDetail bankDetail;
    @SerializedName("document_submission_complete")
    @Expose
    private Boolean documentSubmissionComplete;
    @SerializedName("bank_details_complete")
    @Expose
    private Boolean bankDetailsComplete;
    @SerializedName("phone_no")
    @Expose
    private String phoneNo;
    @SerializedName("active")
    @Expose
    private Boolean active;
    @SerializedName("profile_pic_url")
    @Expose
    private String profilePicUrl;
    @SerializedName("profile_pic_thumbnail_url")
    @Expose
    private String profilePicThumbnailUrl;
    @SerializedName("gcm_id")
    @Expose
    private String gcmId;

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

    public PermanentAddress getPermanentAddress() {
        return permanentAddress;
    }

    public void setPermanentAddress(PermanentAddress permanentAddress) {
        this.permanentAddress = permanentAddress;
    }

    public WorkAddress getWorkAddress() {
        return workAddress;
    }

    public void setWorkAddress(WorkAddress workAddress) {
        this.workAddress = workAddress;
    }

    public BankDetail getBankDetail() {
        return bankDetail;
    }

    public void setBankDetail(BankDetail bankDetail) {
        this.bankDetail = bankDetail;
    }

    public Boolean getDocumentSubmissionComplete() {
        return documentSubmissionComplete;
    }

    public void setDocumentSubmissionComplete(Boolean documentSubmissionComplete) {
        this.documentSubmissionComplete = documentSubmissionComplete;
    }

    public Boolean getBankDetailsComplete() {
        return bankDetailsComplete;
    }

    public void setBankDetailsComplete(Boolean bankDetailsComplete) {
        this.bankDetailsComplete = bankDetailsComplete;
    }

    public String getPhoneNo() {
        return phoneNo;
    }

    public void setPhoneNo(String phoneNo) {
        this.phoneNo = phoneNo;
    }

    public Boolean getActive() {
        return active;
    }

    public void setActive(Boolean active) {
        this.active = active;
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

    public String getGcmId() {
        return gcmId;
    }

    public void setGcmId(String gcmId) {
        this.gcmId = gcmId;
    }


    @Override
    public String toString() {
        return "Profile{" +
                "id=" + id +
                ", user=" + user +
                ", permanentAddress=" + permanentAddress +
                ", workAddress=" + workAddress +
                ", bankDetail=" + bankDetail +
                ", documentSubmissionComplete=" + documentSubmissionComplete +
                ", bankDetailsComplete=" + bankDetailsComplete +
                ", phoneNo='" + phoneNo + '\'' +
                ", active=" + active +
                ", profilePicUrl='" + profilePicUrl + '\'' +
                ", profilePicThumbnailUrl='" + profilePicThumbnailUrl + '\'' +
                ", gcmId='" + gcmId + '\'' +
                '}';
    }
}
