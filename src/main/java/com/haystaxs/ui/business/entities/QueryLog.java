package com.haystaxs.ui.business.entities;

import java.sql.Timestamp;

/**
 * Created by Adnan on 10/26/2015.
 */
public class QueryLog {
    private int queryLogId;
    private int gpsdId;
    private int userId;
    private Timestamp submittedOn;
    private String directoryName;

    public int getQueryLogId() {
        return queryLogId;
    }

    public void setQueryLogId(int queryLogId) {
        this.queryLogId = queryLogId;
    }

    public int getGpsdId() {
        return gpsdId;
    }

    public void setGpsdId(int gpsdId) {
        this.gpsdId = gpsdId;
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

    public String getDirectoryName() {
        return directoryName;
    }

    public void setDirectoryName(String directoryName) {
        this.directoryName = directoryName;
    }
}
