package com.haystaxs.ui.business.entities;

import java.sql.Timestamp;
import java.util.List;

/**
 * Created by Adnan on 10/26/2015.
 */
public class QueryLog {
    private int queryLogId;
    private int userId;
    private Timestamp submittedOn;
    private String status;
    private List<QueryLogDate> logDates;


    public int getQueryLogId() {
        return queryLogId;
    }

    public void setQueryLogId(int queryLogId) {
        this.queryLogId = queryLogId;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public Timestamp getSubmittedOn() {
        return submittedOn;
    }

    public void setSubmittedOn(Timestamp submittedOn) {
        this.submittedOn = submittedOn;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public List<QueryLogDate> getLogDates() {
        return logDates;
    }

    public void setLogDates(List<QueryLogDate> logDates) {
        this.logDates = logDates;
    }
}
