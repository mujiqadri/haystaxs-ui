package com.haystaxs.ui.business.entities;

import java.sql.Time;
import java.sql.Timestamp;

/**
 * Created by Adnan on 10/21/2015.
 */
public class Gpsd {
    private int gpsdId;
    private int userId;
    private String dbName;
    private String gpsdVersion;
    private Timestamp gpsdDate;
    private String filename;
    private Timestamp fileSubmittedOn;
    private int noOfLines;
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

    public String getDbName() {
        return dbName;
    }

    public void setDbName(String dbName) {
        this.dbName = dbName;
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

    public int getNoOfLines() {
        return noOfLines;
    }

    public void setNoOfLines(int noOfLines) {
        this.noOfLines = noOfLines;
    }

    public String getGpsdVersion() {
        return gpsdVersion;
    }

    public void setGpsdVersion(String gpsdVersion) {
        this.gpsdVersion = gpsdVersion;
    }

    public Timestamp getGpsdDate() {
        return gpsdDate;
    }

    public void setGpsdDate(Timestamp gpsdDate) {
        this.gpsdDate = gpsdDate;
    }
}
