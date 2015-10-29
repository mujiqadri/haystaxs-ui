package com.haystaxs.ui.business.entities;

import java.sql.Timestamp;

/**
 * Created by Adnan on 10/22/2015.
 */
public class RunLog {
    private int runId;
    private Timestamp runDate;
    private int userId;
    private int gpsdId;
    private Timestamp workloadSubmittedOn;
    private String runlogName;
    private String modelJson;
    private String directoryName;
    private String status;

    public RunLog() {}

    public int getRunId() {
        return runId;
    }

    public void setRunId(int runId) {
        this.runId = runId;
    }

    public Timestamp getRunDate() {
        return runDate;
    }

    public void setRunDate(Timestamp runDate) {
        this.runDate = runDate;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public int getGpsdId() {
        return gpsdId;
    }

    public void setGpsdId(int gpsdId) {
        this.gpsdId = gpsdId;
    }

    public Timestamp getWorkloadSubmittedOn() {
        return workloadSubmittedOn;
    }

    public void setWorkloadSubmittedOn(Timestamp workloadSubmittedOn) {
        this.workloadSubmittedOn = workloadSubmittedOn;
    }

    public String getRunlogName() {
        return runlogName;
    }

    public void setRunlogName(String runlogName) {
        this.runlogName = runlogName;
    }

    public String getModelJson() {
        return modelJson;
    }

    public void setModelJson(String modelJson) {
        this.modelJson = modelJson;
    }

    public String getDirectoryName() {
        return directoryName;
    }

    public void setDirectoryName(String directoryName) {
        this.directoryName = directoryName;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
