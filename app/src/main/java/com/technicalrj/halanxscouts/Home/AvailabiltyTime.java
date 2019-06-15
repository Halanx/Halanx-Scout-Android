package com.technicalrj.halanxscouts.Home;

public class AvailabiltyTime {

    public String day ;
    public String date ;
    public String month ;
    public String fullDate;

    public AvailabiltyTime(String day, String date, String month,String fullDate) {
        this.day = day;
        this.date = date;
        this.month = month;
        this.fullDate = fullDate;
    }

    public String getDay() {
        return day;
    }

    public String getDate() {
        return date;
    }

    public String getMonth() {
        return month;
    }

    public String getFullDate() {
        return fullDate;
    }
}
