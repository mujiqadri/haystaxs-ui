package com.haystaxs.ui.business.entities;

/**
 * Created by adnan on 3/19/16.
 */
public class Ast {
    private int astId;
    private String astJson;
    private boolean isJson;
    private int count;
    private int totalDuration;
    private int totalRows;

    public int getAstId() {
        return astId;
    }

    public void setAstId(int astId) {
        this.astId = astId;
    }

    public String getAstJson() {
        return astJson;
    }

    public void setAstJson(String astJson) {
        this.astJson = astJson;
    }

    public boolean isJson() {
        return isJson;
    }

    public void setIsJson(boolean isJson) {
        this.isJson = isJson;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public int getTotalDuration() {
        return totalDuration;
    }

    public void setTotalDuration(int totalDuration) {
        this.totalDuration = totalDuration;
    }

    public int getTotalRows() {
        return totalRows;
    }

    public void setTotalRows(int totalRows) {
        this.totalRows = totalRows;
    }
}
