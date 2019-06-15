package com.technicalrj.halanxscouts.Home;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class ScheduleAvailability {

    @SerializedName("id")
    @Expose
    private Integer id;
    @SerializedName("start_time")
    @Expose
    private String startTime;
    @SerializedName("end_time")
    @Expose
    private String endTime;
    @SerializedName("scout")
    @Expose
    private Integer scout;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getStartTime() {
        return startTime;
    }

    public void setStartTime(String startTime) {
        this.startTime = startTime;
    }

    public String getEndTime() {
        return endTime;
    }

    public void setEndTime(String endTime) {
        this.endTime = endTime;
    }

    public Integer getScout() {
        return scout;
    }

    public void setScout(Integer scout) {
        this.scout = scout;
    }

    @Override
    public String toString() {
        return "ScheduleAvailability{" +
                "id=" + id +
                ", startTime='" + startTime + '\'' +
                ", endTime='" + endTime + '\'' +
                ", scout=" + scout +
                '}';
    }
}