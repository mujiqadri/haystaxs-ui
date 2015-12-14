package com.haystaxs.ui.business.entities;

import java.sql.Timestamp;

/**
 * Created by adnan on 11/17/15.
 */
public class QueryLogDate {
    private int queryLogId;
    private Timestamp logDate;
    private int queryCount;
    private int sumDuration;
    private Timestamp submittedOn;
    private String originalFileName;
    private int totalRows;

    public int getQueryLogId() {
        return queryLogId;
    }

    public void setQueryLogId(int queryLogId) {
        this.queryLogId = queryLogId;
    }

    public Timestamp getLogDate() {
        return logDate;
    }

    public void setLogDate(Timestamp logDate) {
        this.logDate = logDate;
    }

    public int getQueryCount() {
        return queryCount;
    }

    public void setQueryCount(int queryCount) {
        this.queryCount = queryCount;
    }

    public int getSumDuration() {
        return sumDuration;
    }

    public void setSumDuration(int sumDuration) {
        this.sumDuration = sumDuration;
    }

    public Timestamp getSubmittedOn() {
        return submittedOn;
    }

    public void setSubmittedOn(Timestamp submittedOn) {
        this.submittedOn = submittedOn;
    }

    public String getOriginalFileName() {
        return originalFileName;
    }

    public void setOriginalFileName(String originalFileName) {
        this.originalFileName = originalFileName;
    }

    public int getTotalRows() {
        return totalRows;
    }

    public void setTotalRows(int totalRows) {
        this.totalRows = totalRows;
    }
}
