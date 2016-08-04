package com.haystaxs.ui.business.entities;

import java.sql.Timestamp;

/**
 * Created by adnan on 3/17/16.
 */
public class UserQueriesChartData2 {
    private String date;
    private String queryType;
    private int count;
    private double duration;

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getQueryType() {
        return queryType;
    }

    public void setQueryType(String queryType) {
        this.queryType = queryType;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public double getDuration() {
        return duration;
    }

    public void setDuration(double duration) {
        this.duration = duration;
    }
}
