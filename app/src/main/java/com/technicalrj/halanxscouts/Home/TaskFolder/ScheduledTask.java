
package com.technicalrj.halanxscouts.Home.TaskFolder;

import java.util.List;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class ScheduledTask {

    @SerializedName("id")
    @Expose
    private Integer id;
    @SerializedName("scout")
    @Expose
    private Integer scout;
    @SerializedName("category")
    @Expose
    private Category category;
    @SerializedName("earning")
    @Expose
    private Integer earning;
    @SerializedName("scheduled_at")
    @Expose
    private String scheduledAt;
    @SerializedName("house")
    @Expose
    private House house;
    @SerializedName("space")
    @Expose
    private Object space;
    @SerializedName("customer")
    @Expose
    private Customer customer;
    @SerializedName("sub_tasks")
    @Expose
    private List<SubTask> subTasks = null;
    @SerializedName("review_tags")
    @Expose
    private List<Object> reviewTags = null;
    @SerializedName("remark")
    @Expose
    private String remark;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getScout() {
        return scout;
    }

    public void setScout(Integer scout) {
        this.scout = scout;
    }

    public Category getCategory() {
        return category;
    }

    public void setCategory(Category category) {
        this.category = category;
    }

    public Integer getEarning() {
        return earning;
    }

    public void setEarning(Integer earning) {
        this.earning = earning;
    }

    public String getScheduledAt() {
        return scheduledAt;
    }

    public void setScheduledAt(String scheduledAt) {
        this.scheduledAt = scheduledAt;
    }

    public House getHouse() {
        return house;
    }

    public void setHouse(House house) {
        this.house = house;
    }

    public Object getSpace() {
        return space;
    }

    public void setSpace(Object space) {
        this.space = space;
    }

    public Customer getCustomer() {
        return customer;
    }

    public void setCustomer(Customer customer) {
        this.customer = customer;
    }

    public List<SubTask> getSubTasks() {
        return subTasks;
    }

    public void setSubTasks(List<SubTask> subTasks) {
        this.subTasks = subTasks;
    }

    public List<Object> getReviewTags() {
        return reviewTags;
    }

    public void setReviewTags(List<Object> reviewTags) {
        this.reviewTags = reviewTags;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

}
