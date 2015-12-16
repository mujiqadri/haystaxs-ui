package com.haystaxs.ui.business.entities.selection;

/**
 * Created by adnan on 12/16/15.
 */
public class QueryType {
    private String queryType;
    private int count;
    private double totalDuration;

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

    public double getTotalDuration() {
        return totalDuration;
    }

    public void setTotalDuration(double totalDuration) {
        this.totalDuration = totalDuration;
    }
}
