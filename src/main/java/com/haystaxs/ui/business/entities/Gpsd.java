package com.haystaxs.ui.business.entities;

import java.sql.Timestamp;

/**
 * Created by Adnan on 10/21/2015.
 */
public class Gpsd {
    private int gpsdId;
    private int userId;
    private String dbname;
    private String filename;
    private Timestamp fileSubmittedOn;
    private String status;

    public Gpsd(){}

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

    public String getDbname() {
        return dbname;
    }

    public void setDbname(String dbname) {
        this.dbname = dbname;
    }

    public String getFilename() {
        return filename;
    }

    public void setFilename(String filename) {
        this.filename = filename;
    }

    public Timestamp getFileSubmittedOn() {
        return fileSubmittedOn;
    }

    public void setFileSubmittedOn(Timestamp fileSubmittedOn) {
        this.fileSubmittedOn = fileSubmittedOn;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
