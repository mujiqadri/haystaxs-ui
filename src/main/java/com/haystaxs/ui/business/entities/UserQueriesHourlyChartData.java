package com.haystaxs.ui.business.entities;

/**
 * Created by adnan on 3/17/16.
 */
public class UserQueriesHourlyChartData {
    private String queryType;
    private int hour;
    private double duration;

    public String getQueryType() {
        return queryType;
    }

    public void setQueryType(String queryType) {
        this.queryType = queryType;
    }

    public int getHour() {
        return hour;
    }

    public void setHour(int hour) {
        this.hour = hour;
    }

    public double getDuration() {
        return duration;
    }

    public void setDuration(double duration) {
        this.duration = duration;
    }
}
