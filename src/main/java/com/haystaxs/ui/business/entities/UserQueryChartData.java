package com.haystaxs.ui.business.entities;

/**
 * Created by adnan on 12/20/15.
 */
public class UserQueryChartData {
    private String date;
    private double totalDuration;
    private int totalCount;
    private double selectDuration;
    private int selectCount;
    private double insertDuration;
    private int insertCount;
    private double dropTableDuration;
    private int dropTableCount;

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public int getSelectCount() {
        return selectCount;
    }

    public void setSelectCount(int selectCount) {
        this.selectCount = selectCount;
    }

    public int getInsertCount() {
        return insertCount;
    }

    public void setInsertCount(int insertCount) {
        this.insertCount = insertCount;
    }

    public int getDropTableCount() {
        return dropTableCount;
    }

    public void setDropTableCount(int dropTableCount) {
        this.dropTableCount = dropTableCount;
    }

    public double getTotalDuration() {
        return totalDuration;
    }

    public void setTotalDuration(double totalDuration) {
        this.totalDuration = totalDuration;
    }

    public int getTotalCount() {
        return totalCount;
    }

    public void setTotalCount(int totalCount) {
        this.totalCount = totalCount;
    }

    public double getSelectDuration() {
        return selectDuration;
    }

    public void setSelectDuration(double selectDuration) {
        this.selectDuration = selectDuration;
    }

    public double getInsertDuration() {
        return insertDuration;
    }

    public void setInsertDuration(double insertDuration) {
        this.insertDuration = insertDuration;
    }

    public double getDropTableDuration() {
        return dropTableDuration;
    }

    public void setDropTableDuration(double dropTableDuration) {
        this.dropTableDuration = dropTableDuration;
    }
}
